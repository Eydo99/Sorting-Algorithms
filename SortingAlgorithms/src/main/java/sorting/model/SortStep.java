package sorting.model;

public class SortStep {
    public int[] array;
    public int activeIndex;
    public SortStep(int[] array, int activeIndex) {
        this.array = array.clone();
        this.activeIndex = activeIndex;
    }
}
