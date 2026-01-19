package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.VBox;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GraphController {

    // ==================== FXML COMPONENTS ====================
    @FXML private TextField addVertexField, searchVertexField;
    @FXML private TextField sourceField, destinationField, weightField;
    @FXML private TextField startVertexField, endVertexField;
    @FXML private ComboBox<String> algorithmComboBox;
    @FXML private CheckBox directedCheckBox, weightedCheckBox, showLabelsCheckBox, showWeightsCheckBox;

    // Buttons
    @FXML private Button addVertexButton, removeVertexButton, searchButton, highlightButton;
    @FXML private Button addEdgeButton, removeEdgeButton;
    @FXML private Button traverseButton, clearButton, randomButton;
    @FXML private Button undoButton, redoButton, saveButton, loadButton;
    @FXML private Button zoomInButton, zoomOutButton, resetZoomButton;
    @FXML private Button circularLayoutButton, forceLayoutButton, resetLayoutButton;

    // Display components
    @FXML private Pane graphCanvas;
    @FXML private ScrollPane canvasScrollPane;
    @FXML private Label statusLabel, vertexCountLabel, edgeCountLabel, densityLabel;
    @FXML private TextArea resultArea, operationHistory, algorithmCodeViewer;
    @FXML private ProgressBar operationProgress;

    // ==================== PRIVATE FIELDS ====================
    private GraphModel graphModel;
    private Map<Integer, VertexElement> vertexElements;
    private List<EdgeElement> edgeElements;
    private AnimationService animationService;
    private Timeline currentAnimation;

    // State management
    private boolean isTraversing = false;
    private boolean isDragging = false;
    private double zoomLevel = 1.0;
    private final double MIN_ZOOM = 0.3;
    private final double MAX_ZOOM = 3.0;

    // Undo/Redo system
    private Stack<GraphState> undoStack;
    private Stack<GraphState> redoStack;

    // ==================== ENHANCED DRAG AND DROP FIELDS ====================
// Enhanced drag and drop with double-click detection
    private VertexElement draggedVertex = null;
    private double dragStartX, dragStartY;

    // Double-click detection system
    private long lastClickTime = 0;
    private VertexElement lastClickedVertex = null;
    private final long DOUBLE_CLICK_THRESHOLD = 500; // milliseconds
    private final long HOLD_THRESHOLD = 200; // milliseconds for hold detection

    // Enhanced drag states
    private boolean isEnhancedDragging = false; // Double-click drag mode
    private boolean isDragHoldActive = false;
    private Timeline dragHoldTimer = null;

    // Visual feedback for dragging
    private Timeline dragFeedbackAnimation = null;
    private String originalVertexStyle = "";

    // Canvas boundaries for drag constraints
    private final double BOUNDARY_PADDING = 50.0;


    // ==================== INITIALIZATION ====================
    @FXML
    public void initialize() {
        System.out.println("Enhanced GraphController initialized");

        graphModel = new GraphModel(false); // Start with undirected
        vertexElements = new HashMap<>();
        edgeElements = new ArrayList<>();
        animationService = AnimationService.getInstance();
        undoStack = new Stack<>();
        redoStack = new Stack<>();

        setupComponents();
        setupEventHandlers();
        setupDragAndDrop();

        // **FIX: Ensure canvas is properly sized and visible**
        Platform.runLater(() -> {
            setupCanvasSize();
           // addTestVisualization(); // Debug visualization
            updateDisplay();
            updateAlgorithmCode();
        });

        System.out.println("Enhanced Graph module ready with advanced features");

        // Add keyboard shortcut for copy (Ctrl+C)
        if (algorithmCodeViewer != null) {
            algorithmCodeViewer.setOnKeyPressed(event -> {
                if (event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.C) {
                    copyCodeToClipboard();
                    event.consume();
                }
            });
        }



    }

    private void setupCanvasSize() {
        if (graphCanvas != null) {
            // Set explicit sizes for reliable rendering
            graphCanvas.setMinSize(800, 600);
            graphCanvas.setPrefSize(800, 600);
            graphCanvas.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            System.out.println("Canvas size set: " + graphCanvas.getPrefWidth() + "x" + graphCanvas.getPrefHeight());
        }

        if (canvasScrollPane != null) {
            canvasScrollPane.setFitToWidth(false);
            canvasScrollPane.setFitToHeight(false);
            canvasScrollPane.setPannable(true);
        }
    }

    private void addTestVisualization() {
        // **DEBUG: Add test circle to verify canvas works**
        Circle testCircle = new Circle(100, 100, 15);
        testCircle.setFill(Color.RED);
        testCircle.setStroke(Color.BLACK);
        testCircle.setStrokeWidth(2);

        Text testLabel = new Text(90, 105, "TEST");
        testLabel.setFill(Color.WHITE);
        testLabel.setStyle("-fx-font-weight: bold;");

        graphCanvas.getChildren().addAll(testCircle, testLabel);
        System.out.println("Test visualization added - Canvas children count: " + graphCanvas.getChildren().size());
    }

    private void setupComponents() {
        // Enhanced algorithm selection
        algorithmComboBox.setItems(FXCollections.observableArrayList(
                "BFS (Breadth-First Search)",
                "DFS (Depth-First Search)",
                "Shortest Path",
                "Dijkstra's Algorithm",
                "A* Pathfinding",
                "Topological Sort",
                "Find All Paths",
                "Detect Cycles"
        ));
        algorithmComboBox.setValue("BFS (Breadth-First Search)");

        // Input validation
        setupInputValidation();

        // Canvas scroll and zoom
        if (canvasScrollPane != null) {
            canvasScrollPane.setPannable(true);
            canvasScrollPane.setOnScroll(event -> {
                if (event.isControlDown()) {
                    double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
                    adjustZoom(zoomFactor);
                    event.consume();
                }
            });
        }

        // Setup text areas
        if (operationHistory != null) {
            operationHistory.setText("=== Enhanced Graph Operations History ===\n");
            operationHistory.setEditable(false);
        }

        if (resultArea != null) {
            resultArea.setEditable(false);
        }

        if (algorithmCodeViewer != null) {
            algorithmCodeViewer.setStyle("-fx-font-family: 'Courier New', monospace; -fx-font-size: 12px;");
            algorithmCodeViewer.setEditable(false);
        }
    }

    private void setupInputValidation() {
        // Vertex ID validation (integers only)
        addVertexField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                addVertexField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        searchVertexField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                searchVertexField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        // Edge fields validation
        sourceField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                sourceField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        destinationField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                destinationField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        // Weight validation (decimal numbers)
        weightField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*\\.?\\d*")) {
                weightField.setText(oldText);
            }
        });

        // Traversal fields validation
        startVertexField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                startVertexField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });

        endVertexField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                endVertexField.setText(newText.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void setupEventHandlers() {
        // Enter key handling for quick operations
        addVertexField.setOnAction(e -> addVertex());
        sourceField.setOnAction(e -> addEdge());
        destinationField.setOnAction(e -> addEdge());
        startVertexField.setOnAction(e -> performTraversal());
        searchVertexField.setOnAction(e -> searchVertex());

        // Checkbox handlers
        directedCheckBox.setOnAction(e -> toggleGraphType());
        weightedCheckBox.setOnAction(e -> toggleWeightSupport());
        showLabelsCheckBox.setOnAction(e -> toggleLabelsVisibility());
        showWeightsCheckBox.setOnAction(e -> toggleWeightsVisibility());

        // Algorithm selection handler
        algorithmComboBox.setOnAction(e -> updateAlgorithmCode());
    }
    /**
     * Enhanced drag and drop setup with double-click detection and real-time edge updates
     */
    private void setupDragAndDrop() {
        // Clear any existing handlers
        graphCanvas.setOnMousePressed(null);
        graphCanvas.setOnMouseDragged(null);
        graphCanvas.setOnMouseReleased(null);

        // Set up enhanced mouse handlers
        graphCanvas.setOnMousePressed(this::handleEnhancedMousePressed);
        graphCanvas.setOnMouseDragged(this::handleEnhancedMouseDragged);
        graphCanvas.setOnMouseReleased(this::handleEnhancedMouseReleased);

        System.out.println("✅ Enhanced drag and drop system initialized");
    }


    /**
     * Enhanced mouse pressed handler with double-click detection
     */
    private void handleEnhancedMousePressed(MouseEvent event) {
        if (isTraversing) return;

        long currentTime = System.currentTimeMillis();
        VertexElement clickedVertex = findVertexAtPosition(event.getX(), event.getY());

        if (clickedVertex != null) {
            // Check for double-click
            boolean isDoubleClick = (currentTime - lastClickTime) < DOUBLE_CLICK_THRESHOLD
                    && clickedVertex == lastClickedVertex;

            if (isDoubleClick) {
                // Start enhanced drag mode
                startEnhancedDrag(clickedVertex, event);
                logOperation("ENHANCED_DRAG_START vertex " + clickedVertex.getId() + " | Double-click detected");
            } else {
                // Start hold detection for potential enhanced drag
                startHoldDetection(clickedVertex, event);
            }

            lastClickTime = currentTime;
            lastClickedVertex = clickedVertex;
        } else {
            // Clear selection if clicking on empty space
            clearDragState();
        }
    }

    /**
     * Enhanced mouse dragged handler with real-time edge updates
     */
    private void handleEnhancedMouseDragged(MouseEvent event) {
        if (draggedVertex == null) return;

        // Calculate new position with boundary constraints
        double newX = constrainToCanvasBounds(event.getX(), true);
        double newY = constrainToCanvasBounds(event.getY(), false);

        // Update vertex position
        draggedVertex.setPosition(newX, newY);

        // Real-time edge updates during drag
        updateEdgesForVertexRealtime(draggedVertex.getId());

        // Update drag feedback visual effects
        updateDragFeedback(draggedVertex);

        // Update status with current position
        if (statusLabel != null && isEnhancedDragging) {
            statusLabel.setText("🎯 Dragging vertex " + draggedVertex.getId() +
                    " to (" + String.format("%.0f", newX) + ", " + String.format("%.0f", newY) + ")");
        }
    }

    /**
     * Enhanced mouse released handler
     */
    private void handleEnhancedMouseReleased(MouseEvent event) {
        if (draggedVertex != null) {
            // Finalize drag operation
            finalizeDrag(draggedVertex);
            logOperation("ENHANCED_DRAG_END vertex " + draggedVertex.getId() +
                    " | Final position: (" + String.format("%.1f", draggedVertex.getX()) +
                    ", " + String.format("%.1f", draggedVertex.getY()) + ")");
        }

        // Clear all drag states
        clearDragState();
    }

    /**
     * Find vertex at the given position
     */
    private VertexElement findVertexAtPosition(double x, double y) {
        for (VertexElement vertex : vertexElements.values()) {
            if (vertex.contains(x, y)) {
                return vertex;
            }
        }
        return null;
    }

    /**
     * Start hold detection timer for enhanced drag
     */
    private void startHoldDetection(VertexElement vertex, MouseEvent event) {
        draggedVertex = vertex;
        dragStartX = event.getX();
        dragStartY = event.getY();

        // Start hold timer
        if (dragHoldTimer != null) {
            dragHoldTimer.stop();
        }

        dragHoldTimer = new Timeline(new KeyFrame(Duration.millis(HOLD_THRESHOLD), e -> {
            if (draggedVertex == vertex && !isDragging) {
                startEnhancedDrag(vertex, event);
            }
        }));
        dragHoldTimer.play();
    }

    /**
     * Start enhanced drag mode with visual feedback
     */
    private void startEnhancedDrag(VertexElement vertex, MouseEvent event) {
        if (vertex == null) return;

        isEnhancedDragging = true;
        isDragging = true;
        isDragHoldActive = true;
        draggedVertex = vertex;
        dragStartX = event.getX();
        dragStartY = event.getY();

        // Stop hold timer if running
        if (dragHoldTimer != null) {
            dragHoldTimer.stop();
        }

        // Apply visual feedback
        applyDragVisualFeedback(vertex);

        // Update status
        if (statusLabel != null) {
            statusLabel.setText("🎯 Enhanced drag mode: Vertex " + vertex.getId());
        }

        System.out.println("🎯 Enhanced drag started for vertex " + vertex.getId());
    }

    /**
     * Apply visual feedback during drag
     */
    private void applyDragVisualFeedback(VertexElement vertex) {
        Circle circle = vertex.getCircle();

        // Store original style
        originalVertexStyle = circle.getStyle();

        // Apply enhanced drag styling
        String dragStyle = "-fx-fill: #10b981; -fx-stroke: #059669; -fx-stroke-width: 4; " +
                "-fx-effect: dropshadow(gaussian, #10b981, 15, 0.8, 0, 0);";
        circle.setStyle(dragStyle);

        // Create pulsing animation
        ScaleTransition pulseAnimation = new ScaleTransition(Duration.millis(800), circle);
        pulseAnimation.setFromX(1.0);
        pulseAnimation.setFromY(1.0);
        pulseAnimation.setToX(1.15);
        pulseAnimation.setToY(1.15);
        pulseAnimation.setAutoReverse(true);
        pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        pulseAnimation.setInterpolator(Interpolator.EASE_BOTH);

        // Store animation reference
        dragFeedbackAnimation = new Timeline();
        pulseAnimation.play();
        vertex.setHighlightAnimation(pulseAnimation);
    }

    /**
     * Update drag feedback during movement
     */
    private void updateDragFeedback(VertexElement vertex) {
        // Optional: Add trail effect or position indicator
        // This could include temporary visual elements showing the drag path
    }

    /**
     * Constrain position to canvas bounds
     */
    private double constrainToCanvasBounds(double position, boolean isX) {
        if (isX) {
            double maxX = graphCanvas.getPrefWidth() - BOUNDARY_PADDING;
            return Math.max(BOUNDARY_PADDING, Math.min(position, maxX));
        } else {
            double maxY = graphCanvas.getPrefHeight() - BOUNDARY_PADDING;
            return Math.max(BOUNDARY_PADDING, Math.min(position, maxY));
        }
    }

    /**
     * Real-time edge updates during drag (optimized for performance)
     */
    private void updateEdgesForVertexRealtime(int vertexId) {
        VertexElement vertex = vertexElements.get(vertexId);
        if (vertex == null) return;

        double x = vertex.getX();
        double y = vertex.getY();

        // Update connected edges in real-time
        for (EdgeElement edge : edgeElements) {
            if (edge.getSource() == vertexId) {
                edge.getLine().setStartX(x);
                edge.getLine().setStartY(y);
            }

            if (edge.getDestination() == vertexId) {
                edge.getLine().setEndX(x);
                edge.getLine().setEndY(y);
            }

            // Update weight label position if exists
            if ((edge.getSource() == vertexId || edge.getDestination() == vertexId)
                    && edge.getWeightLabel() != null) {
                Line line = edge.getLine();
                double midX = (line.getStartX() + line.getEndX()) / 2;
                double midY = (line.getStartY() + line.getEndY()) / 2;
                edge.getWeightLabel().setX(midX);
                edge.getWeightLabel().setY(midY);
            }
        }
    }

    /**
     * Finalize drag operation with smooth animation
     */
    private void finalizeDrag(VertexElement vertex) {
        if (vertex == null) return;

        Circle circle = vertex.getCircle();

        // Stop any running animations
        if (vertex.getHighlightAnimation() != null) {
            vertex.getHighlightAnimation().stop();
            vertex.setHighlightAnimation(null);
        }

        // Smooth transition back to normal appearance
        Timeline finalizeAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(circle.styleProperty(), circle.getStyle())),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(circle.styleProperty(), originalVertexStyle, Interpolator.EASE_OUT))
        );

        // Add a final "settle" effect
        ScaleTransition settleEffect = new ScaleTransition(Duration.millis(200), circle);
        settleEffect.setFromX(1.15);
        settleEffect.setFromY(1.15);
        settleEffect.setToX(1.0);
        settleEffect.setToY(1.0);
        settleEffect.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition finalTransition = new ParallelTransition(finalizeAnimation, settleEffect);
        finalTransition.play();

        // Update final display state
        updateDisplay();

        System.out.println("✅ Drag finalized for vertex " + vertex.getId());
    }

    /**
     * Clear all drag-related states
     */
    private void clearDragState() {
        // Stop any running timers
        if (dragHoldTimer != null) {
            dragHoldTimer.stop();
            dragHoldTimer = null;
        }

        if (dragFeedbackAnimation != null) {
            dragFeedbackAnimation.stop();
            dragFeedbackAnimation = null;
        }

        // Reset states
        isDragging = false;
        isEnhancedDragging = false;
        isDragHoldActive = false;
        draggedVertex = null;
        lastClickedVertex = null;

        // Reset status
        if (statusLabel != null && !statusLabel.getText().contains("vertices") && !statusLabel.getText().contains("Empty")) {
            updateDisplay(); // This will reset status to normal
        }
    }





    // ==================== GRAPH OPERATIONS ====================

    @FXML
    private void addVertex() {
        if (isTraversing) return;

        String input = addVertexField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a vertex ID.");
            return;
        }

        try {
            int vertexId = Integer.parseInt(input);
            if (graphModel.containsVertex(vertexId)) {
                showAlert("Vertex Exists", "Vertex " + vertexId + " already exists in the graph.");
                return;
            }

            // Save state for undo
            saveState("ADD_VERTEX_" + vertexId);

            // Add to model
            graphModel.addVertex(vertexId);

            // Create visual element
            VertexElement vertexElement = createVertexElement(vertexId);
            vertexElements.put(vertexId, vertexElement);

            // **FIX: Add to canvas with animation safely**
            Platform.runLater(() -> {
                animateAddVertex(vertexElement);

                // **FIX: Apply layout after adding vertex**
                if (vertexElements.size() > 1) {
                    applyCircularLayout();
                }

                updateDisplay();
                System.out.println("Added vertex " + vertexId + " - Total canvas children: " + graphCanvas.getChildren().size());
            });

            addVertexField.clear();
            logOperation("ADD_VERTEX " + vertexId + " | Total: " + graphModel.getVertexCount() + " vertices");

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for vertex ID.");
        }
    }

    @FXML
    private void removeVertex() {
        if (isTraversing) return;

        String input = addVertexField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a vertex ID to remove.");
            return;
        }

        try {
            int vertexId = Integer.parseInt(input);
            if (!graphModel.containsVertex(vertexId)) {
                showAlert("Vertex Not Found", "Vertex " + vertexId + " does not exist in the graph.");
                return;
            }

            // Save state for undo
            saveState("REMOVE_VERTEX_" + vertexId);

            // Remove from model
            graphModel.removeVertex(vertexId);

            // Remove visual elements
            VertexElement vertexElement = vertexElements.remove(vertexId);
            if (vertexElement != null) {
                animateRemoveVertex(vertexElement);
            }

            // Remove associated edges
            edgeElements.removeIf(edge ->
                    edge.getSource() == vertexId || edge.getDestination() == vertexId);

            addVertexField.clear();
            updateDisplay();
            redrawGraph();

            logOperation("REMOVE_VERTEX " + vertexId + " | Remaining: " + graphModel.getVertexCount() + " vertices");

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for vertex ID.");
        }
    }

    @FXML
    private void addEdge() {
        if (isTraversing) return;

        String sourceInput = sourceField.getText().trim();
        String destInput = destinationField.getText().trim();
        String weightInput = weightField.getText().trim();

        if (sourceInput.isEmpty() || destInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter both source and destination vertices.");
            return;
        }

        double weight = 1.0;
        if (weightedCheckBox.isSelected()) {
            if (weightInput.isEmpty()) {
                showAlert("Invalid Input", "Please enter edge weight for weighted graph.");
                return;
            }
            try {
                weight = Double.parseDouble(weightInput);
                if (weight <= 0) {
                    showAlert("Invalid Weight", "Weight must be positive.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid weight.");
                return;
            }
        }

        try {
            int source = Integer.parseInt(sourceInput);
            int destination = Integer.parseInt(destInput);

            if (!graphModel.containsVertex(source)) {
                showAlert("Vertex Not Found", "Source vertex " + source + " does not exist.");
                return;
            }

            if (!graphModel.containsVertex(destination)) {
                showAlert("Vertex Not Found", "Destination vertex " + destination + " does not exist.");
                return;
            }

            if (graphModel.containsEdge(source, destination)) {
                showAlert("Edge Exists", "Edge from " + source + " to " + destination + " already exists.");
                return;
            }

            // Save state for undo
            saveState("ADD_EDGE_" + source + "_" + destination);

            // Add to model
            graphModel.addEdge(source, destination, weight);

            // **FIX: Create and add visual element safely**
            double finalWeight = weight;
            Platform.runLater(() -> {
                EdgeElement edgeElement = createEdgeElement(source, destination, finalWeight);
                if (edgeElement != null) {
                    edgeElements.add(edgeElement);
                    animateAddEdge(edgeElement);
                    updateAllEdges();
                    updateDisplay();
                    System.out.println("Added edge " + source + "->" + destination + " - Total canvas children: " + graphCanvas.getChildren().size());
                }
            });

            sourceField.clear();
            destinationField.clear();
            weightField.clear();

            logOperation("ADD_EDGE " + source + " -> " + destination +
                    (weightedCheckBox.isSelected() ? " (weight: " + weight + ")" : "") +
                    " | Total: " + graphModel.getEdgeCount() + " edges");

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for vertex IDs.");
        }
    }

    @FXML
    private void removeEdge() {
        if (isTraversing) return;

        String sourceInput = sourceField.getText().trim();
        String destInput = destinationField.getText().trim();

        if (sourceInput.isEmpty() || destInput.isEmpty()) {
            showAlert("Invalid Input", "Please enter both source and destination vertices.");
            return;
        }

        try {
            int source = Integer.parseInt(sourceInput);
            int destination = Integer.parseInt(destInput);

            if (!graphModel.containsEdge(source, destination)) {
                showAlert("Edge Not Found", "Edge from " + source + " to " + destination + " does not exist.");
                return;
            }

            // Save state for undo
            saveState("REMOVE_EDGE_" + source + "_" + destination);

            // Remove from model
            graphModel.removeEdge(source, destination);

            // Remove visual element
            edgeElements.removeIf(edge ->
                    edge.getSource() == source && edge.getDestination() == destination);

            sourceField.clear();
            destinationField.clear();

            updateDisplay();
            redrawGraph();

            logOperation("REMOVE_EDGE " + source + " -> " + destination +
                    " | Remaining: " + graphModel.getEdgeCount() + " edges");

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid integers for vertex IDs.");
        }
    }

    // ==================== SEARCH AND HIGHLIGHT OPERATIONS ====================

    @FXML
    private void searchVertex() {
        if (isTraversing) return;

        String input = searchVertexField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a vertex ID to search.");
            return;
        }

        try {
            int vertexId = Integer.parseInt(input);
            if (!graphModel.containsVertex(vertexId)) {
                showAlert("Vertex Not Found", "Vertex " + vertexId + " does not exist in the graph.");
                return;
            }

            // FIND: Only show popup and zoom effect, NO color change
            VertexElement vertexElement = vertexElements.get(vertexId);
            if (vertexElement != null) {
                // Show popup effect with vertex information
                showVertexPopup(vertexElement, vertexId);

                // Apply zoom/pulse effect without color change
                applyFindEffect(vertexElement);

                // Update result area
                if (resultArea != null) {
                    resultArea.setText("🔍 FOUND: Vertex " + vertexId + "\n" +
                            "Position: (" + String.format("%.1f", vertexElement.getCircle().getCenterX()) +
                            ", " + String.format("%.1f", vertexElement.getCircle().getCenterY()) + ")\n" +
                            "Degree: " + graphModel.getDegree(vertexId) + "\n" +
                            "Neighbors: " + graphModel.getNeighbors(vertexId));
                }
            }

            searchVertexField.clear();
            logOperation("FIND_VERTEX " + vertexId + " | Found and zoomed to location");

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for vertex ID.");
        }
    }

    @FXML
    private void highlightVertex() {
        if (isTraversing) return;

        String input = searchVertexField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a vertex ID to highlight.");
            return;
        }

        try {
            int vertexId = Integer.parseInt(input);
            if (!graphModel.containsVertex(vertexId)) {
                showAlert("Vertex Not Found", "Vertex " + vertexId + " does not exist in the graph.");
                return;
            }

            // HIGHLIGHT: Apply color highlighting effect
            VertexElement vertexElement = vertexElements.get(vertexId);
            if (vertexElement != null) {
                // Clear any previous highlights
                clearAllHighlights();

                // Apply color highlight effect
                applyHighlightEffect(vertexElement, vertexId);

                // Also highlight connected edges
                highlightConnectedEdges(vertexId);

                // Update result area
                if (resultArea != null) {
                    resultArea.setText("✨ HIGHLIGHTED: Vertex " + vertexId + "\n" +
                            "Status: Currently highlighted in the graph\n" +
                            "Degree: " + graphModel.getDegree(vertexId) + "\n" +
                            "Connected to: " + graphModel.getNeighbors(vertexId));
                }
            }

            searchVertexField.clear();
            logOperation("HIGHLIGHT_VERTEX " + vertexId + " | Applied visual highlight");

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for vertex ID.");
        }
    }

