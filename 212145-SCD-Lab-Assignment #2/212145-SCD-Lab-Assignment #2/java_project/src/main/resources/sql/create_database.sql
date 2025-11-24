-- Student Registration System Database Setup
-- MySQL Database Schema for Student Course Registration System
-- Author: Muhammad Kashan Tariq
-- Created: 2025-10-27
-- Version: 1.0

-- Create database
CREATE DATABASE IF NOT EXISTS student_registration 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE student_registration;

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS grades;
DROP TABLE IF EXISTS registrations;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS students;

-- Create Students table
CREATE TABLE students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    student_number VARCHAR(50) UNIQUE NOT NULL,
    program VARCHAR(255) NOT NULL,
    year_level INT NOT NULL CHECK (year_level BETWEEN 1 AND 8),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_student_number (student_number),
    INDEX idx_email (email),
    INDEX idx_program (program),
    INDEX idx_year_level (year_level),
    INDEX idx_active (is_active)
);

-- Create Courses table
CREATE TABLE courses (
    course_id VARCHAR(50) PRIMARY KEY,
    course_name VARCHAR(255) NOT NULL,
    description TEXT,
    credits INT NOT NULL DEFAULT 3 CHECK (credits BETWEEN 1 AND 10),
    max_capacity INT NOT NULL DEFAULT 30 CHECK (max_capacity > 0),
    current_enrollment INT DEFAULT 0 CHECK (current_enrollment >= 0),
    department VARCHAR(255),
    prerequisites TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_course_name (course_name),
    INDEX idx_department (department),
    INDEX idx_credits (credits),
    INDEX idx_active (is_active),
    INDEX idx_enrollment (current_enrollment, max_capacity)
);

-- Create Registrations table (Many-to-Many relationship)
CREATE TABLE registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id VARCHAR(50) NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('Active', 'Dropped', 'Completed') DEFAULT 'Active',
    semester VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    notes TEXT,
    registration_fee DECIMAL(10,2) DEFAULT 0.00,
    fee_paid BOOLEAN DEFAULT FALSE,
    payment_date TIMESTAMP NULL,
    attendance INT DEFAULT 0,
    mid_term_completed BOOLEAN DEFAULT FALSE,
    final_exam_completed BOOLEAN DEFAULT FALSE,
    completed_date TIMESTAMP NULL,
    
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_status (status),
    INDEX idx_semester_year (semester, year),
    INDEX idx_registration_date (registration_date),
    UNIQUE KEY unique_student_course_semester (student_id, course_id, semester, year)
);

-- Create Users table for authentication
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL, -- Should be hashed in production
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role ENUM('Admin', 'Faculty', 'Student', 'Staff') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    failed_login_attempts INT DEFAULT 0,
    last_failed_login TIMESTAMP NULL,
    is_locked BOOLEAN DEFAULT FALSE,
    lock_expiration TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    password_last_changed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    login_count INT DEFAULT 0,
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active),
    INDEX idx_locked (is_locked)
);

-- Create Grades table (New feature for academic performance tracking)
CREATE TABLE grades (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_id VARCHAR(50) NOT NULL,
    score DECIMAL(5,2) NOT NULL CHECK (score >= 0 AND score <= 100),
    letter_grade CHAR(1) NOT NULL CHECK (letter_grade IN ('A', 'B', 'C', 'D', 'F')),
    quality_points DECIMAL(3,2) NOT NULL CHECK (quality_points BETWEEN 0.0 AND 4.0),
    grade_type ENUM('MidTerm', 'Final', 'Assignment', 'Quiz', 'Project') NOT NULL,
    semester VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    grade_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    graded_by VARCHAR(255),
    comments TEXT,
    is_final BOOLEAN DEFAULT FALSE,
    assignment_score DECIMAL(5,2) DEFAULT 0.00,
    quiz_score DECIMAL(5,2) DEFAULT 0.00,
    midterm_score DECIMAL(5,2) DEFAULT 0.00,
    final_exam_score DECIMAL(5,2) DEFAULT 0.00,
    participation_score DECIMAL(5,2) DEFAULT 0.00,
    total_possible_score DECIMAL(5,2) DEFAULT 100.00,
    is_published BOOLEAN DEFAULT FALSE,
    published_date TIMESTAMP NULL,
    is_appealed BOOLEAN DEFAULT FALSE,
    appeal_comments TEXT,
    
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_letter_grade (letter_grade),
    INDEX idx_grade_type (grade_type),
    INDEX idx_semester_year (semester, year),
    INDEX idx_final_grade (is_final),
    INDEX idx_published (is_published),
    INDEX idx_appealed (is_appealed),
    UNIQUE KEY unique_student_course_final (student_id, course_id, is_final) -- Only one final grade per student per course
);

