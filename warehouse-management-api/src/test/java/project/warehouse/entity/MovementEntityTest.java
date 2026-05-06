package project.warehouse.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MovementEntityTest {

    private Movement movement;
    private Product product;
    private Location fromLocation;
    private Location toLocation;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Кирпич");

        fromLocation = new Location();
        fromLocation.setId(1);
        fromLocation.setName("Стеллаж А-1");

        toLocation = new Location();
        toLocation.setId(2);
        toLocation.setName("Стеллаж А-2");

        movement = new Movement();
        movement.setId(1);
        movement.setProduct(product);
        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        movement.setQuantity(50);
        movement.setMovementDate(LocalDate.now());
    }

    @Test
    void testConstructorAndGetters() {
        assertNotNull(movement);
        assertEquals(1, movement.getId());
        assertEquals(product, movement.getProduct());
        assertEquals(fromLocation, movement.getFromLocation());
        assertEquals(toLocation, movement.getToLocation());
        assertEquals(50, movement.getQuantity());
        assertNotNull(movement.getMovementDate());
    }

    @Test
    void testSetters() {
        Product newProduct = new Product();
        newProduct.setId(2);

        movement.setProduct(newProduct);
        movement.setQuantity(100);

        assertEquals(newProduct, movement.getProduct());
        assertEquals(100, movement.getQuantity());
    }

    @Test
    void testIsReceipt() {
        movement.setFromLocation(null);
        movement.setToLocation(toLocation);
        assertTrue(movement.isReceipt());

        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        assertFalse(movement.isReceipt());
    }

    @Test
    void testIsShipment() {
        movement.setFromLocation(fromLocation);
        movement.setToLocation(null);
        assertTrue(movement.isShipment());

        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        assertFalse(movement.isShipment());
    }

    @Test
    void testIsTransfer() {
        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        assertTrue(movement.isTransfer());

        movement.setFromLocation(null);
        movement.setToLocation(toLocation);
        assertFalse(movement.isTransfer());

        movement.setFromLocation(fromLocation);
        movement.setToLocation(null);
        assertFalse(movement.isTransfer());
    }

    @Test
    void testParameterizedConstructor() {
        Movement mov = new Movement(product, fromLocation, toLocation, 30);
        assertEquals(product, mov.getProduct());
        assertEquals(fromLocation, mov.getFromLocation());
        assertEquals(toLocation, mov.getToLocation());
        assertEquals(30, mov.getQuantity());
    }
}