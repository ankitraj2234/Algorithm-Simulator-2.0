package com.simulator;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.application.Platform;







public class StackController {

    @FXML private TextField inputField;
    @FXML private Button pushButton, popButton, peekButton, clearButton;
    @FXML private Label statusLabel, sizeLabel, topLabel, capacityLabel;
    @FXML
    VBox stackContainer;
    @FXML private VBox visualizationPane;
    @FXML private TextArea operationHistory;
    @FXML private ProgressBar capacityBar;
    @FXML private Button codesButton;

    private StackModel stackModel;
    private List<StackElement> visualElements;
    private Timeline currentAnimation;
    private AnimationService animationService;

    @FXML
    public void initialize() {
        System.out.println("StackController initialized");

        stackModel = new StackModel(15); // Max capacity of 15
        visualElements = new ArrayList<>();
        animationService = AnimationService.getInstance();

        setupComponents();
        setupEventHandlers();
        updateDisplay();

        System.out.println("Stack module ready");
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
            operationHistory.setText("=== Stack Operations History ===\n");
            operationHistory.setEditable(false);
        }
    }

    private void setupEventHandlers() {
        // Enter key support for push operation
        inputField.setOnAction(e -> pushElement());
    }
    @FXML
    private void openCodesPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/stack-codes.fxml"));
            Parent root = loader.load();
            StackCodeController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initOwner(stackContainer.getScene().getWindow());
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle("Stack Code Examples");

            Scene scene = new Scene(root, 1200, 800);

            // 🎯 ENHANCED: Copy stylesheets and apply theme
            boolean isDarkMode = ThemeManager.getInstance().isDarkMode();

            // Copy stylesheets from main app
            scene.getStylesheets().clear();
            scene.getStylesheets().addAll(stackContainer.getScene().getStylesheets());

            // Apply theme classes to scene and popup roots
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
            Stage parentStage = (Stage) stackContainer.getScene().getWindow();
            popupStage.setX(parentStage.getX() + 50);
            popupStage.setY(parentStage.getY() + 50);

            controller.setParentController(this);

            // 🎯 CRITICAL: Handle popup closing properly
            popupStage.setOnCloseRequest(e -> {
                controller.cleanup(); // Ensure cleanup happens
            });

            popupStage.show();
            logOperation("STACK_CODES_POPUP_OPENED | Stack code examples displayed");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not open code examples: " + e.getMessage());
        }
    }



    @FXML
    private void pushElement() {

        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a number to push onto the stack.");
            return;
        }

        try {
            int value = Integer.parseInt(input);
            if (value < 1 || value > 999) {
                showAlert("Invalid Range", "Please enter a number between 1 and 999.");
                return;
            }

            if (stackModel.isFull()) {
                showAlert("Stack Full", "Cannot push - Stack has reached maximum capacity of " + stackModel.getCapacity());
                return;
            }

            // Add to model
            stackModel.push(value);

            // Create visual element
            StackElement element = createStackElement(value);
            visualElements.add(element);

            // Animate push operation
            animatePush(element);

            // Clear input and update display
            inputField.clear();
            updateDisplay();
            logOperation("PUSH " + value + " | Size: " + stackModel.size());

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void popElement() {

        if (stackModel.isEmpty()) {
            showAlert("Stack Empty", "Cannot pop from an empty stack.");
            return;
        }

        // Remove from model
        int poppedValue = stackModel.pop();

        // Get visual element
        StackElement element = visualElements.get(visualElements.size() - 1);

        // Animate pop operation
        animatePop(element, () -> {
            visualElements.remove(visualElements.size() - 1);
            stackContainer.getChildren().remove(element.getContainer());
            updateDisplay();
        });

        // Show popped value
        showInfo("Element Popped", "Popped value: " + poppedValue);
        logOperation("POP " + poppedValue + " | Size: " + stackModel.size());
    }

    @FXML
    private void peekElement() {
        if (stackModel.isEmpty()) {
            showAlert("Stack Empty", "Cannot peek into an empty stack.");
            return;
        }

        int topValue = stackModel.peek();

        // Animate peek (highlight top element)
        if (!visualElements.isEmpty()) {
            StackElement topElement = visualElements.get(visualElements.size() - 1);
            animatePeek(topElement);
        }

        showInfo("Peek Result", "Top element: " + topValue);
        logOperation("PEEK " + topValue + " | Size: " + stackModel.size());
    }

    @FXML
    private void clearStack() {

        if (stackModel.isEmpty()) {
            showAlert("Stack Empty", "Stack is already empty.");
            return;
        }

        // Confirm clear operation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Stack");
        confirmAlert.setHeaderText("Confirm Clear Operation");
        confirmAlert.setContentText("Are you sure you want to clear all " + stackModel.size() + " elements from the stack?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Animate clear operation
            animateClear(() -> {
                stackModel.clear();
                visualElements.clear();
                stackContainer.getChildren().clear();
                updateDisplay();
                logOperation("CLEAR ALL | Stack emptied");
            });
        }
    }



    private StackElement createStackElement(int value) {
        VBox container = new VBox();
        container.setAlignment(javafx.geometry.Pos.CENTER);
        container.getStyleClass().add("stack-element");
        container.setPrefWidth(120);
        container.setPrefHeight(50);
        container.setStyle("-fx-background-color: #3b82f6; -fx-background-radius: 12; -fx-border-color: #1d4ed8; -fx-border-width: 2; -fx-border-radius: 12;");

        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("stack-value");
        valueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 18px;");

        Label positionLabel = new Label("pos: " + stackModel.size());
        positionLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #dbeafe;");

        container.getChildren().addAll(valueLabel, positionLabel);

        return new StackElement(container, valueLabel, value);
    }

    private void animatePush(StackElement element) {
        VBox container = element.getContainer();

        // Start from right side (off-screen)
        container.setTranslateX(250);
        container.setOpacity(0);
        container.setScaleX(0.3);
        container.setScaleY(0.3);

        stackContainer.getChildren().add(0, container); // Add to top

        // Create push animation
        ParallelTransition pushAnim = new ParallelTransition();

        // Slide in from right
        TranslateTransition slide = animationService.createTranslateTransition(container, 0, 0, 600);

        // Fade in
        FadeTransition fade = animationService.createFadeAnimation(container, 0, 1, 600);

        // Scale up with bounce effect
        ScaleTransition scale = animationService.createScaleTransition(container, 1.1, 1.1, 400);

        // Scale back to normal
        ScaleTransition scaleBack = animationService.createScaleTransition(container, 1.0, 1.0, 200);
        scaleBack.setDelay(Duration.millis(400));

        pushAnim.getChildren().addAll(slide, fade, scale, scaleBack);
        pushAnim.play();
    }

    private void animatePop(StackElement element, Runnable onComplete) {
        VBox container = element.getContainer();

        // Create pop animation
        ParallelTransition popAnim = new ParallelTransition();

        // Scale up then disappear
        ScaleTransition scaleUp = animationService.createScaleTransition(container, 1.3, 1.3, 250);

        // Move up and fade out
        TranslateTransition moveUp = animationService.createTranslateTransition(container, 0, -150, 500);
        moveUp.setDelay(Duration.millis(250));

        FadeTransition fadeOut = animationService.createFadeAnimation(container, 1, 0, 500);
        fadeOut.setDelay(Duration.millis(250));

        // Rotation for visual effect
        RotateTransition rotate = new RotateTransition(Duration.millis(500), container);
        rotate.setByAngle(180);
        rotate.setDelay(Duration.millis(250));

        popAnim.getChildren().addAll(scaleUp, moveUp, fadeOut, rotate);
        popAnim.setOnFinished(e -> onComplete.run());
        popAnim.play();
    }

    private void animatePeek(StackElement element) {
        VBox container = element.getContainer();

        // Create pulse animation with color change
        ScaleTransition pulse1 = animationService.createScaleTransition(container, 1.4, 1.4, 200);
        ScaleTransition pulse2 = animationService.createScaleTransition(container, 1.0, 1.0, 200);

        // Color change animation
        Timeline colorAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(container.styleProperty(),
                        "-fx-background-color: #3b82f6; -fx-background-radius: 12; -fx-border-color: #1d4ed8; -fx-border-width: 2; -fx-border-radius: 12;")),
                new KeyFrame(Duration.millis(400), new KeyValue(container.styleProperty(),
                        "-fx-background-color: #f59e0b; -fx-background-radius: 12; -fx-border-color: #d97706; -fx-border-width: 2; -fx-border-radius: 12;")),
                new KeyFrame(Duration.millis(800), new KeyValue(container.styleProperty(),
                        "-fx-background-color: #3b82f6; -fx-background-radius: 12; -fx-border-color: #1d4ed8; -fx-border-width: 2; -fx-border-radius: 12;"))
        );

        SequentialTransition peekAnim = new SequentialTransition(pulse1, pulse2);
        ParallelTransition fullAnim = new ParallelTransition(peekAnim, colorAnim);
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
            TranslateTransition moveOut = animationService.createTranslateTransition(container, -400, 0, 600);
            moveOut.setDelay(Duration.millis(i * 100));

            FadeTransition fadeOut = animationService.createFadeAnimation(container, 1, 0, 600);
            fadeOut.setDelay(Duration.millis(i * 100));

            RotateTransition rotate = new RotateTransition(Duration.millis(600), container);
            rotate.setToAngle(360);
            rotate.setDelay(Duration.millis(i * 100));

            ParallelTransition elementAnim = new ParallelTransition(moveOut, fadeOut, rotate);
            clearAnim.getChildren().add(elementAnim);
        }

        clearAnim.setOnFinished(e -> onComplete.run());
        clearAnim.play();
    }



    private void updateDisplay() {
        if (statusLabel != null) {
            statusLabel.setText(stackModel.isEmpty() ? "Stack is Empty" : "Stack has " + stackModel.size() + " elements");
        }

        if (sizeLabel != null) {
            sizeLabel.setText("Size: " + stackModel.size());
        }

        if (topLabel != null) {
            topLabel.setText("Top: " + (stackModel.isEmpty() ? "null" : stackModel.peek()));
        }

        if (capacityLabel != null) {
            capacityLabel.setText("Capacity: " + stackModel.size() + "/" + stackModel.getCapacity());
        }

        if (capacityBar != null) {
            double usage = (double) stackModel.size() / stackModel.getCapacity();
            capacityBar.setProgress(usage);

            // Change color based on usage
            if (usage > 0.8) {
                capacityBar.setStyle("-fx-accent: #ef4444;"); // Red when nearly full
            } else if (usage > 0.6) {
                capacityBar.setStyle("-fx-accent: #f59e0b;"); // Orange when getting full
            } else {
                capacityBar.setStyle("-fx-accent: #10b981;"); // Green when normal
            }
        }

        updateControlStates();
    }

    private void updateControlStates() {
        boolean isEmpty = stackModel.isEmpty();
        boolean isFull = stackModel.isFull();

        if (pushButton != null) pushButton.setDisable(isFull);
        if (popButton != null) popButton.setDisable(isEmpty );
        if (peekButton != null) peekButton.setDisable(isEmpty );
        if (clearButton != null) clearButton.setDisable(isEmpty );
    }

    public void logOperation(String operation) {
        if (operationHistory != null) {
            operationHistory.appendText(operation + "\n");
            operationHistory.setScrollTop(Double.MAX_VALUE); // Auto-scroll to bottom
        }
        System.out.println("Stack Operation: " + operation);
    }

    @FXML
    private void goBack() {
        try {
            // Stop any running animation
            if (currentAnimation != null) {
                currentAnimation.stop();
            }

            Stage currentStage = (Stage) stackContainer.getScene().getWindow();
            // ✅ Use hide() instead of close() for back navigation
            currentStage.hide();
            System.out.println("Stack module hidden (can be reopened)");

        } catch (Exception e) {
            System.err.println("Error hiding Stack window: " + e.getMessage());
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

    // Inner class for stack elements
    private static class StackElement {
        private final VBox container;
        private final Label label;
        private final int value;

        public StackElement(VBox container, Label label, int value) {
            this.container = container;
            this.label = label;
            this.value = value;
        }

        public VBox getContainer() { return container; }
        public Label getLabel() { return label; }
        public int getValue() { return value; }
    }
}
