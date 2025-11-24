package com.studentregistrationsystem.service;

import com.studentregistrationsystem.model.Student;
import com.studentregistrationsystem.model.Course;
import com.studentregistrationsystem.model.Registration;
import com.studentregistrationsystem.model.Grade;
import com.studentregistrationsystem.dao.StudentDAO;
import com.studentregistrationsystem.dao.CourseDAO;
import com.studentregistrationsystem.dao.RegistrationDAO;
import com.studentregistrationsystem.dao.GradeDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentService
 * Implements business logic for student-related operations
 * Acts as an intermediary between controllers and DAOs
 * Provides higher-level operations and business rules validation
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class StudentService {
    
    // Data Access Objects
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private RegistrationDAO registrationDAO;
    private GradeDAO gradeDAO;
    
    /**
     * Default Constructor
     * Initializes all required DAOs
     */
    public StudentService() {
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        this.registrationDAO = new RegistrationDAO();
        this.gradeDAO = new GradeDAO();
    }
    
    /**
     * Parameterized Constructor
     * Allows injection of custom DAOs for testing
     * 
     * @param studentDAO StudentDAO instance
     * @param courseDAO CourseDAO instance
     * @param registrationDAO RegistrationDAO instance
     * @param gradeDAO GradeDAO instance
     */
    public StudentService(StudentDAO studentDAO, CourseDAO courseDAO, 
                         RegistrationDAO registrationDAO, GradeDAO gradeDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
        this.registrationDAO = registrationDAO;
        this.gradeDAO = gradeDAO;
    }
    
    /**
     * Save Student
     * Validates and saves a student to the database
     * 
     * @param student Student object to save
     * @return true if save successful, false otherwise
     */
    public boolean saveStudent(Student student) {
        try {
            // Validate student data
            List<String> validationErrors = validateStudent(student);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            // Check for duplicate student number
            Student existingStudent = studentDAO.findByStudentNumber(student.getStudentNumber());
            if (existingStudent != null && existingStudent.getId() != student.getId()) {
                System.err.println("Student number already exists: " + student.getStudentNumber());
                return false;
            }
            
            // Save student
            studentDAO.save(student);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while saving student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find Student by ID
     * Retrieves a student by their primary key
     * 
     * @param id Student ID to search for
     * @return Student object if found, null otherwise
     */
    public Student findStudentById(int id) {
        try {
            return studentDAO.findById(id);
        } catch (SQLException e) {
            System.err.println("Database error while finding student: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Find All Students
     * Retrieves all students from the database
     * 
     * @return List of all Student objects
     */
    public List<Student> findAllStudents() {
        try {
            return studentDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Database error while finding all students: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Update Student
     * Updates an existing student in the database
     * 
     * @param student Student object to update
     * @return true if update successful, false otherwise
     */
    public boolean updateStudent(Student student) {
        try {
            // Validate student data
            List<String> validationErrors = validateStudent(student);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            studentDAO.update(student);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while updating student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete Student
     * Removes a student from the database
     * 
     * @param id Student ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteStudent(int id) {
        try {
            studentDAO.delete(id);
            return true;
        } catch (SQLException e) {
            System.err.println("Database error while deleting student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Search Students
     * Searches students by name, email, or student number
     * 
     * @param searchTerm Search term to look for
     * @return List of matching Student objects
     */
    public List<Student> searchStudents(String searchTerm) {
        try {
            return studentDAO.search(searchTerm);
        } catch (SQLException e) {
            System.err.println("Database error while searching students: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Register Student for Course
     * Handles the complete student course registration process
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     * @param semester Semester (e.g., "Fall", "Spring")
     * @param year Year
     * @return true if registration successful, false otherwise
     */
    public boolean registerStudentForCourse(int studentId, String courseId, String semester, int year) {
        try {
            // Get student and course
            Student student = studentDAO.findById(studentId);
            Course course = courseDAO.findById(courseId);
            
            if (student == null) {
                System.err.println("Student not found with ID: " + studentId);
                return false;
            }
            
            if (course == null) {
                System.err.println("Course not found with ID: " + courseId);
                return false;
            }
            
            // Check if course has space
            if (!course.hasSpace()) {
                System.err.println("Course is full: " + course.getCourseName());
                return false;
            }
            
            // Check prerequisites (simplified implementation)
            if (!validatePrerequisites(student, course)) {
                System.err.println("Student does not meet prerequisites for course: " + course.getCourseName());
                return false;
            }
            
            // Check for duplicate registration
            List<Registration> existingRegistrations = registrationDAO.findByStudentId(studentId);
            boolean alreadyRegistered = existingRegistrations.stream()
                .anyMatch(reg -> reg.getCourseId().equals(courseId) && 
                                reg.getSemester().equals(semester) && 
                                reg.getYear() == year &&
                                "Active".equals(reg.getStatus()));
            
            if (alreadyRegistered) {
                System.err.println("Student is already registered for this course");
                return false;
            }
            
            // Create registration
            Registration registration = new Registration(student, course, semester, year);
            boolean success = registrationDAO.save(registration);
            
            if (success) {
                // Update course enrollment
                course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
                courseDAO.update(course);
                
                System.out.println("Student " + student.getName() + " successfully registered for " + course.getCourseName());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Drop Course for Student
     * Handles course withdrawal process
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     * @param semester Semester
     * @param year Year
     * @return true if drop successful, false otherwise
     */
    public boolean dropCourseForStudent(int studentId, String courseId, String semester, int year) {
        try {
            // Find the registration
            Registration registration = registrationDAO.findByStudentAndCourse(studentId, courseId, semester, year);
            
            if (registration == null) {
                System.err.println("Registration not found for student " + studentId + " and course " + courseId);
                return false;
            }
            
            // Drop the course
            boolean success = registrationDAO.drop(registration);
            
            if (success) {
                // Update course enrollment
                Course course = courseDAO.findById(courseId);
                if (course != null) {
                    course.setCurrentEnrollment(Math.max(0, course.getCurrentEnrollment() - 1));
                    courseDAO.update(course);
                }
                
                System.out.println("Student " + studentId + " dropped course " + courseId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Database error during course drop: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get Student's Registered Courses
     * Returns all courses a student is registered for
     * 
     * @param studentId Student ID
     * @return List of Course objects the student is registered for
     */
    public List<Course> getStudentCourses(int studentId) {
        try {
            List<Course> courses = new ArrayList<>();
            List<Registration> registrations = registrationDAO.findByStudentId(studentId);
            
            for (Registration registration : registrations) {
                if ("Active".equals(registration.getStatus())) {
                    Course course = courseDAO.findById(registration.getCourseId());
                    if (course != null) {
                        courses.add(course);
                    }
                }
            }
            
            return courses;
            
        } catch (SQLException e) {
            System.err.println("Database error while getting student courses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get Student's Grades
     * Returns all grades for a student
     * 
     * @param studentId Student ID
     * @return List of Grade objects for the student
     */
    public List<Grade> getStudentGrades(int studentId) {
        try {
            return gradeDAO.findByStudentId(studentId);
        } catch (SQLException e) {
            System.err.println("Database error while getting student grades: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Calculate Student's GPA
     * Calculates the Grade Point Average for a student
     * 
     * @param studentId Student ID
     * @return double value representing GPA (0.0 to 4.0)
     */
    public double calculateStudentGPA(int studentId) {
        try {
            return gradeDAO.calculateStudentGPA(studentId);
        } catch (SQLException e) {
            System.err.println("Database error while calculating GPA: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Get Student's Registration History
     * Returns complete registration history for a student
     * 
     * @param studentId Student ID
     * @return List of Registration objects
     */
    public List<Registration> getStudentRegistrationHistory(int studentId) {
        try {
            return registrationDAO.findByStudentId(studentId);
        } catch (SQLException e) {
            System.err.println("Database error while getting registration history: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get Student Statistics
     * Returns comprehensive statistics about a student
     * 
     * @param studentId Student ID
     * @return String containing student statistics
     */
    public String getStudentStatistics(int studentId) {
        try {
            Student student = studentDAO.findById(studentId);
            if (student == null) {
                return "Student not found";
            }
            
            List<Registration> registrations = registrationDAO.findByStudentId(studentId);
            List<Grade> grades = gradeDAO.findByStudentId(studentId);
            double gpa = calculateStudentGPA(studentId);
            
            int totalRegistrations = registrations.size();
            int activeCourses = (int) registrations.stream()
                .filter(r -> "Active".equals(r.getStatus()))
                .count();
            int completedCourses = (int) registrations.stream()
                .filter(r -> "Completed".equals(r.getStatus()))
                .count();
            
            StringBuilder stats = new StringBuilder();
            stats.append("Student Statistics for: ").append(student.getName()).append("\n");
            stats.append("Student Number: ").append(student.getStudentNumber()).append("\n");
            stats.append("Program: ").append(student.getProgram()).append("\n");
            stats.append("Year Level: ").append(student.getYearLevel()).append("\n");
            stats.append("Total Registrations: ").append(totalRegistrations).append("\n");
            stats.append("Active Courses: ").append(activeCourses).append("\n");
            stats.append("Completed Courses: ").append(completedCourses).append("\n");
            stats.append("Current GPA: ").append(String.format("%.2f", gpa)).append("\n");
            
            return stats.toString();
            
        } catch (SQLException e) {
            return "Error retrieving student statistics: " + e.getMessage();
        }
    }
    
    /**
     * Validate Prerequisites
     * Checks if a student meets the prerequisites for a course
     * 
     * @param student Student to validate
     * @param course Course to validate prerequisites for
     * @return true if prerequisites met, false otherwise
     */
    private boolean validatePrerequisites(Student student, Course course) {
        // Simplified prerequisite validation
        // In a real system, this would check completed courses and grades
        
        if (course.getPrerequisites() == null || course.getPrerequisites().trim().isEmpty()) {
            return true; // No prerequisites required
        }
        
        // Example: Check year level requirement
        if (student.getYearLevel() >= 2) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Validate Student Data
     * Validates student data before database operations
     * 
     * @param student Student object to validate
     * @return List of validation error messages (empty if valid)
     */
    private List<String> validateStudent(Student student) {
        return studentDAO.validateStudent(student);
    }
    
    /**
     * Get Students by Program
     * Returns students enrolled in a specific program
     * 
     * @param program Program to filter by
     * @return List of Student objects in the specified program
     */
    public List<Student> getStudentsByProgram(String program) {
        try {
            return studentDAO.findByProgram(program);
        } catch (SQLException e) {
            System.err.println("Database error while finding students by program: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get Students by Year Level
     * Returns students at a specific year level
     * 
     * @param yearLevel Year level to filter by
     * @return List of Student objects at the specified year level
     */
    public List<Student> getStudentsByYearLevel(int yearLevel) {
        try {
            return studentDAO.findByYearLevel(yearLevel);
        } catch (SQLException e) {
            System.err.println("Database error while finding students by year level: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get Available Courses for Student
     * Returns courses that a student can register for
     * 
     * @param studentId Student ID
     * @return List of Course objects available for registration
     */
    public List<Course> getAvailableCoursesForStudent(int studentId) {
        try {
            List<Course> availableCourses = courseDAO.findWithSpace();
            Student student = studentDAO.findById(studentId);
            
            if (student == null) {
                return new ArrayList<>();
            }
            
            // Filter courses based on prerequisites and year level
            return availableCourses.stream()
                .filter(course -> validatePrerequisites(student, course))
                .filter(course -> student.getYearLevel() >= 1) // Basic year level check
                .collect(java.util.stream.Collectors.toList());
                
        } catch (SQLException e) {
            System.err.println("Database error while getting available courses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Close Resources
     * Cleanup method to close database connections and resources
     */
    public void close() {
        if (studentDAO != null) studentDAO.close();
        if (courseDAO != null) courseDAO.close();
        if (registrationDAO != null) registrationDAO.close();
        if (gradeDAO != null) gradeDAO.close();
        
        System.out.println("StudentService resources closed");
    }
}