package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.InventoryBatches;
import utils.DbContext;

public class InventoryBatchesDAO extends DbContext {

	public List<InventoryBatches> getByReturnId(int returnId) {
		return new ArrayList<>();
	}

	public boolean updateStatus(int batchId, String status) {
		return true;
	}

	public double getAvailableQuantity(int productId) {
		if (connection == null || productId <= 0) {
			return 0;
		}
		String sql = "SELECT COALESCE(SUM(AvailableQty), 0) AS AvailableQuantity "
				+ "FROM InventoryBatches WHERE ProductId = ? AND Status = 'Available'";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, productId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() ? rs.getDouble("AvailableQuantity") : 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
}
