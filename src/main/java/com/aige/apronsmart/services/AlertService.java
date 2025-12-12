package com.aige.apronsmart.services;

import com.aige.apronsmart.models.Alert;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;

/**
 * Service for alert operations
 */
public class AlertService extends BaseApiService {
    
    private static AlertService instance;
    
    private AlertService() {}
    
    public static AlertService getInstance() {
        if (instance == null) {
            instance = new AlertService();
        }
        return instance;
    }
    
    public List<Alert> getAllAlerts() throws IOException {
        String response = httpClient.newCall(buildRequest("/alerts").get().build()).execute().body().string();
        return objectMapper.readValue(response, new TypeReference<List<Alert>>() {});
    }
    
    public List<Alert> getActiveAlerts() throws IOException {
        String response = httpClient.newCall(buildRequest("/alerts/active").get().build()).execute().body().string();
        return objectMapper.readValue(response, new TypeReference<List<Alert>>() {});
    }
    
    public Alert getAlertById(Long id) throws IOException {
        return get("/alerts/" + id, Alert.class);
    }
    
    public Alert acknowledgeAlert(Long id) throws IOException {
        return post("/alerts/" + id + "/acknowledge", null, Alert.class);
    }
    
    public Alert resolveAlert(Long id, String resolution) throws IOException {
        return post("/alerts/" + id + "/resolve", resolution, Alert.class);
    }
    
    public void dismissAlert(Long id) throws IOException {
        delete("/alerts/" + id);
    }
}
