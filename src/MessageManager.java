package src;

import java.sql.*;
import java.util.*;

/**
 * MessageManager.java
 * Manages messaging system between admin and workers
 * Provides message sending, retrieval, and notification features
 * 
 * Features:
 * - Send messages
 * - Retrieve messages
 * - Get unread message count
 * - Mark messages as read
 * - Get conversation history
 */
public class MessageManager {
    
    private static final String TAG = "[MessageManager]";
    
    /**
     * Send a message from one user to another
     */
    public static int sendMessage(int senderId, int recipientId, String subject, String messageText, String messageType) {
        try {
            String query = "INSERT INTO messages (sender_id, recipient_id, subject, message_text, message_type) " +
                          "VALUES (?, ?, ?, ?, ?)";
            
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;
            
            PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, senderId);
            pst.setInt(2, recipientId);
            pst.setString(3, subject);
            pst.setString(4, messageText);
            pst.setString(5, messageType);
            
            int result = pst.executeUpdate();
            
            if (result > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int messageId = rs.getInt(1);
                    System.out.println(TAG + " Message sent successfully - ID: " + messageId);
                    
                    // Create notification for recipient
                    String senderNameQuery = "SELECT first_name, last_name FROM users WHERE id = ?";
                    PreparedStatement nameStmt = con.prepareStatement(senderNameQuery);
                    nameStmt.setInt(1, senderId);
                    ResultSet nameRs = nameStmt.executeQuery();
                    
                    String senderName = "Unknown";
                    if (nameRs.next()) {
                        senderName = nameRs.getString("first_name") + " " + nameRs.getString("last_name");
                    }
                    
                    nameRs.close();
                    nameStmt.close();
                    
                    NotificationManager.notifyNewMessage(recipientId, senderName);
                    
                    rs.close();
                    pst.close();
                    con.close();
                    return messageId;
                }
            }
            
            pst.close();
            con.close();
            return 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error sending message: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get unread messages for a user
     */
    public static List<MessageInfo> getUnreadMessages(int userId, int limit) {
        List<MessageInfo> messages = new ArrayList<>();
        
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return messages;
            
            String query = "SELECT m.message_id, u.first_name, u.last_name, m.subject, " +
                          "m.message_text, m.message_type, m.created_at " +
                          "FROM messages m " +
                          "JOIN users u ON m.sender_id = u.id " +
                          "WHERE m.recipient_id = ? AND m.is_read = FALSE " +
                          "ORDER BY m.created_at DESC LIMIT ?";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, limit);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                MessageInfo msg = new MessageInfo();
                msg.id = rs.getInt("message_id");
                msg.senderName = rs.getString("first_name") + " " + rs.getString("last_name");
                msg.subject = rs.getString("subject");
                msg.text = rs.getString("message_text");
                msg.type = rs.getString("message_type");
                msg.createdAt = rs.getTimestamp("created_at");
                messages.add(msg);
            }
            
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error retrieving messages: " + e.getMessage());
        }
        
        return messages;
    }
    
    /**
     * Get all messages for a user (conversation with another user)
     */
    public static List<MessageInfo> getConversation(int userId, int otherUserId, int limit) {
        List<MessageInfo> messages = new ArrayList<>();
        
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return messages;
            
            String query = "SELECT m.message_id, u.first_name, u.last_name, m.sender_id, " +
                          "m.subject, m.message_text, m.message_type, m.created_at, m.is_read " +
                          "FROM messages m " +
                          "JOIN users u ON m.sender_id = u.id " +
                          "WHERE ((m.sender_id = ? AND m.recipient_id = ?) OR " +
                          "(m.sender_id = ? AND m.recipient_id = ?)) " +
                          "ORDER BY m.created_at DESC LIMIT ?";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, otherUserId);
            pst.setInt(3, otherUserId);
            pst.setInt(4, userId);
            pst.setInt(5, limit);
            
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                MessageInfo msg = new MessageInfo();
                msg.id = rs.getInt("message_id");
                msg.senderId = rs.getInt("sender_id");
                msg.senderName = rs.getString("first_name") + " " + rs.getString("last_name");
                msg.subject = rs.getString("subject");
                msg.text = rs.getString("message_text");
                msg.type = rs.getString("message_type");
                msg.createdAt = rs.getTimestamp("created_at");
                msg.isRead = rs.getBoolean("is_read");
                messages.add(msg);
            }
            
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error retrieving conversation: " + e.getMessage());
        }
        
        return messages;
    }
    
    /**
     * Mark message as read
     */
    public static boolean markAsRead(int messageId) {
        try {
            String query = "UPDATE messages SET is_read = TRUE WHERE message_id = ?";
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, messageId);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error marking message as read: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get total unread messages count for a user
     */
    public static int getUnreadCount(int userId) {
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
            System.err.println(TAG + " Error getting unread count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Delete message
     */
    public static boolean deleteMessage(int messageId) {
        try {
            String query = "DELETE FROM messages WHERE message_id = ?";
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, messageId);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error deleting message: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Send notification message to all workers
     */
    public static int sendNotificationToWorkers(int adminId, String subject, String message) {
        int sentCount = 0;
        
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;
            
            // Get all worker IDs
            String getWorkersQuery = "SELECT id FROM users WHERE user_type = 'WORKER'";
            PreparedStatement getWorkers = con.prepareStatement(getWorkersQuery);
            ResultSet workersRs = getWorkers.executeQuery();
            
            List<Integer> workerIds = new ArrayList<>();
            while (workersRs.next()) {
                workerIds.add(workersRs.getInt("id"));
            }
            
            workersRs.close();
            getWorkers.close();
            
            // Send message to each worker
            for (int workerId : workerIds) {
                int result = sendMessage(adminId, workerId, subject, message, "NOTIFICATION");
                if (result > 0) {
                    sentCount++;
                }
            }
            
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error sending broadcast message: " + e.getMessage());
        }
        
        return sentCount;
    }
    
    /**
     * Inner class for message information
     */
    public static class MessageInfo {
        public int id;
        public int senderId;
        public String senderName;
        public String subject;
        public String text;
        public String type;
        public Timestamp createdAt;
        public boolean isRead;
        
        @Override
        public String toString() {
            return "From: " + senderName + " | Subject: " + subject + " | " + createdAt;
        }
    }
}
