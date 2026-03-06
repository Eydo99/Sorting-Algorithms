package sorting.visualization;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import sorting.model.SortStep;

import java.util.List;

public class SortAnimator {


    private final List<SortStep> sortSteps;
    private final long totalComparisons;
    private final long totalInterchanges;
    private final BarChartPane barChartPane;


    private int currentStep=0;
    private Timeline timeline;

    public SortAnimator(List<SortStep> sortSteps,long totalComparisons,long totalInterchanges,BarChartPane barChartPane) {
        this.totalComparisons=totalComparisons;
        this.totalInterchanges=totalInterchanges;
        this.sortSteps=sortSteps;
        this.barChartPane=barChartPane;
    }

    public void startAnimation(double speed) {
        if (!hasMoreSteps()) return;
        if(timeline!=null) timeline.stop();

        double delay=300.0/speed;

        timeline=new Timeline(new KeyFrame(Duration.millis(delay), event -> {
            if(hasMoreSteps())
            {
                drawCurrentStep();
                currentStep++;
            }
            else
            {
                timeline.stop();
                drawFinalState();
            }
                }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void pauseAnimation() {
        if(timeline!=null) timeline.pause();
    }


    public void step()
    {
        if(!hasMoreSteps()) return;
        drawCurrentStep();
        currentStep++;
    }



    public void reset()
    {
        if(timeline!=null) timeline.stop();
        currentStep=0;
        barChartPane.reset();
    }


    public boolean hasMoreSteps() { return currentStep < sortSteps.size(); }

    private void  drawCurrentStep()
    {
        SortStep sortStep=sortSteps.get(currentStep);
        int progress= (currentStep/sortSteps.size()*100);
        long comparisons=(totalComparisons*currentStep)/sortSteps.size();
        long interchanges=(totalInterchanges*currentStep)/sortSteps.size();

        barChartPane.drawFrame(sortStep.array,sortStep.activeIndex,comparisons,interchanges,progress);
    }

    private void drawFinalState() {
        SortStep lastStep = sortSteps.getLast();
        barChartPane.drawFrame(lastStep.array, -1, totalComparisons, totalInterchanges, 100);
    }
}