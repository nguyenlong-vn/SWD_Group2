package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.Users;
import service.DiscrepancyService;
import view.DiscrepancyView;

@WebServlet(name = "DiscrepancyController", urlPatterns = {"/DiscrepancyController"})
public class DiscrepancyController extends HttpServlet {

    private final DiscrepancyService discrepancyService = new DiscrepancyService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            case "getDiscrepancyForm":
                handleGetDiscrepancyForm(request, response);
                break;
            case "createDiscrepancyLog":
                handleCreateDiscrepancyLog(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/PurchaseController?action=discrepancyList");
                break;
        }
    }

    public DiscrepancyView getDiscrepancyForm(int importOrderId) {
        return discrepancyService.getDiscrepancyForm(importOrderId);
    }

    public int createDiscrepancyLog(int importOrderId, double quantity, int reportedBy) {
        return discrepancyService.createDiscrepancyLog(importOrderId, quantity, reportedBy);
    }

    private void handleGetDiscrepancyForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importOrderId = parsePositiveInt(request.getParameter("importOrderId"));
        DiscrepancyView discrepancyView = (importOrderId > 0) ? getDiscrepancyForm(importOrderId) : null;
        if (discrepancyView == null) {
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=discrepancyList");
            return;
        }
        request.setAttribute("discrepancyView", discrepancyView);
        request.getRequestDispatcher("/CompareProvisionalReceipt.jsp").forward(request, response);
    }

    private void handleCreateDiscrepancyLog(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importOrderId = parsePositiveInt(request.getParameter("importOrderId"));
        double quantity = parseQuantity(request.getParameter("quantity"));
        Users user = (Users) request.getSession().getAttribute("user");
        int reportedBy = (user == null) ? 0 : user.getUserId();

        int createdCount = createDiscrepancyLog(importOrderId, quantity, reportedBy);
        if (createdCount >= 0) {
            String message = createdCount == 0
                    ? "No new damaged quantity was found to report."
                    : createdCount + " damaged loss report(s) saved successfully.";
            request.getSession().setAttribute("success", message);
            response.sendRedirect(request.getContextPath()
                    + "/DiscrepancyController?action=getDiscrepancyForm&importOrderId=" + importOrderId);
            return;
        }

        DiscrepancyView discrepancyView = (importOrderId > 0) ? getDiscrepancyForm(importOrderId) : null;
        if (discrepancyView == null) {
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=discrepancyList");
            return;
        }
        request.setAttribute("discrepancyView", discrepancyView);
        request.setAttribute("error", "Invalid input. Loss Report could not be saved.");
        request.getRequestDispatcher("/CompareProvisionalReceipt.jsp").forward(request, response);
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : -1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private double parseQuantity(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
