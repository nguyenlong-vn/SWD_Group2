package model;

public class ProvisionalReceiptDetails {
    private int provisionalDetailId;
    private int provisionalReceiptId;
    private int productId;
    private String productName;
    private double quantity;
    private double gradeAQuantity;
    private double gradeBQuantity;
    private double damagedQuantity;
    private double remainingReturnQuantity;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getGradeAQuantity() {
        return gradeAQuantity;
    }

    public void setGradeAQuantity(double gradeAQuantity) {
        this.gradeAQuantity = gradeAQuantity;
    }

    public double getGradeBQuantity() {
        return gradeBQuantity;
    }

    public void setGradeBQuantity(double gradeBQuantity) {
        this.gradeBQuantity = gradeBQuantity;
    }

    public double getDamagedQuantity() {
        return damagedQuantity;
    }

    public void setDamagedQuantity(double damagedQuantity) {
        this.damagedQuantity = damagedQuantity;
    }

    public double getRemainingReturnQuantity() {
        return remainingReturnQuantity;
    }

    public void setRemainingReturnQuantity(double remainingReturnQuantity) {
        this.remainingReturnQuantity = remainingReturnQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
