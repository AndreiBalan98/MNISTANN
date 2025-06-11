package org.example.mnistann.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ApplicationController {
    @FXML
    private VBox mainContainer;

    @FXML
    private Button createModelButton;

    @FXML
    private Button testModelButton;

    @FXML
    protected void onCreateModelClick() {
        loadScene("/org/example/fxml/create-model-view.fxml");
    }

    @FXML
    protected void onTestModelClick() {
        loadScene("/org/example/fxml/test-model-view.fxml");
    }

    private void loadScene(String fxmlFile) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene newScene = new Scene(fxmlLoader.load(), 600, 900);

            // Obține stage-ul curent din orice nod din scena actuală
            Stage currentStage = (Stage) mainContainer.getScene().getWindow();
            currentStage.setScene(newScene);
        } catch (IOException e) {
            System.out.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }
}