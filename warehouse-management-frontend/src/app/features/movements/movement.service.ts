import { inject, Injectable } from '@angular/core';
import { ApiService } from '../../core/services/api.service';
import { ReceiptBatchRequest } from './models/receipt-batch-request';
import { Observable } from 'rxjs';
import { ShipmentBatchRequest } from './models/shipment-batch-request';
import { MovementResponse } from './models/movement-response';
import { MoveRequest } from './models/move-request';

@Injectable({ providedIn: 'root' })
export class MovementService {
  private api = inject(ApiService);

  createBatchReceipt(request: ReceiptBatchRequest): Observable<string> {
    return this.api.post('/movements/receipt/batch', request);
  }

  createBatchShipment(request: ShipmentBatchRequest): Observable<string> {
    return this.api.post('/movements/shipment/batch', request);
  }

  moveProduct(request: MoveRequest): Observable<MovementResponse> {
    return this.api.post('/movements/move', request);
  }
}
