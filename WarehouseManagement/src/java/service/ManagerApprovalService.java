package service;

import dao.ExportOrdersDAO;
import dao.ImportOrdersDAO;
import dao.ImportOrderDetailsDAO;
import dao.SupplierReturnsDAO;
import dao.SupplierReturnDetailsDAO;
import dao.ExportOrderDetailsDAO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.ImportOrderDetails;
import model.ManagerApprovalOrder;
import model.ManagerOrderDetail;
import view.ManagerView;

public class ManagerApprovalService {
    private final ImportOrdersDAO importOrdersDAO = new ImportOrdersDAO();
    private final ExportOrdersDAO exportOrdersDAO = new ExportOrdersDAO();
    private final SupplierReturnsDAO supplierReturnsDAO = new SupplierReturnsDAO();

    public ManagerView getView(String section) {
        ManagerView view = new ManagerView();
        view.setSection(section);
        switch (section) {
            case "imports":
                view.setTitle("Approve Import Orders");
                view.setOrders(importOrdersDAO.getPendingOrdersForApproval());
                break;
            case "exports":
                view.setTitle("Approve Export Orders");
                view.setOrders(exportOrdersDAO.getPendingOrdersForApproval());
                break;
            case "returns":
                view.setTitle("Approve Supplier Returns");
                view.setOrders(supplierReturnsDAO.getPendingReturnsForApproval());
                break;
            default:
                view.setSection("dashboard");
                view.setTitle("Warehouse Manager Dashboard");
                view.setOrders(Collections.<ManagerApprovalOrder>emptyList());
                break;
        }
        return view;
    }

    public ManagerView getDetailsView(String orderType, int orderId) {
        String section = sectionForOrderType(orderType);
        if ("dashboard".equals(section) || orderId <= 0) {
            return null;
        }

        ManagerView view = getView(section);
        ManagerApprovalOrder selectedOrder = null;
        for (ManagerApprovalOrder order : view.getOrders()) {
            if (order.getOrderId() == orderId) {
                selectedOrder = order;
                break;
            }
        }
        if (selectedOrder == null) {
            return null;
        }

        view.setSection("details");
        view.setTitle("Order Details");
        view.setSelectedOrder(selectedOrder);
        view.setOrderType(orderTypeForSection(section));
        view.setReturnSection(section);
        view.setOrderDetails(getOrderDetails(section, orderId));
        return view;
    }

    public boolean completeOrder(String orderType, int orderId, int acceptedBy) {
        if (orderId <= 0 || orderType == null || acceptedBy <= 0) {
            return false;
        }
        switch (orderType) {
            case "import":
                return importOrdersDAO.completePendingOrder(orderId, acceptedBy);
            case "export":
                return exportOrdersDAO.completePendingOrder(orderId, acceptedBy);
            case "return":
                return supplierReturnsDAO.completePendingReturn(orderId, acceptedBy);
            default:
                return false;
        }
    }

    public boolean rejectOrder(String orderType, int orderId, int rejectedBy) {
        if (orderId <= 0 || orderType == null || rejectedBy <= 0) {
            return false;
        }
        switch (orderType) {
            case "import":
                return importOrdersDAO.rejectPendingOrder(orderId, rejectedBy);
            case "export":
                return exportOrdersDAO.rejectPendingOrder(orderId, rejectedBy);
            case "return":
                return supplierReturnsDAO.rejectPendingReturn(orderId, rejectedBy);
            default:
                return false;
        }
    }

    private List<ManagerOrderDetail> getOrderDetails(String section, int orderId) {
        List<ManagerOrderDetail> details = new ArrayList<>();
        switch (section) {
            case "imports":
                for (ImportOrderDetails detail : new ImportOrderDetailsDAO().getDetailsByImportOrderId(orderId)) {
                    details.add(new ManagerOrderDetail(detail.getProductName(), detail.getQuantity(),
                            detail.getUnit(), detail.getUnitPrice(), null));
                }
                break;
            case "exports":
                details = new ExportOrderDetailsDAO().getDetailsByExportOrderId(orderId);
                break;
            case "returns":
                details = new SupplierReturnDetailsDAO().getDetailsByReturnId(orderId);
                break;
            default:
                break;
        }
        return details;
    }

    private String sectionForOrderType(String orderType) {
        if ("import".equals(orderType) || "imports".equals(orderType)) {
            return "imports";
        }
        if ("export".equals(orderType) || "exports".equals(orderType)) {
            return "exports";
        }
        if ("return".equals(orderType) || "returns".equals(orderType)) {
            return "returns";
        }
        return "dashboard";
    }

    private String orderTypeForSection(String section) {
        switch (section) {
            case "imports":
                return "import";
            case "exports":
                return "export";
            case "returns":
                return "return";
            default:
                return null;
        }
    }
}