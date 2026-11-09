package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Users;
import service.VendorReturnService;
import view.VendorReturnView;

@WebServlet(name = "VendorReturnController", urlPatterns = {"/VendorReturnController"})
public class VendorReturnController extends HttpServlet {

    private final VendorReturnService vendorReturnService = new VendorReturnService();

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
            case "getVendorReturnForm":
                handleGetVendorReturnForm(request, response);
                break;
            case "createReturnRequest":
                handleCreateReturnRequest(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/PurchaseController?action=receivedOrderList");
                break;
        }
    }

    public VendorReturnView getVendorReturnForm(int importOrderId) {
        return vendorReturnService.getVendorReturnForm(importOrderId);
    }

    public String createReturnRequest(int supplierId, int importOrderId, String reason, double quantity,
            int createdBy) {
        return vendorReturnService.createReturnRequest(supplierId, importOrderId, reason, quantity, createdBy);
    }

    private void handleGetVendorReturnForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importOrderId = parsePositiveInt(request.getParameter("importOrderId"));
        VendorReturnView vendorReturnView = (importOrderId > 0) ? getVendorReturnForm(importOrderId) : null;
        if (vendorReturnView == null) {
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=receivedOrderList");
            return;
        }
        populateForm(request, vendorReturnView);
        request.getRequestDispatcher("/CreateSupplierReturn.jsp").forward(request, response);
    }

    private void handleCreateReturnRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importOrderId = parsePositiveInt(request.getParameter("importOrderId"));
        int supplierId = parsePositiveInt(request.getParameter("supplierId"));
        double quantity = parseDouble(request.getParameter("quantity"));
        String reason = request.getParameter("reason");

        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String returnNumber = (importOrderId > 0)
                ? createReturnRequest(supplierId, importOrderId, reason, quantity, user == null ? 0 : user.getUserId())
                : null;
        if (returnNumber != null) {
            session.setAttribute("success", "Supplier Return " + returnNumber + " created successfully.");
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=receivedOrderList");
            return;
        }

        String errorMessage;
        if (reason == null || reason.trim().isEmpty()) {
            errorMessage = "Enter a return reason.";
        } else if (supplierId <= 0 || importOrderId <= 0 || !Double.isFinite(quantity) || quantity <= 0.0005) {
            errorMessage = "Invalid input.";
        } else {
            errorMessage = "Supplier Return could not be created. Reload the receipt and try again.";
        }
        request.setAttribute("error", errorMessage);

        VendorReturnView vendorReturnView = (importOrderId > 0) ? getVendorReturnForm(importOrderId) : null;
        if (vendorReturnView == null) {
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=receivedOrderList");
            return;
        }
        populateForm(request, vendorReturnView);
        request.getRequestDispatcher("/CreateSupplierReturn.jsp").forward(request, response);
    }

    private void populateForm(HttpServletRequest request, VendorReturnView vendorReturnView) {
        request.setAttribute("vendorReturnView", vendorReturnView);
        request.setAttribute("receipt", vendorReturnView.getReceipt());
        request.setAttribute("receiptDetails", vendorReturnView.getReceiptDetails());
        request.setAttribute("hasReturnableQuantity", vendorReturnView.isHasReturnableQuantity());
        request.setAttribute("returnableQuantity", vendorReturnView.getReturnableQuantity());
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : -1;
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
