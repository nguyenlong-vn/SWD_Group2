package service;

import dao.GoodsReceiptsDAO;
import dao.ImportOrderDAO;
import dao.InventoryBatchesDAO;
import dao.ProvisionalDAO;
import dao.SupplierDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import model.ImportOrders;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.PurchaseReceiptRow;
import model.Suppliers;
import view.GoodsReceiptView;

public class GoodsReceiptService {

    private final ImportOrderDAO importOrderDAO = new ImportOrderDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ProvisionalDAO provisionalReceiptDAO = new ProvisionalDAO();
    private final InventoryBatchesDAO inventoryBatchDAO = new InventoryBatchesDAO();
    private final GoodsReceiptsDAO goodsReceiptsDAO = new GoodsReceiptsDAO();
    private final PurchaseWorkflowService purchaseWorkflowService = new PurchaseWorkflowService();

    public GoodsReceiptView getGoodsReceiptForm(int importOrderId) {
        if (importOrderId <= 0) {
            return null;
        }
        ImportOrders importOrder = getImportOrderById(importOrderId);
        if (importOrder == null) {
            return null;
        }
        Suppliers supplier = getSupplierById(importOrder.getSupplierId());
        ProvisionalReceipts provisionalReceipt = getProvisionalReceiptByImportOrder(importOrderId);
        if (provisionalReceipt == null || !"Passed".equals(provisionalReceipt.getQcStatus())
                || goodsReceiptsDAO.hasGoodsReceiptForImportOrder(importOrderId)) {
            return null;
        }

        GoodsReceiptView view = new GoodsReceiptView();
        view.setImportOrderId(importOrderId);
        view.setImportOrder(importOrder);
        view.setSupplier(supplier);
        view.setProvisionalReceipt(provisionalReceipt);
        view.setReceipt(buildReceiptRow(importOrder, supplier, provisionalReceipt));

        List<ProvisionalReceiptDetails> details =
                getProvisionalReceiptDetails(provisionalReceipt.getProvisionalReceiptId());
        view.setReceiptDetails(details);
        view.setAvailableLocations(purchaseWorkflowService.getAvailableLocations());
        view.setClassificationValid(purchaseWorkflowService.hasValidClassification(details));
        view.setClassificationIssues(purchaseWorkflowService.getClassificationIssues(details));
        view.setQuarantineLocation(purchaseWorkflowService.getAvailableQuarantineLocation());
        view.setToday(LocalDate.now().toString());
        return view;
    }

    public ImportOrders getImportOrderById(int importOrderId) {
        return importOrderDAO.getImportOrderById(importOrderId);
    }

    public Suppliers getSupplierById(int supplierId) {
        return supplierDAO.getSupplierById(supplierId);
    }

    public ProvisionalReceipts getProvisionalReceiptByImportOrder(int importOrderId) {
        return provisionalReceiptDAO.getProvisionalReceiptByImportOrder(importOrderId);
    }

    public List<ProvisionalReceiptDetails> getProvisionalReceiptDetails(int provisionalReceiptId) {
        return purchaseWorkflowService.getReceiptDetails(provisionalReceiptId);
    }

    public double getAvailableQuantity(int productId) {
        return inventoryBatchDAO.getAvailableQuantity(productId);
    }

    public String createGoodsReceipt(int importOrderId, List<ProvisionalReceiptDetails> importOrderDetail,
            int receivedBy, String notes, Map<String, Integer> locationByDetailGrade,
            Map<String, LocalDate> expiryByDetailGrade) {
        if (importOrderId <= 0 || importOrderDetail == null || importOrderDetail.isEmpty()
                || receivedBy <= 0 || locationByDetailGrade == null || expiryByDetailGrade == null
                || !purchaseWorkflowService.hasValidClassification(importOrderDetail)) {
            return null;
        }
        ProvisionalReceipts provisionalReceipt = getProvisionalReceiptByImportOrder(importOrderId);
        if (provisionalReceipt == null) {
            return null;
        }

        String receiptNumber = purchaseWorkflowService.createGoodsReceipt(
                provisionalReceipt.getProvisionalReceiptId(), receivedBy, notes,
                locationByDetailGrade, expiryByDetailGrade);
        if (receiptNumber == null) {
            return null;
        }

        updateImportOrder(importOrderId, importOrderDetail);
        return receiptNumber;
    }

    public boolean updateImportOrder(int importOrderId, List<ProvisionalReceiptDetails> importOrderDetail) {
        ImportOrders importOrder = getImportOrderById(importOrderId);
        if (importOrder == null || importOrderDetail == null || importOrderDetail.isEmpty()) {
            return false;
        }
        return importOrderDAO.updateImportOrder(importOrderId, importOrder.getDetails());
    }

    private PurchaseReceiptRow buildReceiptRow(ImportOrders importOrder, Suppliers supplier,
            ProvisionalReceipts provisionalReceipt) {
        PurchaseReceiptRow row = new PurchaseReceiptRow();
        row.setProvisionalReceiptId(provisionalReceipt.getProvisionalReceiptId());
        row.setImportOrderId(importOrder.getImportOrderId());
        row.setSupplierId(importOrder.getSupplierId());
        row.setReceiptNumber(provisionalReceipt.getReceiptNumber());
        row.setReceivedDate(provisionalReceipt.getReceivedDate());
        row.setStatus(provisionalReceipt.getStatus());
        row.setQcStatus(provisionalReceipt.getQcStatus());
        row.setImportOrderNumber(importOrder.getOrderNumber());
        row.setSupplierName(supplier == null ? null : supplier.getSupplierName());
        return row;
    }
}
