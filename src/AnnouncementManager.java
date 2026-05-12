package src;

import java.sql.*;
import java.util.*;

/**
 * AnnouncementManager.java
 * Manages system-wide announcements for all users
 * Features:
 * - Post announcements
 * - Retrieve announcements
 * - Mark as read
 * - Delete announcements
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
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println(TAG + " Error posting announcement: " + e.getMessage());
            return false;
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
