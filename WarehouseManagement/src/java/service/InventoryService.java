package service;

public class InventoryService {

    public boolean activateBatchesOfReceipt(int receiptId) {
        if (receiptId <= 0) {
            return false;
        }
        return true;
    }
}
