package com.studentregistrationsystem.service;

import com.studentregistrationsystem.model.Course;
import com.studentregistrationsystem.dao.CourseDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseService
 * Implements business logic for course-related operations
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class CourseService {
    
    // Data Access Object
    private CourseDAO courseDAO;
    
    /**
     * Default Constructor
     * Initializes CourseDAO
     */
    public CourseService() {
        this.courseDAO = new CourseDAO();
    }
    
    /**
     * Parameterized Constructor
     * Allows injection of custom CourseDAO for testing
     * 
     * @param courseDAO CourseDAO instance
     */
    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }
    
    /**
     * Save Course
     * Validates and saves a course to the database
     * 
     * @param course Course object to save
     * @return true if save successful, false otherwise
     */
    public boolean saveCourse(Course course) {
        try {
            // Validate course data
            List<String> validationErrors = validateCourse(course);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            courseDAO.save(course);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while saving course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find Course by ID
     * Retrieves a course by its identifier
     * 
     * @param courseId Course ID to search for
     * @return Course object if found, null otherwise
     */
    public Course findCourseById(String courseId) {
        try {
            return courseDAO.findById(courseId);
        } catch (SQLException e) {
            System.err.println("Database error while finding course: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Find All Courses
     * Retrieves all courses from the database
     * 
     * @return List of all Course objects
     */
    public List<Course> findAllCourses() {
        try {
            return courseDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Database error while finding all courses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Update Course
     * Updates an existing course in the database
     * 
     * @param course Course object to update
     * @return true if update successful, false otherwise
     */
    public boolean updateCourse(Course course) {
        try {
            // Validate course data
            List<String> validationErrors = validateCourse(course);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            courseDAO.update(course);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while updating course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete Course
     * Removes a course from the database
     * 
     * @param courseId Course ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteCourse(String courseId) {
        try {
            courseDAO.delete(courseId);
            return true;
        } catch (SQLException e) {
            System.err.println("Database error while deleting course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Search Courses
     * Searches courses by name, ID, or description
     * 
     * @param searchTerm Search term to look for
     * @return List of matching Course objects
     */
    public List<Course> searchCourses(String searchTerm) {
        try {
            return courseDAO.search(searchTerm);
        } catch (SQLException e) {
            System.err.println("Database error while searching courses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Find Courses with Available Space
     * Retrieves courses that have available enrollment capacity
     * 
     * @return List of Course objects with available space
     */
    public List<Course> findCoursesWithSpace() {
        try {
            return courseDAO.findWithSpace();
        } catch (SQLException e) {
            System.err.println("Database error while finding courses with space: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Find Courses by Department
     * Retrieves courses by their academic department
     * 
     * @param department Department to search for
     * @return List of Course objects in the specified department
     */
    public List<Course> findCoursesByDepartment(String department) {
        try {
            return courseDAO.findByDepartment(department);
        } catch (SQLException e) {
            System.err.println("Database error while finding courses by department: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Find Courses by Credits
     * Retrieves courses by their credit hours
     * 
     * @param credits Credit hours to search for
     * @return List of Course objects with specified credits
     */
    public List<Course> findCoursesByCredits(int credits) {
        try {
            return courseDAO.findByCredits(credits);
        } catch (SQLException e) {
            System.err.println("Database error while finding courses by credits: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Create Course
     * Handles the complete course creation process
     * 
     * @param courseId Unique course identifier
     * @param courseName Name of the course
     * @param description Course description
     * @param credits Credit hours
     * @param maxCapacity Maximum enrollment capacity
     * @param department Academic department
     * @param prerequisites Prerequisites (can be null)
     * @return true if creation successful, false otherwise
     */
    public boolean createCourse(String courseId, String courseName, String description, 
                               int credits, int maxCapacity, String department, String prerequisites) {
        try {
            // Validate inputs
            if (courseId == null || courseId.trim().isEmpty()) {
                System.err.println("Course ID is required");
                return false;
            }
            
            if (courseName == null || courseName.trim().isEmpty()) {
                System.err.println("Course name is required");
                return false;
            }
            
            if (credits <= 0) {
                System.err.println("Credits must be greater than 0");
                return false;
            }
            
            if (maxCapacity <= 0) {
                System.err.println("Maximum capacity must be greater than 0");
                return false;
            }
            
            // Check if course already exists
            Course existingCourse = findCourseById(courseId);
            if (existingCourse != null) {
                System.err.println("Course already exists with ID: " + courseId);
                return false;
            }
            
            // Create course object
            Course course = new Course(courseId, courseName, credits, description);
            course.setMaxCapacity(maxCapacity);
            course.setDepartment(department);
            course.setPrerequisites(prerequisites);
            
            return saveCourse(course);
            
        } catch (Exception e) {
            System.err.println("Error creating course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update Course Capacity
     * Updates the maximum capacity for a course
     * 
     * @param courseId Course ID to update
     * @param newMaxCapacity New maximum capacity
     * @return true if update successful, false otherwise
     */
    public boolean updateCourseCapacity(String courseId, int newMaxCapacity) {
        try {
            Course course = findCourseById(courseId);
            if (course == null) {
                System.err.println("Course not found: " + courseId);
                return false;
            }
            
            if (newMaxCapacity <= 0) {
                System.err.println("Maximum capacity must be greater than 0");
                return false;
            }
            
            if (course.getCurrentEnrollment() > newMaxCapacity) {
                System.err.println("Cannot reduce capacity below current enrollment: " + course.getCurrentEnrollment());
                return false;
            }
            
            course.setMaxCapacity(newMaxCapacity);
            return updateCourse(course);
            
        } catch (Exception e) {
            System.err.println("Error updating course capacity: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update Course Enrollment
     * Updates the current enrollment count for a course
     * 
     * @param courseId Course ID to update
     * @param newEnrollment New enrollment count
     * @return true if update successful, false otherwise
     */
    public boolean updateCourseEnrollment(String courseId, int newEnrollment) {
        try {
            Course course = findCourseById(courseId);
            if (course == null) {
                System.err.println("Course not found: " + courseId);
                return false;
            }
            
            if (newEnrollment < 0) {
                System.err.println("Enrollment count cannot be negative");
                return false;
            }
            
            if (newEnrollment > course.getMaxCapacity()) {
                System.err.println("Enrollment cannot exceed maximum capacity");
                return false;
            }
            
            courseDAO.updateEnrollment(courseId, newEnrollment);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error updating course enrollment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get Course Statistics
     * Returns comprehensive statistics about courses
     * 
     * @return String containing course statistics
     */
    public String getCourseStatistics() {
        try {
            return courseDAO.getCourseStatistics();
        } catch (SQLException e) {
            return "Error retrieving course statistics: " + e.getMessage();
        }
    }
    
    /**
     * Check Course Availability
     * Checks if a course has available enrollment space
     * 
     * @param courseId Course ID to check
     * @return true if course has space, false otherwise
     */
    public boolean isCourseAvailable(String courseId) {
        try {
            Course course = findCourseById(courseId);
            return course != null && course.hasSpace();
        } catch (Exception e) {
            System.err.println("Error checking course availability: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get Available Departments
     * Returns list of departments that offer courses
     * 
     * @return List of department names
     */
    public List<String> getAvailableDepartments() {
        try {
            List<Course> allCourses = findAllCourses();
            List<String> departments = new ArrayList<>();
            
            for (Course course : allCourses) {
                String department = course.getDepartment();
                if (department != null && !department.trim().isEmpty() && !departments.contains(department)) {
                    departments.add(department);
                }
            }
            
            return departments;
            
        } catch (Exception e) {
            System.err.println("Error retrieving departments: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get Courses by Credit Range
     * Retrieves courses within a specific credit range
     * 
     * @param minCredits Minimum credits
     * @param maxCredits Maximum credits
     * @return List of Course objects within the credit range
     */
    public List<Course> getCoursesByCreditRange(int minCredits, int maxCredits) {
        try {
            List<Course> allCourses = findAllCourses();
            List<Course> filteredCourses = new ArrayList<>();
            
            for (Course course : allCourses) {
                if (course.getCredits() >= minCredits && course.getCredits() <= maxCredits) {
                    filteredCourses.add(course);
                }
            }
            
            return filteredCourses;
            
        } catch (Exception e) {
            System.err.println("Error filtering courses by credit range: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Validate Course Data
     * Validates course data before database operations
     * 
     * @param course Course object to validate
     * @return List of validation error messages (empty if valid)
     */
    private List<String> validateCourse(Course course) {
        return courseDAO.validateCourse(course);
    }
    
    /**
     * Close Resources
     * Cleanup method to close database connections and resources
     */
    public void close() {
        if (courseDAO != null) courseDAO.close();
        System.out.println("CourseService resources closed");
    }
}