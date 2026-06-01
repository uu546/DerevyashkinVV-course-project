import { ShipmentItem } from "./shipment-item";

export interface ShipmentBatchRequest {
  fromLocationId: number;
  items: ShipmentItem[];
}
