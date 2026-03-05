package sorting.model;

import java.util.List;

public class ComparisonSummary {
    private String algorithmName;
    private int arraySize;
    private ArrayType arrayType;
    private long comparisons;
    private long interchanges;
   private long avgRuntimeNs;
   private long minRuntimeNs;
   private long maxRuntimeNs;

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public ArrayType getArrayType() {
        return arrayType;
    }

    public void setArrayType(ArrayType arrayType) {
        this.arrayType = arrayType;
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void setComparisons(long comparisons) {
        this.comparisons = comparisons;
    }

    public long getInterchanges() {
        return interchanges;
    }

    public void setInterchanges(long interchanges) {
        this.interchanges = interchanges;
    }

    public long getAvgRuntimeNs() {
        return avgRuntimeNs;
    }

    public void setAvgRuntimeNs(long avgRuntimeNs) {
        this.avgRuntimeNs = avgRuntimeNs;
    }

    public long getMinRuntimeNs() {
        return minRuntimeNs;
    }

    public void setMinRuntimeNs(long minRuntimeNs) {
        this.minRuntimeNs = minRuntimeNs;
    }


    public long getMaxRuntimeNs() {
        return maxRuntimeNs;
    }

    public void setMaxRuntimeNs(long maxRuntimeNs) {
        this.maxRuntimeNs = maxRuntimeNs;
    }

    public static  ComparisonSummary getSummary(List<SortResult> results) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long total = 0;
        ComparisonSummary summary=new ComparisonSummary();
        for(SortResult result: results) {
            if(result.getRuntimeNs() < min) {
                min = result.getRuntimeNs();
            }
            if(result.getRuntimeNs() > max) {
                max = result.getRuntimeNs();
            }
            total += result.getRuntimeNs();
        }
        long avg = total / results.size();
        summary.setAvgRuntimeNs(avg);
        summary.setMinRuntimeNs(min);
        summary.setMaxRuntimeNs(max);
        summary.setAlgorithmName(results.getFirst().getAlgorithmName());
        summary.setArraySize(results.getFirst().getArraySize());
        summary.setArrayType(results.getFirst().getArrayType());
        summary.setComparisons(results.getFirst().getComparisons());
        summary.setInterchanges(results.getFirst().getInterchanges());
        return summary;
    }

}
