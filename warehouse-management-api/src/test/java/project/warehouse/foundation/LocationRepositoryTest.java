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
import project.warehouse.foundation.interfaces.ILocationRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class LocationRepositoryTest {

    @Autowired
    private ILocationRepository locationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Warehouse testWarehouse;
    private Type testType;
    private Temperature testTemperature;
    private Location testLocation;

    @BeforeEach
    void setUp() {
        // Создаём склад
        testWarehouse = new Warehouse();
        testWarehouse.setName("Основной склад");
        testWarehouse.setAddress("г. Москва, ул. Ленина, 1");
        entityManager.persist(testWarehouse);

        // Создаём тип локации
        testType = new Type();
        testType.setTitle("Стеллаж");
        entityManager.persist(testType);

        // Создаём температурный режим
        testTemperature = new Temperature();
        testTemperature.setTitle("Комнатная");
        entityManager.persist(testTemperature);

        // Создаём тестовую локацию
        testLocation = new Location();
        testLocation.setName("А-1");
        testLocation.setWarehouse(testWarehouse);
        testLocation.setType(testType);
        testLocation.setTemperature(testTemperature);
        entityManager.persist(testLocation);

        entityManager.flush();
    }

    // ==================== ТЕСТЫ findById ====================

    @Test
    void testFindById_ShouldReturnLocation() {
        Location found = locationRepository.findById(testLocation.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(testLocation.getId(), found.getId());
        assertEquals("А-1", found.getName());
        assertEquals(testWarehouse.getId(), found.getWarehouse().getId());
        assertEquals(testType.getId(), found.getType().getId());
        assertEquals(testTemperature.getId(), found.getTemperature().getId());
    }

    @Test
    void testFindById_WithNullId_ShouldReturnEmpty() {
        Optional<Location> found = locationRepository.findById(null);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindById_NotFound_ShouldReturnEmpty() {
        Location found = locationRepository.findById(999).orElse(null);

        assertNull(found);
    }

    // ==================== ТЕСТЫ findAll ====================

    @Test
    void testFindAll_ShouldReturnList() {
        List<Location> locations = locationRepository.findAll();

        assertNotNull(locations);
        assertTrue(locations.size() >= 1);

        // Проверяем, что наш тестовый объект присутствует в списке
        boolean found = locations.stream().anyMatch(l -> l.getId().equals(testLocation.getId()));
        assertTrue(found);
    }

    @Test
    void testFindAll_AfterSave_ShouldIncludeNewLocation() {
        // Создаём новую локацию
        Location newLocation = new Location();
        newLocation.setName("Б-2");
        newLocation.setWarehouse(testWarehouse);
        newLocation.setType(testType);
        newLocation.setTemperature(testTemperature);
        locationRepository.save(newLocation);
        entityManager.flush();

        List<Location> locations = locationRepository.findAll();

        assertNotNull(locations);
        boolean found = locations.stream().anyMatch(l -> "Б-2".equals(l.getName()));
        assertTrue(found);
    }

    // ==================== ТЕСТЫ save ====================

    @Test
    void testSave_NewLocation_ShouldPersist() {
        Location newLocation = new Location();
        newLocation.setName("В-3");
        newLocation.setWarehouse(testWarehouse);
        newLocation.setType(testType);
        newLocation.setTemperature(testTemperature);

        Location saved = locationRepository.save(newLocation);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("В-3", saved.getName());
        assertEquals(testWarehouse.getId(), saved.getWarehouse().getId());
        assertEquals(testType.getId(), saved.getType().getId());
        assertEquals(testTemperature.getId(), saved.getTemperature().getId());

        // Проверяем, что запись действительно сохранилась в БД
        Location found = locationRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("В-3", found.getName());
    }

    @Test
    void testSave_NewLocationWithoutTemperature_ShouldPersist() {
        Location newLocation = new Location();
        newLocation.setName("Г-4");
        newLocation.setWarehouse(testWarehouse);
        newLocation.setType(testType);
        newLocation.setTemperature(null);  // Без температурного режима

        Location saved = locationRepository.save(newLocation);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Г-4", saved.getName());
        assertNull(saved.getTemperature());

        // Проверяем, что запись действительно сохранилась в БД
        Location found = locationRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertNull(found.getTemperature());
    }

    @Test
    void testSave_UpdateExistingLocation_ShouldMerge() {
        // Изменяем название и температуру
        testLocation.setName("А-1 (обновлённая)");
        Temperature newTemperature = new Temperature();
        newTemperature.setTitle("Холодильная");
        entityManager.persist(newTemperature);
        testLocation.setTemperature(newTemperature);

        Location updated = locationRepository.save(testLocation);

        assertNotNull(updated);
        assertEquals("А-1 (обновлённая)", updated.getName());
        assertEquals(newTemperature.getId(), updated.getTemperature().getId());

        // Проверяем, что изменения сохранились
        Location found = locationRepository.findById(testLocation.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("А-1 (обновлённая)", found.getName());
        assertEquals(newTemperature.getId(), found.getTemperature().getId());
    }

    @Test
    void testSave_UpdateLocationWarehouse_ShouldMerge() {
        // Создаём новый склад
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setName("Новый склад");
        newWarehouse.setAddress("г. Москва, ул. Пушкина, 2");
        entityManager.persist(newWarehouse);

        // Меняем склад у локации
        testLocation.setWarehouse(newWarehouse);

        Location updated = locationRepository.save(testLocation);

        assertNotNull(updated);
        assertEquals(newWarehouse.getId(), updated.getWarehouse().getId());

        // Проверяем, что изменения сохранились
        Location found = locationRepository.findById(testLocation.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(newWarehouse.getId(), found.getWarehouse().getId());
    }

    @Test
    void testSave_UpdateLocationType_ShouldMerge() {
        // Создаём новый тип
        Type newType = new Type();
        newType.setTitle("Паллета");
        entityManager.persist(newType);

        // Меняем тип у локации
        testLocation.setType(newType);

        Location updated = locationRepository.save(testLocation);

        assertNotNull(updated);
        assertEquals(newType.getId(), updated.getType().getId());

        // Проверяем, что изменения сохранились
        Location found = locationRepository.findById(testLocation.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(newType.getId(), found.getType().getId());
    }

    // ==================== ТЕСТЫ findById после save ====================

    @Test
    void testFindById_AfterSave_ShouldReturnCorrectLocation() {
        Location newLocation = new Location();
        newLocation.setName("Д-5");
        newLocation.setWarehouse(testWarehouse);
        newLocation.setType(testType);
        newLocation.setTemperature(testTemperature);

        Location saved = locationRepository.save(newLocation);

        Location found = locationRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Д-5", found.getName());
    }

    // ==================== ТЕСТЫ findAll с несколькими записями ====================

    @Test
    void testFindAll_WithMultipleLocations_ShouldReturnAll() {
        // Создаём несколько дополнительных локаций
        Location location2 = new Location();
        location2.setName("Б-2");
        location2.setWarehouse(testWarehouse);
        location2.setType(testType);
        location2.setTemperature(testTemperature);
        locationRepository.save(location2);

        Location location3 = new Location();
        location3.setName("В-3");
        location3.setWarehouse(testWarehouse);
        location3.setType(testType);
        location3.setTemperature(null);
        locationRepository.save(location3);

        List<Location> locations = locationRepository.findAll();

        assertNotNull(locations);
        assertTrue(locations.size() >= 3);

        // Проверяем, что все созданные локации присутствуют
        boolean hasLocation2 = locations.stream().anyMatch(l -> "Б-2".equals(l.getName()));
        boolean hasLocation3 = locations.stream().anyMatch(l -> "В-3".equals(l.getName()));

        assertTrue(hasLocation2);
        assertTrue(hasLocation3);
    }

    // ==================== ТЕСТЫ граничных случаев ====================

    @Test
    void testSave_LocationWithNullName_ShouldThrowException() {
        Location invalidLocation = new Location();
        invalidLocation.setName(null);
        invalidLocation.setWarehouse(testWarehouse);
        invalidLocation.setType(testType);

        assertThrows(Exception.class, () -> {
            locationRepository.save(invalidLocation);
            entityManager.flush();
        });
    }

    @Test
    void testSave_LocationWithNullWarehouse_ShouldThrowException() {
        Location invalidLocation = new Location();
        invalidLocation.setName("Без склада");
        invalidLocation.setWarehouse(null);
        invalidLocation.setType(testType);

        assertThrows(Exception.class, () -> {
            locationRepository.save(invalidLocation);
            entityManager.flush();
        });
    }

    @Test
    void testSave_LocationWithNullType_ShouldThrowException() {
        Location invalidLocation = new Location();
        invalidLocation.setName("Без типа");
        invalidLocation.setWarehouse(testWarehouse);
        invalidLocation.setType(null);

        assertThrows(Exception.class, () -> {
            locationRepository.save(invalidLocation);
            entityManager.flush();
        });
    }

    // ==================== ТЕСТЫ findById с разными ID ====================

    @Test
    void testFindById_WithNegativeId_ShouldReturnEmpty() {
        Location found = locationRepository.findById(-1).orElse(null);

        assertNull(found);
    }

    @Test
    void testFindById_WithZeroId_ShouldReturnEmpty() {
        Location found = locationRepository.findById(0).orElse(null);

        assertNull(found);
    }
}