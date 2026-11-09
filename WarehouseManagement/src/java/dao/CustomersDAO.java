package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Customers;
import utils.DbContext;

public class CustomersDAO extends DbContext {

	public List<Customers> getActiveCustomers() {
		List<Customers> customers = new ArrayList<>();
		if (connection == null) {
			return customers;
		}
		String sql = "SELECT CustomerId, CustomerCode, CustomerName, Phone, Email, Address, Status "
				+ "FROM Customers WHERE Status = 'Active' ORDER BY CustomerName";
		try (PreparedStatement ps = connection.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				customers.add(new Customers(rs.getInt("CustomerId"), rs.getString("CustomerCode"),
						rs.getString("CustomerName"), rs.getString("Phone"), rs.getString("Email"),
						rs.getString("Address"), rs.getString("Status")));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return customers;
	}
}
