package model;

import java.sql.Timestamp;

public class GoodsReceiptEntry {
    private int productId;
    private int locationId;
    private String batchCode;
    private Timestamp receivedDate;
    private Timestamp expiryDate;
    private double quantity;
    private double unitCost;
    private String unit;
    private String qualityGrade;
    private String status;

    public int getProductId() { return productId; }
    public void setProductId(int value) { productId = value; }
    public int getLocationId() { return locationId; }
    public void setLocationId(int value) { locationId = value; }
    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String value) { batchCode = value; }
    public Timestamp getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Timestamp value) { receivedDate = value; }
    public Timestamp getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Timestamp value) { expiryDate = value; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double value) { quantity = value; }
    public double getUnitCost() { return unitCost; }
    public void setUnitCost(double value) { unitCost = value; }
    public String getUnit() { return unit; }
    public void setUnit(String value) { unit = value; }
    public String getQualityGrade() { return qualityGrade; }
    public void setQualityGrade(String value) { qualityGrade = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
}