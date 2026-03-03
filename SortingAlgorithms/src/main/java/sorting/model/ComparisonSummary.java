package sorting.model;

import java.util.List;

public class ComparisonSummary {
   private long avgRuntimeNs;
   private long minRuntimeNs;
   private long maxRuntimeNs;


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
        return summary;
    }

}
