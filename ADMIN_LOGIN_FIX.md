# 🚨 ADMIN LOGIN ISSUE - STEP BY STEP FIX

## The Problem
You can't log in as admin1/admin2 because:
1. ✅ **Database connection works** (JDBC driver is now in classpath)
2. ❌ **Admin accounts not inserted** (database_setup.sql not run yet)

---

## ✅ SOLUTION: Follow These Steps Exactly

### Step 1: Open MySQL Command Line
**Option A: Windows Command Prompt**
1. Press `Win + R`
2. Type `cmd`
3. Press Enter
4. Navigate to your project folder:
   ```cmd
   cd c:\Users\user\OneDrive\Desktop\Payroll Management System
   ```
5. Connect to MySQL:
   ```cmd
   mysql -u root
   ```

**Option B: Use SQLyog**
1. Open SQLyog
2. Connect to your MySQL server
3. Open a new query window

---

### Step 2: Run Database Setup Script

**In MySQL Command Line:**
```sql
USE payrollmanagement;
SOURCE database_setup.sql;
```

**Or in SQLyog:**
1. Open `database_setup.sql` file
2. Click "Execute" button or press `Ctrl + Enter`

---

### Step 3: Verify Admin Accounts Were Created

**In MySQL, run:**
```sql
SELECT * FROM admin;
```

**You should see:**
```
+----+-------------+---------------+---------------------+
| id | admin_login | password      | created_at          |
+----+-------------+---------------+---------------------+
|  1 | admin1      | 12345678910   | 2026-04-07 12:00:00 |
|  2 | admin2      | 10987654321   | 2026-04-07 12:00:00 |
+----+-------------+---------------+---------------------+
```

**Also check worker accounts:**
```sql
SELECT * FROM useracc;
```

**You should see:**
```
+----+-----------+-----------+---------------------+
| id | user_name | password  | created_at          |
+----+-----------+-----------+---------------------+
|  1 | worker1   | worker123 | 2026-04-07 12:00:00 |
+----+-----------+-----------+---------------------+
```

---

### Step 4: Test Database Connection (Optional)

Run our test program:
```cmd
cd c:\Users\user\OneDrive\Desktop\Payroll Management System
java -cp ".;lib/mysql-connector-j-9.6.0.jar" DBTest
```

**Expected output:**
```
Testing database connection and admin accounts...
Database Connected!
✅ Database connection successful!

--- Checking admin table ---
Admin: admin1 / 12345678910
Admin: admin2 / 10987654321
✅ Admin accounts found!

--- Checking useracc table ---
Worker: worker1 / worker123
✅ Worker accounts found!

--- Testing login queries ---
✅ Admin1 login query works!
✅ Worker1 login query works!

--- Summary ---
If everything shows ✅, then login should work!
```

---

### Step 5: Compile and Run the Application

**Compile with JDBC driver:**
```cmd
cd c:\Users\user\OneDrive\Desktop\Payroll Management System
javac -cp "lib/mysql-connector-j-9.6.0.jar" main.java src/*.java
```

**Run with JDBC driver:**
```cmd
java -cp ".;lib/mysql-connector-j-9.6.0.jar" main
```

---

### Step 6: Test Login

**Admin Login Test:**
- Username: `admin1`
- Password: `12345678910`
- Role: `Administrator`
- Expected: AdminPanel opens

**Worker Login Test:**
- Username: `worker1`
- Password: `worker123`
- Role: `Worker`
- Expected: WorkerPanel opens

---

## 🔧 Alternative: If MySQL Command Line Doesn't Work

### Option 1: Use SQLyog Interface
1. Open SQLyog
2. Connect to your database
3. Copy the contents of `database_setup.sql`
4. Paste into SQLyog query window
5. Execute the query

### Option 2: Manual Table Creation
If you prefer to create tables manually:

**Create admin table:**
```sql
CREATE TABLE admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    admin_login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Insert admin accounts:**
```sql
INSERT INTO admin (admin_login, password) VALUES
('admin1', '12345678910'),
('admin2', '10987654321');
```

**Create useracc table:**
```sql
CREATE TABLE useracc (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Insert worker account:**
```sql
INSERT INTO useracc (user_name, password) VALUES
('worker1', 'worker123');
```

---

## 🐛 Troubleshooting

### Problem: "mysql command not found"
**Solution:** Use SQLyog or MySQL Workbench instead of command line

### Problem: "Access denied for user 'root'@'localhost'"
**Solution:** Make sure MySQL is running and you have the correct password

### Problem: "Unknown database 'payrollmanagement'"
**Solution:** Create the database first:
```sql
CREATE DATABASE payrollmanagement;
USE payrollmanagement;
```

### Problem: Still can't login after setup
**Solution:** Run the DBTest again to verify accounts exist

---

## 📋 Quick Reference

### Database Setup Commands:
```sql
-- Connect to MySQL
mysql -u root

-- Use database
USE payrollmanagement;

-- Run setup script
SOURCE database_setup.sql;

-- Verify setup
SELECT * FROM admin;
SELECT * FROM useracc;
```

### Application Commands:
```cmd
-- Compile
javac -cp "lib/mysql-connector-j-9.6.0.jar" main.java src/*.java

-- Run
java -cp ".;lib/mysql-connector-j-9.6.0.jar" main

-- Test database
java -cp ".;lib/mysql-connector-j-9.6.0.jar" DBTest
```

### Login Credentials:
- **Admin1:** admin1 / 12345678910
- **Admin2:** admin2 / 10987654321
- **Worker:** worker1 / worker123

---

## ✅ Expected Result

After following these steps, you should be able to:

1. ✅ Connect to database
2. ✅ See admin accounts in MySQL
3. ✅ Login as admin1 or admin2
4. ✅ Access AdminPanel
5. ✅ Register employees
6. ✅ View employee list

---

**Status:** Ready to fix - just run the database setup script!  
**Issue:** Admin accounts not inserted into database yet  
**Solution:** Run `database_setup.sql` in MySQL
