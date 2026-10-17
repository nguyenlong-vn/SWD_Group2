package model;

import java.sql.Timestamp;

public class QualityInspections {
    private int inspectionId;
    private int batchId;
    private int inspectedBy;
    private Timestamp inspectionDate;
    private String grade;
    private String result;
    private String notes;

    public QualityInspections() {
    }

    public QualityInspections(int inspectionId, int batchId, int inspectedBy, Timestamp inspectionDate, String grade, String result, String notes) {
        this.inspectionId = inspectionId;
        this.batchId = batchId;
        this.inspectedBy = inspectedBy;
        this.inspectionDate = inspectionDate;
        this.grade = grade;
        this.result = result;
        this.notes = notes;
    }

    public int getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(int inspectionId) {
        this.inspectionId = inspectionId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
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
