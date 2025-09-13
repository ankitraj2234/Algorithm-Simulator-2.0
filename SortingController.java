package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.*;

public class SortingController implements Initializable {

    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private Slider arraySizeSlider;
    @FXML private Label arraySizeLabel;
    @FXML private BarChart<String, Number> arrayChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private HBox arrayValuesContainer;
    @FXML private ProgressBar sortingProgress;
    @FXML private Label progressLabel;
    @FXML private Label comparisonsLabel, swapsLabel, timeLabel;
    @FXML private TextArea algorithmInfo;
    @FXML private TextArea algorithmCodeViewer;
    @FXML private Button playButton, pauseButton, resetButton, stepButton;
    @FXML private Button generateRandomButton, generateCustomButton;
    @FXML private Slider speedSlider;

    private SortingService sortingService;
    private int[] originalArray;
    private int[] currentArray;
    private XYChart.Series<String, Number> dataSeries;
    private Timeline sortingAnimation;
    private int currentStep = 0;
    private List<SortingStep> sortingSteps;
    private boolean isAnimating = false;
    private AnimationService animationService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SortingController initialized");
        sortingService = new SortingService();
        animationService = AnimationService.getInstance();
        dataSeries = new XYChart.Series<>();
        arrayChart.getData().add(dataSeries);

        setupComponents();
        setupEventHandlers();

        // Initialize with default values
        algorithmComboBox.setValue("Bubble Sort");
        updateAlgorithmInfo();
        updateAlgorithmCode();
        generateRandomArray();

