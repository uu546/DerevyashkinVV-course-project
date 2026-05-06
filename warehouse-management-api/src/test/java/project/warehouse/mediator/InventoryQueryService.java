package project.warehouse.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.warehouse.control.dto.*;
import project.warehouse.entity.*;
import project.warehouse.foundation.interfaces.IInventoryRepository;
import project.warehouse.mediator.services.InventoryQueryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryQueryServiceTest {

    @Mock
    private IInventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryQueryService inventoryQueryService;

    private Product product1;
    private Product product2;
    private Location location1;
    private Location location2;
    private Warehouse warehouse;
    private Category category;
    private Unit unit;
    private Type type;

    @BeforeEach
    void setUp() {
        // Category
        category = new Category();
        category.setId(1);
        category.setTitle("Стройматериалы");

        // Unit
        unit = new Unit();
        unit.setId(1);
        unit.setTitle("шт");

        // Type
        type = new Type();
        type.setId(1);
        type.setTitle("Стеллаж");

        // Warehouse
        warehouse = new Warehouse();
        warehouse.setId(1);
        warehouse.setName("Основной склад");
        warehouse.setAddress("г. Москва, ул. Ленина, 1");

        // Products
        product1 = new Product();
        product1.setId(1);
        product1.setName("Кирпич красный");
        product1.setCategory(category);
        product1.setUnit(unit);
        product1.setIsPerishable(false);

        product2 = new Product();
        product2.setId(2);
        product2.setName("Цемент М500");
        product2.setCategory(category);
        product2.setUnit(unit);
        product2.setIsPerishable(false);

        // Locations
        location1 = new Location();
        location1.setId(1);
        location1.setName("Стеллаж А-1");
        location1.setWarehouse(warehouse);
        location1.setType(type);

        location2 = new Location();
        location2.setId(2);
        location2.setName("Паллета Б-2");
        location2.setWarehouse(warehouse);
        location2.setType(type);
    }

    @Test
    void testGetFullInventorySummary_WithData() {
        // Создаём тестовые Inventory
        Inventory inv1 = new Inventory();
        inv1.setProduct(product1);
        inv1.setLocation(location1);
        inv1.setQuantity(100);

        Inventory inv2 = new Inventory();
        inv2.setProduct(product2);
        inv2.setLocation(location1);
        inv2.setQuantity(50);

        Inventory inv3 = new Inventory();
        inv3.setProduct(product1);
        inv3.setLocation(location2);
        inv3.setQuantity(200);

        List<Inventory> inventories = Arrays.asList(inv1, inv2, inv3);

        when(inventoryRepository.findAllWithDetails()).thenReturn(inventories);

        InventorySummaryResponse response = inventoryQueryService.getFullInventorySummary();

        assertNotNull(response);
        assertNotNull(response.getWarehouses());
        assertNotNull(response.getTotalSummary());

        // Проверяем склады
        assertEquals(1, response.getWarehouses().size());
        WarehouseInventoryDto warehouseDto = response.getWarehouses().get(0);
        assertEquals(1, warehouseDto.getWarehouseId());
        assertEquals("Основной склад", warehouseDto.getWarehouseName());

        // Проверяем локации
        assertEquals(2, warehouseDto.getLocations().size());

        // Проверяем общую сводку
        TotalSummaryDto summary = response.getTotalSummary();
        assertEquals(1, summary.getTotalWarehouses());
        assertEquals(2, summary.getTotalLocations());
        assertEquals(3, summary.getTotalProducts());
        assertEquals(350, summary.getTotalItems());
    }

    @Test
    void testGetFullInventorySummary_EmptyData() {
        when(inventoryRepository.findAllWithDetails()).thenReturn(new ArrayList<>());

        InventorySummaryResponse response = inventoryQueryService.getFullInventorySummary();

        assertNotNull(response);
        assertTrue(response.getWarehouses().isEmpty());

        TotalSummaryDto summary = response.getTotalSummary();
        assertEquals(0, summary.getTotalWarehouses());
        assertEquals(0, summary.getTotalLocations());
        assertEquals(0, summary.getTotalProducts());
        assertEquals(0, summary.getTotalItems());
    }

    @Test
    void testGetFullInventorySummary_NullData() {
        when(inventoryRepository.findAllWithDetails()).thenReturn(null);

        InventorySummaryResponse response = inventoryQueryService.getFullInventorySummary();

        assertNotNull(response);
        assertNotNull(response.getWarehouses());
        assertTrue(response.getWarehouses().isEmpty());
    }
}