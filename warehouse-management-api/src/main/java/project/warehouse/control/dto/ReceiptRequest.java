package project.warehouse.control.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReceiptRequest {

    @NotNull(message = "productId не может быть null")
    private Integer productId;

    @NotNull(message = "toLocationId не может быть null")
    private Integer toLocationId;

    @Positive(message = "quantity должен быть больше 0")
    private Integer quantity;

    // Конструкторы
    public ReceiptRequest() {}

    public ReceiptRequest(Integer productId, Integer toLocationId, Integer quantity) {
        this.productId = productId;
        this.toLocationId = toLocationId;
        this.quantity = quantity;
    }

    // Геттеры
    public Integer getProductId() {
        return productId;
    }

    public Integer getToLocationId() {
        return toLocationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // Сеттеры
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}