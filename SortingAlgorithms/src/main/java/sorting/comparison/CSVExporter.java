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

    public static void export(List<ComparisonSummary> summaries,String filePath)
    {

        new File("data/output").mkdirs();

        try (PrintWriter writer=new PrintWriter(new FileWriter(filePath),true)) {

            writer.println("Algorithm,ArraySize,ArrayType,NoOfRuns,AvgRuntimeNs,MinRuntimeNs,MaxRuntimeNs,Comparisons,Interchanges");

            for (ComparisonSummary s : summaries) {
                writer.println(
                                s.getAlgorithmName() + "," +
                                s.getArraySize() + "," +
                                s.getArrayType() + "," +
                                s.getRuns() + "," +
                                s.getAvgRuntimeNs() + "," +
                                s.getMinRuntimeNs() + "," +
                                s.getMaxRuntimeNs() + "," +
                                 s.getComparisons()+","+
                                s.getInterchanges()
                );
            }

        } catch(IOException e) {
            System.out.println("Error writing CSV: " + e.getMessage());
        }
    }

}