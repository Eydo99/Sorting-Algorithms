package sorting.algorithms;


public  class SortAlgorithmFactory {

    private SortAlgorithmFactory() {}

    public static SortAlgorithm getAlgorithm(String algorithmName) {
        return switch (algorithmName) {
            case "BubbleSort" -> new BubbleSort();
            case "InsertionSort" -> new InsertionSort();
            case "SelectionSort" -> new SelectionSort();
            case "MergeSort" -> new MergeSort();
            case "QuickSort" -> new QuickSort();
            case "HeapSort" -> new HeapSort();
            default -> null;
        };
    }
}
