package org.example.mnistann.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mnistann.neuralnetwork.DigitsNN;
import java.io.File;
import java.util.Map;

import java.io.IOException;

public class TestModelController {
    @FXML
    private Label statusLabel;

    @FXML private ComboBox<String> modelsComboBox;
    @FXML private Button loadModelButton;

    @FXML
    protected void initialize() {
        statusLabel.setText("Select a model to load");
        populateModelsComboBox();
    }

    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/fxml/application-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            Stage currentStage = (Stage) statusLabel.getScene().getWindow();
            currentStage.setScene(scene);
        } catch (IOException e) {
            System.out.println("Error loading main view: " + e.getMessage());
        }
    }

    @FXML
    protected void onLoadModelClick() {
        String selectedModel = modelsComboBox.getSelectionModel().getSelectedItem();
        if (selectedModel == null) {
            statusLabel.setText("Please select a model first");
            return;
        }

        DigitsNN loadedModel;
        try {
            File modelFile = new File("src/main/resources/models", selectedModel);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> modelData = mapper.readValue(modelFile, Map.class);

            // Extrage configurația
            int inputSize = (Integer) modelData.get("inputSize");
            int numberOfHiddenLayers = (Integer) modelData.get("numberOfHiddenLayers");
            int[] hiddenLayersSize = mapper.convertValue(modelData.get("hiddenLayersSize"), int[].class);
            int outputSize = (Integer) modelData.get("outputSize");

            // Creează noul model
            loadedModel = new DigitsNN(inputSize, numberOfHiddenLayers, hiddenLayersSize, outputSize, false);

            // Încarcă weights și biases
            double[][][] weights = mapper.convertValue(modelData.get("weights"), double[][][].class);
            double[][] biases = mapper.convertValue(modelData.get("biases"), double[][].class);

            loadedModel.setWeights(weights);
            loadedModel.setBiases(biases);

            statusLabel.setText("Model loaded successfully: " + selectedModel);

        } catch (Exception e) {
            statusLabel.setText("Error loading model: " + e.getMessage());
            loadedModel = null;
        }
    }

    private void populateModelsComboBox() {
        try {
            File modelsDir = new File("src/main/resources/models");
            if (modelsDir.exists() && modelsDir.isDirectory()) {
                File[] jsonFiles = modelsDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (jsonFiles != null) {
                    for (File file : jsonFiles) {
                        modelsComboBox.getItems().add(file.getName());
                    }
                }
            }
            if (modelsComboBox.getItems().isEmpty()) {
                statusLabel.setText("No models found in resources/models");
            }
        } catch (Exception e) {
            statusLabel.setText("Error loading models list: " + e.getMessage());
        }
    }
}