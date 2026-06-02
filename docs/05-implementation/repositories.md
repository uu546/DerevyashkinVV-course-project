## Реализация слоя Foundation (репозитории)

### Структура репозиториев

| Интерфейс | Реализация | Назначение |
|-----------|------------|------------|
| `IProductRepository` | `ProductRepositoryImpl` | Доступ к товарам (Product) |
| `ILocationRepository` | `LocationRepositoryImpl` | Доступ к локациям (Location) |
| `IInventoryRepository` | `InventoryRepositoryImpl` | Доступ к остаткам (Inventory) |
| `IMovementRepository` | `MovementRepositoryImpl` | Доступ к движениям (Movement) |
| `IUserRepository` | `UserRepositoryImpl` | Доступ к пользователям (User) |

---

### 1. IProductRepository.java (интерфейс)

```java
package ru.edu.project.warehouse.foundation.interfaces;

import ru.edu.project.warehouse.entity.Product;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с сущностью Product.
 * Определяет контракт для доступа к данным товаров.
 */
public interface IProductRepository {

    Optional<Product> findById(Integer id);
    
    Optional<Product> findByName(String name);
    
    List<Product> findAll();
    
    List<Product> findByCategoryId(Integer categoryId);
    
    List<Product> findByIsPerishableTrue();
    
    Product save(Product product);
    
    void deleteById(Integer id);
    
    boolean existsById(Integer id);
}
```

### 2. ProductRepositoryImpl.java (реализация)

```java
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements IProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Product> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    @Override
    public Optional<Product> findByName(String name) {
        String jpql = "SELECT p FROM Product p WHERE p.name = :name";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        query.setParameter("name", name);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<Product> findAll() {
        String jpql = "SELECT p FROM Product p";
        return entityManager.createQuery(jpql, Product.class).getResultList();
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        String jpql = "SELECT p FROM Product p WHERE p.category.id = :categoryId";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }

    @Override
    public List<Product> findByIsPerishableTrue() {
        String jpql = "SELECT p FROM Product p WHERE p.isPerishable = true";
        return entityManager.createQuery(jpql, Product.class).getResultList();
    }

    @Override
    @Transactional
    public Product save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String jpql = "SELECT COUNT(p) FROM Product p WHERE p.id = :id";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }
}
```

### 3. ILocationRepository.java (интерфейс)

```java

import ru.edu.project.warehouse.entity.Location;
import ru.edu.project.warehouse.entity.Warehouse;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с сущностью Location.
 * Определяет контракт для доступа к данным локаций (ячеек хранения).
 */
public interface ILocationRepository {

    Optional<Location> findById(Integer id);
    
    Optional<Location> findByNameAndWarehouse(String name, Warehouse warehouse);
    
    List<Location> findByWarehouseId(Integer warehouseId);
    
    List<Location> findByTypeId(Integer typeId);
    
    List<Location> findAll();
    
    Location save(Location location);
    
    void deleteById(Integer id);
    
    boolean existsById(Integer id);
}
```

### 3. LocationRepositoryImpl.java (реализация)

```java
package ru.edu.project.warehouse.foundation.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.edu.project.warehouse.entity.Location;
import ru.edu.project.warehouse.entity.Warehouse;
import ru.edu.project.warehouse.foundation.interfaces.ILocationRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class LocationRepositoryImpl implements ILocationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Location> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Location.class, id));
    }

    @Override
    public Optional<Location> findByNameAndWarehouse(String name, Warehouse warehouse) {
        String jpql = "SELECT l FROM Location l WHERE l.name = :name AND l.warehouse = :warehouse";
        TypedQuery<Location> query = entityManager.createQuery(jpql, Location.class);
        query.setParameter("name", name);
        query.setParameter("warehouse", warehouse);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<Location> findByWarehouseId(Integer warehouseId) {
        String jpql = "SELECT l FROM Location l WHERE l.warehouse.id = :warehouseId";
        TypedQuery<Location> query = entityManager.createQuery(jpql, Location.class);
        query.setParameter("warehouseId", warehouseId);
        return query.getResultList();
    }

    @Override
    public List<Location> findByTypeId(Integer typeId) {
        String jpql = "SELECT l FROM Location l WHERE l.type.id = :typeId";
        TypedQuery<Location> query = entityManager.createQuery(jpql, Location.class);
        query.setParameter("typeId", typeId);
        return query.getResultList();
    }

    @Override
    public List<Location> findAll() {
        String jpql = "SELECT l FROM Location l";
        return entityManager.createQuery(jpql, Location.class).getResultList();
    }

    @Override
    @Transactional
    public Location save(Location location) {
        if (location.getId() == null) {
            entityManager.persist(location);
            return location;
        } else {
            return entityManager.merge(location);
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Location location = entityManager.find(Location.class, id);
        if (location != null) {
            entityManager.remove(location);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String jpql = "SELECT COUNT(l) FROM Location l WHERE l.id = :id";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }
}
```

