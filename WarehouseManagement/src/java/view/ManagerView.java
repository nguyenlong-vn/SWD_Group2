package view;

import java.util.ArrayList;
import java.util.List;
import model.ManagerApprovalOrder;
import model.ManagerOrderDetail;

public class ManagerView {
    private String section;
    private String title;
    private List<ManagerApprovalOrder> orders = new ArrayList<>();
    private ManagerApprovalOrder selectedOrder;
    private List<ManagerOrderDetail> orderDetails = new ArrayList<>();
    private String orderType;
    private String returnSection;

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<ManagerApprovalOrder> getOrders() { return orders; }
    public void setOrders(List<ManagerApprovalOrder> orders) { this.orders = orders; }
    public ManagerApprovalOrder getSelectedOrder() { return selectedOrder; }
    public void setSelectedOrder(ManagerApprovalOrder selectedOrder) { this.selectedOrder = selectedOrder; }
    public List<ManagerOrderDetail> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<ManagerOrderDetail> orderDetails) { this.orderDetails = orderDetails; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public String getReturnSection() { return returnSection; }
    public void setReturnSection(String returnSection) { this.returnSection = returnSection; }
}