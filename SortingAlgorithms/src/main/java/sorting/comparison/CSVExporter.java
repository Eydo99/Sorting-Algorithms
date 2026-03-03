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

    public static void export(List<SortResult> results, ComparisonSummary summary, String filePath) {

        new File("data/output").mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {

            // write header only if file is new/empty
            writer.println("Algorithm      ArraySize       ArrayType      RunNumber" +
                    "    RuntimeNs      Comparisons       Interchanges");

            // write one row per result
            for(SortResult r : results) {
                writer.println(
                        r.getAlgorithmName() + "         " +
                                r.getArraySize() + "            " +
                                r.getArrayType() + "           " +
                                r.getRunNumber() + "          " +
                                r.getRuntimeNs() + "            " +
                                r.getComparisons() + "               " +
                                r.getInterchanges()
                );
            }

            // write summary row
            writer.println(
                            "SUMMARY: "+
                                    "1-AvgRunTime: "+
                            summary.getAvgRuntimeNs() + "   " +
                                    "2-MaxRunTime: "+
                            summary.getMaxRuntimeNs() + "   " +
                                    "3-MinRunTime: "+
                            summary.getMinRuntimeNs()
            );

        } catch(IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }

    public static   void exportAll(List<List<SortResult>> allResults, String filePath) {
        for(List<SortResult> results : allResults) {
            ComparisonSummary summary = ComparisonSummary.getSummary(results);
            export(results, summary, filePath);
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