        System.out.println("Sorting module ready");
    }

    private void setupComponents() {
        // Configure algorithm combo box - UPDATED with 9 algorithms
        algorithmComboBox.setItems(FXCollections.observableArrayList(
                "Bubble Sort", "Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort",
                "Heap Sort", "Shell Sort", "Radix Sort", "Counting Sort"
        ));

        // Configure chart appearance
        arrayChart.setLegendVisible(false);
        arrayChart.setAnimated(false);
        arrayChart.getStyleClass().add("sorting-chart");
        xAxis.setLabel("Array Index");
        yAxis.setLabel("Value");

        // Configure sliders
        arraySizeSlider.setMin(5);
        arraySizeSlider.setMax(50);
        arraySizeSlider.setValue(20);
        speedSlider.setMin(0.1);
        speedSlider.setMax(3.0);
        speedSlider.setValue(1.0);

        // Configure code viewer
        if (algorithmCodeViewer != null) {
            algorithmCodeViewer.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px;");
        }

        updateControlStates();
    }

    private void setupEventHandlers() {
        // Array size slider listener
        arraySizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int size = newVal.intValue();
            arraySizeLabel.setText("Array Size: " + size);
            if (!isAnimating) {
                generateRandomArray();
            }
        });

        // Speed slider listener
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            animationService.setAnimationSpeed(newVal.doubleValue());
        });

        // Algorithm selection listener
        algorithmComboBox.setOnAction(e -> {
            updateAlgorithmInfo();
            updateAlgorithmCode();
            resetArray();
        });
    }

    @FXML
    private void generateRandomArray() {
        if (isAnimating) return;
        int size = (int) arraySizeSlider.getValue();
        originalArray = new int[size];
        Random random = new Random();

        // Generate unique random values for better visualization
        Set<Integer> usedValues = new HashSet<>();
        for (int i = 0; i < size; i++) {
            int value;
            do {
                value = random.nextInt(90) + 10; // Values between 10-99
            } while (usedValues.contains(value) && usedValues.size() < 90);
            usedValues.add(value);
            originalArray[i] = value;
        }

        currentArray = Arrays.copyOf(originalArray, originalArray.length);
        updateVisualization();
        resetStatistics();
        System.out.println("Generated random array: " + Arrays.toString(originalArray));
    }

    @FXML
    private void generateCustomArray() {
        if (isAnimating) return;
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Custom Array Input");
        dialog.setHeaderText("Enter Array Elements");
        dialog.setContentText("Enter numbers separated by spaces (e.g., 45 23 67 12 89):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                String[] parts = input.trim().split("\\s+");
                int[] customArray = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    int value = Integer.parseInt(parts[i]);
                    if (value < 1 || value > 999) {
                        throw new NumberFormatException("Values must be between 1 and 999");
                    }
                    customArray[i] = value;
                }

                originalArray = customArray;
                currentArray = Arrays.copyOf(originalArray, originalArray.length);
                arraySizeSlider.setValue(customArray.length);
                updateVisualization();
                resetStatistics();
                System.out.println("Generated custom array: " + Arrays.toString(originalArray));
            } catch (NumberFormatException e) {
                showAlert("Invalid Input",
                        "Please enter valid integers between 1 and 999, separated by spaces.\n\n" +
                                "Example: 45 23 67 12 89");
            }
        });
    }

    @FXML
    private void startSorting() {
        if (isAnimating) return;
        String algorithm = algorithmComboBox.getValue();
        SortingAlgorithm sortingAlg = sortingService.getAlgorithm(algorithm);
        if (sortingAlg == null) {
            showAlert("Error", "Algorithm not found: " + algorithm);
            return;
        }

        // Reset to original state
        currentArray = Arrays.copyOf(originalArray, originalArray.length);
        updateVisualization();
        // Generate sorting steps
        sortingSteps = sortingAlg.generateSteps(Arrays.copyOf(currentArray, currentArray.length));
        currentStep = 0;
        // Start animation
        createSortingAnimation();
        if (sortingAnimation != null) {
            sortingAnimation.play();
        }

        isAnimating = true;
        updateControlStates();
        sortingService.startTiming();
        System.out.println("Started sorting with " + algorithm + ", " + sortingSteps.size() + " steps");
    }

    @FXML
    private void pauseSorting() {
        if (sortingAnimation != null && isAnimating) {
            sortingAnimation.pause();
            isAnimating = false;
            updateControlStates();
            System.out.println("Sorting paused");
        }
    }

    @FXML
    private void resetArray() {
        if (sortingAnimation != null) {
            sortingAnimation.stop();
        }

        currentArray = Arrays.copyOf(originalArray, originalArray.length);
        currentStep = 0;
        isAnimating = false;
        updateVisualization();
        resetStatistics();
        updateControlStates();
        System.out.println("Array reset to original state");
    }

    @FXML
    private void stepForward() {
        if (!isAnimating) {
            // Initialize if not started
            if (sortingSteps == null) {
                String algorithm = algorithmComboBox.getValue();
                SortingAlgorithm sortingAlg = sortingService.getAlgorithm(algorithm);
                if (sortingAlg != null) {
                    sortingSteps = sortingAlg.generateSteps(Arrays.copyOf(currentArray, currentArray.length));
                    currentStep = 0;
                    sortingService.startTiming();
                }
            }
            performSingleStep();
        }
    }

    private void performSingleStep() {
        if (sortingSteps == null || currentStep >= sortingSteps.size()) {
            return;
        }

        SortingStep step = sortingSteps.get(currentStep);
        executeStep(step);
        currentStep++;
        updateProgress();
        if (currentStep >= sortingSteps.size()) {
            onSortingComplete();
        }
    }

    private void createSortingAnimation() {
        if (sortingSteps == null || sortingSteps.isEmpty()) {
            return;
        }

        sortingAnimation = new Timeline();
        double delay = 1000.0 / animationService.getAnimationSpeed();
        for (int i = 0; i < sortingSteps.size(); i++) {
            final int stepIndex = i;
            SortingStep step = sortingSteps.get(i);
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis((i + 1) * delay),
                    e -> {
                        executeStep(step);
                        currentStep = stepIndex + 1;
                        updateProgress();
                        if (currentStep >= sortingSteps.size()) {
                            Platform.runLater(this::onSortingComplete);
                        }
                    }
            );
            sortingAnimation.getKeyFrames().add(keyFrame);
        }

        sortingAnimation.setOnFinished(e -> onSortingComplete());
    }

    private void executeStep(SortingStep step) {
        if (step == null) return;
        switch (step.getType()) {
            case COMPARE:
                highlightComparison(step.getIndex1(), step.getIndex2());
                sortingService.incrementComparisons();
                break;
            case SWAP:
                performSwap(step.getIndex1(), step.getIndex2());
                highlightSwap(step.getIndex1(), step.getIndex2());
                sortingService.incrementSwaps();
                break;
            case SET:
                if (step.getIndex1() < currentArray.length) {
                    currentArray[step.getIndex1()] = step.getValue();
                    updateVisualization();
                }
                break;
            case HIGHLIGHT:
                highlightElement(step.getIndex1());
                break;
        }
        updateStatistics();
    }

    private void performSwap(int index1, int index2) {
        if (index1 < currentArray.length && index2 < currentArray.length) {
            int temp = currentArray[index1];
            currentArray[index1] = currentArray[index2];
            currentArray[index2] = temp;
            updateVisualization();
        }
    }

    private void highlightComparison(int index1, int index2) {
        Platform.runLater(() -> {
            if (dataSeries != null && dataSeries.getData() != null) {
                for (int i = 0; i < dataSeries.getData().size(); i++) {
                    XYChart.Data<String, Number> data = dataSeries.getData().get(i);
                    if (data.getNode() != null) {
                        if (i == index1 || i == index2) {
                            data.getNode().setStyle("-fx-bar-fill: #f59e0b; -fx-effect: dropshadow(gaussian, rgba(245,158,11,0.6), 8, 0, 0, 0);");
                        } else {
                            data.getNode().setStyle("-fx-bar-fill: #3b82f6;");
                        }
                    }
                }
            }
        });
    }

    private void highlightSwap(int index1, int index2) {
        Platform.runLater(() -> {
            if (dataSeries != null && dataSeries.getData() != null) {
                for (int i = 0; i < dataSeries.getData().size(); i++) {
                    XYChart.Data<String, Number> data = dataSeries.getData().get(i);
                    if (data.getNode() != null) {
                        if (i == index1 || i == index2) {
                            data.getNode().setStyle("-fx-bar-fill: #ef4444; -fx-effect: dropshadow(gaussian, rgba(239,68,68,0.6), 8, 0, 0, 0);");
                        } else {
                            data.getNode().setStyle("-fx-bar-fill: #3b82f6;");
                        }
                    }
                }
            }
        });
    }

    private void highlightElement(int index) {
        Platform.runLater(() -> {
            if (dataSeries != null && dataSeries.getData() != null && index < dataSeries.getData().size()) {
                XYChart.Data<String, Number> data = dataSeries.getData().get(index);
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-bar-fill: #10b981; -fx-effect: dropshadow(gaussian, rgba(16,185,129,0.6), 8, 0, 0, 0);");
                }
            }
        });
    }

    private void updateVisualization() {
        Platform.runLater(() -> {
            if (dataSeries != null && currentArray != null) {
                dataSeries.getData().clear();
                if (arrayValuesContainer != null) {
                    arrayValuesContainer.getChildren().clear();
                }

                for (int i = 0; i < currentArray.length; i++) {
                    // Add to chart
                    dataSeries.getData().add(new XYChart.Data<>(String.valueOf(i), currentArray[i]));
                    // Add to values display
                    if (arrayValuesContainer != null) {
                        Label valueLabel = new Label(String.valueOf(currentArray[i]));
                        valueLabel.getStyleClass().add("array-value");
                        valueLabel.setMinWidth(40);
                        valueLabel.setAlignment(javafx.geometry.Pos.CENTER);
                        arrayValuesContainer.getChildren().add(valueLabel);
                    }
                }

                // Style bars with gradient effect
                for (int i = 0; i < dataSeries.getData().size(); i++) {
                    XYChart.Data<String, Number> data = dataSeries.getData().get(i);
                    if (data.getNode() != null) {
                        data.getNode().setStyle("-fx-bar-fill: #3b82f6;");
                    }
                }
            }
        });
    }

    private void onSortingComplete() {
        isAnimating = false;
        updateControlStates();
        // Final highlighting - all bars green with animation
        Platform.runLater(() -> {
            if (dataSeries != null && dataSeries.getData() != null) {
                Timeline completion = new Timeline();
                for (int i = 0; i < dataSeries.getData().size(); i++) {
                    final int index = i;
                    XYChart.Data<String, Number> data = dataSeries.getData().get(i);
                    if (data.getNode() != null) {
                        KeyFrame keyFrame = new KeyFrame(
                                Duration.millis(i * 50),
                                e -> data.getNode().setStyle("-fx-bar-fill: #10b981; -fx-effect: dropshadow(gaussian, rgba(16,185,129,0.6), 8, 0, 0, 0);")
                        );
                        completion.getKeyFrames().add(keyFrame);
                    }
                }
                completion.play();
            }
        });

        long elapsedTime = sortingService.stopTiming();
        if (timeLabel != null) {
            timeLabel.setText("Time: " + elapsedTime + "ms");
        }

        if (progressLabel != null) {
            progressLabel.setText("Sorting completed! Array is now sorted.");
        }

        if (sortingProgress != null) {
            sortingProgress.setProgress(1.0);
        }

        System.out.println("Sorting completed in " + elapsedTime + "ms");
    }

    private void updateProgress() {
        if (sortingSteps != null && sortingProgress != null && progressLabel != null) {
            double progress = (double) currentStep / sortingSteps.size();
            sortingProgress.setProgress(progress);
            progressLabel.setText("Progress: " + String.format("%.1f", progress * 100) + "% (" + currentStep + "/" + sortingSteps.size() + " steps)");
        }
    }

    private void updateStatistics() {
        Platform.runLater(() -> {
            if (comparisonsLabel != null) {
                comparisonsLabel.setText("Comparisons: " + sortingService.getComparisons());
            }

            if (swapsLabel != null) {
                swapsLabel.setText("Swaps: " + sortingService.getSwaps());
            }
        });
    }

    private void resetStatistics() {
        sortingService.resetCounters();
        if (comparisonsLabel != null) {
            comparisonsLabel.setText("Comparisons: 0");
        }

        if (swapsLabel != null) {
            swapsLabel.setText("Swaps: 0");
        }

        if (timeLabel != null) {
            timeLabel.setText("Time: 0ms");
        }

        if (progressLabel != null) {
            progressLabel.setText("Ready to sort - Select algorithm and click Start");
        }

        if (sortingProgress != null) {
            sortingProgress.setProgress(0);
        }
    }

    private void updateAlgorithmInfo() {
        String algorithm = algorithmComboBox != null ? algorithmComboBox.getValue() : "Bubble Sort";
        if (algorithm != null && algorithmInfo != null) {
            String info = getAlgorithmInfo(algorithm);
            algorithmInfo.setText(info);
        }
    }

    private void updateAlgorithmCode() {
        String algorithm = algorithmComboBox != null ? algorithmComboBox.getValue() : "Bubble Sort";
        if (algorithm != null && algorithmCodeViewer != null) {
            String code = getAlgorithmCode(algorithm);
            algorithmCodeViewer.setText(code);
        }
    }

    // UPDATED: Get pseudocode for all 9 algorithms
    private String getAlgorithmCode(String algorithm) {
        return switch (algorithm) {
            case "Bubble Sort" ->
                    "// BUBBLE SORT PSEUDOCODE\n\n" +
                            "function bubbleSort(arr):\n" +
                            "    n = length(arr)\n" +
                            "    \n" +
                            "    for i = 0 to n-1:\n" +
                            "        swapped = false\n" +
                            "        \n" +
                            "        for j = 0 to n-i-2:\n" +
                            "            if arr[j] > arr[j+1]:\n" +
                            "                swap(arr[j], arr[j+1])\n" +
                            "                swapped = true\n" +
                            "        \n" +
                            "        if swapped == false:\n" +
                            "            break  // Array is sorted\n" +
                            "    \n" +
                            "    return arr\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void bubbleSort(int[] arr) {\n" +
                            "    int n = arr.length;\n" +
                            "    for (int i = 0; i < n-1; i++) {\n" +
                            "        boolean swapped = false;\n" +
                            "        for (int j = 0; j < n-i-1; j++) {\n" +
                            "            if (arr[j] > arr[j+1]) {\n" +
                            "                int temp = arr[j];\n" +
                            "                arr[j] = arr[j+1];\n" +
                            "                arr[j+1] = temp;\n" +
                            "                swapped = true;\n" +
                            "            }\n" +
                            "        }\n" +
                            "        if (!swapped) break;\n" +
                            "    }\n" +
                            "}";

            case "Selection Sort" ->
                    "// SELECTION SORT PSEUDOCODE\n\n" +
                            "function selectionSort(arr):\n" +
                            "    n = length(arr)\n" +
                            "    \n" +
                            "    for i = 0 to n-1:\n" +
                            "        minIndex = i\n" +
                            "        \n" +
                            "        for j = i+1 to n-1:\n" +
                            "            if arr[j] < arr[minIndex]:\n" +
                            "                minIndex = j\n" +
                            "        \n" +
                            "        if minIndex != i:\n" +
                            "            swap(arr[i], arr[minIndex])\n" +
                            "    \n" +
                            "    return arr\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void selectionSort(int[] arr) {\n" +
                            "    int n = arr.length;\n" +
                            "    for (int i = 0; i < n-1; i++) {\n" +
                            "        int minIndex = i;\n" +
                            "        for (int j = i+1; j < n; j++) {\n" +
                            "            if (arr[j] < arr[minIndex]) {\n" +
                            "                minIndex = j;\n" +
                            "            }\n" +
                            "        }\n" +
                            "        if (minIndex != i) {\n" +
                            "            int temp = arr[i];\n" +
                            "            arr[i] = arr[minIndex];\n" +
                            "            arr[minIndex] = temp;\n" +
                            "        }\n" +
                            "    }\n" +
                            "}";

            case "Insertion Sort" ->
                    "// INSERTION SORT PSEUDOCODE\n\n" +
                            "function insertionSort(arr):\n" +
                            "    n = length(arr)\n" +
                            "    \n" +
                            "    for i = 1 to n-1:\n" +
                            "        key = arr[i]\n" +
                            "        j = i - 1\n" +
                            "        \n" +
                            "        while j >= 0 and arr[j] > key:\n" +
                            "            arr[j+1] = arr[j]\n" +
                            "            j = j - 1\n" +
                            "        \n" +
                            "        arr[j+1] = key\n" +
                            "    \n" +
                            "    return arr\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void insertionSort(int[] arr) {\n" +
                            "    int n = arr.length;\n" +
                            "    for (int i = 1; i < n; i++) {\n" +
                            "        int key = arr[i];\n" +
                            "        int j = i - 1;\n" +
                            "        \n" +
                            "        while (j >= 0 && arr[j] > key) {\n" +
                            "            arr[j + 1] = arr[j];\n" +
                            "            j = j - 1;\n" +
                            "        }\n" +
                            "        arr[j + 1] = key;\n" +
                            "    }\n" +
                            "}";

            case "Merge Sort" ->
                    "// MERGE SORT PSEUDOCODE\n\n" +
                            "function mergeSort(arr, left, right):\n" +
                            "    if left < right:\n" +
                            "        mid = left + (right - left) / 2\n" +
                            "        \n" +
                            "        mergeSort(arr, left, mid)\n" +
                            "        mergeSort(arr, mid + 1, right)\n" +
                            "        merge(arr, left, mid, right)\n" +
                            "\n" +
                            "function merge(arr, left, mid, right):\n" +
                            "    n1 = mid - left + 1\n" +
                            "    n2 = right - mid\n" +
                            "    \n" +
                            "    create temp arrays L[n1] and R[n2]\n" +
                            "    copy data to temp arrays\n" +
                            "    \n" +
                            "    i = 0, j = 0, k = left\n" +
                            "    while i < n1 and j < n2:\n" +
                            "        if L[i] <= R[j]:\n" +
                            "            arr[k] = L[i]\n" +
                            "            i++\n" +
                            "        else:\n" +
                            "            arr[k] = R[j]\n" +
                            "            j++\n" +
                            "        k++\n" +
                            "    \n" +
                            "    copy remaining elements\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void mergeSort(int[] arr, int left, int right) {\n" +
                            "    if (left < right) {\n" +
                            "        int mid = left + (right - left) / 2;\n" +
                            "        mergeSort(arr, left, mid);\n" +
                            "        mergeSort(arr, mid + 1, right);\n" +
                            "        merge(arr, left, mid, right);\n" +
                            "    }\n" +
                            "}";

            case "Quick Sort" ->
                    "// QUICK SORT PSEUDOCODE\n\n" +
                            "function quickSort(arr, low, high):\n" +
                            "    if low < high:\n" +
                            "        pi = partition(arr, low, high)\n" +
                            "        \n" +
                            "        quickSort(arr, low, pi - 1)\n" +
                            "        quickSort(arr, pi + 1, high)\n" +
                            "\n" +
                            "function partition(arr, low, high):\n" +
                            "    pivot = arr[high]  // Choose last element\n" +
                            "    i = low - 1        // Index of smaller element\n" +
                            "    \n" +
                            "    for j = low to high - 1:\n" +
                            "        if arr[j] < pivot:\n" +
                            "            i++\n" +
                            "            swap(arr[i], arr[j])\n" +
                            "    \n" +
                            "    swap(arr[i + 1], arr[high])\n" +
                            "    return i + 1\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void quickSort(int[] arr, int low, int high) {\n" +
                            "    if (low < high) {\n" +
                            "        int pi = partition(arr, low, high);\n" +
                            "        quickSort(arr, low, pi - 1);\n" +
                            "        quickSort(arr, pi + 1, high);\n" +
                            "    }\n" +
                            "}\n" +
                            "\n" +
                            "int partition(int[] arr, int low, int high) {\n" +
                            "    int pivot = arr[high];\n" +
                            "    int i = (low - 1);\n" +
                            "    for (int j = low; j < high; j++) {\n" +
                            "        if (arr[j] < pivot) {\n" +
                            "            i++;\n" +
                            "            swap(arr, i, j);\n" +
                            "        }\n" +
                            "    }\n" +
                            "    swap(arr, i + 1, high);\n" +
                            "    return i + 1;\n" +
                            "}";

            case "Heap Sort" ->
                    "// HEAP SORT PSEUDOCODE\n\n" +
                            "function heapSort(arr):\n" +
                            "    n = length(arr)\n" +
                            "    \n" +
                            "    // Build heap (rearrange array)\n" +
                            "    for i = n/2 - 1 down to 0:\n" +
                            "        heapify(arr, n, i)\n" +
                            "    \n" +
                            "    // Extract elements from heap one by one\n" +
                            "    for i = n-1 down to 1:\n" +
                            "        swap(arr[0], arr[i])  // Move root to end\n" +
                            "        heapify(arr, i, 0)    // Heapify reduced heap\n" +
                            "\n" +
                            "function heapify(arr, n, i):\n" +
                            "    largest = i\n" +
                            "    left = 2*i + 1\n" +
                            "    right = 2*i + 2\n" +
                            "    \n" +
                            "    if left < n and arr[left] > arr[largest]:\n" +
                            "        largest = left\n" +
                            "    \n" +
                            "    if right < n and arr[right] > arr[largest]:\n" +
                            "        largest = right\n" +
                            "    \n" +
                            "    if largest != i:\n" +
                            "        swap(arr[i], arr[largest])\n" +
                            "        heapify(arr, n, largest)\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void heapSort(int[] arr) {\n" +
                            "    int n = arr.length;\n" +
                            "    \n" +
                            "    // Build heap\n" +
                            "    for (int i = n / 2 - 1; i >= 0; i--)\n" +
                            "        heapify(arr, n, i);\n" +
                            "    \n" +
                            "    // Extract elements\n" +
                            "    for (int i = n - 1; i > 0; i--) {\n" +
                            "        int temp = arr[0];\n" +
                            "        arr[0] = arr[i];\n" +
                            "        arr[i] = temp;\n" +
                            "        \n" +
                            "        heapify(arr, i, 0);\n" +
                            "    }\n" +
                            "}";

            case "Shell Sort" ->
                    "// SHELL SORT PSEUDOCODE\n\n" +
                            "function shellSort(arr):\n" +
                            "    n = length(arr)\n" +
                            "    \n" +
                            "    // Start with big gap, reduce gap\n" +
                            "    for gap = n/2; gap > 0; gap = gap/2:\n" +
                            "        \n" +
                            "        // Gapped insertion sort\n" +
                            "        for i = gap; i < n; i++:\n" +
                            "            temp = arr[i]\n" +
                            "            j = i\n" +
                            "            \n" +
                            "            while j >= gap and arr[j-gap] > temp:\n" +
                            "                arr[j] = arr[j-gap]\n" +
                            "                j -= gap\n" +
                            "            \n" +
                            "            arr[j] = temp\n" +
                            "    \n" +
                            "    return arr\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void shellSort(int[] arr) {\n" +
                            "    int n = arr.length;\n" +
                            "    \n" +
                            "    // Start with big gap, reduce\n" +
                            "    for (int gap = n/2; gap > 0; gap /= 2) {\n" +
                            "        \n" +
                            "        // Gapped insertion sort\n" +
                            "        for (int i = gap; i < n; i++) {\n" +
                            "            int temp = arr[i];\n" +
                            "            int j;\n" +
                            "            \n" +
                            "            for (j = i; j >= gap && arr[j-gap] > temp; j -= gap) {\n" +
                            "                arr[j] = arr[j-gap];\n" +
                            "            }\n" +
                            "            \n" +
                            "            arr[j] = temp;\n" +
                            "        }\n" +
                            "    }\n" +
                            "}";

            case "Radix Sort" ->
                    "// RADIX SORT PSEUDOCODE\n\n" +
                            "function radixSort(arr):\n" +
                            "    max = findMax(arr)\n" +
                            "    \n" +
                            "    // Do counting sort for every digit\n" +
                            "    for exp = 1; max/exp > 0; exp *= 10:\n" +
                            "        countingSortByDigit(arr, exp)\n" +
                            "\n" +
                            "function countingSortByDigit(arr, exp):\n" +
                            "    n = length(arr)\n" +
                            "    output[n], count[10]\n" +
                            "    \n" +
                            "    // Count occurrences of each digit\n" +
                            "    for i = 0 to n-1:\n" +
                            "        digit = (arr[i] / exp) % 10\n" +
                            "        count[digit]++\n" +
                            "    \n" +
                            "    // Change count to actual positions\n" +
                            "    for i = 1 to 9:\n" +
                            "        count[i] += count[i-1]\n" +
                            "    \n" +
                            "    // Build output array\n" +
                            "    for i = n-1 down to 0:\n" +
                            "        digit = (arr[i] / exp) % 10\n" +
                            "        output[count[digit] - 1] = arr[i]\n" +
                            "        count[digit]--\n" +
                            "    \n" +
                            "    // Copy back to original array\n" +
                            "    for i = 0 to n-1:\n" +
                            "        arr[i] = output[i]\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void radixSort(int[] arr) {\n" +
                            "    int max = Arrays.stream(arr).max().orElse(0);\n" +
                            "    \n" +
                            "    // Do counting sort for every digit\n" +
                            "    for (int exp = 1; max / exp > 0; exp *= 10) {\n" +
                            "        countingSortByDigit(arr, exp);\n" +
                            "    }\n" +
                            "}\n" +
                            "\n" +
                            "void countingSortByDigit(int[] arr, int exp) {\n" +
                            "    int n = arr.length;\n" +
                            "    int[] output = new int[n];\n" +
                            "    int[] count = new int[10];\n" +
                            "    \n" +
                            "    // Store count of occurrences\n" +
                            "    for (int i = 0; i < n; i++) {\n" +
                            "        count[(arr[i] / exp) % 10]++;\n" +
                            "    }\n" +
                            "    \n" +
                            "    // Change count to positions\n" +
                            "    for (int i = 1; i < 10; i++) {\n" +
                            "        count[i] += count[i - 1];\n" +
                            "    }\n" +
                            "    \n" +
                            "    // Build output array\n" +
                            "    for (int i = n - 1; i >= 0; i--) {\n" +
                            "        output[count[(arr[i] / exp) % 10] - 1] = arr[i];\n" +
                            "        count[(arr[i] / exp) % 10]--;\n" +
                            "    }\n" +
                            "    \n" +
                            "    // Copy back\n" +
                            "    System.arraycopy(output, 0, arr, 0, n);\n" +
                            "}";

            case "Counting Sort" ->
                    "// COUNTING SORT PSEUDOCODE\n\n" +
                            "function countingSort(arr):\n" +
                            "    n = length(arr)\n" +
                            "    max = findMax(arr)\n" +
                            "    min = findMin(arr)\n" +
                            "    range = max - min + 1\n" +
                            "    \n" +
                            "    count[range], output[n]\n" +
                            "    \n" +
                            "    // Count occurrences of each value\n" +
                            "    for i = 0 to n-1:\n" +
                            "        count[arr[i] - min]++\n" +
                            "    \n" +
                            "    // Calculate cumulative count\n" +
                            "    for i = 1 to range-1:\n" +
                            "        count[i] += count[i-1]\n" +
                            "    \n" +
                            "    // Build output array\n" +
                            "    for i = n-1 down to 0:\n" +
                            "        value = arr[i]\n" +
                            "        pos = count[value - min] - 1\n" +
                            "        output[pos] = value\n" +
                            "        count[value - min]--\n" +
                            "    \n" +
                            "    // Copy back to original\n" +
                            "    for i = 0 to n-1:\n" +
                            "        arr[i] = output[i]\n\n" +
                            "// JAVA IMPLEMENTATION\n" +
                            "public void countingSort(int[] arr) {\n" +
                            "    if (arr.length == 0) return;\n" +
                            "    \n" +
                            "    int max = Arrays.stream(arr).max().orElse(0);\n" +
                            "    int min = Arrays.stream(arr).min().orElse(0);\n" +
                            "    int range = max - min + 1;\n" +
                            "    \n" +
                            "    int[] count = new int[range];\n" +
                            "    int[] output = new int[arr.length];\n" +
                            "    \n" +
                            "    // Count occurrences\n" +
                            "    for (int i = 0; i < arr.length; i++) {\n" +
                            "        count[arr[i] - min]++;\n" +
                            "    }\n" +
                            "    \n" +
                            "    // Calculate cumulative count\n" +
                            "    for (int i = 1; i < range; i++) {\n" +
                            "        count[i] += count[i - 1];\n" +
                            "    }\n" +
                            "    \n" +
                            "    // Build output array\n" +
                            "    for (int i = arr.length - 1; i >= 0; i--) {\n" +
                            "        int value = arr[i];\n" +
                            "        int pos = count[value - min] - 1;\n" +
                            "        output[pos] = value;\n" +
                            "        count[value - min]--;\n" +
                            "    }\n" +
                            "    \n" +
                            "    // Copy back\n" +
                            "    System.arraycopy(output, 0, arr, 0, arr.length);\n" +
                            "}";

            default -> "// Select an algorithm to view its pseudocode and implementation\n\n" +
                    "Available algorithms:\n" +
                    "• Bubble Sort\n" +
                    "• Selection Sort\n" +
                    "• Insertion Sort\n" +
                    "• Merge Sort\n" +
                    "• Quick Sort\n" +
                    "• Heap Sort\n" +
                    "• Shell Sort\n" +
                    "• Radix Sort\n" +
                    "• Counting Sort\n\n" +
                    "Each algorithm shows both pseudocode and Java implementation.";
        };
    }

    // UPDATED: Algorithm information for all 9 algorithms
    private String getAlgorithmInfo(String algorithm) {
        return switch (algorithm) {
            case "Bubble Sort" ->
                    "🔄 BUBBLE SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n) - when array is already sorted\n" +
                            "• Average: O(n²) - random order\n" +
                            "• Worst: O(n²) - reverse sorted\n\n" +
                            "Space Complexity: O(1) - in-place sorting\n\n" +
                            "Algorithm:\n" +
                            "Bubble Sort repeatedly compares adjacent elements and swaps them if they are in the wrong order. The largest element 'bubbles up' to its correct position in each pass.\n\n" +
                            "Advantages:\n" +
                            "• Simple implementation\n" +
                            "• Stable sorting algorithm\n" +
                            "• In-place sorting\n\n" +
                            "Disadvantages:\n" +
                            "• Poor performance on large datasets\n" +
                            "• Not efficient for real-world applications";

            case "Selection Sort" ->
                    "🎯 SELECTION SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n²) - always performs n² comparisons\n" +
                            "• Average: O(n²)\n" +
                            "• Worst: O(n²)\n\n" +
                            "Space Complexity: O(1) - in-place sorting\n\n" +
                            "Algorithm:\n" +
                            "Selection Sort divides the array into sorted and unsorted regions. It repeatedly finds the minimum element from the unsorted region and places it at the beginning.\n\n" +
                            "Advantages:\n" +
                            "• Simple implementation\n" +
                            "• Minimum number of swaps (O(n))\n" +
                            "• In-place sorting\n\n" +
                            "Disadvantages:\n" +
                            "• Poor performance O(n²)\n" +
                            "• Not stable\n" +
                            "• Not adaptive";

            case "Insertion Sort" ->
                    "📝 INSERTION SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n) - when array is already sorted\n" +
                            "• Average: O(n²)\n" +
                            "• Worst: O(n²) - reverse sorted\n\n" +
                            "Space Complexity: O(1) - in-place sorting\n\n" +
                            "Algorithm:\n" +
                            "Insertion Sort builds the sorted array one element at a time by repeatedly taking an element from the unsorted portion and inserting it into the correct position.\n\n" +
                            "Advantages:\n" +
                            "• Efficient for small datasets\n" +
                            "• Adaptive (performs well on nearly sorted data)\n" +
                            "• Stable and in-place\n" +
                            "• Online (can sort as it receives data)\n\n" +
                            "Disadvantages:\n" +
                            "• Poor performance on large datasets";

            case "Merge Sort" ->
                    "🔀 MERGE SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n log n)\n" +
                            "• Average: O(n log n)\n" +
                            "• Worst: O(n log n) - consistent performance\n\n" +
                            "Space Complexity: O(n) - requires additional space\n\n" +
                            "Algorithm:\n" +
                            "Merge Sort uses divide-and-conquer approach. It divides the array into halves, recursively sorts them, and then merges the sorted halves.\n\n" +
                            "Advantages:\n" +
                            "• Guaranteed O(n log n) performance\n" +
                            "• Stable sorting algorithm\n" +
                            "• Predictable performance\n" +
                            "• Good for large datasets\n\n" +
                            "Disadvantages:\n" +
                            "• Requires O(n) extra space\n" +
                            "• Not in-place sorting";

            case "Quick Sort" ->
                    "⚡ QUICK SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n log n) - good pivot selection\n" +
                            "• Average: O(n log n)\n" +
                            "• Worst: O(n²) - poor pivot selection\n\n" +
                            "Space Complexity: O(log n) - recursion stack\n\n" +
                            "Algorithm:\n" +
                            "Quick Sort picks a pivot element, partitions the array around it, and recursively sorts the sub-arrays. Elements smaller than pivot go left, larger go right.\n\n" +
                            "Advantages:\n" +
                            "• Very fast average performance\n" +
                            "• In-place sorting\n" +
                            "• Cache-efficient\n" +
                            "• Widely used in practice\n\n" +
                            "Disadvantages:\n" +
                            "• Unstable sorting\n" +
                            "• Worst-case O(n²) performance\n" +
                            "• Performance depends on pivot selection";

            case "Heap Sort" ->
                    "🏗️ HEAP SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n log n) - always consistent\n" +
                            "• Average: O(n log n)\n" +
                            "• Worst: O(n log n) - guaranteed performance\n\n" +
                            "Space Complexity: O(1) - in-place sorting\n\n" +
                            "Algorithm:\n" +
                            "Heap Sort builds a binary heap data structure and repeatedly extracts the maximum element. It first builds a max heap, then repeatedly moves the root to the end.\n\n" +
                            "Advantages:\n" +
                            "• Guaranteed O(n log n) performance\n" +
                            "• In-place sorting\n" +
                            "• No worst-case degradation\n" +
                            "• Memory efficient\n\n" +
                            "Disadvantages:\n" +
                            "• Not stable\n" +
                            "• Poor cache locality\n" +
                            "• Generally slower than Quick Sort in practice";

            case "Shell Sort" ->
                    "🐚 SHELL SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n log n) - depends on gap sequence\n" +
                            "• Average: O(n^1.25) to O(n^1.5)\n" +
                            "• Worst: O(n²) - poor gap sequence\n\n" +
                            "Space Complexity: O(1) - in-place sorting\n\n" +
                            "Algorithm:\n" +
                            "Shell Sort is a generalization of insertion sort that uses gap sequences. It starts with large gaps and gradually reduces them, performing gapped insertion sorts.\n\n" +
                            "Advantages:\n" +
                            "• Significant improvement over insertion sort\n" +
                            "• In-place sorting\n" +
                            "• Adaptive to some extent\n" +
                            "• Good for medium-sized arrays\n\n" +
                            "Disadvantages:\n" +
                            "• Performance depends on gap sequence\n" +
                            "• Not stable\n" +
                            "• Analysis is complex";

            case "Radix Sort" ->
                    "🔢 RADIX SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(d × n) - linear time!\n" +
                            "• Average: O(d × n)\n" +
                            "• Worst: O(d × n) where d is number of digits\n\n" +
                            "Space Complexity: O(k + n) - k is range of digits\n\n" +
                            "Algorithm:\n" +
                            "Radix Sort processes individual digits of numbers. It sorts by least significant digit first, then moves to more significant digits using counting sort as subroutine.\n\n" +
                            "Advantages:\n" +
                            "• Linear time complexity O(n)\n" +
                            "• Non-comparison based\n" +
                            "• Stable sorting\n" +
                            "• Excellent for integers\n\n" +
                            "Disadvantages:\n" +
                            "• Limited to integers/fixed-length strings\n" +
                            "• Requires extra space\n" +
                            "• Not in-place";

            case "Counting Sort" ->
                    "📊 COUNTING SORT\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(n + k) - linear time!\n" +
                            "• Average: O(n + k)\n" +
                            "• Worst: O(n + k) where k is range of input\n\n" +
                            "Space Complexity: O(k) - counting array\n\n" +
                            "Algorithm:\n" +
                            "Counting Sort counts the number of occurrences of each distinct element, then uses this information to place elements directly in their sorted positions.\n\n" +
                            "Advantages:\n" +
                            "• Linear time complexity O(n)\n" +
                            "• Stable sorting\n" +
                            "• Non-comparison based\n" +
                            "• Perfect for small integer ranges\n\n" +
                            "Disadvantages:\n" +
                            "• Limited to integers with known range\n" +
                            "• Requires extra space O(k)\n" +
                            "• Not practical for large ranges";

            default -> "Select an algorithm to view detailed information about its complexity, implementation, and use cases.";
        };
    }

    private void updateControlStates() {
        if (playButton != null) playButton.setDisable(isAnimating);
        if (pauseButton != null) pauseButton.setDisable(!isAnimating);
        if (resetButton != null) resetButton.setDisable(false);
        if (stepButton != null) stepButton.setDisable(isAnimating);
        if (generateRandomButton != null) generateRandomButton.setDisable(isAnimating);
        if (generateCustomButton != null) generateCustomButton.setDisable(isAnimating);
        if (algorithmComboBox != null) algorithmComboBox.setDisable(isAnimating);
        if (arraySizeSlider != null) arraySizeSlider.setDisable(isAnimating);
    }

    @FXML
    private void goBack() {
        try {
            if (sortingAnimation != null) {
                sortingAnimation.stop();
            }

            Stage currentStage = (Stage) arrayChart.getScene().getWindow();
            // ✅ Use hide() instead of close() for back navigation
            currentStage.hide();
            System.out.println("Sorting module hidden (can be reopened)");

        } catch (Exception e) {
            System.err.println("Error hiding Sorting window: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for sorting steps
    public static class SortingStep {
        public enum StepType { COMPARE, SWAP, SET, HIGHLIGHT }
        private final StepType type;
        private final int index1;
        private final int index2;
        private final int value;

        public SortingStep(StepType type, int index1, int index2) {
            this.type = type;
            this.index1 = index1;
            this.index2 = index2;
            this.value = -1;
        }

        public SortingStep(StepType type, int index1) {
            this.type = type;
            this.index1 = index1;
            this.index2 = -1;
            this.value = -1;
        }

        public SortingStep(StepType type, int index1, int index2, int value) {
            this.type = type;
            this.index1 = index1;
            this.index2 = index2;
            this.value = value;
        }

        public StepType getType() { return type; }
        public int getIndex1() { return index1; }
        public int getIndex2() { return index2; }
        public int getValue() { return value; }
    }
}
