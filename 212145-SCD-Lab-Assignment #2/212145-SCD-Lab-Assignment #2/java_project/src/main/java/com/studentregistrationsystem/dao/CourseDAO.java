package com.studentregistrationsystem.dao;

import com.studentregistrationsystem.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDAO (Data Access Object)
 * Handles all database operations for Course entities
 * Implements CRUD operations using JDBC for MySQL database
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class CourseDAO {
    
    // Database connection configuration
    private static final String URL = "jdbc:mysql://localhost:3306/student_registration";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    // SQL queries for CRUD operations
    private static final String INSERT_COURSE = 
        "INSERT INTO courses (course_id, course_name, description, credits, max_capacity, department, prerequisites, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_COURSE_BY_ID = 
        "SELECT * FROM courses WHERE course_id = ?";
    
    private static final String SELECT_ALL_COURSES = 
        "SELECT * FROM courses ORDER BY course_name";
    
    private static final String UPDATE_COURSE = 
        "UPDATE courses SET course_name = ?, description = ?, credits = ?, max_capacity = ?, current_enrollment = ?, department = ?, prerequisites = ?, is_active = ?, updated_at = ? WHERE course_id = ?";
    
    private static final String DELETE_COURSE = 
        "DELETE FROM courses WHERE course_id = ?";
    
    private static final String COUNT_COURSES = 
        "SELECT COUNT(*) FROM courses";
    
    private static final String FIND_COURSES_BY_DEPARTMENT = 
        "SELECT * FROM courses WHERE department = ? ORDER BY course_name";
    
    private static final String FIND_COURSES_BY_CREDITS = 
        "SELECT * FROM courses WHERE credits = ? ORDER BY course_name";
    
    private static final String SEARCH_COURSES = 
        "SELECT * FROM courses WHERE course_name LIKE ? OR course_id LIKE ? OR description LIKE ? ORDER BY course_name";
    
    private static final String FIND_COURSES_WITH_SPACE = 
        "SELECT * FROM courses WHERE current_enrollment < max_capacity AND is_active = 1 ORDER BY course_name";
    
    private static final String UPDATE_ENROLLMENT = 
        "UPDATE courses SET current_enrollment = ?, updated_at = ? WHERE course_id = ?";
    
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
     * Save Course
     * Inserts a new course into the database
     * 
     * @param course Course object to save
     * @throws SQLException if database operation fails
     */
    public void save(Course course) throws SQLException {
        if (course.getCourseId() == null || course.getCourseId().trim().isEmpty()) {
            throw new SQLException("Course ID cannot be null or empty");
        }
        
        // Check if course already exists
        if (findById(course.getCourseId()) != null) {
            update(course);
            return;
        }
        
        // Insert new course
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_COURSE)) {
            
            stmt.setString(1, course.getCourseId());
            stmt.setString(2, course.getCourseName());
            stmt.setString(3, course.getDescription());
            stmt.setInt(4, course.getCredits());
            stmt.setInt(5, course.getMaxCapacity());
            stmt.setString(6, course.getDepartment());
            stmt.setString(7, course.getPrerequisites());
            stmt.setBoolean(8, course.isActive());
            stmt.setTimestamp(9, Timestamp.valueOf(course.getCreatedAt()));
            stmt.setTimestamp(10, Timestamp.valueOf(course.getUpdatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }
            
            System.out.println("Course saved successfully: " + course.getCourseId());
        }
    }
    
    /**
     * Find Course by ID
     * Retrieves a course by its unique identifier
     * 
     * @param courseId Course ID to search for
     * @return Course object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Course findById(String courseId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_COURSE_BY_ID)) {
            
            stmt.setString(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find All Courses
     * Retrieves all courses from the database
     * 
     * @return List of all Course objects
     * @throws SQLException if database operation fails
     */
    public List<Course> findAll() throws SQLException {
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_COURSES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }
        
        return courses;
    }
    
    /**
     * Update Course
     * Updates an existing course in the database
     * 
     * @param course Course object to update
     * @throws SQLException if database operation fails
     */
    public void update(Course course) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_COURSE)) {
            
            stmt.setString(1, course.getCourseName());
            stmt.setString(2, course.getDescription());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getMaxCapacity());
            stmt.setInt(5, course.getCurrentEnrollment());
            stmt.setString(6, course.getDepartment());
            stmt.setString(7, course.getPrerequisites());
            stmt.setBoolean(8, course.isActive());
            stmt.setTimestamp(9, Timestamp.valueOf(course.getUpdatedAt()));
            stmt.setString(10, course.getCourseId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating course failed, no rows affected.");
            }
            
            System.out.println("Course updated successfully: " + course.getCourseId());
        }
    }
    
    /**
     * Delete Course
     * Removes a course from the database
     * 
     * @param courseId Course ID to delete
     * @throws SQLException if database operation fails
     */
    public void delete(String courseId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_COURSE)) {
            
            stmt.setString(1, courseId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting course failed, no rows affected.");
            }
            
            System.out.println("Course deleted successfully: " + courseId);
        }
    }
    
    /**
     * Count Courses
     * Returns the total number of courses in the database
     * 
     * @return int representing course count
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_COURSES);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Find Courses by Department
     * Retrieves courses by their academic department
     * 
     * @param department Department to search for
     * @return List of Course objects in the specified department
     * @throws SQLException if database operation fails
     */
    public List<Course> findByDepartment(String department) throws SQLException {
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_COURSES_BY_DEPARTMENT)) {
            
            stmt.setString(1, department);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        }
        
        return courses;
    }
    
    /**
     * Find Courses by Credits
     * Retrieves courses by their credit hours
     * 
     * @param credits Credit hours to search for
     * @return List of Course objects with specified credits
     * @throws SQLException if database operation fails
     */
    public List<Course> findByCredits(int credits) throws SQLException {
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_COURSES_BY_CREDITS)) {
            
            stmt.setInt(1, credits);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        }
        
        return courses;
    }
    
    /**
     * Search Courses
     * Searches courses by name, ID, or description
     * 
     * @param searchTerm Search term to look for
     * @return List of matching Course objects
     * @throws SQLException if database operation fails
     */
    public List<Course> search(String searchTerm) throws SQLException {
        List<Course> courses = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_COURSES)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        }
        
        return courses;
    }
    
    /**
     * Find Courses with Available Space
     * Retrieves courses that have available enrollment capacity
     * 
     * @return List of Course objects with available space
     * @throws SQLException if database operation fails
     */
    public List<Course> findWithSpace() throws SQLException {
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_COURSES_WITH_SPACE);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        }
        
        return courses;
    }
    
    /**
     * Update Course Enrollment
     * Updates the current enrollment count for a course
     * 
     * @param courseId Course ID to update
     * @param newEnrollment New enrollment count
     * @throws SQLException if database operation fails
     */
    public void updateEnrollment(String courseId, int newEnrollment) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ENROLLMENT)) {
            
            stmt.setInt(1, newEnrollment);
            stmt.setTimestamp(2, Timestamp.valueOf(java.time.LocalDateTime.now()));
            stmt.setString(3, courseId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating course enrollment failed, no rows affected.");
            }
            
            System.out.println("Course enrollment updated: " + courseId + " -> " + newEnrollment);
        }
    }
    
    /**
     * Get Course Statistics
     * Returns statistics about course enrollments
     * 
     * @return String containing course statistics
     * @throws SQLException if database operation fails
     */
    public String getCourseStatistics() throws SQLException {
        StringBuilder stats = new StringBuilder();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_courses, " +
                    "AVG(current_enrollment) as avg_enrollment, " +
                    "SUM(current_enrollment) as total_enrolled, " +
                    "AVG(max_capacity) as avg_capacity " +
                    "FROM courses WHERE is_active = 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                stats.append("Course Statistics:\n");
                stats.append("Total Courses: ").append(rs.getInt("total_courses")).append("\n");
                stats.append("Average Enrollment: ").append(String.format("%.1f", rs.getDouble("avg_enrollment"))).append("\n");
                stats.append("Total Students Enrolled: ").append(rs.getInt("total_enrolled")).append("\n");
                stats.append("Average Capacity: ").append(String.format("%.1f", rs.getDouble("avg_capacity"))).append("\n");
            }
        }
        
        return stats.toString();
    }
    
    /**
     * Map Result Set to Course Object
     * Helper method to convert database result set to Course model object
     * 
     * @param rs ResultSet containing course data
     * @return Course object populated with data
     * @throws SQLException if mapping fails
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        
        course.setCourseId(rs.getString("course_id"));
        course.setCourseName(rs.getString("course_name"));
        course.setDescription(rs.getString("description"));
        course.setCredits(rs.getInt("credits"));
        course.setMaxCapacity(rs.getInt("max_capacity"));
        course.setCurrentEnrollment(rs.getInt("current_enrollment"));
        course.setDepartment(rs.getString("department"));
        course.setPrerequisites(rs.getString("prerequisites"));
        course.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            course.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            course.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return course;
    }
    
    /**
     * Validate Course Data
     * Validates course data before database operations
     * 
     * @param course Course object to validate
     * @return List of validation error messages (empty if valid)
     */
    public List<String> validateCourse(Course course) {
        List<String> errors = new ArrayList<>();
        
        if (course.getCourseId() == null || course.getCourseId().trim().isEmpty()) {
            errors.add("Course ID is required");
        }
        
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            errors.add("Course name is required");
        }
        
        if (course.getCredits() <= 0) {
            errors.add("Credits must be greater than 0");
        }
        
        if (course.getMaxCapacity() <= 0) {
            errors.add("Maximum capacity must be greater than 0");
        }
        
        if (course.getCurrentEnrollment() < 0) {
            errors.add("Current enrollment cannot be negative");
        }
        
        if (course.getCurrentEnrollment() > course.getMaxCapacity()) {
            errors.add("Current enrollment cannot exceed maximum capacity");
        }
        
        return errors;
    }
    
    /**
     * Get Courses with Pagination
     * Retrieves courses with pagination support
     * 
     * @param page Page number (starting from 1)
     * @param pageSize Number of records per page
     * @return List of Course objects for the specified page
     * @throws SQLException if database operation fails
     */
    public List<Course> findWithPagination(int page, int pageSize) throws SQLException {
        List<Course> courses = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT * FROM courses ORDER BY course_name LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }
        }
        
        return courses;
    }
    
    /**
     * Close Connection
     * Cleanup method to close database connections
     * This is handled automatically by try-with-resources blocks
     */
    public void close() {
        // Connection cleanup is handled by try-with-resources in getConnection
        System.out.println("CourseDAO cleanup completed");
    }
}