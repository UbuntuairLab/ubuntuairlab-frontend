package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.services.NotificationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for Notifications module - UbuntuAirLab
 */
public class NotificationsController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationsController.class);
    
    @FXML private Label unreadCountLabel;
    @FXML private Label criticalCountLabel;
    @FXML private VBox notificationsContainer;
    @FXML private VBox emptyState;
    @FXML private Button filterAllBtn;
    @FXML private Button filterUnreadBtn;
    @FXML private Button filterCriticalBtn;
    
    private final NotificationService notificationService = NotificationService.getInstance();
    private List<Map<String, Object>> allNotifications = new ArrayList<>();
    private String currentFilter = "all";
    
    @FXML
    public void initialize() {
        loadNotifications();
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleRefresh() {
        loadNotifications();
    }
    
    @FXML
    private void handleMarkAllRead() {
        new Thread(() -> {
            try {
                notificationService.markAllRead();
                Platform.runLater(() -> {
                    loadNotifications();
                    showSuccess("Toutes les notifications ont été marquées comme lues");
                });
            } catch (Exception e) {
                logger.error("Error marking all notifications as read", e);
                Platform.runLater(() -> showError("Erreur", "Impossible de marquer les notifications comme lues"));
            }
        }).start();
    }
    
    @FXML
    private void handleFilterAll() {
        currentFilter = "all";
        updateFilterButtons();
        displayNotifications(allNotifications);
    }
    
    @FXML
    private void handleFilterUnread() {
        currentFilter = "unread";
        updateFilterButtons();
        List<Map<String, Object>> filtered = allNotifications.stream()
            .filter(n -> !((Boolean) n.getOrDefault("is_read", false)))
            .toList();
        displayNotifications(filtered);
    }
    
    @FXML
    private void handleFilterCritical() {
        currentFilter = "critical";
        updateFilterButtons();
        List<Map<String, Object>> filtered = allNotifications.stream()
            .filter(n -> "critical".equals(n.get("priority")))
            .toList();
        displayNotifications(filtered);
    }
    
    private void updateFilterButtons() {
        filterAllBtn.getStyleClass().remove("filter-button-active");
        filterUnreadBtn.getStyleClass().remove("filter-button-active");
        filterCriticalBtn.getStyleClass().remove("filter-button-active");
        
        switch (currentFilter) {
            case "all":
                filterAllBtn.getStyleClass().add("filter-button-active");
                break;
            case "unread":
                filterUnreadBtn.getStyleClass().add("filter-button-active");
                break;
            case "critical":
                filterCriticalBtn.getStyleClass().add("filter-button-active");
                break;
        }
    }
    
    private void loadNotifications() {
        new Thread(() -> {
            try {
                List<Map<String, Object>> notifications = notificationService.getAllNotifications();
                Map<String, Object> unreadCount = notificationService.getUnreadCount();
                
                Platform.runLater(() -> {
                    allNotifications = notifications;
                    displayNotifications(notifications);
                    
                    // Update counters
                    int unread = ((Number) unreadCount.getOrDefault("unread_count", 0)).intValue();
                    long critical = notifications.stream()
                        .filter(n -> "critical".equals(n.get("priority")))
                        .count();
                    
                    unreadCountLabel.setText(String.valueOf(unread));
                    criticalCountLabel.setText(String.valueOf(critical));
                });
            } catch (Exception e) {
                logger.error("Error loading notifications", e);
                Platform.runLater(() -> showError("Erreur", "Impossible de charger les notifications"));
            }
        }).start();
    }
    
    private void displayNotifications(List<Map<String, Object>> notifications) {
        notificationsContainer.getChildren().clear();
        
        if (notifications.isEmpty()) {
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            return;
        }
        
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        
        for (Map<String, Object> notification : notifications) {
            VBox card = createNotificationCard(notification);
            notificationsContainer.getChildren().add(card);
        }
    }
    
    private VBox createNotificationCard(Map<String, Object> notification) {
        VBox card = new VBox(8);
        card.getStyleClass().add("notification-card");
        card.setPadding(new Insets(12));
        
        boolean isRead = (Boolean) notification.getOrDefault("is_read", false);
        String priority = (String) notification.getOrDefault("priority", "normal");
        
        if (!isRead) {
            card.getStyleClass().add("notification-unread");
        }
        if ("critical".equals(priority)) {
            card.getStyleClass().add("notification-critical");
        }
        
        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Priority icon
        String icon = getPriorityIcon(priority);
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(20));
        
        // Title
        Label titleLabel = new Label((String) notification.getOrDefault("title", "Notification"));
        titleLabel.getStyleClass().add("notification-title");
        titleLabel.setFont(Font.font("Inter SemiBold", 14));
        titleLabel.setWrapText(true);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        
        // Timestamp
        String timestampStr = (String) notification.get("created_at");
        String formattedTime = formatTimestamp(timestampStr);
        Label timeLabel = new Label(formattedTime);
        timeLabel.getStyleClass().add("notification-time");
        timeLabel.setFont(Font.font("Inter", 11));
        
        header.getChildren().addAll(iconLabel, titleLabel, timeLabel);
        
        // Message
        Label messageLabel = new Label((String) notification.getOrDefault("message", ""));
        messageLabel.getStyleClass().add("notification-message");
        messageLabel.setFont(Font.font("Inter", 13));
        messageLabel.setWrapText(true);
        
        // Actions
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_RIGHT);
        
        if (!isRead) {
            Button markReadBtn = new Button("Marquer comme lu");
            markReadBtn.getStyleClass().add("notification-action-button");
            markReadBtn.setFont(Font.font("Inter", 11));
            markReadBtn.setOnAction(e -> markAsRead(notification));
            actions.getChildren().add(markReadBtn);
        }
        
        card.getChildren().addAll(header, messageLabel, actions);
        
        return card;
    }
    
    private String getPriorityIcon(String priority) {
        return switch (priority) {
            case "critical" -> "🔴";
            case "high" -> "🟠";
            case "medium" -> "🟡";
            default -> "🔵";
        };
    }
    
    private String formatTimestamp(String timestamp) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime now = LocalDateTime.now();
            
            long minutesAgo = java.time.Duration.between(dateTime, now).toMinutes();
            if (minutesAgo < 1) return "À l'instant";
            if (minutesAgo < 60) return minutesAgo + " min";
            if (minutesAgo < 1440) return (minutesAgo / 60) + "h";
            return (minutesAgo / 1440) + "j";
        } catch (Exception e) {
            return "";
        }
    }
    
    private void markAsRead(Map<String, Object> notification) {
        // Implementation depends on API endpoint
        logger.info("Marking notification as read: {}", notification.get("id"));
        loadNotifications();
    }
    
    private void navigateToDashboard() {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(
                getClass().getResource("/fxml/dashboard.fxml")
            );
            javafx.stage.Stage stage = (javafx.stage.Stage) notificationsContainer.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            logger.error("Error navigating to dashboard", e);
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
