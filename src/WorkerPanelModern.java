package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

/**
 * WorkerPanelModern.java
 * Modern worker dashboard with balance display (Konek2Card style), messaging, and utility services
 */
public class WorkerPanelModern extends JFrame {
    private DBConnection.UserInfo workerUser;
    private DBConnection.EmployeeInfo employeeInfo;
    private JTabbedPane tabbedPane;
    private JLabel balanceLabel;
    private JLabel lastSalaryLabel;
    private JLabel notificationBadgeLabel;
    private int notificationCount = 0;
    private int messageCount = 0;

    public WorkerPanelModern(DBConnection.UserInfo workerUser) {
        this.workerUser = workerUser;

        // Get employee info
        String employeeId = getEmployeeIdFromUserId(workerUser.id);
        if (employeeId != null) {
            this.employeeInfo = DBConnection.getEmployeeInfo(employeeId);
        }

        // Frame setup
        setTitle("Payroll System - Worker Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
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

        tabbedPane.addTab("🏠 Home", createHomePanel());
        tabbedPane.addTab("💰 Wallet & Balance", createWalletPanel());
        tabbedPane.addTab("📬 Messages (" + messageCount + ")", createMessagesPanel());
        tabbedPane.addTab("💳 Pay Bills", createBillPaymentPanel());
        tabbedPane.addTab("📱 E-Wallet", createEWalletPanel());
        tabbedPane.addTab("👤 Profile", createProfilePanel());
        
        // Update notification counts from database
        updateNotificationCounts();

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
    
    /**
     * Update notification and message counts from database
     */
    private void updateNotificationCounts() {
        try {
            notificationCount = NotificationManager.getUnreadNotificationCount(workerUser.id);
            messageCount = MessageManager.getUnreadCount(workerUser.id);
            
            // Update tab title with message count
            if (tabbedPane.getTabCount() > 2) {
                tabbedPane.setTitleAt(2, "📬 Messages (" + messageCount + ")");
            }
            
            // Update notification badge
            if (notificationBadgeLabel != null) {
                int total = notificationCount + messageCount;
                if (total > 0) {
                    notificationBadgeLabel.setText("🔔 (" + total + ")");
                    notificationBadgeLabel.setForeground(UITheme.DANGER_RED);
                } else {
                    notificationBadgeLabel.setText("🔔");
                    notificationBadgeLabel.setForeground(Color.WHITE);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update notification counts: " + e.getMessage());
        }
    }

    /**
     * Create header panel
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("👋 Welcome, " + workerUser.getFullName() + "!");
        titleLabel.setFont(UITheme.FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);

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
        
        JButton notificationBtn = new JButton(notificationBadgeLabel.getText());
        notificationBtn.setOpaque(false);
        notificationBtn.setContentAreaFilled(false);
        notificationBtn.setBorderPainted(false);
        notificationBtn.setForeground(Color.WHITE);
        notificationBtn.setFont(UITheme.FONT_HEADING);
        notificationBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationBtn.addActionListener(e -> showNotificationPanel());
        
        JButton logoutBtn = UITheme.createModernButton("Logout", UITheme.DANGER_RED);
        logoutBtn.addActionListener(e -> logout());
        
        rightPanel.add(notificationBadgeLabel);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(logoutBtn);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    
    /**
     * Show notification panel with all notifications and messages
     */
    private void showNotificationPanel() {
        JFrame notifFrame = new JFrame("Notifications & Messages");
        notifFrame.setSize(500, 600);
        notifFrame.setLocationRelativeTo(this);
        notifFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Notifications
        JLabel notifTitle = new JLabel("🔔 Notifications (" + notificationCount + ")");
        notifTitle.setFont(UITheme.FONT_SUBHEADING);
        notifTitle.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(notifTitle);
        panel.add(Box.createVerticalStrut(10));
        
        List<NotificationManager.NotificationInfo> notifications = 
            NotificationManager.getUnreadNotifications(workerUser.id, 10);
        
        if (notifications.isEmpty()) {
            JLabel noNotifLabel = new JLabel("No new notifications");
            noNotifLabel.setFont(UITheme.FONT_REGULAR);
            noNotifLabel.setForeground(UITheme.TEXT_SECONDARY);
            panel.add(noNotifLabel);
        } else {
            for (NotificationManager.NotificationInfo notif : notifications) {
                JPanel notifCard = UITheme.createCardPanel();
                notifCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                notifCard.setLayout(new BoxLayout(notifCard, BoxLayout.Y_AXIS));
                
                JLabel titleLabel = new JLabel(notif.title);
                titleLabel.setFont(UITheme.FONT_BOLD);
                titleLabel.setForeground(UITheme.TEXT_PRIMARY);
                
                JLabel msgLabel = new JLabel(notif.message);
                msgLabel.setFont(UITheme.FONT_SMALL);
                msgLabel.setForeground(UITheme.TEXT_SECONDARY);
                
                notifCard.add(titleLabel);
                notifCard.add(msgLabel);
                panel.add(notifCard);
                panel.add(Box.createVerticalStrut(5));
            }
        }
        
        panel.add(Box.createVerticalStrut(15));
        
        // Messages
        JLabel msgTitle = new JLabel("💬 Messages (" + messageCount + ")");
        msgTitle.setFont(UITheme.FONT_SUBHEADING);
        msgTitle.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(msgTitle);
        panel.add(Box.createVerticalStrut(10));
        
        List<MessageManager.MessageInfo> messages = 
            MessageManager.getUnreadMessages(workerUser.id, 10);
        
        if (messages.isEmpty()) {
            JLabel noMsgLabel = new JLabel("No new messages");
            noMsgLabel.setFont(UITheme.FONT_REGULAR);
            noMsgLabel.setForeground(UITheme.TEXT_SECONDARY);
            panel.add(noMsgLabel);
        } else {
            for (MessageManager.MessageInfo msg : messages) {
                JPanel msgCard = UITheme.createCardPanel();
                msgCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                msgCard.setLayout(new BoxLayout(msgCard, BoxLayout.Y_AXIS));
                
                JLabel fromLabel = new JLabel("From: " + msg.senderName);
                fromLabel.setFont(UITheme.FONT_BOLD);
                fromLabel.setForeground(UITheme.PRIMARY_BLUE);
                
                JLabel subjLabel = new JLabel(msg.subject);
                subjLabel.setFont(UITheme.FONT_SMALL);
                subjLabel.setForeground(UITheme.TEXT_SECONDARY);
                
                msgCard.add(fromLabel);
                msgCard.add(subjLabel);
                panel.add(msgCard);
                panel.add(Box.createVerticalStrut(5));
            }
        }
        
        panel.add(Box.createVerticalGlue());
        
        JButton refreshBtn = UITheme.createModernButton("Refresh", UITheme.SUCCESS_GREEN);
        refreshBtn.addActionListener(e -> {
            updateNotificationCounts();
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
     * Create home/dashboard panel
     */
    private JScrollPane createHomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Balance card (Konek2Card style)
        double balance = employeeInfo != null ? employeeInfo.balance : 0.0;
        JPanel balanceCard = createBalanceDisplayCard(balance);
        balanceCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        panel.add(balanceCard);
        panel.add(Box.createVerticalStrut(20));

        // Quick actions
        JLabel quickLabel = new JLabel("Quick Actions");
        quickLabel.setFont(UITheme.FONT_SUBHEADING);
        quickLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(quickLabel);
        panel.add(Box.createVerticalStrut(10));

        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        actionsPanel.setOpaque(false);
        actionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JButton payBillBtn = createActionButton("💧 Pay Water", UITheme.PRIMARY_BLUE);
        JButton payElectricBtn = createActionButton("⚡ Pay Electric", UITheme.SECONDARY_TEAL);
        JButton checkSalaryBtn = createActionButton("💰 Check Salary", UITheme.SUCCESS_GREEN);
        JButton connectWalletBtn = createActionButton("📱 Connect E-Wallet", UITheme.ACCENT_ORANGE);
        JButton viewSlipsBtn = createActionButton("📄 View Slips", UITheme.WARNING_YELLOW);
        JButton supportBtn = createActionButton("💬 Support", UITheme.DANGER_RED);

        payBillBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        payElectricBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        checkSalaryBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        connectWalletBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        viewSlipsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Your salary slips will appear here"));
        supportBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));

        actionsPanel.add(payBillBtn);
        actionsPanel.add(payElectricBtn);
        actionsPanel.add(checkSalaryBtn);
        actionsPanel.add(connectWalletBtn);
        actionsPanel.add(viewSlipsBtn);
        actionsPanel.add(supportBtn);

        panel.add(actionsPanel);
        panel.add(Box.createVerticalStrut(20));

        // Notifications
        JLabel notifLabel = new JLabel("📢 Notifications");
        notifLabel.setFont(UITheme.FONT_SUBHEADING);
        notifLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(notifLabel);
        panel.add(Box.createVerticalStrut(10));

        String[] notifications = {
            "✓ Salary released on May 1, 2024 - PHP 40,000.00",
            "📌 Please submit your attendance slip by May 5",
            "💳 Your GCash e-wallet has been verified",
            "⚠️ Water bill due on May 15 - PHP 850.00",
            "💬 HR message: Check your salary computation"
        };

        for (String notif : notifications) {
            JPanel notifItem = UITheme.createCardPanel();
            notifItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            JLabel notifLabel2 = new JLabel(notif);
            notifLabel2.setFont(UITheme.FONT_REGULAR);
            notifLabel2.setForeground(UITheme.TEXT_SECONDARY);
            notifItem.add(notifLabel2);
            panel.add(notifItem);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create balance display card (Konek2Card style)
     */
    private JPanel createBalanceDisplayCard(double balance) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, UITheme.PRIMARY_BLUE,
                    getWidth(), getHeight(), UITheme.SECONDARY_TEAL
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Decorative circles
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(getWidth() - 100, -50, 150, 150);
                g2.fillOval(-50, getHeight() - 80, 120, 120);
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setOpaque(false);

        // Card company name
        JLabel bankLabel = new JLabel("PAYROLL WALLET");
        bankLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        bankLabel.setForeground(new Color(200, 220, 240));
        bankLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // "Available Balance" label
        JLabel balanceLabelText = new JLabel("Available Balance");
        balanceLabelText.setFont(UITheme.FONT_REGULAR);
        balanceLabelText.setForeground(new Color(200, 220, 240));
        balanceLabelText.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Balance amount
        balanceLabel = new JLabel("PHP " + String.format("%,.2f", balance));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Last salary info
        lastSalaryLabel = new JLabel("Last Salary: PHP " + (employeeInfo != null ? String.format("%,.2f", employeeInfo.lastSalaryAmount) : "0.00"));
        lastSalaryLabel.setFont(UITheme.FONT_SMALL);
        lastSalaryLabel.setForeground(new Color(220, 240, 255));
        lastSalaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(bankLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(balanceLabelText);
        card.add(Box.createVerticalStrut(8));
        card.add(balanceLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(lastSalaryLabel);

        return card;
    }

    /**
     * Create action button for quick access
     */
    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                if (getModel().isArmed()) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                
                super.paintComponent(g);
            }
        };

        btn.setForeground(Color.WHITE);
        btn.setFont(UITheme.FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 60));

        return btn;
    }

    /**
     * Create wallet/balance panel
     */
    private JScrollPane createWalletPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("💰 Wallet & Balance");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        // Current balance
        JPanel balanceCard = UITheme.createCardPanel();
        balanceCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JLabel balanceText = new JLabel("Current Balance: PHP " + (employeeInfo != null ? String.format("%,.2f", employeeInfo.balance) : "0.00"));
        balanceText.setFont(UITheme.FONT_TITLE);
        balanceText.setForeground(UITheme.SUCCESS_GREEN);
        balanceCard.add(balanceText);
        panel.add(balanceCard);
        panel.add(Box.createVerticalStrut(15));

        // Transaction history
        JLabel historyLabel = new JLabel("Transaction History");
        historyLabel.setFont(UITheme.FONT_SUBHEADING);
        historyLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(historyLabel);
        panel.add(Box.createVerticalStrut(10));

        String[] columns = {"Date", "Description", "Amount", "Type", "Balance"};
        Object[][] data = {
            {"2024-05-01", "Salary Credit", "+40,000.00", "Credit", "40,000.00"},
            {"2024-04-30", "Water Bill Payment", "-850.00", "Debit", "0.00"},
            {"2024-04-28", "Electric Bill Payment", "-1,200.00", "Debit", "850.00"},
            {"2024-04-20", "Salary Credit", "+40,000.00", "Credit", "2,050.00"}
        };

        JTable historyTable = new JTable(data, columns);
        historyTable.setFont(UITheme.FONT_REGULAR);
        historyTable.setRowHeight(25);
        historyTable.setBackground(UITheme.BG_WHITE);
        historyTable.setGridColor(UITheme.BORDER_GRAY);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 250));
        panel.add(scrollPane);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create messages panel
     */
    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Messages list
        String[] columns = {"From", "Subject", "Date", "Status"};
        Object[][] data = {
            {"Admin", "✓ Salary Released", "2024-05-01", "Read"},
            {"HR", "📌 Submit Attendance Slip", "2024-04-30", "Unread"},
            {"Admin", "E-Wallet Verification Approved", "2024-04-28", "Read"},
            {"Support", "Payment Confirmation", "2024-04-25", "Read"}
        };

        JTable messagesTable = new JTable(data, columns);
        messagesTable.setFont(UITheme.FONT_REGULAR);
        messagesTable.setRowHeight(25);
        messagesTable.setBackground(UITheme.BG_WHITE);
        JScrollPane scrollPane = new JScrollPane(messagesTable);

        // Compose button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton replyBtn = UITheme.createModernButton("💬 Reply", UITheme.PRIMARY_BLUE);
        JButton reportBtn = UITheme.createModernButton("📢 Report Issue", UITheme.DANGER_RED);

        buttonPanel.add(replyBtn);
        buttonPanel.add(reportBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create bill payment panel
     */
    private JScrollPane createBillPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("💳 Pay Bills");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Select bill
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Select Bill:"), gbc);
        String[] bills = {"Water Bill - PHP 850.00 (Due: May 10)", "Electric Bill - PHP 1,500.00 (Due: May 15)"};
        JComboBox<String> billCombo = new JComboBox<>(bills);
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        panel.add(billCombo, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Amount to Pay (PHP):"), gbc);
        JTextField amountField = UITheme.createModernTextField(15);
        amountField.setText("850.00");
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // Payment method
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Payment Method:"), gbc);
        String[] methods = {"Wallet Balance", "PayMaya", "GCash", "PayPal"};
        JComboBox<String> methodCombo = new JComboBox<>(methods);
        gbc.gridx = 1;
        panel.add(methodCombo, gbc);

        // Confirm info
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel infoPanel = UITheme.createCardPanel();
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel infoLabel = new JLabel("<html>Balance After Payment: PHP 39,150.00<br/>Transaction will be processed immediately</html>");
        infoLabel.setFont(UITheme.FONT_REGULAR);
        infoLabel.setForeground(UITheme.TEXT_SECONDARY);
        infoPanel.add(infoLabel);
        panel.add(infoPanel, gbc);

        // Pay button
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JButton payBtn = UITheme.createModernButton("💳 Pay Now", UITheme.SUCCESS_GREEN);
        payBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        payBtn.addActionListener(e -> {
            String bill = (String) billCombo.getSelectedItem();
            JOptionPane.showMessageDialog(panel, "✓ Bill payment successful!\n\nBill: " + bill + "\nTransaction ID: TXN" + System.currentTimeMillis());
        });
        panel.add(payBtn, gbc);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create E-wallet panel
     */
    private JScrollPane createEWalletPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("📱 E-Wallet Connection");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Available wallets
        JLabel walletsLabel = new JLabel("Available Payment Gateways");
        walletsLabel.setFont(UITheme.FONT_SUBHEADING);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(walletsLabel, gbc);

        // PayMaya
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JPanel payMayaCard = createWalletCard("PayMaya", "09123456789", "✓ Verified", UITheme.PRIMARY_BLUE);
        panel.add(payMayaCard, gbc);

        // GCash
        gbc.gridx = 1;
        JPanel gcashCard = createWalletCard("GCash", "09987654321", "✓ Verified", UITheme.SECONDARY_TEAL);
        panel.add(gcashCard, gbc);

        // PayPal
        gbc.gridx = 0;
        gbc.gridy = 3;
        JPanel paypalCard = createWalletCard("PayPal", "john.doe@email.com", "⏳ Pending", UITheme.WARNING_YELLOW);
        panel.add(paypalCard, gbc);

        // Add new wallet
        gbc.gridx = 1;
        gbc.gridy = 3;
        JPanel addWalletCard = UITheme.createCardPanel();
        addWalletCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JButton addWalletBtn = new JButton("➕ Add E-Wallet");
        addWalletBtn.setFont(UITheme.FONT_BOLD);
        addWalletBtn.setForeground(UITheme.PRIMARY_BLUE);
        addWalletBtn.setOpaque(false);
        addWalletBtn.setBorderPainted(false);
        addWalletBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addWalletCard.add(addWalletBtn);
        panel.add(addWalletCard, gbc);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create wallet card
     */
    private JPanel createWalletCard(String provider, String account, String status, Color color) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridBagLayout());
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel providerLabel = new JLabel(provider);
        providerLabel.setFont(UITheme.FONT_BOLD);
        providerLabel.setForeground(color);
        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(providerLabel, gbc);

        JLabel accountLabel = new JLabel(account);
        accountLabel.setFont(UITheme.FONT_SMALL);
        accountLabel.setForeground(UITheme.TEXT_SECONDARY);
        gbc.gridy = 1;
        card.add(accountLabel, gbc);

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(status.contains("Verified") ? UITheme.SUCCESS_GREEN : UITheme.WARNING_YELLOW);
        gbc.gridx = 1;
        gbc.gridy = 0;
        card.add(statusLabel, gbc);

        return card;
    }

    /**
     * Create profile panel
     */
    private JScrollPane createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("👤 Profile Settings");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Name fields
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("First Name:"), gbc);
        JTextField firstNameField = UITheme.createModernTextField(20);
        firstNameField.setText(workerUser.firstName);
        firstNameField.setEditable(false);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        JTextField lastNameField = UITheme.createModernTextField(20);
        lastNameField.setText(workerUser.lastName);
        lastNameField.setEditable(false);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = UITheme.createModernTextField(20);
        emailField.setText(workerUser.email);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Phone number for notifications
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("📱 Phone Number (for notifications):"), gbc);
        JTextField phoneField = UITheme.createModernTextField(20);
        phoneField.setText(workerUser.phoneNumber != null ? workerUser.phoneNumber : "");
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        // Employee info
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Position:"), gbc);
        JTextField positionField = UITheme.createModernTextField(20);
        positionField.setText(employeeInfo != null ? employeeInfo.position : "");
        positionField.setEditable(false);
        gbc.gridx = 1;
        panel.add(positionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Department:"), gbc);
        JTextField deptField = UITheme.createModernTextField(20);
        deptField.setText(employeeInfo != null ? employeeInfo.department : "");
        deptField.setEditable(false);
        gbc.gridx = 1;
        panel.add(deptField, gbc);

        // Notification preferences
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        JLabel notifLabel = new JLabel("🔔 Notification Preferences");
        notifLabel.setFont(UITheme.FONT_SUBHEADING);
        gbc.gridy = 8;
        panel.add(notifLabel, gbc);

        gbc.gridy = 9;
        JCheckBox salaryNotif = new JCheckBox("Notify when salary is released", true);
        panel.add(salaryNotif, gbc);

        gbc.gridy = 10;
        JCheckBox billNotif = new JCheckBox("Notify about due bills", true);
        panel.add(billNotif, gbc);

        gbc.gridy = 11;
        JCheckBox msgNotif = new JCheckBox("Notify when I receive messages", true);
        panel.add(msgNotif, gbc);

        // Save button
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        JButton saveBtn = UITheme.createModernButton("💾 Save Changes", UITheme.SUCCESS_GREEN);
        saveBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> {
            updateUserPhone(phoneField.getText());
            JOptionPane.showMessageDialog(panel, "✓ Profile updated successfully!");
        });
        panel.add(saveBtn, gbc);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Update user phone number
     */
    private void updateUserPhone(String phone) {
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                String query = "UPDATE users SET phone_number = ? WHERE id = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, phone);
                pst.setInt(2, workerUser.id);
                pst.executeUpdate();
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get employee ID from user ID
     */
    private String getEmployeeIdFromUserId(int userId) {
        try {
            Connection con = DBConnection.getConnection();
            if (con != null) {
                String query = "SELECT employee_id FROM employee WHERE user_id = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setInt(1, userId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    return rs.getString("employee_id");
                }
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Logout user with confirmation and better error handling
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
