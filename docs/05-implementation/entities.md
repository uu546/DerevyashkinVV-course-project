
## Классы-сущности (Entity)

### 1. Category.java

```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Категория товаров".
 * Используется для группировки товаров по общим признакам.
 * 
 * @table categories
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String title;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    // Конструкторы
    public Category() {}

    public Category(String title) {
        this.title = title;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public List<Product> getProducts() { return products; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setProducts(List<Product> products) { this.products = products; }
}
```

### 2. Unit.java

```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Единица измерения".
 * Определяет единицы измерения товаров (шт, кг, л и т.д.).
 */
@Entity
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String title;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    // Конструкторы
    public Unit() {}

    public Unit(String title) {
        this.title = title;
    }

    // Геттеры и Сеттеры
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public List<Product> getProducts() { return products; }

    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setProducts(List<Product> products) { this.products = products; }
}
```

### 3. Temperature.java
```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Температурный режим".
 * Определяет условия хранения для локаций.
 */
@Entity
@Table(name = "temperatures")
public class Temperature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    @OneToMany(mappedBy = "temperature", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();

    public Temperature() {}

    public Temperature(String title) {
        this.title = title;
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public List<Location> getLocations() { return locations; }

    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setLocations(List<Location> locations) { this.locations = locations; }
}
```

### 4. Type.java

```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Тип локации".
 * Определяет тип места хранения (стеллаж, паллета, холодильник).
 */
@Entity
@Table(name = "types")
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String title;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();

    // Конструкторы
    public Type() {}

    public Type(String title) {
        this.title = title;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public List<Location> getLocations() { return locations; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setLocations(List<Location> locations) { this.locations = locations; }
}
```

### 5. Warehouse.java

```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Склад".
 * Представляет физическое место хранения товаров.
 */
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

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();

    // Конструкторы
    public Warehouse() {}

    public Warehouse(String name, String address) {
        this.name = name;
        this.address = address;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public List<Location> getLocations() { return locations; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setLocations(List<Location> locations) { this.locations = locations; }
}
```

### 6. Product.java

```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Товар".
 * Представляет номенклатурную единицу, хранящуюся на складе.
 */
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Movement> movements = new ArrayList<>();

    // Конструкторы
    public Product() {}

    public Product(String name, Category category, Unit unit) {
        this.name = name;
        this.category = category;
        this.unit = unit;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Boolean getIsPerishable() { return isPerishable; }
    public Integer getExpiryDays() { return expiryDays; }
    public Category getCategory() { return category; }
    public Unit getUnit() { return unit; }
    public List<Inventory> getInventories() { return inventories; }
    public List<Movement> getMovements() { return movements; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setIsPerishable(Boolean isPerishable) { this.isPerishable = isPerishable; }
    public void setExpiryDays(Integer expiryDays) { this.expiryDays = expiryDays; }
    public void setCategory(Category category) { this.category = category; }
    public void setUnit(Unit unit) { this.unit = unit; }
    public void setInventories(List<Inventory> inventories) { this.inventories = inventories; }
    public void setMovements(List<Movement> movements) { this.movements = movements; }

    // ========== БИЗНЕС-МЕТОДЫ ==========
    public boolean isPerishable() {
        return Boolean.TRUE.equals(isPerishable);
    }

    public boolean isExpired(int daysSinceProduction) {
        if (!isPerishable() || expiryDays == null) {
            return false;
        }
        return daysSinceProduction > expiryDays;
    }
}
```

### 7. Location.java

```java
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Локация (ячейка хранения)".
 */
@Entity
@Table(name = "locations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"warehouse_id", "name", "type_id"}, name = "uq_locations_warehouse_name_type")
})
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

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Inventory> inventories = new ArrayList<>();

    // Конструкторы
    public Location() {}

    public Location(String name, Warehouse warehouse, Type type) {
        this.name = name;
        this.warehouse = warehouse;
        this.type = type;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getName() { return name; }
    public Warehouse getWarehouse() { return warehouse; }
    public Type getType() { return type; }
    public Temperature getTemperature() { return temperature; }
    public List<Inventory> getInventories() { return inventories; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
    public void setType(Type type) { this.type = type; }
    public void setTemperature(Temperature temperature) { this.temperature = temperature; }
    public void setInventories(List<Inventory> inventories) { this.inventories = inventories; }

    // ========== БИЗНЕС-МЕТОДЫ ==========
    public String getFullName() {
        return (warehouse != null ? warehouse.getName() : "") + " - " + name;
    }
}
```

### 8. Inventory.java

```java
import jakarta.persistence.*;

/**
 * Сущность "Остаток товара".
 */
@Entity
@Table(name = "inventories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "location_id"}, name = "uq_inventories_product_location")
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

    // Геттеры
    public Integer getId() { return id; }
    public Product getProduct() { return product; }
    public Location getLocation() { return location; }
    public Integer getQuantity() { return quantity; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setProduct(Product product) { this.product = product; }
    public void setLocation(Location location) { this.location = location; }
    public void setQuantity(Integer quantity) { this.quantity = quantity != null ? quantity : 0; }

    // ========== БИЗНЕС-МЕТОДЫ ==========
    public void addQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    public void subtractQuantity(int amount) {
        if (amount <= 0) return;
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            throw new IllegalArgumentException(
                "Недостаточно товара. Доступно: " + this.quantity + ", запрошено: " + amount
            );
        }
    }

    public boolean isAvailableQuantity(int requested) {
        return this.quantity >= requested;
    }
}
```

### 9. Movement.java

```java
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Сущность "Движение товара".
 */
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
    public Integer getId() { return id; }
    public Product getProduct() { return product; }
    public Location getFromLocation() { return fromLocation; }
    public Location getToLocation() { return toLocation; }
    public Integer getQuantity() { return quantity; }
    public LocalDate getMovementDate() { return movementDate; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setProduct(Product product) { this.product = product; }
    public void setFromLocation(Location fromLocation) { this.fromLocation = fromLocation; }
    public void setToLocation(Location toLocation) { this.toLocation = toLocation; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setMovementDate(LocalDate movementDate) { this.movementDate = movementDate; }

    // ========== БИЗНЕС-МЕТОДЫ ==========
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
```

### 10. User.java

```java
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность "Пользователь".
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "role", nullable = false)
    private String role = "USER";

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Конструкторы
    public User() {}

    public User(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Геттеры
    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getIsActive() { return isActive; }

    // Сеттеры
    public void setId(Integer id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setRole(String role) { this.role = role; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
```