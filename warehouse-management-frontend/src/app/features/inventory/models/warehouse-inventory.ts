import { LocationInventory } from './location-inventory';

export interface WarehouseInventory {
  warehouseId: number;
  warehouseName: string;
  warehouseAddress: string;
  locations: LocationInventory[];
  totalItems: number;
}
