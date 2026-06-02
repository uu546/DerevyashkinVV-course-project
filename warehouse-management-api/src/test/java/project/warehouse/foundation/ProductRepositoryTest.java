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
import project.warehouse.foundation.interfaces.IProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ProductRepositoryTest {

    @Autowired
    private IProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Category testCategory;
    private Unit testUnit;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Создаём категорию
        testCategory = new Category();
        testCategory.setTitle("Стройматериалы");
        entityManager.persist(testCategory);

        // Создаём единицу измерения
        testUnit = new Unit();
        testUnit.setTitle("шт");
        entityManager.persist(testUnit);

        // Создаём тестовый товар
        testProduct = new Product();
        testProduct.setName("Кирпич красный");
        testProduct.setDescription("Керамический, полнотелый");
        testProduct.setCategory(testCategory);
        testProduct.setUnit(testUnit);
        testProduct.setIsPerishable(false);
        testProduct.setExpiryDays(null);
        entityManager.persist(testProduct);

        entityManager.flush();
        entityManager.clear(); // Очищаем кэш после сохранения
    }

    // ==================== ТЕСТЫ findById ====================

    @Test
    void testFindById_ShouldReturnProduct() {
        Product found = productRepository.findById(testProduct.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(testProduct.getId(), found.getId());
        assertEquals("Кирпич красный", found.getName());
        assertEquals(testCategory.getId(), found.getCategory().getId());
        assertEquals(testUnit.getId(), found.getUnit().getId());
        assertFalse(found.getIsPerishable());
        assertNull(found.getExpiryDays());
    }

    @Test
    void testFindById_NotFound_ShouldReturnEmpty() {
        Product found = productRepository.findById(999).orElse(null);

        assertNull(found);
    }

    @Test
    void testFindById_WithNullId_ShouldReturnEmpty() {
        try {
            Optional<Product> result = productRepository.findById(null);
            assertFalse(result.isPresent());
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("id") || e.getMessage().contains("null"));
        }
    }

    // ==================== ТЕСТЫ findAll ====================

    @Test
    void testFindAll_ShouldReturnList() {
        List<Product> products = productRepository.findAll();

        assertNotNull(products);
        assertTrue(products.size() >= 1);

        boolean found = products.stream().anyMatch(p -> p.getId().equals(testProduct.getId()));
        assertTrue(found);
    }

    @Test
    void testFindAll_AfterSave_ShouldIncludeNewProduct() {
        Product newProduct = new Product();
        newProduct.setName("Цемент М500");
        newProduct.setDescription("Мешок 50 кг");
        newProduct.setCategory(testCategory);
        newProduct.setUnit(testUnit);
        newProduct.setIsPerishable(false);
        newProduct.setExpiryDays(365);
        productRepository.save(newProduct);
        entityManager.flush();

        List<Product> products = productRepository.findAll();

        assertNotNull(products);
        boolean found = products.stream().anyMatch(p -> "Цемент М500".equals(p.getName()));
        assertTrue(found);
    }

    @Test
    void testFindAll_WithMultipleProducts_ShouldReturnAll() {
        Product product2 = new Product();
        product2.setName("Ноутбук Lenovo");
        product2.setDescription("15.6\", 16GB RAM");
        product2.setCategory(testCategory);
        product2.setUnit(testUnit);
        product2.setIsPerishable(false);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Бумага А4");
        product3.setDescription("500 листов, 80 г/м²");
        product3.setCategory(testCategory);
        product3.setUnit(testUnit);
        product3.setIsPerishable(false);
        productRepository.save(product3);

        List<Product> products = productRepository.findAll();

        assertNotNull(products);
        assertTrue(products.size() >= 3);

        boolean hasProduct2 = products.stream().anyMatch(p -> "Ноутбук Lenovo".equals(p.getName()));
        boolean hasProduct3 = products.stream().anyMatch(p -> "Бумага А4".equals(p.getName()));

        assertTrue(hasProduct2);
        assertTrue(hasProduct3);
    }

    // ==================== ТЕСТЫ save ====================

    @Test
    void testSave_NewProduct_ShouldPersist() {
        Product newProduct = new Product();
        newProduct.setName("Песок строительный");
        newProduct.setDescription("Мытый, фракция 0-5 мм");
        newProduct.setCategory(testCategory);
        newProduct.setUnit(testUnit);
        newProduct.setIsPerishable(false);
        newProduct.setExpiryDays(null);

        Product saved = productRepository.save(newProduct);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Песок строительный", saved.getName());
        assertEquals(testCategory.getId(), saved.getCategory().getId());
        assertEquals(testUnit.getId(), saved.getUnit().getId());

        Product found = productRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Песок строительный", found.getName());
    }

    @Test
    void testSave_NewProductWithExpiryDays_ShouldPersist() {
        Product perishableProduct = new Product();
        perishableProduct.setName("Молоко");
        perishableProduct.setDescription("Пастеризованное, 3.2%");
        perishableProduct.setCategory(testCategory);
        perishableProduct.setUnit(testUnit);
        perishableProduct.setIsPerishable(true);
        perishableProduct.setExpiryDays(7);

        Product saved = productRepository.save(perishableProduct);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertTrue(saved.getIsPerishable());
        assertEquals(7, saved.getExpiryDays());

        Product found = productRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertTrue(found.getIsPerishable());
        assertEquals(7, found.getExpiryDays());
    }

    @Test
    void testSave_NewProductWithDescription_ShouldPersist() {
        Product productWithDesc = new Product();
        productWithDesc.setName("Доска обрезная");
        productWithDesc.setDescription("Сосна, 50x150x6000 мм, сорт 1");
        productWithDesc.setCategory(testCategory);
        productWithDesc.setUnit(testUnit);
        productWithDesc.setIsPerishable(false);

        Product saved = productRepository.save(productWithDesc);

        assertNotNull(saved);
        assertEquals("Доска обрезная", saved.getName());
        assertEquals("Сосна, 50x150x6000 мм, сорт 1", saved.getDescription());

        Product found = productRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Сосна, 50x150x6000 мм, сорт 1", found.getDescription());
    }

    @Test
    void testSave_UpdateExistingProduct_ShouldMerge() {
        testProduct.setName("Кирпич керамический");
        testProduct.setDescription("Пустотелый");
        testProduct.setExpiryDays(null);

        Product updated = productRepository.save(testProduct);

        assertNotNull(updated);
        assertEquals("Кирпич керамический", updated.getName());
        assertEquals("Пустотелый", updated.getDescription());

        Product found = productRepository.findById(testProduct.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Кирпич керамический", found.getName());
        assertEquals("Пустотелый", found.getDescription());
    }

    @Test
    void testSave_UpdateProductCategory_ShouldMerge() {
        Category newCategory = new Category();
        newCategory.setTitle("Отделочные материалы");
        entityManager.persist(newCategory);

        testProduct.setCategory(newCategory);

        Product updated = productRepository.save(testProduct);

        assertNotNull(updated);
        assertEquals(newCategory.getId(), updated.getCategory().getId());

        Product found = productRepository.findById(testProduct.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(newCategory.getId(), found.getCategory().getId());
    }

    @Test
    void testSave_UpdateProductUnit_ShouldMerge() {
        Unit newUnit = new Unit();
        newUnit.setTitle("кг");
        entityManager.persist(newUnit);

        testProduct.setUnit(newUnit);

        Product updated = productRepository.save(testProduct);

        assertNotNull(updated);
        assertEquals(newUnit.getId(), updated.getUnit().getId());

        Product found = productRepository.findById(testProduct.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(newUnit.getId(), found.getUnit().getId());
    }

    @Test
    void testSave_UpdateProductToPerishable_ShouldMerge() {
        testProduct.setIsPerishable(true);
        testProduct.setExpiryDays(30);

        Product updated = productRepository.save(testProduct);

        assertNotNull(updated);
        assertTrue(updated.getIsPerishable());
        assertEquals(30, updated.getExpiryDays());

        Product found = productRepository.findById(testProduct.getId()).orElse(null);
        assertNotNull(found);
        assertTrue(found.getIsPerishable());
        assertEquals(30, found.getExpiryDays());
    }

    // ==================== ТЕСТЫ deleteById ====================

    @Test
    void testDeleteById_ShouldRemoveProduct() {
        Product toDelete = new Product();
        toDelete.setName("Товар на удаление");
        toDelete.setCategory(testCategory);
        toDelete.setUnit(testUnit);
        Product saved = productRepository.save(toDelete);

        assertNotNull(productRepository.findById(saved.getId()).orElse(null));

        productRepository.deleteById(saved.getId());
        entityManager.flush();

        Product deleted = productRepository.findById(saved.getId()).orElse(null);
        assertNull(deleted);
    }

    @Test
    void testDeleteById_NotFound_ShouldNotThrowException() {
        assertDoesNotThrow(() -> productRepository.deleteById(999));
    }

    @Test
    void testDeleteById_WithInvalidId_ShouldNotThrowException() {
        // Передаём заведомо несуществующий ID (не null, чтобы избежать исключения)
        assertDoesNotThrow(() -> productRepository.deleteById(-1));
        assertDoesNotThrow(() -> productRepository.deleteById(0));
    }

    @Test
    void testFindById_AfterDelete_ShouldReturnEmpty() {
        Integer idToDelete = testProduct.getId();

        productRepository.deleteById(idToDelete);
        entityManager.flush();

        Product found = productRepository.findById(idToDelete).orElse(null);
        assertNull(found);
    }

    // ==================== ТЕСТЫ findById после save ====================

    @Test
    void testFindById_AfterSave_ShouldReturnCorrectProduct() {
        Product newProduct = new Product();
        newProduct.setName("Новый товар");
        newProduct.setCategory(testCategory);
        newProduct.setUnit(testUnit);

        Product saved = productRepository.save(newProduct);

        Product found = productRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Новый товар", found.getName());
    }

    // ==================== ТЕСТЫ save с повторным вызовом ====================

    @Test
    void testSave_Twice_ShouldWorkCorrectly() {
        Product product = new Product();
        product.setName("Тестовый товар");
        product.setCategory(testCategory);
        product.setUnit(testUnit);

        Product saved1 = productRepository.save(product);
        assertNotNull(saved1.getId());

        saved1.setName("Изменённое название");
        Product saved2 = productRepository.save(saved1);

        assertEquals(saved1.getId(), saved2.getId());
        assertEquals("Изменённое название", saved2.getName());

        Product found = productRepository.findById(saved1.getId()).orElse(null);
        assertNotNull(found);
        assertEquals("Изменённое название", found.getName());
    }

    // ==================== ТЕСТЫ граничных случаев ====================

    @Test
    void testSave_ProductWithNullName_ShouldThrowException() {
        Product invalidProduct = new Product();
        invalidProduct.setName(null);
        invalidProduct.setCategory(testCategory);
        invalidProduct.setUnit(testUnit);

        assertThrows(Exception.class, () -> {
            productRepository.save(invalidProduct);
            entityManager.flush();
        });
    }

    @Test
    void testSave_ProductWithNullCategory_ShouldThrowException() {
        Product invalidProduct = new Product();
        invalidProduct.setName("Товар без категории");
        invalidProduct.setCategory(null);
        invalidProduct.setUnit(testUnit);

        assertThrows(Exception.class, () -> {
            productRepository.save(invalidProduct);
            entityManager.flush();
        });
    }

    @Test
    void testSave_ProductWithNullUnit_ShouldThrowException() {
        Product invalidProduct = new Product();
        invalidProduct.setName("Товар без единицы измерения");
        invalidProduct.setCategory(testCategory);
        invalidProduct.setUnit(null);

        assertThrows(Exception.class, () -> {
            productRepository.save(invalidProduct);
            entityManager.flush();
        });
    }

    // ==================== ТЕСТЫ findById с разными ID ====================

    @Test
    void testFindById_WithNegativeId_ShouldReturnEmpty() {
        Product found = productRepository.findById(-1).orElse(null);

        assertNull(found);
    }

    @Test
    void testFindById_WithZeroId_ShouldReturnEmpty() {
        Product found = productRepository.findById(0).orElse(null);

        assertNull(found);
    }
}