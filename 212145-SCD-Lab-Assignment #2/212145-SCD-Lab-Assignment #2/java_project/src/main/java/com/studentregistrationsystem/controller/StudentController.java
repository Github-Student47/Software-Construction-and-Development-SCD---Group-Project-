package com.studentregistrationsystem.controller;

import com.studentregistrationsystem.model.Student;
import com.studentregistrationsystem.model.Course;
import com.studentregistrationsystem.model.Registration;
import com.studentregistrationsystem.model.Grade;
import com.studentregistrationsystem.model.User;
import com.studentregistrationsystem.service.StudentService;
import com.studentregistrationsystem.service.CourseService;
import com.studentregistrationsystem.service.GradeService;
import com.studentregistrationsystem.service.UserService;

import java.util.List;
import java.util.ArrayList;

/**terterterte
 * Handles HTTP requests and responses for student-related operations
 * Implements the Controller layer in MVC architecture
 * 
 * @author Adeel Hussain
 * @version 1.0
 * @since 2025-10-27
 */
public class StudentController {
    
    // Services for business logic
    private StudentService studentService;
    private CourseService courseService;
    private GradeService gradeService;
    private UserService userService;
    
    /**
     * Default Constructor
     * Initializes all required services
     */
    public StudentController() {
        this.studentService = new StudentService();
        this.courseService = new CourseService();
        this.gradeService = new GradeService();
        this.userService = new UserService();
    }
    
    /**
     * Create Student
     * Handles the creation of a new student
     * 
     * @param name Student's full name
     * @param email Student's email address
     * @param studentNumber Unique student identifier
     * @param program Student's academic program
     * @param yearLevel Student's year level (1-8)
     * @return Result object indicating success or failure
     */
    public OperationResult createStudent(String name, String email, String studentNumber, String program, int yearLevel) {
        try {
            // Validate input parameters
            if (name == null || name.trim().isEmpty()) {
                return new OperationResult(false, "Student name is required");
            }
            
            if (email == null || email.trim().isEmpty()) {
                return new OperationResult(false, "Email is required");
            }
            
            if (studentNumber == null || studentNumber.trim().isEmpty()) {
                return new OperationResult(false, "Student number is required");
            }
            
            if (program == null || program.trim().isEmpty()) {
                return new OperationResult(false, "Program is required");
            }
            
            if (yearLevel < 1 || yearLevel > 8) {
                return new OperationResult(false, "Year level must be between 1 and 8");
            }
            
            // Create student object
            Student student = new Student(name, email, studentNumber, program);
            student.setYearLevel(yearLevel);
            
            // Save student through service
            boolean success = studentService.saveStudent(student);
            
            if (success) {
                return new OperationResult(true, "Student created successfully with ID: " + student.getId(), student);
            } else {
                return new OperationResult(false, "Failed to create student. Please check your data and try again.");
            }
            
        } catch (Exception e) {
            return new OperationResult(false, "Error creating student: " + e.getMessage());
        }
    }
    
    /**
     * Get Student by ID
     * Retrieves a student by their ID
     * 
     * @param studentId Student ID to search for
     * @return Result object containing student data or error message
     */
    public OperationResult getStudent(int studentId) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            Student student = studentService.findStudentById(studentId);
            
