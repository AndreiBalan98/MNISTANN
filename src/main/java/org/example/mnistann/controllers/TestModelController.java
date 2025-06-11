package org.example.mnistann.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class TestModelController {
    @FXML
    private Label statusLabel;

    @FXML
    protected void initialize() {
        statusLabel.setText("Test Model Window - Ready");
    }

    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/fxml/application-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 500, 600);
            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.setScene(scene);
        } catch (IOException e) {
            System.out.println("Error loading main view: " + e.getMessage());
        }
    }
}