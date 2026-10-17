package model;

public class StockAdjustmentDetails {
    private int adjustmentDetailId;
    private int adjustmentId;
    private int batchId;
    private double systemQty;
    private double actualQty;
    private double quantityChange;

    public StockAdjustmentDetails() {
    }

    public StockAdjustmentDetails(int adjustmentDetailId, int adjustmentId, int batchId, double systemQty, double actualQty, double quantityChange) {
        this.adjustmentDetailId = adjustmentDetailId;
        this.adjustmentId = adjustmentId;
        this.batchId = batchId;
        this.systemQty = systemQty;
        this.actualQty = actualQty;
        this.quantityChange = quantityChange;
    }

    public int getAdjustmentDetailId() {
        return adjustmentDetailId;
    }

    public void setAdjustmentDetailId(int adjustmentDetailId) {
        this.adjustmentDetailId = adjustmentDetailId;
    }

    public int getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(int adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public double getSystemQty() {
        return systemQty;
    }

    public void setSystemQty(double systemQty) {
        this.systemQty = systemQty;
    }

    public double getActualQty() {
        return actualQty;
    }

    public void setActualQty(double actualQty) {
        this.actualQty = actualQty;
    }

    public double getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(double quantityChange) {
        this.quantityChange = quantityChange;
    }
}
