package service;

import dao.ImportOrderDAO;
import dao.ProvisionalDAO;
import dao.SupplierDAO;
import dao.SupplierReturnsDAO;
import java.util.ArrayList;
import java.util.List;
import model.ImportOrders;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.PurchaseReceiptRow;
import model.Suppliers;
import view.VendorReturnView;

public class VendorReturnService {

    private final ImportOrderDAO importOrderDAO = new ImportOrderDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final SupplierReturnsDAO supplierReturnDAO = new SupplierReturnsDAO();
    private final ProvisionalDAO provisionalReceiptDAO = new ProvisionalDAO();
    private final PurchaseWorkflowService purchaseWorkflowService = new PurchaseWorkflowService();

    public VendorReturnView getVendorReturnForm(int importOrderId) {
        if (importOrderId <= 0) {
            return null;
        }
        ImportOrders importOrder = getImportOrderById(importOrderId);
        if (importOrder == null) {
            return null;
        }
        Suppliers supplier = getSupplierById(importOrder.getSupplierId());

        ProvisionalReceipts provisionalReceipt = provisionalReceiptDAO.getProvisionalReceiptByImportOrder(importOrderId);
        if (provisionalReceipt == null || !"Failed".equals(provisionalReceipt.getQcStatus())) {
            return null;
        }
        PurchaseReceiptRow receipt = purchaseWorkflowService.getCompletedReceipt(provisionalReceipt.getProvisionalReceiptId());
        if (receipt == null) {
            return null;
        }
        List<ProvisionalReceiptDetails> details = purchaseWorkflowService.getReceiptDetails(receipt.getProvisionalReceiptId());
        double returnableQuantity = totalRemaining(details);
        if (returnableQuantity <= 0.0005) {
            return null;
        }

        VendorReturnView view = new VendorReturnView();
        view.setImportOrderId(importOrderId);
        view.setSupplierId(importOrder.getSupplierId());
        view.setImportOrder(importOrder);
        view.setSupplier(supplier);
        view.setReceipt(receipt);
        view.setReceiptDetails(details);
        view.setHasReturnableQuantity(true);
        view.setReturnableQuantity(returnableQuantity);
        return view;
    }

    public ImportOrders getImportOrderById(int importOrderId) {
        return importOrderDAO.getImportOrderById(importOrderId);
    }

    public Suppliers getSupplierById(int supplierId) {
        return supplierDAO.getSupplierById(supplierId);
    }

    public String createReturnRequest(int supplierId, int importOrderId, String reason, double quantity,
            int createdBy) {
        if (supplierId <= 0 || importOrderId <= 0 || createdBy <= 0
                || reason == null || reason.trim().isEmpty()
                || !Double.isFinite(quantity) || quantity <= 0.0005) {
            return null;
        }
        return saveSupplierReturn(supplierId, importOrderId, reason.trim(), quantity, createdBy);
    }

    public String saveSupplierReturn(int supplierId, int importOrderId, String reason, double quantity,
            int createdBy) {
        ProvisionalReceipts provisionalReceipt = provisionalReceiptDAO.getProvisionalReceiptByImportOrder(importOrderId);
        if (provisionalReceipt == null || !"Failed".equals(provisionalReceipt.getQcStatus())) {
            return null;
        }
        List<ProvisionalReceiptDetails> receivedDetails =
                purchaseWorkflowService.getReceiptDetails(provisionalReceipt.getProvisionalReceiptId());
        double returnableQuantity = totalRemaining(receivedDetails);
        if (returnableQuantity <= 0.0005 || quantity - returnableQuantity > 0.0005) {
            return null;
        }

        List<ProvisionalReceiptDetails> returnDetails = new ArrayList<>();
        for (ProvisionalReceiptDetails received : receivedDetails) {
            double remaining = received.getRemainingReturnQuantity();
            if (remaining > 0) {
                ProvisionalReceiptDetails returnDetail = new ProvisionalReceiptDetails();
                returnDetail.setProvisionalDetailId(received.getProvisionalDetailId());
                returnDetail.setQuantity(remaining);
                returnDetails.add(returnDetail);
            }
        }
        if (returnDetails.isEmpty()) {
            return null;
        }

        String returnNumber = "RET-" + System.currentTimeMillis();
        int returnId = supplierReturnDAO.saveSupplierReturn(supplierId, importOrderId, returnNumber, createdBy,
                reason, returnDetails);
        return returnId > 0 ? returnNumber : null;
    }

    private double totalRemaining(List<ProvisionalReceiptDetails> details) {
        if (details == null) {
            return 0;
        }
        double total = 0;
        for (ProvisionalReceiptDetails detail : details) {
            double remaining = detail.getRemainingReturnQuantity();
            if (Double.isFinite(remaining) && remaining > 0) {
                total += remaining;
            }
        }
        return total;
    }
}
