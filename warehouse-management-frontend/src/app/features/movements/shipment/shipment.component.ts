import { CommonModule } from '@angular/common';
import { Component, effect, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MovementService } from '../movement.service';
import { ReferenceService } from '../../../core/services/reference.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { ProductStock } from '../../inventory/models/product-stock';
import { InventoryService } from '../../inventory/inventory.service';

@Component({
  selector: 'app-shipment',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './shipment.component.html',
  styleUrls: ['./shipment.component.css'],
})
export class ShipmentComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private movementService = inject(MovementService);
  private referenceService = inject(ReferenceService);
  private inventoryService = inject(InventoryService);
  private router = inject(Router);

  shipmentForm: FormGroup = this.fb.group({
    fromLocationId: ['', [Validators.required]],
    items: this.fb.array([]),
  });

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private subscriptions: Map<number, Subscription> = new Map();
  availableStocksMap = signal<Map<number, ProductStock[]>>(new Map());
  availableLocations = signal<ProductStock[]>([]);
  maxQuantities = signal<Map<number, number>>(new Map());

  hasNoStock = signal<Map<number, boolean>>(new Map()); 
  allItemsNoStock = signal(false); 

  get items(): FormArray {
    return this.shipmentForm.get('items') as FormArray;
  }

  get products() {
    return this.referenceService.products;
  }

  get fromLocationId() {
    return this.shipmentForm.get('fromLocationId');
  }

  ngOnInit(): void {
    this.referenceService.loadProducts();
    this.addItem();

    this.fromLocationId?.valueChanges.subscribe(() => {
      this.updateMaxQuantities();
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
    this.subscriptions.clear();
  }

  addItem(): void {
    const index = this.items.length;
    const itemGroup = this.fb.group({
      productId: ['', [Validators.required]],
      quantity: ['', [Validators.required, Validators.min(1)]],
    });

    const productIdControl = itemGroup.get('productId');

    if (productIdControl) {
      const subscription = productIdControl.valueChanges.subscribe(
        (productIdValue: string | null) => {
          const productId = productIdValue ? Number(productIdValue) : null;

          if (productId && !isNaN(productId)) {
            this.loadAvailableStocks(productId, index);
            itemGroup.get('quantity')?.setValue('', { emitEvent: false });
          } else {
            const newMap = new Map(this.availableStocksMap());
            newMap.delete(index);
            this.availableStocksMap.set(newMap);
            itemGroup.get('quantity')?.setValue('', { emitEvent: false });
            this.updateAvailableLocations();
            this.updateNoStockStatus();
          }
        },
      );

      this.subscriptions.set(index, subscription);
    }

    this.items.push(itemGroup);
  }

  removeItem(index: number): void {
    const sub = this.subscriptions.get(index);
    if (sub) {
      sub.unsubscribe();
      this.subscriptions.delete(index);
    }

    const newMap = new Map(this.availableStocksMap());
    newMap.delete(index);
    this.availableStocksMap.set(newMap);

    const newNoStockMap = new Map(this.hasNoStock());
    newNoStockMap.delete(index);
    this.hasNoStock.set(newNoStockMap);

    this.items.removeAt(index);
    this.updateAvailableLocations();
    this.updateNoStockStatus();
  }

  loadAvailableStocks(productId: number, itemIndex: number): void {
    this.inventoryService.getProductStockByLocations(productId).subscribe({
      next: (stocks) => {
        // Фильтруем только ячейки с положительным остатком
        const filteredStocks = stocks.filter((s) => s.quantity > 0);

        const newMap = new Map(this.availableStocksMap());
        newMap.set(itemIndex, filteredStocks);
        this.availableStocksMap.set(newMap);

        // Обновляем статус наличия товара
        const newNoStockMap = new Map(this.hasNoStock());
        newNoStockMap.set(itemIndex, filteredStocks.length === 0);
        this.hasNoStock.set(newNoStockMap);

        this.updateAvailableLocations();
        this.updateMaxQuantities();
        this.updateNoStockStatus();
      },
      error: (err) => {
        console.error('Ошибка загрузки остатков:', err);
        const newMap = new Map(this.availableStocksMap());
        newMap.set(itemIndex, []);
        this.availableStocksMap.set(newMap);

        const newNoStockMap = new Map(this.hasNoStock());
        newNoStockMap.set(itemIndex, true);
        this.hasNoStock.set(newNoStockMap);

        this.updateAvailableLocations();
        this.updateMaxQuantities();
        this.updateNoStockStatus();
      },
    });
  }

  updateNoStockStatus(): void {
    const totalItems = this.items.length;
    if (totalItems === 0) {
      this.allItemsNoStock.set(false);
      return;
    }

    // Проверяем, есть ли хотя бы один товар с остатками
    let hasAnyStock = false;
    for (let i = 0; i < totalItems; i++) {
      const stocks = this.availableStocksMap().get(i) || [];
      if (stocks.length > 0) {
        hasAnyStock = true;
        break;
      }
    }

    this.allItemsNoStock.set(!hasAnyStock);
  }

  isItemOutOfStock(itemIndex: number): boolean {
    return this.hasNoStock().get(itemIndex) || false;
  }

  updateAvailableLocations(): void {
    const totalItems = this.items.length;
    if (totalItems === 0) {
      this.availableLocations.set([]);
      return;
    }

    // Находим ячейки, которые есть у ВСЕХ товаров (у которых есть остатки)
    const locationProductMap = new Map<
      number,
      { stock: ProductStock; productsFound: Set<number> }
    >();

    for (let i = 0; i < totalItems; i++) {
      const stocks = this.availableStocksMap().get(i) || [];
      // Пропускаем товары без остатков
      if (stocks.length === 0) continue;

      for (const stock of stocks) {
        if (!locationProductMap.has(stock.locationId)) {
          locationProductMap.set(stock.locationId, {
            stock: stock,
            productsFound: new Set(),
          });
        }
        locationProductMap.get(stock.locationId)!.productsFound.add(i);
      }
    }

    // Оставляем только те локации, которые есть у всех товаров (у которых есть остатки)
    const itemsWithStock = Array.from({ length: totalItems }, (_, i) => i).filter(
      (i) => (this.availableStocksMap().get(i) || []).length > 0,
    );

    const validLocations: ProductStock[] = [];
    for (const [locationId, data] of locationProductMap) {
      if (data.productsFound.size === itemsWithStock.length && itemsWithStock.length > 0) {
        validLocations.push(data.stock);
      }
    }

    this.availableLocations.set(validLocations);

    // Если текущая выбранная ячейка невалидна, сбрасываем
    const currentLocationId = this.fromLocationId?.value;
    if (currentLocationId && !validLocations.some((l) => l.locationId === currentLocationId)) {
      this.fromLocationId?.setValue('', { emitEvent: false });
      this.updateMaxQuantities();
    }
  }

  updateMaxQuantities(): void {
    const locationIdRaw = this.fromLocationId?.value;

    // Преобразуем в число
    const locationId = locationIdRaw ? Number(locationIdRaw) : null;

    if (!locationId || isNaN(locationId)) {
      this.maxQuantities.set(new Map());
      return;
    }

    const newMaxQuantities = new Map<number, number>();
    const totalItems = this.items.length;

    for (let i = 0; i < totalItems; i++) {
      const stocks = this.availableStocksMap().get(i) || [];
      console.log(
        `Поиск для товара ${i}, ищем locationId = ${locationId} (число) в stocks:`,
        stocks.map((s) => ({ id: s.locationId, type: typeof s.locationId, qty: s.quantity })),
      );

      // Сравниваем числа
      const stock = stocks.find((s) => s.locationId === locationId);
      const maxQty = stock?.quantity || 0;
      console.log(`Товар ${i}, найденный stock:`, stock, `maxQty = ${maxQty}`);
      newMaxQuantities.set(i, maxQty);

      const item = this.items.at(i);
      const quantityControl = item.get('quantity');
      if (quantityControl) {
        quantityControl.setValidators([
          Validators.required,
          Validators.min(1),
          Validators.max(maxQty),
        ]);
        quantityControl.updateValueAndValidity();

        const currentValue = quantityControl.value;
        if (currentValue > maxQty) {
          console.log(`Текущее значение ${currentValue} больше максимума ${maxQty}, сбрасываем`);
          quantityControl.setValue(maxQty > 0 ? maxQty : '', { emitEvent: false });
        }
      }
    }

    this.maxQuantities.set(newMaxQuantities);
  }

  getAvailableStocksForItem(itemIndex: number): ProductStock[] {
    return this.availableStocksMap().get(itemIndex) || [];
  }

  getMaxQuantityForItem(itemIndex: number): number {
    const max = this.maxQuantities().get(itemIndex) || 0;
    return max;
  }

  getLocationDisplayName(stock: ProductStock): string {
    // return `${stock.locationFullName} (${stock.warehouseName}) - ${stock.quantity} ${stock.unit}`;
    return `${stock.locationFullName} - ${stock.locationType}`;
  }

  onSubmit(): void {
    if (this.shipmentForm.invalid) {
      this.shipmentForm.markAllAsTouched();
      return;
    }

    const locationId = this.fromLocationId?.value;
    if (!locationId) {
      this.errorMessage.set('Выберите ячейку для отгрузки');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const items = this.shipmentForm.value.items;
    if (!items || items.length === 0) {
      this.errorMessage.set('Добавьте хотя бы один товар');
      this.isLoading.set(false);
      return;
    }

    const request = {
      fromLocationId: locationId,
      items: items.map((item: any) => ({
        productId: item.productId,
        quantity: item.quantity,
      })),
    };

    this.movementService.createBatchShipment(request).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/inventory/summary']);
      },
      error: (error) => {
        let errorMsg = 'Ошибка при создании отгрузки';
        if (error.error?.message) {
          errorMsg = error.error.message;
        } else if (typeof error.error === 'string') {
          errorMsg = error.error;
        }
        this.errorMessage.set(errorMsg);
        this.isLoading.set(false);
      },
    });
  }
}
