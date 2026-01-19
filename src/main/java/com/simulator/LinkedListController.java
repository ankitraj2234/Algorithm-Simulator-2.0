package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedListController {
    private static final Logger logger = LoggerFactory.getLogger(LinkedListController.class);

    @FXML
    private TextField inputField;
    @FXML
    private TextField positionField;
    @FXML
    private TextField searchField;
    @FXML
    private Button insertBeginButton, insertEndButton, insertPositionButton;
    @FXML
    private Button deleteValueButton, deletePositionButton, searchButton;
    @FXML
    private Button clearButton;
    @FXML
    private HBox listVisualization;
    @FXML
    private Label statusLabel, sizeLabel, resultLabel;
    @FXML
    private TextArea operationHistory;
    @FXML
    private ProgressBar operationProgress;
    @FXML
    private Button codesButton;

    private LinkedListModel listModel;
    private List<NodeElement> visualNodes;
    private AnimationService animationService;
    private Timeline currentAnimation;
    private boolean isOperationRunning = false;

    @FXML
    public void initialize() {
        System.out.println("LinkedListController initialized");

        listModel = new LinkedListModel();
        visualNodes = new ArrayList<>();
        animationService = AnimationService.getInstance();

        setupComponents();
        setupEventHandlers();
        updateDisplay();

        System.out.println("LinkedList module ready");
    }

    @FXML
    private void openCodesPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/code-popup.fxml"));
            Parent root = loader.load();
            CodePopupController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initOwner(listVisualization.getScene().getWindow());
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle("LinkedList Code Examples");

            Scene scene = new Scene(root, 1200, 800);

            // 🎯 ENHANCED: Copy stylesheets and apply theme
            boolean isDarkMode = ThemeManager.getInstance().isDarkMode();

            // Copy stylesheets from main app
            scene.getStylesheets().clear();
            scene.getStylesheets().addAll(listVisualization.getScene().getStylesheets());

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
            Stage parentStage = (Stage) listVisualization.getScene().getWindow();
            popupStage.setX(parentStage.getX() + 50);
            popupStage.setY(parentStage.getY() + 50);

            controller.setParentController(this);

            // 🎯 CRITICAL: Handle popup closing properly
            popupStage.setOnCloseRequest(e -> {
                controller.cleanup(); // Ensure cleanup happens
            });

            popupStage.show();
            logOperation("CODES_POPUP_OPENED | LinkedList code examples displayed");

        } catch (Exception e) {
            logger.error("Could not open code examples", e);
            showAlert("Error", "Could not open code examples: " + e.getMessage());
        }
    }

    private void setupComponents() {
        // Configure input validation
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                inputField.setText(newText.replaceAll("[^\\d]", ""));
            }
            if (newText.length() > 3) {
                inputField.setText(newText.substring(0, 3));
            }
        });

        positionField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                positionField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                searchField.setText(newText.replaceAll("[^\\d]", ""));
            }
            if (newText.length() > 3) {
                searchField.setText(newText.substring(0, 3));
            }
        });

        inputField.setPromptText("Enter value (1-999)");
        positionField.setPromptText("Position");
        searchField.setPromptText("Search value");

        if (operationHistory != null) {
            operationHistory.setText("=== LinkedList Operations History ===\n");
            operationHistory.setEditable(false);
        }
    }

    private void setupEventHandlers() {
        inputField.setOnAction(e -> insertAtEnd());
        positionField.setOnAction(e -> insertAtPosition());
        searchField.setOnAction(e -> searchValue());
    }

    @FXML
    private void insertAtBeginning() {
        if (isOperationRunning)
            return;

        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a value to insert.");
            return;
        }

        try {
            int value = Integer.parseInt(input);
            if (value < 1 || value > 999) {
                showAlert("Invalid Range", "Please enter a number between 1 and 999.");
                return;
            }

            isOperationRunning = true;
            updateControlStates();

            // Add to model
            listModel.insertAtBeginning(value);

            // Create visual node
            NodeElement nodeElement = createNodeElement(value, 0);
            visualNodes.add(0, nodeElement);

            // Update positions of existing nodes
            updateNodePositions();

            // Animate insertion
            animateInsertAtBeginning(nodeElement, () -> {
                isOperationRunning = false;
                updateControlStates();
                updateDisplay();
            });

            inputField.clear();
            logOperation("INSERT_BEGIN " + value + " | Size: " + listModel.size());

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void insertAtEnd() {
        if (isOperationRunning)
            return;

        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a value to insert.");
            return;
        }

        try {
            int value = Integer.parseInt(input);
            if (value < 1 || value > 999) {
                showAlert("Invalid Range", "Please enter a number between 1 and 999.");
                return;
            }

            isOperationRunning = true;
            updateControlStates();

            // Add to model
            listModel.insertAtEnd(value);

            // Create visual node
            NodeElement nodeElement = createNodeElement(value, visualNodes.size());
            visualNodes.add(nodeElement);

            // Animate insertion
            animateInsertAtEnd(nodeElement, () -> {
                isOperationRunning = false;
                updateControlStates();
                updateDisplay();
            });

            inputField.clear();
            logOperation("INSERT_END " + value + " | Size: " + listModel.size());

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void insertAtPosition() {
        if (isOperationRunning)
            return;

        String input = inputField.getText().trim();
        String posInput = positionField.getText().trim();

        if (input.isEmpty() || posInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter both value and position.");
            return;
        }

        try {
            int value = Integer.parseInt(input);
            int position = Integer.parseInt(posInput);

            if (value < 1 || value > 999) {
                showAlert("Invalid Range", "Please enter a value between 1 and 999.");
                return;
            }

            if (position < 0 || position > listModel.size()) {
                showAlert("Invalid Position", "Position must be between 0 and " + listModel.size());
                return;
            }

            isOperationRunning = true;
            updateControlStates();

            // Add to model
            listModel.insertAtPosition(position, value);

            // Create visual node
            NodeElement nodeElement = createNodeElement(value, position);
            visualNodes.add(position, nodeElement);

            // Update positions of nodes after insertion point
            updateNodePositions();

            // Animate insertion
            animateInsertAtPosition(nodeElement, position, () -> {
                isOperationRunning = false;
                updateControlStates();
                updateDisplay();
            });

            inputField.clear();
            positionField.clear();
            logOperation("INSERT_POS " + value + " at " + position + " | Size: " + listModel.size());

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers.");
        }
    }

    @FXML
    private void deleteValue() {
        if (isOperationRunning)
            return;

        String input = inputField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a value to delete.");
            return;
        }

        try {
            int value = Integer.parseInt(input);

            if (listModel.isEmpty()) {
                showAlert("List Empty", "Cannot delete from an empty list.");
                return;
            }

            // Find the node to delete
            int position = findNodePosition(value);
            if (position == -1) {
                showAlert("Value Not Found", "Value " + value + " not found in the list.");
                return;
            }

            isOperationRunning = true;
            updateControlStates();

            // Remove from model
            boolean deleted = listModel.delete(value);
            if (deleted) {
                // Get visual node
                NodeElement nodeToDelete = visualNodes.get(position);

                // Animate deletion
                animateDelete(nodeToDelete, position, () -> {
                    visualNodes.remove(position);
                    updateNodePositions();
                    isOperationRunning = false;
                    updateControlStates();
                    updateDisplay();
                });

                inputField.clear();
                logOperation("DELETE " + value + " | Size: " + listModel.size());
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void deleteAtPosition() {
        if (isOperationRunning)
            return;

        String posInput = positionField.getText().trim();
        if (posInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter a position to delete.");
            return;
        }

        try {
            int position = Integer.parseInt(posInput);

            if (listModel.isEmpty()) {
                showAlert("List Empty", "Cannot delete from an empty list.");
                return;
            }

            if (position < 0 || position >= listModel.size()) {
                showAlert("Invalid Position", "Position must be between 0 and " + (listModel.size() - 1));
                return;
            }

            isOperationRunning = true;
            updateControlStates();

            // Get value for logging
            int value = visualNodes.get(position).getValue();

            // Remove from model
            boolean deleted = listModel.deleteAtPosition(position);
            if (deleted) {
                // Get visual node
                NodeElement nodeToDelete = visualNodes.get(position);

                // Animate deletion
                animateDelete(nodeToDelete, position, () -> {
                    visualNodes.remove(position);
                    updateNodePositions();
                    isOperationRunning = false;
                    updateControlStates();
                    updateDisplay();
                });

                positionField.clear();
                logOperation("DELETE_POS " + value + " at " + position + " | Size: " + listModel.size());
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void searchValue() {

        String input = searchField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a value to search.");
            return;
        }

        try {
            int value = Integer.parseInt(input);

            if (listModel.isEmpty()) {
                resultLabel.setText("❌ List is empty");
                resultLabel.setStyle("-fx-text-fill: #ef4444;");
                return;
            }

            // Animate search process
            animateSearch(value, (position) -> {
                if (position != -1) {
                    resultLabel.setText("✅ Found " + value + " at position " + position);
                    resultLabel.setStyle("-fx-text-fill: #10b981;");
                    highlightNode(position);
                } else {
                    resultLabel.setText("❌ Value " + value + " not found");
                    resultLabel.setStyle("-fx-text-fill: #ef4444;");
                }

                logOperation(
                        "SEARCH " + value + " | Result: " + (position != -1 ? "Found at " + position : "Not found"));
            });

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer.");
        }
    }

    @FXML
    private void clearList() {
        if (isOperationRunning)
            return;

        if (listModel.isEmpty()) {
            showAlert("List Empty", "List is already empty.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear List");
        confirmAlert.setHeaderText("Confirm Clear Operation");
        confirmAlert.setContentText("Are you sure you want to clear all " + listModel.size() + " elements?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            isOperationRunning = true;
            updateControlStates();

            animateClear(() -> {
                listModel.clear();
                visualNodes.clear();
                listVisualization.getChildren().clear();
                isOperationRunning = false;
                updateControlStates();
                updateDisplay();
                logOperation("CLEAR_ALL | List emptied");
            });
        }
    }

    private NodeElement createNodeElement(int value, int position) {
        VBox nodeContainer = new VBox();
        nodeContainer.setAlignment(javafx.geometry.Pos.CENTER);
        nodeContainer.setSpacing(5);
        nodeContainer.getStyleClass().add("list-node");

        // Main node circle
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.getStyleClass().add("node-value");
        valueLabel.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-font-size: 16px; -fx-min-width: 50; -fx-min-height: 50; " +
                "-fx-background-radius: 25; -fx-alignment: center;");

        // Position label
        Label posLabel = new Label("pos: " + position);
        posLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");

        // Arrow (if not the last node)
        Label arrowLabel = new Label("→");
        arrowLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        nodeContainer.getChildren().addAll(valueLabel, posLabel);

        return new NodeElement(nodeContainer, valueLabel, arrowLabel, value, position);
    }

    private void updateVisualization() {
        Platform.runLater(() -> {
            listVisualization.getChildren().clear();

            for (int i = 0; i < visualNodes.size(); i++) {
                NodeElement nodeElement = visualNodes.get(i);
                listVisualization.getChildren().add(nodeElement.getContainer());

                // Add arrow if not the last node
                if (i < visualNodes.size() - 1) {
                    listVisualization.getChildren().add(nodeElement.getArrow());
                }
            }

            // Add null terminator
            if (!visualNodes.isEmpty()) {
                Label nullLabel = new Label("null");
                nullLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #94a3b8; -fx-font-style: italic;");
                listVisualization.getChildren().add(nullLabel);
            }
        });
    }

    private void animateInsertAtBeginning(NodeElement nodeElement, Runnable onComplete) {
        VBox container = nodeElement.getContainer();
        container.setTranslateY(-100);
        container.setOpacity(0);
        container.setScaleX(0.5);
        container.setScaleY(0.5);

        listVisualization.getChildren().add(0, container);

        ParallelTransition insertAnim = new ParallelTransition();

        TranslateTransition slideDown = animationService.createTranslateTransition(container, 0, 0, 600);
        FadeTransition fadeIn = animationService.createFadeAnimation(container, 0, 1, 600);
        ScaleTransition scaleUp = animationService.createScaleTransition(container, 1.0, 1.0, 600);

        insertAnim.getChildren().addAll(slideDown, fadeIn, scaleUp);
        insertAnim.setOnFinished(e -> {
            updateVisualization();
            onComplete.run();
        });
        insertAnim.play();
    }

    private void animateInsertAtEnd(NodeElement nodeElement, Runnable onComplete) {
        VBox container = nodeElement.getContainer();
        container.setTranslateX(200);
        container.setOpacity(0);
        container.setScaleX(0.5);
        container.setScaleY(0.5);

        listVisualization.getChildren().add(container);

        ParallelTransition insertAnim = new ParallelTransition();

        TranslateTransition slideIn = animationService.createTranslateTransition(container, 0, 0, 600);
        FadeTransition fadeIn = animationService.createFadeAnimation(container, 0, 1, 600);
        ScaleTransition scaleUp = animationService.createScaleTransition(container, 1.0, 1.0, 600);

        insertAnim.getChildren().addAll(slideIn, fadeIn, scaleUp);
        insertAnim.setOnFinished(e -> {
            updateVisualization();
            onComplete.run();
        });
        insertAnim.play();
    }

    private void animateInsertAtPosition(NodeElement nodeElement, int position, Runnable onComplete) {
        VBox container = nodeElement.getContainer();
        container.setTranslateY(-100);
        container.setOpacity(0);
        container.setScaleX(0.5);
        container.setScaleY(0.5);

        // Insert at correct position in visualization
        int visualIndex = position * 2; // Account for arrows
        if (visualIndex < listVisualization.getChildren().size()) {
            listVisualization.getChildren().add(visualIndex, container);
        } else {
            listVisualization.getChildren().add(container);
        }

        ParallelTransition insertAnim = new ParallelTransition();

        TranslateTransition slideDown = animationService.createTranslateTransition(container, 0, 0, 600);
        FadeTransition fadeIn = animationService.createFadeAnimation(container, 0, 1, 600);
        ScaleTransition scaleUp = animationService.createScaleTransition(container, 1.0, 1.0, 600);

        insertAnim.getChildren().addAll(slideDown, fadeIn, scaleUp);
        insertAnim.setOnFinished(e -> {
            updateVisualization();
            onComplete.run();
        });
        insertAnim.play();
    }

    private void animateDelete(NodeElement nodeElement, int position, Runnable onComplete) {
        VBox container = nodeElement.getContainer();

        ParallelTransition deleteAnim = new ParallelTransition();

        ScaleTransition shrink = animationService.createScaleTransition(container, 0, 0, 500);
        FadeTransition fadeOut = animationService.createFadeAnimation(container, 1, 0, 500);
        TranslateTransition moveUp = animationService.createTranslateTransition(container, 0, -100, 500);
        RotateTransition rotate = new RotateTransition(Duration.millis(500), container);
        rotate.setByAngle(360);

        deleteAnim.getChildren().addAll(shrink, fadeOut, moveUp, rotate);
        deleteAnim.setOnFinished(e -> {
            listVisualization.getChildren().remove(container);
            onComplete.run();
        });
        deleteAnim.play();
    }

    private void animateSearch(int value, SearchCallback callback) {
        Timeline searchAnim = new Timeline();
        int position = listModel.search(value);

        // Animate through each node
        for (int i = 0; i < visualNodes.size(); i++) {
            final int index = i;
            KeyFrame searchFrame = new KeyFrame(
                    Duration.millis((i + 1) * 300),
                    e -> {
                        highlightSearchNode(index);
                        if (index == visualNodes.size() - 1 || (position != -1 && index == position)) {
                            Platform.runLater(() -> callback.onSearchComplete(position));
                        }
                    });
            searchAnim.getKeyFrames().add(searchFrame);

            if (position != -1 && i == position) {
                break;
            }
        }

        searchAnim.play();
    }

    private void animateClear(Runnable onComplete) {
        if (visualNodes.isEmpty()) {
            onComplete.run();
            return;
        }

        ParallelTransition clearAnim = new ParallelTransition();

        for (int i = 0; i < visualNodes.size(); i++) {
            VBox container = visualNodes.get(i).getContainer();

            TranslateTransition moveOut = animationService.createTranslateTransition(container, -300, 0, 600);
            moveOut.setDelay(Duration.millis(i * 100));

            FadeTransition fadeOut = animationService.createFadeAnimation(container, 1, 0, 600);
            fadeOut.setDelay(Duration.millis(i * 100));

            RotateTransition rotate = new RotateTransition(Duration.millis(600), container);
            rotate.setByAngle(720);
            rotate.setDelay(Duration.millis(i * 100));

            ParallelTransition nodeAnim = new ParallelTransition(moveOut, fadeOut, rotate);
            clearAnim.getChildren().add(nodeAnim);
        }

        clearAnim.setOnFinished(e -> onComplete.run());
        clearAnim.play();
    }

    private void updateNodePositions() {
        for (int i = 0; i < visualNodes.size(); i++) {
            NodeElement node = visualNodes.get(i);
            node.setPosition(i);

            // Update position label
            Label posLabel = (Label) ((VBox) node.getContainer()).getChildren().get(1);
            posLabel.setText("pos: " + i);
        }
    }

    private int findNodePosition(int value) {
        for (int i = 0; i < visualNodes.size(); i++) {
            if (visualNodes.get(i).getValue() == value) {
                return i;
            }
        }
        return -1;
    }

    private void highlightNode(int position) {
        if (position >= 0 && position < visualNodes.size()) {
            VBox container = visualNodes.get(position).getContainer();
            Label valueLabel = visualNodes.get(position).getValueLabel();

            Timeline highlight = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(valueLabel.styleProperty(),
                            "-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-min-width: 50; -fx-min-height: 50; " +
                                    "-fx-background-radius: 25; -fx-alignment: center;")),
                    new KeyFrame(Duration.millis(1000), new KeyValue(valueLabel.styleProperty(),
                            "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-min-width: 50; -fx-min-height: 50; " +
                                    "-fx-background-radius: 25; -fx-alignment: center;")));

            ScaleTransition pulse = animationService.createScaleTransition(container, 1.2, 1.2, 300);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);

            ParallelTransition highlightAnim = new ParallelTransition(highlight, pulse);
            highlightAnim.play();
        }
    }

    private void highlightSearchNode(int position) {
        if (position >= 0 && position < visualNodes.size()) {
            Label valueLabel = visualNodes.get(position).getValueLabel();

            Timeline searchHighlight = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(valueLabel.styleProperty(),
                            "-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-min-width: 50; -fx-min-height: 50; " +
                                    "-fx-background-radius: 25; -fx-alignment: center;")),
                    new KeyFrame(Duration.millis(500), new KeyValue(valueLabel.styleProperty(),
                            "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; " +
                                    "-fx-font-size: 16px; -fx-min-width: 50; -fx-min-height: 50; " +
                                    "-fx-background-radius: 25; -fx-alignment: center;")));

            searchHighlight.play();
        }
    }

    private void updateDisplay() {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(listModel.isEmpty() ? "LinkedList is Empty"
                        : "LinkedList has " + listModel.size() + " elements");
            }

            if (sizeLabel != null) {
                sizeLabel.setText("Size: " + listModel.size());
            }

            updateVisualization();
            updateControlStates();
        });
    }

    private void updateControlStates() {
        boolean isEmpty = listModel.isEmpty();
        boolean isRunning = isOperationRunning;

        if (insertBeginButton != null)
            insertBeginButton.setDisable(isRunning);
        if (insertEndButton != null)
            insertEndButton.setDisable(isRunning);
        if (insertPositionButton != null)
            insertPositionButton.setDisable(isRunning);
        if (deleteValueButton != null)
            deleteValueButton.setDisable(isEmpty || isRunning);
        if (deletePositionButton != null)
            deletePositionButton.setDisable(isEmpty || isRunning);
        if (searchButton != null)
            searchButton.setDisable(isEmpty || isRunning);
        if (clearButton != null)
            clearButton.setDisable(isEmpty || isRunning);
        if (inputField != null)
            inputField.setDisable(isRunning);
        if (positionField != null)
            positionField.setDisable(isRunning);
        if (searchField != null)
            searchField.setDisable(isRunning);
    }

    public void logOperation(String operation) {
        if (operationHistory != null) {
            operationHistory.appendText(operation + "\n");
            operationHistory.setScrollTop(Double.MAX_VALUE);
        }
        System.out.println("LinkedList Operation: " + operation);
    }

    @FXML
    private void goBack() {
        try {
            if (currentAnimation != null) {
                currentAnimation.stop();
            }

            Stage currentStage = (Stage) listVisualization.getScene().getWindow();
            // ✅ Use hide() instead of close() for back navigation
            currentStage.hide();
            System.out.println("LinkedList module hidden (can be reopened)");

        } catch (Exception e) {
            System.err.println("Error hiding LinkedList window: " + e.getMessage());
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

    // Callback interface for search
    @FunctionalInterface
    private interface SearchCallback {
        void onSearchComplete(int position);
    }

    // Inner class for visual node elements
    private static class NodeElement {
        private final VBox container;
        private final Label valueLabel;
        private final Label arrow;
        private final int value;
        private int position;

        public NodeElement(VBox container, Label valueLabel, Label arrow, int value, int position) {
            this.container = container;
            this.valueLabel = valueLabel;
            this.arrow = arrow;
            this.value = value;
            this.position = position;
        }

        public VBox getContainer() {
            return container;
        }

        public Label getValueLabel() {
            return valueLabel;
        }

        public Label getArrow() {
            return arrow;
        }

        public int getValue() {
            return value;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
