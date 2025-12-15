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
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Flights module - UbuntuAirLab
 * Displays real-time flight information from the API
 */
public class FlightsController {
    
    private static final Logger logger = LoggerFactory.getLogger(FlightsController.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @FXML private ImageView backgroundImageView;
    @FXML private VBox flightsContainer;
    @FXML private Label totalFlightsLabel;
    @FXML private Label arrivalsLabel;
    @FXML private Label departuresLabel;
    @FXML private Label activeLabel;
    @FXML private Button allFilterButton;
    @FXML private Button arrivalFilterButton;
    @FXML private Button departureFilterButton;
    @FXML private Button activeFilterButton;
    
    private final FlightService flightService = FlightService.getInstance();
    private List<Flight> allFlights = List.of();
    private String currentFilter = "all";
    
    @FXML
    public void initialize() {
        // Bind background image to fill the entire screen
        if (backgroundImageView != null && backgroundImageView.getParent() instanceof StackPane) {
            StackPane parent = (StackPane) backgroundImageView.getParent();
            backgroundImageView.fitWidthProperty().bind(parent.widthProperty());
            backgroundImageView.fitHeightProperty().bind(parent.heightProperty());
            backgroundImageView.setPreserveRatio(false);
        }
        
        loadFlights();
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleRefresh() {
        loadFlights();
    }
    
    @FXML
    private void handleFilterAll() {
        currentFilter = "all";
        updateFilterButtons();
        displayFlights(allFlights);
    }
    
    @FXML
    private void handleFilterArrival() {
        currentFilter = "arrival";
        updateFilterButtons();
        List<Flight> filtered = allFlights.stream()
            .filter(f -> f.getEta() != null) // Arrivals have ETA
            .collect(Collectors.toList());
        displayFlights(filtered);
    }
    
    @FXML
    private void handleFilterDeparture() {
        currentFilter = "departure";
        updateFilterButtons();
        List<Flight> filtered = allFlights.stream()
            .filter(f -> f.getEtd() != null) // Departures have ETD
            .collect(Collectors.toList());
        displayFlights(filtered);
    }
    
    @FXML
    private void handleFilterActive() {
        currentFilter = "active";
        updateFilterButtons();
        List<Flight> filtered = allFlights.stream()
            .filter(f -> f.getStatus() == Flight.FlightStatus.ACTIVE)
            .collect(Collectors.toList());
        displayFlights(filtered);
    }
    
    private void updateFilterButtons() {
        allFilterButton.getStyleClass().remove("filter-button-active");
        arrivalFilterButton.getStyleClass().remove("filter-button-active");
        departureFilterButton.getStyleClass().remove("filter-button-active");
        activeFilterButton.getStyleClass().remove("filter-button-active");
        
        switch (currentFilter) {
            case "all":
                allFilterButton.getStyleClass().add("filter-button-active");
                break;
            case "arrival":
                arrivalFilterButton.getStyleClass().add("filter-button-active");
                break;
            case "departure":
                departureFilterButton.getStyleClass().add("filter-button-active");
                break;
            case "active":
                activeFilterButton.getStyleClass().add("filter-button-active");
                break;
        }
    }
    
    private void loadFlights() {
        new Thread(() -> {
            try {
                logger.info("Loading flights from API...");
                com.aige.apronsmart.models.FlightsResponse response = flightService.getAllFlights();
                List<Flight> flights = response != null && response.getFlights() != null ? 
                    response.getFlights() : List.of();
                
                Platform.runLater(() -> {
                    allFlights = flights;
                    updateStats(flights);
                    
                    // Apply current filter
                    switch (currentFilter) {
                        case "arrival":
                            handleFilterArrival();
                            break;
                        case "departure":
                            handleFilterDeparture();
                            break;
                        case "active":
                            handleFilterActive();
                            break;
                        default:
                            displayFlights(flights);
                    }
                });
                
            } catch (Exception e) {
                logger.error("Failed to load flights", e);
                Platform.runLater(() -> showError("Erreur lors du chargement des vols"));
            }
        }).start();
    }
    
    private void updateStats(List<Flight> flights) {
        long arrivals = flights.stream()
            .filter(f -> f.getEta() != null)
            .count();
        
        long departures = flights.stream()
            .filter(f -> f.getEtd() != null)
            .count();
        
        long active = flights.stream()
            .filter(f -> f.getStatus() == Flight.FlightStatus.ACTIVE)
            .count();
        
        totalFlightsLabel.setText(String.valueOf(flights.size()));
        arrivalsLabel.setText(String.valueOf(arrivals));
        departuresLabel.setText(String.valueOf(departures));
        activeLabel.setText(String.valueOf(active));
    }
    
    private void displayFlights(List<Flight> flights) {
        flightsContainer.getChildren().clear();
        
        if (flights.isEmpty()) {
            VBox emptyState = createEmptyState();
            flightsContainer.getChildren().add(emptyState);
            return;
        }
        
        for (Flight flight : flights) {
            VBox flightCard = createFlightCard(flight);
            flightsContainer.getChildren().add(flightCard);
        }
    }
    
    private VBox createEmptyState() {
        VBox emptyState = new VBox(12);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(60, 20, 60, 20));
        
        Label icon = new Label("✈️");
        icon.setStyle("-fx-font-size: 48px;");
        
        Label title = new Label("Aucun vol");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label message = new Label("Aucune donnée de vol disponible pour le moment");
        message.setStyle("-fx-text-fill: #93adc8; -fx-font-size: 14px;");
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);
        
        emptyState.getChildren().addAll(icon, title, message);
        return emptyState;
    }
    
