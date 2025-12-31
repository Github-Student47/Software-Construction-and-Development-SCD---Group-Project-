package com.scd.project.dao;

import com.scd.project.model.Student;
import java.util.List;

/**
 * Data Access Object (DAO) Interface for Student entity
 * 
 * This interface defines the contract for database operations on Student records.
 * Following the DAO pattern, this separates database logic from business logic.
 * 
 * The DAO pattern demonstrates:
 * - Abstraction: Defining operations without implementation details
 * - Encapsulation: Hiding database access complexity
 * - Reusability: Same interface can have different implementations
 * 
 * Team Members:
 * - Muhammad Kashan Tariq (212145)
 * - Adeel Hussain (221829)
 * - Syed Abdain (221855)
 * - Muhammad Tauseef (221789)
 */
public interface StudentDAO {
    
    /**
     * Save a new student record to the database
     * @param student - The student object to save
     */
    void save(Student student);
    
    /**
     * Update an existing student record
     * @param student - The student object with updated data
     */
    void update(Student student);
    
    /**
     * Delete a student record by ID
     * @param id - The ID of the student to delete
     */
    void delete(int id);
    
    /**
     * Retrieve a single student by ID
     * @param id - The ID of the student to retrieve
     * @return The Student object, or null if not found
     */
    Student getById(int id);
    
    /**
     * Retrieve all student records from the database
     * @return List of all students
     */
    List<Student> getAll();
    
    /**
     * Search students by name (partial match)
     * @param name - The search term for student name
     * @return List of matching students
     */
    List<Student> searchByName(String name);
    
    /**
     * Get count of all students
     * @return Total number of students in database
     */
    int getCount();
}
