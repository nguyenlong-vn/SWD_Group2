package model;

import java.sql.Timestamp;

public class ProvisionalReceipts {
    private int provisionalReceiptId;
    private int importOrderId;
    private String receiptNumber;
    private Timestamp receivedDate;
    private int receivedBy;
    private String status;
    private String notes;

    public ProvisionalReceipts() {
    }

    public ProvisionalReceipts(int provisionalReceiptId, int importOrderId, String receiptNumber, Timestamp receivedDate, int receivedBy, String status, String notes) {
        this.provisionalReceiptId = provisionalReceiptId;
        this.importOrderId = importOrderId;
        this.receiptNumber = receiptNumber;
        this.receivedDate = receivedDate;
        this.receivedBy = receivedBy;
        this.status = status;
        this.notes = notes;
    }

    public int getProvisionalReceiptId() {
        return provisionalReceiptId;
    }

    public void setProvisionalReceiptId(int provisionalReceiptId) {
        this.provisionalReceiptId = provisionalReceiptId;
    }

    public int getImportOrderId() {
        return importOrderId;
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId = importOrderId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public int getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(int receivedBy) {
        this.receivedBy = receivedBy;
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
