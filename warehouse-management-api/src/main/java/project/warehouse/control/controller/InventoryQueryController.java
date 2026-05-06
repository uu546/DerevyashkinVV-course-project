package project.warehouse.control.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.warehouse.control.dto.InventorySummaryResponse;
import project.warehouse.mediator.interfaces.IInventoryQueryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryQueryController {

    private final IInventoryQueryService inventoryQueryService;

    public InventoryQueryController(IInventoryQueryService inventoryQueryService) {
        this.inventoryQueryService = inventoryQueryService;
    }

    @GetMapping("/summary")
    public ResponseEntity<InventorySummaryResponse> getFullInventorySummary() {
        InventorySummaryResponse response = inventoryQueryService.getFullInventorySummary();
        return ResponseEntity.ok(response);
    }
}