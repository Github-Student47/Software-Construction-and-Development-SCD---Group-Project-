package com.studentregistrationsystem.dao;

import com.studentregistrationsystem.model.Student;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO (Data Access Object)
 * Handles all database operations for Student entities
 * Implements CRUD operations using JDBC for MySQL database
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class StudentDAO {
    
    // Database connection configuration
    private static final String URL = "jdbc:mysql://localhost:3306/student_registration";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    // SQL queries for CRUD operations
    private static final String INSERT_STUDENT = 
        "INSERT INTO students (name, email, student_number, program, year_level, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_STUDENT_BY_ID = 
        "SELECT * FROM students WHERE id = ?";
    
    private static final String SELECT_STUDENT_BY_NUMBER = 
        "SELECT * FROM students WHERE student_number = ?";
    
    private static final String SELECT_ALL_STUDENTS = 
        "SELECT * FROM students ORDER BY name";
    
    private static final String UPDATE_STUDENT = 
        "UPDATE students SET name = ?, email = ?, student_number = ?, program = ?, year_level = ?, is_active = ?, updated_at = ? WHERE id = ?";
    
    private static final String DELETE_STUDENT = 
        "DELETE FROM students WHERE id = ?";
    
    private static final String COUNT_STUDENTS = 
        "SELECT COUNT(*) FROM students";
    
    private static final String FIND_STUDENTS_BY_PROGRAM = 
        "SELECT * FROM students WHERE program = ? ORDER BY name";
    
    private static final String FIND_STUDENTS_BY_YEAR_LEVEL = 
        "SELECT * FROM students WHERE year_level = ? ORDER BY name";
    
    private static final String SEARCH_STUDENTS = 
        "SELECT * FROM students WHERE name LIKE ? OR email LIKE ? OR student_number LIKE ? ORDER BY name";
    
    /**
     * Get database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Save Student
     * Inserts a new student into the database
     * 
     * @param student Student object to save
     * @throws SQLException if database operation fails
     */
    public void save(Student student) throws SQLException {
        if (student.getId() == 0) {
            // Insert new student
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(INSERT_STUDENT, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, student.getName());
                stmt.setString(2, student.getEmail());
                stmt.setString(3, student.getStudentNumber());
                stmt.setString(4, student.getProgram());
                stmt.setInt(5, student.getYearLevel());
                stmt.setBoolean(6, student.isActive());
                stmt.setTimestamp(7, Timestamp.valueOf(student.getCreatedAt()));
                stmt.setTimestamp(8, Timestamp.valueOf(student.getUpdatedAt()));
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating student failed, no rows affected.");
                }
                
                // Get generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getInt(1));
                    }
                }
                
                System.out.println("Student saved successfully with ID: " + student.getId());
            }
        } else {
            // Update existing student
            update(student);
        }
    }
    
    /**
     * Find Student by ID
     * Retrieves a student by their primary key
     * 
     * @param id Student ID to search for
     * @return Student object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Student findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_STUDENT_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find Student by Student Number
     * Retrieves a student by their unique student number
     * 
     * @param studentNumber Student number to search for
     * @return Student object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Student findByStudentNumber(String studentNumber) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_STUDENT_BY_NUMBER)) {
            
            stmt.setString(1, studentNumber);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find All Students
     * Retrieves all students from the database
     * 
     * @return List of all Student objects
     * @throws SQLException if database operation fails
     */
    public List<Student> findAll() throws SQLException {
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_STUDENTS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        
        return students;
    }
    
    /**
     * Update Student
     * Updates an existing student in the database
     * 
     * @param student Student object to update
     * @throws SQLException if database operation fails
     */
    public void update(Student student) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_STUDENT)) {
            
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getEmail());
            stmt.setString(3, student.getStudentNumber());
            stmt.setString(4, student.getProgram());
            stmt.setInt(5, student.getYearLevel());
            stmt.setBoolean(6, student.isActive());
            stmt.setTimestamp(7, Timestamp.valueOf(student.getUpdatedAt()));
            stmt.setInt(8, student.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating student failed, no rows affected.");
            }
            
            System.out.println("Student updated successfully: " + student.getId());
        }
    }
    
    /**
     * Delete Student
     * Removes a student from the database
     * 
     * @param id Student ID to delete
     * @throws SQLException if database operation fails
     */
    public void delete(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_STUDENT)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting student failed, no rows affected.");
            }
            
            System.out.println("Student deleted successfully: " + id);
        }
    }
    
    /**
     * Count Students
     * Returns the total number of students in the database
     * 
     * @return int representing student count
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_STUDENTS);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Find Students by Program
     * Retrieves students by their academic program
     * 
     * @param program Program to search for
     * @return List of Student objects in the specified program
     * @throws SQLException if database operation fails
     */
    public List<Student> findByProgram(String program) throws SQLException {
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_STUDENTS_BY_PROGRAM)) {
            
            stmt.setString(1, program);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        
        return students;
    }
    
    /**
     * Find Students by Year Level
     * Retrieves students by their year level
     * 
     * @param yearLevel Year level to search for
     * @return List of Student objects in the specified year level
     * @throws SQLException if database operation fails
     */
    public List<Student> findByYearLevel(int yearLevel) throws SQLException {
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_STUDENTS_BY_YEAR_LEVEL)) {
            
            stmt.setInt(1, yearLevel);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        
        return students;
    }
    
    /**
     * Search Students
     * Searches students by name, email, or student number
     * 
     * @param searchTerm Search term to look for
     * @return List of matching Student objects
     * @throws SQLException if database operation fails
     */
    public List<Student> search(String searchTerm) throws SQLException {
        List<Student> students = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_STUDENTS)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        
        return students;
    }
    
    /**
     * Map Result Set to Student Object
     * Helper method to convert database result set to Student model object
     * 
     * @param rs ResultSet containing student data
     * @return Student object populated with data
     * @throws SQLException if mapping fails
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        
        student.setId(rs.getInt("id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setStudentNumber(rs.getString("student_number"));
        student.setProgram(rs.getString("program"));
        student.setYearLevel(rs.getInt("year_level"));
        student.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            student.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return student;
    }
    
    /**
     * Validate Student Data
     * Validates student data before database operations
     * 
     * @param student Student object to validate
     * @return List of validation error messages (empty if valid)
     */
    public List<String> validateStudent(Student student) {
        List<String> errors = new ArrayList<>();
        
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            errors.add("Student name is required");
        }
        
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(student.getEmail())) {
            errors.add("Invalid email format");
        }
        
        if (student.getStudentNumber() == null || student.getStudentNumber().trim().isEmpty()) {
            errors.add("Student number is required");
        }
        
        if (student.getProgram() == null || student.getProgram().trim().isEmpty()) {
            errors.add("Program is required");
        }
        
        if (student.getYearLevel() < 1 || student.getYearLevel() > 8) {
            errors.add("Year level must be between 1 and 8");
        }
        
        return errors;
    }
    
    /**
     * Email Validation Helper
     * Simple email format validation
     * 
     * @param email Email to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Get Students with Pagination
     * Retrieves students with pagination support
     * 
     * @param page Page number (starting from 1)
     * @param pageSize Number of records per page
     * @return List of Student objects for the specified page
     * @throws SQLException if database operation fails
     */
    public List<Student> findWithPagination(int page, int pageSize) throws SQLException {
        List<Student> students = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT * FROM students ORDER BY name LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        
        return students;
    }
    
    /**
     * Close Connection
     * Cleanup method to close database connections
     * This is handled automatically by try-with-resources blocks
     */
    public void close() {
        // Connection cleanup is handled by try-with-resources in getConnection
        System.out.println("StudentDAO cleanup completed");
    }
}