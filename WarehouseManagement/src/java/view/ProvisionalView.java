package view;

import controller.ProvisionalController;
import java.text.SimpleDateFormat;
import java.util.List;
import model.ImportOrderDetails;
import model.ImportOrders;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.QualityInspections;
import model.Suppliers;

public class ProvisionalView {

    private ImportOrders importOrder;
    private List<ImportOrderDetails> orderDetails;
    private Suppliers supplier;
    private ProvisionalReceipts receipt;
    private List<ProvisionalReceiptDetails> receiptDetails;
    private QualityInspections inspection;

    public ProvisionalView() {
    }

    public ProvisionalView(ImportOrders importOrder, List<ImportOrderDetails> orderDetails, Suppliers supplier) {
        this.importOrder = importOrder;
        this.orderDetails = orderDetails;
        this.supplier = supplier;
    }

    public ImportOrders getImportOrder() {
        return importOrder;
    }

    public void setImportOrder(ImportOrders importOrder) {
        this.importOrder = importOrder;
    }

    public List<ImportOrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<ImportOrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Suppliers getSupplier() {
        return supplier;
    }

    public void setSupplier(Suppliers supplier) {
        this.supplier = supplier;
    }

    public ProvisionalReceipts getReceipt() {
        return receipt;
    }

    public void setReceipt(ProvisionalReceipts receipt) {
        this.receipt = receipt;
    }

    public List<ProvisionalReceiptDetails> getReceiptDetails() {
        return receiptDetails;
    }

    public void setReceiptDetails(List<ProvisionalReceiptDetails> receiptDetails) {
        this.receiptDetails = receiptDetails;
    }

    public QualityInspections getInspection() {
        return inspection;
    }

    public void setInspection(QualityInspections inspection) {
        this.inspection = inspection;
    }

    public String getSupplierName() {
        return (supplier != null) ? supplier.getSupplierName() : "";
    }

    public String getSupplierCode() {
        return (supplier != null) ? supplier.getSupplierCode() : "";
    }

    public String getFormattedOrderDate() {
        if (importOrder == null || importOrder.getOrderDate() == null) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(importOrder.getOrderDate());
    }

    public String getFormattedExpectedDate() {
        if (importOrder == null || importOrder.getExpectedDate() == null) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(importOrder.getExpectedDate());
    }

    public double getTotalQuantity() {
        double total = 0;
        if (orderDetails != null) {
            for (ImportOrderDetails d : orderDetails) {
                total += d.getQuantity();
            }
        }
        return total;
    }

    public int getDetailCount() {
        return (orderDetails != null) ? orderDetails.size() : 0;
    }

    public ProvisionalView getImportOrder(String importOrderId) {
        return new ProvisionalController().getImportOrder(importOrderId);
    }

    public String createProvisionalReceipt(int importOrderId, int receivedBy, String notes,
            List<ProvisionalReceiptDetails> detailItems) {
        return new ProvisionalController().createProvisionalReceipt(importOrderId, receivedBy, notes, detailItems);
    }

    public ProvisionalView getProvisionalDetail(int provisionalReceiptId) {
        return new ProvisionalController().getProvisionalDetail(provisionalReceiptId);
    }

    public boolean updateProvisionalReceipt(int provisionalReceiptId, List<ProvisionalReceiptDetails> itemDetails,
            String checkStatus, String notes, int inspectedBy) {
        return new ProvisionalController().updateProvisionalReceipt(
                provisionalReceiptId, itemDetails, checkStatus, notes, inspectedBy);
    }
}
