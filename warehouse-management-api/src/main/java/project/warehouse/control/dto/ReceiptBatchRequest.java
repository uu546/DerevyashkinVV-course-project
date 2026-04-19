package project.warehouse.control.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ReceiptBatchRequest {

    @NotNull(message = "toLocationId не может быть null")
    private Integer toLocationId;

    @Valid
    @Size(min = 1, message = "Должен быть хотя бы один товар")
    private List<ReceiptItem> items = new ArrayList<>();

    public ReceiptBatchRequest() {}

    public ReceiptBatchRequest(Integer toLocationId, List<ReceiptItem> items) {
        this.toLocationId = toLocationId;
        this.items = items != null ? items : new ArrayList<>();
    }

    public Integer getToLocationId() {
        return toLocationId;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

}