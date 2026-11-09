package model;

import java.sql.Timestamp;

public class QualityInspections {
    private int inspectionId;
    private int provisionalReceiptId;
    private int inspectedBy;
    private Timestamp inspectionDate;
    private String result;
    private String notes;

    public QualityInspections() {
    }

    public QualityInspections(int inspectionId, int provisionalReceiptId, int inspectedBy, Timestamp inspectionDate, String result, String notes) {
        this.inspectionId = inspectionId;
        this.provisionalReceiptId = provisionalReceiptId;
        this.inspectedBy = inspectedBy;
        this.inspectionDate = inspectionDate;
        this.result = result;
        this.notes = notes;
    }

    public int getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(int inspectionId) {
        this.inspectionId = inspectionId;
    }

    public int getProvisionalReceiptId() {
        return provisionalReceiptId;
    }

    public void setProvisionalReceiptId(int provisionalReceiptId) {
        this.provisionalReceiptId = provisionalReceiptId;
    }

    public int getInspectedBy() {
        return inspectedBy;
    }

    public void setInspectedBy(int inspectedBy) {
        this.inspectedBy = inspectedBy;
    }

    public Timestamp getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(Timestamp inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
