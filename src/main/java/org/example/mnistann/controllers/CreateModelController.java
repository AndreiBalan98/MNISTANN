package org.example.mnistann.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.mnistann.model.Model;
import org.example.mnistann.neuralnetwork.DigitsNN;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.util.Arrays;

public class CreateModelController {
    @FXML private TextField inputSizeField;
    @FXML private TextField hiddenLayersField;
    @FXML private TextField hiddenSizesField;
    @FXML private TextField outputSizeField;
    @FXML private CheckBox initZeroCheck;
    @FXML private TextArea consoleArea;

    @FXML
    protected void onSaveClick() {
        try {
            int inputSize = Integer.parseInt(inputSizeField.getText());
            int numberOfHiddenLayers = Integer.parseInt(hiddenLayersField.getText());

            // Split hidden layer sizes
            String[] sizesStr = hiddenSizesField.getText().split(",");
            int[] hiddenLayersSizes = new int[sizesStr.length];
            for (int i = 0; i < sizesStr.length; i++) {
                hiddenLayersSizes[i] = Integer.parseInt(sizesStr[i].trim());
            }

            int outputSize = Integer.parseInt(outputSizeField.getText());
            boolean initializeWithZero = initZeroCheck.isSelected();

            Model model = new Model(inputSize, numberOfHiddenLayers, hiddenLayersSizes, outputSize, initializeWithZero);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "model_" + timestamp + ".json";

            saveModelToJson(model.nn, filename);

            // Display configuration in console
            consoleArea.appendText("Model saved successfully!\n");
            consoleArea.appendText("Input size: " + inputSize + "\n");
            consoleArea.appendText("Hidden layers: " + Arrays.toString(hiddenLayersSizes) + "\n");
            consoleArea.appendText("Output size: " + outputSize + "\n");
            consoleArea.appendText("------------------------\n");

        } catch (NumberFormatException ex) {
            consoleArea.appendText("Error: Invalid number format\n");
        }
    }

    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/fxml/application-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            Stage currentStage = (Stage) inputSizeField.getScene().getWindow();
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
}