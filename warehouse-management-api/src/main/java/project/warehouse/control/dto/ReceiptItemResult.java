package project.warehouse.control.dto;

public class ReceiptItemResult {

    private Integer productId;
    private String productName;
    private Integer quantity;
    private boolean success;
    private String errorMessage;
    private Integer movementId;

    // Конструкторы
    public ReceiptItemResult() {}

    public ReceiptItemResult(Integer productId, String productName, Integer quantity,
                             boolean success, String errorMessage, Integer movementId) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.success = success;
        this.errorMessage = errorMessage;
        this.movementId = movementId;
    }

    // Фабричный метод для успешного результата
    public static ReceiptItemResult success(Integer productId, String productName,
                                            Integer quantity, Integer movementId) {
        return new ReceiptItemResult(productId, productName, quantity, true, null, movementId);
    }

    // Фабричный метод для ошибки
    public static ReceiptItemResult error(Integer productId, String productName,
                                          Integer quantity, String errorMessage) {
        return new ReceiptItemResult(productId, productName, quantity, false, errorMessage, null);
    }

    // Геттеры
    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getMovementId() {
        return movementId;
    }

    // Сеттеры
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setMovementId(Integer movementId) {
        this.movementId = movementId;
    }
}