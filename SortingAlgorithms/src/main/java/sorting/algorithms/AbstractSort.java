package sorting.algorithms;

import sorting.model.SortStep;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSort implements  SortAlgorithm {
    protected long comparisons=0;
    protected  long interchanges=0;
    protected  List<SortStep> steps=new ArrayList<>();
    protected  boolean collectSteps=false;
    String name;

    protected void swap(int[] current,int i,int j){
        int temp=current[i];
        current[i]=current[j];
        current[j]=temp;
    }

    protected void addStep(int[] current,int activeIndex){
        if(collectSteps){
            steps.add(new SortStep(current,activeIndex));
        }
    }

    @Override
    public long getComparisons(){
        return comparisons;
    }

    @Override
    public long getInterchanges(){
        return interchanges;
    }

    @Override
    public List<SortStep> getSteps(){
        return steps;
    }

    @Override
    public void reset(){
        steps.clear();
        comparisons=0;
        interchanges=0;
    }

    public void setSteps(boolean steps) {
        collectSteps=steps;
    }

    @Override
    public String getName() {
        return name;
    }




}
