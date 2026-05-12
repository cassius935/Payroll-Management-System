# Payroll Management System - Documentation

## Overview
This is a modular Java-based Payroll Management System with a GUI that supports two user roles:
- **Administrator**: Can register employees, calculate salaries, and manage payroll
- **Worker**: Can view their own wage status and payment records

## Project Structure

```
Payroll Management System/
├── main.java                    # Application entry point
├── src/
│   ├── PayrollApp.java          # Main application launcher
│   ├── LoginFrame.java          # Login interface with role selection
│   ├── AdminPanel.java          # Administrator dashboard
│   ├── WorkerPanel.java         # Worker view for checking wages
│   ├── DBConnection.java        # Database connection handler (pre-existing)
│   ├── EmployeeManager.java     # Employee registration & unique ID generation
│   └── SalaryCalculator.java    # Salary calculation and deduction logic
└── lib/
    └── yql1.txt
```

## Class Descriptions

### 1. **main.java**
- **Purpose**: Entry point of the application
- **Main Method**: `main(String[] args)`
- **Usage**: Run this class to start the application

### 2. **PayrollApp.java**
- **Purpose**: Application launcher that initializes the Swing application
- **Main Method**: Creates and displays the LoginFrame
- **No database integration needed**

### 3. **LoginFrame.java**
- **Purpose**: Login interface where users authenticate and select their role
- **Features**:
  - Username/Password fields
  - Role selection (Administrator/Worker)
  - Routes users to appropriate panel
- **Database Connection Points** (marked with TODO):
  - Line ~75: `authenticateUser()` method - Query user credentials from database
  - Expected Query: `SELECT * FROM users WHERE username = ? AND password = ? AND role = ?`

### 4. **AdminPanel.java**
- **Purpose**: Administrator dashboard with multiple functions
- **Tabs/Features**:
  1. **Register Employee**: Register new employees with auto-generated unique IDs
  2. **View Employees**: Display employee directory
  3. **Manage Payroll**: Calculate net pay with breakdown
  4. **Settings**: Display system configuration

- **Database Connection Points** (marked with TODO):
  - **Employee Registration** (Line ~148): Insert new employee into database
    ```
    INSERT INTO employees (employee_id, first_name, last_name, email, 
                          department, position, hourly_rate)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    ```
  - **View Employees** (Line ~189): Retrieve all employees
    ```
    SELECT * FROM employees
    ```

### 5. **WorkerPanel.java**
- **Purpose**: Restricted view for workers to check their wages and payment records
- **Tabs/Features**:
  1. **My Wages**: Calculate and view wage breakdown
  2. **Payment Records**: View payment history
  3. **Deductions**: View tax and deduction details
  4. **Profile**: View personal and employment information

- **Database Connection Points** (marked with TODO):
  - **My Wages** - Get hourly rate (Line ~99):
    ```
    SELECT hourly_rate FROM employees WHERE employee_id = ?
    ```
  - **Payment Records** (Line ~149): Get payment history
    ```
    SELECT * FROM payments WHERE employee_id = ? ORDER BY date DESC
    ```
  - **Profile** (Line ~198): Get employee profile information
    ```
    SELECT * FROM employees WHERE employee_id = ?
    ```

### 6. **EmployeeManager.java**
- **Purpose**: Handles employee registration and unique ID generation
- **Key Methods**:
  - `generateUniqueEmployeeId()`: Generates unique ID (Format: EMPxxxxxx)
  - `registerEmployee()`: Registers new employee
  - `validateEmployeeData()`: Validates employee information
  - `isEmployeeIdUsed()`: Checks if ID already exists
  - `getRegisteredEmployeeCount()`: Returns total registered employees
  - `loadExistingEmployeeIds()`: Loads existing IDs from database on startup

- **Database Connection Points** (marked with TODO):
  - **Load Existing IDs** (Line ~115): On app startup, load all existing employee IDs
    ```
    SELECT employee_id FROM employees
    ```
  - **Save New Employee** (Line ~78): After calling `registerEmployee()`, save to database
    ```
    INSERT INTO employees (employee_id, first_name, last_name, email, 
                          department, position, hourly_rate)
    VALUES (?, ?, ?, ?, ?, ?, ?)
    ```

### 7. **SalaryCalculator.java**
- **Purpose**: Core salary calculation logic with deduction handling
- **Key Methods**:
  - `calculateGrossSalary(double hourlyRate, double hoursWorked)`: Calculates gross pay
    - Accounts for overtime (1.5x for hours over 40)
  - `calculateIncomeTax(double grossSalary)`: 15% of gross salary
  - `calculateSocialSecurity(double grossSalary)`: 6.2% of gross salary
  - `calculateMedicare(double grossSalary)`: 1.45% of gross salary
  - `calculateTotalDeductions(double grossSalary)`: Sum of all deductions
  - `calculateNetPay(double hourlyRate, double hoursWorked)`: Final take-home pay
  - `getSalaryBreakdown()`: Returns formatted salary details

