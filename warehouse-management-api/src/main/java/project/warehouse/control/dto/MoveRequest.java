package project.warehouse.control.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MoveRequest {

    @NotNull(message = "productId не может быть null")
    private Integer productId;

    @NotNull(message = "fromLocationId не может быть null")
    private Integer fromLocationId;

    @NotNull(message = "toLocationId не может быть null")
    private Integer toLocationId;

    @Positive(message = "quantity должен быть больше 0")
    private Integer quantity;

    // Конструкторы
    public MoveRequest() {}

    public MoveRequest(Integer productId, Integer fromLocationId, Integer toLocationId, Integer quantity) {
        this.productId = productId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.quantity = quantity;
    }

    // Геттеры
    public Integer getProductId() {
        return productId;
    }

    public Integer getFromLocationId() {
        return fromLocationId;
    }

    public Integer getToLocationId() {
        return toLocationId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    // Сеттеры
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setFromLocationId(Integer fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public void setToLocationId(Integer toLocationId) {
        this.toLocationId = toLocationId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}