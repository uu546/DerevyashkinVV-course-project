package project.warehouse.mediator.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.warehouse.control.dto.ReceiptBatchRequest;
import project.warehouse.control.dto.ReceiptItem;
import project.warehouse.control.dto.ShipmentBatchRequest;
import project.warehouse.control.dto.ShipmentItem;
import project.warehouse.entity.Inventory;
import project.warehouse.entity.Location;
import project.warehouse.entity.Movement;
import project.warehouse.entity.Product;
import project.warehouse.foundation.interfaces.IInventoryRepository;
import project.warehouse.foundation.interfaces.ILocationRepository;
import project.warehouse.foundation.interfaces.IMovementRepository;
import project.warehouse.foundation.interfaces.IProductRepository;
import project.warehouse.mediator.interfaces.IMovementService;
import java.util.List;

@Service
public class MovementService implements IMovementService {

    private final IMovementRepository movementRepository;
    private final IProductRepository productRepository;
    private final ILocationRepository locationRepository;
    private final IInventoryRepository inventoryRepository;

    public MovementService(IMovementRepository movementRepository,
                               IProductRepository productRepository,
                               ILocationRepository locationRepository,
                               IInventoryRepository inventoryRepository) {
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public Movement createReceipt(Integer productId, Integer toLocationId, Integer quantity) {
        // 1. Проверяем существование товара и локации
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Location toLocation = locationRepository.findById(toLocationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + toLocationId));

        // 2. Увеличиваем остатки (создаёт запись, если её нет)
        inventoryRepository.addQuantity(productId, toLocationId, quantity);

        // 3. Создаём запись движения (fromLocation = null означает приход)
        Movement movement = new Movement();
        movement.setProduct(product);
        movement.setToLocation(toLocation);
        movement.setFromLocation(null);
        movement.setQuantity(quantity);

        return movementRepository.save(movement);
    }

    @Override
    @Transactional
    public void createBatchReceipt(ReceiptBatchRequest request) {
        Integer toLocationId = request.getToLocationId();
        List<ReceiptItem> items = request.getItems();

        // 1. Проверяем существование локации
        Location toLocation = locationRepository.findById(toLocationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + toLocationId));

        // 2. Предварительная валидация всех товаров
        for (ReceiptItem item : items) {
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();

            // Проверяем существование товара
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            // Проверяем количество
            if (quantity <= 0) {
                throw new RuntimeException("Quantity must be positive for product: " + product.getName());
            }
        }

        // 3. Если все проверки пройдены — выполняем операции
        for (ReceiptItem item : items) {
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();

            Product product = productRepository.findById(productId).get();

            // Увеличиваем остатки
            inventoryRepository.addQuantity(productId, toLocationId, quantity);

            // Создаём запись движения
            Movement movement = new Movement();
            movement.setProduct(product);
            movement.setToLocation(toLocation);
            movement.setFromLocation(null);
            movement.setQuantity(quantity);

            movementRepository.save(movement);
        }
    }

    @Override
    @Transactional
    public Movement createShipment(Integer productId, Integer fromLocationId, Integer quantity) {
        // 1. Проверяем существование товара и локации
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Location fromLocation = locationRepository.findById(fromLocationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + fromLocationId));

        // ПРОВЕРКА: существует ли товар на указанной локации?
        int currentStock = getCurrentStock(productId, fromLocationId);
        if (currentStock == 0) {
            throw new RuntimeException(
                    "Невозможно отгрузить товар " + product.getName() + " с локации " + fromLocation.getName() + ", так как количество товара равно 0 на указанной локации. Сначала оформите приёмку."
            );
        }

        // ПРОВЕРКА: достаточность остатков
        if (currentStock < quantity) {
            throw new RuntimeException(
                    "Недостаточно товара " + product.getName() +" на локации " + fromLocation.getName() +". Доступно: " + currentStock + ", запрошено: " + quantity
            );
        }

        // 2. Проверяем и уменьшаем остатки
        inventoryRepository.subtractQuantity(productId, fromLocationId, quantity);

        // 3. Создаём запись движения (toLocation = null означает расход)
        Movement movement = new Movement();
        movement.setProduct(product);
        movement.setFromLocation(fromLocation);
        movement.setToLocation(null);
        movement.setQuantity(quantity);

        return movementRepository.save(movement);
    }

    @Override
    @Transactional
    public void createBatchShipment(ShipmentBatchRequest request) {
        Integer fromLocationId = request.getFromLocationId();
        List<ShipmentItem> items = request.getItems();

        // 1. Проверяем существование локации
        Location fromLocation = locationRepository.findById(fromLocationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + fromLocationId));

        // 2. Предварительная валидация всех товаров
        for (ShipmentItem item : items) {
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();

            // Проверяем существование товара
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            // Проверяем количество
            if (quantity <= 0) {
                throw new RuntimeException("Quantity must be positive for product: " + product.getName());
            }

            // Проверяем, существует ли товар на этой локации
            Inventory inventory = inventoryRepository.findByProductAndLocation(product, fromLocation)
                    .orElseThrow(() -> new RuntimeException(
                            "Невозможно отгрузить товар " + product.getName() + " с локации " + fromLocation.getName() + ", так как товар отсутствует на указанной локации. Сначала оформите приёмку."
                    ));

            // Проверяем достаточность остатков
            if (inventory.getQuantity() < quantity) {
                throw new RuntimeException(
                        "Недостаточно товара " + product.getName() +" на локации " + fromLocation.getName() +". Доступно: " + inventory.getQuantity() + ", запрошено: " + quantity
                );
            }
        }

        // 3. Если все проверки пройдены — выполняем операции
        for (ShipmentItem item : items) {
            Integer productId = item.getProductId();
            Integer quantity = item.getQuantity();

            Product product = productRepository.findById(productId).get();

            // Получаем текущий Inventory и уменьшаем количество
            Inventory inventory = inventoryRepository.findByProductAndLocation(product, fromLocation).get();
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventoryRepository.save(inventory);

            // Создаём запись движения
            Movement movement = new Movement();
            movement.setProduct(product);
            movement.setFromLocation(fromLocation);
            movement.setToLocation(null);
            movement.setQuantity(quantity);

            movementRepository.save(movement);
        }
    }

    @Override
    @Transactional
    public Movement moveProduct(Integer productId, Integer fromLocationId, Integer toLocationId, Integer quantity) {
        // 1. Проверяем существование товара и локаций
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Location fromLocation = locationRepository.findById(fromLocationId)
                .orElseThrow(() -> new RuntimeException("From location not found with id: " + fromLocationId));
        Location toLocation = locationRepository.findById(toLocationId)
                .orElseThrow(() -> new RuntimeException("To location not found with id: " + toLocationId));

        // 2. Проверяем достаточность остатков в исходной локации
            int currentStock = getCurrentStock(productId, fromLocationId);
        if (currentStock < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + currentStock + ", requested: " + quantity);
        }

        // 3. Уменьшаем остатки в исходной локации
        inventoryRepository.subtractQuantity(productId, fromLocationId, quantity);

        // 4. Увеличиваем остатки в целевой локации (создаёт запись, если её нет)
        inventoryRepository.addQuantity(productId, toLocationId, quantity);

        // 5. Создаём запись движения
        Movement movement = new Movement();
        movement.setProduct(product);
        movement.setFromLocation(fromLocation);
        movement.setToLocation(toLocation);
        movement.setQuantity(quantity);

        return movementRepository.save(movement);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCurrentStock(Integer productId, Integer locationId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));

        return inventoryRepository.findByProductAndLocation(product, location)
                .map(Inventory::getQuantity)
                .orElse(0);
    }

}