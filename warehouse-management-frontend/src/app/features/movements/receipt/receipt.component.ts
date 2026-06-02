import { Component, inject, OnInit, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MovementService } from '../movement.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ReferenceService } from '../../../core/services/reference.service';
import { RoleService } from '../../../core/services/role.service';

@Component({
  selector: 'app-receipt',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './receipt.component.html',
  styleUrls: ['./receipt.component.css'],
})
export class ReceiptComponent implements OnInit {
  private fb = inject(FormBuilder);
  private movementService = inject(MovementService);
  private referenceService = inject(ReferenceService);
  private router = inject(Router);
  roleService = inject(RoleService);
  receiptForm: FormGroup = this.fb.group({
    toLocationId: ['', [Validators.required]],
    items: this.fb.array([]),
  });

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  get items(): FormArray {
    return this.receiptForm.get('items') as FormArray;
  }

  get locations() {
    return this.referenceService.locations;
  }

  get products() {
    return this.referenceService.products;
  }

  ngOnInit(): void {
    this.referenceService.loadLocations();
    this.referenceService.loadProducts();
    this.addItem();

    setTimeout(() => {
      console.log('asas', this.locations());
    }, 3000);
  }

  addItem(): void {
    this.items.push(
      this.fb.group({
        productId: ['', [Validators.required]],
        quantity: ['', [Validators.required, Validators.min(1)]],
      }),
    );
  }

  removeItem(index: number): void {
    this.items.removeAt(index);
  }

  onSubmit(): void {
    if (this.receiptForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const request = {
      toLocationId: this.receiptForm.value.toLocationId,
      items: this.receiptForm.value.items,
    };

    this.movementService.createBatchReceipt(request).subscribe({
      next: () => {
        console.log(request);
        this.isLoading.set(false);
        this.router.navigate(['/inventory/summary']);
      },
      error: (error) => {
        console.log(error);
        this.errorMessage.set(error.error?.message || 'Ошибка при создании приёмки');
        this.isLoading.set(false);
      },
    });
  }
}
