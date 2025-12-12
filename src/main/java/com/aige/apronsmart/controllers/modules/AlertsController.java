package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.Alert;
import com.aige.apronsmart.services.AlertService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Alerts module - UbuntuAirLab
 */
public class AlertsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertsController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @FXML private VBox alertsContainer;
    @FXML private Button typeFilterButton;
    @FXML private Button statusFilterButton;
    @FXML private Button periodFilterButton;
    @FXML private ToggleGroup sortGroup;
    
    private final AlertService alertService = AlertService.getInstance();
    
    @FXML
    public void initialize() {
        loadAlerts();
    }
    
    private void loadAlerts() {
        new Thread(() -> {
            try {
                // Load conflicts and notifications from API
                com.aige.apronsmart.services.ParkingService parkingService = 
                    com.aige.apronsmart.services.ParkingService.getInstance();
                com.aige.apronsmart.services.NotificationService notificationService = 
                    com.aige.apronsmart.services.NotificationService.getInstance();
                
                List<java.util.Map<String, Object>> conflicts = parkingService.getConflicts();
                List<java.util.Map<String, Object>> notifications = notificationService.getCriticalNotifications();
                
                Platform.runLater(() -> {
                    displayAlertsFromAPI(conflicts, notifications);
                });
            } catch (Exception e) {
                logger.error("Error loading alerts from API", e);
                Platform.runLater(() -> displaySampleAlerts());
            }
        }).start();
    }
    
    private void displayAlertsFromAPI(List<java.util.Map<String, Object>> conflicts, 
                                      List<java.util.Map<String, Object>> notifications) {
        alertsContainer.getChildren().clear();
        
        // Display parking conflicts
        for (java.util.Map<String, Object> conflict : conflicts) {
            String title = "Conflit de parking - " + conflict.getOrDefault("spot_number", "N/A");
            String category = "Alerte opérationnelle";
            String time = "Il y a quelques instants";
            addAlertItem(title, category, time, "critical", "Nouveau");
        }
        
        // Display critical notifications
        for (java.util.Map<String, Object> notification : notifications) {
            String title = String.valueOf(notification.getOrDefault("message", "Notification"));
            String category = String.valueOf(notification.getOrDefault("type", "Alerte système"));
            String time = "Il y a quelques instants";
            String severity = String.valueOf(notification.getOrDefault("severity", "medium"));
            addAlertItem(title, category, time, severity, "Nouveau");
        }
        
        // If no alerts, show message
        if (conflicts.isEmpty() && notifications.isEmpty()) {
            Label noAlerts = new Label("✓ Aucune alerte active");
            noAlerts.setStyle("-fx-font-size: 16px; -fx-text-fill: #10b981; -fx-padding: 20px;");
            alertsContainer.getChildren().add(noAlerts);
        }
    }
    
    private void displaySampleAlerts() {
        // Display sample alerts for demo
        alertsContainer.getChildren().clear();
        
        addAlertItem("Capteur hors ligne - B12", "Alerte de sécurité", "Il y a 5m", "critical", "Nouveau");
        addAlertItem("Batterie faible - C04", "Alerte d'équipement", "Il y a 15m", "high", "Reconnu");
        addAlertItem("Obstruction détectée - A07", "Alerte opérationnelle", "Il y a 45m", "medium", "Reconnu");
        addAlertItem("Accès non autorisé - D11", "Alerte de sécurité", "Il y a 2h", "resolved", "Résolu");
    }
    
    private void displayAlerts(List<Alert> alerts) {
        alertsContainer.getChildren().clear();
        
        for (Alert alert : alerts) {
            String severity = alert.getSeverity().name().toLowerCase();
            String status = alert.getStatus().getDisplayName();
            String timeAgo = formatTimeAgo(alert.getCreatedAt());
            
            addAlertItem(alert.getTitle(), alert.getType().getDisplayName(), timeAgo, severity, status);
        }
    }
    
    private void addAlertItem(String title, String category, String time, String severity, String status) {
        HBox alertItem = new HBox(12);
        alertItem.setAlignment(Pos.TOP_LEFT);
        alertItem.getStyleClass().add("alert-item");
        alertItem.setPadding(new Insets(12));
        
        // Severity indicator
        Region indicator = new Region();
        indicator.setMinWidth(6);
        indicator.setMaxWidth(6);
        indicator.setMinHeight(48);
        indicator.setPrefHeight(48);
        indicator.getStyleClass().add("alert-indicator-" + severity);
        
        // Content
        VBox content = new VBox(4);
        HBox.setHgrow(content, Priority.ALWAYS);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("alert-title");
        titleLabel.setFont(Font.font("Inter SemiBold", 16));
        
        Label categoryLabel = new Label(category);
        categoryLabel.getStyleClass().add("alert-category");
        categoryLabel.setFont(Font.font("Inter", 14));
        
        HBox metaRow = new HBox(8);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("alert-time");
        timeLabel.setFont(Font.font("Inter", 12));
        
        Label statusBadge = new Label(status);
        statusBadge.setPadding(new Insets(2, 8, 2, 8));
        statusBadge.setFont(Font.font("Inter Medium", 12));
        
        switch (status.toLowerCase()) {
            case "nouveau":
                statusBadge.getStyleClass().add("badge-new");
                break;
            case "reconnu":
                statusBadge.getStyleClass().add("badge-acknowledged");
                break;
            case "résolu":
                statusBadge.getStyleClass().add("badge-resolved");
                break;
            default:
                statusBadge.getStyleClass().add("badge-new");
        }
        
        metaRow.getChildren().addAll(timeLabel, statusBadge);
        VBox.setMargin(metaRow, new Insets(4, 0, 0, 0));
        
        content.getChildren().addAll(titleLabel, categoryLabel, metaRow);
        
        // Chevron
        Label chevron = new Label("›");
        chevron.getStyleClass().add("chevron-icon");
        HBox.setMargin(chevron, new Insets(12, 0, 0, 0));
        
        alertItem.getChildren().addAll(indicator, content, chevron);
        alertItem.setOnMouseClicked(e -> handleAlertClick(title));
        
        alertsContainer.getChildren().add(alertItem);
    }
    
    private void handleAlertClick(String title) {
        logger.info("Alert clicked: {}", title);
        // TODO: Open alert details
    }
    
    private String formatTimeAgo(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "Il y a quelques instants";
        
        java.time.Duration duration = java.time.Duration.between(dateTime, java.time.LocalDateTime.now());
        long minutes = duration.toMinutes();
        
        if (minutes < 60) {
            return "Il y a " + minutes + "m";
        } else if (minutes < 1440) {
            return "Il y a " + (minutes / 60) + "h";
        } else {
            return "Il y a " + (minutes / 1440) + "j";
        }
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleSettings() {
        logger.info("Settings button clicked");
    }
    
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            AigApronSmartApplication.getPrimaryStage().setScene(scene);
        } catch (IOException e) {
            logger.error("Error navigating to dashboard", e);
        }
    }
    
    private void applyFilters() {
        // TODO: Implement filtering
        loadAlerts();
    }
}
