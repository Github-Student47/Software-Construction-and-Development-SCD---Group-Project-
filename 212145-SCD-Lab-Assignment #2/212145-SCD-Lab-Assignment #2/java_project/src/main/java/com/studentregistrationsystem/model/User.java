package com.studentregistrationsystem.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User Model Class
 * Represents a user in the Student Course Registration System
 * Handles authentication, authorization, and user management
 * 
 * @author Muhammad Kashan Tariq
 * @version 1.0
 * @since 2025-10-27
 */
public class User {
    
    // Primary key and authentication fields
    private int userId;
    private String username;
    private String password; // Should be encrypted in production
    private String email;
    private String firstName;
    private String lastName;
    
    // Authorization and role management
    private String role; // Admin, Student, Faculty, Staff
    private List<String> permissions;
    private boolean isActive;
    private boolean isVerified;
    
    // Account security
    private String securityQuestion;
    private String securityAnswer; // Should be hashed in production
    private int failedLoginAttempts;
    private LocalDateTime lastFailedLogin;
    private boolean isLocked;
    private LocalDateTime lockExpiration;
    
    // Activity tracking
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;
    private LocalDateTime passwordLastChanged;
    private int loginCount;
    
    // Session management
    private String sessionToken;
    private LocalDateTime sessionExpiry;
    private String ipAddress;
    private String userAgent;
    
    /**
     * Default Constructor
     * Creates an empty User object
     */
    public User() {
        this.permissions = new ArrayList<>();
        this.isActive = true;
        this.isVerified = false;
        this.failedLoginAttempts = 0;
        this.isLocked = false;
        this.loginCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.passwordLastChanged = LocalDateTime.now();
    }
    
