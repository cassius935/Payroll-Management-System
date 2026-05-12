package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.*;

/**
 * main.java
 * MAIN ENTRY POINT FOR THE PAYROLL MANAGEMENT SYSTEM
 * Initializes and launches the application with splash screen
 * Features: Proper error handling, database checks, and resource loading
 */
public class main {
    
    private static final String APP_NAME = "Payroll Management System";
    private static final String APP_VERSION = "2.0.0";
    private static boolean dbConnected = false;
    
    public static void main(String[] args) {
        try {
            // Initialize system configuration first
            SystemConfig.initialize();
            System.out.println(SystemConfig.getStartupReport());
            
            // Show splash screen for better user experience
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
            
            // Initialize application on Event Dispatch Thread
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    initializeApplication(splash);
                }
            });
        } catch (Exception t) {
            System.err.println("[CRITICAL] Application startup error: " + t.getMessage());
            t.printStackTrace();
            showErrorDialog("CRITICAL ERROR",
                "Unable to launch Payroll Management System.\n\n" +
                "Error: " + t.getMessage() + "\n\n" +
                "Please ensure:\n" +
                "1. Java version 8 or higher is installed\n" +
                "2. All required JAR files are in the classpath\n" +
                "3. MySQL driver (mysql-connector-java) is available\n" +
                "4. Database connection properties are correct");
        }
    }
    
    /**
     * Initialize the application with proper sequence
     */
    private static void initializeApplication(SplashScreen splash) {
        try {
            splash.updateStatus("Loading configuration...");
            Thread.sleep(300);
            
            // Step 1: Check database connection
            splash.updateStatus("Checking database connection...");
            dbConnected = DBConnection.isDatabaseAvailable();
            
            // Step 2: Load employee data
            splash.updateStatus("Loading employee data...");
            EmployeeManager.loadExistingEmployeeIds();
            Thread.sleep(300);
            
            // Step 3: Load notification manager
            splash.updateStatus("Initializing notification system...");
            NotificationManager.initialize();
            Thread.sleep(300);
            
            // Step 4: Launch login frame
            splash.updateStatus("Launching login interface...");
            Thread.sleep(300);
            splash.setVisible(false);
            splash.dispose();
            
            new LoginFrame();
            
        } catch (InterruptedException e) {
            System.err.println("[WARN] Initialization interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            splash.setVisible(false);
            splash.dispose();
            new LoginFrame();
        } catch (Exception e) {
            System.err.println("[ERROR] Initialization error: " + e.getMessage());
            e.printStackTrace();
            splash.setVisible(false);
            splash.dispose();
            
            if (dbConnected) {
                showErrorDialog("Initialization Error",
                    "Failed to initialize application:\n\n" + e.getMessage() +
                    "\n\nThe system will attempt to continue in demo mode.");
            } else {
                showErrorDialog("Database Connection Warning",
                    "Unable to connect to database.\n\n" +
                    "The system will run in OFFLINE/DEMO MODE.\n\n" +
                    "Ensure MySQL is running and database is configured:\n" +
                    "- Host: localhost:3306\n" +
                    "- Database: payroll_db\n" +
                    "- User: root");
            }
            
            // Try to continue anyway
            try {
                new LoginFrame();
            } catch (Exception ex) {
                showErrorDialog("FATAL ERROR",
                    "Cannot launch login frame: " + ex.getMessage());
                System.exit(1);
            }
        }
    }
    
    /**
     * Display error dialog with styling
     */
    private static void showErrorDialog(String title, String message) {
        try {
            JOptionPane.showMessageDialog(null,
                message,
                title + " - " + APP_NAME,
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("[ERROR] " + title + ": " + message);
        }
    }
    
    /**
     * Get database status
     */
    public static boolean isDatabaseConnected() {
        return dbConnected;
    }
    
    /**
     * Get application version
     */
    public static String getAppVersion() {
        return APP_VERSION;
    }
}
