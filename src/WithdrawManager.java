package src;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WithdrawManager.java
 * Manages employee withdrawals with complete transaction tracking
 * Features:
 * - Process withdrawals with balance validation
 * - Generate receipts
 * - Track withdrawal history
 * - Update employee balance
 */
public class WithdrawManager {

    private static final String TAG = "[WithdrawManager]";

    /**
     * Withdrawal transaction information
     */
    public static class WithdrawalInfo {
        public int withdrawalId;
        public int userId;
        public double withdrawAmount;
        public double balanceAfter;
        public LocalDateTime withdrawalDate;
        public String status; // APPROVED, PENDING, REJECTED
        public String receiptNumber;

        @Override
        public String toString() {
            return String.format("Withdrawal ID: %d, Amount: %.2f, Balance After: %.2f, Date: %s",
                    withdrawalId, withdrawAmount, balanceAfter, withdrawalDate);
        }
    }

    /**
     * Create withdrawal table if it doesn't exist
     */
    public static void initializeWithdrawalTable() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS withdrawals (" +
                    "withdrawal_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "user_id INT NOT NULL," +
                    "withdrawal_amount DECIMAL(10, 2) NOT NULL," +
                    "balance_before DECIMAL(10, 2) NOT NULL," +
                    "balance_after DECIMAL(10, 2) NOT NULL," +
                    "status ENUM('APPROVED', 'PENDING', 'REJECTED') DEFAULT 'PENDING'," +
                    "receipt_number VARCHAR(50) UNIQUE," +
                    "withdrawal_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "processed_at TIMESTAMP NULL," +
                    "notes TEXT," +
                    "FOREIGN KEY (user_id) REFERENCES users(id))";

            DBConnection.executeSafeUpdate(createTableSQL);
            System.out.println(TAG + " Withdrawal table initialized successfully");
        } catch (Exception e) {
            System.err.println(TAG + " Failed to initialize withdrawal table: " + e.getMessage());
        }
    }

    /**
     * Process a withdrawal request
     * @return WithdrawalInfo object or null if failed
     */
    public static WithdrawalInfo processWithdrawal(int userId, double withdrawAmount) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) return null;

            con.setAutoCommit(false);

            // Get current balance
            String balanceQuery = "SELECT balance FROM employee WHERE user_id = ? FOR UPDATE";
            PreparedStatement balanceStmt = con.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, userId);
            ResultSet balanceRs = balanceStmt.executeQuery();

            if (!balanceRs.next()) {
                con.rollback();
                System.err.println(TAG + " Employee not found for user ID: " + userId);
                return null;
            }

            double currentBalance = balanceRs.getDouble("balance");
            balanceRs.close();
            balanceStmt.close();

            // Validate withdrawal amount
            if (withdrawAmount <= 0) {
                con.rollback();
                System.err.println(TAG + " Invalid withdrawal amount: " + withdrawAmount);
                return null;
            }

            if (withdrawAmount > currentBalance) {
                con.rollback();
                System.err.println(TAG + " Insufficient balance. Available: " + currentBalance + ", Requested: " + withdrawAmount);
                return null;
            }

            // Calculate new balance
            double newBalance = currentBalance - withdrawAmount;

            // Generate receipt number
            String receiptNumber = generateReceiptNumber(userId);

            // Insert withdrawal record
            String insertQuery = "INSERT INTO withdrawals (user_id, withdrawal_amount, balance_before, balance_after, status, receipt_number) " +
                    "VALUES (?, ?, ?, ?, 'APPROVED', ?)";
            PreparedStatement insertStmt = con.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            insertStmt.setInt(1, userId);
            insertStmt.setDouble(2, withdrawAmount);
            insertStmt.setDouble(3, currentBalance);
            insertStmt.setDouble(4, newBalance);
            insertStmt.setString(5, receiptNumber);

            int result = insertStmt.executeUpdate();
            if (result == 0) {
                con.rollback();
                System.err.println(TAG + " Failed to insert withdrawal record");
                return null;
            }

            ResultSet generatedKeys = insertStmt.getGeneratedKeys();
            int withdrawalId = 0;
            if (generatedKeys.next()) {
                withdrawalId = generatedKeys.getInt(1);
            }
            insertStmt.close();

            // Update employee balance
            String updateBalanceQuery = "UPDATE employee SET balance = balance - ? WHERE user_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateBalanceQuery);
            updateStmt.setDouble(1, withdrawAmount);
            updateStmt.setInt(2, userId);

            int updateResult = updateStmt.executeUpdate();
            if (updateResult == 0) {
                con.rollback();
                System.err.println(TAG + " Failed to update employee balance");
                return null;
            }
            updateStmt.close();

            // Create notification for user
            String title = "Withdrawal Successful";
            String message = "You have successfully withdrawn PHP " + String.format("%.2f", withdrawAmount) +
                    ". Receipt #: " + receiptNumber;
            NotificationManager.createNotification(userId, "SALARY_RELEASED", title, message, receiptNumber);

            con.commit();

            // Return withdrawal info
            WithdrawalInfo info = new WithdrawalInfo();
            info.withdrawalId = withdrawalId;
            info.userId = userId;
            info.withdrawAmount = withdrawAmount;
            info.balanceAfter = newBalance;
            info.withdrawalDate = LocalDateTime.now();
            info.status = "APPROVED";
            info.receiptNumber = receiptNumber;

            System.out.println(TAG + " Withdrawal processed successfully - Receipt: " + receiptNumber);
            return info;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException rollbackError) {
                    System.err.println(TAG + " Rollback failed: " + rollbackError.getMessage());
                }
            }
            System.err.println(TAG + " Error processing withdrawal: " + e.getMessage());
            return null;
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
     * Generate unique receipt number
     */
    private static String generateReceiptNumber(int userId) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "WD-" + userId + "-" + now.format(formatter);
    }

    /**
     * Get withdrawal history for a user
     */
    public static java.util.List<WithdrawalInfo> getWithdrawalHistory(int userId, int limit) {
        java.util.List<WithdrawalInfo> withdrawals = new java.util.ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return withdrawals;

            String query = "SELECT withdrawal_id, user_id, withdrawal_amount, balance_after, " +
                    "status, receipt_number, withdrawal_date FROM withdrawals " +
                    "WHERE user_id = ? ORDER BY withdrawal_date DESC LIMIT ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, limit);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                WithdrawalInfo info = new WithdrawalInfo();
                info.withdrawalId = rs.getInt("withdrawal_id");
                info.userId = rs.getInt("user_id");
                info.withdrawAmount = rs.getDouble("withdrawal_amount");
                info.balanceAfter = rs.getDouble("balance_after");
                info.status = rs.getString("status");
                info.receiptNumber = rs.getString("receipt_number");
                info.withdrawalDate = rs.getTimestamp("withdrawal_date").toLocalDateTime();
                withdrawals.add(info);
            }

            rs.close();
            pst.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(TAG + " Error retrieving withdrawal history: " + e.getMessage());
        }

        return withdrawals;
    }

    /**
     * Get total withdrawals for a user in a period
     */
    public static double getTotalWithdrawals(int userId, int days) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return 0;

            String query = "SELECT SUM(withdrawal_amount) as total FROM withdrawals " +
                    "WHERE user_id = ? AND status = 'APPROVED' AND " +
                    "withdrawal_date >= DATE_SUB(NOW(), INTERVAL ? DAY)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setInt(1, userId);
            pst.setInt(2, days);

            ResultSet rs = pst.executeQuery();
            double total = 0;
            if (rs.next()) {
                total = rs.getDouble("total");
            }

            rs.close();
            pst.close();
            con.close();
            return total;
        } catch (SQLException e) {
            System.err.println(TAG + " Error getting total withdrawals: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Cancel a withdrawal (admin only)
     */
    public static boolean cancelWithdrawal(int withdrawalId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) return false;

            con.setAutoCommit(false);

            // Get withdrawal details
            String selectQuery = "SELECT user_id, withdrawal_amount, status FROM withdrawals WHERE withdrawal_id = ? FOR UPDATE";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setInt(1, withdrawalId);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                con.rollback();
                return false;
            }

            int userId = rs.getInt("user_id");
            double withdrawAmount = rs.getDouble("withdrawal_amount");
            String status = rs.getString("status");

            if (!"APPROVED".equals(status)) {
                con.rollback();
                return false;
            }

            rs.close();
            selectStmt.close();

            // Update withdrawal status
            String updateQuery = "UPDATE withdrawals SET status = 'REJECTED', processed_at = NOW() WHERE withdrawal_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateQuery);
            updateStmt.setInt(1, withdrawalId);
            updateStmt.executeUpdate();
            updateStmt.close();

            // Refund balance
            String refundQuery = "UPDATE employee SET balance = balance + ? WHERE user_id = ?";
            PreparedStatement refundStmt = con.prepareStatement(refundQuery);
            refundStmt.setDouble(1, withdrawAmount);
            refundStmt.setInt(2, userId);
            refundStmt.executeUpdate();
            refundStmt.close();

            con.commit();
            System.out.println(TAG + " Withdrawal cancelled and refunded - Withdrawal ID: " + withdrawalId);
            return true;

        } catch (SQLException e) {
            System.err.println(TAG + " Error cancelling withdrawal: " + e.getMessage());
            return false;
        }
    }
}
