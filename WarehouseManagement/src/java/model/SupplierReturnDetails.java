package model;

public class SupplierReturnDetails {
    private int returnDetailId;
    private int returnId;
    private int provisionalDetailId;
    private double quantity;
    private String reason;

    public SupplierReturnDetails() {
    }

    public SupplierReturnDetails(int returnDetailId, int returnId, int provisionalDetailId, double quantity, String reason) {
        this.returnDetailId = returnDetailId;
        this.returnId = returnId;
        this.provisionalDetailId = provisionalDetailId;
        this.quantity = quantity;
        this.reason = reason;
    }

    public int getReturnDetailId() {
        return returnDetailId;
    }

    public void setReturnDetailId(int returnDetailId) {
        this.returnDetailId = returnDetailId;
    }

    public int getReturnId() {
        return returnId;
    }

    public void setReturnId(int returnId) {
        this.returnId = returnId;
    }

    public int getProvisionalDetailId() {
        return provisionalDetailId;
    }

    public void setProvisionalDetailId(int provisionalDetailId) {
        this.provisionalDetailId = provisionalDetailId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
