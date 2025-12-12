package com.aige.apronsmart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for authentication endpoints
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    private User user;
    
    public AuthResponse() {}
    
    public AuthResponse(String accessToken, String tokenType, User user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.user = user;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
