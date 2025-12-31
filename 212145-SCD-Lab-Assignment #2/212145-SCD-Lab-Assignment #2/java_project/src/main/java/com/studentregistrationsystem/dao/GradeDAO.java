package com.studentregistrationsystem.dao;

import com.studentregistrationsystem.model.Grade;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * GradeDAO (Data Access Object)
 * Handles all database operations for Grade entities
 * Implements CRUD operations using JDBC for MySQL database
 * New feature: Grade management system for tracking student academic performance
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class GradeDAO {
    
    // Database connection configuration
    private static final String URL = "jdbc:mysql://localhost:3306/student_registration";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    // SQL queries for CRUD operations
    private static final String INSERT_GRADE = 
        "INSERT INTO grades (student_id, course_id, score, letter_grade, quality_points, grade_type, semester, year, grade_date, last_modified_date, graded_by, comments, is_final, assignment_score, quiz_score, midterm_score, final_exam_score, participation_score, total_possible_score, is_published, published_date, is_appealed, appeal_comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_GRADE_BY_ID = 
        "SELECT * FROM grades WHERE grade_id = ?";
    
    private static final String SELECT_GRADE_BY_STUDENT_COURSE = 
        "SELECT * FROM grades WHERE student_id = ? AND course_id = ? ORDER BY grade_date DESC LIMIT 1";
    
    private static final String SELECT_GRADES_BY_STUDENT = 
        "SELECT * FROM grades WHERE student_id = ? ORDER BY grade_date DESC";
    
    private static final String SELECT_GRADES_BY_COURSE = 
        "SELECT * FROM grades WHERE course_id = ? ORDER BY grade_date DESC";
    
    private static final String SELECT_ALL_GRADES = 
        "SELECT * FROM grades ORDER BY grade_date DESC";
    
    private static final String UPDATE_GRADE = 
        "UPDATE grades SET score = ?, letter_grade = ?, quality_points = ?, last_modified_date = ?, graded_by = ?, comments = ?, assignment_score = ?, quiz_score = ?, midterm_score = ?, final_exam_score = ?, participation_score = ?, is_published = ?, published_date = ? WHERE grade_id = ?";
    
    private static final String DELETE_GRADE = 
        "DELETE FROM grades WHERE grade_id = ?";
    
    private static final String COUNT_GRADES = 
        "SELECT COUNT(*) FROM grades";
    
    private static final String FIND_GRADES_BY_SEMESTER = 
        "SELECT * FROM grades WHERE semester = ? AND year = ? ORDER BY grade_date DESC";
    
    private static final String CALCULATE_STUDENT_GPA = 
        "SELECT AVG(quality_points) as gpa FROM grades WHERE student_id = ? AND is_final = 1";
    
    private static final String PUBLISH_GRADE = 
        "UPDATE grades SET is_published = 1, published_date = ? WHERE grade_id = ?";
    
    private static final String SUBMIT_APPEAL = 
        "UPDATE grades SET is_appealed = 1, appeal_comments = ? WHERE grade_id = ?";
    
    private static final String PROCESS_APPEAL = 
        "UPDATE grades SET is_appealed = 0, score = ?, letter_grade = ?, quality_points = ?, comments = ?, last_modified_date = ? WHERE grade_id = ?";
    
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
     * Save Grade
     * Inserts a new grade into the database
     * 
     * @param grade Grade object to save
     * @throws SQLException if database operation fails
     */
    public void save(Grade grade) throws SQLException {
        if (grade.getStudentId() <= 0 || grade.getCourseId() == null || grade.getCourseId().trim().isEmpty()) {
            throw new SQLException("Student ID and Course ID are required");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_GRADE, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, grade.getStudentId());
            stmt.setString(2, grade.getCourseId());
            stmt.setDouble(3, grade.getScore());
            stmt.setString(4, grade.getLetterGrade());
            stmt.setDouble(5, grade.getQualityPoints());
            stmt.setString(6, grade.getGradeType());
            stmt.setString(7, grade.getSemester());
            stmt.setInt(8, grade.getYear());
            stmt.setTimestamp(9, Timestamp.valueOf(grade.getGradeDate()));
            stmt.setTimestamp(10, Timestamp.valueOf(grade.getLastModifiedDate()));
            stmt.setString(11, grade.getGradedBy());
            stmt.setString(12, grade.getComments());
            stmt.setBoolean(13, grade.isFinal());
            stmt.setDouble(14, grade.getAssignmentScore());
            stmt.setDouble(15, grade.getQuizScore());
            stmt.setDouble(16, grade.getMidtermScore());
            stmt.setDouble(17, grade.getFinalExamScore());
            stmt.setDouble(18, grade.getParticipationScore());
            stmt.setDouble(19, grade.getTotalPossibleScore());
            stmt.setBoolean(20, grade.isPublished());
            
            Timestamp publishedDate = grade.getPublishedDate();
            stmt.setTimestamp(21, publishedDate != null ? Timestamp.valueOf(publishedDate) : null);
            
            stmt.setBoolean(22, grade.isAppealed());
            stmt.setString(23, grade.getAppealComments());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating grade failed, no rows affected.");
            }
            
            // Get generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    grade.setGradeId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Grade saved successfully with ID: " + grade.getGradeId());
        }
    }
    
    /**
     * Find Grade by ID
     * Retrieves a grade by its primary key
     * 
     * @param gradeId Grade ID to search for
     * @return Grade object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Grade findById(int gradeId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GRADE_BY_ID)) {
            
            stmt.setInt(1, gradeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGrade(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find Grade by Student and Course
     * Retrieves the most recent grade for a student in a specific course
     * 
     * @param studentId Student ID to search for
     * @param courseId Course ID to search for
     * @return Grade object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Grade findByStudentAndCourse(int studentId, String courseId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GRADE_BY_STUDENT_COURSE)) {
            
            stmt.setInt(1, studentId);
            stmt.setString(2, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGrade(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find Grades by Student
     * Retrieves all grades for a specific student
     * 
     * @param studentId Student ID to search for
     * @return List of Grade objects for the student
     * @throws SQLException if database operation fails
     */
    public List<Grade> findByStudentId(int studentId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GRADES_BY_STUDENT)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }
        }
        
        return grades;
    }
    
    /**
     * Find Grades by Course
     * Retrieves all grades for a specific course
     * 
     * @param courseId Course ID to search for
     * @return List of Grade objects for the course
     * @throws SQLException if database operation fails
     */
    public List<Grade> findByCourseId(String courseId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_GRADES_BY_COURSE)) {
            
            stmt.setString(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }
        }
        
        return grades;
    }
    
    /**
     * Find All Grades
     * Retrieves all grades from the database
     * 
     * @return List of all Grade objects
     * @throws SQLException if database operation fails
     */
    public List<Grade> findAll() throws SQLException {
        List<Grade> grades = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_GRADES);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                grades.add(mapResultSetToGrade(rs));
            }
        }
        
        return grades;
    }
    
    /**
     * Update Grade
     * Updates an existing grade in the database
     * 
     * @param grade Grade object to update
     * @throws SQLException if database operation fails
     */
    public void update(Grade grade) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_GRADE)) {
            
            stmt.setDouble(1, grade.getScore());
            stmt.setString(2, grade.getLetterGrade());
            stmt.setDouble(3, grade.getQualityPoints());
            stmt.setTimestamp(4, Timestamp.valueOf(grade.getLastModifiedDate()));
            stmt.setString(5, grade.getGradedBy());
            stmt.setString(6, grade.getComments());
            stmt.setDouble(7, grade.getAssignmentScore());
            stmt.setDouble(8, grade.getQuizScore());
            stmt.setDouble(9, grade.getMidtermScore());
            stmt.setDouble(10, grade.getFinalExamScore());
            stmt.setDouble(11, grade.getParticipationScore());
            stmt.setBoolean(12, grade.isPublished());
            
            Timestamp publishedDate = grade.getPublishedDate();
            stmt.setTimestamp(13, publishedDate != null ? Timestamp.valueOf(publishedDate) : null);
            
            stmt.setInt(14, grade.getGradeId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating grade failed, no rows affected.");
            }
            
            System.out.println("Grade updated successfully: " + grade.getGradeId());
        }
    }
    
    /**
     * Delete Grade
     * Removes a grade from the database
     * 
     * @param gradeId Grade ID to delete
     * @throws SQLException if database operation fails
     */
    public void delete(int gradeId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_GRADE)) {
            
            stmt.setInt(1, gradeId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting grade failed, no rows affected.");
            }
            
            System.out.println("Grade deleted successfully: " + gradeId);
        }
    }
    
    /**
     * Count Grades
     * Returns the total number of grades in the database
     * 
     * @return int representing grade count
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_GRADES);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Find Grades by Semester
     * Retrieves grades for a specific semester and year
     * 
     * @param semester Semester to search for (e.g., "Fall", "Spring")
     * @param year Year to search for
     * @return List of Grade objects for the specified semester
     * @throws SQLException if database operation fails
     */
    public List<Grade> findBySemester(String semester, int year) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_GRADES_BY_SEMESTER)) {
            
            stmt.setString(1, semester);
            stmt.setInt(2, year);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }
        }
        
        return grades;
    }
    
    /**
     * Calculate Student GPA
     * Calculates the Grade Point Average for a student
     * 
     * @param studentId Student ID to calculate GPA for
     * @return double value representing GPA (0.0 to 4.0)
     * @throws SQLException if database operation fails
     */
    public double calculateStudentGPA(int studentId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(CALCULATE_STUDENT_GPA)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double gpa = rs.getDouble("gpa");
                    return rs.wasNull() ? 0.0 : gpa;
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Publish Grade
     * Makes a grade visible to the student
     * 
     * @param gradeId Grade ID to publish
     * @throws SQLException if database operation fails
     */
    public void publishGrade(int gradeId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(PUBLISH_GRADE)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, gradeId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Publishing grade failed, no rows affected.");
            }
            
            System.out.println("Grade published successfully: " + gradeId);
        }
    }
    
    /**
     * Submit Appeal
     * Student submits an appeal for grade review
     * 
     * @param gradeId Grade ID to appeal
     * @param appealComments Comments explaining the appeal
     * @throws SQLException if database operation fails
     */
    public void submitAppeal(int gradeId, String appealComments) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SUBMIT_APPEAL)) {
            
            stmt.setString(1, appealComments);
            stmt.setInt(2, gradeId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Submitting appeal failed, no rows affected.");
            }
            
            System.out.println("Grade appeal submitted: " + gradeId);
        }
    }
    
    /**
     * Process Appeal
     * Processes the decision on a grade appeal
     * 
     * @param gradeId Grade ID to process
     * @param newScore New score after appeal
     * @param letterGrade New letter grade
     * @param qualityPoints New quality points
     * @param decisionComments Comments on the decision
     * @throws SQLException if database operation fails
     */
    public void processAppeal(int gradeId, double newScore, String letterGrade, double qualityPoints, String decisionComments) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(PROCESS_APPEAL)) {
            
            stmt.setDouble(1, newScore);
            stmt.setString(2, letterGrade);
            stmt.setDouble(3, qualityPoints);
            stmt.setString(4, decisionComments);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(6, gradeId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Processing appeal failed, no rows affected.");
            }
            
            System.out.println("Grade appeal processed: " + gradeId);
        }
    }
    
    /**
     * Get Grade Statistics
     * Returns statistics about grade distributions
     * 
     * @return String containing grade statistics
     * @throws SQLException if database operation fails
     */
    public String getGradeStatistics() throws SQLException {
        StringBuilder stats = new StringBuilder();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_grades, " +
                    "AVG(score) as avg_score, " +
                    "COUNT(CASE WHEN letter_grade = 'A' THEN 1 END) as count_A, " +
                    "COUNT(CASE WHEN letter_grade = 'B' THEN 1 END) as count_B, " +
                    "COUNT(CASE WHEN letter_grade = 'C' THEN 1 END) as count_C, " +
                    "COUNT(CASE WHEN letter_grade = 'D' THEN 1 END) as count_D, " +
                    "COUNT(CASE WHEN letter_grade = 'F' THEN 1 END) as count_F " +
                    "FROM grades WHERE is_final = 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                stats.append("Grade Statistics:\n");
                stats.append("Total Final Grades: ").append(rs.getInt("total_grades")).append("\n");
                stats.append("Average Score: ").append(String.format("%.2f", rs.getDouble("avg_score"))).append("\n");
                stats.append("Grade Distribution:\n");
                stats.append("  A: ").append(rs.getInt("count_A")).append(" students\n");
                stats.append("  B: ").append(rs.getInt("count_B")).append(" students\n");
                stats.append("  C: ").append(rs.getInt("count_C")).append(" students\n");
                stats.append("  D: ").append(rs.getInt("count_D")).append(" students\n");
                stats.append("  F: ").append(rs.getInt("count_F")).append(" students\n");
            }
        }
        
        return stats.toString();
    }
    
    /**
     * Map Result Set to Grade Object
     * Helper method to convert database result set to Grade model object
     * 
     * @param rs ResultSet containing grade data
     * @return Grade object populated with data
     * @throws SQLException if mapping fails
     */
    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setStudentId(rs.getInt("student_id"));
        grade.setCourseId(rs.getString("course_id"));
        grade.setScore(rs.getDouble("score"));
        grade.setLetterGrade(rs.getString("letter_grade"));
        grade.setQualityPoints(rs.getDouble("quality_points"));
        grade.setGradeType(rs.getString("grade_type"));
        grade.setSemester(rs.getString("semester"));
        grade.setYear(rs.getInt("year"));
        
        Timestamp gradeDate = rs.getTimestamp("grade_date");
        if (gradeDate != null) {
            grade.setGradeDate(gradeDate.toLocalDateTime());
        }
        
        Timestamp lastModifiedDate = rs.getTimestamp("last_modified_date");
        if (lastModifiedDate != null) {
            grade.setLastModifiedDate(lastModifiedDate.toLocalDateTime());
        }
        
        grade.setGradedBy(rs.getString("graded_by"));
        grade.setComments(rs.getString("comments"));
        grade.setIsFinal(rs.getBoolean("is_final"));
        grade.setAssignmentScore(rs.getDouble("assignment_score"));
        grade.setQuizScore(rs.getDouble("quiz_score"));
        grade.setMidtermScore(rs.getDouble("midterm_score"));
        grade.setFinalExamScore(rs.getDouble("final_exam_score"));
        grade.setParticipationScore(rs.getDouble("participation_score"));
        grade.setTotalPossibleScore(rs.getDouble("total_possible_score"));
        grade.setPublished(rs.getBoolean("is_published"));
        
        Timestamp publishedDate = rs.getTimestamp("published_date");
        if (publishedDate != null) {
            grade.setPublishedDate(publishedDate.toLocalDateTime());
        }
        
        grade.setAppealed(rs.getBoolean("is_appealed"));
        grade.setAppealComments(rs.getString("appeal_comments"));
        
        return grade;
    }
    
    /**
     * Validate Grade Data
     * Validates grade data before database operations
     * 
     * @param grade Grade object to validate
     * @return List of validation error messages (empty if valid)
     */
    public List<String> validateGrade(Grade grade) {
        List<String> errors = new ArrayList<>();
        
        if (grade.getStudentId() <= 0) {
            errors.add("Valid student ID is required");
        }
        
        if (grade.getCourseId() == null || grade.getCourseId().trim().isEmpty()) {
            errors.add("Course ID is required");
        }
        
        if (grade.getScore() < 0 || grade.getScore() > grade.getTotalPossibleScore()) {
            errors.add("Score must be between 0 and " + grade.getTotalPossibleScore());
        }
        
        if (grade.getLetterGrade() == null || grade.getLetterGrade().trim().isEmpty()) {
            errors.add("Letter grade is required");
        }
        
        if (grade.getGradeType() == null || grade.getGradeType().trim().isEmpty()) {
            errors.add("Grade type is required");
        }
        
        if (grade.getSemester() == null || grade.getSemester().trim().isEmpty()) {
            errors.add("Semester is required");
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
        System.out.println("GradeDAO cleanup completed");
    }
}