/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.ProvisionalReceiptDetails;
import utils.DbContext;

public class ProvisionalReceiptDetailsDAO extends DbContext {
	public List<ProvisionalReceiptDetails> getDetailsByReceiptId(int provisionalReceiptId) {
		List<ProvisionalReceiptDetails> details = new ArrayList<>();
		String sql = "SELECT d.ProvisionalDetailId, d.ProvisionalReceiptId, d.ProductId, p.ProductName, d.Quantity, d.Unit, "
				+ "COALESCE(SUM(CASE WHEN g.GradeType = 'A' THEN g.Quantity ELSE 0 END), 0) AS GradeAQuantity, "
				+ "COALESCE(SUM(CASE WHEN g.GradeType = 'B' THEN g.Quantity ELSE 0 END), 0) AS GradeBQuantity, "
				+ "COALESCE(SUM(CASE WHEN g.GradeType = 'Damaged' THEN g.Quantity ELSE 0 END), 0) AS DamagedQuantity, "
				+ "d.Quantity - COALESCE((SELECT SUM(srd.Quantity) FROM SupplierReturnDetails srd "
				+ "JOIN SupplierReturns sr ON sr.ReturnId = srd.ReturnId "
				+ "WHERE srd.ProvisionalDetailId = d.ProvisionalDetailId "
				+ "AND sr.Status IN ('Pending', 'Completed')), 0) AS RemainingReturnQuantity "
				+ "FROM ProvisionalReceiptDetails d JOIN Products p ON p.ProductId = d.ProductId "
				+ "LEFT JOIN ProductDetailGrades g ON g.ProvisionalDetailId = d.ProvisionalDetailId "
				+ "WHERE d.ProvisionalReceiptId = ? "
				+ "GROUP BY d.ProvisionalDetailId, d.ProvisionalReceiptId, d.ProductId, p.ProductName, d.Quantity, d.Unit "
				+ "ORDER BY d.ProvisionalDetailId";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, provisionalReceiptId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					ProvisionalReceiptDetails detail = new ProvisionalReceiptDetails();
					detail.setProvisionalDetailId(rs.getInt("ProvisionalDetailId"));
					detail.setProvisionalReceiptId(rs.getInt("ProvisionalReceiptId"));
					detail.setProductId(rs.getInt("ProductId"));
					detail.setProductName(rs.getString("ProductName"));
					detail.setQuantity(rs.getDouble("Quantity"));
					detail.setGradeAQuantity(rs.getDouble("GradeAQuantity"));
					detail.setGradeBQuantity(rs.getDouble("GradeBQuantity"));
					detail.setDamagedQuantity(rs.getDouble("DamagedQuantity"));
					detail.setRemainingReturnQuantity(Math.max(0, rs.getDouble("RemainingReturnQuantity")));
					detail.setUnit(rs.getString("Unit"));
					details.add(detail);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return details;
	}
}
