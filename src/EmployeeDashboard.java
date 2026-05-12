import javax.swing.*;
import java.awt.*;

// employee home page
public class EmployeeDashboard extends JFrame {
    public EmployeeDashboard(String empId) {
        // setup employee window
        setTitle("Employee Portal - " + empId);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // starry night colors
        JPanel panel = new JPanel();
        panel.setBackground(new Color(10, 24, 74));
        panel.setLayout(null);

        JLabel title = new JLabel("Welcome, " + empId);
        title.setForeground(new Color(173, 216, 230)); // star accent color
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBounds(40, 20, 250, 30);
        panel.add(title);

        // area to read messages and salary
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setBackground(new Color(24, 50, 115));
        displayArea.setForeground(Color.WHITE);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        // for scrolling if too many messages
        JScrollPane scroll = new JScrollPane(displayArea);
        scroll.setBounds(40, 70, 400, 200);
        panel.add(scroll);

        // button to check inbox
        JButton checkBtn = new JButton("Refresh Inbox");
        checkBtn.setBackground(Color.LIGHT_GRAY);
        checkBtn.setBounds(80, 300, 150, 40);
        panel.add(checkBtn);

        // logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(250, 300, 150, 40);
        panel.add(logoutBtn);

        // check inbox action
        checkBtn.addActionListener(e -> {
            String records = EmployeeManager.getRecords(empId);
            displayArea.setText(records);
        });

        // logout action
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        // load data automatically on start
        displayArea.setText(EmployeeManager.getRecords(empId));

        add(panel);
    }
}