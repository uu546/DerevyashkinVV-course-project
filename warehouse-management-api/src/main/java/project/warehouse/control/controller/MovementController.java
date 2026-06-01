package project.warehouse.control.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import project.warehouse.control.dto.*;
import project.warehouse.mediator.interfaces.IMovementService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/movements")
public class MovementController {

    private final IMovementService movementService;

    public MovementController(IMovementService movementService) {
        this.movementService = movementService;
    }

    /**
     * Приёмка одного товара на склад
     * POST /api/movements/receipt
     */
    @PostMapping("/receipt")
    public ResponseEntity<MovementResponse> createReceipt(@Valid @RequestBody ReceiptRequest request) {
        var movement = movementService.createReceipt(
                request.getProductId(),
                request.getToLocationId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(new MovementResponse(
                movement.getId(),
                "OK",
                "Приёмка успешно оформлена",
                movement.getMovementDate()
        ));
    }

    /**
     * МАССОВАЯ приёмка товаров на склад (список товаров)
     * POST /api/movements/receipt/batch
     */
    @PostMapping("/receipt/batch")
    public ResponseEntity<Map<String, String>> createBatchReceipt(@Valid @RequestBody ReceiptBatchRequest request) {
        movementService.createBatchReceipt(request);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Товары успешно добавлены на склад");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Отгрузка товара со склада
     * POST /api/movements/shipment
     */
    @PostMapping("/shipment")
    public ResponseEntity<MovementResponse> createShipment(@Valid @RequestBody ShipmentRequest request) {
        var movement = movementService.createShipment(
                request.getProductId(),
                request.getFromLocationId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(new MovementResponse(
                movement.getId(),
                "OK",
                "Отгрузка успешно оформлена",
                movement.getMovementDate()
        ));
    }

    @PostMapping("/shipment/batch")
    public ResponseEntity<Map<String, String>> createBatchShipment(@Valid @RequestBody ShipmentBatchRequest request) {
        movementService.createBatchShipment(request);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Товары успешно отгружены со склада");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Перемещение товара между локациями
     * POST /api/movements/move
     */
    @PostMapping("/move")
    public ResponseEntity<MovementResponse> moveProduct(@Valid @RequestBody MoveRequest request) {
        var movement = movementService.moveProduct(
                request.getProductId(),
                request.getFromLocationId(),
                request.getToLocationId(),
                request.getQuantity()
        );

        return ResponseEntity.ok(new MovementResponse(
                movement.getId(),
                "OK",
                "Товар успешно перемещён",
                movement.getMovementDate()
        ));
    }
}