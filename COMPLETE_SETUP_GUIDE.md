# PAYROLL MANAGEMENT SYSTEM v2.0.0 - COMPLETE SETUP GUIDE

## System Overview
This is a modern, feature-rich Payroll Management System built with Java Swing, featuring:
- Dual-role login (Admin and Worker)
- Real-time notification system with message counters
- Salary calculation and management
- Employee wallet and balance tracking
- Bill payment system (Water & Electric)
- E-wallet integration (GCash, PayMaya)
- Attendance tracking
- Comprehensive reporting

---

## QUICK START

### Step 1: Database Setup
1. **Install MySQL** if not already installed
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Windows users: Use MySQL Installer
   - Mac/Linux users: Use brew or apt

2. **Create Database**
   ```bash
   # Open MySQL command line
   mysql -u root -p
   
   # Run these commands:
   CREATE DATABASE payroll_db;
   USE payroll_db;
   source database_setup.sql;
   ```

3. **Default Admin Account**
   - Username: `admin`
   - Password: `Admin@2024!`
   - Login Code: `ADM-2024-PAYROLL-001`

4. **Default Test Worker Account**
   - Username: `worker1`
   - Password: `Worker@123`
   - Login Code: `WRK-2024-SYSTEM-001`

### Step 2: Run the Application

**Option A: Direct Compilation & Execution**
```bash
# Navigate to the src directory
cd src

# Compile all Java files
javac *.java

# Run the application
java main
```

**Option B: Using Batch File (Windows)**
```bash
# Simply double-click run.bat in the project root
# This will compile and execute automatically
```

**Option C: Using IDE (VS Code, IntelliJ, Eclipse)**
- Open the project folder
- Set up Java/Maven build tools if needed
- Run `main.java` as the main class

---

## NEW FEATURES IN v2.0.0

### 1. **Notification System with Counters**
- **Message Counter**: Shows unread message count in the Messages tab
- **Notification Badge**: Header displays total notifications and messages
  - For Workers: Badge shows combined count of notifications + messages
  - For Admins: Badge shows count of worker messages
- **Click Notification Bell**: Opens detailed notification panel
  - View all unread notifications
  - View all unread messages
  - Mark messages as read
  - Refresh to update counts

### 2. **Enhanced Main Entry Point**
- **Splash Screen**: Professional loading screen on startup
- **Progressive Initialization**: Shows loading status
- **Error Handling**: Graceful fallback to demo mode if database unavailable
- **Configuration Validation**: Checks system requirements on startup

### 3. **Notification Manager**
New class `NotificationManager.java` provides:
- Automatic notification creation
- Unread notification counting
- Notification categories (Salary, Messages, Payments, System)
- Automatic notifications when:
  - Salary is released
  - New messages arrive
  - Payment reminders are due
  - System alerts occur

### 4. **Message Manager**
New class `MessageManager.java` provides:
- Send messages between admin and workers
- Message counting (unread only)
- Message history retrieval
- Broadcast messages to all workers
- Automatic notification on new message

### 5. **System Configuration**
New class `SystemConfig.java` provides:
- Centralized configuration management
- System diagnostics and validation
- Startup verification
- Environment checking
- Java version validation

### 6. **Enhanced UI Updates**
- Notification badge with dynamic counts
- Better tab labeling with message counts
- Notification panel for quick overview
- Improved header styling with notification interaction

---

## USER ROLES & FEATURES

### ADMIN Features:
- Dashboard with overview statistics
- Manage all employees
- Release and track salary payments
- Send messages and notifications to workers
- View worker attendance
- Manage bills
- E-wallet payment tracking
- Access message notification count in header

### WORKER Features:
- View dashboard with quick actions
- Check wallet balance and salary
- View transaction history
- Send messages to admin
- Pay bills (water, electric)
- Connect e-wallet accounts
- View profile information
- Receive and view notifications
- See unread message count

---

## SYSTEM REQUIREMENTS

- **Java**: 8 or higher
- **MySQL**: 5.7 or higher
- **RAM**: Minimum 2GB
- **Disk Space**: 200MB
- **Display**: 1024x768 minimum resolution recommended

---

## TROUBLESHOOTING

### Issue: "MySQL Driver not found"
**Solution**:
- Add `mysql-connector-java` JAR to classpath
- File should be in lib/ folder
- Ensure CLASSPATH includes: `lib/*`

