package project.warehouse.control.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ShipmentItem {

    @NotNull(message = "productId не может быть null")
    private Integer productId;

    @Positive(message = "quantity должен быть больше 0")
    private Integer quantity;

    public ShipmentItem() {}

    public ShipmentItem(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
