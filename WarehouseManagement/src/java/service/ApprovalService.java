package service;

import dao.ExportOrdersDAO;
import dao.GoodsReceiptsDAO;
import dao.ImportOrdersDAO;
import dao.InventoryBatchesDAO;
import dao.SupplierReturnsDAO;
import java.util.List;
import model.InventoryBatches;
import model.ManagerApprovalOrder;

public class ApprovalService {

    private final ImportOrdersDAO importOrdersDAO = new ImportOrdersDAO();
    private final ExportOrdersDAO exportOrdersDAO = new ExportOrdersDAO();
    private final GoodsReceiptsDAO goodsReceiptsDAO = new GoodsReceiptsDAO();
    private final SupplierReturnsDAO supplierReturnsDAO = new SupplierReturnsDAO();
    private final InventoryBatchesDAO inventoryBatchesDAO = new InventoryBatchesDAO();
    private final InventoryService inventoryService = new InventoryService();
    private final FefoService fefoService = new FefoService();

    public List<ManagerApprovalOrder> getPendingReceipts() {
        return importOrdersDAO.getPendingOrdersForApproval();
    }

    public List<ManagerApprovalOrder> getPendingGoodsReceipts() {
        return exportOrdersDAO.getPendingOrdersForApproval();
    }

    public List<ManagerApprovalOrder> getPendingReturns() {
        return supplierReturnsDAO.getPendingReturnsForApproval();
    }

    public boolean approveImportGoodsReceipt(int receiptId, int approverId) {
        if (!goodsReceiptsDAO.updateReceiptStatus(receiptId, "APPROVED", approverId)) {
            return false;
        }
        return inventoryService.activateBatchesOfReceipt(receiptId);
    }

    public boolean rejectImportGoodsReceipt(int receiptId, int approverId) {
        return goodsReceiptsDAO.updateReceiptStatus(receiptId, "REJECTED", approverId);
    }

    public boolean approveExportOrder(int orderId, int approverId) {
        if (!exportOrdersDAO.updateStatus(orderId, "APPROVED", approverId)) {
            return false;
        }
        return fefoService.allocateStock(orderId);
    }

    public boolean rejectExportOrder(int orderId, int approverId) {
        return exportOrdersDAO.updateStatus(orderId, "REJECTED", approverId);
    }

    public boolean approveSupplierReturn(int returnId, int approverId) {
        if (!supplierReturnsDAO.updateReturnStatus(returnId, "APPROVED", approverId)) {
            return false;
        }
        List<InventoryBatches> batches = inventoryBatchesDAO.getByReturnId(returnId);
        for (InventoryBatches batch : batches) {
            if (!inventoryBatchesDAO.updateStatus(batch.getBatchId(), "REJECTED")) {
                return false;
            }
        }
        return true;
    }

    public boolean rejectSupplierReturn(int returnId, int approverId) {
        return supplierReturnsDAO.updateReturnStatus(returnId, "REJECTED", approverId);
    }
}
