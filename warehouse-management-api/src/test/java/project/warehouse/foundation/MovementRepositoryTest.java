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
import project.warehouse.foundation.interfaces.IMovementRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class MovementRepositoryTest {

    @Autowired
    private IMovementRepository movementRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Product testProduct;
    private Location fromLocation;
    private Location toLocation;
    private Movement testMovement;

    @BeforeEach
    void setUp() {
        // Создаём категорию
        Category category = new Category();
        category.setTitle("Тестовая категория");
        entityManager.persist(category);

        // Создаём единицу измерения
        Unit unit = new Unit();
        unit.setTitle("шт");
        entityManager.persist(unit);

        // Создаём товар
        testProduct = new Product();
        testProduct.setName("Тестовый товар");
        testProduct.setCategory(category);
        testProduct.setUnit(unit);
        testProduct.setIsPerishable(false);
        entityManager.persist(testProduct);

        // Создаём склад
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Тестовый склад");
        warehouse.setAddress("Тестовый адрес");
        entityManager.persist(warehouse);

        // Создаём тип локации
        Type type = new Type();
        type.setTitle("Стеллаж");
        entityManager.persist(type);

        // Создаём исходную локацию
        fromLocation = new Location();
        fromLocation.setName("Ячейка А-1");
        fromLocation.setWarehouse(warehouse);
        fromLocation.setType(type);
        entityManager.persist(fromLocation);

        // Создаём целевую локацию
        toLocation = new Location();
        toLocation.setName("Ячейка Б-2");
        toLocation.setWarehouse(warehouse);
        toLocation.setType(type);
        entityManager.persist(toLocation);

        // Создаём тестовое движение
        testMovement = new Movement();
        testMovement.setProduct(testProduct);
        testMovement.setFromLocation(fromLocation);
        testMovement.setToLocation(toLocation);
        testMovement.setQuantity(50);
        testMovement.setMovementDate(LocalDate.now());
        entityManager.persist(testMovement);

        entityManager.flush();
    }

    // ==================== ТЕСТЫ findById ====================

    @Test
    void testFindById_ShouldReturnMovement() {
        Movement found = movementRepository.findById(testMovement.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(testMovement.getId(), found.getId());
        assertEquals(testProduct.getId(), found.getProduct().getId());
        assertEquals(50, found.getQuantity());
    }

    @Test
    void testFindById_NotFound_ShouldReturnEmpty() {
        Movement found = movementRepository.findById(999).orElse(null);

        assertNull(found);
    }

    // ==================== ТЕСТЫ findAll ====================

    @Test
    void testFindAll_ShouldReturnList() {
        List<Movement> movements = movementRepository.findAll();

        assertNotNull(movements);
        assertTrue(movements.size() >= 1);
    }

    // ==================== ТЕСТЫ findByProductId ====================

    @Test
    void testFindByProductId_ShouldReturnMovements() {
        List<Movement> movements = movementRepository.findByProductId(testProduct.getId());

        assertNotNull(movements);
        assertTrue(movements.size() >= 1);
        assertEquals(testProduct.getId(), movements.get(0).getProduct().getId());
    }

    @Test
    void testFindByProductId_NotFound_ShouldReturnEmptyList() {
        List<Movement> movements = movementRepository.findByProductId(999);

        assertNotNull(movements);
        assertTrue(movements.isEmpty());
    }

    // ==================== ТЕСТЫ findByFromLocationId ====================

    @Test
    void testFindByFromLocationId_ShouldReturnMovements() {
        List<Movement> movements = movementRepository.findByFromLocationId(fromLocation.getId());

        assertNotNull(movements);
        assertTrue(movements.size() >= 1);
        assertEquals(fromLocation.getId(), movements.get(0).getFromLocation().getId());
    }

    @Test
    void testFindByFromLocationId_NotFound_ShouldReturnEmptyList() {
        List<Movement> movements = movementRepository.findByFromLocationId(999);

        assertNotNull(movements);
        assertTrue(movements.isEmpty());
    }

    // ==================== ТЕСТЫ findByToLocationId ====================

    @Test
    void testFindByToLocationId_ShouldReturnMovements() {
        List<Movement> movements = movementRepository.findByToLocationId(toLocation.getId());

        assertNotNull(movements);
        assertTrue(movements.size() >= 1);
        assertEquals(toLocation.getId(), movements.get(0).getToLocation().getId());
    }

    @Test
    void testFindByToLocationId_NotFound_ShouldReturnEmptyList() {
        List<Movement> movements = movementRepository.findByToLocationId(999);

        assertNotNull(movements);
        assertTrue(movements.isEmpty());
    }

    // ==================== ТЕСТЫ findByMovementDateBetween ====================

    @Test
    void testFindByMovementDateBetween_ShouldReturnMovements() {
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        List<Movement> movements = movementRepository.findByMovementDateBetween(startDate, endDate);

        assertNotNull(movements);
        assertTrue(movements.size() >= 1);
    }

    @Test
    void testFindByMovementDateBetween_OutsideRange_ShouldReturnEmptyList() {
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(5);

        List<Movement> movements = movementRepository.findByMovementDateBetween(startDate, endDate);

        assertNotNull(movements);
        assertTrue(movements.isEmpty());
    }

    // ==================== ТЕСТЫ save ====================

    @Test
    void testSave_NewMovement_ShouldPersist() {
        Movement newMovement = new Movement();
        newMovement.setProduct(testProduct);
        newMovement.setFromLocation(fromLocation);
        newMovement.setToLocation(toLocation);
        newMovement.setQuantity(100);
        newMovement.setMovementDate(LocalDate.now());

        Movement saved = movementRepository.save(newMovement);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(100, saved.getQuantity());

        // Проверяем, что запись действительно сохранилась в БД
        Movement found = movementRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(100, found.getQuantity());
    }

    @Test
    void testSave_UpdateExistingMovement_ShouldMerge() {
        // Изменяем количество
        testMovement.setQuantity(75);

        Movement updated = movementRepository.save(testMovement);

        assertNotNull(updated);
        assertEquals(75, updated.getQuantity());

        // Проверяем, что изменения сохранились
        Movement found = movementRepository.findById(testMovement.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(75, found.getQuantity());
    }

    @Test
    void testSave_ReceiptMovement_ShouldWork() {
        // Движение с toLocation и null fromLocation (приёмка)
        Movement receipt = new Movement();
        receipt.setProduct(testProduct);
        receipt.setFromLocation(null);
        receipt.setToLocation(toLocation);
        receipt.setQuantity(200);
        receipt.setMovementDate(LocalDate.now());

        Movement saved = movementRepository.save(receipt);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertNull(saved.getFromLocation());
        assertNotNull(saved.getToLocation());
        assertEquals(200, saved.getQuantity());
    }

    @Test
    void testSave_ShipmentMovement_ShouldWork() {
        // Движение с fromLocation и null toLocation (отгрузка)
        Movement shipment = new Movement();
        shipment.setProduct(testProduct);
        shipment.setFromLocation(fromLocation);
        shipment.setToLocation(null);
        shipment.setQuantity(30);
        shipment.setMovementDate(LocalDate.now());

        Movement saved = movementRepository.save(shipment);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertNotNull(saved.getFromLocation());
        assertNull(saved.getToLocation());
        assertEquals(30, saved.getQuantity());
    }

    // ==================== ТЕСТЫ deleteById ====================

    @Test
    void testDeleteById_ShouldRemoveMovement() {
        // Сохраняем новое движение для удаления
        Movement toDelete = new Movement();
        toDelete.setProduct(testProduct);
        toDelete.setFromLocation(fromLocation);
        toDelete.setToLocation(toLocation);
        toDelete.setQuantity(10);
        toDelete.setMovementDate(LocalDate.now());
        Movement saved = movementRepository.save(toDelete);

        assertNotNull(movementRepository.findById(saved.getId()).orElse(null));

        movementRepository.deleteById(saved.getId());
        entityManager.flush();

        Movement deleted = movementRepository.findById(saved.getId()).orElse(null);
        assertNull(deleted);
    }

    @Test
    void testDeleteById_NotFound_ShouldNotThrowException() {
        // Проверяем, что метод не выбрасывает исключение при удалении несуществующей записи
        assertDoesNotThrow(() -> movementRepository.deleteById(999));
    }

    // ==================== ТЕСТЫ findById после удаления ====================

    @Test
    void testFindById_AfterDelete_ShouldReturnEmpty() {
        Integer idToDelete = testMovement.getId();

        movementRepository.deleteById(idToDelete);
        entityManager.flush();

        Movement found = movementRepository.findById(idToDelete).orElse(null);
        assertNull(found);
    }
}