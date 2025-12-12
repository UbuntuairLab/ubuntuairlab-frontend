package com.aige.apronsmart.controllers;

import com.aige.apronsmart.services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Hyperlink loginLink;
    
    private final AuthService authService = AuthService.getInstance();
    
    @FXML
    public void initialize() {
        logger.info("Initialisation du RegisterController");
        
        // Set default role
        roleComboBox.setValue("operator");
        
        // Add validation listeners
        usernameField.textProperty().addListener((obs, old, newVal) -> clearMessages());
        emailField.textProperty().addListener((obs, old, newVal) -> clearMessages());
        passwordField.textProperty().addListener((obs, old, newVal) -> clearMessages());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> clearMessages());
    }
    
    @FXML
    private void handleRegister() {
        clearMessages();
        
        // Validation
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();
        
        if (username.isEmpty()) {
            showError("Le nom d'utilisateur est requis");
            return;
        }
        
        if (email.isEmpty()) {
            showError("L'email est requis");
            return;
        }
        
        if (!isValidEmail(email)) {
            showError("Format d'email invalide");
            return;
        }
        
        if (password.isEmpty()) {
            showError("Le mot de passe est requis");
            return;
        }
        
        if (password.length() < 6) {
            showError("Le mot de passe doit contenir au moins 6 caractères");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }
        
        if (role == null || role.isEmpty()) {
            showError("Veuillez sélectionner un rôle");
            return;
        }
        
        // Disable button during registration
        registerButton.setDisable(true);
        registerButton.setText("Création en cours...");
        
        // Execute registration in background thread
        new Thread(() -> {
            try {
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", username);
                userData.put("email", email);
                userData.put("password", password);
                userData.put("role", role);
                
                Map<String, Object> response = authService.register(userData);
                
                Platform.runLater(() -> {
                    if (response != null && response.containsKey("user")) {
                        showSuccess("Compte créé avec succès ! Redirection...");
                        
                        // Wait 2 seconds then redirect to login
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                Platform.runLater(this::handleBackToLogin);
                            } catch (InterruptedException e) {
                                logger.error("Erreur lors de l'attente", e);
                            }
                        }).start();
                    } else {
                        showError("Erreur lors de la création du compte");
                        resetButton();
                    }
                });
                
            } catch (Exception e) {
                logger.error("Erreur lors de l'inscription", e);
                Platform.runLater(() -> {
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("409")) {
                        showError("Ce nom d'utilisateur ou email existe déjà");
                    } else if (errorMessage != null && errorMessage.contains("400")) {
                        showError("Données invalides. Vérifiez vos informations");
                    } else {
                        showError("Erreur de connexion au serveur");
                    }
                    resetButton();
                });
            }
        }).start();
    }
    
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("AIG APRON SMART - Connexion");
            
            logger.info("Retour à la page de connexion");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la page de connexion", e);
            showError("Erreur lors du chargement de la page de connexion");
        }
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }
    
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
    
    private void clearMessages() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }
    
    private void resetButton() {
        registerButton.setDisable(false);
        registerButton.setText("Créer un compte");
    }
}
