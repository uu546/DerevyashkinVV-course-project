package project.warehouse.control.dto;

public class ProductDto {
    private Integer id;
    private String name;
    private String description;
    private String unit;
    private Integer categoryId;
    private String categoryTitle;
    private Boolean isPerishable;

    public ProductDto() {}

    public ProductDto(Integer id, String name, String description, String unit,
                      Integer categoryId, String categoryTitle, Boolean isPerishable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.unit = unit;
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.isPerishable = isPerishable;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public Integer getCategoryId() { return categoryId; }
    public String getCategoryTitle() { return categoryTitle; }
    public Boolean getIsPerishable() { return isPerishable; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setCategoryTitle(String categoryTitle) { this.categoryTitle = categoryTitle; }
    public void setIsPerishable(Boolean isPerishable) { this.isPerishable = isPerishable; }
}