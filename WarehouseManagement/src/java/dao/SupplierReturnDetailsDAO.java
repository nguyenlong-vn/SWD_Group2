package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.ManagerOrderDetail;
import utils.DbContext;

public class SupplierReturnDetailsDAO extends DbContext {

	public List<ManagerOrderDetail> getDetailsByReturnId(int returnId) {
		List<ManagerOrderDetail> details = new ArrayList<>();
		if (connection == null || returnId <= 0) {
			return details;
		}
		String sql = "SELECT p.ProductName, srd.Quantity, prd.Unit, srd.Reason "
				+ "FROM SupplierReturnDetails srd "
				+ "JOIN ProvisionalReceiptDetails prd ON prd.ProvisionalDetailId = srd.ProvisionalDetailId "
				+ "JOIN Products p ON p.ProductId = prd.ProductId "
				+ "WHERE srd.ReturnId = ? ORDER BY srd.ReturnDetailId";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, returnId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					details.add(new ManagerOrderDetail(rs.getString("ProductName"), rs.getDouble("Quantity"),
							rs.getString("Unit"), null, rs.getString("Reason")));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return details;
	}
}
