package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Model representing a user (compatible with UbuntuAirLab API)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String username;
    private String email;
    
    @JsonProperty("full_name")
    private String fullName;
    
    private String firstName;
    private String lastName;
    
    private String role; // operator, admin, viewer
    private String department;
    private String phone;
    
    @JsonProperty("is_active")
    private Boolean isActive;
    
    @JsonProperty("last_login")
    private LocalDateTime lastLogin;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    // No-arg constructor
    public User() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
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
    }
    
    // Compatibility method for legacy UserRole enum
    public void setRole(UserRole roleEnum) {
        this.role = roleEnum.name().toLowerCase();
    }
    
    public String getFullName() {
        if (fullName != null && !fullName.isEmpty()) {
            return fullName;
        }
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return username != null ? username : email;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
        // Try to parse into first and last name
        if (fullName != null && fullName.contains(" ")) {
            String[] parts = fullName.split(" ", 2);
            this.firstName = parts[0];
            this.lastName = parts[1];
        }
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Legacy enum for compatibility
    public enum UserRole {
        ADMIN("Administrateur"),
        OPERATOR("Opérateur"),
        OPS("Opérateur"),
        ATC("Contrôleur ATC"),
        VIEWER("Observateur");
        
        private final String displayName;
        
        UserRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
}
