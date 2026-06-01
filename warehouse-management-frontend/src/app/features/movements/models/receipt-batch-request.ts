import { ReceiptItem } from './receipt-item';

export interface ReceiptBatchRequest {
  toLocationId: number;
  items: ReceiptItem[];
}