### Issue: "Cannot connect to database"
**Solutions**:
1. Verify MySQL is running:
   ```bash
   # Windows
   services.msc > look for MySQL
   
   # Mac/Linux
   mysql.server status
   ```

2. Check connection parameters:
   - Host: localhost:3306
   - Database: payroll_db
   - User: root
   - Password: (leave empty or match your setup)

3. System will run in DEMO MODE if database unavailable
   - Use default credentials for testing
   - No data persistence in demo mode

### Issue: "Module not found" or "Cannot find symbol"
**Solution**:
- Ensure all Java files compile without errors:
  ```bash
  javac -d bin src/*.java
  java -cp bin src.main
  ```

### Issue: Application window doesn't appear
**Solution**:
1. Check system output for errors:
   ```bash
   java main 2>&1 | less
   ```
2. Verify display/graphics drivers
3. Try running with explicit Java executable:
   ```bash
   java -Djava.awt.headless=false main
   ```

---

## COMPILATION & EXECUTION

### Full Compilation
```bash
cd src
javac -encoding UTF-8 *.java
```

### Run with Classpath (includes database driver)
```bash
# Ensure mysql-connector-java JAR is in lib/
java -cp .:lib/* main
```

### Build to bin directory
```bash
javac -d ../bin *.java
cd ../bin
java -cp .:../lib/* src.main
```

---

## DATABASE TABLES

The system uses these main tables:
- `users`: User login information (Admin/Worker)
- `employee`: Employee profile data
- `worker_wallet`: Wallet balance tracking
- `messages`: Inter-user communication
- `notifications`: System alerts and notifications
- `salary_records`: Salary computation history
- `attendance`: Daily attendance tracking
- `bills`: Bill management
- `transactions`: Financial transaction history
- `e_wallet_accounts`: E-wallet connection data
- `admin_logs`: Admin activity tracking

---

## DEFAULT TEST CREDENTIALS

### Admin Account
- **Username**: admin
- **Password**: Admin@2024!

### Worker Account 1
- **Username**: worker1
- **Password**: Worker@123

### Worker Account 2
- **Username**: worker2
- **Password**: Worker@456

---

## IMPORTANT NOTES

1. **Always backup your database** before major updates
2. **Demo mode** activates automatically if database is unavailable
3. **Notifications** require database connection for persistence
4. **Password hashing**: SHA-256 is used for security
5. **Default timezone**: UTC (configurable in database URL)

---

## SUPPORT & HELP

For issues or questions:
1. Check the System Config output in console
2. Review DatabaseConnection error logs
3. Enable debug mode by checking log files
4. Verify database connection parameters
5. Test with demo credentials first

---

## VERSION HISTORY

### v2.0.0 (Current)
- Added notification system with message counters
- New SplashScreen with progress indication
- NotificationManager for real-time alerts
- MessageManager for communication
- SystemConfig for centralized settings
- Enhanced error handling and logging
- Improved UI with notification badges
- Better startup validation

### v1.0.0
- Initial release
- Basic login and user management
- Salary calculation
- Employee management
- Attendance tracking

---

## File Structure
```
Payroll Management System/
├── src/
│   ├── main.java                 (Entry point - FULLY EXECUTABLE)
│   ├── LoginFrame.java           (Login interface)
│   ├── AdminPanelModern.java     (Admin dashboard)
│   ├── WorkerPanelModern.java    (Worker dashboard)
│   ├── DBConnection.java         (Database handler)
│   ├── NotificationManager.java  (NEW - Notifications)
│   ├── MessageManager.java       (NEW - Messaging)
│   ├── SystemConfig.java         (NEW - Configuration)
│   ├── SplashScreen.java         (NEW - Startup screen)
│   ├── EmployeeManager.java      (Employee utilities)
│   ├── SalaryCalculator.java     (Salary computation)
│   └── UITheme.java              (UI styling)
├── lib/
│   └── mysql-connector-java.jar  (Database driver)
├── database_setup.sql            (Database schema)
├── run.bat                        (Windows batch runner)
└── README.md                      (This file)
```

---

## ENHANCEMENT SUMMARY

This v2.0.0 update includes:
✅ Complete notification system with counters
✅ Message management and unread counts
✅ Professional splash screen
✅ System configuration utilities
✅ Enhanced error handling
✅ Improved UI with notification badges
✅ Comprehensive logging
✅ Auto-initialization on startup
✅ Demo mode fallback
✅ Better code documentation

---

**Developed**: May 2024
**License**: Internal Use Only
**Support**: Check error logs in console output

