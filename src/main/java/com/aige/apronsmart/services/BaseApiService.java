package com.aige.apronsmart.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Base API service class with common HTTP operations
 */
public abstract class BaseApiService {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseApiService.class);
    protected static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    protected static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    
    protected static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    protected static final MediaType FORM_URLENCODED = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");
    
    protected static String baseUrl;
    protected static String authToken;
    
    static {
        loadConfiguration();
    }
    
    private static void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = BaseApiService.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                baseUrl = properties.getProperty("api.base.url", "http://localhost:8080/api");
            } else {
                baseUrl = "http://localhost:8080/api";
                logger.warn("application.properties not found, using default base URL: {}", baseUrl);
            }
        } catch (IOException e) {
            baseUrl = "http://localhost:8080/api";
            logger.error("Error loading configuration, using default base URL: {}", baseUrl, e);
        }
    }
    
    public static void setAuthToken(String token) {
        authToken = token;
    }
    
    public static void clearAuthToken() {
        authToken = null;
    }
    
    protected Request.Builder buildRequest(String endpoint) {
        Request.Builder builder = new Request.Builder()
                .url(baseUrl + endpoint);
        
        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }
        
        return builder;
    }
    
    protected <T> T get(String endpoint, Class<T> responseClass) throws IOException {
        Request request = buildRequest(endpoint).get().build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, responseClass);
        }
    }
    
    protected <T> T post(String endpoint, Object requestBody, Class<T> responseClass) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        
        Request request = buildRequest(endpoint)
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                logger.error("API Error {}: {}", response.code(), errorBody);
                throw new IOException("API Error " + response.code() + ": " + errorBody);
            }
            
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, responseClass);
        }
    }
    
    protected <T> T postFormUrlEncoded(String endpoint, String formData, Class<T> responseClass) throws IOException {
        RequestBody body = RequestBody.create(formData, FORM_URLENCODED);
        
        Request request = buildRequest(endpoint)
                .post(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                logger.error("API Error {}: {}", response.code(), errorBody);
                throw new IOException("API Error " + response.code() + ": " + errorBody);
            }
            
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, responseClass);
        }
    }
    
    protected <T> T put(String endpoint, Object requestBody, Class<T> responseClass) throws IOException {
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        RequestBody body = RequestBody.create(jsonBody, JSON);
        
        Request request = buildRequest(endpoint)
                .put(body)
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            String responseBody = response.body().string();
            return objectMapper.readValue(responseBody, responseClass);
        }
    }
    
    protected void delete(String endpoint) throws IOException {
        Request request = buildRequest(endpoint)
                .delete()
                .build();
        
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
        }
    }
}
