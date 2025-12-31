package com.scd.project.model;

/**
 * Student Entity Class
 * Represents a student record in the database
 * 
 * Team Members:
 * - Muhammad Kashan Tariq (212145)
 * - Adeel Hussain (221829)
 * - Syed Abdain (221855)
 * - Muhammad Tauseef (221789)
 * 
 * This class demonstrates Object-Oriented Programming principles:
 * - Encapsulation: All fields are private with public getters/setters
 * - Constructors: Default and parameterized constructors
 * - toString(): For easy display of student information
 */
public class Student {
    
    // Private fields for encapsulation
    private int id;
    private String name;
    private String email;
    private String course;
    private String phone;
    private String address;
    
    /**
     * Default constructor - required for Spring bean creation and JSP form binding
     */
    public Student() {
        super();
    }
    
    /**
     * Parameterized constructor for creating Student objects with data
     */
    public Student(int id, String name, String email, String course) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
    }
    
    /**
     * Parameterized constructor without ID (for creating new students)
     */
    public Student(String name, String email, String course) {
        super();
        this.name = name;
        this.email = email;
        this.course = course;
    }
    
    // Getter and Setter methods for encapsulation
    
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
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = course;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Override toString() for easy debugging and logging
     */
    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", email=" + email + ", course=" + course + "]";
    }
}
