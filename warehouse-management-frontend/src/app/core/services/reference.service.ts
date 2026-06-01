import { inject, Injectable, signal } from '@angular/core';
import { Product } from '../../features/movements/models/product';
import { Location } from '../../features/movements/models/location';
import { ApiService } from './api.service';

@Injectable({ providedIn: 'root' })
export class ReferenceService {
  private api = inject(ApiService);

  private productsSignal = signal<Product[]>([]);
  private locationsSignal = signal<Location[]>([]);

  products = this.productsSignal.asReadonly();
  locations = this.locationsSignal.asReadonly();

  loadProducts(): void {
    this.api.get<Product[]>('/products').subscribe({
      next: (data) => this.productsSignal.set(data),
      error: (err) => console.error('Ошибка загрузки товаров:', err),
    });
  }

  loadLocations(): void {
    this.api.get<Location[]>('/locations').subscribe({
      next: (data) => this.locationsSignal.set(data),
      error: (err) => console.error('Ошибка загрузки локаций:', err),
    });
  }
}
