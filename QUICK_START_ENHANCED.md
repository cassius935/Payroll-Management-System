# 🚀 QUICK START GUIDE - Enhanced Payroll System

## Installation & Setup (5 minutes)

### Step 1: Database Setup
```bash
1. Open MySQL Command Line or MySQL Workbench
2. Run: CREATE DATABASE payroll_db;
3. Select the database: USE payroll_db;
4. Import the setup script from: database_setup.sql
```

### Step 2: Verify Connection
Edit `src/DBConnection.java` if needed:
```java
private static final String URL = "jdbc:mysql://localhost:3306/payroll_db";
private static final String USER = "root";
private static final String PASSWORD = "";  // Add password if you have one
```

### Step 3: Run the Application
```bash
javac -cp "lib/*;." src/*.java
java -cp "lib/*;." src.PayrollApp
```

---

## Login Credentials (Copy-Paste Ready)

### 🔒 Admin Account
```
Username: admin
Password: Admin@2024!
```

### 👷 Worker Account 1
```
Username: worker1
Password: Worker@123
```

### 👷 Worker Account 2
```
Username: worker2
Password: Worker@456
```

---

## First Time Setup for Admin

1. **Login** with admin credentials
2. Go to **Employee Management** tab
3. View existing 5 sample employees
4. Click **Add Employee** to register more
5. Go to **Salary Management** tab
6. Release salary to an employee (e.g., ₱40,000 for John Doe)
7. View the transaction in the admin dashboard

---

## First Time Setup for Worker

1. **Login** with worker1 credentials
2. See **Home** tab with balance card (Konek2Card style)
3. Update **Profile** tab:
   - Add phone number (e.g., 09123456789)
   - Check notification preferences
   - Click Save
4. Go to **E-Wallet** tab:
   - View example PayMaya and GCash accounts
   - They're pre-verified for testing
5. Go to **Pay Bills** tab:
   - Select a bill
   - Click Pay Now
   - Confirm the payment
6. Check **Messages** tab for notifications

---

## Key Features to Try

### 🏠 For Admin:
- [ ] Dashboard overview - See all stats
- [ ] Add new employee - Test registration
- [ ] Release salary - Try with 1-2 absences
- [ ] Send notification - Message all workers
- [ ] Manage e-wallets - Verify an account
- [ ] Process bills - Mark bills as paid
- [ ] Track attendance - Mark workers present/absent

### 🏠 For Worker:
- [ ] Check balance - See Konek2Card display
- [ ] View transactions - See payment history
- [ ] Read messages - Check notifications
- [ ] Pay a bill - Try water or electric
- [ ] Connect e-wallet - Select PayMaya/GCash
- [ ] Update profile - Add phone number
- [ ] Receive notifications - Admin sends message

---

## Default Credentials Summary

| User | Username | Password | Role |
|------|----------|----------|------|
| System Admin | `admin` | `Admin@2024!` | ADMIN |
| Employee 1 | `worker1` | `Worker@123` | WORKER |
| Employee 2 | `worker2` | `Worker@456` | WORKER |

---

## 🎨 What's New (Version 2.0)

✅ **Unified Login** - No role selection, auto-detects user type
✅ **Modern UI** - Fintech-inspired design with gradients
✅ **Balance Card** - Konek2Card-style display
✅ **Salary Release** - With automatic absence deductions
✅ **Messaging System** - Admin to worker communication
✅ **E-Wallet Integration** - PayMaya, GCash, PayPal
✅ **Bill Payment** - Pay water and electric directly
✅ **Notifications** - Real-time alerts for salary release
✅ **Phone Verification** - SMS notification support
✅ **Transaction History** - Complete payment tracking
✅ **Admin Dashboard** - Overview of all operations
✅ **Attendance Tracking** - Mark present/absent/leave
✅ **Multiple Languages Ready** - Extensible for localization
✅ **Login Codes** - Unique code per user for verification

---

## Database Tables Structure

All created automatically:
- ✅ users - Login & authentication
- ✅ employee - Worker profiles
- ✅ worker_wallet - Balance tracking
- ✅ attendance - Daily records
- ✅ salary_records - Payroll history
- ✅ messages - Communication
- ✅ notifications - Alerts
- ✅ e_wallet_accounts - Payment methods
- ✅ bills - Water/Electric bills
- ✅ transactions - Payment history
- ✅ admin_logs - Audit trail

