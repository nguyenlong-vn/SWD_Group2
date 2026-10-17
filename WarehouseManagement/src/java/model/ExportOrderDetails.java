package model;

public class ExportOrderDetails {
    private int exportDetailId;
    private int exportOrderId;
    private int productId;
    private double quantity;
    private String unit;
    private double unitPrice;

    public ExportOrderDetails() {
    }

    public ExportOrderDetails(int exportDetailId, int exportOrderId, int productId, double quantity, String unit, double unitPrice) {
        this.exportDetailId = exportDetailId;
        this.exportOrderId = exportOrderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
    }

    public int getExportDetailId() {
        return exportDetailId;
    }

    public void setExportDetailId(int exportDetailId) {
        this.exportDetailId = exportDetailId;
    }

    public int getExportOrderId() {
        return exportOrderId;
    }

    public void setExportOrderId(int exportOrderId) {
        this.exportOrderId = exportOrderId;
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