    private VBox createFlightCard(Flight flight) {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(12));
        
        // Header with callsign and status
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label callsignLabel = new Label(flight.getCallsign() != null ? flight.getCallsign() : "N/A");
        callsignLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusBadge = new Label(getStatusText(flight.getStatus()));
        statusBadge.setStyle(getStatusStyle(flight.getStatus()));
        statusBadge.setPadding(new Insets(4, 12, 4, 12));
        
        header.getChildren().addAll(callsignLabel, spacer, statusBadge);
        
        // Type and route info
        HBox typeRow = new HBox(12);
        typeRow.setAlignment(Pos.CENTER_LEFT);
        
        Label typeIcon = new Label(getFlightTypeIcon(flight));
        typeIcon.setStyle("-fx-font-size: 16px;");
        
        Label typeLabel = new Label(getFlightTypeText(flight));
        typeLabel.setStyle("-fx-text-fill: #93adc8; -fx-font-size: 14px;");
        
        if (flight.getOrigin() != null || flight.getDestination() != null) {
            Label separator = new Label("•");
            separator.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 14px;");
            
            String route = "";
            if (flight.getEta() != null && flight.getOrigin() != null) {
                route = "De " + flight.getOrigin();
            } else if (flight.getEtd() != null && flight.getDestination() != null) {
                route = "Vers " + flight.getDestination();
            }
            
            Label routeLabel = new Label(route);
            routeLabel.setStyle("-fx-text-fill: #93adc8; -fx-font-size: 14px;");
            
            typeRow.getChildren().addAll(typeIcon, typeLabel, separator, routeLabel);
        } else {
            typeRow.getChildren().addAll(typeIcon, typeLabel);
        }
        
        // Times and parking info
        VBox detailsBox = new VBox(6);
        
        // Show ETA for arrivals, ETD for departures
        LocalDateTime timeToShow = flight.getEta() != null ? flight.getEta() : flight.getEtd();
        if (timeToShow != null) {
            HBox timeRow = new HBox(8);
            timeRow.setAlignment(Pos.CENTER_LEFT);
            
            Label timeIcon = new Label("🕐");
            String timeLabel = flight.getEta() != null ? "Arrivée prévue: " : "Départ prévu: ";
            Label timeLabelNode = new Label(timeLabel + timeToShow.format(TIME_FORMATTER));
            timeLabelNode.setStyle("-fx-text-fill: #d1d5db; -fx-font-size: 13px;");
            
            timeRow.getChildren().addAll(timeIcon, timeLabelNode);
            detailsBox.getChildren().add(timeRow);
        }
        
        if (flight.getAssignedPosteCode() != null && !flight.getAssignedPosteCode().isEmpty()) {
            HBox parkingRow = new HBox(8);
            parkingRow.setAlignment(Pos.CENTER_LEFT);
            
            Label parkingIcon = new Label("🅿️");
            Label parkingLabel = new Label("Poste: " + flight.getAssignedPosteCode());
            parkingLabel.setStyle("-fx-text-fill: #d1d5db; -fx-font-size: 13px;");
            
            parkingRow.getChildren().addAll(parkingIcon, parkingLabel);
            detailsBox.getChildren().add(parkingRow);
        }
        
        // ICAO24 identifier
        if (flight.getIcao24() != null) {
            Label icaoLabel = new Label("ICAO24: " + flight.getIcao24());
            icaoLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 11px; -fx-font-family: monospace;");
            detailsBox.getChildren().add(icaoLabel);
        }
        
