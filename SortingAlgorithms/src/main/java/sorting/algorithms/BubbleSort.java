package sorting.algorithms;

public class BubbleSort extends AbstractSort {

    public BubbleSort() {
        this.name = "BubbleSort";
    }

    @Override
    public void sort(int[] array) {
        reset();
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                addStep(array.clone(), j);
                if (array[j] > array[j + 1]) {
                    swap(array, j, j + 1);
                    interchanges++;
                    addStep(array.clone(), j);
                }
                comparisons++;
            }
        }
    }

}