    /**
     * Parameterized Constructor
     * Creates a User object with basic authentication data
     * 
     * @param username Unique username
     * @param password User password
     * @param email User email
     * @param role User role (Admin, Student, Faculty, Staff)
     */
    public User(String username, String password, String email, String role) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        initializePermissions();
    }
    
    /**
     * Initialize Permissions
     * Sets up default permissions based on user role
     */
    private void initializePermissions() {
        permissions.clear();
        
        switch (role != null ? role.toLowerCase() : "") {
            case "admin":
                permissions.add("create_student");
                permissions.add("read_student");
                permissions.add("update_student");
                permissions.add("delete_student");
                permissions.add("create_course");
                permissions.add("read_course");
                permissions.add("update_course");
                permissions.add("delete_course");
                permissions.add("manage_grades");
                permissions.add("generate_reports");
                permissions.add("system_admin");
                break;
            case "faculty":
                permissions.add("read_student");
                permissions.add("update_student");
                permissions.add("read_course");
                permissions.add("update_course");
                permissions.add("manage_grades");
                permissions.add("view_own_courses");
                break;
            case "student":
                permissions.add("read_own_profile");
                permissions.add("update_own_profile");
                permissions.add("view_courses");
                permissions.add("register_course");
                permissions.add("view_own_grades");
                permissions.add("view_own_registrations");
                break;
            case "staff":
                permissions.add("read_student");
                permissions.add("read_course");
                permissions.add("view_registrations");
                permissions.add("generate_basic_reports");
                break;
        }
    }
    
    /**
     * Login Method
     * Processes user login with security checks
     * 
     * @param username Username provided
     * @param password Password provided
     * @return LoginResult object with success status and details
     */
    public LoginResult login(String username, String password) {
        // Check if user is locked
        if (isLocked) {
            if (lockExpiration != null && LocalDateTime.now().isAfter(lockExpiration)) {
                // Lock has expired, reset lock status
                this.isLocked = false;
                this.failedLoginAttempts = 0;
            } else {
                return new LoginResult(false, "Account is temporarily locked. Try again later.");
            }
        }
        
        // Verify username and password
        if (this.username.equals(username) && this.password.equals(password)) {
            // Successful login
            this.lastLogin = LocalDateTime.now();
            this.loginCount++;
            this.failedLoginAttempts = 0;
            this.updatedAt = LocalDateTime.now();
            
            // Generate new session token
            this.sessionToken = generateSessionToken();
            this.sessionExpiry = LocalDateTime.now().plusHours(24);
            
            return new LoginResult(true, "Login successful", sessionToken);
        } else {
            // Failed login
            this.failedLoginAttempts++;
            this.lastFailedLogin = LocalDateTime.now();
            
            // Lock account after 5 failed attempts
            if (this.failedLoginAttempts >= 5) {
                this.isLocked = true;
                this.lockExpiration = LocalDateTime.now().plusMinutes(30);
                return new LoginResult(false, "Account locked due to too many failed login attempts.");
            }
            
            return new LoginResult(false, "Invalid username or password.");
        }
    }
    
    /**
     * Logout Method
     * Processes user logout and session termination
     * 
     * @return true if logout successful
     */
    public boolean logout() {
        try {
            this.sessionToken = null;
            this.sessionExpiry = null;
            this.ipAddress = null;
            this.userAgent = null;
            this.updatedAt = LocalDateTime.now();
            
            System.out.println("User " + username + " logged out successfully");
            return true;
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate Session
     * Checks if current session is still valid
     * 
     * @param sessionToken Token to validate
     * @return true if session is valid, false otherwise
     */
    public boolean validateSession(String sessionToken) {
        return this.sessionToken != null && 
               this.sessionToken.equals(sessionToken) &&
               this.sessionExpiry != null && 
               LocalDateTime.now().isBefore(this.sessionExpiry);
    }
    
    /**
     * Check Permission
     * Verifies if user has specific permission
     * 
     * @param permission Permission to check
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }
    
    /**
     * Add Permission
     * Adds a new permission to the user
     * 
     * @param permission Permission to add
     * @return true if added successfully
     */
    public boolean addPermission(String permission) {
        if (permissions != null && !permissions.contains(permission)) {
            permissions.add(permission);
            this.updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }
    
    /**
     * Remove Permission
     * Removes a permission from the user
     * 
     * @param permission Permission to remove
     * @return true if removed successfully
     */
    public boolean removePermission(String permission) {
        if (permissions != null) {
            boolean removed = permissions.remove(permission);
            if (removed) {
                this.updatedAt = LocalDateTime.now();
            }
            return removed;
        }
        return false;
    }
    
    /**
     * Change Password
     * Updates user password with security validation
     * 
     * @param currentPassword Current password
     * @param newPassword New password
     * @param confirmPassword Confirmation of new password
     * @return true if password change successful, false otherwise
     */
    public boolean changePassword(String currentPassword, String newPassword, String confirmPassword) {
        // Validate current password
        if (!this.password.equals(currentPassword)) {
            return false;
        }
        
        // Validate new password
        if (newPassword == null || newPassword.length() < 8) {
            return false;
        }
        
        // Validate password confirmation
        if (!newPassword.equals(confirmPassword)) {
            return false;
        }
        
        // Update password
        this.password = newPassword;
        this.passwordLastChanged = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        return true;
    }
    
    /**
     * Generate Session Token
     * Creates a unique session token for the user
     * 
     * @return String representing session token
     */
    private String generateSessionToken() {
        return "token_" + userId + "_" + System.currentTimeMillis();
    }
    
    /**
     * Get User Display Name
     * Returns formatted user name for display
     * 
     * @return String containing full name or username
     */
    public String getDisplayName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username;
    }
    
    /**
     * Get User Summary
     * Returns formatted user information summary
     * 
     * @return String containing user summary
     */
    public String getUserSummary() {
        return String.format("User: %s (%s) | Role: %s | Status: %s | Last Login: %s",
                           getDisplayName(), username, role,
                           isActive ? "Active" : "Inactive",
                           lastLogin != null ? lastLogin.toString() : "Never");
    }
    
    /**
     * Check Security Status
     * Returns security status information
     * 
     * @return String containing security status
     */
    public String getSecurityStatus() {
        String status = String.format("Active: %s | Verified: %s | Locked: %s | Failed Attempts: %d",
                                    isActive ? "Yes" : "No",
                                    isVerified ? "Yes" : "No",
                                    isLocked ? "Yes" : "No",
                                    failedLoginAttempts);
        return status;
    }
    
    // Getter and Setter methods following JavaBean conventions
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        this.passwordLastChanged = LocalDateTime.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
        initializePermissions();
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public boolean isVerified() {
        return isVerified;
    }
    
    public void setVerified(boolean verified) {
        isVerified = verified;
    }
    
    public String getSecurityQuestion() {
        return securityQuestion;
    }
    
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
    
    public String getSecurityAnswer() {
        return securityAnswer;
    }
    
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
    
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getLastFailedLogin() {
        return lastFailedLogin;
    }
    
    public void setLastFailedLogin(LocalDateTime lastFailedLogin) {
        this.lastFailedLogin = lastFailedLogin;
    }
    
    public boolean isLocked() {
        return isLocked;
    }
    
    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    
    public LocalDateTime getLockExpiration() {
        return lockExpiration;
    }
    
    public void setLockExpiration(LocalDateTime lockExpiration) {
        this.lockExpiration = lockExpiration;
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
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getPasswordLastChanged() {
        return passwordLastChanged;
    }
    
    public void setPasswordLastChanged(LocalDateTime passwordLastChanged) {
        this.passwordLastChanged = passwordLastChanged;
    }
    
    public int getLoginCount() {
        return loginCount;
    }
    
    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }
    
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    public LocalDateTime getSessionExpiry() {
        return sessionExpiry;
    }
    
    public void setSessionExpiry(LocalDateTime sessionExpiry) {
        this.sessionExpiry = sessionExpiry;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    /**
     * LoginResult Inner Class
     * Encapsulates the result of a login attempt
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final String sessionToken;
        
        public LoginResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public LoginResult(boolean success, String message, String sessionToken) {
            this.success = success;
            this.message = message;
            this.sessionToken = sessionToken;
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
    }
    
    /**
     * toString method for debugging and logging
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                ", isVerified=" + isVerified +
                ", failedLoginAttempts=" + failedLoginAttempts +
                ", isLocked=" + isLocked +
                ", createdAt=" + createdAt +
                '}';
    }
    
    /**
     * equals method for object comparison
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId == user.userId || username != null && username.equals(user.username);
    }
    
    /**
     * hashCode method for collections
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId, username);
    }
}