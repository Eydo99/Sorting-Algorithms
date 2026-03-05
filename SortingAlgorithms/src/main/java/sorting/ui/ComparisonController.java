package sorting.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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



//
//    // ── Data lists ───────────────────────────────────────────────
//    private final ObservableList<ComparisonTask> pendingTasks =
//            FXCollections.observableArrayList();
//    private final ObservableList<SortResult> results =
//            FXCollections.observableArrayList();


    @FXML private CheckBox checkBubble;
    @FXML private CheckBox checkInsertion;
    @FXML private CheckBox checkSelection;
    @FXML private CheckBox checkMerge;
    @FXML private CheckBox checkQuick;
    @FXML private CheckBox checkHeap;;


    @FXML private ComboBox<String> arrayTypeCombo;
    @FXML private TextField arraySizeField;
    @FXML private TextField runsField;
    @FXML private CheckBox fromFileField;
    @FXML private TextField fileNameField;

    @FXML  private TableView<ComparisonSummary> resultsTable;
    @FXML private TableColumn<ComparisonSummary, String> resultAlgoCol;
    @FXML private TableColumn<ComparisonSummary, Integer> resultSizeCol;
    @FXML private TableColumn<ComparisonSummary, String> resultTypeCol;
    @FXML private TableColumn<ComparisonSummary, Long> resultMinRunTimeCol;
    @FXML private TableColumn<ComparisonSummary, Long> resultMaxRunTimeCol;
    @FXML private TableColumn<ComparisonSummary, Long> resultAverageCol;
    @FXML private TableColumn<ComparisonSummary, Long> resultCompCol;
    @FXML private TableColumn<ComparisonSummary, Long> resultInterCol;


    @FXML private TableView<ComparisonTask> pendingTasksTable;
    @FXML private TableColumn<ComparisonTask, String> pendingAlgoCol;
    @FXML private TableColumn<ComparisonTask, Integer> pendingRunsCol;
    @FXML private TableColumn<ComparisonTask, Integer> pendingSizeCol;
    @FXML private TableColumn<ComparisonTask, String> pendingTypeCol;
    @FXML private TableColumn<ComparisonTask, Boolean> pendingFromFileCol;

    @FXML private Label avgLabel;
    @FXML private Label minLabel;
    @FXML private Label maxLabel;
    @FXML private Label resultCountLabel;

     private final ObservableList<ComparisonTask> pendingTasks = FXCollections.observableArrayList();
     private final ObservableList<ComparisonSummary> results = FXCollections.observableArrayList();




    @FXML
    public void initialize() {
        arrayTypeCombo.getItems().setAll("SORTED","INVERSELY_SORTED","RANDOM");
        arrayTypeCombo.setValue("RANDOM");

        fromFileField.setOnAction((event) -> {
            fileNameField.setDisable(!fromFileField.isSelected());
        });

        resultAlgoCol.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        resultSizeCol.setCellValueFactory(new PropertyValueFactory<>("arraySize"));
        resultTypeCol.setCellValueFactory(new PropertyValueFactory<>("arrayType"));
        resultMinRunTimeCol.setCellValueFactory(new PropertyValueFactory<>("minRuntimeNs"));
        resultMaxRunTimeCol.setCellValueFactory(new PropertyValueFactory<>("maxRuntimeNs"));
        resultAverageCol.setCellValueFactory(new PropertyValueFactory<>("avgRuntimeNs"));
        resultCompCol.setCellValueFactory(new PropertyValueFactory<>("comparisons"));
        resultInterCol.setCellValueFactory(new PropertyValueFactory<>("interchanges"));
        resultsTable.setItems(results);
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN); //looked for it


        pendingAlgoCol.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        pendingRunsCol.setCellValueFactory(new PropertyValueFactory<>("noOfRuns"));
        pendingSizeCol.setCellValueFactory(new PropertyValueFactory<>("arraySize"));
        pendingTypeCol.setCellValueFactory(new PropertyValueFactory<>("arrayType"));
        pendingFromFileCol.setCellValueFactory(new PropertyValueFactory<>("fromFile"));
        pendingTasksTable.setItems(pendingTasks);
        pendingTasksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);


        // Update result count badge whenever results change
        results.addListener((javafx.collections.ListChangeListener<ComparisonSummary>) c ->
                resultCountLabel.setText(results.size() + " rows")
        );


    }


    @FXML
    private void handleRunSequential()
    {
        List<ComparisonTask> tasks = buildTasksFromForm();
        if(tasks == null) return;

        results.clear();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {

                for(ComparisonTask task : tasks)
                {
                    ComparisonRunner runner = new ComparisonRunner(task);
                    List<SortResult> result = runner.run();
                    ComparisonSummary summary = ComparisonSummary.getSummary(result);

                    Platform.runLater(() -> {
                        results.add(summary);
                        pendingTasks.remove(task);
                    });
                }

                Platform.runLater(() -> updateSummary());

                return null;
            }
        };

        new Thread(task).start();
    }

    @FXML
    private void handleRunParallel()
    {
        List<ComparisonTask> tasks = buildTasksFromForm();
        if(tasks == null) return;

        results.clear();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {

                ParallelComparisonManager manager = new ParallelComparisonManager();

                for (ComparisonTask t : tasks)
                    manager.addTask(t);

                List<List<SortResult>> allResults = manager.runAll();
                manager.shutdown();

                for(List<SortResult> r : allResults)
                {
                    ComparisonSummary summary = ComparisonSummary.getSummary(r);

                    Platform.runLater(() -> results.add(summary));
                }

                Platform.runLater(() -> {
                    pendingTasks.removeAll(tasks);
                    updateSummary();
                });

                return null;
            }
        };

        new Thread(task).start();
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
            List<ComparisonSummary> summaries = new ArrayList<>(results);
            CSVExporter.export(summaries, file.getAbsolutePath());
        }
    }


    @FXML
    private void handleClear() {
        pendingTasks.clear();
        results.clear();
        avgLabel.setText("--");
        minLabel.setText("--");
        maxLabel.setText("--");
        resultCountLabel.setText("");
    }



    private List<ComparisonTask> buildTasksFromForm() {
        List<String> selectedAlgorithms=getSelectedAlgorithms();
        if(selectedAlgorithms.isEmpty())
        {
            showAlert("No algorithms selected.");
            return null;
        }

        int size;
        try
        {
            size=Integer.parseInt(arraySizeField.getText().trim());
            if(size<1)
            {
                showAlert("Please enter a number greater than 1.");
                return null;
            }
        }
        catch (NumberFormatException e)
        {
            showAlert("Please enter an integer");
            return null;
        }

        int runs;
        try
        {
            runs=Integer.parseInt(runsField.getText().trim());
            if(runs<1)
            {
                showAlert("Please enter a number greater than 1.");
                return null;
            }
        }catch (NumberFormatException e)
        {
            showAlert("Please enter an integer");
            return null;
        }

        boolean isFromFIle=fromFileField.isSelected();
        String fileName= (isFromFIle) ? fileNameField.getText().trim() : "";
        if(isFromFIle && fileName.isEmpty())
        {
            showAlert("Please enter a file name");
            return null;
        }

        ArrayType arrayType=ArrayType.valueOf(arrayTypeCombo.getValue().trim().toUpperCase());
        List<ComparisonTask> tasks = new ArrayList<>();

        for(String selectedAlgorithm : selectedAlgorithms)
        {
            tasks.add(new ComparisonTask(selectedAlgorithm,size,arrayType,runs,isFromFIle,fileName));
        }

        pendingTasks.addAll(tasks);
        return tasks;
    }


    private List<String> getSelectedAlgorithms() {
        List<String> algorithms = new ArrayList<>();
        if(checkBubble.isSelected()) { algorithms.add("BubbleSort"); }
        if(checkInsertion.isSelected()) { algorithms.add("InsertionSort"); }
        if(checkSelection.isSelected()) { algorithms.add("SelectionSort"); }
        if(checkQuick.isSelected()) { algorithms.add("QuickSort"); }
        if(checkMerge.isSelected()) { algorithms.add("MergeSort"); }
        if(checkHeap.isSelected()) { algorithms.add("HeapSort"); }
        return algorithms;
    }


    private void updateSummary() {
        long avg = results.stream().mapToLong(ComparisonSummary::getAvgRuntimeNs).sum() / results.size();
        long min = results.stream().mapToLong(ComparisonSummary::getMinRuntimeNs).min().orElse(0);
        long max = results.stream().mapToLong(ComparisonSummary::getMaxRuntimeNs).max().orElse(0);
        avgLabel.setText(avg + " ns");
        minLabel.setText(min + " ns");
        maxLabel.setText(max + " ns");
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