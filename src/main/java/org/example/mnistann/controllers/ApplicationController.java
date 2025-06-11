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
    private Button trainModelButton;

    @FXML
    protected void onCreateModelClick() {
        loadScene("/org/example/fxml/create-model-view.fxml", "Create");
    }

    @FXML
    protected void onTestModelClick() {
        loadScene("/org/example/fxml/test-model-view.fxml", "Test");
    }

    @FXML
    protected void onTrainModelClick() {
        loadScene("/org/example/fxml/train-model-view.fxml", "Train");
    }

    private void loadScene(String fxmlFile, String nextScene) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene newScene = null;

            if (nextScene.equals("Create")) {
                newScene = new Scene(fxmlLoader.load(), 600, 700);
            }
            else if (nextScene.equals("Test")) {
                newScene = new Scene(fxmlLoader.load(), 600, 700);
            }
            else if (nextScene.equals("Train")) {
                newScene = new Scene(fxmlLoader.load(), 600, 900);
            }
            else {
                newScene = new Scene(fxmlLoader.load(), 600, 700);
            }

            Stage currentStage = (Stage) mainContainer.getScene().getWindow();
            currentStage.setScene(newScene);
        } catch (IOException e) {
            System.out.println("Error loading " + fxmlFile + ": " + e.getMessage());
        }
    }
}