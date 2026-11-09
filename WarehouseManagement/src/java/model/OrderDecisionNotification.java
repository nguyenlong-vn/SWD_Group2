package model;

import java.sql.Timestamp;

public class OrderDecisionNotification {
    private int notificationId;
    private String decision;
    private String orderType;
    private String orderNumber;
    private Timestamp createdAt;

    public OrderDecisionNotification(int notificationId, String decision, String orderType, String orderNumber,
            Timestamp createdAt) {
        this.notificationId = notificationId;
        this.decision = decision;
        this.orderType = orderType;
        this.orderNumber = orderNumber;
        this.createdAt = createdAt;
    }

    public int getNotificationId() { return notificationId; }
    public String getDecision() { return decision; }
    public String getOrderType() { return orderType; }
    public String getOrderNumber() { return orderNumber; }
    public Timestamp getCreatedAt() { return createdAt; }
}