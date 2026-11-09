package service;

import dao.GoodsReceiptsDAO;
import dao.ImportOrderDetailsDAO;
import dao.ProvisionalReceiptDetailsDAO;
import dao.ProvisionalReceiptsDAO;
import dao.StorageLocationsDAO;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Set;
import java.util.stream.Collectors;
import model.GoodsReceiptEntry;
import model.ImportOrderDetails;
import model.ProvisionalReceiptDetails;
import model.PurchaseReceiptRow;
import model.StorageLocations;

public class PurchaseWorkflowService {

    private final ProvisionalReceiptsDAO provisionalReceiptsDAO = new ProvisionalReceiptsDAO();
    private final ProvisionalReceiptDetailsDAO provisionalReceiptDetailsDAO = new ProvisionalReceiptDetailsDAO();
    private final ImportOrderDetailsDAO importOrderDetailsDAO = new ImportOrderDetailsDAO();
    private final StorageLocationsDAO storageLocationsDAO = new StorageLocationsDAO();
    private final GoodsReceiptsDAO goodsReceiptsDAO = new GoodsReceiptsDAO();

    public List<PurchaseReceiptRow> getPurchaseReceipts() {
        return provisionalReceiptsDAO.getPurchaseReceipts();
    }

    public PurchaseReceiptRow getCompletedReceipt(int provisionalReceiptId) {
        return provisionalReceiptsDAO.getCompletedPurchaseReceipt(provisionalReceiptId);
    }

    public List<ProvisionalReceiptDetails> getReceiptDetails(int provisionalReceiptId) {
        return provisionalReceiptDetailsDAO.getDetailsByReceiptId(provisionalReceiptId);
    }

    public List<StorageLocations> getAvailableLocations() {
        return storageLocationsDAO.getAvailableLocations();
    }

    public StorageLocations getAvailableQuarantineLocation() {
        return storageLocationsDAO.getAvailableQuarantineLocation();
    }

    public boolean hasValidClassification(List<ProvisionalReceiptDetails> details) {
        if (details == null || details.isEmpty()) {
            return false;
        }
        boolean hasClassifiedQuantity = false;
        for (ProvisionalReceiptDetails detail : details) {
            double classified = detail.getGradeAQuantity() + detail.getGradeBQuantity() + detail.getDamagedQuantity();
            if (!Double.isFinite(classified) || detail.getGradeAQuantity() < 0 || detail.getGradeBQuantity() < 0
                    || detail.getDamagedQuantity() < 0 || classified - detail.getQuantity() > 0.0005) {
                return false;
            }
            hasClassifiedQuantity |= classified > 0;
        }
        return hasClassifiedQuantity;
    }

    public List<String> getClassificationIssues(List<ProvisionalReceiptDetails> details) {
        List<String> issues = new ArrayList<>();
        if (details == null || details.isEmpty()) {
            issues.add("No product details are available for this receipt.");
            return issues;
        }
        for (ProvisionalReceiptDetails detail : details) {
            double classified = detail.getGradeAQuantity() + detail.getGradeBQuantity() + detail.getDamagedQuantity();
            double difference = Math.round((detail.getQuantity() - classified) * 1000.0) / 1000.0;
            if (difference > 0.0005) {
                issues.add(detail.getProductName() + ": " + difference + " " + detail.getUnit() + " unclassified.");
            } else if (difference < -0.0005) {
                issues.add(detail.getProductName() + ": " + Math.abs(difference) + " " + detail.getUnit()
                        + " overclassified.");
            }
        }
        return issues;
    }

