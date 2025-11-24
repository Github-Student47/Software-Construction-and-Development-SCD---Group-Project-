package com.studentregistrationsystem.model;

import java.time.LocalDateTime;

/**
 * Grade Model Class
 * Represents a grade entry for a student in a specific course
 * This is the new feature added to the system for tracking academic performance
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class Grade {
    
    // Primary key
    private int gradeId;
    
    // Foreign keys and relationships
    private int studentId;
    private String courseId;
    private Student student;
    private Course course;
    
    // Grade information
    private double score; // Score out of 100
    private String letterGrade; // A, B, C, D, F
    private double qualityPoints; // 4.0, 3.0, 2.0, 1.0, 0.0
    private String gradeType; // MidTerm, Final, Assignment, Quiz, Project
    private String semester;
    private int year;
    
    // Grade metadata
    private LocalDateTime gradeDate;
    private LocalDateTime lastModifiedDate;
    private String gradedBy; // Instructor name
    private String comments;
    private boolean isFinal;
    
    // Grade components
    private double assignmentScore;
    private double quizScore;
    private double midtermScore;
    private double finalExamScore;
    private double participationScore;
    private double totalPossibleScore;
    
    // Academic tracking
    private boolean isPublished;
    private LocalDateTime publishedDate;
    private boolean isAppealed;
    private String appealComments;
    
    /**
     * Default Constructor
     * Creates an empty Grade object
     */
    public Grade() {
        this.gradeDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.isPublished = false;
        this.isAppealed = false;
        this.totalPossibleScore = 100.0;
        this.isFinal = false;
    }
    
    /**
     * Parameterized Constructor
     * Creates a Grade object for specific student-course assessment
     * 
     * @param student Student being graded
     * @param course Course for which grade is assigned
     * @param score Numeric score achieved
     * @param gradeType Type of assessment (MidTerm, Final, etc.)
     * @param semester Semester of the course
     * @param year Year of the course
     */
    public Grade(Student student, Course course, double score, String gradeType, String semester, int year) {
        this();
        this.student = student;
        this.course = course;
        this.studentId = student.getId();
        this.courseId = course.getCourseId();
        this.score = score;
        this.gradeType = gradeType;
        this.semester = semester;
        this.year = year;
        calculateLetterGrade();
        calculateQualityPoints();
    }
    
    /**
     * Calculate Letter Grade
     * Converts numeric score to letter grade based on standard grading scale
     * 
     * @return String representing letter grade
     */
    public String calculateLetterGrade() {
        if (score >= 90) {
            this.letterGrade = "A";
        } else if (score >= 80) {
            this.letterGrade = "B";
        } else if (score >= 70) {
            this.letterGrade = "C";
        } else if (score >= 60) {
            this.letterGrade = "D";
        } else {
            this.letterGrade = "F";
        }
        
        return this.letterGrade;
    }
    
    /**
     * Calculate Quality Points
     * Converts letter grade to quality points for GPA calculation
     * 
     * @return double value representing quality points
     */
    public double calculateQualityPoints() {
        switch (letterGrade) {
            case "A": this.qualityPoints = 4.0; break;
            case "B": this.qualityPoints = 3.0; break;
            case "C": this.qualityPoints = 2.0; break;
            case "D": this.qualityPoints = 1.0; break;
            case "F": this.qualityPoints = 0.0; break;
            default: this.qualityPoints = 0.0;
        }
        
        return this.qualityPoints;
    }
    
    /**
     * Update Grade
     * Updates the numeric score and recalculates letter grade and quality points
     * 
     * @param newScore New numeric score
     * @param gradedBy Instructor who graded
     * @param comments Additional comments
     * @return true if update successful, false otherwise
     */
    public boolean updateGrade(double newScore, String gradedBy, String comments) {
        try {
            if (newScore >= 0 && newScore <= totalPossibleScore) {
                this.score = newScore;
                calculateLetterGrade();
                calculateQualityPoints();
                this.gradedBy = gradedBy;
                this.comments = comments;
                this.lastModifiedDate = LocalDateTime.now();
                
                System.out.println("Grade updated: Score = " + score + ", Letter = " + letterGrade);
                return true;
            } else {
                System.err.println("Invalid score. Must be between 0 and " + totalPossibleScore);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error updating grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Publish Grade
     * Makes the grade visible to the student
     * 
     * @return true if publish successful
     */
    public boolean publishGrade() {
        try {
            this.isPublished = true;
            this.publishedDate = LocalDateTime.now();
            this.lastModifiedDate = LocalDateTime.now();
            
            System.out.println("Grade published for student " + 
                             (student != null ? student.getName() : "Unknown"));
            return true;
        } catch (Exception e) {
            System.err.println("Error publishing grade: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Submit Appeal
     * Student submits an appeal for grade review
     * 
     * @param appealComments Student's appeal comments
     * @return true if appeal submitted successfully
     */
    public boolean submitAppeal(String appealComments) {
        try {
            this.isAppealed = true;
            this.appealComments = appealComments;
            this.lastModifiedDate = LocalDateTime.now();
            
            System.out.println("Grade appeal submitted for student " + 
                             (student != null ? student.getName() : "Unknown"));
            return true;
        } catch (Exception e) {
            System.err.println("Error submitting appeal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process Appeal Decision
     * Processes the decision on a grade appeal
     * 
     * @param newScore New score after appeal (can be same as original)
     * @param decision Appeal decision (Upheld, Partially Upheld, Denied)
     * @param decisionComments Comments on the decision
     * @return true if processing successful
     */
    public boolean processAppealDecision(double newScore, String decision, String decisionComments) {
        try {
            // Update score if changed
            if (newScore != this.score) {
                this.score = newScore;
                calculateLetterGrade();
                calculateQualityPoints();
            }
            
            // Mark appeal as resolved
            this.isAppealed = false;
            this.comments = "Appeal " + decision + ". " + decisionComments;
            this.lastModifiedDate = LocalDateTime.now();
            
            System.out.println("Appeal processed: " + decision + " for student " + 
                             (student != null ? student.getName() : "Unknown"));
            return true;
        } catch (Exception e) {
            System.err.println("Error processing appeal: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Calculate Weighted Score
     * Calculates score based on grade components (if applicable)
     * 
     * @return double representing weighted score
     */
    public double calculateWeightedScore() {
        double totalWeight = assignmentScore + quizScore + midtermScore + finalExamScore + participationScore;
        
        // If components add up to reasonable total, use them
        if (totalWeight > 0 && totalWeight <= totalPossibleScore) {
            return totalWeight;
        }
        
        // Otherwise, use the main score
        return this.score;
    }
    
    /**
     * Get Grade Summary
     * Returns formatted grade information
     * 
     * @return String containing grade summary
     */
    public String getGradeSummary() {
        String status = String.format("Grade #%d | Student: %s | Course: %s | Score: %.2f | Letter: %s | QP: %.1f | Type: %s",
                                     gradeId,
                                     student != null ? student.getName() : "Unknown",
                                     course != null ? course.getCourseName() : "Unknown",
                                     score, letterGrade, qualityPoints, gradeType);
        return status;
    }
    
    /**
     * Get Academic Performance
     * Returns detailed academic performance information
     * 
     * @return String containing performance details
     */
    public String getAcademicPerformance() {
        return String.format("Final: %.2f%% (%s) | Components: A=%.2f, Q=%.2f, M=%.2f, F=%.2f, P=%.2f | Published: %s",
                           score, letterGrade,
                           assignmentScore, quizScore, midtermScore, finalExamScore, participationScore,
                           isPublished ? "Yes" : "No");
    }
    
    /**
     * Calculate Semester GPA Impact
     * Calculates how this grade affects the student's semester GPA
     * 
     * @return double value representing GPA contribution
     */
    public double calculateGPAImpact() {
        return qualityPoints * (course != null ? course.getCredits() : 0);
    }
    
    /**
     * Check Grade Validation
     * Validates grade data for consistency
     * 
     * @return true if grade data is valid, false otherwise
     */
    public boolean isValidGrade() {
        return score >= 0 && score <= totalPossibleScore &&
               letterGrade != null && !letterGrade.isEmpty() &&
               student != null && course != null;
    }
    
    // Getter and Setter methods following JavaBean conventions
    
    public int getGradeId() {
        return gradeId;
    }
    
    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
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
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
        calculateLetterGrade();
        calculateQualityPoints();
    }
    
    public String getLetterGrade() {
        return letterGrade;
    }
    
    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
        calculateQualityPoints();
    }
    
    public double getQualityPoints() {
        return qualityPoints;
    }
    
    public void setQualityPoints(double qualityPoints) {
        this.qualityPoints = qualityPoints;
    }
    
    public String getGradeType() {
        return gradeType;
    }
    
    public void setGradeType(String gradeType) {
        this.gradeType = gradeType;
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
    
    public LocalDateTime getGradeDate() {
        return gradeDate;
    }
    
    public void setGradeDate(LocalDateTime gradeDate) {
        this.gradeDate = gradeDate;
    }
    
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
    
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public String getGradedBy() {
        return gradedBy;
    }
    
    public void setGradedBy(String gradedBy) {
        this.gradedBy = gradedBy;
    }
    
    public String getComments() {
        return comments;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    public boolean isFinal() {
        return isFinal;
    }
    
    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }
    
    public double getAssignmentScore() {
        return assignmentScore;
    }
    
    public void setAssignmentScore(double assignmentScore) {
        this.assignmentScore = assignmentScore;
    }
    
    public double getQuizScore() {
        return quizScore;
    }
    
    public void setQuizScore(double quizScore) {
        this.quizScore = quizScore;
    }
    
    public double getMidtermScore() {
        return midtermScore;
    }
    
    public void setMidtermScore(double midtermScore) {
        this.midtermScore = midtermScore;
    }
    
    public double getFinalExamScore() {
        return finalExamScore;
    }
    
    public void setFinalExamScore(double finalExamScore) {
        this.finalExamScore = finalExamScore;
    }
    
    public double getParticipationScore() {
        return participationScore;
    }
    
    public void setParticipationScore(double participationScore) {
        this.participationScore = participationScore;
    }
    
    public double getTotalPossibleScore() {
        return totalPossibleScore;
    }
    
    public void setTotalPossibleScore(double totalPossibleScore) {
        this.totalPossibleScore = totalPossibleScore;
    }
    
    public boolean isPublished() {
        return isPublished;
    }
    
    public void setPublished(boolean published) {
        isPublished = published;
        if (published) {
            this.publishedDate = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }
    
    public boolean isAppealed() {
        return isAppealed;
    }
    
    public void setAppealed(boolean appealed) {
        isAppealed = appealed;
    }
    
    public String getAppealComments() {
        return appealComments;
    }
    
    public void setAppealComments(String appealComments) {
        this.appealComments = appealComments;
    }
    
    /**
     * toString method for debugging and logging
     */
    @Override
    public String toString() {
        return "Grade{" +
                "gradeId=" + gradeId +
                ", studentId=" + studentId +
                ", courseId='" + courseId + '\'' +
                ", score=" + score +
                ", letterGrade='" + letterGrade + '\'' +
                ", qualityPoints=" + qualityPoints +
                ", gradeType='" + gradeType + '\'' +
                ", semester='" + semester + '\'' +
                ", year=" + year +
                ", isPublished=" + isPublished +
                '}';
    }
    
    /**
     * equals method for object comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Grade grade = (Grade) obj;
        return gradeId == grade.gradeId;
    }
    
    /**
     * hashCode method for collections
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(gradeId);
    }
}