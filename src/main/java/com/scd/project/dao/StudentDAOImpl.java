package com.scd.project.dao;

import com.scd.project.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StudentDAO Implementation using Spring JDBC
 * 
 * This class provides the actual implementation of StudentDAO interface
 * using Spring's JdbcTemplate for database operations.
 * 
 * Key features:
 * - Uses @Repository annotation for Spring bean registration
 * - Uses @Autowired for dependency injection of JdbcTemplate
 * - Implements all CRUD operations defined in StudentDAO interface
 * 
 * Demonstrates:
 * - Data Access Object (DAO) pattern
 * - Spring dependency injection
 * - JDBC template pattern for database operations
 * - Exception handling
 * 
 * Team Members:
 * - Muhammad Kashan Tariq (212145)
 * - Adeel Hussain (221829)
 * - Syed Abdain (221855)
 * - Muhammad Tauseef (221789)
 */
@Repository
public class StudentDAOImpl implements StudentDAO {
    
    // SQL queries for database operations
    private static final String SQL_INSERT = 
        "INSERT INTO students (name, email, course, phone, address) VALUES (?, ?, ?, ?, ?)";
    
    private static final String SQL_UPDATE = 
        "UPDATE students SET name=?, email=?, course=?, phone=?, address=? WHERE id=?";
    
    private static final String SQL_DELETE = 
        "DELETE FROM students WHERE id=?";
    
    private static final String SQL_SELECT_BY_ID = 
        "SELECT * FROM students WHERE id=?";
    
    private static final String SQL_SELECT_ALL = 
        "SELECT * FROM students ORDER BY id DESC";
    
    private static final String SQL_SEARCH_BY_NAME = 
        "SELECT * FROM students WHERE name LIKE ? ORDER BY name";
    
    private static final String SQL_SELECT_COUNT = 
        "SELECT COUNT(*) FROM students";
    
    // Spring JDBC template for database operations
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * Save a new student record
     */
    @Override
    public void save(Student student) {
        try {
            jdbcTemplate.update(SQL_INSERT,
                student.getName(),
                student.getEmail(),
                student.getCourse(),
                student.getPhone(),
                student.getAddress()
            );
            System.out.println("Student saved successfully: " + student.getName());
        } catch (Exception e) {
            System.err.println("Error saving student: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Update an existing student record
     */
    @Override
    public void update(Student student) {
        try {
            jdbcTemplate.update(SQL_UPDATE,
                student.getName(),
                student.getEmail(),
                student.getCourse(),
                student.getPhone(),
                student.getAddress(),
                student.getId()
            );
            System.out.println("Student updated successfully: " + student.getId());
        } catch (Exception e) {
            System.err.println("Error updating student: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Delete a student record by ID
     */
    @Override
    public void delete(int id) {
        try {
            jdbcTemplate.update(SQL_DELETE, id);
            System.out.println("Student deleted successfully: " + id);
        } catch (Exception e) {
            System.err.println("Error deleting student: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Retrieve a student by ID
     */
    @Override
    public Student getById(int id) {
        try {
            Student student = jdbcTemplate.queryForObject(
                SQL_SELECT_BY_ID,
                new BeanPropertyRowMapper<>(Student.class),
                id
            );
            return student;
        } catch (Exception e) {
            System.err.println("Error fetching student by ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Retrieve all students from database
     */
    @Override
    public List<Student> getAll() {
        try {
            List<Student> students = jdbcTemplate.query(
                SQL_SELECT_ALL,
                new BeanPropertyRowMapper<>(Student.class)
            );
            return students;
        } catch (Exception e) {
            System.err.println("Error fetching all students: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Search students by name (partial match)
     */
    @Override
    public List<Student> searchByName(String name) {
        try {
            String searchPattern = "%" + name + "%";
            List<Student> students = jdbcTemplate.query(
                SQL_SEARCH_BY_NAME,
                new BeanPropertyRowMapper<>(Student.class),
                searchPattern
            );
            return students;
        } catch (Exception e) {
            System.err.println("Error searching students: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get total count of students
     */
    @Override
    public int getCount() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                SQL_SELECT_COUNT,
                Integer.class
            );
            return count != null ? count : 0;
        } catch (Exception e) {
            System.err.println("Error counting students: " + e.getMessage());
            return 0;
        }
    }
}
