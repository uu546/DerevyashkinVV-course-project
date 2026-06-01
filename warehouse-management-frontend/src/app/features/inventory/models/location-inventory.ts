import { ProductInventory } from "./product-inventory";

export interface LocationInventory {
  locationId: number;
  locationName: string;
  locationType: string;
  products: ProductInventory[];
  totalItems: number;
}
