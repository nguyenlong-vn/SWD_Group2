package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Users;
import service.ManagerApprovalService;
import view.ManagerView;

@WebServlet(name = "ManagerController", urlPatterns = {"/ManagerDashboard"})
public class ManagerController extends HttpServlet {
    private final ManagerApprovalService managerService = new ManagerApprovalService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isManager(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if ("details".equals(action)) {
            String orderType = request.getParameter("orderType");
            int orderId = parsePositiveInt(request.getParameter("orderId"));
            ManagerView detailsView = managerService.getDetailsView(orderType, orderId);
            if (detailsView == null) {
                response.sendRedirect(request.getContextPath() + "/ManagerDashboard?action=" + sectionFor(orderType));
                return;
            }
            request.setAttribute("managerView", detailsView);
            forwardWithMessage(request, response);
            return;
        }

        String section = action;
        if (!"imports".equals(section) && !"exports".equals(section) && !"returns".equals(section)) {
            section = "dashboard";
        }
        request.setAttribute("managerView", managerService.getView(section));
        forwardWithMessage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isManager(request, response)) {
            return;
        }

        String orderType = request.getParameter("orderType");
        String returnTo = sectionFor(orderType);
        int orderId = parsePositiveInt(request.getParameter("orderId"));
        String action = request.getParameter("action");
        Users manager = (Users) request.getSession().getAttribute("user");
        int actedBy = manager == null ? 0 : manager.getUserId();
        boolean updated = "accept".equals(action)
                && managerService.completeOrder(orderType, orderId, actedBy);
        if ("reject".equals(action)) {
            updated = managerService.rejectOrder(orderType, orderId, actedBy);
        }
        String resultMessage = updated
                ? ("accept".equals(action) ? "Order accepted and marked Completed." : "Order rejected.")
                : "The order is no longer pending or could not be found.";
        request.getSession().setAttribute("managerMessage", resultMessage);
        response.sendRedirect(request.getContextPath() + "/ManagerDashboard?action=" + returnTo);
    }

    private void forwardWithMessage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Object message = session.getAttribute("managerMessage");
        if (message != null) {
            request.setAttribute("managerMessage", message);
            session.removeAttribute("managerMessage");
        }
        request.getRequestDispatcher("/WarehouseManagerDashboard.jsp").forward(request, response);
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

    private String sectionFor(String orderType) {
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
}