package src;

import java.util.HashMap;

// to manage data and prevent data loss
public class EmployeeManager {
    // for storing data safely in memory
    private static HashMap<String, String> employeeRecords = new HashMap<>();

    // to load initial setup
    public static void loadData() {
        // dummy data to test the system
        employeeRecords.put("emp1", "System: Welcome to your new dashboard!\n\n");
    }

    // for admin to give money
    public static void giveSalary(String empId, String amount) {
        String current = employeeRecords.getOrDefault(empId, "");
        current += "💰 [SALARY RECEIVED]: $" + amount + "\n";
        employeeRecords.put(empId, current);
    }

    // for admin to send message directly
    public static void sendMessage(String empId, String message) {
        String current = employeeRecords.getOrDefault(empId, "");
        current += "✉️ [MESSAGE FROM ADMIN]: " + message + "\n";
        employeeRecords.put(empId, current);
    }

    // for employee to see their stuff
    public static String getRecords(String empId) {
        return employeeRecords.getOrDefault(empId, "Inbox is empty. No messages yet.");
    }
}