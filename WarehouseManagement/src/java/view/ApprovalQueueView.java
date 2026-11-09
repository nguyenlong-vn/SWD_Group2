package view;

import java.util.ArrayList;
import java.util.List;
import model.ManagerApprovalOrder;

public class ApprovalQueueView {

    private String title;
    private List<ManagerApprovalOrder> pendingOrders = new ArrayList<>();
    private String message;

    public ApprovalQueueView() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ManagerApprovalOrder> getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(List<ManagerApprovalOrder> pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ManagerApprovalOrder> renderQueueTable() {
        return pendingOrders;
    }
}
