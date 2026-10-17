package model;

import java.sql.Timestamp;

public class StockAdjustments {
    private int adjustmentId;
    private int countId;
    private int requestedBy;
    private int approvedBy;
    private Timestamp requestDate;
    private String reason;
    private String status;
    private Timestamp approvedDate;

    public StockAdjustments() {
    }

    public StockAdjustments(int adjustmentId, int countId, int requestedBy, int approvedBy, Timestamp requestDate, String reason, String status, Timestamp approvedDate) {
        this.adjustmentId = adjustmentId;
        this.countId = countId;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.requestDate = requestDate;
        this.reason = reason;
        this.status = status;
        this.approvedDate = approvedDate;
    }

    public int getAdjustmentId() {
        return adjustmentId;
    }

    public void setAdjustmentId(int adjustmentId) {
        this.adjustmentId = adjustmentId;
    }

    public int getCountId() {
        return countId;
    }

    public void setCountId(int countId) {
        this.countId = countId;
    }

    public int getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(int requestedBy) {
        this.requestedBy = requestedBy;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }
}
