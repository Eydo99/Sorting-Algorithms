package sorting.visualization;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.util.Map;

public class BarChartPane {



    private static final Color background_color=Color.web("#13141a");
    private static final Color active_bar_color=Color.web("#ff5c6a");
    private static final Color idle_bar_color=Color.web("#2e3148");
    private static final Color done_bar_color=Color.web("#00d4aa");


    private static final int canvas_width= 500;
    private static final int canvas_height= 300;
    private static final int array_canvas_height= 40;

    private final VBox root;
    private final Canvas bars_canvas;
    private final Canvas array_canvas;
    private final Label comparison_label;
    private final Label interchanges_label;
    private final Label progress_label;


    private final int[] originalArray;

    private static final Map<String,String> complexity_map=Map.of(
            "BubbleSort","O(n^2)",
            "InsertionSort","O(n^2)",
            "SelectionSort","O(n^2)",
            "HeapSort","O(n logn)",
            "MergeSort","O(n logn)",
            "QuickSort","O(n logn)"
    );



    public BarChartPane(String algoName, int[] originalArray) {
        this.originalArray = originalArray.clone();

        // ── Header ──
        Label nameLabel=new Label(algoName);
        nameLabel.getStyleClass().add("viz-algo-name");

        String complexity=complexity_map.get(algoName);
        Label complexityLabel=new Label(complexity);
        complexityLabel.getStyleClass().add("viz-complexity");

        HBox header=new HBox(nameLabel,complexityLabel);
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("viz-panel-header");

        // ── Canvas ──
        bars_canvas=new Canvas(canvas_width, canvas_height);
        StackPane canvasWrapper=new StackPane(bars_canvas);
        canvasWrapper.setStyle("-fx-padding: 10 10 0 10;");

        // ── Stats bar ──
        comparison_label=new Label("Comp:  0");
        interchanges_label=new Label("Inter: 0");
        progress_label=new Label("0%");

        comparison_label.getStyleClass().add("viz-stat-comp");
        interchanges_label.getStyleClass().add("viz-stat-inter");
        progress_label.getStyleClass().add("viz-stat-label");

        comparison_label.setMinWidth(110);
        interchanges_label.setMinWidth(110);
        progress_label.setMinWidth(40);

        // Spacer pushes progressLabel to the right
        Region spacer=new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Order: compLabel | interLabel | spacer | progressLabel
        HBox statsBar = new HBox(comparison_label,interchanges_label,spacer,progress_label);
        statsBar.setSpacing(25);
        statsBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        statsBar.getStyleClass().add("viz-stats-bar");

        // ── Array number canvas ──
        array_canvas=new Canvas(canvas_width,canvas_height);
        StackPane arrayWrapper=new StackPane(array_canvas);
        arrayWrapper.setStyle("-fx-padding: 4 10 10 10;");

        // ── Root panel ──
        root=new VBox(header, canvasWrapper, statsBar, arrayWrapper);
        root.getStyleClass().add("viz-panel");
        root.setPrefWidth(canvas_width + 40);

        // Draw initial unsorted state
        drawFrame(originalArray, -1, 0, 0, 0);
    }


    public void drawFrame(int[] array, int activeIdx, long comparisons, long interchanges, int progress) {

        GraphicsContext gc=bars_canvas.getGraphicsContext2D();


        gc.setFill(background_color);
        gc.fillRect(0, 0,canvas_width,canvas_height);

        if (array == null || array.length == 0)
            return;


        int max=array[0];
        int min=array[0];
        for (int v : array)
        {
            if (v > max) max = v;
            if (v < min) min = v;
        }
        int shift = (min<0)? -min : 0;
        int range=max-min;
        if(range ==0) range=1;


        double barWidth = (double) canvas_width / array.length;
        double gap = barWidth > 4 ? 1.5 : 0.5;

        for (int i = 0; i < array.length; i++) {
            double barHeight=((double) (array[i]+shift) / range) * canvas_height;
            double x=i * barWidth;
            double y=canvas_height - barHeight;


            if (progress == 100) {
                gc.setFill(done_bar_color);
            } else if (i == activeIdx) {
                gc.setFill(active_bar_color);
            } else {
                gc.setFill(idle_bar_color);
            }

            gc.fillRoundRect(x + gap, y, barWidth - gap * 2, barHeight, 3, 3);
        }


        if (activeIdx >= 0 && activeIdx < array.length && progress < 100) {
            playTone(array[activeIdx], max);
        }


        drawArrayBoxes(array,activeIdx,progress);


        comparison_label.setText("Comp:  "+comparisons);
        interchanges_label.setText("Inter: "+interchanges);
        progress_label.setText(progress+"%");
    }

    public void reset() {
        drawFrame(originalArray, -1, 0, 0, 0);
    }


    private void drawArrayBoxes(int[] array, int activeIdx, int progress) {
        GraphicsContext gc = array_canvas.getGraphicsContext2D();


        gc.setFill(background_color);
        gc.fillRect(0, 0, canvas_width, array_canvas_height);

        if (array == null || array.length == 0)
            return;

        double cell_width=(double) canvas_width / array.length;
        double cellHeight=array_canvas_height - 8;
        double y = 4;
        double gap=cell_width > 6 ? 1.5 : 0.5;

        // Adaptive font size
        double fontSize = Math.min(14, Math.max(7, cell_width * 0.55));
        gc.setFont(Font.font("Monospace", fontSize));
        gc.setTextAlign(TextAlignment.CENTER);

        for (int i = 0; i < array.length; i++) {
            double x = i * cell_width;

            if (progress == 100) {
                gc.setFill(done_bar_color.deriveColor(0, 1, 1, 0.25));
            } else if (i == activeIdx) {
                gc.setFill(active_bar_color.deriveColor(0, 1, 1, 0.3));
            } else {
                gc.setFill(idle_bar_color.deriveColor(0, 1, 1, 0.5));
            }
            gc.fillRoundRect(x + gap, y, cell_width - gap * 2, cellHeight, 4, 4);

            if (progress == 100) {
                gc.setFill(done_bar_color);
            } else if (i == activeIdx) {
                gc.setFill(active_bar_color);
            } else {
                gc.setFill(Color.web("#b0b3c8"));
            }

            // Draw value — only if cells are wide enough to read
            if (cell_width > 10) {
                gc.fillText(String.valueOf(array[i]), x + cell_width / 2, y + cellHeight * 0.68);
            }
        }
    }

    public VBox getRoot() {
        return root;
    }


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

                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,
                        false);

                DataLine.Info info = new DataLine.Info(
                        javax.sound.sampled.SourceDataLine.class, format);

                SourceDataLine line = (SourceDataLine) AudioSystem
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