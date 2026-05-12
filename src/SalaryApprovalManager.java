package src;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * SalaryApprovalManager.java
 * Manages salary approval process - admin must approve before paying employees
 * Features:
 * - Create salary pending approval
 * - Approve/reject salaries
 * - Track approval history
 * - Update employee balance on approval
 */
public class SalaryApprovalManager {

    private static final String TAG = "[SalaryApprovalManager]";

    /**
     * Salary approval information
     */
    public static class SalaryApprovalInfo {
        public int approvalId;
        public int userId;
        public String employeeName;
        public double salaryAmount;
        public String status; // PENDING, APPROVED, REJECTED
        public LocalDateTime createdAt;
        public LocalDateTime approvedAt;
        public String approvedBy;
        public String reason;

        @Override
        public String toString() {
            return String.format("Employee: %s, Amount: %.2f, Status: %s", employeeName, salaryAmount, status);
        }
    }

    /**
     * Create salary approval table if it doesn't exist
     */
    public static void initializeSalaryApprovalTable() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS salary_approvals (" +
                    "approval_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "salary_amount DECIMAL(10, 2) NOT NULL," +
                    "status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "approved_at TIMESTAMP NULL," +
                    "approved_by INT," +
                    "reason TEXT," +
                    "notes TEXT," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (approved_by) REFERENCES users(id))";

