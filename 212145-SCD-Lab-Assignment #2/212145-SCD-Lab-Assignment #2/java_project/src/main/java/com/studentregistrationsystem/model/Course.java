package com.studentregistrationsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Model Class
 * Represents a course entity in the Student Course Registration System
 * This class provides course management functionality for MVC architecture
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class Course {
    
    // Private attributes representing course data
    private String courseId;
    private String courseName;
    private String description;
    private int credits;
    private int maxCapacity;
    private int currentEnrollment;
    private String department;
    private String prerequisites;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationship with other entities
    private List<Student> enrolledStudents;
    private List<Registration> registrations;
    private List<Grade> grades;
    
    /**
     * Default Constructor
     * Creates an empty Course object
     */
    public Course() {
        this.enrolledStudents = new ArrayList<>();
        this.registrations = new ArrayList<>();
        this.grades = new ArrayList<>();
        this.currentEnrollment = 0;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized Constructor
     * Creates a Course object with specified values
     * 
     * @param courseId Unique course identifier
     * @param courseName Name of the course
     * @param credits Credit hours for the course
     * @param description Course description
     */
    public Course(String courseId, String courseName, int credits, String description) {
        this();
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.description = description;
    }
    
    /**
     * Add Course
     * Adds this course to the system with validation
     * 
     * @return true if course can be added, false otherwise
     */
    public boolean addCourse() {
        // Validate course data
        if (courseId == null || courseId.trim().isEmpty()) {
            System.err.println("Course ID cannot be empty");
            return false;
        }
        
        if (courseName == null || courseName.trim().isEmpty()) {
            System.err.println("Course name cannot be empty");
            return false;
        }
        
        if (credits <= 0) {
            System.err.println("Credits must be positive");
            return false;
        }
        
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * View Course Details
     * Returns comprehensive course information
     * 
     * @return Course object with all details
     */
    public Course viewCourseDetails() {
        return new Course(
            this.courseId,
            this.courseName,
            this.credits,
            this.description
        );
    }
    
    /**
     * Check if Course has Space
     * Verifies if the course can accept more students
     * 
     * @return true if space available, false if course is full
     */
    public boolean hasSpace() {
        return currentEnrollment < maxCapacity;
    }
    
    /**
     * Enroll Student
     * Adds a student to this course
     * 
     * @param student Student to enroll
     * @return true if enrollment successful, false if course full
     */
    public boolean enrollStudent(Student student) {
        if (!hasSpace()) {
            System.err.println("Course is full. Cannot enroll more students.");
            return false;
        }
        
        // Check if already enrolled
        boolean alreadyEnrolled = enrolledStudents.stream()
            .anyMatch(s -> s.getId() == student.getId());
        
        if (alreadyEnrolled) {
            System.err.println("Student already enrolled in this course");
            return false;
        }
        
        enrolledStudents.add(student);
        currentEnrollment++;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Remove Student
     * Removes a student from this course
     * 
     * @param student Student to remove
     * @return true if removal successful, false otherwise
     */
    public boolean removeStudent(Student student) {
        boolean removed = enrolledStudents.remove(student);
        if (removed) {
            currentEnrollment--;
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
    
    /**
     * Get Enrollment Percentage
     * Calculates the percentage of capacity filled
     * 
     * @return double value (0.0 to 1.0)
     */
    public double getEnrollmentPercentage() {
        return maxCapacity > 0 ? (double) currentEnrollment / maxCapacity : 0.0;
    }
    
    /**
     * Get Course Statistics
     * Returns formatted course statistics
     * 
     * @return String containing course stats
     */
    public String getCourseStatistics() {
        return String.format("%s (%s) | %d/%d students (%.1f%%) | %d credits",
                           courseName, courseId, currentEnrollment, maxCapacity, 
                           getEnrollmentPercentage() * 100, credits);
    }
    
    /**
     * Calculate Average Grade
     * Calculates the average grade for all students in this course
     * 
     * @return double value representing average grade
     */
    public double getAverageGrade() {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }
        
        return grades.stream()
            .mapToDouble(Grade::getScore)
            .average()
            .orElse(0.0);
    }
    
    /**
     * Validate Prerequisites
     * Checks if a student meets the prerequisites for this course
     * 
     * @param student Student to check
     * @return true if prerequisites met, false otherwise
     */
    public boolean validatePrerequisites(Student student) {
        // Simplified prerequisite validation
        // In a real system, this would check completed courses and grades
        if (prerequisites == null || prerequisites.trim().isEmpty()) {
            return true; // No prerequisites
        }
        
        // Placeholder logic - should be enhanced based on actual requirements
        return student.getYearLevel() >= 2; // Example: must be 2nd year or higher
    }
    
    // Getter and Setter methods following JavaBean conventions
    
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        this.credits = credits;
    }
    
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    public int getCurrentEnrollment() {
        return currentEnrollment;
    }
    
    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPrerequisites() {
        return prerequisites;
    }
    
    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }
    
    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
        this.currentEnrollment = enrolledStudents != null ? enrolledStudents.size() : 0;
    }
    
    public List<Registration> getRegistrations() {
        return registrations;
    }
    
    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
    }
    
    public List<Grade> getGrades() {
        return grades;
    }
    
    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }
    
    /**
     * toString method for debugging and logging
     */
    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", description='" + description + '\'' +
                ", credits=" + credits +
                ", maxCapacity=" + maxCapacity +
                ", currentEnrollment=" + currentEnrollment +
                ", department='" + department + '\'' +
                ", isActive=" + isActive +
                '}';
    }
    
    /**
     * equals method for object comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return courseId != null && courseId.equals(course.courseId);
    }
    
    /**
     * hashCode method for collections
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(courseId);
    }
}