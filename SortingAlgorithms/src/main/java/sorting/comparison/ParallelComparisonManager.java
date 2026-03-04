package sorting.comparison;

import sorting.model.ArrayType;
import sorting.model.ComparisonTask;
import sorting.model.SortResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelComparisonManager {
    private final List<ComparisonTask> tasks = new ArrayList<>();
    private ExecutorService executor;

    public void addTask(ComparisonTask task) {
        tasks.add(task);
    }


    public List<List<SortResult>> runAll()  {
        List<Future<List<SortResult>>> futures = new ArrayList<>();
        executor = Executors.newFixedThreadPool(tasks.size());

        for (ComparisonTask task : tasks) {
            Future<List<SortResult>> future= executor.submit(()->
            {ComparisonRunner runner = new ComparisonRunner(task);
               return  runner.run();
            });
            futures.add(future);
        }
        List<List<SortResult>> results = new ArrayList<>();
        for (Future<List<SortResult>> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());;
            }
        }
        return results;
    }
    public  void shutdown() {
         executor.shutdown();
    }

    public static void main(String[] args) {

        ParallelComparisonManager manager = new ParallelComparisonManager();

        // Add 6 different algorithm tasks
        manager.addTask(new ComparisonTask("BubbleSort",  1000, ArrayType.RANDOM, 3, false, null));
        manager.addTask(new ComparisonTask("SelectionSort", 1000, ArrayType.RANDOM, 3, false, null));
        manager.addTask(new ComparisonTask("InsertionSort", 1000, ArrayType.RANDOM, 3, false, null));
        manager.addTask(new ComparisonTask("MergeSort",   1000, ArrayType.RANDOM, 3, false, null));
        manager.addTask(new ComparisonTask("HeapSort",    1000, ArrayType.RANDOM, 3, false, null));
        manager.addTask(new ComparisonTask("QuickSort",   1000, ArrayType.RANDOM, 3, false, null));

        // Run all in parallel
        List<List<SortResult>> allResults = manager.runAll();
        manager.shutdown();

        // Print results
        for(List<SortResult> results : allResults) {
            System.out.println("=== " + results.get(0).getAlgorithmName() + " ===");
            for(SortResult r : results) {
                System.out.println("Run " + r.getRunNumber() +
                        " | Time: " + r.getRuntimeNs() + "ns" +
                        " | Comparisons: " + r.getComparisons() +
                        " | Interchanges: " + r.getInterchanges());
            }
            System.out.println("---");
        }
    }

}
