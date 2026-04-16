package project.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temperature_id")
    private Temperature temperature;

    // Конструкторы
    public Location() {}

    public Location(String name, Warehouse warehouse, Type type, Temperature temperature) {
        this.name = name;
        this.warehouse = warehouse;
        this.type = type;
        this.temperature = temperature;
    }

    public Location(Integer id, String name, Warehouse warehouse, Type type, Temperature temperature) {
        this.id = id;
        this.name = name;
        this.warehouse = warehouse;
        this.type = type;
        this.temperature = temperature;
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Type getType() {
        return type;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }
}