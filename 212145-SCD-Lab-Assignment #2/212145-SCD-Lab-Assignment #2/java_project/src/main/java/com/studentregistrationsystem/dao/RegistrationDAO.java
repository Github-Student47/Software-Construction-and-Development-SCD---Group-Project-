package com.studentregistrationsystem.dao;

import com.studentregistrationsystem.model.Registration;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RegistrationDAO (Data Access Object)
 * Handles all database operations for Registration entities
 * Manages the relationship between students and courses
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class RegistrationDAO {
    
    // Database connection configuration
    private static final String URL = "jdbc:mysql://localhost:3306/student_registration";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    // SQL queries for CRUD operations
    private static final String INSERT_REGISTRATION = 
        "INSERT INTO registrations (student_id, course_id, semester, year, status, notes, registration_fee, fee_paid, attendance, mid_term_completed, final_exam_completed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_REGISTRATION_BY_ID = 
        "SELECT * FROM registrations WHERE registration_id = ?";
    
    private static final String SELECT_REGISTRATION_BY_STUDENT_COURSE = 
        "SELECT * FROM registrations WHERE student_id = ? AND course_id = ? AND semester = ? AND year = ? ORDER BY registration_date DESC LIMIT 1";
    
    private static final String SELECT_REGISTRATIONS_BY_STUDENT = 
        "SELECT * FROM registrations WHERE student_id = ? ORDER BY registration_date DESC";
    
    private static final String SELECT_REGISTRATIONS_BY_COURSE = 
        "SELECT * FROM registrations WHERE course_id = ? ORDER BY registration_date DESC";
    
    private static final String SELECT_ALL_REGISTRATIONS = 
        "SELECT * FROM registrations ORDER BY registration_date DESC";
    
    private static final String UPDATE_REGISTRATION = 
        "UPDATE registrations SET status = ?, notes = ?, registration_fee = ?, fee_paid = ?, payment_date = ?, attendance = ?, mid_term_completed = ?, final_exam_completed = ?, completed_date = ?, last_modified_date = ? WHERE registration_id = ?";
    
    private static final String DELETE_REGISTRATION = 
        "DELETE FROM registrations WHERE registration_id = ?";
    
    private static final String COUNT_REGISTRATIONS = 
        "SELECT COUNT(*) FROM registrations";
    
    private static final String FIND_REGISTRATIONS_BY_STATUS = 
        "SELECT * FROM registrations WHERE status = ? ORDER BY registration_date DESC";
    
    private static final String FIND_REGISTRATIONS_BY_SEMESTER = 
        "SELECT * FROM registrations WHERE semester = ? AND year = ? ORDER BY registration_date DESC";
    
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
     * Save Registration
     * Inserts a new registration into the database
     * 
     * @param registration Registration object to save
     * @return true if save successful
     * @throws SQLException if database operation fails
     */
    public boolean save(Registration registration) throws SQLException {
        if (registration.getStudent() == null || registration.getCourse() == null) {
            throw new SQLException("Student and Course are required for registration");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_REGISTRATION, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, registration.getStudent().getId());
            stmt.setString(2, registration.getCourse().getCourseId());
            stmt.setString(3, registration.getSemester());
            stmt.setInt(4, registration.getYear());
            stmt.setString(5, registration.getStatus());
            stmt.setString(6, registration.getNotes());
            stmt.setDouble(7, registration.getRegistrationFee());
            stmt.setBoolean(8, registration.isFeePaid());
            stmt.setInt(9, registration.getAttendance());
            stmt.setBoolean(10, registration.isMidTermCompleted());
            stmt.setBoolean(11, registration.isFinalExamCompleted());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating registration failed, no rows affected.");
            }
            
            // Get generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    registration.setRegistrationId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Registration saved successfully with ID: " + registration.getRegistrationId());
            return true;
        }
    }
    
    /**
     * Find Registration by ID
     * Retrieves a registration by its primary key
     * 
     * @param id Registration ID to search for
     * @return Registration object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Registration findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_REGISTRATION_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find Registration by Student and Course
     * Retrieves registration for a specific student-course combination
     * 
     * @param studentId Student ID to search for
     * @param courseId Course ID to search for
     * @param semester Semester to search for
     * @param year Year to search for
     * @return Registration object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Registration findByStudentAndCourse(int studentId, String courseId, String semester, int year) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_REGISTRATION_BY_STUDENT_COURSE)) {
            
            stmt.setInt(1, studentId);
            stmt.setString(2, courseId);
            stmt.setString(3, semester);
            stmt.setInt(4, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find Registrations by Student
     * Retrieves all registrations for a specific student
     * 
     * @param studentId Student ID to search for
     * @return List of Registration objects for the student
     * @throws SQLException if database operation fails
     */
    public List<Registration> findByStudentId(int studentId) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_REGISTRATIONS_BY_STUDENT)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        
        return registrations;
    }
    
    /**
     * Find Registrations by Course
     * Retrieves all registrations for a specific course
     * 
     * @param courseId Course ID to search for
     * @return List of Registration objects for the course
     * @throws SQLException if database operation fails
     */
    public List<Registration> findByCourseId(String courseId) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_REGISTRATIONS_BY_COURSE)) {
            
            stmt.setString(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        
        return registrations;
    }
    
    /**
     * Find All Registrations
     * Retrieves all registrations from the database
     * 
     * @return List of all Registration objects
     * @throws SQLException if database operation fails
     */
    public List<Registration> findAll() throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_REGISTRATIONS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
        }
        
        return registrations;
    }
    
    /**
     * Update Registration
     * Updates an existing registration in the database
     * 
     * @param registration Registration object to update
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean update(Registration registration) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_REGISTRATION)) {
            
            stmt.setString(1, registration.getStatus());
            stmt.setString(2, registration.getNotes());
            stmt.setDouble(3, registration.getRegistrationFee());
            stmt.setBoolean(4, registration.isFeePaid());
            
            Timestamp paymentDate = registration.getPaymentDate();
            stmt.setTimestamp(5, paymentDate != null ? Timestamp.valueOf(paymentDate) : null);
            
            stmt.setInt(6, registration.getAttendance());
            stmt.setBoolean(7, registration.isMidTermCompleted());
            stmt.setBoolean(8, registration.isFinalExamCompleted());
            
            Timestamp completedDate = registration.getCompletedDate();
            stmt.setTimestamp(9, completedDate != null ? Timestamp.valueOf(completedDate) : null);
            
            stmt.setTimestamp(10, Timestamp.valueOf(registration.getLastModifiedDate()));
            stmt.setInt(11, registration.getRegistrationId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating registration failed, no rows affected.");
            }
            
            System.out.println("Registration updated successfully: " + registration.getRegistrationId());
            return true;
        }
    }
    
    /**
     * Drop Registration
     * Changes the status of a registration to 'Dropped'
     * 
     * @param registration Registration object to drop
     * @return true if drop successful
     * @throws SQLException if database operation fails
     */
    public boolean drop(Registration registration) throws SQLException {
        registration.setStatus("Dropped");
        registration.setLastModifiedDate(LocalDateTime.now());
        return update(registration);
    }
    
    /**
     * Complete Registration
     * Changes the status of a registration to 'Completed'
     * 
     * @param registration Registration object to complete
     * @return true if completion successful
     * @throws SQLException if database operation fails
     */
    public boolean complete(Registration registration) throws SQLException {
        registration.setStatus("Completed");
        registration.setCompletedDate(LocalDateTime.now());
        registration.setLastModifiedDate(LocalDateTime.now());
        return update(registration);
    }
    
    /**
     * Delete Registration
     * Removes a registration from the database
     * 
     * @param id Registration ID to delete
     * @throws SQLException if database operation fails
     */
    public void delete(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_REGISTRATION)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting registration failed, no rows affected.");
            }
            
            System.out.println("Registration deleted successfully: " + id);
        }
    }
    
    /**
     * Count Registrations
     * Returns the total number of registrations in the database
     * 
     * @return int representing registration count
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_REGISTRATIONS);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Find Registrations by Status
     * Retrieves registrations by their status
     * 
     * @param status Status to filter by (Active, Dropped, Completed)
     * @return List of Registration objects with the specified status
     * @throws SQLException if database operation fails
     */
    public List<Registration> findByStatus(String status) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_REGISTRATIONS_BY_STATUS)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        
        return registrations;
    }
    
    /**
     * Find Registrations by Semester
     * Retrieves registrations for a specific semester and year
     * 
     * @param semester Semester to search for (e.g., "Fall", "Spring")
     * @param year Year to search for
     * @return List of Registration objects for the specified semester
     * @throws SQLException if database operation fails
     */
    public List<Registration> findBySemester(String semester, int year) throws SQLException {
        List<Registration> registrations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_REGISTRATIONS_BY_SEMESTER)) {
            
            stmt.setString(1, semester);
            stmt.setInt(2, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        
        return registrations;
    }
    
    /**
     * Process Payment
     * Updates registration to mark fee as paid
     * 
     * @param registrationId Registration ID to process payment for
     * @param amount Amount paid
     * @return true if payment processed successfully
     * @throws SQLException if database operation fails
     */
    public boolean processPayment(int registrationId, double amount) throws SQLException {
        Registration registration = findById(registrationId);
        if (registration == null) {
            throw new SQLException("Registration not found: " + registrationId);
        }
        
        if (amount >= registration.getRegistrationFee()) {
            registration.setFeePaid(true);
            registration.setPaymentDate(LocalDateTime.now());
            registration.setLastModifiedDate(LocalDateTime.now());
            return update(registration);
        }
        
        return false;
    }
    
    /**
     * Update Attendance
     * Updates attendance for a registration
     * 
     * @param registrationId Registration ID to update
     * @param attendance New attendance count
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean updateAttendance(int registrationId, int attendance) throws SQLException {
        Registration registration = findById(registrationId);
        if (registration == null) {
            throw new SQLException("Registration not found: " + registrationId);
        }
        
        registration.setAttendance(attendance);
        registration.setLastModifiedDate(LocalDateTime.now());
        return update(registration);
    }
    
    /**
     * Mark Milestones
     * Updates milestone completion status
     * 
     * @param registrationId Registration ID to update
     * @param midTermComplete Whether mid-term is complete
     * @param finalExamComplete Whether final exam is complete
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    public boolean markMilestones(int registrationId, boolean midTermComplete, boolean finalExamComplete) throws SQLException {
        Registration registration = findById(registrationId);
        if (registration == null) {
            throw new SQLException("Registration not found: " + registrationId);
        }
        
        registration.setMidTermCompleted(midTermComplete);
        registration.setFinalExamCompleted(finalExamComplete);
        registration.setLastModifiedDate(LocalDateTime.now());
        return update(registration);
    }
    
    /**
     * Get Registration Statistics
     * Returns statistics about registrations
     * 
     * @return String containing registration statistics
     * @throws SQLException if database operation fails
     */
    public String getRegistrationStatistics() throws SQLException {
        StringBuilder stats = new StringBuilder();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_registrations, " +
                    "COUNT(CASE WHEN status = 'Active' THEN 1 END) as active_registrations, " +
                    "COUNT(CASE WHEN status = 'Dropped' THEN 1 END) as dropped_registrations, " +
                    "COUNT(CASE WHEN status = 'Completed' THEN 1 END) as completed_registrations, " +
                    "COUNT(CASE WHEN fee_paid = 1 THEN 1 END) as paid_registrations, " +
                    "SUM(CASE WHEN fee_paid = 1 THEN registration_fee ELSE 0 END) as total_revenue " +
                    "FROM registrations";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                stats.append("Registration Statistics:\n");
                stats.append("Total Registrations: ").append(rs.getInt("total_registrations")).append("\n");
                stats.append("Active: ").append(rs.getInt("active_registrations")).append("\n");
                stats.append("Dropped: ").append(rs.getInt("dropped_registrations")).append("\n");
                stats.append("Completed: ").append(rs.getInt("completed_registrations")).append("\n");
                stats.append("Paid Registrations: ").append(rs.getInt("paid_registrations")).append("\n");
                stats.append("Total Revenue: $").append(String.format("%.2f", rs.getDouble("total_revenue"))).append("\n");
            }
        }
        
        return stats.toString();
    }
    
    /**
     * Map Result Set to Registration Object
     * Helper method to convert database result set to Registration model object
     * 
     * @param rs ResultSet containing registration data
     * @return Registration object populated with data
     * @throws SQLException if mapping fails
     */
    private Registration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        Registration registration = new Registration();
        
        registration.setRegistrationId(rs.getInt("registration_id"));
        registration.setStudentId(rs.getInt("student_id"));
        registration.setCourseId(rs.getString("course_id"));
        registration.setStatus(rs.getString("status"));
        registration.setSemester(rs.getString("semester"));
        registration.setYear(rs.getInt("year"));
        registration.setNotes(rs.getString("notes"));
        registration.setRegistrationFee(rs.getDouble("registration_fee"));
        registration.setFeePaid(rs.getBoolean("fee_paid"));
        registration.setAttendance(rs.getInt("attendance"));
        registration.setMidTermCompleted(rs.getBoolean("mid_term_completed"));
        registration.setFinalExamCompleted(rs.getBoolean("final_exam_completed"));
        registration.setIsFinal(rs.getBoolean("is_final")); // If this field exists
        
        Timestamp registrationDate = rs.getTimestamp("registration_date");
        if (registrationDate != null) {
            registration.setRegistrationDate(registrationDate.toLocalDateTime());
        }
        
        Timestamp lastModifiedDate = rs.getTimestamp("last_modified_date");
        if (lastModifiedDate != null) {
            registration.setLastModifiedDate(lastModifiedDate.toLocalDateTime());
        }
        
        Timestamp paymentDate = rs.getTimestamp("payment_date");
        if (paymentDate != null) {
            registration.setPaymentDate(paymentDate.toLocalDateTime());
        }
        
        Timestamp completedDate = rs.getTimestamp("completed_date");
        if (completedDate != null) {
            registration.setCompletedDate(completedDate.toLocalDateTime());
        }
        
        return registration;
    }
    
    /**
     * Validate Registration Data
     * Validates registration data before database operations
     * 
     * @param registration Registration object to validate
     * @return List of validation error messages (empty if valid)
     */
    public List<String> validateRegistration(Registration registration) {
        List<String> errors = new ArrayList<>();
        
        if (registration.getStudentId() <= 0) {
            errors.add("Valid student ID is required");
        }
        
        if (registration.getCourseId() == null || registration.getCourseId().trim().isEmpty()) {
            errors.add("Course ID is required");
        }
        
        if (registration.getSemester() == null || registration.getSemester().trim().isEmpty()) {
            errors.add("Semester is required");
        }
        
        if (registration.getYear() <= 2020 || registration.getYear() > 2030) {
            errors.add("Valid year is required (2020-2030)");
        }
        
        if (registration.getStatus() == null || registration.getStatus().trim().isEmpty()) {
            errors.add("Status is required");
        }
        
        if (registration.getRegistrationFee() < 0) {
            errors.add("Registration fee cannot be negative");
        }
        
        return errors;
    }
    
    /**
     * Close Connection
     * Cleanup method to close database connections
     * This is handled automatically by try-with-resources blocks
     */
    public void close() {
        // Connection cleanup is handled by try-with-resources in getConnection
        System.out.println("RegistrationDAO cleanup completed");
    }
}