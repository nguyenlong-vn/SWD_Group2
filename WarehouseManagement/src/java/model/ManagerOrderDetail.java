package model;

public class ManagerOrderDetail {
    private String productName;
    private double quantity;
    private String unit;
    private Double unitPrice;
    private String reason;

    public ManagerOrderDetail(String productName, double quantity, String unit, Double unitPrice, String reason) {
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
        this.reason = reason;
    }

    public String getProductName() { return productName; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public Double getUnitPrice() { return unitPrice; }
    public String getReason() { return reason; }
}