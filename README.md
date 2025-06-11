# MNIST Neural Network Project

## Descriere generală

Acest proiect implementează o rețea neurală pentru recunoașterea cifrelor scrise de mână din dataset-ul MNIST, folosind JavaFX pentru interfața grafică și o implementare proprie de rețea neurală. Aplicația permite crearea, antrenarea și testarea modelelor de rețele neurale cu arhitecturi configurabile.

## Tehnologii utilizate

- **Java 17** - Limbajul de programare principal
- **JavaFX 17** - Framework pentru interfața grafică
- **DeepLearning4J** - Pentru încărcarea și procesarea dataset-ului MNIST
- **Jackson** - Pentru serializarea/deserializarea modelelor în format JSON
- **Maven** - Build tool și dependency management

## Structura proiectului

```
src/
├── main/
│   ├── java/org/example/mnistann/
│   │   ├── Main.java                           # Clasa principală de lansare
│   │   ├── controllers/                        # Controllere JavaFX
│   │   │   ├── ApplicationController.java      # Controller pentru meniul principal
│   │   │   ├── CreateModelController.java      # Controller pentru crearea modelelor
│   │   │   ├── TestModelController.java        # Controller pentru testarea modelelor
│   │   │   └── HelloController.java            # Controller de test
│   │   ├── neuralnetwork/                      # Implementarea rețelei neurale
│   │   │   ├── ForwardNeuralNetwork.java       # Clasa abstractă de bază
│   │   │   └── DigitsNN.java                   # Implementarea specifică pentru cifre
│   │   └── utils/                              # Utilitare matematice
│   │       └── Maths.java                      # Funcții matematice
│   └── resources/
│       ├── org/example/fxml/                   # Fișiere FXML pentru UI
│       │   ├── application-view.fxml           # Meniul principal
│       │   ├── create-model-view.fxml          # Interfața de creare modele
│       │   ├── test-model-view.fxml            # Interfața de testare
│       │   └── hello-view.fxml                 # View de test
│       └── models/                             # Director pentru salvarea modelelor
```

## Arhitectura aplicației

### 1. Clasa Main
**Locație**: `src/main/java/org/example/mnistann/Main.java`

Clasa principală care lansează aplicația JavaFX.

```java
public class Main extends Application
```

**Responsabilități**:
- Inițializarea aplicației JavaFX
- Încărcarea view-ului principal

### 2. Controllere JavaFX

#### ApplicationController
**Locație**: `src/main/java/org/example/mnistann/controllers/ApplicationController.java`

Controller pentru meniul principal al aplicației.

**Câmpuri principale**:
- `VBox mainContainer` - Container principal UI
- `Button createModelButton` - Buton pentru crearea modelelor
- `Button testModelButton` - Buton pentru testarea modelelor

**Metode**:
- `onCreateModelClick()` - Navighează la interfața de creare modele
- `onTestModelClick()` - Navighează la interfața de testare
- `loadScene(String fxmlFile)` - Încarcă o nouă scenă FXML

#### CreateModelController
**Locație**: `src/main/java/org/example/mnistann/controllers/CreateModelController.java`

Controller pentru configurarea și antrenarea modelelor.

**Câmpuri de configurare**:
```java
@FXML private TextField inputSizeField;          // Dimensiunea input-ului
@FXML private TextField hiddenLayersField;       // Numărul de straturi ascunse
@FXML private TextField hiddenSizesField;        // Dimensiunile straturilor ascunse
@FXML private TextField outputSizeField;         // Dimensiunea output-ului
@FXML private CheckBox initZeroCheck;            // Inițializarea cu zero
@FXML private TextField epochsField;             // Numărul de epoci
@FXML private TextField learningRateField;       // Rata de învățare
@FXML private TextField trainSizeField;          // Dimensiunea setului de antrenare
@FXML private TextField testSizeField;           // Dimensiunea setului de test
@FXML private TextField batchSizeField;          // Dimensiunea batch-ului
@FXML private TextArea consoleArea;              // Consolă pentru output
```