---

## Common Tasks

### Admin: Release Salary
1. Go to "Salary Management"
2. Select employee name
3. Enter ₱40,000 for monthly salary
4. Enter number of absences (e.g., 2)
5. Deduction auto-calculated: 2 × ₱500 = ₱1,000
6. Net salary shows: ₱39,000
7. Click "Release Salary"
8. ✓ Salary added to worker's wallet

### Worker: Check Balance
1. Click "Home" tab
2. See balance on blue card: ₱39,000.00
3. See last salary info below
4. Click "Wallet & Balance" for full history

### Worker: Pay Bill
1. Click "Pay Bills" tab
2. Select "Water Bill - ₱850.00"
3. Choose "Wallet Balance" as payment method
4. Click "Pay Now"
5. ✓ Deducted from balance
6. New balance: ₱38,150.00

---

## 📞 Support & Troubleshooting

### Can't Connect to Database?
- Check if MySQL is running
- Verify `payroll_db` exists
- Run database_setup.sql again

### Login Failed?
- Check spelling of username/password
- Ensure account is active in database
- Verify user record exists in `users` table

### UI Not Displaying?
- Update Java to 11+
- Check screen resolution (1280x1024+)
- Clear Java cache

---

## 💡 Tips & Tricks

1. **Pre-filled Test Data**: Don't delete sample employees - use for testing
2. **Generate Codes**: Each user has unique login_code in database
3. **Transaction Tracking**: All payments tracked in transactions table
4. **Audit Log**: View admin actions in admin_logs table
5. **Account Verification**: E-wallets show as "Verified" after confirmation

---

## 📊 Testing Scenarios

### Scenario 1: Monthly Payroll
1. Admin logs in
2. Goes to Salary Management
3. Releases ₱40,000 to worker1 (2 absences)
4. Net: ₱39,000
5. Worker1 logs in
6. Sees ₱39,000 in balance
7. ✓ Success

### Scenario 2: Bill Payment
1. Worker logs in
2. Click Pay Bills
3. Select Electric Bill ₱1,500
4. Choose Wallet as method
5. Click Pay Now
6. Balance reduces to ₱37,500
7. See transaction in history
8. ✓ Success

### Scenario 3: E-Wallet Connection
1. Worker logs in
2. Click E-Wallet
3. See PayMaya pre-verified
4. Admin can now transfer salary to PayMaya
5. Worker notified via SMS (if phone added)
6. ✓ Success

---

## 📝 Sample SQL Queries

### Check User Credentials
```sql
SELECT id, username, user_type, login_code FROM users;
```

### View Employee Wallets
```sql
SELECT e.employee_id, u.first_name, u.last_name, ww.balance 
FROM employee e 
JOIN users u ON e.user_id = u.id 
JOIN worker_wallet ww ON e.employee_id = ww.employee_id;
```

### View Salary History
```sql
SELECT employee_id, gross_salary, absences, absence_deduction, net_salary, payment_status 
FROM salary_records 
ORDER BY created_at DESC;
```

### Check E-Wallet Accounts
```sql
SELECT e.employee_id, u.first_name, wa.wallet_type, wa.account_identifier, wa.is_verified
FROM e_wallet_accounts wa
JOIN employee e ON wa.employee_id = e.employee_id
JOIN users u ON e.user_id = u.id;
```

---

## ✅ Checklist for Full Setup

- [ ] MySQL database created
- [ ] database_setup.sql executed
- [ ] DBConnection.java configured correctly
- [ ] PayrollApp.java compiles without errors
- [ ] Application launches successfully
- [ ] Admin login works (admin/Admin@2024!)
- [ ] Worker login works (worker1/Worker@123)
- [ ] Dashboard displays all stats
- [ ] Salary release functions
- [ ] Bill payment works
- [ ] E-wallet shows accounts
- [ ] Messages send successfully

---

## 🎯 Next Steps

1. **Customize**: Modify UITheme.java to change colors
2. **Extend**: Add more e-wallet providers
3. **Integrate**: Connect to real payment gateways
4. **Deploy**: Package as executable JAR
5. **Enhance**: Add more reporting features

---

**Version**: 2.0 Enhanced
**Status**: Ready to Use
**Last Updated**: May 2024

For detailed documentation, see: **ENHANCED_SYSTEM_GUIDE.md**
