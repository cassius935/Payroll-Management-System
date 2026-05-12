# MIHOYO - QUICK START GUIDE

## 🚀 How to Run the System

### Step 1: Open Terminal/Command Prompt
```bash
cd "c:\Users\user\OneDrive\Desktop\Payroll Management System"
```

### Step 2: Run the Application
```bash
java -cp ".;lib/*" src.main
```

That's it! The system will:
- Initialize automatically
- Show a splash screen
- Connect to database
- Launch the login interface

**NO FILE DIALOGS!** ✅ - The system runs smoothly without forcing any file selections

---

## 🔑 Default Login Credentials

### Admin Account:
- **Username**: admin
- **Password**: Admin@2024!

### Employee Account:
- Create new employees through the Admin panel

---

## 📋 What's New in Mihoyo v3.0.0

### ✨ System Rebranding
- System name is now **"Mihoyo"** everywhere
- Professional branding throughout the application
- Updated version to 3.0.0

### 🏠 Employee Home Page (Now BLUE!)
- Beautiful blue gradient background
- Shows latest announcements
- 6 quick action buttons for fast access
- Balance display card in Konek2Card style

### 📢 Global Announcements
- Admin can post announcements for all employees
- Employees see announcements on home page and dedicated tab
- Admin has "📢 Announcements" tab to manage them

### 👤 Profile Customization
- Employees can set a display name (nickname)
- **Important**: Nickname doesn't change SQL records - just display name
- Can add bio and profile information
- Editable contact phone number

### 💳 Combined Bills Menu
- Water and electric bills now in ONE "💳 Bills" menu item
- Pay both together in single transaction
- Shows combined total (PHP 2,350.00)
- Much simpler than separate bills

### 💬 Admin Messaging
- Admin can send individual messages to employees
- Messages tab shows all worker messages
- Compose new messages directly

---

## 📱 Main Features

### For Employees:
- ✅ View balance and salary info
- ✅ Pay combined water & electric bills
- ✅ Connect multiple e-wallets
- ✅ View and customize profile
- ✅ Receive announcements from admin
- ✅ Message admin/management
- ✅ Check transaction history

### For Admin:
- ✅ Manage employees (add/edit/delete)
- ✅ Release salaries to employees
- ✅ Send announcements to everyone
- ✅ Message individual employees
- ✅ Track attendance
- ✅ Manage e-wallet accounts
- ✅ View dashboard with stats

---

## 🎨 Color Indicators

| Color | Meaning |
|-------|---------|
| 🔵 Blue | Primary actions, payroll info |
| 🟢 Green | Success, salary, positive actions |
| 🟠 Orange | Alerts, e-wallet, warnings |
| 🟡 Yellow | Cautions, pending items |
| 🔴 Red | Danger, logout, critical actions |
| 🔷 Teal | Secondary info, cool actions |

---

## ⚙️ System Requirements

- Java 8 or higher (latest: Java 25)
- MySQL Server running and configured
- All JAR files in `lib/` folder
- Stable internet connection for database

---

## 🐛 Troubleshooting

### "Could not find main class" error:
Make sure you're in the correct directory:
```bash
cd "c:\Users\user\OneDrive\Desktop\Payroll Management System"
```

### Database connection error:
1. Ensure MySQL is running
2. Check database name: `payroll_db`
3. Verify credentials in [DBConnection.java](src/DBConnection.java)

### UI looks strange:
- Make sure you have display scaling set correctly
- Try zooming in/out with Ctrl+Plus/Minus

---

## 📞 Support

For issues or questions about:
- **Profile customization**: Check "👤 Profile" tab in worker panel
- **Announcements**: Admin posts in "📢 Announcements" tab
- **Bills payment**: Combined in "💳 Bills" tab
- **Salary info**: View in "💰 Wallet & Balance" tab

---

## 🎯 Key Improvements Made

1. ✅ **Executable System** - Runs directly with `java -cp ".;lib/*" src.main`
2. ✅ **No Forced Dialogs** - Application launches smoothly to login
3. ✅ **Rebranded as Mihoyo** - Professional name throughout
4. ✅ **Announcements System** - Global communications from admin
5. ✅ **Profile Customization** - Display name without changing SQL records
6. ✅ **Combined Bills** - Water and electric in single menu
7. ✅ **Blue Home Page** - Beautiful gradient design for workers
8. ✅ **Enhanced Admin Features** - Better messaging and control

---

**Version**: 3.0.0  
**System**: Mihoyo Payroll Management System  
**Status**: ✅ READY TO USE  

Enjoy using Mihoyo! 🎉
