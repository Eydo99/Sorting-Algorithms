package sorting.visualization;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class BarChartPane {

    // ── Colors ───────────────────────────────────────────────────
    private static final Color COLOR_BACKGROUND = Color.web("#13141a");
    private static final Color COLOR_BAR_IDLE = Color.web("#2e3148");
    private static final Color COLOR_BAR_ACTIVE = Color.web("#ff5c6a");
    private static final Color COLOR_BAR_DONE = Color.web("#00d4aa");

    // ── Dimensions ───────────────────────────────────────────────
    private static final int CANVAS_WIDTH = 400;
    private static final int CANVAS_HEIGHT = 280;
    private static final int ARRAY_CV_HEIGHT = 50;

    // ── Complexity map ───────────────────────────────────────────
    private static final java.util.Map<String, String> COMPLEXITY = java.util.Map.of(
            "BubbleSort", "O(n²)",
            "InsertionSort", "O(n²)",
            "SelectionSort", "O(n²)",
            "MergeSort", "O(n log n)",
            "QuickSort", "O(n log n)",
            "HeapSort", "O(n log n)");

    // ── UI Components ────────────────────────────────────────────
    private final VBox root;
    private final Canvas canvas;
    private final Canvas arrayCanvas;
    private final Label compLabel;
    private final Label interLabel;
    private final Label progressLabel;

    // ── State ────────────────────────────────────────────────────
    private final int[] originalArray;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────
    public BarChartPane(String algoName, int[] originalArray) {
        this.originalArray = originalArray.clone();

        // ── Header ──
        Label nameLabel = new Label(algoName);
        nameLabel.getStyleClass().add("viz-algo-name");

        String complexity = COMPLEXITY.getOrDefault(algoName, "O(?)");
        Label complexityLabel = new Label(complexity);
        complexityLabel.getStyleClass().add("viz-complexity");

        HBox header = new HBox(nameLabel, complexityLabel);
        header.setSpacing(8);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        header.getStyleClass().add("viz-panel-header");

        // ── Canvas ──
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        StackPane canvasWrapper = new StackPane(canvas);
        canvasWrapper.setStyle("-fx-padding: 10 10 0 10;");

        // ── Stats bar ──
        compLabel = new Label("Comp:  0");
        interLabel = new Label("Inter: 0");
        progressLabel = new Label("0%");

        compLabel.getStyleClass().add("viz-stat-comp");
        interLabel.getStyleClass().add("viz-stat-inter");
        progressLabel.getStyleClass().add("viz-stat-label");

        compLabel.setMinWidth(110);
        interLabel.setMinWidth(110);
        progressLabel.setMinWidth(40);

        // Spacer pushes progressLabel to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Order: compLabel | interLabel | spacer | progressLabel
        HBox statsBar = new HBox(
                compLabel, interLabel, spacer, progressLabel);
        statsBar.setSpacing(16);
        statsBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        statsBar.getStyleClass().add("viz-stats-bar");

        // ── Array number canvas ──
        arrayCanvas = new Canvas(CANVAS_WIDTH, ARRAY_CV_HEIGHT);
        StackPane arrayWrapper = new StackPane(arrayCanvas);
        arrayWrapper.setStyle("-fx-padding: 4 10 10 10;");

        // ── Root panel ──
        root = new VBox(header, canvasWrapper, statsBar, arrayWrapper);
        root.getStyleClass().add("viz-panel");
        root.setPrefWidth(CANVAS_WIDTH + 40);

        // Draw initial unsorted state
        drawFrame(originalArray, -1, 0, 0, 0);
    }

    // ─────────────────────────────────────────────────────────────
    // drawFrame() — draws one snapshot of the array
    // ─────────────────────────────────────────────────────────────
    public void drawFrame(int[] array, int activeIdx,
            long comparisons, long interchanges,
            int progress) {

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 1. Clear canvas
        gc.setFill(COLOR_BACKGROUND);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        if (array == null || array.length == 0)
            return;

        // 2. Find max ONCE before the loop
        int max = 1;
        for (int v : array)
            if (v > max)
                max = v;

        // 3. Calculate bar dimensions
        double barWidth = (double) CANVAS_WIDTH / array.length;
        double gap = barWidth > 4 ? 1.5 : 0.5;

        // 4. Draw each bar
        for (int i = 0; i < array.length; i++) {
            double barHeight = ((double) array[i] / max) * CANVAS_HEIGHT;
            double x = i * barWidth;
            double y = CANVAS_HEIGHT - barHeight;

            // Color logic:
            // progress 100 → all teal (fully sorted)
            // activeIdx → red (currently being compared)
            // finalArray match → teal (already in sorted position)
            // everything else → grey
            // Color logic
            if (progress == 100) {
                gc.setFill(COLOR_BAR_DONE);
            } else if (i == activeIdx) {
                gc.setFill(COLOR_BAR_ACTIVE);
            } else {
                gc.setFill(COLOR_BAR_IDLE);
            }

            gc.fillRoundRect(x + gap, y, barWidth - gap * 2, barHeight, 3, 3);
        }

        // 5. Play tone AFTER drawing — once per frame, not inside loop
        if (activeIdx >= 0 && activeIdx < array.length && progress < 100) {
            playTone(array[activeIdx], max);
        }

        // 6. Draw array number boxes
        drawArrayBoxes(array, activeIdx, progress);

        // 7. Update labels
        compLabel.setText("Comp:  " + comparisons);
        interLabel.setText("Inter: " + interchanges);
        progressLabel.setText(progress + "%");
    }

    // ─────────────────────────────────────────────────────────────
    // reset() — redraws original unsorted array
    // ─────────────────────────────────────────────────────────────
    public void reset() {
        drawFrame(originalArray, -1, 0, 0, 0);
    }

    // ─────────────────────────────────────────────────────────────
    // drawArrayBoxes() — draws array values as numbered cells
    // ─────────────────────────────────────────────────────────────
    private void drawArrayBoxes(int[] array, int activeIdx, int progress) {
        GraphicsContext gc = arrayCanvas.getGraphicsContext2D();

        // Clear
        gc.setFill(COLOR_BACKGROUND);
        gc.fillRect(0, 0, CANVAS_WIDTH, ARRAY_CV_HEIGHT);

        if (array == null || array.length == 0)
            return;

        double cellW = (double) CANVAS_WIDTH / array.length;
        double cellH = ARRAY_CV_HEIGHT - 8;
        double y = 4;
        double gap = cellW > 6 ? 1.5 : 0.5;

        // Adaptive font size
        double fontSize = Math.min(14, Math.max(7, cellW * 0.55));
        gc.setFont(Font.font("Monospace", fontSize));
        gc.setTextAlign(TextAlignment.CENTER);

        for (int i = 0; i < array.length; i++) {
            double x = i * cellW;

            // Box color
            if (progress == 100) {
                gc.setFill(COLOR_BAR_DONE.deriveColor(0, 1, 1, 0.25));
            } else if (i == activeIdx) {
                gc.setFill(COLOR_BAR_ACTIVE.deriveColor(0, 1, 1, 0.3));
            } else {
                gc.setFill(COLOR_BAR_IDLE.deriveColor(0, 1, 1, 0.5));
            }
            gc.fillRoundRect(x + gap, y, cellW - gap * 2, cellH, 4, 4);

            // Text color
            if (progress == 100) {
                gc.setFill(COLOR_BAR_DONE);
            } else if (i == activeIdx) {
                gc.setFill(COLOR_BAR_ACTIVE);
            } else {
                gc.setFill(Color.web("#b0b3c8"));
            }

            // Draw value — only if cells are wide enough to read
            if (cellW > 10) {
                gc.fillText(String.valueOf(array[i]), x + cellW / 2, y + cellH * 0.68);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    // getRoot() — returns the panel VBox for display
    // ─────────────────────────────────────────────────────────────
    public VBox getRoot() {
        return root;
    }

    // ─────────────────────────────────────────────────────────────
    // playTone() — plays a sine wave tone based on bar value
    // ─────────────────────────────────────────────────────────────
    public void playTone(int value, int maxValue) {
        if (maxValue == 0)
            return;

        double frequency = 150 + ((double) value / maxValue) * 1050;

        new Thread(() -> {
            try {
                float sampleRate = 44100;
                int durationMs = 60;
                int numSamples = (int) (sampleRate * durationMs / 1000);
                byte[] buffer = new byte[numSamples * 2];

                for (int i = 0; i < numSamples; i++) {
                    double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                    double envelope = 1.0;

                    // Fade in first 10%, fade out last 20%
                    if (i < numSamples * 0.1) {
                        envelope = i / (numSamples * 0.1);
                    } else if (i > numSamples * 0.8) {
                        envelope = (numSamples - i) / (numSamples * 0.2);
                    }

                    short sample = (short) (Math.sin(angle) * envelope * 32767 * 0.3);
                    buffer[2 * i] = (byte) (sample & 0xFF);
                    buffer[2 * i + 1] = (byte) ((sample >> 8) & 0xFF);
                }

                javax.sound.sampled.AudioFormat format = new javax.sound.sampled.AudioFormat(sampleRate, 16, 1, true,
                        false);

                javax.sound.sampled.DataLine.Info info = new javax.sound.sampled.DataLine.Info(
                        javax.sound.sampled.SourceDataLine.class, format);

                javax.sound.sampled.SourceDataLine line = (javax.sound.sampled.SourceDataLine) javax.sound.sampled.AudioSystem
                        .getLine(info);

                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
                line.close();

            } catch (Exception e) {
                // Silently ignore audio errors
            }
        }).start();
    }
}