package com.aige.apronsmart.services;

import com.aige.apronsmart.models.AuthResponse;
import com.aige.apronsmart.models.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Authentication service for UbuntuAirLab API
 * Uses OAuth2 password flow with form-urlencoded
 */
public class AuthService extends BaseApiService {
    
    private static AuthService instance;
    private User currentUser;
    
    private AuthService() {}
    
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    /**
     * Login with email and password
     * @param email User email
     * @param password User password
     * @return AuthResponse containing access token and user info
     * @throws IOException if authentication fails
     */
    public AuthResponse login(String email, String password) throws IOException {
        // Build form-urlencoded data
        // API expects: username=email&password=password
        String formData = "username=" + URLEncoder.encode(email, StandardCharsets.UTF_8) + 
                         "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
        
        logger.info("Authenticating user: {}", email);
        
        AuthResponse response = postFormUrlEncoded("/auth/login", formData, AuthResponse.class);
        
        if (response != null && response.getAccessToken() != null) {
            setAuthToken(response.getAccessToken());
            this.currentUser = response.getUser();
            logger.info("User authenticated successfully: {}", email);
        } else {
            logger.error("Authentication failed for user: {}", email);
            throw new IOException("Authentication failed: Invalid credentials");
        }
        
        return response;
    }
    
    /**
     * Register a new user
     * @param email User email
     * @param password User password
     * @param fullName User full name
     * @param role User role (operator, admin, viewer)
     * @return AuthResponse containing access token and user info
     * @throws IOException if registration fails
     */
    public AuthResponse register(String email, String password, String fullName, String role) throws IOException {
        RegisterRequest request = new RegisterRequest(email, password, fullName, role);
        
        logger.info("Registering new user: {}", email);
        
        AuthResponse response = post("/auth/register", request, AuthResponse.class);
        
        if (response != null && response.getAccessToken() != null) {
            setAuthToken(response.getAccessToken());
            this.currentUser = response.getUser();
            logger.info("User registered successfully: {}", email);
        }
        
        return response;
    }
    
    /**
     * Register a new user with Map data (for UI)
     * @param userData Map containing username, email, password, role
     * @return Map with registration response
     * @throws IOException if registration fails
     */
    public Map<String, Object> register(Map<String, Object> userData) throws IOException {
        logger.info("Registering new user: {}", userData.get("username"));
        
        String jsonBody = objectMapper.writeValueAsString(userData);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonBody, JSON);
        
        okhttp3.Request request = buildRequest("/auth/register")
                .post(body)
                .build();
        
        try (okhttp3.Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                logger.error("API Error {}: {}", response.code(), errorBody);
                throw new IOException("API Error " + response.code() + ": " + errorBody);
            }
            
            String responseBody = response.body().string();
            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
            
            // Extract token if present
            if (result.containsKey("access_token")) {
                setAuthToken((String) result.get("access_token"));
            }
            
            return result;
        }
    }
    
    /**
     * Get current authenticated user profile
     * @return User profile
     * @throws IOException if request fails
     */
    public User getCurrentUserProfile() throws IOException {
        logger.info("Fetching current user profile");
        User user = get("/auth/me", User.class);
        this.currentUser = user;
        return user;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        logger.info("Logging out user");
        clearAuthToken();
        this.currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public boolean isAuthenticated() {
        return currentUser != null && authToken != null;
    }
    
    /**
     * Registration request model
     */
    public static class RegisterRequest {
        private String email;
        private String password;
        private String full_name;
        private String role;
        
        public RegisterRequest(String email, String password, String fullName, String role) {
            this.email = email;
            this.password = password;
            this.full_name = fullName;
            this.role = role != null ? role : "operator";
        }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFull_name() { return full_name; }
        public void setFull_name(String full_name) { this.full_name = full_name; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
