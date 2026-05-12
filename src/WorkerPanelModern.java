package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

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
        setTitle("Mihoyo - Worker Dashboard");
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
        tabbedPane.addTab("💸 Withdraw Money", createWithdrawPanel());
        tabbedPane.addTab("📬 Messages (" + messageCount + ")", createMessagesPanel());
        tabbedPane.addTab("💳 Bills", createBillPaymentPanel());
        tabbedPane.addTab("📱 E-Wallet", createEWalletPanel());
        tabbedPane.addTab("👤 Profile", createProfilePanel());
        tabbedPane.addTab("📢 Announcements 🔴", createAnnouncementsPanel());
        
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
     * Create home/dashboard panel with BLUE background
     */
    private JScrollPane createHomePanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Blue gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, UITheme.PRIMARY_BLUE,
                    getWidth(), getHeight(), new Color(0, 150, 200)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
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
        quickLabel.setForeground(Color.WHITE);
        panel.add(quickLabel);
        panel.add(Box.createVerticalStrut(10));

        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        actionsPanel.setOpaque(false);
        actionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JButton payBillBtn = createActionButton("💧⚡ Pay Bills", UITheme.PRIMARY_BLUE);
        JButton checkSalaryBtn = createActionButton("💰 Check Salary", UITheme.SUCCESS_GREEN);
        JButton connectWalletBtn = createActionButton("📱 E-Wallet", UITheme.ACCENT_ORANGE);
        JButton viewSlipsBtn = createActionButton("📄 View Slips", UITheme.WARNING_YELLOW);
        JButton supportBtn = createActionButton("💬 Support", UITheme.DANGER_RED);
        JButton profileBtn = createActionButton("👤 Profile", UITheme.SECONDARY_TEAL);

        payBillBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        checkSalaryBtn.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        connectWalletBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        viewSlipsBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Your salary slips will appear here"));
        supportBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        profileBtn.addActionListener(e -> tabbedPane.setSelectedIndex(5));

        actionsPanel.add(payBillBtn);
        actionsPanel.add(checkSalaryBtn);
        actionsPanel.add(connectWalletBtn);
        actionsPanel.add(viewSlipsBtn);
        actionsPanel.add(supportBtn);
        actionsPanel.add(profileBtn);

        panel.add(actionsPanel);
        panel.add(Box.createVerticalStrut(20));

        // Announcements section
        JLabel announcementLabel = new JLabel("📢 Announcements");
        announcementLabel.setFont(UITheme.FONT_SUBHEADING);
        announcementLabel.setForeground(Color.WHITE);
        panel.add(announcementLabel);
        panel.add(Box.createVerticalStrut(10));
        
        List<AnnouncementManager.AnnouncementInfo> announcements = AnnouncementManager.getAllAnnouncements();
        
        if (announcements.isEmpty()) {
            JLabel noAnnouncementLabel = new JLabel("No announcements available");
            noAnnouncementLabel.setFont(UITheme.FONT_REGULAR);
            noAnnouncementLabel.setForeground(new Color(200, 220, 240));
            JPanel announcementCard = UITheme.createCardPanel();
            announcementCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            announcementCard.add(noAnnouncementLabel);
            panel.add(announcementCard);
        } else {
            for (int i = 0; i < Math.min(2, announcements.size()); i++) {
                AnnouncementManager.AnnouncementInfo announcement = announcements.get(i);
                JPanel announcementCard = UITheme.createCardPanel();
                announcementCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                announcementCard.setLayout(new BoxLayout(announcementCard, BoxLayout.Y_AXIS));
                
                JLabel announcementTitleLabel = new JLabel(announcement.title);
                announcementTitleLabel.setFont(UITheme.FONT_BOLD);
                announcementTitleLabel.setForeground(UITheme.PRIMARY_BLUE);
                
                JLabel announcementMsgLabel = new JLabel(announcement.message.length() > 80 ? 
                    announcement.message.substring(0, 80) + "..." : announcement.message);
                announcementMsgLabel.setFont(UITheme.FONT_REGULAR);
                announcementMsgLabel.setForeground(UITheme.TEXT_PRIMARY);
                
                announcementCard.add(announcementTitleLabel);
                announcementCard.add(Box.createVerticalStrut(5));
                announcementCard.add(announcementMsgLabel);
                panel.add(announcementCard);
                panel.add(Box.createVerticalStrut(10));
            }
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
     * Create withdraw panel
     */
    private JScrollPane createWithdrawPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("💸 Withdraw Money");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Current Balance Display
        JPanel balanceCard = UITheme.createCardPanel();
        balanceCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        double currentBalance = employeeInfo != null ? employeeInfo.balance : 0.0;
        JLabel balanceLabel = new JLabel("Available Balance: PHP " + String.format("%,.2f", currentBalance));
        balanceLabel.setFont(UITheme.FONT_BOLD);
        balanceLabel.setForeground(UITheme.SUCCESS_GREEN);
        balanceCard.add(balanceLabel);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(balanceCard, gbc);

        // Withdraw amount
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel amountLabel = new JLabel("Withdrawal Amount (PHP):");
        panel.add(amountLabel, gbc);
        
        JTextField amountField = UITheme.createModernTextField(20);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // Amount presets
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel presetsLabel = new JLabel("Quick Select:");
        presetsLabel.setFont(UITheme.FONT_SMALL);
        presetsLabel.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(presetsLabel, gbc);

        gbc.gridy = 4;
        JPanel presetsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        presetsPanel.setOpaque(false);
        
        for (String preset : new String[]{"500", "1000", "2000", "5000", "10000"}) {
            JButton presetBtn = new JButton("PHP " + preset);
            presetBtn.setFont(UITheme.FONT_SMALL);
            presetBtn.setBackground(UITheme.PRIMARY_BLUE);
            presetBtn.setForeground(Color.WHITE);
            presetBtn.setFocusPainted(false);
            presetBtn.setBorder(BorderFactory.createRaisedBevelBorder());
            String finalAmount = preset;
            presetBtn.addActionListener(e -> amountField.setText(finalAmount));
            presetsPanel.add(presetBtn);
        }
        panel.add(presetsPanel, gbc);

        // Information card
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel infoCard = UITheme.createCardPanel();
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        
        JLabel infoTitle = new JLabel("ℹ️ Withdrawal Information");
        infoTitle.setFont(UITheme.FONT_BOLD);
        infoTitle.setForeground(UITheme.TEXT_PRIMARY);
        
        JLabel infoText1 = new JLabel("• Withdrawal is processed immediately");
        infoText1.setFont(UITheme.FONT_SMALL);
        infoText1.setForeground(UITheme.TEXT_SECONDARY);
        
        JLabel infoText2 = new JLabel("• You will receive a receipt with transaction details");
        infoText2.setFont(UITheme.FONT_SMALL);
        infoText2.setForeground(UITheme.TEXT_SECONDARY);
        
        JLabel infoText3 = new JLabel("• Receipt contains: Name, Amount, Balance, and Date");
        infoText3.setFont(UITheme.FONT_SMALL);
        infoText3.setForeground(UITheme.TEXT_SECONDARY);
        
        infoCard.add(infoTitle);
        infoCard.add(Box.createVerticalStrut(5));
        infoCard.add(infoText1);
        infoCard.add(infoText2);
        infoCard.add(infoText3);
        
        panel.add(infoCard, gbc);

        // Withdraw button
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton withdrawBtn = UITheme.createModernButton("✓ Process Withdrawal", UITheme.SUCCESS_GREEN);
        withdrawBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        withdrawBtn.addActionListener(e -> processWithdrawal(amountField));
        panel.add(withdrawBtn, gbc);

        // Withdrawal history
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JLabel historyLabel = new JLabel("Recent Withdrawals");
        historyLabel.setFont(UITheme.FONT_SUBHEADING);
        panel.add(historyLabel, gbc);

        gbc.gridy = 8;
        String[] columns = {"Date", "Amount", "Balance After", "Receipt #", "Status"};
        java.util.List<WithdrawManager.WithdrawalInfo> withdrawals = WithdrawManager.getWithdrawalHistory(workerUser.id, 10);
        
        Object[][] data = new Object[withdrawals.size()][5];
        for (int i = 0; i < withdrawals.size(); i++) {
            WithdrawManager.WithdrawalInfo w = withdrawals.get(i);
            data[i][0] = w.withdrawalDate.toLocalDate();
            data[i][1] = "PHP " + String.format("%.2f", w.withdrawAmount);
            data[i][2] = "PHP " + String.format("%.2f", w.balanceAfter);
            data[i][3] = w.receiptNumber;
            data[i][4] = w.status;
        }

        JTable historyTable = new JTable(data, columns);
        historyTable.setFont(UITheme.FONT_SMALL);
        historyTable.setRowHeight(25);
        historyTable.setBackground(UITheme.BG_WHITE);
        historyTable.setGridColor(UITheme.BORDER_GRAY);

        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.add(historyScroll, gbc);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Process withdrawal request
     */
    private void processWithdrawal(JTextField amountField) {
        String amountText = amountField.getText().trim();
        
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a withdrawal amount",
                "Missing Amount",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double withdrawAmount = Double.parseDouble(amountText);
            
            if (withdrawAmount <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid amount greater than 0",
                    "Invalid Amount",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            double currentBalance = employeeInfo != null ? employeeInfo.balance : 0.0;
            if (withdrawAmount > currentBalance) {
                JOptionPane.showMessageDialog(this,
                    "Insufficient balance!\n\nAvailable: PHP " + String.format("%.2f", currentBalance) + "\nRequested: PHP " + String.format("%.2f", withdrawAmount),
                    "Insufficient Balance",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirm withdrawal
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to withdraw PHP " + String.format("%.2f", withdrawAmount) + "?\n\n" +
                "Current Balance: PHP " + String.format("%.2f", currentBalance) + "\n" +
                "Balance After: PHP " + String.format("%.2f", currentBalance - withdrawAmount),
                "Confirm Withdrawal",
                JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Process withdrawal
            WithdrawManager.WithdrawalInfo withdrawal = WithdrawManager.processWithdrawal(workerUser.id, withdrawAmount);
            
            if (withdrawal != null) {
                // Generate and show receipt
                String receipt = ReceiptGenerator.generateReceipt(
                    workerUser.getFullName(),
                    getEmployeeIdFromUserId(workerUser.id),
                    withdrawal.withdrawAmount,
                    withdrawal.balanceAfter,
                    withdrawal.receiptNumber,
                    withdrawal.withdrawalDate
                );

                // Save receipt to file
                ReceiptGenerator.saveReceiptToFile(
                    workerUser.getFullName(),
                    getEmployeeIdFromUserId(workerUser.id),
                    withdrawal.withdrawAmount,
                    withdrawal.balanceAfter,
                    withdrawal.receiptNumber,
                    withdrawal.withdrawalDate
                );

                // Show receipt dialog
                JFrame receiptFrame = new JFrame("Withdrawal Receipt");
                receiptFrame.setSize(600, 700);
                receiptFrame.setLocationRelativeTo(this);
                receiptFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                JTextArea receiptArea = new JTextArea(receipt);
                receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
                receiptArea.setEditable(false);
                receiptArea.setBackground(new Color(245, 245, 245));
                receiptArea.setForeground(Color.BLACK);
                receiptArea.setMargin(new Insets(15, 15, 15, 15));

                JScrollPane scrollPane = new JScrollPane(receiptArea);
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                buttonPanel.setBackground(UITheme.BG_LIGHT);
                
                JButton printBtn = UITheme.createModernButton("🖨️ Print Receipt", UITheme.PRIMARY_BLUE);
                printBtn.addActionListener(e -> {
                    try {
                        receiptArea.print();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(receiptFrame, "Print error: " + ex.getMessage());
                    }
                });
                
                JButton saveBtn = UITheme.createModernButton("💾 Save as Text", UITheme.SECONDARY_TEAL);
                saveBtn.addActionListener(e -> {
                    JOptionPane.showMessageDialog(receiptFrame,
                        "Receipt has been saved as: " + withdrawal.receiptNumber + ".txt\n" +
                        "Location: receipts/ folder",
                        "Receipt Saved",
                        JOptionPane.INFORMATION_MESSAGE);
                });
                
                JButton closeBtn = UITheme.createModernButton("Close", UITheme.DANGER_RED);
                closeBtn.addActionListener(e -> receiptFrame.dispose());
                
                buttonPanel.add(printBtn);
                buttonPanel.add(saveBtn);
                buttonPanel.add(closeBtn);

                receiptFrame.add(scrollPane, BorderLayout.CENTER);
                receiptFrame.add(buttonPanel, BorderLayout.SOUTH);
                receiptFrame.setVisible(true);

                // Clear the input field and show success message
                amountField.setText("");
                JOptionPane.showMessageDialog(this,
                    "✓ Withdrawal successful!\n\n" +
                    "Amount: PHP " + String.format("%.2f", withdrawal.withdrawAmount) + "\n" +
                    "Balance Remaining: PHP " + String.format("%.2f", withdrawal.balanceAfter) + "\n" +
                    "Receipt #: " + withdrawal.receiptNumber + "\n\n" +
                    "A receipt has been generated and saved.",
                    "Withdrawal Successful",
                    JOptionPane.INFORMATION_MESSAGE);

                // Refresh the employee info to update balance
                String employeeId = getEmployeeIdFromUserId(workerUser.id);
                if (employeeId != null) {
                    employeeInfo = DBConnection.getEmployeeInfo(employeeId);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error processing withdrawal. Please try again.",
                    "Withdrawal Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number",
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE);
        }
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
     * Create bill payment panel - COMBINED Water & Electric Bills
     */
    private JScrollPane createBillPaymentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel titleLabel = new JLabel("💳 Bills Management");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Water & Electric Combined Bill
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Water & Electric Bill"), gbc);
        
        JPanel billCard = UITheme.createCardPanel();
        billCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        billCard.setLayout(new GridBagLayout());
        GridBagConstraints billGbc = new GridBagConstraints();
        billGbc.insets = new Insets(5, 5, 5, 5);
        billGbc.fill = GridBagConstraints.HORIZONTAL;
        billGbc.weightx = 1.0;
        
        JLabel waterLabel = new JLabel("💧 Water Bill: PHP 850.00 (Due: May 10)");
        waterLabel.setFont(UITheme.FONT_REGULAR);
        waterLabel.setForeground(UITheme.SUCCESS_GREEN);
        billGbc.gridx = 0;
        billGbc.gridy = 0;
        billCard.add(waterLabel, billGbc);
        
        JLabel electricLabel = new JLabel("⚡ Electric Bill: PHP 1,500.00 (Due: May 15)");
        electricLabel.setFont(UITheme.FONT_REGULAR);
        electricLabel.setForeground(UITheme.ACCENT_ORANGE);
        billGbc.gridy = 1;
        billCard.add(electricLabel, billGbc);
        
        JLabel totalLabel = new JLabel("📊 Total: PHP 2,350.00");
        totalLabel.setFont(UITheme.FONT_BOLD);
        totalLabel.setForeground(UITheme.PRIMARY_BLUE);
        billGbc.gridy = 2;
        billCard.add(totalLabel, billGbc);
        
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(billCard, gbc);

        // Amount to pay
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Amount to Pay (PHP):"), gbc);
        JTextField amountField = UITheme.createModernTextField(15);
        amountField.setText("2350.00");
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        panel.add(amountField, gbc);

        // Payment method
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Payment Method:"), gbc);
        String[] methods = {"Wallet Balance", "PayMaya", "GCash", "PayPal"};
        JComboBox<String> methodCombo = new JComboBox<>(methods);
        gbc.gridx = 1;
        panel.add(methodCombo, gbc);

        // Confirm info
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel infoPanel = UITheme.createCardPanel();
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        JLabel infoLabel = new JLabel("<html>✓ Both water and electric bills can be paid together<br/>Balance After Payment: PHP 37,650.00<br/>Transaction will be processed immediately</html>");
        infoLabel.setFont(UITheme.FONT_REGULAR);
        infoLabel.setForeground(UITheme.TEXT_SECONDARY);
        infoPanel.add(infoLabel);
        panel.add(infoPanel, gbc);

        // Pay button
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton payBtn = UITheme.createModernButton("💳 Pay Both Bills Now", UITheme.SUCCESS_GREEN);
        payBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 45));
        payBtn.addActionListener(e -> {
            String amount = amountField.getText();
            String method = (String) methodCombo.getSelectedItem();
            JOptionPane.showMessageDialog(panel, 
                "✓ Bills payment successful!\n\n" +
                "Water & Electric Bills: PHP " + amount + "\n" +
                "Payment Method: " + method + "\n" +
                "Transaction ID: TXN" + System.currentTimeMillis(),
                "Payment Confirmed",
                JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(payBtn, gbc);

        // Payment history
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JLabel historyLabel = new JLabel("Payment History");
        historyLabel.setFont(UITheme.FONT_SUBHEADING);
        panel.add(historyLabel, gbc);

        String[] columns = {"Date", "Type", "Amount", "Status"};
        Object[][] data = {
            {"2024-04-01", "Water & Electric", "-2,350.00", "Paid"},
            {"2024-03-01", "Water & Electric", "-2,300.00", "Paid"},
            {"2024-02-01", "Water & Electric", "-2,250.00", "Paid"}
        };

        JTable historyTable = new JTable(data, columns);
        historyTable.setFont(UITheme.FONT_REGULAR);
        historyTable.setRowHeight(25);
        historyTable.setBackground(UITheme.BG_WHITE);
        
        gbc.gridy = 8;
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.add(scrollPane, gbc);

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

        // Get profile info
        ProfileManager.ProfileInfo profileInfo = ProfileManager.getProfile(workerUser.id);

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

        // Nickname/Display name (customizable)
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Display Name (Nickname):"), gbc);
        JTextField nicknameField = UITheme.createModernTextField(20);
        nicknameField.setText(profileInfo != null && profileInfo.nickname != null ? profileInfo.nickname : workerUser.getFullName());
        gbc.gridx = 1;
        panel.add(nicknameField, gbc);

        // Bio
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Bio:"), gbc);
        JTextArea bioArea = new JTextArea(3, 20);
        bioArea.setFont(UITheme.FONT_REGULAR);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setText(profileInfo != null && profileInfo.bio != null ? profileInfo.bio : "");
        bioArea.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_GRAY, 1));
        JScrollPane bioScroll = new JScrollPane(bioArea);
        gbc.gridx = 1;
        panel.add(bioScroll, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        JTextField emailField = UITheme.createModernTextField(20);
        emailField.setText(workerUser.email);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Phone number for notifications
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("📱 Phone Number (for notifications):"), gbc);
        JTextField phoneField = UITheme.createModernTextField(20);
        phoneField.setText(workerUser.phoneNumber != null ? workerUser.phoneNumber : "");
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        // Employee info
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Position:"), gbc);
        JTextField positionField = UITheme.createModernTextField(20);
        positionField.setText(employeeInfo != null ? employeeInfo.position : "");
        positionField.setEditable(false);
        gbc.gridx = 1;
        panel.add(positionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Department:"), gbc);
        JTextField deptField = UITheme.createModernTextField(20);
        deptField.setText(employeeInfo != null ? employeeInfo.department : "");
        deptField.setEditable(false);
        gbc.gridx = 1;
        panel.add(deptField, gbc);

        // Notification preferences
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        JLabel notifLabel = new JLabel("🔔 Notification Preferences");
        notifLabel.setFont(UITheme.FONT_SUBHEADING);
        gbc.gridy = 10;
        panel.add(notifLabel, gbc);

        gbc.gridy = 11;
        JCheckBox salaryNotif = new JCheckBox("Notify when salary is released", true);
        panel.add(salaryNotif, gbc);

        gbc.gridy = 12;
        JCheckBox billNotif = new JCheckBox("Notify about due bills", true);
        panel.add(billNotif, gbc);

        gbc.gridy = 13;
        JCheckBox msgNotif = new JCheckBox("Notify when I receive messages", true);
        panel.add(msgNotif, gbc);

        // Save button
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        JButton saveBtn = UITheme.createModernButton("💾 Save Changes", UITheme.SUCCESS_GREEN);
        saveBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.addActionListener(e -> {
            updateUserPhone(phoneField.getText());
            ProfileManager.updateNickname(workerUser.id, nicknameField.getText());
            ProfileManager.updateBio(workerUser.id, bioArea.getText());
            JOptionPane.showMessageDialog(panel, 
                "✓ Profile updated successfully!\n\n" +
                "Your display name is now: " + nicknameField.getText() + "\n" +
                "(Your actual name in the system remains unchanged)",
                "Profile Updated",
                JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(saveBtn, gbc);

        panel.add(Box.createVerticalGlue());
        return new JScrollPane(panel);
    }

    /**
     * Create announcements panel
     */
    private JScrollPane createAnnouncementsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("📢 Announcements");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));

        List<AnnouncementManager.AnnouncementInfo> announcements = AnnouncementManager.getAllAnnouncements();
        
        if (announcements.isEmpty()) {
            JLabel noAnnouncementLabel = new JLabel("No announcements from management at this time");
            noAnnouncementLabel.setFont(UITheme.FONT_REGULAR);
            noAnnouncementLabel.setForeground(UITheme.TEXT_SECONDARY);
            JPanel emptyCard = UITheme.createCardPanel();
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            emptyCard.add(noAnnouncementLabel);
            panel.add(emptyCard);
        } else {
            for (AnnouncementManager.AnnouncementInfo announcement : announcements) {
                JPanel announcementCard = UITheme.createCardPanel();
                announcementCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
                announcementCard.setLayout(new BoxLayout(announcementCard, BoxLayout.Y_AXIS));
                
                JLabel announcementTitleLabel = new JLabel(announcement.title);
                announcementTitleLabel.setFont(UITheme.FONT_BOLD);
                announcementTitleLabel.setForeground(UITheme.PRIMARY_BLUE);
                announcementTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel announcementMsgLabel = new JLabel(announcement.message);
                announcementMsgLabel.setFont(UITheme.FONT_REGULAR);
                announcementMsgLabel.setForeground(UITheme.TEXT_PRIMARY);
                announcementMsgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                JLabel announcementByLabel = new JLabel("From: " + announcement.announcedBy + " • " + announcement.createdAt);
                announcementByLabel.setFont(UITheme.FONT_SMALL);
                announcementByLabel.setForeground(UITheme.TEXT_SECONDARY);
                announcementByLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                announcementCard.add(announcementTitleLabel);
                announcementCard.add(Box.createVerticalStrut(8));
                announcementCard.add(announcementMsgLabel);
                announcementCard.add(Box.createVerticalStrut(5));
                announcementCard.add(announcementByLabel);
                panel.add(announcementCard);
                panel.add(Box.createVerticalStrut(15));
            }
        }

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
