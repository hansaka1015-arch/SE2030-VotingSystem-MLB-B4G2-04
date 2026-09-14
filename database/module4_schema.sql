-- ========================================================
-- SE2030 Software Engineering - Group Y2-S1-MLB-B4G2-04
-- Web-Based Voting System for Reality Show (VoteSphere Lanka)
-- Module 4: Paid Vote & Credit Management Database Schema
-- ========================================================

USE votingsystem_db;

-- 1. Table: Credit Packages
CREATE TABLE IF NOT EXISTS credit_packages (
    package_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price_lkr DECIMAL(10,2) NOT NULL,
    credit_amount INT NOT NULL,
    bonus_credits INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Table: User Credits (Wallet Balance)
CREATE TABLE IF NOT EXISTS user_credits (
    user_id INT PRIMARY KEY,
    balance INT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Table: Transactions Log
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    user_id INT NOT NULL,
    package_id INT DEFAULT NULL,
    amount_lkr DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    credits_added INT NOT NULL,
    payment_method VARCHAR(100) NOT NULL,
    status ENUM('COMPLETED', 'PENDING', 'FAILED') DEFAULT 'COMPLETED',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (package_id) REFERENCES credit_packages(package_id) ON DELETE SET NULL
);

-- ========================================================
-- Sample Initial Data Seeding
-- ========================================================

-- Seed Credit Packages
INSERT INTO credit_packages (package_id, name, price_lkr, credit_amount, bonus_credits, is_active) VALUES
(1, 'Starter Pack', 250.00, 10, 0, TRUE),
(2, 'Fan Pack', 1000.00, 50, 5, TRUE),
(3, 'Super Fan Pack', 2500.00, 150, 25, TRUE),
(4, 'Mega Producer Pack', 5000.00, 300, 60, TRUE)
ON DUPLICATE KEY UPDATE 
    name=VALUES(name), price_lkr=VALUES(price_lkr), credit_amount=VALUES(credit_amount), bonus_credits=VALUES(bonus_credits);

-- Seed Sample User (if not already existing in users table)
INSERT INTO users (user_id, full_name, email, password_hash, role) VALUES
(1, 'Kasun Perera', 'kasun@votesphere.lk', 'hashed_pass_123', 'PUBLIC_VOTER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- Seed User Wallet Balance for User #1
INSERT INTO user_credits (user_id, balance) VALUES
(1, 80)
ON DUPLICATE KEY UPDATE balance=VALUES(balance);

-- Seed Initial Sample Transactions
INSERT INTO transactions (transaction_id, user_id, package_id, amount_lkr, credits_added, payment_method, status, transaction_date) VALUES
('VSL-TXN-9842105', 1, 2, 1000.00, 55, 'Visa ending in 8892', 'COMPLETED', '2026-09-14 13:24:57'),
('VSL-TXN-8723910', 1, 3, 2500.00, 175, 'eZ Cash (0779842105)', 'COMPLETED', '2026-09-07 19:40:12'),
('VSL-TXN-7612049', 1, 1, 250.00, 10, 'Mastercard ending in 1102', 'COMPLETED', '2026-08-25 14:10:05')
ON DUPLICATE KEY UPDATE status=VALUES(status);
