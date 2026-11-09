package service;

import dao.CustomersDAO;
import dao.ExportOrderDetailsDAO;
import dao.ExportOrdersDAO;
import dao.InventoryBatchesDAO;
import dao.ProductsDAO;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import model.Customers;
import model.ExportOrderDetails;
import model.ExportOrders;
import model.Products;
import view.OutboundOrderView;

public class OutboundOrderService {

    private final ExportOrdersDAO exportOrdersDAO = new ExportOrdersDAO();
    private final ExportOrderDetailsDAO exportOrderDetailsDAO = new ExportOrderDetailsDAO();

    public OutboundOrderView getOrderForm(String errorMessage, String successMessage) {
        OutboundOrderView view = new OutboundOrderView();
        view.setCustomers(new CustomersDAO().getActiveCustomers());
        view.setProducts(new ProductsDAO().getActiveProducts());
        view.setErrorMessage(errorMessage);
        view.setSuccessMessage(successMessage);
        return view;
    }

    public OutboundOrderView processCreateOrder(String customerIdValue, String orderDateValue, int createdBy,
            String[] productIdValues, String[] quantityValues) {
        OutboundOrderView view = getOrderForm(null, null);
        String validationError = validateOrderFields(customerIdValue, orderDateValue, createdBy,
                productIdValues, quantityValues, view);
        if (validationError != null) {
            view.setErrorMessage(validationError);
            return view;
        }

        int customerId = Integer.parseInt(customerIdValue);
        LocalDate orderDate = LocalDate.parse(orderDateValue);
        Map<Integer, Products> productsById = new HashMap<>();
        for (Products product : view.getProducts()) {
            productsById.put(product.getProductId(), product);
        }

        List<ExportOrderDetails> details = new ArrayList<>();
        for (int index = 0; index < productIdValues.length; index++) {
            Products product = productsById.get(Integer.parseInt(productIdValues[index]));
            ExportOrderDetails detail = new ExportOrderDetails();
            detail.setProductId(product.getProductId());
            detail.setQuantity(Double.parseDouble(quantityValues[index]));
            detail.setUnit(product.getUnit());
            detail.setUnitPrice(product.getBasePrice());
            details.add(detail);
        }

        String orderNumber = "SO-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        ExportOrders order = new ExportOrders();
        order.setCustomerId(customerId);
        order.setOrderNumber(orderNumber);
        order.setOrderDate(Timestamp.valueOf(orderDate.atStartOfDay()));
        order.setStatus("Pending");
        order.setCreatedBy(createdBy);

        int exportOrderId = saveOutboundOrder(order);
        if (exportOrderId <= 0) {
            view.setErrorMessage("The export order could not be saved. Please try again.");
            return view;
        }

        if (!saveOrderDetails(exportOrderId, details)) {
            view.setErrorMessage("The export order details could not be saved. Please try again.");
            return view;
        }

        view.setSuccessMessage("Export order " + orderNumber + " created successfully.");
        return view;
    }

    public int saveOutboundOrder(ExportOrders orderData) {
        return exportOrdersDAO.saveOutboundOrder(orderData);
    }

    public boolean saveOrderDetails(int exportOrderId, List<ExportOrderDetails> orderDetails) {
        return exportOrderDetailsDAO.saveOrderDetails(exportOrderId, orderDetails);
    }

    private String validateOrderFields(String customerIdValue, String orderDateValue, int createdBy,
            String[] productIdValues, String[] quantityValues, OutboundOrderView view) {
        int customerId = parsePositiveInt(customerIdValue);
        if (customerId == 0 || !containsCustomer(view.getCustomers(), customerId)) {
            return "Select an active customer.";
        }
        if (createdBy <= 0) {
            return "The signed-in sales user could not be identified.";
        }
        try {
            LocalDate.parse(orderDateValue);
        } catch (Exception ex) {
            return "Enter a valid order date.";
        }
        if (productIdValues == null || quantityValues == null
            || productIdValues.length == 0 || productIdValues.length != quantityValues.length) {
            return "Add at least one complete product line.";
        }

        Map<Integer, Products> activeProductsById = new HashMap<>();
        for (Products product : view.getProducts()) {
            activeProductsById.put(product.getProductId(), product);
        }
        Map<Integer, Double> requestedQuantityByProduct = new HashMap<>();
        for (int index = 0; index < productIdValues.length; index++) {
            int productId = parsePositiveInt(productIdValues[index]);
            if (!activeProductsById.containsKey(productId)) {
                return "Select a valid active product for every line.";
            }
            try {
                double quantity = Double.parseDouble(quantityValues[index]);
                if (!Double.isFinite(quantity) || quantity <= 0) {
                    return "Quantity must be greater than zero.";
                }
                requestedQuantityByProduct.merge(productId, quantity, Double::sum);
            } catch (Exception ex) {
                return "Enter a valid quantity for every product.";
            }
        }

        InventoryBatchesDAO inventoryBatchesDAO = new InventoryBatchesDAO();
        for (Map.Entry<Integer, Double> requested : requestedQuantityByProduct.entrySet()) {
            Products product = activeProductsById.get(requested.getKey());
            double availableQuantity = inventoryBatchesDAO.getAvailableQuantity(product.getProductId());
            if (requested.getValue() > availableQuantity + 0.0005) {
                return "Insufficient stock for " + product.getProductName() + ". Requested: "
                        + requested.getValue() + " " + product.getUnit() + "; available: "
                        + availableQuantity + " " + product.getUnit() + ".";
            }
        }
        return null;
    }

    private boolean containsCustomer(List<Customers> customers, int customerId) {
        for (Customers customer : customers) {
            if (customer.getCustomerId() == customerId) {
                return true;
            }
        }
        return false;
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : 0;
        } catch (Exception ex) {
            return 0;
        }
    }
}
