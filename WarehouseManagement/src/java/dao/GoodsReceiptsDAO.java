/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import model.GoodsReceiptEntry;
import utils.DbContext;

public class GoodsReceiptsDAO extends DbContext {

    public boolean hasGoodsReceiptForImportOrder(int importOrderId) {
        String sql = "SELECT 1 FROM GoodsReceipts WHERE ImportOrderId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateReceiptStatus(int receiptId, String status, int approverId) {
        if (receiptId <= 0 || approverId <= 0 || status == null) {
            return false;
        }
        ImportOrdersDAO importOrdersDAO = new ImportOrdersDAO();
        if ("APPROVED".equalsIgnoreCase(status)) {
            return importOrdersDAO.completePendingOrder(receiptId, approverId);
        }
        if ("REJECTED".equalsIgnoreCase(status)) {
            return importOrdersDAO.rejectPendingOrder(receiptId, approverId);
        }
        return false;
    }

        public int createGoodsReceipt(int importOrderId, int provisionalReceiptId, String receiptNumber,
            int receivedBy, String notes, List<GoodsReceiptEntry> entries) {
        if (connection == null || entries == null || entries.isEmpty()) {
            return -1;
        }

        boolean previousAutoCommit = true;
        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            String insertReceipt = "INSERT INTO GoodsReceipts "
                    + "(ImportOrderId, ReceiptNumber, ReceivedDate, ReceivedBy, Status, Notes) "
                    + "VALUES (?, ?, ?, ?, 'Completed', ?)";
            int receiptId;
            try (PreparedStatement ps = connection.prepareStatement(insertReceipt, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, importOrderId);
                ps.setString(2, receiptNumber);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setInt(4, receivedBy);
                ps.setString(5, notes);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        connection.rollback();
                        return -1;
                    }
                    receiptId = keys.getInt(1);
                }
            }

            String insertBatch = "INSERT INTO InventoryBatches "
                    + "(ProductId, LocationId, BatchCode, ReceivedDate, ExpiryDate, InitialQty, CurrentQty, "
                    + "ReservedQty, AvailableQty, UnitCost, QualityGrade, Status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?, ?, ?, ?)";
            String insertDetail = "INSERT INTO GoodsReceiptDetails "
                    + "(ReceiptId, ProductId, BatchId, Quantity, Unit, UnitCost) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement batchPs = connection.prepareStatement(insertBatch, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement detailPs = connection.prepareStatement(insertDetail)) {
                for (GoodsReceiptEntry entry : entries) {
                    batchPs.setInt(1, entry.getProductId());
                    batchPs.setInt(2, entry.getLocationId());
                    batchPs.setString(3, entry.getBatchCode());
                    batchPs.setTimestamp(4, entry.getReceivedDate());
                    batchPs.setTimestamp(5, entry.getExpiryDate());
                    batchPs.setDouble(6, entry.getQuantity());
                    batchPs.setDouble(7, entry.getQuantity());
                    batchPs.setDouble(8, "Quarantine".equals(entry.getStatus()) ? 0 : entry.getQuantity());
                    batchPs.setDouble(9, entry.getUnitCost());
                    batchPs.setString(10, entry.getQualityGrade());
                    batchPs.setString(11, entry.getStatus());
                    batchPs.executeUpdate();

                    int batchId;
                    try (ResultSet keys = batchPs.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new IllegalStateException("Inventory batch was not created.");
                        }
                        batchId = keys.getInt(1);
                    }

                    detailPs.setInt(1, receiptId);
                    detailPs.setInt(2, entry.getProductId());
                    detailPs.setInt(3, batchId);
                    detailPs.setDouble(4, entry.getQuantity());
                    detailPs.setString(5, entry.getUnit());
                    detailPs.setDouble(6, entry.getUnitCost());
                    detailPs.executeUpdate();
                }
            }

            String completeReceipt = "UPDATE ProvisionalReceipts SET Status = 'Completed' "
                    + "WHERE ProvisionalReceiptId = ? AND ImportOrderId = ? AND Status = 'Not Approved'";
            try (PreparedStatement ps = connection.prepareStatement(completeReceipt)) {
                ps.setInt(1, provisionalReceiptId);
                ps.setInt(2, importOrderId);
                if (ps.executeUpdate() != 1) {
                    connection.rollback();
                    return -1;
                }
            }

            connection.commit();
            return receiptId;
        } catch (Exception ex) {
            try {
                connection.rollback();
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }
            ex.printStackTrace();
            return -1;
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
