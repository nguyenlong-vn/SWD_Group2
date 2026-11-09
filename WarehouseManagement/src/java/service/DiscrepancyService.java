package service;

import dao.ImportOrderDAO;
import dao.LossReportsDAO;
import dao.ProvisionalDAO;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.ImportOrderDetails;
import model.ImportOrders;
import model.OrderDiscrepancyRow;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import view.DiscrepancyView;

public class DiscrepancyService {

    private final ImportOrderDAO importOrderDAO = new ImportOrderDAO();
    private final ProvisionalDAO provisionalReceiptDAO = new ProvisionalDAO();
    private final LossReportsDAO lossReportsDAO = new LossReportsDAO();

    public DiscrepancyView getDiscrepancyForm(int importOrderId) {
        if (importOrderId <= 0) {
            return null;
        }
        ImportOrders importOrder = importOrderDAO.getImportOrderById(importOrderId);
        if (importOrder == null) {
            return null;
        }

        DiscrepancyView view = new DiscrepancyView();
        view.setImportOrderId(importOrderId);
        view.setImportOrderNumber(importOrder.getOrderNumber());
        ProvisionalReceipts receipt = provisionalReceiptDAO.getProvisionalReceiptByImportOrder(importOrderId);
        view.setReceiptNumber(receipt == null ? null : receipt.getReceiptNumber());
        view.setDiscrepancies(buildDiscrepancyRows(
                getImportOrderDetails(importOrderId), getProvisionalReceiptDetails(importOrderId)));
        return view;
    }

    public List<ImportOrderDetails> getImportOrderDetails(int importOrderId) {
        return importOrderDAO.getImportOrderDetails(importOrderId);
    }

    public List<ProvisionalReceiptDetails> getProvisionalReceiptDetails(int importOrderId) {
        return provisionalReceiptDAO.getProvisionalReceiptDetails(importOrderId);
    }

    public int createDiscrepancyLog(int importOrderId, double quantity, int reportedBy) {
        if (importOrderId <= 0 || reportedBy <= 0 || !Double.isFinite(quantity) || quantity <= 0) {
            return -1;
        }
        return saveLossReport(importOrderId, quantity, reportedBy);
    }

    public int saveLossReport(int importOrderId, double quantity, int reportedBy) {
        return lossReportsDAO.saveLossReport(importOrderId, quantity, reportedBy);
    }

    private List<OrderDiscrepancyRow> buildDiscrepancyRows(List<ImportOrderDetails> orderDetails,
            List<ProvisionalReceiptDetails> receiptDetails) {
        Map<Integer, OrderDiscrepancyRow> rows = new LinkedHashMap<>();
        for (ImportOrderDetails detail : orderDetails) {
            OrderDiscrepancyRow row = rows.computeIfAbsent(detail.getProductId(), productId -> {
                OrderDiscrepancyRow created = new OrderDiscrepancyRow();
                created.setProductId(productId);
                created.setProductName(detail.getProductName());
                created.setUnit(detail.getUnit());
                return created;
            });
            row.setOrderedQuantity(row.getOrderedQuantity() + detail.getQuantity());
        }
        for (ProvisionalReceiptDetails detail : receiptDetails) {
            OrderDiscrepancyRow row = rows.computeIfAbsent(detail.getProductId(), productId -> {
                OrderDiscrepancyRow created = new OrderDiscrepancyRow();
                created.setProductId(productId);
                created.setProductName(detail.getProductName());
                created.setUnit(detail.getUnit());
                return created;
            });
            row.setReceivedQuantity(row.getReceivedQuantity() + detail.getQuantity());
            row.setGradeAQuantity(row.getGradeAQuantity() + detail.getGradeAQuantity());
            row.setGradeBQuantity(row.getGradeBQuantity() + detail.getGradeBQuantity());
            row.setDamagedQuantity(row.getDamagedQuantity() + detail.getDamagedQuantity());
        }
        return new ArrayList<>(rows.values());
    }
}
