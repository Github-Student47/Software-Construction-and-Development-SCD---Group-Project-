package com.studentregistrationsystem.model;

import java.time.LocalDateTime;

/**
 * Registration Model Class
 * Represents the relationship between Student and Course (Many-to-Many)
 * Manages course registration process in the Student Course Registration System
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class Registration {
    
    // Primary key
    private int registrationId;
    
    // Foreign keys and relationships
    private int studentId;
    private String courseId;
    private Student student;
    private Course course;
    
    // Registration metadata
    private LocalDateTime registrationDate;
    private LocalDateTime lastModifiedDate;
    private String status; // Active, Dropped, Completed
    private String semester;
    private int year;
    private String notes;
    
    // Payment information
    private double registrationFee;
    private boolean feePaid;
    private LocalDateTime paymentDate;
    
    // Academic tracking
    private int attendance;
    private boolean midTermCompleted;
    private boolean finalExamCompleted;
    private LocalDateTime completedDate;
    
    /**
     * Default Constructor
     * Creates an empty Registration object
     */
    public Registration() {
        this.registrationDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.status = "Active";
        this.feePaid = false;
        this.attendance = 0;
        this.midTermCompleted = false;
        this.finalExamCompleted = false;
    }
    
    /**
     * Parameterized Constructor
     * Creates a Registration object for student-course pairing
     * 
     * @param student Student being registered
     * @param course Course being registered for
     * @param semester Semester of registration
     * @param year Year of registration
     */
    public Registration(Student student, Course course, String semester, int year) {
        this();
        this.student = student;
        this.course = course;
        this.studentId = student.getId();
        this.courseId = course.getCourseId();
        this.semester = semester;
        this.year = year;
    }
    
    /**
     * Register Student
     * Processes student registration for a course
     * 
     * @param student Student to register
     * @param course Course to register for
     * @return true if registration successful, false otherwise
     */
    public boolean registerStudent(Student student, Course course) {
        try {
            // Validate student and course
            if (student == null || course == null) {
                System.err.println("Student and course cannot be null");
                return false;
            }
            
            // Check if course has space
            if (!course.hasSpace()) {
                System.err.println("Course is full: " + course.getCourseName());
                return false;
            }
            
            // Set relationships
            this.student = student;
            this.course = course;
            this.studentId = student.getId();
            this.courseId = course.getCourseId();
            
            // Calculate registration fee (example: 100 per credit hour)
            this.registrationFee = course.getCredits() * 100.0;
            
            // Update timestamps
            this.registrationDate = LocalDateTime.now();
            this.lastModifiedDate = LocalDateTime.now();
            
            // Add to student's registrations
            if (student.getRegistrations() != null) {
                student.getRegistrations().add(this);
            }
            
            // Add to course's registrations
            if (course.getRegistrations() != null) {
                course.getRegistrations().add(this);
            }
            
            // Enroll student in course
            course.enrollStudent(student);
            
            System.out.println("Student " + student.getName() + " successfully registered for " + course.getCourseName());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error registering student: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Drop Course
     * Processes course withdrawal
     * 
     * @return true if drop successful, false otherwise
     */
    public boolean dropCourse() {
        try {
            if (student != null && course != null) {
                // Remove from student's registration list
                if (student.getRegistrations() != null) {
                    student.getRegistrations().remove(this);
                }
                
                // Remove from course's registration list
                if (course.getRegistrations() != null) {
                    course.getRegistrations().remove(this);
                }
                
                // Remove student from course enrollment
                course.removeStudent(student);
                
                // Update status
                this.status = "Dropped";
                this.lastModifiedDate = LocalDateTime.now();
                
                System.out.println("Student " + student.getName() + " dropped course " + course.getCourseName());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error dropping course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Complete Course
     * Marks the course as completed for the student
     * 
     * @return true if completion processed successfully
     */
    public boolean completeCourse() {
        try {
            this.status = "Completed";
            this.completedDate = LocalDateTime.now();
            this.lastModifiedDate = LocalDateTime.now();
            
            System.out.println("Course " + course.getCourseName() + " marked as completed for student " + student.getName());
            return true;
        } catch (Exception e) {
            System.err.println("Error completing course: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process Payment
     * Records payment for the registration
     * 
     * @param amount Amount paid
     * @return true if payment processed successfully
     */
    public boolean processPayment(double amount) {
        try {
            if (amount >= this.registrationFee) {
                this.feePaid = true;
                this.paymentDate = LocalDateTime.now();
                this.lastModifiedDate = LocalDateTime.now();
                
                System.out.println("Payment of $" + amount + " processed for registration " + registrationId);
                return true;
            } else {
                System.err.println("Insufficient payment. Required: $" + registrationFee + ", Received: $" + amount);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update Attendance
     * Updates attendance record for the student
     * 
     * @param newAttendance New attendance count
     * @return true if update successful
     */
    public boolean updateAttendance(int newAttendance) {
        try {
            if (newAttendance >= 0) {
                this.attendance = newAttendance;
                this.lastModifiedDate = LocalDateTime.now();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating attendance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Mark MidTerm Complete
     * Marks that the mid-term exam was taken
     */
    public void markMidTermComplete() {
        this.midTermCompleted = true;
        this.lastModifiedDate = LocalDateTime.now();
    }
    
    /**
     * Mark Final Exam Complete
     * Marks that the final exam was taken
     */
    public void markFinalExamComplete() {
        this.finalExamCompleted = true;
        this.lastModifiedDate = LocalDateTime.now();
    }
    
    /**
     * Get Registration Summary
     * Returns formatted registration information
     * 
     * @return String containing registration summary
     */
    public String getRegistrationSummary() {
        String status = String.format("Registration #%d | Student: %s | Course: %s | Status: %s | Fee: $%.2f (%s)",
                                     registrationId,
                                     student != null ? student.getName() : "Unknown",
                                     course != null ? course.getCourseName() : "Unknown",
                                     this.status,
                                     registrationFee,
                                     feePaid ? "Paid" : "Unpaid");
        return status;
    }
    
    /**
     * Get Academic Progress
     * Returns formatted academic progress information
     * 
     * @return String containing academic progress
     */
    public String getAcademicProgress() {
        String attendancePercent = course != null ? 
            String.format("%.1f%%", (attendance / (double)(course.getCredits() * 16)) * 100) : "N/A";
            
        return String.format("Attendance: %d classes | Mid-term: %s | Final: %s | Attendance Rate: %s",
                           attendance,
                           midTermCompleted ? "Completed" : "Pending",
                           finalExamCompleted ? "Completed" : "Pending",
                           attendancePercent);
    }
    
    // Getter and Setter methods following JavaBean conventions
    
    public int getRegistrationId() {
        return registrationId;
    }
    
    public void setRegistrationId(int registrationId) {
        this.registrationId = registrationId;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
        if (student != null) {
            this.studentId = student.getId();
        }
    }
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
        if (course != null) {
            this.courseId = course.getCourseId();
        }
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getSemester() {
        return semester;
    }
    
    public void setSemester(String semester) {
        this.semester = semester;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public double getRegistrationFee() {
        return registrationFee;
    }
    
    public void setRegistrationFee(double registrationFee) {
        this.registrationFee = registrationFee;
    }
    
    public boolean isFeePaid() {
        return feePaid;
    }
    
    public void setFeePaid(boolean feePaid) {
        this.feePaid = feePaid;
        if (feePaid) {
            this.paymentDate = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public int getAttendance() {
        return attendance;
    }
    
    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }
    
    public boolean isMidTermCompleted() {
        return midTermCompleted;
    }
    
    public void setMidTermCompleted(boolean midTermCompleted) {
        this.midTermCompleted = midTermCompleted;
    }
    
    public boolean isFinalExamCompleted() {
        return finalExamCompleted;
    }
    
    public void setFinalExamCompleted(boolean finalExamCompleted) {
        this.finalExamCompleted = finalExamCompleted;
    }
    
    public LocalDateTime getCompletedDate() {
        return completedDate;
    }
    
    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }
    
    /**
     * toString method for debugging and logging
     */
    @Override
    public String toString() {
        return "Registration{" +
                "registrationId=" + registrationId +
                ", studentId=" + studentId +
                ", courseId='" + courseId + '\'' +
                ", registrationDate=" + registrationDate +
                ", status='" + status + '\'' +
                ", semester='" + semester + '\'' +
                ", year=" + year +
                ", registrationFee=" + registrationFee +
                ", feePaid=" + feePaid +
                '}';
    }
    
    /**
     * equals method for object comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Registration that = (Registration) obj;
        return registrationId == that.registrationId;
    }
    
    /**
     * hashCode method for collections
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(registrationId);
    }
}