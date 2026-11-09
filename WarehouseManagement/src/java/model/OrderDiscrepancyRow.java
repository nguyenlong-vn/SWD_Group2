package model;

public class OrderDiscrepancyRow {
    private int productId;
    private String productName;
    private String unit;
    private double orderedQuantity;
    private double receivedQuantity;
    private double gradeAQuantity;
    private double gradeBQuantity;
    private double damagedQuantity;

    public int getProductId() { return productId; }
    public void setProductId(int value) { productId = value; }
    public String getProductName() { return productName; }
    public void setProductName(String value) { productName = value; }
    public String getUnit() { return unit; }
    public void setUnit(String value) { unit = value; }
    public double getOrderedQuantity() { return orderedQuantity; }
    public void setOrderedQuantity(double value) { orderedQuantity = value; }
    public double getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(double value) { receivedQuantity = value; }
    public double getGradeAQuantity() { return gradeAQuantity; }
    public void setGradeAQuantity(double value) { gradeAQuantity = value; }
    public double getGradeBQuantity() { return gradeBQuantity; }
    public void setGradeBQuantity(double value) { gradeBQuantity = value; }
    public double getDamagedQuantity() { return damagedQuantity; }
    public void setDamagedQuantity(double value) { damagedQuantity = value; }
    public double getDifference() { return receivedQuantity - orderedQuantity; }
}