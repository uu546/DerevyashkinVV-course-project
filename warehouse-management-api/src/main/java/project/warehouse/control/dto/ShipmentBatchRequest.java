package project.warehouse.control.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ShipmentBatchRequest {

    @NotNull(message = "fromLocationId не может быть null")
    private Integer fromLocationId;

    @Valid
    @Size(min = 1, message = "Должен быть хотя бы один товар")
    private List<ShipmentItem> items = new ArrayList<>();

    public ShipmentBatchRequest() {}

    public ShipmentBatchRequest(Integer fromLocationId, List<ShipmentItem> items) {
        this.fromLocationId = fromLocationId;
        this.items = items != null ? items : new ArrayList<>();
    }

    public Integer getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Integer fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public List<ShipmentItem> getItems() {
        return items;
    }

    public void setItems(List<ShipmentItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }
}