package com.studentregistrationsystem.dao;

import com.studentregistrationsystem.model.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO (Data Access Object)
 * Handles all database operations for User entities
 * Manages user authentication and authorization data
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class UserDAO {
    
    // Database connection configuration
    private static final String URL = "jdbc:mysql://localhost:3306/student_registration";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";
    
    // SQL queries for CRUD operations
    private static final String INSERT_USER = 
        "INSERT INTO users (username, password, email, first_name, last_name, role, is_active, is_verified, failed_login_attempts, is_locked, lock_expiration, login_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_USER_BY_ID = 
        "SELECT * FROM users WHERE user_id = ?";
    
    private static final String SELECT_USER_BY_USERNAME = 
        "SELECT * FROM users WHERE username = ?";
    
    private static final String SELECT_USER_BY_EMAIL = 
        "SELECT * FROM users WHERE email = ?";
    
    private static final String SELECT_ALL_USERS = 
        "SELECT * FROM users ORDER BY created_at DESC";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET password = ?, email = ?, first_name = ?, last_name = ?, role = ?, is_active = ?, is_verified = ?, failed_login_attempts = ?, last_failed_login = ?, is_locked = ?, lock_expiration = ?, last_login = ?, password_last_changed = ?, login_count = ? WHERE user_id = ?";
    
    private static final String UPDATE_LOGIN_ATTEMPTS = 
        "UPDATE users SET failed_login_attempts = ?, last_failed_login = ?, is_locked = ?, lock_expiration = ? WHERE user_id = ?";
    
    private static final String UPDATE_SUCCESSFUL_LOGIN = 
        "UPDATE users SET last_login = ?, login_count = login_count + 1, failed_login_attempts = 0, is_locked = FALSE WHERE user_id = ?";
    
    private static final String DELETE_USER = 
        "DELETE FROM users WHERE user_id = ?";
    
    private static final String COUNT_USERS = 
        "SELECT COUNT(*) FROM users";
    
    private static final String FIND_USERS_BY_ROLE = 
        "SELECT * FROM users WHERE role = ? ORDER BY created_at DESC";
    
    private static final String FIND_USERS_BY_STATUS = 
        "SELECT * FROM users WHERE is_active = ? ORDER BY created_at DESC";
    
    private static final String SEARCH_USERS = 
        "SELECT * FROM users WHERE username LIKE ? OR email LIKE ? OR first_name LIKE ? OR last_name LIKE ? ORDER BY created_at DESC";
    
    /**
     * Get database connection
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Save User
     * Inserts a new user into the database
     * 
     * @param user User object to save
     * @throws SQLException if database operation fails
     */
    public void save(User user) throws SQLException {
        if (user.getUserId() == 0) {
            // Insert new user
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getEmail());
                stmt.setString(4, user.getFirstName());
                stmt.setString(5, user.getLastName());
                stmt.setString(6, user.getRole());
                stmt.setBoolean(7, user.isActive());
                stmt.setBoolean(8, user.isVerified());
                stmt.setInt(9, user.getFailedLoginAttempts());
                stmt.setBoolean(10, user.isLocked());
                
                Timestamp lockExpiration = user.getLockExpiration();
                stmt.setTimestamp(11, lockExpiration != null ? Timestamp.valueOf(lockExpiration) : null);
                
                stmt.setInt(12, user.getLoginCount());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
                
                // Get generated ID
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                
                System.out.println("User saved successfully with ID: " + user.getUserId());
            }
        } else {
            // Update existing user
            update(user);
        }
    }
    
    /**
     * Find User by ID
     * Retrieves a user by their primary key
     * 
     * @param id User ID to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public User findById(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_ID)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find User by Username
     * Retrieves a user by their username
     * 
     * @param username Username to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public User findByUsername(String username) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_USERNAME)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find User by Email
     * Retrieves a user by their email address
     * 
     * @param email Email to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public User findByEmail(String email) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_EMAIL)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find All Users
     * Retrieves all users from the database
     * 
     * @return List of all User objects
     * @throws SQLException if database operation fails
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    /**
     * Update User
     * Updates an existing user in the database
     * 
     * @param user User object to update
     * @throws SQLException if database operation fails
     */
    public void update(User user) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getRole());
            stmt.setBoolean(6, user.isActive());
            stmt.setBoolean(7, user.isVerified());
            stmt.setInt(8, user.getFailedLoginAttempts());
            
            Timestamp lastFailedLogin = user.getLastFailedLogin();
            stmt.setTimestamp(9, lastFailedLogin != null ? Timestamp.valueOf(lastFailedLogin) : null);
            
            stmt.setBoolean(10, user.isLocked());
            
            Timestamp lockExpiration = user.getLockExpiration();
            stmt.setTimestamp(11, lockExpiration != null ? Timestamp.valueOf(lockExpiration) : null);
            
            Timestamp lastLogin = user.getLastLogin();
            stmt.setTimestamp(12, lastLogin != null ? Timestamp.valueOf(lastLogin) : null);
            
            stmt.setTimestamp(13, Timestamp.valueOf(user.getPasswordLastChanged()));
            stmt.setInt(14, user.getLoginCount());
            stmt.setInt(15, user.getUserId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            
            System.out.println("User updated successfully: " + user.getUserId());
        }
    }
    
    /**
     * Update Failed Login Attempts
     * Updates failed login attempt count and lock status
     * 
     * @param userId User ID to update
     * @param failedAttempts Number of failed attempts
     * @param isLocked Whether user should be locked
     * @param lockExpiration Lock expiration timestamp
     * @throws SQLException if database operation fails
     */
    public void updateFailedLoginAttempts(int userId, int failedAttempts, boolean isLocked, LocalDateTime lockExpiration) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_LOGIN_ATTEMPTS)) {
            
            stmt.setInt(1, failedAttempts);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(3, isLocked);
            stmt.setTimestamp(4, lockExpiration != null ? Timestamp.valueOf(lockExpiration) : null);
            stmt.setInt(5, userId);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Update Successful Login
     * Updates user after successful login attempt
     * 
     * @param userId User ID to update
     * @throws SQLException if database operation fails
     */
    public void updateSuccessfulLogin(int userId) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SUCCESSFUL_LOGIN)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(2, userId);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Delete User
     * Removes a user from the database
     * 
     * @param id User ID to delete
     * @throws SQLException if database operation fails
     */
    public void delete(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Deleting user failed, no rows affected.");
            }
            
            System.out.println("User deleted successfully: " + id);
        }
    }
    
    /**
     * Count Users
     * Returns the total number of users in the database
     * 
     * @return int representing user count
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(COUNT_USERS);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Find Users by Role
     * Retrieves users by their role
     * 
     * @param role Role to filter by (Admin, Faculty, Student, Staff)
     * @return List of User objects with the specified role
     * @throws SQLException if database operation fails
     */
    public List<User> findByRole(String role) throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_USERS_BY_ROLE)) {
            
            stmt.setString(1, role);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Find Users by Status
     * Retrieves users by their active status
     * 
     * @param isActive Whether to filter by active status
     * @return List of User objects with the specified status
     * @throws SQLException if database operation fails
     */
    public List<User> findByStatus(boolean isActive) throws SQLException {
        List<User> users = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_USERS_BY_STATUS)) {
            
            stmt.setBoolean(1, isActive);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Search Users
     * Searches users by username, email, or name
     * 
     * @param searchTerm Search term to look for
     * @return List of matching User objects
     * @throws SQLException if database operation fails
     */
    public List<User> search(String searchTerm) throws SQLException {
        List<User> users = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_USERS)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Validate User Data
     * Validates user data before database operations
     * 
     * @param user User object to validate
     * @return List of validation error messages (empty if valid)
     */
    public List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();
        
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            errors.add("Username is required");
        }
        
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            errors.add("Password must be at least 6 characters long");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            errors.add("Email is required");
        } else if (!isValidEmail(user.getEmail())) {
            errors.add("Invalid email format");
        }
        
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            errors.add("Role is required");
        }
        
        return errors;
    }
    
    /**
     * Email Validation Helper
     * Simple email format validation
     * 
     * @param email Email to validate
     * @return true if email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Map Result Set to User Object
     * Helper method to convert database result set to User model object
     * 
     * @param rs ResultSet containing user data
     * @return User object populated with data
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        user.setVerified(rs.getBoolean("is_verified"));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));
        user.setLocked(rs.getBoolean("is_locked"));
        user.setLoginCount(rs.getInt("login_count"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }
        
        Timestamp lastFailedLogin = rs.getTimestamp("last_failed_login");
        if (lastFailedLogin != null) {
            user.setLastFailedLogin(lastFailedLogin.toLocalDateTime());
        }
        
        Timestamp lockExpiration = rs.getTimestamp("lock_expiration");
        if (lockExpiration != null) {
            user.setLockExpiration(lockExpiration.toLocalDateTime());
        }
        
        Timestamp passwordLastChanged = rs.getTimestamp("password_last_changed");
        if (passwordLastChanged != null) {
            user.setPasswordLastChanged(passwordLastChanged.toLocalDateTime());
        }
        
        return user;
    }
    
    /**
     * Get Users with Pagination
     * Retrieves users with pagination support
     * 
     * @param page Page number (starting from 1)
     * @param pageSize Number of records per page
     * @return List of User objects for the specified page
     * @throws SQLException if database operation fails
     */
    public List<User> findWithPagination(int page, int pageSize) throws SQLException {
        List<User> users = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT * FROM users ORDER BY created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        
        return users;
    }
    
    /**
     * Close Connection
     * Cleanup method to close database connections
     * This is handled automatically by try-with-resources blocks
     */
    public void close() {
        // Connection cleanup is handled by try-with-resources in getConnection
        System.out.println("UserDAO cleanup completed");
    }
}