package project.warehouse.mediator.interfaces;

import project.warehouse.control.dto.ReceiptBatchRequest;
import project.warehouse.control.dto.ShipmentBatchRequest;
import project.warehouse.entity.Movement;

public interface IMovementService {

    /**
     * Приёмка товара на склад
     * @param productId ID товара
     * @param toLocationId ID локации (куда кладём)
     * @param quantity количество
     */
    Movement createReceipt(Integer productId, Integer toLocationId, Integer quantity);

    void createBatchReceipt(ReceiptBatchRequest request);

    /**
     * Отгрузка товара со склада
     * @param productId ID товара
     * @param fromLocationId ID локации (откуда берём)
     * @param quantity количество
     */
    Movement createShipment(Integer productId, Integer fromLocationId, Integer quantity);

    void createBatchShipment(ShipmentBatchRequest request);

    /**
     * Перемещение товара между локациями
     * @param productId ID товара
     * @param fromLocationId ID исходной локации
     * @param toLocationId ID целевой локации
     * @param quantity количество
     */
    Movement moveProduct(Integer productId, Integer fromLocationId, Integer toLocationId, Integer quantity);

    /**
     * Получить текущий остаток товара на локации
     * @param productId ID товара
     * @param locationId ID локации
     * @return количество
     */
    int getCurrentStock(Integer productId, Integer locationId);
}