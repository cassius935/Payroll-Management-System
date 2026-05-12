package src;

import javax.swing.*;

/**
 * PayrollApp.java
 * Main entry point for the enhanced Payroll Management System application.
 * Initializes the application and launches the modern login interface.
 */
public class PayrollApp {

    public static void main(String[] args) {
        EmployeeManager.loadExistingEmployeeIds();

        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create and display the modern login frame
                new LoginFrame();
            }
        });
    }
}
