package project.warehouse.control.dto;

import java.util.ArrayList;
import java.util.List;

public class LocationInventoryDto {
    private Integer locationId;
    private String locationName;
    private String locationType;  // тип ячейки (стеллаж, паллета)
    private List<ProductInventoryDto> products;
    private Integer totalItems;  // суммарное количество товаров в ячейке
    public LocationInventoryDto() {
        this.products = new ArrayList<>();
    }

    public LocationInventoryDto(Integer locationId, String locationName, String locationType,
                                List<ProductInventoryDto> products, Integer totalItems) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationType = locationType;
        this.products = products != null ? products : new ArrayList<>();
        this.totalItems = totalItems;
    }

    // Геттеры
    public Integer getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationType() {
        return locationType;
    }

    public List<ProductInventoryDto> getProducts() {
        return products;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    // Сеттеры
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public void setProducts(List<ProductInventoryDto> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
}