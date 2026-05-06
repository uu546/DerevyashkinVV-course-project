package project.warehouse.control.dto;

import java.util.ArrayList;
import java.util.List;

public class InventorySummaryResponse {
    private List<WarehouseInventoryDto> warehouses;
    private TotalSummaryDto totalSummary;
    public InventorySummaryResponse() {
        this.warehouses = new ArrayList<>();
    }

    public InventorySummaryResponse(List<WarehouseInventoryDto> warehouses, TotalSummaryDto totalSummary) {
        this.warehouses = warehouses != null ? warehouses : new ArrayList<>();
        this.totalSummary = totalSummary;
    }

    // Геттеры
    public List<WarehouseInventoryDto> getWarehouses() {
        return warehouses;
    }

    public TotalSummaryDto getTotalSummary() {
        return totalSummary;
    }

    // Сеттеры
    public void setWarehouses(List<WarehouseInventoryDto> warehouses) {
        this.warehouses = warehouses != null ? warehouses : new ArrayList<>();
    }

    public void setTotalSummary(TotalSummaryDto totalSummary) {
        this.totalSummary = totalSummary;
    }
}
