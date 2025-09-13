package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;


public class SearchingController {

    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private TextField searchValueField;
    @FXML private TextField arrayInputField;
    @FXML private HBox arrayVisualization;
    @FXML private Label resultLabel;
    @FXML private Label comparisonsLabel;
    @FXML private Label timeLabel;
    @FXML private TextArea algorithmInfo;
    @FXML private Button generateArrayButton;
    @FXML private Button searchButton;
    @FXML private Button resetButton;
    @FXML private ProgressBar searchProgress;
    @FXML private Label statusLabel;

    private int[] currentArray;
    private AnimationService animationService;
    private Timeline searchAnimation;
    private boolean isSearching = false;
    private int comparisons = 0;
    private long startTime = 0;

    @FXML
    public void initialize() {
        System.out.println("SearchingController initialized");

        animationService = AnimationService.getInstance();
        setupComponents();
        setupEventHandlers();

        algorithmComboBox.setValue("Linear Search");
        updateAlgorithmInfo();
        generateRandomArray();

        System.out.println("Searching module ready");
    }

    private void setupComponents() {
        algorithmComboBox.setItems(FXCollections.observableArrayList(
                "Linear Search", "Binary Search", "Interpolation Search"
        ));

        searchValueField.setPromptText("Enter value to search");
        arrayInputField.setPromptText("Enter array elements separated by spaces");

        updateControlStates();
    }

    private void setupEventHandlers() {
        algorithmComboBox.setOnAction(e -> {
            updateAlgorithmInfo();
            resetSearch();
        });

        searchValueField.setOnAction(e -> performSearch());
        arrayInputField.setOnAction(e -> generateCustomArray());
    }

    @FXML
    private void generateRandomArray() {
        if (isSearching) return;

        Random random = new Random();
        int size = 15 + random.nextInt(16); // 15-30 elements
        currentArray = new int[size];

        for (int i = 0; i < size; i++) {
            currentArray[i] = random.nextInt(100) + 1;
        }

        // Sort array if binary search or interpolation search is selected
        String algorithm = algorithmComboBox.getValue();
        if ("Binary Search".equals(algorithm) || "Interpolation Search".equals(algorithm)) {
            Arrays.sort(currentArray);
        }

        updateVisualization();
        resetSearch();
        updateArrayInput();

        System.out.println("Generated array: " + Arrays.toString(currentArray));
    }

    @FXML
    private void generateCustomArray() {
        if (isSearching) return;

        String input = arrayInputField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter array elements separated by spaces.");
            return;
        }

