package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * AdminPanel.java
 * Provides the administrator interface with functions to:
 * - Register new team members with automatic unique ID generation
 * - Manage employee records
 * - View payroll information
 * - Access system settings
 */
public class AdminPanel extends JFrame {
    private JTabbedPane tabbedPane;

    public AdminPanel() {
        // Frame setup
        setTitle("Payroll Management System - Administrator Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        UITheme.applyTheme(this);
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // Create main panel with header and tabs
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(UITheme.BG_LIGHT);

        // Add header
        JPanel headerPanel = UITheme.createHeaderPanel("👨‍💼 Administrator Panel", "👨‍💼");
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create tabbed interface with icons
        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.BG_LIGHT);
        tabbedPane.setForeground(UITheme.TEXT_PRIMARY);

        // Add tabs for different admin functions
        tabbedPane.addTab("📝 Register Employee", createEmployeeRegistrationPanel());
        tabbedPane.addTab("👥 View Employees", createViewEmployeesPanel());
        tabbedPane.addTab("💰 Manage Payroll", createPayrollManagementPanel());
        tabbedPane.addTab("⚙️ Settings", createSettingsPanel());

        // Add tabbedPane with padding
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        tabContainer.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(tabContainer, BorderLayout.CENTER);

        add(mainPanel);

        // Add menu bar
        createMenuBar();
    }

