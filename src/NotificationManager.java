package src;

import java.sql.*;
import java.util.*;

/**
 * NotificationManager.java
 * Manages user notifications including message notifications and system alerts
 * Provides notification count, retrieval, and management functionalities
 * 
 * Features:
 * - Get unread notification count
 * - Get unread message count
 * - Create notifications
 * - Retrieve notifications
 * - Mark as read
 * - Delete notifications
 */
public class NotificationManager {
    
    private static final String TAG = "[NotificationManager]";
    private static boolean initialized = false;
    
    /**
     * Initialize the notification system
     */
    public static void initialize() {
        try {
            // Create notification table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS notifications (" +
                "notification_id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_id INT NOT NULL," +
                "notification_type ENUM('SALARY_RELEASED', 'ATTENDANCE_REQUIRED', 'MESSAGE', 'PAYMENT_REMINDER', 'SYSTEM') DEFAULT 'SYSTEM'," +
                "title VARCHAR(100) NOT NULL," +
                "message TEXT NOT NULL," +
                "related_data TEXT," +
                "is_read BOOLEAN DEFAULT FALSE," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY (user_id) REFERENCES users(id))";
            
            DBConnection.executeSafeUpdate(createTableSQL);
            initialized = true;
            System.out.println(TAG + " Notification system initialized successfully");
        } catch (Exception e) {
            System.err.println(TAG + " Failed to initialize: " + e.getMessage());
            initialized = false;
        }
    }
    
    /**
     * Get unread notification count for a user
     */
    public static int getUnreadNotificationCount(int userId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;
            
            String query = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND is_read = FALSE";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("count");
            }
            
            rs.close();
            pst.close();
            con.close();
            
            return count;
        } catch (SQLException e) {
            System.err.println(TAG + " Error getting notification count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get unread message count for a user
     */
    public static int getUnreadMessageCount(int userId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;
            
            String query = "SELECT COUNT(*) as count FROM messages WHERE recipient_id = ? AND is_read = FALSE";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            
            int count = 0;
            if (rs.next()) {
                count = rs.getInt("count");
            }
            
            rs.close();
            pst.close();
            con.close();
            
            return count;
        } catch (SQLException e) {
            System.err.println(TAG + " Error getting message count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get total unread count (notifications + messages)
     */
    public static int getTotalUnreadCount(int userId) {
        return getUnreadNotificationCount(userId) + getUnreadMessageCount(userId);
    }
    
    /**
     * Create a new notification
     */
    public static int createNotification(int userId, String type, String title, String message, String relatedData) {
        try {
            String query = "INSERT INTO notifications (user_id, notification_type, title, message, related_data) " +
                          "VALUES (?, ?, ?, ?, ?)";
            
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;
            
            PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, userId);
            pst.setString(2, type);
            pst.setString(3, title);
            pst.setString(4, message);
            pst.setString(5, relatedData);
            
            int result = pst.executeUpdate();
            
            if (result > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int notificationId = rs.getInt(1);
                    System.out.println(TAG + " Notification created: " + notificationId);
                    rs.close();
                    pst.close();
                    con.close();
                    return notificationId;
                }
            }
            
            pst.close();
            con.close();
            return 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error creating notification: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get all unread notifications for a user
     */
    public static List<NotificationInfo> getUnreadNotifications(int userId, int limit) {
        List<NotificationInfo> notifications = new ArrayList<>();
        
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return notifications;
            
            String query = "SELECT notification_id, notification_type, title, message, related_data, created_at " +
                          "FROM notifications WHERE user_id = ? AND is_read = FALSE " +
                          "ORDER BY created_at DESC LIMIT ?";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, limit);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                NotificationInfo notif = new NotificationInfo();
                notif.id = rs.getInt("notification_id");
                notif.type = rs.getString("notification_type");
                notif.title = rs.getString("title");
                notif.message = rs.getString("message");
                notif.relatedData = rs.getString("related_data");
                notif.createdAt = rs.getTimestamp("created_at");
                notifications.add(notif);
            }
            
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error retrieving notifications: " + e.getMessage());
        }
        
        return notifications;
    }
    
    /**
     * Mark notification as read
     */
    public static boolean markAsRead(int notificationId) {
        try {
            String query = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, notificationId);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error marking notification as read: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete notification
     */
    public static boolean deleteNotification(int notificationId) {
        try {
            String query = "DELETE FROM notifications WHERE notification_id = ?";
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, notificationId);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error deleting notification: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create salary notification for an employee
     */
    public static void notifySalaryReleased(int userId, String employeeId, double amount) {
        String title = "Salary Released!";
        String message = String.format("Your salary of PHP %.2f has been released and credited to your wallet.", amount);
        String relatedData = "employee_id:" + employeeId + "|amount:" + amount;
        createNotification(userId, "SALARY_RELEASED", title, message, relatedData);
    }
    
    /**
     * Create message notification
     */
    public static void notifyNewMessage(int userId, String senderName) {
        String title = "New Message";
        String message = "You have received a message from " + senderName;
        createNotification(userId, "MESSAGE", title, message, null);
    }
    
    /**
     * Create system notification
     */
    public static void notifySystem(int userId, String title, String message) {
        createNotification(userId, "SYSTEM", title, message, null);
    }
    
    /**
     * Create payment reminder notification
     */
    public static void notifyPaymentReminder(int userId, String billType, double amount) {
        String title = "Payment Reminder";
        String message = String.format("You have a %s bill of PHP %.2f due for payment.", billType, amount);
        String relatedData = "bill_type:" + billType + "|amount:" + amount;
        createNotification(userId, "PAYMENT_REMINDER", title, message, relatedData);
    }
    
    /**
     * Create attendance reminder notification
     */
    public static void notifyAttendanceRequired(int userId, String employeeName) {
        String title = "Attendance Required";
        String message = "Please mark your attendance for today.";
        createNotification(userId, "ATTENDANCE_REQUIRED", title, message, null);
    }
    
    /**
     * Clear all notifications for a user
     */
    public static boolean clearAllNotifications(int userId) {
        try {
            String query = "DELETE FROM notifications WHERE user_id = ?";
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result >= 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error clearing notifications: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inner class for notification information
     */
    public static class NotificationInfo {
        public int id;
        public String type;
        public String title;
        public String message;
        public String relatedData;
        public Timestamp createdAt;
        
        @Override
        public String toString() {
            return "[" + type + "] " + title + ": " + message;
        }
    }
}
