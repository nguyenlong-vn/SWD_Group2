package service;

import dao.OrderRejectionNotificationsDAO;
import java.util.Collections;
import java.util.List;
import model.OrderDecisionNotification;

public class OrderRejectionNotificationService {
    private final OrderRejectionNotificationsDAO notificationsDAO = new OrderRejectionNotificationsDAO();

    public List<OrderDecisionNotification> getUndismissedForUser(int userId) {
        if (userId <= 0) {
            return Collections.emptyList();
        }
        return notificationsDAO.getUndismissedForUser(userId);
    }

    public boolean dismissForUser(int notificationId, int userId) {
        return notificationsDAO.dismissForUser(notificationId, userId);
    }
}