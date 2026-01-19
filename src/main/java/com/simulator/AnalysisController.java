package com.simulator;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import java.util.*;

public class AnalysisController {

    // Top controls
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> algoAComboBox;
    @FXML
    private ComboBox<String> algoBComboBox;
    @FXML
    private Button analyzeButton;
    @FXML
    private Button swapButton;

    // Summary labels
    @FXML
    private Label nameALabel, bestALabel, avgALabel, worstALabel, spaceALabel;
    @FXML
    private Label nameBLabel, bestBLabel, avgBLabel, worstBLabel, spaceBLabel;

    // Charts
    @FXML
    private LineChart<Number, Number> complexityChart;
    @FXML
    private NumberAxis xAxis, yAxis;
    @FXML
    private BarChart<String, Number> performanceChart;
    @FXML
    private CategoryAxis perfXAxis;
    @FXML
    private NumberAxis perfYAxis;

    // Table and narrative
    @FXML
    private TableView<Row> comparisonTable;
    @FXML
    private TableColumn<Row, String> nameColumn;
    @FXML
    private TableColumn<Row, String> bestColumn;
    @FXML
    private TableColumn<Row, String> avgColumn;
    @FXML
    private TableColumn<Row, String> worstColumn;
    @FXML
    private TableColumn<Row, String> spaceColumn;
    @FXML
    private TextArea analysisText;

    // Domain table (always visible)
    @FXML
    private TableView<Row> domainTable;
    @FXML
    private TableColumn<Row, String> dNameColumn;
    @FXML
    private TableColumn<Row, String> dBestColumn;
    @FXML
    private TableColumn<Row, String> dAvgColumn;
    @FXML
    private TableColumn<Row, String> dWorstColumn;
    @FXML
    private TableColumn<Row, String> dSpaceColumn;

    private AlgorithmRepository repo;

    @FXML
    public void initialize() {
        repo = AlgorithmRepository.getInstance();
        setupControls();
        setupTable();
        setupDomainTable();
        setupCharts();
        bindHandlers();

        // Increase analysis text area size
        if (analysisText != null) {
            analysisText.setPrefRowCount(25);
            analysisText.setPrefHeight(500);
            analysisText.setWrapText(true);
        }

        // Defaults
        if (!repo.getCategories().isEmpty()) {
            typeComboBox.getSelectionModel().selectFirst();
            populateAlgorithms();
            if (!algoAComboBox.getItems().isEmpty())
                algoAComboBox.getSelectionModel().select(0);
            if (algoBComboBox.getItems().size() > 1)
                algoBComboBox.getSelectionModel().select(1);
            analyze();
        }
    }

