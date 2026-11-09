package view;

import controller.VendorReturnController;
import java.util.List;
import model.ImportOrders;
import model.ProvisionalReceiptDetails;
import model.PurchaseReceiptRow;
import model.Suppliers;

public class VendorReturnView {

    private int importOrderId;
    private int supplierId;
    private ImportOrders importOrder;
    private Suppliers supplier;
    private PurchaseReceiptRow receipt;
    private List<ProvisionalReceiptDetails> receiptDetails;
    private boolean hasReturnableQuantity;
    private double returnableQuantity;

    public VendorReturnView() {
    }

    public int getImportOrderId() {
        return importOrderId;
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId = importOrderId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
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

    public boolean isHasReturnableQuantity() {
        return hasReturnableQuantity;
    }

    public void setHasReturnableQuantity(boolean hasReturnableQuantity) {
        this.hasReturnableQuantity = hasReturnableQuantity;
    }

    public double getReturnableQuantity() {
        return returnableQuantity;
    }

    public void setReturnableQuantity(double returnableQuantity) {
        this.returnableQuantity = returnableQuantity;
    }

    public VendorReturnView getVendorReturnForm(int importOrderId) {
        return new VendorReturnController().getVendorReturnForm(importOrderId);
    }

    public String createReturnRequest(int supplierId, int importOrderId, String reason, double quantity,
            int createdBy) {
        return new VendorReturnController().createReturnRequest(supplierId, importOrderId, reason, quantity, createdBy);
    }
}