            if (student != null) {
                return new OperationResult(true, "Student found", student);
            } else {
                return new OperationResult(false, "Student not found with ID: " + studentId);
            }
            
        } catch (Exception e) {
            return new OperationResult(false, "Error retrieving student: " + e.getMessage());
        }
    }
    
    /**
     * Get All Students
     * Retrieves all students from the database
     * 
     * @return Result object containing list of all students
     */
    public OperationResult getAllStudents() {
        try {
            List<Student> students = studentService.findAllStudents();
            return new OperationResult(true, "Retrieved " + students.size() + " students", students);
        } catch (Exception e) {
            return new OperationResult(false, "Error retrieving students: " + e.getMessage());
        }
    }
    
    /**
     * Update Student
     * Updates an existing student's information
     * 
     * @param studentId Student ID to update
     * @param name New name (null to keep existing)
     * @param email New email (null to keep existing)
     * @param program New program (null to keep existing)
     * @param yearLevel New year level (0 to keep existing)
     * @return Result object indicating success or failure
     */
    public OperationResult updateStudent(int studentId, String name, String email, String program, int yearLevel) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            // Get existing student
            Student student = studentService.findStudentById(studentId);
            if (student == null) {
                return new OperationResult(false, "Student not found with ID: " + studentId);
            }
            
            // Update fields if provided
            if (name != null && !name.trim().isEmpty()) {
                student.setName(name);
            }
            
            if (email != null && !email.trim().isEmpty()) {
                student.setEmail(email);
            }
            
            if (program != null && !program.trim().isEmpty()) {
                student.setProgram(program);
            }
            
            if (yearLevel > 0 && yearLevel <= 8) {
                student.setYearLevel(yearLevel);
            }
            
            // Update student through service
            boolean success = studentService.updateStudent(student);
            
            if (success) {
                return new OperationResult(true, "Student updated successfully", student);
            } else {
                return new OperationResult(false, "Failed to update student");
            }
            
        } catch (Exception e) {
            return new OperationResult(false, "Error updating student: " + e.getMessage());
        }
    }
    
    /**
     * Delete Student
     * Removes a student from the database
     * 
     * @param studentId Student ID to delete
     * @return Result object indicating success or failure
     */
    public OperationResult deleteStudent(int studentId) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            boolean success = studentService.deleteStudent(studentId);
            
            if (success) {
                return new OperationResult(true, "Student deleted successfully");
            } else {
                return new OperationResult(false, "Failed to delete student or student not found");
            }
            
        } catch (Exception e) {
            return new OperationResult(false, "Error deleting student: " + e.getMessage());
        }
    }
    
    /**
     * Register Student for Course
     * Handles student course registration
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     * @param semester Semester (e.g., "Fall", "Spring")
     * @param year Year
     * @return Result object indicating success or failure
     */
    public OperationResult registerStudentForCourse(int studentId, String courseId, String semester, int year) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            if (courseId == null || courseId.trim().isEmpty()) {
                return new OperationResult(false, "Course ID is required");
            }
            
            if (semester == null || semester.trim().isEmpty()) {
                return new OperationResult(false, "Semester is required");
            }
            
            if (year < 2020 || year > 2030) {
                return new OperationResult(false, "Invalid year (must be between 2020-2030)");
            }
            
            boolean success = studentService.registerStudentForCourse(studentId, courseId, semester, year);
            
            if (success) {
                return new OperationResult(true, "Student registered for course successfully");
            } else {
                return new OperationResult(false, "Registration failed. Please check student and course details.");
            }
            
        } catch (Exception e) {
            return new OperationResult(false, "Error during registration: " + e.getMessage());
        }
    }
    
    /**
     * Drop Course for Student
     * Handles course withdrawal
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     * @param semester Semester
     * @param year Year
     * @return Result object indicating success or failure
     */
    public OperationResult dropCourseForStudent(int studentId, String courseId, String semester, int year) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            boolean success = studentService.dropCourseForStudent(studentId, courseId, semester, year);
            
            if (success) {
                return new OperationResult(true, "Course dropped successfully");
            } else {
                return new OperationResult(false, "Failed to drop course or registration not found");
            }
            
        } catch (Exception e) {
            return new OperationResult(false, "Error dropping course: " + e.getMessage());
        }
    }
    
    /**
     * Get Student's Courses
     * Retrieves all courses a student is registered for
     * 
     * @param studentId Student ID
     * @return Result object containing list of courses
     */
    public OperationResult getStudentCourses(int studentId) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            List<Course> courses = studentService.getStudentCourses(studentId);
            return new OperationResult(true, "Retrieved " + courses.size() + " courses", courses);
            
        } catch (Exception e) {
            return new OperationResult(false, "Error retrieving student courses: " + e.getMessage());
        }
    }
    
    /**
     * Get Student's Grades
     * Retrieves all grades for a student
     * 
     * @param studentId Student ID
     * @return Result object containing list of grades
     */
    public OperationResult getStudentGrades(int studentId) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            List<Grade> grades = studentService.getStudentGrades(studentId);
            return new OperationResult(true, "Retrieved " + grades.size() + " grades", grades);
            
        } catch (Exception e) {
            return new OperationResult(false, "Error retrieving student grades: " + e.getMessage());
        }
    }
    
    /**
     * Get Student GPA
     * Calculates and returns student's GPA
     * 
     * @param studentId Student ID
     * @return Result object containing GPA value
     */
    public OperationResult getStudentGPA(int studentId) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            double gpa = studentService.calculateStudentGPA(studentId);
            return new OperationResult(true, "GPA calculated successfully", gpa);
            
        } catch (Exception e) {
            return new OperationResult(false, "Error calculating GPA: " + e.getMessage());
        }
    }
    
    /**
     * Search Students
     * Searches students by name, email, or student number
     * 
     * @param searchTerm Search term
     * @return Result object containing list of matching students
     */
    public OperationResult searchStudents(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return new OperationResult(false, "Search term is required");
            }
            
            List<Student> students = studentService.searchStudents(searchTerm);
            return new OperationResult(true, "Found " + students.size() + " students", students);
            
        } catch (Exception e) {
            return new OperationResult(false, "Error searching students: " + e.getMessage());
        }
    }
    
    /**
     * Get Student Statistics
     * Returns comprehensive statistics about a student
     * 
     * @param studentId Student ID
     * @return Result object containing student statistics
     */
    public OperationResult getStudentStatistics(int studentId) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            String statistics = studentService.getStudentStatistics(studentId);
            return new OperationResult(true, "Statistics retrieved successfully", statistics);
            
        } catch (Exception e) {
            return new OperationResult(false, "Error retrieving statistics: " + e.getMessage());
        }
    }
    
    /**
     * Get Available Courses for Student
     * Returns courses that a student can register for
     * 
     * @param studentId Student ID
     * @return Result object containing list of available courses
     */
    public OperationResult getAvailableCoursesForStudent(int studentId) {
        try {
            if (studentId <= 0) {
                return new OperationResult(false, "Invalid student ID");
            }
            
            List<Course> courses = studentService.getAvailableCoursesForStudent(studentId);
            return new OperationResult(true, "Found " + courses.size() + " available courses", courses);
            
        } catch (Exception e) {
            return new OperationResult(false, "Error retrieving available courses: " + e.getMessage());
        }
    }
    
    /**
     * Close Resources
     * Cleanup method to close all service resources
     */
    public void close() {
        if (studentService != null) studentService.close();
        if (courseService != null) courseService.close();
        if (gradeService != null) gradeService.close();
        if (userService != null) userService.close();
        
        System.out.println("StudentController resources closed");
    }
    
    /**
     * OperationResult Inner Class
     * Encapsulates the result of an operation
     */
    public static class OperationResult {
        private final boolean success;
        private final String message;
        private final Object data;
        
        public OperationResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public OperationResult(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public Object getData() {
            return data;
        }
        
        public <T> T getDataAs(Class<T> clazz) {
            if (data != null && clazz.isInstance(data)) {
                return clazz.cast(data);
            }
            return null;
        }
    }
}