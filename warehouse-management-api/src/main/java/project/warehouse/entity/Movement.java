package project.warehouse.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "movements")
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id")
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location_id")
    private Location toLocation;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "movement_date", nullable = false)
    private LocalDate movementDate = LocalDate.now();

    // Конструкторы
    public Movement() {}

    public Movement(Product product, Location fromLocation, Location toLocation, Integer quantity) {
        this.product = product;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.quantity = quantity;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDate getMovementDate() {
        return movementDate;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
    }

    // Бизнес-методы
    public boolean isReceipt() {
        return fromLocation == null;
    }

    public boolean isShipment() {
        return toLocation == null;
    }

    public boolean isTransfer() {
        return fromLocation != null && toLocation != null;
    }
}