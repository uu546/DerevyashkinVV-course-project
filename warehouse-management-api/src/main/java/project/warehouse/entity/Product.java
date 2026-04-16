package project.warehouse.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "is_perishable")
    private Boolean isPerishable = false;

    @Column(name = "expiry_days")
    private Integer expiryDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Inventory> inventories = new ArrayList<>();

    // Конструкторы
    public Product() {}

    public Product(String name, String description, Boolean isPerishable,
                   Integer expiryDays, Category category, Unit unit) {
        this.name = name;
        this.description = description;
        this.isPerishable = isPerishable;
        this.expiryDays = expiryDays;
        this.category = category;
        this.unit = unit;
    }

    public Product(Integer id, String name, String description, Boolean isPerishable,
                   Integer expiryDays, Category category, Unit unit, List<Inventory> inventories) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPerishable = isPerishable;
        this.expiryDays = expiryDays;
        this.category = category;
        this.unit = unit;
        this.inventories = inventories;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getIsPerishable() {
        return isPerishable;
    }

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public Category getCategory() {
        return category;
    }

    public Unit getUnit() {
        return unit;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsPerishable(Boolean isPerishable) {
        this.isPerishable = isPerishable;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }

}