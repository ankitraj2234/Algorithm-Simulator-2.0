// Enhanced BaseController.java
package com.simulator;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.application.Platform;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class BaseController implements Initializable {
    protected AnimationService animationService;
    protected ThemeManager themeManager;
    protected boolean isAnimating = false;
    protected boolean isPaused = false;
    protected Slider speedSlider;

    @Override
    public final void initialize(URL location, ResourceBundle resources) {
        // Initialize common services
        animationService = AnimationService.getInstance();
        themeManager = ThemeManager.getInstance();

        // Register scene for theme updates
        Platform.runLater(() -> {
            if (getScene() != null) {
                themeManager.registerScene(getScene());
            }
        });

        // Template method pattern - called in order
        initializeServices();
        setupComponents();
        setupEventHandlers();
        configureAnimations();
        setupKeyboardShortcuts();

        System.out.println(getClass().getSimpleName() + " initialized successfully");
    }

    // Common initialization
    protected void initializeServices() {
        // Override if needed
    }

    // Abstract methods - must be implemented by subclasses
    protected abstract void setupComponents();
    protected abstract void setupEventHandlers();
    protected abstract void configureAnimations();

    // Common functionality with default implementation
    protected void setupKeyboardShortcuts() {
        // Default keyboard shortcuts for all modules
        Platform.runLater(() -> {
            if (getScene() != null) {
                getScene().setOnKeyPressed(event -> {
                    if (event.isControlDown()) {
                        switch (event.getCode()) {
                            case SPACE -> toggleAnimation();
                            case R -> resetVisualization();
                            case T -> themeManager.toggleTheme();
                            case ESCAPE -> closeModule();
                        }
                    }
                });
            }
        });
    }

    // Common animation control methods
    protected void startAnimation() {
        if (!isAnimating) {
            isAnimating = true;
            isPaused = false;
            updateControlStates();
            onAnimationStart();
        }
    }

    protected void pauseAnimation() {
        if (isAnimating) {
            isPaused = !isPaused;
            updateControlStates();
            onAnimationPause(isPaused);
        }
    }

    protected void stopAnimation() {
        isAnimating = false;
        isPaused = false;
        updateControlStates();
        onAnimationStop();
    }

    protected void resetVisualization() {
        stopAnimation();
        onReset();
        updateControlStates();
    }

    protected void toggleAnimation() {
        if (isAnimating) {
            pauseAnimation();
        } else {
            startAnimation();
        }
    }

    // Abstract animation lifecycle methods
    protected abstract void onAnimationStart();
    protected abstract void onAnimationPause(boolean isPaused);
    protected abstract void onAnimationStop();
    protected abstract void onReset();

    // Common UI update
    protected void updateControlStates() {
        // Override in subclasses to update UI state
    }

    // Utility methods
    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected javafx.scene.Scene getScene() {
        // Override in subclasses to return current scene
        return null;
    }

    protected void closeModule() {
        // Override to handle module closing
        Platform.runLater(() -> {
            if (getScene() != null && getScene().getWindow() instanceof javafx.stage.Stage) {
                ((javafx.stage.Stage) getScene().getWindow()).close();
            }
        });
    }

    // Cleanup method for proper resource management
    public void cleanup() {
        if (getScene() != null) {
            themeManager.unregisterScene(getScene());
        }
        stopAnimation();
        System.out.println(getClass().getSimpleName() + " cleaned up");
    }
}
