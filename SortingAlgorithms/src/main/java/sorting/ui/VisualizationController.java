package sorting.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import sorting.algorithms.AbstractSort;
import sorting.algorithms.SortAlgorithm;
import sorting.algorithms.SortAlgorithmFactory;
import sorting.input.ArrayGenerator;
import sorting.model.ArrayType;
import sorting.model.SortStep;
import sorting.visualization.BarChartPane;
import sorting.visualization.SortAnimator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VisualizationController {
    @FXML private ComboBox<String> vizTypeCombo;
    @FXML private TextField        vizSizeField;
    @FXML private Slider           speedSlider;
    @FXML private HBox             visualizerBox;

    @FXML private CheckBox vizCheckBubble;
    @FXML private CheckBox vizCheckInsertion;
    @FXML private CheckBox vizCheckSelection;
    @FXML private CheckBox vizCheckMerge;
    @FXML private CheckBox vizCheckQuick;
    @FXML private CheckBox vizCheckHeap;

    @FXML private Button selectFileBtn;
    @FXML private Label  selectedFileLabel;
    private String selectedFilePath = null;

    private final List<SortAnimator> animators = new ArrayList<>();

    @FXML
    public void initialize() {
        vizTypeCombo.getItems().addAll(
                "RANDOM", "SORTED", "INVERSELY_SORTED"
        );
        vizTypeCombo.setValue("RANDOM");
    }

    @FXML
    private void handleAddVisualizer() {

        List<String> algorithms = getSelectedAlgorithms();
        if (algorithms.isEmpty()) {
            showAlert("Please select at least one algorithm");
            return;
        }

        int[] baseArray = buildBaseArray();
        if (baseArray == null) return;

        for (String algoName : algorithms) {

            SortAlgorithm algo = SortAlgorithmFactory.getAlgorithm(algoName);
            ((AbstractSort) algo).setSteps(true);
            int[] arrayToSort = baseArray.clone();
            algo.sort(arrayToSort);

            List<SortStep> steps = algo.getSteps();

            if (steps.isEmpty()) {
                showAlert(algoName + " produced no steps. Try a larger array");
                continue;
            }

            BarChartPane chartPane = new BarChartPane(algoName, baseArray);

            SortAnimator animator = new SortAnimator(
                    steps,
                    algo.getComparisons(),
                    algo.getInterchanges(),
                    chartPane
            );

            animators.add(animator);
            visualizerBox.getChildren().add(chartPane.getRoot());
        }
    }

    private int[] buildBaseArray() {

        if (selectedFilePath != null) {
            int[] array = ArrayGenerator.generateFromFile(selectedFilePath);

            if (array == null) {
                showAlert("Could not read file: " + selectedFilePath);
                return null;
            }
            if (array.length < 2) {
                showAlert("File must contain at least 2 elements");
                return null;
            }
            if (array.length > 100) {
                showAlert("File array too large — maximum 100 elements for visualization");
                return null;
            }
            return array;

        } else {
            if (vizTypeCombo.getValue() == null) {
                showAlert("Please select an array type");
                return null;
            }

            int size;
            try {
                size = Integer.parseInt(vizSizeField.getText().trim());
                if (size < 2 || size > 100) {
                    showAlert("Array size must be between 2 and 100");
                    return null;
                }
            } catch (NumberFormatException e) {
                showAlert("Array size must be a valid number");
                return null;
            }

            ArrayType type = ArrayType.valueOf(vizTypeCombo.getValue());
            return ArrayGenerator.generateFromRandom(size, type);
        }
    }

    @FXML
    private void handlePlay() {
        if (animators.isEmpty()) {
            showAlert("Add at least one visualizer first");
            return;
        }

        double speed = speedSlider.getValue();
        for (SortAnimator animator : animators) {
            animator.startAnimation(speed);
        }
    }

    @FXML
    private void handlePause() {
        for (SortAnimator animator : animators) {
            animator.pauseAnimation();
        }
    }

    @FXML
    private void handleStep() {
        for (SortAnimator animator : animators) {
            animator.step();
        }
    }

    @FXML
    private void handleReset() {
        for (SortAnimator animator : animators) animator.reset();
        visualizerBox.getChildren().clear();
        animators.clear();
        selectedFilePath = null;
        selectedFileLabel.setText("No file selected");
    }

    private List<String> getSelectedAlgorithms() {
        List<String> selected = new ArrayList<>();
        if (vizCheckBubble.isSelected())    selected.add("BubbleSort");
        if (vizCheckInsertion.isSelected()) selected.add("InsertionSort");
        if (vizCheckSelection.isSelected()) selected.add("SelectionSort");
        if (vizCheckMerge.isSelected())     selected.add("MergeSort");
        if (vizCheckQuick.isSelected())     selected.add("QuickSort");
        if (vizCheckHeap.isSelected())      selected.add("HeapSort");
        return selected;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleSelectFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Input File");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = chooser.showOpenDialog(
                selectFileBtn.getScene().getWindow()
        );

        if (file != null) {
            selectedFilePath = file.getAbsolutePath();
            selectedFileLabel.setText(file.getName());
        }
    }
}