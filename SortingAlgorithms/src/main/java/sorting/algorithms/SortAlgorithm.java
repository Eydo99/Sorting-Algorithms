package sorting.algorithms;

import java.util.List;

public interface SortAlgorithm {
    void sort(int[] array);
    String getName();
    long getComparisons();
    long getInterchanges();
    void reset();
    List<int[]> getSteps();
}
