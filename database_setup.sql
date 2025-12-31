-- ============================================================================
-- University Student Management System - Database Setup Script
-- ============================================================================
-- 
-- This SQL script creates the database and tables required for the USMS
-- web application. Run this script in MySQL to set up the database.
--
-- Prerequisites:
-- 1. MySQL Server must be running (XAMPP MySQL)
-- 2. Create database named: studentsmanagementsystem
--
-- Usage:
-- 1. Open phpMyAdmin (http://localhost/phpmyadmin)
-- 2. Click on "studentsmanagementsystem" database
-- 3. Click on the SQL tab
-- 4. Copy and paste this entire script
-- 5. Click "Go" to execute
--
-- ============================================================================

-- ============================================================================
-- STEP 1: Create Students Table
-- ============================================================================

CREATE TABLE IF NOT EXISTS students (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    course VARCHAR(50) NOT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    address TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- STEP 2: Insert Sample Data
-- ============================================================================

INSERT INTO students (name, email, course, phone, address) VALUES
('Muhammad Kashan Tariq', 'kashan@example.com', 'Software Engineering', '0301-2345678', '123 Main Street'),
('Adeel Hussain', 'adeel@example.com', 'Computer Science', '0302-3456789', '456 Oak Avenue'),
('Syed Abdain', 'abdain@example.com', 'Information Technology', '0303-4567890', '789 Pine Road'),
('Muhammad Tauseef', 'tauseef@example.com', 'Data Science', '0304-5678901', '321 Elm Drive'),
('Ali Ahmed', 'ali.ahmed@example.com', 'Cyber Security', '0305-6789012', '654 Cedar Lane'),
('Fatima Begum', 'fatima.begum@example.com', 'Artificial Intelligence', '0306-7890123', '987 Birch Street'),
('Omar Khalid', 'omar.khalid@example.com', 'Business Administration', '0307-8901234', '147 Maple Avenue'),
('Aisha Khan', 'aisha.khan@example.com', 'Mathematics', '0308-9012345', '258 Walnut Road'),
('Hassan Ali', 'hassan.ali@example.com', 'Physics', '0309-0123456', '369 Cherry Lane'),
('Zara Ahmed', 'zara.ahmed@example.com', 'Software Engineering', '0310-1234567', '741 Spruce Court');

-- ============================================================================
-- STEP 3: Verify Setup
-- ============================================================================

SELECT * FROM students;

-- ============================================================================
-- END OF DATABASE SETUP SCRIPT
-- ============================================================================
