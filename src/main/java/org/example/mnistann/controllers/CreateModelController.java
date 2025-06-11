package org.example.mnistann.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
    @FXML private TextField epochsField;
    @FXML private TextField learningRateField;
    @FXML private TextField trainSizeField;
    @FXML private TextField testSizeField;
    @FXML private TextField batchSizeField;
    @FXML private TextArea consoleArea;

    private int inputSize;
    private int numberOfHiddenLayers;
    private int[] hiddenLayersSizes;
    private int outputSize;
    private boolean initializeWithZero;
    private int epochs;
    private double learningRate;
    private int trainSize;
    private int testSize;
    private int batchSize;

    @FXML
    protected void onSaveClick() {
        try {
            inputSize = Integer.parseInt(inputSizeField.getText());
            numberOfHiddenLayers = Integer.parseInt(hiddenLayersField.getText());

            // Split hidden layer sizes
            String[] sizesStr = hiddenSizesField.getText().split(",");
            hiddenLayersSizes = new int[sizesStr.length];
            for (int i = 0; i < sizesStr.length; i++) {
                hiddenLayersSizes[i] = Integer.parseInt(sizesStr[i].trim());
            }

            outputSize = Integer.parseInt(outputSizeField.getText());
            initializeWithZero = initZeroCheck.isSelected();
            epochs = Integer.parseInt(epochsField.getText());
            learningRate = Double.parseDouble(learningRateField.getText());
            trainSize = Integer.parseInt(trainSizeField.getText());
            testSize = Integer.parseInt(testSizeField.getText());
            batchSize = Integer.parseInt(batchSizeField.getText());

            // Display configuration in console
            consoleArea.appendText("Configuration saved successfully!\n");
            consoleArea.appendText("Input size: " + inputSize + "\n");
            consoleArea.appendText("Hidden layers: " + Arrays.toString(hiddenLayersSizes) + "\n");
            consoleArea.appendText("Output size: " + outputSize + "\n");
            consoleArea.appendText("Learning rate: " + learningRate + "\n");
            consoleArea.appendText("Epochs: " + epochs + "\n");
            consoleArea.appendText("Batch size: " + batchSize + "\n");
            consoleArea.appendText("------------------------\n");

        } catch (NumberFormatException ex) {
            consoleArea.appendText("Error: Invalid number format\n");
        }
    }

    @FXML
    protected void onStartTrainingClick() throws IOException {
        consoleArea.appendText("Training started...\n");

        new Thread(() -> {
            try {
                DigitsNN model = new DigitsNN(inputSize, numberOfHiddenLayers, hiddenLayersSizes, outputSize, initializeWithZero);
                model.train(epochs, learningRate, trainSize, testSize, batchSize, consoleArea);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String filename = "model_" + timestamp + ".json";

                saveModelToJson(model, filename);

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

            // Save training parameters
            modelData.put("epochs", epochs);
            modelData.put("learningRate", learningRate);
            modelData.put("trainSize", trainSize);
            modelData.put("testSize", testSize);
            modelData.put("batchSize", batchSize);

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