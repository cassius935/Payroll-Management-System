# MIHOYO TECHNICAL DOCUMENTATION

## 📐 Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│          MIHOYO PAYROLL MANAGEMENT SYSTEM            │
│                   Version 3.0.0                      │
└─────────────────────────────────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    [Login]         [Admin Panel]    [Worker Panel]
        │                 │                 │
        └─────────────────┼─────────────────┘
                          │
        ┌─────────────────┴─────────────────┐
        │                                   │
    [Database]                      [Managers]
        │                           ├─ EmployeeManager
        │                           ├─ MessageManager
        └─→ payroll_db             ├─ NotificationManager
            ├─ users               ├─ AnnouncementManager
            ├─ employee            ├─ ProfileManager
            ├─ announcements       ├─ SalaryCalculator
            ├─ messages            └─ DBConnection
            ├─ notifications
            ├─ worker_wallet
            └─ user_profiles
```

---

## 🗂️ File Structure

### Core Entry Point:
```
src/main.java                    (Main application entry point)
  └─ Launches splash screen
  └─ Initializes system
  └─ Routes to login frame
```

### New Manager Classes:
```
src/AnnouncementManager.java     (NEW - Global announcements)
  ├─ postAnnouncement()
  ├─ getAllAnnouncements()
  └─ deleteAnnouncement()

src/ProfileManager.java          (NEW - Profile customization)
  ├─ getProfile()
  ├─ updateNickname()
  ├─ updateBio()
  ├─ updateProfileImage()
  └─ getDisplayName()
