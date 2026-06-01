package project.warehouse.control.dto;

public class ProductLocationStockDto {
    private Integer locationId;
    private String locationName;
    private String locationFullName;
    private String locationType;
    private String warehouseName;
    private Integer quantity;
    private String unit;
    public ProductLocationStockDto() {}


    public ProductLocationStockDto(Integer locationId, String locationName, String locationFullName,
                                   String locationType, String warehouseName, Integer quantity, String unit) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationFullName = locationFullName;
        this.locationType = locationType;
        this.warehouseName = warehouseName;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Геттеры
    public Integer getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
    public String getLocationFullName() { return locationFullName; }
    public String getLocationType() { return locationType; }
    public String getWarehouseName() { return warehouseName; }
    public Integer getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    // Сеттеры
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setLocationFullName(String locationFullName) { this.locationFullName = locationFullName; }
    public void setLocationType(String locationType) { this.locationType = locationType; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
}
