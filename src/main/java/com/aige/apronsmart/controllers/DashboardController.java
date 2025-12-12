package com.aige.apronsmart.controllers;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.User;
import com.aige.apronsmart.services.AuthService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main Dashboard Controller for UbuntuAirLab
 */
public class DashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @FXML private VBox mainContainer;
    @FXML private ImageView backgroundImageView;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label timeLabel;
    
    // Stats labels
    @FXML private Label occupiedPostesLabel;
    @FXML private Label utilizationRateLabel;
    @FXML private Label upcomingFlightsLabel;
    @FXML private Label systemStatusLabel;
    @FXML private Label alertCountBadge;
    
    // Module cards
    @FXML private StackPane radarButton;
    @FXML private StackPane visualization3dButton;
    @FXML private StackPane postesButton;
    @FXML private StackPane planningButton;
    @FXML private StackPane alertsButton;
    @FXML private StackPane historyButton;
    
    private final AuthService authService = AuthService.getInstance();
    private Timeline clockTimeline;
    
    @FXML
    public void initialize() {
        // Bind background image to fill the entire screen
        if (backgroundImageView != null && backgroundImageView.getParent() instanceof StackPane) {
            StackPane parent = (StackPane) backgroundImageView.getParent();
            backgroundImageView.fitWidthProperty().bind(parent.widthProperty());
            backgroundImageView.fitHeightProperty().bind(parent.heightProperty());
        }
        
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getFullName());
            // Display role with proper formatting
            String role = currentUser.getRole();
            if (role != null) {
                // Capitalize first letter
                String displayRole = role.substring(0, 1).toUpperCase() + role.substring(1);
                userRoleLabel.setText(displayRole);
            } else {
                userRoleLabel.setText("Opérateur");
            }
        } else {
            userNameLabel.setText("Utilisateur");
            userRoleLabel.setText("Opérateur");
        }
        
        // Start clock
        startClock();
        
        // Initialize stats (these would come from API in real app)
        updateStats();
    }
    
    private void startClock() {
        clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            timeLabel.setText(now.format(TIME_FORMATTER));
        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }
    
    private void updateStats() {
        // Fetch real-time stats from API
        new Thread(() -> {
            try {
                com.aige.apronsmart.services.DashboardService dashboardService = 
                    com.aige.apronsmart.services.DashboardService.getInstance();
                com.aige.apronsmart.models.DashboardStats stats = dashboardService.getStats();
                
                com.aige.apronsmart.services.ParkingService parkingService = 
                    com.aige.apronsmart.services.ParkingService.getInstance();
                com.aige.apronsmart.models.ParkingAvailability parking = parkingService.getAvailability();
                
                Platform.runLater(() -> {
                    occupiedPostesLabel.setText(parking.getOccupied() + " / " + parking.getTotalSpots());
                    utilizationRateLabel.setText(String.format("%.0f%%", parking.getUtilizationRate() * 100));
                    upcomingFlightsLabel.setText(String.valueOf(stats.getActiveFlights()));
                    systemStatusLabel.setText("Opérationnel");
                    alertCountBadge.setText(String.valueOf(stats.getConflictsDetected()));
                });
            } catch (Exception e) {
                logger.error("Error loading dashboard stats from API", e);
                Platform.runLater(() -> {
                    // Fallback to demo data
                    occupiedPostesLabel.setText("- / -");
                    utilizationRateLabel.setText("-");
                    upcomingFlightsLabel.setText("-");
                    systemStatusLabel.setText("Hors ligne");
                    alertCountBadge.setText("0");
                });
            }
        }).start();
    }
    
    @FXML
    private void loadRadarModule(MouseEvent event) {
        loadModule("/fxml/modules/radar.fxml", "Radar Live");
    }
    
    @FXML
    private void loadVisualization3dModule(MouseEvent event) {
        loadModule("/fxml/modules/visualization3d.fxml", "Visualisation 3D");
    }
    
    @FXML
    private void loadPostesModule(MouseEvent event) {
        loadModule("/fxml/modules/postes.fxml", "Gestion des Postes");
    }
    
    @FXML
    private void loadPlanningModule(MouseEvent event) {
        loadModule("/fxml/modules/planning.fxml", "Planification");
    }
    
    @FXML
    private void loadAlertsModule(MouseEvent event) {
        loadModule("/fxml/modules/alerts.fxml", "Alertes");
    }
    
    @FXML
    private void loadHistoryModule(MouseEvent event) {
        loadModule("/fxml/modules/history.fxml", "Historique");
    }
    
    @FXML
    private void loadPredictionsModule(MouseEvent event) {
        loadModule("/fxml/modules/predictions.fxml", "Prédictions ML");
    }
    
    private void loadModule(String fxmlPath, String moduleName) {
        try {
            logger.info("Loading module: {}", moduleName);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent moduleRoot = loader.load();
            
            Scene scene = new Scene(moduleRoot);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            
            AigApronSmartApplication.getPrimaryStage().setScene(scene);
        } catch (IOException e) {
            logger.error("Error loading module: {}", moduleName, e);
        }
    }
    
    @FXML
    private void handleLogout() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
        
        authService.logout();
        logger.info("User logged out");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            AigApronSmartApplication.getPrimaryStage().setScene(scene);
        } catch (IOException e) {
            logger.error("Error loading login screen", e);
        }
    }
}
