package view;

import controller.GoodsReceiptController;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import model.ImportOrders;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.PurchaseReceiptRow;
import model.StorageLocations;
import model.Suppliers;

public class GoodsReceiptView {

    private int importOrderId;
    private ImportOrders importOrder;
    private Suppliers supplier;
    private ProvisionalReceipts provisionalReceipt;
    private PurchaseReceiptRow receipt;
    private List<ProvisionalReceiptDetails> receiptDetails;
    private List<StorageLocations> availableLocations;
    private StorageLocations quarantineLocation;
    private boolean classificationValid;
    private List<String> classificationIssues;
    private String today;

    public GoodsReceiptView() {
    }

    public int getImportOrderId() {
        return importOrderId;
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId = importOrderId;
    }

    public ImportOrders getImportOrder() {
        return importOrder;
    }

    public void setImportOrder(ImportOrders importOrder) {
        this.importOrder = importOrder;
    }

    public Suppliers getSupplier() {
        return supplier;
    }

    public void setSupplier(Suppliers supplier) {
        this.supplier = supplier;
    }

    public ProvisionalReceipts getProvisionalReceipt() {
        return provisionalReceipt;
    }

    public void setProvisionalReceipt(ProvisionalReceipts provisionalReceipt) {
        this.provisionalReceipt = provisionalReceipt;
    }

    public PurchaseReceiptRow getReceipt() {
        return receipt;
    }

    public void setReceipt(PurchaseReceiptRow receipt) {
        this.receipt = receipt;
    }

    public List<ProvisionalReceiptDetails> getReceiptDetails() {
        return receiptDetails;
    }

    public void setReceiptDetails(List<ProvisionalReceiptDetails> receiptDetails) {
        this.receiptDetails = receiptDetails;
    }

    public List<StorageLocations> getAvailableLocations() {
        return availableLocations;
    }

    public void setAvailableLocations(List<StorageLocations> availableLocations) {
        this.availableLocations = availableLocations;
    }

    public StorageLocations getQuarantineLocation() {
        return quarantineLocation;
    }

    public void setQuarantineLocation(StorageLocations quarantineLocation) {
        this.quarantineLocation = quarantineLocation;
    }

    public boolean isClassificationValid() {
        return classificationValid;
    }

    public void setClassificationValid(boolean classificationValid) {
        this.classificationValid = classificationValid;
    }

    public List<String> getClassificationIssues() {
        return classificationIssues;
    }

    public void setClassificationIssues(List<String> classificationIssues) {
        this.classificationIssues = classificationIssues;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public boolean isHasDamagedQuantity() {
        return receiptDetails != null && receiptDetails.stream()
                .anyMatch(detail -> detail.getDamagedQuantity() > 0);
    }

    public GoodsReceiptView getGoodsReceiptForm(int importOrderId) {
        return new GoodsReceiptController().getGoodsReceiptForm(importOrderId);
    }

    public String createGoodsReceipt(int importOrderId, List<ProvisionalReceiptDetails> importOrderDetail,
            int receivedBy, String notes, Map<String, Integer> locationByDetailGrade,
            Map<String, LocalDate> expiryByDetailGrade) {
        return new GoodsReceiptController().createGoodsReceipt(importOrderId, importOrderDetail, receivedBy, notes,
                locationByDetailGrade, expiryByDetailGrade);
    }
}
