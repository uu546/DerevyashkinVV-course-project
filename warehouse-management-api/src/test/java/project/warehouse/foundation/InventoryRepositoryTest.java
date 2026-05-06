package project.warehouse.foundation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import project.warehouse.entity.*;
import project.warehouse.foundation.interfaces.IInventoryRepository;
import project.warehouse.foundation.interfaces.ILocationRepository;
import project.warehouse.foundation.interfaces.IProductRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class InventoryRepositoryTest {

    @Autowired
    private IInventoryRepository inventoryRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ILocationRepository locationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Product testProduct;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // Create category
        Category category = new Category();
        category.setTitle("Тестовая категория");
        entityManager.persist(category);

        // Create unit
        Unit unit = new Unit();
        unit.setTitle("шт");
        entityManager.persist(unit);

        // Create product
        testProduct = new Product();
        testProduct.setName("Тестовый товар");
        testProduct.setCategory(category);
        testProduct.setUnit(unit);
        testProduct.setIsPerishable(false);
        testProduct = productRepository.save(testProduct);

        // Create warehouse
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Тестовый склад");
        warehouse.setAddress("Тестовый адрес");
        entityManager.persist(warehouse);

        // Create type
        Type type = new Type();
        type.setTitle("Стеллаж");
        entityManager.persist(type);

        // Create location
        testLocation = new Location();
        testLocation.setName("Тестовая локация");
        testLocation.setWarehouse(warehouse);
        testLocation.setType(type);
        testLocation = locationRepository.save(testLocation);

        entityManager.flush();
    }

    @Test
    void testSaveAndFindById() {
        Inventory inventory = new Inventory();
        inventory.setProduct(testProduct);
        inventory.setLocation(testLocation);
        inventory.setQuantity(100);

        Inventory saved = inventoryRepository.save(inventory);

        assertNotNull(saved.getId());
        assertEquals(100, saved.getQuantity());

        Inventory found = inventoryRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(testProduct.getId(), found.getProduct().getId());
    }

    @Test
    void testAddQuantity_ExistingRecord() {
        Inventory inventory = new Inventory();
        inventory.setProduct(testProduct);
        inventory.setLocation(testLocation);
        inventory.setQuantity(100);
        inventoryRepository.save(inventory);

        inventoryRepository.addQuantity(testProduct.getId(), testLocation.getId(), 50);

        entityManager.flush();
        entityManager.clear();

        Inventory updated = inventoryRepository.findByProductAndLocation(testProduct, testLocation).orElse(null);
        assertNotNull(updated);
        assertEquals(150, updated.getQuantity());
    }

    @Test
    void testAddQuantity_NewRecord() {
        inventoryRepository.addQuantity(testProduct.getId(), testLocation.getId(), 50);

        entityManager.flush();
        entityManager.clear();

        Inventory inventory = inventoryRepository.findByProductAndLocation(testProduct, testLocation).orElse(null);
        assertNotNull(inventory);
        assertEquals(50, inventory.getQuantity());
    }

    @Test
    void testSubtractQuantity() {
        Inventory inventory = new Inventory();
        inventory.setProduct(testProduct);
        inventory.setLocation(testLocation);
        inventory.setQuantity(100);
        inventoryRepository.save(inventory);

        inventoryRepository.subtractQuantity(testProduct.getId(), testLocation.getId(), 30);

        entityManager.flush();
        entityManager.clear();

        Inventory updated = inventoryRepository.findByProductAndLocation(testProduct, testLocation).orElse(null);
        assertNotNull(updated);
        assertEquals(70, updated.getQuantity());
    }

    @Test
    void testGetTotalStockByProduct() {
        Inventory inventory = new Inventory();
        inventory.setProduct(testProduct);
        inventory.setLocation(testLocation);
        inventory.setQuantity(100);
        inventoryRepository.save(inventory);

        int totalStock = inventoryRepository.getTotalStockByProduct(testProduct.getId());
        assertEquals(100, totalStock);
    }
}