**Metode principale**:
- `onSaveClick()` - Salvează configurația introdusă
- `onStartTrainingClick()` - Începe antrenarea modelului în thread separat
- `onBackClick()` - Navighează înapoi la meniul principal
- `saveModelToJson(DigitsNN model, String filename)` - Salvează modelul în format JSON

**Tipare de denominare**:
- Câmpurile UI se termină cu `Field` sau `Area`
- Butoanele se termină cu `Button`
- Metodele pentru evenimente încep cu `on` urmat de numele acțiunii și `Click`

### 3. Rețeaua neurală

#### ForwardNeuralNetwork (Clasă abstractă)
**Locație**: `src/main/java/org/example/mnistann/neuralnetwork/ForwardNeuralNetwork.java`

Clasa abstractă de bază pentru rețele neurale feed-forward.

**Câmpuri principale**:
```java
private final int inputSize;                                    // Dimensiunea input-ului
private final int numberOfHiddenLayers;                         // Numărul de straturi ascunse
private final int[] hiddenLayersSize;                          // Dimensiunile straturilor ascunse
private final int outputSize;                                   // Dimensiunea output-ului
private double[][][] weights;                                   // Matricile de greutăți [strat][neuron_intrare][neuron_ieșire]
private double[][] biases;                                      // Vectorii de bias-uri [strat][neuron]
private Function<Double, Double>[] activationFunctions;        // Funcțiile de activare
private Function<Double, Double>[] activationDerivatives;      // Derivatele funcțiilor de activare
```

**Constructor**:
```java
public ForwardNeuralNetwork(int inputSize, int numberOfHiddenLayers, 
                           int[] hiddenLayersSize, int outputSize, 
                           Boolean initializeWith0)
```

**Metode principale**:
- `initializeNetwork()` - Inițializează structura rețelei
- `initializeWeightsXavier()` - Inițializează greutățile folosind metoda Xavier
- `feedForward(double[] input)` - Propagarea înainte
- `backpropagation(double[] input, double[] expectedOutput, double learningRate)` - Algoritm de backpropagation
- `train(...)` - Metodă abstractă pentru antrenare

**Funcții de activare**:
- Straturile ascunse folosesc **ReLU** (Rectified Linear Unit)
- Stratul de ieșire folosește **Sigmoid**

#### DigitsNN
**Locație**: `src/main/java/org/example/mnistann/neuralnetwork/DigitsNN.java`

Implementarea specifică pentru recunoașterea cifrelor MNIST.

**Constructor**:
```java
public DigitsNN(int inputSize, int numberOfHiddenLayers, 
                int[] hiddenLayersSize, int outputSize, 
                Boolean initializeWith0)
```

**Metode de antrenare**:
```java
// Antrenare cu output în consolă
public void train(int epochs, double learningRate, int trainSize, 
                 int testSize, int batchSize) throws IOException

// Antrenare cu output în UI TextArea
public void train(int epochs, double learningRate, int trainSize, 
                 int testSize, int batchSize, TextArea consoleArea) throws IOException
```

**Metode private**:
- `computeAccuracy(DataSetIterator dataSetIterator, int dataSize)` - Calculează acuratețea

### 4. Utilitare matematice

#### Maths
**Locație**: `src/main/java/org/example/mnistann/utils/Maths.java`

Clasă utilitară cu funcții matematice necesare.

**Metode**:
```java
public static double[] matrixMultiplication(double[] vector, double[][] matrix)  // Înmulțirea matrice-vector
public static double relu(double x)                                              // Funcția ReLU
public static double reluDerivative(double x)                                    // Derivata ReLU
public static double sigmoid(double x)                                           // Funcția Sigmoid
public static double sigmoidDerivative(double x)                                 // Derivata Sigmoid
```

## Tipare de programare și convenții

