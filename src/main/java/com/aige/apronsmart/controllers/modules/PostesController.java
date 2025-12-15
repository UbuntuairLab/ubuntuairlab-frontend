package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.Poste;
import com.aige.apronsmart.services.PosteService;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Controller for Postes Management module - UbuntuAirLab
 */
public class PostesController {
    
    private static final Logger logger = LoggerFactory.getLogger(PostesController.class);
    
    @FXML private ImageView backgroundImageView;
    @FXML private GridPane postesGrid;
    @FXML private Label totalPostesLabel;
    @FXML private Label freePostesLabel;
    @FXML private Label occupiedPostesLabel;
    @FXML private Label reservedPostesLabel;
    @FXML private TextField searchField;
    
    private final PosteService posteService = PosteService.getInstance();
    private final ObservableList<Poste> postesList = FXCollections.observableArrayList();
    private Poste selectedPoste;
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
        
        setupSearch();
        loadPostes();
    }
    
    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((obs, old, newVal) -> filterPostes(newVal));
        }
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleFilterAll() {
        currentFilter = "all";
        displayPostes(postesList);
    }
    
    @FXML
    private void handleFilterFree() {
        currentFilter = "free";
        List<Poste> filtered = postesList.stream()
            .filter(p -> p.getStatus() == Poste.PosteStatus.LIBRE)
            .toList();
        displayPostes(filtered);
    }
    
    @FXML
    private void handleFilterOccupied() {
        currentFilter = "occupied";
        List<Poste> filtered = postesList.stream()
            .filter(p -> p.getStatus() == Poste.PosteStatus.OCCUPE)
            .toList();
        displayPostes(filtered);
    }
    
    @FXML
    private void handleFilterReserved() {
        currentFilter = "reserved";
        List<Poste> filtered = postesList.stream()
            .filter(p -> p.getStatus() == Poste.PosteStatus.RESERVE)
            .toList();
        displayPostes(filtered);
    }
    
    @FXML
    private void handleFilterMaintenance() {
        currentFilter = "maintenance";
        List<Poste> filtered = postesList.stream()
            .filter(p -> p.getStatus() == Poste.PosteStatus.MAINTENANCE)
            .toList();
        displayPostes(filtered);
    }
    
    @FXML
    private void handleFilterIndisponible() {
        currentFilter = "indisponible";
        List<Poste> filtered = postesList.stream()
            .filter(p -> p.getStatus() == Poste.PosteStatus.INDISPONIBLE)
            .toList();
        displayPostes(filtered);
    }
    
    @FXML
    private void handleRefresh() {
        loadPostes();
    }
    
    @FXML
    private void handleClearFilters() {
        currentFilter = "all";
        if (searchField != null) {
            searchField.clear();
        }
        displayPostes(postesList);
    }
    
    @FXML
    private void handleAssignFlight() {
        logger.info("Assign flight requested");
        showActionDialog("Assigner un vol à un poste", "assign");
    }
    
    @FXML
    private void handleCivilRecall() {
        logger.info("Civil recall requested");
        showActionDialog("Rappel Civil - Libérer un poste", "recall");
    }
    
    @FXML
    private void handleMilitaryTransfer() {
        logger.info("Military transfer requested");
        showActionDialog("Transfert Militaire", "transfer");
    }
    
    private void showActionDialog(String title, String action) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(title);
        
        switch (action) {
            case "assign":
                dialog.setHeaderText("🤖 Allocation Automatique de Parking");
                dialog.setContentText("Entrez l'ICAO24 du vol pour allocation automatique:\n" +
                        "L'algorithme sélectionnera le meilleur poste disponible.\n\n" +
                        "💡 Vérifiez les vols dans 'Radar Live' d'abord\n" +
                        "Exemple: 3c6444");
                dialog.showAndWait().ifPresent(input -> {
                    // Clean input: remove parentheses, spaces, etc.
                    String icao24 = input.replaceAll("[()\\s,]+", "").trim();
                    if (!icao24.isEmpty() && icao24.length() >= 6) {
                        logger.debug("Requesting auto-allocation for ICAO24: '{}'", icao24);
                        assignFlightToSpot(icao24, null); // spotCode not used anymore
                    } else {
                        showError("Format invalide", "Format attendu: ICAO24 (6+ caractères)\nExemple: 3c6444");
                    }
                });
                break;
                
            case "recall":
                dialog.setContentText("Entrez le code du poste à libérer:");
                dialog.showAndWait().ifPresent(code -> civilRecall(code.trim()));
                break;
                
            case "transfer":
                dialog.setContentText("Entrez le code du poste à transférer:");
                dialog.showAndWait().ifPresent(code -> militaryTransfer(code.trim()));
                break;
        }
    }
    
    private void assignFlightToSpot(String icao24, String spotCode) {
        logger.info("Requesting automatic parking allocation for flight: {}", icao24);
        
        new Thread(() -> {
            try {
                com.aige.apronsmart.services.ParkingService parkingService = 
                    com.aige.apronsmart.services.ParkingService.getInstance();
                
                // API uses automatic allocation - no manual spot selection
                java.util.Map<String, Object> response = parkingService.assignParking(icao24);
                logger.info("Auto-allocation response: {}", response);
                
                // Check for error responses
                if (response != null && !Boolean.TRUE.equals(response.get("success"))) {
                    String detail = response.getOrDefault("detail", "Unknown error").toString();
                    javafx.application.Platform.runLater(() -> 
                        showError("Erreur d'allocation automatique", 
                            "L'API a retourné une erreur:\n" + detail + 
                            "\n\n💡 Astuce: Vérifiez que le vol existe dans le module 'Radar Live' et qu'il n'a pas déjà un parking assigné."));
                    return;
                }
                
                // Extract allocated spot
                String allocatedSpot = response.getOrDefault("spot_id", "inconnu").toString();
                String spotType = response.getOrDefault("spot_type", "").toString();
                boolean overflow = Boolean.TRUE.equals(response.get("overflow_to_military"));
                
                javafx.application.Platform.runLater(() -> {
                    // Build success message
                    StringBuilder message = new StringBuilder("✅ Vol " + icao24 + " assigné automatiquement au poste " + allocatedSpot);
                    if (!spotType.isEmpty()) {
                        message.append(" (").append(spotType).append(")");
                    }
                    if (overflow) {
                        message.append("\n⚠️ Débordement vers zone militaire");
                    }
                    if (response.containsKey("reason")) {
                        message.append("\n\nRaison: ").append(response.get("reason"));
                    }
                    if (response != null) {
                        if (response.containsKey("message")) {
                            message.append("\n\n").append(response.get("message"));
                        }
                        if (response.containsKey("allocation_id")) {
                            message.append("\nID Allocation: ").append(response.get("allocation_id"));
                        }
                    }
                    
                    showInfo("Succès", message.toString());
                    
                    // Wait a bit for API to update, then refresh
                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // Wait 500ms for API consistency
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        javafx.application.Platform.runLater(this::loadPostes);
                    }).start();
                });
            } catch (java.io.IOException e) {
                logger.error("Assignment failed", e);
                javafx.application.Platform.runLater(() -> 
                    showError("Erreur", "Impossible d'assigner le vol: " + e.getMessage()));
            }
        }).start();
    }
    
    private void civilRecall(String spotCode) {
        new Thread(() -> {
            try {
                com.aige.apronsmart.services.ParkingService parkingService = 
                    com.aige.apronsmart.services.ParkingService.getInstance();
                
                java.util.Map<String, Object> response = parkingService.civilRecall(spotCode);
                javafx.application.Platform.runLater(() -> {
                    showInfo("Succès", "Rappel civil effectué pour le poste " + spotCode);
                    loadPostes(); // Refresh
                });
            } catch (java.io.IOException e) {
                javafx.application.Platform.runLater(() -> 
                    showError("Erreur", "Impossible d'effectuer le rappel: " + e.getMessage()));
            }
        }).start();
    }
    
    private void militaryTransfer(String spotCode) {
        new Thread(() -> {
            try {
                com.aige.apronsmart.services.ParkingService parkingService = 
                    com.aige.apronsmart.services.ParkingService.getInstance();
                
                java.util.Map<String, Object> response = parkingService.militaryTransfer(spotCode);
                logger.info("Military transfer response: {}", response);
                
                javafx.application.Platform.runLater(() -> {
                    // Build detailed message
                    StringBuilder message = new StringBuilder("Transfert militaire effectué pour le poste " + spotCode);
                    if (response != null) {
                        if (response.containsKey("message")) {
                            message.append("\n\n").append(response.get("message"));
                        }
                        if (response.containsKey("new_spot")) {
                            message.append("\nNouveau poste: ").append(response.get("new_spot"));
                        }
                        if (response.containsKey("old_spot")) {
                            message.append("\nAncien poste: ").append(response.get("old_spot"));
                        }
                    }
                    
                    showInfo("Succès", message.toString());
                    loadPostes(); // Refresh
                });
            } catch (java.io.IOException e) {
                logger.error("Military transfer failed", e);
                javafx.application.Platform.runLater(() -> 
                    showError("Erreur", "Impossible d'effectuer le transfert: " + e.getMessage()));
            }
        }).start();
    }
    
    private void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void loadPostes() {
        new Thread(() -> {
            try {
                // Load parking spots from API
                com.aige.apronsmart.services.ParkingService parkingService = 
                    com.aige.apronsmart.services.ParkingService.getInstance();
                
                List<com.aige.apronsmart.models.ParkingSpot> apiSpots = parkingService.getAllParkingSpots();
                logger.info("Loaded {} parking spots from API", apiSpots.size());
                
                // Log each API spot for debugging
                for (com.aige.apronsmart.models.ParkingSpot spot : apiSpots) {
                    logger.info("API Spot - ID: {}, spotNumber: '{}', code: '{}', status: {}, type: {}", 
                        spot.getSpotId(), spot.getSpotNumber(), spot.getCode(), spot.getStatus(), spot.getType());
                }
                
                com.aige.apronsmart.models.ParkingAvailability availability = parkingService.getAvailability();
                
                // Get current allocations to determine occupied spots
                List<com.aige.apronsmart.models.ParkingAllocation> allocations = new java.util.ArrayList<>();
                try {
                    allocations = parkingService.getAllocations();
                    logger.info("Loaded {} parking allocations", allocations.size());
                    
                    // Log each allocation for debugging
                    for (com.aige.apronsmart.models.ParkingAllocation alloc : allocations) {
                        logger.debug("Allocation: Spot {} - Flight: {} ({})", 
                            alloc.getSpotCode(), alloc.getCallsign(), alloc.getIcao24());
                    }
                } catch (Exception e) {
                    logger.warn("Could not load allocations: {}", e.getMessage());
                }
                
                // Define all 18 parking spots of the airport
                String[] allSpotNumbers = {
                    "N1", "N2", "P1", "P2", "P3", "P4", "P5", 
                    "S1", "S2", "S3", "S4", "S5", "S6", "S7", "S8", "S9", 
                    "S10A", "S10B"
                };
                
                // Map hardcoded spot codes to API numeric codes
                // API returns spotNumber as "1", "2", "4", "5" etc.
                Map<String, String> codeToApiNumber = new java.util.HashMap<>();
                codeToApiNumber.put("P2", "2");  // Cargo spot 2 -> API spot 2
                codeToApiNumber.put("P1", "1");  // Cargo spot 1 -> API spot 1
                codeToApiNumber.put("P4", "4");  // Cargo spot 4 -> API spot 4
                codeToApiNumber.put("S1", "5");  // Pax spot 1 -> API spot 5
                
                // Reverse mapping: API number -> hardcoded code
                Map<String, String> apiNumberToCode = new java.util.HashMap<>();
                for (Map.Entry<String, String> entry : codeToApiNumber.entrySet()) {
                    apiNumberToCode.put(entry.getValue(), entry.getKey());
                }
                
                // Create a map of API spots for quick lookup (using both API number and our codes)
                Map<String, com.aige.apronsmart.models.ParkingSpot> apiSpotMap = new java.util.HashMap<>();
                for (com.aige.apronsmart.models.ParkingSpot spot : apiSpots) {
                    if (spot.getSpotNumber() != null) {
                        // Map by API number (convert Integer to String)
                        String spotNumStr = String.valueOf(spot.getSpotNumber());
                        apiSpotMap.put(spotNumStr, spot);
                        // Also map by our hardcoded code if mapping exists
                        String ourCode = apiNumberToCode.get(spotNumStr);
                        if (ourCode != null) {
                            apiSpotMap.put(ourCode, spot);
                            logger.debug("Mapped API spot {} to our code {}", spot.getSpotNumber(), ourCode);
                        }
                    }
                }
                
                // Create a map of allocations by spot code
                Map<String, com.aige.apronsmart.models.ParkingAllocation> allocationMap = new java.util.HashMap<>();
                for (com.aige.apronsmart.models.ParkingAllocation alloc : allocations) {
                    if (alloc.getSpotCode() != null) {
                        // Map by API spot code
                        allocationMap.put(alloc.getSpotCode(), alloc);
                        // Also map by our hardcoded code if mapping exists
                        String ourCode = apiNumberToCode.get(alloc.getSpotCode());
                        if (ourCode != null) {
                            allocationMap.put(ourCode, alloc);
                        }
                    }
                }
                
                // Convert all spots to Poste objects
                List<Poste> postes = new java.util.ArrayList<>();
                int generatedId = 1;
                
                for (String spotNumber : allSpotNumbers) {
                    Poste poste = new Poste();
                    poste.setCode(spotNumber);
                    poste.setId((long) generatedId++);
                    
                    // Set default type and zone based on spot code
                    if (spotNumber.startsWith("N")) {
                        poste.setType(Poste.PosteType.MILITARY);
                        poste.setZone(Poste.PosteZone.MILITARY);
                    } else if (spotNumber.startsWith("S")) {
                        poste.setType(Poste.PosteType.PAX);
                        poste.setZone(Poste.PosteZone.A);
                    } else if (spotNumber.startsWith("P")) {
                        poste.setType(Poste.PosteType.CARGO);
                        poste.setZone(Poste.PosteZone.B);
                    } else {
                        poste.setType(Poste.PosteType.GENERAL);
                        poste.setZone(Poste.PosteZone.C);
                    }
                    
                    // Check if we have data from API for this spot
                    com.aige.apronsmart.models.ParkingSpot spot = apiSpotMap.get(spotNumber);
                    com.aige.apronsmart.models.ParkingAllocation allocation = allocationMap.get(spotNumber);
                    
                    if (spot != null && spot.getSpotId() != null) {
                        // Use spotId hash for internal ID (API doesn't provide numeric ID)
                        poste.setId((long) spot.getSpotId().hashCode());
                    }
                    
                    // Determine status: allocation takes priority, then spot status
                    boolean statusSet = false;
                    
                    // First check if there's an active allocation (occupied)
                    if (allocation != null && (allocation.getIcao24() != null || allocation.getCallsign() != null)) {
                        poste.setStatus(Poste.PosteStatus.OCCUPE);
                        String flightId = allocation.getCallsign() != null ? allocation.getCallsign() : allocation.getIcao24();
                        poste.setOccupiedByCallsign(flightId);
                        statusSet = true;
                        logger.debug("Spot {} occupied by allocation: {}", spotNumber, flightId);
                    }
                    
                    // Then check API spot status
                    if (!statusSet && spot != null) {
                        if (spot.getStatus() != null) {
                            switch (spot.getStatus().toLowerCase()) {
                                case "available":
                                    poste.setStatus(Poste.PosteStatus.LIBRE);
                                    logger.debug("Spot {} is available from API", spotNumber);
                                    break;
                                case "occupied":
                                    poste.setStatus(Poste.PosteStatus.OCCUPE);
                                    logger.debug("Spot {} is occupied from API", spotNumber);
                                    break;
                                case "maintenance":
                                    poste.setStatus(Poste.PosteStatus.MAINTENANCE);
                                    logger.debug("Spot {} is in maintenance from API", spotNumber);
                                    break;
                                case "reserved":
                                    poste.setStatus(Poste.PosteStatus.RESERVE);
                                    logger.debug("Spot {} is reserved from API", spotNumber);
                                    break;
                                default:
                                    poste.setStatus(Poste.PosteStatus.LIBRE);
                            }
                        } else {
                            // API returned the spot but without status - mark as free
                            poste.setStatus(Poste.PosteStatus.LIBRE);
                            logger.debug("Spot {} from API has no status, marking as LIBRE", spotNumber);
                        }
                        statusSet = true;
                    }
                    
                    // If API didn't return this spot at all, mark as OCCUPE
                    if (!statusSet) {
                        poste.setStatus(Poste.PosteStatus.OCCUPE);
                        logger.debug("Spot {} not in API, marking as OCCUPE (occupied by default)", spotNumber);
                    }
                    
                    postes.add(poste);
                }
                
                logger.info("Created {} parking spots - API: {}, Allocations: {}", 
                           postes.size(), apiSpots.size(), allocations.size());
                
                Platform.runLater(() -> {
                    postesList.clear();
                    postesList.addAll(postes);
                    displayPostes(postes);
                    
                    // Calculate real stats from actual postes data
                    long total = postes.size();
                    long libre = postes.stream().filter(p -> p.getStatus() == Poste.PosteStatus.LIBRE).count();
                    long occupe = postes.stream().filter(p -> p.getStatus() == Poste.PosteStatus.OCCUPE).count();
                    long maintenance = postes.stream().filter(p -> p.getStatus() == Poste.PosteStatus.MAINTENANCE).count();
                    long reserve = postes.stream().filter(p -> p.getStatus() == Poste.PosteStatus.RESERVE).count();
                    long indisponible = postes.stream().filter(p -> p.getStatus() == Poste.PosteStatus.INDISPONIBLE).count();
                    
                    logger.info("Parking stats - Total: {}, Libre: {}, Occupé: {}, Maintenance: {}, Réservé: {}, Indisponible: {}", 
                               total, libre, occupe, maintenance, reserve, indisponible);
                    
                    // Update UI with real counts
                    if (totalPostesLabel != null) totalPostesLabel.setText(String.valueOf(total));
                    if (freePostesLabel != null) freePostesLabel.setText(String.valueOf(libre));
                    if (occupiedPostesLabel != null) occupiedPostesLabel.setText(String.valueOf(occupe));
                    if (reservedPostesLabel != null) reservedPostesLabel.setText(String.valueOf(reserve));
                });
            } catch (Exception e) {
                logger.error("Error loading postes from API", e);
                Platform.runLater(() -> {
                    displaySamplePostes();
                });
            }
        }).start();
    }
    
    private void displaySamplePostes() {
        if (postesGrid == null) return;
        postesGrid.getChildren().clear();
        
        // Sample poste data - 18 postes de l'aéroport
        String[][] samplePostes = {
            {"N2", "LIBRE", "#4CAF50"},
            {"N1", "OCCUPE", "#F44336"},
            {"P1", "LIBRE", "#4CAF50"},
            {"P2", "OCCUPE", "#F44336"},
            {"P3", "RESERVE", "#FF9800"},
            {"P4", "LIBRE", "#4CAF50"},
            {"P5", "OCCUPE", "#F44336"},
            {"S1", "LIBRE", "#4CAF50"},
            {"S2", "MAINTENANCE", "#9E9E9E"},
            {"S3", "OCCUPE", "#F44336"},
            {"S4", "LIBRE", "#4CAF50"},
            {"S5", "RESERVE", "#FF9800"},
            {"S6", "LIBRE", "#4CAF50"},
            {"S7", "OCCUPE", "#F44336"},
            {"S8", "LIBRE", "#4CAF50"},
            {"S9", "OCCUPE", "#F44336"},
            {"S10A", "LIBRE", "#4CAF50"},
            {"S10B", "RESERVE", "#FF9800"},
        };
        
        int col = 0;
        int row = 0;
        int maxCols = 6;
        
        for (String[] poste : samplePostes) {
            VBox card = createSamplePosteCard(poste[0], poste[1], poste[2]);
            postesGrid.add(card, col, row);
            
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
        
        updateStatsWithSample();
    }
    
    private VBox createSamplePosteCard(String code, String status, String color) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setMinWidth(60);
        card.setMinHeight(70);
        card.getStyleClass().add("poste-card");
        card.setStyle("-fx-background-color: #1a2632; -fx-background-radius: 8; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8;");
        
        // Poste code
        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Status indicator
        Region indicator = new Region();
        indicator.setMinSize(8, 8);
        indicator.setMaxSize(8, 8);
        indicator.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        
        // Status text
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(codeLabel, indicator, statusLabel);
        
        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(card.getStyle() + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(23, 115, 207, 0.4), 10, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #1a2632; -fx-background-radius: 12; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 12;"));
        
        return card;
    }
    
    private void displayPostes(List<Poste> postes) {
        if (postesGrid == null) return;
        postesGrid.getChildren().clear();
        
        int col = 0;
        int row = 0;
        int maxCols = 6;
        
        for (Poste poste : postes) {
            VBox posteCard = createPosteCard(poste);
            postesGrid.add(posteCard, col, row);
            
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createPosteCard(Poste poste) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setMinWidth(60);
        card.setMinHeight(70);
        card.getStyleClass().add("poste-card");
        
        String color = getStatusColor(poste.getStatus());
        card.setStyle("-fx-background-color: #1a2632; -fx-background-radius: 8; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8;");
        
        // Poste code
        Label codeLabel = new Label(poste.getCode());
        codeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Status indicator
        Region indicator = new Region();
        indicator.setMinSize(8, 8);
        indicator.setMaxSize(8, 8);
        indicator.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        
        // Status text
        Label statusLabel = new Label(poste.getStatus().getDisplayName());
        statusLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(codeLabel, indicator, statusLabel);
        
        // Occupied by flight
        if (poste.getOccupiedByCallsign() != null) {
            Label occupiedLabel = new Label("✈ " + poste.getOccupiedByCallsign());
            occupiedLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: #8892a0;");
            card.getChildren().add(occupiedLabel);
        }
        
        // Click handler
        card.setOnMouseClicked(event -> {
            selectedPoste = poste;
            showPosteDetails(poste);
        });
        
        // Hover effect
        final String baseStyle = card.getStyle();
        card.setOnMouseEntered(e -> card.setStyle(baseStyle + "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(23, 115, 207, 0.4), 10, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle(baseStyle));
        
        return card;
    }
    
    private String getStatusColor(Poste.PosteStatus status) {
        if (status == null) return "#8892a0";
        switch (status) {
            case LIBRE: return "#4CAF50";
            case OCCUPE: return "#F44336";
            case RESERVE: return "#FF9800";
            case MAINTENANCE: return "#9E9E9E";
            default: return "#8892a0";
        }
    }
    
    private void updateStats(List<Poste> postes) {
        int total = postes.size();
        long free = postes.stream().filter(p -> p.getStatus() == Poste.PosteStatus.LIBRE).count();
        long occupied = postes.stream().filter(p -> p.getStatus() == Poste.PosteStatus.OCCUPE).count();
        
        if (totalPostesLabel != null) totalPostesLabel.setText(String.valueOf(total));
        if (freePostesLabel != null) freePostesLabel.setText(String.valueOf(free));
        if (occupiedPostesLabel != null) occupiedPostesLabel.setText(String.valueOf(occupied));
    }
    
    private void updateStatsFromAPI(com.aige.apronsmart.models.ParkingAvailability availability) {
        if (totalPostesLabel != null) totalPostesLabel.setText(String.valueOf(availability.getTotalSpots()));
        if (freePostesLabel != null) freePostesLabel.setText(String.valueOf(availability.getAvailable()));
        if (occupiedPostesLabel != null) occupiedPostesLabel.setText(String.valueOf(availability.getOccupied()));
    }
    
    private void updateStatsWithSample() {
        if (totalPostesLabel != null) totalPostesLabel.setText("18");
        if (freePostesLabel != null) freePostesLabel.setText("8");
        if (occupiedPostesLabel != null) occupiedPostesLabel.setText("7");
    }
    
    private void filterPostes(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            displayPostes(postesList);
            return;
        }
        
        String filter = searchText.toLowerCase();
        List<Poste> filtered = postesList.stream()
            .filter(p -> p.getCode().toLowerCase().contains(filter) ||
                        (p.getOccupiedByCallsign() != null && p.getOccupiedByCallsign().toLowerCase().contains(filter)))
            .toList();
        displayPostes(filtered);
    }
    
    private void showPosteDetails(Poste poste) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du Poste");
        alert.setHeaderText("Poste " + poste.getCode());
        
        StringBuilder content = new StringBuilder();
        
        // Status
        if (poste.getStatus() != null) {
            content.append("Statut: ").append(poste.getStatus().getDisplayName()).append("\n");
        }
        
        // Type
        if (poste.getType() != null) {
            content.append("Type: ").append(poste.getType().getDisplayName()).append("\n");
        }
        
        // Zone
        if (poste.getZone() != null) {
            content.append("Zone: ").append(poste.getZone().getDisplayName()).append("\n");
        }
        
        // Occupied by
        if (poste.getOccupiedByCallsign() != null) {
            content.append("Occupé par: ").append(poste.getOccupiedByCallsign()).append("\n");
        }
        
        alert.setContentText(content.toString());
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
