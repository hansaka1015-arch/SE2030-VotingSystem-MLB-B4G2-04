-- ========================================================
-- SE2030 Software Engineering - Group Y2-S1-MLB-B4G2-04
-- Web-Based Voting System for Reality Show (BrightStar Media)
-- Database Schema Definition & Seed Data (MySQL)
-- Role: Developer 4 (Frontend/UI & UC-05 Real-Time Analytics)
-- Target: Week 10 Progress Evaluation (75% Target)
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
-- Aligned with com.votingsystem.model.Contestant & ContestantDAO
CREATE TABLE IF NOT EXISTS contestants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    show_id INT NOT NULL,
    contestant_code VARCHAR(20) NOT NULL DEFAULT '#01',
    full_name VARCHAR(100) NOT NULL,
    bio_summary TEXT,
    profile_image_url VARCHAR(255),
    status ENUM('ACTIVE', 'SAFE', 'AT RISK', 'ELIMINATED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
    FOREIGN KEY (contestant_id) REFERENCES contestants(id),
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

-- ========================================================
-- SEED DATA (For Evaluation Viva & Demonstration)
-- ========================================================

-- Insert Show
INSERT INTO shows (show_id, title, description, season_number, status)
VALUES (1, 'Voice of Lanka 2026', 'Premier nationwide television vocal reality competition.', 4, 'ACTIVE')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Insert Live Episode
INSERT INTO episodes (episode_id, show_id, episode_number, title, voting_start_time, voting_end_time, status)
VALUES (1, 1, 8, 'Episode 08: Grand Finals', NOW(), DATE_ADD(NOW(), INTERVAL 3 HOUR), 'VOTING_OPEN')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Insert Show Producer User
INSERT INTO users (user_id, full_name, email, password_hash, role)
VALUES (1, 'Hettiarachchi D.K.S.H.', 'producer@brightstar.media', 'sha256_hash_prod_2026', 'SHOW_PRODUCER')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

-- Insert 5 Official Contestants
INSERT INTO contestants (id, show_id, contestant_code, full_name, bio_summary, profile_image_url, status)
VALUES 
(1, 1, '#01', 'Kavinda Perera', 'Celebrated for breathtaking acoustic renditions and flawless vocal pitch across live performance rounds.', NULL, 'ACTIVE'),
(2, 1, '#02', 'Natasha Fernando', 'A powerhouse vocalist who captivates audiences nationwide with unmatched emotional resonance and dynamic range.', NULL, 'SAFE'),
(3, 1, '#03', 'Sahan Wickramasinghe', 'Infuses high-octane rock energy, electrifying guitar solos, and raw vocal grit into every prime-time broadcast.', NULL, 'AT RISK'),
(4, 1, '#04', 'Aanya Jayasuriya', 'Visionary performer blending modern electronic pop production with traditional percussion and sharp choreography.', NULL, 'ACTIVE'),
(5, 1, '#05', 'Thilina Bandara', 'Brings authentic Sri Lankan folk narrative poetry, smooth acoustic warmth, and classical vocal ornamentation.', NULL, 'ELIMINATED')
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name), profile_image_url=VALUES(profile_image_url), status=VALUES(status);