### Denominarea variabilelor și metodelor
- **camelCase** pentru variabile și metode: `inputSize`, `numberOfHiddenLayers`
- **PascalCase** pentru clase: `ForwardNeuralNetwork`, `DigitsNN`
- **SCREAMING_SNAKE_CASE** pentru constante
- Metodele pentru evenimente UI încep cu `on`: `onSaveClick()`, `onBackClick()`
- Câmpurile UI au sufixe descriptive: `Field`, `Button`, `Area`

### Tipuri de date
- `int` - pentru dimensiuni, numere de straturi, indici
- `double` - pentru valori numerice (greutăți, bias-uri, rate de învățare)
- `double[]` - pentru vectori (input, output, activări)
- `double[][]` - pentru matrici de bias-uri
- `double[][][]` - pentru tensori de greutăți (3D: [strat][neuron_intrare][neuron_ieșire])
- `Function<Double, Double>` - pentru funcții de activare și derivatele lor

### Gestionarea erorilor
- `IOException` pentru operațiile de I/O
- `NumberFormatException` pentru parsing-ul input-urilor numerice
- Mesajele de eroare sunt afișate în `TextArea consoleArea`

### Threading
- Antrenarea se face într-un thread separat pentru a nu bloca UI-ul
- `Platform.runLater()` pentru actualizarea UI-ului din thread-uri background

## Fluxul de lucru

### 1. Crearea unui model
1. Utilizatorul completează configurația în `create-model-view.fxml`
2. `CreateModelController.onSaveClick()` validează și salvează configurația
3. `CreateModelController.onStartTrainingClick()` creează un `DigitsNN` și începe antrenarea
4. Modelul antrenat este salvat în format JSON în directorul `resources/models/`

### 2. Structura fișierului JSON
```json
{
  "inputSize": 784,
  "numberOfHiddenLayers": 2,
  "hiddenLayersSize": [128, 64],
  "outputSize": 10,
  "weights": [...],
  "biases": [...],
  "epochs": 10,
  "learningRate": 0.01,
  "trainSize": 1000,
  "testSize": 200,
  "batchSize": 32
}
```

## Configurație recomandată pentru MNIST

- **Input size**: 784 (28x28 pixeli)
- **Hidden layers**: 2-3 straturi
- **Hidden sizes**: [128, 64] sau [256, 128, 64]
- **Output size**: 10 (cifre 0-9)
- **Learning rate**: 0.01 - 0.001
- **Epochs**: 10-50
- **Batch size**: 32-128

## Cum să contribui

### Adăugarea unei noi funcționalități
1. Creează clase noi în package-urile corespunzătoare
2. Respectă convențiile de denominare existente
3. Adaugă documentație în stilul JavaDoc
4. Testează funcționalitatea în interfața grafică

### Îmbunătățirea rețelei neurale
1. Extinde `ForwardNeuralNetwork` pentru noi arhitecturi
2. Adaugă noi funcții de activare în `Maths.java`
3. Implementează noi algoritmi de optimizare

### Îmbunătățirea UI-ului
1. Modifică fișierele FXML pentru aspectul vizual
2. Adaugă noi controllere pentru funcționalități complexe
3. Respectă pattern-ul MVC existent

## Dependințe Maven

Proiectul folosește următoarele dependințe principale:
- `org.openjfx:javafx-controls:17.0.6`
- `org.deeplearning4j:deeplearning4j-core:1.0.0-beta7`
- `org.nd4j:nd4j-native:1.0.0-beta7`
- `com.fasterxml.jackson.core:jackson-databind:2.15.2`

Pentru lista completă, consultă `pom.xml`.

## Rularea proiectului

```bash
# Compilare și rulare
mvn clean javafx:run

# Doar compilare
mvn clean compile
```

## Observații tehnice

- Rețeaua folosește **Xavier initialization** pentru greutăți
- Algoritmul de optimizare este **Gradient Descent** simplu
- Dataset-ul MNIST este încărcat automat prin DeepLearning4J
- Modelele sunt salvate în format JSON pentru portabilitate
- UI-ul este responsive și oferă feedback în timp real