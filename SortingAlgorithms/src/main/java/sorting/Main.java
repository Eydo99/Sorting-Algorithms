package sorting;

import sorting.algorithms.*;

public class Main {


    public static void main(String[] args) {

        QuickSort sorter = new QuickSort();

        // Test 1 - Random array
        int[] arr1 = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        sorter.sort(arr1);
        System.out.println("Test 1 - Random:");
        printArray(arr1);
        printStats(sorter);

        // Test 2 - Already sorted
        sorter.reset();
        int[] arr2 = {1, 2, 3, 4, 5};
        sorter.sort(arr2);
        System.out.println("Test 2 - Already Sorted:");
        printArray(arr2);
        printStats(sorter);

        // Test 3 - Inversely sorted (worst case for bubble)
        sorter.reset();
        int[] arr3 = {5, 4, 3, 2, 1};
        sorter.sort(arr3);
        System.out.println("Test 3 - Inversely Sorted:");
        printArray(arr3);
        printStats(sorter);

        // Test 4 - Single element
        sorter.reset();
        int[] arr4 = {42};
        sorter.sort(arr4);
        System.out.println("Test 4 - Single Element:");
        printArray(arr4);
        printStats(sorter);

        // Test 5 - verify steps are captured
        sorter.reset();
        sorter.setSteps(true);
        int[] arr5 = {3, 1, 2};
        sorter.sort(arr5);
        System.out.println("Test 5 - Steps captured: "
                + sorter.getSteps().size() + " steps");
    }

    static void printArray(int[] arr) {
        System.out.print("Result: ");
        for (int x : arr) System.out.print(x + " ");
        System.out.println();
    }

    static void printStats(SortAlgorithm sorter) {
        System.out.println("Comparisons:  " + sorter.getComparisons());
        System.out.println("Interchanges: " + sorter.getInterchanges());
        System.out.println("---");
    }
}
