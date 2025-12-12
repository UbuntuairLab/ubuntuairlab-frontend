package com.aige.apronsmart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main Application Class for UbuntuAirLab
 * Système intelligent de gestion des postes de stationnement aéroportuaires
 */
public class AigApronSmartApplication extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(AigApronSmartApplication.class);
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        logger.info("Starting UbuntuAirLab Application");
        
        // Load login view
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = fxmlLoader.load();
        
        Scene scene = new Scene(root, 480, 884);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        
        stage.setTitle("UbuntuAirLab - Système de Gestion Intelligent");
        stage.setScene(scene);
        stage.setMinWidth(480);
        stage.setMinHeight(700);
        
        // Set application icon
        try {
            var iconStream = getClass().getResourceAsStream("/images/logo.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            logger.warn("Could not load application icon", e);
        }
        
        stage.show();
        
        logger.info("UbuntuAirLab started successfully");
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
