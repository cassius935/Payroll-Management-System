package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import src.DBConnection.UserInfo;

/**
 * LoginFrame.java
 * Modern, unified login interface for both Admin and Worker users
 * No role selection - login determines user type automatically
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private char defaultPasswordEchoChar;
    private JButton loginButton;
    private JLabel errorLabel;
    private JLabel logoLabel;

    public LoginFrame() {
        // Frame setup
        setTitle("Mihoyo - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);
        
        UITheme.applyTheme(this);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, UITheme.PRIMARY_BLUE,
                    getWidth(), getHeight(), UITheme.SECONDARY_TEAL
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Top section with logo/branding
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));

        logoLabel = new JLabel("💼 MIHOYO");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Payroll Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 220, 240));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(logoLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(subtitleLabel);
        topPanel.add(Box.createVerticalGlue());

        // Center section with login form (white card)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 40, 20));
        centerPanel.setOpaque(false);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(UITheme.BG_WHITE);
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Add rounded corners effect
        formCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        formCard.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Form title
        JLabel formTitle = new JLabel("Login to Your Account");
        formTitle.setFont(UITheme.FONT_HEADING);
        formTitle.setForeground(UITheme.TEXT_PRIMARY);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        formCard.add(formTitle, gbc);

        // Username field
        gbc.gridy = 1;
        gbc.insets = new Insets(12, 0, 12, 0);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(UITheme.FONT_BOLD);
        usernameLabel.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(usernameLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 12, 0);
        usernameField = UITheme.createModernTextField(20);
        usernameField.setText("admin");  // Default for easy testing
        formCard.add(usernameField, gbc);

        // Password field
        gbc.gridy = 3;
        gbc.insets = new Insets(12, 0, 12, 0);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(UITheme.FONT_BOLD);
        passwordLabel.setForeground(UITheme.TEXT_PRIMARY);
        formCard.add(passwordLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 12, 0);
        passwordField = UITheme.createModernPasswordField(20);
        passwordField.setText("Admin@2024!");  // Default for easy testing
        defaultPasswordEchoChar = passwordField.getEchoChar();
        formCard.add(passwordField, gbc);

        gbc.gridy = 5;
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setForeground(new Color(80, 90, 110));
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar(defaultPasswordEchoChar);
            }
        });
        formCard.add(showPasswordCheckBox, gbc);

        // Error label
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 12, 0);
        errorLabel = new JLabel();
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.DANGER_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(errorLabel, gbc);

        // Login button with better styling
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton = UITheme.createModernButton("LOGIN", UITheme.PRIMARY_BLUE);
        loginButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.addActionListener(new LoginButtonListener());
        formCard.add(loginButton, gbc);

        // Forgot password link
        gbc.gridy = 8;
        gbc.insets = new Insets(15, 0, 0, 0);
        JLabel forgotLabel = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotLabel.setFont(UITheme.FONT_SMALL);
        forgotLabel.setForeground(UITheme.PRIMARY_BLUE);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                    "Password Reset Feature:\n\nPlease contact your administrator to reset your password.",
                    "Password Reset",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        formCard.add(forgotLabel, gbc);

        centerPanel.add(formCard, BorderLayout.CENTER);

        // Bottom info panel with better styling
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        JLabel infoLabel = new JLabel("<html><center><b>Demo Credentials:</b><br>" +
                                     "Admin: admin / Admin@2024!<br>" +
                                     "Worker: worker1 / Worker@123</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(200, 220, 240));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(infoLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Inner class to handle login button actions
     */
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Validation
            if (username.isEmpty()) {
                errorLabel.setText("❌ Please enter username");
                usernameField.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                errorLabel.setText("❌ Please enter password");
                passwordField.requestFocus();
                return;
            }

            if (username.length() < 3) {
                errorLabel.setText("❌ Username must be at least 3 characters");
                usernameField.requestFocus();
                return;
            }

            // Disable button and show loading state
            loginButton.setEnabled(false);
            loginButton.setText("🔄 LOGGING IN...");

            // Perform authentication in background thread
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Simulate processing
                    
                    // Authenticate user
                    UserInfo userInfo = DBConnection.authenticateUser(username, password);
                    
                    SwingUtilities.invokeLater(() -> {
                        if (userInfo != null) {
                            errorLabel.setText("");
                            DBConnection.updateLastLogin(userInfo.id);
                            
                            // Route to the modern panel implementation based on user type
                            LoginFrame.this.dispose();

                            if (userInfo.userType.equals("ADMIN")) {
                                new AdminPanelModern(userInfo).setVisible(true);
                            } else {
                                new WorkerPanelModern(userInfo).setVisible(true);
                            }
                        } else {
                            errorLabel.setText("❌ Invalid username or password");
                            passwordField.setText("");
                            loginButton.setEnabled(true);
                            loginButton.setText("LOGIN");
                            usernameField.requestFocus();
                        }
                    });
                } catch (InterruptedException ex) {
                    SwingUtilities.invokeLater(() -> {
                        loginButton.setEnabled(true);
                        loginButton.setText("LOGIN");
                        errorLabel.setText("❌ Login interrupted, please try again");
                    });
                    System.err.println("Login thread interrupted: " + ex.getMessage());
                }
            }).start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}
