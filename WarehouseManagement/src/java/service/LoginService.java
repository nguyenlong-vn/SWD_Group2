package service;

import dao.UsersDAO;
import model.Users;

public class LoginService {

    private final UsersDAO usersDAO = new UsersDAO();

    public Users login(String username, String password) {
        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            return null;
        }

        Users user = usersDAO.getUserByUsername(username.trim());
        if (user == null) {
            return null;
        }

        if (!user.isIsActive()) {
            return null;
        }

        if (!password.equals(user.getPasswordHash())) {
            return null;
        }

        user.setPasswordHash(null);
        return user;
    }

    public String toRoleKey(String roleName) {
        if (roleName == null) {
            return "";
        }
        return roleName.replaceAll("\\s+", "");
    }
}
