package sorting.algorithms;

import java.util.Random;

public class QuickSort extends AbstractSort {

    public QuickSort() {
        this.name = "QuickSort";
    }

    @Override
    public void sort(int[] array)
    {
        reset();
        quickSort(array,0,array.length-1);
    }

    private void quickSort(int[] array, int p, int r) {
        if(p<r)
        {
            int pivot=randomizedPartition(array,p,r);
            quickSort(array,p,pivot-1);
            quickSort(array,pivot+1,r);
        }
    }
    private int randomizedPartition(int[] array, int p, int r) {
        Random random = new Random();
        int pivot=random.nextInt(p,r+1);
        swap(array,pivot,p);
        interchanges++;
        addStep(array.clone(),pivot);
        return  partition(array,p,r);
    }

    private int partition(int[] array, int p, int r) {
        int pivot=array[p];
        int i=p;
        int j=p+1;
        for(;j<=r;j++)
        {
            if(array[j]<=pivot)
            {
                i++;
                if(i!=j)
                {
                    swap(array,i,j);
                    interchanges++;
                    addStep(array.clone(),j);
                }
            }
            comparisons++;
        }
        if(i!=p)
        {
            swap(array,i,p);
            interchanges++;
            addStep(array.clone(),i);
        }

        return  i;
    }



}
