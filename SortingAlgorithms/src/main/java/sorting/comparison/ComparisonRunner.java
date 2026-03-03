package sorting.comparison;

import sorting.algorithms.SortAlgorithm;
import sorting.algorithms.SortAlgorithmFactory;
import sorting.input.ArrayGenerator;
import sorting.model.ArrayType;
import sorting.model.ComparisonSummary;
import sorting.model.ComparisonTask;
import sorting.model.SortResult;

import java.util.ArrayList;
import java.util.List;

public class ComparisonRunner {
    private final ComparisonTask task;
    public ComparisonRunner(ComparisonTask task) {
        this.task = task;
    }

    public List<SortResult> run() {
        SortAlgorithm algorithm = SortAlgorithmFactory.getAlgorithm(task.getAlgorithmName());
        int[] original= (task.isFromFile()) ? ArrayGenerator.generateFromFile(task.getFileName()) :
                ArrayGenerator.generateFromRandom(task.getArraySize(), task.getArrayType());
        List<SortResult> results = new ArrayList<>();
        for(int i = 0; i < task.getNoOfRuns(); i++) {
            algorithm.reset();
            long start =System.nanoTime();
            algorithm.sort(original.clone());
            long end =System.nanoTime();
            long duration = end - start;
            SortResult result = mapAlgorithmToResult(algorithm,duration,i,original.length);
            results.add(result);
        }
        return results;
    }

    private SortResult mapAlgorithmToResult(SortAlgorithm algorithm,long duration,int index,int arraySize) {
        SortResult result=new SortResult();
        result.setRuntimeNs(duration);
        result.setComparisons(algorithm.getComparisons());
        result.setAlgorithmName(algorithm.getName());
        result.setArraySize(arraySize);
        result.setInterchanges(algorithm.getInterchanges());
        result.setArrayType(task.getArrayType());
        result.setRunNumber(index+1);
        return result;
    }

//    public static void main(String[] args) {
//
//        // Test 1 - BubbleSort, Random, 5 runs
//        ComparisonTask task1 = new ComparisonTask("BubbleSort", 10, ArrayType.RANDOM, 5, false, null);
//        ComparisonRunner runner1 = new ComparisonRunner(task1);
//        List<SortResult> results1 = runner1.run();
//
//        System.out.println("=== BubbleSort - Random - 10 elements ===");
//        for(SortResult r : results1) {
//            System.out.println("Run " + r.getRunNumber() +
//                    " | Time: " + r.getRuntimeNs() + "ns" +
//                    " | Comparisons: " + r.getComparisons() +
//                    " | Interchanges: " + r.getInterchanges());
//        }
//
//        // Test 2 - MergeSort, Sorted
//        ComparisonTask task2 = new ComparisonTask("MergeSort", 10, ArrayType.SORTED, 3, false, null);
//        ComparisonRunner runner2 = new ComparisonRunner(task2);
//        List<SortResult> results2 = runner2.run();
//
//        System.out.println("\n=== MergeSort - Sorted - 10 elements ===");
//        for(SortResult r : results2) {
//            System.out.println("Run " + r.getRunNumber() +
//                    " | Time: " + r.getRuntimeNs() + "ns" +
//                    " | Comparisons: " + r.getComparisons() +
//                    " | Interchanges: " + r.getInterchanges());
//        }
//
//        // Test 3 - SelectionSort, Inversely Sorted
//        ComparisonTask task3 = new ComparisonTask("SelectionSort", 10, ArrayType.INVERSELY_SORTED, 3, false, null);
//        ComparisonRunner runner3 = new ComparisonRunner(task3);
//        List<SortResult> results3 = runner3.run();
//
//        System.out.println("\n=== SelectionSort - Inversely Sorted - 10 elements ===");
//        for(SortResult r : results3) {
//            System.out.println("Run " + r.getRunNumber() +
//                    " | Time: " + r.getRuntimeNs() + "ns" +
//                    " | Comparisons: " + r.getComparisons() +
//                    " | Interchanges: " + r.getInterchanges());
//        }
//    }


}