- **Deduction Breakdown**:
  - Income Tax: 15%
  - Social Security: 6.2%
  - Medicare: 1.45%
  - Health Insurance: $100 (fixed)
  - Pension Contribution: $50 (fixed)

- **No database integration needed** - this is pure calculation logic

### 8. **DBConnection.java** (Pre-existing)
- **Purpose**: Centralized database connection management
- **Configuration**:
  - URL: `jdbc:mysql://localhost:3306/payrollmanagement?useSSL=false&serverTimezone=UTC`
  - Driver: MySQL
  - User: `root`
  - Password: (empty)

- **Usage Example**:
  ```java
  Connection con = DBConnection.getConnection();
  if (con != null) {
      // Execute queries
  }
  ```

## How to Integrate Your Database

### Step 1: Database Schema Setup
Create the following tables in your `payrollmanagement` database:

```sql
-- Users table for login authentication
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('Administrator', 'Worker') NOT NULL
);

-- Employees table
CREATE TABLE employees (
    employee_id VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    position VARCHAR(50),
    hourly_rate DECIMAL(10, 2) NOT NULL,
    hire_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payments table for payment history
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) NOT NULL,
    payment_date DATE NOT NULL,
    gross_pay DECIMAL(10, 2),
    total_deductions DECIMAL(10, 2),
    net_pay DECIMAL(10, 2),
    hours_worked DECIMAL(5, 2),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);
```

### Step 2: Implement Database Queries
Search for all `TODO: ` comments in the code (they note database integration points):

1. **LoginFrame.java** - Implement authentication
2. **AdminPanel.java** - Implement employee registration and retrieval
3. **WorkerPanel.java** - Implement wage and payment record retrieval
4. **EmployeeManager.java** - Load existing employee IDs on startup

### Step 3: Example Implementation

#### Example 1: LoginFrame Authentication
```java
private boolean authenticateUser(String username, String password, String role) {
    try {
        Connection con = DBConnection.getConnection();
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, password); // Consider using hashing in production
        pst.setString(3, role);
        
        ResultSet rs = pst.executeQuery();
        boolean result = rs.next();
        
        con.close();
        return result;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

#### Example 2: Employee Registration
```java
// In AdminPanel.java, after calling EmployeeManager.registerEmployee():
try {
    Connection con = DBConnection.getConnection();
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
    con.close();
} catch (SQLException e) {
    e.printStackTrace();
}
```

#### Example 3: Load Employee IDs on Startup
```java
// In EmployeeManager.java loadExistingEmployeeIds():
try {
    Connection con = DBConnection.getConnection();
    String query = "SELECT employee_id FROM employees";
    PreparedStatement pst = con.prepareStatement(query);
    ResultSet rs = pst.executeQuery();
    
    while (rs.next()) {
        usedEmployeeIds.add(rs.getString("employee_id"));
    }
    
    con.close();
} catch (SQLException e) {
    e.printStackTrace();
}
```

## Demo Credentials

For testing the application without database integration:

**Administrator Login:**
- Username: `admin`
- Password: `admin123`
- Role: `Administrator`

**Worker Login:**
- Username: `worker`
- Password: `worker123`
- Role: `Worker`

## Running the Application

### Option 1: Command Line
```bash
javac main.java src/*.java
java main
```

### Option 2: IDE (IntelliJ, Eclipse, VS Code)
1. Open the project folder
2. Right-click on `main.java`
3. Select "Run" or "Run main.java"

## Modular Design Features

### Separation of Concerns
- **PayrollApp.java**: Application initialization
- **LoginFrame.java**: Authentication and routing
- **AdminPanel.java**: Admin-specific functionality
- **WorkerPanel.java**: Worker-specific functionality
- **EmployeeManager.java**: Employee data management
- **SalaryCalculator.java**: Salary calculations
- **DBConnection.java**: Database connectivity

### Easy Extension
All classes include clear comments indicating where to add database queries. To add new features:
1. Create new methods in relevant class
2. Add corresponding database query
3. Update GUI components as needed

## Future Enhancements

1. **Security**: Implement password hashing (BCrypt)
2. **Reports**: Add PDF/Excel salary slip generation
3. **Notifications**: Email notifications for payroll
4. **Attendance**: Track employee attendance
5. **Leave Management**: Request and approval workflow
6. **Performance Reviews**: Link performance to pay adjustments
7. **Authentication**: Two-factor authentication

## Support

For questions or issues:
1. Check the TODO comments in the code
2. Review the database schema requirements
3. Verify DBConnection credentials match your database setup
4. Ensure all required tables are created in the database

---

**Version**: 1.0  
**Last Updated**: April 2026
