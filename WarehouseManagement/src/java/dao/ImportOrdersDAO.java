package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.ManagerApprovalOrder;
import dao.OrderRejectionNotificationsDAO;
import model.ImportOrders;
import utils.DbContext;

public class ImportOrdersDAO extends DbContext {

    public List<ManagerApprovalOrder> getPendingOrdersForApproval() {
        List<ManagerApprovalOrder> orders = new ArrayList<>();
        String sql = "SELECT io.ImportOrderId, io.OrderNumber, io.OrderDate, s.SupplierName, u.FullName, "
                + "CONVERT(VARCHAR(10), io.ExpectedDate, 23) AS Details, io.Status "
                + "FROM ImportOrders io JOIN Suppliers s ON s.SupplierId = io.SupplierId "
                + "JOIN Users u ON u.UserId = io.CreatedBy WHERE io.Status = 'Pending' "
                + "ORDER BY io.OrderDate DESC, io.ImportOrderId DESC";
        if (connection == null) {
            return orders;
        }
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(new ManagerApprovalOrder(rs.getInt("ImportOrderId"), rs.getString("OrderNumber"),
                        rs.getTimestamp("OrderDate"), rs.getString("SupplierName"), rs.getString("FullName"),
                        "Expected: " + rs.getString("Details"), rs.getString("Status")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return orders;
    }

    public boolean completePendingOrder(int importOrderId, int acceptedBy) {
        if (connection == null || importOrderId <= 0 || acceptedBy <= 0) {
            return false;
        }

        boolean previousAutoCommit = true;
        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            String orderNumber;
            String selectSql = "SELECT OrderNumber FROM ImportOrders WITH (UPDLOCK, ROWLOCK) "
                    + "WHERE ImportOrderId = ? AND Status = 'Pending'";
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setInt(1, importOrderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    orderNumber = rs.getString("OrderNumber");
                }
            }

            String updateSql = "UPDATE ImportOrders SET Status = 'Completed' "
                    + "WHERE ImportOrderId = ? AND Status = 'Pending'";
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, importOrderId);
                if (ps.executeUpdate() != 1 || !OrderRejectionNotificationsDAO.insertNotificationsForRole(connection,
                        "Purchase Staff", acceptedBy, "Accepted", "Import Order", importOrderId, orderNumber)) {
                    connection.rollback();
                    return false;
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

    public boolean rejectPendingOrder(int importOrderId, int rejectedBy) {
        if (connection == null || importOrderId <= 0 || rejectedBy <= 0) {
            return false;
        }

        boolean previousAutoCommit = true;
        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            String orderNumber;
                String selectSql = "SELECT OrderNumber FROM ImportOrders WITH (UPDLOCK, ROWLOCK) "
                    + "WHERE ImportOrderId = ? AND Status = 'Pending'";
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setInt(1, importOrderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }
                    orderNumber = rs.getString("OrderNumber");
                }
            }

            String updateSql = "UPDATE ImportOrders SET Status = 'Rejected' "
                    + "WHERE ImportOrderId = ? AND Status = 'Pending'";
            try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                ps.setInt(1, importOrderId);
                if (ps.executeUpdate() != 1 || !OrderRejectionNotificationsDAO.insertNotificationsForRole(connection,
                        "Purchase Staff", rejectedBy, "Rejected", "Import Order", importOrderId, orderNumber)) {
                    connection.rollback();
                    return false;
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

    public ImportOrders getImportOrderById(int importOrderId) {
        String sql = "SELECT ImportOrderId, SupplierId, CreatedBy, OrderNumber, OrderDate, ExpectedDate, Status "
                + "FROM ImportOrders WHERE ImportOrderId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, importOrderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ImportOrders o = new ImportOrders();
                o.setImportOrderId(rs.getInt("ImportOrderId"));
                o.setSupplierId(rs.getInt("SupplierId"));
                o.setCreatedBy(rs.getInt("CreatedBy"));
                o.setOrderNumber(rs.getString("OrderNumber"));
                o.setOrderDate(rs.getTimestamp("OrderDate"));
                o.setExpectedDate(rs.getTimestamp("ExpectedDate"));
                o.setStatus(rs.getString("Status"));
                return o;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ImportOrders getImportOrderByOrderNumber(String orderNumber) {
        String sql = "SELECT ImportOrderId, SupplierId, CreatedBy, OrderNumber, OrderDate, ExpectedDate, Status "
                + "FROM ImportOrders WHERE OrderNumber = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, orderNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ImportOrders o = new ImportOrders();
                o.setImportOrderId(rs.getInt("ImportOrderId"));
                o.setSupplierId(rs.getInt("SupplierId"));
                o.setCreatedBy(rs.getInt("CreatedBy"));
                o.setOrderNumber(rs.getString("OrderNumber"));
                o.setOrderDate(rs.getTimestamp("OrderDate"));
                o.setExpectedDate(rs.getTimestamp("ExpectedDate"));
                o.setStatus(rs.getString("Status"));
                return o;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
