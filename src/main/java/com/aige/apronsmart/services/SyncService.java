package com.aige.apronsmart.services;

import java.io.IOException;
import java.util.Map;

/**
 * Service for data synchronization with UbuntuAirLab API
 */
public class SyncService extends BaseApiService {
    
    private static SyncService instance;
    
    private SyncService() {}
    
    public static SyncService getInstance() {
        if (instance == null) {
            instance = new SyncService();
        }
        return instance;
    }
    
    /**
     * Trigger manual data synchronization
     * @return Sync result
     */
    public Map<String, Object> triggerSync() throws IOException {
        logger.info("Triggering manual data synchronization");
        String responseStr = httpClient.newCall(
                buildRequest("/sync/trigger")
                    .post(okhttp3.RequestBody.create("", JSON))
                    .build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, Map.class);
    }
    
    /**
     * Get synchronization status
     * @return Sync status information
     */
    public Map<String, Object> getSyncStatus() throws IOException {
        logger.info("Fetching sync status");
        return get("/sync/status", Map.class);
    }
    
    /**
     * Set sync interval in minutes
     * @param minutes Interval in minutes
     * @return Updated sync configuration
     */
    public Map<String, Object> setSyncInterval(int minutes) throws IOException {
        logger.info("Setting sync interval to {} minutes", minutes);
        String responseStr = httpClient.newCall(
                buildRequest("/sync/interval/" + minutes)
                    .post(okhttp3.RequestBody.create("", JSON))
                    .build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, Map.class);
    }
    
    /**
     * Update sync interval in minutes (PATCH method)
     * @param minutes Interval in minutes
     * @return Updated sync configuration
     */
    public Map<String, Object> updateSyncInterval(int minutes) throws IOException {
        logger.info("Updating sync interval to {} minutes", minutes);
        String responseStr = httpClient.newCall(
                buildRequest("/sync/interval/" + minutes)
                    .patch(okhttp3.RequestBody.create("", JSON))
                    .build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, Map.class);
    }
}
