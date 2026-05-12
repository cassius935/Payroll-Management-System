package src;

import java.io.*;
import java.util.*;

/**
 * SystemConfig.java
 * Centralized configuration and system utilities for the Payroll Management System
 * Handles database configuration, logging, and system diagnostics
 */
public class SystemConfig {
    
    private static final String TAG = "[SystemConfig]";
    private static final String CONFIG_FILE = "payroll_system.config";
    private static Properties configProperties;
    private static boolean isInitialized = false;
    
    // Default configuration values
    public static final String DB_URL = "jdbc:mysql://localhost:3306/payroll_db";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "";
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Application settings
    public static final String APP_NAME = "Mihoyo";
    public static final String APP_VERSION = "3.0.0";
    public static final String APP_BUILD = "2024-05-12";
    public static final boolean DEMO_MODE_ENABLED = true;
    
    // Notification settings
    public static final int NOTIFICATION_CHECK_INTERVAL = 30000; // 30 seconds
    public static final int MAX_NOTIFICATIONS_DISPLAY = 10;
    
    /**
     * Initialize system configuration
     */
    public static void initialize() {
        if (isInitialized) return;
        
        try {
            loadConfiguration();
            validateEnvironment();
            logSystemInfo();
            isInitialized = true;
            System.out.println(TAG + " System initialized successfully");
        } catch (Exception e) {
            System.err.println(TAG + " Initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load configuration from file or use defaults
     */
    private static void loadConfiguration() {
        configProperties = new Properties();
        
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                FileInputStream fis = new FileInputStream(configFile);
                configProperties.load(fis);
                fis.close();
                System.out.println(TAG + " Configuration loaded from file");
            } else {
                System.out.println(TAG + " Using default configuration");
            }
        } catch (IOException e) {
            System.err.println(TAG + " Error loading config file: " + e.getMessage());
        }
    }
    
    /**
     * Validate system environment
     */
    private static void validateEnvironment() {
        System.out.println("\n" + TAG + " ===== SYSTEM VALIDATION =====");
        
        // Check Java version
        String javaVersion = System.getProperty("java.version");
        double version = Double.parseDouble(javaVersion.split("\\.")[0]);
        System.out.println(TAG + " Java Version: " + javaVersion);
        if (version < 8) {
            System.err.println(TAG + " WARNING: Java 8 or higher is required");
        }
        
        // Check OS
        String osName = System.getProperty("os.name");
        System.out.println(TAG + " Operating System: " + osName);
        
        // Check available processors
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(TAG + " Available Processors: " + processors);
        
        // Check memory
        long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long totalMemory = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        System.out.println(TAG + " Memory - Max: " + maxMemory + "MB, Total: " + totalMemory + "MB");
        
        // Check database connection
        boolean dbConnected = DBConnection.isDatabaseAvailable();
        System.out.println(TAG + " Database Status: " + (dbConnected ? "CONNECTED" : "DISCONNECTED (DEMO MODE)"));
        
        // Check required classes
        checkRequiredClasses();
        
        System.out.println(TAG + " ===== VALIDATION COMPLETE =====\n");
    }
    
    /**
     * Verify all required classes are available
     */
    private static void checkRequiredClasses() {
        String[] requiredClasses = {
            "src.LoginFrame",
            "src.AdminPanelModern",
            "src.WorkerPanelModern",
            "src.DBConnection",
            "src.NotificationManager",
            "src.MessageManager",
            "src.SalaryCalculator"
        };
        
        for (String className : requiredClasses) {
            try {
                Class.forName(className);
                System.out.println(TAG + " ✓ " + className);
            } catch (ClassNotFoundException e) {
                System.err.println(TAG + " ✗ " + className + " NOT FOUND");
            }
        }
    }
    
    /**
     * Log system information
     */
    private static void logSystemInfo() {
        System.out.println("\n" + TAG + " ===== SYSTEM INFORMATION =====");
        System.out.println(TAG + " Application: " + APP_NAME);
        System.out.println(TAG + " Version: " + APP_VERSION);
        System.out.println(TAG + " Build Date: " + APP_BUILD);
        System.out.println(TAG + " Demo Mode: " + DEMO_MODE_ENABLED);
        System.out.println(TAG + " Database: " + DB_URL);
        System.out.println(TAG + " ===== ===== ===== =====\n");
    }
    
    /**
     * Get configuration property
     */
    public static String getProperty(String key, String defaultValue) {
        return configProperties.getProperty(key, defaultValue);
    }
    
    /**
     * Set configuration property
     */
    public static void setProperty(String key, String value) {
        configProperties.setProperty(key, value);
    }
    
    /**
     * Save configuration to file
     */
    public static void saveConfiguration() {
        try {
            FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
            configProperties.store(fos, "Payroll System Configuration");
            fos.close();
            System.out.println(TAG + " Configuration saved successfully");
        } catch (IOException e) {
            System.err.println(TAG + " Error saving configuration: " + e.getMessage());
        }
    }
    
    /**
     * Print diagnostic information
     */
    public static void printDiagnostics() {
        System.out.println("\n" + TAG + " ===== SYSTEM DIAGNOSTICS =====");
        System.out.println("Database Available: " + DBConnection.isDatabaseAvailable());
        System.out.println("Current Time: " + new java.util.Date());
        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        System.out.println("Temp Directory: " + System.getProperty("java.io.tmpdir"));
        System.out.println("User Home: " + System.getProperty("user.home"));
        System.out.println(TAG + " ===== END DIAGNOSTICS =====\n");
    }
    
    /**
     * Create startup verification report
     */
    public static String getStartupReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n========== STARTUP VERIFICATION REPORT ==========\n\n");
        report.append("Application: ").append(APP_NAME).append(" v").append(APP_VERSION).append("\n");
        report.append("Build Date: ").append(APP_BUILD).append("\n");
        report.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        report.append("Operating System: ").append(System.getProperty("os.name")).append("\n");
        report.append("Database Status: ").append(DBConnection.isDatabaseAvailable() ? "ONLINE" : "OFFLINE/DEMO").append("\n");
        report.append("Time: ").append(new java.util.Date()).append("\n");
        report.append("\n============================================\n");
        return report.toString();
    }
}