    public String createGoodsReceipt(int provisionalReceiptId, int receivedBy, String notes,
            Map<String, Integer> locationByDetailGrade, Map<String, LocalDate> expiryByDetailGrade) {
        PurchaseReceiptRow receipt = getCompletedReceipt(provisionalReceiptId);
        if (receipt == null || !"Passed".equals(receipt.getQcStatus()) || receivedBy <= 0
                || receipt.isGoodsReceiptExists() || goodsReceiptsDAO.hasGoodsReceiptForImportOrder(receipt.getImportOrderId())
                || locationByDetailGrade == null || expiryByDetailGrade == null) {
            return null;
        }

        List<ProvisionalReceiptDetails> details = getReceiptDetails(provisionalReceiptId);
        if (!hasValidClassification(details)) {
            return null;
        }

        Set<Integer> availableLocations = getAvailableLocations().stream()
                .map(StorageLocations::getLocationId).collect(Collectors.toSet());
        boolean hasDamagedQuantity = details.stream().anyMatch(detail -> detail.getDamagedQuantity() > 0);
        StorageLocations quarantineLocation = hasDamagedQuantity
            ? getAvailableQuarantineLocation() : null;
        if (hasDamagedQuantity && quarantineLocation == null) {
            return null;
        }
        Map<Integer, double[]> costByProduct = new HashMap<>();
        for (ImportOrderDetails detail : importOrderDetailsDAO.getDetailsByImportOrderId(receipt.getImportOrderId())) {
            double[] cost = costByProduct.computeIfAbsent(detail.getProductId(), ignored -> new double[2]);
            cost[0] += detail.getQuantity() * detail.getUnitPrice();
            cost[1] += detail.getQuantity();
        }

        Timestamp receivedDate = new Timestamp(System.currentTimeMillis());
        List<GoodsReceiptEntry> entries = new ArrayList<>();
        int requiredGradeBatches = 0;
        for (ProvisionalReceiptDetails detail : details) {
            double[] cost = costByProduct.get(detail.getProductId());
            if (cost == null || cost[1] <= 0) {
                return null;
            }
            double unitCost = cost[0] / cost[1];
            requiredGradeBatches += addGradeEntry(entries, receipt, detail, locationByDetailGrade,
                expiryByDetailGrade, availableLocations, quarantineLocation, receivedDate, unitCost,
                "A", detail.getGradeAQuantity(), "Available");
            requiredGradeBatches += addGradeEntry(entries, receipt, detail, locationByDetailGrade,
                expiryByDetailGrade, availableLocations, quarantineLocation, receivedDate, unitCost,
                "B", detail.getGradeBQuantity(), "Available");
            requiredGradeBatches += addGradeEntry(entries, receipt, detail, locationByDetailGrade,
                expiryByDetailGrade, availableLocations, quarantineLocation, receivedDate, unitCost,
                "Damaged", detail.getDamagedQuantity(), "Quarantine");
        }

        if (entries.isEmpty() || entries.size() != requiredGradeBatches
            || locationByDetailGrade.size() != requiredGradeBatches
            || expiryByDetailGrade.size() != requiredGradeBatches) {
            return null;
        }
        String receiptNumber = "GR-" + System.currentTimeMillis();
        int goodsReceiptId = goodsReceiptsDAO.createGoodsReceipt(
            receipt.getImportOrderId(), provisionalReceiptId, receiptNumber, receivedBy, notes, entries);
        return goodsReceiptId > 0 ? receiptNumber : null;
    }

    private int addGradeEntry(List<GoodsReceiptEntry> entries, PurchaseReceiptRow receipt,
            ProvisionalReceiptDetails detail, Map<String, Integer> locationByDetailGrade,
            Map<String, LocalDate> expiryByDetailGrade, Set<Integer> availableLocations,
            StorageLocations quarantineLocation,
            Timestamp receivedDate, double unitCost, String grade, double quantity, String status) {
        if (quantity <= 0) {
            return 0;
        }
        String key = detail.getProvisionalDetailId() + "|" + grade;
        Integer locationId;
        LocalDate expiryDate;
        if ("Damaged".equals(grade)) {
            locationId = quarantineLocation == null ? null : quarantineLocation.getLocationId();
            expiryDate = receivedDate.toLocalDateTime().toLocalDate();
        } else {
            locationId = locationByDetailGrade.get(key);
            expiryDate = expiryByDetailGrade.get(key);
        }
        if (locationId == null || !availableLocations.contains(locationId) || expiryDate == null
                || expiryDate.isBefore(LocalDate.now())) {
            return 1;
        }
        addEntry(entries, receipt, detail, locationId, expiryDate, receivedDate, unitCost, grade, quantity, status);
        return 1;
    }

    private void addEntry(List<GoodsReceiptEntry> entries, PurchaseReceiptRow receipt,
            ProvisionalReceiptDetails detail, int locationId, LocalDate expiryDate,
            Timestamp receivedDate, double unitCost, String grade, double quantity, String status) {
        if (quantity <= 0) {
            return;
        }
        GoodsReceiptEntry entry = new GoodsReceiptEntry();
        entry.setProductId(detail.getProductId());
        entry.setLocationId(locationId);
        entry.setBatchCode("PR" + receipt.getProvisionalReceiptId() + "-D"
                + detail.getProvisionalDetailId() + "-" + grade + "-" + System.nanoTime());
        entry.setReceivedDate(receivedDate);
        entry.setExpiryDate(Timestamp.valueOf(expiryDate.atStartOfDay()));
        entry.setQuantity(quantity);
        entry.setUnitCost(unitCost);
        entry.setUnit(detail.getUnit());
        entry.setQualityGrade(grade);
        entry.setStatus(status);
        entries.add(entry);
    }
}