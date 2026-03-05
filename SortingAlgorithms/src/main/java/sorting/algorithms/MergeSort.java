package sorting.algorithms;

public class MergeSort extends AbstractSort {

    public MergeSort() {
        this.name = "MergeSort";
    }

    @Override
    public void sort(int[] array) {
        reset();
        mergeSort(array);
    }

    private void mergeSort(int[] array) {
        int n = array.length;
        if (n < 2)
            return;

        int mid = n / 2;
        int[] left = new int[mid];
        int[] right = new int[n - mid];
        int l = 0, r = 0;
        for (; l < n; l++) {
            if (l < mid)
                left[l] = array[l];
            else {
                right[r] = array[l];
                r++;
            }
        }

        mergeSort(left);
        mergeSort(right);
        merge(array, left, right);
    }

    private void merge(int[] array, int[] left, int[] right) {
        int leftSize = left.length;
        int rightSize = right.length;
        int l = 0, r = 0, i = 0;

        while (l < leftSize && r < rightSize) {
            if (left[l] < right[r]) {
                array[i] = left[l];
                l++;
                i++;
                interchanges++;
            } else {
                array[i] = right[r];
                r++;
                i++;
                interchanges++;
            }
            addStep(array.clone(), i - 1);
            comparisons++;
        }
        while (l < leftSize) {
            array[i] = left[l];
            l++;
            i++;
            interchanges++;
            addStep(array.clone(), i - 1);
        }

        while (rightSize > r) {
            array[i] = right[r];
            r++;
            i++;
            interchanges++;
            addStep(array.clone(), i - 1);
        }
    }

}
