package project.warehouse.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductEntityTest {

    private Product product;
    private Category category;
    private Unit unit;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1);
        category.setTitle("Стройматериалы");

        unit = new Unit();
        unit.setId(1);
        unit.setTitle("шт");

        product = new Product();
        product.setId(1);
        product.setName("Кирпич красный");
        product.setCategory(category);
        product.setUnit(unit);
        product.setIsPerishable(false);
    }

    @Test
    void testIsPerishable_False() {
        assertFalse(product.isPerishable());
    }

    @Test
    void testIsPerishable_True() {
        product.setIsPerishable(true);
        assertTrue(product.isPerishable());
    }

    @Test
    void testParameterizedConstructor() {
        Product prod = new Product("Тест", "Описание", true, 10, category, unit);
        assertEquals("Тест", prod.getName());
        assertEquals("Описание", prod.getDescription());
        assertTrue(prod.getIsPerishable());
        assertEquals(10, prod.getExpiryDays());
        assertEquals(category, prod.getCategory());
        assertEquals(unit, prod.getUnit());
    }
}