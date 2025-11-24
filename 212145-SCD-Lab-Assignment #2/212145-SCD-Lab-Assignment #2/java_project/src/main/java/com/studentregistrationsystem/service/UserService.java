package com.studentregistrationsystem.service;

import com.studentregistrationsystem.model.User;
import com.studentregistrationsystem.dao.UserDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserService
 * Implements business logic for user-related operations
 * Handles authentication, authorization, and user management
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class UserService {
    
    // Data Access Object
    private UserDAO userDAO;
    
    /**
     * Default Constructor
     * Initializes UserDAO
     */
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    /**
     * Parameterized Constructor
     * Allows injection of custom UserDAO for testing
     * 
     * @param userDAO UserDAO instance
     */
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    /**
     * Save User
     * Validates and saves a user to the database
     * 
     * @param user User object to save
     * @return true if save successful, false otherwise
     */
    public boolean saveUser(User user) {
        try {
            // Validate user data
            List<String> validationErrors = validateUser(user);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            // Check for duplicate username
            User existingUser = userDAO.findByUsername(user.getUsername());
            if (existingUser != null && existingUser.getUserId() != user.getUserId()) {
                System.err.println("Username already exists: " + user.getUsername());
                return false;
            }
            
            // Check for duplicate email
            User existingEmailUser = userDAO.findByEmail(user.getEmail());
            if (existingEmailUser != null && existingEmailUser.getUserId() != user.getUserId()) {
                System.err.println("Email already exists: " + user.getEmail());
                return false;
            }
            
            userDAO.save(user);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while saving user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find User by ID
     * Retrieves a user by their primary key
     * 
     * @param userId User ID to search for
     * @return User object if found, null otherwise
     */
    public User findUserById(int userId) {
        try {
            return userDAO.findById(userId);
        } catch (SQLException e) {
            System.err.println("Database error while finding user: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Find User by Username
     * Retrieves a user by their username
     * 
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public User findUserByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (SQLException e) {
            System.err.println("Database error while finding user: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Find All Users
     * Retrieves all users from the database
     * 
     * @return List of all User objects
     */
    public List<User> findAllUsers() {
        try {
            return userDAO.findAll();
        } catch (SQLException e) {
            System.err.println("Database error while finding all users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Update User
     * Updates an existing user in the database
     * 
     * @param user User object to update
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        try {
            // Validate user data
            List<String> validationErrors = validateUser(user);
            if (!validationErrors.isEmpty()) {
                System.err.println("Validation errors: " + validationErrors);
                return false;
            }
            
            userDAO.update(user);
            return true;
            
        } catch (SQLException e) {
            System.err.println("Database error while updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete User
     * Removes a user from the database
     * 
     * @param userId User ID to delete
     * @return true if delete successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        try {
            userDAO.delete(userId);
            return true;
        } catch (SQLException e) {
            System.err.println("Database error while deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Authenticate User
     * Handles user login authentication
     * 
     * @param username Username provided
     * @param password Password provided
     * @return LoginResult object with authentication status and details
     */
    public LoginResult authenticateUser(String username, String password) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return new LoginResult(false, "Username is required", null);
            }
            
            if (password == null || password.trim().isEmpty()) {
                return new LoginResult(false, "Password is required", null);
            }
            
            User user = userDAO.findByUsername(username.trim());
            if (user == null) {
                return new LoginResult(false, "Invalid username or password", null);
            }
            
            User.LoginResult loginResult = user.login(username.trim(), password);
            
            if (loginResult.isSuccess()) {
                // Update successful login in database
                userDAO.updateSuccessfulLogin(user.getUserId());
                return new LoginResult(true, loginResult.getMessage(), loginResult.getSessionToken(), user);
            } else {
                // Update failed login attempts
                userDAO.updateFailedLoginAttempts(user.getUserId(), 
                                                user.getFailedLoginAttempts(),
                                                user.isLocked(),
                                                user.getLockExpiration());
                return new LoginResult(false, loginResult.getMessage(), null, null);
            }
            
        } catch (Exception e) {
            return new LoginResult(false, "Authentication error: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Logout User
     * Handles user logout and session termination
     * 
     * @param userId User ID to logout
     * @return true if logout successful
     */
    public boolean logoutUser(int userId) {
        try {
            User user = findUserById(userId);
            if (user != null) {
                return user.logout();
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate Session
     * Checks if a session token is still valid
     * 
     * @param userId User ID
     * @param sessionToken Session token to validate
     * @return true if session is valid, false otherwise
     */
    public boolean validateSession(int userId, String sessionToken) {
        try {
            User user = findUserById(userId);
            if (user != null) {
                return user.validateSession(sessionToken);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error validating session: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Change User Password
     * Updates user's password with security validation
     * 
     * @param userId User ID to update password for
     * @param currentPassword Current password
     * @param newPassword New password
     * @param confirmPassword Confirmation of new password
     * @return true if password change successful, false otherwise
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword, String confirmPassword) {
        try {
            User user = findUserById(userId);
            if (user == null) {
                return false;
            }
            
            return user.changePassword(currentPassword, newPassword, confirmPassword);
            
        } catch (Exception e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create User
     * Handles the complete user creation process
     * 
     * @param username Unique username
     * @param password User password
     * @param email User email
     * @param firstName User's first name
     * @param lastName User's last name
     * @param role User role (Admin, Faculty, Student, Staff)
     * @return true if creation successful, false otherwise
     */
    public boolean createUser(String username, String password, String email, String firstName, 
                             String lastName, String role) {
        try {
            // Validate inputs
            if (username == null || username.trim().isEmpty()) {
                System.err.println("Username is required");
                return false;
            }
            
            if (password == null || password.length() < 6) {
                System.err.println("Password must be at least 6 characters long");
                return false;
            }
            
            if (email == null || email.trim().isEmpty()) {
                System.err.println("Email is required");
                return false;
            }
            
            if (role == null || role.trim().isEmpty()) {
                System.err.println("Role is required");
                return false;
            }
            
            // Check if user already exists
            User existingUser = findUserByUsername(username);
            if (existingUser != null) {
                System.err.println("Username already exists: " + username);
                return false;
            }
            
            // Create user object
            User user = new User(username, password, email, role);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setVerified(false);
            
            return saveUser(user);
            
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Find Users by Role
     * Retrieves users by their role
     * 
     * @param role Role to filter by (Admin, Faculty, Student, Staff)
     * @return List of User objects with the specified role
     */
    public List<User> findUsersByRole(String role) {
        try {
            return userDAO.findByRole(role);
        } catch (SQLException e) {
            System.err.println("Database error while finding users by role: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Find Users by Status
     * Retrieves users by their active status
     * 
     * @param isActive Whether to filter by active status
     * @return List of User objects with the specified status
     */
    public List<User> findUsersByStatus(boolean isActive) {
        try {
            return userDAO.findByStatus(isActive);
        } catch (SQLException e) {
            System.err.println("Database error while finding users by status: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Search Users
     * Searches users by username, email, or name
     * 
     * @param searchTerm Search term to look for
     * @return List of matching User objects
     */
    public List<User> searchUsers(String searchTerm) {
        try {
            return userDAO.search(searchTerm);
        } catch (SQLException e) {
            System.err.println("Database error while searching users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Check User Permission
     * Verifies if user has specific permission
     * 
     * @param userId User ID to check
     * @param permission Permission to verify
     * @return true if user has permission, false otherwise
     */
    public boolean checkUserPermission(int userId, String permission) {
        try {
            User user = findUserById(userId);
            if (user != null) {
                return user.hasPermission(permission);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error checking user permission: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add User Permission
     * Adds a new permission to the user
     * 
     * @param userId User ID to update
     * @param permission Permission to add
     * @return true if added successfully
     */
    public boolean addUserPermission(int userId, String permission) {
        try {
            User user = findUserById(userId);
            if (user != null) {
                boolean added = user.addPermission(permission);
                if (added) {
                    updateUser(user);
                }
                return added;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error adding user permission: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Activate User
     * Activates a user account
     * 
     * @param userId User ID to activate
     * @return true if activation successful
     */
    public boolean activateUser(int userId) {
        try {
            User user = findUserById(userId);
            if (user != null) {
                user.setActive(true);
                return updateUser(user);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error activating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deactivate User
     * Deactivates a user account
     * 
     * @param userId User ID to deactivate
     * @return true if deactivation successful
     */
    public boolean deactivateUser(int userId) {
        try {
            User user = findUserById(userId);
            if (user != null) {
                user.setActive(false);
                return updateUser(user);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate User Data
     * Validates user data before database operations
     * 
     * @param user User object to validate
     * @return List of validation error messages (empty if valid)
     */
    private List<String> validateUser(User user) {
        return userDAO.validateUser(user);
    }
    
    /**
     * Close Resources
     * Cleanup method to close database connections and resources
     */
    public void close() {
        if (userDAO != null) userDAO.close();
        System.out.println("UserService resources closed");
    }
    
    /**
     * LoginResult Inner Class
     * Encapsulates the result of a login attempt
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final String sessionToken;
        private final User user;
        
        public LoginResult(boolean success, String message, String sessionToken) {
            this(success, message, sessionToken, null);
        }
        
        public LoginResult(boolean success, String message, String sessionToken, User user) {
            this.success = success;
            this.message = message;
            this.sessionToken = sessionToken;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getSessionToken() {
            return sessionToken;
        }
        
        public User getUser() {
            return user;
        }
    }
}