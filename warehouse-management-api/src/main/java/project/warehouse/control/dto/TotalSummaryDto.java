package project.warehouse.control.dto;


public class TotalSummaryDto {
    private Integer totalWarehouses;
    private Integer totalLocations;
    private Integer totalProducts;
    private Integer totalItems;  // общее количество товаров на всех складах
    // Конструктор по умолчанию
    public TotalSummaryDto() {}

    // Конструктор со всеми полями
    public TotalSummaryDto(Integer totalWarehouses, Integer totalLocations, Integer totalProducts, Integer totalItems) {
        this.totalWarehouses = totalWarehouses;
        this.totalLocations = totalLocations;
        this.totalProducts = totalProducts;
        this.totalItems = totalItems;
    }

    // Геттеры
    public Integer getTotalWarehouses() {
        return totalWarehouses;
    }

    public Integer getTotalLocations() {
        return totalLocations;
    }

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    // Сеттеры
    public void setTotalWarehouses(Integer totalWarehouses) {
        this.totalWarehouses = totalWarehouses;
    }

    public void setTotalLocations(Integer totalLocations) {
        this.totalLocations = totalLocations;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }
}