    /**
     * Creates the employee registration panel.
     * Allows administrators to register new employees with automatic ID generation.
     */
    private JPanel createEmployeeRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Scroll pane for form
        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setBackground(UITheme.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        // Section 1: Personal Information
        gbc.gridy = 0;
        formContent.add(UITheme.createSectionHeader("👤 Personal Information"), gbc);

        JTextField firstNameField = UITheme.createModernTextField(25);
        JTextField lastNameField = UITheme.createModernTextField(25);
        JTextField emailField = UITheme.createModernTextField(25);
        JTextField phoneNumberField = UITheme.createModernTextField(25);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formContent.add(UITheme.createFormFieldPanel("First Name", firstNameField), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formContent.add(UITheme.createFormFieldPanel("Last Name", lastNameField), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formContent.add(UITheme.createFormFieldPanel("Email", emailField), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formContent.add(UITheme.createFormFieldPanel("Phone Number", phoneNumberField), gbc);

        // Section 2: Job Information
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formContent.add(UITheme.createSectionHeader("💼 Job Information"), gbc);

        JComboBox<String> departmentBox = new JComboBox<>(
            new String[]{"Sales", "IT", "HR", "Finance", "Operations"}
        );
        departmentBox.setFont(UITheme.FONT_REGULAR);
        departmentBox.setPreferredSize(new Dimension(0, 35));

        JTextField positionField = UITheme.createModernTextField(25);
        JTextField hourlyRateField = UITheme.createModernTextField(25);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        formContent.add(UITheme.createFormFieldPanel("Department", departmentBox), gbc);
        gbc.gridx = 1;
        formContent.add(UITheme.createFormFieldPanel("Position", positionField), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formContent.add(UITheme.createFormFieldPanel("Hourly Rate ($)", hourlyRateField), gbc);

        JTextField employeeIdField = UITheme.createModernTextField(25);
        employeeIdField.setEditable(false);
        employeeIdField.setBackground(new Color(240, 240, 240));
        gbc.gridx = 1;
        formContent.add(UITheme.createFormFieldPanel("Employee ID (Auto-Generated)", employeeIdField), gbc);

        // Section 3: Actions
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formContent.add(UITheme.createSectionHeader("📋 Action"), gbc);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        JButton generateIdButton = UITheme.createModernButton("Generate ID", UITheme.SECONDARY_TEAL);
        generateIdButton.addActionListener(e -> {
            String employeeId = EmployeeManager.generateUniqueEmployeeId();
            employeeIdField.setText(employeeId);
        });

        JButton registerButton = UITheme.createSuccessButton("Register Employee");
        registerButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String phoneNumber = phoneNumberField.getText().trim();
                String department = (String) departmentBox.getSelectedItem();
                String position = positionField.getText().trim();
                String hourlyRateText = hourlyRateField.getText().trim();
                String employeeId = employeeIdField.getText().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    phoneNumber.isEmpty() || position.isEmpty() || hourlyRateText.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                        "Please fill in all fields.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    double hourlyRate = Double.parseDouble(hourlyRateText);

                    if (!EmployeeManager.validateEmployeeData(firstName, lastName, email, hourlyRate)) {
                        JOptionPane.showMessageDialog(null,
                            "Invalid employee data. Please check all fields.",
                            "Validation Error",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (employeeId.isEmpty()) {
                        employeeId = EmployeeManager.generateUniqueEmployeeId();
                        employeeIdField.setText(employeeId);
                    }

                    DBConnection.WorkerCreationInfo workerInfo = DBConnection.createWorkerAccount(
                        firstName, lastName, email, phoneNumber, department, position, hourlyRate, employeeId
                    );

                    if (workerInfo == null) {
                        JOptionPane.showMessageDialog(null,
                            "Unable to create worker account. Check database connection.",
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JOptionPane.showMessageDialog(null,
                        "✓ Employee registered successfully!\n\n" +
                        "Username: " + workerInfo.username + "\n" +
                        "Temp Password: " + workerInfo.temporaryPassword,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    firstNameField.setText("");
                    lastNameField.setText("");
                    emailField.setText("");
                    phoneNumberField.setText("");
                    positionField.setText("");
                    hourlyRateField.setText("");
                    employeeIdField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid hourly rate.", "Format Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionPanel.add(generateIdButton);
        actionPanel.add(registerButton);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formContent.add(actionPanel, gbc);

        gbc.gridy = 8;
        gbc.weighty = 1.0;
        formContent.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(formContent);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the view employees panel.
     * Displays list of all registered employees with their usernames and password management.
     */
    private JPanel createViewEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Employee Directory - View & Manage");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(10, 40);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JButton refreshButton = new JButton("Refresh List");
        JButton changePasswordButton = new JButton("Change Password");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(changePasswordButton);

        // Load employee data from database
        refreshButton.addActionListener(e -> {
            try {
                Connection con = DBConnection.getConnection();
                if (con != null) {
                    String query = "SELECT u.id, u.username, u.first_name, u.last_name, e.department, e.position, u.email, u.phone_number, e.employee_id " +
                                   "FROM users u JOIN employee e ON u.id = e.user_id " +
                                   "WHERE u.user_type = 'WORKER' ORDER BY e.employee_id";
                    PreparedStatement pst = con.prepareStatement(query);
                    ResultSet rs = pst.executeQuery();

                    StringBuilder employeeList = new StringBuilder();
                    employeeList.append("╔════════════════════════════════════════════════════════════════════════════════════════╗\n");
                    employeeList.append(String.format("║ %-15s │ %-15s │ %-20s │ %-20s │ %-10s ║\n", 
                                       "Employee ID", "Username", "Name", "Department", "Position"));
                    employeeList.append("╠════════════════════════════════════════════════════════════════════════════════════════╣\n");

                    boolean hasRecords = false;
                    while (rs.next()) {
                        hasRecords = true;
                        String empId = rs.getString("employee_id");
                        String username = rs.getString("username");
                        String name = rs.getString("first_name") + " " + rs.getString("last_name");
                        String dept = rs.getString("department");
                        String pos = rs.getString("position");

                        employeeList.append(String.format("║ %-15s │ %-15s │ %-20s │ %-20s │ %-10s ║\n", 
                                           empId, username, name, dept, pos));
                    }

                    if (!hasRecords) {
                        employeeList.append("║ No workers registered yet.                                                                        ║\n");
                    }

                    employeeList.append("╚════════════════════════════════════════════════════════════════════════════════════════╝\n\n");
                    employeeList.append("NOTE: All employees have been registered and can login with their username.\n");
                    employeeList.append("To change an employee's password, click 'Change Password' button.\n");

                    textArea.setText(employeeList.toString());
                    rs.close();
                    pst.close();
                    con.close();
                } else {
                    textArea.setText("Database connection failed. Cannot load employee data.");
                }
            } catch (SQLException ex) {
                textArea.setText("Error loading employee data: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        changePasswordButton.addActionListener(e -> {
            String employeeId = JOptionPane.showInputDialog(panel, 
                "Enter Employee ID to change password:", 
                "Change Employee Password", 
                JOptionPane.INFORMATION_MESSAGE);

            if (employeeId != null && !employeeId.trim().isEmpty()) {
                try {
                    Connection con = DBConnection.getConnection();
                    if (con != null) {
                        String query = "SELECT u.id, u.username, u.first_name, u.last_name FROM users u " +
                                       "JOIN employee e ON u.id = e.user_id WHERE e.employee_id = ? AND u.user_type = 'WORKER'";
                        PreparedStatement pst = con.prepareStatement(query);
                        pst.setString(1, employeeId.trim());
                        ResultSet rs = pst.executeQuery();

                        if (rs.next()) {
                            int userId = rs.getInt("id");
                            String username = rs.getString("username");
                            String name = rs.getString("first_name") + " " + rs.getString("last_name");

                            JPasswordField passwordField = new JPasswordField();
                            JPasswordField confirmField = new JPasswordField();
                            Object[] message = {
                                "Employee: " + name + " (" + username + ")",
                                "New Password:", passwordField,
                                "Confirm Password:", confirmField
                            };

                            int option = JOptionPane.showConfirmDialog(panel, message,
                                "Set New Password", JOptionPane.OK_CANCEL_OPTION);

                            if (option == JOptionPane.OK_OPTION) {
                                String newPassword = new String(passwordField.getPassword());
                                String confirmPassword = new String(confirmField.getPassword());

                                if (newPassword.isEmpty()) {
                                    JOptionPane.showMessageDialog(panel,
                                        "Password cannot be empty.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                } else if (!newPassword.equals(confirmPassword)) {
                                    JOptionPane.showMessageDialog(panel,
                                        "Passwords do not match.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                } else if (newPassword.length() < 6) {
                                    JOptionPane.showMessageDialog(panel,
                                        "Password must be at least 6 characters long.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    if (DBConnection.adminChangeEmployeePassword(userId, newPassword)) {
                                        JOptionPane.showMessageDialog(panel,
                                            "Password changed successfully for " + name,
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                                    } else {
                                        JOptionPane.showMessageDialog(panel,
                                            "Failed to change password.",
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                            rs.close();
                        } else {
                            JOptionPane.showMessageDialog(panel,
                                "Employee not found with ID: " + employeeId,
                                "Not Found", JOptionPane.WARNING_MESSAGE);
                        }
                        pst.close();
                        con.close();
                    } else {
                        JOptionPane.showMessageDialog(panel,
                            "Database connection failed.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel,
                        "Error: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // Initial load
        refreshButton.doClick();

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Creates the payroll management panel.
     * Allows administrators to calculate and manage payroll.
     */
    private JPanel createPayrollManagementPanel() {
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
        formPanel.add(UITheme.createSectionHeader("💰 Calculate Net Pay"), gbc);

        JTextField hourlyRateField = UITheme.createModernTextField(25);
        JTextField hoursWorkedField = UITheme.createModernTextField(25);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        formPanel.add(UITheme.createFormFieldPanel("Hourly Rate ($)", hourlyRateField), gbc);
        gbc.gridx = 1;
        formPanel.add(UITheme.createFormFieldPanel("Hours Worked", hoursWorkedField), gbc);

        // Result area
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 10, 0);
        JTextArea resultArea = new JTextArea(14, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        resultArea.setBackground(new Color(248, 249, 250));
        resultArea.setForeground(UITheme.TEXT_PRIMARY);
        JScrollPane scrollPane = new JScrollPane(resultArea);
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
                double hourlyRate = Double.parseDouble(hourlyRateField.getText());
                double hoursWorked = Double.parseDouble(hoursWorkedField.getText());

                if (hourlyRate <= 0 || hoursWorked <= 0) {
                    JOptionPane.showMessageDialog(panel, "Values must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String breakdown = SalaryCalculator.getSalaryBreakdown(hourlyRate, hoursWorked);
                resultArea.setText(breakdown);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid numbers.", "Format Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(calculateButton, gbc);

        gbc.gridx = 1;
        JButton clearButton = UITheme.createSecondaryButton("Clear");
        clearButton.addActionListener(e -> {
            hourlyRateField.setText("");
            hoursWorkedField.setText("");
            resultArea.setText("");
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
     * Creates the settings panel.
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(UITheme.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = 3;

        // Header
        gbc.gridy = 0;
        contentPanel.add(UITheme.createSectionHeader("⚙️ System Settings"), gbc);

        // Stats Cards
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 5, 10, 5);

        JPanel statPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        statPanel.setOpaque(false);

        JPanel card1 = UITheme.createInfoCard(
            "Total Employees",
            String.valueOf(EmployeeManager.getRegisteredEmployeeCount()),
            "Registered in system",
            UITheme.PRIMARY_BLUE
        );
        statPanel.add(card1);

        JPanel card2 = UITheme.createInfoCard(
            "System Status",
            "✓ Active",
            "All systems operational",
            UITheme.SUCCESS_GREEN
        );
        statPanel.add(card2);

        JPanel card3 = UITheme.createInfoCard(
            "Database",
            "Connected",
            "Payroll database",
            UITheme.SECONDARY_TEAL
        );
        statPanel.add(card3);

        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 0, 20, 0);
        contentPanel.add(statPanel, gbc);

        // Settings Section 1: Deduction Rates
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 0, 10, 0);
        contentPanel.add(UITheme.createSectionHeader("💰 Tax & Deduction Rates"), gbc);

        String[] deductions = {
            "Income Tax",
            "15%",
            "Social Security",
            "6.2%",
            "Medicare",
            "1.45%",
            "Health Insurance",
            "$100.00 / month",
            "Pension Contribution",
            "$50.00 / month"
        };

        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);

        for (int i = 0; i < deductions.length; i += 2) {
            gbc.gridx = (i / 2) % 3;
            if (i > 0 && i % 6 == 0) gbc.gridy++;

            JPanel deductionCard = new JPanel(new BoxLayout(new JPanel(), BoxLayout.Y_AXIS));
            deductionCard.setOpaque(false);

            JLabel nameLabel = new JLabel(deductions[i]);
            nameLabel.setFont(UITheme.FONT_BOLD);
            nameLabel.setForeground(UITheme.TEXT_PRIMARY);

            JLabel valueLabel = new JLabel(deductions[i + 1]);
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            valueLabel.setForeground(UITheme.PRIMARY_BLUE);

            deductionCard.add(nameLabel);
            deductionCard.add(Box.createVerticalStrut(5));
            deductionCard.add(valueLabel);

            contentPanel.add(deductionCard, gbc);
        }

        // Settings Section 2: Policies
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        contentPanel.add(UITheme.createSectionHeader("📋 Payroll Policies"), gbc);

        String[] policies = {
            "Regular Work Hours",
            "40 hours per week",
            "Overtime Rate",
            "1.5x hourly rate",
            "Pay Period",
            "Bi-weekly",
            "Payment Method",
            "Bank Transfer / Wallet"
        };

        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        gbc.gridx = 0;

        for (int i = 0; i < policies.length; i += 2) {
            if (i > 0 && i % 4 == 0) {
                gbc.gridy++;
                gbc.gridx = 0;
            } else if (i > 0) {
                gbc.gridx += 1;
            }

            JPanel policyCard = new JPanel(new BoxLayout(new JPanel(), BoxLayout.Y_AXIS));
            policyCard.setOpaque(false);

            JLabel nameLabel = new JLabel(policies[i]);
            nameLabel.setFont(UITheme.FONT_BOLD);
            nameLabel.setForeground(UITheme.TEXT_PRIMARY);

            JLabel valueLabel = new JLabel(policies[i + 1]);
            valueLabel.setFont(UITheme.FONT_REGULAR);
            valueLabel.setForeground(UITheme.SECONDARY_TEAL);

            policyCard.add(nameLabel);
            policyCard.add(Box.createVerticalStrut(5));
            policyCard.add(valueLabel);

            contentPanel.add(policyCard, gbc);
        }

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        contentPanel.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the menu bar.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            this.dispose();
            // TODO: Return to login screen
            new LoginFrame().setVisible(true);
        });
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Payroll Management System v1.0\n" +
                "Administrator Module\n\n" +
                "For support, contact: admin@payroll.com", 
                "About", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
}
