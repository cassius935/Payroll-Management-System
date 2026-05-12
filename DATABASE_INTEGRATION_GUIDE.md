## QUICK DATABASE INTEGRATION GUIDE

This guide shows exactly where and how to add your database queries to connect the GUI to your database.

---

## 1. LoginFrame.java - User Authentication

**Location**: Line ~75 in the `authenticateUser()` method

**Current Code**:
```java
private boolean authenticateUser(String username, String password, String role) {
    // Demo authentication - replace with database query
    if (role.equals("Administrator")) {
        return username.equals("admin") && password.equals("admin123");
    } else {
        return username.equals("worker") && password.equals("worker123");
    }
}
```

**Replace with**:
```java
private boolean authenticateUser(String username, String password, String role) {
    try {
        Connection con = DBConnection.getConnection();
        if (con == null) return false;
        
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, password);
        pst.setString(3, role);
        
        ResultSet rs = pst.executeQuery();
        boolean result = rs.next();
        
        rs.close();
        pst.close();
        con.close();
        
        return result;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        return false;
    }
}
```

---

## 2. AdminPanel.java - Employee Registration

**Location**: Line ~148 in the "Register Employee" button's action listener

**Add After**:
```java
// Register employee
String registrationInfo = EmployeeManager.registerEmployee(
    firstName, lastName, email, department, position, hourlyRate
);
```

**Add This**:
```java
// ADDITION: Save to database
try {
    Connection con = DBConnection.getConnection();
    if (con != null) {
        String query = "INSERT INTO employees (employee_id, first_name, last_name, email, " +
                      "department, position, hourly_rate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, employeeId);
        pst.setString(2, firstName);
        pst.setString(3, lastName);
        pst.setString(4, email);
        pst.setString(5, department);
        pst.setString(6, position);
        pst.setDouble(7, hourlyRate);
        
        pst.executeUpdate();
        pst.close();
        con.close();
    }
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
}
```

---

## 3. AdminPanel.java - View Employees List

**Location**: Line ~189 in `createViewEmployeesPanel()` method

**Current Code**:
```java
JTextArea textArea = new JTextArea(10, 40);
textArea.setEditable(false);
textArea.setText("Employee list will be displayed here...");
```

**Replace with**:
```java
JTextArea textArea = new JTextArea(10, 40);
textArea.setEditable(false);

// Load employee data from database
try {
    Connection con = DBConnection.getConnection();
    if (con != null) {
        String query = "SELECT * FROM employees ORDER BY employee_id";
        PreparedStatement pst = con.prepareStatement(query);
        ResultSet rs = pst.executeQuery();
        
        StringBuilder employeeList = new StringBuilder();
        employeeList.append(String.format("%-15s | %-20s | %-20s | %-15s | %-20s\n", 
                           "Employee ID", "Name", "Department", "Position", "Email"));
        employeeList.append("----------------------------------------------------------------------\n");
        
        while (rs.next()) {
            String id = rs.getString("employee_id");
            String name = rs.getString("first_name") + " " + rs.getString("last_name");
            String dept = rs.getString("department");
            String pos = rs.getString("position");
            String email = rs.getString("email");
            
            employeeList.append(String.format("%-15s | %-20s | %-20s | %-15s | %-20s\n", 
                               id, name, dept, pos, email));
        }
        
        textArea.setText(employeeList.toString());
        rs.close();
        pst.close();
        con.close();
    }
} catch (SQLException e) {
    textArea.setText("Error loading employees: " + e.getMessage());
}
```

---

## 4. AdminPanel.java - Load Hourly Rate for Payroll Calculation

**Location**: Line ~237 in `createPayrollManagementPanel()` method

Add this method to get an employee's hourly rate:

```java
private double getEmployeeHourlyRate(String employeeId) {
    double hourlyRate = 0;
    try {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            String query = "SELECT hourly_rate FROM employees WHERE employee_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, employeeId);
            
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                hourlyRate = rs.getDouble("hourly_rate");
            }
            
            rs.close();
            pst.close();
            con.close();
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
    }
    return hourlyRate;
}
```

---

## 5. WorkerPanel.java - Load Worker's Hourly Rate

**Location**: Line ~99 in `createMyWagesPanel()` method

**Current Code**:
```java
// TODO: Replace with database query to get actual hourly rate
hourlyRateField.setText("$25.00");
```

**Replace with**:
```java
// Load worker's hourly rate from database
try {
    Connection con = DBConnection.getConnection();
    if (con != null) {
        String query = "SELECT hourly_rate FROM employees WHERE employee_id IN " +
                      "(SELECT employee_id FROM users WHERE username = ?)";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, loggedInUsername);
        
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            double rate = rs.getDouble("hourly_rate");
            hourlyRateField.setText("$" + String.format("%.2f", rate));
        }
        
        rs.close();
        pst.close();
        con.close();
    }
} catch (SQLException e) {
    hourlyRateField.setText("Error loading rate");
}
```

---

## 6. WorkerPanel.java - View Payment History

