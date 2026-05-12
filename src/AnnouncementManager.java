package src;

import java.sql.*;
import java.util.*;

/**
 * AnnouncementManager.java
 * Manages system-wide announcements for all users
 * Features:
 * - Post announcements
 * - Retrieve announcements
 * - Mark as read with user tracking
 * - Delete announcements
 * - Track unread announcements with red dot indicator
 */
public class AnnouncementManager {
    
    private static final String TAG = "[AnnouncementManager]";
    
    public static class AnnouncementInfo {
        public int id;
        public String title;
        public String message;
        public String announcedBy;
        public java.sql.Timestamp createdAt;
        public boolean isRead;
    }
    
    /**
     * Initialize announcement tracking table
     */
    public static void initializeAnnouncementTracking() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS announcement_read (" +
                    "read_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "announcement_id INT NOT NULL," +
                    "read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_user_announcement (user_id, announcement_id)," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (announcement_id) REFERENCES announcements(announcement_id))";
            
            DBConnection.executeSafeUpdate(createTableSQL);
            System.out.println(TAG + " Announcement tracking table initialized successfully");
        } catch (Exception e) {
            System.err.println(TAG + " Failed to initialize announcement tracking: " + e.getMessage());
        }
    }
    
    /**
     * Post a new announcement
     */
    public static boolean postAnnouncement(int adminId, String title, String message) {
        try {
            String query = "INSERT INTO announcements (admin_id, title, message, created_at) VALUES (?, ?, ?, NOW())";
            
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, adminId);
            pst.setString(2, title);
            pst.setString(3, message);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            if (result > 0) {
                System.out.println(TAG + " Announcement posted successfully");
                
                // Notify all users
                notifyAllUsersOfAnnouncement(title, message);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println(TAG + " Error posting announcement: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Notify all users of new announcement
     */
    private static void notifyAllUsersOfAnnouncement(String title, String message) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return;
            
            // Get all user IDs
            String query = "SELECT id FROM users WHERE user_type = 'WORKER'";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                int userId = rs.getInt("id");
                NotificationManager.createNotification(userId,
                        "SYSTEM",
                        "📢 New Announcement: " + title,
                        message,
                        "ANNOUNCEMENT");
            }
            
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error notifying users: " + e.getMessage());
        }
    }
    
    /**
     * Get all announcements
     */
    public static List<AnnouncementInfo> getAllAnnouncements() {
        List<AnnouncementInfo> announcements = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return announcements;
            
            String query = "SELECT a.announcement_id, a.title, a.message, CONCAT(u.first_name, ' ', u.last_name) as admin_name, " +
                          "a.created_at FROM announcements a JOIN users u ON a.admin_id = u.id ORDER BY a.created_at DESC LIMIT 20";
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            while (rs.next()) {
                AnnouncementInfo announcement = new AnnouncementInfo();
                announcement.id = rs.getInt("announcement_id");
                announcement.title = rs.getString("title");
                announcement.message = rs.getString("message");
                announcement.announcedBy = rs.getString("admin_name");
                announcement.createdAt = rs.getTimestamp("created_at");
                announcements.add(announcement);
            }
            
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error fetching announcements: " + e.getMessage());
        }
        return announcements;
    }
    
    /**
     * Get announcements with read status for a user
     */
    public static List<AnnouncementInfo> getAnnouncementsForUser(int userId) {
        List<AnnouncementInfo> announcements = new ArrayList<>();
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return announcements;
            
            String query = "SELECT a.announcement_id, a.title, a.message, CONCAT(u.first_name, ' ', u.last_name) as admin_name, " +
                          "a.created_at, (ar.read_id IS NOT NULL) as isRead " +
                          "FROM announcements a " +
                          "JOIN users u ON a.admin_id = u.id " +
                          "LEFT JOIN announcement_read ar ON a.announcement_id = ar.announcement_id AND ar.user_id = ? " +
                          "ORDER BY a.created_at DESC LIMIT 20";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                AnnouncementInfo announcement = new AnnouncementInfo();
                announcement.id = rs.getInt("announcement_id");
                announcement.title = rs.getString("title");
                announcement.message = rs.getString("message");
                announcement.announcedBy = rs.getString("admin_name");
                announcement.createdAt = rs.getTimestamp("created_at");
                announcement.isRead = rs.getBoolean("isRead");
                announcements.add(announcement);
            }
            
            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error fetching announcements for user: " + e.getMessage());
        }
        return announcements;
    }
    
    /**
     * Get unread announcement count for a user
     */
    public static int getUnreadAnnouncementCount(int userId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;
            
            String query = "SELECT COUNT(*) as count FROM announcements a " +
                          "WHERE NOT EXISTS (SELECT 1 FROM announcement_read ar WHERE ar.user_id = ? AND ar.announcement_id = a.announcement_id)";
            
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
            System.err.println(TAG + " Error getting unread announcement count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Mark announcement as read
     */
    public static boolean markAnnouncementAsRead(int userId, int announcementId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            String query = "INSERT IGNORE INTO announcement_read (user_id, announcement_id) VALUES (?, ?)";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, announcementId);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error marking announcement as read: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an announcement
     */
    public static boolean deleteAnnouncement(int announcementId) {
        try {
            String query = "DELETE FROM announcements WHERE announcement_id = ?";
            
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, announcementId);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error deleting announcement: " + e.getMessage());
            return false;
        }
    }
}
