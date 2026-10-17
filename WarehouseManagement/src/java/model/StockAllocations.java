package model;

import java.sql.Timestamp;

public class StockAllocations {
    private int allocationId;
    private int exportDetailId;
    private int batchId;
    private double quantity;
    private Timestamp allocatedDate;
    private String status;

    public StockAllocations() {
    }

    public StockAllocations(int allocationId, int exportDetailId, int batchId, double quantity, Timestamp allocatedDate, String status) {
        this.allocationId = allocationId;
        this.exportDetailId = exportDetailId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.allocatedDate = allocatedDate;
        this.status = status;
    }

    public int getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(int allocationId) {
        this.allocationId = allocationId;
    }

    public int getExportDetailId() {
        return exportDetailId;
    }

    public void setExportDetailId(int exportDetailId) {
        this.exportDetailId = exportDetailId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Timestamp getAllocatedDate() {
        return allocatedDate;
    }

    public void setAllocatedDate(Timestamp allocatedDate) {
        this.allocatedDate = allocatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
