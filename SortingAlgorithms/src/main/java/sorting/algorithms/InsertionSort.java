package sorting.algorithms;

public class InsertionSort extends AbstractSort {

    public InsertionSort() {
        this.name = "InsertionSort";
    }

    @Override
    public void sort(int[] array) {
        reset();
        for (int i = 1; i < array.length; i++) {
            int j = i - 1;
            int temp = array[i];
            while (j >= 0) {
                addStep(array.clone(), j);
                comparisons++;
                if (array[j] > temp) {
                    array[j + 1] = array[j];
                    interchanges++;
                    addStep(array.clone(), j + 1);
                    j--;
                } else
                    break;
            }
            array[j + 1] = temp;
            if (j != i - 1) {
                interchanges++;
                addStep(array.clone(), j + 1);
            }
        }
    }

}
