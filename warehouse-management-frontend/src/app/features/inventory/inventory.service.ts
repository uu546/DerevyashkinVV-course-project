import { inject, Injectable } from '@angular/core';
import { ApiService } from '../../core/services/api.service';
import { Observable, tap } from 'rxjs';
import { ProductStock } from './models/product-stock';

@Injectable({
  providedIn: 'root',
})
@Injectable({ providedIn: 'root' })
export class InventoryService {
  private api = inject(ApiService);

  getProductStockByLocations(productId: number): Observable<ProductStock[]> {
    return this.api.get<ProductStock[]>(`/inventory/product/${productId}/locations`).pipe(tap((v) => console.log("sss", v)));
  }
}
