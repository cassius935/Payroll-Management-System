-- Enhanced Payroll Management System Database Schema
-- Includes login codes, balance, messaging, notifications, e-wallet, and bill payment

-- Create users table (unified login for both admin and workers)
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    login_code VARCHAR(20) UNIQUE NOT NULL,
    user_type ENUM('ADMIN', 'WORKER') NOT NULL DEFAULT 'WORKER',
    phone_number VARCHAR(20),
    email VARCHAR(100),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Create employee table (worker profile and salary information)
CREATE TABLE IF NOT EXISTS employee (
    employee_id VARCHAR(20) PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    department VARCHAR(50),
    position VARCHAR(50),
    hourly_rate DECIMAL(10, 2) NOT NULL,
    monthly_salary DECIMAL(12, 2),
    hire_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create wallet/balance table for workers
CREATE TABLE IF NOT EXISTS worker_wallet (
    wallet_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(12, 2) DEFAULT 0,
    last_salary_date TIMESTAMP NULL,
    last_salary_amount DECIMAL(12, 2),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Create attendance table (for tracking absences and leaves)
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) NOT NULL,
    work_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LEAVE', 'HALF_DAY') DEFAULT 'PRESENT',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    UNIQUE KEY unique_attendance (employee_id, work_date)
);

-- Create salary/payroll table (for salary transactions)
CREATE TABLE IF NOT EXISTS salary_records (
    salary_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) NOT NULL,
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    gross_salary DECIMAL(12, 2) NOT NULL,
    absences INT DEFAULT 0,
    absence_deduction DECIMAL(10, 2) DEFAULT 0,
    other_deductions DECIMAL(10, 2) DEFAULT 0,
    net_salary DECIMAL(12, 2) NOT NULL,
    payment_status ENUM('PENDING', 'RELEASED', 'PAID') DEFAULT 'PENDING',
    release_date TIMESTAMP NULL,
    payment_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Create messages/inbox table (communication between admin and workers)
CREATE TABLE IF NOT EXISTS messages (
    message_id INT PRIMARY KEY AUTO_INCREMENT,
    sender_id INT NOT NULL,
    recipient_id INT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message_text TEXT NOT NULL,
    message_type ENUM('SALARY_SLIP', 'NOTIFICATION', 'GENERAL', 'ALERT') DEFAULT 'GENERAL',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);

-- Create notifications table (for real-time alerts)
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    notification_type ENUM('SALARY_RELEASED', 'ATTENDANCE_REQUIRED', 'MESSAGE', 'PAYMENT_REMINDER', 'SYSTEM') DEFAULT 'SYSTEM',
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    related_data TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create e-wallet accounts table (PayMaya, GCash, PayPal integration)
CREATE TABLE IF NOT EXISTS e_wallet_accounts (
    wallet_account_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) NOT NULL,
    wallet_type ENUM('PAYMAYA', 'GCASH', 'PAYPAL') NOT NULL,
    account_identifier VARCHAR(100) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Create bills table (water and electric bills payment tracking)
CREATE TABLE IF NOT EXISTS bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) NOT NULL,
    bill_type ENUM('WATER', 'ELECTRIC', 'OTHER') NOT NULL,
    bill_amount DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'OVERDUE') DEFAULT 'PENDING',
    due_date DATE NOT NULL,
    bill_reference VARCHAR(100),
    payment_date TIMESTAMP NULL,
    payment_method ENUM('WALLET', 'BANK_TRANSFER', 'CASH') DEFAULT 'WALLET',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Create transaction history table (track all wallet and bill transactions)
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id VARCHAR(20) NOT NULL,
    transaction_type ENUM('SALARY_CREDIT', 'BILL_PAYMENT', 'WALLET_TRANSFER', 'E_WALLET_TRANSFER') NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    description VARCHAR(255),
    status ENUM('PENDING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Create admin activity log
CREATE TABLE IF NOT EXISTS admin_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    admin_id INT NOT NULL,
    action VARCHAR(255) NOT NULL,
    target_employee_id VARCHAR(20),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id),
    FOREIGN KEY (target_employee_id) REFERENCES employee(employee_id)
);

-- === DATA INITIALIZATION ===

-- Admin Credentials with Login Codes
-- DEFAULT ADMIN: username=admin, password=Admin@2024!
-- Login Code: ADM-2024-PAYROLL-001
INSERT INTO users (username, password, login_code, user_type, email, first_name, last_name) 
VALUES ('admin', SHA2('Admin@2024!', 256), 'ADM-2024-PAYROLL-001', 'ADMIN', 'admin@payroll.com', 'System', 'Administrator')
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- Sample Worker Account for Testing
-- DEFAULT WORKER: username=worker1, password=Worker@123
-- Login Code: WRK-2024-SYSTEM-001
INSERT INTO users (username, password, login_code, user_type, email, first_name, last_name, phone_number) 
VALUES ('worker1', SHA2('Worker@123', 256), 'WRK-2024-SYSTEM-001', 'WORKER', 'worker1@company.com', 'John', 'Doe', '09123456789')
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- Add second sample worker
-- DEFAULT WORKER: username=worker2, password=Worker@456
-- Login Code: WRK-2024-SYSTEM-002
INSERT INTO users (username, password, login_code, user_type, email, first_name, last_name, phone_number) 
VALUES ('worker2', SHA2('Worker@456', 256), 'WRK-2024-SYSTEM-002', 'WORKER', 'worker2@company.com', 'Jane', 'Smith', '09987654321')
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- Create employee records for workers
INSERT INTO employee (employee_id, user_id, department, position, hourly_rate, monthly_salary)
SELECT 'EMP-001', id, 'Sales', 'Sales Associate', 25.00, 40000.00 FROM users WHERE username = 'worker1'
ON DUPLICATE KEY UPDATE employee_id=VALUES(employee_id);

INSERT INTO employee (employee_id, user_id, department, position, hourly_rate, monthly_salary)
SELECT 'EMP-002', id, 'IT', 'Software Developer', 35.00, 60000.00 FROM users WHERE username = 'worker2'
ON DUPLICATE KEY UPDATE employee_id=VALUES(employee_id);

-- Initialize worker wallets
INSERT INTO worker_wallet (employee_id, balance) VALUES ('EMP-001', 0.00), ('EMP-002', 0.00)
ON DUPLICATE KEY UPDATE wallet_id=wallet_id;
