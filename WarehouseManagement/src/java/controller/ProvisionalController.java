package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.ImportOrders;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.Users;
import service.ProvisionalService;
import view.ProvisionalView;

@WebServlet(name = "ProvisionalController", urlPatterns = {"/ProvisionalController"})
public class ProvisionalController extends HttpServlet {

    private final ProvisionalService provisionalService = new ProvisionalService();

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
            case "getImportOrder":
                handleGetImportOrder(request, response);
                break;
            case "createProvisionalReceipt":
                handleCreateProvisionalReceipt(request, response);
                break;
            case "listProvisionalReceipts":
                handleListProvisionalReceipts(request, response);
                break;
            case "editProvisionalReceipt":
                handleEditProvisionalReceipt(request, response);
                break;
            case "updateProvisionalReceipt":
                handleUpdateProvisionalReceipt(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/CreateProvisionalReceipt.jsp");
                break;
        }
    }

    private void handleListProvisionalReceipts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("provisionalReceipts", provisionalService.getAllProvisionalReceipts());
        request.getRequestDispatcher("/SubmitProvisionalReceiptCheck.jsp").forward(request, response);
    }

    private void handleEditProvisionalReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int provisionalReceiptId = parseReceiptId(request.getParameter("provisionalReceiptId"));
        ProvisionalView detail = (provisionalReceiptId > 0) ? getProvisionalDetail(provisionalReceiptId) : null;
        if (detail == null || detail.getReceipt() == null) {
            response.sendRedirect(request.getContextPath() + "/ProvisionalController?action=listProvisionalReceipts");
            return;
        }
        populateEditView(request, detail);
        request.getRequestDispatcher("/EditProvisionalReceipt.jsp").forward(request, response);
    }

    private void handleUpdateProvisionalReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int provisionalReceiptId = parseReceiptId(request.getParameter("provisionalReceiptId"));
        Users user = (Users) request.getSession().getAttribute("user");
        String[] detailIds = request.getParameterValues("detailId");
        String[] gradeAQuantities = request.getParameterValues("gradeAQuantity");
        String[] gradeBQuantities = request.getParameterValues("gradeBQuantity");
        String[] damagedQuantities = request.getParameterValues("damagedQuantity");
        List<ProvisionalReceiptDetails> details = new ArrayList<>();

        if (detailIds == null || gradeAQuantities == null || gradeBQuantities == null || damagedQuantities == null
                || detailIds.length != gradeAQuantities.length || detailIds.length != gradeBQuantities.length
                || detailIds.length != damagedQuantities.length) {
            request.setAttribute("error", "Dữ liệu phân loại số lượng không hợp lệ.");
        } else {
            try {
                for (int i = 0; i < detailIds.length; i++) {
                    ProvisionalReceiptDetails detail = new ProvisionalReceiptDetails();
                    detail.setProvisionalDetailId(Integer.parseInt(detailIds[i]));
                    detail.setProvisionalReceiptId(provisionalReceiptId);
                    detail.setGradeAQuantity(Double.parseDouble(gradeAQuantities[i]));
                    detail.setGradeBQuantity(Double.parseDouble(gradeBQuantities[i]));
                    detail.setDamagedQuantity(Double.parseDouble(damagedQuantities[i]));
                    details.add(detail);
                }
            } catch (NumberFormatException ex) {
                request.setAttribute("error", "Số cân từng loại phải là số hợp lệ.");
            }
        }

        String result = request.getParameter("result");
        String inspectionNotes = request.getParameter("inspectionNotes");
        int inspectedBy = (user == null) ? 0 : user.getUserId();
        if (request.getAttribute("error") == null
                && updateProvisionalReceipt(provisionalReceiptId, details, result, inspectionNotes, inspectedBy)) {
            request.getSession().setAttribute("success", "Đã cập nhật phiếu "
                    + provisionalService.getProvisionalReceiptById(provisionalReceiptId).getReceiptNumber() + ".");
            response.sendRedirect(request.getContextPath() + "/ProvisionalController?action=listProvisionalReceipts");
            return;
        }

        if (request.getAttribute("error") == null) {
            request.setAttribute("error", "Không thể cập nhật phiếu. Vui lòng kiểm tra lại dữ liệu.");
        }
        ProvisionalView detail = (provisionalReceiptId > 0) ? getProvisionalDetail(provisionalReceiptId) : null;
        if (detail == null || detail.getReceipt() == null) {
            response.sendRedirect(request.getContextPath() + "/ProvisionalController?action=listProvisionalReceipts");
            return;
        }
        populateEditView(request, detail);
        request.getRequestDispatcher("/EditProvisionalReceipt.jsp").forward(request, response);
    }

    private int parseReceiptId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void populateEditView(HttpServletRequest request, ProvisionalView detail) {
        request.setAttribute("receipt", detail.getReceipt());
        request.setAttribute("receiptDetails", detail.getReceiptDetails());
        request.setAttribute("inspection", detail.getInspection());
    }

    public ProvisionalView getProvisionalDetail(int provisionalReceiptId) {
        ProvisionalReceipts receipt = provisionalService.getProvisionalReceiptQuantity(provisionalReceiptId);
        if (receipt == null) {
            return null;
        }
        ProvisionalView detail = new ProvisionalView();
        detail.setReceipt(receipt);
        detail.setReceiptDetails(provisionalService.getReceiptDetails(provisionalReceiptId));
        detail.setInspection(provisionalService.getLatestInspection(provisionalReceiptId));

        ImportOrders importOrder = provisionalService.getImportOrderById(receipt.getImportOrderId());
        if (importOrder != null) {
            detail.setImportOrder(importOrder);
            detail.setOrderDetails(importOrder.getDetails());
            detail.setSupplier(provisionalService.getSupplierById(importOrder.getSupplierId()));
        }
        return detail;
    }

    public boolean updateProvisionalReceipt(int provisionalReceiptId, List<ProvisionalReceiptDetails> itemDetails,
            String checkStatus, String notes, int inspectedBy) {
        return provisionalService.updateProvisionalReceipt(
                provisionalReceiptId, inspectedBy, itemDetails, checkStatus, notes);
    }

    public ProvisionalView getImportOrder(String importOrderId) {
        return provisionalService.getImportOrder(importOrderId);
    }

    public String createProvisionalReceipt(int importOrderId, int receivedBy, String notes,
            List<ProvisionalReceiptDetails> detailItems) {
        return provisionalService.createProvisionalReceipt(importOrderId, receivedBy, notes, detailItems);
    }

    private void handleGetImportOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String importOrderId = request.getParameter("orderCode");
        ProvisionalView provisionalView = getImportOrder(importOrderId);

        if (provisionalView == null) {
            request.setAttribute("error", "Mã đơn nhập không hợp lệ hoặc không tồn tại.");
            request.getRequestDispatcher("/CreateProvisionalReceipt.jsp").forward(request, response);
            return;
        }

        request.setAttribute("provisionalView", provisionalView);
        request.getRequestDispatcher("/CreateProvisionalReceiptDetail.jsp").forward(request, response);
    }

    private void handleCreateProvisionalReceipt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Users user = (Users) session.getAttribute("user");
        int receivedBy = (user != null) ? user.getUserId() : 0;

        int importOrderId = Integer.parseInt(request.getParameter("importOrderId"));
        String notes = request.getParameter("notes");

        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        String[] units = request.getParameterValues("unit");

        List<ProvisionalReceiptDetails> detailItems = new ArrayList<>();
        if (productIds != null) {
            for (int i = 0; i < productIds.length; i++) {
                ProvisionalReceiptDetails d = new ProvisionalReceiptDetails();
                d.setProductId(Integer.parseInt(productIds[i]));
                d.setQuantity(Double.parseDouble(quantities[i]));
                d.setUnit(units[i]);
                detailItems.add(d);
            }
        }

        String receiptNumber = createProvisionalReceipt(importOrderId, receivedBy, notes, detailItems);

        if (receiptNumber != null) {
            session.setAttribute("success", "Tạo Provisional Receipt thành công (Mã phiếu: " + receiptNumber + ").");
            response.sendRedirect(request.getContextPath() + "/WarehouseStaffDashboard.jsp");
        } else {
            request.setAttribute("error", "Tạo Provisional Receipt thất bại.");
            request.getRequestDispatcher("/CreateProvisionalReceipt.jsp").forward(request, response);
        }
    }
}
