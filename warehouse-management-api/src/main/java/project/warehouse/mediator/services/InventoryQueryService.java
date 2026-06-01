package project.warehouse.mediator.services;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import project.warehouse.control.dto.*;
import project.warehouse.entity.Inventory;
import project.warehouse.foundation.interfaces.IInventoryRepository;
import project.warehouse.mediator.interfaces.IInventoryQueryService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryQueryService implements IInventoryQueryService {

    private final IInventoryRepository inventoryRepository;

    public InventoryQueryService(IInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public InventorySummaryResponse getFullInventorySummary() {
        // 1. Получаем все остатки с деталями
        List<Inventory> inventories = inventoryRepository.findAllWithDetails();

        // Защита от null
        if (inventories == null) {
            inventories = new ArrayList<>();
        }

        // 2. Группируем по складам
        Map<Integer, List<Inventory>> byWarehouse = inventories.stream()
                .collect(Collectors.groupingBy(i -> i.getLocation().getWarehouse().getId()));

        // 3. Строим DTO для каждого склада
        List<WarehouseInventoryDto> warehouseDtos = new ArrayList<>();
        int totalLocationsCount = 0;
        int totalProductsCount = 0;
        int totalItemsCount = 0;

        for (Map.Entry<Integer, List<Inventory>> warehouseEntry : byWarehouse.entrySet()) {
            List<Inventory> warehouseInventories = warehouseEntry.getValue();

            if (warehouseInventories.isEmpty()) continue;

            var warehouse = warehouseInventories.get(0).getLocation().getWarehouse();

            // Группируем по локациям внутри склада
            Map<Integer, List<Inventory>> byLocation = warehouseInventories.stream()
                    .collect(Collectors.groupingBy(i -> i.getLocation().getId()));

            List<LocationInventoryDto> locationDtos = new ArrayList<>();
            int warehouseTotalItems = 0;

            for (Map.Entry<Integer, List<Inventory>> locationEntry : byLocation.entrySet()) {
                List<Inventory> locationInventories = locationEntry.getValue();
                if (locationInventories.isEmpty()) continue;

                var location = locationInventories.get(0).getLocation();

                // Строим список товаров в локации
                List<ProductInventoryDto> productDtos = locationInventories.stream()
                        .map(inv -> new ProductInventoryDto(
                                inv.getProduct().getId(),
                                inv.getProduct().getName(),
                                inv.getProduct().getUnit().getTitle(),
                                inv.getQuantity(),
                                inv.getProduct().getIsPerishable()
                        ))
                        .collect(Collectors.toList());

                int locationTotalItems = productDtos.stream()
                        .mapToInt(ProductInventoryDto::getQuantity)
                        .sum();

                locationDtos.add(new LocationInventoryDto(
                        location.getId(),
                        location.getName(),
                        location.getType().getTitle(),
                        productDtos,
                        locationTotalItems
                ));

                warehouseTotalItems += locationTotalItems;
                totalLocationsCount++;
                totalProductsCount += productDtos.size();
            }

            warehouseDtos.add(new WarehouseInventoryDto(
                    warehouse.getId(),
                    warehouse.getName(),
                    warehouse.getAddress(),
                    locationDtos,
                    warehouseTotalItems
            ));

            totalItemsCount += warehouseTotalItems;
        }

        // 4. Строим общую сводку
        TotalSummaryDto totalSummary = new TotalSummaryDto(
                warehouseDtos.size(),
                totalLocationsCount,
                totalProductsCount,
                totalItemsCount
        );

        return new InventorySummaryResponse(warehouseDtos, totalSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductLocationStockDto> getProductStockByLocations(Integer productId) {
        List<Inventory> inventories = inventoryRepository.findByProductId(productId);

        return inventories.stream()
                .filter(inv -> inv.getQuantity() > 0)
                .map(inv -> new ProductLocationStockDto(
                        inv.getLocation().getId(),
                        inv.getLocation().getName(),
                        inv.getLocation().getFullName(),
                        inv.getLocation().getType().getTitle(),
                        inv.getLocation().getWarehouse().getName(),
                        inv.getQuantity(),
                        inv.getProduct().getUnit().getTitle()
                ))
                .collect(Collectors.toList());
    }
}