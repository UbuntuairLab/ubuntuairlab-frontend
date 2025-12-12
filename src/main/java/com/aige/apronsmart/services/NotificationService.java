package com.aige.apronsmart.services;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for notifications with UbuntuAirLab API
 */
public class NotificationService extends BaseApiService {
    
    private static NotificationService instance;
    
    private NotificationService() {}
    
    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    /**
     * Get all notifications
     * @return List of notifications
     */
    public List<Map<String, Object>> getAllNotifications() throws IOException {
        logger.info("Fetching all notifications");
        String responseStr = httpClient.newCall(
                buildRequest("/notifications/notifications").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<List<Map<String, Object>>>() {});
    }
    
    /**
     * Get critical notifications only
     * @return List of critical notifications
     */
    public List<Map<String, Object>> getCriticalNotifications() throws IOException {
        logger.info("Fetching critical notifications");
        String responseStr = httpClient.newCall(
                buildRequest("/notifications/notifications/critical").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<List<Map<String, Object>>>() {});
    }
    
    /**
     * Get count of unread notifications
     * @return Map with unread count
     */
    public Map<String, Object> getUnreadCount() throws IOException {
        logger.info("Fetching unread notification count");
        String responseStr = httpClient.newCall(
                buildRequest("/notifications/notifications/unread/count").get().build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * Acknowledge a notification
     * @param notificationId Notification ID
     * @return Acknowledgement result
     */
    public Map<String, Object> acknowledgeNotification(String notificationId) throws IOException {
        logger.info("Acknowledging notification: {}", notificationId);
        String responseStr = httpClient.newCall(
                buildRequest("/notifications/notifications/" + notificationId + "/acknowledge")
                    .post(okhttp3.RequestBody.create("", JSON))
                    .build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
    }
    
    /**
     * Mark all notifications as read
     * @return Operation result
     */
    public Map<String, Object> markAllRead() throws IOException {
        logger.info("Marking all notifications as read");
        String responseStr = httpClient.newCall(
                buildRequest("/notifications/notifications/mark-all-read")
                    .post(okhttp3.RequestBody.create("", JSON))
                    .build()
        ).execute().body().string();
        return objectMapper.readValue(responseStr, new TypeReference<Map<String, Object>>() {});
    }
}
