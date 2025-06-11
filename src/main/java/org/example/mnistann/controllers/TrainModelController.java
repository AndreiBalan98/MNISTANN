package org.example.mnistann.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.mnistann.model.Model;
import org.example.mnistann.neuralnetwork.DigitsNN;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

public class TrainModelController {
    @FXML private VBox modelSelectionSection;
    @FXML private ComboBox<String> modelsComboBox;
    @FXML private Label statusLabel;
    @FXML private Button loadModelButton;

    @FXML private TextField epochsField;
    @FXML private TextField learningRateField;
    @FXML private TextField trainSizeField;
    @FXML private TextField testSizeField;
    @FXML private TextField batchSizeField;
    @FXML private TextArea consoleArea;

    private String selectedModel;
    private Model loadedModel;
    private int epochs;
    private double learningRate;
    private int trainSize;
    private int testSize;
    private int batchSize;

    @FXML
    protected void initialize() {
        statusLabel.setText("Select a model to load");
        populateModelsComboBox();
    }

    @FXML
    protected void onLoadModelClick() {
        selectedModel = modelsComboBox.getSelectionModel().getSelectedItem();
        if (selectedModel == null) {
            statusLabel.setText("Please select a model first");
            return;
        }

        try {
            File modelFile = new File("src/main/resources/models", selectedModel);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> modelData = mapper.readValue(modelFile, Map.class);

            // Extract configuration
            int inputSize = (Integer) modelData.get("inputSize");
            int numberOfHiddenLayers = (Integer) modelData.get("numberOfHiddenLayers");
            int[] hiddenLayersSize = mapper.convertValue(modelData.get("hiddenLayersSize"), int[].class);
            int outputSize = (Integer) modelData.get("outputSize");

            // Create new model
            loadedModel = new Model(inputSize, numberOfHiddenLayers, hiddenLayersSize, outputSize, false);

            // Load weights and biases
            double[][][] weights = mapper.convertValue(modelData.get("weights"), double[][][].class);
            double[][] biases = mapper.convertValue(modelData.get("biases"), double[][].class);

            loadedModel.nn.setWeights(weights);
            loadedModel.nn.setBiases(biases);

        } catch (Exception e) {
            statusLabel.setText("Error loading model: " + e.getMessage());
            loadedModel = null;
        }

        statusLabel.setText("Model " + selectedModel + " loaded");
    }

    @FXML
    protected void onStartTrainingClick() throws IOException {
        consoleArea.appendText("Training started...\n");

        epochs = Integer.parseInt(epochsField.getText());
        learningRate = Double.parseDouble(learningRateField.getText());
        trainSize = Integer.parseInt(trainSizeField.getText());
        testSize = Integer.parseInt(testSizeField.getText());
        batchSize = Integer.parseInt(batchSizeField.getText());

        new Thread(() -> {
            try {
                loadedModel.nn.train(epochs, learningRate, trainSize, testSize, batchSize, consoleArea);

                saveModelToJson(loadedModel.nn, selectedModel);

                Platform.runLater(() -> consoleArea.appendText("Training completed and model saved!\n"));

            } catch (IOException e) {
                Platform.runLater(() -> consoleArea.appendText("Error: " + e.getMessage() + "\n"));
            }
        }).start();
    }

    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/fxml/application-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            Stage currentStage = (Stage) epochsField.getScene().getWindow();
            currentStage.setScene(scene);
        } catch (IOException e) {
            consoleArea.appendText("Error loading main view: " + e.getMessage() + "\n");
        }
    }

    private void saveModelToJson(DigitsNN model, String filename) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> modelData = new HashMap<>();

            // Save model configuration
            modelData.put("inputSize", model.getInputSize());
            modelData.put("numberOfHiddenLayers", model.getNumberOfHiddenLayers());
            modelData.put("hiddenLayersSize", model.getHiddenLayersSize());
            modelData.put("outputSize", model.getOutputSize());

            // Save weights and biases
            modelData.put("weights", model.getWeights());
            modelData.put("biases", model.getBiases());

            // Create models directory
            File modelsDir = new File("src/main/resources/models");

            // Save model to .json
            File outputFile = new File(modelsDir, filename);
            mapper.writeValue(outputFile, modelData);

            Platform.runLater(() -> consoleArea.appendText("Model saved to: " + outputFile.getAbsolutePath() + "\n"));

        } catch (IOException e) {
            Platform.runLater(() -> consoleArea.appendText("Error saving model: " + e.getMessage() + "\n"));
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