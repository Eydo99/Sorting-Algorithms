package sorting.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import sorting.comparison.CSVExporter;
import sorting.comparison.ComparisonRunner;
import sorting.comparison.ParallelComparisonManager;
import sorting.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComparisonController {

    // ── Algorithm checkboxes ─────────────────────────────────────
    @FXML private CheckBox checkBubble;
    @FXML private CheckBox checkInsertion;
    @FXML private CheckBox checkSelection;
    @FXML private CheckBox checkMerge;
    @FXML private CheckBox checkQuick;
    @FXML private CheckBox checkHeap;

    // ── Form inputs ──────────────────────────────────────────────
    @FXML private ComboBox<String> arrayTypeCombo;
    @FXML private TextField        arraySizeField;
    @FXML private TextField        runsField;
    @FXML private CheckBox         fromFileCheck;
    @FXML private TextField        fileNameField;

    // ── Pending tasks table ──────────────────────────────────────
    @FXML private TableView<ComparisonTask>           pendingTasksTable;
    @FXML private TableColumn<ComparisonTask, String>  pendingAlgoCol;
    @FXML private TableColumn<ComparisonTask, Integer> pendingSizeCol;
    @FXML private TableColumn<ComparisonTask, String>  pendingTypeCol;
    @FXML private TableColumn<ComparisonTask, Integer> pendingRunsCol;
    @FXML private TableColumn<ComparisonTask, Boolean> pendingFromFileCol;

    // ── Results table ────────────────────────────────────────────
    @FXML private TableView<SortResult>              resultsTable;
    @FXML private TableColumn<SortResult, String>    resultAlgoCol;
    @FXML private TableColumn<SortResult, Integer>   resultSizeCol;
    @FXML private TableColumn<SortResult, String>    resultTypeCol;
    @FXML private TableColumn<SortResult, Integer>   resultRunCol;
    @FXML private TableColumn<SortResult, Long>      resultTimeCol;
    @FXML private TableColumn<SortResult, Long>      resultCompCol;
    @FXML private TableColumn<SortResult, Long>      resultInterCol;

    // ── Summary labels ───────────────────────────────────────────
    @FXML private Label avgLabel;
    @FXML private Label minLabel;
    @FXML private Label maxLabel;
    @FXML private Label resultCountLabel;

    // ── Data lists ───────────────────────────────────────────────
    private final ObservableList<ComparisonTask> pendingTasks =
            FXCollections.observableArrayList();
    private final ObservableList<SortResult> results =
            FXCollections.observableArrayList();

    // ─────────────────────────────────────────────────────────────
    // initialize() — called automatically after FXML loads
    // ─────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {

        // Fill array type dropdown
        arrayTypeCombo.getItems().addAll(
                "RANDOM", "SORTED", "INVERSELY_SORTED"
        );
        arrayTypeCombo.setValue("RANDOM");

        // Enable file name field only when checkbox is ticked
        fromFileCheck.setOnAction(e ->
                fileNameField.setDisable(!fromFileCheck.isSelected())
        );

        // ── Wire pending tasks table columns ──
        pendingAlgoCol.setCellValueFactory(
                new PropertyValueFactory<>("algorithmName"));
        pendingSizeCol.setCellValueFactory(
                new PropertyValueFactory<>("arraySize"));
        pendingTypeCol.setCellValueFactory(
                new PropertyValueFactory<>("arrayType"));
        pendingRunsCol.setCellValueFactory(
                new PropertyValueFactory<>("noOfRuns"));
        pendingFromFileCol.setCellValueFactory(
                new PropertyValueFactory<>("fromFile"));
        pendingTasksTable.setItems(pendingTasks);
        pendingTasksTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // ── Wire results table columns ──
        resultAlgoCol.setCellValueFactory(
                new PropertyValueFactory<>("algorithmName"));
        resultSizeCol.setCellValueFactory(
                new PropertyValueFactory<>("arraySize"));
        resultTypeCol.setCellValueFactory(
                new PropertyValueFactory<>("arrayType"));
        resultRunCol.setCellValueFactory(
                new PropertyValueFactory<>("runNumber"));
        resultTimeCol.setCellValueFactory(
                new PropertyValueFactory<>("runtimeNs"));
        resultCompCol.setCellValueFactory(
                new PropertyValueFactory<>("comparisons"));
        resultInterCol.setCellValueFactory(
                new PropertyValueFactory<>("interchanges"));
        resultsTable.setItems(results);
        resultsTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Update result count badge whenever results change
        results.addListener((javafx.collections.ListChangeListener<SortResult>) c ->
                resultCountLabel.setText(results.size() + " rows")
        );
    }

    // ─────────────────────────────────────────────────────────────
    // handleRunSequential() — runs tasks one by one
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleRunSequential() {
        List<ComparisonTask> tasks = buildTasksFromForm();
        if (tasks == null) return;

        results.clear();
        List<List<SortResult>> allResults = new ArrayList<>();

        for (ComparisonTask task : tasks) {
            ComparisonRunner runner = new ComparisonRunner(task);
            List<SortResult> taskResults = runner.run();
            results.addAll(taskResults);
            allResults.add(taskResults);
        }

        updateSummary(allResults);
    }

    // ─────────────────────────────────────────────────────────────
    // handleRunParallel() — runs all tasks at the same time
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleRunParallel() {
        List<ComparisonTask> tasks = buildTasksFromForm();
        if (tasks == null) return;

        results.clear();
        ParallelComparisonManager manager = new ParallelComparisonManager();
        for (ComparisonTask task : tasks) manager.addTask(task);

        List<List<SortResult>> allResults = manager.runAll();
        manager.shutdown();

        for (List<SortResult> r : allResults) results.addAll(r);
        updateSummary(allResults);
    }

    // ─────────────────────────────────────────────────────────────
    // handleExportCsv() — opens save dialog and exports
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleExportCsv() {
        if (results.isEmpty()) {
            showAlert("No results to export.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save CSV");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = chooser.showSaveDialog(
                resultsTable.getScene().getWindow()
        );

        if (file != null) {
            List<SortResult> all = new ArrayList<>(results);
            ComparisonSummary summary = ComparisonSummary.getSummary(all);
            CSVExporter.export(all, summary, file.getAbsolutePath());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // handleClear() — clears everything
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleClear() {
        pendingTasks.clear();
        results.clear();
        avgLabel.setText("--");
        minLabel.setText("--");
        maxLabel.setText("--");
        resultCountLabel.setText("");
    }

    // ─────────────────────────────────────────────────────────────
    // buildTasksFromForm() — reads and validates all form inputs
    // returns null if validation fails
    // ─────────────────────────────────────────────────────────────
    private List<ComparisonTask> buildTasksFromForm() {

        // 1. Get selected algorithms
        List<String> algorithms = getSelectedAlgorithms();
        if (algorithms.isEmpty()) {
            showAlert("Please select at least one algorithm.");
            return null;
        }

        // 2. Validate array type
        if (arrayTypeCombo.getValue() == null) {
            showAlert("Please select an array type.");
            return null;
        }

        // 3. Validate size
        int size;
        try {
            size = Integer.parseInt(arraySizeField.getText().trim());
            if (size < 1) {
                showAlert("Array size must be at least 1.");
                return null;
            }
        } catch (NumberFormatException e) {
            showAlert("Array size must be a valid number.");
            return null;
        }

        // 4. Validate runs
        int runs;
        try {
            runs = Integer.parseInt(runsField.getText().trim());
            if (runs < 1) {
                showAlert("Runs must be at least 1.");
                return null;
            }
        } catch (NumberFormatException e) {
            showAlert("Runs must be a valid number.");
            return null;
        }

        // 5. Validate file name if from file is checked
        boolean fromFile = fromFileCheck.isSelected();
        String fileName  = fromFile ? fileNameField.getText().trim() : null;
        if (fromFile && (fileName == null || fileName.isEmpty())) {
            showAlert("Please enter a file name.");
            return null;
        }

        // 6. Build one task per selected algorithm
        ArrayType type = ArrayType.valueOf(arrayTypeCombo.getValue());
        List<ComparisonTask> tasks = new ArrayList<>();

        for (String algo : algorithms) {
            tasks.add(new ComparisonTask(
                    algo, size, type, runs, fromFile, fileName
            ));
        }

        // Add to pending tasks table so user can see them
        pendingTasks.addAll(tasks);
        return tasks;
    }

    // ─────────────────────────────────────────────────────────────
    // getSelectedAlgorithms() — returns list of checked algorithms
    // ─────────────────────────────────────────────────────────────
    private List<String> getSelectedAlgorithms() {
        List<String> selected = new ArrayList<>();
        if (checkBubble.isSelected())    selected.add("BubbleSort");
        if (checkInsertion.isSelected()) selected.add("InsertionSort");
        if (checkSelection.isSelected()) selected.add("SelectionSort");
        if (checkMerge.isSelected())     selected.add("MergeSort");
        if (checkQuick.isSelected())     selected.add("QuickSort");
        if (checkHeap.isSelected())      selected.add("HeapSort");
        return selected;
    }

    // ─────────────────────────────────────────────────────────────
    // updateSummary() — updates Avg / Min / Max labels
    // ─────────────────────────────────────────────────────────────
    private void updateSummary(List<List<SortResult>> allResults) {
        List<SortResult> flat = new ArrayList<>();
        allResults.forEach(flat::addAll);
        if (flat.isEmpty()) return;

        ComparisonSummary summary = ComparisonSummary.getSummary(flat);
        avgLabel.setText(summary.getAvgRuntimeNs() + " ns");
        minLabel.setText(summary.getMinRuntimeNs() + " ns");
        maxLabel.setText(summary.getMaxRuntimeNs() + " ns");
    }

    // ─────────────────────────────────────────────────────────────
    // showAlert() — shows a warning popup
    // ─────────────────────────────────────────────────────────────
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}