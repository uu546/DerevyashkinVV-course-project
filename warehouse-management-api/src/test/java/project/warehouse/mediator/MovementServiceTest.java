package project.warehouse.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.warehouse.entity.*;
import project.warehouse.foundation.interfaces.*;
import project.warehouse.mediator.services.MovementService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private IMovementRepository movementRepository;

    @Mock
    private IProductRepository productRepository;

    @Mock
    private ILocationRepository locationRepository;

    @Mock
    private IInventoryRepository inventoryRepository;

    @InjectMocks
    private MovementService movementService;

    private Product testProduct;
    private Location testLocation;
    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Кирпич красный");

        testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Стеллаж А-1");

        testInventory = new Inventory();
        testInventory.setId(1);
        testInventory.setProduct(testProduct);
        testInventory.setLocation(testLocation);
        testInventory.setQuantity(100);
    }

    @Test
    void testCreateReceipt_Success() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(locationRepository.findById(1)).thenReturn(Optional.of(testLocation));

        Movement mockMovement = new Movement();
        mockMovement.setId(1);
        mockMovement.setProduct(testProduct);
        mockMovement.setToLocation(testLocation);
        mockMovement.setQuantity(50);

        when(movementRepository.save(any(Movement.class))).thenReturn(mockMovement);

        Movement result = movementService.createReceipt(1, 1, 50);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(50, result.getQuantity());

        verify(productRepository).findById(1);
        verify(locationRepository).findById(1);
        verify(inventoryRepository).addQuantity(1, 1, 50);
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    void testCreateReceipt_ProductNotFound_ThrowsException() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            movementService.createReceipt(999, 1, 50);
        });

        verify(inventoryRepository, never()).addQuantity(any(), any(), any());
        verify(movementRepository, never()).save(any(Movement.class));
    }

    @Test
    void testCreateShipment_Success() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(locationRepository.findById(1)).thenReturn(Optional.of(testLocation));
        when(inventoryRepository.findByProductAndLocation(testProduct, testLocation))
                .thenReturn(Optional.of(testInventory));

        Movement mockMovement = new Movement();
        mockMovement.setId(1);
        when(movementRepository.save(any(Movement.class))).thenReturn(mockMovement);

        Movement result = movementService.createShipment(1, 1, 30);

        assertNotNull(result);

        verify(productRepository, times(2)).findById(1);
        verify(locationRepository, times(2)).findById(1);
        verify(inventoryRepository).findByProductAndLocation(testProduct, testLocation);
        verify(inventoryRepository).subtractQuantity(1, 1, 30);
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    void testCreateShipment_NoInventoryRecord_ThrowsException() {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(locationRepository.findById(1)).thenReturn(Optional.of(testLocation));
        when(inventoryRepository.findByProductAndLocation(testProduct, testLocation))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            movementService.createShipment(1, 1, 30);
        });

        verify(inventoryRepository, never()).subtractQuantity(any(), any(), any());
        verify(movementRepository, never()).save(any(Movement.class));
    }

    @Test
    void testCreateShipment_InsufficientStock_ThrowsException() {
        testInventory.setQuantity(10);

        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(locationRepository.findById(1)).thenReturn(Optional.of(testLocation));
        when(inventoryRepository.findByProductAndLocation(testProduct, testLocation))
                .thenReturn(Optional.of(testInventory));

        assertThrows(RuntimeException.class, () -> {
            movementService.createShipment(1, 1, 100);
        });

        verify(inventoryRepository, never()).subtractQuantity(any(), any(), any());
        verify(movementRepository, never()).save(any(Movement.class));
    }

    @Test
    void testMoveProduct_Success() {
        Location toLocation = new Location();
        toLocation.setId(2);
        toLocation.setName("Паллета Б-2");

        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(locationRepository.findById(1)).thenReturn(Optional.of(testLocation));
        when(locationRepository.findById(2)).thenReturn(Optional.of(toLocation));
        when(inventoryRepository.findByProductAndLocation(testProduct, testLocation))
                .thenReturn(Optional.of(testInventory));

        Movement mockMovement = new Movement();
        mockMovement.setId(1);
        when(movementRepository.save(any(Movement.class))).thenReturn(mockMovement);

        Movement result = movementService.moveProduct(1, 1, 2, 30);

        assertNotNull(result);

        verify(inventoryRepository).subtractQuantity(1, 1, 30);
        verify(inventoryRepository).addQuantity(1, 2, 30);
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    void testMoveProduct_InsufficientStock_ThrowsException() {
        Location toLocation = new Location();
        toLocation.setId(2);

        testInventory.setQuantity(10);

        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(locationRepository.findById(1)).thenReturn(Optional.of(testLocation));
        when(locationRepository.findById(2)).thenReturn(Optional.of(toLocation));
        when(inventoryRepository.findByProductAndLocation(testProduct, testLocation))
                .thenReturn(Optional.of(testInventory));

        assertThrows(RuntimeException.class, () -> {
            movementService.moveProduct(1, 1, 2, 100);
        });

        verify(inventoryRepository, never()).subtractQuantity(any(), any(), any());
        verify(inventoryRepository, never()).addQuantity(any(), any(), any());
        verify(movementRepository, never()).save(any(Movement.class));
    }


}