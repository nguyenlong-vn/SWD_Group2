package model;

import java.sql.Timestamp;

public class InventoryBatches {
    private int batchId;
    private int productId;
    private int locationId;
    private String batchCode;
    private Timestamp receivedDate;
    private Timestamp expiryDate;
    private double initialQty;
    private double currentQty;
    private double reservedQty;
    private double availableQty;
    private double unitCost;
    private String qualityGrade;
    private String status;

    public InventoryBatches() {
    }

    public InventoryBatches(int batchId, int productId, int locationId, String batchCode, Timestamp receivedDate, Timestamp expiryDate, double initialQty, double currentQty, double reservedQty, double availableQty, double unitCost, String qualityGrade, String status) {
        this.batchId = batchId;
        this.productId = productId;
        this.locationId = locationId;
        this.batchCode = batchCode;
        this.receivedDate = receivedDate;
        this.expiryDate = expiryDate;
        this.initialQty = initialQty;
        this.currentQty = currentQty;
        this.reservedQty = reservedQty;
        this.availableQty = availableQty;
        this.unitCost = unitCost;
        this.qualityGrade = qualityGrade;
        this.status = status;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Timestamp getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getInitialQty() {
        return initialQty;
    }

    public void setInitialQty(double initialQty) {
        this.initialQty = initialQty;
    }

    public double getCurrentQty() {
        return currentQty;
    }

    public void setCurrentQty(double currentQty) {
        this.currentQty = currentQty;
    }

    public double getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(double reservedQty) {
        this.reservedQty = reservedQty;
    }

    public double getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(double availableQty) {
        this.availableQty = availableQty;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
