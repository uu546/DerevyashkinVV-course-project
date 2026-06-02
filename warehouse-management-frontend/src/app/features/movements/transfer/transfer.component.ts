import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MovementService } from '../movement.service';
import { ReferenceService } from '../../../core/services/reference.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { InventoryService } from '../../inventory/inventory.service';
import { ProductStock } from '../../inventory/models/product-stock';
import { Location } from '../../movements/models/location';
import { RoleService } from '../../../core/services/role.service';

@Component({
  selector: 'app-transfer',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css'],
})
export class TransferComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private movementService = inject(MovementService);
  private referenceService = inject(ReferenceService);
  private inventoryService = inject(InventoryService);
  private router = inject(Router);
roleService = inject(RoleService);
  transferForm: FormGroup = this.fb.group({
    productId: ['', [Validators.required]],
    fromLocationId: ['', [Validators.required]],
    toLocationId: ['', [Validators.required]],
    quantity: ['', [Validators.required, Validators.min(1)]],
  });

  hasStock = signal(false);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  availableStocks = signal<ProductStock[]>([]);
  availableTargetLocations = signal<Location[]>([]);
  allLocations = signal<Location[]>([]);
  maxQuantity = signal(0);

  private subscription: Subscription | null = null;

  get products() {
    return this.referenceService.products;
  }

  get fromLocationId() {
    return this.transferForm.get('fromLocationId');
  }

  get toLocationId() {
    return this.transferForm.get('toLocationId');
  }

  get productId() {
    return this.transferForm.get('productId');
  }

  ngOnInit(): void {
    this.referenceService.loadProducts();
    this.referenceService.loadLocations();

    // Загружаем все локации
    setTimeout(() => {
      this.allLocations.set(this.referenceService.locations());
    }, 500);

    this.productId?.valueChanges.subscribe((productIdValue: string | null) => {
      const productId = productIdValue ? Number(productIdValue) : null;
      if (productId && !isNaN(productId)) {
        this.loadAvailableStocks(productId);
        this.fromLocationId?.setValue('', { emitEvent: false });
        this.toLocationId?.setValue('', { emitEvent: false });
        this.transferForm.get('quantity')?.setValue('', { emitEvent: false });
        this.maxQuantity.set(0);
        this.availableTargetLocations.set([]);
      } else {
        this.availableStocks.set([]);
        this.availableTargetLocations.set([]);
        this.maxQuantity.set(0);
        this.hasStock.set(false);
      }
    });

    this.fromLocationId?.valueChanges.subscribe((locationIdValue: string | null) => {
      const locationId = locationIdValue ? Number(locationIdValue) : null;
      if (locationId && !isNaN(locationId)) {
        this.updateMaxQuantity(locationId);
        this.updateAvailableTargetLocations(locationId);
      } else {
        this.maxQuantity.set(0);
        this.availableTargetLocations.set([]);
      }
    });
  }

  getProductName(): string {
    const productId = +this.productId?.value;
    if (!productId) return '—';
    const product = this.products().find((p) => p.id === productId);
    return product ? product.name : '—';
  }

  getFromLocationName(): string {
    const locationId = +this.fromLocationId?.value;
    if (!locationId) return '—';
    const stock = this.availableStocks().find((s) => s.locationId === locationId);
    return stock ? stock.locationFullName : '—';
  }

  getToLocationName(): string {
    const locationId = +this.toLocationId?.value;
    if (!locationId) return '—';
    const location = this.allLocations().find((l) => l.id === locationId);
    return location ? location.fullName || location.name : '—';
  }

  getTargetLocationDisplayName(location: Location): string {
    return `${location.fullName} [${location.typeTitle}]`;
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  loadAvailableStocks(productId: number): void {
    this.inventoryService.getProductStockByLocations(productId).subscribe({
      next: (stocks) => {
        const filteredStocks = stocks.filter((s) => s.quantity > 0);
        this.availableStocks.set(filteredStocks);
        this.hasStock.set(filteredStocks.length > 0);
      },
      error: (err) => {
        console.error('Ошибка загрузки остатков:', err);
        this.availableStocks.set([]);
        this.hasStock.set(false);
      },
    });
  }

  updateMaxQuantity(locationId: number): void {
    const stock = this.availableStocks().find((s) => s.locationId === locationId);
    const maxQty = stock?.quantity || 0;
    this.maxQuantity.set(maxQty);

    const quantityControl = this.transferForm.get('quantity');
    if (quantityControl) {
      quantityControl.setValidators([
        Validators.required,
        Validators.min(1),
        Validators.max(maxQty),
      ]);
      quantityControl.updateValueAndValidity();

      const currentValue = quantityControl.value;
      if (currentValue > maxQty) {
        quantityControl.setValue(maxQty > 0 ? maxQty : '', { emitEvent: false });
      }
    }
  }

  updateAvailableTargetLocations(excludeLocationId: number): void {
    const targets = this.allLocations().filter((l) => l.id !== excludeLocationId);
    this.availableTargetLocations.set(targets);

    const currentToLocation = this.toLocationId?.value;
    if (currentToLocation && !targets.some((t) => t.id === currentToLocation)) {
      this.toLocationId?.setValue('', { emitEvent: false });
    }
  }

  getLocationDisplayName(stock: ProductStock): string {
    return `${stock.locationFullName} [${stock.locationType}] - ${stock.quantity} ${stock.unit}`;
  }

  isLocationValid(): boolean {
    const locationId = this.fromLocationId?.value;
    if (!locationId) return false;
    return this.availableStocks().some((s) => s.locationId === +locationId);
  }

  onSubmit(): void {
    if (this.transferForm.invalid) {
      this.transferForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const request = {
      productId: this.transferForm.value.productId,
      fromLocationId: this.transferForm.value.fromLocationId,
      toLocationId: this.transferForm.value.toLocationId,
      quantity: this.transferForm.value.quantity,
    };

    this.movementService.moveProduct(request).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/inventory/summary']);
      },
      error: (error) => {
        let errorMsg = 'Ошибка при перемещении товара';
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
