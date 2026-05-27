# SortLab — Sorting Algorithm Analyzer

A desktop application for benchmarking and visualizing classic sorting algorithms. Built with JavaFX for the interactive GUI and Python/matplotlib for post-run statistical analysis.

---

## Features

### Comparison Mode
- Run any combination of 6 sorting algorithms against the same input
- Choose array size, array type (Random / Sorted / Inversely Sorted), and number of runs
- Run sequentially or **in parallel** (thread-pool based, one thread per task)
- Live results table showing min/avg/max runtime (ns), comparisons, and interchanges
- Pending tasks queue so you can track what's still running
- Export results to CSV for external analysis

### Visualization Mode
- Side-by-side animated bar charts for up to 6 algorithms simultaneously
- Adjustable playback speed (0.5× – 4×)
- Step-by-step mode for detailed inspection
- Live counters for comparisons and interchanges as the animation plays
- Audio feedback — pitch corresponds to the value being accessed
- Load arrays from file or generate random/sorted/inversely-sorted arrays (max 100 elements)

### Python Analysis Script
- Reads the exported CSV and renders a 4-panel dark-theme dashboard:
  - Average runtime (log scale bar chart)
  - Min / Avg / Max runtime comparison
  - Comparisons vs Interchanges grouped bar chart
  - Runtime vs Array Size scaling curve (log scale)

---

## Algorithms

| Algorithm | Time Complexity | Notes |
|---|---|---|
| Bubble Sort | O(n²) | Classic comparison sort |
| Insertion Sort | O(n²) | Efficient for nearly sorted data |
| Selection Sort | O(n²) | Minimizes swaps |
| Merge Sort | O(n log n) | Stable, divide-and-conquer |
| Quick Sort | O(n log n) | Randomized pivot partition |
| Heap Sort | O(n log n) | In-place, uses max-heap |

---

## Tech Stack

| Component | Technology |
|---|---|
| GUI | JavaFX 21 (FXML + CSS) |
| Build | Maven |
| Java Version | Java 21 |
| Analysis script | Python 3, pandas, matplotlib, numpy |

---

## Project Structure

```
SortingAlgorithms/
├── pom.xml
├── analyze_sorts.py                 # Python analysis dashboard
├── data/
│   ├── input/
│   │   └── input.txt                # Example input file
│   └── output/
│       └── results.csv              # Sample exported results
└── src/main/
    ├── java/sorting/
    │   ├── Main.java                # JavaFX entry point
    │   ├── algorithms/
    │   │   ├── SortAlgorithm.java   # Interface
    │   │   ├── AbstractSort.java    # Base class (comparisons, interchanges, steps)
    │   │   ├── BubbleSort.java
    │   │   ├── InsertionSort.java
    │   │   ├── SelectionSort.java
    │   │   ├── MergeSort.java
    │   │   ├── QuickSort.java
    │   │   ├── HeapSort.java
    │   │   └── SortAlgorithmFactory.java
    │   ├── comparison/
    │   │   ├── ComparisonRunner.java        # Single-task runner
    │   │   ├── ParallelComparisonManager.java  # Thread-pool orchestrator
    │   │   └── CSVExporter.java
    │   ├── input/
    │   │   └── ArrayGenerator.java  # Random / sorted / file-based arrays
    │   ├── model/
    │   │   ├── ArrayType.java       # RANDOM | SORTED | INVERSELY_SORTED
    │   │   ├── ComparisonTask.java
    │   │   ├── ComparisonSummary.java
    │   │   ├── SortResult.java
    │   │   └── SortStep.java        # Single animation frame
    │   ├── ui/
    │   │   ├── ComparisonController.java
    │   │   └── VisualizationController.java
    │   └── visualization/
    │       ├── BarChartPane.java    # Canvas-based bar chart panel
    │       ├── SortAnimator.java    # Timeline-driven step player
    │       └── Audio.java           # Tone synthesis (pitch = array value)
    └── resources/
        ├── css/style.css            # Carbon dark theme
        └── fxml/
            ├── Main.fxml
            ├── Comparison.fxml
            └── Visualization.fxml
```

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Python 3.9+ with `pandas`, `matplotlib`, `numpy` (for the analysis script only)

### Run the Application

```bash
cd SortingAlgorithms
mvn javafx:run
```

### Install Python Dependencies

```bash
pip install pandas matplotlib numpy
```

### Run the Analysis Dashboard

```bash
# Uses data/output/results.csv by default
python analyze_sorts.py

# Or pass a custom CSV path
python analyze_sorts.py path/to/your/results.csv
```

---

## Input File Format

Arrays can be loaded from `.txt` files. The file should contain a single line with a comma-separated list of integers in Python list notation:

```
[1, 4, 5, 2, 8, 9, 10, 12, 7, 0, 35]
```

The visualization mode accepts files up to 100 elements. The comparison mode has no size limit.

---

## CSV Output Format

Exported by the app or usable directly with the Python script:

```
Algorithm,ArraySize,ArrayType,NoOfRuns,AvgRuntimeNs,MinRuntimeNs,MaxRuntimeNs,Comparisons,Interchanges
MergeSort,400,RANDOM,200,583378,284707,11536066,2960,3488
QuickSort,400,RANDOM,200,2078641,1353684,13918646,3831,1991
...
```

---

## Sample Results

From the included `results.csv` (array size 400, 200 runs, RANDOM):

| Algorithm | Avg Runtime (ns) | Comparisons | Interchanges |
|---|---|---|---|
| MergeSort | 583,378 | 2,960 | 3,488 |
| HeapSort | 2,146,044 | 5,698 | 3,100 |
| QuickSort | 2,078,641 | 3,831 | 1,991 |
| SelectionSort | 18,251,822 | 79,800 | 390 |
| InsertionSort | 18,212,252 | 40,014 | 40,010 |
| BubbleSort | 22,427,551 | 79,800 | 39,587 |

---

## License

MIT
