package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ENTERPRISE-GRADE NAVIGATION SERVICE
 * Enhanced with professional maximization support, multi-monitor handling,
 * and responsive layout management for the Algorithm Simulator
 */
public class NavigationService {
    private final List<Stage> openWindows;

    // Configuration constants
    private static final double WINDOW_OFFSET = 30.0;
    private static final int MAX_CONCURRENT_WINDOWS = 10; // Increased for better productivity
    private static final String ICON_PATH = "/icons/algorithm-simulator.png";
    private static final double SCREEN_USAGE_FACTOR = 0.92; // Allow more screen usage

    // Animation constants
    private static final Duration ANIMATION_DURATION = Duration.millis(250);
    private static final Duration FADE_DURATION = Duration.millis(200);
    private final Map<String, Stage> stageCache = new HashMap<>();

    public NavigationService() {
        this.openWindows = new ArrayList<>();
        System.out.println("🚀 NavigationService initialized with enhanced maximization support");
    }

    /**
     * ENHANCED: Async module opening with better error handling and responsive
     * design
     */
    public CompletableFuture<Stage> openModuleAsync(String fxmlPath, String title, int width, int height) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return openModuleSync(fxmlPath, title, width, height);
            } catch (Exception e) {
                Platform.runLater(() -> handleModuleError(title, e));
                return null;
            }
        });
    }

    private void addStylesheets(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load main.css");
        }
    }

    private void showWindow(Stage stage) {
        stage.show(); // That's it - no complex animations
    }

    private void positionWindow(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        if (openWindows.isEmpty()) {
            // ✅ PERFECT CENTER for first window
            double centerX = screen.getMinX() + (screen.getWidth() - stage.getWidth()) / 2;
            double centerY = screen.getMinY() + (screen.getHeight() - stage.getHeight()) / 2;

            stage.setX(centerX);
            stage.setY(centerY);

            System.out.println("🎯 First window centered at: (" + centerX + ", " + centerY + ")");
        } else {
            // ✅ IMPROVED CASCADE from center, not fixed offset
            double cascadeOffset = openWindows.size() * WINDOW_OFFSET; // Uses your 30px constant

            double baseX = screen.getMinX() + (screen.getWidth() - stage.getWidth()) / 2;
            double baseY = screen.getMinY() + (screen.getHeight() - stage.getHeight()) / 2;

            // Apply cascade offset to centered position
            stage.setX(baseX + cascadeOffset);
            stage.setY(baseY + cascadeOffset);

            System.out.println(
                    "📐 Cascaded window at: (" + (baseX + cascadeOffset) + ", " + (baseY + cascadeOffset) + ")");
        }
    }

    /**
     * ENHANCED: Synchronous module opening with full maximization support
     */
    public Stage openModule(String fxmlPath, String title, int width, int height) {
        return openModuleSync(fxmlPath, title, width, height);
    }

    private Stage openModuleSync(String fxmlPath, String title, int width, int height) {
        try {
            System.out.println("🔍 Opening module: " + title);

            // ✅ ENHANCED: Check stage cache first
            if (stageCache.containsKey(title)) {
                Stage cachedStage = stageCache.get(title);
                if (cachedStage.isShowing()) {
                    // Window is already visible, bring to front
                    System.out.println("🔄 Bringing existing window to front: " + title);
                    cachedStage.toFront();
                    cachedStage.requestFocus();
                    return cachedStage;
                } else {
                    // Window exists but hidden, show it
                    System.out.println("♻️ Reusing hidden stage: " + title);
                    Platform.runLater(() -> {
                        cachedStage.show();
                        cachedStage.toFront();
                        cachedStage.requestFocus();
                    });
                    // Add back to tracking since it's now visible
                    if (!openWindows.contains(cachedStage)) {
                        openWindows.add(cachedStage);
                    }
                    return cachedStage;
                }
            }

            // Enhanced window limit check
            if (openWindows.size() >= MAX_CONCURRENT_WINDOWS) {
                showWarning("Window Limit Reached",
                        "Maximum of " + MAX_CONCURRENT_WINDOWS + " windows allowed for optimal performance.");
                return null;
            }

            // Enhanced FXML loading
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                showError("FXML Resource Missing", "Could not locate FXML file: " + fxmlPath);
                return null;
            }

            Parent root = loader.load();
            Object controller = loader.getController();

            // Create scroll pane
            ScrollPane scrollPane = createEnhancedScrollPane(root);

            // Create stage
            Stage moduleStage = createModuleStage(title, width, height);

            // Create scene
            Scene scene = new Scene(scrollPane, width, height);
            addStylesheets(scene);
            ThemeManager.getInstance().registerScene(scene);
            moduleStage.setScene(scene);

            // Position window
            positionWindow(moduleStage);

            // Add to tracking
            openWindows.add(moduleStage);

            // ✅ ENHANCED: Add to cache for reuse
            stageCache.put(title, moduleStage);

            // ⭐ ENHANCED LIFECYCLE SETUP:
            setupEnhancedWindowLifecycle(moduleStage, controller, title);

            // Setup keyboard shortcuts
            setupAdvancedKeyboardShortcuts(scene, moduleStage);

            // Show window
            showWithEnhancedAnimation(moduleStage);

            System.out.println("✅ Module opened successfully: " + title);
            return moduleStage;

        } catch (Exception e) {
            handleModuleError(title, e);
            return null;
        }
    }

    /**
     * ✅ ENHANCED: Proper window lifecycle with cache management
     */
    private void setupEnhancedWindowLifecycle(Stage stage, Object controller, String title) {
        // Handle window hiding (for back button navigation)
        stage.setOnHiding(e -> {
            System.out.println("👁️ Stage hidden (can be reopened): " + title);
            // Remove from openWindows but keep in cache
            openWindows.remove(stage);
        });

        // Handle window closing (red X button or explicit close)
        stage.setOnCloseRequest(e -> {
            System.out.println("🗑️ Stage closing completely: " + title);

            // Remove from both tracking lists
            openWindows.remove(stage);
            stageCache.remove(title);

            // Cleanup controller - check both BaseController and Cleanable interface
            if (controller instanceof Cleanable) {
                try {
                    ((Cleanable) controller).cleanup();
                } catch (Exception ex) {
                    System.err.println("Cleanup error: " + ex.getMessage());
                }
            } else if (controller instanceof BaseController) {
                try {
                    ((BaseController) controller).cleanup();
                } catch (Exception ex) {
                    System.err.println("Cleanup error: " + ex.getMessage());
                }
            }

            // Unregister from theme manager
            if (stage.getScene() != null) {
                ThemeManager.getInstance().unregisterScene(stage.getScene());
            }

            System.out.println("✅ Stage cleanup completed: " + title);
        });
    }

    /**
     * ENHANCED: Responsive scroll pane with better performance
     */
    private ScrollPane createEnhancedScrollPane(Parent content) {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().addAll("scroll-pane", "enhanced-scroll");

        // Enhanced smooth scrolling with momentum
        scrollPane.setPannable(true);
        scrollPane.setVvalue(0);
        scrollPane.setHvalue(0);

        // ENHANCED: Advanced scroll behaviors
        scrollPane.setOnScroll(event -> {
            if (event.isControlDown()) {
                // Future: Ctrl + scroll for zoom functionality
                event.consume();
            } else {
                // Smooth momentum scrolling
                double deltaY = event.getDeltaY() * 0.002;
                scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
            }
        });

        // Performance optimization
        scrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
            // Optimize content rendering for large scroll areas
            if (newBounds.getWidth() > 2000 || newBounds.getHeight() > 1500) {
                scrollPane.getStyleClass().add("large-viewport");
            }
        });

        return scrollPane;
    }

    private Stage createModuleStage(String title, int width, int height) {
        Stage stage = new Stage();
        stage.setTitle(title);

        // CRITICAL: Simple, clean initialization
        stage.initStyle(StageStyle.DECORATED);
        stage.initModality(Modality.NONE);
        stage.setResizable(true);
        stage.setAlwaysOnTop(false); // The minimize fix

        // Set owner
        try {
            Stage primaryStage = AlgorithmSimulatorApplication.getPrimaryStage();
            if (primaryStage != null && primaryStage.isShowing()) {
                stage.initOwner(primaryStage);
            }
        } catch (Exception e) {
            // Ignore
        }

        // Simple size setup
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setWidth(Math.min(width, screen.getWidth() * 0.9));
        stage.setHeight(Math.min(height, screen.getHeight() * 0.9));
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        return stage;
    }

    /**
     * SIMPLIFIED: Essential window lifecycle only
     */
    private void setupWindowLifecycle(Stage stage, Object controller) {
        // Only the essential close handling
        stage.setOnCloseRequest(e -> {
            openWindows.remove(stage);

            // Cleanup controller - check both interfaces
            if (controller instanceof Cleanable) {
                try {
                    ((Cleanable) controller).cleanup();
                } catch (Exception ex) {
                    System.err.println("Cleanup error: " + ex.getMessage());
                }
            } else if (controller instanceof BaseController) {
                try {
                    ((BaseController) controller).cleanup();
                } catch (Exception ex) {
                    System.err.println("Cleanup error: " + ex.getMessage());
                }
            }

            // Unregister from theme manager
            if (stage.getScene() != null) {
                ThemeManager.getInstance().unregisterScene(stage.getScene());
            }
        });
    }

    /**
     * ENHANCED: Comprehensive keyboard shortcuts with minimize support
     */
    private void setupAdvancedKeyboardShortcuts(Scene scene, Stage stage) {
        scene.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case W:
                        // Ctrl+W to close window
                        stage.close();
                        event.consume();
                        break;
                    case T:
                        // Ctrl+T to toggle theme
                        ThemeManager.getInstance().toggleTheme();
                        event.consume();
                        break;
                    case MINUS:
                    case SUBTRACT:
                        // Ctrl+- to minimize (ENHANCED)
                        stage.setIconified(true);
                        System.out.println("➖ Window minimized via Ctrl+-: " + stage.getTitle());
                        event.consume();
                        break;
                    case DOWN:
                        // Ctrl+Down to minimize (alternative)
                        if (event.isControlDown()) {
                            stage.setIconified(true);
                            System.out.println("➖ Window minimized via Ctrl+Down: " + stage.getTitle());
                            event.consume();
                        }
                        break;
                    case PLUS:
                    case EQUALS:
                    case ADD:
                        // Ctrl++ to maximize/restore
                        stage.setMaximized(!stage.isMaximized());
                        event.consume();
                        break;
                    case UP:
                        // Ctrl+Up to maximize (alternative)
                        if (event.isControlDown()) {
                            stage.setMaximized(!stage.isMaximized());
                            event.consume();
                        }
                        break;
                    case M:
                        // Ctrl+M to maximize/restore (alternative)
                        stage.setMaximized(!stage.isMaximized());
                        event.consume();
                        break;
                    case D:
                        // Ctrl+D to minimize (alternative shortcut)
                        stage.setIconified(true);
                        System.out.println("➖ Window minimized via Ctrl+D: " + stage.getTitle());
                        event.consume();
                        break;
                    case R:
                        // Ctrl+R to refresh/reset
                        System.out.println("🔄 Refresh requested for: " + stage.getTitle());
                        event.consume();
                        break;
                    case DIGIT1:
                    case DIGIT2:
                    case DIGIT3:
                    case DIGIT4:
                    case DIGIT5:
                        // Ctrl+1-5 to switch between open windows
                        int windowIndex = Integer.parseInt(event.getCode().getName().substring(5)) - 1;
                        if (windowIndex < openWindows.size()) {
                            Stage targetWindow = openWindows.get(windowIndex);
                            if (targetWindow.isIconified()) {
                                targetWindow.setIconified(false); // Restore if minimized
                            }
                            targetWindow.toFront();
                            targetWindow.requestFocus();
                        }
                        event.consume();
                        break;
                }
            }

            // Standalone key shortcuts
            switch (event.getCode()) {
                case ESCAPE:
                    // Escape to close window
                    stage.close();
                    event.consume();
                    break;
                case F11:
                    // F11 to toggle fullscreen
                    stage.setFullScreen(!stage.isFullScreen());
                    event.consume();
                    break;
                case F9:
                    // F9 to minimize (common shortcut)
                    stage.setIconified(true);
                    System.out.println("➖ Window minimized via F9: " + stage.getTitle());
                    event.consume();
                    break;
                case F10:
                    // F10 to maximize/restore
                    stage.setMaximized(!stage.isMaximized());
                    event.consume();
                    break;
                case F5:
                    // F5 to refresh
                    System.out.println("🔄 F5 refresh requested for: " + stage.getTitle());
                    event.consume();
                    break;
            }
        });
    }

    /**
     * ENHANCED: Professional show animation with window controls verification
     */
    private void showWithEnhancedAnimation(Stage stage) {
        // Start with invisible stage
        stage.setOpacity(0.0);
        stage.show();

        // ADDED: Verify window controls are enabled after showing
        Platform.runLater(() -> {
            // Force enable all window operations
            stage.setResizable(true);
            stage.setIconified(false);

            // Debug: Print window state
            System.out.println("🔍 Window controls state for " + stage.getTitle() + ":");
            System.out.println("   - Resizable: " + stage.isResizable());
            System.out.println("   - Iconified: " + stage.isIconified());
            System.out.println("   - Maximized: " + stage.isMaximized());
            System.out.println("   - Showing: " + stage.isShowing());
        });

        // Create multiple parallel animations for professional feel
        FadeTransition contentFade = new FadeTransition(ANIMATION_DURATION, stage.getScene().getRoot());
        contentFade.setFromValue(0.0);
        contentFade.setToValue(1.0);
        contentFade.setInterpolator(Interpolator.EASE_OUT);

        // Stage opacity animation
        Timeline stageOpacity = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(stage.opacityProperty(), 0.0)),
                new KeyFrame(ANIMATION_DURATION, new KeyValue(stage.opacityProperty(), 1.0, Interpolator.EASE_OUT)));

        // Enhanced scale animation
        ScaleTransition scaleIn = new ScaleTransition(ANIMATION_DURATION, stage.getScene().getRoot());
        scaleIn.setFromX(0.92);
        scaleIn.setFromY(0.92);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);

        // Professional entrance animation
        ParallelTransition showAnimation = new ParallelTransition(contentFade, stageOpacity, scaleIn);
        showAnimation.setOnFinished(e -> {
            // Final setup after animation
            if (stage.getScene() != null && stage.getScene().getRoot() != null) {
                stage.getScene().getRoot().getStyleClass().add("window-loaded");
            }

            // ADDED: Final verification of window controls
            Platform.runLater(() -> {
                System.out.println("✅ Animation complete - Window controls should be fully functional");
            });
        });

        showAnimation.play();
    }

    // ENHANCED: Error handling with user-friendly messages
    private void handleModuleError(String title, Exception e) {
        e.printStackTrace();

        String detailedMessage = analyzeError(e);

        showError("Module Loading Failed",
                "Failed to open " + title + "\n\n" +
                        "Issue: " + detailedMessage + "\n\n" +
                        "Troubleshooting:\n" +
                        "• Verify FXML files are in resources/fxml/\n" +
                        "• Check controller class compilation\n" +
                        "• Ensure all dependencies are available\n" +
                        "• Restart application if problem persists\n\n" +
                        "Technical details:\n" + e.getMessage());
    }

    private String analyzeError(Exception e) {
        String message = e.getMessage();
        if (message == null)
            message = e.getClass().getSimpleName();

        if (message.contains("FXMLLoader")) {
            return "FXML loading error - check file structure";
        } else if (message.contains("Controller")) {
            return "Controller initialization error";
        } else if (message.contains("Scene")) {
            return "Scene creation error";
        } else if (message.contains("CSS") || message.contains("stylesheet")) {
            return "Stylesheet loading error";
        } else {
            return "Unexpected error during module initialization";
        }
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText("Navigation Service Error");
            alert.setContentText(message);

            // Apply current theme to alert
            try {
                if (alert.getDialogPane().getScene() != null) {
                    ThemeManager.getInstance().registerScene(alert.getDialogPane().getScene());
                }
            } catch (Exception e) {
                System.out.println("ℹ️ Could not apply theme to error dialog");
            }

            alert.showAndWait();
        });
    }

    private void showWarning(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText("Navigation Service Warning");
            alert.setContentText(message);

            // Apply current theme to alert
            try {
                if (alert.getDialogPane().getScene() != null) {
                    ThemeManager.getInstance().registerScene(alert.getDialogPane().getScene());
                }
            } catch (Exception e) {
                System.out.println("ℹ️ Could not apply theme to warning dialog");
            }

            alert.showAndWait();
        });
    }

    /**
     * ENHANCED: Close all windows with staggered animation
     */
    public void closeAllWindows() {
        if (openWindows.isEmpty())
            return;

        List<Stage> windowsCopy = new ArrayList<>(openWindows);

        for (int i = 0; i < windowsCopy.size(); i++) {
            Stage window = windowsCopy.get(i);

            // Staggered animation for visual appeal
            Timeline delayedClose = new Timeline(
                    new KeyFrame(Duration.millis(i * 100), e -> {
                        FadeTransition fadeOut = new FadeTransition(FADE_DURATION, window.getScene().getRoot());
                        fadeOut.setToValue(0.0);
                        fadeOut.setOnFinished(closeEvent -> window.close());
                        fadeOut.play();
                    }));
            delayedClose.play();
        }

        // Clear the list after all animations
        Timeline clearList = new Timeline(
                new KeyFrame(Duration.millis(windowsCopy.size() * 100 + 300), e -> openWindows.clear()));
        clearList.play();
    }

    // ENHANCED: Utility methods with better functionality
    public int getOpenWindowCount() {
        return openWindows.size();
    }

    public List<String> getOpenWindowTitles() {
        return openWindows.stream()
                .map(Stage::getTitle)
                .toList();
    }

    public boolean isModuleOpen(String title) {
        return openWindows.stream()
                .anyMatch(stage -> stage.getTitle().equals(title));
    }

    private Stage getWindowByTitle(String title) {
        return openWindows.stream()
                .filter(stage -> stage.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    public void bringWindowToFront(String title) {
        openWindows.stream()
                .filter(stage -> stage.getTitle().equals(title))
                .findFirst()
                .ifPresent(stage -> {
                    if (stage.isIconified()) {
                        stage.setIconified(false);
                    }
                    stage.toFront();
                    stage.requestFocus();
                });
    }

}
