package view;

import model.Users;

public class LoginView {

    private Users user;
    private String roleKey;
    private String errorMessage;

    public LoginView() {
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }

    public String getDashboardHref() {
        if (roleKey == null) {
            return "/Login";
        }
        switch (roleKey) {
            case "Administrator":
                return "/AdminDashboard";
            case "WarehouseManager":
                return "/ManagerDashboard";
            case "WarehouseStaff":
                return "/WarehouseStaffDashboard.jsp";
            case "PurchaseStaff":
                return "/PurchaseDashboard";
            case "SaleStaff":
                return "/SaleDashboard";
            default:
                return "/Login";
        }
    }
}
