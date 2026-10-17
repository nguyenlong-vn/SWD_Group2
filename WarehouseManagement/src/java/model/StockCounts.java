package model;

import java.sql.Timestamp;

public class StockCounts {
    private int countId;
    private int countedBy;
    private Timestamp countDate;
    private String status;
    private String notes;

    public StockCounts() {
    }

    public StockCounts(int countId, int countedBy, Timestamp countDate, String status, String notes) {
        this.countId = countId;
        this.countedBy = countedBy;
        this.countDate = countDate;
        this.status = status;
        this.notes = notes;
    }

    public int getCountId() {
        return countId;
    }

    public void setCountId(int countId) {
        this.countId = countId;
    }

    public int getCountedBy() {
        return countedBy;
    }

    public void setCountedBy(int countedBy) {
        this.countedBy = countedBy;
    }

    public Timestamp getCountDate() {
        return countDate;
    }

    public void setCountDate(Timestamp countDate) {
        this.countDate = countDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