// ==================== VISUAL EFFECTS METHODS ====================

    /**
     * Apply FIND effect: Popup + zoom/pulse effect WITHOUT color change
     */
    private void applyFindEffect(VertexElement vertexElement) {
        Circle circle = vertexElement.getCircle();

        // Store original style to restore later
        String originalStyle = circle.getStyle();

        // Create zoom-in effect to draw attention
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(300), circle);
        zoomIn.setFromX(1.0);
        zoomIn.setFromY(1.0);
        zoomIn.setToX(1.4);
        zoomIn.setToY(1.4);
        zoomIn.setInterpolator(Interpolator.EASE_OUT);

        // Create zoom-out effect to return to normal
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(300), circle);
        zoomOut.setFromX(1.4);
        zoomOut.setFromY(1.4);
        zoomOut.setToX(1.0);
        zoomOut.setToY(1.0);
        zoomOut.setInterpolator(Interpolator.EASE_IN);

        // Add subtle shadow effect during zoom
        Timeline shadowEffect = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(circle.styleProperty(), originalStyle)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(circle.styleProperty(), originalStyle +
                                "-fx-effect: dropshadow(gaussian, #4f46e5, 15, 0.7, 0, 0);")),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(circle.styleProperty(), originalStyle))
        );

        // Combine animations
        SequentialTransition findAnimation = new SequentialTransition(zoomIn, zoomOut);
        ParallelTransition fullEffect = new ParallelTransition(findAnimation, shadowEffect);

        fullEffect.play();

        // Also scroll to center the vertex if it's outside viewport
        Platform.runLater(() -> centerVertexInView(vertexElement));
    }

    /**
     * Apply HIGHLIGHT effect: Color change and persistent highlighting
     */
    /**
     * Apply HIGHLIGHT effect: Color change and persistent highlighting with 3-second auto-reset
     */
    private void applyHighlightEffect(VertexElement vertexElement, int vertexId) {
        Circle circle = vertexElement.getCircle();
        Text label = vertexElement.getLabel();

        // Store original styles for restoration
        String originalCircleStyle = "-fx-fill: #3b82f6; -fx-stroke: #1d4ed8; -fx-stroke-width: 2;";
        String originalLabelStyle = "-fx-fill: white; -fx-font-weight: bold;";

        // Apply highlight colors
        String highlightStyle = "-fx-fill: #fbbf24; -fx-stroke: #f59e0b; -fx-stroke-width: 3; " +
                "-fx-effect: dropshadow(gaussian, #f59e0b, 10, 0.6, 0, 0);";
        circle.setStyle(highlightStyle);

        if (label != null) {
            label.setStyle("-fx-fill: #92400e; -fx-font-weight: bold; " +
                    "-fx-effect: dropshadow(gaussian, #fef3c7, 2, 0.5, 0, 0);");
        }

        // Add pulsing animation for highlighted vertex
        ScaleTransition pulse = new ScaleTransition(Duration.millis(1000), circle);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(6); // 3 seconds total (6 half-cycles * 0.5s each)
        pulse.setInterpolator(Interpolator.EASE_BOTH);

        // Store animation reference to stop it if needed
        vertexElement.setHighlightAnimation(pulse);
        pulse.play();

        // ⭐ AUTO-RESET AFTER 3 SECONDS
        Timeline autoReset = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    // Stop the pulsing animation
                    if (vertexElement.getHighlightAnimation() != null) {
                        vertexElement.getHighlightAnimation().stop();
                        vertexElement.setHighlightAnimation(null);
                    }

                    // Smooth transition back to original colors
                    Timeline fadeBack = new Timeline(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(circle.styleProperty(), highlightStyle)),
                            new KeyFrame(Duration.millis(500),
                                    new KeyValue(circle.styleProperty(), originalCircleStyle, Interpolator.EASE_OUT))
                    );

                    if (label != null) {
                        Timeline labelFadeBack = new Timeline(
                                new KeyFrame(Duration.ZERO,
                                        new KeyValue(label.styleProperty(), label.getStyle())),
                                new KeyFrame(Duration.millis(500),
                                        new KeyValue(label.styleProperty(), originalLabelStyle, Interpolator.EASE_OUT))
                        );

                        ParallelTransition resetTransition = new ParallelTransition(fadeBack, labelFadeBack);
                        resetTransition.play();
                    } else {
                        fadeBack.play();
                    }

                    // Reset connected edges after a brief delay
                    Timeline edgeReset = new Timeline(
                            new KeyFrame(Duration.millis(200), edgeEvent -> resetConnectedEdges(vertexId))
                    );
                    edgeReset.play();

                    // Update status
                    if (statusLabel != null) {
                        String currentText = statusLabel.getText();
                        if (currentText.contains("HIGHLIGHTED")) {
                            statusLabel.setText("Highlight automatically cleared after 3 seconds");
                        }
                    }

                    logOperation("AUTO_RESET_HIGHLIGHT vertex " + vertexId + " after 3 seconds");
                })
        );

        autoReset.play();
        logOperation("HIGHLIGHT applied to vertex " + vertexId + " (auto-reset in 3s)");
    }
    /**
     * Reset connected edges back to normal appearance with smooth animation
     */
    private void resetConnectedEdges(int vertexId) {
        for (EdgeElement edge : edgeElements) {
            if (edge.getSource() == vertexId || edge.getDestination() == vertexId) {
                Line line = edge.getLine();

                // Get current and target styles
                String currentStyle = line.getStyle() != null ? line.getStyle() : "";
                String normalStyle;

                if (graphModel.isDirected()) {
                    normalStyle = "-fx-stroke: #64748b; -fx-stroke-width: 2;";
                } else {
                    normalStyle = "-fx-stroke: #94a3b8; -fx-stroke-width: 1.5;";
                }

                // Smooth transition back to normal
                Timeline edgeTransition = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(line.styleProperty(), currentStyle)),
                        new KeyFrame(Duration.millis(400),
                                new KeyValue(line.styleProperty(), normalStyle, Interpolator.EASE_OUT))
                );
                edgeTransition.play();

                // Reset weight label if exists
                if (edge.getWeightLabel() != null) {
                    Timeline weightTransition = new Timeline(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(edge.getWeightLabel().styleProperty(), edge.getWeightLabel().getStyle())),
                            new KeyFrame(Duration.millis(400),
                                    new KeyValue(edge.getWeightLabel().styleProperty(),
                                            "-fx-text-fill: #374151; -fx-font-size: 10px; " +
                                                    "-fx-background-color: rgba(255,255,255,0.8); " +
                                                    "-fx-padding: 1; -fx-background-radius: 2;", Interpolator.EASE_OUT))
                    );
                    weightTransition.play();
                }
            }
        }
    }


    /**
     * Show popup with vertex information (for FIND operation)
     */
    private void showVertexPopup(VertexElement vertexElement, int vertexId) {
        // Create popup content
        VBox popupContent = new VBox(8);
        popupContent.setStyle("-fx-background-color: #1f2937; -fx-padding: 12; " +
                "-fx-background-radius: 8; -fx-border-color: #4f46e5; " +
                "-fx-border-width: 2; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label titleLabel = new Label("🔍 Vertex " + vertexId);
        titleLabel.setStyle("-fx-text-fill: #e5e7eb; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label degreeLabel = new Label("Degree: " + graphModel.getDegree(vertexId));
        degreeLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");

        Label neighborsLabel = new Label("Neighbors: " + graphModel.getNeighbors(vertexId));
        neighborsLabel.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");
        neighborsLabel.setWrapText(true);
        neighborsLabel.setMaxWidth(200);

        popupContent.getChildren().addAll(titleLabel, degreeLabel, neighborsLabel);

        // Create and show popup
        javafx.stage.Popup popup = new javafx.stage.Popup();
        popup.getContent().add(popupContent);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        // Position popup near the vertex
        Circle circle = vertexElement.getCircle();
        double screenX = circle.localToScreen(circle.getBoundsInLocal()).getMinX();
        double screenY = circle.localToScreen(circle.getBoundsInLocal()).getMinY();

        popup.show(graphCanvas.getScene().getWindow(), screenX + 30, screenY - 10);

        // Auto-hide popup after 3 seconds
        Timeline autoHide = new Timeline(new KeyFrame(Duration.seconds(3), e -> popup.hide()));
        autoHide.play();
    }

    /**
     * Highlight edges connected to the highlighted vertex
     */
    private void highlightConnectedEdges(int vertexId) {
        for (EdgeElement edge : edgeElements) {
            if (edge.getSource() == vertexId || edge.getDestination() == vertexId) {
                Line line = edge.getLine();
                line.setStyle("-fx-stroke: #fbbf24; -fx-stroke-width: 3; " +
                        "-fx-effect: dropshadow(gaussian, #f59e0b, 5, 0.5, 0, 0);");

                // Highlight weight label if exists
                if (edge.getWeightLabel() != null) {
                    edge.getWeightLabel().setStyle("-fx-text-fill: #92400e; -fx-font-weight: bold; " +
                            "-fx-background-color: #fef3c7; -fx-padding: 2; " +
                            "-fx-background-radius: 3;");
                }
            }
        }
    }

    /**
     * Clear all highlights from vertices and edges
     */
    /**
     * Clear all highlights from vertices and edges with immediate effect
     */
    private void clearAllHighlights() {
        // Clear vertex highlights
        for (VertexElement vertex : vertexElements.values()) {
            // Stop any highlight animations
            if (vertex.getHighlightAnimation() != null) {
                vertex.getHighlightAnimation().stop();
                vertex.setHighlightAnimation(null);
            }

            // Reset vertex appearance immediately
            resetVertexAppearance(vertex);
        }

        // Clear edge highlights
        for (EdgeElement edge : edgeElements) {
            resetEdgeAppearance(edge);
        }

        // Update status
        if (statusLabel != null) {
            statusLabel.setText("All highlights cleared manually");
        }

        logOperation("CLEAR_ALL_HIGHLIGHTS | Manual clear operation");
    }


    /**
     * Center a vertex in the viewport
     */
    private void centerVertexInView(VertexElement vertexElement) {
        if (canvasScrollPane == null) return;

        Circle circle = vertexElement.getCircle();
        double vertexX = circle.getCenterX();
        double vertexY = circle.getCenterY();

        // Calculate scroll values to center the vertex
        double canvasWidth = graphCanvas.getPrefWidth();
        double canvasHeight = graphCanvas.getPrefHeight();
        double viewportWidth = canvasScrollPane.getViewportBounds().getWidth();
        double viewportHeight = canvasScrollPane.getViewportBounds().getHeight();

        double hValue = Math.max(0, Math.min(1, (vertexX - viewportWidth / 2) / (canvasWidth - viewportWidth)));
        double vValue = Math.max(0, Math.min(1, (vertexY - viewportHeight / 2) / (canvasHeight - viewportHeight)));

        // Animate scroll to center
        Timeline centerAnimation = new Timeline(
                new KeyFrame(Duration.millis(500),
                        new KeyValue(canvasScrollPane.hvalueProperty(), hValue, Interpolator.EASE_BOTH),
                        new KeyValue(canvasScrollPane.vvalueProperty(), vValue, Interpolator.EASE_BOTH)
                )
        );
        centerAnimation.play();
    }





// ==================== UTILITY METHODS FOR APPEARANCE ====================

    private void resetVertexAppearance(VertexElement vertex) {
        Circle circle = vertex.getCircle();
        Text label = vertex.getLabel();

        // 🎯 USE DIRECT PROPERTIES FOR RELIABLE COLOR RESTORATION
        circle.setFill(Color.web("#3b82f6"));  // Blue fill
        circle.setStroke(Color.web("#1d4ed8")); // Darker blue stroke
        circle.setStrokeWidth(2);
        circle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 2, 2);");

        if (label != null) {
            label.setFill(Color.WHITE);
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        }
    }

    private void resetEdgeAppearance(EdgeElement edge) {
        Line line = edge.getLine();

        // 🎯 USE DIRECT PROPERTIES FOR RELIABLE COLOR RESTORATION
        if (graphModel.isDirected()) {
            line.setStroke(Color.web("#64748b"));
            line.setStrokeWidth(2);
        } else {
            line.setStroke(Color.web("#94a3b8"));
            line.setStrokeWidth(1.5);
        }
        line.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 1, 1);");

        // Reset weight label if exists
        if (edge.getWeightLabel() != null) {
            edge.getWeightLabel().setFill(Color.web("#374151"));
            edge.getWeightLabel().setStyle("-fx-font-size: 10px; -fx-background-color: rgba(255,255,255,0.8); " +
                    "-fx-padding: 1; -fx-background-radius: 2;");
        }
    }

    @FXML
    private void performTraversal() {
        if (isTraversing) return;

        String input = startVertexField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Invalid Input", "Please enter a start vertex for traversal.");
            return;
        }

        try {
            int startVertex = Integer.parseInt(input);
            if (!graphModel.containsVertex(startVertex)) {
                showAlert("Vertex Not Found", "Start vertex " + startVertex + " does not exist.");
                return;
            }

            String algorithm = algorithmComboBox.getValue();
            isTraversing = true;
            updateControlStates();

            switch (algorithm) {
                case "BFS (Breadth-First Search)":
                    List<Integer> bfsResult = graphModel.bfs(startVertex);
                    animateTraversal(bfsResult, "BFS");
                    break;
                case "DFS (Depth-First Search)":
                    List<Integer> dfsResult = graphModel.dfs(startVertex);
                    animateTraversal(dfsResult, "DFS");
                    break;
                case "Shortest Path":
                    performShortestPath(startVertex);
                    break;
                case "Dijkstra's Algorithm":
                    performDijkstra(startVertex);
                    break;
                case "A* Pathfinding":
                    performAStar(startVertex);
                    break;
                case "Topological Sort":
                    performTopologicalSort();
                    break;
                default:
                    isTraversing = false;
                    updateControlStates();
                    showAlert("Algorithm Not Implemented", "Selected algorithm is not yet implemented.");
                    break;
            }

        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid integer for start vertex.");
        }
    }

    private void performShortestPath(int startVertex) {
        String endInput = endVertexField.getText().trim();
        if (endInput.isEmpty()) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Shortest Path");
            dialog.setHeaderText("Enter Destination Vertex");
            dialog.setContentText("Destination:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(input -> {
                try {
                    int endVertex = Integer.parseInt(input);
                    if (!graphModel.containsVertex(endVertex)) {
                        showAlert("Vertex Not Found", "Destination vertex " + endVertex + " does not exist.");
                        isTraversing = false;
                        updateControlStates();
                        return;
                    }

                    List<Integer> path = graphModel.getShortestPath(startVertex, endVertex);
                    if (path.isEmpty()) {
                        resultArea.setText("No path found from " + startVertex + " to " + endVertex);
                        isTraversing = false;
                        updateControlStates();
                    } else {
                        animatePathTraversal(path, "Shortest Path from " + startVertex + " to " + endVertex);
                    }

                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter a valid integer for destination vertex.");
                    isTraversing = false;
                    updateControlStates();
                }
            });

            if (!result.isPresent()) {
                isTraversing = false;
                updateControlStates();
            }
        } else {
            try {
                int endVertex = Integer.parseInt(endInput);
                if (!graphModel.containsVertex(endVertex)) {
                    showAlert("Vertex Not Found", "End vertex " + endVertex + " does not exist.");
                    isTraversing = false;
                    updateControlStates();
                    return;
                }

                List<Integer> path = graphModel.getShortestPath(startVertex, endVertex);
                if (path.isEmpty()) {
                    resultArea.setText("No path found from " + startVertex + " to " + endVertex);
                    isTraversing = false;
                    updateControlStates();
                } else {
                    animatePathTraversal(path, "Shortest Path from " + startVertex + " to " + endVertex);
                }

            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid integer for end vertex.");
                isTraversing = false;
                updateControlStates();
            }
        }
    }

    private void performDijkstra(int startVertex) {
        // Similar to performShortestPath but for weighted graphs
        if (!weightedCheckBox.isSelected()) {
            showAlert("Graph Not Weighted", "Dijkstra's algorithm requires a weighted graph.");
            isTraversing = false;
            updateControlStates();
            return;
        }

        performShortestPath(startVertex); // For now, use same logic
    }

    private void performAStar(int startVertex) {
        performShortestPath(startVertex); // For now, use same logic
    }

    private void performTopologicalSort() {
        if (!directedCheckBox.isSelected()) {
            showAlert("Graph Not Directed", "Topological sort requires a directed graph.");
            isTraversing = false;
            updateControlStates();
            return;
        }

        try {
            List<Integer> result = graphModel.topologicalSort();
            animateTraversal(result, "Topological Sort");
        } catch (Exception e) {
            showAlert("Error", "Error performing topological sort: " + e.getMessage());
            isTraversing = false;
            updateControlStates();
        }
    }

    // ==================== LAYOUT ALGORITHMS ====================

    @FXML
    private void applyCircularLayout() {
        if (vertexElements.isEmpty()) return;

        double centerX = graphCanvas.getPrefWidth() / 2;
        double centerY = graphCanvas.getPrefHeight() / 2;
        double radius = Math.min(centerX, centerY) - 100;

        List<Integer> vertices = new ArrayList<>(vertexElements.keySet());
        int numVertices = vertices.size();

        System.out.println("Applying circular layout to " + numVertices + " vertices");

        for (int i = 0; i < numVertices; i++) {
            double angle = 2 * Math.PI * i / numVertices;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            VertexElement vertex = vertexElements.get(vertices.get(i));
            vertex.setPosition(x, y);

            System.out.println("Positioned vertex " + vertices.get(i) + " at (" + x + ", " + y + ")");
        }

        // **FIX: Update all edge positions**
        updateAllEdges();

        // **FIX: Force canvas refresh**
        Platform.runLater(() -> {
            graphCanvas.requestLayout();
        });

        logOperation("LAYOUT Applied circular layout to " + numVertices + " vertices");
    }

    @FXML
    private void applyForceLayout() {
        if (vertexElements.isEmpty()) return;

        // Simple force-directed layout
        int iterations = 100;
        double k = Math.sqrt((graphCanvas.getPrefWidth() * graphCanvas.getPrefHeight()) / vertexElements.size());

        for (int iter = 0; iter < iterations; iter++) {
            // Calculate repulsive forces
            for (VertexElement v1 : vertexElements.values()) {
                v1.resetForces();
                for (VertexElement v2 : vertexElements.values()) {
                    if (v1 != v2) {
                        double dx = v1.getX() - v2.getX();
                        double dy = v1.getY() - v2.getY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance > 0) {
                            double force = k * k / distance;
                            v1.addForce(force * dx / distance, force * dy / distance);
                        }
                    }
                }
            }

            // Calculate attractive forces
            for (EdgeElement edge : edgeElements) {
                VertexElement v1 = vertexElements.get(edge.getSource());
                VertexElement v2 = vertexElements.get(edge.getDestination());
                if (v1 != null && v2 != null) {
                    double dx = v1.getX() - v2.getX();
                    double dy = v1.getY() - v2.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance > 0) {
                        double force = distance * distance / k;
                        v1.addForce(-force * dx / distance, -force * dy / distance);
                        v2.addForce(force * dx / distance, force * dy / distance);
                    }
                }
            }

            // Apply forces and limit displacement
            double maxDisplacement = k / 4;
            for (VertexElement vertex : vertexElements.values()) {
                double displacement = Math.sqrt(vertex.getForceX() * vertex.getForceX() +
                        vertex.getForceY() * vertex.getForceY());
                if (displacement > maxDisplacement) {
                    vertex.scaleForces(maxDisplacement / displacement);
                }

                double newX = Math.max(50, Math.min(graphCanvas.getPrefWidth() - 50,
                        vertex.getX() + vertex.getForceX()));
                double newY = Math.max(50, Math.min(graphCanvas.getPrefHeight() - 50,
                        vertex.getY() + vertex.getForceY()));
                vertex.setPosition(newX, newY);
            }
        }

        updateAllEdges();
        logOperation("LAYOUT Applied force-directed layout");
    }

    @FXML
    private void resetLayout() {
        applyCircularLayout(); // Reset to circular layout
    }

    // ==================== ZOOM AND PAN CONTROLS ====================

    @FXML
    private void zoomIn() {
        adjustZoom(1.2);
    }

    @FXML
    private void zoomOut() {
        adjustZoom(0.8);
    }

    @FXML
    private void resetZoom() {
        zoomLevel = 1.0;
        graphCanvas.setScaleX(zoomLevel);
        graphCanvas.setScaleY(zoomLevel);
    }

    private void adjustZoom(double factor) {
        double newZoom = zoomLevel * factor;
        if (newZoom >= MIN_ZOOM && newZoom <= MAX_ZOOM) {
            zoomLevel = newZoom;
            graphCanvas.setScaleX(zoomLevel);
            graphCanvas.setScaleY(zoomLevel);
        }
    }

    // ==================== UTILITY OPERATIONS ====================

    @FXML
    private void clearGraph() {
        if (isTraversing) return;

        if (graphModel.isEmpty()) {
            showAlert("Graph Empty", "Graph is already empty.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Graph");
        confirmAlert.setHeaderText("Confirm Clear Operation");
        confirmAlert.setContentText("Are you sure you want to clear the entire graph?");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            saveState("CLEAR_ALL");

            animateClear(() -> {
                graphModel.clear();
                vertexElements.clear();
                edgeElements.clear();
                graphCanvas.getChildren().clear();
                updateDisplay();
                logOperation("CLEAR_ALL | Graph cleared");
            });
        }
    }


    @FXML
    private void generateRandomGraph() {

        saveState("GENERATE_RANDOM");

        // Clear existing graph
        graphModel.clear();
        vertexElements.clear();
        edgeElements.clear();
        graphCanvas.getChildren().clear();

        // Generate 6-10 random vertices
        Random random = new Random();
        int numVertices = 6 + random.nextInt(5);

        for (int i = 1; i <= numVertices; i++) {
            graphModel.addVertex(i);
            VertexElement vertexElement = createVertexElement(i);
            vertexElements.put(i, vertexElement);
            graphCanvas.getChildren().addAll(vertexElement.getCircle(), vertexElement.getLabel());
        }

        // Generate random edges
        int numEdges = numVertices + random.nextInt(numVertices);
        Set<String> addedEdges = new HashSet<>();

        for (int i = 0; i < numEdges; i++) {
            int source = 1 + random.nextInt(numVertices);
            int destination = 1 + random.nextInt(numVertices);

            if (source != destination) {
                String edgeKey = Math.min(source, destination) + "-" + Math.max(source, destination);
                if (!addedEdges.contains(edgeKey)) {
                    graphModel.addEdge(source, destination);
                    EdgeElement edgeElement = createEdgeElement(source, destination, 1.0);
                    if (edgeElement != null) {
                        edgeElements.add(edgeElement);
                        graphCanvas.getChildren().add(0, edgeElement.getLine());
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }

        applyCircularLayout();
        updateDisplay();

        logOperation("GENERATE_RANDOM | Generated " + numVertices + " vertices, " + addedEdges.size() + " edges");
    }

    // ==================== UNDO/REDO SYSTEM ====================

    @FXML
    private void undoOperation() {
        if (!undoStack.isEmpty()) {
            // Save current state before undoing
            GraphState currentState = captureCurrentState();
            redoStack.push(currentState);

            // Get previous state and restore it
            GraphState previousState = undoStack.pop();
            restoreState(previousState);

            updateDisplay();
            logOperation("UNDO | Reverted to: " + previousState.getDescription());
        }
    }

    @FXML
    private void redoOperation() {
        if (!redoStack.isEmpty()) {
            // Save current state before redoing
            GraphState currentState = captureCurrentState();
            undoStack.push(currentState);

            // Get next state and restore it
            GraphState nextState = redoStack.pop();
            restoreState(nextState);

            updateDisplay();
            logOperation("REDO | Applied: " + nextState.getDescription());
        }
    }

    /**
     * 🔄 COMPLETE STATE RESTORATION METHOD WITH PROPER COLOR RESTORATION
     */
    private void restoreState(GraphState state) {
        // Clear current graph
        graphCanvas.getChildren().clear();
        vertexElements.clear();
        edgeElements.clear();
        graphModel.clear();

        // Restore graph model properties
        graphModel = new GraphModel(state.wasDirected(), state.wasWeighted());
        directedCheckBox.setSelected(state.wasDirected());
        weightedCheckBox.setSelected(state.wasWeighted());

        // 🔄 RESTORE ALL VERTICES WITH PROPER BLUE STYLING
        for (GraphState.VertexData vertexData : state.getVertexStates().values()) {
            // Add to model
            graphModel.addVertex(vertexData.id);

            // Create visual element with proper styling
            Circle circle = new Circle(vertexData.x, vertexData.y, 25);

            // 🎯 FORCE PROPER BLUE COLORS USING DIRECT PROPERTIES
            circle.setFill(Color.web("#3b82f6"));  // Blue fill
            circle.setStroke(Color.web("#1d4ed8")); // Darker blue stroke
            circle.setStrokeWidth(2);

            // Apply CSS effects (shadow) separately
            String effectStyle = "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 2, 2);";
            circle.setStyle(effectStyle);

            Text label = new Text(vertexData.labelText);
            label.setX(vertexData.x - 8);
            label.setY(vertexData.y + 6);

            // 🎯 FORCE PROPER LABEL STYLING
            label.setFill(Color.WHITE);  // White text
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            VertexElement vertex = new VertexElement(circle, label, vertexData.id, vertexData.x, vertexData.y);
            vertexElements.put(vertexData.id, vertex);

            // Add to canvas
            graphCanvas.getChildren().addAll(circle, label);
        }

        // 🔄 RESTORE ALL EDGES WITH PROPER STYLING
        for (GraphState.EdgeData edgeData : state.getEdgeStates()) {
            // Add to model
            graphModel.addEdge(edgeData.source, edgeData.destination, edgeData.weight);

            // Create visual element with proper styling
            VertexElement sourceVertex = vertexElements.get(edgeData.source);
            VertexElement destVertex = vertexElements.get(edgeData.destination);

            if (sourceVertex != null && destVertex != null) {
                Line line = new Line();
                line.setStartX(sourceVertex.getX());
                line.setStartY(sourceVertex.getY());
                line.setEndX(destVertex.getX());
                line.setEndY(destVertex.getY());

                // 🎯 FORCE PROPER EDGE COLORS
                line.setStroke(Color.web("#64748b"));  // Gray color
                line.setStrokeWidth(2);
                line.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 1, 1);");

                Text weightLabel = null;
                if (edgeData.weightText != null) {
                    double midX = (sourceVertex.getX() + destVertex.getX()) / 2;
                    double midY = (sourceVertex.getY() + destVertex.getY()) / 2;
                    weightLabel = new Text(midX, midY, edgeData.weightText);

                    weightLabel.setFill(Color.web("#dc2626")); // Red color
                    weightLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
                    graphCanvas.getChildren().add(weightLabel);
                }

                EdgeElement edge = new EdgeElement(line, edgeData.source, edgeData.destination, edgeData.weight, weightLabel);
                edgeElements.add(edge);

                // Add to canvas (edges behind vertices)
                graphCanvas.getChildren().add(0, line);
            }
        }

        System.out.println("✅ State restored with proper blue colors: " + state.getDescription());
    }


    private void saveState(String description) {
        GraphState state = captureCurrentState();
        state.setDescription(description);
        undoStack.push(state);
        redoStack.clear(); // Clear redo stack when new operation is performed

        // Limit undo stack size
        if (undoStack.size() > 50) {
            undoStack.remove(0);
        }
    }

    private GraphState captureCurrentState() {
        return new GraphState(graphModel, vertexElements, edgeElements);
    }

    // ==================== FILE OPERATIONS ====================

    @FXML
    private void saveGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Graph");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));

        Stage stage = (Stage) graphCanvas.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                // Save graph data
                Map<String, Object> graphData = new HashMap<>();
                graphData.put("vertices", new ArrayList<>(graphModel.getVertices()));
                graphData.put("directed", graphModel.isDirected());

                List<Map<String, Object>> edges = new ArrayList<>();
                for (EdgeElement edge : edgeElements) {
                    Map<String, Object> edgeData = new HashMap<>();
                    edgeData.put("source", edge.getSource());
                    edgeData.put("destination", edge.getDestination());
                    edgeData.put("weight", edge.getWeight());
                    edges.add(edgeData);
                }

                graphData.put("edges", edges);
                oos.writeObject(graphData);

                logOperation("SAVE_GRAPH | Saved to: " + file.getName());
                showInfo("Graph Saved", "Graph successfully saved to " + file.getName());

            } catch (IOException e) {
                showAlert("Save Error", "Error saving graph: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Graph");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graph Files", "*.graph"));

        Stage stage = (Stage) graphCanvas.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                saveState("LOAD_GRAPH");

                // Clear current graph
                graphModel.clear();
                vertexElements.clear();
                edgeElements.clear();
                graphCanvas.getChildren().clear();

                @SuppressWarnings("unchecked")
                Map<String, Object> graphData = (Map<String, Object>) ois.readObject();

                // Restore vertices
                @SuppressWarnings("unchecked")
                List<Integer> vertices = (List<Integer>) graphData.get("vertices");
                for (Integer vertex : vertices) {
                    graphModel.addVertex(vertex);
                    VertexElement vertexElement = createVertexElement(vertex);
                    vertexElements.put(vertex, vertexElement);
                    graphCanvas.getChildren().addAll(vertexElement.getCircle(), vertexElement.getLabel());
                }

                // Restore edges
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> edges = (List<Map<String, Object>>) graphData.get("edges");
                for (Map<String, Object> edgeData : edges) {
                    int source = (Integer) edgeData.get("source");
                    int destination = (Integer) edgeData.get("destination");
                    double weight = (Double) edgeData.getOrDefault("weight", 1.0);

                    graphModel.addEdge(source, destination, weight);
                    EdgeElement edgeElement = createEdgeElement(source, destination, weight);
                    if (edgeElement != null) {
                        edgeElements.add(edgeElement);
                        graphCanvas.getChildren().add(0, edgeElement.getLine());
                        if (edgeElement.getWeightLabel() != null) {
                            graphCanvas.getChildren().add(edgeElement.getWeightLabel());
                        }
                    }
                }

                applyCircularLayout();
                updateDisplay();

                logOperation("LOAD_GRAPH | Loaded from: " + file.getName());
                showInfo("Graph Loaded", "Graph successfully loaded from " + file.getName());

            } catch (IOException | ClassNotFoundException e) {
                showAlert("Load Error", "Error loading graph: " + e.getMessage());
            }
        }
    }

    // ==================== TOGGLE OPERATIONS ====================

    @FXML
    private void toggleGraphType() {
        boolean isDirected = directedCheckBox.isSelected();
        saveState("TOGGLE_DIRECTED");

        graphModel = new GraphModel(isDirected);

        // Rebuild graph with new type
        Map<Integer, VertexElement> oldVertices = new HashMap<>(vertexElements);
        List<EdgeElement> oldEdges = new ArrayList<>(edgeElements);

        vertexElements.clear();
        edgeElements.clear();
        graphCanvas.getChildren().clear();

        // Restore vertices
        for (Map.Entry<Integer, VertexElement> entry : oldVertices.entrySet()) {
            graphModel.addVertex(entry.getKey());
            vertexElements.put(entry.getKey(), entry.getValue());
            graphCanvas.getChildren().addAll(entry.getValue().getCircle(), entry.getValue().getLabel());
        }

        // Restore edges with new direction setting
        for (EdgeElement edge : oldEdges) {
            graphModel.addEdge(edge.getSource(), edge.getDestination());
            edgeElements.add(edge);
            graphCanvas.getChildren().add(0, edge.getLine());
        }

        updateDisplay();
        logOperation("GRAPH_TYPE changed to " + (isDirected ? "DIRECTED" : "UNDIRECTED"));
    }

    @FXML
    private void toggleWeightSupport() {
        boolean isWeighted = weightedCheckBox.isSelected();

        weightField.setDisable(!isWeighted);
        showWeightsCheckBox.setDisable(!isWeighted);

        if (isWeighted) {
            weightField.setPromptText("Weight");
        }

        updateDisplay();
        logOperation("WEIGHT_SUPPORT " + (isWeighted ? "ENABLED" : "DISABLED"));
    }

    @FXML
    private void toggleLabelsVisibility() {
        boolean showLabels = showLabelsCheckBox.isSelected();

        for (VertexElement vertex : vertexElements.values()) {
            vertex.getLabel().setVisible(showLabels);
        }
    }

    @FXML
    private void toggleWeightsVisibility() {
        boolean showWeights = showWeightsCheckBox.isSelected();

        for (EdgeElement edge : edgeElements) {
            if (edge.getWeightLabel() != null) {
                edge.getWeightLabel().setVisible(showWeights && weightedCheckBox.isSelected());
            }
        }
    }

    // ==================== VISUAL ELEMENT CREATION ====================

    private VertexElement createVertexElement(int vertexId) {
        // **FIX: Use simple positioning, layout will arrange properly**
        double x = 200 + (vertexElements.size() * 40);
        double y = 200 + (vertexElements.size() * 25);

        Circle circle = new Circle(x, y, 25);
        circle.setFill(Color.web("#3b82f6"));
        circle.setStroke(Color.web("#1d4ed8"));
        circle.setStrokeWidth(3);
        circle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 4, 0, 2, 2);");

        Text label = new Text(String.valueOf(vertexId));
        label.setX(x - 8);
        label.setY(y + 6);
        label.setFill(Color.WHITE);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        System.out.println("Created vertex " + vertexId + " at (" + x + ", " + y + ")");

        return new VertexElement(circle, label, vertexId, x, y);
    }

    private EdgeElement createEdgeElement(int source, int destination, double weight) {
        VertexElement sourceVertex = vertexElements.get(source);
        VertexElement destVertex = vertexElements.get(destination);

        if (sourceVertex == null || destVertex == null) {
            System.err.println("Cannot create edge: missing vertex elements");
            return null;
        }

        Line line = new Line();
        line.setStartX(sourceVertex.getX());
        line.setStartY(sourceVertex.getY());
        line.setEndX(destVertex.getX());
        line.setEndY(destVertex.getY());
        line.setStroke(Color.web("#64748b"));
        line.setStrokeWidth(2);
        line.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 2, 0, 1, 1);");

        Text weightLabel = null;
        if (weightedCheckBox.isSelected()) {
            double midX = (sourceVertex.getX() + destVertex.getX()) / 2;
            double midY = (sourceVertex.getY() + destVertex.getY()) / 2;

            weightLabel = new Text(midX, midY, String.format("%.1f", weight));
            weightLabel.setFill(Color.web("#dc2626"));
            weightLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        }

        System.out.println("Created edge " + source + "->" + destination);

        return new EdgeElement(line, source, destination, weight, weightLabel);
    }

    // ==================== ANIMATION METHODS ====================

    private void animateAddVertex(VertexElement vertex) {
        // **FIX: Ensure elements are added to canvas**
        graphCanvas.getChildren().addAll(vertex.getCircle(), vertex.getLabel());
        System.out.println("Added vertex " + vertex.getId() + " to canvas - Children count: " + graphCanvas.getChildren().size());

        vertex.getCircle().setScaleX(0);
        vertex.getCircle().setScaleY(0);
        vertex.getLabel().setOpacity(0);

        ScaleTransition scaleUp = animationService.createScaleTransition(vertex.getCircle(), 1.2, 1.2, 300);
        scaleUp.setOnFinished(e -> {
            ScaleTransition scaleNormal = animationService.createScaleTransition(vertex.getCircle(), 1.0, 1.0, 200);
            scaleNormal.play();
        });

        FadeTransition fadeIn = animationService.createFadeAnimation(vertex.getLabel(), 0, 1, 400);

        ParallelTransition addAnim = new ParallelTransition(scaleUp, fadeIn);
        addAnim.play();
    }

    private void animateAddEdge(EdgeElement edge) {
        // **FIX: Add behind vertices (order matters)**
        graphCanvas.getChildren().add(0, edge.getLine()); // Add at beginning
        if (edge.getWeightLabel() != null) {
            graphCanvas.getChildren().add(edge.getWeightLabel());
        }

        System.out.println("Added edge to canvas - Children count: " + graphCanvas.getChildren().size());

        edge.getLine().setOpacity(0);
        FadeTransition fadeIn = animationService.createFadeAnimation(edge.getLine(), 0, 1, 400);

        if (edge.getWeightLabel() != null) {
            edge.getWeightLabel().setOpacity(0);
            FadeTransition weightFade = animationService.createFadeAnimation(edge.getWeightLabel(), 0, 1, 400);
            ParallelTransition edgeAnim = new ParallelTransition(fadeIn, weightFade);
            edgeAnim.play();
        } else {
            fadeIn.play();
        }
    }

    private void animateRemoveVertex(VertexElement vertex) {
        ScaleTransition scaleDown = animationService.createScaleTransition(vertex.getCircle(), 0, 0, 400);
        FadeTransition fadeOut = animationService.createFadeAnimation(vertex.getLabel(), 1, 0, 400);
        ParallelTransition removeAnim = new ParallelTransition(scaleDown, fadeOut);

        removeAnim.setOnFinished(e -> {
            graphCanvas.getChildren().removeAll(vertex.getCircle(), vertex.getLabel());
        });

        removeAnim.play();
    }
    private void animateTraversal(List<Integer> traversalOrder, String algorithmName) {
        if (resultArea != null) {
            resultArea.setText(algorithmName + " Traversal: " + traversalOrder.toString());
        }

        // Reset all vertices to default color first
        resetVertexColors();
        Timeline traversalAnim = new Timeline();

        for (int i = 0; i < traversalOrder.size(); i++) {
            final int index = i; // ✅ Already final
            final int vertexId = traversalOrder.get(i); // ✅ Make this final too!

            KeyFrame visitFrame = new KeyFrame(
                    Duration.millis((index + 1) * 800),
                    e -> {
                        highlightVertex(vertexId, Color.web("#10b981")); // ✅ Now safe to use
                        updateProgress((double) (index + 1) / traversalOrder.size());

                        if (index == traversalOrder.size() - 1) {
                            Platform.runLater(() -> {
                                isTraversing = false;
                                updateControlStates();
                                logOperation("TRAVERSAL " + algorithmName + " | Order: " + traversalOrder);
                            });
                        }
                    }
            );
            traversalAnim.getKeyFrames().add(visitFrame);
        }

        traversalAnim.play();
    }

    private void animatePathTraversal(List<Integer> path, String pathName) {
        if (resultArea != null) {
            resultArea.setText(pathName + ": " + path.toString());
        }

        // Reset all vertices and edges to default color
        resetVertexColors();
        resetEdgeColors();
        Timeline pathAnim = new Timeline();

        for (int i = 0; i < path.size(); i++) {
            final int index = i; // ✅ Already final
            final int vertexId = path.get(i); // ✅ Make this final too!

            KeyFrame visitFrame = new KeyFrame(
                    Duration.millis((index + 1) * 600),
                    e -> {
                        highlightVertex(vertexId, Color.web("#f59e0b")); // ✅ Now safe to use

                        if (index > 0) {
                            // Highlight edge between previous and current vertex
                            final int prevVertex = path.get(index - 1); // ✅ Make this final too!
                            highlightEdge(prevVertex, vertexId, Color.web("#f59e0b"));
                        }

                        updateProgress((double) (index + 1) / path.size());

                        if (index == path.size() - 1) {
                            Platform.runLater(() -> {
                                isTraversing = false;
                                updateControlStates();
                                logOperation("PATH_TRAVERSAL " + pathName + " | Path: " + path);
                            });
                        }
                    }
            );
            pathAnim.getKeyFrames().add(visitFrame);
        }

        pathAnim.play();
    }


    private void animateClear(Runnable onComplete) {
        if (vertexElements.isEmpty()) {
            onComplete.run();
            return;
        }

        ParallelTransition clearAnim = new ParallelTransition();

        for (VertexElement vertex : vertexElements.values()) {
            ScaleTransition scaleDown = animationService.createScaleTransition(vertex.getCircle(), 0, 0, 600);
            FadeTransition fadeOut = animationService.createFadeAnimation(vertex.getLabel(), 1, 0, 600);
            ParallelTransition vertexAnim = new ParallelTransition(scaleDown, fadeOut);
            clearAnim.getChildren().add(vertexAnim);
        }

        for (EdgeElement edge : edgeElements) {
            FadeTransition edgeFade = animationService.createFadeAnimation(edge.getLine(), 1, 0, 600);
            clearAnim.getChildren().add(edgeFade);
        }

        clearAnim.setOnFinished(e -> onComplete.run());
        clearAnim.play();
    }



    // ==================== ALGORITHM PSEUDOCODE ====================

    private void updateAlgorithmCode() {
        String algorithm = algorithmComboBox.getValue();
        if (algorithm != null && algorithmCodeViewer != null) {
            String code = getAlgorithmPseudocode(algorithm);
            algorithmCodeViewer.setText(code);
        }
    }

    private String getAlgorithmPseudocode(String algorithm) {
        return switch (algorithm) {
            case "BFS (Breadth-First Search)" ->
                    "// BREADTH-FIRST SEARCH PSEUDOCODE\n\n" +
                            "function BFS(graph, startVertex):\n" +
                            "  visited = new Set()\n" +
                            "  queue = new Queue()\n" +
                            "  result = new List()\n" +
                            "  \n" +
                            "  queue.enqueue(startVertex)\n" +
                            "  visited.add(startVertex)\n" +
                            "  \n" +
                            "  while queue is not empty:\n" +
                            "    current = queue.dequeue()\n" +
                            "    result.add(current)\n" +
                            "    \n" +
                            "    for each neighbor of current:\n" +
                            "      if neighbor not in visited:\n" +
                            "        visited.add(neighbor)\n" +
                            "        queue.enqueue(neighbor)\n" +
                            "  \n" +
                            "  return result\n\n" +
                            "Time Complexity: O(V + E)\n" +
                            "Space Complexity: O(V)\n\n" +
                            "Applications:\n" +
                            "• Shortest path in unweighted graphs\n" +
                            "• Level-order traversal\n" +
                            "• Web crawling\n" +
                            "• Social networking features";

            case "DFS (Depth-First Search)" ->
                    "// DEPTH-FIRST SEARCH PSEUDOCODE\n\n" +
                            "function DFS(graph, startVertex):\n" +
                            "  visited = new Set()\n" +
                            "  result = new List()\n" +
                            "  \n" +
                            "  DFS_recursive(startVertex, visited, result)\n" +
                            "  return result\n" +
                            "\n" +
                            "function DFS_recursive(vertex, visited, result):\n" +
                            "  visited.add(vertex)\n" +
                            "  result.add(vertex)\n" +
                            "  \n" +
                            "  for each neighbor of vertex:\n" +
                            "    if neighbor not in visited:\n" +
                            "      DFS_recursive(neighbor, visited, result)\n\n" +
                            "Time Complexity: O(V + E)\n" +
                            "Space Complexity: O(V) for recursion stack\n\n" +
                            "Applications:\n" +
                            "• Topological sorting\n" +
                            "• Cycle detection\n" +
                            "• Connected components\n" +
                            "• Maze solving";

            case "Shortest Path" ->
                    "// SHORTEST PATH (BFS-BASED) PSEUDOCODE\n\n" +
                            "function shortestPath(graph, source, destination):\n" +
                            "  if source == destination:\n" +
                            "    return [source]\n" +
                            "  \n" +
                            "  parent = new Map()\n" +
                            "  visited = new Set()\n" +
                            "  queue = new Queue()\n" +
                            "  \n" +
                            "  queue.enqueue(source)\n" +
                            "  visited.add(source)\n" +
                            "  parent[source] = null\n" +
                            "  \n" +
                            "  while queue is not empty:\n" +
                            "    current = queue.dequeue()\n" +
                            "    \n" +
                            "    if current == destination:\n" +
                            "      return reconstructPath(parent, source, destination)\n" +
                            "    \n" +
                            "    for each neighbor of current:\n" +
                            "      if neighbor not in visited:\n" +
                            "        visited.add(neighbor)\n" +
                            "        parent[neighbor] = current\n" +
                            "        queue.enqueue(neighbor)\n" +
                            "  \n" +
                            "  return [] // No path found\n\n" +
                            "Time Complexity: O(V + E)\n" +
                            "Space Complexity: O(V)\n\n" +
                            "Applications:\n" +
                            "• Navigation systems\n" +
                            "• Network routing\n" +
                            "• Game AI pathfinding\n" +
                            "• Social network analysis";

            case "Dijkstra's Algorithm" ->
                    "// DIJKSTRA'S ALGORITHM PSEUDOCODE\n\n" +
                            "function dijkstra(graph, source, target):\n" +
                            "  distances = new Map()\n" +
                            "  previous = new Map()\n" +
                            "  unvisited = new PriorityQueue()\n" +
                            "  \n" +
                            "  // Initialize distances\n" +
                            "  for each vertex in graph:\n" +
                            "    distances[vertex] = INFINITY\n" +
                            "    unvisited.add(vertex, INFINITY)\n" +
                            "  \n" +
                            "  distances[source] = 0\n" +
                            "  unvisited.decreaseKey(source, 0)\n" +
                            "  \n" +
                            "  while unvisited is not empty:\n" +
                            "    current = unvisited.extractMin()\n" +
                            "    \n" +
                            "    if current == target:\n" +
                            "      break\n" +
                            "    \n" +
                            "    for each neighbor of current:\n" +
                            "      if neighbor in unvisited:\n" +
                            "        newDistance = distances[current] + weight(current, neighbor)\n" +
                            "        \n" +
                            "        if newDistance < distances[neighbor]:\n" +
                            "          distances[neighbor] = newDistance\n" +
                            "          previous[neighbor] = current\n" +
                            "          unvisited.decreaseKey(neighbor, newDistance)\n" +
                            "  \n" +
                            "  return reconstructPath(previous, source, target)\n\n" +
                            "Time Complexity: O((V + E) log V) with binary heap\n" +
                            "Space Complexity: O(V)\n\n" +
                            "Applications:\n" +
                            "• GPS navigation systems\n" +
                            "• Network routing protocols\n" +
                            "• Social networks (degrees of separation)\n" +
                            "• Flight connections";

            case "A* Pathfinding" ->
                    "// A* PATHFINDING ALGORITHM PSEUDOCODE\n\n" +
                            "function AStar(graph, start, goal, heuristic):\n" +
                            "  openSet = new PriorityQueue()\n" +
                            "  closedSet = new Set()\n" +
                            "  gScore = new Map() // Cost from start\n" +
                            "  fScore = new Map() // Estimated total cost\n" +
                            "  previous = new Map()\n" +
                            "  \n" +
                            "  gScore[start] = 0\n" +
                            "  fScore[start] = heuristic(start, goal)\n" +
                            "  openSet.add(start, fScore[start])\n" +
                            "  \n" +
                            "  while openSet is not empty:\n" +
                            "    current = openSet.extractMin() // Lowest fScore\n" +
                            "    \n" +
                            "    if current == goal:\n" +
                            "      return reconstructPath(previous, start, goal)\n" +
                            "    \n" +
                            "    closedSet.add(current)\n" +
                            "    \n" +
                            "    for each neighbor of current:\n" +
                            "      if neighbor in closedSet:\n" +
                            "        continue\n" +
                            "      \n" +
                            "      tentativeGScore = gScore[current] + distance(current, neighbor)\n" +
                            "      \n" +
                            "      if neighbor not in openSet:\n" +
                            "        openSet.add(neighbor, tentativeGScore + heuristic(neighbor, goal))\n" +
                            "      else if tentativeGScore >= gScore[neighbor]:\n" +
                            "        continue\n" +
                            "      \n" +
                            "      previous[neighbor] = current\n" +
                            "      gScore[neighbor] = tentativeGScore\n" +
                            "      fScore[neighbor] = tentativeGScore + heuristic(neighbor, goal)\n" +
                            "  \n" +
                            "  return [] // No path found\n\n" +
                            "Time Complexity: O(b^d) where b is branching factor, d is depth\n" +
                            "Space Complexity: O(b^d)\n\n" +
                            "Applications:\n" +
                            "• Game AI pathfinding\n" +
                            "• Robot navigation\n" +
                            "• Puzzle solving\n" +
                            "• Route optimization";

            case "Topological Sort" ->
                    "// TOPOLOGICAL SORT PSEUDOCODE (KAHN'S ALGORITHM)\n\n" +
                            "function topologicalSort(graph):\n" +
                            "  // Only works for Directed Acyclic Graphs (DAG)\n" +
                            "  inDegree = new Map()\n" +
                            "  result = new List()\n" +
                            "  queue = new Queue()\n" +
                            "  \n" +
                            "  // Calculate in-degrees\n" +
                            "  for each vertex in graph:\n" +
                            "    inDegree[vertex] = 0\n" +
                            "  \n" +
                            "  for each edge (u, v) in graph:\n" +
                            "    inDegree[v] += 1\n" +
                            "  \n" +
                            "  // Find vertices with no incoming edges\n" +
                            "  for each vertex in graph:\n" +
                            "    if inDegree[vertex] == 0:\n" +
                            "      queue.enqueue(vertex)\n" +
                            "  \n" +
                            "  while queue is not empty:\n" +
                            "    current = queue.dequeue()\n" +
                            "    result.add(current)\n" +
                            "    \n" +
                            "    for each neighbor of current:\n" +
                            "      inDegree[neighbor] -= 1\n" +
                            "      if inDegree[neighbor] == 0:\n" +
                            "        queue.enqueue(neighbor)\n" +
                            "  \n" +
                            "  if result.size() != graph.vertexCount:\n" +
                            "    throw \"Graph has cycle - topological sort impossible\"\n" +
                            "  \n" +
                            "  return result\n\n" +
                            "Time Complexity: O(V + E)\n" +
                            "Space Complexity: O(V)\n\n" +
                            "Applications:\n" +
                            "• Task scheduling with dependencies\n" +
                            "• Build systems (makefile)\n" +
                            "• Course prerequisite planning\n" +
                            "• Compilation order";

            default ->
                    "// Select an algorithm to view its pseudocode\n\n" +
                            "Available Advanced Algorithms:\n" +
                            "• BFS - Breadth-First Search\n" +
                            "• DFS - Depth-First Search\n" +
                            "• Shortest Path - Unweighted shortest path\n" +
                            "• Dijkstra - Shortest path (weighted graphs)\n" +
                            "• A* - Heuristic pathfinding\n" +
                            "• Topological Sort - DAG ordering\n" +
                            "• Find All Paths - Multiple path discovery\n" +
                            "• Detect Cycles - Cycle detection\n\n" +
                            "Each algorithm includes:\n" +
                            "- Detailed pseudocode\n" +
                            "- Java implementation\n" +
                            "- Time & space complexity\n" +
                            "- Real-world applications";
        };
    }

    // ==================== HELPER METHODS ====================

    private void highlightVertex(int vertexId, Color color) {
        VertexElement vertex = vertexElements.get(vertexId);
        if (vertex != null) {
            vertex.getCircle().setFill(color);

            // Add pulsing animation
            ScaleTransition pulse = animationService.createScaleTransition(vertex.getCircle(), 1.3, 1.3, 300);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(2);
            pulse.play();
        }
    }

    private void highlightEdge(int source, int destination, Color color) {
        for (EdgeElement edge : edgeElements) {
            if (edge.getSource() == source && edge.getDestination() == destination) {
                edge.getLine().setStroke(color);
                edge.getLine().setStrokeWidth(4);
                break;
            }
        }
    }

    private void resetVertexColors() {
        for (VertexElement vertex : vertexElements.values()) {
            vertex.getCircle().setFill(Color.web("#3b82f6"));
        }
    }

    private void resetEdgeColors() {
        for (EdgeElement edge : edgeElements) {
            edge.getLine().setStroke(Color.web("#64748b"));
            edge.getLine().setStrokeWidth(2);
        }
    }

    private void centerOnVertex(VertexElement vertex) {
        if (canvasScrollPane != null) {
            double hValue = (vertex.getX() - canvasScrollPane.getViewportBounds().getWidth() / 2) /
                    (graphCanvas.getBoundsInLocal().getWidth() - canvasScrollPane.getViewportBounds().getWidth());
            double vValue = (vertex.getY() - canvasScrollPane.getViewportBounds().getHeight() / 2) /
                    (graphCanvas.getBoundsInLocal().getHeight() - canvasScrollPane.getViewportBounds().getHeight());

            canvasScrollPane.setHvalue(Math.max(0, Math.min(1, hValue)));
            canvasScrollPane.setVvalue(Math.max(0, Math.min(1, vValue)));
        }
    }

    private void updateEdgesForVertex(int vertexId) {
        VertexElement vertex = vertexElements.get(vertexId);
        if (vertex == null) return;

        for (EdgeElement edge : edgeElements) {
            if (edge.getSource() == vertexId) {
                edge.getLine().setStartX(vertex.getX());
                edge.getLine().setStartY(vertex.getY());
            }

            if (edge.getDestination() == vertexId) {
                edge.getLine().setEndX(vertex.getX());
                edge.getLine().setEndY(vertex.getY());
            }

            // Update weight label position if it exists
            if (edge.getWeightLabel() != null) {
                double midX = (edge.getLine().getStartX() + edge.getLine().getEndX()) / 2;
                double midY = (edge.getLine().getStartY() + edge.getLine().getEndY()) / 2;
                edge.getWeightLabel().setX(midX);
                edge.getWeightLabel().setY(midY);
            }
        }
    }

    private void updateAllEdges() {
        for (EdgeElement edge : edgeElements) {
            VertexElement sourceVertex = vertexElements.get(edge.getSource());
            VertexElement destVertex = vertexElements.get(edge.getDestination());

            if (sourceVertex != null && destVertex != null) {
                edge.getLine().setStartX(sourceVertex.getX());
                edge.getLine().setStartY(sourceVertex.getY());
                edge.getLine().setEndX(destVertex.getX());
                edge.getLine().setEndY(destVertex.getY());

                // Update weight label position
                if (edge.getWeightLabel() != null) {
                    double midX = (sourceVertex.getX() + destVertex.getX()) / 2;
                    double midY = (sourceVertex.getY() + destVertex.getY()) / 2;
                    edge.getWeightLabel().setX(midX);
                    edge.getWeightLabel().setY(midY);
                }
            }
        }
    }

    private void redrawGraph() {
        Platform.runLater(() -> {
            graphCanvas.getChildren().clear();

            // Redraw edges first (behind vertices)
            for (EdgeElement edge : edgeElements) {
                if (vertexElements.containsKey(edge.getSource()) &&
                        vertexElements.containsKey(edge.getDestination())) {
                    graphCanvas.getChildren().add(edge.getLine());
                    if (edge.getWeightLabel() != null) {
                        graphCanvas.getChildren().add(edge.getWeightLabel());
                    }
                }
            }

            // Redraw vertices
            for (VertexElement vertex : vertexElements.values()) {
                graphCanvas.getChildren().addAll(vertex.getCircle(), vertex.getLabel());
            }
        });
    }

    private void updateProgress(double progress) {
        if (operationProgress != null) {
            operationProgress.setProgress(progress);
        }
    }
    @FXML
    private void copyCodeToClipboard() {
        try {
            // Get the algorithm code content
            String algorithm = algorithmComboBox.getValue();
            String codeContent = getAlgorithmCodeImplementation(algorithm);

            if (codeContent == null || codeContent.trim().isEmpty()) {
                showAlert("No Code Available", "No implementation code available for the selected algorithm.");
                return;
            }

            // Copy to clipboard using modern JavaFX approach
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(codeContent);
            clipboard.setContent(content);

            // Show success feedback with animation
            showSuccessTooltip("Code copied to clipboard!");

            // Log operation
            logOperation("COPY_CODE " + algorithm + " | Copied to clipboard");

            System.out.println("✅ Algorithm code copied to clipboard: " + algorithm);

        } catch (Exception e) {
            System.err.println("❌ Error copying code to clipboard: " + e.getMessage());
            showAlert("Copy Failed", "Failed to copy code to clipboard: " + e.getMessage());
        }
    }

    /**
     * Get the actual implementation code for algorithms
     */
    private String getAlgorithmCodeImplementation(String algorithm) {
        if (algorithm == null) return null;

        return switch (algorithm) {
            case "BFS (Breadth-First Search)" ->
                    """
                    // BREADTH-FIRST SEARCH IMPLEMENTATION
                    public List<Integer> bfs(int startVertex) {
                        List<Integer> result = new ArrayList<>();
                        Set<Integer> visited = new HashSet<>();
                        Queue<Integer> queue = new LinkedList<>();
                        
                        queue.offer(startVertex);
                        visited.add(startVertex);
                        
                        while (!queue.isEmpty()) {
                            int current = queue.poll();
                            result.add(current);
                            
                            for (int neighbor : adjacencyList.get(current)) {
                                if (!visited.contains(neighbor)) {
                                    visited.add(neighbor);
                                    queue.offer(neighbor);
                                }
                            }
                        }
                        
                        return result;
                    }""";

            case "DFS (Depth-First Search)" ->
                    """
                    // DEPTH-FIRST SEARCH IMPLEMENTATION
                    public List<Integer> dfs(int startVertex) {
                        List<Integer> result = new ArrayList<>();
                        Set<Integer> visited = new HashSet<>();
                        dfsHelper(startVertex, visited, result);
                        return result;
                    }
                    
                    private void dfsHelper(int vertex, Set<Integer> visited, List<Integer> result) {
                        visited.add(vertex);
                        result.add(vertex);
                        
                        for (int neighbor : adjacencyList.get(vertex)) {
                            if (!visited.contains(neighbor)) {
                                dfsHelper(neighbor, visited, result);
                            }
                        }
                    }""";

            case "Dijkstra's Algorithm" ->
                    """
                    // DIJKSTRA'S SHORTEST PATH ALGORITHM
                    public Map<Integer, Double> dijkstra(int startVertex) {
                        Map<Integer, Double> distances = new HashMap<>();
                        Set<Integer> unvisited = new HashSet<>();
                        
                        // Initialize distances
                        for (int vertex : vertices) {
                            distances.put(vertex, vertex == startVertex ? 0.0 : Double.POSITIVE_INFINITY);
                            unvisited.add(vertex);
                        }
                        
                        while (!unvisited.isEmpty()) {
                            int current = unvisited.stream()
                                .min(Comparator.comparingDouble(distances::get))
                                .orElse(-1);
                                
                            if (distances.get(current) == Double.POSITIVE_INFINITY) break;
                            
                            unvisited.remove(current);
                            
                            for (Edge edge : getEdges(current)) {
                                int neighbor = edge.destination;
                                double newDistance = distances.get(current) + edge.weight;
                                
                                if (newDistance < distances.get(neighbor)) {
                                    distances.put(neighbor, newDistance);
                                }
                            }
                        }
                        
                        return distances;
                    }""";

            default -> {
                // Try to get from algorithm code viewer if available
                if (algorithmCodeViewer != null && !algorithmCodeViewer.getText().trim().isEmpty()) {
                    yield algorithmCodeViewer.getText();
                }
                yield "// No implementation available for: " + algorithm;
            }
        };
    }

    /**
     * Show success feedback with modern styling
     */
    private void showSuccessTooltip(String message) {
        if (statusLabel != null) {
            String originalText = statusLabel.getText();
            String originalStyle = statusLabel.getStyle();

            // Update with success styling
            statusLabel.setText("✅ " + message);
            statusLabel.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");

            // Reset after 3 seconds
            Timeline resetTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(3), e -> {
                        statusLabel.setText(originalText);
                        statusLabel.setStyle(originalStyle);
                    })
            );
            resetTimeline.play();
        }
    }

    private void updateDisplay() {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(graphModel.isEmpty() ?
                        "Graph is Empty" :
                        "Graph: " + graphModel.getVertexCount() + " vertices, " + graphModel.getEdgeCount() + " edges");
            }

            if (vertexCountLabel != null) {
                vertexCountLabel.setText("Vertices: " + graphModel.getVertexCount());
            }

            if (edgeCountLabel != null) {
                edgeCountLabel.setText("Edges: " + graphModel.getEdgeCount());
            }

            if (densityLabel != null && graphModel.getVertexCount() > 0) {
                int maxEdges = graphModel.isDirected() ?
                        graphModel.getVertexCount() * (graphModel.getVertexCount() - 1) :
                        graphModel.getVertexCount() * (graphModel.getVertexCount() - 1) / 2;
                double density = maxEdges > 0 ? (double) graphModel.getEdgeCount() / maxEdges * 100 : 0;
                densityLabel.setText("Density: " + String.format("%.1f", density) + "%");
            }

            updateControlStates();
        });
    }

    private void updateControlStates() {
        boolean isEmpty = graphModel.isEmpty();

        // Enhanced operation state checking - includes all interactive states
        boolean isOperationInProgress = isTraversing ||
                isDragging || isEnhancedDragging || isDragHoldActive;

        // ==================== BUTTON CONTROLS ====================
        // Core graph operation buttons
        if (addVertexButton != null) addVertexButton.setDisable(isOperationInProgress);
        if (removeVertexButton != null) removeVertexButton.setDisable(isEmpty || isOperationInProgress);
        if (addEdgeButton != null) addEdgeButton.setDisable(isOperationInProgress);
        if (removeEdgeButton != null) removeEdgeButton.setDisable(isEmpty || isOperationInProgress);

        // Algorithm and traversal buttons
        if (traverseButton != null) traverseButton.setDisable(isEmpty || isOperationInProgress);
        if (clearButton != null) clearButton.setDisable(isEmpty || isOperationInProgress);
        if (randomButton != null) randomButton.setDisable(isOperationInProgress);

        // Search and highlight buttons (allow during regular drag, disable during enhanced operations)
        boolean isEnhancedOperationOnly = isTraversing || isEnhancedDragging;
        if (searchButton != null) searchButton.setDisable(isEnhancedOperationOnly);
        if (highlightButton != null) highlightButton.setDisable(isEnhancedOperationOnly);

        // ==================== LAYOUT CONTROLS ====================
        if (circularLayoutButton != null) circularLayoutButton.setDisable(isEmpty || isOperationInProgress);
        if (forceLayoutButton != null) forceLayoutButton.setDisable(isEmpty || isOperationInProgress);
        if (resetLayoutButton != null) resetLayoutButton.setDisable(isEmpty || isOperationInProgress);

        // ==================== UNDO/REDO SYSTEM ====================
        if (undoButton != null) undoButton.setDisable(undoStack.isEmpty() || isOperationInProgress);
        if (redoButton != null) redoButton.setDisable(redoStack.isEmpty() || isOperationInProgress);

        // ==================== INPUT FIELDS ====================
        if (addVertexField != null) addVertexField.setDisable(isOperationInProgress);
        if (sourceField != null) sourceField.setDisable(isOperationInProgress);
        if (destinationField != null) destinationField.setDisable(isOperationInProgress);
        if (startVertexField != null) startVertexField.setDisable(isOperationInProgress);
        if (endVertexField != null) endVertexField.setDisable(isOperationInProgress);

        // Search field (allow during regular drag)
        if (searchVertexField != null) searchVertexField.setDisable(isEnhancedOperationOnly);

        // ==================== CONFIGURATION CONTROLS ====================
        if (algorithmComboBox != null) algorithmComboBox.setDisable(isOperationInProgress);
        if (directedCheckBox != null) directedCheckBox.setDisable(isOperationInProgress);
        if (weightedCheckBox != null) weightedCheckBox.setDisable(isOperationInProgress);

        // Display toggles (allow during drag for better UX)
        if (showLabelsCheckBox != null) showLabelsCheckBox.setDisable(isEnhancedOperationOnly);
        if (showWeightsCheckBox != null) showWeightsCheckBox.setDisable(isEnhancedOperationOnly);

        // ==================== FILE OPERATIONS ====================
        if (saveButton != null) saveButton.setDisable(isOperationInProgress);
        if (loadButton != null) loadButton.setDisable(isOperationInProgress);

        // ==================== ZOOM CONTROLS ====================
        // Allow zoom operations during regular drag, disable during intensive operations
        if (zoomInButton != null) zoomInButton.setDisable(isEnhancedOperationOnly);
        if (zoomOutButton != null) zoomOutButton.setDisable(isEnhancedOperationOnly);
        if (resetZoomButton != null) resetZoomButton.setDisable(isEnhancedOperationOnly);

        // ==================== DEBUG INFO ====================
        if (isOperationInProgress && statusLabel != null) {
            // Provide user feedback about current operation state
            String operation = "";
            if (isTraversing) operation = "Algorithm executing";
            else if (isEnhancedDragging) operation = "Enhanced drag active";
            else if (isDragging) operation = "Dragging vertex";
            else if (isDragHoldActive) operation = "Hold detected";

            // Don't override specific drag status messages
            if (!statusLabel.getText().contains("Dragging vertex") && !operation.isEmpty()) {
                statusLabel.setText("🔄 " + operation + " - Controls temporarily disabled");
            }
        }
    }

    private void logOperation(String operation) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logEntry = "[" + timestamp + "] " + operation;

        if (operationHistory != null) {
            operationHistory.appendText(logEntry + "\n");
            operationHistory.setScrollTop(Double.MAX_VALUE);
        }

        System.out.println("Graph Operation: " + logEntry);
    }

    @FXML
    private void goBack() {
        try {
            if (currentAnimation != null) {
                currentAnimation.stop();
            }

            Stage currentStage = (Stage) graphCanvas.getScene().getWindow();
            // ✅ Use hide() instead of close() for back navigation
            currentStage.hide();
            System.out.println("Advance Graph module hidden (can be reopened)");

        } catch (Exception e) {
            System.err.println("Error hiding Graph window: " + e.getMessage());
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

    // ==================== INNER CLASSES ====================

    private static class VertexElement {
        private final Circle circle;
        private final Text label;
        private final int id;
        private double x, y;
        private double forceX = 0, forceY = 0;

        public VertexElement(Circle circle, Text label, int id, double x, double y) {
            this.circle = circle;
            this.label = label;
            this.id = id;
            this.x = x;
            this.y = y;
        }
        // Add this field and methods to your VertexElement class
        private Animation highlightAnimation;

        public Animation getHighlightAnimation() {
            return highlightAnimation;
        }

        public void setHighlightAnimation(Animation animation) {
            this.highlightAnimation = animation;
        }

        public Circle getCircle() { return circle; }
        public Text getLabel() { return label; }
        public int getId() { return id; }
        public double getX() { return x; }
        public double getY() { return y; }
        public double getForceX() { return forceX; }
        public double getForceY() { return forceY; }

        public void setPosition(double x, double y) {
            this.x = x;
            this.y = y;
            circle.setCenterX(x);
            circle.setCenterY(y);
            label.setX(x - 8);
            label.setY(y + 6);
        }

        public void translate(double deltaX, double deltaY) {
            setPosition(x + deltaX, y + deltaY);
        }

        public boolean contains(double mouseX, double mouseY) {
            double distance = Math.sqrt((mouseX - x) * (mouseX - x) + (mouseY - y) * (mouseY - y));
            return distance <= circle.getRadius();
        }

        public void resetForces() {
            forceX = 0;
            forceY = 0;
        }

        public void addForce(double fx, double fy) {
            forceX += fx;
            forceY += fy;
        }

        public void scaleForces(double scale) {
            forceX *= scale;
            forceY *= scale;
        }
    }

    private static class EdgeElement {
        private final Line line;
        private final int source;
        private final int destination;
        private final double weight;
        private final Text weightLabel;

        public EdgeElement(Line line, int source, int destination, double weight, Text weightLabel) {
            this.line = line;
            this.source = source;
            this.destination = destination;
            this.weight = weight;
            this.weightLabel = weightLabel;
        }

        public Line getLine() { return line; }
        public int getSource() { return source; }
        public int getDestination() { return destination; }
        public double getWeight() { return weight; }
        public Text getWeightLabel() { return weightLabel; }
    }

// ==================== ENHANCED GRAPH STATE SYSTEM ====================
// ==================== ENHANCED GRAPH STATE SYSTEM ====================

    /**
     * 🎯 Complete GraphState implementation with full visual state capture
     */
    private static class GraphState {
        private String description;
        private final Map<String, Object> stateData;

        // Deep copy collections for complete state capture
        private final Map<Integer, VertexData> vertexStates;
        private final List<EdgeData> edgeStates;
        private final boolean wasDirected;
        private final boolean wasWeighted;

        public GraphState(GraphModel model, Map<Integer, VertexElement> vertices, List<EdgeElement> edges) {
            this.description = "";
            this.stateData = new HashMap<>();
            this.vertexStates = new HashMap<>();
            this.edgeStates = new ArrayList<>();
            this.wasDirected = model.isDirected();
            this.wasWeighted = model.isWeighted();

            // 🎯 CAPTURE COMPLETE VERTEX STATE INCLUDING STYLING
            for (Map.Entry<Integer, VertexElement> entry : vertices.entrySet()) {
                int vertexId = entry.getKey();
                VertexElement vertex = entry.getValue();

                VertexData vertexData = new VertexData(
                        vertexId,
                        vertex.getX(),
                        vertex.getY(),
                        vertex.getCircle().getStyle(),
                        vertex.getLabel().getText(),
                        vertex.getLabel().getStyle()
                );
                vertexStates.put(vertexId, vertexData);
            }

            // 🎯 CAPTURE COMPLETE EDGE STATE
            for (EdgeElement edge : edges) {
                EdgeData edgeData = new EdgeData(
                        edge.getSource(),
                        edge.getDestination(),
                        edge.getWeight(),
                        edge.getLine().getStyle(),
                        edge.getWeightLabel() != null ? edge.getWeightLabel().getText() : null,
                        edge.getWeightLabel() != null ? edge.getWeightLabel().getStyle() : null
                );
                edgeStates.add(edgeData);
            }

            // Store additional graph model data
            stateData.put("vertexCount", model.getVertexCount());
            stateData.put("edgeCount", model.getEdgeCount());
            stateData.put("density", model.getDensity());
        }

        // Getters
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<Integer, VertexData> getVertexStates() { return vertexStates; }
        public List<EdgeData> getEdgeStates() { return edgeStates; }
        public boolean wasDirected() { return wasDirected; }
        public boolean wasWeighted() { return wasWeighted; }
        public Map<String, Object> getStateData() { return stateData; }

        // 📊 Inner data classes for complete state storage
        public static class VertexData {
            public final int id;
            public final double x, y;
            public final String circleStyle, labelText, labelStyle;

            public VertexData(int id, double x, double y, String circleStyle,
                              String labelText, String labelStyle) {
                this.id = id;
                this.x = x;
                this.y = y;
                this.circleStyle = circleStyle != null ? circleStyle :
                        "-fx-fill: #3b82f6; -fx-stroke: #1d4ed8; -fx-stroke-width: 2;";
                this.labelText = labelText != null ? labelText : "";
                this.labelStyle = labelStyle != null ? labelStyle :
                        "-fx-fill: white; -fx-font-weight: bold;";
            }
        }

        public static class EdgeData {
            public final int source, destination;
            public final double weight;
            public final String lineStyle, weightText, weightStyle;

            public EdgeData(int source, int destination, double weight,
                            String lineStyle, String weightText, String weightStyle) {
                this.source = source;
                this.destination = destination;
                this.weight = weight;
                this.lineStyle = lineStyle != null ? lineStyle :
                        "-fx-stroke: #64748b; -fx-stroke-width: 2;";
                this.weightText = weightText;
                this.weightStyle = weightStyle;
            }
        }
    }


}
