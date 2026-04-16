package project.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "location_id"})
})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private Integer quantity = 0;

    // Конструкторы
    public Inventory() {}

    public Inventory(Product product, Location location, Integer quantity) {
        this.product = product;
        this.location = location;
        this.quantity = quantity != null ? quantity : 0;
    }

    public Inventory(Integer id, Product product, Location location, Integer quantity) {
        this.id = id;
        this.product = product;
        this.location = location;
        this.quantity = quantity != null ? quantity : 0;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity != null ? quantity : 0;
    }
}