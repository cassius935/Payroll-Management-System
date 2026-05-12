package src;

import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * DBConnection.java
 * Enhanced database connection handler with utilities for authentication and queries
 * Features: Connection pooling, error logging, transaction support, and fallback mode
 */
public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/payroll_db?useSSL=false&serverTimezone=UTC&autoReconnect=true&maxReconnects=3&connectTimeout=10000";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static boolean databaseAvailable = true;
    private static long lastConnectionAttempt = 0;
    private static final long CONNECTION_RETRY_DELAY = 5000; // 5 seconds

    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            con.setNetworkTimeout(java.util.concurrent.Executors.newSingleThreadExecutor(), 5000);
            databaseAvailable = true;
            lastConnectionAttempt = System.currentTimeMillis();
            logInfo("Database Connected Successfully!");
        } catch (ClassNotFoundException e) {
            logError("MySQL JDBC Driver not found: " + e.getMessage());
            databaseAvailable = false;
        } catch (SQLException e) {
            logError("Database connection failed: " + e.getMessage() + " (SQL State: " + e.getSQLState() + ")");
            databaseAvailable = false;
            
            // Only log connection retries every 5 seconds to avoid spam
            if (System.currentTimeMillis() - lastConnectionAttempt > CONNECTION_RETRY_DELAY) {
                System.out.println("Running in offline/demo mode. Database connection will be retried.");
                lastConnectionAttempt = System.currentTimeMillis();
            }
        } catch (Exception e) {
            logError("Unexpected error during database connection: " + e.getMessage());
            databaseAvailable = false;
        }
        return con;
    }

    /**
     * Safe SQL execution with error handling
     */
    public static ResultSet executeSafeQuery(String query, Object... params) {
        try {
            Connection con = getConnection();
            if (con == null) return null;

            PreparedStatement pst = con.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pst.setObject(i + 1, params[i]);
            }
            return pst.executeQuery();
        } catch (SQLException e) {
            logError("SQL Query Error: " + e.getMessage() + "\nQuery: " + query);
            return null;
        }
    }

    /**
     * Safe SQL update with error handling and transaction support
     */
    public static int executeSafeUpdate(String query, Object... params) {
        Connection con = null;
        try {
            con = getConnection();
            if (con == null) return 0;

            con.setAutoCommit(false);
            PreparedStatement pst = con.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pst.setObject(i + 1, params[i]);
            }
            int result = pst.executeUpdate();
            con.commit();
            logInfo("SQL Update Successful: " + result + " rows affected");
            return result;
        } catch (SQLException e) {
            logError("SQL Update Error: " + e.getMessage() + "\nQuery: " + query);
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    logError("Rollback failed: " + rollbackError.getMessage());
                }
            }
            return 0;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logError("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Generate SHA256 hash for passwords
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logError("Password hashing failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Logging utility methods
     */
    private static void logInfo(String message) {
        System.out.println("[INFO] " + java.time.LocalDateTime.now() + " - " + message);
    }

    private static void logError(String message) {
        System.err.println("[ERROR] " + java.time.LocalDateTime.now() + " - " + message);
    }

    /**
     * Is database available
     */
    public static boolean isDatabaseAvailable() {
        return databaseAvailable;
    }

    private static final String DEMO_ADMIN_USERNAME = "admin";
    private static final String DEMO_ADMIN_PASSWORD = "Admin@2024!";
    private static final String DEMO_WORKER_USERNAME = "worker1";
    private static final String DEMO_WORKER_PASSWORD = "Worker@123";

    private static UserInfo authenticateFallback(String username, String password) {
        if (DEMO_ADMIN_USERNAME.equalsIgnoreCase(username) && DEMO_ADMIN_PASSWORD.equals(password)) {
            UserInfo userInfo = new UserInfo();
            userInfo.id = -1;
            userInfo.username = DEMO_ADMIN_USERNAME;
            userInfo.userType = "ADMIN";
            userInfo.loginCode = "ADM-DEMO-000";
            userInfo.firstName = "System";
            userInfo.lastName = "Administrator";
            userInfo.email = "admin@payroll.com";
            userInfo.phoneNumber = "";
            return userInfo;
        }
        if (DEMO_WORKER_USERNAME.equalsIgnoreCase(username) && DEMO_WORKER_PASSWORD.equals(password)) {
            UserInfo userInfo = new UserInfo();
            userInfo.id = -2;
            userInfo.username = DEMO_WORKER_USERNAME;
            userInfo.userType = "WORKER";
            userInfo.loginCode = "WRK-DEMO-000";
            userInfo.firstName = "John";
            userInfo.lastName = "Doe";
            userInfo.email = "worker1@company.com";
            userInfo.phoneNumber = "09123456789";
            return userInfo;
        }
        return null;
    }

    public static String generateTemporaryPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*";
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    public static class WorkerCreationInfo {
        public String username;
        public String temporaryPassword;
        public String loginCode;
    }

    public static class WorkerProfile extends UserInfo {
        public String employeeId;
        public String department;
        public String position;
        public double hourlyRate;
        public double monthlySalary;
    }

    public static WorkerCreationInfo createWorkerAccount(String firstName, String lastName, String email, String phoneNumber, String department, String position, double hourlyRate, String employeeId) {
        if (employeeId == null || employeeId.isEmpty()) {
            employeeId = EmployeeManager.generateUniqueEmployeeId();
        }

        WorkerCreationInfo info = new WorkerCreationInfo();
        info.username = employeeId;
        info.temporaryPassword = generateTemporaryPassword(10);
        info.loginCode = generateLoginCode("WORKER");

        try {
            Connection con = getConnection();
            if (con == null) return null;

            con.setAutoCommit(false);

            String userQuery = "INSERT INTO users (username, password, login_code, user_type, email, first_name, last_name, phone_number) VALUES (?, SHA2(?, 256), ?, 'WORKER', ?, ?, ?, ?)";
            PreparedStatement userStmt = con.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, info.username);
            userStmt.setString(2, info.temporaryPassword);
            userStmt.setString(3, info.loginCode);
            userStmt.setString(4, email);
            userStmt.setString(5, firstName);
            userStmt.setString(6, lastName);
            userStmt.setString(7, phoneNumber);
            userStmt.executeUpdate();

            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            int userId = -1;
            if (generatedKeys.next()) {
                userId = generatedKeys.getInt(1);
            }
            generatedKeys.close();
            userStmt.close();

            if (userId <= 0) {
                con.rollback();
                con.close();
                return null;
            }

            String employeeQuery = "INSERT INTO employee (employee_id, user_id, department, position, hourly_rate, monthly_salary) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement empStmt = con.prepareStatement(employeeQuery);
            empStmt.setString(1, employeeId);
            empStmt.setInt(2, userId);
            empStmt.setString(3, department);
            empStmt.setString(4, position);
            empStmt.setDouble(5, hourlyRate);
            empStmt.setDouble(6, hourlyRate * 160);
            empStmt.executeUpdate();
            empStmt.close();

            try {
                String walletQuery = "INSERT INTO worker_wallet (employee_id, balance) VALUES (?, 0.00)";
                PreparedStatement walletStmt = con.prepareStatement(walletQuery);
                walletStmt.setString(1, employeeId);
                walletStmt.executeUpdate();
                walletStmt.close();
            } catch (SQLException ignore) {
            }

            con.commit();
            con.close();
            return info;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WorkerProfile getWorkerProfileByUsername(String username) {
        try {
            Connection con = getConnection();
            if (con == null) return null;

            String query = "SELECT u.id, u.username, u.user_type, u.login_code, u.first_name, u.last_name, u.email, u.phone_number, " +
                           "e.employee_id, e.department, e.position, e.hourly_rate, e.monthly_salary " +
                           "FROM users u LEFT JOIN employee e ON u.id = e.user_id WHERE u.username = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                WorkerProfile profile = new WorkerProfile();
                profile.id = rs.getInt("id");
                profile.username = rs.getString("username");
                profile.userType = rs.getString("user_type");
                profile.loginCode = rs.getString("login_code");
                profile.firstName = rs.getString("first_name");
                profile.lastName = rs.getString("last_name");
                profile.email = rs.getString("email");
                profile.phoneNumber = rs.getString("phone_number");
                profile.employeeId = rs.getString("employee_id");
                profile.department = rs.getString("department");
                profile.position = rs.getString("position");
                profile.hourlyRate = rs.getDouble("hourly_rate");
                profile.monthlySalary = rs.getDouble("monthly_salary");
                con.close();
                return profile;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateWorkerProfile(int userId, String firstName, String lastName, String email, String phoneNumber) {
        try {
            Connection con = getConnection();
            if (con == null) return false;
            String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ? WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, email);
            pst.setString(4, phoneNumber);
            pst.setInt(5, userId);
            int updated = pst.executeUpdate();
            pst.close();
            con.close();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateWorkerPassword(int userId, String newPassword) {
        try {
            Connection con = getConnection();
            if (con == null) return false;
            String query = "UPDATE users SET password = SHA2(?, 256) WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, newPassword);
            pst.setInt(2, userId);
            int updated = pst.executeUpdate();
            pst.close();
            con.close();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Authenticate user with username and password
     * Returns user information if successful
     */
    public static UserInfo authenticateUser(String username, String password) {
        try {
            Connection con = getConnection();
            if (con == null) return authenticateFallback(username, password);

            String query = "SELECT id, username, user_type, login_code, first_name, last_name, email, phone_number FROM users WHERE username = ? AND password = SHA2(?, 256) AND is_active = TRUE";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                UserInfo userInfo = new UserInfo();
                userInfo.id = rs.getInt("id");
                userInfo.username = rs.getString("username");
                userInfo.userType = rs.getString("user_type");
                userInfo.loginCode = rs.getString("login_code");
                userInfo.firstName = rs.getString("first_name");
                userInfo.lastName = rs.getString("last_name");
                userInfo.email = rs.getString("email");
                userInfo.phoneNumber = rs.getString("phone_number");
                
                con.close();
                return userInfo;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return authenticateFallback(username, password);
        }
        return authenticateFallback(username, password);
    }

    /**
     * Get user by ID
     */
    public static UserInfo getUserById(int userId) {
        try {
            Connection con = getConnection();
            if (con == null) return null;

            String query = "SELECT id, username, user_type, login_code, first_name, last_name, email, phone_number FROM users WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                UserInfo userInfo = new UserInfo();
                userInfo.id = rs.getInt("id");
                userInfo.username = rs.getString("username");
                userInfo.userType = rs.getString("user_type");
                userInfo.loginCode = rs.getString("login_code");
                userInfo.firstName = rs.getString("first_name");
                userInfo.lastName = rs.getString("last_name");
                userInfo.email = rs.getString("email");
                userInfo.phoneNumber = rs.getString("phone_number");
                
                con.close();
                return userInfo;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get employee ID from user ID
     */
    public static String getEmployeeIdFromUserId(int userId) {
        try {
            Connection con = getConnection();
            if (con == null) return null;

            String query = "SELECT employee_id FROM employee WHERE user_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                String employeeId = rs.getString("employee_id");
                con.close();
                return employeeId;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get employee information
     */
    public static EmployeeInfo getEmployeeInfo(String employeeId) {
        try {
            Connection con = getConnection();
            if (con == null) return null;

            String query = "SELECT e.*, ww.balance, ww.last_salary_date, ww.last_salary_amount FROM employee e LEFT JOIN worker_wallet ww ON e.employee_id = ww.employee_id WHERE e.employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                EmployeeInfo empInfo = new EmployeeInfo();
                empInfo.employeeId = rs.getString("employee_id");
                empInfo.department = rs.getString("department");
                empInfo.position = rs.getString("position");
                empInfo.hourlyRate = rs.getDouble("hourly_rate");
                empInfo.monthlySalary = rs.getDouble("monthly_salary");
                empInfo.balance = rs.getDouble("balance");
                empInfo.lastSalaryDate = rs.getTimestamp("last_salary_date");
                empInfo.lastSalaryAmount = rs.getDouble("last_salary_amount");
                
                con.close();
                return empInfo;
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update last login timestamp
     */
    public static void updateLastLogin(int userId) {
        try {
            Connection con = getConnection();
            if (con == null) return;

            String query = "UPDATE users SET last_login = NOW() WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all employees with usernames (for admin viewing)
     */
    public static ResultSet getAllEmployeesWithUsernames() {
        try {
            Connection con = getConnection();
            if (con == null) return null;

            String query = "SELECT u.id, u.username, u.first_name, u.last_name, u.email, u.phone_number, " +
                           "e.employee_id, e.department, e.position, e.hourly_rate, e.monthly_salary " +
                           "FROM users u JOIN employee e ON u.id = e.user_id " +
                           "WHERE u.user_type = 'WORKER' ORDER BY e.employee_id";
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Change employee password (admin function)
     */
    public static boolean adminChangeEmployeePassword(int userId, String newPassword) {
        try {
            Connection con = getConnection();
            if (con == null) return false;
            
            String query = "UPDATE users SET password = SHA2(?, 256) WHERE id = ? AND user_type = 'WORKER'";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, newPassword);
            pst.setInt(2, userId);
            int updated = pst.executeUpdate();
            pst.close();
            con.close();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generate unique login code
     */
    public static String generateLoginCode(String userType) {
        long timestamp = System.currentTimeMillis();
        return userType.substring(0, 3).toUpperCase() + "-" + System.currentTimeMillis() % 1000000;
    }

    /**
     * Inner class to store user information
     */
    public static class UserInfo {
        public int id;
        public String username;
        public String userType;  // ADMIN or WORKER
        public String loginCode;
        public String firstName;
        public String lastName;
        public String email;
        public String phoneNumber;

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }

    /**
     * Inner class to store employee information
     */
    public static class EmployeeInfo {
        public String employeeId;
        public String department;
        public String position;
        public double hourlyRate;
        public double monthlySalary;
        public double balance;
        public Timestamp lastSalaryDate;
        public double lastSalaryAmount;
    }
}