package src;

import javax.swing.*;
import java.awt.*;

// for the login screen
public class LoginFrame extends JFrame {
    public LoginFrame() {
        // setup window
        setTitle("Login - Starry Night Payroll");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // starry night background color
        JPanel panel = new JPanel();
        panel.setBackground(new Color(10, 24, 74)); // dark blue
        panel.setLayout(null);

        // title design
        JLabel titleLabel = new JLabel("System Login");
        titleLabel.setForeground(new Color(173, 216, 230)); // light star blue
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(120, 30, 200, 30);
        panel.add(titleLabel);

        // username setup
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(60, 100, 100, 30);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(140, 100, 180, 30);
        panel.add(userField);

        // for security
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(60, 150, 100, 30);
        panel.add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(140, 150, 180, 30);
        panel.add(passField);

        // login button
        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setBackground(new Color(24, 50, 115)); // mid blue
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBounds(140, 220, 120, 40);
        panel.add(loginBtn);

        // to run the check when clicked
        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            
            // simple check for admin or employee
            if (user.equals("admin")) {
                new AdminDashboard().setVisible(true);
                this.dispose(); // close login
            } else if (user.startsWith("emp")) {
                // example: emp1, emp2
                new EmployeeDashboard(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Try 'admin' or 'emp1'");
            }
        });

        add(panel);
    }
}