### 4. IInventoryRepository.java (интерфейс)

```java
package ru.edu.project.warehouse.foundation.interfaces;

import ru.edu.project.warehouse.entity.Inventory;
import ru.edu.project.warehouse.entity.Product;
import ru.edu.project.warehouse.entity.Location;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с сущностью Inventory.
 * Определяет контракт для доступа к данным остатков товаров.
 */
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

### 5. InventoryRepositoryImpl.java (реализация)

```java
package ru.edu.project.warehouse.foundation.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.edu.project.warehouse.entity.Inventory;
import ru.edu.project.warehouse.entity.Location;
import ru.edu.project.warehouse.entity.Product;
import ru.edu.project.warehouse.foundation.interfaces.IInventoryRepository;

import java.util.*;

@Repository
public class InventoryRepositoryImpl implements IInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Inventory> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Inventory.class, id));
    }

    @Override
    public Optional<Inventory> findByProductAndLocation(Product product, Location location) {
        String jpql = "SELECT i FROM Inventory i WHERE i.product = :product AND i.location = :location";
        TypedQuery<Inventory> query = entityManager.createQuery(jpql, Inventory.class);
        query.setParameter("product", product);
        query.setParameter("location", location);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<Inventory> findByProductId(Integer productId) {
        String jpql = "SELECT i FROM Inventory i WHERE i.product.id = :productId";
        TypedQuery<Inventory> query = entityManager.createQuery(jpql, Inventory.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    @Override
    public List<Inventory> findByLocationId(Integer locationId) {
        String jpql = "SELECT i FROM Inventory i WHERE i.location.id = :locationId";
        TypedQuery<Inventory> query = entityManager.createQuery(jpql, Inventory.class);
        query.setParameter("locationId", locationId);
        return query.getResultList();
    }

    @Override
    public List<Inventory> findAll() {
        String jpql = "SELECT i FROM Inventory i";
        return entityManager.createQuery(jpql, Inventory.class).getResultList();
    }

    @Override
    public List<Inventory> findAllWithDetails() {
        String jpql = "SELECT DISTINCT i FROM Inventory i " +
                      "LEFT JOIN FETCH i.product p " +
                      "LEFT JOIN FETCH p.category " +
                      "LEFT JOIN FETCH p.unit " +
                      "LEFT JOIN FETCH i.location l " +
                      "LEFT JOIN FETCH l.warehouse " +
                      "LEFT JOIN FETCH l.type " +
                      "ORDER BY l.warehouse.id, l.id, p.id";
        
        List<Inventory> result = entityManager.createQuery(jpql, Inventory.class).getResultList();
        Map<Integer, Inventory> uniqueMap = new LinkedHashMap<>();
        for (Inventory inv : result) {
            uniqueMap.putIfAbsent(inv.getId(), inv);
        }
        return new ArrayList<>(uniqueMap.values());
    }

    @Override
    @Transactional
    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            entityManager.persist(inventory);
            return inventory;
        } else {
            return entityManager.merge(inventory);
        }
    }

    @Override
    @Transactional
    public void addQuantity(Integer productId, Integer locationId, Integer quantity) {
        entityManager.flush();
        
        String findJpql = "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.location.id = :locationId";
        TypedQuery<Inventory> findQuery = entityManager.createQuery(findJpql, Inventory.class);
        findQuery.setParameter("productId", productId);
        findQuery.setParameter("locationId", locationId);
        
        Inventory existing = findQuery.getResultStream().findFirst().orElse(null);
        
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            entityManager.merge(existing);
        } else {
            Product product = entityManager.find(Product.class, productId);
            Location location = entityManager.find(Location.class, locationId);
            
            if (product == null) {
                throw new RuntimeException("Product not found with id: " + productId);
            }
            if (location == null) {
                throw new RuntimeException("Location not found with id: " + locationId);
            }
            
            Inventory newInventory = new Inventory();
            newInventory.setProduct(product);
            newInventory.setLocation(location);
            newInventory.setQuantity(quantity);
            
            entityManager.persist(newInventory);
        }
        
        entityManager.flush();
    }

    @Override
    @Transactional
    public void subtractQuantity(Integer productId, Integer locationId, Integer quantity) {
        entityManager.flush();
        
        String findJpql = "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.location.id = :locationId";
        TypedQuery<Inventory> findQuery = entityManager.createQuery(findJpql, Inventory.class);
        findQuery.setParameter("productId", productId);
        findQuery.setParameter("locationId", locationId);
        
        Inventory existing = findQuery.getResultStream().findFirst().orElse(null);
        
        if (existing == null) {
            throw new RuntimeException("No inventory record found for product " + productId +
                                       " at location " + locationId);
        }
        
        if (existing.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + existing.getQuantity() +
                                       ", requested: " + quantity);
        }
        
        existing.setQuantity(existing.getQuantity() - quantity);
        entityManager.merge(existing);
        
        entityManager.flush();
    }

    @Override
    public int getTotalStockByProduct(Integer productId) {
        String jpql = "SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("productId", productId);
        Long result = query.getSingleResult();
        return result != null ? result.intValue() : 0;
    }
}
```

### 6. IMovementRepository.java (интерфейс)

```java
package ru.edu.project.warehouse.foundation.interfaces;

