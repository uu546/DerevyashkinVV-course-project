package project.warehouse.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LocationEntityTest {

    private Location location;
    private Warehouse warehouse;
    private Type type;
    private Temperature temperature;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId(1);
        warehouse.setName("Основной склад");

        type = new Type();
        type.setId(1);
        type.setTitle("Стеллаж");

        temperature = new Temperature();
        temperature.setId(1);
        temperature.setTitle("Комнатная");

        location = new Location();
        location.setId(1);
        location.setName("А-1");
        location.setWarehouse(warehouse);
        location.setType(type);
        location.setTemperature(temperature);
    }

    @Test
    void testConstructorAndGetters() {
        assertNotNull(location);
        assertEquals(1, location.getId());
        assertEquals("А-1", location.getName());
        assertEquals(warehouse, location.getWarehouse());
        assertEquals(type, location.getType());
        assertEquals(temperature, location.getTemperature());
    }

    @Test
    void testSetters() {
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setId(2);
        Type newType = new Type();
        newType.setId(2);

        location.setName("Б-2");
        location.setWarehouse(newWarehouse);
        location.setType(newType);
        location.setTemperature(null);

        assertEquals("Б-2", location.getName());
        assertEquals(newWarehouse, location.getWarehouse());
        assertEquals(newType, location.getType());
        assertNull(location.getTemperature());
    }

    @Test
    void testGetFullName_WithWarehouse() {
        assertEquals("Основной склад - А-1", location.getFullName());
    }

    @Test
    void testParameterizedConstructor() {
        Location loc = new Location("Б-3", warehouse, type, temperature);
        assertEquals("Б-3", loc.getName());
        assertEquals(warehouse, loc.getWarehouse());
        assertEquals(type, loc.getType());
        assertEquals(temperature, loc.getTemperature());
    }
}