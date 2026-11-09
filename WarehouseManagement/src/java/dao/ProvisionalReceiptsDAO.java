package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import model.PurchaseReceiptRow;
import utils.DbContext;

public class ProvisionalReceiptsDAO extends DbContext {

    public List<PurchaseReceiptRow> getPurchaseReceipts() {
        List<PurchaseReceiptRow> receipts = new ArrayList<>();
        String sql = "SELECT pr.ProvisionalReceiptId, pr.ImportOrderId, io.SupplierId, pr.ReceiptNumber, pr.ReceivedDate, "
            + "pr.Status, pr.QCStatus, io.OrderNumber, s.SupplierName, "
                + "CASE WHEN EXISTS (SELECT 1 FROM GoodsReceipts gr WHERE gr.ImportOrderId = pr.ImportOrderId) "
                + "THEN 1 ELSE 0 END AS GoodsReceiptExists, "
                + "CASE WHEN EXISTS (SELECT 1 FROM SupplierReturns sr WHERE sr.ImportOrderId = pr.ImportOrderId "
                + "AND sr.Status = 'Pending') THEN 1 ELSE 0 END AS SupplierReturnPending, "
                + "CASE WHEN EXISTS (SELECT 1 FROM SupplierReturns sr WHERE sr.ImportOrderId = pr.ImportOrderId "
                + "AND sr.Status = 'Completed') THEN 1 ELSE 0 END AS SupplierReturnCompleted, "
                + "CASE WHEN EXISTS (SELECT 1 FROM ProvisionalReceiptDetails d "
                + "WHERE d.ProvisionalReceiptId = pr.ProvisionalReceiptId AND d.Quantity - COALESCE(("
                + "SELECT SUM(srd.Quantity) FROM SupplierReturnDetails srd "
                + "JOIN SupplierReturns sr ON sr.ReturnId = srd.ReturnId "
                + "WHERE srd.ProvisionalDetailId = d.ProvisionalDetailId "
                + "AND sr.Status IN ('Pending', 'Completed')), 0) > 0.0005) "
                + "THEN 1 ELSE 0 END AS ReturnableQuantityAvailable "
                + "FROM ProvisionalReceipts pr JOIN ImportOrders io ON io.ImportOrderId = pr.ImportOrderId "
                + "JOIN Suppliers s ON s.SupplierId = io.SupplierId "
                + "WHERE pr.Status = 'Not Approved' AND pr.QCStatus IN ('Passed', 'Failed') "
                + "ORDER BY pr.ReceivedDate DESC, pr.ProvisionalReceiptId DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                receipts.add(mapPurchaseReceiptRow(rs));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return receipts;
    }

    public PurchaseReceiptRow getCompletedPurchaseReceipt(int provisionalReceiptId) {
        String sql = "SELECT pr.ProvisionalReceiptId, pr.ImportOrderId, io.SupplierId, pr.ReceiptNumber, pr.ReceivedDate, "
            + "pr.Status, pr.QCStatus, io.OrderNumber, s.SupplierName, "
                + "CASE WHEN EXISTS (SELECT 1 FROM GoodsReceipts gr WHERE gr.ImportOrderId = pr.ImportOrderId) "
                + "THEN 1 ELSE 0 END AS GoodsReceiptExists, "
                + "CASE WHEN EXISTS (SELECT 1 FROM SupplierReturns sr WHERE sr.ImportOrderId = pr.ImportOrderId "
                + "AND sr.Status = 'Pending') THEN 1 ELSE 0 END AS SupplierReturnPending, "
                + "CASE WHEN EXISTS (SELECT 1 FROM SupplierReturns sr WHERE sr.ImportOrderId = pr.ImportOrderId "
                + "AND sr.Status = 'Completed') THEN 1 ELSE 0 END AS SupplierReturnCompleted, "
                + "CASE WHEN EXISTS (SELECT 1 FROM ProvisionalReceiptDetails d "
                + "WHERE d.ProvisionalReceiptId = pr.ProvisionalReceiptId AND d.Quantity - COALESCE(("
                + "SELECT SUM(srd.Quantity) FROM SupplierReturnDetails srd "
                + "JOIN SupplierReturns sr ON sr.ReturnId = srd.ReturnId "
                + "WHERE srd.ProvisionalDetailId = d.ProvisionalDetailId "
                + "AND sr.Status IN ('Pending', 'Completed')), 0) > 0.0005) "
                + "THEN 1 ELSE 0 END AS ReturnableQuantityAvailable "
                + "FROM ProvisionalReceipts pr JOIN ImportOrders io ON io.ImportOrderId = pr.ImportOrderId "
                + "JOIN Suppliers s ON s.SupplierId = io.SupplierId "
                + "WHERE pr.Status IN ('Completed', 'Not Approved') AND pr.ProvisionalReceiptId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, provisionalReceiptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPurchaseReceiptRow(rs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private PurchaseReceiptRow mapPurchaseReceiptRow(ResultSet rs) throws Exception {
        PurchaseReceiptRow receipt = new PurchaseReceiptRow();
        receipt.setProvisionalReceiptId(rs.getInt("ProvisionalReceiptId"));
        receipt.setImportOrderId(rs.getInt("ImportOrderId"));
        receipt.setSupplierId(rs.getInt("SupplierId"));
        receipt.setReceiptNumber(rs.getString("ReceiptNumber"));
        receipt.setReceivedDate(rs.getTimestamp("ReceivedDate"));
        receipt.setQcStatus(rs.getString("QCStatus"));
        receipt.setImportOrderNumber(rs.getString("OrderNumber"));
        receipt.setSupplierName(rs.getString("SupplierName"));
        receipt.setGoodsReceiptExists(rs.getBoolean("GoodsReceiptExists"));
        receipt.setStatus(rs.getString("Status"));
        receipt.setSupplierReturnPending(rs.getBoolean("SupplierReturnPending"));
        receipt.setSupplierReturnCompleted(rs.getBoolean("SupplierReturnCompleted"));
        receipt.setReturnableQuantityAvailable(rs.getBoolean("ReturnableQuantityAvailable"));
        return receipt;
    }

