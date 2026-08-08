-- ========================================================
-- SE2030 Software Engineering - Group Y2-S1-MLB-B4G2-04
-- Web-Based Voting System for Reality Show (BrightStar Media)
-- Database Schema Definition (MySQL)
-- ========================================================

CREATE DATABASE IF NOT EXISTS votingsystem_db;
USE votingsystem_db;

-- 1. Users Table (Authentication & RBAC)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('PUBLIC_VOTER', 'SHOW_PRODUCER', 'SECURITY_ADMIN', 'FINANCE_OFFICER') DEFAULT 'PUBLIC_VOTER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Shows Table (Show Season Management)
CREATE TABLE IF NOT EXISTS shows (
    show_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    season_number INT NOT NULL,
    status ENUM('UPCOMING', 'ACTIVE', 'COMPLETED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Episodes Table (Timer-Driven Live Voting Windows)
CREATE TABLE IF NOT EXISTS episodes (
    episode_id INT AUTO_INCREMENT PRIMARY KEY,
    show_id INT NOT NULL,
    episode_number INT NOT NULL,
    title VARCHAR(150),
    voting_start_time DATETIME NOT NULL,
    voting_end_time DATETIME NOT NULL,
    status ENUM('SCHEDULED', 'VOTING_OPEN', 'VOTING_CLOSED', 'COMPLETED') DEFAULT 'SCHEDULED',
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE
);

-- 4. Contestants Table (Profiles & Elimination Status)
CREATE TABLE IF NOT EXISTS contestants (
    contestant_id INT AUTO_INCREMENT PRIMARY KEY,
    show_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    bio TEXT,
    image_url VARCHAR(255),
    status ENUM('ACTIVE', 'SAFE', 'ELIMINATED') DEFAULT 'ACTIVE',
    FOREIGN KEY (show_id) REFERENCES shows(show_id) ON DELETE CASCADE
);

-- 5. Credit Wallets Table (Monetization & Credit Top-ups)
CREATE TABLE IF NOT EXISTS credit_wallets (
    wallet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    balance INT DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 6. Votes Table (Sub-500ms Ingestion Engine & Verification)
CREATE TABLE IF NOT EXISTS votes (
    vote_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    contestant_id INT NOT NULL,
    episode_id INT NOT NULL,
    vote_type ENUM('FREE', 'PAID') DEFAULT 'FREE',
    ip_address VARCHAR(45),
    device_fingerprint VARCHAR(255),
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('ACCEPTED', 'REJECTED', 'FLAGGED') DEFAULT 'ACCEPTED',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (contestant_id) REFERENCES contestants(contestant_id),
    FOREIGN KEY (episode_id) REFERENCES episodes(episode_id)
);

-- 7. Audit Logs Table (Immutable Security & Fraud Prevention Log)
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    actor_user_id INT,
    ip_address VARCHAR(45),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);