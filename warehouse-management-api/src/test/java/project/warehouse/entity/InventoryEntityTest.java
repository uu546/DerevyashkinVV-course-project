package project.warehouse.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InventoryEntityTest {

    private Inventory inventory;
    private Product product;
    private Location location;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Кирпич красный");

        location = new Location();
        location.setId(1);
        location.setName("Стеллаж А-1");

        inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setLocation(location);
        inventory.setQuantity(100);
    }

    @Test
    void testAddQuantity() {
        inventory.addQuantity(50);
        assertEquals(150, inventory.getQuantity());
    }

    @Test
    void testAddQuantityNegative() {
        inventory.addQuantity(-10);
        assertEquals(100, inventory.getQuantity());
    }

    @Test
    void testSubtractQuantity() {
        inventory.subtractQuantity(30);
        assertEquals(70, inventory.getQuantity());
    }

    @Test
    void testSubtractQuantity_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            inventory.subtractQuantity(150);
        });
    }

    @Test
    void testIsAvailableQuantity_True() {
        assertTrue(inventory.isAvailableQuantity(50));
    }

    @Test
    void testIsAvailableQuantity_False() {
        assertFalse(inventory.isAvailableQuantity(150));
    }

    @Test
    void testDefaultQuantity() {
        Inventory newInventory = new Inventory();
        assertEquals(0, newInventory.getQuantity());
    }

    @Test
    void testParameterizedConstructor() {
        Inventory inv = new Inventory(product, location, 50);
        assertEquals(product, inv.getProduct());
        assertEquals(location, inv.getLocation());
        assertEquals(50, inv.getQuantity());
    }
}