package sorting.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import sorting.input.ArrayGenerator;
import sorting.algorithms.SortAlgorithm;
import sorting.algorithms.SortAlgorithmFactory;
import sorting.model.ArrayType;
import sorting.visualization.BarChartPane;
import sorting.visualization.SortAnimator;

import java.util.ArrayList;
import java.util.List;

public class VisualizationController {

    // ── FXML fields ──────────────────────────────────────────────
    @FXML private ComboBox<String> vizAlgoCombo;
    @FXML private TextField vizSizeField;
    @FXML private ComboBox<String> vizTypeCombo;
    @FXML private Slider speedSlider;
    @FXML private HBox visualizerBox;

    // ── One animator per panel ───────────────────────────────────
    private final List<SortAnimator> animators = new ArrayList<>();

    // ─────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        vizAlgoCombo.getItems().addAll(
                "BubbleSort","InsertionSort","SelectionSort",
                "MergeSort","QuickSort","HeapSort"
        );
        vizTypeCombo.getItems().addAll("RANDOM","SORTED","INVERSELY_SORTED");
    }

    // ─────────────────────────────────────────────────────────────
    // "Add Visualizer" button
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handleAddVisualizer() {

        // 1. Validate
        if (vizAlgoCombo.getValue() == null) { showAlert("Select an algorithm."); return; }
        if (vizTypeCombo.getValue() == null)  { showAlert("Select an array type."); return; }

        int size;
        try {
            size = Integer.parseInt(vizSizeField.getText().trim());
            if (size < 2 || size > 100) { showAlert("Size must be between 2 and 100."); return; }
        } catch (NumberFormatException e) { showAlert("Array size must be a number."); return; }

        // 2. Generate array and run algorithm to collect steps
        ArrayType type = ArrayType.valueOf(vizTypeCombo.getValue());
        int[] array = ArrayGenerator.generateFromRandom(size, type);

        SortAlgorithm algo = SortAlgorithmFactory.getAlgorithm(vizAlgoCombo.getValue());
        algo.setSteps(true);
        algo.sort(array.clone());

        // 3. Create BarChartPane (the visual panel)
        BarChartPane chartPane = new BarChartPane(vizAlgoCombo.getValue(), array);

        // 4. Create SortAnimator (manages steps + animation)
        SortAnimator animator = new SortAnimator(algo.getSteps(), chartPane);
        animators.add(animator);

        // 5. Add the visual panel to the screen
        visualizerBox.getChildren().add(chartPane.getRoot());
    }

    // ─────────────────────────────────────────────────────────────
    // Playback controls — just delegate to each animator
    // ─────────────────────────────────────────────────────────────
    @FXML
    private void handlePlay() {
        if (animators.isEmpty()) { showAlert("Add at least one visualizer first."); return; }
        double speed = speedSlider.getValue();
        for (SortAnimator animator : animators) animator.play(speed);
    }

    @FXML
    private void handlePause() {
        for (SortAnimator animator : animators) animator.pause();
    }

    @FXML
    private void handleStep() {
        for (SortAnimator animator : animators) animator.step();
    }

    @FXML
    private void handleReset() {
        for (SortAnimator animator : animators) animator.reset();
    }

    // ─────────────────────────────────────────────────────────────
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}