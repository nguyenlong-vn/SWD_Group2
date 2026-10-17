package model;

public class Products {
    private int productId;
    private int categoryId;
    private String productCode;
    private String productName;
    private String unit;
    private double basePrice;
    private String status;

    public Products() {
    }

    public Products(int productId, int categoryId, String productCode, String productName, String unit, double basePrice, String status) {
        this.productId = productId;
        this.categoryId = categoryId;
        this.productCode = productCode;
        this.productName = productName;
        this.unit = unit;
        this.basePrice = basePrice;
        this.status = status;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
