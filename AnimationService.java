package com.simulator;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimationService {

    private static AnimationService instance;
    private double animationSpeed = 1.0;

    private AnimationService() {}

    public static AnimationService getInstance() {
        if (instance == null) {
            instance = new AnimationService();
        }
        return instance;
    }

    public void setAnimationSpeed(double speed) {
        this.animationSpeed = Math.max(0.1, Math.min(5.0, speed));
    }

    public double getAnimationSpeed() {
        return animationSpeed;
    }

    // Smooth color transition for highlighting elements
    public Timeline createHighlightAnimation(Node node, Color fromColor, Color toColor) {
        String fromStyle = String.format("-fx-background-color: #%02X%02X%02X;",
                (int)(fromColor.getRed() * 255),
                (int)(fromColor.getGreen() * 255),
                (int)(fromColor.getBlue() * 255));

        String toStyle = String.format("-fx-background-color: #%02X%02X%02X;",
                (int)(toColor.getRed() * 255),
                (int)(toColor.getGreen() * 255),
                (int)(toColor.getBlue() * 255));

        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.styleProperty(), fromStyle)),
                new KeyFrame(Duration.millis(500 / animationSpeed), new KeyValue(node.styleProperty(), toStyle))
        );
    }

    // Smooth movement animation
    public Timeline createMoveAnimation(Node node, double fromX, double toX, double fromY, double toY) {
        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.translateXProperty(), fromX),
                        new KeyValue(node.translateYProperty(), fromY)),
                new KeyFrame(Duration.millis(800 / animationSpeed),
                        new KeyValue(node.translateXProperty(), toX, Interpolator.EASE_BOTH),
                        new KeyValue(node.translateYProperty(), toY, Interpolator.EASE_BOTH))
        );
    }

    // Scale animation for emphasis
    public Timeline createScaleAnimation(Node node, double fromScale, double toScale) {
        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.scaleXProperty(), fromScale),
                        new KeyValue(node.scaleYProperty(), fromScale)),
                new KeyFrame(Duration.millis(300 / animationSpeed),
                        new KeyValue(node.scaleXProperty(), toScale, Interpolator.EASE_BOTH),
                        new KeyValue(node.scaleYProperty(), toScale, Interpolator.EASE_BOTH))
        );
    }

    // Fade animation
    public FadeTransition createFadeAnimation(Node node, double fromOpacity, double toOpacity, double duration) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration / animationSpeed), node);
        fade.setFromValue(fromOpacity);
        fade.setToValue(toOpacity);
        fade.setInterpolator(Interpolator.EASE_BOTH);
        return fade;
    }

    // Scale transition
    public ScaleTransition createScaleTransition(Node node, double toX, double toY, double duration) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(duration / animationSpeed), node);
        scale.setToX(toX);
        scale.setToY(toY);
        scale.setInterpolator(Interpolator.EASE_OUT);
        return scale;
    }

    // Translate transition
    public TranslateTransition createTranslateTransition(Node node, double toX, double toY, double duration) {
        TranslateTransition translate = new TranslateTransition(Duration.millis(duration / animationSpeed), node);
        translate.setToX(toX);
        translate.setToY(toY);
        translate.setInterpolator(Interpolator.EASE_OUT);
        return translate;
    }

    // Sequential animation builder
    public SequentialTransition createSequentialAnimation(Animation... animations) {
        SequentialTransition sequence = new SequentialTransition(animations);
        sequence.setRate(animationSpeed);
        return sequence;
    }

    // Parallel animation builder
    public ParallelTransition createParallelAnimation(Animation... animations) {
        ParallelTransition parallel = new ParallelTransition(animations);
        parallel.setRate(animationSpeed);
        return parallel;
    }

    // Card click animation
    public void playCardClickAnimation(Node card) {
        ScaleTransition scaleDown = createScaleTransition(card, 0.95, 0.95, 100);
        ScaleTransition scaleUp = createScaleTransition(card, 1.0, 1.0, 100);

        SequentialTransition clickAnim = new SequentialTransition(scaleDown, scaleUp);
        clickAnim.play();
    }

    // Button hover animation
    public void playButtonHoverAnimation(Node button, boolean isEntering) {
        double scale = isEntering ? 1.05 : 1.0;
        ScaleTransition hover = createScaleTransition(button, scale, scale, 150);
        hover.play();
    }

    // Pulse animation for important elements
    public Timeline createPulseAnimation(Node node) {
        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.scaleXProperty(), 1.0),
                        new KeyValue(node.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(500 / animationSpeed),
                        new KeyValue(node.scaleXProperty(), 1.1, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleYProperty(), 1.1, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(1000 / animationSpeed),
                        new KeyValue(node.scaleXProperty(), 1.0, Interpolator.EASE_IN),
                        new KeyValue(node.scaleYProperty(), 1.0, Interpolator.EASE_IN))
        );
    }
}
