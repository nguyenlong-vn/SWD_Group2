package service;

import dao.ImportOrderDAO;
import dao.ProvisionalDAO;
import dao.ProvisionalReceiptDetailsDAO;
import dao.ProvisionalReceiptsDAO;
import dao.QualityInspectionsDAO;
import dao.SupplierDAO;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import model.ImportOrders;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.QualityInspections;
import model.Suppliers;
import view.ProvisionalView;

public class ProvisionalService {

    private final ImportOrderDAO importOrderDAO = new ImportOrderDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final ProvisionalDAO provisionalDAO = new ProvisionalDAO();
    private final ProvisionalReceiptsDAO provisionalReceiptsDAO = new ProvisionalReceiptsDAO();
    private final ProvisionalReceiptDetailsDAO provisionalReceiptDetailsDAO = new ProvisionalReceiptDetailsDAO();
    private final QualityInspectionsDAO qualityInspectionsDAO = new QualityInspectionsDAO();

    public List<ProvisionalReceipts> getAllProvisionalReceipts() {
        return provisionalReceiptsDAO.getAllProvisionalReceipts();
    }

    public ProvisionalReceipts getProvisionalReceiptById(int provisionalReceiptId) {
        return provisionalReceiptsDAO.getProvisionalReceiptById(provisionalReceiptId);
    }

    public List<ProvisionalReceiptDetails> getReceiptDetails(int provisionalReceiptId) {
        return provisionalReceiptDetailsDAO.getDetailsByReceiptId(provisionalReceiptId);
    }

    public QualityInspections getLatestInspection(int provisionalReceiptId) {
        return qualityInspectionsDAO.getLatestByReceiptId(provisionalReceiptId);
    }

    public boolean updateProvisionalReceipt(int provisionalReceiptId, int inspectedBy,
            List<ProvisionalReceiptDetails> details, String result, String inspectionNotes) {
        if (provisionalReceiptId <= 0 || inspectedBy <= 0 || details == null || details.isEmpty()
                || !("Pending".equals(result) || "Passed".equals(result) || "Failed".equals(result))) {
            return false;
        }

        List<ProvisionalReceiptDetails> savedDetails = getReceiptDetails(provisionalReceiptId);
        if (savedDetails.size() != details.size()) {
            return false;
        }
        Map<Integer, ProvisionalReceiptDetails> savedById = new HashMap<>();
        for (ProvisionalReceiptDetails savedDetail : savedDetails) {
            savedById.put(savedDetail.getProvisionalDetailId(), savedDetail);
        }
        HashSet<Integer> updatedIds = new HashSet<>();
        for (ProvisionalReceiptDetails detail : details) {
            ProvisionalReceiptDetails savedDetail = savedById.get(detail.getProvisionalDetailId());
            if (detail.getProvisionalReceiptId() != provisionalReceiptId
                    || detail.getProvisionalDetailId() <= 0
                    || savedDetail == null || !updatedIds.add(detail.getProvisionalDetailId())
                    || !Double.isFinite(detail.getGradeAQuantity()) || detail.getGradeAQuantity() < 0
                    || !Double.isFinite(detail.getGradeBQuantity()) || detail.getGradeBQuantity() < 0
                    || !Double.isFinite(detail.getDamagedQuantity()) || detail.getDamagedQuantity() < 0) {
                return false;
            }
            double classifiedQuantity = detail.getGradeAQuantity()
                    + detail.getGradeBQuantity() + detail.getDamagedQuantity();
            if (Math.abs(classifiedQuantity - savedDetail.getQuantity()) > 0.0005) {
                return false;
            }
        }
        return provisionalDAO.saveProvisionalReceipt(
                provisionalReceiptId, details, result, inspectionNotes, inspectedBy);
    }

    public ProvisionalReceipts getProvisionalReceiptQuantity(int provisionalReceiptId) {
        return provisionalDAO.getProvisionalReceiptQuantity(provisionalReceiptId);
    }

    public ImportOrders getImportOrderById(int importOrderId) {
        return importOrderDAO.getImportOrderById(importOrderId);
    }

    public ProvisionalView getImportOrder(String importOrderId) {
        if (importOrderId == null || importOrderId.trim().isEmpty()) {
            return null;
        }
        ImportOrders order = getImportOrderWithDetails(importOrderId.trim());
        if (order == null) {
            return null;
        }
        Suppliers supplier = getSupplierById(order.getSupplierId());
        return new ProvisionalView(order, order.getDetails(), supplier);
    }

    public ImportOrders getImportOrderWithDetails(String importOrderId) {
        return importOrderDAO.getImportOrderWithDetails(importOrderId);
    }

    public Suppliers getSupplierById(int supplierId) {
        return supplierDAO.getSupplierById(supplierId);
    }

    public String createProvisionalReceipt(int importOrderId, int receivedBy, String notes, List<ProvisionalReceiptDetails> detailItems) {
        if (detailItems == null || detailItems.isEmpty()) {
            return null;
        }

        ProvisionalReceipts receipt = new ProvisionalReceipts();
        receipt.setImportOrderId(importOrderId);
        String receiptNumber = "PR-" + System.currentTimeMillis();
        receipt.setReceiptNumber(receiptNumber);
        receipt.setReceivedDate(new Timestamp(System.currentTimeMillis()));
        receipt.setReceivedBy(receivedBy);
        receipt.setStatus("Not Approved");
        receipt.setQcStatus("Pending");
        receipt.setNotes(notes);

        int receiptId = saveProvisionalReceipt(receipt, detailItems);
        return receiptId > 0 ? receiptNumber : null;
    }

    public int saveProvisionalReceipt(ProvisionalReceipts receipt, List<ProvisionalReceiptDetails> details) {
        return provisionalDAO.saveProvisionalReceipt(receipt, details);
    }
}
