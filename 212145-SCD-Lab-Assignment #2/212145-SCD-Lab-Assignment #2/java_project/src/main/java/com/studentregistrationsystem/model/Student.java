package com.studentregistrationsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Student Model Class
 * Represents a student entity in the Student Course Registration System
 * This class follows JavaBean conventions for MVC architecture
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class Student {
    
    // Private attributes representing student data
    private int id;
    private String name;
    private String email;
    private String studentNumber;
    private String program;
    private int yearLevel;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Relationship with courses (composition - Student owns the list)
    private List<Course> courseList;
    private List<Registration> registrations;
    private List<Grade> grades;
    
    /**
     * Default Constructor
     * Creates an empty Student object
     */
    public Student() {
        this.courseList = new ArrayList<>();
        this.registrations = new ArrayList<>();
        this.grades = new ArrayList<>();
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Parameterized Constructor
     * Creates a Student object with specified values
     * 
     * @param name Student's full name
     * @param email Student's email address
     * @param studentNumber Unique student identifier
     * @param program Student's academic program
     */
    public Student(String name, String email, String studentNumber, String program) {
        this();
        this.name = name;
        this.email = email;
        this.studentNumber = studentNumber;
        this.program = program;
    }
    
    /**
     * Register for a Course
     * Adds a course to the student's course list and creates a registration
     * 
     * @param course The course to register for
     * @return true if registration successful, false otherwise
     */
    public boolean registerCourse(Course course) {
        try {
            // Check if already registered
            boolean alreadyRegistered = registrations.stream()
                .anyMatch(reg -> reg.getCourse().getCourseId().equals(course.getCourseId()));
            
            if (alreadyRegistered) {
                return false; // Already registered
            }
            
            // Add course to list and create registration
            courseList.add(course);
            Registration registration = new Registration();
            registration.setStudent(this);
            registration.setCourse(course);
            registration.setRegistrationDate(LocalDateTime.now());
            registrations.add(registration);
            
            this.updatedAt = LocalDateTime.now();
            return true;
        } catch (Exception e) {
            System.err.println("Error registering course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * View Registered Courses
     * Returns list of all courses the student is registered for
     * 
     * @return List of Course objects
     */
    public List<Course> viewCourses() {
        return new ArrayList<>(courseList);
    }
    
    /**
     * Get Current GPA
     * Calculates the current Grade Point Average for the student
     * 
     * @return double value representing GPA (0.0 to 4.0)
     */
    public double getCurrentGPA() {
        if (grades == null || grades.isEmpty()) {
            return 0.0;
        }
        
        double totalQualityPoints = 0.0;
        int totalCredits = 0;
        
        for (Grade grade : grades) {
            totalQualityPoints += grade.getQualityPoints() * grade.getCourse().getCredits();
            totalCredits += grade.getCourse().getCredits();
        }
        
        return totalCredits > 0 ? totalQualityPoints / totalCredits : 0.0;
    }
    
    /**
     * Get Student Status
     * Returns formatted student status information
     * 
     * @return String containing student details
     */
    public String getStudentStatus() {
        return String.format("Student: %s (%s) | Program: %s | GPA: %.2f", 
                           name, studentNumber, program, getCurrentGPA());
    }
    
    // Getter and Setter methods following JavaBean conventions
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    public String getProgram() {
        return program;
    }
    
    public void setProgram(String program) {
        this.program = program;
    }
    
    public int getYearLevel() {
        return yearLevel;
    }
    
    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
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
    
    public List<Course> getCourseList() {
        return courseList;
    }
    
    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
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
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", program='" + program + '\'' +
                ", yearLevel=" + yearLevel +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
    
    /**
     * equals method for object comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return id == student.id && studentNumber != null && studentNumber.equals(student.studentNumber);
    }
    
    /**
     * hashCode method for collections
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, studentNumber);
    }
}