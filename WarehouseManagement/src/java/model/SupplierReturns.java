package model;

import java.sql.Timestamp;

public class SupplierReturns {
    private int returnId;
    private int supplierId;
    private int importOrderId;
    private String returnNumber;
    private Timestamp returnDate;
    private String reason;
    private String status;
    private int createdBy;

    public SupplierReturns() {
    }

    public SupplierReturns(int returnId, int supplierId, int importOrderId, String returnNumber, Timestamp returnDate, String reason, String status, int createdBy) {
        this.returnId = returnId;
        this.supplierId = supplierId;
        this.importOrderId = importOrderId;
        this.returnNumber = returnNumber;
        this.returnDate = returnDate;
        this.reason = reason;
        this.status = status;
        this.createdBy = createdBy;
    }

    public int getReturnId() {
        return returnId;
    }

    public void setReturnId(int returnId) {
        this.returnId = returnId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getImportOrderId() {
        return importOrderId;
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId = importOrderId;
    }

    public String getReturnNumber() {
        return returnNumber;
    }

    public void setReturnNumber(String returnNumber) {
        this.returnNumber = returnNumber;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
}
