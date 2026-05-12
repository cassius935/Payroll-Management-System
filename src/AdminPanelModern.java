package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * AdminPanelModern.java
 * Modern administrator dashboard with salary management, notifications, and worker management
 */
public class AdminPanelModern extends JFrame {
    private DBConnection.UserInfo adminUser;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;
    private JPanel dashboardPanel;
    private JLabel notificationBadgeLabel;
    private int messageCount = 0;

    public AdminPanelModern(DBConnection.UserInfo adminUser) {
        this.adminUser = adminUser;

        // Frame setup
        setTitle("Payroll System - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(true);

        UITheme.applyTheme(this);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Create header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Create tabbed interface
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UITheme.BG_LIGHT);

        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());
        tabbedPane.addTab("👥 Manage Employees", createEmployeeManagementPanel());
        tabbedPane.addTab("💰 Salary Management", createSalaryManagementPanel());
        tabbedPane.addTab("📬 Messages (" + messageCount + ")", createMessagesPanel());
        tabbedPane.addTab("📱 E-Wallet Integration", createEWalletPanel());
        tabbedPane.addTab("📄 Bill Management", createBillManagementPanel());
        tabbedPane.addTab("✓ Attendance", createAttendancePanel());
        
        // Update message count
        updateMessageCount();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
    
