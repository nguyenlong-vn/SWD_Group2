package model;

public class Customers {
    private int customerId;
    private String customerCode;
    private String customerName;
    private String phone;
    private String email;
    private String address;
    private String status;

    public Customers() {
    }

    public Customers(int customerId, String customerCode, String customerName, String phone, String email, String address, String status) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.status = status;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
