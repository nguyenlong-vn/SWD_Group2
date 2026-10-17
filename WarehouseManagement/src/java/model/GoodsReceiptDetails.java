package model;

public class GoodsReceiptDetails {
    private int receiptDetailId;
    private int receiptId;
    private int productId;
    private int batchId;
    private double quantity;
    private String unit;
    private double unitCost;

    public GoodsReceiptDetails() {
    }

    public GoodsReceiptDetails(int receiptDetailId, int receiptId, int productId, int batchId, double quantity, String unit, double unitCost) {
        this.receiptDetailId = receiptDetailId;
        this.receiptId = receiptId;
        this.productId = productId;
        this.batchId = batchId;
        this.quantity = quantity;
        this.unit = unit;
        this.unitCost = unitCost;
    }

    public int getReceiptDetailId() {
        return receiptDetailId;
    }

    public void setReceiptDetailId(int receiptDetailId) {
        this.receiptDetailId = receiptDetailId;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }
}
