package com.aige.apronsmart.services;

import java.io.IOException;
import java.util.Map;

/**
 * Service for admin and monitoring operations
 */
public class AdminService extends BaseApiService {
    
    private static AdminService instance;
    
    private AdminService() {}
    
    public static AdminService getInstance() {
        if (instance == null) {
            instance = new AdminService();
        }
        return instance;
    }
    
    // Configuration
    @SuppressWarnings("unchecked")
    public Map<String, Object> getConfig() throws IOException {
        String response = httpClient.newCall(
                buildRequest("/admin/config").get().build()
        ).execute().body().string();
        return objectMapper.readValue(response, Map.class);
    }
    
    public Map<String, Object> updateConfig(Map<String, Object> config) throws IOException {
        String json = objectMapper.writeValueAsString(config);
        String response = httpClient.newCall(
                buildRequest("/admin/config")
                        .patch(okhttp3.RequestBody.create(json, JSON))
                        .build()
        ).execute().body().string();
        return objectMapper.readValue(response, Map.class);
    }
    
    // Health & Monitoring (public endpoints, no auth)
    public String healthCheck() throws IOException {
        String url = baseUrl.replace("/api/v1", "") + "/health";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        String response = httpClient.newCall(request).execute().body().string();
        return response;
    }
    
    public String getMetrics() throws IOException {
        String url = baseUrl.replace("/api/v1", "") + "/metrics";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        String response = httpClient.newCall(request).execute().body().string();
        return response;
    }
}
