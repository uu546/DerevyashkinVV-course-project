package project.warehouse.mediator.interfaces;

import project.warehouse.control.dto.InventorySummaryResponse;

public interface IInventoryQueryService {

    /**
     * Получить полную сводку по всем остаткам:
     * - по складам
     * - по ячейкам
     * - по товарам
     */
    InventorySummaryResponse getFullInventorySummary();
}
