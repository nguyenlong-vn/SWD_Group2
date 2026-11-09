package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Roles;
import utils.DbContext;

public class RolesDAO extends DbContext {

    public Roles getRoleById(int roleId) {
        String sql = "SELECT RoleId, RoleName, Description FROM Roles WHERE RoleId = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Roles r = new Roles();
                r.setRoleId(rs.getInt("RoleId"));
                r.setRoleName(rs.getString("RoleName"));
                r.setDescription(rs.getString("Description"));
                return r;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
