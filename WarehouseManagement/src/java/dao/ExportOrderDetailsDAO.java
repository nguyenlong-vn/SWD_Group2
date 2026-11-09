package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.ExportOrderDetails;
import model.ManagerOrderDetail;
import utils.DbContext;

public class ExportOrderDetailsDAO extends DbContext {

	public static boolean saveOrderDetails(Connection connection, int exportOrderId, List<ExportOrderDetails> details)
			throws Exception {
		if (connection == null || exportOrderId <= 0 || details == null || details.isEmpty()) {
			return false;
		}
		String sql = "INSERT INTO ExportOrderDetails "
				+ "(ExportOrderId, ProductId, Quantity, Unit, UnitPrice) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			for (ExportOrderDetails detail : details) {
				ps.setInt(1, exportOrderId);
				ps.setInt(2, detail.getProductId());
				ps.setDouble(3, detail.getQuantity());
				ps.setString(4, detail.getUnit());
				ps.setDouble(5, detail.getUnitPrice());
				ps.addBatch();
			}
			int[] results = ps.executeBatch();
			for (int result : results) {
				if (result == Statement.EXECUTE_FAILED) {
					return false;
				}
			}
			return results.length == details.size();
		}
	}

	public boolean saveOrderDetails(int exportOrderId, List<ExportOrderDetails> orderDetails) {
		try {
			return saveOrderDetails(connection, exportOrderId, orderDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public List<ManagerOrderDetail> getDetailsByExportOrderId(int exportOrderId) {
		List<ManagerOrderDetail> details = new ArrayList<>();
		if (connection == null || exportOrderId <= 0) {
			return details;
		}
		String sql = "SELECT p.ProductName, d.Quantity, d.Unit, d.UnitPrice "
				+ "FROM ExportOrderDetails d JOIN Products p ON p.ProductId = d.ProductId "
				+ "WHERE d.ExportOrderId = ? ORDER BY d.ExportDetailId";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, exportOrderId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					details.add(new ManagerOrderDetail(rs.getString("ProductName"), rs.getDouble("Quantity"),
							rs.getString("Unit"), rs.getDouble("UnitPrice"), null));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return details;
	}
}
