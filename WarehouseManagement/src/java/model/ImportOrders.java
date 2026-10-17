package model;

import java.sql.Timestamp;

public class ImportOrders {
    private int importOrderId;
    private int supplierId;
    private int createdBy;
    private String orderNumber;
    private Timestamp orderDate;
    private Timestamp expectedDate;
    private String status;

    public ImportOrders() {
    }

    public ImportOrders(int importOrderId, int supplierId, int createdBy, String orderNumber, Timestamp orderDate, Timestamp expectedDate, String status) {
        this.importOrderId = importOrderId;
        this.supplierId = supplierId;
        this.createdBy = createdBy;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.expectedDate = expectedDate;
        this.status = status;
    }

    public int getImportOrderId() {
        return importOrderId;
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId = importOrderId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Timestamp getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Timestamp expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
