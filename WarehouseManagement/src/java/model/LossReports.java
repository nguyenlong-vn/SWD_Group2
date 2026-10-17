package model;

import java.sql.Timestamp;

public class LossReports {
    private int lossReportId;
    private int batchId;
    private int reportedBy;
    private String lossType;
    private double quantity;
    private String reason;
    private String status;
    private Timestamp reportedDate;

    public LossReports() {
    }

    public LossReports(int lossReportId, int batchId, int reportedBy, String lossType, double quantity, String reason, String status, Timestamp reportedDate) {
        this.lossReportId = lossReportId;
        this.batchId = batchId;
        this.reportedBy = reportedBy;
        this.lossType = lossType;
        this.quantity = quantity;
        this.reason = reason;
        this.status = status;
        this.reportedDate = reportedDate;
    }

    public int getLossReportId() {
        return lossReportId;
    }

    public void setLossReportId(int lossReportId) {
        this.lossReportId = lossReportId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(int reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getLossType() {
        return lossType;
    }

    public void setLossType(String lossType) {
        this.lossType = lossType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(Timestamp reportedDate) {
        this.reportedDate = reportedDate;
    }
}