    /**
     * Update message count from database
     */
    private void updateMessageCount() {
        try {
            messageCount = MessageManager.getUnreadCount(adminUser.id);
            if (tabbedPane.getTabCount() > 3) {
                tabbedPane.setTitleAt(3, "📬 Messages (" + messageCount + ")");
            }
            if (notificationBadgeLabel != null) {
                if (messageCount > 0) {
                    notificationBadgeLabel.setText("🔔 (" + messageCount + ")");
                    notificationBadgeLabel.setForeground(UITheme.DANGER_RED);
                } else {
                    notificationBadgeLabel.setText("🔔");
                    notificationBadgeLabel.setForeground(Color.WHITE);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update message count: " + e.getMessage());
        }
    }

    /**
     * Create header panel with welcome message and logout
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("🏢 PAYROLL MANAGEMENT SYSTEM - ADMIN");
        titleLabel.setFont(UITheme.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);

        welcomeLabel = new JLabel("Welcome, " + adminUser.getFullName() + "!");
        welcomeLabel.setFont(UITheme.FONT_REGULAR);
        welcomeLabel.setForeground(new Color(200, 220, 240));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(titleLabel);
        leftPanel.add(welcomeLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        
        notificationBadgeLabel = new JLabel("🔔");
        notificationBadgeLabel.setFont(UITheme.FONT_HEADING);
        notificationBadgeLabel.setForeground(Color.WHITE);
        notificationBadgeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationBadgeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showNotificationPanel();
            }
        });
        
        JButton logoutBtn = UITheme.createModernButton("Logout", UITheme.DANGER_RED);
        logoutBtn.addActionListener(e -> logout());

        rightPanel.add(notificationBadgeLabel);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(logoutBtn);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    
    /**
     * Show notification/message panel
     */
    private void showNotificationPanel() {
        JFrame notifFrame = new JFrame("Messages & Notifications");
        notifFrame.setSize(550, 650);
        notifFrame.setLocationRelativeTo(this);
        notifFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Messages section
        JLabel msgTitle = new JLabel("📬 Worker Messages (" + messageCount + ")");
        msgTitle.setFont(UITheme.FONT_SUBHEADING);
        msgTitle.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(msgTitle);
        panel.add(Box.createVerticalStrut(10));
        
        List<MessageManager.MessageInfo> messages = 
            MessageManager.getUnreadMessages(adminUser.id, 15);
        
        if (messages.isEmpty()) {
            JLabel noMsgLabel = new JLabel("No new messages from workers");
            noMsgLabel.setFont(UITheme.FONT_REGULAR);
            noMsgLabel.setForeground(UITheme.TEXT_SECONDARY);
            panel.add(noMsgLabel);
        } else {
            for (MessageManager.MessageInfo msg : messages) {
                JPanel msgCard = UITheme.createCardPanel();
                msgCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                msgCard.setLayout(new BoxLayout(msgCard, BoxLayout.Y_AXIS));
                
                JLabel fromLabel = new JLabel("From: " + msg.senderName);
                fromLabel.setFont(UITheme.FONT_BOLD);
                fromLabel.setForeground(UITheme.PRIMARY_BLUE);
                
                JLabel subjLabel = new JLabel("Subject: " + msg.subject);
                subjLabel.setFont(UITheme.FONT_REGULAR);
                subjLabel.setForeground(UITheme.TEXT_PRIMARY);
                
                JLabel textLabel = new JLabel(msg.text);
                textLabel.setFont(UITheme.FONT_SMALL);
                textLabel.setForeground(UITheme.TEXT_SECONDARY);
                
                msgCard.add(fromLabel);
                msgCard.add(subjLabel);
                msgCard.add(textLabel);
                panel.add(msgCard);
                panel.add(Box.createVerticalStrut(5));
            }
        }
        
        panel.add(Box.createVerticalGlue());
        
        JButton refreshBtn = UITheme.createModernButton("Refresh", UITheme.SUCCESS_GREEN);
        refreshBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshBtn.addActionListener(e -> {
            updateMessageCount();
            notifFrame.dispose();
            showNotificationPanel();
        });
        panel.add(refreshBtn);
        
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBackground(UITheme.BG_LIGHT);
        notifFrame.add(scrollPane);
        notifFrame.setVisible(true);
    }

    /**
     * Create dashboard with overview
     */
    private JScrollPane createDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        // Quick stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        statsPanel.add(createStatCard("👥 Total Employees", "5", UITheme.PRIMARY_BLUE));
        statsPanel.add(createStatCard("💼 Active Today", "4", UITheme.SECONDARY_TEAL));
        statsPanel.add(createStatCard("💰 Pending Salaries", "2", UITheme.WARNING_YELLOW));
        statsPanel.add(createStatCard("📢 Unread Messages", "3", UITheme.ACCENT_ORANGE));

        panel.add(statsPanel);
        panel.add(Box.createVerticalStrut(20));

        // Recent activities
        JLabel recentLabel = new JLabel("Recent Activities");
        recentLabel.setFont(UITheme.FONT_SUBHEADING);
        recentLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(recentLabel);
        panel.add(Box.createVerticalStrut(10));

        String[] activities = {
            "📝 New employee registered: Maria Santos",
            "💸 Salary released to 2 employees",
            "⚠️ 1 employee absent today",
            "✉️ Message from worker1: Please verify my e-wallet",
            "📱 E-wallet payment received: GCash - P500"
        };

        for (String activity : activities) {
            JLabel activityLabel = new JLabel(activity);
            activityLabel.setFont(UITheme.FONT_REGULAR);
            activityLabel.setForeground(UITheme.TEXT_SECONDARY);
            JPanel activityItem = UITheme.createCardPanel();
            activityItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            activityItem.add(activityLabel);
            panel.add(activityItem);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create a stat card
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = UITheme.createBalanceCard(title, value, color);
        card.setPreferredSize(new Dimension(200, 120));
        return card;
    }

    /**
     * Create employee management panel
     */
    private JPanel createEmployeeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Search Employee:");
        JTextField searchField = UITheme.createModernTextField(20);
        JButton searchBtn = UITheme.createModernButton("Search", UITheme.PRIMARY_BLUE);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        // Employee list
        String[] columns = {"ID", "Name", "Position", "Department", "Status", "Action"};
        Object[][] data = loadEmployeeData();

        JTable employeeTable = new JTable(data, columns);
        employeeTable.setFont(UITheme.FONT_REGULAR);
        employeeTable.setRowHeight(25);
        employeeTable.setBackground(UITheme.BG_WHITE);
        employeeTable.setGridColor(UITheme.BORDER_GRAY);

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton addBtn = UITheme.createModernButton("+ Add Employee", UITheme.SUCCESS_GREEN);
        JButton editBtn = UITheme.createModernButton("Edit", UITheme.PRIMARY_BLUE);
        JButton deleteBtn = UITheme.createModernButton("Delete", UITheme.DANGER_RED);

        addBtn.addActionListener(e -> showAddEmployeeDialog());
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create salary management panel
     */
    private JScrollPane createSalaryManagementPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("💰 Salary Management");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(titleLabel, gbc);

        // Employee selection
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Select Employee:"), gbc);
        JComboBox<String> employeeCombo = new JComboBox<>(getEmployeeNames());
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(employeeCombo, gbc);

        // Salary amount
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Salary Amount (PHP):"), gbc);
        JTextField salaryField = UITheme.createModernTextField(20);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(salaryField, gbc);

        // Absences
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Absences (days):"), gbc);
        JSpinner absenceSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 30, 1));
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(absenceSpinner, gbc);

