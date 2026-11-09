package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import model.Users;
import service.ExportOrderService;
import service.OrderRejectionNotificationService;
import view.ExportOrderView;

@WebServlet(name = "ExportOrderController", urlPatterns = {"/SaleDashboard", "/ExportOrderController"})
public class ExportOrderController extends HttpServlet {
    private final ExportOrderService exportOrderService = new ExportOrderService();
    private final OrderRejectionNotificationService rejectionNotificationService = new OrderRejectionNotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isSaleStaff(request, response)) {
            return;
        }

        String path = request.getServletPath();
        String action = request.getParameter("action");
        if ("/SaleDashboard".equals(path) && (action == null || action.isEmpty())) {
            Users user = (Users) request.getSession().getAttribute("user");
            request.setAttribute("rejectionNotifications", rejectionNotificationService.getUndismissedForUser(
                    user == null ? 0 : user.getUserId()));
            request.getRequestDispatcher("/SaleStaffDashboard.jsp").forward(request, response);
            return;
        }
        if ("create".equals(action)) {
            showCreateForm(request, response, null);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/SaleDashboard");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isSaleStaff(request, response)) {
            return;
        }
        String action = request.getParameter("action");
        if ("dismissRejectionNotification".equals(action)) {
            Users user = (Users) request.getSession().getAttribute("user");
            rejectionNotificationService.dismissForUser(parsePositiveInt(request.getParameter("notificationId")),
                    user == null ? 0 : user.getUserId());
            response.sendRedirect(request.getContextPath() + "/SaleDashboard");
            return;
        }
        if (!"createExportOrder".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/SaleDashboard");
            return;
        }

        Users user = (Users) request.getSession().getAttribute("user");
        ExportOrderView view = exportOrderService.createOrder(
                request.getParameter("customerId"), request.getParameter("orderDate"),
                user == null ? 0 : user.getUserId(), request.getParameterValues("productId"),
                request.getParameterValues("quantity"));
        if (view.getErrorMessage() != null) {
            request.setAttribute("exportOrderView", view);
            request.setAttribute("today", LocalDate.now().toString());
            request.getRequestDispatcher("/CreateExportOrder.jsp").forward(request, response);
            return;
        }

        request.getSession().setAttribute("exportOrderSuccess", view.getSuccessMessage());
        response.sendRedirect(request.getContextPath() + "/ExportOrderController?action=create");
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object flashMessage = session.getAttribute("exportOrderSuccess");
        ExportOrderView view = exportOrderService.getOrderForm(errorMessage,
                flashMessage == null ? null : flashMessage.toString());
        session.removeAttribute("exportOrderSuccess");
        request.setAttribute("exportOrderView", view);
        request.setAttribute("today", LocalDate.now().toString());
        request.getRequestDispatcher("/CreateExportOrder.jsp").forward(request, response);
    }

    private boolean isSaleStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"SaleStaff".equals(request.getSession().getAttribute("role"))) {
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