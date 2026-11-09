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
import service.OutboundOrderService;
import view.OutboundOrderView;

@WebServlet(name = "OutboundOrderController", urlPatterns = {"/OutboundOrderController"})
public class OutboundOrderController extends HttpServlet {

    private final OutboundOrderService outboundOrderService = new OutboundOrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isSaleStaff(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "getOutboundOrderForm":
                handleGetOutboundOrderForm(request, response);
                break;
            case "processCreateOrder":
                handleProcessCreateOrder(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/SaleDashboard");
                break;
        }
    }

    public OutboundOrderView getOrderForm(String errorMessage, String successMessage) {
        return outboundOrderService.getOrderForm(errorMessage, successMessage);
    }

    public OutboundOrderView processCreateOrder(String customerIdValue, String orderDateValue, int createdBy,
            String[] productIdValues, String[] quantityValues) {
        return outboundOrderService.processCreateOrder(customerIdValue, orderDateValue, createdBy,
                productIdValues, quantityValues);
    }

    private void handleGetOutboundOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object flashMessage = session.getAttribute("exportOrderSuccess");
        OutboundOrderView view = getOrderForm(null, flashMessage == null ? null : flashMessage.toString());
        session.removeAttribute("exportOrderSuccess");
        request.setAttribute("exportOrderView", view);
        request.setAttribute("today", LocalDate.now().toString());
        request.getRequestDispatcher("/CreateExportOrder.jsp").forward(request, response);
    }

    private void handleProcessCreateOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Users user = (Users) request.getSession().getAttribute("user");
        OutboundOrderView view = processCreateOrder(
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
        response.sendRedirect(request.getContextPath() + "/OutboundOrderController?action=getOutboundOrderForm");
    }

    private boolean isSaleStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!"SaleStaff".equals(request.getSession().getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return false;
        }
        return true;
    }
}
