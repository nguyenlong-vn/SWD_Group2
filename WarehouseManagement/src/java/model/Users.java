package model;

public class Users {
    private int userId;
    private int roleId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private boolean isActive;

    public Users() {
    }

    public Users(int userId, int roleId, String username, String passwordHash, String fullName, String email, boolean isActive) {
        this.userId = userId;
        this.roleId = roleId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
