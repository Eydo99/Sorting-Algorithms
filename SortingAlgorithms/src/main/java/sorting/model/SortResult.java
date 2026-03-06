package sorting.model;

public class SortResult {
    String algorithmName;
    int arraySize;
    long comparisons;
    long interchanges;
    long runtimeNs;
    String arrayType;
    int runNumber;


    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
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

    public long getRuntimeNs() {
        return runtimeNs;
    }

    public void setRuntimeNs(long runtimeNs) {
        this.runtimeNs = runtimeNs;
    }

    public long getInterchanges() {
        return interchanges;
    }

    public void setInterchanges(long interchanges) {
        this.interchanges = interchanges;
    }

    public String getArrayType() {
        return arrayType;
    }

    public void setArrayType(String arrayType) {
        this.arrayType = arrayType;
    }


    public int getRunNumber() {
        return runNumber;
    }

    public void setRunNumber(int runNumber) {
        this.runNumber = runNumber;
    }


}
