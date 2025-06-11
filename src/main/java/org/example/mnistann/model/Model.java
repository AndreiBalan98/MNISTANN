package org.example.mnistann.model;

import org.example.mnistann.neuralnetwork.DigitsNN;

public class Model {
    public DigitsNN nn;

    public Model(int inputSize, int numberOfHiddenLayers, int[] hiddenLayersSize, int outputSize, Boolean initializeWithZero)
    {
        nn = new DigitsNN(inputSize, numberOfHiddenLayers, hiddenLayersSize, outputSize, initializeWithZero);
    }
}
