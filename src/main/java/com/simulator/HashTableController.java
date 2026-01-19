package com.simulator;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Hash Table visualization module.
 * Visualizes key-value operations with collision handling.
 */
public class HashTableController {
    private static final Logger logger = LoggerFactory.getLogger(HashTableController.class);

    @FXML
    private TextField keyField;
    @FXML
    private TextField valueField;
    @FXML
    private Button insertButton, searchButton, deleteButton, clearButton;
    @FXML
    private Label statusLabel, sizeLabel, loadFactorLabel, collisionLabel, capacityLabel;
    @FXML
    private HBox bucketsContainer;
    @FXML
    private VBox visualizationPane;
    @FXML
    private TextArea operationHistory;
    @FXML
    private Label hashResultLabel;
    @FXML
    private Button codesButton;

    private HashTableModel hashTable;
    private List<VBox> bucketVisuals;
    private AnimationService animationService;
    private static final int BUCKET_WIDTH = 100;

    @FXML
    public void initialize() {
        System.out.println("HashTableController initialized");

        hashTable = new HashTableModel(8); // 8 buckets
        bucketVisuals = new ArrayList<>();
        animationService = AnimationService.getInstance();

        setupComponents();
        setupEventHandlers();
        createBucketVisualization();
        updateDisplay();

        System.out.println("Hash Table module ready");
    }

