package com.simulator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * ENHANCED: Main Application with proper window management and cleanup
 * This version ensures child windows (algorithm modules) have proper minimize functionality
 */
public class AlgorithmSimulatorApplication extends Application {
    private static Stage primaryStage;
    private static MainController mainController; // Store controller reference
    private static ApplicationResourceManager resourceManager;

    // Application metadata
    private static final String APP_TITLE = "Algorithm Simulator - Professional Edition v2.0";
    private static final String APP_VERSION = "2.0.0";
    private static final double MIN_WIDTH = 1200;
    private static final double MIN_HEIGHT = 800;
    private static final double SCREEN_USAGE_FACTOR = 0.85;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            primaryStage = stage;
            resourceManager = new ApplicationResourceManager();
            System.out.println("🚀 Starting Algorithm Simulator v" + APP_VERSION);

            // ENHANCED: Initialize theme system first
            initializeThemeSystem();

            // ENHANCED: Load main view with controller reference
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();

            // CRITICAL: Store controller reference for cleanup
            mainController = loader.getController();
            if (mainController == null) {
                System.err.println("⚠️ Warning: MainController not found in FXML");
            }

            // ENHANCED: Calculate optimal window dimensions
            WindowDimensions dimensions = calculateOptimalDimensions();

            // Create scene with proper styling
            Scene scene = new Scene(root, dimensions.width, dimensions.height);
            loadApplicationStylesheets(scene);

            // ENHANCED: Configure primary stage properly
            configurePrimaryStage(stage, scene, dimensions);

            // ENHANCED: Setup application lifecycle
            setupApplicationLifecycle(stage);

            // Show the application
            stage.show();

            // Log memory statistics
            logMemoryStatistics("Application Started");

