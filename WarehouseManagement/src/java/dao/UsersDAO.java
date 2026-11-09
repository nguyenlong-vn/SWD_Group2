package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Users;
import utils.DbContext;

public class UsersDAO extends DbContext {

    public Users getUserByUsername(String username) {
        String sql = "SELECT u.UserId, u.RoleId, u.Username, u.Password AS PasswordHash, u.FullName, u.Email, u.IsActive, "
                + "r.RoleName "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.RoleId = r.RoleId "
                + "WHERE u.Username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Users u = new Users();
                u.setUserId(rs.getInt("UserId"));
                u.setRoleId(rs.getInt("RoleId"));
                u.setUsername(rs.getString("Username"));
                u.setPasswordHash(rs.getString("PasswordHash"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setIsActive(rs.getBoolean("IsActive"));
                u.setRoleName(rs.getString("RoleName"));
                return u;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Users getUserById(int userId) {
        String sql = "SELECT u.UserId, u.RoleId, u.Username, u.Password AS PasswordHash, u.FullName, u.Email, u.IsActive, "
                + "r.RoleName "
                + "FROM Users u "
                + "LEFT JOIN Roles r ON u.RoleId = r.RoleId "
                + "WHERE u.UserId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Users u = new Users();
                u.setUserId(rs.getInt("UserId"));
                u.setRoleId(rs.getInt("RoleId"));
                u.setUsername(rs.getString("Username"));
                u.setPasswordHash(rs.getString("PasswordHash"));
                u.setFullName(rs.getString("FullName"));
                u.setEmail(rs.getString("Email"));
                u.setIsActive(rs.getBoolean("IsActive"));
                u.setRoleName(rs.getString("RoleName"));
                return u;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
