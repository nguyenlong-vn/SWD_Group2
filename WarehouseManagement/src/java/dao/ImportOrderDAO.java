package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.ImportOrderDetails;
import model.ImportOrders;
import utils.DbContext;

public class ImportOrderDAO extends DbContext {

    public ImportOrders getImportOrderWithDetails(String importOrderId) {
        ImportOrders order = getImportOrderByOrderNumber(importOrderId);
        if (order == null) {
            return null;
        }
        order.setDetails(getDetailsByImportOrderId(order.getImportOrderId()));
        return order;
    }

    public ImportOrders getImportOrderById(int importOrderId) {
        String sql = "SELECT ImportOrderId, SupplierId, CreatedBy, OrderNumber, OrderDate, ExpectedDate, Status "
                + "FROM ImportOrders WHERE ImportOrderId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ImportOrders o = mapOrder(rs);
                    o.setDetails(getDetailsByImportOrderId(o.getImportOrderId()));
                    return o;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ImportOrderDetails> getImportOrderDetails(int importOrderId) {
        return getDetailsByImportOrderId(importOrderId);
    }

    public boolean updateImportOrder(int importOrderId, List<ImportOrderDetails> importOrderDetail) {
        if (connection == null || importOrderId <= 0 || importOrderDetail == null || importOrderDetail.isEmpty()) {
            return false;
        }
        String sql = "UPDATE ImportOrders SET Status = 'Completed' WHERE ImportOrderId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importOrderId);
            return ps.executeUpdate() >= 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private ImportOrders getImportOrderByOrderNumber(String orderNumber) {
        String sql = "SELECT ImportOrderId, SupplierId, CreatedBy, OrderNumber, OrderDate, ExpectedDate, Status "
                + "FROM ImportOrders WHERE OrderNumber = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private ImportOrders mapOrder(ResultSet rs) throws Exception {
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

    private List<ImportOrderDetails> getDetailsByImportOrderId(int importOrderId) {
        List<ImportOrderDetails> list = new ArrayList<>();
        String sql = "SELECT d.ImportDetailId, d.ImportOrderId, d.ProductId, d.Quantity, d.Unit, d.UnitPrice, "
                + "p.ProductName "
                + "FROM ImportOrderDetails d "
                + "LEFT JOIN Products p ON d.ProductId = p.ProductId "
                + "WHERE d.ImportOrderId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, importOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImportOrderDetails d = new ImportOrderDetails();
                    d.setImportDetailId(rs.getInt("ImportDetailId"));
                    d.setImportOrderId(rs.getInt("ImportOrderId"));
                    d.setProductId(rs.getInt("ProductId"));
                    d.setQuantity(rs.getDouble("Quantity"));
                    d.setUnit(rs.getString("Unit"));
                    d.setUnitPrice(rs.getDouble("UnitPrice"));
                    d.setProductName(rs.getString("ProductName"));
                    list.add(d);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