    private void setupControls() {
        typeComboBox.setItems(FXCollections.observableArrayList(repo.getCategories()));
        algoAComboBox.setItems(FXCollections.observableArrayList());
        algoBComboBox.setItems(FXCollections.observableArrayList());
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().name()));
        bestColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().best()));
        avgColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().avg()));
        worstColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().worst()));
        spaceColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().space()));
    }

    private void setupDomainTable() {
        if (dNameColumn != null)
            dNameColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().name()));
        if (dBestColumn != null)
            dBestColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().best()));
        if (dAvgColumn != null)
            dAvgColumn.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().avg()));
        if (dWorstColumn != null)
            dWorstColumn
                    .setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().worst()));
        if (dSpaceColumn != null)
            dSpaceColumn
                    .setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue().space()));
    }

    private void setupCharts() {
        complexityChart.setAnimated(false);
        complexityChart.setCreateSymbols(false);
        xAxis.setLabel("Input Size (n)");
        yAxis.setLabel("Relative Cost");
        performanceChart.setAnimated(false);
        perfXAxis.setLabel("Algorithm");
        perfYAxis.setLabel("Lower is better");
    }

    private void bindHandlers() {
        typeComboBox.setOnAction(e -> populateAlgorithms());
        swapButton.setOnAction(e -> swapAlgorithms());
        analyzeButton.setOnAction(e -> analyze());
    }

    private void populateAlgorithms() {
        String cat = typeComboBox.getValue();
        if (cat != null) {
            List<String> names = repo.getAlgorithms(cat).stream().map(AlgorithmRecord::getName).toList();
            algoAComboBox.setItems(FXCollections.observableArrayList(names));
            algoBComboBox.setItems(FXCollections.observableArrayList(names));

            // Populate domain table
            populateDomainTable();
        }
    }

    private void populateDomainTable() {
        String cat = typeComboBox.getValue();
        if (cat != null && domainTable != null) {
            List<AlgorithmRecord> domain = repo.getAlgorithms(cat);
            List<Row> rows = domain.stream()
                    .map(a -> new Row(a.getName(), a.getBest(), a.getAverage(), a.getWorst(), a.getSpace()))
                    .toList();
            domainTable.setItems(FXCollections.observableArrayList(rows));
            System.out.println("Domain table populated with " + rows.size() + " rows for category: " + cat);
        }
    }

    @FXML
    private void swapAlgorithms() {
        String a = algoAComboBox.getValue();
        String b = algoBComboBox.getValue();
        if (a == null || b == null)
            return;
        algoAComboBox.setValue(b);
        algoBComboBox.setValue(a);
        analyze();
    }

    @FXML
    private void analyze() {
        String cat = typeComboBox.getValue();
        String aName = algoAComboBox.getValue();
        String bName = algoBComboBox.getValue();
        if (cat == null || aName == null || bName == null || aName.equals(bName)) {
            analysisText.setText("Select a category and two distinct algorithms, then click Analyze.");
            comparisonTable.setItems(FXCollections.observableArrayList());
            complexityChart.getData().clear();
            performanceChart.getData().clear();
            return;
        }

        AlgorithmRecord A = repo.find(cat, aName).orElse(null);
        AlgorithmRecord B = repo.find(cat, bName).orElse(null);
        if (A == null || B == null)
            return;

        // Summary tiles
        fillSummary(A, nameALabel, bestALabel, avgALabel, worstALabel, spaceALabel);
        fillSummary(B, nameBLabel, bestBLabel, avgBLabel, worstBLabel, spaceBLabel);

        // Table (two rows)
        List<Row> tableRows = Arrays.asList(
                new Row(A.getName(), A.getBest(), A.getAverage(), A.getWorst(), A.getSpace()),
                new Row(B.getName(), B.getBest(), B.getAverage(), B.getWorst(), B.getSpace()));
        comparisonTable.setItems(FXCollections.observableArrayList(tableRows));

        // Charts - FIXED: Show all 6 complexity curves
        drawComplexityCurves(cat, A, B);
        drawPerformanceBars(A, B);

        // Narrative
        analysisText.setText(buildNarrative(cat, A, B));
    }

    private void fillSummary(AlgorithmRecord r, Label name, Label best, Label avg, Label worst, Label space) {
        if (name != null)
            name.setText(r.getName());
        if (best != null)
            best.setText(r.getBest());
        if (avg != null)
            avg.setText(r.getAverage());
        if (worst != null)
            worst.setText(r.getWorst());
        if (space != null)
            space.setText(r.getSpace());
    }

    // FIXED: Show Best/Average/Worst curves for BOTH algorithms (6 curves total)
    private void drawComplexityCurves(String cat, AlgorithmRecord A, AlgorithmRecord B) {
        complexityChart.getData().clear();
        int maxN = 100;
        NumberAxis x = xAxis;
        NumberAxis y = yAxis;
        x.setAutoRanging(false);
        x.setLowerBound(1);
        x.setUpperBound(maxN);
        x.setTickUnit(10);
        y.setAutoRanging(true);

        // Create series for Algorithm A (3 curves)
        XYChart.Series<Number, Number> bestA = new XYChart.Series<>();
        bestA.setName(A.getName() + " — Best " + safe(A.getBest()));
        XYChart.Series<Number, Number> avgA = new XYChart.Series<>();
        avgA.setName(A.getName() + " — Avg " + safe(A.getAverage()));
        XYChart.Series<Number, Number> worstA = new XYChart.Series<>();
        worstA.setName(A.getName() + " — Worst " + safe(A.getWorst()));

        // Create series for Algorithm B (3 curves)
        XYChart.Series<Number, Number> bestB = new XYChart.Series<>();
        bestB.setName(B.getName() + " — Best " + safe(B.getBest()));
        XYChart.Series<Number, Number> avgB = new XYChart.Series<>();
        avgB.setName(B.getName() + " — Avg " + safe(B.getAverage()));
        XYChart.Series<Number, Number> worstB = new XYChart.Series<>();
        worstB.setName(B.getName() + " — Worst " + safe(B.getWorst()));

        // Populate data points for all 6 series
        for (int n = 1; n <= maxN; n += 5) {
            // Algorithm A data points
            bestA.getData().add(new XYChart.Data<>(n, evalComplexity(A.getBest(), n, cat)));
            avgA.getData().add(new XYChart.Data<>(n, evalComplexity(A.getAverage(), n, cat)));
            worstA.getData().add(new XYChart.Data<>(n, evalComplexity(A.getWorst(), n, cat)));

            // Algorithm B data points
            bestB.getData().add(new XYChart.Data<>(n, evalComplexity(B.getBest(), n, cat)));
            avgB.getData().add(new XYChart.Data<>(n, evalComplexity(B.getAverage(), n, cat)));
            worstB.getData().add(new XYChart.Data<>(n, evalComplexity(B.getWorst(), n, cat)));
        }

        // Add all 6 series to the chart (will show 6 different colors)
        complexityChart.getData().addAll(bestA, avgA, worstA, bestB, avgB, worstB);

        System.out.println("Added " + complexityChart.getData().size() + " series to complexity chart");
    }

    private String safe(String s) {
        return s == null ? "O(n)" : s;
    }

    private void drawPerformanceBars(AlgorithmRecord A, AlgorithmRecord B) {
        performanceChart.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Average-case score");
        s.getData().add(new XYChart.Data<>(A.getName(), perfScore(A.getAverage())));
        s.getData().add(new XYChart.Data<>(B.getName(), perfScore(B.getAverage())));
        performanceChart.getData().add(s);
    }

    private String buildNarrative(String cat, AlgorithmRecord A, AlgorithmRecord B) {
        StringBuilder sb = new StringBuilder();
        sb.append("🔍 ALGORITHM COMPARISON ANALYSIS\n");
        sb.append("═══════════════════════════════════════\n\n");

        sb.append("📁 Category: ").append(cat).append("\n\n");

        sb.append("⚖️ COMPLEXITY COMPARISON:\n");
        sb.append("┌─ Algorithm A: ").append(A.getName()).append("\n");
        sb.append("│  • Best: ").append(A.getBest()).append("\n");
        sb.append("│  • Average: ").append(A.getAverage()).append("\n");
        sb.append("│  • Worst: ").append(A.getWorst()).append("\n");
        sb.append("│  • Space: ").append(A.getSpace()).append("\n");
        sb.append("└─────────────────────────────────\n\n");

        sb.append("┌─ Algorithm B: ").append(B.getName()).append("\n");
        sb.append("│  • Best: ").append(B.getBest()).append("\n");
        sb.append("│  • Average: ").append(B.getAverage()).append("\n");
        sb.append("│  • Worst: ").append(B.getWorst()).append("\n");
        sb.append("│  • Space: ").append(B.getSpace()).append("\n");
        sb.append("└─────────────────────────────────\n\n");

        // Winner by average-case
        int cmp = Integer.compare(perfScore(A.getAverage()), perfScore(B.getAverage()));
        sb.append("🏆 RECOMMENDATION:\n");
        if (cmp < 0) {
            sb.append("✅ Prefer ").append(A.getName())
                    .append(" for typical inputs based on average-case complexity.\n");
            sb.append("   ").append(A.getName()).append(" has better average performance: ").append(A.getAverage())
                    .append(" vs ").append(B.getAverage()).append("\n\n");
        } else if (cmp > 0) {
            sb.append("✅ Prefer ").append(B.getName())
                    .append(" for typical inputs based on average-case complexity.\n");
            sb.append("   ").append(B.getName()).append(" has better average performance: ").append(B.getAverage())
                    .append(" vs ").append(A.getAverage()).append("\n\n");
        } else {
            sb.append("⚖️ Both algorithms are comparable on average complexity.\n");
            sb.append("   Consider stability, in-place behavior, and specific data characteristics.\n\n");
        }

        // Extras for sorting
        if ("Sorting Algorithms".equalsIgnoreCase(cat)) {
            sb.append("📋 ALGORITHM PROPERTIES:\n");
            sb.append("┌─ Stability & Memory Usage:\n");
            sb.append("│  • ").append(A.getName()).append(": Stable=")
                    .append(A.getStable()).append(", In-Place=").append(A.getInPlace()).append("\n");
            sb.append("│  • ").append(B.getName()).append(": Stable=")
                    .append(B.getStable()).append(", In-Place=").append(B.getInPlace()).append("\n");
            sb.append("└─────────────────────────────────\n\n");
        }

        // Practical usage notes
        sb.append("💡 PRACTICAL CONSIDERATIONS:\n");
        if (!A.getNotes().isBlank()) {
            sb.append("• ").append(A.getName()).append(": ").append(A.getNotes()).append("\n");
        }
        if (!B.getNotes().isBlank()) {
            sb.append("• ").append(B.getName()).append(": ").append(B.getNotes()).append("\n");
        }

        sb.append("\n📊 WHEN TO USE EACH:\n");
        sb.append("• Choose ").append(A.getName()).append(" when: ");
        if (A.getBest().contains("O(n)")) {
            sb.append("data is nearly sorted, ");
        }
        if (A.getSpace().contains("O(1)")) {
            sb.append("memory is limited, ");
        }
        if (A.getStable()) {
            sb.append("stability is required, ");
        }
        sb.append("typical use case applies.\n");

        sb.append("• Choose ").append(B.getName()).append(" when: ");
        if (B.getBest().contains("O(n)")) {
            sb.append("data is nearly sorted, ");
        }
        if (B.getSpace().contains("O(1)")) {
            sb.append("memory is limited, ");
        }
        if (B.getStable()) {
            sb.append("stability is required, ");
        }
        sb.append("typical use case applies.\n");

        return sb.toString();
    }

    // Map typical classes to numeric cost; used for bar chart and y-scaling
    private int perfScore(String c) {
        String k = normalize(c);
        return switch (k) {
            case "O(1)" -> 1;
            case "O(log n)" -> 2;
            case "O(log log n)" -> 2; // small constant class
            case "O(n)" -> 5;
            case "O(n log n)" -> 8;
            case "O(n^2)", "O(n²)" -> 25;
            case "O(n^3)", "O(n³)" -> 60;
            case "O(2^n)" -> 100;
            case "O(v+e)" -> 10; // graph linear
            case "O(v^2)" -> 25; // graph quadratic
            default -> 12;
        };
    }

    private double evalComplexity(String c, int n, String cat) {
        String k = normalize(c);
        switch (k) {
            case "O(1)" -> {
                return 1;
            }
            case "O(log n)" -> {
                return Math.log(Math.max(2, n)) / Math.log(2);
            }
            case "O(log log n)" -> {
                return Math.log(Math.max(2, Math.log(Math.max(2, n)) / Math.log(2))) / Math.log(2);
            }
            case "O(n)" -> {
                return n;
            }
            case "O(n log n)" -> {
                return n * (Math.log(Math.max(2, n)) / Math.log(2));
            }
            case "O(n^2)", "O(n²)" -> {
                return n * n;
            }
            case "O(n^3)", "O(n³)" -> {
                return n * n * n;
            }
            case "O(2^n)" -> {
                return Math.pow(2, Math.min(n, 20));
            }
            // Graph forms: approximate with n or n^2 to visualize
            case "O(v+e)" -> {
                return n;
            }
            case "O(v^2)" -> {
                return n * n;
            }
            default -> {
                return n;
            }
        }
    }

    private String normalize(String raw) {
        if (raw == null)
            return "";
        String s = raw.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        s = s.replace("logn", "log n");
        s = s.replace("nlogn", "n log n");
        s = s.replace("n2", "n^2");
        s = s.replace("v+e", "v+e");
        s = s.replace("v2", "v^2");
        // Canonical forms we switch on
        if (s.contains("loglog"))
            return "O(log log n)";
        if (s.contains("log") && s.startsWith("o(nlog"))
            return "O(n log n)";
        if (s.contains("2^n"))
            return "O(2^n)";
        if (s.contains("n^2") || s.contains("n²"))
            return "O(n^2)";
        if (s.contains("n^3") || s.contains("n³"))
            return "O(n^3)";
        if (s.contains("(v+e)"))
            return "O(V+E)".toLowerCase(Locale.ROOT);
        if (s.contains("(v^2)") || s.contains("(v²)"))
            return "O(V^2)".toLowerCase(Locale.ROOT);
        return raw.toUpperCase(Locale.ROOT);
    }

    @FXML
    private void goBack() {
        try {
            ((javafx.stage.Stage) complexityChart.getScene().getWindow()).close();
        } catch (Exception e) {
            System.err.println("Error closing Analysis window: " + e.getMessage());
        }
    }

    // Table row record
    public record Row(String name, String best, String avg, String worst, String space) {
    }
}
