package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Suppliers;
import utils.DbContext;

public class SuppliersDAO extends DbContext {

    public Suppliers getSupplierById(int supplierId) {
        String sql = "SELECT SupplierId, SupplierCode, SupplierName, Phone, Email, Address, Status "
                + "FROM Suppliers WHERE SupplierId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Suppliers s = new Suppliers();
                s.setSupplierId(rs.getInt("SupplierId"));
                s.setSupplierCode(rs.getString("SupplierCode"));
                s.setSupplierName(rs.getString("SupplierName"));
                s.setPhone(rs.getString("Phone"));
                s.setEmail(rs.getString("Email"));
                s.setAddress(rs.getString("Address"));
                s.setStatus(rs.getString("Status"));
                return s;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
