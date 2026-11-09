package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Users;
import service.ApprovalService;
import view.ApprovalQueueView;

@WebServlet(name = "ApprovalController", urlPatterns = {"/ApprovalController"})
public class ApprovalController extends HttpServlet {

    private final ApprovalService approvalService = new ApprovalService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isManager(request, response)) {
            return;
        }
        handleGetQueue(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isManager(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        String orderType = normalizeOrderType(request.getParameter("orderType"));
        int orderId = parsePositiveInt(request.getParameter("orderId"));
        Users manager = (Users) request.getSession().getAttribute("user");
        int approverId = manager == null ? 0 : manager.getUserId();

        boolean updated = false;
        if ("export".equals(orderType)) {
            if ("approve".equals(action)) {
                updated = approveExportOrder(orderId, approverId);
            } else if ("reject".equals(action)) {
                updated = rejectExportOrder(orderId, approverId);
            }
        } else if ("return".equals(orderType)) {
            if ("approve".equals(action)) {
                updated = approveSupplierReturn(orderId, approverId);
            } else if ("reject".equals(action)) {
                updated = rejectSupplierReturn(orderId, approverId);
            }
        } else {
            if ("approve".equals(action)) {
                updated = approveImportGoodsReceipt(orderId, approverId);
            } else if ("reject".equals(action)) {
                updated = rejectImportGoodsReceipt(orderId, approverId);
            }
        }

        String resultMessage;
        if (!updated) {
            resultMessage = "The order is no longer pending or could not be found.";
        } else if ("return".equals(orderType)) {
            resultMessage = "approve".equals(action) ? "Return order approved successfully." : "Return order rejected.";
        } else {
            resultMessage = "approve".equals(action) ? "Order approved and marked Completed." : "Order rejected.";
        }
        request.getSession().setAttribute("approvalMessage", resultMessage);
        response.sendRedirect(request.getContextPath() + "/ApprovalController?orderType=" + orderType);
    }

    public ApprovalQueueView getPendingReceipts() {
        ApprovalQueueView view = new ApprovalQueueView();
        view.setTitle("Approve Import Orders");
        view.setPendingOrders(approvalService.getPendingReceipts());
        return view;
    }

    public ApprovalQueueView getPendingGoodsReceipts() {
        ApprovalQueueView view = new ApprovalQueueView();
        view.setTitle("Approve Export Orders");
        view.setPendingOrders(approvalService.getPendingGoodsReceipts());
        return view;
    }

    public ApprovalQueueView getPendingReturns() {
        ApprovalQueueView view = new ApprovalQueueView();
        view.setTitle("Approve Supplier Returns");
        view.setPendingOrders(approvalService.getPendingReturns());
        return view;
    }

    public boolean approveImportGoodsReceipt(int receiptId, int approverId) {
        return approvalService.approveImportGoodsReceipt(receiptId, approverId);
    }

    public boolean rejectImportGoodsReceipt(int receiptId, int approverId) {
        return approvalService.rejectImportGoodsReceipt(receiptId, approverId);
    }

    public boolean approveExportOrder(int orderId, int approverId) {
        return approvalService.approveExportOrder(orderId, approverId);
    }

    public boolean rejectExportOrder(int orderId, int approverId) {
        return approvalService.rejectExportOrder(orderId, approverId);
    }

    public boolean approveSupplierReturn(int returnId, int approverId) {
        return approvalService.approveSupplierReturn(returnId, approverId);
    }

    public boolean rejectSupplierReturn(int returnId, int approverId) {
        return approvalService.rejectSupplierReturn(returnId, approverId);
    }

    private void handleGetQueue(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String orderType = normalizeOrderType(request.getParameter("orderType"));
        ApprovalQueueView queueView;
        if ("export".equals(orderType)) {
            queueView = getPendingGoodsReceipts();
        } else if ("return".equals(orderType)) {
            queueView = getPendingReturns();
        } else {
            queueView = getPendingReceipts();
        }

        HttpSession session = request.getSession();
        Object message = session.getAttribute("approvalMessage");
        if (message != null) {
            queueView.setMessage(message.toString());
            session.removeAttribute("approvalMessage");
        }
        queueView.renderQueueTable();
        request.setAttribute("approvalQueueView", queueView);
        request.setAttribute("orderType", orderType);
        request.getRequestDispatcher("/ApprovalQueue.jsp").forward(request, response);
    }

    private String normalizeOrderType(String orderType) {
        if ("export".equalsIgnoreCase(orderType)) {
            return "export";
        }
        if ("return".equalsIgnoreCase(orderType)) {
            return "return";
        }
        return "import";
    }

    private boolean isManager(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"WarehouseManager".equals(request.getSession().getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return false;
        }
        return true;
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
