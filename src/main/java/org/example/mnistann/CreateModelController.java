package org.example.mnistann;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

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

    private int inputSize;
    private int numberOfHiddenLayers;
    private String hiddenLayersSizes;
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
            hiddenLayersSizes = hiddenSizesField.getText();
            outputSize = Integer.parseInt(outputSizeField.getText());
            initializeWithZero = initZeroCheck.isSelected();
            epochs = Integer.parseInt(epochsField.getText());
            learningRate = Double.parseDouble(learningRateField.getText());
            trainSize = Integer.parseInt(trainSizeField.getText());
            testSize = Integer.parseInt(testSizeField.getText());
            batchSize = Integer.parseInt(batchSizeField.getText());

            System.out.println("Configuration saved successfully!");
        } catch (NumberFormatException ex) {
            System.out.println("Error: Invalid number format");
        }
    }

    @FXML
    protected void onBackClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("application-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            Stage currentStage = (Stage) inputSizeField.getScene().getWindow();
            currentStage.setScene(scene);
        } catch (IOException e) {
            System.out.println("Error loading main view: " + e.getMessage());
        }
    }
}