    private void setupComponents() {
        // Input field validation
        keyField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 15) {
                keyField.setText(newText.substring(0, 15));
            }
        });

        valueField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 15) {
                valueField.setText(newText.substring(0, 15));
            }
        });

        keyField.setPromptText("Enter key");
        valueField.setPromptText("Enter value");

        // Initialize operation history
        if (operationHistory != null) {
            operationHistory.setText("=== Hash Table Operations History ===\n");
            operationHistory.setEditable(false);
        }
    }

    private void setupEventHandlers() {
        // Enable search with just key
        keyField.setOnAction(e -> {
            if (!keyField.getText().trim().isEmpty() && !valueField.getText().trim().isEmpty()) {
                insertEntry();
            }
        });

        // Live hash preview
        keyField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.trim().isEmpty() && hashResultLabel != null) {
                int hash = hashTable.hash(newVal.trim());
                hashResultLabel.setText("Hash(" + newVal.trim() + ") = " + hash);
            } else if (hashResultLabel != null) {
                hashResultLabel.setText("Enter a key to see hash");
            }
        });
    }

    private void createBucketVisualization() {
        bucketsContainer.getChildren().clear();
        bucketVisuals.clear();

        for (int i = 0; i < hashTable.getCapacity(); i++) {
            VBox bucketBox = createBucketVisual(i);
            bucketVisuals.add(bucketBox);
            bucketsContainer.getChildren().add(bucketBox);
        }
    }

    private VBox createBucketVisual(int index) {
        VBox bucket = new VBox(5);
        bucket.setAlignment(Pos.TOP_CENTER);
        bucket.getStyleClass().add("hashtable-bucket");
        bucket.setStyle("-fx-background-color: derive(-fx-surface, -5%); -fx-border-color: -fx-border; " +
                "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;");
        bucket.setPrefWidth(BUCKET_WIDTH);
        bucket.setMinHeight(120);

        // Bucket index label
        Label indexLabel = new Label("[" + index + "]");
        indexLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ec4899;");

        bucket.getChildren().add(indexLabel);
        return bucket;
    }

    @FXML
    private void openCodesPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/hashtable-codes.fxml"));
            Parent root = loader.load();
            HashTableCodeController controller = loader.getController();

            Stage popupStage = new Stage();
            popupStage.initOwner(bucketsContainer.getScene().getWindow());
            popupStage.initModality(Modality.NONE);
            popupStage.setTitle("Hash Table Code Examples");

            Scene scene = new Scene(root, 1200, 800);

            // Apply theme
            boolean isDarkMode = ThemeManager.getInstance().isDarkMode();
            scene.getStylesheets().clear();
            scene.getStylesheets().addAll(bucketsContainer.getScene().getStylesheets());

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

            Stage parentStage = (Stage) bucketsContainer.getScene().getWindow();
            popupStage.setX(parentStage.getX() + 50);
            popupStage.setY(parentStage.getY() + 50);

            controller.setParentController(this);
            popupStage.setOnCloseRequest(e -> controller.cleanup());

            popupStage.show();
            logOperation("HASHTABLE_CODES_POPUP_OPENED | Hash Table code examples displayed");

        } catch (Exception e) {
            logger.error("Could not open code examples", e);
            AlertHelper.showWarning("Error", "Could not open code examples: " + e.getMessage());
        }
    }

    @FXML
    private void insertEntry() {
        String key = keyField.getText().trim();
        String value = valueField.getText().trim();

        if (key.isEmpty() || value.isEmpty()) {
            AlertHelper.showWarning("Invalid Input", "Please enter both key and value.");
            return;
        }

        boolean isUpdate = hashTable.containsKey(key);
        int bucketIndex = hashTable.hash(key);

        // Insert into model
        hashTable.put(key, value);

        // Animate insertion
        animateInsertion(bucketIndex, key, value, isUpdate);

        // Clear fields and update display
        keyField.clear();
        valueField.clear();
        updateDisplay();

        String action = isUpdate ? "UPDATED" : "INSERTED";
        logOperation(action + " " + key + "=" + value + " | Bucket: " + bucketIndex);
    }

    @FXML
    private void searchEntry() {
        String key = keyField.getText().trim();

        if (key.isEmpty()) {
            AlertHelper.showWarning("Invalid Input", "Please enter a key to search.");
            return;
        }

        int bucketIndex = hashTable.hash(key);
        String value = hashTable.get(key);

        // Animate search
        animateSearch(bucketIndex, key, value != null);

        if (value != null) {
            AlertHelper.showInfo("Search Result", "Found: " + key + " = " + value);
            logOperation("SEARCH_FOUND " + key + "=" + value + " | Bucket: " + bucketIndex);
        } else {
            AlertHelper.showWarning("Not Found", "Key '" + key + "' not found in hash table.");
            logOperation("SEARCH_NOT_FOUND " + key + " | Bucket: " + bucketIndex);
        }
    }

    @FXML
    private void deleteEntry() {
        String key = keyField.getText().trim();

        if (key.isEmpty()) {
            AlertHelper.showWarning("Invalid Input", "Please enter a key to delete.");
            return;
        }

        int bucketIndex = hashTable.hash(key);
        boolean removed = hashTable.remove(key);

        if (removed) {
            // Animate deletion
            animateDeletion(bucketIndex, key);
            keyField.clear();
            updateDisplay();
            rebuildBucketVisual(bucketIndex);
            AlertHelper.showInfo("Deleted", "Key '" + key + "' removed from hash table.");
            logOperation("DELETED " + key + " | Bucket: " + bucketIndex);
        } else {
            AlertHelper.showWarning("Not Found", "Key '" + key + "' not found in hash table.");
            logOperation("DELETE_NOT_FOUND " + key + " | Bucket: " + bucketIndex);
        }
    }

    @FXML
    private void clearTable() {
        if (hashTable.isEmpty()) {
            AlertHelper.showWarning("Empty Table", "Hash table is already empty.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Hash Table");
        confirmAlert.setHeaderText("Confirm Clear Operation");
        confirmAlert.setContentText("Are you sure you want to clear all " + hashTable.size() + " entries?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            animateClear(() -> {
                hashTable.clear();
                createBucketVisualization();
                updateDisplay();
                logOperation("CLEAR ALL | Hash table emptied");
            });
        }
    }

    private void animateInsertion(int bucketIndex, String key, String value, boolean isUpdate) {
        VBox bucket = bucketVisuals.get(bucketIndex);

        // Create entry visual
        VBox entryBox = createEntryVisual(key, value);
        entryBox.setOpacity(0);
        entryBox.setScaleX(0.3);
        entryBox.setScaleY(0.3);

        if (!isUpdate) {
            bucket.getChildren().add(entryBox);
        }

        // Animate bucket highlight
        animateBucketHighlight(bucket, "#ec4899");

        // Fade in the entry
        ParallelTransition insertAnim = new ParallelTransition();

        FadeTransition fade = animationService.createFadeAnimation(entryBox, 0, 1, 500);
        ScaleTransition scale = animationService.createScaleTransition(entryBox, 1.1, 1.1, 400);
        ScaleTransition scaleBack = animationService.createScaleTransition(entryBox, 1.0, 1.0, 200);
        scaleBack.setDelay(Duration.millis(400));

        insertAnim.getChildren().addAll(fade, scale, scaleBack);
        insertAnim.play();
    }

    private void animateSearch(int bucketIndex, String key, boolean found) {
        VBox bucket = bucketVisuals.get(bucketIndex);

        // Highlight bucket during search
        String color = found ? "#10b981" : "#ef4444";
        animateBucketHighlight(bucket, color);

        // Find and highlight the entry if found
        if (found) {
            for (javafx.scene.Node node : bucket.getChildren()) {
                if (node instanceof VBox && node.getUserData() != null && node.getUserData().equals(key)) {
                    animateEntryHighlight((VBox) node, "#10b981");
                    break;
                }
            }
        }
    }

    private void animateDeletion(int bucketIndex, String key) {
        VBox bucket = bucketVisuals.get(bucketIndex);

        for (javafx.scene.Node node : bucket.getChildren()) {
            if (node instanceof VBox && node.getUserData() != null && node.getUserData().equals(key)) {
                FadeTransition fade = animationService.createFadeAnimation(node, 1, 0, 400);
                ScaleTransition scale = animationService.createScaleTransition(node, 0.3, 0.3, 400);

                ParallelTransition deleteAnim = new ParallelTransition(fade, scale);
                deleteAnim.setOnFinished(e -> bucket.getChildren().remove(node));
                deleteAnim.play();
                break;
            }
        }
    }

    private void animateClear(Runnable onComplete) {
        ParallelTransition clearAnim = new ParallelTransition();

        for (VBox bucket : bucketVisuals) {
            for (javafx.scene.Node node : bucket.getChildren()) {
                if (node instanceof VBox) {
                    FadeTransition fade = animationService.createFadeAnimation(node, 1, 0, 500);
                    RotateTransition rotate = new RotateTransition(Duration.millis(500), node);
                    rotate.setByAngle(180);
                    ParallelTransition nodeAnim = new ParallelTransition(fade, rotate);
                    clearAnim.getChildren().add(nodeAnim);
                }
            }
        }

        clearAnim.setOnFinished(e -> onComplete.run());
        clearAnim.play();
    }

    private void animateBucketHighlight(VBox bucket, String color) {
        Timeline colorAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(bucket.styleProperty(),
                        "-fx-background-color: derive(-fx-surface, -5%); -fx-border-color: " + color + "; " +
                                "-fx-border-width: 3; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;")),
                new KeyFrame(Duration.millis(800), new KeyValue(bucket.styleProperty(),
                        "-fx-background-color: derive(-fx-surface, -5%); -fx-border-color: -fx-border; " +
                                "-fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;")));
        colorAnim.play();
    }

    private void animateEntryHighlight(VBox entry, String color) {
        ScaleTransition pulse = animationService.createScaleTransition(entry, 1.2, 1.2, 200);
        ScaleTransition pulseBack = animationService.createScaleTransition(entry, 1.0, 1.0, 200);
        SequentialTransition seq = new SequentialTransition(pulse, pulseBack);
        seq.play();
    }

    private VBox createEntryVisual(String key, String value) {
        VBox entryBox = new VBox(2);
        entryBox.setAlignment(Pos.CENTER);
        entryBox.setUserData(key);
        entryBox.getStyleClass().add("hashtable-entry");
        entryBox.setStyle("-fx-background-color: #ec4899; -fx-background-radius: 8; " +
                "-fx-border-color: #db2777; -fx-border-width: 2; -fx-border-radius: 8; -fx-padding: 5;");
        entryBox.setPrefWidth(85);

        Label keyLabel = new Label(key);
        keyLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 11px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-text-fill: #fce7f3; -fx-font-size: 10px;");

        entryBox.getChildren().addAll(keyLabel, valueLabel);
        return entryBox;
    }

    private void rebuildBucketVisual(int bucketIndex) {
        VBox bucket = bucketVisuals.get(bucketIndex);
        bucket.getChildren().clear();

        // Add index label
        Label indexLabel = new Label("[" + bucketIndex + "]");
        indexLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #ec4899;");
        bucket.getChildren().add(indexLabel);

        // Add entries
        for (HashTableModel.Entry entry : hashTable.getBucket(bucketIndex)) {
            VBox entryBox = createEntryVisual(entry.getKey(), entry.getValue());
            bucket.getChildren().add(entryBox);
        }
    }

    private void updateDisplay() {
        if (statusLabel != null) {
            statusLabel.setText(
                    hashTable.isEmpty() ? "Hash Table is Empty" : "Hash Table has " + hashTable.size() + " entries");
        }

        if (sizeLabel != null) {
            sizeLabel.setText("Size: " + hashTable.size());
        }

        if (capacityLabel != null) {
            capacityLabel.setText("Buckets: " + hashTable.getCapacity());
        }

        if (loadFactorLabel != null) {
            loadFactorLabel.setText(String.format("Load Factor: %.2f", hashTable.getLoadFactor()));
        }

        if (collisionLabel != null) {
            collisionLabel.setText("Collisions: " + hashTable.getCollisionCount());
        }
    }

    public void logOperation(String operation) {
        if (operationHistory != null) {
            operationHistory.appendText(operation + "\n");
            operationHistory.setScrollTop(Double.MAX_VALUE);
        }
        System.out.println("HashTable Operation: " + operation);
    }

    @FXML
    private void goBack() {
        try {
            Stage currentStage = (Stage) bucketsContainer.getScene().getWindow();
            currentStage.hide();
            System.out.println("Hash Table module hidden");
        } catch (Exception e) {
            System.err.println("Error hiding Hash Table window: " + e.getMessage());
        }
    }

    // Removed showAlert and showInfo - using AlertHelper instead
}
