package sorting.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

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

    @FXML
    public void initialize() {
        vizTypeCombo.getItems().addAll(
                "RANDOM", "SORTED", "INVERSELY_SORTED"
        );
        vizTypeCombo.setValue("RANDOM");
    }

    @FXML private void handleAddVisualizer() {}
    @FXML private void handlePlay()          {}
    @FXML private void handlePause()         {}
    @FXML private void handleStep()          {}
    @FXML private void handleReset()         {}
}