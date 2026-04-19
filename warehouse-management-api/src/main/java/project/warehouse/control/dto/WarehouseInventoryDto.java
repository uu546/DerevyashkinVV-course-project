package project.warehouse.control.dto;

import java.util.ArrayList;
import java.util.List;

public class WarehouseInventoryDto {
    private Integer warehouseId;
    private String warehouseName;
    private String warehouseAddress;
    private List<LocationInventoryDto> locations;
    private Integer totalItems;  // суммарное количество товаров на складе (штук)
    // Конструктор по умолчанию
    public WarehouseInventoryDto() {
        this.locations = new ArrayList<>();
    }

    // Конструктор со всеми полями
    public WarehouseInventoryDto(Integer warehouseId, String warehouseName, String warehouseAddress,
                                 List<LocationInventoryDto> locations, Integer totalItems) {
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.warehouseAddress = warehouseAddress;
        this.locations = locations != null ? locations : new ArrayList<>();
        this.totalItems = totalItems;
    }

    // Геттеры
    public Integer getWarehouseId() {
        return warehouseId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public List<LocationInventoryDto> getLocations() {
        return locations;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    // Сеттеры
    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }

    public void setLocations(List<LocationInventoryDto> locations) {
        this.locations = locations != null ? locations : new ArrayList<>();
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
}