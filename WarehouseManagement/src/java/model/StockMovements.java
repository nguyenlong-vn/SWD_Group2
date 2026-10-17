package model;

import java.sql.Timestamp;

public class StockMovements {
    private int movementId;
    private int batchId;
    private int fromLocationId;
    private int toLocationId;
    private int movedBy;
    private double quantity;
    private Timestamp movementDate;
    private String reason;

    public StockMovements() {
    }

    public StockMovements(int movementId, int batchId, int fromLocationId, int toLocationId, int movedBy, double quantity, Timestamp movementDate, String reason) {
        this.movementId = movementId;
        this.batchId = batchId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.movedBy = movedBy;
        this.quantity = quantity;
        this.movementDate = movementDate;
        this.reason = reason;
    }

    public int getMovementId() {
        return movementId;
    }

    public void setMovementId(int movementId) {
        this.movementId = movementId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(int fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public int getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(int toLocationId) {
        this.toLocationId = toLocationId;
    }

    public int getMovedBy() {
        return movedBy;
    }

    public void setMovedBy(int movedBy) {
        this.movedBy = movedBy;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Timestamp getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(Timestamp movementDate) {
        this.movementDate = movementDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
