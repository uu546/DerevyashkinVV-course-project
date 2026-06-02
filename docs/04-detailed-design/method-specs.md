## Спецификация методов

### 1. Control слой (REST Controllers)

#### MovementController.java

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final IMovementService movementService;

    public MovementController(IMovementService movementService) {
        this.movementService = movementService;
    }

    /**
     * Оформляет массовую приёмку товаров на склад.
     * 
     * @param request DTO со списком товаров и локацией
     * @return ResponseEntity с сообщением об успехе
     */
    @PostMapping("/receipt/batch")
    public ResponseEntity<String> createBatchReceipt(@Valid @RequestBody ReceiptBatchRequest request) {
        movementService.createBatchReceipt(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Товары успешно добавлены");
    }

    /**
     * Оформляет отгрузку товара со склада.
     * 
     * @param request DTO с данными отгрузки (productId, fromLocationId, quantity)
     * @return ResponseEntity с информацией о созданном движении
     * @throws RuntimeException если недостаточно товара или товар не найден
     */
    @PostMapping("/shipment")
    public ResponseEntity<MovementResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        Movement movement = movementService.createShipment(
            request.getProductId(),
            request.getFromLocationId(),
            request.getQuantity()
        );
        return ResponseEntity.ok(new MovementResponse(movement.getId(), "OK", "Отгрузка оформлена", movement.getMovementDate()));
    }

    /**
     * Перемещает товар из одной локации в другую.
     * 
     * @param request DTO с данными перемещения (productId, fromLocationId, toLocationId, quantity)
     * @return ResponseEntity с информацией о созданном движении
     * @throws RuntimeException если недостаточно товара или локации не найдены
     */
    @PostMapping("/move")
    public ResponseEntity<MovementResponse> moveProduct(@Valid @RequestBody MoveRequest request) {
        Movement movement = movementService.moveProduct(
            request.getProductId(),
            request.getFromLocationId(),
            request.getToLocationId(),
            request.getQuantity()
        );
        return ResponseEntity.ok(new MovementResponse(movement.getId(), "OK", "Товар перемещён", movement.getMovementDate()));
    }
}
```

### 2. Mediator слой (Services)

#### IMovementService.java
```java

public interface IMovementService {
    /**
     * Создаёт массовую приёмку товаров (несколько товаров за раз).
     *
     * @param request DTO со списком товаров и целевой локацией
     * @throws RuntimeException если любой из товаров или локация не найдены
     */
    void createBatchReceipt(ReceiptBatchRequest request);

    /**
     * Создаёт операцию массовой отгрузки товаров.
     *
     * @param request DTO со списком товаров и исходной локацией
     * @throws RuntimeException если любого товара недостаточно
     */
    void createBatchShipment(ShipmentBatchRequest request);

    /**
     * Перемещает товар между локациями.
     *
     * @param productId      идентификатор товара
     * @param fromLocationId идентификатор исходной локации
     * @param toLocationId   идентификатор целевой локации
     * @param quantity       количество товара
     * @return созданная сущность Movement
     * @throws RuntimeException если недостаточно товара или локации не найдены
     */
    Movement moveProduct(Integer productId, Integer fromLocationId, Integer toLocationId, Integer quantity);

