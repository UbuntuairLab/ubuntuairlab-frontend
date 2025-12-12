package com.aige.apronsmart.controllers;

import com.aige.apronsmart.AigApronSmartApplication;
import com.aige.apronsmart.models.User;
import com.aige.apronsmart.services.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for login screen
 */
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private ImageView logoImageView;
    @FXML private ImageView backgroundImageView;
    @FXML private StackPane rootPane;
    @FXML private Hyperlink registerLink;
    
    private final AuthService authService = AuthService.getInstance();
    private int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    @FXML
    public void initialize() {
        progressIndicator.setVisible(false);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        // Bind background image to fill the entire screen
        if (backgroundImageView != null && backgroundImageView.getParent() instanceof StackPane) {
            StackPane parent = (StackPane) backgroundImageView.getParent();
            backgroundImageView.fitWidthProperty().bind(parent.widthProperty());
            backgroundImageView.fitHeightProperty().bind(parent.heightProperty());
            backgroundImageView.setPreserveRatio(false); // Cover the entire area
        }
        
        // Load logo
        try {
            var logoStream = getClass().getResourceAsStream("/images/logo.png");
            if (logoStream != null) {
                Image logo = new Image(logoStream);
                logoImageView.setImage(logo);
            } else {
                logoImageView.setVisible(false);
            }
        } catch (Exception e) {
            logger.warn("Could not load logo image", e);
            logoImageView.setVisible(false);
        }
        
        // Enter key listener
        passwordField.setOnAction(event -> handleLogin());
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez saisir votre identifiant et mot de passe");
            return;
        }
        
        // Disable UI during login
        setUIEnabled(false);
        progressIndicator.setVisible(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        // DEMO MODE: Use "demo" / "demo" to test without backend
        if ("demo".equals(username) && "demo".equals(password)) {
            logger.info("DEMO MODE: Login successful");
            createDemoUser();
            loadDashboard();
            return;
        }
        
        // Perform login in background thread
        new Thread(() -> {
            try {
                // Login with UbuntuAirLab API
                authService.login(username, password);
                
                Platform.runLater(() -> {
                    logger.info("Login successful for user: {}", username);
                    loadDashboard();
                });
            } catch (IOException e) {
                logger.error("Login error", e);
                Platform.runLater(() -> {
                    loginAttempts++;
                    int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts;
                    
                    String errorMessage = e.getMessage();
                    if (errorMessage.contains("401")) {
                        if (remainingAttempts > 0) {
                            showError("Identifiants incorrects. " + remainingAttempts + " essai(s) restant(s)");
                        } else {
                            showError("Trop de tentatives échouées. Contactez l'administrateur.");
                            loginButton.setDisable(true);
                        }
                    } else {
                        showError("Erreur de connexion au serveur. Vérifiez votre connexion réseau.");
                    }
                    
                    setUIEnabled(true);
                    progressIndicator.setVisible(false);
                });
            }
        }).start();
    }
    
    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            
            AigApronSmartApplication.getPrimaryStage().setScene(scene);
        } catch (IOException e) {
            logger.error("Error loading dashboard", e);
            showError("Erreur lors du chargement du tableau de bord");
            setUIEnabled(true);
            progressIndicator.setVisible(false);
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    
    private void setUIEnabled(boolean enabled) {
        usernameField.setDisable(!enabled);
        passwordField.setDisable(!enabled);
        loginButton.setDisable(!enabled);
    }
    
    private void createDemoUser() {
        // Create a demo user for testing without backend
        User demoUser = new User();
        demoUser.setId(1L);
        demoUser.setUsername("demo");
        demoUser.setEmail("demo@aige.tg");
        demoUser.setFirstName("Demo");
        demoUser.setLastName("User");
        demoUser.setRole(User.UserRole.ADMIN);
        demoUser.setDepartment("Operations");
        demoUser.setIsActive(true);
        
        // Set demo token and user
        AuthService.setAuthToken("demo-token-12345");
        authService.setCurrentUser(demoUser);
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            
            Scene scene = usernameField.getScene();
            scene.setRoot(root);
            
            logger.info("Navigation vers la page d'inscription");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la page d'inscription", e);
            showError("Erreur lors du chargement de la page d'inscription");
        }
    }
}
