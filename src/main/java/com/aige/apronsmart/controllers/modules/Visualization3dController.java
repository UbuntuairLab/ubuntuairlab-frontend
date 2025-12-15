package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.ParkingSpot;
import com.aige.apronsmart.services.ParkingService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Controller for 3D Visualization module - UbuntuAirLab
 * Shows real-time parking state in 3D
 */
public class Visualization3dController {
    
    private static final Logger logger = LoggerFactory.getLogger(Visualization3dController.class);
    
    @FXML private VBox visualization3dContainer;
    @FXML private WebView webView3d;
    @FXML private ComboBox<String> viewModeComboBox;
    @FXML private Slider zoomSlider;
    @FXML private CheckBox weatherCheckBox;
    @FXML private CheckBox labelsCheckBox;
    
    private WebEngine webEngine;
    private Timeline autoRefreshTimeline;
    private final ParkingService parkingService = ParkingService.getInstance();
    
    @FXML
    public void initialize() {
        setupControls();
        setup3DView();
        
        // Auto-refresh every 10 seconds for real-time updates
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            loadParkingData();
        }));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
    }
    
    private void loadParkingData() {
        new Thread(() -> {
            try {
                List<ParkingSpot> parkingSpots = parkingService.getAllParkingSpots();
                Platform.runLater(() -> updateParkingVisualization(parkingSpots));
            } catch (Exception e) {
                logger.error("Error loading parking data from API", e);
                Platform.runLater(() -> {
                    // Display error in UI if needed
                });
            }
        }).start();
    }
    
    private void updateParkingVisualization(List<ParkingSpot> parkingSpots) {
        if (webEngine != null && parkingSpots != null) {
            StringBuilder js = new StringBuilder("updateParkingData([");
            for (int i = 0; i < parkingSpots.size(); i++) {
                ParkingSpot spot = parkingSpots.get(i);
                String status = spot.getStatus() != null ? spot.getStatus().toLowerCase() : "libre";
                String spotId = spot.getSpotId() != null ? spot.getSpotId() : 
                               (spot.getSpotNumber() != null ? String.valueOf(spot.getSpotNumber()) : "unknown");
                js.append(String.format("{id:'%s',status:'%s'}", spotId, status));
                if (i < parkingSpots.size() - 1) js.append(",");
            }
            js.append("]);" );
            try {
                webEngine.executeScript(js.toString());
            } catch (Exception e) {
                logger.debug("Could not update 3D view");
            }
        }
    }
    
    private void setupControls() {
        viewModeComboBox.getItems().addAll(
            "Mode Normal", "Mode Prédiction IA", "Mode Planification", "Mode Replay"
        );
        viewModeComboBox.setValue("Mode Normal");
        viewModeComboBox.setOnAction(event -> changeViewMode());
        
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> updateZoom(newVal.doubleValue()));
        
        weatherCheckBox.setSelected(true);
        weatherCheckBox.setOnAction(event -> toggleWeather(weatherCheckBox.isSelected()));
        
        labelsCheckBox.setSelected(true);
        labelsCheckBox.setOnAction(event -> toggleLabels(labelsCheckBox.isSelected()));
    }
    
    private void setup3DView() {
        if (webView3d == null) return;
        webEngine = webView3d.getEngine();
        
        // Load 3D visualization from external HTML file
        try {
            java.net.URL htmlUrl = getClass().getResource("/html/visualization3d.html");
            if (htmlUrl != null) {
                webEngine.load(htmlUrl.toExternalForm());
                logger.info("Loaded 3D visualization from HTML resource");
            } else {
                logger.warn("3D visualization HTML not found, using fallback");
                loadFallback3D();
            }
        } catch (Exception e) {
            logger.error("Error loading 3D visualization", e);
            loadFallback3D();
        }
        
        // Load initial parking data after page loads
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                loadParkingData();
            }
        });
    }
    
    private void loadFallback3D() {
        String fallback = "<!DOCTYPE html><html><head><style>" +
            "body{margin:0;background:#111921;display:flex;align-items:center;justify-content:center;height:100vh;color:white;font-family:sans-serif;}" +
            "</style></head><body><div><h2>Visualisation 3D</h2><p>Chargement des données...</p></div></body></html>";
        webEngine.loadContent(fallback);
    }
    
    private void changeViewMode() {
        String mode = viewModeComboBox.getValue();
        logger.info("Changing view mode to: {}", mode);
        // TODO: Update 3D view based on mode
    }
    
    private void updateZoom(double value) {
        try {
            webEngine.executeScript("setZoom(" + value + ");");
        } catch (Exception e) {
            logger.debug("Could not update zoom");
        }
    }
    
    @FXML
    private void handleResetView() {
        try {
            webEngine.executeScript("resetView();");
            zoomSlider.setValue(50);
        } catch (Exception e) {
            logger.debug("Could not reset view");
        }
    }
    
    private void toggleWeather(boolean enabled) {
        logger.info("Weather overlay: {}", enabled);
        try {
            webEngine.executeScript("toggleWeather(" + enabled + ");");
        } catch (Exception e) {
            logger.debug("Could not toggle weather");
        }
    }
    
    private void toggleLabels(boolean enabled) {
        logger.info("Labels display: {}", enabled);
        try {
            webEngine.executeScript("toggleLabels(" + enabled + ");");
        } catch (Exception e) {
            logger.debug("Could not toggle labels");
        }
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
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
}
