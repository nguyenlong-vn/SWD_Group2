/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.ManagerApprovalOrder;
import model.ProvisionalReceiptDetails;
import dao.OrderRejectionNotificationsDAO;
import utils.DbContext;

public class SupplierReturnsDAO extends DbContext {

	public List<ManagerApprovalOrder> getPendingReturnsForApproval() {
		List<ManagerApprovalOrder> returns = new ArrayList<>();
		String sql = "SELECT sr.ReturnId, sr.ReturnNumber, sr.ReturnDate, s.SupplierName, u.FullName, "
				+ "CONCAT(io.OrderNumber, ' - ', sr.Reason) AS Details, sr.Status "
				+ "FROM SupplierReturns sr JOIN Suppliers s ON s.SupplierId = sr.SupplierId "
				+ "JOIN ImportOrders io ON io.ImportOrderId = sr.ImportOrderId "
				+ "JOIN Users u ON u.UserId = sr.CreatedBy WHERE sr.Status = 'Pending' "
				+ "ORDER BY sr.ReturnDate DESC, sr.ReturnId DESC";
		if (connection == null) {
			return returns;
		}
		try (PreparedStatement ps = connection.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				returns.add(new ManagerApprovalOrder(rs.getInt("ReturnId"), rs.getString("ReturnNumber"),
						rs.getTimestamp("ReturnDate"), rs.getString("SupplierName"), rs.getString("FullName"),
						rs.getString("Details"), rs.getString("Status")));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returns;
	}

	public boolean updateReturnStatus(int returnId, String status, int approverId) {
		if ("APPROVED".equalsIgnoreCase(status)) {
			return completePendingReturn(returnId, approverId);
		}
		if ("REJECTED".equalsIgnoreCase(status)) {
			return rejectPendingReturn(returnId, approverId);
		}
		return false;
	}

	public boolean completePendingReturn(int returnId, int acceptedBy) {
		if (connection == null || returnId <= 0 || acceptedBy <= 0) {
			return false;
		}

		boolean previousAutoCommit = true;
		try {
			previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			String returnNumber;
			String selectSql = "SELECT ReturnNumber FROM SupplierReturns WITH (UPDLOCK, ROWLOCK) "
					+ "WHERE ReturnId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
				ps.setInt(1, returnId);
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						connection.rollback();
						return false;
					}
					returnNumber = rs.getString("ReturnNumber");
				}
			}

