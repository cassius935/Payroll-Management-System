package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * main.java
 * Entry point for the Payroll Management System application.
 * Initializes and launches the application through LoginFrame.
 * 
 * FULLY EXECUTABLE - This is the main entry point class
 */
public class main {
    public static void main(String[] args) {
        try {
            // Initialize the application on the Event Dispatch Thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Load employee data
                        EmployeeManager.loadExistingEmployeeIds();
                        
                        // Launch the login frame
                        new LoginFrame();
                    } catch (Exception e) {
                        System.err.println("Error launching login frame: " + e.getMessage());
                        e.printStackTrace();
                        showErrorDialog("Failed to launch login interface: " + e.getMessage());
                    }
                }
            });
        } catch (Exception t) {
            System.err.println("Application startup error: " + t.getMessage());
            t.printStackTrace();
            showErrorDialog("Unable to launch Payroll Management System.\nPlease check your Java setup and classpath.");
        }
    }
    
    /**
     * Display error dialog
     */
    private static void showErrorDialog(String message) {
        try {
            JOptionPane.showMessageDialog(null,
                message,
                "Startup Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error: " + message);
        }
    }
}
