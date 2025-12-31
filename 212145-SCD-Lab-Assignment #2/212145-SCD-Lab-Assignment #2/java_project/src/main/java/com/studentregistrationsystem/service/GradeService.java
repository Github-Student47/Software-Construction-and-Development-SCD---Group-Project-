package com.studentregistrationsystem.service;

import com.studentregistrationsystem.model.Grade;
import com.studentregistrationsystem.model.Student;
import com.studentregistrationsystem.model.Course;
import com.studentregistrationsystem.dao.GradeDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * GradeService
 * Implements business logic for grade-related operations
 * New feature: Grade management system for tracking academic performance
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class GradeService {
    
    // Data Access Object
    private GradeDAO gradeDAO;
    
    /**
     * Default Constructor
     * Initializes GradeDAO
     */
    public GradeService() {
        this.gradeDAO = new GradeDAO();
    }
    
    /**
     * Parameterized Constructor
     * Allows injection of custom GradeDAO for testing
     * 
     * @param gradeDAO GradeDAO instance
     */
    public GradeService(GradeDAO gradeDAO) {
        this.gradeDAO = gradeDAO;
    }
    
    /**
     * Save Grade
     * Validates and saves a grade to the database
     * 
     * @param grade Grade object to save
     * @return true if save successful, false otherwise
     */
    public boolean saveGrade(Grade grade) {
        try {
            // Validate grade data
            List<String> validationErrors = validateGrade(grade);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            // Save grade
            gradeDAO.save(grade);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while saving grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find Grade by ID
     * Retrieves a grade by its primary key
     * 
     * @param gradeId Grade ID to search for
     * @return Grade object if found, null otherwise
     */
    public Grade findGradeById(int gradeId) {
        try {
            return gradeDAO.findById(gradeId);
        } catch (SQLException e) {
            System.err.println("Database error while finding grade: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Find Grade by Student and Course
     * Retrieves the most recent grade for a student in a specific course
     * 
     * @param studentId Student ID to search for
     * @param courseId Course ID to search for
     * @return Grade object if found, null otherwise
     */
    public Grade findGradeByStudentAndCourse(int studentId, String courseId) {
        try {
            return gradeDAO.findByStudentAndCourse(studentId, courseId);
        } catch (SQLException e) {
            System.err.println("Database error while finding grade: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Find Grades by Student
     * Retrieves all grades for a specific student
     * 
     * @param studentId Student ID to search for
     * @return List of Grade objects for the student
     */
    public List<Grade> findGradesByStudent(int studentId) {
        try {
            return gradeDAO.findByStudentId(studentId);
        } catch (SQLException e) {
            System.err.println("Database error while finding grades: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Find Grades by Course
     * Retrieves all grades for a specific course
     * 
     * @param courseId Course ID to search for
     * @return List of Grade objects for the course
     */
    public List<Grade> findGradesByCourse(String courseId) {
        try {
            return gradeDAO.findByCourseId(courseId);
        } catch (SQLException e) {
            System.err.println("Database error while finding grades: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Find All Grades
     * Retrieves all grades from the database
     * 
     * @return List of all Grade objects
     */
    public List<Grade> findAllGrades() {
        try {
            return gradeDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Database error while finding all grades: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Update Grade
     * Updates an existing grade in the database
     * 
     * @param grade Grade object to update
     * @return true if update successful, false otherwise
     */
    public boolean updateGrade(Grade grade) {
        try {
            // Validate grade data
            List<String> validationErrors = validateGrade(grade);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            gradeDAO.update(grade);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while updating grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete Grade
     * Removes a grade from the database
     * 
     * @param gradeId Grade ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteGrade(int gradeId) {
        try {
            gradeDAO.delete(gradeId);
            return true;
        } catch (SQLException e) {
            System.err.println("Database error while deleting grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Assign Grade to Student
     * Handles the complete grade assignment process
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     * @param score Numeric score achieved
     * @param gradeType Type of assessment (MidTerm, Final, Assignment, etc.)
     * @param semester Semester (e.g., "Fall", "Spring")
     * @param year Year
     * @param gradedBy Instructor name
     * @param isFinal Whether this is a final grade
     * @return true if assignment successful, false otherwise
     */
    public boolean assignGrade(int studentId, String courseId, double score, String gradeType, 
                              String semester, int year, String gradedBy, boolean isFinal) {
        try {
            // Validate inputs
            if (studentId <= 0 || courseId == null || courseId.trim().isEmpty()) {
                System.err.println("Student ID and Course ID are required");
                return false;
            }
            
            if (score < 0 || score > 100) {
                System.err.println("Score must be between 0 and 100");
                return false;
            }
            
            // Create grade object
            Grade grade = new Grade();
            grade.setStudentId(studentId);
            grade.setCourseId(courseId);
            grade.setScore(score);
            grade.setGradeType(gradeType);
            grade.setSemester(semester);
            grade.setYear(year);
            grade.setGradedBy(gradedBy);
            grade.setIsFinal(isFinal);
            
            // Calculate letter grade and quality points
            grade.calculateLetterGrade();
            grade.calculateQualityPoints();
            
            return saveGrade(grade);
            
        } catch (Exception e) {
            System.err.println("Error assigning grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Publish Grade
     * Makes a grade visible to the student
     * 
     * @param gradeId Grade ID to publish
     * @return true if publish successful, false otherwise
     */
    public boolean publishGrade(int gradeId) {
        try {
            gradeDAO.publishGrade(gradeId);
            return true;
        } catch (SQLException e) {
            System.err.println("Database error while publishing grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Submit Grade Appeal
     * Student submits an appeal for grade review
     * 
     * @param gradeId Grade ID to appeal
     * @param appealComments Comments explaining the appeal
     * @return true if appeal submitted successfully, false otherwise
     */
    public boolean submitGradeAppeal(int gradeId, String appealComments) {
        try {
            if (appealComments == null || appealComments.trim().isEmpty()) {
                System.err.println("Appeal comments are required");
                return false;
            }
            
            gradeDAO.submitAppeal(gradeId, appealComments);
            return true;
        } catch (SQLException e) {
            System.err.println("Database error while submitting appeal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process Grade Appeal
     * Processes the decision on a grade appeal
     * 
     * @param gradeId Grade ID to process
     * @param newScore New score after appeal (can be same as original)
     * @param letterGrade New letter grade
     * @param qualityPoints New quality points
     * @param decisionComments Comments on the decision
     * @return true if processing successful, false otherwise
     */
    public boolean processGradeAppeal(int gradeId, double newScore, String letterGrade, 
                                     double qualityPoints, String decisionComments) {
        try {
            // Validate inputs
            if (newScore < 0 || newScore > 100) {
                System.err.println("Score must be between 0 and 100");
                return false;
            }
            
            if (letterGrade == null || letterGrade.trim().isEmpty()) {
                System.err.println("Letter grade is required");
                return false;
            }
            
            gradeDAO.processAppeal(gradeId, newScore, letterGrade, qualityPoints, decisionComments);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while processing appeal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calculate Student GPA
     * Calculates the Grade Point Average for a student
     * 
     * @param studentId Student ID to calculate GPA for
     * @return double value representing GPA (0.0 to 4.0)
     */
    public double calculateGPA(int studentId) {
        try {
            return gradeDAO.calculateStudentGPA(studentId);
        } catch (SQLException e) {
            System.err.println("Database error while calculating GPA: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Get Grades by Semester
     * Retrieves grades for a specific semester and year
     * 
     * @param semester Semester to search for (e.g., "Fall", "Spring")
     * @param year Year to search for
     * @return List of Grade objects for the specified semester
     */
    public List<Grade> getGradesBySemester(String semester, int year) {
        try {
            return gradeDAO.findBySemester(semester, year);
        } catch (SQLException e) {
            System.err.println("Database error while finding grades by semester: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get Course Grade Statistics
     * Returns grade distribution statistics for a course
     * 
     * @param courseId Course ID to get statistics for
     * @return String containing grade statistics
     */
    public String getCourseGradeStatistics(String courseId) {
        try {
            List<Grade> grades = findGradesByCourse(courseId);
            
            if (grades.isEmpty()) {
                return "No grades found for course: " + courseId;
            }
            
            StringBuilder stats = new StringBuilder();
            stats.append("Grade Statistics for Course: ").append(courseId).append("\n");
            stats.append("Total Grades: ").append(grades.size()).append("\n");
            
            // Count grades by letter
            int countA = 0, countB = 0, countC = 0, countD = 0, countF = 0;
            double totalScore = 0;
            
            for (Grade grade : grades) {
                totalScore += grade.getScore();
                
                switch (grade.getLetterGrade()) {
                    case "A": countA++; break;
                    case "B": countB++; break;
                    case "C": countC++; break;
                    case "D": countD++; break;
                    case "F": countF++; break;
                }
            }
            
            double averageScore = totalScore / grades.size();
            
            stats.append("Average Score: ").append(String.format("%.2f", averageScore)).append("\n");
            stats.append("Grade Distribution:\n");
            stats.append("  A: ").append(countA).append(" (").append(countA * 100.0 / grades.size()).append("%)\n");
            stats.append("  B: ").append(countB).append(" (").append(countB * 100.0 / grades.size()).append("%)\n");
            stats.append("  C: ").append(countC).append(" (").append(countC * 100.0 / grades.size()).append("%)\n");
            stats.append("  D: ").append(countD).append(" (").append(countD * 100.0 / grades.size()).append("%)\n");
            stats.append("  F: ").append(countF).append(" (").append(countF * 100.0 / grades.size()).append("%)\n");
            
            return stats.toString();
            
        } catch (Exception e) {
            return "Error retrieving grade statistics: " + e.getMessage();
        }
    }
    
    /**
     * Validate Grade Data
     * Validates grade data before database operations
     * 
     * @param grade Grade object to validate
     * @return List of validation error messages (empty if valid)
     */
    private List<String> validateGrade(Grade grade) {
        return gradeDAO.validateGrade(grade);
    }
    
    /**
     * Close Resources
     * Cleanup method to close database connections and resources
     */
    public void close() {
        if (gradeDAO != null) gradeDAO.close();
        System.out.println("GradeService resources closed");
    }
}