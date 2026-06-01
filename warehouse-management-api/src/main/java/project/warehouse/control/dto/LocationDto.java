package project.warehouse.control.dto;

public class LocationDto {
    private Integer id;
    private String name;
    private String fullName;
    private Integer warehouseId;
    private String warehouseName;
    private Integer typeId;
    private String typeTitle;
    private Integer temperatureId;
    private String temperatureTitle;

    public LocationDto() {}

    public LocationDto(Integer id, String name, String fullName,
                       Integer warehouseId, String warehouseName,
                       Integer typeId, String typeTitle,
                       Integer temperatureId, String temperatureTitle) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.warehouseId = warehouseId;
        this.warehouseName = warehouseName;
        this.typeId = typeId;
        this.typeTitle = typeTitle;
        this.temperatureId = temperatureId;
        this.temperatureTitle = temperatureTitle;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getFullName() { return fullName; }
    public Integer getWarehouseId() { return warehouseId; }
    public String getWarehouseName() { return warehouseName; }
    public Integer getTypeId() { return typeId; }
    public String getTypeTitle() { return typeTitle; }
    public Integer getTemperatureId() { return temperatureId; }
    public String getTemperatureTitle() { return temperatureTitle; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setWarehouseId(Integer warehouseId) { this.warehouseId = warehouseId; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public void setTypeTitle(String typeTitle) { this.typeTitle = typeTitle; }
    public void setTemperatureId(Integer temperatureId) { this.temperatureId = temperatureId; }
    public void setTemperatureTitle(String temperatureTitle) { this.temperatureTitle = temperatureTitle; }
}