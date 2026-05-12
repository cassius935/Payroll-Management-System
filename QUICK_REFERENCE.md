# QUICK REFERENCE GUIDE - Payroll Management System v2.0.0

## QUICK START (30 seconds)

### Windows Users
```
1. Double-click: runApp.bat
   That's it! The system will compile and run automatically.
```

### Linux/Mac Users
```
bash run.sh
```

### Manual Compilation
```
cd src
javac -encoding UTF-8 -d ../bin *.java
cd ../bin
java -cp .:../lib/* src.main
```

---

## DEFAULT LOGIN CREDENTIALS

### Admin Account
```
Username: admin
Password: Admin@2024!
```

### Worker Accounts (for testing)
```
Worker 1: worker1 / Worker@123
Worker 2: worker2 / Worker@456
```

---

## MAIN CLASSES & THEIR PURPOSE

| Class | Purpose | Key Methods |
|-------|---------|-------------|
| `main` | Entry point - START HERE | `main(String[] args)` |
| `LoginFrame` | Login UI | User authentication |
| `AdminPanelModern` | Admin dashboard | `createHeaderPanel()` |
| `WorkerPanelModern` | Worker dashboard | `updateNotificationCounts()` |
| `DBConnection` | Database handler | `getConnection()` |
| `NotificationManager` | Notifications ⭐ NEW | `getUnreadNotificationCount()` |
| `MessageManager` | Messages ⭐ NEW | `getUnreadCount()` |
| `SplashScreen` | Startup screen ⭐ NEW | `updateStatus()` |
| `SystemConfig` | Configuration ⭐ NEW | `initialize()` |
| `SalaryCalculator` | Payroll logic | `calculateGrossSalary()` |
| `EmployeeManager` | Employee data | `generateUniqueEmployeeId()` |
| `UITheme` | UI styling | `createModernButton()` |

⭐ = New in v2.0.0

---

## NOTIFICATION SYSTEM QUICK REFERENCE

### Getting Unread Counts
```java
// Get notification count
int notifCount = NotificationManager.getUnreadNotificationCount(userId);

// Get message count
int msgCount = MessageManager.getUnreadCount(userId);

// Get combined count
int totalCount = NotificationManager.getTotalUnreadCount(userId);
```

### Creating Notifications
```java
// Automatic salary notification
NotificationManager.notifySalaryReleased(userId, employeeId, amount);

// Message notification
NotificationManager.notifyNewMessage(userId, senderName);

// Payment reminder
NotificationManager.notifyPaymentReminder(userId, billType, amount);

// System notification
NotificationManager.notifySystem(userId, title, message);
```

### Sending Messages
```java
// Send message from admin to worker
MessageManager.sendMessage(adminId, workerId, subject, messageText, "GENERAL");

// Broadcast to all workers
MessageManager.sendNotificationToWorkers(adminId, subject, message);

// Get unread messages
List<MessageManager.MessageInfo> messages = 
    MessageManager.getUnreadMessages(userId, 10);
```

---

## DATABASE SETUP (One-time)

### 1. Create Database
```sql
CREATE DATABASE payroll_db;
USE payroll_db;
source database_setup.sql;
```

### 2. Verify Tables
```sql
SHOW TABLES;
```

### 3. Check Sample Data
```sql
SELECT * FROM users;
SELECT * FROM notifications;
SELECT * FROM messages;
```

---

## FILE STRUCTURE

```
Payroll Management System/
├── src/                          Source files
│   ├── main.java                 ✓ ENTRY POINT - Run this
│   ├── LoginFrame.java           Login screen
│   ├── AdminPanelModern.java     Admin dashboard
│   ├── WorkerPanelModern.java    Worker dashboard
│   ├── NotificationManager.java  ⭐ Notification system
│   ├── MessageManager.java       ⭐ Message system
│   ├── SplashScreen.java         ⭐ Startup screen
│   ├── SystemConfig.java         ⭐ Configuration
│   ├── DBConnection.java         Database connection
│   ├── SalaryCalculator.java     Salary computation
│   ├── EmployeeManager.java      Employee utilities
│   └── UITheme.java              UI styling
├── bin/                          Compiled classes
├── lib/                          Third-party JARs
│   └── mysql-connector-java.jar  (Required for database)
├── database_setup.sql            Database schema
├── runApp.bat                    ✓ Windows launcher
├── run.sh                        ✓ Linux/Mac launcher
├── COMPLETE_SETUP_GUIDE.md       Detailed setup
└── PROJECT_COMPLETION_SUMMARY_v2.md Complete info

⭐ = New in v2.0.0
✓ = Can be used to start the application
```

