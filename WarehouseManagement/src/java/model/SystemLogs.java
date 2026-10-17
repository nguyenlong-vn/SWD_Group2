package model;

import java.sql.Timestamp;

public class SystemLogs {
    private int logId;
    private int userId;
    private String action;
    private String details;
    private Timestamp createdAt;

    public SystemLogs() {
    }

    public SystemLogs(int logId, int userId, String action, String details, Timestamp createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.createdAt = createdAt;
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
