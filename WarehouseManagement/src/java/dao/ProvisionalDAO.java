package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.ProvisionalReceiptDetails;
import model.ProvisionalReceipts;
import utils.DbContext;

public class ProvisionalDAO extends DbContext {

    public ProvisionalReceipts getProvisionalReceiptQuantity(int provisionalReceiptId) {
        String sql = "SELECT ProvisionalReceiptId, ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, QCStatus, Notes "
                + "FROM ProvisionalReceipts WHERE ProvisionalReceiptId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, provisionalReceiptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ProvisionalReceipts getProvisionalReceiptByImportOrder(int importOrderId) {
        String sql = "SELECT TOP 1 ProvisionalReceiptId, ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, QCStatus, Notes "
                + "FROM ProvisionalReceipts WHERE ImportOrderId = ? ORDER BY ProvisionalReceiptId DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ProvisionalReceiptDetails> getProvisionalReceiptDetails(int importOrderId) {
        List<ProvisionalReceiptDetails> details = new ArrayList<>();
        String sql = "SELECT d.ProvisionalDetailId, d.ProvisionalReceiptId, d.ProductId, p.ProductName, d.Quantity, d.Unit, "
                + "COALESCE(SUM(CASE WHEN g.GradeType = 'A' THEN g.Quantity ELSE 0 END), 0) AS GradeAQuantity, "
                + "COALESCE(SUM(CASE WHEN g.GradeType = 'B' THEN g.Quantity ELSE 0 END), 0) AS GradeBQuantity, "
                + "COALESCE(SUM(CASE WHEN g.GradeType = 'Damaged' THEN g.Quantity ELSE 0 END), 0) AS DamagedQuantity "
                + "FROM ProvisionalReceipts pr "
                + "JOIN ProvisionalReceiptDetails d ON d.ProvisionalReceiptId = pr.ProvisionalReceiptId "
                + "JOIN Products p ON p.ProductId = d.ProductId "
                + "LEFT JOIN ProductDetailGrades g ON g.ProvisionalDetailId = d.ProvisionalDetailId "
                + "WHERE pr.ImportOrderId = ? "
                + "GROUP BY d.ProvisionalDetailId, d.ProvisionalReceiptId, d.ProductId, p.ProductName, d.Quantity, d.Unit "
                + "ORDER BY d.ProvisionalDetailId";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProvisionalReceiptDetails detail = new ProvisionalReceiptDetails();
                    detail.setProvisionalDetailId(rs.getInt("ProvisionalDetailId"));
                    detail.setProvisionalReceiptId(rs.getInt("ProvisionalReceiptId"));
                    detail.setProductId(rs.getInt("ProductId"));
                    detail.setProductName(rs.getString("ProductName"));
                    detail.setQuantity(rs.getDouble("Quantity"));
                    detail.setUnit(rs.getString("Unit"));
                    detail.setGradeAQuantity(rs.getDouble("GradeAQuantity"));
                    detail.setGradeBQuantity(rs.getDouble("GradeBQuantity"));
                    detail.setDamagedQuantity(rs.getDouble("DamagedQuantity"));
                    details.add(detail);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return details;
    }

    public boolean saveProvisionalReceipt(int provisionalReceiptId, List<ProvisionalReceiptDetails> itemDetails,
            String checkStatus, String notes, int inspectedBy) {
        return new ProvisionalReceiptsDAO().updateReceiptDetailsAndQuality(
                provisionalReceiptId, inspectedBy, itemDetails, checkStatus, notes);
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