import ru.edu.project.warehouse.entity.Movement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с сущностью Movement.
 * Определяет контракт для доступа к данным движений товаров.
 */
public interface IMovementRepository {

    Optional<Movement> findById(Integer id);
    
    List<Movement> findByProductId(Integer productId);
    
    List<Movement> findByFromLocationId(Integer locationId);
    
    List<Movement> findByToLocationId(Integer locationId);
    
    List<Movement> findByMovementDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Movement> findAll();
    
    Movement save(Movement movement);
}
```

### 7. MovementRepositoryImpl.java (реализация)

```java
package ru.edu.project.warehouse.foundation.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.edu.project.warehouse.entity.Movement;
import ru.edu.project.warehouse.foundation.interfaces.IMovementRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MovementRepositoryImpl implements IMovementRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Movement> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Movement.class, id));
    }

    @Override
    public List<Movement> findByProductId(Integer productId) {
        String jpql = "SELECT m FROM Movement m WHERE m.product.id = :productId ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    @Override
    public List<Movement> findByFromLocationId(Integer locationId) {
        String jpql = "SELECT m FROM Movement m WHERE m.fromLocation.id = :locationId ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("locationId", locationId);
        return query.getResultList();
    }

    @Override
    public List<Movement> findByToLocationId(Integer locationId) {
        String jpql = "SELECT m FROM Movement m WHERE m.toLocation.id = :locationId ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("locationId", locationId);
        return query.getResultList();
    }

    @Override
    public List<Movement> findByMovementDateBetween(LocalDate startDate, LocalDate endDate) {
        String jpql = "SELECT m FROM Movement m WHERE m.movementDate BETWEEN :startDate AND :endDate ORDER BY m.movementDate DESC";
        TypedQuery<Movement> query = entityManager.createQuery(jpql, Movement.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    @Override
    public List<Movement> findAll() {
        String jpql = "SELECT m FROM Movement m ORDER BY m.movementDate DESC";
        return entityManager.createQuery(jpql, Movement.class).getResultList();
    }

    @Override
    @Transactional
    public Movement save(Movement movement) {
        if (movement.getId() == null) {
            entityManager.persist(movement);
            return movement;
        } else {
            return entityManager.merge(movement);
        }
    }
}
```

### 8. IUserRepository.java (интерфейс)

```java
package ru.edu.project.warehouse.foundation.interfaces;

import ru.edu.project.warehouse.entity.User;
import java.util.Optional;

/**
 * Интерфейс репозитория для работы с сущностью User.
 * Определяет контракт для доступа к данным пользователей.
 */
public interface IUserRepository {

    Optional<User> findById(Integer id);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    User save(User user);
}
```

### 9. UserRepositoryImpl.java (реализация)

```java
package ru.edu.project.warehouse.foundation.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.edu.project.warehouse.entity.User;
import ru.edu.project.warehouse.foundation.interfaces.IUserRepository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements IUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }
}
```

### Общие принципы реализации репозиториев

| Принцип | Описание |
|----------|----------|
| `@Repository` | Аннотация Spring, обозначающая компонент доступа к данным |
| `@PersistenceContext` | Внедрение EntityManager для работы с JPA |
| `@Transactional` | Выполнение операций изменения данных в рамках транзакции |
| `persist()` / `merge()` | `persist()` используется для создания новой записи, `merge()` — для обновления существующей |
| JPQL | Используется для объектно-ориентированных запросов к сущностям |
| JOIN FETCH | Применяется для предотвращения проблемы N+1 при загрузке связанных сущностей |