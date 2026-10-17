package model;

import java.sql.Timestamp;

public class ExportOrders {
    private int exportOrderId;
    private int customerId;
    private String orderNumber;
    private Timestamp orderDate;
    private String status;
    private int createdBy;

    public ExportOrders() {
    }

    public ExportOrders(int exportOrderId, int customerId, String orderNumber, Timestamp orderDate, String status, int createdBy) {
        this.exportOrderId = exportOrderId;
        this.customerId = customerId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.status = status;
        this.createdBy = createdBy;
    }

    public int getExportOrderId() {
        return exportOrderId;
    }

    public void setExportOrderId(int exportOrderId) {
        this.exportOrderId = exportOrderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
