package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.*;

/**
 * main.java
 * MAIN ENTRY POINT FOR MIHOYO PAYROLL SYSTEM
 * Initializes and launches the application with splash screen
 * Features: Proper error handling, database checks, resource loading, and robust execution
 * This is the OFFICIAL entry point - run this directly to start the system
 */
public class main {
    
    private static final String APP_NAME = "Mihoyo";
    private static final String APP_VERSION = "3.0.0";
    private static boolean dbConnected = false;
    
    public static void main(String[] args) {
        try {
            // Ensure we're running on the Event Dispatch Thread
            if (!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        launchApplication();
                    }
                });
            } else {
                launchApplication();
            }
        } catch (Exception t) {
            System.err.println("[CRITICAL] Application startup error: " + t.getMessage());
            t.printStackTrace();
            showErrorDialog("CRITICAL ERROR",
                "Unable to launch Mihoyo System.\n\n" +
                "Error: " + t.getMessage() + "\n\n" +
                "Please ensure:\n" +
                "1. Java version 8 or higher is installed\n" +
                "2. All required JAR files are in the classpath\n" +
                "3. MySQL driver (mysql-connector-java) is available\n" +
                "4. Database connection properties are correct\n" +
                "5. MySQL server is running");
        }
    }
    
    /**
     * Launch the application safely
     */
    private static void launchApplication() {
        try {
            // Initialize system configuration first
            SystemConfig.initialize();
            System.out.println(SystemConfig.getStartupReport());
            
            // Show splash screen for better user experience
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
            
            // Initialize application
            initializeApplication(splash);
        } catch (Exception e) {
            System.err.println("[CRITICAL] Launch error: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("CRITICAL ERROR", "Failed to launch application: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Initialize the application with proper sequence - no forced dialogs
     */
    private static void initializeApplication(SplashScreen splash) {
        try {
            splash.updateStatus("Loading configuration...");
            Thread.sleep(200);
            
            // Step 1: Check database connection
            splash.updateStatus("Checking database connection...");
            dbConnected = DBConnection.isDatabaseAvailable();
            
            // Step 2: Load employee data
            splash.updateStatus("Loading employee data...");
            EmployeeManager.loadExistingEmployeeIds();
            Thread.sleep(200);
            
            // Step 3: Load notification manager
            splash.updateStatus("Initializing notification system...");
            NotificationManager.initialize();
            Thread.sleep(200);
            
            // Step 4: Launch login frame
            splash.updateStatus("Launching login interface...");
            Thread.sleep(200);
            splash.setVisible(false);
            splash.dispose();
            
            // Launch login without any dialogs or forced selections
            new LoginFrame();
            
        } catch (InterruptedException e) {
            System.err.println("[WARN] Initialization interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            handleInitializationError(splash, false);
        } catch (Exception e) {
            System.err.println("[ERROR] Initialization error: " + e.getMessage());
            e.printStackTrace();
            handleInitializationError(splash, dbConnected);
        }
    }
    
    /**
     * Handle initialization errors gracefully
     */
    private static void handleInitializationError(SplashScreen splash, boolean dbAvailable) {
        try {
            splash.setVisible(false);
            splash.dispose();
        } catch (Exception ex) {
            // Ignore disposal errors
        }
        
        if (dbAvailable) {
            showErrorDialog("Initialization Error",
                "Failed to initialize some components.\n\n" +
                "The system will continue to run.\n" +
                "Some features may be limited.");
        } else {
            showErrorDialog("Database Connection Warning",
                "Unable to connect to database.\n\n" +
                "The system will run in OFFLINE/DEMO MODE.\n\n" +
                "To fix this:\n" +
                "1. Ensure MySQL is running\n" +
                "2. Check database configuration\n" +
                "3. Verify connection credentials");
        }
        
        // Always try to launch login frame
        try {
            new LoginFrame();
        } catch (Exception e) {
            System.err.println("[CRITICAL] Failed to launch login frame: " + e.getMessage());
            System.exit(1);
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

