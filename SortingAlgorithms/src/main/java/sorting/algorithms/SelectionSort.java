package sorting.algorithms;

public class SelectionSort  extends AbstractSort {

    public SelectionSort() {
        this.name = "SelectionSort";
    }
    @Override
    public void sort(int[] array) {
        reset();
        for(int i = 0; i < array.length - 1; i++) {
            int min = i;
            for(int j = i + 1; j < array.length; j++) {
                if(array[j]<array[min]) {
                    min = j;
                }
                comparisons++;
            }
            if(i!=min)
            {
                swap(array, i, min);
                interchanges++;
                addStep(array.clone());
            }
            else addStep(array.clone());
        }
    }


}
