package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TooltipService {
    private static TooltipService instance;
    private Popup currentTooltip;
    private Timeline showAnimation;
    private Timeline hideAnimation;
    private Node currentTargetNode;

    // ENTERPRISE: Professional timer management
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "TooltipService-Timer");
        t.setDaemon(true);
        return t;
    });

    private ScheduledFuture<?> showTask;
    private ScheduledFuture<?> hideTask;

    // PERFORMANCE: Debouncing configuration
    private static final long HOVER_DELAY_MS = 800;
    private static final long HIDE_DELAY_MS = 300;

    // STATE: Simple state management
    private volatile boolean isShowing = false;
    private volatile boolean isScheduledToShow = false;
    private volatile boolean isScheduledToHide = false;

    private TooltipService() {}

    public static TooltipService getInstance() {
        if (instance == null) {
            synchronized (TooltipService.class) {
                if (instance == null) {
                    instance = new TooltipService();
                }
            }
        }
        return instance;
    }

    /**
     * ✅ NEW: Cancel tooltip immediately on user click/interaction
     */
    public synchronized void cancelTooltipOnInteraction() {
        // Cancel any pending operations
        cancelShowTask();
        cancelHideTask();

        // Immediately hide current tooltip without delay
        if (isShowing && currentTooltip != null) {
            try {
                // Stop any running animations
                if (showAnimation != null && showAnimation.getStatus() == Animation.Status.RUNNING) {
                    showAnimation.stop();
                }
                if (hideAnimation != null && hideAnimation.getStatus() == Animation.Status.RUNNING) {
                    hideAnimation.stop();
                }

                // Immediate fast hide animation
                VBox content = (VBox) currentTooltip.getContent().get(0);
                Timeline quickHide = new Timeline();
                quickHide.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(content.opacityProperty(), content.getOpacity()),
                                new KeyValue(content.scaleXProperty(), content.getScaleX()),
                                new KeyValue(content.scaleYProperty(), content.getScaleY())
                        ),
                        new KeyFrame(Duration.millis(100),
                                new KeyValue(content.opacityProperty(), 0.0, Interpolator.EASE_IN),
                                new KeyValue(content.scaleXProperty(), 0.7, Interpolator.EASE_IN),
                                new KeyValue(content.scaleYProperty(), 0.7, Interpolator.EASE_IN)
                        )
                );

                quickHide.setOnFinished(e -> {
                    currentTooltip.hide();
                    currentTooltip = null;
                    isShowing = false;
                    currentTargetNode = null;
                });
                quickHide.play();

            } catch (Exception e) {
                forceHideTooltip();
            }
        }

        // Reset state
        currentTargetNode = null;
        isScheduledToShow = false;
        isScheduledToHide = false;
        System.out.println("⚡ Tooltip cancelled on user interaction");
    }

    /**
     * ENTERPRISE: Professional hover handling with debouncing
     */
    public synchronized void showAlgorithmTooltip(Node targetNode, AlgorithmInfo algorithmInfo) {
        // Cancel any pending hide operations
        cancelHideTask();

        // If already showing for same target, do nothing
        if (isShowing && currentTargetNode == targetNode) {
            return;
        }

        // If showing for different target, hide current first
        if (isShowing && currentTargetNode != targetNode) {
            hideCurrentTooltipImmediate();
        }

        // Cancel any pending show operations
        cancelShowTask();

        // Set new target
        currentTargetNode = targetNode;
        isScheduledToShow = true;

        // DEBOUNCING: Schedule show with delay
        showTask = scheduler.schedule(() -> {
            Platform.runLater(() -> {
                if (currentTargetNode == targetNode && isScheduledToShow) {
                    createAndShowTooltip(targetNode, algorithmInfo);
                }
            });
        }, HOVER_DELAY_MS, TimeUnit.MILLISECONDS);

        System.out.println("🕐 Tooltip scheduled to show for: " + algorithmInfo.getTitle());
    }

    /**
     * ENTERPRISE: Professional hide handling with delay
     */
    public synchronized void hideCurrentTooltip() {
        // Cancel any pending show operations
        cancelShowTask();

        if (!isShowing) {
            return;
        }

        // Cancel any existing hide task
        cancelHideTask();

        isScheduledToHide = true;
        currentTargetNode = null;

        // DEBOUNCING: Schedule hide with small delay
        hideTask = scheduler.schedule(() -> {
            Platform.runLater(() -> {
                if (isScheduledToHide) {
                    hideCurrentTooltipImmediate();
                }
            });
        }, HIDE_DELAY_MS, TimeUnit.MILLISECONDS);

        System.out.println("🕐 Tooltip scheduled to hide");
    }

    private synchronized void hideCurrentTooltipImmediate() {
        isScheduledToShow = false;
        isScheduledToHide = false;

        if (currentTooltip != null && isShowing) {
            try {
                hideWithAnimation(currentTooltip);
            } catch (Exception e) {
                System.err.println("❌ Error hiding tooltip: " + e.getMessage());
                forceHideTooltip();
            }
        }
    }

    private void forceHideTooltip() {
        try {
            if (currentTooltip != null) {
                currentTooltip.hide();
            }
        } catch (Exception e) {
            System.err.println("❌ Force hide failed: " + e.getMessage());
        } finally {
            currentTooltip = null;
            isShowing = false;
            currentTargetNode = null;
        }
    }

    private void cancelShowTask() {
        if (showTask != null && !showTask.isDone()) {
            showTask.cancel(false);
            isScheduledToShow = false;
        }
    }

    private void cancelHideTask() {
        if (hideTask != null && !hideTask.isDone()) {
            hideTask.cancel(false);
            isScheduledToHide = false;
        }
    }

    public boolean isTooltipShowing() {
        return isShowing;
    }

    private void createAndShowTooltip(Node targetNode, AlgorithmInfo algorithmInfo) {
        try {
            // Create new tooltip
            currentTooltip = createTooltipPopup(algorithmInfo);
            // Position tooltip
            positionTooltip(currentTooltip, targetNode);
            // Show with animation
            showWithAnimation(currentTooltip, targetNode);
            System.out.println("✅ Tooltip shown for: " + algorithmInfo.getTitle());
        } catch (Exception e) {
            System.err.println("❌ Error creating tooltip: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Popup createTooltipPopup(AlgorithmInfo info) {
        Popup popup = new Popup();
        popup.setAutoHide(false);
        popup.setConsumeAutoHidingEvents(false);
        popup.setHideOnEscape(true);

        // Create tooltip content
        VBox content = new VBox();
        content.setSpacing(14);
        content.setPadding(new Insets(18));
        content.setAlignment(Pos.TOP_LEFT);
        content.setMaxWidth(380);
        content.setPrefWidth(360);

        // ✅ CRITICAL: Add mouse event handlers to prevent mouse dance
        content.setOnMouseEntered(e -> {
            cancelHideTask(); // Keep tooltip visible when mouse is on it
            System.out.println("🖱️ Mouse entered tooltip - keeping visible");
        });

        content.setOnMouseExited(e -> {
            hideCurrentTooltip(); // Hide when mouse leaves tooltip
            System.out.println("🖱️ Mouse exited tooltip - scheduling hide");
        });

        // Apply theme-aware styling
        updateTooltipTheme(content);

        // Header with icon and title
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon();
        icon.setIconLiteral(info.getIconLiteral());
        icon.setIconSize(26);
        icon.getStyleClass().add("tooltip-icon");

        Label titleLabel = new Label(info.getTitle());
        titleLabel.getStyleClass().add("tooltip-title");

        header.getChildren().addAll(icon, titleLabel);

        // Description
        Label descriptionLabel = new Label(info.getDescription());
        descriptionLabel.getStyleClass().add("tooltip-description");
        descriptionLabel.setWrapText(true);

        // Use Cases Section
        Label useCasesTitle = new Label("Common Use Cases:");
        useCasesTitle.getStyleClass().add("tooltip-section-title");

        Label useCasesLabel = new Label(info.getUseCases());
        useCasesLabel.getStyleClass().add("tooltip-text");
        useCasesLabel.setWrapText(true);

        // Time Complexity
        HBox complexityBox = new HBox(10);
        complexityBox.setAlignment(Pos.CENTER_LEFT);

        Label complexityTitle = new Label("Time Complexity:");
        complexityTitle.getStyleClass().add("tooltip-section-title");

        Label complexityLabel = new Label(info.getTimeComplexity());
        complexityLabel.getStyleClass().add("tooltip-complexity");

        complexityBox.getChildren().addAll(complexityTitle, complexityLabel);

        // Space Complexity
        HBox spaceComplexityBox = new HBox(10);
        spaceComplexityBox.setAlignment(Pos.CENTER_LEFT);

        Label spaceTitle = new Label("Space Complexity:");
        spaceTitle.getStyleClass().add("tooltip-section-title");

        Label spaceLabel = new Label(info.getSpaceComplexity());
        spaceLabel.getStyleClass().add("tooltip-complexity");

        spaceComplexityBox.getChildren().addAll(spaceTitle, spaceLabel);

        content.getChildren().addAll(
                header,
                descriptionLabel,
                useCasesTitle,
                useCasesLabel,
                complexityBox,
                spaceComplexityBox
        );

        popup.getContent().add(content);
        return popup;
    }

    private void updateTooltipTheme(VBox content) {
        boolean isDarkMode = ThemeManager.getInstance().isDarkMode();

        if (isDarkMode) {
            content.setStyle(
                    "-fx-background-color: #1a202c; " +
                            "-fx-border-color: #4a5568; " +
                            "-fx-border-width: 1px; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 20, 0, 0, 8);" +
                            "-fx-background-insets: 0;" +
                            "-fx-border-insets: 0;"
            );
            content.getStyleClass().clear();
            content.getStyleClass().addAll("tooltip-container", "dark-theme");
        } else {
            content.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-border-color: #cbd5e0; " +
                            "-fx-border-width: 1px; " +
                            "-fx-background-radius: 12px; " +
                            "-fx-border-radius: 12px; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 8);" +
                            "-fx-background-insets: 0;" +
                            "-fx-border-insets: 0;"
            );
            content.getStyleClass().clear();
            content.getStyleClass().addAll("tooltip-container", "light-theme");
        }
    }

    private void positionTooltip(Popup popup, Node targetNode) {
        try {
            double nodeX = targetNode.localToScreen(targetNode.getBoundsInLocal()).getMinX();
            double nodeY = targetNode.localToScreen(targetNode.getBoundsInLocal()).getMinY();
            double nodeWidth = targetNode.getBoundsInLocal().getWidth();
            double nodeHeight = targetNode.getBoundsInLocal().getHeight();

            // ✅ SIMPLE & RELIABLE: Basic positioning with larger offsets
            double tooltipX = nodeX + nodeWidth + 35;  // Increased offset
            double tooltipY = nodeY - 20;              // Slightly higher

            // Screen bounds checking
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();

            // Intelligent positioning
            if (tooltipX + 400 > screenWidth) {
                tooltipX = nodeX - 435; // Show on left with larger gap
            }

            if (tooltipY + 280 > screenHeight) {
                tooltipY = screenHeight - 300;
            }

            tooltipX = Math.max(20, tooltipX);
            tooltipY = Math.max(20, tooltipY);

            popup.setX(tooltipX);
            popup.setY(tooltipY);
        } catch (Exception e) {
            System.err.println("❌ Error positioning tooltip: " + e.getMessage());
        }
    }

    private void showWithAnimation(Popup popup, Node targetNode) {
        try {
            popup.show(targetNode.getScene().getWindow());

            // ✅ ENHANCED: Set transparency after show
            Platform.runLater(() -> {
                try {
                    if (popup.getScene() != null) {
                        popup.getScene().setFill(null);
                        popup.getScene().getRoot().setStyle("-fx-background-color: transparent;");
                    }
                } catch (Exception e) {
                    // Ignore transparency errors
                }
            });

            isShowing = true;

            VBox content = (VBox) popup.getContent().get(0);
            content.setOpacity(0.0);
            content.setScaleX(0.85);
            content.setScaleY(0.85);

            if (showAnimation != null && showAnimation.getStatus() == Animation.Status.RUNNING) {
                showAnimation.stop();
            }

            showAnimation = new Timeline();
            showAnimation.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(content.opacityProperty(), 0.0),
                            new KeyValue(content.scaleXProperty(), 0.85),
                            new KeyValue(content.scaleYProperty(), 0.85)
                    ),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(content.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(content.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(content.scaleYProperty(), 1.0, Interpolator.EASE_OUT)
                    )
            );

            showAnimation.play();
        } catch (Exception e) {
            System.err.println("❌ Error showing tooltip animation: " + e.getMessage());
            isShowing = false;
        }
    }

    private void hideWithAnimation(Popup popup) {
        try {
            if (showAnimation != null && showAnimation.getStatus() == Animation.Status.RUNNING) {
                showAnimation.stop();
            }

            VBox content = (VBox) popup.getContent().get(0);

            if (hideAnimation != null && hideAnimation.getStatus() == Animation.Status.RUNNING) {
                hideAnimation.stop();
            }

            hideAnimation = new Timeline();
            hideAnimation.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(content.opacityProperty(), content.getOpacity()),
                            new KeyValue(content.scaleXProperty(), content.getScaleX()),
                            new KeyValue(content.scaleYProperty(), content.getScaleY())
                    ),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(content.opacityProperty(), 0.0, Interpolator.EASE_IN),
                            new KeyValue(content.scaleXProperty(), 0.85, Interpolator.EASE_IN),
                            new KeyValue(content.scaleYProperty(), 0.85, Interpolator.EASE_IN)
                    )
            );

            hideAnimation.setOnFinished(e -> {
                try {
                    popup.hide();
                    currentTooltip = null;
                    isShowing = false;
                    currentTargetNode = null;
                } catch (Exception ex) {
                    System.err.println("❌ Error in hide animation finish: " + ex.getMessage());
                    forceHideTooltip();
                }
            });

            hideAnimation.play();
        } catch (Exception e) {
            System.err.println("❌ Error hiding tooltip animation: " + e.getMessage());
            forceHideTooltip();
        }
    }

    public void shutdown() {
        try {
            cancelShowTask();
            cancelHideTask();
            hideCurrentTooltipImmediate();
            scheduler.shutdown();
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static class AlgorithmInfo {
        private final String title;
        private final String description;
        private final String useCases;
        private final String timeComplexity;
        private final String spaceComplexity;
        private final String iconLiteral;

        public AlgorithmInfo(String title, String description, String useCases,
                             String timeComplexity, String spaceComplexity, String iconLiteral) {
            this.title = title;
            this.description = description;
            this.useCases = useCases;
            this.timeComplexity = timeComplexity;
            this.spaceComplexity = spaceComplexity;
            this.iconLiteral = iconLiteral;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getUseCases() { return useCases; }
        public String getTimeComplexity() { return timeComplexity; }
        public String getSpaceComplexity() { return spaceComplexity; }
        public String getIconLiteral() { return iconLiteral; }
    }
}