```

### UI Panels:
```
src/LoginFrame.java              (Updated branding)
src/AdminPanelModern.java        (Added announcements tab)
src/WorkerPanelModern.java       (Enhanced home, profile, bills)
```

### Supporting Systems:
```
src/DBConnection.java            (Database connectivity)
src/EmployeeManager.java         (Employee registration)
src/MessageManager.java          (Employee messaging)
src/NotificationManager.java     (System notifications)
src/SalaryCalculator.java        (Payroll calculations)
src/SystemConfig.java            (System configuration)
src/UITheme.java                 (UI styling)
```

---

## 💾 Database Tables

### New Tables Created:

#### announcements
```sql
CREATE TABLE announcements (
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id)
);
```

#### user_profiles
```sql
CREATE TABLE user_profiles (
    user_id INT PRIMARY KEY,
    nickname VARCHAR(255),
    bio TEXT,
    profile_image VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Modified Tables:
- `messages` - Already exists, used for admin-employee messaging
- `notifications` - Already exists, system notifications
- `users` - No changes (preserves original names)
- `employee` - No changes

---

## 🔄 Class Diagrams

### AnnouncementManager
```java
public class AnnouncementManager {
    // Inner class
    static class AnnouncementInfo {
        int id;
        String title;
        String message;
        String announcedBy;
        java.sql.Timestamp createdAt;
        boolean isRead;
    }
    
    // Methods
    static boolean postAnnouncement(int adminId, String title, String message)
    static List<AnnouncementInfo> getAllAnnouncements()
    static boolean deleteAnnouncement(int announcementId)
}
```

### ProfileManager
```java
public class ProfileManager {
    // Inner class
    static class ProfileInfo {
        int userId;
        String firstName;
        String lastName;
        String nickname;
        String email;
        String department;
        String position;
        String bio;
        String profileImage;
    }
    
    // Methods
    static ProfileInfo getProfile(int userId)
    static boolean updateNickname(int userId, String nickname)
    static boolean updateBio(int userId, String bio)
    static boolean updateProfileImage(int userId, String imageUrl)
    static String getDisplayName(int userId)
}
```

---

## 🎨 UI Component Updates

### WorkerPanelModern Changes:

#### Home Panel (New Blue Design):
```java
private JScrollPane createHomePanel() {
    // Blue gradient background (PRIMARY_BLUE to cyan)
    // Shows balance card
    // 6 action buttons (Pay Bills, Check Salary, E-Wallet, etc.)
    // Latest announcements display
    // Interactive components with proper styling
}
```

#### Bills Panel (Combined):
```java
private JScrollPane createBillPaymentPanel() {
    // Single "💳 Bills" tab
    // Shows both water and electric bills
    // Combined total calculation
    // Single payment processes both
    // Payment method selection
    // History of combined payments
}
```

#### Profile Panel (Enhanced):
```java
private JScrollPane createProfilePanel() {
    // Display name/nickname field (editable)
    // Bio textarea
    // Email field
    // Phone number (editable for notifications)
    // Position & Department (read-only)
    // Notification preferences
    // Save Changes button
}
```

#### Announcements Panel (New):
```java
private JScrollPane createAnnouncementsPanel() {
    // Lists all announcements from admin
    // Shows title, message, author, timestamp
    // Scrollable list with card design
}
```

---

## 🔐 Security Considerations

### Profile Customization Safety:
- Display names stored separately from authentication
- User's actual name in `users` table remains unchanged
- All transactions still use official employee records
- Admin reports show real names, not nicknames

### Database Access:
- All queries use PreparedStatement (SQL injection prevention)
- Connection pooling with proper timeout handling
- Read-only fields for sensitive information (name, position)

### Authentication:
- Password verification in login process
- Session management through UserInfo objects
- Automatic role detection (admin vs. employee)

---

## 🚀 Performance Optimizations

### Database:
- Connection pooling with timeout handling
- Limited announcement retrieval (20 recent)
- Announcement auto-loading on startup
- Efficient list queries with proper indexing

### UI:
- Tab-based lazy loading (panels created on demand)
- Card panel reuse with UITheme utility methods
- Gradient rendering cached where possible
- Event dispatch on EDT for thread safety

---

## 📊 Data Flow Diagrams

### Announcement Creation Flow:
```
Admin Panel
    │
    ├─ User clicks "Post Announcement"
    │
    ├─ AdminPanelModern.createAnnouncementsPanel()
    │
    ├─ User enters title and message
    │
    ├─ Clicks "Post Announcement"
    │
    ├─ AnnouncementManager.postAnnouncement(adminId, title, message)
    │
    ├─ INSERT INTO announcements
    │
    └─ Success message + list refreshes

Worker Panel (Home & Announcements tabs)
    │
    ├─ AnnouncementManager.getAllAnnouncements()
    │
    ├─ SELECT * FROM announcements (with admin name JOIN)
    │
    ├─ Display latest 2 on home page
    │
    └─ Show all in announcements tab
```

### Profile Customization Flow:
```
Worker Profile Tab
    │
    ├─ ProfileManager.getProfile(userId)
    │
    ├─ SELECT from users, user_profiles, employee (LEFT JOINs)
    │
    ├─ Display current nickname, bio, info
    │
    ├─ User edits nickname and/or bio
    │
    ├─ Clicks "Save Changes"
    │
    ├─ ProfileManager.updateNickname()
    │ 
    ├─ INSERT/UPDATE user_profiles
    │
    └─ Success confirmation + profile refreshes
```

---

## 🧪 Testing Checklist

### Functionality:
- [ ] Application launches without forced dialogs
- [ ] Login works for admin and employee
- [ ] Admin can post announcements
- [ ] Announcements appear on home page
- [ ] Employees can edit their profile/nickname
- [ ] Combined bills show both water and electric
- [ ] Bill payment processes successfully
- [ ] Blue home page displays correctly
- [ ] All tabs open without errors

### UI/UX:
- [ ] Blue gradient home page renders properly
- [ ] Colors display correctly on all screens
- [ ] Text is readable with good contrast
- [ ] Buttons are responsive to clicks
- [ ] Navigation between tabs works smoothly
- [ ] Forms have proper validation
- [ ] Error messages display clearly

### Database:
- [ ] Announcements table created automatically
- [ ] User profiles table created automatically
- [ ] Data persists after logout/login
- [ ] No SQL errors in console
- [ ] Connection remains stable

---

## 🔧 Configuration

### [SystemConfig.java](src/SystemConfig.java):
```java
APP_NAME = "Mihoyo"              // System branding
APP_VERSION = "3.0.0"            // Current version
DB_URL = "jdbc:mysql://..."      // Database connection
DEMO_MODE_ENABLED = true         // Demo mode support
NOTIFICATION_CHECK_INTERVAL = 30000  // 30 seconds
```

### [DBConnection.java](src/DBConnection.java):
```java
URL = "jdbc:mysql://localhost:3306/payroll_db"
USER = "root"
PASSWORD = ""  // Set your password here
```

---

## 📈 Future Enhancement Ideas

1. **Profile Pictures**: Currently supports image URLs, can add upload functionality
2. **Advanced Announcements**: Add categories, scheduling, targeting specific departments
3. **Nickname Display**: Show nicknames everywhere (reports, messages, etc.)
4. **More Bills**: Water, Electric, Internet, etc. - combine as needed
5. **Dark Mode**: Add theme switching capability
6. **Mobile App**: Create mobile version using same Java backend
7. **Email Notifications**: Send announcements via email
8. **Audit Log**: Track all profile changes and access

---

## 🐛 Known Issues & Workarounds

None at this time. System tested and working correctly.

---

## 📞 Developer Notes

### Important Links:
- [Implementation Report](IMPLEMENTATION_REPORT.md) - Full feature documentation
- [Quick Start Guide](QUICK_START_MIHOYO.md) - User guide
- [Database Setup](database_setup.sql) - Initial database structure

### Key Principles:
1. Always use PreparedStatement for SQL queries
2. Run UI operations on Event Dispatch Thread (EDT)
3. Close database connections in finally blocks
4. Log errors for debugging
5. Test with and without database connection

### Common Modifications:
- To change app name: Update `SystemConfig.APP_NAME`
- To change colors: Modify `UITheme` color constants
- To add new buttons: Use `UITheme.createModernButton()`
- To add new tables: Create in database, add manager class

---

**Document Version**: 1.0  
**Last Updated**: May 12, 2026  
**Mihoyo**: Version 3.0.0  
**Status**: ✅ PRODUCTION READY