        // Deduction amount
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Deduction per Absence (PHP):"), gbc);
        JTextField deductionField = UITheme.createModernTextField(20);
        deductionField.setText("500.00");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(deductionField, gbc);

        // Net salary display
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Net Salary:"), gbc);
        JLabel netSalaryLabel = new JLabel("PHP 0.00");
        netSalaryLabel.setFont(UITheme.FONT_HEADING);
        netSalaryLabel.setForeground(UITheme.SUCCESS_GREEN);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(netSalaryLabel, gbc);

        // Release salary button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton releaseBtn = UITheme.createModernButton("💳 Release Salary", UITheme.SUCCESS_GREEN);
        releaseBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        releaseBtn.addActionListener(e -> {
            String employee = (String) employeeCombo.getSelectedItem();
            String salaryText = salaryField.getText();
            int absences = (Integer) absenceSpinner.getValue();
            try {
                double salary = Double.parseDouble(salaryText);
                releaseSalary(employee, salary, absences);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid salary amount");
            }
        });
        panel.add(releaseBtn, gbc);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create messages and notifications panel
     */
    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Messages list
        String[] columns = {"From", "Subject", "Date", "Status"};
        Object[][] data = {
            {"John Doe", "Salary Slip Request", "2024-05-01", "Unread"},
            {"Jane Smith", "E-wallet Connection Issue", "2024-04-30", "Read"},
            {"Maria Santos", "Attendance Query", "2024-04-29", "Read"}
        };

        JTable messagesTable = new JTable(data, columns);
        messagesTable.setFont(UITheme.FONT_REGULAR);
        messagesTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(messagesTable);

        // Compose button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton composeBtn = UITheme.createModernButton("📝 Compose Message", UITheme.PRIMARY_BLUE);
        JButton notifyBtn = UITheme.createModernButton("🔔 Send Notification", UITheme.ACCENT_ORANGE);

        composeBtn.addActionListener(e -> showComposeMessageDialog());
        notifyBtn.addActionListener(e -> showNotificationDialog());

