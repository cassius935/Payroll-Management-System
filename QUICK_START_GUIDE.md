# 🚀 QUICK START GUIDE - Payroll Management System v2.1

## ⚡ 30-Second Startup

### Option 1: Run the Application (Easiest)
```batch
1. Double-click `run.bat`
2. Wait for application to load
3. Use demo credentials (see below)
```

### Option 2: Build and Run
```batch
1. Double-click `build.bat` (compiles project)
2. Double-click `run.bat` (runs application)
```

### Option 3: Command Line
```batch
cd "Payroll Management System"
javac -encoding UTF-8 -cp "src;lib\mysql-connector-j-9.6.0.jar" src\*.java
java -cp "src;lib\mysql-connector-j-9.6.0.jar" src.main
```

---

## 👤 Demo Credentials

### Admin Account
- **Username**: admin
- **Password**: Admin@2024!
- **Access**: Full system administration, employee management, salary processing

### Worker Account
- **Username**: worker1
- **Password**: Worker@123
- **Access**: Personal dashboard, balance view, bill payment, profile management

---

## 🎯 Main Features

### For Administrators
✓ Employee Management (Add, Edit, Delete)
✓ Salary Management & Processing
✓ Attendance Tracking
✓ Messaging & Notifications
✓ E-Wallet Integration Setup
✓ Bill Management
✓ Dashboard with Statistics

### For Workers
✓ View Account Balance
✓ Check Salary History
✓ Pay Bills Online
✓ Connect E-Wallets (PayMaya, GCash, PayPal)
✓ View Notifications
✓ Update Profile
✓ Transaction History

---

## 📋 System Requirements

### Minimum Requirements
- **Java**: JDK 8 or higher
- **RAM**: 512 MB minimum (1 GB recommended)
- **Storage**: 100 MB free space
- **OS**: Windows 7 or later, Mac OS 10.12+, Linux

### Optional - For Full Functionality
- **Database**: MySQL 5.7+ (for persistent data)
- **JDBC Driver**: MySQL Connector/J 9.6.0+ (included)

---

## 🔧 First-Time Setup

### 1. Verify Java Installation
```batch
java -version
javac -version
```

If these commands fail, install JDK from java.com

### 2. (Optional) Setup Database
```sql
1. Open MySQL Workbench or command line
2. Run: database_setup.sql
3. Create database 'payroll_db'
4. Update credentials in DBConnection.java if needed
```

### 3. Run the Application
```batch
Double-click run.bat
```

---

## 🎨 Interface Overview

### Login Screen
- Modern gradient background (Blue to Teal)
- Simple username/password form
- Show Password toggle
- Demo credentials displayed at bottom

### Admin Dashboard
- **Dashboard Tab**: System statistics and recent activities
- **Manage Employees Tab**: Employee list with actions
- **Salary Management Tab**: Calculate and release salaries
- **Messages Tab**: Internal messaging system
- **E-Wallet Tab**: Payment gateway setup
- **Bill Management Tab**: Track employee bills
- **Attendance Tab**: Daily attendance records

### Worker Dashboard
- **Home Tab**: Quick actions and notifications
- **Wallet Tab**: Balance and transaction history
- **Messages Tab**: Inbox and messaging
- **Pay Bills Tab**: Quick bill payments
- **E-Wallet Tab**: Connected payment methods
- **Profile Tab**: Personal information

---

## 💡 Tips & Tricks

### Quick Navigation
- Click emojis to navigate between tabs
- Use demo credentials for testing
- Check the console (command line) for debugging info

### Salary Calculation
- Includes Philippine tax rates:
  - SSS: 4.5%
  - PhilHealth: 2.5%
  - Pag-IBIG: 2%
  - Income Tax: ~12%
- Absence deduction: PHP 500 per day

### Error Handling
- If database connection fails, the system runs in demo mode
- All error messages display with helpful guidance
- Check terminal for detailed error logs

---

## 🔐 Security & Data

### Built-in Security
✓ SHA-256 password hashing
✓ Parameterized SQL queries (prevents SQL injection)
✓ Connection timeout protection
✓ Automatic fallback to demo mode

### Demo Mode
- Works without MySQL database
- Uses built-in demo accounts
- No data persistence (for testing only)

---

## 🐛 Troubleshooting

### "Compilation FAILED" Error
**Solution**: 
- Ensure Java JDK (not JRE) is installed
- Update PATH environment variable
- Try: `javac -version` in command prompt

### "Could not find JDBC Driver"
**Solution**:
- Driver is included (lib folder)
- Run from correct directory
- Check classpath in run.bat

### Application Won't Start
**Solution**:
- Close any previous instance
- Check if port is available
- Verify Java installation
- Check system logs

### Database Connection Failed
**Solution**:
- Normal in demo mode - continue anyway
- Install MySQL 5.7+
- Run database_setup.sql
- Update credentials in src/DBConnection.java

---

## 📊 Project Structure

```
Payroll Management System/
├── src/
│   ├── main.java                    ✓ Entry point
│   ├── LoginFrame.java              ✓ Login UI
│   ├── AdminPanelModern.java        ✓ Admin dashboard
│   ├── WorkerPanelModern.java       ✓ Worker dashboard
│   ├── DBConnection.java            ✓ Database operations
│   ├── SalaryCalculator.java        ✓ Payroll calculations
│   ├── UITheme.java                 ✓ Styling system
│   └── [other files]
├── lib/
│   └── mysql-connector-j-9.6.0.jar
├── build.bat                        ✓ Compile script
├── run.bat                          ✓ Run script
├── database_setup.sql               ✓ Database schema
└── README files

✓ = Fully enhanced and tested
```

---

## 📞 Support & Documentation

### Files Included
- `SYSTEM_IMPROVEMENTS.md` - Technical details of enhancements
- `DATABASE_INTEGRATION_GUIDE.md` - Setup database
- `README.md` - Project overview
- `CHANGELOG.md` - Update history

### Getting Help
1. Check included documentation
2. Review error messages in console
3. Verify system requirements
4. Check Java installation

---

## 🎉 You're Ready to Go!

**Double-click `run.bat` and start using the system!**

### First Steps
1. ✓ Login with demo credentials
2. ✓ Explore the admin dashboard
3. ✓ Try salary calculations
4. ✓ Test employee management
5. ✓ Switch to worker account to see employee view

---

**Version**: 2.1 Enhanced Edition
**Last Updated**: May 6, 2026
**Status**: Production Ready ✓

Enjoy the enhanced Payroll Management System!