-- Insert sample data

-- Insert sample students
INSERT INTO students (name, email, student_number, program, year_level) VALUES
('Muhammad Kashan Tariq', 'muhammad.kashan@student.edu', '212145', 'Computer Science', 3),
('John Smith', 'john.smith@student.edu', '210001', 'Computer Science', 4),
('Jane Doe', 'jane.doe@student.edu', '210002', 'Information Technology', 3),
('Alice Johnson', 'alice.johnson@student.edu', '210003', 'Software Engineering', 2),
('Bob Wilson', 'bob.wilson@student.edu', '210004', 'Computer Science', 1),
('Carol Brown', 'carol.brown@student.edu', '210005', 'Information Technology', 4),
('David Lee', 'david.lee@student.edu', '210006', 'Computer Science', 2),
('Emma Davis', 'emma.davis@student.edu', '210007', 'Software Engineering', 3);

-- Insert sample courses
INSERT INTO courses (course_id, course_name, description, credits, max_capacity, department, prerequisites) VALUES
('CS101', 'Introduction to Programming', 'Basic programming concepts using Java', 3, 30, 'Computer Science', 'None'),
('CS201', 'Data Structures', 'Fundamental data structures and algorithms', 4, 25, 'Computer Science', 'CS101'),
('CS301', 'Software Engineering', 'Software development methodologies and practices', 3, 20, 'Computer Science', 'CS201'),
('CS401', 'Database Systems', 'Database design and management', 4, 25, 'Computer Science', 'CS201'),
('CS405', 'Web Development', 'Frontend and backend web development', 3, 30, 'Computer Science', 'CS201'),
('IT101', 'Information Technology Fundamentals', 'Basic IT concepts and applications', 3, 35, 'Information Technology', 'None'),
('IT202', 'Network Fundamentals', 'Computer networking concepts', 4, 25, 'Information Technology', 'IT101'),
('SE201', 'Software Design Patterns', 'Design patterns and software architecture', 3, 20, 'Software Engineering', 'CS101');

-- Insert sample users
INSERT INTO users (username, password, email, first_name, last_name, role, is_verified) VALUES
('admin', 'admin123', 'admin@university.edu', 'System', 'Administrator', 'Admin', TRUE),
('faculty1', 'faculty123', 'faculty@university.edu', 'Dr. Michael', 'Johnson', 'Faculty', TRUE),
('faculty2', 'faculty123', 'faculty2@university.edu', 'Dr. Sarah', 'Williams', 'Faculty', TRUE),
('kashan212145', 'student123', 'muhammad.kashan@student.edu', 'Muhammad Kashan', 'Tariq', 'Student', TRUE),
('john210001', 'student123', 'john.smith@student.edu', 'John', 'Smith', 'Student', TRUE),
('jane210002', 'student123', 'jane.doe@student.edu', 'Jane', 'Doe', 'Student', TRUE),
('staff1', 'staff123', 'staff@university.edu', 'Mary', 'Taylor', 'Staff', TRUE);

