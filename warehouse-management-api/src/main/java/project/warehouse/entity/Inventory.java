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

    /**
     * Увеличить количество товара
     * @param amount количество для добавления (должно быть > 0)
     */
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    /**
     * Уменьшить количество товара
     * @param amount количество для списания (должно быть > 0 и не больше текущего остатка)
     * @throws IllegalArgumentException если недостаточно товара
     */
    public void subtractQuantity(int amount) {
        if (amount <= 0) {
            return;
        }
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            throw new IllegalArgumentException("Недостаточно товара на складе. Доступно: " + this.quantity + ", запрошено: " + amount);
        }
    }

    /**
     * Проверить, достаточно ли товара
     * @param requestedQuantity запрашиваемое количество
     * @return true если достаточно
     */
    public boolean isAvailableQuantity(int requestedQuantity) {
        return this.quantity >= requestedQuantity;
    }
}