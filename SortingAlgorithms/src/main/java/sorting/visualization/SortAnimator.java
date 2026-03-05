package sorting.visualization;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import sorting.model.SortStep;

import java.util.List;

public class SortAnimator {

    // ── Data ─────────────────────────────────────────────────────
    private final List<SortStep> steps;
    private final long           totalComparisons;
    private final long           totalInterchanges;
    private final BarChartPane   chartPane;

    // ── Animation state ──────────────────────────────────────────
    private int      currentStep = 0;
    private Timeline timeline;

    // ─────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────
    public SortAnimator(List<SortStep> steps,
                        long totalComparisons,
                        long totalInterchanges,
                        BarChartPane chartPane) {
        this.steps             = steps;
        this.totalComparisons  = totalComparisons;
        this.totalInterchanges = totalInterchanges;
        this.chartPane         = chartPane;
    }

    // ─────────────────────────────────────────────────────────────
    // play() — starts the Timeline animation
    // ─────────────────────────────────────────────────────────────
    public void play(double speed) {
        if (!hasMoreSteps()) return;
        if (timeline != null) timeline.stop();

        double delayMs = 300.0 / speed;

        timeline = new Timeline(new KeyFrame(Duration.millis(delayMs), e -> {
            if (hasMoreSteps()) {
                drawCurrentStep();
                currentStep++;
            } else {
                timeline.stop();
                drawFinalState();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // ─────────────────────────────────────────────────────────────
    // pause() — freezes the animation at current step
    // ─────────────────────────────────────────────────────────────
    public void pause() {
        if (timeline != null) timeline.pause();
    }

    // ─────────────────────────────────────────────────────────────
    // step() — manually advances exactly one step
    // ─────────────────────────────────────────────────────────────
    public void step() {
        if (!hasMoreSteps()) return;
        drawCurrentStep();
        currentStep++;
    }

    // ─────────────────────────────────────────────────────────────
    // reset() — goes back to step 0
    // ─────────────────────────────────────────────────────────────
    public void reset() {
        if (timeline != null) timeline.stop();
        currentStep = 0;
        chartPane.reset();
    }

    // ─────────────────────────────────────────────────────────────
    // hasMoreSteps() — true if animation is not finished
    // ─────────────────────────────────────────────────────────────
    public boolean hasMoreSteps() {
        return currentStep < steps.size();
    }

    // ─────────────────────────────────────────────────────────────
    // drawCurrentStep() — draws the current step on the canvas
    // ─────────────────────────────────────────────────────────────
    private void drawCurrentStep() {
        SortStep step = steps.get(currentStep);

        int  progress     = (int) ((currentStep / (double) steps.size()) * 100);
        long comparisons  = (long) (totalComparisons  * currentStep / (double) steps.size());
        long interchanges = (long) (totalInterchanges * currentStep / (double) steps.size());

        chartPane.drawFrame(
                step.array,
                step.activeIndex,
                comparisons,
                interchanges,
                progress
        );
    }

    // ─────────────────────────────────────────────────────────────
    // drawFinalState() — draws fully sorted array all teal
    // ─────────────────────────────────────────────────────────────
    private void drawFinalState() {
        SortStep lastStep = steps.get(steps.size() - 1);
        chartPane.drawFrame(
                lastStep.array,
                -1,
                totalComparisons,
                totalInterchanges,
                100
        );
    }
}