# 🚀 Quick Start - Fixed System

## Immediate Next Steps

### 1️⃣ Setup MySQL Database (One-time)
```bash
# Make sure MySQL is running, then:
mysql -u root -p < database_setup.sql
```

### 2️⃣ Run the Application
```bash
# Double-click run.bat
# OR run in terminal:
run.bat
```

### 3️⃣ Login as Admin
- Username: `admin`
- Password: `Admin@2024!`

### 4️⃣ Register an Employee

**In Admin Panel:**
1. Click "Register Employee" tab
2. Fill in the form:
   - First Name: John
   - Last Name: Doe
   - Email: john@example.com
   - Phone: 09123456789
   - Department: IT
   - Position: Developer
   - Hourly Rate: 15.00
3. Click "Generate ID" → auto-generates Employee ID
4. Click "Register Employee"
5. **Note**: Save the temporary password shown!

### 5️⃣ View All Employees & Usernames

**In Admin Panel:**
1. Click "View Employees" tab
2. Click "Refresh List"
3. See all registered employees with their **usernames**

```
╔════════════════════════════════════════════════════════════════╗
║ Employee ID     │ Username        │ Name                 │ ... ║
╠════════════════════════════════════════════════════════════════╣
║ EMP123456       │ EMP123456       │ John Doe             │ ... ║
║ EMP789012       │ EMP789012       │ Jane Smith           │ ... ║
╚════════════════════════════════════════════════════════════════╝
```

### 6️⃣ Change Employee Password

**In Admin Panel:**
1. Click "View Employees" tab
2. Click "Change Password"
3. Enter Employee ID: `EMP123456`
4. Enter new password (min 6 characters)
5. Confirm password
6. Click OK

✅ Password changed!

---

## 🔑 Important Notes

### Usernames
- **Employee's username** = Employee ID (assigned during registration)
- Username cannot be changed
- Example: `EMP123456`

### Passwords
- Stored securely as SHA-256 hashes
- Admin can reset any employee password
- Employees can change own password after login
- Temporary password given at registration

### Demo Credentials (if no database)
- Admin: `admin` / `Admin@2024!`
- Worker: `worker1` / `Worker@123`

---

## 📊 What Now Works

✅ **Admin can:**
- See employee list with usernames
- Change employee passwords
- Register new employees
- View all employee details

✅ **Database:**
- Connected properly with JDBC driver
- All employee data persisted
- Secure password storage

✅ **Application:**
- Starts without errors
- Handles database disconnection gracefully
- Works with or without database (demo mode)

---

## ❓ Common Tasks

### Task: Reset an Employee's Password
1. Go to View Employees → Change Password
2. Enter their Employee ID
3. Set new password
4. Done!

### Task: Create New Employee Account
1. Go to Register Employee
2. Fill form and click "Register Employee"
3. New employee gets username = their Employee ID
4. Employee can login with temporary password
5. Employee must change password on first login

### Task: Find Employee Username
1. Go to View Employees → Refresh List
2. Find employee name in list
3. Their username is shown in second column
4. Username = Employee ID

### Task: Login as Employee
1. Use Employee ID as username
2. Use password (set by admin or personal password)
3. Employee sees WorkerPanel interface

---

## 🛠️ If Something Goes Wrong

| Problem | Solution |
|---------|----------|
| App won't compile | Run: `javac -cp "lib\mysql-connector-j-9.6.0.jar;." main.java src\*.java` |
| App won't run | Run: `java -cp ".;lib\mysql-connector-j-9.6.0.jar" main` |
| Can't connect to database | Make sure MySQL is running and `payroll_db` exists |
| Employee list is empty | Click "Refresh List" button or register employees first |
| Password change fails | Check that Employee ID is correct |

---

## ✨ What Was Fixed

✅ Fixed `main.java` - no more startup crashes
✅ Fixed database connection - proper JDBC driver setup  
✅ Fixed Admin Panel - View Employees now shows **usernames**
✅ Added password change feature - admin can reset passwords
✅ Improved error handling - clean messages instead of errors

---

**Your system is ready to use! 🎉**