-- Insert sample registrations
INSERT INTO registrations (student_id, course_id, semester, year, status, fee_paid, payment_date) VALUES
(1, 'CS101', 'Fall', 2025, 'Active', TRUE, '2025-08-15 10:30:00'),
(1, 'CS201', 'Fall', 2025, 'Active', TRUE, '2025-08-15 10:35:00'),
(2, 'CS101', 'Fall', 2025, 'Active', TRUE, '2025-08-16 09:15:00'),
(2, 'CS201', 'Fall', 2025, 'Active', TRUE, '2025-08-16 09:20:00'),
(2, 'CS301', 'Fall', 2025, 'Active', TRUE, '2025-08-16 09:25:00'),
(3, 'IT101', 'Fall', 2025, 'Active', TRUE, '2025-08-17 11:00:00'),
(3, 'IT202', 'Fall', 2025, 'Active', TRUE, '2025-08-17 11:05:00'),
(4, 'CS101', 'Fall', 2025, 'Active', TRUE, '2025-08-18 14:30:00'),
(4, 'SE201', 'Fall', 2025, 'Active', TRUE, '2025-08-18 14:35:00'),
(5, 'CS101', 'Fall', 2025, 'Completed', TRUE, '2025-01-15 10:00:00'),
(5, 'CS201', 'Fall', 2025, 'Completed', TRUE, '2025-01-15 10:05:00'),
(6, 'IT101', 'Fall', 2025, 'Completed', TRUE, '2025-01-16 11:00:00'),
(6, 'IT202', 'Fall', 2025, 'Completed', TRUE, '2025-01-16 11:05:00'),
(6, 'CS405', 'Fall', 2025, 'Completed', TRUE, '2025-01-16 11:10:00');

-- Update course enrollment counts based on active registrations
UPDATE courses SET current_enrollment = (
    SELECT COUNT(*) 
    FROM registrations 
    WHERE registrations.course_id = courses.course_id 
    AND registrations.status = 'Active'
);

-- Insert sample grades (New feature - Grade management)
INSERT INTO grades (student_id, course_id, score, letter_grade, quality_points, grade_type, semester, year, graded_by, is_final, is_published, published_date) VALUES
(5, 'CS101', 92.5, 'A', 4.00, 'Final', 'Fall', 2024, 'Dr. Michael Johnson', TRUE, TRUE, '2024-12-15 16:00:00'),
(5, 'CS201', 88.0, 'B', 3.00, 'Final', 'Fall', 2024, 'Dr. Michael Johnson', TRUE, TRUE, '2024-12-15 16:05:00'),
(6, 'IT101', 85.5, 'B', 3.00, 'Final', 'Fall', 2024, 'Dr. Sarah Williams', TRUE, TRUE, '2024-12-16 14:00:00'),
(6, 'IT202', 90.0, 'A', 4.00, 'Final', 'Fall', 2024, 'Dr. Sarah Williams', TRUE, TRUE, '2024-12-16 14:05:00'),
(6, 'CS405', 87.5, 'B', 3.00, 'Final', 'Fall', 2024, 'Dr. Sarah Williams', TRUE, TRUE, '2024-12-16 14:10:00'),
(1, 'CS101', 95.0, 'A', 4.00, 'MidTerm', 'Fall', 2025, 'Dr. Michael Johnson', FALSE, FALSE, NULL),
(2, 'CS101', 88.5, 'B', 3.00, 'MidTerm', 'Fall', 2025, 'Dr. Michael Johnson', FALSE, FALSE, NULL),
(2, 'CS301', 92.0, 'A', 4.00, 'Assignment', 'Fall', 2025, 'Dr. Michael Johnson', FALSE, FALSE, NULL),
(3, 'IT101', 89.0, 'B', 3.00, 'Quiz', 'Fall', 2025, 'Dr. Sarah Williams', FALSE, FALSE, NULL),
(4, 'SE201', 91.5, 'A', 4.00, 'Project', 'Fall', 2025, 'Dr. Sarah Williams', FALSE, FALSE, NULL);

-- Create views for common queries

-- View for Student Summary with GPA
CREATE VIEW student_summary AS
SELECT 
    s.id,
    s.name,
    s.student_number,
    s.program,
    s.year_level,
    s.email,
    COUNT(r.registration_id) as total_registrations,
    COUNT(CASE WHEN r.status = 'Active' THEN 1 END) as active_courses,
    COUNT(CASE WHEN r.status = 'Completed' THEN 1 END) as completed_courses,
    IFNULL(AVG(g.quality_points), 0.0) as current_gpa,
    MAX(g.grade_date) as last_grade_date
FROM students s
LEFT JOIN registrations r ON s.id = r.student_id
LEFT JOIN grades g ON s.id = g.student_id AND g.is_final = 1
WHERE s.is_active = TRUE
GROUP BY s.id, s.name, s.student_number, s.program, s.year_level, s.email;

