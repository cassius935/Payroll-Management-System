# 🏢 Enhanced Payroll Management System
## Complete System Documentation & Setup Guide

### Version: 2.0 (Modernized)
### Last Updated: May 2024

---

## 📋 Table of Contents
1. [System Overview](#system-overview)
2. [Login Credentials](#login-credentials)
3. [Features](#features)
4. [Database Setup](#database-setup)
5. [How to Run](#how-to-run)
6. [User Guides](#user-guides)
7. [API & Database Reference](#api--database-reference)

---

## 🎯 System Overview

The Enhanced Payroll Management System is a modern, comprehensive solution for managing employee payroll, communications, and digital payments. Inspired by fintech applications like Konek2Card, it features:

- **Unified Login System**: No role selection - the system determines user type automatically
- **Modern UI**: Beautiful gradient designs and card-based layouts
- **Salary Management**: Release salaries with automatic deductions for absences
- **Digital Wallet**: Balance display and transaction tracking (Konek2Card style)
- **Messaging System**: Communication between admins and workers
- **E-Wallet Integration**: PayMaya, GCash, PayPal support
- **Bill Payment System**: Pay water and electric bills directly
- **Notification System**: Real-time alerts for salary releases and important events
- **Profile Management**: Phone number connection for SMS notifications

---

## 🔐 Login Credentials

### Default Admin Account
```
Username: admin
Password: Admin@2024!
Login Code: ADM-2024-PAYROLL-001
Type: Administrator
```

### Default Worker Accounts
```
Worker #1:
Username: worker1
Password: Worker@123
Login Code: WRK-2024-SYSTEM-001
Name: John Doe
Department: Sales

Worker #2:
Username: worker2
Password: Worker@456
Login Code: WRK-2024-SYSTEM-002
Name: Jane Smith
Department: IT
```

---

## ✨ Key Features

### 👨‍💼 Administrator Panel

#### 1. **Dashboard** 📊
- Quick overview of total employees
- Active workers today
- Pending salaries
- Unread messages
- Recent activities feed

#### 2. **Employee Management** 👥
- View all employees
- Add new employees
- Edit employee details
- Manage employee information
- Search and filter employees

#### 3. **Salary Management** 💰
- Release salaries to employees
- Automatic deduction for absences
- Calculate net salary
- Track payment status
- View payment history

#### 4. **Messages & Notifications** 📬
- Send messages to workers
- Compose notifications
- Notify all workers (e.g., submit slips reminder)
- View message history
- Track read status

#### 5. **E-Wallet Integration** 📱
- Connect worker e-wallets (PayMaya, GCash, PayPal)
- Verify accounts
- Manage active wallets
- View connected accounts per worker
- Support for direct salary transfers

#### 6. **Bill Management** 📄
- Track water and electric bills
- Process bill payments
- View bill status
- Set payment reminders
- Download payment receipts

#### 7. **Attendance Management** ✓
- Mark daily attendance
- Track absences
- Record leave
- Mark half days
- Generate attendance reports

---

### 👷 Worker Dashboard

#### 1. **Home Screen** 🏠
- **Konek2Card-Style Balance Card**:
  - Available balance display
  - Last salary information
  - Card-themed gradient design
- Quick action buttons
- Notification feed
- Recent activity summary

#### 2. **Wallet & Balance** 💰
- View current balance
- Transaction history
- Filter transactions
- View transaction details
- Download statements

#### 3. **Messages** 📬
- Receive messages from admin
- Salary slip notifications
- Attendance reminders
- Payment confirmations
- Reply to messages

#### 4. **Pay Bills** 💳
- Pay water bills
- Pay electric bills
- Select payment method
- Instant payment processing
- Transaction confirmation
- Payment receipt

#### 5. **E-Wallet Connection** 📱
- Connect to PayMaya
- Connect to GCash
- Connect to PayPal
- Verify accounts
- View connected wallets
- Manage payment methods

#### 6. **Profile Settings** 👤
- View personal information
- Add phone number for SMS notifications
- View employment details
- Notification preferences
- Save settings

---

## 🗄️ Database Setup

### Prerequisites
- MySQL Server (local or remote)
- Database name: `payroll_db`
- User: `root`
- Password: (empty or configure in DBConnection.java)

### Setup Instructions

1. **Create Database**:
```sql
CREATE DATABASE payroll_db;
USE payroll_db;
```

2. **Run Setup Script**:
Execute the `database_setup.sql` file in your MySQL client:
```bash
mysql -u root payroll_db < database_setup.sql
```

### Database Tables

#### 1. **users** - Authentication
```
Stores login credentials and user information
- id, username, password (SHA256), login_code
- user_type (ADMIN/WORKER)
- email, phone_number, first_name, last_name
- is_active, created_at, last_login
```

#### 2. **employee** - Worker Information
```
Stores employee profile data
- employee_id, user_id
- department, position
- hourly_rate, monthly_salary
- hire_date
```

#### 3. **worker_wallet** - Digital Wallet
```
Stores worker balance and salary information
- wallet_id, employee_id
- balance, last_salary_date, last_salary_amount
- updated_at
```

#### 4. **attendance** - Daily Records
```
Tracks daily attendance
- attendance_id, employee_id, work_date
- status (PRESENT/ABSENT/LEAVE/HALF_DAY)
- notes, created_at
```

#### 5. **salary_records** - Payroll
```
Stores salary transactions
- salary_id, employee_id
- pay_period_start, pay_period_end
- gross_salary, absences, absence_deduction, other_deductions, net_salary
- payment_status (PENDING/RELEASED/PAID)
- release_date, payment_date
```

#### 6. **messages** - Communication
```
Internal messaging system
- message_id, sender_id, recipient_id
- subject, message_text
- message_type (SALARY_SLIP/NOTIFICATION/GENERAL/ALERT)
- is_read, created_at
```

#### 7. **notifications** - Alerts
```
Real-time notifications
- notification_id, user_id
- notification_type (SALARY_RELEASED/ATTENDANCE_REQUIRED/MESSAGE/PAYMENT_REMINDER/SYSTEM)
- title, message, related_data (JSON)
- is_read, created_at
```

#### 8. **e_wallet_accounts** - E-Wallet Management
```
Stores connected e-wallet accounts
- wallet_account_id, employee_id
- wallet_type (PAYMAYA/GCASH/PAYPAL)
- account_identifier, is_primary, is_verified
- verified_at, created_at
```

#### 9. **bills** - Utility Bills
```
Tracks water and electric bills
- bill_id, employee_id
- bill_type (WATER/ELECTRIC/OTHER)
- bill_amount, payment_status (PENDING/PAID/OVERDUE)
- due_date, payment_date
- payment_method, bill_reference
```

#### 10. **transactions** - Payment History
```
All wallet transactions
- transaction_id, employee_id
- transaction_type (SALARY_CREDIT/BILL_PAYMENT/WALLET_TRANSFER/E_WALLET_TRANSFER)
- amount, description, status
- transaction_date
```

#### 11. **admin_logs** - Audit Trail
```
Admin activity logging
- log_id, admin_id
- action, target_employee_id
- details (JSON), created_at
```

---

## 🚀 How to Run

### Option 1: Run via IDE (VS Code / IntelliJ)
1. Open the project folder
2. Ensure MySQL is running with payroll_db database
3. Run `src/PayrollApp.java`
4. Login with provided credentials

### Option 2: Compile and Run from Terminal
```bash
# Compile all Java files
javac -cp "lib/*;." src/*.java

# Run the application
java -cp "lib/*;." src.PayrollApp
```

### System Requirements
- Java 11 or higher
- MySQL 5.7 or higher
- 100MB free disk space
- 1280x1024 minimum screen resolution

---

## 📖 User Guides

### Admin Guide

#### Release a Salary
1. Go to **Salary Management** tab
2. Select employee from dropdown
3. Enter gross salary amount
4. Enter number of absences (if any)
5. System calculates deduction (₱500 per absence)
6. Click **Release Salary**
7. Confirmation sent to worker

#### Send Notification to Workers
1. Go to **Messages & Notifications** tab
2. Click **Send Notification**
3. Select notification type:
   - Attendance: Request to submit slips
   - Message: General message
   - Alert: Important announcement
4. Click Send
5. All active workers receive notification

#### Manage E-Wallets
1. Go to **E-Wallet Integration** tab
2. Select worker
3. Choose wallet provider (PayMaya/GCash/PayPal)
4. Enter account identifier
5. Click **Verify Account**
6. Account is now available for salary transfers

---

### Worker Guide

#### Check Your Balance
1. Click **Home** tab
2. View balance on the Konek2Card-style card
3. See last salary info
4. View transaction history below

#### Pay a Bill
1. Click **Pay Bills** tab
2. Select bill from dropdown
3. Amount auto-fills
4. Choose payment method (Wallet/E-Wallet)
5. Review details
6. Click **Pay Now**
7. Instant confirmation

#### Connect E-Wallet
1. Click **E-Wallet** tab
2. Select wallet provider
3. Enter phone number or email
4. Click verify
5. Follow SMS/email verification
6. Account shows as "✓ Verified"

#### Update Phone Number
1. Click **Profile** tab
2. Enter phone number
3. Check notification preferences
4. Click **Save Changes**
5. You'll receive SMS when salary is released

---

## 🔌 API & Database Reference

### Key Methods in DBConnection.java

```java
// Authentication
UserInfo authenticateUser(String username, String password)
void updateLastLogin(int userId)

// User Operations
UserInfo getUserById(int userId)
EmployeeInfo getEmployeeInfo(String employeeId)

// Password
String hashPassword(String password)
String generateLoginCode(String userType)
```

### Classes

#### UserInfo
```java
public class UserInfo {
    int id;
    String username;
    String userType;      // ADMIN or WORKER
    String loginCode;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    
    String getFullName();
}
```

#### EmployeeInfo
```java
public class EmployeeInfo {
    String employeeId;
    String department;
    String position;
    double hourlyRate;
    double monthlySalary;
    double balance;
    Timestamp lastSalaryDate;
    double lastSalaryAmount;
}
```

---

## 🎨 UI Theme

The system uses a modern fintech-inspired color scheme:

- **Primary Blue**: #186DC9
- **Secondary Teal**: #00A79E
- **Accent Orange**: #FF8C42
- **Success Green**: #2EC46F
- **Warning Yellow**: #FFC107
- **Danger Red**: #E53935

All components use:
- Rounded corners (8-15px radius)
- Subtle shadows
- Gradient backgrounds
- Modern fonts (Segoe UI)

---

## 🔒 Security Features

1. **Password Hashing**: SHA-256 hashing for all passwords
2. **Login Codes**: Unique codes generated for each user
3. **Session Management**: Tracks last login time
4. **Account Status**: Can activate/deactivate accounts
5. **Audit Logging**: All admin actions logged

---

## 📊 Sample Data

The system comes pre-loaded with:
- 1 Admin account
- 2 Worker accounts
- Pre-configured departments
- Sample transaction history
- Example e-wallet accounts

---

## 🆘 Troubleshooting

### Database Connection Failed
1. Ensure MySQL is running
2. Check database name: `payroll_db`
3. Verify user credentials in DBConnection.java
4. Run database_setup.sql again

### Login Failed
1. Verify username and password
2. Ensure user account is active
3. Check database connectivity
4. Verify login_code in users table

### UI Display Issues
1. Ensure screen resolution is 1280x1024+
2. Check Java version (11+)
3. Verify font availability
4. Check system color settings

---

## 📝 Notes

- All passwords in database are SHA-256 hashed
- Balance updates in real-time
- Transactions are tracked automatically
- Admin logs all major actions
- System supports multiple concurrent workers

---

## 🔄 Updates & Maintenance

### To Add New Workers
1. Go to Employee Management
2. Click "Add Employee"
3. Fill in details
4. System auto-generates login code
5. Create user account
6. Send credentials to worker

### To Modify Salary
1. Go to Salary Management
2. Select employee
3. Enter new amount
4. Confirm changes
5. Update is retroactive

### To Reset Password
1. Contact system admin
2. Admin can reset via backend
3. Employee gets new temporary password
4. Employee changes on first login

---

## 📞 Support

For issues or questions:
1. Check this documentation
2. Review error messages
3. Check database connectivity
4. Verify account status
5. Contact system administrator

---

**System Version**: 2.0 Enhanced (May 2024)
**Status**: Production Ready
**Last Verified**: May 1, 2024
