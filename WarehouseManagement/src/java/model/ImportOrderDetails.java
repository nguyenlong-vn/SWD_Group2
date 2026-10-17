package model;

public class ImportOrderDetails {
    private int importDetailId;
    private int importOrderId;
    private int productId;
    private double quantity;
    private String unit;
    private double unitPrice;

    public ImportOrderDetails() {
    }

    public ImportOrderDetails(int importDetailId, int importOrderId, int productId, double quantity, String unit, double unitPrice) {
        this.importDetailId = importDetailId;
        this.importOrderId = importOrderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
    }

    public int getImportDetailId() {
        return importDetailId;
    }

    public void setImportDetailId(int importDetailId) {
        this.importDetailId = importDetailId;
    }

    public int getImportOrderId() {
        return importOrderId;
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId = importOrderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
