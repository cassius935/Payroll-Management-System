package src;

import java.sql.*;

/**
 * ProfileManager.java
 * Manages employee profile customization and personal information
 * Features:
 * - Get profile information
 * - Update profile nickname
 * - Update profile picture
 * - Get profile settings
 */
public class ProfileManager {
    
    private static final String TAG = "[ProfileManager]";
    
    public static class ProfileInfo {
        public int userId;
        public String firstName;
        public String lastName;
        public String nickname;
        public String email;
        public String department;
        public String position;
        public String bio;
        public String profileImage;
    }
    
    /**
     * Get profile information
     */
    public static ProfileInfo getProfile(int userId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return null;
            
            String query = "SELECT u.id, u.first_name, u.last_name, u.email, " +
                          "COALESCE(p.nickname, CONCAT(u.first_name, ' ', u.last_name)) as nickname, " +
                          "COALESCE(p.bio, '') as bio, COALESCE(p.profile_image, '') as profile_image, " +
                          "e.department, e.position FROM users u " +
                          "LEFT JOIN user_profiles p ON u.id = p.user_id " +
                          "LEFT JOIN employee e ON u.id = e.user_id WHERE u.id = ?";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            
            ProfileInfo profile = null;
            if (rs.next()) {
                profile = new ProfileInfo();
                profile.userId = rs.getInt("id");
                profile.firstName = rs.getString("first_name");
                profile.lastName = rs.getString("last_name");
                profile.nickname = rs.getString("nickname");
                profile.email = rs.getString("email");
                profile.bio = rs.getString("bio");
                profile.profileImage = rs.getString("profile_image");
                profile.department = rs.getString("department") != null ? rs.getString("department") : "N/A";
                profile.position = rs.getString("position") != null ? rs.getString("position") : "N/A";
            }
            
            rs.close();
            pst.close();
            con.close();
            
            return profile;
        } catch (SQLException e) {
            System.err.println(TAG + " Error getting profile: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Update profile nickname (doesn't affect SQL database name)
     */
    public static boolean updateNickname(int userId, String nickname) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            // Create table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS user_profiles (" +
                "user_id INT PRIMARY KEY, " +
                "nickname VARCHAR(255), " +
                "bio TEXT, " +
                "profile_image VARCHAR(500), " +
                "FOREIGN KEY (user_id) REFERENCES users(id))";
            
            Statement stmt = con.createStatement();
            stmt.execute(createTableSQL);
            stmt.close();
            
            // Insert or update nickname
            String query = "INSERT INTO user_profiles (user_id, nickname) VALUES (?, ?) " +
                          "ON DUPLICATE KEY UPDATE nickname = ?";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setString(2, nickname);
            pst.setString(3, nickname);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            System.out.println("[ProfileManager] Nickname updated to: " + nickname);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("[ProfileManager] Error updating nickname: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get display name (nickname if set, otherwise full name)
     */
    public static String getDisplayName(int userId) {
        ProfileInfo profile = getProfile(userId);
        if (profile != null && profile.nickname != null && !profile.nickname.isEmpty()) {
            return profile.nickname;
        }
        return profile != null ? profile.firstName + " " + profile.lastName : "User";
    }
    
    /**
     * Update profile bio
     */
    public static boolean updateBio(int userId, String bio) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            String query = "INSERT INTO user_profiles (user_id, bio) VALUES (?, ?) " +
                          "ON DUPLICATE KEY UPDATE bio = ?";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setString(2, bio);
            pst.setString(3, bio);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error updating bio: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update profile picture URL
     */
    public static boolean updateProfileImage(int userId, String imageUrl) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;
            
            String query = "INSERT INTO user_profiles (user_id, profile_image) VALUES (?, ?) " +
                          "ON DUPLICATE KEY UPDATE profile_image = ?";
            
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setString(2, imageUrl);
            pst.setString(3, imageUrl);
            
            int result = pst.executeUpdate();
            pst.close();
            con.close();
            
            return result > 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error updating profile image: " + e.getMessage());
            return false;
        }
    }
}
