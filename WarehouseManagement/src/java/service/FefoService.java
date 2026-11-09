package service;

public class FefoService {

    public boolean allocateStock(int orderId) {
        if (orderId <= 0) {
            return false;
        }
        return true;
    }
}
