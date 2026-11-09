package model;

import java.sql.Timestamp;

public class ManagerApprovalOrder {
    private int orderId;
    private String orderNumber;
    private Timestamp orderDate;
    private String partyName;
    private String createdByName;
    private String details;
    private String status;

    public ManagerApprovalOrder(int orderId, String orderNumber, Timestamp orderDate, String partyName,
            String createdByName, String details, String status) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.partyName = partyName;
        this.createdByName = createdByName;
        this.details = details;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public Timestamp getOrderDate() { return orderDate; }
    public String getPartyName() { return partyName; }
    public String getCreatedByName() { return createdByName; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
}