package com.aige.apronsmart.controllers.modules;

import com.aige.apronsmart.models.Flight;
import com.aige.apronsmart.services.FlightService;
import com.aige.apronsmart.services.PredictionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictionsController {

    @FXML private ComboBox<Flight> flightComboBox;
    @FXML private Label icao24Label;
    @FXML private Label callsignLabel;
    @FXML private Label flightTypeLabel;
    @FXML private Label statusLabel;
    
    @FXML private TextField altitudeField;
    @FXML private TextField speedField;
    @FXML private TextField distanceField;
    @FXML private TextField temperatureField;
    
    @FXML private Button predictButton;
    @FXML private Button batchPredictButton;
    @FXML private Button checkHealthButton;
    @FXML private Button refreshButton;
    
    @FXML private VBox resultsContainer;
    
    // Model 1 - ETA
    @FXML private Label etaLabel;
    @FXML private Label delay15Label;
    @FXML private Label delay30Label;
    
    // Model 2 - Occupation
    @FXML private Label occupationLabel;
    @FXML private Label occupationRangeLabel;
    
    // Model 3 - Conflicts
    @FXML private Label decisionLabel;
    @FXML private Label conflictRiskLabel;
    @FXML private Label saturationRiskLabel;
    @FXML private Label explanationLabel;
    
    // Metadata
    @FXML private Label timestampLabel;
    @FXML private Label versionLabel;
    
    private final FlightService flightService = FlightService.getInstance();
    private final PredictionService predictionService = PredictionService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private Flight selectedFlight;

    @FXML
    public void initialize() {
        setupFlightComboBox();
        handleLoadFlights();
    }

    private void setupFlightComboBox() {
        flightComboBox.setCellFactory(lv -> new ListCell<Flight>() {
            @Override
            protected void updateItem(Flight flight, boolean empty) {
                super.updateItem(flight, empty);
                if (empty || flight == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (%s)", 
                        flight.getCallsign() != null ? flight.getCallsign() : "N/A",
                        flight.getIcao24(),
                        flight.getStatus() != null ? flight.getStatus().name() : "N/A"
                    ));
                }
            }
        });
        
        flightComboBox.setButtonCell(new ListCell<Flight>() {
            @Override
            protected void updateItem(Flight flight, boolean empty) {
                super.updateItem(flight, empty);
                if (empty || flight == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s", 
                        flight.getCallsign() != null ? flight.getCallsign() : "N/A",
                        flight.getIcao24()
                    ));
                }
            }
        });
        
        flightComboBox.setOnAction(e -> handleFlightSelection());
    }

    @FXML
    private void handleLoadFlights() {
        refreshButton.setDisable(true);
        new Thread(() -> {
            try {
                List<Flight> flights = flightService.getActiveFlights();
                
                Platform.runLater(() -> {
                    flightComboBox.setItems(FXCollections.observableArrayList(flights));
                    if (!flights.isEmpty()) {
                        showInfo("Vols chargés", flights.size() + " vols actifs trouvés");
                    } else {
                        showWarning("Vols chargés", "Aucun vol actif trouvé");
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> showError("Erreur lors du chargement des vols", e.getMessage()));
            } finally {
                Platform.runLater(() -> refreshButton.setDisable(false));
            }
        }).start();
    }

    private void handleFlightSelection() {
        selectedFlight = flightComboBox.getValue();
        if (selectedFlight != null) {
            icao24Label.setText(selectedFlight.getIcao24());
            callsignLabel.setText(selectedFlight.getCallsign() != null ? selectedFlight.getCallsign() : "N/A");
            flightTypeLabel.setText(selectedFlight.getNature() != null ? selectedFlight.getNature().name() : "N/A");
            statusLabel.setText(selectedFlight.getStatus() != null ? selectedFlight.getStatus().name() : "N/A");
            
            // Auto-fill fields if data available
            if (selectedFlight.getAltitude() != null && selectedFlight.getAltitude() > 0) {
                altitudeField.setText(String.valueOf((int)(selectedFlight.getAltitude() * 3.28084))); // m to ft
            }
            if (selectedFlight.getSpeed() != null && selectedFlight.getSpeed() > 0) {
                speedField.setText(String.valueOf((int)(selectedFlight.getSpeed() * 1.94384))); // m/s to knots
            }
            
            predictButton.setDisable(false);
        } else {
            clearFlightInfo();
            predictButton.setDisable(true);
        }
    }

    @FXML
    private void handlePredict() {
        if (selectedFlight == null) {
            showError("Erreur", "Veuillez sélectionner un vol");
            return;
        }
        
        predictButton.setDisable(true);
        resultsContainer.setVisible(false);
        
        new Thread(() -> {
            try {
                Map<String, Object> requestData = buildPredictionRequest();
                String jsonRequest = objectMapper.writeValueAsString(requestData);
                
                okhttp3.RequestBody body = okhttp3.RequestBody.create(
                    jsonRequest, 
                    okhttp3.MediaType.parse("application/json; charset=utf-8")
                );
                
                String url = "https://air-lab.bestwebapp.tech/api/v1/predictions/predict";
                okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + getAuthToken())
                    .build();
                
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
                
                String response = client.newCall(request).execute().body().string();
                
                Platform.runLater(() -> {
                    try {
                        displayPredictionResults(response);
                    } catch (IOException e) {
                        showError("Erreur d'affichage", e.getMessage());
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> showError("Erreur de prédiction", e.getMessage()));
            } finally {
                Platform.runLater(() -> predictButton.setDisable(false));
            }
        }).start();
    }

    private Map<String, Object> buildPredictionRequest() {
        Map<String, Object> data = new HashMap<>();
        data.put("icao24", selectedFlight.getIcao24());
        data.put("type_vol", selectedFlight.getNature() != null ? selectedFlight.getNature().name().toLowerCase() : "civil");
        
        // Required fields (will be auto-filled by API if not provided)
        if (!altitudeField.getText().isEmpty()) {
            data.put("altitude", Double.parseDouble(altitudeField.getText()));
        }
        if (!speedField.getText().isEmpty()) {
            data.put("vitesse_actuelle", Double.parseDouble(speedField.getText()));
        }
        if (!distanceField.getText().isEmpty()) {
            data.put("distance_piste", Double.parseDouble(distanceField.getText()));
        }
        
        // Optional weather parameter
        if (!temperatureField.getText().isEmpty()) {
            Map<String, Object> meteo = new HashMap<>();
            meteo.put("temperature", Double.parseDouble(temperatureField.getText()));
            data.put("meteo", meteo);
        }
        
        return data;
    }

    private void displayPredictionResults(String jsonResponse) throws IOException {
        JsonNode root = objectMapper.readTree(jsonResponse);
        
        // Model 1 - ETA
        JsonNode model1 = root.get("model_1_eta");
        if (model1 != null) {
            JsonNode etaNode = model1.get("eta_ajuste");
            if (etaNode != null && !etaNode.isNull()) {
                etaLabel.setText(formatDateTime(etaNode.asText()));
            } else {
                etaLabel.setText("N/A");
            }
            
            JsonNode probaDelay = model1.get("proba_delay");
            if (probaDelay != null) {
                delay15Label.setText(formatProbability(probaDelay.get("15min")));
                delay30Label.setText(formatProbability(probaDelay.get("30min")));
            }
        }
        
        // Model 2 - Occupation
        JsonNode model2 = root.get("model_2_occupation");
        if (model2 != null) {
            JsonNode tempsOccup = model2.get("temps_occupation_minutes");
            if (tempsOccup != null && !tempsOccup.isNull()) {
                occupationLabel.setText(tempsOccup.asInt() + " minutes");
                
                JsonNode intervalConf = model2.get("interval_confiance_95");
                if (intervalConf != null && intervalConf.isArray()) {
                    occupationRangeLabel.setText(String.format("[%d - %d] min", 
                        intervalConf.get(0).asInt(), 
                        intervalConf.get(1).asInt()
                    ));
                }
            } else {
                occupationLabel.setText("N/A");
            }
        }
        
        // Model 3 - Conflicts
        JsonNode model3 = root.get("model_3_conflict");
        if (model3 != null) {
            JsonNode decision = model3.get("decision_recommandee");
            if (decision != null && !decision.isNull()) {
                String decisionText = decision.asText();
                decisionLabel.setText(decisionText);
                decisionLabel.setStyle(getDecisionStyle(decisionText));
            }
            
            conflictRiskLabel.setText(formatProbability(model3.get("risque_conflit")));
            saturationRiskLabel.setText(formatProbability(model3.get("risque_saturation")));
            
            JsonNode expl = model3.get("explication");
            if (expl != null && !expl.isNull()) {
                explanationLabel.setText(expl.asText());
            }
        }
        
        // Metadata
        JsonNode timestamp = root.get("timestamp");
        if (timestamp != null) {
            timestampLabel.setText("Timestamp: " + timestamp.asText());
        }
        
        JsonNode version = root.get("version_api");
        if (version != null) {
            versionLabel.setText("Version API: " + version.asText());
        }
        
        resultsContainer.setVisible(true);
        resultsContainer.setManaged(true);
    }

    private String formatProbability(JsonNode node) {
        if (node == null || node.isNull()) {
            return "N/A";
        }
        double value = node.asDouble();
        return String.format("%.1f%%", value * 100);
    }

    private String formatDateTime(String isoDateTime) {
        try {
            LocalDateTime dt = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
            return dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            return isoDateTime;
        }
    }

    private String getDecisionStyle(String decision) {
        if (decision.toLowerCase().contains("autoriser") || decision.toLowerCase().contains("ok")) {
            return "-fx-text-fill: green; -fx-font-weight: bold;";
        } else if (decision.toLowerCase().contains("attendre") || decision.toLowerCase().contains("différer")) {
            return "-fx-text-fill: orange; -fx-font-weight: bold;";
        } else if (decision.toLowerCase().contains("refuser") || decision.toLowerCase().contains("interdire")) {
            return "-fx-text-fill: red; -fx-font-weight: bold;";
        }
        return "-fx-font-weight: bold;";
    }

    @FXML
    private void handleCheckHealth() {
        checkHealthButton.setDisable(true);
        new Thread(() -> {
            try {
                String url = "https://air-lab.bestwebapp.tech/api/v1/predictions/health";
                okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + getAuthToken())
                    .build();
                
                okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
                
                String response = client.newCall(request).execute().body().string();
                JsonNode root = objectMapper.readTree(response);
                
                boolean healthy = root.has("status") && "healthy".equalsIgnoreCase(root.get("status").asText());
                String message = root.has("message") ? root.get("message").asText() : "Service ML opérationnel";
                
                Platform.runLater(() -> {
                    if (healthy) {
                        showInfo("Santé API ML", message);
                    } else {
                        showWarning("Santé API ML", message);
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> showError("Erreur santé ML", "Impossible de vérifier l'état du service ML: " + e.getMessage()));
            } finally {
                Platform.runLater(() -> checkHealthButton.setDisable(false));
            }
        }).start();
    }

    @FXML
    private void handleResetFields() {
        altitudeField.clear();
        speedField.clear();
        distanceField.clear();
        temperatureField.clear();
    }

    @FXML
    private void handleRefresh() {
        handleLoadFlights();
    }

    @FXML
    private void handleBack() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/dashboard.fxml")
            );
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) flightComboBox.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            showError("Erreur de navigation", e.getMessage());
        }
    }

    private void clearFlightInfo() {
        icao24Label.setText("-");
        callsignLabel.setText("-");
        flightTypeLabel.setText("-");
        statusLabel.setText("-");
    }

    private String getAuthToken() {
        // Access the static authToken from BaseApiService through a service
        try {
            // The token is set globally when user logs in via BaseApiService.setAuthToken()
            // We can get it by making a simple reflection or using a getter
            // For now, use FlightService to access it through inheritance
            java.lang.reflect.Field field = com.aige.apronsmart.services.BaseApiService.class
                .getDeclaredField("authToken");
            field.setAccessible(true);
            String token = (String) field.get(null);
            return token != null ? token : "";
        } catch (Exception e) {
            return "";
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleBatchPredict() {
        // Get all active flights
        List<Flight> activeFlights;
        try {
            activeFlights = flightService.getActiveFlights();
        } catch (IOException e) {
            showError("Erreur", "Erreur lors de la récupération des vols : " + e.getMessage());
            return;
        }
        
        if (activeFlights == null || activeFlights.isEmpty()) {
            showWarning("Aucun vol actif", "Il n'y a pas de vols actifs à prédire.");
            return;
        }
        
        // Confirm action
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Prédictions en masse");
        confirmAlert.setContentText(String.format(
            "Voulez-vous lancer les prédictions pour %d vols actifs ?\n\n" +
            "Cette opération peut prendre plusieurs secondes.",
            activeFlights.size()
        ));
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        
        // Disable button during operation
        batchPredictButton.setDisable(true);
        batchPredictButton.setText("Prédiction en cours...");
        
        // Prepare flight data for batch prediction
        new Thread(() -> {
            try {
                List<Map<String, Object>> flightDataList = new java.util.ArrayList<>();
                
                for (Flight flight : activeFlights) {
                    Map<String, Object> flightData = new HashMap<>();
                    flightData.put("icao24", flight.getIcao24());
                    flightData.put("callsign", flight.getCallsign() != null ? flight.getCallsign() : "");
                    flightData.put("altitude", flight.getAltitude() != null ? flight.getAltitude() : 0.0);
                    flightData.put("speed", flight.getSpeed() != null ? flight.getSpeed() : 0.0);
                    flightData.put("distance", 50.0); // Default distance
                    flightData.put("temperature", 20.0); // Default temperature
                    
                    flightDataList.add(flightData);
                }
                
                // Call batch predict API
                Map<String, Object> response = predictionService.batchPredict(flightDataList);
                
                Platform.runLater(() -> {
                    batchPredictButton.setDisable(false);
                    batchPredictButton.setText("🔮 Prédire Tous les Vols");
                    
                    if (response != null && response.containsKey("predictions")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> predictions = (List<Map<String, Object>>) response.get("predictions");
                        
                        // Show summary
                        int successful = 0;
                        int failed = 0;
                        
                        for (Map<String, Object> pred : predictions) {
                            String status = (String) pred.get("status");
                            if ("success".equals(status)) {
                                successful++;
                            } else {
                                failed++;
                            }
                        }
                        
                        String summary = String.format(
                            "Prédictions terminées :\n\n" +
                            "✅ Réussies : %d\n" +
                            "❌ Échouées : %d\n" +
                            "📊 Total : %d",
                            successful, failed, predictions.size()
                        );
                        
                        showInfo("Prédictions en masse", summary);
                        
                        // Refresh the current flight if selected
                        if (selectedFlight != null) {
                            handlePredict();
                        }
                    } else {
                        showError("Erreur", "Erreur lors des prédictions en masse");
                    }
                });
                
            } catch (Exception e) {
                Platform.runLater(() -> {
                    batchPredictButton.setDisable(false);
                    batchPredictButton.setText("🔮 Prédire Tous les Vols");
                    
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("503")) {
                        showError("Service indisponible", "Le service de prédictions ML n'est pas disponible actuellement.");
                    } else if (errorMessage != null && errorMessage.contains("timeout")) {
                        showError("Timeout", "La requête a pris trop de temps. Réessayez avec moins de vols.");
                    } else {
                        showError("Erreur", "Erreur lors des prédictions : " + errorMessage);
                    }
                });
            }
        }).start();
    }
}
