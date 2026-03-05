package sorting.comparison;

import sorting.model.ArrayType;
import sorting.model.ComparisonSummary;
import sorting.model.ComparisonTask;
import sorting.model.SortResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSVExporter {

    private CSVExporter() {}

    public static void export(List<ComparisonSummary> summaries, String filePath) {

        new File("data/output").mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            // Header
            writer.println("Algorithm,ArraySize,ArrayType,AvgRuntimeNs,MinRuntimeNs,MaxRuntimeNs");

            // write one row per summary
            for (ComparisonSummary s : summaries) {

                writer.println(
                        s.getAlgorithmName() + "," +
                                s.getArraySize() + "," +
                                s.getArrayType() + "," +
                                s.getAvgRuntimeNs() + "," +
                                s.getMinRuntimeNs() + "," +
                                s.getMaxRuntimeNs()
                );
            }

        } catch(IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }


//    public static void main(String[] args) {
//
//        // Run comparison
//        ComparisonTask task = new ComparisonTask("BubbleSort", 10, ArrayType.RANDOM, 5, false, null);
//        ComparisonRunner runner = new ComparisonRunner(task);
//        List<SortResult> results = runner.run();
//        ComparisonSummary summary = runner.getSummary(results);
//
//        // Print to console
//        System.out.println("=== Individual Runs ===");
//        for(SortResult r : results) {
//            System.out.println("Run " + r.getRunNumber() +
//                    " | Time: " + r.getRuntimeNs() + "ns" +
//                    " | Comparisons: " + r.getComparisons() +
//                    " | Interchanges: " + r.getInterchanges());
//        }
//
//        System.out.println("\n=== Summary ===");
//        System.out.println("Avg: " + summary.getAvgRuntimeNs() + "ns");
//        System.out.println("Min: " + summary.getMinRuntimeNs() + "ns");
//        System.out.println("Max: " + summary.getMaxRuntimeNs() + "ns");
//
//        // Export to CSV
//        CSVExporter.export(results, summary, "data/output/results.csv");
//        System.out.println("\nCSV exported successfully");
//    }

//    public static void main(String[] args) {
//
//        ParallelComparisonManager manager = new ParallelComparisonManager();
//
//        manager.addTask(new ComparisonTask("BubbleSort",   100, ArrayType.RANDOM, 3, false, null));
//        manager.addTask(new ComparisonTask("MergeSort",    100, ArrayType.RANDOM, 3, false, null));
//        manager.addTask(new ComparisonTask("SelectionSort",100, ArrayType.SORTED, 3, false, null));
//
//        List<List<SortResult>> allResults = manager.runAll();
//        manager.shutdown();
//
//        CSVExporter.exportAll(allResults, "data/output/results.csv");
//        System.out.println("CSV exported successfully");
//    }
}