        buttonPanel.add(composeBtn);
        buttonPanel.add(notifyBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create E-wallet integration panel
     */
    private JScrollPane createEWalletPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("📱 E-Wallet Integration");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // E-wallet options
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        String[] wallets = {"PayMaya", "GCash", "PayPal"};
        JComboBox<String> walletCombo = new JComboBox<>(wallets);
        panel.add(new JLabel("Select E-Wallet Provider:"), gbc);
        gbc.gridx = 1;
        panel.add(walletCombo, gbc);

        // Account identifier
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Account/Phone Number:"), gbc);
        JTextField accountField = UITheme.createModernTextField(20);
        gbc.gridx = 1;
        panel.add(accountField, gbc);

        // Verify button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton verifyBtn = UITheme.createModernButton("✓ Verify Account", UITheme.SUCCESS_GREEN);
        verifyBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(verifyBtn, gbc);

        // Active wallets
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel activeLabel = new JLabel("Active E-Wallet Accounts");
        activeLabel.setFont(UITheme.FONT_SUBHEADING);
        panel.add(activeLabel, gbc);

        String[] walletColumns = {"Provider", "Account", "Status", "Action"};
        Object[][] walletData = {
            {"PayMaya", "09123456789", "✓ Verified", "Edit"},
            {"GCash", "09987654321", "⏳ Pending", "Verify"}
        };

        JTable walletTable = new JTable(walletData, walletColumns);
        walletTable.setFont(UITheme.FONT_REGULAR);
        gbc.gridy = 5;
        JScrollPane scrollPane = new JScrollPane(walletTable);
        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.add(scrollPane, gbc);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create bill management panel
     */
    private JPanel createBillManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Bill types
        JPanel filterPanel = new JPanel();
        filterPanel.setOpaque(false);
        JLabel filterLabel = new JLabel("Bill Type:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"All", "Water", "Electric", "Other"});
        filterPanel.add(filterLabel);
        filterPanel.add(typeCombo);

        // Bills table
        String[] columns = {"Employee", "Bill Type", "Amount", "Due Date", "Status", "Action"};
        Object[][] data = {
            {"John Doe", "Electric", "1,500.00", "2024-05-10", "Pending", "Process"},
            {"Jane Smith", "Water", "850.00", "2024-05-15", "Paid", "View"},
            {"Maria Santos", "Electric", "1,200.00", "2024-05-10", "Overdue", "Collect"}
        };

        JTable billsTable = new JTable(data, columns);
        billsTable.setFont(UITheme.FONT_REGULAR);
        billsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(billsTable);

        // Action buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton addBillBtn = UITheme.createModernButton("➕ Add Bill", UITheme.PRIMARY_BLUE);
        JButton processBtn = UITheme.createModernButton("💳 Process Payment", UITheme.SUCCESS_GREEN);

        buttonPanel.add(addBillBtn);
        buttonPanel.add(processBtn);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create attendance panel
     */
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Date selector
        JPanel datePanel = new JPanel();
        datePanel.setOpaque(false);
        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = UITheme.createModernTextField(15);
        dateField.setText("2024-05-01");
        datePanel.add(dateLabel);
        datePanel.add(dateField);

        // Attendance table
        String[] columns = {"Employee ID", "Name", "Status", "Notes", "Action"};
        Object[][] data = {
            {"EMP-001", "John Doe", "✓ Present", "On time", "Edit"},
            {"EMP-002", "Jane Smith", "✗ Absent", "No leave", "Mark Leave"},
            {"EMP-003", "Maria Santos", "⏳ Half Day", "Doctor appointment", "Edit"}
        };

        JTable attendanceTable = new JTable(data, columns);
        attendanceTable.setFont(UITheme.FONT_REGULAR);
        attendanceTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(attendanceTable);

        // Update button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton updateBtn = UITheme.createModernButton("💾 Save Attendance", UITheme.SUCCESS_GREEN);
        buttonPanel.add(updateBtn);

        panel.add(datePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Helper method to load employee data
     */
    private Object[][] loadEmployeeData() {
        return new Object[][] {
            {"EMP-001", "John Doe", "Sales Associate", "Sales", "✓ Active", "View"},
            {"EMP-002", "Jane Smith", "Software Developer", "IT", "✓ Active", "View"},
            {"EMP-003", "Maria Santos", "HR Manager", "HR", "✓ Active", "View"},
            {"EMP-004", "Pedro Garcia", "Accountant", "Finance", "✓ Active", "View"},
            {"EMP-005", "Rosa Reyes", "Operations Coordinator", "Operations", "✓ Active", "View"}
        };
    }

    /**
     * Get employee names for combo box
     */
    private String[] getEmployeeNames() {
        return new String[]{"John Doe", "Jane Smith", "Maria Santos", "Pedro Garcia", "Rosa Reyes"};
    }

    /**
     * Show add employee dialog with comprehensive form validation
     */
    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add New Employee", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UITheme.BG_WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("👤 Add New Employee");
        titleLabel.setFont(UITheme.FONT_HEADING);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("First Name: *"), gbc);
        JTextField firstNameField = UITheme.createModernTextField(15);
        gbc.gridx = 1;
        mainPanel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Last Name: *"), gbc);
        JTextField lastNameField = UITheme.createModernTextField(15);
        gbc.gridx = 1;
        mainPanel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Email: *"), gbc);
        JTextField emailField = UITheme.createModernTextField(15);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);

        // Position
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Position: *"), gbc);
        JTextField positionField = UITheme.createModernTextField(15);
        gbc.gridx = 1;
        mainPanel.add(positionField, gbc);

        // Department
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Department: *"), gbc);
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{"Sales", "IT", "HR", "Finance", "Operations", "Other"});
        gbc.gridx = 1;
        mainPanel.add(deptCombo, gbc);

