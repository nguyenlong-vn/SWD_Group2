/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.QualityInspections;
import utils.DbContext;

public class QualityInspectionsDAO extends DbContext {
	public QualityInspections getLatestByReceiptId(int provisionalReceiptId) {
		String sql = "SELECT TOP 1 InspectionId, ProvisionalReceiptId, InspectedBy, InspectionDate, Result, Notes "
				+ "FROM QualityInspections WHERE ProvisionalReceiptId = ? "
				+ "ORDER BY InspectionDate DESC, InspectionId DESC";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, provisionalReceiptId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					QualityInspections inspection = new QualityInspections();
					inspection.setInspectionId(rs.getInt("InspectionId"));
					inspection.setProvisionalReceiptId(rs.getInt("ProvisionalReceiptId"));
					inspection.setInspectedBy(rs.getInt("InspectedBy"));
					inspection.setInspectionDate(rs.getTimestamp("InspectionDate"));
					inspection.setResult(rs.getString("Result"));
					inspection.setNotes(rs.getString("Notes"));
					return inspection;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
