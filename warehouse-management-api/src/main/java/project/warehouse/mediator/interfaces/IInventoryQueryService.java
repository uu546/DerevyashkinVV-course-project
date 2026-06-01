package project.warehouse.mediator.interfaces;

import project.warehouse.control.dto.InventorySummaryResponse;
import project.warehouse.control.dto.ProductLocationStockDto;

import java.util.List;

public interface IInventoryQueryService {

    /**
     * Получить полную сводку по всем остаткам:
     * - по складам
     * - по ячейкам
     * - по товарам
     */
    InventorySummaryResponse getFullInventorySummary();

    List<ProductLocationStockDto> getProductStockByLocations(Integer productId);
}
