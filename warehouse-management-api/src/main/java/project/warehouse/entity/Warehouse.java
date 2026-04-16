package project.warehouse.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String address;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>();

    // Конструкторы
    public Warehouse() {}

    public Warehouse(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Warehouse(Integer id, String name, String address, List<Location> locations) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.locations = locations != null ? locations : new ArrayList<>();
    }

    // Геттеры
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Location> getLocations() {
        return locations;
    }

    // Сеттеры
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations != null ? locations : new ArrayList<>();
    }
}