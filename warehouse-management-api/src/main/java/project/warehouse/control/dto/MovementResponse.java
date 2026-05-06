package project.warehouse.control.dto;
import java.time.LocalDate;

public class MovementResponse {

    private Integer movementId;
    private String status;
    private String message;
    private LocalDate movementDate;

    public MovementResponse() {}

    public MovementResponse(Integer movementId, String status, String message, LocalDate movementDate) {
        this.movementId = movementId;
        this.status = status;
        this.message = message;
        this.movementDate = movementDate;
    }

    // Геттеры
    public Integer getMovementId() {
        return movementId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getMovementDate() {
        return movementDate;
    }

    // Сеттеры
    public void setMovementId(Integer movementId) {
        this.movementId = movementId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
    }
}