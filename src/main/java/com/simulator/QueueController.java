package com.simulator;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Queue Operations module.
 * Visualizes FIFO (First-In-First-Out) data structure operations.
 */
public class QueueController {
    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);

    @FXML
    private TextField inputField;
    @FXML
    private Button enqueueButton, dequeueButton, frontButton, rearButton, clearButton;
    @FXML
    private Label statusLabel, sizeLabel, frontLabel, rearLabel, capacityLabel;
    @FXML
    private HBox queueContainer;
    @FXML
    private VBox visualizationPane;
    @FXML
    private TextArea operationHistory;
    @FXML
    private ProgressBar capacityBar;
    @FXML
    private Button codesButton;

    private QueueModel queueModel;
    private List<QueueElement> visualElements;
    private Timeline currentAnimation;
    private AnimationService animationService;

    @FXML
    public void initialize() {
        System.out.println("QueueController initialized");

        queueModel = new QueueModel(15); // Max capacity of 15
        visualElements = new ArrayList<>();
        animationService = AnimationService.getInstance();

        setupComponents();
        setupEventHandlers();
        updateDisplay();

        System.out.println("Queue module ready");
    }

    private void setupComponents() {
        // Configure input validation - only numbers
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                inputField.setText(newText.replaceAll("[^\\d]", ""));
            }
            if (newText.length() > 3) {
                inputField.setText(newText.substring(0, 3));
            }
        });

        inputField.setPromptText("Enter number (1-999)");

        // Initialize operation history
        if (operationHistory != null) {
            operationHistory.setText("=== Queue Operations History ===\n");
            operationHistory.setEditable(false);
        }
    }

    private void setupEventHandlers() {
        // Enter key support for enqueue operation
        inputField.setOnAction(e -> enqueueElement());
    }

    @FXML
    private void openCodesPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/queue-codes.fxml"));
            Parent root = loader.load();
            QueueCodeController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initOwner(queueContainer.getScene().getWindow());
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle("Queue Code Examples");

            Scene scene = new Scene(root, 1200, 800);

            // Apply theme
            boolean isDarkMode = ThemeManager.getInstance().isDarkMode();
            scene.getStylesheets().clear();
            scene.getStylesheets().addAll(queueContainer.getScene().getStylesheets());

            String themeClass = isDarkMode ? "dark-theme" : "light-theme";
            String removeClass = isDarkMode ? "light-theme" : "dark-theme";

            scene.getRoot().getStyleClass().removeAll(removeClass);
            scene.getRoot().getStyleClass().add(themeClass);

            root.getStyleClass().removeAll(removeClass);
            root.getStyleClass().add(themeClass);
            root.getStyleClass().addAll("main-layout", "algorithm-page", "code-popup-window");

            popupStage.setScene(scene);
            popupStage.setMinWidth(1000);
            popupStage.setMinHeight(700);
            popupStage.setResizable(true);

            // Center on parent window
            Stage parentStage = (Stage) queueContainer.getScene().getWindow();
            popupStage.setX(parentStage.getX() + 50);
            popupStage.setY(parentStage.getY() + 50);

            controller.setParentController(this);

            popupStage.setOnCloseRequest(e -> {
                controller.cleanup();
            });

            popupStage.show();
            logOperation("QUEUE_CODES_POPUP_OPENED | Queue code examples displayed");

        } catch (Exception e) {
            logger.error("Could not open code examples", e);
            AlertHelper.showWarning("Error", "Could not open code examples: " + e.getMessage());
        }
    }

    @FXML
    private void enqueueElement() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            AlertHelper.showWarning("Invalid Input", "Please enter a number to enqueue.");
            return;
        }

        try {
            int value = Integer.parseInt(input);
            if (value < 1 || value > 999) {
                AlertHelper.showWarning("Invalid Range", "Please enter a number between 1 and 999.");
                return;
            }

            if (queueModel.isFull()) {
                AlertHelper.showWarning("Queue Full",
                        "Cannot enqueue - Queue has reached maximum capacity of " + queueModel.getCapacity());
                return;
            }

            // Add to model
            queueModel.enqueue(value);

            // Create visual element
            QueueElement element = createQueueElement(value);
            visualElements.add(element);

            // Animate enqueue operation (from right)
            animateEnqueue(element);

            // Clear input and update display
            inputField.clear();
            updateDisplay();
            logOperation("ENQUEUE " + value + " | Size: " + queueModel.size());

        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void dequeueElement() {
        if (queueModel.isEmpty()) {
            AlertHelper.showWarning("Queue Empty", "Cannot dequeue from an empty queue.");
            return;
        }

        // Remove from model
        int dequeuedValue = queueModel.dequeue();

        // Get visual element (first element - front of queue)
        QueueElement element = visualElements.get(0);

        // Animate dequeue operation (from left)
        animateDequeue(element, () -> {
            visualElements.remove(0);
            queueContainer.getChildren().remove(element.getContainer());
            updateDisplay();
        });

        // Show dequeued value
        AlertHelper.showInfo("Element Dequeued", "Dequeued value: " + dequeuedValue);
        logOperation("DEQUEUE " + dequeuedValue + " | Size: " + queueModel.size());
    }

    @FXML
    private void showFront() {
        if (queueModel.isEmpty()) {
            AlertHelper.showWarning("Queue Empty", "Cannot view front of an empty queue.");
            return;
        }

        int frontValue = queueModel.front();

        // Animate front highlight (first element)
        if (!visualElements.isEmpty()) {
            QueueElement frontElement = visualElements.get(0);
            animateHighlight(frontElement);
        }

        AlertHelper.showInfo("Front Element", "Front of queue: " + frontValue);
        logOperation("FRONT " + frontValue + " | Size: " + queueModel.size());
    }

    @FXML
    private void showRear() {
        if (queueModel.isEmpty()) {
            AlertHelper.showWarning("Queue Empty", "Cannot view rear of an empty queue.");
            return;
        }

        int rearValue = queueModel.rear();

        // Animate rear highlight (last element)
        if (!visualElements.isEmpty()) {
            QueueElement rearElement = visualElements.get(visualElements.size() - 1);
            animateHighlight(rearElement);
        }

        AlertHelper.showInfo("Rear Element", "Rear of queue: " + rearValue);
        logOperation("REAR " + rearValue + " | Size: " + queueModel.size());
    }

    @FXML
    private void clearQueue() {
        if (queueModel.isEmpty()) {
            AlertHelper.showWarning("Queue Empty", "Queue is already empty.");
            return;
        }

        // Confirm clear operation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Queue");
        confirmAlert.setHeaderText("Confirm Clear Operation");
        confirmAlert.setContentText(
                "Are you sure you want to clear all " + queueModel.size() + " elements from the queue?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Animate clear operation
            animateClear(() -> {
                queueModel.clear();
                visualElements.clear();
                queueContainer.getChildren().clear();
                updateDisplay();
                logOperation("CLEAR ALL | Queue emptied");
            });
        }
    }

    private QueueElement createQueueElement(int value) {
        VBox container = new VBox();
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.getStyleClass().add("queue-element");
        container.setPrefWidth(70);
        container.setPrefHeight(60);
        container.setStyle(
                "-fx-background-color: #14b8a6; -fx-background-radius: 12; -fx-border-color: #0d9488; -fx-border-width: 2; -fx-border-radius: 12;");

        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("queue-value");
        valueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 18px;");

        Label positionLabel = new Label("pos: " + queueModel.size());
        positionLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #ccfbf1;");

        container.getChildren().addAll(valueLabel, positionLabel);

        return new QueueElement(container, valueLabel, value);
    }

    private void animateEnqueue(QueueElement element) {
        VBox container = element.getContainer();

        // Start from right side (off-screen)
        container.setTranslateX(250);
        container.setOpacity(0);
        container.setScaleX(0.3);
        container.setScaleY(0.3);

        queueContainer.getChildren().add(container); // Add to rear (right side)

        // Create enqueue animation
        ParallelTransition enqueueAnim = new ParallelTransition();

        // Slide in from right
        TranslateTransition slide = animationService.createTranslateTransition(container, 0, 0, 600);

        // Fade in
        FadeTransition fade = animationService.createFadeAnimation(container, 0, 1, 600);

        // Scale up with bounce effect
        ScaleTransition scale = animationService.createScaleTransition(container, 1.1, 1.1, 400);

        // Scale back to normal
        ScaleTransition scaleBack = animationService.createScaleTransition(container, 1.0, 1.0, 200);
        scaleBack.setDelay(Duration.millis(400));

        enqueueAnim.getChildren().addAll(slide, fade, scale, scaleBack);
        enqueueAnim.play();
    }

    private void animateDequeue(QueueElement element, Runnable onComplete) {
        VBox container = element.getContainer();

        // Create dequeue animation
        ParallelTransition dequeueAnim = new ParallelTransition();

        // Scale up then disappear
        ScaleTransition scaleUp = animationService.createScaleTransition(container, 1.3, 1.3, 250);

        // Move left and fade out
        TranslateTransition moveLeft = animationService.createTranslateTransition(container, -150, 0, 500);
        moveLeft.setDelay(Duration.millis(250));

        FadeTransition fadeOut = animationService.createFadeAnimation(container, 1, 0, 500);
        fadeOut.setDelay(Duration.millis(250));

        // Rotation for visual effect
        RotateTransition rotate = new RotateTransition(Duration.millis(500), container);
        rotate.setByAngle(-180);
        rotate.setDelay(Duration.millis(250));

        dequeueAnim.getChildren().addAll(scaleUp, moveLeft, fadeOut, rotate);
        dequeueAnim.setOnFinished(e -> onComplete.run());
        dequeueAnim.play();
    }

    private void animateHighlight(QueueElement element) {
        VBox container = element.getContainer();

        // Create pulse animation with color change
        ScaleTransition pulse1 = animationService.createScaleTransition(container, 1.4, 1.4, 200);
        ScaleTransition pulse2 = animationService.createScaleTransition(container, 1.0, 1.0, 200);

        // Color change animation
        Timeline colorAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(container.styleProperty(),
                        "-fx-background-color: #14b8a6; -fx-background-radius: 12; -fx-border-color: #0d9488; -fx-border-width: 2; -fx-border-radius: 12;")),
                new KeyFrame(Duration.millis(400), new KeyValue(container.styleProperty(),
                        "-fx-background-color: #f59e0b; -fx-background-radius: 12; -fx-border-color: #d97706; -fx-border-width: 2; -fx-border-radius: 12;")),
                new KeyFrame(Duration.millis(800), new KeyValue(container.styleProperty(),
                        "-fx-background-color: #14b8a6; -fx-background-radius: 12; -fx-border-color: #0d9488; -fx-border-width: 2; -fx-border-radius: 12;")));

        SequentialTransition highlightAnim = new SequentialTransition(pulse1, pulse2);
        ParallelTransition fullAnim = new ParallelTransition(highlightAnim, colorAnim);
        fullAnim.play();
    }

    private void animateClear(Runnable onComplete) {
        if (visualElements.isEmpty()) {
            onComplete.run();
            return;
        }

        ParallelTransition clearAnim = new ParallelTransition();

        for (int i = 0; i < visualElements.size(); i++) {
            VBox container = visualElements.get(i).getContainer();

            // Stagger the animations
            TranslateTransition moveOut = animationService.createTranslateTransition(container, 0, 400, 600);
            moveOut.setDelay(Duration.millis(i * 80));

            FadeTransition fadeOut = animationService.createFadeAnimation(container, 1, 0, 600);
            fadeOut.setDelay(Duration.millis(i * 80));

            RotateTransition rotate = new RotateTransition(Duration.millis(600), container);
            rotate.setToAngle(360);
            rotate.setDelay(Duration.millis(i * 80));

            ParallelTransition elementAnim = new ParallelTransition(moveOut, fadeOut, rotate);
            clearAnim.getChildren().add(elementAnim);
        }

        clearAnim.setOnFinished(e -> onComplete.run());
        clearAnim.play();
    }

    private void updateDisplay() {
        if (statusLabel != null) {
            statusLabel
                    .setText(queueModel.isEmpty() ? "Queue is Empty" : "Queue has " + queueModel.size() + " elements");
        }

        if (sizeLabel != null) {
            sizeLabel.setText("Size: " + queueModel.size());
        }

        if (frontLabel != null) {
            frontLabel.setText("Front: " + (queueModel.isEmpty() ? "null" : queueModel.front()));
        }

        if (rearLabel != null) {
            rearLabel.setText("Rear: " + (queueModel.isEmpty() ? "null" : queueModel.rear()));
        }

        if (capacityLabel != null) {
            capacityLabel.setText("Capacity: " + queueModel.size() + "/" + queueModel.getCapacity());
        }

        if (capacityBar != null) {
            double usage = (double) queueModel.size() / queueModel.getCapacity();
            capacityBar.setProgress(usage);

            // Change color based on usage
            if (usage > 0.8) {
                capacityBar.setStyle("-fx-accent: #ef4444;"); // Red when nearly full
            } else if (usage > 0.6) {
                capacityBar.setStyle("-fx-accent: #f59e0b;"); // Orange when getting full
            } else {
                capacityBar.setStyle("-fx-accent: #14b8a6;"); // Teal when normal
            }
        }

        updateControlStates();
    }

    private void updateControlStates() {
        boolean isEmpty = queueModel.isEmpty();
        boolean isFull = queueModel.isFull();

        if (enqueueButton != null)
            enqueueButton.setDisable(isFull);
        if (dequeueButton != null)
            dequeueButton.setDisable(isEmpty);
        if (frontButton != null)
            frontButton.setDisable(isEmpty);
        if (rearButton != null)
            rearButton.setDisable(isEmpty);
        if (clearButton != null)
            clearButton.setDisable(isEmpty);
    }

    public void logOperation(String operation) {
        if (operationHistory != null) {
            operationHistory.appendText(operation + "\n");
            operationHistory.setScrollTop(Double.MAX_VALUE); // Auto-scroll to bottom
        }
        System.out.println("Queue Operation: " + operation);
    }

    @FXML
    private void goBack() {
        try {
            // Stop any running animation
            if (currentAnimation != null) {
                currentAnimation.stop();
            }

            Stage currentStage = (Stage) queueContainer.getScene().getWindow();
            currentStage.hide();
            System.out.println("Queue module hidden (can be reopened)");

        } catch (Exception e) {
            System.err.println("Error hiding Queue window: " + e.getMessage());
        }
    }

    // Removed showAlert and showInfo - using AlertHelper instead

    // Inner class for queue elements
    private static class QueueElement {
        private final VBox container;
        private final Label label;
        private final int value;

        public QueueElement(VBox container, Label label, int value) {
            this.container = container;
            this.label = label;
            this.value = value;
        }

        public VBox getContainer() {
            return container;
        }

        public Label getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }
}
