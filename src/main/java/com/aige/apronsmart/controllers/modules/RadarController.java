package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.Flight;
import com.aige.apronsmart.services.FlightService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Radar Live module - UbuntuAirLab
 */
public class RadarController {
    
    private static final Logger logger = LoggerFactory.getLogger(RadarController.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @FXML private StackPane mapContainer;
    @FXML private WebView mapWebView;
    @FXML private VBox flightsListContainer;
    @FXML private TextField searchField;
    @FXML private Label flightCountLabel;
    @FXML private Slider zoomSlider;
    
    // Bottom sheet for flight details
    @FXML private VBox bottomSheet;
    @FXML private Label bottomSheetFlightNumber;
    @FXML private Label bottomSheetAircraftType;
    @FXML private Label bottomSheetStatus;
    @FXML private Label bottomSheetOrigin;
    @FXML private Label bottomSheetDestination;
    @FXML private Label bottomSheetEta;
    @FXML private Label bottomSheetParking;
    @FXML private Label bottomSheetAltitude;
    
    private final FlightService flightService = FlightService.getInstance();
    private final com.aige.apronsmart.services.OpenSkyService openSkyService = com.aige.apronsmart.services.OpenSkyService.getInstance();
    private final ObservableList<Flight> flightsList = FXCollections.observableArrayList();
    private Timeline autoRefreshTimeline;
    private WebEngine webEngine;
    
    // Cache for simulated flight positions to create smooth movement
    private final java.util.Map<String, SimulatedPosition> flightPositionCache = new java.util.concurrent.ConcurrentHashMap<>();
    
    private static class SimulatedPosition {
        double lat, lon, altitude, heading, angle, distance;
        long lastUpdate;
        
        SimulatedPosition(double lat, double lon, double altitude, double heading, double angle, double distance) {
            this.lat = lat;
            this.lon = lon;
            this.altitude = altitude;
            this.heading = heading;
            this.angle = angle;
            this.distance = distance;
            this.lastUpdate = System.currentTimeMillis();
        }
    }
    
    @FXML
    public void initialize() {
        setupMap();
        setupSearch();
        loadFlights();
        
        // Auto-refresh every 10 seconds for real-time tracking
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            logger.info("⟳ Auto-refreshing flights for real-time tracking...");
            loadFlights();
        }));
        autoRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        autoRefreshTimeline.play();
        
        logger.info("✓ Auto-refresh enabled: flights will update every 10 seconds");
    }
    
    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterFlights(newValue);
            });
        }
    }
    
    private void setupMap() {
        if (mapWebView == null) return;
        
        webEngine = mapWebView.getEngine();
        
        // Enable JavaScript to Java callback
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
                window.setMember("javaApp", this);
            }
        });
        
        // Load OpenStreetMap with Leaflet
        String mapHtml = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" integrity=\"sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=\" crossorigin=\"\"/>\n" +
            "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\" integrity=\"sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=\" crossorigin=\"\"></script>\n" +
            "    <style>\n" +
            "        html, body { height: 100%; margin: 0; padding: 0; overflow: hidden; background: #111921; }\n" +
            "        #map { position: absolute; top: 0; bottom: 0; left: 0; right: 0; width: 100%; height: 100%; }\n" +
            "        .flight-marker { cursor: pointer; }\n" +
            "        .flight-icon { font-size: 24px; transform-origin: center; transition: transform 0.3s; }\n" +
            "        .flight-icon:hover { transform: scale(1.3); }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script>\n" +
            "        // Initialize map with proper options\n" +
            "        var map = L.map('map', {\n" +
            "            zoomControl: false,\n" +
            "            preferCanvas: true,\n" +
            "            fadeAnimation: false,\n" +
            "            zoomAnimation: false\n" +
            "        }).setView([6.1656, 1.2544], 11);\n" +
            "        \n" +
        // Add tile layer with stable configuration\n" +
            "        var tileLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
            "            attribution: '© OpenStreetMap',\n" +
            "            maxZoom: 19,\n" +
            "            minZoom: 3,\n" +
            "            tileSize: 256,\n" +
            "            keepBuffer: 6,\n" +
            "            maxNativeZoom: 19,\n" +
            "            updateWhenIdle: true,\n" +
            "            updateWhenZooming: false,\n" +
            "            updateInterval: 200,\n" +
            "            bounds: [[-90, -180], [90, 180]],\n" +
            "            noWrap: false\n" +
            "        }).addTo(map);\n" +
            "        \n" +
            "        // Tile error handling with backoff\n" +
            "        var tileErrorCount = 0;\n" +
            "        tileLayer.on('tileerror', function(error, tile) {\n" +
            "            tileErrorCount++;\n" +
            "            if (tileErrorCount < 10) {\n" +
            "                // Retry loading the tile after a delay\n" +
            "                setTimeout(function() {\n" +
            "                    tile.src = tile.src;\n" +
            "                }, 1000);\n" +
            "            }\n" +
            "        });\n" +
            "        \n" +
            "        tileLayer.on('tileload', function() {\n" +
            "            tileErrorCount = Math.max(0, tileErrorCount - 1);\n" +
            "        });\n" +
            "        \n" +
            "        // Single initial map size fix\n" +
            "        setTimeout(function() {\n" +
            "            map.invalidateSize();\n" +
            "        }, 100);\n" +
            "        var airportIcon = L.icon({\n" +
            "            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',\n" +
            "            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',\n" +
            "            iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]\n" +
            "        });\n" +
            "        L.marker([6.1656, 1.2544], {icon: airportIcon}).addTo(map)\n" +
            "            .bindPopup('<b>Aéroport International Gnassingbé Eyadéma</b><br>Lomé, Togo');\n" +
            "        var flightMarkers = {};\n" +
            "        var flightsData = {};\n" +
            "        function addFlight(flight) {\n" +
            "            if (!flight.latitude || !flight.longitude) return;\n" +
            "            flightsData[flight.id] = flight;\n" +
            "            var marker = L.marker([flight.latitude, flight.longitude], {\n" +
            "                icon: L.divIcon({ className: 'flight-marker', html: '<div class=\"flight-icon\">✈️</div>', iconSize: [30, 30], iconAnchor: [15, 15] }),\n" +
            "                rotationAngle: flight.heading || 0\n" +
            "            }).addTo(map);\n" +
            "            marker.on('click', function() {\n" +
            "                if (window.javaApp) {\n" +
            "                    window.javaApp.onFlightClicked(flight.id, flight.callsign, flight.aircraftType || 'Unknown', \n" +
            "                        flight.status || 'Unknown', flight.origin || 'N/A', flight.destination || 'LFW',\n" +
            "                        flight.eta || '--:--', flight.parking || 'Non assigné', flight.altitude || 0);\n" +
            "                }\n" +
            "                map.setView([flight.latitude, flight.longitude], 13);\n" +
            "            });\n" +
            "            flightMarkers[flight.id] = marker;\n" +
            "        }\n" +
            "        function updateFlights(flights) {\n" +
            "            Object.values(flightMarkers).forEach(m => map.removeLayer(m));\n" +
            "            flightMarkers = {};\n" +
            "            flightsData = {};\n" +
            "            flights.forEach(addFlight);\n" +
            "        }\n" +
            "        function focusFlight(lat, lon) { map.setView([lat, lon], 13); }\n" +
            "        function zoomIn() { map.zoomIn(); }\n" +
            "        function zoomOut() { map.zoomOut(); }\n" +
            "        function centerMap() { map.setView([6.1656, 1.2544], 11); }\n" +
            "        console.log('Leaflet map initialized - waiting for flight data from API');\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";
        
        webEngine.loadContent(mapHtml);
    }
    
    /**
     * Called from JavaScript when a flight marker is clicked on the map
     */
    public void onFlightClicked(String id, String callsign, String aircraftType, String status,
                                 String origin, String destination, String eta, String parking, int altitude) {
        Platform.runLater(() -> {
            logger.info("Flight clicked: {}", callsign);
            showBottomSheet(callsign, aircraftType, status, origin, destination, eta, parking, altitude);
        });
    }
    
    private void showBottomSheet(String callsign, String aircraftType, String status,
                                  String origin, String destination, String eta, String parking, int altitude) {
        if (bottomSheet == null) return;
        
        // Update bottom sheet content
        if (bottomSheetFlightNumber != null) bottomSheetFlightNumber.setText(callsign);
        if (bottomSheetAircraftType != null) bottomSheetAircraftType.setText(aircraftType);
        if (bottomSheetStatus != null) {
            bottomSheetStatus.setText(status);
            // Update status badge style based on status
            bottomSheetStatus.getStyleClass().removeAll("status-badge-success", "status-badge-warning", "status-badge-error", "status-badge-info");
            switch (status.toLowerCase()) {
                case "atterri": bottomSheetStatus.getStyleClass().add("status-badge-success"); break;
                case "en approche": bottomSheetStatus.getStyleClass().add("status-badge-warning"); break;
                case "retardé": bottomSheetStatus.getStyleClass().add("status-badge-error"); break;
                default: bottomSheetStatus.getStyleClass().add("status-badge-info"); break;
            }
        }
        if (bottomSheetOrigin != null) bottomSheetOrigin.setText(origin);
        if (bottomSheetDestination != null) bottomSheetDestination.setText(destination);
        if (bottomSheetEta != null) bottomSheetEta.setText(eta);
        if (bottomSheetParking != null) bottomSheetParking.setText(parking);
        if (bottomSheetAltitude != null) {
            bottomSheetAltitude.setText(altitude > 0 ? String.format("Alt: %,d ft", altitude) : "Au sol");
        }
        
        // Show bottom sheet with animation
        bottomSheet.setVisible(true);
        bottomSheet.setManaged(true);
    }
    
    @FXML
    private void handleCloseBottomSheet() {
        if (bottomSheet != null) {
            bottomSheet.setVisible(false);
            bottomSheet.setManaged(false);
        }
    }
    
    @FXML
    private void handleBack() {
        logger.info("Back button clicked - navigating to dashboard");
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
        navigateToDashboard();
    }
    
    @FXML
    private void handleZoomIn() {
        if (webEngine != null) {
            webEngine.executeScript("zoomIn();");
        }
    }
    
    @FXML
    private void handleZoomOut() {
        if (webEngine != null) {
            webEngine.executeScript("zoomOut();");
        }
    }
    
    @FXML
    private void handleCenterMap() {
        if (webEngine != null) {
            webEngine.executeScript("centerMap();");
        }
    }
    
    @FXML
    private void handleSync() {
        logger.info("Manual sync requested");
        Button syncButton = (Button) bottomSheet.getScene().lookup("#syncButton");
        if (syncButton != null) {
            syncButton.setDisable(true);
        }
        
        new Thread(() -> {
            try {
                java.util.Map<String, Object> response = flightService.triggerSync();
                logger.info("Sync triggered: {}", response);
                
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Synchronisation");
                    alert.setHeaderText(null);
                    alert.setContentText("Synchronisation lancée avec succès. Les données seront mises à jour dans quelques instants.");
                    alert.showAndWait();
                    
                    // Reload flights after 2 seconds
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(this::loadFlights);
                        } catch (InterruptedException e) {
                            logger.error("Sleep interrupted", e);
                        }
                    }).start();
                    
                    if (syncButton != null) {
                        syncButton.setDisable(false);
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Impossible de lancer la synchronisation: " + e.getMessage());
                    alert.showAndWait();
                    
                    if (syncButton != null) {
                        syncButton.setDisable(false);
                    }
                });
            }
        }).start();
    }
    
    @FXML
    private void handleMenu() {
        // TODO: Show menu options
        logger.info("Menu requested");
    }
    
    @FXML
    private void handleRefresh() {
        loadFlights();
    }
    
    @FXML
    private void handlePlayPause() {
        if (autoRefreshTimeline.getStatus() == Animation.Status.RUNNING) {
            autoRefreshTimeline.pause();
        } else {
            autoRefreshTimeline.play();
        }
    }
    
    private void loadFlights() {
        new Thread(() -> {
            try {
                logger.info("Loading flights from local API and OpenSky Network...");
                
                // 1. Load flights from local API
                List<Flight> localFlights = new ArrayList<>();
                try {
                    localFlights = flightService.getActiveFlights();
                    logger.info("Loaded {} flights from local API", localFlights.size());
                } catch (IOException e) {
                    logger.warn("Could not load from local API: {}", e.getMessage());
                }
                
                // 2. Load real-time data from OpenSky Network
                List<Map<String, Object>> openSkyFlights = new ArrayList<>();
                try {
                    openSkyFlights = openSkyService.getFlightsNearAirport();
                    logger.info("Loaded {} flights from OpenSky Network", openSkyFlights.size());
                } catch (IOException e) {
                    logger.warn("Could not load from OpenSky: {}", e.getMessage());
                }
                
                // 3. Merge data: Update local flights with OpenSky GPS data
                for (Flight localFlight : localFlights) {
                    for (Map<String, Object> openSkyFlight : openSkyFlights) {
                        String openSkyIcao = (String) openSkyFlight.get("icao24");
                        if (openSkyIcao != null && openSkyIcao.equalsIgnoreCase(localFlight.getIcao24())) {
                            // Update with real GPS data from OpenSky
                            localFlight.setLatitude((Double) openSkyFlight.get("latitude"));
                            localFlight.setLongitude((Double) openSkyFlight.get("longitude"));
                            localFlight.setAltitude((Double) openSkyFlight.get("altitude"));
                            localFlight.setHeading((Double) openSkyFlight.get("heading"));
                            localFlight.setSpeed((Double) openSkyFlight.get("speed"));
                            logger.info("✓ Updated {} with OpenSky GPS data", localFlight.getCallsign());
                            break;
                        }
                    }
                }
                
                // 4. Add OpenSky-only flights (not in local DB)
                for (Map<String, Object> openSkyFlight : openSkyFlights) {
                    String openSkyIcao = (String) openSkyFlight.get("icao24");
                    boolean found = localFlights.stream()
                            .anyMatch(f -> f.getIcao24() != null && f.getIcao24().equalsIgnoreCase(openSkyIcao));
                    
                    if (!found) {
                        // Create Flight object from OpenSky data
                        Flight newFlight = new Flight();
                        newFlight.setIcao24(openSkyIcao);
                        newFlight.setCallsign((String) openSkyFlight.get("callsign"));
                        newFlight.setLatitude((Double) openSkyFlight.get("latitude"));
                        newFlight.setLongitude((Double) openSkyFlight.get("longitude"));
                        newFlight.setAltitude((Double) openSkyFlight.get("altitude"));
                        newFlight.setHeading((Double) openSkyFlight.get("heading"));
                        newFlight.setSpeed((Double) openSkyFlight.get("speed"));
                        
                        Boolean onGround = (Boolean) openSkyFlight.get("on_ground");
                        if (onGround != null && onGround) {
                            newFlight.setStatus(Flight.FlightStatus.LANDED);
                        } else {
                            newFlight.setStatus(Flight.FlightStatus.ACTIVE);
                        }
                        
                        localFlights.add(newFlight);
                        logger.info("+ Added OpenSky-only flight: {}", newFlight.getCallsign());
                    }
                }
                
                final List<Flight> mergedFlights = localFlights;
                logger.info("Total flights after merge: {}", mergedFlights.size());
                
                Platform.runLater(() -> {
                    flightsList.clear();
                    flightsList.addAll(mergedFlights);
                    if (flightCountLabel != null) {
                        String timestamp = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                        flightCountLabel.setText(mergedFlights.size() + " vol(s) • " + timestamp);
                    }
                    displayFlightsList(mergedFlights);
                    updateMapFlights(mergedFlights);
                    logger.info("✓ Map updated with {} flights (real GPS data)", mergedFlights.size());
                });
            } catch (Exception e) {
                logger.error("Error loading flights: {}", e.getMessage(), e);
                Platform.runLater(() -> {
                    if (flightCountLabel != null) {
                        flightCountLabel.setText("Erreur");
                    }
                    showErrorState("Erreur de chargement des vols");
                });
            }
        }).start();
    }
    
    private void showErrorState(String message) {
        if (flightsListContainer == null) return;
        flightsListContainer.getChildren().clear();
        
        VBox errorBox = new VBox(10);
        errorBox.setAlignment(Pos.CENTER);
        errorBox.setPadding(new Insets(20));
        errorBox.setStyle("-fx-background-color: #1a2632; -fx-background-radius: 8;");
        
        Label errorLabel = new Label("⚠️");
        errorLabel.setStyle("-fx-font-size: 32px;");
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8892a0; -fx-wrap-text: true; -fx-text-alignment: center;");
        
        Button retryButton = new Button("Réessayer");
        retryButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-padding: 8 16;");
        retryButton.setOnAction(e -> loadFlights());
        
        errorBox.getChildren().addAll(errorLabel, messageLabel, retryButton);
        flightsListContainer.getChildren().add(errorBox);
    }
    
    private void displayFlightsList(List<Flight> flights) {
        if (flightsListContainer == null) return;
        flightsListContainer.getChildren().clear();
        
        for (Flight flight : flights) {
            String statusColor = getStatusColor(flight.getStatus());
            String eta = flight.getEta() != null ? flight.getEta().format(TIME_FORMATTER) : "--:--";
            addFlightItem(flight.getCallsign(), flight.getStatus().getDisplayName(), 
                         flight.getAircraftType(), eta, statusColor);
        }
    }
    
    private void addFlightItem(String callsign, String status, String type, String eta, String color) {
        HBox item = new HBox();
        item.setAlignment(Pos.CENTER_LEFT);
        item.setSpacing(12);
        item.setPadding(new Insets(12, 16, 12, 16));
        item.getStyleClass().add("flight-item");
        item.setStyle("-fx-background-color: #1a2632; -fx-background-radius: 8;");
        
        // Status indicator
        Region indicator = new Region();
        indicator.setMinSize(8, 8);
        indicator.setMaxSize(8, 8);
        indicator.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        
        // Flight info
        VBox info = new VBox(2);
        Label callsignLabel = new Label(callsign);
        callsignLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label typeLabel = new Label(type != null ? type : "---");
        typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8892a0;");
        info.getChildren().addAll(callsignLabel, typeLabel);
        HBox.setHgrow(info, Priority.ALWAYS);
        
        // Status and ETA
        VBox rightInfo = new VBox(2);
        rightInfo.setAlignment(Pos.CENTER_RIGHT);
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + color + ";");
        Label etaLabel = new Label("ETA: " + eta);
        etaLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #8892a0;");
        rightInfo.getChildren().addAll(statusLabel, etaLabel);
        
        item.getChildren().addAll(indicator, info, rightInfo);
        flightsListContainer.getChildren().add(item);
    }
    
    private String getStatusColor(Flight.FlightStatus status) {
        if (status == null) return "#8892a0";
        switch (status) {
            case EN_ROUTE: return "#2196F3";
            case APPROACHING: return "#FF9800";
            case LANDED:
            case PARKED: return "#4CAF50";
            case DELAYED: return "#F44336";
            default: return "#8892a0";
        }
    }
    
    private void updateMapFlights(List<Flight> flights) {
        if (webEngine == null || flights == null) return;
        
        // Airport coordinates (Lomé, Togo)
        final double AIRPORT_LAT = 6.1656;
        final double AIRPORT_LON = 1.2544;
        
        StringBuilder jsCode = new StringBuilder("updateFlights([");
        boolean first = true;
        int flightIndex = 0;
        
        for (Flight f : flights) {
            flightIndex++;
            double lat = f.getLatitude() != null ? f.getLatitude() : 0;
            double lon = f.getLongitude() != null ? f.getLongitude() : 0;
            
            // If flight has no GPS coordinates, generate simulated position based on status
            if (lat == 0 && lon == 0) {
                String flightId = f.getIcao24() != null ? f.getIcao24() : String.valueOf(f.getId());
                SimulatedPosition cached = flightPositionCache.get(flightId);
                
                double angle, distance;
                int altitude;
                
                if (cached != null) {
                    // Use cached position and simulate progressive movement
                    angle = cached.angle;
                    distance = cached.distance;
                    altitude = (int) cached.altitude;
                    
                    // Simulate movement based on status
                    if (f.getStatus() != null) {
                        switch (f.getStatus()) {
                            case APPROACHING:
                            case ACTIVE:
                                distance = Math.max(0.01, distance - 0.015);
                                if (distance < 0.05) {
                                    altitude = Math.max(0, altitude - 500);
                                }
                                break;
                            case SCHEDULED:
                            case EN_ROUTE:
                            case DELAYED:
                                distance = Math.max(0.20, distance - 0.005);
                                break;
                            case LANDED:
                            case PARKED:
                            case DEPARTED:
                                distance = 0.01;
                                altitude = 0;
                                break;
                            case CANCELLED:
                                // Keep last known position
                                break;
                        }
                    }
                } else {
                    // First time seeing this flight
                    angle = (flightIndex * 45.0) % 360.0;
                    
                    if (f.getStatus() != null) {
                        switch (f.getStatus()) {
                            case APPROACHING:
                                distance = 0.15;
                                altitude = 3000 + (flightIndex * 500);
                                break;
                            case EN_ROUTE:
                                distance = 0.25;
                                altitude = 8000 + (flightIndex * 1000);
                                break;
                            case ACTIVE:
                                distance = 0.18;
                                altitude = 5000 + (flightIndex * 800);
                                break;
                            case LANDED:
                            case PARKED:
                                distance = 0.01;
                                altitude = 0;
                                break;
                            default:
                                distance = 0.20;
                                altitude = 6000;
                        }
                    } else {
                        distance = 0.20;
                        altitude = 6000;
                    }
                }
                
                // Calculate position using basic trigonometry
                lat = AIRPORT_LAT + (distance * Math.cos(Math.toRadians(angle)));
                lon = AIRPORT_LON + (distance * Math.sin(Math.toRadians(angle)));
                
                // Update cache
                double heading = (angle + 180) % 360;
                flightPositionCache.put(flightId, new SimulatedPosition(lat, lon, altitude, heading, angle, distance));
                
                // Update flight object if altitude is missing
                if (f.getAltitude() == null || f.getAltitude() == 0) {
                    f.setAltitude((double) altitude);
                }
                
                // Set heading towards airport
                if (f.getHeading() == null || f.getHeading() == 0) {
                    f.setHeading((angle + 180) % 360);
                }
                
                logger.debug("Generated position for flight {}: lat={}, lon={}, alt={}", 
                            f.getCallsign(), lat, lon, altitude);
            }
            
            if (!first) {
                jsCode.append(",");
            }
            first = false;
            
            // Build flight object with proper null checking and escaping
            jsCode.append("{")
                .append("id:'").append(f.getIcao24() != null ? f.getIcao24() : f.getId()).append("',")
                .append("callsign:'").append(escapeJs(f.getCallsign())).append("',")
                .append("aircraftType:'").append(escapeJs(f.getAircraftType())).append("',")
                .append("latitude:").append(lat).append(",")
                .append("longitude:").append(lon).append(",")
                .append("altitude:").append(f.getAltitude() != null ? f.getAltitude().intValue() : 0).append(",")
                .append("heading:").append(f.getHeading() != null ? f.getHeading().intValue() : 0).append(",")
                .append("speed:").append(f.getSpeed() != null ? f.getSpeed().intValue() : 0).append(",")
                .append("status:'").append(f.getStatus() != null ? escapeJs(f.getStatus().getDisplayName()) : "Inconnu").append("',")
                .append("origin:'").append(escapeJs(f.getOrigin())).append("',")
                .append("destination:'").append(escapeJs(f.getDestination())).append("',")
                .append("eta:'").append(f.getEta() != null ? f.getEta().format(TIME_FORMATTER) : "--:--").append("',")
                .append("parking:'").append(escapeJs(f.getAssignedPosteCode())).append("'")
                .append("}");
        }
        jsCode.append("]);");
        
        try {
            webEngine.executeScript(jsCode.toString());
            logger.debug("Updated {} flights on map", flights.size());
        } catch (Exception e) {
            logger.debug("Map not ready yet: {}", e.getMessage());
        }
    }
    
    private void filterFlights(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            displayFlightsList(flightsList);
            updateMapFlights(flightsList);
            return;
        }
        
        String filter = searchText.toLowerCase();
        List<Flight> filtered = flightsList.stream()
            .filter(f -> f.getCallsign() != null && f.getCallsign().toLowerCase().contains(filter) ||
                        (f.getAircraftType() != null && f.getAircraftType().toLowerCase().contains(filter)))
            .toList();
        displayFlightsList(filtered);
        updateMapFlights(filtered);
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
    
    /**
     * Escape string for safe use in JavaScript code
     */
    private String escapeJs(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("'", "\\'")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
    
    @FXML
    private void handleSettings() {
        // Create settings dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Paramètres du Radar");
        dialog.setHeaderText("Configuration de la synchronisation automatique");
        
        // Create dialog content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // Current sync status
        Label statusLabel = new Label("Statut actuel de la synchronisation :");
        statusLabel.setStyle("-fx-font-weight: bold;");
        
        Label currentStatusLabel = new Label("Chargement...");
        
        // Sync interval configuration
        Label intervalLabel = new Label("Intervalle de synchronisation (minutes) :");
        intervalLabel.setStyle("-fx-font-weight: bold; -fx-padding: 20 0 0 0;");
        
        HBox intervalBox = new HBox(10);
        intervalBox.setAlignment(Pos.CENTER_LEFT);
        
        Spinner<Integer> intervalSpinner = new Spinner<>(1, 60, 10);
        intervalSpinner.setEditable(true);
        intervalSpinner.setPrefWidth(100);
        
        Label intervalHelp = new Label("(Entre 1 et 60 minutes)");
        intervalHelp.setStyle("-fx-text-fill: #666;");
        
        intervalBox.getChildren().addAll(intervalSpinner, intervalHelp);
        
        // Apply button
        Button applyButton = new Button("Appliquer");
        applyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10 20;");
        
        Label resultLabel = new Label();
        resultLabel.setWrapText(true);
        
        applyButton.setOnAction(e -> {
            int minutes = intervalSpinner.getValue();
            applyButton.setDisable(true);
            applyButton.setText("Application...");
            
            new Thread(() -> {
                try {
                    com.aige.apronsmart.services.SyncService syncService = 
                        com.aige.apronsmart.services.SyncService.getInstance();
                    
                    Map<String, Object> response = syncService.updateSyncInterval(minutes);
                    
                    Platform.runLater(() -> {
                        if (response != null && response.containsKey("interval_minutes")) {
                            resultLabel.setText("✅ Intervalle mis à jour avec succès !");
                            resultLabel.setStyle("-fx-text-fill: #4CAF50;");
                            
                            // Refresh status
                            loadSyncStatus(currentStatusLabel);
                        } else {
                            resultLabel.setText("❌ Erreur lors de la mise à jour");
                            resultLabel.setStyle("-fx-text-fill: #f44336;");
                        }
                        
                        applyButton.setDisable(false);
                        applyButton.setText("Appliquer");
                    });
                    
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        resultLabel.setText("❌ Erreur : " + ex.getMessage());
                        resultLabel.setStyle("-fx-text-fill: #f44336;");
                        applyButton.setDisable(false);
                        applyButton.setText("Appliquer");
                    });
                }
            }).start();
        });
        
        content.getChildren().addAll(
            statusLabel,
            currentStatusLabel,
            intervalLabel,
            intervalBox,
            applyButton,
            resultLabel
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
        // Load current status
        loadSyncStatus(currentStatusLabel);
        
        dialog.showAndWait();
    }
    
    private void loadSyncStatus(Label statusLabel) {
        new Thread(() -> {
            try {
                com.aige.apronsmart.services.SyncService syncService = 
                    com.aige.apronsmart.services.SyncService.getInstance();
                
                Map<String, Object> status = syncService.getSyncStatus();
                
                Platform.runLater(() -> {
                    if (status != null) {
                        String lastSync = (String) status.get("last_sync");
                        Integer interval = (Integer) status.get("interval_minutes");
                        Boolean isRunning = (Boolean) status.get("is_running");
                        
                        String statusText = String.format(
                            "🔄 État : %s\n" +
                            "⏰ Intervalle : %d minutes\n" +
                            "🕐 Dernière sync : %s",
                            isRunning ? "En cours" : "Arrêté",
                            interval != null ? interval : 10,
                            lastSync != null ? lastSync : "Jamais"
                        );
                        
                        statusLabel.setText(statusText);
                        statusLabel.setStyle("-fx-font-family: monospace;");
                    } else {
                        statusLabel.setText("❌ Impossible de récupérer le statut");
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("❌ Erreur : " + e.getMessage());
                });
            }
        }).start();
    }
}