            System.out.println("✅ Algorithm Simulator started successfully!");
            System.out.println("📊 Window size: " + (int)dimensions.width + "x" + (int)dimensions.height);
        } catch (Exception e) {
            System.err.println("❌ Failed to start Algorithm Simulator: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * ENHANCED: Initialize theme system with error handling
     */
    private void initializeThemeSystem() {
        try {
            ThemeManager.getInstance().setDefaultTheme();
            System.out.println("🎨 Theme system initialized successfully");
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Theme system initialization failed: " + e.getMessage());
        }
    }

    /**
     * ENHANCED: Calculate optimal window dimensions
     */
    private WindowDimensions calculateOptimalDimensions() {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // Calculate optimal size with reasonable limits
        double windowWidth = Math.min(1600, screenWidth * SCREEN_USAGE_FACTOR);
        double windowHeight = Math.min(1000, screenHeight * SCREEN_USAGE_FACTOR);

        return new WindowDimensions(windowWidth, windowHeight, screenBounds);
    }

    /**
     * ENHANCED: Load application stylesheets with fallback
     */
    private void loadApplicationStylesheets(Scene scene) {
        String[] stylesheets = {
                "/css/main.css",
                "/css/themes.css",
                "/css/responsive.css"
        };

        for (String stylesheet : stylesheets) {
            try {
                if (getClass().getResource(stylesheet) != null) {
                    scene.getStylesheets().add(getClass().getResource(stylesheet).toExternalForm());
                    System.out.println("✅ Loaded stylesheet: " + stylesheet);
                }
            } catch (Exception e) {
                System.err.println("⚠️ Warning: Could not load " + stylesheet + ": " + e.getMessage());
            }
        }
    }

    /**
     * Log memory statistics
     */
    private void logMemoryStatistics(String context) {
        try {
            MemoryStats stats = getMemoryStats();
            System.out.println("📊 Memory Statistics [" + context + "]:");
            System.out.println("   Used: " + stats.usedMemoryMB + " MB");
            System.out.println("   Free: " + stats.freeMemoryMB + " MB");
            System.out.println("   Total: " + stats.totalMemoryMB + " MB");
            System.out.println("   Max: " + stats.maxMemoryMB + " MB");
        } catch (Exception e) {
            System.out.println("ℹ️ Could not gather memory statistics: " + e.getMessage());
        }
    }

    /**
     * Get memory statistics
     */
    private MemoryStats getMemoryStats() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return new MemoryStats(
                usedMemory / (1024 * 1024),
                freeMemory / (1024 * 1024),
                totalMemory / (1024 * 1024),
                maxMemory / (1024 * 1024)
        );
    }


    private void configurePrimaryStage(Stage stage, Scene scene, WindowDimensions dimensions) {
        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);

        // ENHANCED: Set size constraints
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.setResizable(true);

        // Set initial size
        stage.setWidth(dimensions.width);
        stage.setHeight(dimensions.height);

        // ✅ FIXED: Manual centering instead of centerOnScreen()
        Rectangle2D screenBounds = dimensions.screenBounds;
        double centerX = screenBounds.getMinX() + (screenBounds.getWidth() - dimensions.width) / 2;
        double centerY = screenBounds.getMinY() + (screenBounds.getHeight() - dimensions.height) / 2;

        stage.setX(centerX);
        stage.setY(centerY);

        System.out.println("🎯 Main window positioned at center: (" + centerX + ", " + centerY + ")");

        // ENHANCED: Better maximization handling
        stage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (isNowMaximized) {
                Platform.runLater(() -> {
                    stage.setX(dimensions.screenBounds.getMinX());
                    stage.setY(dimensions.screenBounds.getMinY());
                    stage.setWidth(dimensions.screenBounds.getWidth());
                    stage.setHeight(dimensions.screenBounds.getHeight());
                });
                System.out.println("🔍 Main window maximized to full screen");
            } else {
                System.out.println("🔍 Main window restored");
            }
        });
    }


    /**
     * ENHANCED: Setup comprehensive application lifecycle
     */
    private void setupApplicationLifecycle(Stage stage) {
        // Handle window close request
        stage.setOnCloseRequest(event -> {
            System.out.println("🛑 Application shutdown initiated by user");
            performApplicationCleanup();
        });

        // Handle window iconified (minimized) state
        stage.iconifiedProperty().addListener((obs, wasIconified, isIconified) -> {
            if (isIconified) {
                System.out.println("➖ Main window minimized");
            } else {
                System.out.println("⬆️ Main window restored from minimize");
            }
        });

        // Handle focus changes
        stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                System.out.println("👁️ Main window gained focus");
            }
        });

        // Setup JVM shutdown hook for emergency cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 JVM shutdown detected, performing emergency cleanup");
            performApplicationCleanup();
        }));

        // CRITICAL: Platform exit handling - don't auto-exit to control cleanup
        Platform.setImplicitExit(false);
        stage.setOnHidden(event -> {
            performApplicationCleanup();
            Platform.exit();
        });
    }

    /**
     * ENHANCED: Comprehensive application cleanup
     */
    private void performApplicationCleanup() {
        try {
            System.out.println("🧹 Starting application cleanup...");

            // Log memory before cleanup
            logMemoryStatistics("Before Cleanup");

            // Cleanup main controller
            if (mainController != null) {
                mainController.cleanup();
                System.out.println("✅ Main controller cleaned up");
            }

            // Cleanup resource manager
            if (resourceManager != null) {
                resourceManager.cleanup();
                System.out.println("✅ Resource manager cleaned up");
            }

            // Cleanup theme manager
            ThemeManager.getInstance().clearAllScenes();
            System.out.println("✅ Theme manager cleaned up");

            // Reset animation service if needed
            try {
                AnimationService.getInstance().setAnimationSpeed(1.0); // Reset to default
                System.out.println("✅ Animation service reset");
            } catch (Exception e) {
                System.out.println("ℹ️ Animation service cleanup not needed");
            }

            // Suggest garbage collection (replaces deprecated runFinalization)
            System.gc();

            // Log final memory statistics
            logMemoryStatistics("After Cleanup");

            System.out.println("🎉 Application cleanup completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Error during application cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ENHANCED: Proper application stop handling
     */
    @Override
    public void stop() {
        System.out.println("🛑 Application stop() method called");
        performApplicationCleanup();
    }

    /**
     * PUBLIC API: Get primary stage reference
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * PUBLIC API: Get main controller reference
     */
    public static MainController getMainController() {
        return mainController;
    }

    /**
     * PUBLIC API: Get application version
     */
    public static String getVersion() {
        return APP_VERSION;
    }

    /**
     * PUBLIC API: Check if application is properly initialized
     */
    public static boolean isInitialized() {
        return primaryStage != null && mainController != null;
    }

    /**
     * UTILITY: Window dimensions data class
     */
    private static class WindowDimensions {
        final double width;
        final double height;
        final Rectangle2D screenBounds;

        WindowDimensions(double width, double height, Rectangle2D screenBounds) {
            this.width = width;
            this.height = height;
            this.screenBounds = screenBounds;
        }
    }

    /**
     * Memory statistics data class
     */
    private static class MemoryStats {
        final long usedMemoryMB;
        final long freeMemoryMB;
        final long totalMemoryMB;
        final long maxMemoryMB;

        MemoryStats(long usedMemoryMB, long freeMemoryMB, long totalMemoryMB, long maxMemoryMB) {
            this.usedMemoryMB = usedMemoryMB;
            this.freeMemoryMB = freeMemoryMB;
            this.totalMemoryMB = totalMemoryMB;
            this.maxMemoryMB = maxMemoryMB;
        }
    }

    /**
     * Application resource manager for proper cleanup
     */
    private static class ApplicationResourceManager {
        private final List<AutoCloseable> resources;
        private final List<Runnable> cleanupTasks;

        public ApplicationResourceManager() {
            this.resources = new ArrayList<>();
            this.cleanupTasks = new ArrayList<>();
        }

        public void registerResource(AutoCloseable resource) {
            if (resource != null) {
                resources.add(resource);
            }
        }

        public void registerCleanupTask(Runnable task) {
            if (task != null) {
                cleanupTasks.add(task);
            }
        }

        public void cleanup() {
            // Execute cleanup tasks
            for (Runnable task : cleanupTasks) {
                try {
                    task.run();
                } catch (Exception e) {
                    System.err.println("⚠️ Error executing cleanup task: " + e.getMessage());
                }
            }

            // Close resources
            for (AutoCloseable resource : resources) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("⚠️ Error closing resource: " + e.getMessage());
                }
            }

            resources.clear();
            cleanupTasks.clear();
        }
    }

    /**
     * ENHANCED: Main method with system properties
     */
    public static void main(String[] args) {
        // Set system properties for better JavaFX performance
        System.setProperty("javafx.animation.pulse", "60");
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.subpixeltext", "false");

        System.out.println("🎬 Launching Algorithm Simulator...");
        launch(args);
    }
}
