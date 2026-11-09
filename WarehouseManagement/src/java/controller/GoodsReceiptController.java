package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.Users;
import service.GoodsReceiptService;
import view.GoodsReceiptView;

@WebServlet(name = "GoodsReceiptController", urlPatterns = {"/GoodsReceiptController"})
public class GoodsReceiptController extends HttpServlet {

    private final GoodsReceiptService goodsReceiptService = new GoodsReceiptService();

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
            case "getGoodsReceiptForm":
                handleGetGoodsReceiptForm(request, response);
                break;
            case "createGoodsReceipt":
                handleCreateGoodsReceipt(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/PurchaseController?action=goodsReceiptList");
                break;
        }
    }

    public GoodsReceiptView getGoodsReceiptForm(int importOrderId) {
        return goodsReceiptService.getGoodsReceiptForm(importOrderId);
    }

    public String createGoodsReceipt(int importOrderId, List<ProvisionalReceiptDetails> importOrderDetail,
            int receivedBy, String notes, Map<String, Integer> locationByDetailGrade,
            Map<String, LocalDate> expiryByDetailGrade) {
        return goodsReceiptService.createGoodsReceipt(importOrderId, importOrderDetail, receivedBy, notes,
                locationByDetailGrade, expiryByDetailGrade);
    }

    private void handleGetGoodsReceiptForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importOrderId = parsePositiveInt(request.getParameter("importOrderId"));
        GoodsReceiptView goodsReceiptView = (importOrderId > 0) ? getGoodsReceiptForm(importOrderId) : null;
        if (goodsReceiptView == null) {
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=goodsReceiptList");
            return;
        }
        populateForm(request, goodsReceiptView);
        request.getRequestDispatcher("/CreateGoodsReceipt.jsp").forward(request, response);
    }

    private void handleCreateGoodsReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int importOrderId = parsePositiveInt(request.getParameter("importOrderId"));
        String[] detailIds = request.getParameterValues("detailId");
        String[] gradeTypes = request.getParameterValues("gradeType");
        String[] locationIds = request.getParameterValues("locationId");
        String[] expiryDates = request.getParameterValues("expiryDate");
        Map<String, Integer> locations = new HashMap<>();
        Map<String, LocalDate> expiries = new HashMap<>();
        String error = null;

        if (detailIds == null || gradeTypes == null || detailIds.length != gradeTypes.length) {
            error = "Product grade rows are invalid.";
        } else {
            try {
                int storageFieldsIndex = 0;
                for (int i = 0; i < detailIds.length; i++) {
                    int detailId = Integer.parseInt(detailIds[i]);
                    String gradeType = gradeTypes[i];
                    if (!("A".equals(gradeType) || "B".equals(gradeType) || "Damaged".equals(gradeType))) {
                        error = "Unknown product grade.";
                        break;
                    }
                    String key = detailId + "|" + gradeType;
                    if ("Damaged".equals(gradeType)) {
                        locations.put(key, -1);
                        expiries.put(key, LocalDate.now());
                    } else {
                        if (locationIds == null || expiryDates == null
                                || storageFieldsIndex >= locationIds.length || storageFieldsIndex >= expiryDates.length) {
                            error = "A location and expiry date are required for each Grade A/B batch.";
                            break;
                        }
                        if (locations.put(key, Integer.parseInt(locationIds[storageFieldsIndex])) != null) {
                            error = "Duplicate grade rows were submitted.";
                            break;
                        }
                        expiries.put(key, LocalDate.parse(expiryDates[storageFieldsIndex]));
                        storageFieldsIndex++;
                    }
                }
                if (error == null && (locationIds == null || expiryDates == null
                        || storageFieldsIndex != locationIds.length || storageFieldsIndex != expiryDates.length)) {
                    error = "Location and expiry data did not match the Grade A/B rows.";
                }
            } catch (RuntimeException ex) {
                error = "Enter a valid location and expiry date for each Grade A/B batch.";
            }
        }

        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        String receiptNumber = null;
        if (error == null && importOrderId > 0) {
            ProvisionalReceipts provisionalReceipt = goodsReceiptService.getProvisionalReceiptByImportOrder(importOrderId);
            List<ProvisionalReceiptDetails> importOrderDetail = (provisionalReceipt == null) ? null
                    : goodsReceiptService.getProvisionalReceiptDetails(provisionalReceipt.getProvisionalReceiptId());
            receiptNumber = createGoodsReceipt(importOrderId, importOrderDetail,
                    user == null ? 0 : user.getUserId(), request.getParameter("notes"), locations, expiries);
        }
        if (receiptNumber != null) {
            session.setAttribute("success", "Goods Receipt " + receiptNumber + " created successfully.");
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=goodsReceiptList");
            return;
        }

        request.setAttribute("error", error == null
                ? "Goods Receipt could not be created. Check classification, expiry dates, locations, and existing receipts."
                : error);
        GoodsReceiptView goodsReceiptView = (importOrderId > 0) ? getGoodsReceiptForm(importOrderId) : null;
        if (goodsReceiptView == null) {
            response.sendRedirect(request.getContextPath() + "/PurchaseController?action=goodsReceiptList");
            return;
        }
        populateForm(request, goodsReceiptView);
        request.getRequestDispatcher("/CreateGoodsReceipt.jsp").forward(request, response);
    }

    private void populateForm(HttpServletRequest request, GoodsReceiptView goodsReceiptView) {
        request.setAttribute("goodsReceiptView", goodsReceiptView);
        request.setAttribute("receipt", goodsReceiptView.getReceipt());
        request.setAttribute("receiptDetails", goodsReceiptView.getReceiptDetails());
        request.setAttribute("availableLocations", goodsReceiptView.getAvailableLocations());
        request.setAttribute("classificationValid", goodsReceiptView.isClassificationValid());
        request.setAttribute("classificationIssues", goodsReceiptView.getClassificationIssues());
        request.setAttribute("hasDamagedQuantity", goodsReceiptView.isHasDamagedQuantity());
        request.setAttribute("quarantineLocation", goodsReceiptView.getQuarantineLocation());
        request.setAttribute("today", goodsReceiptView.getToday());
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
