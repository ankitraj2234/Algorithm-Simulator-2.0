package com.simulator;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StackCodeController implements Initializable {

    // FXML Components
    @FXML private BorderPane rootPane;
    @FXML private VBox headerContainer;
    @FXML private TextField searchField;
    @FXML private SplitPane mainSplitPane;
    @FXML private ScrollPane categoryScrollPane;
    @FXML private VBox categoryContainer;
    @FXML private VBox codeDisplayContainer;
    @FXML private VBox codeHeaderSection;
    @FXML private VBox codeAreaSection;
    @FXML private VBox codeFooterSection;
    @FXML private Label codeTitle;
    @FXML private Label codeDescription;
    @FXML private TextArea codeTextArea;
    @FXML private Label codeStats;
    @FXML private Label copyStatus;
    @FXML private Button copyButton;
    @FXML private Button wrapTextButton;
    @FXML private Button expandAllBtn;
    @FXML private Button collapseAllBtn;

    // Data and State
    private StackController parentController;
    private StackCodeRepository codeRepository;
    private Map<String, CategoryTile> categoryTiles = new HashMap<>();
    private List<String> allExamples = new ArrayList<>();

    // 🎯 CRITICAL: Scene reference for theme management
    private Scene popupScene;
    private boolean isSceneRegistered = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("StackCodeController initialized");
        codeRepository = new StackCodeRepository();
        setupSearchFunctionality();
        createCategoryTiles();
        setupEventHandlers();
        showWelcomeMessage();

        // 🎯 CRITICAL: Register with ThemeManager after scene is ready
        Platform.runLater(this::registerWithThemeManager);
    }

    public void setParentController(StackController parentController) {
        this.parentController = parentController;
    }

    /**
     * 🎯 CRITICAL: Register popup scene with ThemeManager for live theme updates
     */
    private void registerWithThemeManager() {
        if (rootPane.getScene() == null) {
            Platform.runLater(this::registerWithThemeManager);
            return;
        }

        try {
            popupScene = rootPane.getScene();
            ThemeManager.getInstance().registerScene(popupScene);
            isSceneRegistered = true;

            // Apply current theme immediately
            applyCurrentTheme();

            System.out.println("✅ StackCodeController registered with ThemeManager");
        } catch (Exception e) {
            System.err.println("❌ Failed to register with ThemeManager: " + e.getMessage());
        }
    }

    /**
     * 🎯 CRITICAL: Apply current theme from ThemeManager
     */
    private void applyCurrentTheme() {
        if (popupScene == null) return;

        try {
            boolean isDarkMode = ThemeManager.getInstance().isDarkMode();
            String themeClass = isDarkMode ? "dark-theme" : "light-theme";
            String removeClass = isDarkMode ? "light-theme" : "dark-theme";

            // Apply theme to scene root
            popupScene.getRoot().getStyleClass().removeAll(removeClass);
            popupScene.getRoot().getStyleClass().add(themeClass);

            // Apply theme to popup root
            rootPane.getStyleClass().removeAll(removeClass);
            rootPane.getStyleClass().add(themeClass);

            // Update category tiles
            categoryTiles.values().forEach(tile -> tile.useThemeClasses(isDarkMode));

            // Force CSS refresh
            Platform.runLater(() -> {
                popupScene.getRoot().applyCss();
                rootPane.applyCss();
            });

            System.out.println("🎨 Theme applied to popup: " + (isDarkMode ? "Dark" : "Light"));
        } catch (Exception e) {
            System.err.println("❌ Error applying theme: " + e.getMessage());
        }
    }

    /**
     * Create modern category tiles using CSS variables
     */
    private void createCategoryTiles() {
        CategoryInfo[] categories = {
                new CategoryInfo("📚 Basic Operations", "#3b82f6", new String[]{
                        "Create Stack", "Push Operation", "Pop Operation", "Peek Operation"
                }),
                new CategoryInfo("🔧 Stack Management", "#10b981", new String[]{
                        "Stack Overflow Check", "Stack Underflow Check", "Size Management", "Clear Stack"
                }),
                new CategoryInfo("🚀 Stack Applications", "#f59e0b", new String[]{
                        "Stack Applications", "Balanced Parentheses", "String Reversal", "Expression Evaluation"
                }),
                new CategoryInfo("⚡ Advanced Operations", "#8b5cf6", new String[]{
                        "Multi Stack", "Stack with Min", "Stack Iterator", "Stack Clone"
                })
        };

        for (CategoryInfo categoryInfo : categories) {
            CategoryTile tile = new CategoryTile(categoryInfo, this::loadCodeExample);
            categoryTiles.put(categoryInfo.name, tile);
            categoryContainer.getChildren().add(tile.getContainer());

            // Add all examples to searchable list
            for (String example : categoryInfo.examples) {
                allExamples.add(example);
            }
        }
    }

    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterCategories(newText.toLowerCase().trim());
        });
    }

    private void filterCategories(String searchTerm) {
        if (searchTerm.isEmpty()) {
            categoryTiles.values().forEach(tile -> tile.setVisible(true));
        } else {
            categoryTiles.values().forEach(tile -> {
                boolean hasMatch = tile.containsExample(searchTerm);
                tile.setVisible(hasMatch);
                tile.filterExamples(searchTerm);
            });
        }
    }

    private void setupEventHandlers() {
        copyStatus.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isEmpty()) {
                Timeline hideStatus = new Timeline(new KeyFrame(Duration.seconds(3), e -> copyStatus.setText("")));
                hideStatus.play();
            }
        });

        codeTextArea.textProperty().addListener((obs, oldText, newText) -> {
            Platform.runLater(() -> {
                codeTextArea.positionCaret(0);
                codeTextArea.setScrollTop(0);
            });
        });
    }

    private void loadCodeExample(String exampleName) {
        StackCodeRepository.CodeExample example = codeRepository.getCodeExample(exampleName);
        if (example != null) {
            codeTitle.setText("📄 " + example.getTitle());
            codeDescription.setText(example.getDescription());
            codeTextArea.setText(example.getCode());

            String[] lines = example.getCode().split("\n");
            codeStats.setText(String.format("Lines: %d | Characters: %d | Complexity: %s",
                    lines.length, example.getCode().length(), example.getComplexity()));

            copyButton.setDisable(false);

            if (parentController != null) {
                parentController.logOperation("STACK_CODE_VIEWED | " + example.getTitle());
            }
        }
    }

    private void showWelcomeMessage() {
        codeTitle.setText("📚 Stack Code Repository");
        codeDescription.setText("Welcome to the comprehensive Stack code examples collection. " +
                "Browse categories below or use the search bar to find specific implementations.");
        codeTextArea.setText(codeRepository.getWelcomeMessage());
        codeStats.setText("Ready to explore Stack implementations");
        copyButton.setDisable(true);
    }

    @FXML
    private void copyCodeToClipboard() {
        String codeText = codeTextArea.getText();
        if (!codeText.isEmpty()) {
            try {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(codeText);
                clipboard.setContent(content);

                copyStatus.setText("✅ Code copied to clipboard!");
                copyStatus.getStyleClass().removeAll("error-text");
                copyStatus.getStyleClass().add("success-text");

                if (parentController != null) {
                    parentController.logOperation("STACK_CODE_COPIED | " + codeTitle.getText());
                }
            } catch (Exception e) {
                copyStatus.setText("❌ Failed to copy code");
                copyStatus.getStyleClass().removeAll("success-text");
                copyStatus.getStyleClass().add("error-text");
            }
        }
    }

    @FXML
    private void toggleWordWrap() {
        boolean currentWrap = codeTextArea.isWrapText();
        codeTextArea.setWrapText(!currentWrap);

        if (!currentWrap) {
            wrapTextButton.setText("Unwrap");
            wrapTextButton.setGraphic(new FontIcon("fas-align-justify"));
            copyStatus.setText("📝 Word wrap enabled - lines break automatically");
        } else {
            wrapTextButton.setText("Wrap");
            wrapTextButton.setGraphic(new FontIcon("fas-align-left"));
            copyStatus.setText("📝 Word wrap disabled - scroll horizontally for long lines");
        }

        copyStatus.getStyleClass().removeAll("error-text", "success-text");
        copyStatus.getStyleClass().add("info-text");
    }

    @FXML
    private void expandAllCategories() {
        categoryTiles.values().forEach(tile -> tile.setExpanded(true));
        copyStatus.setText("🌳 All categories expanded");
        copyStatus.getStyleClass().removeAll("error-text", "success-text");
        copyStatus.getStyleClass().add("info-text");
    }

    @FXML
    private void collapseAllCategories() {
        categoryTiles.values().forEach(tile -> tile.setExpanded(false));
        copyStatus.setText("🌲 All categories collapsed");
        copyStatus.getStyleClass().removeAll("error-text", "success-text");
        copyStatus.getStyleClass().add("info-text");
    }

    @FXML
    private void closePopup() {
        // 🎯 CRITICAL: Unregister from ThemeManager before closing
        cleanup();

        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();

        if (parentController != null) {
            parentController.logOperation("STACK_CODES_POPUP_CLOSED | Stack code examples window closed");
        }
    }

    /**
     * 🎯 CRITICAL: Cleanup method to unregister from ThemeManager
     */
    public void cleanup() {
        try {
            if (isSceneRegistered && popupScene != null) {
                ThemeManager.getInstance().unregisterScene(popupScene);
                isSceneRegistered = false;
                System.out.println("🧹 StackCodeController unregistered from ThemeManager");
            }
        } catch (Exception e) {
            System.err.println("❌ Error during cleanup: " + e.getMessage());
        }
    }

    // Inner Classes
    private static class CategoryInfo {
        final String name;
        final String color;
        final String[] examples;

        CategoryInfo(String name, String color, String[] examples) {
            this.name = name;
            this.color = color;
            this.examples = examples;
        }
    }

    /**
     * Category tile using CSS variables only
     */
    private static class CategoryTile {
        private final VBox container;
        private final HBox headerBox;
        private final VBox contentBox;
        private final Label titleLabel;
        private final FontIcon expandIcon;
        private final CategoryInfo categoryInfo;
        private final List<Button> exampleButtons = new ArrayList<>();
        private boolean isExpanded = true;

        public CategoryTile(CategoryInfo categoryInfo, java.util.function.Consumer<String> onExampleClick) {
            this.categoryInfo = categoryInfo;

            // Create main container with CSS classes only
            container = new VBox();
            container.setSpacing(0);
            container.getStyleClass().addAll("control-panel", "rounded");

            // Create header
            headerBox = new HBox();
            headerBox.setAlignment(Pos.CENTER_LEFT);
            headerBox.setSpacing(10);
            headerBox.setPadding(new Insets(15, 15, 15, 15));
            headerBox.getStyleClass().add("category-header");
            headerBox.setStyle("-fx-cursor: hand;");

            titleLabel = new Label(categoryInfo.name);
            titleLabel.getStyleClass().add("category-title");

            expandIcon = new FontIcon("fas-chevron-down");
            expandIcon.setIconSize(12);
            expandIcon.getStyleClass().add("expand-icon");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            headerBox.getChildren().addAll(titleLabel, spacer, expandIcon);

            // Create content area
            contentBox = new VBox();
            contentBox.setSpacing(8);
            contentBox.setPadding(new Insets(0, 15, 15, 15));
            contentBox.getStyleClass().add("category-content");

            // Add example buttons
            for (String example : categoryInfo.examples) {
                Button exampleBtn = new Button(example);
                exampleBtn.getStyleClass().addAll("control-button", "example-button");
                exampleBtn.setMaxWidth(Double.MAX_VALUE);
                exampleBtn.setAlignment(Pos.CENTER_LEFT);
                exampleBtn.setOnAction(e -> onExampleClick.accept(example));
                exampleButtons.add(exampleBtn);
                contentBox.getChildren().add(exampleBtn);
            }

            headerBox.setOnMouseClicked(e -> toggleExpanded());
            container.getChildren().addAll(headerBox, contentBox);
        }

        public VBox getContainer() { return container; }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
            contentBox.setVisible(expanded);
            contentBox.setManaged(expanded);
            expandIcon.setIconLiteral(expanded ? "fas-chevron-down" : "fas-chevron-right");
        }

        public void toggleExpanded() { setExpanded(!isExpanded); }

        public boolean containsExample(String searchTerm) {
            return titleLabel.getText().toLowerCase().contains(searchTerm) ||
                    java.util.Arrays.stream(categoryInfo.examples)
                            .anyMatch(example -> example.toLowerCase().contains(searchTerm));
        }

        public void filterExamples(String searchTerm) {
            for (int i = 0; i < exampleButtons.size(); i++) {
                Button btn = exampleButtons.get(i);
                boolean matches = categoryInfo.examples[i].toLowerCase().contains(searchTerm);
                btn.setVisible(matches);
                btn.setManaged(matches);
            }
        }

        public void setVisible(boolean visible) {
            container.setVisible(visible);
            container.setManaged(visible);
        }

        /**
         * No manual theme classes needed - CSS variables handle everything
         */
        public void useThemeClasses(boolean isDarkMode) {
            // CSS variables handle all the theming automatically
        }
    }
}
