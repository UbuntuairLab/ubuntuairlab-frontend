package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.Flight;
import com.aige.apronsmart.services.FlightService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for History module - UbuntuAirLab
 * Provides historical flight data with advanced filtering
 */
public class HistoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    
    @FXML private ImageView backgroundImageView;
    @FXML private VBox historyContainer;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterTypeComboBox;
    @FXML private ComboBox<String> flightTypeComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button searchButton;
    @FXML private Button exportButton;
    @FXML private TableView<Flight> resultsTable;
    @FXML private Label resultsCountLabel;
    
    private final FlightService flightService = FlightService.getInstance();
    private ObservableList<Flight> flightsList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Bind background image to fill the entire screen
        if (backgroundImageView != null && backgroundImageView.getParent() instanceof StackPane) {
            StackPane parent = (StackPane) backgroundImageView.getParent();
            backgroundImageView.fitWidthProperty().bind(parent.widthProperty());
            backgroundImageView.fitHeightProperty().bind(parent.heightProperty());
            backgroundImageView.setPreserveRatio(false);
        }
        
        setupControls();
        setupTable();
        loadInitialData();
    }
    
    private void setupControls() {
        // Date pickers - default to last 7 days
        if (startDatePicker != null) startDatePicker.setValue(LocalDate.now().minusDays(7));
        if (endDatePicker != null) endDatePicker.setValue(LocalDate.now());
        
        // Filter type combo
        if (filterTypeComboBox != null) {
            filterTypeComboBox.getItems().addAll(
                "Tous les vols", "Arrivées", "Départs", "Vols futurs"
            );
            filterTypeComboBox.setValue("Tous les vols");
        }
        
        // Flight type filter
        if (flightTypeComboBox != null) {
            flightTypeComboBox.getItems().addAll(
                "Tous types", "Arrivée", "Départ"
            );
            flightTypeComboBox.setValue("Tous types");
        }
        
        // Status filter
        if (statusComboBox != null) {
            statusComboBox.getItems().addAll(
                "Tous statuts", "Actif", "Atterri", "Programmé", "En route", "Approche", "Retardé", "Annulé", "Parti"
            );
            statusComboBox.setValue("Tous statuts");
        }
    }
    
    private void setupTable() {
        if (resultsTable == null) return;
        
        TableColumn<Flight, String> callsignCol = new TableColumn<>("Indicatif");
        callsignCol.setCellValueFactory(new PropertyValueFactory<>("callsign"));
        callsignCol.setPrefWidth(100);
        
        TableColumn<Flight, String> originCol = new TableColumn<>("Origine");
        originCol.setCellValueFactory(new PropertyValueFactory<>("origin"));
        originCol.setPrefWidth(120);
        
        TableColumn<Flight, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("aircraftType"));
        typeCol.setPrefWidth(80);
        
        TableColumn<Flight, String> statusCol = new TableColumn<>("Statut");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Flight, String> parkingCol = new TableColumn<>("Parking");
        parkingCol.setCellValueFactory(new PropertyValueFactory<>("assignedPosteCode"));
        parkingCol.setPrefWidth(80);
        
        resultsTable.getColumns().addAll(callsignCol, originCol, typeCol, statusCol, parkingCol);
        resultsTable.setItems(flightsList);
    }
    
    private void loadInitialData() {
        handleSearch();
    }
    
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }
    
    @FXML
    private void handleSearch() {
        logger.info("Searching flight history");
        
        new Thread(() -> {
            try {
                // Determine filters
                String flightType = null;
                if (flightTypeComboBox != null && !"Tous types".equals(flightTypeComboBox.getValue())) {
                    flightType = "Arrivée".equals(flightTypeComboBox.getValue()) ? "arrival" : "departure";
                }
                
                String status = null;
                if (statusComboBox != null && !"Tous statuts".equals(statusComboBox.getValue())) {
                    status = statusComboBox.getValue().toLowerCase();
                }
                
                // For future flights (> 7 days ahead)
                String futureDate = null;
                if (filterTypeComboBox != null && "Vols futurs".equals(filterTypeComboBox.getValue())) {
                    futureDate = LocalDate.now().plusDays(8).format(DateTimeFormatter.ISO_LOCAL_DATE);
                }
                
                // Fetch flights with filters
                List<Flight> flights = flightService.getFlightsWithFilters(
                    flightType, 
                    status, 
                    100, 
                    0,
                    futureDate
                );
                
                // Apply text search filter if any
                String searchText = searchField != null ? searchField.getText() : "";
                if (searchText != null && !searchText.trim().isEmpty()) {
                    final String search = searchText.toLowerCase();
                    flights = flights.stream()
                        .filter(f -> 
                            f.getCallsign().toLowerCase().contains(search) ||
                            (f.getOrigin() != null && f.getOrigin().toLowerCase().contains(search)) ||
                            (f.getCompany() != null && f.getCompany().toLowerCase().contains(search)) ||
                            (f.getAssignedPosteCode() != null && f.getAssignedPosteCode().toLowerCase().contains(search))
                        )
                        .toList();
                }
                
                final List<Flight> finalFlights = flights;
                Platform.runLater(() -> {
                    flightsList.clear();
                    flightsList.addAll(finalFlights);
                    if (resultsCountLabel != null) {
                        resultsCountLabel.setText(finalFlights.size() + " résultat(s)");
                    }
                    logger.info("Loaded {} historical flights", finalFlights.size());
                });
                
            } catch (Exception e) {
                logger.error("Error loading flight history", e);
                Platform.runLater(() -> showError("Erreur", "Impossible de charger l'historique des vols"));
            }
        }).start();
    }
    
    @FXML
    private void handleExport() {
        logger.info("Exporting history to CSV");
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Exporter l'historique");
            fileChooser.setInitialFileName("historique_vols_" + LocalDate.now() + ".csv");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            
            java.io.File file = fileChooser.showSaveDialog(historyContainer.getScene().getWindow());
            if (file != null) {
                exportToCSV(file);
                showSuccess("Exportation réussie vers: " + file.getName());
            }
        } catch (Exception e) {
            logger.error("Error exporting history", e);
            showError("Erreur", "Impossible d'exporter l'historique");
        }
    }
    
    private void exportToCSV(java.io.File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Header
            writer.append("Indicatif,Origine,Type,Statut,Parking,Altitude,Vitesse\n");
            
            // Data
            for (Flight flight : flightsList) {
                writer.append(flight.getCallsign()).append(",");
                writer.append(flight.getOrigin() != null ? flight.getOrigin() : "").append(",");
                writer.append(flight.getAircraftType() != null ? flight.getAircraftType() : "").append(",");
                writer.append(flight.getStatus() != null ? flight.getStatus().toString() : "").append(",");
                writer.append(flight.getAssignedPosteCode() != null ? flight.getAssignedPosteCode() : "").append(",");
                writer.append(String.valueOf(flight.getAltitude())).append(",");
                writer.append(String.valueOf(flight.getHeading())).append("\n");
            }
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
