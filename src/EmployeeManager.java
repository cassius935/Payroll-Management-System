package src;

import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.sql.*;

/**
 * EmployeeManager.java
 * Manages employee registration and automatically generates unique, non-repeating
 * identification numbers to prevent duplicates.
 * 
 * This class handles:
 * - Generating unique employee IDs
 * - Storing and tracking used IDs
 * - Registering new employees
 */
public class EmployeeManager {

    // Set to store all previously generated IDs (prevents duplicates)
    private static Set<String> usedEmployeeIds = new HashSet<>();
    
    // Random number generator for ID creation
    private static Random random = new Random();

    // ID prefix for easy identification
    private static final String ID_PREFIX = "EMP";

    /**
     * Generates a unique, non-repeating employee ID.
     * Format: EMP + 6-digit random number (e.g., EMP123456)
     * Ensures no duplicate IDs are generated.
     * 
     * @return A unique employee ID
     */
    public static String generateUniqueEmployeeId() {
        String employeeId;
        
        // Keep generating IDs until we find one that hasn't been used
        do {
            int randomNumber = 100000 + random.nextInt(900000); // Generates 6-digit number
            employeeId = ID_PREFIX + randomNumber;
        } while (usedEmployeeIds.contains(employeeId));

        // Add the new ID to the used set
        usedEmployeeIds.add(employeeId);

        return employeeId;
    }

    /**
     * Registers a new employee with their information.
     * Automatically generates a unique ID and stores the employee data.
     * 
     * @param firstName Employee's first name
     * @param lastName Employee's last name
     * @param email Employee's email address
     * @param department Employee's department
     * @param position Employee's job position
     * @param hourlyRate Employee's hourly rate
     * @return A formatted string with the registration details including the generated ID
     */
    public static String registerEmployee(String firstName, String lastName, 
                                         String email, String department, 
                                         String position, double hourlyRate) {
        // Generate unique ID for the new employee
        String employeeId = generateUniqueEmployeeId();

        // TODO: Insert employee data into database
        // Example: INSERT INTO employees VALUES (employeeId, firstName, lastName, email, department, position, hourlyRate)

        // Build registration confirmation
        StringBuilder registrationInfo = new StringBuilder();
        registrationInfo.append("========== EMPLOYEE REGISTRATION SUCCESS ==========\n");
        registrationInfo.append(String.format("Employee ID: %s\n", employeeId));
        registrationInfo.append(String.format("Name: %s %s\n", firstName, lastName));
        registrationInfo.append(String.format("Email: %s\n", email));
        registrationInfo.append(String.format("Department: %s\n", department));
        registrationInfo.append(String.format("Position: %s\n", position));
        registrationInfo.append(String.format("Hourly Rate: $%.2f\n", hourlyRate));
        registrationInfo.append("====================================================");

        return registrationInfo.toString();
    }

    /**
     * Validates employee information before registration.
     * 
     * @param firstName Employee's first name
     * @param lastName Employee's last name
     * @param email Employee's email address
     * @param hourlyRate Employee's hourly rate
     * @return true if all data is valid, false otherwise
     */
    public static boolean validateEmployeeData(String firstName, String lastName, 
                                               String email, double hourlyRate) {
        // Check if required fields are not empty
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() || 
            email.trim().isEmpty()) {
            return false;
        }

        // Validate email format (simple check)
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }

        // Validate hourly rate is positive
        if (hourlyRate <= 0) {
            return false;
        }

        return true;
    }

    /**
     * Checks if a given employee ID has already been used.
     * 
     * @param employeeId The employee ID to check
     * @return true if the ID is already in use, false otherwise
     */
    public static boolean isEmployeeIdUsed(String employeeId) {
        return usedEmployeeIds.contains(employeeId);
    }

    /**
     * Returns the number of unique employees registered so far.
     * 
     * @return The count of registered employees
     */
    public static int getRegisteredEmployeeCount() {
        return usedEmployeeIds.size();
    }

    /**
     * Loads existing employee IDs from database into the usedEmployeeIds set.
     * Call this on application startup to prevent ID conflicts.
     * Queries the payroll table and adds all existing IDs to the tracking set.
     */
    public static void loadExistingEmployeeIds() {
        try {
            Connection con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("Warning: Could not connect to database to load existing employee IDs");
                return;
            }

            String query = "SELECT employee_id FROM employee";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            int count = 0;
            while (rs.next()) {
                String employeeId = rs.getString("employee_id");
                usedEmployeeIds.add(employeeId);
                count++;
            }

            System.out.println("Successfully loaded " + count + " existing employee IDs from database");

            rs.close();
            pst.close();
            con.close();

        } catch (SQLException e) {
            System.out.println("Error loading existing employee IDs from database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
