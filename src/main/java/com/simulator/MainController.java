package com.simulator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.Initializable;
import com.simulator.TooltipService;
import com.simulator.TooltipService.AlgorithmInfo;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class MainController implements Initializable {

    @FXML
    private Button themeToggleButton;
    @FXML
    private Label statusLabel;
    @FXML
    private VBox sortingCard;
    @FXML
    private VBox searchCard;
    @FXML
    private VBox stackCard;
    @FXML
    private VBox linkedListCard;
    @FXML
    private VBox arrayCard;
    @FXML
    private VBox analysisCard;
    @FXML
    private VBox graphCard;
    @FXML
    private VBox queueCard; // NEW: Queue module
    @FXML
    private VBox hashTableCard; // NEW: HashTable module
    @FXML
    private Button sortingButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button stackButton;
    @FXML
    private Button linkedListButton;
    @FXML
    private Button arrayButton;
    @FXML
    private Button analysisButton;
    @FXML
    private Button graphButton;
    @FXML
    private Button queueButton; // NEW: Queue button
    @FXML
    private Button hashTableButton; // NEW: HashTable button

    private NavigationService navigationService;
    private AnimationService animationService;
    private TooltipService tooltipService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("MainController initialized");

        // Initialize services with error handling
        try {
            navigationService = new NavigationService();
            animationService = AnimationService.getInstance();
            tooltipService = TooltipService.getInstance(); // NEW: Initialize tooltip service
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
        }

        // Register scene with theme manager
        Platform.runLater(() -> {
            if (statusLabel != null && statusLabel.getScene() != null) {
                try {
                    ThemeManager.getInstance().registerScene(statusLabel.getScene());
                    setupKeyboardShortcuts();
                } catch (Exception e) {
                    System.out.println("Scene registration not available: " + e.getMessage());
                }
            }
        });

        // Set initial emoji theme
        updateThemeEmoji();
        updateStatusLabel("Algorithm Simulator ready - Click any card to explore algorithms");

        // Setup click handlers and hover tooltips
        setupClickHandlers();
        setupHoverTooltips(); // NEW: Setup hover tooltips

        // Start welcome animation
        playWelcomeAnimation();
    }

    /**
     * NEW: Setup hover tooltips for algorithm cards
     */
    private void setupHoverTooltips() {
        // Sorting Algorithms Tooltip
        if (sortingCard != null) {
            AlgorithmInfo sortingInfo = new AlgorithmInfo(
                    "Sorting Algorithms",
                    "Fundamental algorithms that arrange elements in a specific order. Essential for optimizing search operations and data organization.",
                    "• Database indexing and queries\n• E-commerce product listings\n• Scientific data analysis\n• File system organization",
                    "O(n log n) to O(n²)",
                    "O(1) to O(n)",
                    "fas-sort-amount-up");
            setupCardTooltip(sortingCard, sortingInfo);
        }

        // Search Algorithms Tooltip
        if (searchCard != null) {
            AlgorithmInfo searchInfo = new AlgorithmInfo(
                    "Search Algorithms",
                    "Efficient methods to locate specific elements within data structures. Critical for information retrieval systems.",
                    "• Web search engines\n• Database queries\n• Autocomplete systems\n• Navigation applications",
                    "O(log n) to O(n)",
                    "O(1) to O(n)",
                    "fas-search");
            setupCardTooltip(searchCard, searchInfo);
        }

        // Stack Operations Tooltip
        if (stackCard != null) {
            AlgorithmInfo stackInfo = new AlgorithmInfo(
                    "Stack Operations",
                    "Last-In-First-Out (LIFO) data structure operations. Fundamental for memory management and expression evaluation.",
                    "• Function call management\n• Undo operations in applications\n• Expression parsing\n• Browser history",
                    "O(1) for all operations",
                    "O(n)",
                    "fas-layer-group");
            setupCardTooltip(stackCard, stackInfo);
        }

        // Linked Lists Tooltip
        if (linkedListCard != null) {
            AlgorithmInfo linkedListInfo = new AlgorithmInfo(
                    "Linked Lists",
                    "Dynamic data structures with elements connected through pointers. Excellent for dynamic memory allocation.",
                    "• Music playlists\n• Image viewers\n• Undo/Redo functionality\n• Dynamic memory management",
                    "O(1) to O(n)",
                    "O(n)",
                    "fas-link");
            setupCardTooltip(linkedListCard, linkedListInfo);
        }

        // Array Operations Tooltip
        if (arrayCard != null) {
            AlgorithmInfo arrayInfo = new AlgorithmInfo(
                    "Array Operations",
                    "Fundamental operations on contiguous memory structures. Building blocks for more complex data structures.",
                    "• Matrix operations\n• Image processing\n• Game development\n• Scientific computing",
                    "O(1) to O(n)",
                    "O(1) to O(n)",
                    "fas-th-list");
            setupCardTooltip(arrayCard, arrayInfo);
        }

        // Graph Algorithms Tooltip
        if (graphCard != null) {
            AlgorithmInfo graphInfo = new AlgorithmInfo(
                    "Graph Algorithms",
                    "Algorithms for traversing and analyzing graph structures. Essential for network analysis and pathfinding.",
                    "• Social network analysis\n• GPS navigation systems\n• Network routing\n• Recommendation systems",
                    "O(V + E) to O(V²)",
                    "O(V) to O(V + E)",
                    "fas-project-diagram");
            setupCardTooltip(graphCard, graphInfo);
        }

        // Performance Analysis Tooltip
        if (analysisCard != null) {
            AlgorithmInfo analysisInfo = new AlgorithmInfo(
                    "Performance Analysis",
                    "Mathematical analysis of algorithm efficiency using Big-O notation. Critical for choosing optimal solutions.",
                    "• Algorithm optimization\n• System performance tuning\n• Resource allocation\n• Scalability planning",
                    "Varies by algorithm",
                    "Varies by algorithm",
                    "fas-chart-line");
            setupCardTooltip(analysisCard, analysisInfo);
        }

        // NEW: Queue Operations Tooltip
        if (queueCard != null) {
            AlgorithmInfo queueInfo = new AlgorithmInfo(
                    "Queue Operations",
                    "First-In-First-Out (FIFO) data structure operations. Fundamental for task scheduling and buffering.",
                    "• Task scheduling systems\n• Print job management\n• Breadth-first search\n• Buffer queues",
                    "O(1) for all operations",
                    "O(n)",
                    "fas-stream");
            setupCardTooltip(queueCard, queueInfo);
        }

        // NEW: Hash Table Tooltip
        if (hashTableCard != null) {
            AlgorithmInfo hashTableInfo = new AlgorithmInfo(
                    "Hash Table",
                    "Key-value data structure using hash functions for O(1) average lookup. Essential for fast data retrieval.",
                    "• Database indexing\n• Caching systems\n• Symbol tables\n• Duplicate detection",
                    "O(1) average, O(n) worst",
                    "O(n)",
                    "fas-hashtag");
            setupCardTooltip(hashTableCard, hashTableInfo);
        }
    }

    /**
     * ENTERPRISE: Professional tooltip setup with debouncing
     */
    private void setupCardTooltip(VBox card, AlgorithmInfo info) {
        // CARD-TO-CARD FIX: Let TooltipService handle card switching
        // Don't block events - the service already debounces and handles transitions

        card.setOnMouseEntered(e -> {
            // Always notify the service - it will handle hiding previous tooltip
            // and showing the new one with proper debouncing
            System.out.println("🖱️ Mouse entered: " + info.getTitle());
            tooltipService.showAlgorithmTooltip(card, info);
            updateStatusLabel("📖 Learning about " + info.getTitle() + "...");
        });

        card.setOnMouseExited(e -> {
            // Only hide if we're not immediately entering another card
            // The TooltipService's debouncing will handle this properly
            System.out.println("🖱️ Mouse exited: " + info.getTitle());
            tooltipService.hideCurrentTooltip();
            updateStatusLabel("Algorithm Simulator ready - Click any card to explore algorithms");
        });
    }

    public void cleanup() {
        System.out.println("🧹 Starting MainController cleanup...");

        try {
            // Hide any open tooltips and shutdown tooltip service
            if (tooltipService != null) {
                tooltipService.hideCurrentTooltip();
                System.out.println("✅ Tooltip service cleaned up");
            }

            // Close all navigation windows
            if (navigationService != null) {
                navigationService.closeAllWindows();
                System.out.println("✅ Navigation service cleaned up");
            }

            // Unregister scene from theme manager
            if (statusLabel != null && statusLabel.getScene() != null) {
                ThemeManager.getInstance().unregisterScene(statusLabel.getScene());
                System.out.println("✅ Scene unregistered from ThemeManager");
            }

            // Stop any running animations
            if (animationService != null) {
                // Animation service cleanup if needed
                System.out.println("✅ Animation service cleaned up");
            }

            System.out.println("🎉 MainController cleanup completed successfully");

        } catch (Exception e) {
            System.err.println("❌ Error during MainController cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupKeyboardShortcuts() {
        if (statusLabel != null && statusLabel.getScene() != null) {
            statusLabel.getScene().setOnKeyPressed(this::handleKeyboardShortcuts);
            System.out.println("✅ Keyboard shortcuts registered successfully");
        }
    }

    private void updateThemeEmoji() {
        if (themeToggleButton != null) {
            try {
                boolean isDarkMode = ThemeManager.getInstance().isDarkMode();
                if (isDarkMode) {
                    themeToggleButton.setText("🌙");
                    themeToggleButton.setStyle(
                            "-fx-font-size: 22px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-text-fill: #a78bfa; " +
                                    "-fx-background-color: rgba(167,139,250,0.1); " +
                                    "-fx-border-color: rgba(167,139,250,0.3); " +
                                    "-fx-border-width: 1px; " +
                                    "-fx-border-radius: 10px; " +
                                    "-fx-background-radius: 10px; " +
                                    "-fx-padding: 8px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-pref-width: 50px; " +
                                    "-fx-pref-height: 50px; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(167,139,250,0.5), 8, 0, 0, 3);");
                    System.out.println("🌙 Dark Mode - Moon emoji displayed");
                } else {
                    themeToggleButton.setText("💡");
                    themeToggleButton.setStyle(
                            "-fx-font-size: 22px; " +
                                    "-fx-font-weight: bold; " +
                                    "-fx-text-fill: #f59e0b; " +
                                    "-fx-background-color: rgba(245,158,11,0.1); " +
                                    "-fx-border-color: rgba(245,158,11,0.3); " +
                                    "-fx-border-width: 1px; " +
                                    "-fx-border-radius: 10px; " +
                                    "-fx-background-radius: 10px; " +
                                    "-fx-padding: 8px; " +
                                    "-fx-cursor: hand; " +
                                    "-fx-pref-width: 50px; " +
                                    "-fx-pref-height: 50px; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(245,158,11,0.5), 8, 0, 0, 3);");
                    System.out.println("💡 Light Mode - Light bulb emoji displayed");
                }
            } catch (Exception e) {
                System.err.println("❌ Error updating theme emoji: " + e.getMessage());
                themeToggleButton.setText("🎨");
                themeToggleButton.setStyle("-fx-font-size: 20px; -fx-cursor: hand;");
            }
        }
    }

    private void setupClickHandlers() {
        // Card click handlers
        if (sortingCard != null) {
            sortingCard.setOnMouseClicked(this::openSortingModule);
        }
        if (searchCard != null) {
            searchCard.setOnMouseClicked(this::openSearchModule);
        }
        if (stackCard != null) {
            stackCard.setOnMouseClicked(this::openStackModule);
        }
        if (linkedListCard != null) {
            linkedListCard.setOnMouseClicked(this::openLinkedListModule);
        }
        if (arrayCard != null) {
            arrayCard.setOnMouseClicked(this::openArrayModule);
        }
        if (analysisCard != null) {
            analysisCard.setOnMouseClicked(this::openAnalysisModule);
        }
        if (graphCard != null) {
            graphCard.setOnMouseClicked(this::openGraphModule);
        }

        // Button click handlers
        if (sortingButton != null) {
            sortingButton.setOnAction(e -> openSortingModule(null));
        }
        if (searchButton != null) {
            searchButton.setOnAction(e -> openSearchModule(null));
        }
        if (stackButton != null) {
            stackButton.setOnAction(e -> openStackModule(null));
        }
        if (linkedListButton != null) {
            linkedListButton.setOnAction(e -> openLinkedListModule(null));
        }
        if (arrayButton != null) {
            arrayButton.setOnAction(e -> openArrayModule(null));
        }
        if (analysisButton != null) {
            analysisButton.setOnAction(e -> openAnalysisModule(null));
        }
        if (graphButton != null) {
            graphButton.setOnAction(e -> openGraphModule(null));
        }
        // NEW: Queue click handlers
        if (queueCard != null) {
            queueCard.setOnMouseClicked(this::openQueueModule);
        }
        if (queueButton != null) {
            queueButton.setOnAction(e -> openQueueModule(null));
        }
        // NEW: HashTable click handlers
        if (hashTableCard != null) {
            hashTableCard.setOnMouseClicked(this::openHashTableModule);
        }
        if (hashTableButton != null) {
            hashTableButton.setOnAction(e -> openHashTableModule(null));
        }
    }

    private void playWelcomeAnimation() {
        if (statusLabel != null && animationService != null) {
            try {
                FadeTransition fadeIn = animationService.createFadeAnimation(statusLabel, 0.0, 1.0, 1000);
                fadeIn.play();
            } catch (Exception e) {
                System.out.println("Animation service error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void toggleTheme() {
        System.out.println("🎯 Theme toggle clicked - Current mode: " +
                (ThemeManager.getInstance().isDarkMode() ? "Dark" : "Light"));
        try {
            if (themeToggleButton != null) {
                ScaleTransition scaleOut = new ScaleTransition(Duration.millis(100), themeToggleButton);
                scaleOut.setToX(1.3);
                scaleOut.setToY(1.3);
                scaleOut.setAutoReverse(true);
                scaleOut.setCycleCount(2);
                scaleOut.play();

                if (animationService != null) {
                    try {
                        animationService.playButtonHoverAnimation(themeToggleButton, true);
                    } catch (Exception e) {
                        System.out.println("Animation service not available: " + e.getMessage());
                    }
                }
            }

            ThemeManager.getInstance().toggleTheme();
            updateThemeEmoji();
            updateStatusLabel("🎨 Theme switched to " +
                    (ThemeManager.getInstance().isDarkMode() ? "Dark" : "Light") + " mode");

        } catch (Exception e) {
            System.err.println("Error toggling theme: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showSettings() {
        System.out.println("Settings button clicked");
        Alert settingsDialog = new Alert(Alert.AlertType.INFORMATION);
        settingsDialog.setTitle("Settings");
        settingsDialog.setHeaderText("Algorithm Simulator Settings");
        settingsDialog.setContentText(
                "🎨 Theme Settings:\n" +
                        "• Current Theme: " + ThemeManager.getInstance().getCurrentTheme() + "\n" +
                        "• Dark Mode: " + (ThemeManager.getInstance().isDarkMode() ? "Enabled" : "Disabled") + "\n\n" +
                        "🚀 Performance Settings:\n" +
                        "• Animation Speed: "
                        + String.format("%.1fx", AnimationService.getInstance().getAnimationSpeed()) + "\n" +
                        "• Hardware Acceleration: Enabled\n\n" +
                        "📊 Statistics:\n" +
                        "• Open Windows: " + navigationService.getOpenWindowCount() + "\n" +
                        "• Memory Usage: Optimized\n\n" +
                        "💡 Tip: Use Ctrl+T to quickly toggle themes!");
        settingsDialog.showAndWait();
        updateStatusLabel("Settings accessed - Customize your experience");
    }

    @FXML
    private void showAbout() {
        System.out.println("About button clicked");

        try {
            // Create custom stage for about dialog
            Stage aboutStage = new Stage();
            aboutStage.initOwner(statusLabel.getScene().getWindow());
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.setTitle("About Algorithm Simulator");
            aboutStage.setResizable(true);

            // Create main container
            VBox mainContainer = new VBox();
            mainContainer.setSpacing(0);
            mainContainer.getStyleClass().addAll("main-layout", "about-dialog");

            // Header section
            HBox header = new HBox();
            header.setAlignment(javafx.geometry.Pos.CENTER);
            header.setSpacing(15);
            header.getStyleClass().add("about-header");
            header.setPadding(new javafx.geometry.Insets(25, 30, 20, 30));

            // App icon
            FontIcon appIcon = new FontIcon("fas-laptop-code");
            appIcon.setIconSize(48);
            appIcon.getStyleClass().add("about-icon");

            // Title section
            VBox titleSection = new VBox();
            titleSection.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            titleSection.setSpacing(5);

            Label appTitle = new Label("Algorithm Simulator");
            appTitle.getStyleClass().add("about-title");
            appTitle.setWrapText(true);

            Label appVersion = new Label("Professional Edition v2.0");
            appVersion.getStyleClass().add("about-version");
            appVersion.setWrapText(true);

            Label authorLabel = new Label("by Ankit Raj");
            authorLabel.getStyleClass().add("about-author");
            authorLabel.setWrapText(true);

            // 🎯 ENHANCED: Increase font size and make bold
            appTitle.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");
            appVersion.setStyle("-fx-font-size: 18px; -fx-font-weight: semi-bold;");
            authorLabel.setStyle("-fx-font-size: 16px; -fx-font-style: italic; -fx-font-weight: normal;");

            // 🎯 ENHANCED: Apply theme-aware drop shadow
            boolean isDarkMode = ThemeManager.getInstance().isDarkMode();

            // Create drop shadow effect with theme-aware colors
            DropShadow titleShadow = new DropShadow();
            titleShadow.setRadius(8.0);
            titleShadow.setOffsetX(4.0);
            titleShadow.setOffsetY(4.0);

            DropShadow versionShadow = new DropShadow();
            versionShadow.setRadius(6.0);
            versionShadow.setOffsetX(3.0);
            versionShadow.setOffsetY(3.0);

            if (isDarkMode) {
                // Dark mode: Golden/yellowish shadow
                titleShadow.setColor(Color.web("rgba(255, 215, 0, 0.8)")); // Rich gold
                versionShadow.setColor(Color.web("rgba(255, 223, 0, 0.6)")); // Lighter gold
            } else {
                // Light mode: Black shadow
                titleShadow.setColor(Color.web("rgba(0, 0, 0, 0.7)")); // Deep black
                versionShadow.setColor(Color.web("rgba(0, 0, 0, 0.5)")); // Lighter black
            }

            // Apply shadow effects
            appTitle.setEffect(titleShadow);
            appVersion.setEffect(versionShadow);

            // 🎯 APPLY THEME-AWARE TEXT COLORS TO HEADER LABELS
            applyThemeTextColors(appTitle, appVersion, authorLabel);

            titleSection.getChildren().addAll(appTitle, appVersion, authorLabel);

            header.getChildren().addAll(appIcon, titleSection);

            // Content area with scroll
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.getStyleClass().add("about-scroll");
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // Content container
            VBox contentContainer = new VBox();
            contentContainer.setSpacing(20);
            contentContainer.setPadding(new javafx.geometry.Insets(20, 35, 30, 35));
            contentContainer.getStyleClass().add("about-content");

            // Create content sections
            contentContainer.getChildren().addAll(
                    createAboutSection("🎯 COMPREHENSIVE INTERACTIVE ALGORITHM VISUALIZATION PLATFORM",
                            "A professional-grade educational tool designed for computer science students, educators, and developers."),

                    createFeatureSection("🚀 CORE ALGORITHM MODULES", new String[] {
                            "• Sorting Algorithms (9): Bubble, Selection, Insertion, Merge, Quick, Heap, Shell, Radix, Counting",
                            "• Search Algorithms (3): Linear, Binary, Interpolation with real-time visualization",
                            "• Data Structures: Professional Stack & LinkedList implementations",
                            "• Graph Algorithms (8): BFS, DFS, Dijkstra, A*, Shortest Path, Topological Sort, Cycle Detection",
                            "• Algorithm Analysis: Comprehensive performance comparison with complexity curves"
                    }),

                    createFeatureSection("📚 INTERACTIVE CODE REPOSITORIES", new String[] {
                            "• Stack Operations: 16+ complete code examples with applications",
                            "• LinkedList Collection: 25+ examples (Singly, Doubly, Circular implementations)",
                            "• Advanced Operations: Merge, reverse, cycle detection, sorting algorithms",
                            "• Copy-to-clipboard functionality with syntax highlighting",
                            "• Searchable code categories with expand/collapse navigation"
                    }),

                    createFeatureSection("🎨 ADVANCED VISUALIZATION FEATURES", new String[] {
                            "• Real-time Algorithm Animation with speed control (0.1x - 5x)",
                            "• Interactive Graph Canvas with drag-and-drop vertex positioning",
                            "• Professional 3D-style elements with shadows and depth effects",
                            "• Smart Color Coding: Comparison (Orange), Success (Green), Operations (Red)",
                            "• Multi-window support with up to 10 concurrent algorithm windows"
                    }),

                    createFeatureSection("🔬 EDUCATIONAL & ANALYSIS TOOLS", new String[] {
                            "• Algorithm Performance Comparison with mathematical complexity curves",
                            "• Side-by-side analysis supporting 6 simultaneous complexity visualizations",
                            "• Detailed pseudocode with complete Java implementations",
                            "• Smart algorithm recommendations based on use cases",
                            "• Comprehensive Big-O notation analysis for all complexity classes"
                    }),

                    createFeatureSection("💻 PROFESSIONAL TECHNOLOGY STACK", new String[] {
                            "• JavaFX 21+ with Modern FXML Architecture",
                            "• AtlantaFX Professional Theme Engine (6 themes: Primer, Nord, Cupertino)",
                            "• Enterprise-level Service Layer (Navigation, Theme, Animation services)",
                            "• Advanced Tooltip System with interaction-aware cancellation",
                            "• Repository Pattern with JSON-based algorithm data management",
                            "• Maven build system with modular architecture"
                    }),

                    createFeatureSection("🎮 USER EXPERIENCE EXCELLENCE", new String[] {
                            "• Live Dark/Light theme switching across all windows",
                            "• Professional keyboard shortcuts (Ctrl+T, Ctrl+W, F11, etc.)",
                            "• Smart input validation with real-time error handling",
                            "• Responsive design supporting multiple screen sizes",
                            "• Automatic window cascading and intelligent positioning"
                    }),

                    createFeatureSection("🎯 TARGET AUDIENCE", new String[] {
                            "👨‍🎓 Computer Science Students (Undergraduate/Graduate)",
                            "👩‍🏫 Educators and Computer Science Professors",
                            "👨‍💻 Professional Developers and Software Engineers",
                            "📝 Technical Interview Candidates",
                            "🏆 Competitive Programming Enthusiasts"
                    }),

                    createFeatureSection("🌟 UNIQUE VALUE PROPOSITION", new String[] {
                            "✅ 6 Major Algorithm Domains with 35+ Specific Implementations",
                            "✅ Complete Code Repository (100+ Examples) with Professional Documentation",
                            "✅ Interactive Experimentation Platform, Not Just Visualization",
                            "✅ Enterprise-Grade Architecture with Modern Design Patterns",
                            "✅ Cross-Platform Compatibility (Windows, macOS, Linux)"
                    }),

                    createAboutSection("🚀 ALGORITHM SIMULATOR - WHERE THEORY MEETS INTERACTIVE PRACTICE",
                            "Built for the next generation of computer scientists and algorithm enthusiasts!"));

            scrollPane.setContent(contentContainer);

            // Button area
            HBox buttonArea = new HBox();
            buttonArea.setAlignment(javafx.geometry.Pos.CENTER);
            buttonArea.setPadding(new javafx.geometry.Insets(20, 30, 25, 30));
            buttonArea.getStyleClass().add("about-buttons");

            Button closeButton = new Button("Close");
            closeButton.getStyleClass().addAll("category-button", "primary-button");
            closeButton.setPrefWidth(120);
            closeButton.setOnAction(e -> aboutStage.close());
            closeButton.setDefaultButton(true);

            buttonArea.getChildren().add(closeButton);

            // Assemble dialog
            mainContainer.getChildren().addAll(header, scrollPane, buttonArea);
            VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

            // Create scene
            Scene aboutScene = new Scene(mainContainer, 900, 700); // Increased width to 900px

            // Apply current theme
            try {
                aboutScene.getStylesheets().addAll(statusLabel.getScene().getStylesheets());
                ThemeManager.getInstance().registerScene(aboutScene);

                String themeClass = ThemeManager.getInstance().isDarkMode() ? "dark-theme" : "light-theme";
                String removeClass = ThemeManager.getInstance().isDarkMode() ? "light-theme" : "dark-theme";
                mainContainer.getStyleClass().removeAll(removeClass);
                mainContainer.getStyleClass().add(themeClass);
            } catch (Exception e) {
                System.out.println("ℹ️ Could not apply theme to about dialog");
            }

            aboutStage.setScene(aboutScene);

            // Set minimum size and center on parent
            aboutStage.setMinWidth(800);
            aboutStage.setMinHeight(600);

            // Center on parent window
            if (statusLabel.getScene() != null && statusLabel.getScene().getWindow() instanceof Stage) {
                Stage parentStage = (Stage) statusLabel.getScene().getWindow();
                aboutStage.setX(parentStage.getX() + (parentStage.getWidth() - 900) / 2);
                aboutStage.setY(parentStage.getY() + (parentStage.getHeight() - 700) / 2);
            }

            // Cleanup on close
            aboutStage.setOnCloseRequest(e -> {
                ThemeManager.getInstance().unregisterScene(aboutScene);
            });

            aboutStage.showAndWait();
            updateStatusLabel(
                    "About Algorithm Simulator - Professional Edition v2.0 | 35+ Algorithms | 6 Domains | 100+ Code Examples");

        } catch (Exception e) {
            System.err.println("❌ Error showing about dialog: " + e.getMessage());
            e.printStackTrace();

            // Fallback to simple alert
            Alert fallbackAlert = new Alert(Alert.AlertType.INFORMATION);
            fallbackAlert.setTitle("About Algorithm Simulator");
            fallbackAlert.setHeaderText("Algorithm Simulator - Professional Edition v2.0");
            fallbackAlert.setContentText("Comprehensive Interactive Algorithm Visualization Platform\n\n" +
                    "Features 35+ algorithms across 6 major domains with professional visualizations and code examples.");
            fallbackAlert.showAndWait();
        }
    }

    // Helper method to create content sections
    private VBox createAboutSection(String title, String description) {
        VBox section = new VBox();
        section.setSpacing(10);
        section.getStyleClass().add("about-section");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-header");
        titleLabel.setWrapText(true);

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("section-description");
        descLabel.setWrapText(true);

        // 🎯 APPLY THEME-AWARE TEXT COLORS
        applyThemeTextColors(titleLabel, descLabel);

        section.getChildren().addAll(titleLabel, descLabel);
        return section;
    }

    // Helper method to create feature sections
    private VBox createFeatureSection(String title, String[] features) {
        VBox section = new VBox();
        section.setSpacing(8);
        section.getStyleClass().add("feature-section");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("section-header");
        titleLabel.setWrapText(true);

        VBox featureList = new VBox();
        featureList.setSpacing(4);
        featureList.getStyleClass().add("feature-list");

        for (String feature : features) {
            Label featureLabel = new Label(feature);
            featureLabel.getStyleClass().add("feature-item");
            featureLabel.setWrapText(true);
            featureList.getChildren().add(featureLabel);

            // 🎯 APPLY THEME-AWARE TEXT COLOR
            applyThemeTextColors(featureLabel);
        }

        // 🎯 APPLY THEME-AWARE TEXT COLOR TO TITLE
        applyThemeTextColors(titleLabel);

        section.getChildren().addAll(titleLabel, featureList);
        return section;
    }

    // 🎯 ENHANCED: Helper method to apply theme-aware text colors with size
    // handling
    private void applyThemeTextColors(Label... labels) {
        try {
            boolean isDarkMode = ThemeManager.getInstance().isDarkMode();

            for (Label label : labels) {
                String baseStyle = "";

                // Determine base styling based on style class
                if (label.getStyleClass().contains("about-title")) {
                    baseStyle = "-fx-font-size: 36px; -fx-font-weight: bold;";
                } else if (label.getStyleClass().contains("about-version")) {
                    baseStyle = "-fx-font-size: 18px; -fx-font-weight: semi-bold;";
                } else if (label.getStyleClass().contains("section-header")) {
                    baseStyle = "-fx-font-size: 16px; -fx-font-weight: bold;";
                } else if (label.getStyleClass().contains("about-author")) { // ✅ NEW
                    baseStyle = "-fx-font-size: 16px; -fx-font-style: italic; -fx-font-weight: normal;";
                } else if (label.getStyleClass().contains("feature-item") ||
                        label.getStyleClass().contains("section-description")) {
                    baseStyle = "-fx-font-size: 13px;";
                }

                if (isDarkMode) {
                    // Dark mode: White text
                    label.setStyle(baseStyle + "; -fx-text-fill: #ffffff; -fx-opacity: 0.98;");
                } else {
                    // Light mode: Dark text
                    label.setStyle(baseStyle + "; -fx-text-fill: #1a1a1a; -fx-opacity: 0.95;");
                }
            }

            System.out.println(
                    "✅ Applied " + (isDarkMode ? "dark" : "light") + " theme colors to " + labels.length + " labels");

        } catch (Exception e) {
            System.out.println("ℹ️ Could not apply theme text colors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===== MODULE NAVIGATION METHODS ===== //

    @FXML
    private void openSortingModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("🔄 Opening Sorting Algorithms module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/SortingView.fxml", "Sorting Algorithms - Interactive Visualization",
                    1400, 900);
        }
        updateStatusLabel("🔄 Sorting Algorithms - Bubble, Selection, Insertion, Merge, Quick Sort");
    }

    @FXML
    private void openSearchModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("🔍 Opening Search Algorithms module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/SearchingView.fxml", "Search Algorithms - Linear & Binary Search", 1300,
                    800);
        }
        updateStatusLabel("🔍 Search Algorithms - Linear, Binary, and Interpolation Search");
    }

    @FXML
    private void openStackModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("📚 Opening Stack Operations module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/StackView.fxml", "Stack Operations - LIFO Data Structure", 1100, 700);
        }
        updateStatusLabel("📚 Stack Operations - Push, Pop, Peek with Visual Animations");
    }

    @FXML
    private void openLinkedListModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("🔗 Opening Linked List Operations module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/LinkedListView.fxml", "Linked List Operations - Dynamic Data Structure",
                    1300, 800);
        }
        updateStatusLabel("🔗 Linked List - Insert, Delete, Search with Visual Connections");
    }

    @FXML
    private void openGraphModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("🌐 Opening Graph Algorithms module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/GraphView.fxml", "Graph Algorithms - BFS & DFS Visualization", 1400,
                    900);
        }
        updateStatusLabel("🌐 Graph Algorithms - BFS, DFS, Shortest Path with Interactive Visualization");
    }

    @FXML
    private void openArrayModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("📊 Opening Array Operations module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/ArrayView.fxml", "Array Operations - Dynamic Array Management", 1200,
                    700);
        }
        updateStatusLabel("📊 Array Operations - Insert, Delete, Update with Visual Feedback");
    }

    @FXML
    private void openAnalysisModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("📈 Opening Performance Analysis module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/AnalysisView.fxml", "Performance Analysis - Algorithm Complexity", 1400,
                    850);
        }
        updateStatusLabel("📈 Performance Analysis - Time & Space Complexity Visualization");
    }

    // NEW: Queue Module Navigation
    @FXML
    private void openQueueModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("📤 Opening Queue Operations module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/QueueView.fxml", "Queue Operations - FIFO Data Structure", 1100, 700);
        }
        updateStatusLabel("📤 Queue Operations - Enqueue, Dequeue, Front, Rear with Visual Animations");
    }

    // NEW: Hash Table Module Navigation
    @FXML
    private void openHashTableModule(MouseEvent event) {
        TooltipService.getInstance().cancelTooltipOnInteraction();

        System.out.println("#️⃣ Opening Hash Table module");
        if (event != null)
            playCardClickAnimation(event);
        if (navigationService != null) {
            navigationService.openModule("/fxml/HashTableView.fxml", "Hash Table - Key-Value Data Structure", 1200,
                    750);
        }
        updateStatusLabel("#️⃣ Hash Table - Insert, Search, Delete with Collision Visualization");
    }

    private void playCardClickAnimation(MouseEvent event) {
        if (event.getSource() instanceof javafx.scene.Node && animationService != null) {
            javafx.scene.Node card = (javafx.scene.Node) event.getSource();
            try {
                animationService.playCardClickAnimation(card);
            } catch (Exception e) {
                System.out.println("Card animation error: " + e.getMessage());
            }
        }
    }

    private void updateStatusLabel(String message) {
        if (statusLabel != null && message != null) {
            Platform.runLater(() -> {
                if (animationService != null) {
                    try {
                        FadeTransition fadeOut = animationService.createFadeAnimation(statusLabel, 1.0, 0.3, 200);
                        fadeOut.setOnFinished(e -> {
                            statusLabel.setText(message);
                            FadeTransition fadeIn = animationService.createFadeAnimation(statusLabel, 0.3, 1.0, 200);
                            fadeIn.play();
                        });
                        fadeOut.play();
                    } catch (Exception e) {
                        statusLabel.setText(message);
                    }
                } else {
                    statusLabel.setText(message);
                }
            });
        }
    }

    public void handleKeyboardShortcuts(KeyEvent event) {
        if (event.isControlDown()) {
            switch (event.getCode()) {
                case T:
                    System.out.println("⌨️ Ctrl+T pressed - Toggling theme");
                    toggleTheme();
                    event.consume();
                    break;
                case Q:
                    System.out.println("⌨️ Ctrl+Q pressed - Exiting application");
                    Platform.exit();
                    event.consume();
                    break;
                case H:
                    System.out.println("⌨️ Ctrl+H pressed - Showing about");
                    showAbout();
                    event.consume();
                    break;
                case S:
                    System.out.println("⌨️ Ctrl+S pressed - Showing settings");
                    showSettings();
                    event.consume();
                    break;
                default:
                    break;
            }
        }

        switch (event.getCode()) {
            case F1:
                showAbout();
                event.consume();
                break;
            case F2:
                showSettings();
                event.consume();
                break;
            case ESCAPE:
                break;
        }
    }

    @FXML
    private void showFeedback() {
        System.out.println("=== FEEDBACK DIALOG DEBUG START ===");
        System.out.println("Current time: " + new java.util.Date());
        System.out.println("JavaFX version: " + System.getProperty("javafx.version"));
        System.out.println("Java version: " + System.getProperty("java.version"));

        try {
            // Log resource search attempts
            System.out.println("🔍 Searching for FXML resources...");

            String[] possiblePaths = {
                    "/fxml/FeedbackView.fxml",
                    "/FeedbackView.fxml",
                    "FeedbackView.fxml",
                    "/com/simulator/FeedbackView.fxml"
            };

            URL fxmlUrl = null;
            for (String path : possiblePaths) {
                System.out.println("Trying path: " + path);
                fxmlUrl = getClass().getResource(path);
                if (fxmlUrl != null) {
                    System.out.println("✅ FOUND FXML at: " + fxmlUrl);
                    break;
                } else {
                    System.out.println("❌ NOT FOUND: " + path);
                }
            }

            if (fxmlUrl == null) {
                System.err.println("❌ CRITICAL ERROR: FXML file not found in any location!");
                System.err.println("Classpath resources:");
                try {
                    java.util.Enumeration<URL> resources = getClass().getClassLoader().getResources("");
                    while (resources.hasMoreElements()) {
                        System.err.println("  - " + resources.nextElement());
                    }
                } catch (Exception e) {
                    System.err.println("Could not list classpath resources: " + e.getMessage());
                }

                showErrorAlert("Resource Error",
                        "Feedback dialog FXML file not found.\n\n" +
                                "This usually happens when the application is not packaged correctly.\n\n" +
                                "Check the log file for detailed information.");
                return;
            }

            // Try to create the stage
            System.out.println("🔍 Creating feedback stage...");
            Stage feedbackStage = new Stage();

            // Check if we have a parent window
            if (statusLabel != null && statusLabel.getScene() != null && statusLabel.getScene().getWindow() != null) {
                feedbackStage.initOwner(statusLabel.getScene().getWindow());
                System.out.println("✅ Parent window set");
            } else {
                System.out.println("⚠️ No parent window available");
            }

            feedbackStage.initModality(Modality.APPLICATION_MODAL);
            feedbackStage.setTitle("Send Feedback - Algorithm Simulator");
            feedbackStage.setResizable(true);

            // Try to load FXML
            System.out.println("🔍 Loading FXML...");
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            Parent root;
            try {
                root = loader.load();
                System.out.println("✅ FXML loaded successfully");
            } catch (Exception fxmlError) {
                System.err.println("❌ FXML Loading Error:");
                fxmlError.printStackTrace();

                showErrorAlert("FXML Loading Error",
                        "Could not load the feedback dialog interface.\n\n" +
                                "Error: " + fxmlError.getMessage() + "\n\n" +
                                "This might be due to missing dependencies or corrupt FXML file.");
                return;
            }

            // Check controller
            System.out.println("🔍 Getting controller...");
            try {
                FeedbackController controller = loader.getController();
                if (controller != null) {
                    System.out.println("✅ Controller loaded successfully: " + controller.getClass().getName());
                    // Try to set dialog stage if method exists
                    try {
                        java.lang.reflect.Method setStageMethod = controller.getClass().getMethod("setDialogStage",
                                Stage.class);
                        setStageMethod.invoke(controller, feedbackStage);
                        System.out.println("✅ Dialog stage set on controller");
                    } catch (Exception stageError) {
                        System.out.println("ℹ️ setDialogStage method not found or failed: " + stageError.getMessage());
                    }
                } else {
                    System.err.println("⚠️ Warning: FeedbackController is null");
                }
            } catch (Exception controllerError) {
                System.err.println("⚠️ Controller access error: " + controllerError.getMessage());
            }

            // Create scene and load CSS
            System.out.println("🔍 Creating scene and loading CSS...");
            Scene feedbackScene = new Scene(root, 600, 750);

            // Try to load CSS
            String[] cssPaths = {
                    "/css/main.css",
                    "/main.css",
                    "main.css"
            };

            boolean cssLoaded = false;
            for (String cssPath : cssPaths) {
                System.out.println("Trying CSS path: " + cssPath);
                try {
                    URL cssUrl = getClass().getResource(cssPath);
                    if (cssUrl != null) {
                        feedbackScene.getStylesheets().add(cssUrl.toExternalForm());
                        System.out.println("✅ CSS loaded from: " + cssUrl);
                        cssLoaded = true;
                        break;
                    } else {
                        System.out.println("❌ CSS not found at: " + cssPath);
                    }
                } catch (Exception cssError) {
                    System.err.println("❌ CSS loading error for " + cssPath + ": " + cssError.getMessage());
                }
            }

            if (!cssLoaded) {
                System.err.println("⚠️ Warning: No CSS file could be loaded. Using default styling.");
            }

            // Apply theme
            System.out.println("🔍 Applying theme...");
            try {
                if (ThemeManager.getInstance().isDarkMode()) {
                    root.getStyleClass().add("dark-theme");
                    System.out.println("✅ Dark theme applied");
                } else {
                    root.getStyleClass().add("light-theme");
                    System.out.println("✅ Light theme applied");
                }
            } catch (Exception themeError) {
                System.err.println("⚠️ Theme application error: " + themeError.getMessage());
            }

            // Set up stage
            System.out.println("🔍 Setting up stage...");
            feedbackStage.setScene(feedbackScene);
            feedbackStage.setMinWidth(500);
            feedbackStage.setMinHeight(600);

            // Try to center on parent
            try {
                if (statusLabel != null && statusLabel.getScene() != null
                        && statusLabel.getScene().getWindow() instanceof Stage) {
                    Stage parentStage = (Stage) statusLabel.getScene().getWindow();
                    feedbackStage.setX(parentStage.getX() + (parentStage.getWidth() - 600) / 2);
                    feedbackStage.setY(parentStage.getY() + (parentStage.getHeight() - 750) / 2);
                    System.out.println("✅ Stage centered on parent");
                }
            } catch (Exception centerError) {
                System.err.println("⚠️ Could not center stage: " + centerError.getMessage());
            }

            // Show the stage
            System.out.println("🔍 Showing feedback stage...");
            feedbackStage.showAndWait();
            System.out.println("✅ Feedback dialog completed successfully");

            // Update status
            if (statusLabel != null) {
                updateStatusLabel("Feedback system ready - Thank you!");
            }

        } catch (Exception e) {
            System.err.println("❌ CRITICAL ERROR in showFeedback():");
            System.err.println("Error type: " + e.getClass().getSimpleName());
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace();

            showErrorAlert("Critical Error",
                    "Cannot open feedback dialog.\n\n" +
                            "Error: " + e.getClass().getSimpleName() + "\n" +
                            "Message: " + e.getMessage() + "\n\n" +
                            "Please check the log file for complete details.");
        } finally {
            System.out.println("=== FEEDBACK DIALOG DEBUG END ===");
            System.out.println();
        }
    }

    private void showErrorAlert(String title, String message) {
        try {
            System.out.println("🔍 Showing error alert: " + title);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            System.out.println("✅ Error alert shown successfully");
        } catch (Exception alertError) {
            System.err.println("❌ Could not show alert: " + alertError.getMessage());
            alertError.printStackTrace();
        }
    }

}
