package sorting.model;



public class ComparisonTask {
  private String algorithmName;
  private int arraySize;
  private int noOfRuns;
  private ArrayType arrayType;
  private boolean fromFile;
  private String fileName;

  public ComparisonTask(String algorithmName, int arraySize, ArrayType arrayType,int noOfRuns, boolean fromFile, String fileName) {
    this.algorithmName = algorithmName;
    this.arraySize = arraySize;
    this.noOfRuns = noOfRuns;
    this.arrayType = arrayType;
    this.fileName = fileName;
    this.fromFile = fromFile;
  }
  public String getAlgorithmName() {
    return algorithmName;
  }
  public int getArraySize() {
    return arraySize;
  }
  public int getNoOfRuns() {
    return noOfRuns;
  }
  public ArrayType getArrayType() {
    return arrayType;
  }
  public String getFileName() {
    return fileName;
  }
  public boolean isFromFile() {
    return fromFile;
  }

}
