package model;

public class ProvisionalReceiptDetails {
    private int provisionalDetailId;
    private int provisionalReceiptId;
    private int productId;
    private double quantity;
    private String unit;

    public ProvisionalReceiptDetails() {
    }

    public ProvisionalReceiptDetails(int provisionalDetailId, int provisionalReceiptId, int productId, double quantity, String unit) {
        this.provisionalDetailId = provisionalDetailId;
        this.provisionalReceiptId = provisionalReceiptId;
        this.productId = productId;
        this.quantity = quantity;
        this.unit = unit;
    }

    public int getProvisionalDetailId() {
        return provisionalDetailId;
    }

    public void setProvisionalDetailId(int provisionalDetailId) {
        this.provisionalDetailId = provisionalDetailId;
    }

    public int getProvisionalReceiptId() {
        return provisionalReceiptId;
    }

    public void setProvisionalReceiptId(int provisionalReceiptId) {
        this.provisionalReceiptId = provisionalReceiptId;
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
}
