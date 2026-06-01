package project.warehouse.foundation.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import project.warehouse.entity.Inventory;
import project.warehouse.entity.Location;
import project.warehouse.entity.Product;
import project.warehouse.foundation.interfaces.IInventoryRepository;

import java.util.*;

@Repository
public class InventoryRepository implements IInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

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
    public Optional<Inventory> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Inventory.class, id));
    }


    @Override
    @Transactional
    public void deleteById(Integer id) {
        Inventory inventory = entityManager.find(Inventory.class, id);
        if (inventory != null) {
            entityManager.remove(inventory);
        }
    }

    @Override
    @Transactional
    public void addQuantity(Integer productId, Integer locationId, Integer quantity) {
        // 1. Проверяем, существует ли запись Inventory для этой пары product + location
        String findJpql = "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.location.id = :locationId";
        TypedQuery<Inventory> findQuery = entityManager.createQuery(findJpql, Inventory.class);
        findQuery.setParameter("productId", productId);
        findQuery.setParameter("locationId", locationId);

        Inventory existing = findQuery.getResultStream().findFirst().orElse(null);

        if (existing != null) {
            // 2. Если запись существует — обновляем количество
            String updateJpql = "UPDATE Inventory i SET i.quantity = i.quantity + :quantity " +
                    "WHERE i.product.id = :productId AND i.location.id = :locationId";
            entityManager.createQuery(updateJpql)
                    .setParameter("quantity", quantity)
                    .setParameter("productId", productId)
                    .setParameter("locationId", locationId)
                    .executeUpdate();
        } else {
            // 3. Если записи нет — создаём новую
            // Загружаем Product и Location
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
    }

    @Override
    @Transactional
    public void subtractQuantity(Integer productId, Integer locationId, Integer quantity) {
        // 1. Проверяем существование записи
        String findJpql = "SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.location.id = :locationId";
        TypedQuery<Inventory> findQuery = entityManager.createQuery(findJpql, Inventory.class);
        findQuery.setParameter("productId", productId);
        findQuery.setParameter("locationId", locationId);

        Inventory existing = findQuery.getResultStream().findFirst().orElse(null);

        if (existing == null) {
            // Если записи нет, списывать нечего
            throw new RuntimeException("No inventory record found for product " + productId +
                    " at location " + locationId);
        }

        // 2. Проверяем достаточность количества
        if (existing.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + existing.getQuantity() +
                    ", requested: " + quantity);
        }

        // 3. Обновляем количество
        String updateJpql = "UPDATE Inventory i SET i.quantity = i.quantity - :quantity " +
                "WHERE i.product.id = :productId AND i.location.id = :locationId";
        int updated = entityManager.createQuery(updateJpql)
                .setParameter("quantity", quantity)
                .setParameter("productId", productId)
                .setParameter("locationId", locationId)
                .executeUpdate();

        if (updated == 0) {
            throw new RuntimeException("Failed to update inventory");
        }
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
    public int getTotalStockByProduct(Integer productId) {
        String jpql = "SELECT SUM(i.quantity) FROM Inventory i WHERE i.product.id = :productId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("productId", productId);
        Long result = query.getSingleResult();
        return result != null ? result.intValue() : 0;
    }
}