---

## COMMON COMMANDS

### View Unread Count (for Developers)
```java
// In any class
int count = NotificationManager.getUnreadNotificationCount(userId);
System.out.println("Unread notifications: " + count);
```

### Send Test Notification
```java
NotificationManager.notifySystem(userId, "Test", "This is a test notification");
```

### Check Database Connection
```java
if (DBConnection.isDatabaseAvailable()) {
    System.out.println("Database is online");
} else {
    System.out.println("Running in DEMO MODE");
}
```

### Print System Information
```java
SystemConfig.printDiagnostics();
System.out.println(SystemConfig.getStartupReport());
```

---

## TROUBLESHOOTING QUICK FIXES

| Problem | Solution |
|---------|----------|
| "Java not found" | Install Java 8+: https://java.com/download |
| "Cannot connect to database" | Start MySQL server, or system will use demo mode |
| "MySQL driver not found" | Download JAR, place in lib/ folder |
| "Compilation errors" | Ensure all files are in src/ directory |
| "Application won't start" | Check console for error messages |
| "No notifications show" | Ensure database is running and connected |
| "Message count is 0" | Send a message from admin to worker |

---

## NOTIFICATION BADGE LOCATIONS

### Worker Dashboard
- **Header**: Bell icon showing total count `🔔 (5)`
- **Messages Tab**: Count in tab title `📬 Messages (3)`
- **Notification Panel**: Click bell to view all

### Admin Dashboard
- **Header**: Bell icon showing message count `🔔 (2)`
- **Messages Tab**: Count in tab title `📬 Messages (2)`
- **Notification Panel**: Click bell to view worker messages

---

## KEY FEATURES AT A GLANCE

✅ **Dual Login**: Admin and Worker roles
✅ **Notifications**: Real-time alerts with counters
✅ **Messages**: Admin-to-worker communication
✅ **Salary**: Automatic calculation and tracking
✅ **Wallet**: Employee balance management
✅ **Bills**: Water and electric bill payment
✅ **E-Wallet**: GCash, PayMaya, PayPal integration
✅ **Attendance**: Track employee presence
✅ **Modern UI**: Professional design with themes
✅ **Demo Mode**: Works without database
✅ **Splash Screen**: Professional startup
✅ **Error Handling**: Graceful error management

---

## DEVELOPMENT TIPS

1. **Always initialize SystemConfig first**
   ```java
   SystemConfig.initialize();
   ```

2. **Check database before operations**
   ```java
   if (!DBConnection.isDatabaseAvailable()) {
       // Use demo mode or warn user
   }
   ```

3. **Use NotificationManager for user alerts**
   ```java
   NotificationManager.notifySystem(userId, "Title", "Message");
   ```

4. **Always close database connections**
   ```java
   if (con != null) con.close();
   ```

5. **Log errors with context**
   ```java
   System.err.println("[ERROR] Detailed context: " + e.getMessage());
   ```

---

## SYSTEM REQUIREMENTS

- **Java**: 8 or higher (tested on Java 8, 11, 17)
- **MySQL**: 5.7 or higher
- **RAM**: 2GB minimum
- **Display**: 1024x768 minimum
- **OS**: Windows, Linux, macOS

---

## USEFUL RESOURCES

- **Java Downloads**: https://java.com
- **MySQL**: https://www.mysql.com
- **MySQL JDBC Driver**: https://dev.mysql.com/downloads/connector/j/
- **MySQL Docs**: https://dev.mysql.com/doc/
- **Java Swing Docs**: https://docs.oracle.com/javase/8/docs/

---

## VERSION INFO

- **Current Version**: 2.0.0
- **Release Date**: May 12, 2024
- **Status**: Production Ready
- **New Features**: Notification system, Message counters, Splash screen, System diagnostics

---

**Last Updated**: May 12, 2024
**For Questions**: Check COMPLETE_SETUP_GUIDE.md for detailed information

