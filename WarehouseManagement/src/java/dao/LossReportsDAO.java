/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.ProvisionalReceiptDetails;
import utils.DbContext;

public class LossReportsDAO extends DbContext {

	public int createDamagedLossReports(int provisionalReceiptId, String receiptNumber,
			int reportedBy, List<ProvisionalReceiptDetails> details) {
		if (connection == null || details == null || details.isEmpty()) {
			return -1;
		}

		boolean previousAutoCommit = true;
		int createdCount = 0;
		try {
			previousAutoCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);

			String insertSql = "INSERT INTO LossReports "
					+ "(BatchId, ProvisionalDetailId, ReportedBy, LossType, Quantity, Reason, Status, ReportedDate) "
					+ "VALUES (NULL, ?, ?, 'Damaged', ?, ?, 'Pending', ?)";
			String existingSql = "SELECT COALESCE(SUM(Quantity), 0) AS PreviouslyReported "
					+ "FROM LossReports WITH (UPDLOCK, HOLDLOCK) "
					+ "WHERE ProvisionalDetailId = ? AND LossType = 'Damaged'";

			for (ProvisionalReceiptDetails detail : details) {
				if (detail.getDamagedQuantity() <= 0) {
					continue;
				}

				String reason = "Damaged quantity from provisional receipt " + receiptNumber
						+ " detail " + detail.getProvisionalDetailId();
				double previouslyReported = 0;
				try (PreparedStatement ps = connection.prepareStatement(existingSql)) {
					ps.setInt(1, detail.getProvisionalDetailId());
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							previouslyReported = rs.getDouble("PreviouslyReported");
						}
					}
				}
				double quantityToCreate = detail.getDamagedQuantity() - previouslyReported;
				if (quantityToCreate <= 0.0005) {
					continue;
				}
				try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
					insert.setInt(1, detail.getProvisionalDetailId());
					insert.setInt(2, reportedBy);
					insert.setDouble(3, quantityToCreate);
					insert.setString(4, reason);
					insert.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
					insert.executeUpdate();
				}
				createdCount++;
			}

			connection.commit();
			return createdCount;
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

	public int saveLossReport(int importOrderId, double quantity, int reportedBy) {
		if (connection == null || importOrderId <= 0 || reportedBy <= 0
				|| !Double.isFinite(quantity) || quantity <= 0) {
			return -1;
		}

		int provisionalReceiptId = -1;
		String receiptNumber = null;
		List<ProvisionalReceiptDetails> details = new ArrayList<>();
		String sql = "SELECT pr.ProvisionalReceiptId, pr.ReceiptNumber, d.ProvisionalDetailId, d.ProductId, "
				+ "d.Quantity, d.Unit, "
				+ "COALESCE(SUM(CASE WHEN g.GradeType = 'Damaged' THEN g.Quantity ELSE 0 END), 0) AS DamagedQuantity "
				+ "FROM ProvisionalReceipts pr "
				+ "JOIN ProvisionalReceiptDetails d ON d.ProvisionalReceiptId = pr.ProvisionalReceiptId "
				+ "LEFT JOIN ProductDetailGrades g ON g.ProvisionalDetailId = d.ProvisionalDetailId "
				+ "WHERE pr.ImportOrderId = ? "
				+ "GROUP BY pr.ProvisionalReceiptId, pr.ReceiptNumber, d.ProvisionalDetailId, d.ProductId, d.Quantity, d.Unit "
				+ "ORDER BY d.ProvisionalDetailId";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, importOrderId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					if (provisionalReceiptId <= 0) {
						provisionalReceiptId = rs.getInt("ProvisionalReceiptId");
						receiptNumber = rs.getString("ReceiptNumber");
					}
					ProvisionalReceiptDetails detail = new ProvisionalReceiptDetails();
					detail.setProvisionalDetailId(rs.getInt("ProvisionalDetailId"));
					detail.setProvisionalReceiptId(rs.getInt("ProvisionalReceiptId"));
					detail.setProductId(rs.getInt("ProductId"));
					detail.setQuantity(rs.getDouble("Quantity"));
					detail.setUnit(rs.getString("Unit"));
					detail.setDamagedQuantity(rs.getDouble("DamagedQuantity"));
					details.add(detail);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}

		if (details.isEmpty() || receiptNumber == null) {
			return 0;
		}
		return createDamagedLossReports(provisionalReceiptId, receiptNumber, reportedBy, details);
	}

}