        try {
            String[] parts = input.split("\\s+");
            currentArray = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {
                currentArray[i] = Integer.parseInt(parts[i]);
            }

            // Sort array if binary search or interpolation search is selected
            String algorithm = algorithmComboBox.getValue();
            if ("Binary Search".equals(algorithm) || "Interpolation Search".equals(algorithm)) {
                Arrays.sort(currentArray);
                updateArrayInput(); // Update display with sorted array
            }

            updateVisualization();
            resetSearch();

            System.out.println("Custom array created: " + Arrays.toString(currentArray));

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers separated by spaces.");
        }
    }

    @FXML
    private void performSearch() {
        if (isSearching || currentArray == null) return;

        String searchText = searchValueField.getText().trim();
        if (searchText.isEmpty()) {
            showAlert("No Search Value", "Please enter a value to search for.");
            return;
        }

        try {
            int searchValue = Integer.parseInt(searchText);
            String algorithm = algorithmComboBox.getValue();

            isSearching = true;
            updateControlStates();
            resetStatistics();
            startTime = System.currentTimeMillis();

            switch (algorithm) {
                case "Linear Search":
                    performLinearSearch(searchValue);
                    break;
                case "Binary Search":
                    performBinarySearch(searchValue);
                    break;
                case "Interpolation Search":
                    performInterpolationSearch(searchValue);
                    break;
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Search Value", "Please enter a valid integer to search for.");
        }
    }

    private void performLinearSearch(int searchValue) {
        List<SearchStep> steps = new ArrayList<>();

        for (int i = 0; i < currentArray.length; i++) {
            steps.add(new SearchStep(SearchStep.StepType.COMPARE, i, searchValue, currentArray[i]));

            if (currentArray[i] == searchValue) {
                steps.add(new SearchStep(SearchStep.StepType.FOUND, i, searchValue, currentArray[i]));
                break;
            }
        }

        if (steps.get(steps.size() - 1).getType() != SearchStep.StepType.FOUND) {
            steps.add(new SearchStep(SearchStep.StepType.NOT_FOUND, -1, searchValue, -1));
        }

        animateSearch(steps);
    }

    private void performBinarySearch(int searchValue) {
        List<SearchStep> steps = new ArrayList<>();
        int left = 0, right = currentArray.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            steps.add(new SearchStep(SearchStep.StepType.SET_BOUNDS, left, right, mid));
            steps.add(new SearchStep(SearchStep.StepType.COMPARE, mid, searchValue, currentArray[mid]));

            if (currentArray[mid] == searchValue) {
                steps.add(new SearchStep(SearchStep.StepType.FOUND, mid, searchValue, currentArray[mid]));
                break;
            } else if (currentArray[mid] < searchValue) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        if (steps.get(steps.size() - 1).getType() != SearchStep.StepType.FOUND) {
            steps.add(new SearchStep(SearchStep.StepType.NOT_FOUND, -1, searchValue, -1));
        }

        animateSearch(steps);
    }

    private void performInterpolationSearch(int searchValue) {
        List<SearchStep> steps = new ArrayList<>();
        int left = 0, right = currentArray.length - 1;

        while (left <= right && searchValue >= currentArray[left] && searchValue <= currentArray[right]) {
            if (left == right) {
                steps.add(new SearchStep(SearchStep.StepType.COMPARE, left, searchValue, currentArray[left]));
                if (currentArray[left] == searchValue) {
                    steps.add(new SearchStep(SearchStep.StepType.FOUND, left, searchValue, currentArray[left]));
                } else {
                    steps.add(new SearchStep(SearchStep.StepType.NOT_FOUND, -1, searchValue, -1));
                }
                break;
            }

            // Calculate interpolated position
            int pos = left + ((searchValue - currentArray[left]) * (right - left)) /
                    (currentArray[right] - currentArray[left]);

            steps.add(new SearchStep(SearchStep.StepType.INTERPOLATE, pos, left, right));
            steps.add(new SearchStep(SearchStep.StepType.COMPARE, pos, searchValue, currentArray[pos]));

            if (currentArray[pos] == searchValue) {
                steps.add(new SearchStep(SearchStep.StepType.FOUND, pos, searchValue, currentArray[pos]));
                break;
            }

            if (currentArray[pos] < searchValue) {
                left = pos + 1;
            } else {
                right = pos - 1;
            }
        }

        if (steps.isEmpty() || steps.get(steps.size() - 1).getType() != SearchStep.StepType.FOUND) {
            steps.add(new SearchStep(SearchStep.StepType.NOT_FOUND, -1, searchValue, -1));
        }

        animateSearch(steps);
    }

    private void animateSearch(List<SearchStep> steps) {
        searchAnimation = new Timeline();
        double delay = 800.0 / animationService.getAnimationSpeed();

        for (int i = 0; i < steps.size(); i++) {
            final int stepIndex = i;
            SearchStep step = steps.get(i);

            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis((i + 1) * delay),
                    e -> {
                        executeSearchStep(step);
                        updateProgress((double) (stepIndex + 1) / steps.size());

                        if (stepIndex == steps.size() - 1) {
                            Platform.runLater(this::onSearchComplete);
                        }
                    }
            );

            searchAnimation.getKeyFrames().add(keyFrame);
        }

        searchAnimation.play();
    }

    private void executeSearchStep(SearchStep step) {
        Platform.runLater(() -> {
            switch (step.getType()) {
                case COMPARE:
                    highlightElement(step.getIndex(), "#f59e0b"); // Orange for comparison
                    comparisons++;
                    updateStatistics();
                    break;

                case FOUND:
                    highlightElement(step.getIndex(), "#10b981"); // Green for found
                    resultLabel.setText("✅ Found " + step.getSearchValue() + " at index " + step.getIndex());
                    resultLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    break;

                case NOT_FOUND:
                    clearHighlights();
                    resultLabel.setText("❌ Value " + step.getSearchValue() + " not found in array");
                    resultLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    break;

                case SET_BOUNDS:
                    highlightRange(step.getIndex(), step.getArrayValue(), "#8b5cf6"); // Purple for bounds
                    break;

                case INTERPOLATE:
                    highlightElement(step.getIndex(), "#06b6d4"); // Cyan for interpolated position
                    break;
            }
        });
    }

    private void updateVisualization() {
        Platform.runLater(() -> {
            if (arrayVisualization != null && currentArray != null) {
                arrayVisualization.getChildren().clear();

                for (int i = 0; i < currentArray.length; i++) {
                    Label element = new Label(String.valueOf(currentArray[i]));
                    element.getStyleClass().add("array-element");
                    element.setFont(Font.font(null, FontWeight.BOLD, 18));
// ✅ Use array-element class
                    element.setMinWidth(45);
                    element.setMinHeight(45);
                    element.setAlignment(javafx.geometry.Pos.CENTER);

                    // Add index label
                    Label indexLabel = new Label(String.valueOf(i));
                    indexLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");

                    javafx.scene.layout.VBox container = new javafx.scene.layout.VBox();
                    container.setAlignment(javafx.geometry.Pos.CENTER);
                    container.getChildren().addAll(element, indexLabel);
                    container.setSpacing(2);

                    arrayVisualization.getChildren().add(container);
                }
            }
        });
    }

    private void highlightElement(int index, String color) {
        if (index >= 0 && index < arrayVisualization.getChildren().size()) {
            javafx.scene.layout.VBox container = (javafx.scene.layout.VBox) arrayVisualization.getChildren().get(index);
            Label element = (Label) container.getChildren().get(0);
            element.setStyle("-fx-background-color: " + color + "; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-background-radius: 8; -fx-font-weight: bold; -fx-text-fill: white;");

            // Add pulse animation
            ScaleTransition pulse = animationService.createScaleTransition(element, 1.2, 1.2, 200);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            pulse.play();
        }
    }

    private void highlightRange(int left, int right, String color) {
        clearHighlights();
        for (int i = left; i <= right && i < arrayVisualization.getChildren().size(); i++) {
            javafx.scene.layout.VBox container = (javafx.scene.layout.VBox) arrayVisualization.getChildren().get(i);
            Label element = (Label) container.getChildren().get(0);
            element.setStyle("-fx-background-color: " + color + "; -fx-border-color: " + color + "; -fx-border-width: 1; -fx-background-radius: 8; -fx-font-weight: bold; -fx-text-fill: white; -fx-opacity: 0.7;");
        }
    }

    private void clearHighlights() {
        for (int i = 0; i < arrayVisualization.getChildren().size(); i++) {
            javafx.scene.layout.VBox container = (javafx.scene.layout.VBox) arrayVisualization.getChildren().get(i);
            Label element = (Label) container.getChildren().get(0);
            element.setStyle("-fx-background-color: #e2e8f0; -fx-border-color: #94a3b8; -fx-border-width: 1; -fx-background-radius: 8; -fx-font-weight: bold;");
        }
    }

    private void updateArrayInput() {
        if (arrayInputField != null && currentArray != null) {
            arrayInputField.setText(Arrays.toString(currentArray).replaceAll("[\\[\\]]", "").replaceAll(",", ""));
        }
    }

    private void updateProgress(double progress) {
        if (searchProgress != null) {
            searchProgress.setProgress(progress);
        }
    }

    private void updateStatistics() {
        if (comparisonsLabel != null) {
            comparisonsLabel.setText("Comparisons: " + comparisons);
        }
    }

    private void resetStatistics() {
        comparisons = 0;
        updateStatistics();
        if (timeLabel != null) {
            timeLabel.setText("Time: 0ms");
        }
    }

    private void onSearchComplete() {
        isSearching = false;
        updateControlStates();

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (timeLabel != null) {
            timeLabel.setText("Time: " + elapsedTime + "ms");
        }

        if (statusLabel != null) {
            statusLabel.setText("Search completed! Algorithm: " + algorithmComboBox.getValue());
        }

        System.out.println("Search completed in " + elapsedTime + "ms with " + comparisons + " comparisons");
    }

    @FXML
    private void resetSearch() {
        if (searchAnimation != null) {
            searchAnimation.stop();
        }

        isSearching = false;
        clearHighlights();
        resetStatistics();
        updateControlStates();

        if (resultLabel != null) {
            resultLabel.setText("Enter a value and click Search to begin");
            resultLabel.setStyle("-fx-text-fill: #64748b;");
        }

        if (searchProgress != null) {
            searchProgress.setProgress(0);
        }

        if (statusLabel != null) {
            statusLabel.setText("Ready to search - " + algorithmComboBox.getValue());
        }
    }

    private void updateAlgorithmInfo() {
        String algorithm = algorithmComboBox != null ? algorithmComboBox.getValue() : "Linear Search";
        if (algorithm != null && algorithmInfo != null) {
            String info = getAlgorithmInfo(algorithm);
            algorithmInfo.setText(info);
        }

        // Update array sorting based on algorithm
        if (currentArray != null) {
            if ("Binary Search".equals(algorithm) || "Interpolation Search".equals(algorithm)) {
                Arrays.sort(currentArray);
                updateVisualization();
                updateArrayInput();
            }
        }
    }

    private String getAlgorithmInfo(String algorithm) {
        return switch (algorithm) {
            case "Linear Search" ->
                    "🔍 LINEAR SEARCH\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(1) - element found at first position\n" +
                            "• Average: O(n/2) - element found in middle\n" +
                            "• Worst: O(n) - element not found or at end\n\n" +
                            "Space Complexity: O(1) - constant space\n\n" +
                            "Algorithm:\n" +
                            "Linear Search checks each element sequentially from the beginning until the target is found or the end is reached.\n\n" +
                            "Advantages:\n" +
                            "• Works on unsorted arrays\n" +
                            "• Simple implementation\n" +
                            "• No preprocessing required\n\n" +
                            "Disadvantages:\n" +
                            "• Slow for large datasets\n" +
                            "• O(n) time complexity";

            case "Binary Search" ->
                    "⚡ BINARY SEARCH\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(1) - element found at middle\n" +
                            "• Average: O(log n)\n" +
                            "• Worst: O(log n) - element not found\n\n" +
                            "Space Complexity: O(1) - iterative version\n\n" +
                            "Algorithm:\n" +
                            "Binary Search works on sorted arrays by repeatedly dividing the search space in half, comparing the target with the middle element.\n\n" +
                            "Advantages:\n" +
                            "• Very fast O(log n) performance\n" +
                            "• Efficient for large datasets\n" +
                            "• Predictable performance\n\n" +
                            "Disadvantages:\n" +
                            "• Requires sorted array\n" +
                            "• Not suitable for frequent insertions/deletions";

            case "Interpolation Search" ->
                    "🎯 INTERPOLATION SEARCH\n\n" +
                            "Time Complexity:\n" +
                            "• Best: O(1) - direct hit\n" +
                            "• Average: O(log log n) - uniformly distributed\n" +
                            "• Worst: O(n) - poor distribution\n\n" +
                            "Space Complexity: O(1) - constant space\n\n" +
                            "Algorithm:\n" +
                            "Interpolation Search improves Binary Search by calculating the probable position based on the value being searched, similar to how humans search in a dictionary.\n\n" +
                            "Advantages:\n" +
                            "• Better than binary search for uniformly distributed data\n" +
                            "• O(log log n) average performance\n" +
                            "• More intuitive approach\n\n" +
                            "Disadvantages:\n" +
                            "• Requires sorted array\n" +
                            "• Poor performance on non-uniform data\n" +
                            "• More complex implementation";

            default -> "Select an algorithm to view detailed information.";
        };
    }

    private void updateControlStates() {
        if (searchButton != null) searchButton.setDisable(isSearching);
        if (generateArrayButton != null) generateArrayButton.setDisable(isSearching);
        if (resetButton != null) resetButton.setDisable(false);
        if (algorithmComboBox != null) algorithmComboBox.setDisable(isSearching);
        if (arrayInputField != null) arrayInputField.setDisable(isSearching);
        if (searchValueField != null) searchValueField.setDisable(isSearching);
    }

    @FXML
    private void goBack() {
        try {
            if (searchAnimation != null) {
                searchAnimation.stop();
            }

            Stage currentStage = (Stage) arrayVisualization.getScene().getWindow();
            // ✅ Use hide() instead of close() for back navigation
            currentStage.hide();
            System.out.println("Searching Module module hidden (can be reopened)");

        } catch (Exception e) {
            System.err.println("Error hiding Searching Module window: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for search steps
    public static class SearchStep {
        public enum StepType { COMPARE, FOUND, NOT_FOUND, SET_BOUNDS, INTERPOLATE }

        private final StepType type;
        private final int index;
        private final int searchValue;
        private final int arrayValue;

        public SearchStep(StepType type, int index, int searchValue, int arrayValue) {
            this.type = type;
            this.index = index;
            this.searchValue = searchValue;
            this.arrayValue = arrayValue;
        }

        public StepType getType() { return type; }
        public int getIndex() { return index; }
        public int getSearchValue() { return searchValue; }
        public int getArrayValue() { return arrayValue; }
    }
}
