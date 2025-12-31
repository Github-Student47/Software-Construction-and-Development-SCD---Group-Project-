package com.scd.project.controller;

import com.scd.project.dao.StudentDAO;
import com.scd.project.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Student Controller - Handles all HTTP requests related to Student operations
 * 
 * This controller acts as the intermediary between the Model (Student data)
 * and the View (JSP pages), following the MVC architecture pattern.
 * 
 * Controller responsibilities:
 * - Receive HTTP requests from clients
 * - Call appropriate DAO methods for data operations
 * - Add data to the model for view rendering
 * - Return view names for rendering responses
 * 
 * Demonstrates:
 * - MVC Controller pattern
 * - Spring annotations (@Controller, @Autowired, @GetMapping, @PostMapping)
 * - Request parameter handling
 * - Path variables for RESTful URLs
 * - Model attribute binding
 * 
 * Team Members:
 * - Muhammad Kashan Tariq (212145)
 * - Adeel Hussain (221829)
 * - Syed Abdain (221855)
 * - Muhammad Tauseef (221789)
 */
@Controller
public class StudentController {
    
    // DAO for database operations
    @Autowired
    private StudentDAO studentDAO;
    
    /**
     * Home page - redirects to student list
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/students";
    }
    
    /**
     * Display list of all students
     * URL: /students or /student-list
     */
    @GetMapping("/students")
    public String listStudents(Model model) {
        // Get all students from database
        List<Student> students = studentDAO.getAll();
        
        // Add students to model for view
        model.addAttribute("students", students);
        model.addAttribute("title", "Student List - USMS");
        
        // Return view name (will resolve to /WEB-INF/views/list-students.jsp)
        return "list-students";
    }
    
    /**
     * Display add student form
     * URL: /students/add
     */
    @GetMapping("/students/add")
    public String showAddForm(Model model) {
        // Create new Student object for form binding
        Student student = new Student();
        
        // Add empty student to model
        model.addAttribute("student", student);
        model.addAttribute("title", "Add New Student - USMS");
        model.addAttribute("action", "Add");
        
        return "student-form";
    }
    
    /**
     * Process add student form submission
     * URL: /students/save (POST)
     */
    @PostMapping("/students/save")
    public String saveStudent(@ModelAttribute("student") Student student) {
        // Save student to database
        studentDAO.save(student);
        
        // Redirect to student list page
        return "redirect:/students";
    }
    
    /**
     * Display edit student form
     * URL: /students/edit/{id}
     */
    @GetMapping("/students/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        // Get student by ID from database
        Student student = studentDAO.getById(id);
        
        if (student != null) {
            model.addAttribute("student", student);
            model.addAttribute("title", "Edit Student - USMS");
            model.addAttribute("action", "Update");
            return "student-form";
        } else {
            return "redirect:/students";
        }
    }
    
    /**
     * Process edit student form submission
     * URL: /students/update (POST)
     */
    @PostMapping("/students/update")
    public String updateStudent(@ModelAttribute("student") Student student) {
        // Update student in database
        studentDAO.update(student);
        
        // Redirect to student list page
        return "redirect:/students";
    }
    
    /**
     * Delete a student
     * URL: /students/delete/{id}
     */
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable int id) {
        // Delete student from database
        studentDAO.delete(id);
        
        // Redirect to student list page
        return "redirect:/students";
    }
    
    /**
     * Search students by name
     * URL: /students/search
     */
    @GetMapping("/students/search")
    public String searchStudents(
            @RequestParam("keyword") String keyword,
            Model model) {
        
        List<Student> students;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            // If no keyword, show all students
            students = studentDAO.getAll();
        } else {
            // Search by name
            students = studentDAO.searchByName(keyword);
        }
        
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", "Search Results - USMS");
        
        return "list-students";
    }
    
    /**
     * Display student details
     * URL: /students/view/{id}
     */
    @GetMapping("/students/view/{id}")
    public String viewStudent(@PathVariable int id, Model model) {
        // Get student by ID from database
        Student student = studentDAO.getById(id);
        
        if (student != null) {
            model.addAttribute("student", student);
            model.addAttribute("title", "Student Details - USMS");
            return "student-view";
        } else {
            return "redirect:/students";
        }
    }
}
