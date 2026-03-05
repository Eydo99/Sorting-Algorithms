package sorting.algorithms;

import sorting.model.SortStep;

import java.util.List;

public interface SortAlgorithm {
    void sort(int[] array);
    String getName();
    long getComparisons();
    long getInterchanges();
    void reset();
    List<SortStep> getSteps();
    void setSteps(boolean collectSteps);
}