-- View for Course Statistics
CREATE VIEW course_statistics AS
SELECT 
    c.course_id,
    c.course_name,
    c.credits,
    c.max_capacity,
    c.current_enrollment,
    ROUND((c.current_enrollment / c.max_capacity * 100), 1) as enrollment_percentage,
    c.department,
    COUNT(r.registration_id) as total_registrations,
    COUNT(CASE WHEN r.status = 'Active' THEN 1 END) as active_registrations,
    COUNT(CASE WHEN r.status = 'Completed' THEN 1 END) as completed_registrations,
    AVG(g.score) as average_grade,
    COUNT(g.grade_id) as total_grades
FROM courses c
LEFT JOIN registrations r ON c.course_id = r.course_id
LEFT JOIN grades g ON c.course_id = g.course_id
WHERE c.is_active = TRUE
GROUP BY c.course_id, c.course_name, c.credits, c.max_capacity, c.current_enrollment, c.department;

-- View for Grade Report
CREATE VIEW grade_report AS
SELECT 
    g.grade_id,
    s.name as student_name,
    s.student_number,
    c.course_name,
    g.score,
    g.letter_grade,
    g.quality_points,
    g.grade_type,
    g.semester,
    g.year,
    g.grade_date,
    g.graded_by,
    g.is_final,
    g.is_published
FROM grades g
JOIN students s ON g.student_id = s.id
JOIN courses c ON g.course_id = c.course_id
ORDER BY g.grade_date DESC;

-- Create stored procedures

-- Procedure to register student for course
DELIMITER //
CREATE PROCEDURE RegisterStudentForCourse(
    IN p_student_id INT,
    IN p_course_id VARCHAR(50),
    IN p_semester VARCHAR(50),
    IN p_year INT
)
BEGIN
    DECLARE v_course_capacity INT DEFAULT 0;
    DECLARE v_current_enrollment INT DEFAULT 0;
    DECLARE v_registration_fee DECIMAL(10,2) DEFAULT 0;
    DECLARE v_exists INT DEFAULT 0;
    
    -- Check if registration already exists
    SELECT COUNT(*) INTO v_exists
    FROM registrations 
    WHERE student_id = p_student_id 
    AND course_id = p_course_id 
    AND semester = p_semester 
    AND year = p_year 
    AND status = 'Active';
    
    IF v_exists > 0 THEN
        SELECT 'Student already registered for this course' as message;
    ELSE
        -- Get course capacity and enrollment
        SELECT max_capacity, current_enrollment INTO v_course_capacity, v_current_enrollment
        FROM courses WHERE course_id = p_course_id;
        
        IF v_current_enrollment >= v_course_capacity THEN
            SELECT 'Course is full' as message;
        ELSE
            -- Calculate registration fee (example: $100 per credit hour)
            SELECT credits * 100.0 INTO v_registration_fee
            FROM courses WHERE course_id = p_course_id;
            
            -- Insert registration
            INSERT INTO registrations (student_id, course_id, semester, year, registration_fee)
            VALUES (p_student_id, p_course_id, p_semester, p_year, v_registration_fee);
            
            -- Update course enrollment
            UPDATE courses 
            SET current_enrollment = current_enrollment + 1 
            WHERE course_id = p_course_id;
            
            SELECT 'Registration successful' as message;
        END IF;
    END IF;
END //
DELIMITER ;

-- Procedure to calculate student GPA
DELIMITER //
CREATE PROCEDURE CalculateStudentGPA(
    IN p_student_id INT,
    OUT p_gpa DECIMAL(3,2)
)
BEGIN
    SELECT IFNULL(AVG(quality_points), 0.0) INTO p_gpa
    FROM grades 
    WHERE student_id = p_student_id 
    AND is_final = 1;
END //
DELIMITER ;

-- Grant privileges (adjust as needed for your environment)
-- GRANT ALL PRIVILEGES ON student_registration.* TO 'studentapp'@'localhost';
-- FLUSH PRIVILEGES;

-- Create indexes for performance optimization
CREATE INDEX idx_student_search ON students (name, email, student_number);
CREATE INDEX idx_course_search ON courses (course_name, department);
CREATE INDEX idx_grade_performance ON grades (student_id, is_final, letter_grade);
CREATE INDEX idx_registration_active ON registrations (student_id, status, semester, year);

-- Display setup completion message
SELECT 'Student Registration System Database Setup Complete!' as message;