package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * WorkerPanel.java
 * Provides a restricted view for workers to safely check their:
 * - Individual wage status
 * - Payment records
 * - Salary history
 * - Deductions and tax information
 * 
 * Workers can only view their own records, not company-wide data.
 */
public class WorkerPanel extends JFrame {
    private String loggedInUsername;
    private JTabbedPane tabbedPane;

    public WorkerPanel(String username) {
        this.loggedInUsername = username;

        // Frame setup
        setTitle("Payroll Management System - Worker Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        UITheme.applyTheme(this);
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Add modern header with welcome message
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.BORDER_GRAY);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel("👷");
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 24));
        headerPanel.add(iconLabel, BorderLayout.WEST);

        JPanel welcomePanel = new JPanel(new BoxLayout(new JPanel(), BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(UITheme.FONT_HEADING);
        welcomeLabel.setForeground(UITheme.TEXT_PRIMARY);
        JLabel subtitleLabel = new JLabel("Worker Portal - View Your Payroll Information");
        subtitleLabel.setFont(UITheme.FONT_REGULAR);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(subtitleLabel);
        headerPanel.add(welcomePanel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed interface with icons
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.BG_LIGHT);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);
        
        tabbedPane.addTab("💰 My Wages", createMyWagesPanel());
        tabbedPane.addTab("📊 Payment Records", createPaymentRecordsPanel());
        tabbedPane.addTab("📉 Deductions", createDeductionsPanel());
        tabbedPane.addTab("👤 Profile", createProfilePanel());

        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tabContainer.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(tabContainer, BorderLayout.CENTER);

        add(mainPanel);

        // Add menu bar
        createMenuBar();
    }

    /**
     * Creates the "My Wages" panel where workers can view their wage information.
     */
    private JPanel createMyWagesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        // Header
        gbc.gridy = 0;
        formPanel.add(UITheme.createSectionHeader("💼 Your Wage Information"), gbc);

        JTextField hourlyRateField = UITheme.createModernTextField(25);
        hourlyRateField.setEditable(false);
        hourlyRateField.setBackground(new Color(240, 240, 240));
        hourlyRateField.setText("$25.00");

        JTextField hoursWorkedField = UITheme.createModernTextField(25);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        formPanel.add(UITheme.createFormFieldPanel("Your Hourly Rate", hourlyRateField), gbc);
        gbc.gridx = 1;
        formPanel.add(UITheme.createFormFieldPanel("Hours Worked", hoursWorkedField), gbc);

        // Wage details area
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        JTextArea wageDetailsArea = new JTextArea(16, 50);
        wageDetailsArea.setEditable(false);
        wageDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        wageDetailsArea.setBackground(new Color(248, 249, 250));
        wageDetailsArea.setForeground(UITheme.TEXT_PRIMARY);
        wageDetailsArea.setText("Enter your hours worked and click 'Calculate' to see your wage breakdown.");
        JScrollPane scrollPane = new JScrollPane(wageDetailsArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        formPanel.add(scrollPane, gbc);

        // Buttons
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(15, 0, 0, 0);
        gbc.gridx = 0;

        JButton calculateButton = UITheme.createSuccessButton("Calculate");
        calculateButton.addActionListener(e -> {
            try {
                String rateText = hourlyRateField.getText().replace("$", "").trim();
                double hourlyRate = Double.parseDouble(rateText);
                double hoursWorked = Double.parseDouble(hoursWorkedField.getText());

                if (hoursWorked <= 0) {
                    JOptionPane.showMessageDialog(null, "Hours must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String breakdown = SalaryCalculator.getSalaryBreakdown(hourlyRate, hoursWorked);
                wageDetailsArea.setText(breakdown);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers.", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(calculateButton, gbc);

        gbc.gridx = 1;
        JButton clearButton = UITheme.createSecondaryButton("Clear");
        clearButton.addActionListener(e -> {
            hoursWorkedField.setText("");
            wageDetailsArea.setText("Enter your hours worked and click 'Calculate' to see your wage breakdown.");
        });
        formPanel.add(clearButton, gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        JScrollPane mainScroll = new JScrollPane(formPanel);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(mainScroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the "Payment Records" panel showing payment history.
     */
    private JPanel createPaymentRecordsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BG_LIGHT);

        JPanel headerPanel = UITheme.createHeaderPanel("Payment History", "📊");
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JTextArea paymentHistoryArea = new JTextArea(20, 60);
        paymentHistoryArea.setEditable(false);
        paymentHistoryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        paymentHistoryArea.setBackground(new Color(248, 249, 250));
        paymentHistoryArea.setForeground(UITheme.TEXT_PRIMARY);
        paymentHistoryArea.setText(
            "╔════════════════════════════════════════════════════════════════════╗\n" +
            "║ Your Payment Records                                             ║\n" +
            "╠════════════════════════════════════════════════════════════════════╣\n" +
            "║ Date       │ Gross Pay  │ Deductions │ Net Pay    │ Status       ║\n" +
            "╠════════════════════════════════════════════════════════════════════╣\n" +
            "║ 2026-04-01 │ $2,000.00  │ $500.00    │ $1,500.00  │ Paid ✓       ║\n" +
            "║ 2026-03-15 │ $2,080.00  │ $520.00    │ $1,560.00  │ Paid ✓       ║\n" +
            "║ 2026-03-01 │ $1,960.00  │ $490.00    │ $1,470.00  │ Paid ✓       ║\n" +
            "║ 2026-02-15 │ $2,000.00  │ $500.00    │ $1,500.00  │ Paid ✓       ║\n" +
            "║ 2026-02-01 │ $2,040.00  │ $510.00    │ $1,530.00  │ Paid ✓       ║\n" +
            "╚════════════════════════════════════════════════════════════════════╝\n\n" +
            "💡 Tip: Contact HR for detailed payment slip for any date above."
        );

        JScrollPane scrollPane = new JScrollPane(paymentHistoryArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the "Deductions" panel showing tax and deduction information.
     */
    private JPanel createDeductionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BG_LIGHT);

        JPanel headerPanel = UITheme.createHeaderPanel("Tax & Deductions", "📉");
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        JTextArea deductionsArea = new JTextArea(20, 70);
        deductionsArea.setEditable(false);
        deductionsArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        deductionsArea.setBackground(new Color(248, 249, 250));
        deductionsArea.setForeground(UITheme.TEXT_PRIMARY);
        deductionsArea.setText(
            "═══════════════════════════════════════════════════════════════════════\n" +
            "                    TAX & DEDUCTION SUMMARY\n" +
            "═══════════════════════════════════════════════════════════════════════\n\n" +
            "📊 DEDUCTION BREAKDOWN:\n\n" +
            "1. 💼 Income Tax (Federal)\n" +
            "   Rate: 15% of gross salary\n" +
            "   Purpose: Federal income tax withholding\n" +
            "   Monthly (from $2,000 gross): $300.00\n\n" +
            "2. 🏛️  Social Security\n" +
            "   Rate: 6.2% of gross salary\n" +
            "   Purpose: Retirement and disability benefits\n" +
            "   Monthly (from $2,000 gross): $124.00\n\n" +
            "3. 🏥 Medicare\n" +
            "   Rate: 1.45% of gross salary\n" +
            "   Purpose: Healthcare insurance\n" +
            "   Monthly (from $2,000 gross): $29.00\n\n" +
            "4. 🛡️  Health Insurance\n" +
            "   Amount: $100.00 per pay period\n" +
            "   Purpose: Group health coverage\n" +
            "   Monthly: $100.00\n\n" +
            "5. 🏦 Pension Contribution\n" +
            "   Amount: $50.00 per pay period\n" +
            "   Purpose: Retirement savings plan\n" +
            "   Monthly: $50.00\n\n" +
            "═══════════════════════════════════════════════════════════════════════\n" +
            "TOTAL ESTIMATED DEDUCTIONS (from $2,000 gross):\n" +
            "   Income Tax + SS + Medicare + Health + Pension = $603.00 / month\n" +
            "   Net Pay: $1,397.00 / month\n" +
            "═══════════════════════════════════════════════════════════════════════\n\n" +
            "❓ For questions about deductions, contact HR department."
        );

        JScrollPane scrollPane = new JScrollPane(deductionsArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the "Profile" panel showing worker profile information.
     */
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        // Header
        gbc.gridy = 0;
        formPanel.add(UITheme.createSectionHeader("👤 Your Profile"), gbc);

        DBConnection.WorkerProfile profile = DBConnection.getWorkerProfileByUsername(loggedInUsername);
        String firstName = profile != null && profile.firstName != null ? profile.firstName : "";
        String lastName = profile != null && profile.lastName != null ? profile.lastName : "";
        String email = profile != null && profile.email != null ? profile.email : "";
        String phoneNumber = profile != null && profile.phoneNumber != null ? profile.phoneNumber : "";
        String department = profile != null && profile.department != null ? profile.department : "N/A";
        String position = profile != null && profile.position != null ? profile.position : "N/A";
        String employeeId = profile != null && profile.employeeId != null ? profile.employeeId : "N/A";

        JTextField usernameField = UITheme.createModernTextField(25);
        usernameField.setText(loggedInUsername);
        usernameField.setEditable(false);
        usernameField.setBackground(new Color(240, 240, 240));

        JTextField employeeIdField = UITheme.createModernTextField(25);
        employeeIdField.setText(employeeId);
        employeeIdField.setEditable(false);
        employeeIdField.setBackground(new Color(240, 240, 240));

        // Row 1: Username & Employee ID
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        formPanel.add(UITheme.createFormFieldPanel("Username", usernameField), gbc);
        gbc.gridx = 1;
        formPanel.add(UITheme.createFormFieldPanel("Employee ID", employeeIdField), gbc);

        JTextField firstNameField = UITheme.createModernTextField(25);
        firstNameField.setText(firstName);
        JTextField lastNameField = UITheme.createModernTextField(25);
        lastNameField.setText(lastName);

        // Row 2: First & Last Name
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(UITheme.createFormFieldPanel("First Name", firstNameField), gbc);
        gbc.gridx = 1;
        formPanel.add(UITheme.createFormFieldPanel("Last Name", lastNameField), gbc);

        JTextField emailField = UITheme.createModernTextField(25);
        emailField.setText(email);
        JTextField phoneField = UITheme.createModernTextField(25);
        phoneField.setText(phoneNumber);

        // Row 3: Email & Phone
        gbc.gridy = 3;
        gbc.gridx = 0;
        formPanel.add(UITheme.createFormFieldPanel("Email", emailField), gbc);
        gbc.gridx = 1;
        formPanel.add(UITheme.createFormFieldPanel("Phone Number", phoneField), gbc);

        JTextField departmentField = UITheme.createModernTextField(25);
        departmentField.setText(department);
        departmentField.setEditable(false);
        departmentField.setBackground(new Color(240, 240, 240));

        JTextField positionField = UITheme.createModernTextField(25);
        positionField.setText(position);
        positionField.setEditable(false);
        positionField.setBackground(new Color(240, 240, 240));

        // Row 4: Department & Position (read-only)
        gbc.gridy = 4;
        gbc.gridx = 0;
        formPanel.add(UITheme.createFormFieldPanel("Department", departmentField), gbc);
        gbc.gridx = 1;
        formPanel.add(UITheme.createFormFieldPanel("Position", positionField), gbc);

        // Section: Change Password
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(UITheme.createSectionHeader("🔐 Change Password"), gbc);

        JPasswordField newPasswordField = UITheme.createModernPasswordField(25);
        JPasswordField confirmPasswordField = UITheme.createModernPasswordField(25);

        char passwordEchoChar = newPasswordField.getEchoChar();
        
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        formPanel.add(UITheme.createFormFieldPanel("New Password", newPasswordField), gbc);
        gbc.gridx = 1;
        formPanel.add(UITheme.createFormFieldPanel("Confirm Password", confirmPasswordField), gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JCheckBox showPasswordCheckbox = new JCheckBox("Show passwords");
        showPasswordCheckbox.setOpaque(false);
        showPasswordCheckbox.setFont(UITheme.FONT_REGULAR);
        showPasswordCheckbox.setForeground(UITheme.TEXT_SECONDARY);
        showPasswordCheckbox.addActionListener(e -> {
            if (showPasswordCheckbox.isSelected()) {
                newPasswordField.setEchoChar((char) 0);
                confirmPasswordField.setEchoChar((char) 0);
            } else {
                newPasswordField.setEchoChar(passwordEchoChar);
                confirmPasswordField.setEchoChar(passwordEchoChar);
            }
        });
        formPanel.add(showPasswordCheckbox, gbc);

        // Buttons
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.gridx = 0;

        JButton saveButton = UITheme.createSuccessButton("Save Changes");
        saveButton.addActionListener(e -> {
            if (profile == null) {
                JOptionPane.showMessageDialog(null, "Profile could not be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String updatedFirstName = firstNameField.getText().trim();
            String updatedLastName = lastNameField.getText().trim();
            String updatedEmail = emailField.getText().trim();
            String updatedPhone = phoneField.getText().trim();
            String updatedPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (updatedFirstName.isEmpty() || updatedLastName.isEmpty() || updatedEmail.isEmpty()) {
                JOptionPane.showMessageDialog(null, "First name, last name, and email are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update profile information
            if (DBConnection.updateWorkerProfile(profile.id, updatedFirstName, updatedLastName, updatedEmail, updatedPhone)) {
                JOptionPane.showMessageDialog(null, "✓ Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Update password if new one is provided
            if (!updatedPassword.isEmpty()) {
                if (!updatedPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (updatedPassword.length() < 6) {
                    JOptionPane.showMessageDialog(null, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (DBConnection.updateWorkerPassword(profile.id, updatedPassword)) {
                    JOptionPane.showMessageDialog(null, "✓ Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    newPasswordField.setText("");
                    confirmPasswordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to change password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        formPanel.add(saveButton, gbc);

        gbc.gridx = 1;
        JButton cancelButton = UITheme.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> {
            firstNameField.setText(firstName);
            lastNameField.setText(lastName);
            emailField.setText(email);
            phoneField.setText(phoneNumber);
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        });
        formPanel.add(cancelButton, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the menu bar for the worker panel.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            this.dispose();
            // Return to login screen
            new LoginFrame().setVisible(true);
        });
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem faqItem = new JMenuItem("FAQ");
        faqItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "FREQUENTLY ASKED QUESTIONS\n\n" +
                "Q: How is my net pay calculated?\n" +
                "A: Net Pay = Gross Salary - Deductions\n\n" +
                "Q: What deductions apply to my salary?\n" +
                "A: Income Tax (15%), Social Security (6.2%), Medicare (1.45%),\n" +
                "Health Insurance ($100), Pension ($50)\n\n" +
                "Q: How is overtime calculated?\n" +
                "A: Hours over 40 per week are paid at 1.5x your hourly rate.\n\n" +
                "Q: Who do I contact for payroll questions?\n" +
                "A: Please contact HR at hr@company.com", 
                "Help - FAQ", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Payroll Management System v1.0\n" +
                "Worker Module\n\n" +
                "For support, contact: support@payroll.com", 
                "About", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(faqItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
}
