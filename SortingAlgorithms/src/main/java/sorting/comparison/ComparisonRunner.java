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
            if(task.getArrayType().equals(ArrayType.RANDOM) && !task.isFromFile())
            {
                original=ArrayGenerator.generateFromRandom(task.getArraySize(), task.getArrayType());
            }
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
        result.setRunNumber(index+1);
        String arrayType = task.isFromFile()
                ? task.getFileName()  // "eyad.txt"
                : task.getArrayType().toString();  // "RANDOM"

        result.setArrayType(arrayType);
        return result;
    }



}
