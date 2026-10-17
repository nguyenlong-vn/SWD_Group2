package model;

public class StockCountDetails {
    private int countDetailId;
    private int countId;
    private int batchId;
    private double systemQty;
    private double countedQty;
    private double difference;

    public StockCountDetails() {
    }

    public StockCountDetails(int countDetailId, int countId, int batchId, double systemQty, double countedQty, double difference) {
        this.countDetailId = countDetailId;
        this.countId = countId;
        this.batchId = batchId;
        this.systemQty = systemQty;
        this.countedQty = countedQty;
        this.difference = difference;
    }

    public int getCountDetailId() {
        return countDetailId;
    }

    public void setCountDetailId(int countDetailId) {
        this.countDetailId = countDetailId;
    }

    public int getCountId() {
        return countId;
    }

    public void setCountId(int countId) {
        this.countId = countId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public double getSystemQty() {
        return systemQty;
    }

    public void setSystemQty(double systemQty) {
        this.systemQty = systemQty;
    }

    public double getCountedQty() {
        return countedQty;
    }

    public void setCountedQty(double countedQty) {
        this.countedQty = countedQty;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }
}