        // Real-time tracking information (if available)
        if (flight.getLatitude() != null && flight.getLongitude() != null) {
            VBox trackingBox = new VBox(4);
            trackingBox.setStyle("-fx-background-color: #1a2632; -fx-background-radius: 4; -fx-padding: 8;");
            
            Label trackingHeader = new Label("🛰️ Suivi en temps réel");
            trackingHeader.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px; -fx-font-weight: bold;");
            trackingBox.getChildren().add(trackingHeader);
            
            // Position
            Label posLabel = new Label(String.format("Position: %.4f°N, %.4f°E", flight.getLatitude(), flight.getLongitude()));
            posLabel.setStyle("-fx-text-fill: #d1d5db; -fx-font-size: 11px;");
            trackingBox.getChildren().add(posLabel);
            
            // On Ground / In Flight
            if (flight.getOnGround() != null) {
                String groundStatus = flight.getOnGround() ? "Au sol" : "En vol";
                String groundIcon = flight.getOnGround() ? "🛬" : "🛫";
                Label groundLabel = new Label(groundIcon + " " + groundStatus);
                groundLabel.setStyle("-fx-text-fill: " + (flight.getOnGround() ? "#FF9800" : "#4CAF50") + "; -fx-font-size: 11px;");
                trackingBox.getChildren().add(groundLabel);
            }
            
            // Altitude, Speed, Heading in one line
            StringBuilder trackingInfo = new StringBuilder();
            if (flight.getAltitude() != null && flight.getAltitude() > 0) {
                int altFt = (int)(flight.getAltitude() * 3.28084);
                trackingInfo.append(String.format("Alt: %,d ft", altFt));
            }
            if (flight.getVelocity() != null && flight.getVelocity() > 0) {
                double speedKmh = flight.getVelocity() * 3.6;
                if (trackingInfo.length() > 0) trackingInfo.append(" • ");
                trackingInfo.append(String.format("%.0f km/h", speedKmh));
            }
            if (flight.getHeading() != null && flight.getHeading() >= 0) {
                if (trackingInfo.length() > 0) trackingInfo.append(" • ");
                trackingInfo.append(String.format("Cap: %.0f°", flight.getHeading()));
            }
            
            if (trackingInfo.length() > 0) {
                Label trackingLabel = new Label(trackingInfo.toString());
                trackingLabel.setStyle("-fx-text-fill: #93adc8; -fx-font-size: 11px;");
                trackingBox.getChildren().add(trackingLabel);
            }
            
            // Last update timestamp
            if (flight.getLastPositionUpdate() != null) {
                try {
                    java.time.Instant updateTime = java.time.Instant.parse(flight.getLastPositionUpdate());
                    java.time.Instant now = java.time.Instant.now();
                    long minutesAgo = java.time.Duration.between(updateTime, now).toMinutes();
                    
                    String updateText = minutesAgo < 1 ? "À l'instant" : 
                                       minutesAgo < 5 ? "Il y a " + minutesAgo + " min" : 
                                       "Il y a " + minutesAgo + " min";
                    String updateColor = minutesAgo < 5 ? "#4CAF50" : "#FF9800";
                    
                    Label updateLabel = new Label("Mis à jour: " + updateText);
                    updateLabel.setStyle("-fx-text-fill: " + updateColor + "; -fx-font-size: 10px; -fx-font-style: italic;");
                    trackingBox.getChildren().add(updateLabel);
                } catch (Exception e) {
                    // Ignore parse errors
                }
            }
            
            detailsBox.getChildren().add(trackingBox);
        }
        
        card.getChildren().addAll(header, typeRow);
        if (!detailsBox.getChildren().isEmpty()) {
            card.getChildren().add(detailsBox);
        }
        
        return card;
    }
    
    private String getFlightTypeIcon(Flight flight) {
        if (flight.getEta() != null && flight.getEtd() == null) {
            return "🛬";
        } else if (flight.getEtd() != null && flight.getEta() == null) {
            return "🛫";
        }
        return "✈️";
    }
    
    private String getFlightTypeText(Flight flight) {
        if (flight.getEta() != null && flight.getEtd() == null) {
            return "Arrivée";
        } else if (flight.getEtd() != null && flight.getEta() == null) {
            return "Départ";
        }
        return "Vol";
    }
    
    private String getStatusText(Flight.FlightStatus status) {
        if (status == null) return "Inconnu";
        return status.getDisplayName();
    }
    
    private String getStatusStyle(Flight.FlightStatus status) {
        if (status == null) {
            return "-fx-background-color: #374151; -fx-text-fill: #9ca3af; -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
        }
        
        String color;
        switch (status) {
            case ACTIVE:
            case APPROACHING:
            case EN_ROUTE:
                color = "-fx-background-color: #065f46; -fx-text-fill: #10b981;";
                break;
            case SCHEDULED:
                color = "-fx-background-color: #1e3a8a; -fx-text-fill: #3b82f6;";
                break;
            case DELAYED:
                color = "-fx-background-color: #92400e; -fx-text-fill: #f59e0b;";
                break;
            case CANCELLED:
                color = "-fx-background-color: #7f1d1d; -fx-text-fill: #ef4444;";
                break;
            case LANDED:
            case PARKED:
            case DEPARTED:
                color = "-fx-background-color: #374151; -fx-text-fill: #9ca3af;";
                break;
            default:
                color = "-fx-background-color: #374151; -fx-text-fill: #9ca3af;";
        }
        
        return color + " -fx-background-radius: 4; -fx-font-size: 11px; -fx-font-weight: bold;";
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(AigApronSmartApplication.class.getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = flightsContainer.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            logger.error("Failed to navigate to dashboard", e);
        }
    }
}
