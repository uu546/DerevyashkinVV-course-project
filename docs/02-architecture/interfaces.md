
# Спецификация интерфейсов

## Интерфейсы в архитектуре PCMEF

Интерфейсы в архитектуре PCMEF играют ключевую роль для реализации принципа **инверсии зависимостей (Dependency Inversion Principle)** из SOLID.

| Принцип | Описание | Реализация в проекте |
|---------|----------|----------------------|
| **DIP** | Модули верхних уровней не должны зависеть от модулей нижних уровней. Оба должны зависеть от абстракций. | Mediator (сервисы) зависит от интерфейсов `IRepository`, а не от конкретных реализаций репозиториев. |
| **IoC** | Управление зависимостями передаётся внешнему контейнеру (Spring). | Зависимости внедряются через конструктор. |

### Преимущества интерфейсов

| Преимущество | Описание |
|--------------|----------|
| **Тестируемость** | Легко подменить реальный репозиторий моком (Mockito) при юнит-тестировании сервисов. |
| **Гибкость** | Можно заменить реализацию репозитория (например, с JPA на JDBC), не меняя код сервиса. |
| **Слабая связанность** | Сервис не привязан к конкретной технологии доступа к данным. |
| **Чёткие границы** | Интерфейс определяет контракт — что делает слой, но не как. |

---

## Интерфейсы Foundation слоя (репозитории)

### IProductRepository

```java
public interface IProductRepository {
    Optional<Product> findById(Integer id);
    List<Product> findAll();
    Product save(Product product);
    void deleteById(Integer id);
    boolean existsById(Integer id);
}
```

### ILocationRepository
```java
public interface ILocationRepository {
    Optional<Location> findById(Integer id);
    List<Location> findAll();
    Location save(Location location);
    void deleteById(Integer id);
    boolean existsById(Integer id);
}
```

### IInventoryRepository
```java
public interface IInventoryRepository {
    Optional<Inventory> findById(Integer id);
    Optional<Inventory> findByProductAndLocation(Product product, Location location);
    List<Inventory> findByProductId(Integer productId);
    List<Inventory> findByLocationId(Integer locationId);
    List<Inventory> findAll();
    List<Inventory> findAllWithDetails();
    Inventory save(Inventory inventory);
    void addQuantity(Integer productId, Integer locationId, Integer quantity);
    void subtractQuantity(Integer productId, Integer locationId, Integer quantity);
    int getTotalStockByProduct(Integer productId);
}
```

### IMovementRepository
```java
public interface IMovementRepository {
    Optional<Movement> findById(Integer id);
    List<Movement> findByProductId(Integer productId);
    List<Movement> findAll();
    Movement save(Movement movement);
}
```

### IUserRepository
```java
public interface IUserRepository {
    Optional<User> findById(Integer id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
}
```

## Интерфейсы Mediator слоя (сервисы)

### IMovementService
```java
public interface IMovementService {
    void createReceipt(ReceiptBatchRequest request);
    void createShipment(ShipmentBatchRequest request);
    Movement moveProduct(Integer productId, Integer fromLocationId, Integer toLocationId, Integer quantity);
    int getCurrentStock(Integer productId, Integer locationId);
}
```

### IInventoryService
```java
public interface IInventoryService {
    int getCurrentStock(Integer productId, Integer locationId);
    InventoryResponse updateStock(Integer productId, Integer locationId, int newQuantity);
    InventoryResponse addStock(Integer productId, Integer locationId, int quantity);
    InventoryResponse subtractStock(Integer productId, Integer locationId, int quantity);
    int getTotalStockByProduct(Integer productId);
}
```

### IInventoryQueryService
```java
public interface IInventoryQueryService {
    InventorySummaryResponse getFullInventorySummary();
    List<ProductLocationStockDto> getProductStockByLocations(Integer productId);
}
```

### IAuthService
```java
public interface IAuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(RegisterRequest request);
}
```

## Диаграмма зависимостей интерфейсов
![alt text](/docs/02-architecture/images/interface.png)