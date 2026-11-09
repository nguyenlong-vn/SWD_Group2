package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.ImportOrderDetails;
import utils.DbContext;

public class ImportOrderDetailsDAO extends DbContext {

    public List<ImportOrderDetails> getDetailsByImportOrderId(int importOrderId) {
        List<ImportOrderDetails> list = new ArrayList<>();
        String sql = "SELECT d.ImportDetailId, d.ImportOrderId, d.ProductId, d.Quantity, d.Unit, d.UnitPrice, "
                + "p.ProductName "
                + "FROM ImportOrderDetails d "
                + "LEFT JOIN Products p ON d.ProductId = p.ProductId "
                + "WHERE d.ImportOrderId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, importOrderId);
            ResultSet rs = ps.executeQuery();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
