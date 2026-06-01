import { TotalSummary } from "./total-summary";
import { WarehouseInventory } from "./warehouse-inventory";

export interface InventorySummaryResponse {
  warehouses: WarehouseInventory[];
  totalSummary: TotalSummary;
}