        // Hourly Rate
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Hourly Rate (PHP): *"), gbc);
        JTextField rateField = UITheme.createModernTextField(15);
        rateField.setText("250.00");
        gbc.gridx = 1;
        mainPanel.add(rateField, gbc);

        // Error label
        JLabel errorLabel = new JLabel();
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER_RED);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        mainPanel.add(errorLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton addBtn = UITheme.createSuccessButton("✓ Add Employee");
        addBtn.addActionListener(e -> {
            // Validate all fields
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String position = positionField.getText().trim();
            String department = (String) deptCombo.getSelectedItem();
            String rateText = rateField.getText().trim();

            if (firstName.isEmpty()) {
                errorLabel.setText("❌ First name is required");
                return;
            }
            if (lastName.isEmpty()) {
                errorLabel.setText("❌ Last name is required");
                return;
            }
            if (email.isEmpty() || !email.contains("@")) {
                errorLabel.setText("❌ Valid email is required");
                return;
            }
            if (position.isEmpty()) {
                errorLabel.setText("❌ Position is required");
                return;
            }

            try {
                double hourlyRate = Double.parseDouble(rateText);
                if (hourlyRate <= 0) {
                    errorLabel.setText("❌ Hourly rate must be greater than 0");
                    return;
                }

                // Here you would add database integration
                JOptionPane.showMessageDialog(dialog, 
                    "✓ Employee added successfully!\n\n" +
                    "Name: " + firstName + " " + lastName + "\n" +
                    "Position: " + position + "\n" +
                    "Department: " + department,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                errorLabel.setText("❌ Invalid hourly rate format");
            }
        });

        buttonPanel.add(cancelBtn);
        buttonPanel.add(addBtn);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 10, 0, 10);
        mainPanel.add(buttonPanel, gbc);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * Show compose message dialog
     */
    private void showComposeMessageDialog() {
        JOptionPane.showMessageDialog(this, "Compose Message\n\nSelect recipient and write your message.");
    }

    /**
     * Show notification dialog
     */
    private void showNotificationDialog() {
        JOptionPane.showMessageDialog(this, "Send Notification\n\nAll workers will be notified to submit their attendance slips.");
    }

    /**
     * Release salary to employee with proper error handling
     */
    private void releaseSalary(String employee, double salary, int absences) {
        if (salary <= 0) {
            JOptionPane.showMessageDialog(this, "❌ Please enter a valid salary amount greater than 0", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double deduction = absences * 500.0;
        double netSalary = salary - deduction;
        
        if (netSalary < 0) {
            JOptionPane.showMessageDialog(this, "❌ Net salary cannot be negative. Check deductions.", "Invalid Calculation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Connection con = DBConnection.getConnection();
            if (con == null) {
                JOptionPane.showMessageDialog(this, "⚠️ Database connection failed. Please try again.", "Connection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "UPDATE worker_wallet SET balance = balance + ?, last_salary_date = NOW(), last_salary_amount = ? WHERE employee_id IN (SELECT employee_id FROM employee WHERE user_id IN (SELECT id FROM users WHERE CONCAT(first_name, ' ', last_name) LIKE ?))";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setDouble(1, netSalary);
            pst.setDouble(2, netSalary);
            pst.setString(3, "%" + employee + "%");
            
            int updated = pst.executeUpdate();
            pst.close();
            con.close();
            
            if (updated > 0) {
                String message = String.format("✓ Salary released successfully!\n\nEmployee: %s\nBase Salary: PHP %.2f\nAbsences: %d days\nDeduction: PHP %.2f\nNet Salary: PHP %.2f", 
                    employee, salary, absences, deduction, netSalary);
                JOptionPane.showMessageDialog(this, message, "Salary Released", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Employee not found or could not update salary", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error releasing salary: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "❌ Database error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "❌ Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Logout user with confirmation
     */
    private void logout() {
        int response = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?\n\nYou will need to login again.",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            this.dispose();
            try {
                new LoginFrame();
            } catch (Exception e) {
                System.err.println("Error launching login frame: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
