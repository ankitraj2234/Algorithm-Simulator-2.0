package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ArrayController  {

    @FXML private TextField valueField, indexField;
    @FXML private Slider arraySizeSlider;
    @FXML private Label arraySizeLabel;
    @FXML private Button insertButton, deleteButton, updateButton, searchButton;
    @FXML private Button generateButton, clearButton, demoButton;
    @FXML private HBox arrayVisualization;
    @FXML private Label statusLabel, sizeLabel, resultLabel;
    @FXML private TextArea operationHistory;
    @FXML private ProgressBar operationProgress;

    private List<Integer> arrayList;
    private List<Label> visualElements;
    private AnimationService animationService;
    private Timeline currentAnimation;
    private boolean isDemoRunning = false;
    private boolean isOperationRunning = false;

    @FXML
    public void initialize() {
        System.out.println("ArrayController initialized");

        arrayList = new ArrayList<>();
        visualElements = new ArrayList<>();
        animationService = AnimationService.getInstance();

        setupComponents();
        setupEventHandlers();
        generateRandomArray();

        System.out.println("Array module ready");
    }

    private void setupComponents() {
        // Configure input validation
        valueField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                valueField.setText(newText.replaceAll("[^\\d]", ""));
            }
            if (newText.length() > 3) {
                valueField.setText(newText.substring(0, 3));
            }
        });

        indexField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                indexField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        valueField.setPromptText("Enter value (1-999)");
        indexField.setPromptText("Index position");

        // Configure slider
        arraySizeSlider.setMin(5);
        arraySizeSlider.setMax(20);
        arraySizeSlider.setValue(10);

        if (operationHistory != null) {
            operationHistory.setText("=== Array Operations History ===\n");
            operationHistory.setEditable(false);
        }
    }

    private void setupEventHandlers() {
        valueField.setOnAction(e -> insertElement());
        indexField.setOnAction(e -> insertElement());

        arraySizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int size = newVal.intValue();
            arraySizeLabel.setText("Array Size: " + size);
            if (!isOperationRunning && !isDemoRunning) {
                generateRandomArray();
            }
        });
    }

    @FXML
    private void generateRandomArray() {
        if (isOperationRunning || isDemoRunning) return;

        int size = (int) arraySizeSlider.getValue();
        arrayList.clear();

        java.util.Random random = new java.util.Random();
        for (int i = 0; i < size; i++) {
            arrayList.add(random.nextInt(99) + 1); // Values 1-99
        }

        updateVisualization();
        updateDisplay();
        logOperation("GENERATE random array of size " + size);
    }

    @FXML
    private void insertElement() {
        if (isOperationRunning || isDemoRunning) return;

        String valueInput = valueField.getText().trim();
        String indexInput = indexField.getText().trim();

        if (valueInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter a value to insert.");
            return;
        }

        try {
            int value = Integer.parseInt(valueInput);
            if (value < 1 || value > 999) {
                showAlert("Invalid Range", "Please enter a value between 1 and 999.");
                return;
            }

            int index;
            if (indexInput.isEmpty()) {
                // Insert at end
                index = arrayList.size();
            } else {
                index = Integer.parseInt(indexInput);
                if (index < 0 || index > arrayList.size()) {
                    showAlert("Invalid Index", "Index must be between 0 and " + arrayList.size());
                    return;
                }
            }

            isOperationRunning = true;
            updateControlStates();

            // Insert into array
            arrayList.add(index, value);

            // Animate insertion
            animateInsert(index, value, () -> {
                isOperationRunning = false;
                updateControlStates();
                updateDisplay();
            });

            valueField.clear();
            indexField.clear();
            logOperation("INSERT " + value + " at index " + index + " | Size: " + arrayList.size());

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers.");
        }
    }

    @FXML
    private void deleteElement() {
        if (isOperationRunning || isDemoRunning) return;

        String indexInput = indexField.getText().trim();
        if (indexInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter an index to delete.");
            return;
        }

        try {
            int index = Integer.parseInt(indexInput);

            if (arrayList.isEmpty()) {
                showAlert("Array Empty", "Cannot delete from an empty array.");
                return;
            }

            if (index < 0 || index >= arrayList.size()) {
                showAlert("Invalid Index", "Index must be between 0 and " + (arrayList.size() - 1));
                return;
            }

            isOperationRunning = true;
            updateControlStates();

            int deletedValue = arrayList.get(index);

            // Animate deletion
            animateDelete(index, () -> {
                arrayList.remove(index);
                isOperationRunning = false;
                updateControlStates();
                updateDisplay();
            });

            indexField.clear();
            logOperation("DELETE " + deletedValue + " at index " + index + " | Size: " + arrayList.size());

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for index.");
        }
    }

    @FXML
    private void updateElement() {
        if (isOperationRunning || isDemoRunning) return;

        String valueInput = valueField.getText().trim();
        String indexInput = indexField.getText().trim();

        if (valueInput.isEmpty() || indexInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter both value and index.");
            return;
        }

        try {
            int value = Integer.parseInt(valueInput);
            int index = Integer.parseInt(indexInput);

            if (value < 1 || value > 999) {
                showAlert("Invalid Range", "Please enter a value between 1 and 999.");
                return;
            }

            if (arrayList.isEmpty()) {
                showAlert("Array Empty", "Cannot update an empty array.");
                return;
            }

            if (index < 0 || index >= arrayList.size()) {
                showAlert("Invalid Index", "Index must be between 0 and " + (arrayList.size() - 1));
                return;
            }

            int oldValue = arrayList.get(index);
            arrayList.set(index, value);

            // Animate update
            animateUpdate(index, oldValue, value);

            valueField.clear();
            indexField.clear();
            updateDisplay();
            logOperation("UPDATE index " + index + " from " + oldValue + " to " + value);

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers.");
        }
    }

    @FXML
    private void searchElement() {
        if (isDemoRunning) return;

        String valueInput = valueField.getText().trim();
        if (valueInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter a value to search.");
            return;
        }

        try {
            int value = Integer.parseInt(valueInput);

            if (arrayList.isEmpty()) {
                resultLabel.setText("❌ Array is empty");
                resultLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            // Animate search
            animateSearch(value, (index) -> {
                if (index != -1) {
                    resultLabel.setText("✅ Found " + value + " at index " + index);
                    resultLabel.setStyle("-fx-text-fill: #10b981;");
                } else {
                    resultLabel.setText("❌ Value " + value + " not found");
                    resultLabel.setStyle("-fx-text-fill: #ef4444;");
                }

                logOperation("SEARCH " + value + " | Result: " + (index != -1 ? "Found at " + index : "Not found"));
            });

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void clearArray() {
        if (isOperationRunning || isDemoRunning) return;

        if (arrayList.isEmpty()) {
            showAlert("Array Empty", "Array is already empty.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Array");
        confirmAlert.setHeaderText("Confirm Clear Operation");
        confirmAlert.setContentText("Are you sure you want to clear all " + arrayList.size() + " elements?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            isOperationRunning = true;
            updateControlStates();

            animateClear(() -> {
                arrayList.clear();
                isOperationRunning = false;
                updateControlStates();
                updateDisplay();
                logOperation("CLEAR_ALL | Array emptied");
            });
        }
    }

    @FXML
    private void runDemo() {
        if (isDemoRunning || isOperationRunning) return;

        isDemoRunning = true;
        updateControlStates();

        Timeline demoAnimation = createDemoAnimation();
        currentAnimation = demoAnimation;
        demoAnimation.play();

        logOperation("=== DEMO STARTED ===");
    }

    private void updateVisualization() {
        Platform.runLater(() -> {
            arrayVisualization.getChildren().clear();
            visualElements.clear();

            for (int i = 0; i < arrayList.size(); i++) {
                Label element = createArrayElement(arrayList.get(i), i);
                visualElements.add(element);
                arrayVisualization.getChildren().add(element);
            }
        });
    }

    private Label createArrayElement(int value, int index) {
        Label element = new Label(String.valueOf(value));
        element.getStyleClass().add("array-element");
        element.setMinWidth(60);
        element.setMinHeight(60);
        element.setAlignment(javafx.geometry.Pos.CENTER);
        element.setStyle("-fx-background-color: #dbeafe; -fx-border-color: #3b82f6; " +
                "-fx-border-width: 2; -fx-background-radius: 8; -fx-font-weight: bold; " +
                "-fx-font-size: 16px; -fx-text-fill: #1e40af;");

        // Add tooltip with index
        Tooltip tooltip = new Tooltip("Index: " + index + "\nValue: " + value);
        Tooltip.install(element, tooltip);

        return element;
    }

    private void animateInsert(int index, int value, Runnable onComplete) {
        Label newElement = createArrayElement(value, index);
        newElement.setScaleX(0);
        newElement.setScaleY(0);
        newElement.setOpacity(0);

        // Add to visualization at correct position
        if (index < arrayVisualization.getChildren().size()) {
            arrayVisualization.getChildren().add(index, newElement);
        } else {
            arrayVisualization.getChildren().add(newElement);
        }

        visualElements.add(index, newElement);

        // Animate appearance
        ParallelTransition insertAnim = new ParallelTransition();

        ScaleTransition scaleUp = animationService.createScaleTransition(newElement, 1.2, 1.2, 400);
        ScaleTransition scaleNormal = animationService.createScaleTransition(newElement, 1.0, 1.0, 200);
        scaleNormal.setDelay(Duration.millis(400));

        FadeTransition fadeIn = animationService.createFadeAnimation(newElement, 0, 1, 600);

        SequentialTransition scaleSeq = new SequentialTransition(scaleUp, scaleNormal);
        insertAnim.getChildren().addAll(scaleSeq, fadeIn);

        insertAnim.setOnFinished(e -> onComplete.run());
        insertAnim.play();
    }

    private void animateDelete(int index, Runnable onComplete) {
        if (index < visualElements.size()) {
            Label element = visualElements.get(index);

            ParallelTransition deleteAnim = new ParallelTransition();

            ScaleTransition shrink = animationService.createScaleTransition(element, 0, 0, 500);
            FadeTransition fadeOut = animationService.createFadeAnimation(element, 1, 0, 500);
            TranslateTransition moveUp = animationService.createTranslateTransition(element, 0, -50, 500);

            deleteAnim.getChildren().addAll(shrink, fadeOut, moveUp);
            deleteAnim.setOnFinished(e -> {
                arrayVisualization.getChildren().remove(element);
                visualElements.remove(element);
                onComplete.run();
            });
            deleteAnim.play();
        }
    }

    private void animateUpdate(int index, int oldValue, int newValue) {
        if (index < visualElements.size()) {
            Label element = visualElements.get(index);

            // Color change animation
            Timeline colorChange = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(element.styleProperty(),
                            "-fx-background-color: #f59e0b; -fx-border-color: #d97706; " +
                                    "-fx-border-width: 2; -fx-background-radius: 8; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-text-fill: white;")),
                    new KeyFrame(Duration.millis(600), new KeyValue(element.styleProperty(),
                            "-fx-background-color: #dbeafe; -fx-border-color: #3b82f6; " +
                                    "-fx-border-width: 2; -fx-background-radius: 8; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-text-fill: #1e40af;"))
            );

            // Scale animation
            ScaleTransition pulse = animationService.createScaleTransition(element, 1.3, 1.3, 300);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);

            // Update text
            Platform.runLater(() -> element.setText(String.valueOf(newValue)));

            ParallelTransition updateAnim = new ParallelTransition(colorChange, pulse);
            updateAnim.play();
        }
    }

    private void animateSearch(int value, SearchCallback callback) {
        Timeline searchAnim = new Timeline();
        int foundIndex = -1;

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i) == value) {
                foundIndex = i;
                break;
            }
        }

        final int targetIndex = foundIndex;

        // Animate through each element
        for (int i = 0; i < arrayList.size(); i++) {
            final int index = i;
            KeyFrame searchFrame = new KeyFrame(
                    Duration.millis((i + 1) * 200),
                    e -> {
                        highlightSearchElement(index, targetIndex == index);

                        if (index == arrayList.size() - 1 || index == targetIndex) {
                            Platform.runLater(() -> callback.onSearchComplete(targetIndex));
                        }
                    }
            );
            searchAnim.getKeyFrames().add(searchFrame);

            if (index == targetIndex) break;
        }

        searchAnim.play();
    }

    private void animateClear(Runnable onComplete) {
        if (visualElements.isEmpty()) {
            onComplete.run();
            return;
        }

        ParallelTransition clearAnim = new ParallelTransition();

        for (int i = 0; i < visualElements.size(); i++) {
            Label element = visualElements.get(i);

            TranslateTransition moveOut = animationService.createTranslateTransition(element, 0, -200, 600);
            moveOut.setDelay(Duration.millis(i * 50));

            FadeTransition fadeOut = animationService.createFadeAnimation(element, 1, 0, 600);
            fadeOut.setDelay(Duration.millis(i * 50));

            RotateTransition rotate = new RotateTransition(Duration.millis(600), element);
            rotate.setByAngle(360);
            rotate.setDelay(Duration.millis(i * 50));

            ParallelTransition elementAnim = new ParallelTransition(moveOut, fadeOut, rotate);
            clearAnim.getChildren().add(elementAnim);
        }

        clearAnim.setOnFinished(e -> {
            arrayVisualization.getChildren().clear();
            visualElements.clear();
            onComplete.run();
        });
        clearAnim.play();
    }

    private Timeline createDemoAnimation() {
        Timeline demo = new Timeline();

        demo.getKeyFrames().addAll(
                new KeyFrame(Duration.seconds(1), e -> {
                    valueField.setText("42");
                    insertElement();
                }),
                new KeyFrame(Duration.seconds(2.5), e -> {
                    valueField.setText("17");
                    indexField.setText("0");
                    insertElement();
                }),
                new KeyFrame(Duration.seconds(4), e -> {
                    valueField.setText("89");
                    insertElement();
                }),
                new KeyFrame(Duration.seconds(5.5), e -> {
                    valueField.setText("55");
                    indexField.setText("2");
                    updateElement();
                }),
                new KeyFrame(Duration.seconds(7), e -> {
                    valueField.setText("17");
                    searchElement();
                }),
                new KeyFrame(Duration.seconds(9), e -> {
                    indexField.setText("1");
                    deleteElement();
                }),
                new KeyFrame(Duration.seconds(10.5), e -> {
                    valueField.setText("99");
                    insertElement();
                }),
                new KeyFrame(Duration.seconds(12), e -> {
                    isDemoRunning = false;
                    updateControlStates();
                    logOperation("=== DEMO COMPLETED ===");
                    showInfo("Demo Complete", "Array operations demonstration finished!\n\n" +
                            "Final array size: " + arrayList.size() + " elements");
                })
        );

        return demo;
    }

    private void highlightSearchElement(int index, boolean isTarget) {
        if (index < visualElements.size()) {
            Label element = visualElements.get(index);
            String color = isTarget ? "#10b981" : "#f59e0b"; // Green if target, orange if searching

            Timeline highlight = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(element.styleProperty(),
                            "-fx-background-color: " + color + "; -fx-border-color: " + color + "; " +
                                    "-fx-border-width: 2; -fx-background-radius: 8; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-text-fill: white;")),
                    new KeyFrame(Duration.millis(isTarget ? 2000 : 400), new KeyValue(element.styleProperty(),
                            "-fx-background-color: #dbeafe; -fx-border-color: #3b82f6; " +
                                    "-fx-border-width: 2; -fx-background-radius: 8; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-text-fill: #1e40af;"))
            );

            highlight.play();
        }
    }

    private void updateDisplay() {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(arrayList.isEmpty() ?
                        "Array is Empty" :
                        "Array has " + arrayList.size() + " elements");
            }

            if (sizeLabel != null) {
                sizeLabel.setText("Size: " + arrayList.size());
            }

            updateVisualization();
            updateControlStates();
        });
    }

    private void updateControlStates() {
        boolean isEmpty = arrayList.isEmpty();
        boolean isRunning = isOperationRunning || isDemoRunning;

        if (insertButton != null) insertButton.setDisable(isRunning);
        if (deleteButton != null) deleteButton.setDisable(isEmpty || isRunning);
        if (updateButton != null) updateButton.setDisable(isEmpty || isRunning);
        if (searchButton != null) searchButton.setDisable(isEmpty || isRunning);
        if (clearButton != null) clearButton.setDisable(isEmpty || isRunning);
        if (generateButton != null) generateButton.setDisable(isRunning);
        if (demoButton != null) demoButton.setDisable(isRunning);
        if (valueField != null) valueField.setDisable(isRunning);
        if (indexField != null) indexField.setDisable(isRunning);
        if (arraySizeSlider != null) arraySizeSlider.setDisable(isRunning);
    }

    private void logOperation(String operation) {
        if (operationHistory != null) {
            operationHistory.appendText(operation + "\n");
            operationHistory.setScrollTop(Double.MAX_VALUE);
        }
        System.out.println("Array Operation: " + operation);
    }

    @FXML
    private void goBack() {
        try {
            if (currentAnimation != null) {
                currentAnimation.stop();
            }

            Stage currentStage = (Stage) arrayVisualization.getScene().getWindow();
            // ✅ Use hide() instead of close() for back navigation
            currentStage.hide();
            System.out.println("Array module hidden (can be reopened)");

        } catch (Exception e) {
            System.err.println("Error hiding Array window: " + e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface SearchCallback {
        void onSearchComplete(int index);
    }
}
