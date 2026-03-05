package sorting.algorithms;

public class HeapSort extends AbstractSort {

    int heapSize;

    public HeapSort() {
        this.name = "HeapSort";
        this.heapSize = 0;
    }

    @Override
    public void sort(int[] array) {
        reset();
        heapSort(array);
    }

    private void max_heapify(int[] array, int i) {
        addStep(array.clone(), i);
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        int largest = i;
        if (left < heapSize) {
            comparisons++;
            if (array[left] > array[largest])
                largest = left;
        }

        if (right < heapSize) {
            comparisons++;
            if (array[right] > array[largest])
                largest = right;
        }
        if (largest != i) {
            swap(array, i, largest);
            interchanges++;
            addStep(array.clone(), i);
            max_heapify(array, largest);
        }
    }

    private void build_max_heap(int[] array) {
        heapSize = array.length;
        for (int i = (heapSize / 2) - 1; i >= 0; i--)
            max_heapify(array, i);

    }

    private void heapSort(int[] array) {
        build_max_heap(array);

        for (int i = heapSize - 1; i > 0; i--) {
            swap(array, 0, i);
            interchanges++;
            addStep(array.clone(), i);
            heapSize--;
            max_heapify(array, 0);
        }

    }
}