    /**
     * Возвращает текущий остаток товара на указанной локации.
     *
     * @param productId  идентификатор товара
     * @param locationId идентификатор локации
     * @return количество товара (0, если товар не найден на локации)
     */
    int getCurrentStock(Integer productId, Integer locationId);
}
```

#### MovementServiceImpl.java (фрагменты реализации)
```java

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovementServiceImpl implements IMovementService {

    private final IMovementRepository movementRepository;
    private final IProductRepository productRepository;
    private final ILocationRepository locationRepository;
    private final IInventoryRepository inventoryRepository;

    public MovementServiceImpl(IMovementRepository movementRepository,
                               IProductRepository productRepository,
                               ILocationRepository locationRepository,
                               IInventoryRepository inventoryRepository) {
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Movement createReceipt(Integer productId, Integer toLocationId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        Location location = locationRepository.findById(toLocationId)
            .orElseThrow(() -> new RuntimeException("Location not found: " + toLocationId));

        inventoryRepository.addQuantity(productId, toLocationId, quantity);

        Movement movement = new Movement();
        movement.setProduct(product);
        movement.setToLocation(location);
        movement.setQuantity(quantity);

        return movementRepository.save(movement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Movement createShipment(Integer productId, Integer fromLocationId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        Location location = locationRepository.findById(fromLocationId)
            .orElseThrow(() -> new RuntimeException("Location not found: " + fromLocationId));

        int currentStock = getCurrentStock(productId, fromLocationId);
        if (currentStock < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + currentStock);
        }

        inventoryRepository.subtractQuantity(productId, fromLocationId, quantity);

        Movement movement = new Movement();
        movement.setProduct(product);
        movement.setFromLocation(location);
        movement.setQuantity(quantity);

        return movementRepository.save(movement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public int getCurrentStock(Integer productId, Integer locationId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        Location location = locationRepository.findById(locationId)
            .orElseThrow(() -> new RuntimeException("Location not found: " + locationId));

        return inventoryRepository.findByProductAndLocation(product, location)
            .map(Inventory::getQuantity)
            .orElse(0);
    }
}
```

#### IInventoryQueryService.java
```java

public interface IInventoryQueryService {

    /**
     * Формирует полную сводку по складам, локациям и остаткам товаров.
     *
     * @return DTO со сводной информацией (склады → локации → товары)
     */
    InventorySummaryResponse getFullInventorySummary();

    /**
     * Возвращает список ячеек с остатками для указанного товара.
     *
     * @param productId идентификатор товара
     * @return список DTO с информацией о ячейках и количестве
     */
    List<ProductLocationStockDto> getProductStockByLocations(Integer productId);
}
```

### 3. Foundation слой (Repositories)
#### IInventoryRepository.java
```java
package ru.edu.project.warehouse.foundation.interfaces;

import ru.edu.project.warehouse.entity.Inventory;
import ru.edu.project.warehouse.entity.Product;
import ru.edu.project.warehouse.entity.Location;
import java.util.List;
import java.util.Optional;

public interface IInventoryRepository {

    /**
     * Находит запись об остатках по идентификатору.
     *
     * @param id идентификатор записи Inventory
     * @return Optional с Inventory, если найден
     */
    Optional<Inventory> findById(Integer id);

    /**
     * Находит запись об остатках по товару и локации.
     *
     * @param product  товар
     * @param location локация
     * @return Optional с Inventory, если найден
     */
    Optional<Inventory> findByProductAndLocation(Product product, Location location);

    /**
     * Находит все записи об остатках для указанного товара.
     *
     * @param productId идентификатор товара
     * @return список Inventory
     */
    List<Inventory> findByProductId(Integer productId);

    /**
     * Находит все записи об остатках для указанной локации.
     *
     * @param locationId идентификатор локации
     * @return список Inventory
     */
    List<Inventory> findByLocationId(Integer locationId);

    /**
     * Возвращает все записи об остатках.
     *
     * @return список всех Inventory
     */
    List<Inventory> findAll();

    /**
     * Возвращает все записи об остатках с предварительной загрузкой связанных сущностей.
     * Использует JOIN FETCH для оптимизации производительности.
     *
     * @return список Inventory с загруженными product, category, unit, location, warehouse, type
     */
    List<Inventory> findAllWithDetails();

    /**
     * Сохраняет или обновляет запись об остатках.
     *
     * @param inventory сущность Inventory
     * @return сохранённая сущность
     */
    Inventory save(Inventory inventory);

    /**
     * Увеличивает количество товара на локации.
     * Если запись не существует — создаёт новую.
     *
     * @param productId  идентификатор товара
     * @param locationId идентификатор локации
     * @param quantity   количество для добавления (должно быть > 0)
     */
    void addQuantity(Integer productId, Integer locationId, Integer quantity);

    /**
     * Уменьшает количество товара на локации.
     *
     * @param productId  идентификатор товара
     * @param locationId идентификатор локации
     * @param quantity   количество для списания
     * @throws RuntimeException если недостаточно товара или запись не найдена
     */
    void subtractQuantity(Integer productId, Integer locationId, Integer quantity);

    /**
     * Возвращает общее количество товара на всех локациях.
     *
     * @param productId идентификатор товара
     * @return суммарное количество
     */
    int getTotalStockByProduct(Integer productId);
}
```

#### InventoryRepositoryImpl.java (фрагменты реализации)
```java
package ru.edu.project.warehouse.foundation.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepositoryImpl implements IInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void addQuantity(Integer productId, Integer locationId, Integer quantity) {
        // Проверяем существование записи
        String findJpql = "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.location.id = :locationId";
        Inventory existing = entityManager.createQuery(findJpql, Inventory.class)
            .setParameter("productId", productId)
            .setParameter("locationId", locationId)
            .getResultStream()
            .findFirst()
            .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            entityManager.merge(existing);
        } else {
            Product product = entityManager.find(Product.class, productId);
            Location location = entityManager.find(Location.class, locationId);
            
            Inventory newInventory = new Inventory();
            newInventory.setProduct(product);
            newInventory.setLocation(location);
            newInventory.setQuantity(quantity);
            entityManager.persist(newInventory);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Inventory> findAllWithDetails() {
        String jpql = "SELECT DISTINCT i FROM Inventory i " +
                      "LEFT JOIN FETCH i.product p " +
                      "LEFT JOIN FETCH p.category " +
                      "LEFT JOIN FETCH p.unit " +
                      "LEFT JOIN FETCH i.location l " +
                      "LEFT JOIN FETCH l.warehouse w " +
                      "LEFT JOIN FETCH l.type t " +
                      "ORDER BY w.id, l.id, p.id";
        return entityManager.createQuery(jpql, Inventory.class).getResultList();
    }
}
```

#### IProductRepository.java
```java
package ru.edu.project.warehouse.foundation.interfaces;

import ru.edu.project.warehouse.entity.Product;
import java.util.List;
import java.util.Optional;

public interface IProductRepository {

    /**
     * Находит товар по идентификатору.
     *
     * @param id идентификатор товара
     * @return Optional с Product, если найден
     */
    Optional<Product> findById(Integer id);

    /**
     * Возвращает список всех товаров.
     *
     * @return список всех Product
     */
    List<Product> findAll();

    /**
     * Сохраняет товар.
     *
     * @param product сущность Product
     * @return сохранённая сущность
     */
    Product save(Product product);

    /**
     * Удаляет товар по идентификатору.
     *
     * @param id идентификатор товара
     */
    void deleteById(Integer id);

    /**
     * Проверяет существование товара по идентификатору.
     *
     * @param id идентификатор товара
     * @return true, если товар существует
     */
    boolean existsById(Integer id);
}
```

#### ILocationRepository.java
```java
package ru.edu.project.warehouse.foundation.interfaces;

import ru.edu.project.warehouse.entity.Location;
import java.util.List;
import java.util.Optional;

public interface ILocationRepository {

    /**
     * Находит локацию по идентификатору.
     *
     * @param id идентификатор локации
     * @return Optional с Location, если найдена
     */
    Optional<Location> findById(Integer id);

    /**
     * Возвращает список всех локаций.
     *
     * @return список всех Location
     */
    List<Location> findAll();

    /**
     * Сохраняет локацию.
     *
     * @param location сущность Location
     * @return сохранённая сущность
     */
    Location save(Location location);

    /**
     * Проверяет существование локации по идентификатору.
     *
     * @param id идентификатор локации
     * @return true, если локация существует
     */
    boolean existsById(Integer id);
}
```

### Спецификация DTO
#### MovementResponse.java
```java
package ru.edu.project.warehouse.control.dto;

import java.time.LocalDate;

public class MovementResponse {
    private Integer movementId;
    private String status;
    private String message;
    private LocalDate movementDate;

    /**
     * Конструктор ответа на операцию движения.
     *
     * @param movementId   идентификатор созданного движения
     * @param status       статус операции (OK/ERROR)
     * @param message      текстовое сообщение
     * @param movementDate дата движения
     */
    public MovementResponse(Integer movementId, String status, String message, LocalDate movementDate) {
        this.movementId = movementId;
        this.status = status;
        this.message = message;
        this.movementDate = movementDate;
    }

    // Геттеры и сеттеры...
}
```

#### ReceiptBatchRequest.java
```java
package ru.edu.project.warehouse.control.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ReceiptBatchRequest {

    @NotNull(message = "toLocationId не может быть null")
    private Integer toLocationId;

    @Valid
    @Size(min = 1, message = "Должен быть хотя бы один товар")
    private List<ReceiptItem> items = new ArrayList<>();

    /**
     * Возвращает идентификатор целевой локации.
     *
     * @return toLocationId
     */
    public Integer getToLocationId() {
        return toLocationId;
    }

    /**
     * Устанавливает идентификатор целевой локации.
     *
     * @param toLocationId идентификатор локации
     */
    public void setToLocationId(Integer toLocationId) {
        this.toLocationId = toLocationId;
    }

    /**
     * Возвращает список товаров в поставке.
     *
     * @return список ReceiptItem
     */
    public List<ReceiptItem> getItems() {
        return items;
    }

    /**
     * Устанавливает список товаров в поставке.
     *
     * @param items список ReceiptItem
     */
    public void setItems(List<ReceiptItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}
```

## Сводная таблица методов по слоям

Таблица отражает распределение основных классов и их ключевых методов по слоям архитектуры PCMEF.

| Слой | Класс | Ключевые методы |
|-------|--------|----------------|
| **Control** | MovementController | createBatchReceipt(), createBatchShipment(), moveProduct() |
| **Control** | InventoryQueryController | getFullInventorySummary(), getProductStockByLocations() |
| **Control** | AuthController | login(), register() |
| **Mediator** | IMovementService | createReceipt(), createShipment(), moveProduct() |
| **Mediator** | IInventoryQueryService | getFullInventorySummary(), getProductStockByLocations() |
| **Mediator** | IAuthService | login(), register() |
| **Foundation** | IInventoryRepository | findById(), findByProductAndLocation(), addQuantity(), subtractQuantity() |
| **Foundation** | IMovementRepository | findById(), findByProductId(), save() |
| **Foundation** | IProductRepository | findById(), findAll(), save() |
| **Foundation** | ILocationRepository | findById(), findAll(), save() |
| **Foundation** | IUserRepository | findByEmail(), existsByEmail(), save() |