    public List<ProvisionalReceipts> getAllProvisionalReceipts() {
        List<ProvisionalReceipts> receipts = new ArrayList<>();
        String sql = "SELECT ProvisionalReceiptId, ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, QCStatus, Notes "
            + "FROM ProvisionalReceipts WHERE Status = 'Not Approved' AND QCStatus = 'Pending' "
            + "ORDER BY ReceivedDate DESC, ProvisionalReceiptId DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                receipts.add(mapReceipt(rs));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return receipts;
    }

    public ProvisionalReceipts getProvisionalReceiptById(int provisionalReceiptId) {
        String sql = "SELECT ProvisionalReceiptId, ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, QCStatus, Notes "
                + "FROM ProvisionalReceipts WHERE ProvisionalReceiptId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, provisionalReceiptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReceipt(rs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private ProvisionalReceipts mapReceipt(ResultSet rs) throws Exception {
        ProvisionalReceipts receipt = new ProvisionalReceipts();
        receipt.setProvisionalReceiptId(rs.getInt("ProvisionalReceiptId"));
        receipt.setImportOrderId(rs.getInt("ImportOrderId"));
        receipt.setReceiptNumber(rs.getString("ReceiptNumber"));
        receipt.setReceivedDate(rs.getTimestamp("ReceivedDate"));
        receipt.setReceivedBy(rs.getInt("ReceivedBy"));
        receipt.setStatus(rs.getString("Status"));
        receipt.setQcStatus(rs.getString("QCStatus"));
        receipt.setNotes(rs.getString("Notes"));
        return receipt;
    }

    public boolean updateReceiptDetailsAndQuality(int provisionalReceiptId, int inspectedBy,
            List<ProvisionalReceiptDetails> details, String result, String inspectionNotes) {
        if (connection == null || details == null) {
            return false;
        }

        boolean previousAutoCommit = true;
        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            String deleteGrades = "DELETE FROM ProductDetailGrades "
                    + "WHERE ProvisionalDetailId = ? AND EXISTS ("
                    + "SELECT 1 FROM ProvisionalReceiptDetails "
                    + "WHERE ProvisionalDetailId = ? AND ProvisionalReceiptId = ?)";
            String insertGrade = "INSERT INTO ProductDetailGrades "
                    + "(ProvisionalDetailId, GradeType, Quantity) VALUES (?, ?, ?)";
            try (PreparedStatement deletePs = connection.prepareStatement(deleteGrades);
                    PreparedStatement insertPs = connection.prepareStatement(insertGrade)) {
                for (ProvisionalReceiptDetails detail : details) {
                    deletePs.setInt(1, detail.getProvisionalDetailId());
                    deletePs.setInt(2, detail.getProvisionalDetailId());
                    deletePs.setInt(3, provisionalReceiptId);
                    deletePs.executeUpdate();

                    insertGrade(insertPs, detail.getProvisionalDetailId(), "A", detail.getGradeAQuantity());
                    insertGrade(insertPs, detail.getProvisionalDetailId(), "B", detail.getGradeBQuantity());
                    insertGrade(insertPs, detail.getProvisionalDetailId(), "Damaged", detail.getDamagedQuantity());
                }
            }

                String updateReceipt = "UPDATE ProvisionalReceipts SET Status = 'Not Approved', QCStatus = ? "
                    + "WHERE ProvisionalReceiptId = ?";
            try (PreparedStatement ps = connection.prepareStatement(updateReceipt)) {
                ps.setString(1, result);
                ps.setInt(2, provisionalReceiptId);
                if (ps.executeUpdate() != 1) {
                    throw new IllegalArgumentException("Provisional receipt was not found.");
                }
            }

            int inspectionId = -1;
            String findInspection = "SELECT TOP 1 InspectionId FROM QualityInspections "
                    + "WHERE ProvisionalReceiptId = ? ORDER BY InspectionDate DESC, InspectionId DESC";
            try (PreparedStatement ps = connection.prepareStatement(findInspection)) {
                ps.setInt(1, provisionalReceiptId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        inspectionId = rs.getInt("InspectionId");
                    }
                }
            }

