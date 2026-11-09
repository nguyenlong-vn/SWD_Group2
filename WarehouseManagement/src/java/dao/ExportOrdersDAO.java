package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.ExportOrderDetails;
import model.ExportOrders;
import model.ManagerApprovalOrder;
import dao.OrderRejectionNotificationsDAO;
import utils.DbContext;

public class ExportOrdersDAO extends DbContext {

	public int createOutboundOrder(ExportOrders order, List<ExportOrderDetails> details) {
		if (connection == null || order == null || details == null || details.isEmpty()) {
			return -1;
		}

		boolean previousAutoCommit = true;
		try {
			previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);

			String sql = "INSERT INTO ExportOrders (CustomerId, OrderNumber, OrderDate, Status, CreatedBy) "
					+ "VALUES (?, ?, ?, 'Pending', ?)";
			int exportOrderId;
			try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, order.getCustomerId());
				ps.setString(2, order.getOrderNumber());
				ps.setTimestamp(3, order.getOrderDate());
				ps.setInt(4, order.getCreatedBy());
				if (ps.executeUpdate() != 1) {
					connection.rollback();
					return -1;
				}
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (!keys.next()) {
						connection.rollback();
						return -1;
					}
					exportOrderId = keys.getInt(1);
				}
			}

			if (!ExportOrderDetailsDAO.saveOrderDetails(connection, exportOrderId, details)) {
				connection.rollback();
				return -1;
			}
			connection.commit();
			return exportOrderId;
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

	public int saveOutboundOrder(ExportOrders orderData) {
		if (connection == null || orderData == null) {
			return -1;
		}

		String sql = "INSERT INTO ExportOrders (CustomerId, OrderNumber, OrderDate, Status, CreatedBy) "
				+ "VALUES (?, ?, ?, 'Pending', ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, orderData.getCustomerId());
			ps.setString(2, orderData.getOrderNumber());
			ps.setTimestamp(3, orderData.getOrderDate());
			ps.setInt(4, orderData.getCreatedBy());
			if (ps.executeUpdate() != 1) {
				return -1;
			}
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					return keys.getInt(1);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	public boolean updateStatus(int orderId, String status, int approverId) {
		if (orderId <= 0 || approverId <= 0 || status == null) {
			return false;
		}
		if ("APPROVED".equalsIgnoreCase(status)) {
			return completePendingOrder(orderId, approverId);
		}
		if ("REJECTED".equalsIgnoreCase(status)) {
			return rejectPendingOrder(orderId, approverId);
		}
		return false;
	}

	public List<ManagerApprovalOrder> getPendingOrdersForApproval() {
		List<ManagerApprovalOrder> orders = new ArrayList<>();
		String sql = "SELECT eo.ExportOrderId, eo.OrderNumber, eo.OrderDate, c.CustomerName, u.FullName, eo.Status "
				+ "FROM ExportOrders eo JOIN Customers c ON c.CustomerId = eo.CustomerId "
				+ "JOIN Users u ON u.UserId = eo.CreatedBy WHERE eo.Status = 'Pending' "
				+ "ORDER BY eo.OrderDate DESC, eo.ExportOrderId DESC";
		if (connection == null) {
			return orders;
		}
		try (PreparedStatement ps = connection.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				orders.add(new ManagerApprovalOrder(rs.getInt("ExportOrderId"), rs.getString("OrderNumber"),
						rs.getTimestamp("OrderDate"), rs.getString("CustomerName"), rs.getString("FullName"),
						"-", rs.getString("Status")));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return orders;
	}

	public boolean completePendingOrder(int exportOrderId, int acceptedBy) {
		if (connection == null || exportOrderId <= 0 || acceptedBy <= 0) {
			return false;
		}

		boolean previousAutoCommit = true;
		try {
			previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			String orderNumber;
			String selectSql = "SELECT OrderNumber FROM ExportOrders WITH (UPDLOCK, ROWLOCK) "
					+ "WHERE ExportOrderId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
				ps.setInt(1, exportOrderId);
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						connection.rollback();
						return false;
					}
					orderNumber = rs.getString("OrderNumber");
				}
			}

			String updateSql = "UPDATE ExportOrders SET Status = 'Completed' "
					+ "WHERE ExportOrderId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
				ps.setInt(1, exportOrderId);
				if (ps.executeUpdate() != 1 || !OrderRejectionNotificationsDAO.insertNotificationsForRole(connection,
						"Sale Staff", acceptedBy, "Accepted", "Export Order", exportOrderId, orderNumber)) {
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

	public boolean rejectPendingOrder(int exportOrderId, int rejectedBy) {
		if (connection == null || exportOrderId <= 0 || rejectedBy <= 0) {
			return false;
		}

		boolean previousAutoCommit = true;
		try {
			previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			String orderNumber;
			String selectSql = "SELECT OrderNumber FROM ExportOrders WITH (UPDLOCK, ROWLOCK) "
					+ "WHERE ExportOrderId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
				ps.setInt(1, exportOrderId);
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						connection.rollback();
						return false;
					}
					orderNumber = rs.getString("OrderNumber");
				}
			}

			String updateSql = "UPDATE ExportOrders SET Status = 'Rejected' "
					+ "WHERE ExportOrderId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
				ps.setInt(1, exportOrderId);
				if (ps.executeUpdate() != 1 || !OrderRejectionNotificationsDAO.insertNotificationsForRole(connection,
						"Sale Staff", rejectedBy, "Rejected", "Export Order", exportOrderId, orderNumber)) {
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
}