            DBConnection.executeSafeUpdate(createTableSQL);
            System.out.println(TAG + " Salary approval table initialized successfully");
        } catch (Exception e) {
            System.err.println(TAG + " Failed to initialize salary approval table: " + e.getMessage());
        }
    }

    /**
     * Create a pending salary for approval
     */
    public static int createPendingSalary(int userId, double salaryAmount, String reason) {
        try {
            String query = "INSERT INTO salary_approvals (user_id, salary_amount, reason) VALUES (?, ?, ?)";
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;

            PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, userId);
            pst.setDouble(2, salaryAmount);
            pst.setString(3, reason != null ? reason : "Regular monthly salary");

            int result = pst.executeUpdate();
            if (result > 0) {
                ResultSet keys = pst.getGeneratedKeys();
                if (keys.next()) {
                    int approvalId = keys.getInt(1);
                    System.out.println(TAG + " Pending salary created - Approval ID: " + approvalId);

                    // Notify employee about pending salary
                    NotificationManager.createNotification(userId,
                            "SALARY_RELEASED",
                            "Salary Pending Approval",
                            "Your salary of PHP " + String.format("%.2f", salaryAmount) + " is pending approval.",
                            "SALARY_" + approvalId);

                    pst.close();
                    con.close();
                    return approvalId;
                }
            }

            pst.close();
            con.close();
            return 0;
        } catch (SQLException e) {
            System.err.println(TAG + " Error creating pending salary: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Approve a salary and credit to employee account
     */
    public static boolean approveSalary(int approvalId, int adminUserId) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) return false;

            con.setAutoCommit(false);

            // Get approval details
            String selectQuery = "SELECT user_id, salary_amount, status FROM salary_approvals WHERE approval_id = ? FOR UPDATE";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setInt(1, approvalId);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                con.rollback();
                System.err.println(TAG + " Approval not found - ID: " + approvalId);
                return false;
            }

            int userId = rs.getInt("user_id");
            double salaryAmount = rs.getDouble("salary_amount");
            String status = rs.getString("status");

            if (!"PENDING".equals(status)) {
                con.rollback();
                System.err.println(TAG + " Approval is not pending - Status: " + status);
                return false;
            }

            rs.close();
            selectStmt.close();

            // Update approval status
            String updateApprovalQuery = "UPDATE salary_approvals SET status = 'APPROVED', approved_at = NOW(), approved_by = ? WHERE approval_id = ?";
            PreparedStatement updateApprovalStmt = con.prepareStatement(updateApprovalQuery);
            updateApprovalStmt.setInt(1, adminUserId);
            updateApprovalStmt.setInt(2, approvalId);
            updateApprovalStmt.executeUpdate();
            updateApprovalStmt.close();

            // Update employee balance
            String updateBalanceQuery = "UPDATE employee SET balance = balance + ? WHERE user_id = ?";
            PreparedStatement updateBalanceStmt = con.prepareStatement(updateBalanceQuery);
            updateBalanceStmt.setDouble(1, salaryAmount);
            updateBalanceStmt.setInt(2, userId);
            int balanceResult = updateBalanceStmt.executeUpdate();
            updateBalanceStmt.close();

            if (balanceResult == 0) {
                con.rollback();
                System.err.println(TAG + " Failed to update employee balance");
                return false;
            }

            // Create notification
            NotificationManager.createNotification(userId,
                    "SALARY_RELEASED",
                    "Salary Approved and Released",
                    "Your salary of PHP " + String.format("%.2f", salaryAmount) + " has been approved and credited to your account.",
                    "SALARY_" + approvalId);

            con.commit();
            System.out.println(TAG + " Salary approved - Approval ID: " + approvalId + ", Amount: " + salaryAmount);
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(TAG + " Rollback failed: " + rollbackError.getMessage());
                }
            }
            System.err.println(TAG + " Error approving salary: " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println(TAG + " Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Reject a salary request
     */
    public static boolean rejectSalary(int approvalId, int adminUserId, String reason) {
        try {
            String query = "UPDATE salary_approvals SET status = 'REJECTED', approved_at = NOW(), approved_by = ?, reason = ? WHERE approval_id = ?";
            Connection con = DBConnection.getConnection();
            if (con == null) return false;

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, adminUserId);
            pst.setString(2, reason != null ? reason : "No reason provided");
            pst.setInt(3, approvalId);

            int result = pst.executeUpdate();
            if (result > 0) {
                // Get user ID for notification
                String getUserQuery = "SELECT user_id FROM salary_approvals WHERE approval_id = ?";
                PreparedStatement userStmt = con.prepareStatement(getUserQuery);
                userStmt.setInt(1, approvalId);
                ResultSet rs = userStmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    NotificationManager.createNotification(userId,
                            "SYSTEM",
                            "Salary Request Rejected",
                            "Your salary request has been rejected. Reason: " + reason,
                            "SALARY_" + approvalId);
                }
                rs.close();
                userStmt.close();

                pst.close();
                con.close();
                System.out.println(TAG + " Salary rejected - Approval ID: " + approvalId);
                return true;
            }

            pst.close();
            con.close();
            return false;
        } catch (SQLException e) {
            System.err.println(TAG + " Error rejecting salary: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all pending salary approvals
     */
    public static List<SalaryApprovalInfo> getPendingSalaries() {
        List<SalaryApprovalInfo> salaries = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return salaries;

            String query = "SELECT sa.approval_id, sa.user_id, u.first_name, u.last_name, sa.salary_amount, " +
                    "sa.status, sa.created_at, sa.reason FROM salary_approvals sa " +
                    "JOIN users u ON sa.user_id = u.id " +
                    "WHERE sa.status = 'PENDING' ORDER BY sa.created_at ASC";

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                SalaryApprovalInfo info = new SalaryApprovalInfo();
                info.approvalId = rs.getInt("approval_id");
                info.userId = rs.getInt("user_id");
                info.employeeName = rs.getString("first_name") + " " + rs.getString("last_name");
                info.salaryAmount = rs.getDouble("salary_amount");
                info.status = rs.getString("status");
                info.createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                info.reason = rs.getString("reason");
                salaries.add(info);
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error retrieving pending salaries: " + e.getMessage());
        }

        return salaries;
    }

    /**
     * Get all salary approvals for a user
     */
    public static List<SalaryApprovalInfo> getSalaryHistory(int userId) {
        List<SalaryApprovalInfo> salaries = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return salaries;

            String query = "SELECT approval_id, user_id, salary_amount, status, created_at, approved_at, reason " +
                    "FROM salary_approvals WHERE user_id = ? ORDER BY created_at DESC LIMIT 20";

            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                SalaryApprovalInfo info = new SalaryApprovalInfo();
                info.approvalId = rs.getInt("approval_id");
                info.userId = rs.getInt("user_id");
                info.salaryAmount = rs.getDouble("salary_amount");
                info.status = rs.getString("status");
                info.createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                Timestamp approvedAtTs = rs.getTimestamp("approved_at");
                if (approvedAtTs != null) {
                    info.approvedAt = approvedAtTs.toLocalDateTime();
                }
                info.reason = rs.getString("reason");
                salaries.add(info);
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error retrieving salary history: " + e.getMessage());
        }

        return salaries;
    }

    /**
     * Get pending salary count for admin dashboard
     */
    public static int getPendingSalaryCount() {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;

            String query = "SELECT COUNT(*) as count FROM salary_approvals WHERE status = 'PENDING'";
            PreparedStatement pst = con.prepareStatement(query);
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
            System.err.println(TAG + " Error getting pending salary count: " + e.getMessage());
            return 0;
        }
    }
}
