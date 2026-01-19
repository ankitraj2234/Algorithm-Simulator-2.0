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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TooltipService {
    private static final Logger logger = LoggerFactory.getLogger(TooltipService.class);
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

    private TooltipService() {
    }

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
                                new KeyValue(content.scaleYProperty(), content.getScaleY())),
                        new KeyFrame(Duration.millis(100),
                                new KeyValue(content.opacityProperty(), 0.0, Interpolator.EASE_IN),
                                new KeyValue(content.scaleXProperty(), 0.7, Interpolator.EASE_IN),
                                new KeyValue(content.scaleYProperty(), 0.7, Interpolator.EASE_IN)));

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
     * FIXED: Robust hover handling that works for card-to-card transitions
     */
    public synchronized void showAlgorithmTooltip(Node targetNode, AlgorithmInfo algorithmInfo) {
        // Cancel any pending hide - we're showing a new tooltip
        cancelHideTask();
        isScheduledToHide = false;

        // If already showing for same target, do nothing
        if (isShowing && currentTargetNode == targetNode) {
            return;
        }

        // Cancel any pending show for a different target
        cancelShowTask();

        // If showing for different target, hide current IMMEDIATELY (no delay)
        if (currentTooltip != null && isShowing) {
            try {
                currentTooltip.hide();
            } catch (Exception ignored) {
            }
            currentTooltip = null;
            isShowing = false;
        }

        // Set new target
        currentTargetNode = targetNode;
        isScheduledToShow = true;

        // Schedule show with delay
        showTask = scheduler.schedule(() -> {
            Platform.runLater(() -> {
                // Only show if this target is still the current one
                if (currentTargetNode == targetNode && isScheduledToShow) {
                    createAndShowTooltip(targetNode, algorithmInfo);
                }
            });
        }, HOVER_DELAY_MS, TimeUnit.MILLISECONDS);

        System.out.println("🕐 Tooltip scheduled for: " + algorithmInfo.getTitle());
    }

    /**
     * FIXED: Hide with delay but don't clear target until actually hidden
     */
    public synchronized void hideCurrentTooltip() {
        // Cancel any pending show
        cancelShowTask();
        isScheduledToShow = false;

        if (!isShowing && currentTooltip == null) {
            currentTargetNode = null;
            return;
        }

        // Cancel any existing hide task
        cancelHideTask();
        isScheduledToHide = true;

        // Schedule hide with delay - allows card-to-card to cancel this
        hideTask = scheduler.schedule(() -> {
            Platform.runLater(() -> {
                // Only hide if still scheduled (not cancelled by new showAlgorithmTooltip)
                if (isScheduledToHide) {
                    hideCurrentTooltipImmediate();
                    currentTargetNode = null;
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
            logger.error("Error creating tooltip", e);
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

        // JITTER FIX: Make tooltip completely non-interactive
        // This prevents any mouse event conflicts with the card hover
        content.setMouseTransparent(true);

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
                spaceComplexityBox);

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
                            "-fx-border-insets: 0;");
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
                            "-fx-border-insets: 0;");
            content.getStyleClass().clear();
            content.getStyleClass().addAll("tooltip-container", "light-theme");
        }
    }

    private void positionTooltip(Popup popup, Node targetNode) {
        try {
            // Get card position on screen
            double nodeX = targetNode.localToScreen(targetNode.getBoundsInLocal()).getMinX();
            double nodeY = targetNode.localToScreen(targetNode.getBoundsInLocal()).getMinY();
            double nodeWidth = targetNode.getBoundsInLocal().getWidth();
            double nodeCenterX = nodeX + nodeWidth / 2;

            // Get screen bounds
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();
            double screenCenterX = screenWidth / 2;

            double tooltipWidth = 400;
            double tooltipX;
            double tooltipY = nodeY;

            // FULLSCREEN FIX: Always position at SCREEN EDGES for maximum distance
            // This ensures tooltip never interferes with cards regardless of screen size

            // Determine if card is on left half or right half of screen
            if (nodeCenterX <= screenCenterX) {
                // Card is on LEFT side of screen -> Position tooltip at RIGHT EDGE
                tooltipX = screenWidth - tooltipWidth - 30;
            } else {
                // Card is on RIGHT side of screen -> Position tooltip at LEFT EDGE
                tooltipX = 30;
            }

            // For cards near center, choose the side with more distance
            double distanceToLeft = nodeX;
            double distanceToRight = screenWidth - (nodeX + nodeWidth);

            // If card is truly in the middle (within 200px of center)
            if (Math.abs(nodeCenterX - screenCenterX) < 200) {
                // Place tooltip on the side with MORE space
                if (distanceToLeft > distanceToRight) {
                    tooltipX = 30; // Left edge
                } else {
                    tooltipX = screenWidth - tooltipWidth - 30; // Right edge
                }
            }

            // Vertical positioning - keep aligned with card but within bounds
            if (tooltipY + 280 > screenHeight) {
                tooltipY = screenHeight - 300;
            }
            tooltipY = Math.max(30, tooltipY);

            popup.setX(tooltipX);
            popup.setY(tooltipY);

            System.out.println("📍 Tooltip positioned at: X=" + tooltipX + " (card center at " + nodeCenterX
                    + ", screen center at " + screenCenterX + ")");
        } catch (Exception e) {
            logger.error("Error positioning tooltip", e);
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
                            new KeyValue(content.scaleYProperty(), 0.85)),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(content.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(content.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                            new KeyValue(content.scaleYProperty(), 1.0, Interpolator.EASE_OUT)));

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
                            new KeyValue(content.scaleYProperty(), content.getScaleY())),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(content.opacityProperty(), 0.0, Interpolator.EASE_IN),
                            new KeyValue(content.scaleXProperty(), 0.85, Interpolator.EASE_IN),
                            new KeyValue(content.scaleYProperty(), 0.85, Interpolator.EASE_IN)));

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

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getUseCases() {
            return useCases;
        }

        public String getTimeComplexity() {
            return timeComplexity;
        }

        public String getSpaceComplexity() {
            return spaceComplexity;
        }

        public String getIconLiteral() {
            return iconLiteral;
        }
    }
}
