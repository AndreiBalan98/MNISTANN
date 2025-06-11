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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import java.awt.image.BufferedImage;

import java.io.IOException;

public class TestModelController {
    @FXML
    private Label statusLabel;

    @FXML private ComboBox<String> modelsComboBox;
    @FXML private Button loadModelButton;
    private DigitsNN loadedModel;

    // UI elements for testing
    @FXML private VBox modelSelectionSection;
    @FXML private HBox testingSection;
    @FXML private Canvas drawingCanvas;
    @FXML private Button eraseButton;

    // Prediction labels
    @FXML private Label prediction0, prediction1, prediction2, prediction3, prediction4;
    @FXML private Label prediction5, prediction6, prediction7, prediction8, prediction9;
    private Label[] predictionLabels;

    // Drawing variables
    private GraphicsContext gc;
    private boolean isDrawing = false;

    @FXML
    protected void initialize() {
        statusLabel.setText("Select a model to load");
        populateModelsComboBox();

        // Initialize prediction labels array
        predictionLabels = new Label[]{
                prediction0, prediction1, prediction2, prediction3, prediction4,
                prediction5, prediction6, prediction7, prediction8, prediction9
        };
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

            // Switch to testing interface
            modelSelectionSection.setVisible(false);
            testingSection.setVisible(true);
            eraseButton.setVisible(true);

            // Initialize canvas
            setupCanvas();

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

    private void setupCanvas() {
        gc = drawingCanvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 280, 280);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(15);

        // Mouse event handlers
        drawingCanvas.setOnMousePressed(this::startDrawing);
        drawingCanvas.setOnMouseDragged(this::drawing);
        drawingCanvas.setOnMouseReleased(this::stopDrawing);
    }

    private void startDrawing(MouseEvent e) {
        isDrawing = true;
        gc.beginPath();
        gc.moveTo(e.getX(), e.getY());
        gc.stroke();
    }

    private void drawing(MouseEvent e) {
        if (isDrawing) {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();
        }
    }

    private void stopDrawing(MouseEvent e) {
        if (isDrawing) {
            isDrawing = false;
            // Predict when user stops drawing
            predictDigit();
        }
    }

    @FXML
    protected void onEraseClick() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 280, 280);
        // Reset predictions in original order
        for (int i = 0; i < 10; i++) {
            predictionLabels[i].setText(i + ": 0.00%");
            predictionLabels[i].setTextFill(Color.BLACK);
            predictionLabels[i].setStyle("-fx-font-size: 14px;");
        }
    }

    private void predictDigit() {
        try {
            // Capture canvas as image
            WritableImage writableImage = new WritableImage(280, 280);
            drawingCanvas.snapshot(null, writableImage);

            // Convert to 28x28 grayscale array
            double[] input = convertImageToInput(writableImage);

            // Get prediction from model
            double[] output = loadedModel.feedForward(input);

            // Update prediction labels
            updatePredictionLabels(output);

        } catch (Exception e) {
            System.out.println("Error predicting digit: " + e.getMessage());
        }
    }

    private double[] convertImageToInput(WritableImage image) {
        // Create 28x28 array
        double[] input = new double[784]; // 28*28
        PixelReader pixelReader = image.getPixelReader();

        // Scale down from 280x280 to 28x28
        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                // Sample 10x10 area for each pixel
                double graySum = 0;
                for (int dy = 0; dy < 10; dy++) {
                    for (int dx = 0; dx < 10; dx++) {
                        int pixelX = x * 10 + dx;
                        int pixelY = y * 10 + dy;
                        Color color = pixelReader.getColor(pixelX, pixelY);
                        // Convert to grayscale (0 = black, 1 = white)
                        double gray = (color.getRed() + color.getGreen() + color.getBlue()) / 3.0;
                        graySum += gray;
                    }
                }
                // Average and invert (0 = white, 1 = black) to match MNIST
                double avgGray = graySum / 100.0;
                input[y * 28 + x] = 1.0 - avgGray; // Invert for MNIST format
            }
        }

        return input;
    }

    private void updatePredictionLabels(double[] predictions) {
        // Create array of indices and sort by prediction value (descending)
        Integer[] indices = new Integer[10];
        for (int i = 0; i < 10; i++) {
            indices[i] = i;
        }

        // Sort indices by prediction values in descending order
        java.util.Arrays.sort(indices, (a, b) -> Double.compare(predictions[b], predictions[a]));

        // Update labels in sorted order
        for (int i = 0; i < 10; i++) {
            int digit = indices[i];
            double percentage = predictions[digit] * 100;
            predictionLabels[i].setText(String.format("%d: %.2f%%", digit, percentage));

            // Highlight the top prediction (first in sorted list)
            if (i == 0) {
                predictionLabels[i].setTextFill(Color.RED);
                predictionLabels[i].setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            } else {
                predictionLabels[i].setTextFill(Color.BLACK);
                predictionLabels[i].setStyle("-fx-font-size: 14px;");
            }
        }
    }
}