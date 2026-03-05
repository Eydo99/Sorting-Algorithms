package sorting.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import sorting.algorithms.AbstractSort;
import sorting.algorithms.SortAlgorithm;
import sorting.algorithms.SortAlgorithmFactory;
import sorting.input.ArrayGenerator;
import sorting.model.ArrayType;
import sorting.model.SortStep;
import sorting.visualization.BarChartPane;
import sorting.visualization.SortAnimator;

import java.util.ArrayList;
import java.util.List;

public class VisualizationController {

    // ── FXML fields ──────────────────────────────────────────────
    @FXML private ComboBox<String> vizTypeCombo;
    @FXML private TextField        vizSizeField;
    @FXML private Slider           speedSlider;
    @FXML private HBox             visualizerBox;

    // ── Algorithm checkboxes ─────────────────────────────────────
    @FXML private CheckBox vizCheckBubble;
    @FXML private CheckBox vizCheckInsertion;
    @FXML private CheckBox vizCheckSelection;
    @FXML private CheckBox vizCheckMerge;
    @FXML private CheckBox vizCheckQuick;
    @FXML private CheckBox vizCheckHeap;

    // ── One animator per panel ───────────────────────────────────
    private final List<SortAnimator> animators = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────
    // initialize() — called automatically after FXML loads
    // ─────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        vizTypeCombo.getItems().addAll(
                "RANDOM", "SORTED", "INVERSELY_SORTED"
        );
        vizTypeCombo.setValue("RANDOM");
    }

    // ─────────────────────────────────────────────────────────────
    // handleAddVisualizer() — creates one panel per checked algo
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleAddVisualizer() {

        // 1. Get selected algorithms
        List<String> algorithms = getSelectedAlgorithms();
        if (algorithms.isEmpty()) {
            showAlert("Please select at least one algorithm.");
            return;
        }

        // 2. Validate array type
        if (vizTypeCombo.getValue() == null) {
            showAlert("Please select an array type.");
            return;
        }

        // 3. Validate size
        int size;
        try {
            size = Integer.parseInt(vizSizeField.getText().trim());
            if (size < 2 || size > 100) {
                showAlert("Array size must be between 2 and 100.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Array size must be a valid number.");
            return;
        }

        // 4. Generate ONE shared array for all algorithms
        ArrayType type  = ArrayType.valueOf(vizTypeCombo.getValue());
        int[] baseArray = ArrayGenerator.generateFromRandom(size, type);

        // 5. Create one panel per selected algorithm
        for (String algoName : algorithms) {

            // Run algorithm with step collection ON
            SortAlgorithm algo = SortAlgorithmFactory.getAlgorithm(algoName);
            ((AbstractSort) algo).setSteps(true);
            int[] arrayToSort = baseArray.clone();
            algo.sort(arrayToSort);

            List<SortStep> steps = algo.getSteps();

            if (steps.isEmpty()) {
                showAlert(algoName + " produced no steps. Try a larger array.");
                continue;
            }

            // Create the visual panel
            BarChartPane chartPane = new BarChartPane(algoName, baseArray);

            // Create the animator
            SortAnimator animator = new SortAnimator(
                    steps,
                    algo.getComparisons(),
                    algo.getInterchanges(),
                    chartPane
            );

            // Store and display
            animators.add(animator);
            visualizerBox.getChildren().add(chartPane.getRoot());
        }
    }

    // ─────────────────────────────────────────────────────────────
    // handlePlay() — starts all animators together
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handlePlay() {
        if (animators.isEmpty()) {
            showAlert("Add at least one visualizer first.");
            return;
        }

        double speed = speedSlider.getValue();
        for (SortAnimator animator : animators) {
            animator.play(speed);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // handlePause() — pauses all animators
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handlePause() {
        for (SortAnimator animator : animators) {
            animator.pause();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // handleStep() — advances all animators by one step
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleStep() {
        for (SortAnimator animator : animators) {
            animator.step();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // handleReset() — resets all animators and clears panels
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleReset() {
        for (SortAnimator animator : animators) {
            animator.reset();
        }
        // Clear all panels from screen and animator list
        visualizerBox.getChildren().clear();
        animators.clear();
    }

    // ─────────────────────────────────────────────────────────────
    // getSelectedAlgorithms() — returns list of checked algorithms
    // ─────────────────────────────────────────────────────────────
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