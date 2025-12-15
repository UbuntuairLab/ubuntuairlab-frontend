package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.Flight;
import com.aige.apronsmart.services.FlightService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Planning module - UbuntuAirLab
 */
public class PlanningController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlanningController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @FXML private ImageView backgroundImageView;
    @FXML private Label dateLabel;
    @FXML private VBox timelineContainer;
    @FXML private ScrollPane timelineScrollPane;
    
    private final FlightService flightService = FlightService.getInstance();
    private LocalDate currentDate = LocalDate.now();
    private String currentView = "timeline"; // timeline, resources, calendar
    
    @FXML
    public void initialize() {
        // Bind background image to fill the entire screen
        if (backgroundImageView != null && backgroundImageView.getParent() instanceof StackPane) {
            StackPane parent = (StackPane) backgroundImageView.getParent();
            backgroundImageView.fitWidthProperty().bind(parent.widthProperty());
            backgroundImageView.fitHeightProperty().bind(parent.heightProperty());
            backgroundImageView.setPreserveRatio(false);
        }
        
        updateDateLabel();
        loadPlanning();
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleViewTimeline() {
        currentView = "timeline";
        loadPlanning();
    }
    
    @FXML
    private void handleViewResources() {
        currentView = "resources";
        loadPlanning();
    }
    
    @FXML
    private void handleViewCalendar() {
        currentView = "calendar";
        loadPlanning();
    }
    
    @FXML
    private void handlePreviousDay() {
        currentDate = currentDate.minusDays(1);
        updateDateLabel();
        loadPlanning();
    }
    
    @FXML
    private void handleNextDay() {
        currentDate = currentDate.plusDays(1);
        updateDateLabel();
        loadPlanning();
    }
    
    @FXML
    private void handleCalendarPick() {
        // TODO: Open calendar picker dialog
        logger.info("Calendar picker requested");
    }
    
    @FXML
    private void handleAddFlight() {
        // TODO: Open add flight dialog
        logger.info("Add flight requested");
    }
    
    @FXML
    private void handleCreateFlight() {
        // TODO: Open create flight dialog
        logger.info("Create flight requested");
    }
    
    private void updateDateLabel() {
        if (dateLabel != null) {
            dateLabel.setText(currentDate.format(DATE_FORMATTER));
        }
    }
    
    private void loadPlanning() {
        new Thread(() -> {
            try {
                // Get all flights and filter by date (new API doesn't have date filter)
                List<Flight> allFlights = flightService.getAllFlights().getFlights();
                List<Flight> flights = allFlights; // TODO: Filter by currentDate if needed
                Platform.runLater(() -> {
                    displayTimeline(flights);
                });
            } catch (IOException e) {
                logger.error("Error loading planning", e);
                Platform.runLater(() -> {
                    displaySampleTimeline();
                });
            }
        }).start();
    }
    
    private void displaySampleTimeline() {
        if (timelineContainer == null) return;
        timelineContainer.getChildren().clear();
        
        // Add resource lanes
        addResourceLane("Poste A1", new String[][]{
            {"ET 920", "06:00", "08:30", "#1773cf"},
            {"TK 632", "10:00", "12:00", "#4CAF50"}
        });
        
        addResourceLane("Poste A2", new String[][]{
            {"AF 740", "07:30", "09:00", "#FF9800"},
            {"RW 205", "11:00", "14:00", "#F44336"}
        });
        
        addResourceLane("Poste B1", new String[][]{
            {"KL 512", "08:00", "10:30", "#1773cf"},
        });
        
        addResourceLane("Poste B2", new String[][]{
            {"BA 098", "09:30", "11:30", "#4CAF50"},
            {"LH 331", "13:00", "15:00", "#1773cf"}
        });
        
        addResourceLane("Poste C1", new String[][]{
            {"DL 145", "05:30", "07:00", "#FF9800"},
            {"UA 873", "12:00", "14:30", "#4CAF50"}
        });
    }
    
    private void addResourceLane(String resourceName, String[][] flights) {
        VBox lane = new VBox(8);
        lane.setPadding(new Insets(12, 0, 12, 0));
        lane.setStyle("-fx-border-color: #2a3847; -fx-border-width: 0 0 1 0;");
        
        // Resource label
        Label resourceLabel = new Label(resourceName);
        resourceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Timeline row
        HBox timelineRow = new HBox(8);
        timelineRow.setAlignment(Pos.CENTER_LEFT);
        timelineRow.setPadding(new Insets(8, 0, 0, 0));
        
        for (String[] flight : flights) {
            HBox flightCard = createFlightCard(flight[0], flight[1], flight[2], flight[3]);
            timelineRow.getChildren().add(flightCard);
        }
        
        lane.getChildren().addAll(resourceLabel, timelineRow);
        timelineContainer.getChildren().add(lane);
    }
    
    private HBox createFlightCard(String callsign, String startTime, String endTime, String color) {
        HBox card = new HBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10, 14, 10, 14));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8;");
        
        // Flight info
        VBox info = new VBox(2);
        Label callsignLabel = new Label(callsign);
        callsignLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label timeLabel = new Label(startTime + " - " + endTime);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.8);");
        info.getChildren().addAll(callsignLabel, timeLabel);
        
        card.getChildren().add(info);
        
        // Click handler
        card.setOnMouseClicked(e -> showFlightDetails(callsign, startTime, endTime));
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8;"));
        
        return card;
    }
    
    private void displayTimeline(List<Flight> flights) {
        if (timelineContainer == null) return;
        
        if (flights.isEmpty()) {
            displaySampleTimeline();
            return;
        }
        
        timelineContainer.getChildren().clear();
        
        // Group flights by assigned poste
        flights.stream()
            .filter(f -> f.getAssignedPosteCode() != null)
            .collect(java.util.stream.Collectors.groupingBy(Flight::getAssignedPosteCode))
            .forEach((posteCode, posteFlights) -> {
                VBox lane = new VBox(8);
                lane.setPadding(new Insets(12, 0, 12, 0));
                lane.setStyle("-fx-border-color: #2a3847; -fx-border-width: 0 0 1 0;");
                
                Label resourceLabel = new Label("Poste " + posteCode);
                resourceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
                
                HBox timelineRow = new HBox(8);
                timelineRow.setAlignment(Pos.CENTER_LEFT);
                timelineRow.setPadding(new Insets(8, 0, 0, 0));
                
                for (Flight flight : posteFlights) {
                    String startTime = flight.getEta() != null ? flight.getEta().format(TIME_FORMATTER) : "--:--";
                    String endTime = flight.getEtd() != null ? flight.getEtd().format(TIME_FORMATTER) : "--:--";
                    String color = getFlightColor(flight.getStatus());
                    
                    HBox flightCard = createFlightCard(flight.getCallsign(), startTime, endTime, color);
                    timelineRow.getChildren().add(flightCard);
                }
                
                lane.getChildren().addAll(resourceLabel, timelineRow);
                timelineContainer.getChildren().add(lane);
            });
    }
    
    private String getFlightColor(Flight.FlightStatus status) {
        if (status == null) return "#1773cf";
        switch (status) {
            case SCHEDULED: return "#1773cf";
            case EN_ROUTE: return "#2196F3";
            case APPROACHING: return "#FF9800";
            case LANDED:
            case PARKED: return "#4CAF50";
            case DELAYED: return "#F44336";
            case DEPARTED: return "#9E9E9E";
            default: return "#1773cf";
        }
    }
    
    private void showFlightDetails(String callsign, String startTime, String endTime) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Vol");
        alert.setHeaderText("Vol " + callsign);
        alert.setContentText("Heure d'arrivée: " + startTime + "\nHeure de départ: " + endTime);
        alert.showAndWait();
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
