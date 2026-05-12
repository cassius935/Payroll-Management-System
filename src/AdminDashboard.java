import javax.swing.*;
import java.awt.*;

// admin home page
public class AdminDashboard extends JFrame {
    public AdminDashboard() {
        // setup admin window
        setTitle("Admin Dashboard - Starry Night");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // starry night colors
        JPanel panel = new JPanel();
        panel.setBackground(new Color(10, 24, 74));
        panel.setLayout(null);

        JLabel title = new JLabel("Admin Control Panel");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBounds(130, 30, 250, 30);
        panel.add(title);

        // button to pay salary
        JButton payBtn = new JButton("Give Salary");
        payBtn.setBackground(new Color(24, 50, 115));
        payBtn.setForeground(Color.WHITE);
        payBtn.setBounds(80, 120, 150, 50);
        panel.add(payBtn);

        // button to message
        JButton msgBtn = new JButton("Message Employee");
        msgBtn.setBackground(new Color(24, 50, 115));
        msgBtn.setForeground(Color.WHITE);
        msgBtn.setBounds(250, 120, 170, 50);
        panel.add(msgBtn);

        // to logout safely
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(180, 230, 120, 30);
        panel.add(logoutBtn);

        // pay action
        payBtn.addActionListener(e -> {
            String emp = JOptionPane.showInputDialog("Enter Employee ID (e.g., emp1):");
            String amount = JOptionPane.showInputDialog("Enter Salary Amount ($):");
            if(emp != null && amount != null && !emp.isEmpty()) {
                EmployeeManager.giveSalary(emp, amount);
                JOptionPane.showMessageDialog(this, "Salary sent to " + emp);
            }
        });

        // message action
        msgBtn.addActionListener(e -> {
            String emp = JOptionPane.showInputDialog("Enter Employee ID (e.g., emp1):");
            String msg = JOptionPane.showInputDialog("Enter your message:");
            if(emp != null && msg != null && !emp.isEmpty()) {
                EmployeeManager.sendMessage(emp, msg);
                JOptionPane.showMessageDialog(this, "Message sent to " + emp);
            }
        });

        // logout action
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        add(panel);
    }
}