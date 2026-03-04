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

    // Add Task form
    @FXML private ComboBox<String> algorithmCombo;
    @FXML private TextField runsField;
    @FXML private TextField arraySizeField;
    @FXML private ComboBox<String> arrayTypeCombo;
    @FXML private CheckBox fromFileCheck;
    @FXML private TextField fileNameField;
    @FXML private Button addTaskBtn;

    // Pending Tasks table
    @FXML private TableView<ComparisonTask> pendingTasksTable;
    @FXML private TableColumn<ComparisonTask, String>  pendingAlgoCol;
    @FXML private TableColumn<ComparisonTask, Integer> pendingSizeCol;
    @FXML private TableColumn<ComparisonTask, String>  pendingTypeCol;
    @FXML private TableColumn<ComparisonTask, Integer> pendingRunsCol;
    @FXML private TableColumn<ComparisonTask, Boolean> pendingFromFileCol;

    // Results table
    @FXML private TableView<SortResult> resultsTable;
    @FXML private TableColumn<SortResult, String>  resultAlgoCol;
    @FXML private TableColumn<SortResult, Integer> resultSizeCol;
    @FXML private TableColumn<SortResult, String>  resultTypeCol;
    @FXML private TableColumn<SortResult, Integer> resultRunCol;
    @FXML private TableColumn<SortResult, Long>    resultTimeCol;
    @FXML private TableColumn<SortResult, Long>    resultCompCol;
    @FXML private TableColumn<SortResult, Long>    resultInterCol;

    // Summary labels
    @FXML private Label avgLabel;
    @FXML private Label minLabel;
    @FXML private Label maxLabel;

    private final ObservableList<ComparisonTask> pendingTasks = FXCollections.observableArrayList();
    private final ObservableList<SortResult> results = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Fill dropdowns
        algorithmCombo.getItems().addAll("BubbleSort","InsertionSort","SelectionSort","MergeSort","QuickSort","HeapSort");
        arrayTypeCombo.getItems().addAll("RANDOM","SORTED","INVERSELY_SORTED");

        // Enable file name field only when checkbox is ticked
        fromFileCheck.setOnAction(e -> fileNameField.setDisable(!fromFileCheck.isSelected()));

        // Wire pending tasks table columns
        pendingAlgoCol.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        pendingSizeCol.setCellValueFactory(new PropertyValueFactory<>("arraySize"));
        pendingTypeCol.setCellValueFactory(new PropertyValueFactory<>("arrayType"));
        pendingRunsCol.setCellValueFactory(new PropertyValueFactory<>("noOfRuns"));
        pendingFromFileCol.setCellValueFactory(new PropertyValueFactory<>("fromFile"));
        pendingTasksTable.setItems(pendingTasks);

        // Wire results table columns
        resultAlgoCol.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        resultSizeCol.setCellValueFactory(new PropertyValueFactory<>("arraySize"));
        resultTypeCol.setCellValueFactory(new PropertyValueFactory<>("arrayType"));
        resultRunCol.setCellValueFactory(new PropertyValueFactory<>("runNumber"));
        resultTimeCol.setCellValueFactory(new PropertyValueFactory<>("runtimeNs"));
        resultCompCol.setCellValueFactory(new PropertyValueFactory<>("comparisons"));
        resultInterCol.setCellValueFactory(new PropertyValueFactory<>("interchanges"));
        resultsTable.setItems(results);
    }

    @FXML
    private void handleAddTask() {
        // Validate inputs
        if (algorithmCombo.getValue() == null) { showAlert("Please select an algorithm."); return; }
        if (arrayTypeCombo.getValue() == null)  { showAlert("Please select an array type."); return; }

        int size = 0, runs = 0;
        try { size = Integer.parseInt(arraySizeField.getText().trim()); }
        catch (NumberFormatException e) { showAlert("Array Size must be a number."); return; }
        try { runs = Integer.parseInt(runsField.getText().trim()); }
        catch (NumberFormatException e) { showAlert("Runs must be a number."); return; }

        boolean fromFile = fromFileCheck.isSelected();
        String fileName = fromFile ? fileNameField.getText().trim() : null;
        if (fromFile && (fileName == null || fileName.isEmpty())) { showAlert("Please enter a file name."); return; }

        ArrayType type = ArrayType.valueOf(arrayTypeCombo.getValue());
        ComparisonTask task = new ComparisonTask(algorithmCombo.getValue(), size, type, runs, fromFile, fileName);
        pendingTasks.add(task);
    }

    @FXML
    private void handleRunSequential() {
        if (pendingTasks.isEmpty()) { showAlert("No tasks to run."); return; }
        results.clear();
        List<List<SortResult>> allResults = new ArrayList<>();
        for (ComparisonTask task : pendingTasks) {
            ComparisonRunner runner = new ComparisonRunner(task);
            List<SortResult> taskResults = runner.run();
            results.addAll(taskResults);
            allResults.add(taskResults);
        }
        updateSummary(allResults);
    }

    @FXML
    private void handleRunParallel() {
        if (pendingTasks.isEmpty()) { showAlert("No tasks to run."); return; }
        results.clear();
        ParallelComparisonManager manager = new ParallelComparisonManager();
        for (ComparisonTask task : pendingTasks) manager.addTask(task);
        List<List<SortResult>> allResults = manager.runAll();
        manager.shutdown();
        for (List<SortResult> r : allResults) results.addAll(r);
        updateSummary(allResults);
    }

    @FXML
    private void handleExportCsv() {
        if (results.isEmpty()) { showAlert("No results to export."); return; }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save CSV");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = chooser.showSaveDialog(resultsTable.getScene().getWindow());
        if (file != null) {
            List<SortResult> allResults = new ArrayList<>(results);
            ComparisonSummary summary = ComparisonSummary.getSummary(allResults);
            CSVExporter.export(allResults, summary, file.getAbsolutePath());
        }
    }

    @FXML
    private void handleClear() {
        pendingTasks.clear();
        results.clear();
        avgLabel.setText("--");
        minLabel.setText("--");
        maxLabel.setText("--");
    }

    private void updateSummary(List<List<SortResult>> allResults) {
        List<SortResult> flat = new ArrayList<>();
        allResults.forEach(flat::addAll);
        if (flat.isEmpty()) return;
        ComparisonSummary summary = ComparisonSummary.getSummary(flat);
        avgLabel.setText(summary.getAvgRuntimeNs() + " ns");
        minLabel.setText(summary.getMinRuntimeNs() + " ns");
        maxLabel.setText(summary.getMaxRuntimeNs() + " ns");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}