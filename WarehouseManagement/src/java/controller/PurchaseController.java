package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Users;
import service.OrderRejectionNotificationService;
import service.PurchaseWorkflowService;

@WebServlet(name = "PurchaseController", urlPatterns = {"/PurchaseDashboard", "/PurchaseController"})
public class PurchaseController extends HttpServlet {

    private final PurchaseWorkflowService purchaseService = new PurchaseWorkflowService();
    private final OrderRejectionNotificationService rejectionNotificationService = new OrderRejectionNotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!"PurchaseStaff".equals(request.getSession().getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String action = request.getParameter("action");
        if ("dismissRejectionNotification".equals(action) && "POST".equalsIgnoreCase(request.getMethod())) {
            Users user = (Users) request.getSession().getAttribute("user");
            rejectionNotificationService.dismissForUser(parsePositiveInt(request.getParameter("notificationId")),
                    user == null ? 0 : user.getUserId());
            response.sendRedirect(request.getContextPath() + "/PurchaseDashboard");
            return;
        }
        if ("/PurchaseDashboard".equals(request.getServletPath()) || action == null || action.isEmpty()) {
            Users user = (Users) request.getSession().getAttribute("user");
            request.setAttribute("rejectionNotifications", rejectionNotificationService.getUndismissedForUser(
                    user == null ? 0 : user.getUserId()));
            request.getRequestDispatcher("/PurchaseStaffDashboard.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "receivedOrderList":
            case "discrepancyList":
            case "goodsReceiptList":
                handleReceiptList(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/PurchaseDashboard");
        }
    }

    private void handleReceiptList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("receipts", purchaseService.getPurchaseReceipts());
        request.getRequestDispatcher("/PurchaseCompletedReceiptList.jsp").forward(request, response);
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : -1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}