			String updateSql = "UPDATE SupplierReturns SET Status = 'Completed' WHERE ReturnId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
				ps.setInt(1, returnId);
				if (ps.executeUpdate() != 1 || !OrderRejectionNotificationsDAO.insertNotificationsForRole(connection,
						"Purchase Staff", acceptedBy, "Accepted", "Supplier Return", returnId, returnNumber)) {
					connection.rollback();
					return false;
				}
			}
			String completeReceiptSql = "UPDATE pr SET Status = CASE WHEN EXISTS ("
					+ "SELECT 1 FROM SupplierReturnDetails otherDetails "
					+ "JOIN ProvisionalReceiptDetails otherReceiptDetails ON otherReceiptDetails.ProvisionalDetailId = otherDetails.ProvisionalDetailId "
					+ "JOIN SupplierReturns otherReturn ON otherReturn.ReturnId = otherDetails.ReturnId "
					+ "WHERE otherReceiptDetails.ProvisionalReceiptId = pr.ProvisionalReceiptId "
					+ "AND otherReturn.Status = 'Pending') THEN 'Awaiting Approval' ELSE 'Completed' END "
					+ "FROM ProvisionalReceipts pr WHERE pr.Status = 'Awaiting Approval' AND EXISTS ("
					+ "SELECT 1 FROM SupplierReturnDetails srd "
					+ "JOIN ProvisionalReceiptDetails d ON d.ProvisionalDetailId = srd.ProvisionalDetailId "
					+ "WHERE srd.ReturnId = ? AND d.ProvisionalReceiptId = pr.ProvisionalReceiptId)";
			try (PreparedStatement ps = connection.prepareStatement(completeReceiptSql)) {
				ps.setInt(1, returnId);
				if (ps.executeUpdate() == 0) {
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

	public boolean rejectPendingReturn(int returnId, int rejectedBy) {
		if (connection == null || returnId <= 0 || rejectedBy <= 0) {
			return false;
		}

		boolean previousAutoCommit = true;
		try {
			previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			String returnNumber;
			    String selectSql = "SELECT ReturnNumber FROM SupplierReturns WITH (UPDLOCK, ROWLOCK) "
					+ "WHERE ReturnId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
				ps.setInt(1, returnId);
				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						connection.rollback();
						return false;
					}
					returnNumber = rs.getString("ReturnNumber");
				}
			}

			String updateSql = "UPDATE SupplierReturns SET Status = 'Rejected' WHERE ReturnId = ? AND Status = 'Pending'";
			try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
				ps.setInt(1, returnId);
				if (ps.executeUpdate() != 1 || !OrderRejectionNotificationsDAO.insertNotificationsForRole(connection,
						"Purchase Staff", rejectedBy, "Rejected", "Supplier Return", returnId, returnNumber)) {
					connection.rollback();
					return false;
				}
			}
			String reopenReceiptSql = "UPDATE pr SET Status = CASE WHEN EXISTS ("
					+ "SELECT 1 FROM SupplierReturnDetails otherDetails "
					+ "JOIN ProvisionalReceiptDetails otherReceiptDetails ON otherReceiptDetails.ProvisionalDetailId = otherDetails.ProvisionalDetailId "
					+ "JOIN SupplierReturns otherReturn ON otherReturn.ReturnId = otherDetails.ReturnId "
					+ "WHERE otherReceiptDetails.ProvisionalReceiptId = pr.ProvisionalReceiptId "
					+ "AND otherReturn.Status = 'Pending') THEN 'Awaiting Approval' ELSE 'Not Approved' END "
					+ "FROM ProvisionalReceipts pr WHERE pr.Status = 'Awaiting Approval' AND EXISTS ("
					+ "SELECT 1 FROM SupplierReturnDetails srd "
					+ "JOIN ProvisionalReceiptDetails d ON d.ProvisionalDetailId = srd.ProvisionalDetailId "
					+ "WHERE srd.ReturnId = ? AND d.ProvisionalReceiptId = pr.ProvisionalReceiptId)";
			try (PreparedStatement ps = connection.prepareStatement(reopenReceiptSql)) {
				ps.setInt(1, returnId);
				if (ps.executeUpdate() == 0) {
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

	public int saveSupplierReturn(int supplierId, int importOrderId, String returnNumber,
			int createdBy, String reason, List<ProvisionalReceiptDetails> details) {
		if (connection == null || details == null || details.isEmpty()) {
			return -1;
		}

		boolean previousAutoCommit = true;
		try {
			previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);

			String remainingSql = "SELECT d.Quantity - COALESCE((SELECT SUM(srd.Quantity) "
					+ "FROM SupplierReturnDetails srd JOIN SupplierReturns sr ON sr.ReturnId = srd.ReturnId "
					+ "WHERE srd.ProvisionalDetailId = d.ProvisionalDetailId "
					+ "AND sr.Status IN ('Pending', 'Completed')), 0) AS Remaining "
					+ "FROM ProvisionalReceiptDetails d WITH (UPDLOCK, HOLDLOCK) "
					+ "JOIN ProvisionalReceipts pr ON pr.ProvisionalReceiptId = d.ProvisionalReceiptId "
					+ "WHERE d.ProvisionalDetailId = ? AND pr.ImportOrderId = ?";
			try (PreparedStatement ps = connection.prepareStatement(remainingSql)) {
				for (ProvisionalReceiptDetails detail : details) {
					ps.setInt(1, detail.getProvisionalDetailId());
					ps.setInt(2, importOrderId);
					try (ResultSet rs = ps.executeQuery()) {
						if (!rs.next() || detail.getQuantity() <= 0
								|| detail.getQuantity() - rs.getDouble("Remaining") > 0.0005) {
							throw new IllegalArgumentException("Return quantity exceeds the remaining received quantity.");
						}
					}
				}
			}

			String insertReturn = "INSERT INTO SupplierReturns "
					+ "(SupplierId, ImportOrderId, ReturnNumber, ReturnDate, Reason, Status, CreatedBy) "
					+ "VALUES (?, ?, ?, ?, ?, 'Pending', ?)";
			int returnId;
			try (PreparedStatement ps = connection.prepareStatement(insertReturn, Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, supplierId);
				ps.setInt(2, importOrderId);
				ps.setString(3, returnNumber);
				ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				ps.setString(5, reason);
				ps.setInt(6, createdBy);
				ps.executeUpdate();
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (!keys.next()) {
						connection.rollback();
						return -1;
					}
					returnId = keys.getInt(1);
				}
			}

			String insertDetail = "INSERT INTO SupplierReturnDetails "
					+ "(ReturnId, ProvisionalDetailId, Quantity, Reason) VALUES (?, ?, ?, ?)";
			try (PreparedStatement ps = connection.prepareStatement(insertDetail)) {
				for (ProvisionalReceiptDetails detail : details) {
					ps.setInt(1, returnId);
					ps.setInt(2, detail.getProvisionalDetailId());
					ps.setDouble(3, detail.getQuantity());
					ps.setString(4, reason);
					ps.addBatch();
				}
				ps.executeBatch();
			}

			String awaitApprovalSql = "UPDATE pr SET Status = 'Awaiting Approval' "
					+ "FROM ProvisionalReceipts pr WHERE pr.Status = 'Not Approved' AND pr.QCStatus = 'Failed' "
					+ "AND EXISTS (SELECT 1 FROM SupplierReturnDetails srd "
					+ "JOIN ProvisionalReceiptDetails d ON d.ProvisionalDetailId = srd.ProvisionalDetailId "
					+ "WHERE srd.ReturnId = ? AND d.ProvisionalReceiptId = pr.ProvisionalReceiptId)";
			try (PreparedStatement ps = connection.prepareStatement(awaitApprovalSql)) {
				ps.setInt(1, returnId);
				if (ps.executeUpdate() == 0) {
					connection.rollback();
					return -1;
				}
			}

			connection.commit();
			return returnId;
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
