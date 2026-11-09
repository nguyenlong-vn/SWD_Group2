package model;

import java.sql.Timestamp;

public class PurchaseReceiptRow {
    private int provisionalReceiptId;
    private int importOrderId;
    private int supplierId;
    private String receiptNumber;
    private String importOrderNumber;
    private String supplierName;
    private Timestamp receivedDate;
    private String status;
    private String qcStatus;
    private boolean goodsReceiptExists;
    private boolean supplierReturnPending;
    private boolean supplierReturnCompleted;
    private boolean returnableQuantityAvailable;

    public int getProvisionalReceiptId() { return provisionalReceiptId; }
    public void setProvisionalReceiptId(int value) { provisionalReceiptId = value; }
    public int getImportOrderId() { return importOrderId; }
    public void setImportOrderId(int value) { importOrderId = value; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int value) { supplierId = value; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String value) { receiptNumber = value; }
    public String getImportOrderNumber() { return importOrderNumber; }
    public void setImportOrderNumber(String value) { importOrderNumber = value; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String value) { supplierName = value; }
    public Timestamp getReceivedDate() { return receivedDate; }
    public void setReceivedDate(Timestamp value) { receivedDate = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { status = value; }
    public String getQcStatus() { return qcStatus; }
    public void setQcStatus(String value) { qcStatus = value; }
    public boolean isGoodsReceiptExists() { return goodsReceiptExists; }
    public void setGoodsReceiptExists(boolean value) { goodsReceiptExists = value; }
    public boolean isSupplierReturnPending() { return supplierReturnPending; }
    public void setSupplierReturnPending(boolean value) { supplierReturnPending = value; }
    public boolean isSupplierReturnCompleted() { return supplierReturnCompleted; }
    public void setSupplierReturnCompleted(boolean value) { supplierReturnCompleted = value; }
    public boolean isReturnableQuantityAvailable() { return returnableQuantityAvailable; }
    public void setReturnableQuantityAvailable(boolean value) { returnableQuantityAvailable = value; }
}