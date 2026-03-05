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

    public List<List<SortResult>> runAll() {
        List<Future<List<SortResult>>> futures = new ArrayList<>();
        executor = Executors.newFixedThreadPool(Math.min(tasks.size(), Runtime.getRuntime().availableProcessors()));

        for (ComparisonTask task : tasks) {
            Future<List<SortResult>> future = executor.submit(() -> {
                ComparisonRunner runner = new ComparisonRunner(task);
                return runner.run();
            });
            futures.add(future);
        }
        List<List<SortResult>> results = new ArrayList<>();
        for (Future<List<SortResult>> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                System.out.println(e.getMessage());
            }
        }
        tasks.clear();
        return results;
    }

    public void shutdown() {
        executor.shutdown();
    }

}
