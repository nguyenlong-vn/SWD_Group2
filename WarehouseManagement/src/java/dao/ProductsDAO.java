package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Products;
import utils.DbContext;

public class ProductsDAO extends DbContext {

	public List<Products> getActiveProducts() {
		List<Products> products = new ArrayList<>();
		if (connection == null) {
			return products;
		}
		String sql = "SELECT ProductId, CategoryId, ProductCode, ProductName, Unit, BasePrice, Status "
				+ "FROM Products WHERE Status = 'Active' ORDER BY ProductName";
		try (PreparedStatement ps = connection.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				products.add(new Products(rs.getInt("ProductId"), rs.getInt("CategoryId"),
						rs.getString("ProductCode"), rs.getString("ProductName"), rs.getString("Unit"),
						rs.getDouble("BasePrice"), rs.getString("Status")));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return products;
	}
}
