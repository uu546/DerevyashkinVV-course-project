package project.warehouse.control.dto;

public class ProductInventoryDto {
    private Integer productId;
    private String productName;
    private String unit;
    private Integer quantity;
    private Boolean isPerishable;

    // Конструктор по умолчанию
    public ProductInventoryDto() {}

    // Конструктор со всеми полями
    public ProductInventoryDto(Integer productId, String productName, String unit, Integer quantity, Boolean isPerishable) {
        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.quantity = quantity;
        this.isPerishable = isPerishable;
    }

    // Геттеры
    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getUnit() {
        return unit;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Boolean getIsPerishable() {
        return isPerishable;
    }

    // Сеттеры
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setIsPerishable(Boolean isPerishable) {
        this.isPerishable = isPerishable;
    }
}