            if (inspectionId > 0) {
                String updateInspection = "UPDATE QualityInspections SET InspectedBy = ?, InspectionDate = ?, "
                        + "Result = ?, Notes = ? WHERE InspectionId = ?";
                try (PreparedStatement ps = connection.prepareStatement(updateInspection)) {
                    ps.setInt(1, inspectedBy);
                    ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    ps.setString(3, result);
                    ps.setString(4, inspectionNotes);
                    ps.setInt(5, inspectionId);
                    ps.executeUpdate();
                }
            } else {
                String insertInspection = "INSERT INTO QualityInspections "
                        + "(ProvisionalReceiptId, InspectedBy, InspectionDate, Result, Notes) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertInspection)) {
                    ps.setInt(1, provisionalReceiptId);
                    ps.setInt(2, inspectedBy);
                    ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    ps.setString(4, result);
                    ps.setString(5, inspectionNotes);
                    ps.executeUpdate();
                }
            }

            connection.commit();
            return true;
        } catch (Exception ex) {
            try {
                connection.rollback();
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }
            ex.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void insertGrade(PreparedStatement ps, int provisionalDetailId, String gradeType, double quantity)
            throws Exception {
        ps.setInt(1, provisionalDetailId);
        ps.setString(2, gradeType);
        ps.setDouble(3, quantity);
        ps.executeUpdate();
    }

    public int saveProvisionalReceipt(ProvisionalReceipts receipt, List<ProvisionalReceiptDetails> details) {
        String insertReceipt = "INSERT INTO ProvisionalReceipts (ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, QCStatus, Notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertDetail = "INSERT INTO ProvisionalReceiptDetails (ProvisionalReceiptId, ProductId, Quantity, Unit) "
                + "VALUES (?, ?, ?, ?)";
        try {
            connection.setAutoCommit(false);

            PreparedStatement psReceipt = connection.prepareStatement(insertReceipt, Statement.RETURN_GENERATED_KEYS);
            psReceipt.setInt(1, receipt.getImportOrderId());
            psReceipt.setString(2, receipt.getReceiptNumber());
            psReceipt.setTimestamp(3, receipt.getReceivedDate());
            psReceipt.setInt(4, receipt.getReceivedBy());
            psReceipt.setString(5, receipt.getStatus());
            psReceipt.setString(6, receipt.getQcStatus());
            psReceipt.setString(7, receipt.getNotes());
            psReceipt.executeUpdate();

            int receiptId = -1;
            ResultSet rs = psReceipt.getGeneratedKeys();
            if (rs.next()) {
                receiptId = rs.getInt(1);
            }
            rs.close();
            psReceipt.close();

            if (receiptId <= 0) {
                connection.rollback();
                return -1;
            }

            PreparedStatement psDetail = connection.prepareStatement(insertDetail);
            for (ProvisionalReceiptDetails d : details) {
                psDetail.setInt(1, receiptId);
                psDetail.setInt(2, d.getProductId());
                psDetail.setDouble(3, d.getQuantity());
                psDetail.setString(4, d.getUnit());
                psDetail.addBatch();
            }
            psDetail.executeBatch();
            psDetail.close();

            connection.commit();
            return receiptId;
        } catch (Exception ex) {
            try {
                connection.rollback();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
            return -1;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
