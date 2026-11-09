package view;

import controller.DiscrepancyController;
import java.util.List;
import model.OrderDiscrepancyRow;

public class DiscrepancyView {

    private int importOrderId;
    private String importOrderNumber;
    private String receiptNumber;
    private List<OrderDiscrepancyRow> discrepancies;

    public DiscrepancyView() {
    }

    public int getImportOrderId() {
        return importOrderId;
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId = importOrderId;
    }

    public String getImportOrderNumber() {
        return importOrderNumber;
    }

    public void setImportOrderNumber(String importOrderNumber) {
        this.importOrderNumber = importOrderNumber;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public List<OrderDiscrepancyRow> getDiscrepancies() {
        return discrepancies;
    }

    public void setDiscrepancies(List<OrderDiscrepancyRow> discrepancies) {
        this.discrepancies = discrepancies;
    }

    public boolean isHasDamagedQuantity() {
        return discrepancies != null && discrepancies.stream().anyMatch(row -> row.getDamagedQuantity() > 0);
    }

    public double getTotalDamagedQuantity() {
        double total = 0;
        if (discrepancies != null) {
            for (OrderDiscrepancyRow row : discrepancies) {
                total += row.getDamagedQuantity();
            }
        }
        return total;
    }

    public DiscrepancyView getDiscrepancyForm(int importOrderId) {
        return new DiscrepancyController().getDiscrepancyForm(importOrderId);
    }

    public int createDiscrepancyLog(int importOrderId, double quantity, int reportedBy) {
        return new DiscrepancyController().createDiscrepancyLog(importOrderId, quantity, reportedBy);
    }
}
