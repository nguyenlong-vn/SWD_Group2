package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.OrderDecisionNotification;
import utils.DbContext;

public class OrderRejectionNotificationsDAO extends DbContext {

    public static boolean insertNotification(Connection connection, int recipientUserId, int rejectedBy,
            String decision, String orderType, int orderId, String orderNumber) throws Exception {
        if (connection == null || recipientUserId <= 0 || rejectedBy <= 0 || orderId <= 0
                || !("Accepted".equals(decision) || "Rejected".equals(decision))
                || orderType == null || orderNumber == null) {
            return false;
        }
        String sql = "INSERT INTO OrderRejectionNotifications "
                + "(RecipientUserId, RejectedBy, Decision, OrderType, OrderId, OrderNumber) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, recipientUserId);
            ps.setInt(2, rejectedBy);
            ps.setString(3, decision);
            ps.setString(4, orderType);
            ps.setInt(5, orderId);
            ps.setString(6, orderNumber);
            return ps.executeUpdate() == 1;
        }
    }

    public static boolean insertNotificationsForRole(Connection connection, String recipientRoleName,
            int actorUserId, String decision, String orderType, int orderId, String orderNumber) throws Exception {
        if (connection == null || recipientRoleName == null || actorUserId <= 0 || orderId <= 0
                || !("Accepted".equals(decision) || "Rejected".equals(decision))
                || orderType == null || orderNumber == null) {
            return false;
        }

        List<Integer> recipientUserIds = new ArrayList<>();
        String recipientsSql = "SELECT u.UserId FROM Users u JOIN Roles r ON r.RoleId = u.RoleId "
                + "WHERE r.RoleName = ? AND u.IsActive = 1";
        try (PreparedStatement ps = connection.prepareStatement(recipientsSql)) {
            ps.setString(1, recipientRoleName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recipientUserIds.add(rs.getInt("UserId"));
                }
            }
        }
        if (recipientUserIds.isEmpty()) {
            return false;
        }
        for (Integer recipientUserId : recipientUserIds) {
            if (!insertNotification(connection, recipientUserId, actorUserId, decision, orderType, orderId,
                    orderNumber)) {
                return false;
            }
        }
        return true;
    }

    public List<OrderDecisionNotification> getUndismissedForUser(int userId) {
        List<OrderDecisionNotification> notifications = new ArrayList<>();
        if (connection == null || userId <= 0) {
            return notifications;
        }
        String sql = "SELECT NotificationId, Decision, OrderType, OrderNumber, CreatedAt "
                + "FROM OrderRejectionNotifications WHERE RecipientUserId = ? AND IsDismissed = 0 "
                + "ORDER BY CreatedAt DESC, NotificationId DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                        notifications.add(new OrderDecisionNotification(rs.getInt("NotificationId"),
                            rs.getString("Decision"), rs.getString("OrderType"), rs.getString("OrderNumber"),
                            createdAt));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return notifications;
    }

    public boolean dismissForUser(int notificationId, int userId) {
        if (connection == null || notificationId <= 0 || userId <= 0) {
            return false;
        }
        String sql = "UPDATE OrderRejectionNotifications SET IsDismissed = 1, DismissedAt = SYSUTCDATETIME() "
                + "WHERE NotificationId = ? AND RecipientUserId = ? AND IsDismissed = 0";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}