# Admin Account Setup Instructions

## Quick Setup

Follow these steps to set up your admin accounts and database:

### Step 1: Verify Database is Running
Make sure MySQL is running on your machine.

### Step 2: Run Database Setup Script

#### Option A: Using MySQL Command Line
```bash
mysql -u root < database_setup.sql
```

#### Option B: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Open `database_setup.sql` file
4. Click "Execute" button or press Ctrl+Enter
5. Verify that the tables are created and admin accounts are inserted

#### Option C: Using Command Prompt
1. Open Command Prompt
2. Navigate to your project directory:
   ```bash
   cd c:\Users\user\OneDrive\Desktop\Payroll Management System
   ```
3. Connect to MySQL:
   ```bash
   mysql -u root
   ```
4. Use the payrollmanagement database:
   ```sql
   USE payrollmanagement;
   ```
5. Copy and paste the contents of `database_setup.sql` into the MySQL prompt

### Step 3: Verify Admin Accounts Were Created

Run this query in MySQL:
```sql
SELECT * FROM admin;
```

You should see:
| id | admin_login | password      |
|----|-------------|---------------|
| 1  | admin1      | 12345678910   |
| 2  | admin2      | 10987654321   |

Also check worker accounts:
```sql
SELECT * FROM useracc;
```

You should see:
| id | user_name | password  |
|----|-----------|-----------|
| 1  | worker1   | worker123 |

### Step 4: Run the Application

```bash
javac main.java src/*.java
java main
```

### Step 5: Login with Admin Accounts

**Admin 1 Login:**
- Username: `admin1`
- Password: `12345678910`
- Role: `Administrator`

**Admin 2 Login:**
- Username: `admin2`
- Password: `10987654321`
- Role: `Administrator`

**Sample Worker Login:**
- Username: `worker1`
- Password: `worker123`
- Role: `Worker`

---

## What Happens on Login

1. **PayrollApp** starts and calls `EmployeeManager.loadExistingEmployeeIds()`
   - This loads all existing employee IDs from the database
   - Prevents duplicate ID generation

2. **LoginFrame** displays the login screen
   - Authenticates against the `users` table in your database
   - Verifies username, password, and role match

3. **AdminPanel** opens after successful login
   - Can register new employees
   - Each employee gets a unique ID (EMP + 6 digits)
   - Employee data is saved to the `employees` table

---

## Testing the Full Flow

### Test 1: Admin Registration
1. Login as `admin1` with password `12345678910`
2. Go to "Register Employee" tab
3. Click "Generate ID" button
4. Fill in employee details:
   - First Name: John
   - Last Name: Doe
   - Email: job.doe@company.com
   - Department: Sales
   - Position: Sales Associate
   - Hourly Rate: 25.00
5. Click "Register Employee"
6. You should see success message with generated ID

### Test 2: View Employees
1. Click "View Employees" tab
2. You should see John Doe listed with the generated ID

### Test 3: Calculate Net Pay
1. Click "Manage Payroll" tab
2. Enter Hourly Rate: 25.00
3. Enter Hours Worked: 40
4. Click "Calculate Net Pay"
5. See the salary breakdown

---

## Database Tables Created

### admin table (for administrator login)
- Stores admin login credentials
- Columns: id, admin_login, password, created_at
- Admin accounts inserted by default

### useracc table (for worker login)
- Stores worker login credentials
- Columns: id, user_name, password, created_at
- Sample worker account inserted

### payroll table (for employee records)
- Stores employee information
- Columns: employee_id (PRIMARY KEY), first_name, last_name, email, department, position, hourly_rate, hire_date

### payments table (for payment history)
- Stores payment records
- Columns: payment_id, employee_id (FOREIGN KEY), payment_date, gross_pay, total_deductions, net_pay, hours_worked

---

## Troubleshooting

### Database Connection Error
**Problem**: "Database connection failed"
**Solution**: 
- Check that MySQL is running
- Verify DBConnection.java has correct credentials
- Default is: root user, no password, database: payrollmanagement

### Login Failed
**Problem**: "Invalid credentials. Please try again."
**Solutions**:
- Check username and password are correct (case-sensitive)
- Verify admin accounts were inserted into database
- Run the verification query above to confirm

### No Employees Shown
**Problem**: "No employees registered yet" in Employee Directory
**Solution**:
- This is normal - register your first employee
- Click "Register Employee" tab and add a new employee

### Duplicate Employee ID Error
**Problem**: Getting same ID twice
**Solution**:
- IDs are now loaded from database on startup
- Restart the application after registering employees

---

## What Changed in Your Code

### LoginFrame.java
- Now queries `users` table in database for authentication
- Replaced demo credentials with real database lookup

### PayrollApp.java
- Calls `EmployeeManager.loadExistingEmployeeIds()` on startup
- Ensures unique ID generation without conflicts

### EmployeeManager.java
- `loadExistingEmployeeIds()` now queries the database
- Loads all existing employee IDs on application start

### AdminPanel.java
- Employee registration now saves to `employees` table
- View Employees tab displays employees from database
- Uses SQL prepared statements to prevent SQL injection

---

## Database Credentials (in DBConnection.java)

- **Host**: localhost
- **Port**: 3306
- **Database**: payrollmanagement
- **User**: root
- **Password**: (empty)

If your database credentials are different, edit `src/DBConnection.java` and update the connection string.

---

## Next Steps

After setup:
1. ✓ Admin can login and generate employee IDs
2. ✓ Employees are saved to database
3. Next: Connect worker login and payment records
4. Next: Setup password encryption for security
5. Next: Add more validation and error handling

---

**Setup Date**: April 2026  
**Status**: Ready for Testing
