package view;

import controller.OutboundOrderController;
import java.util.ArrayList;
import java.util.List;
import model.Customers;
import model.Products;

public class OutboundOrderView {

    private List<Customers> customers = new ArrayList<>();
    private List<Products> products = new ArrayList<>();
    private String errorMessage;
    private String successMessage;

    public OutboundOrderView() {
    }

    public List<Customers> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customers> customers) {
        this.customers = customers;
    }

    public List<Products> getProducts() {
        return products;
    }

    public void setProducts(List<Products> products) {
        this.products = products;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    public OutboundOrderView getOrderForm(String errorMessage, String successMessage) {
        return new OutboundOrderController().getOrderForm(errorMessage, successMessage);
    }

    public OutboundOrderView processCreateOrder(String customerIdValue, String orderDateValue, int createdBy,
            String[] productIdValues, String[] quantityValues) {
        return new OutboundOrderController().processCreateOrder(customerIdValue, orderDateValue, createdBy,
                productIdValues, quantityValues);
    }
}