**Location**: Line ~149 in `createPaymentRecordsPanel()` method

**Current Code**:
```java
JTextArea paymentHistoryArea = new JTextArea(20, 60);
paymentHistoryArea.setEditable(false);
paymentHistoryArea.setText("Payment History will be displayed here...");
```

**Replace with**:
```java
JTextArea paymentHistoryArea = new JTextArea(20, 60);
paymentHistoryArea.setEditable(false);

// Load payment history from database
try {
    Connection con = DBConnection.getConnection();
    if (con != null) {
        String query = "SELECT p.* FROM payments p " +
                      "JOIN employees e ON p.employee_id = e.employee_id " +
                      "WHERE e.employee_id = " +
                      "(SELECT employee_id FROM students WHERE username = ?) " +
                      "ORDER BY p.payment_date DESC";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, loggedInUsername);
        
        ResultSet rs = pst.executeQuery();
        StringBuilder history = new StringBuilder();
        history.append(String.format("%-12s | %-12s | %-12s | %-15s | %-12s\n", 
                       "Date", "Gross Pay", "Deductions", "Hours Worked", "Net Pay"));
        history.append("--------------------------------------------------------------\n");
        
        while (rs.next()) {
            String date = rs.getDate("payment_date").toString();
            double gross = rs.getDouble("gross_pay");
            double deductions = rs.getDouble("total_deductions");
            double hours = rs.getDouble("hours_worked");
            double net = rs.getDouble("net_pay");
            
            history.append(String.format("%-12s | $%-11.2f | $%-14.2f | %-15.2f | $%-11.2f\n",
                           date, gross, deductions, hours, net));
        }
        
        paymentHistoryArea.setText(history.toString());
        rs.close();
        pst.close();
        con.close();
    }
} catch (SQLException e) {
    paymentHistoryArea.setText("Error loading payment history: " + e.getMessage());
}
```

---

## 7. WorkerPanel.java - Load Worker Profile Information

**Location**: Line ~198 in `createProfilePanel()` method

**Replace**:
```java
// TODO: Query database to populate these fields with actual employee data
```

**With**:
```java
// Load employee profile from database
try {
    Connection con = DBConnection.getConnection();
    if (con != null) {
        String query = "SELECT e.* FROM employees e " +
                      "JOIN users u ON e.employee_id = u.id " +
                      "WHERE u.username = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, loggedInUsername);
        
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            employeeIdField.setText(rs.getString("employee_id"));
            fullNameField.setText(rs.getString("first_name") + " " + rs.getString("last_name"));
            departmentField.setText(rs.getString("department"));
            positionField.setText(rs.getString("position"));
            emailField.setText(rs.getString("email"));
        }
        
        rs.close();
        pst.close();
        con.close();
    }
} catch (SQLException e) {
    JOptionPane.showMessageDialog(null, "Error loading profile: " + e.getMessage());
}
```

---

## 8. EmployeeManager.java - Load Existing Employee IDs on Startup

**Location**: Line ~115 in `loadExistingEmployeeIds()` method

**Current Code**:
```java
public static void loadExistingEmployeeIds() {
    // TODO: Execute query like: SELECT employee_id FROM employees
    // Add each result to usedEmployeeIds
    System.out.println("Loading existing employee IDs from database...");
}
```

**Replace with**:
```java
public static void loadExistingEmployeeIds() {
    try {
        Connection con = DBConnection.getConnection();
        if (con != null) {
            String query = "SELECT employee_id FROM employees";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                usedEmployeeIds.add(rs.getString("employee_id"));
            }
            
            System.out.println("Loaded " + usedEmployeeIds.size() + " existing employee IDs");
            
            rs.close();
            pst.close();
            con.close();
        }
    } catch (SQLException e) {
        System.out.println("Error loading employee IDs: " + e.getMessage());
    }
}
```

**Call this in PayrollApp.java main method**:
```java
@Override
public void run() {
    // Load existing employee IDs to prevent duplicates
    EmployeeManager.loadExistingEmployeeIds();
    
    // Create and display the login frame
    LoginFrame loginFrame = new LoginFrame();
    loginFrame.setVisible(true);
}
```

---

## Import Statements Needed

Add these imports to files where you add database code:

```java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
```

---

## Testing Your Integration

After implementing each database query, test it:

1. **Login**: Verify authentication works with database credentials
2. **Registration**: Register a new employee and check database table
3. **View Employees**: Verify employee list displays correctly
4. **Payroll**: Calculate pay and verify database retrieval
5. **Worker View**: Login as worker and verify data loads

---

## Common Errors & Solutions

| Error | Solution |
|-------|----------|
| "Connection Refused" | Ensure MySQL is running and DBConnection credentials are correct |
| "Table not found" | Verify all required tables are created in the database |
| "NULL value" | Check that employee exists in database before querying |
| "PreparedStatement null" | Ensure DBConnection.getConnection() is not returning null |

---

**Quick Reference**: Search for `TODO:` in all Java files to find all integration points!
