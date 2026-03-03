package sorting.input;

import sorting.model.ArrayType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class ArrayGenerator {


    public int[] generateFromFile(String fileName)  {
        String path = "data/input/"+fileName;
        try(BufferedReader reader = new BufferedReader(new FileReader(path)))
        {
            String line=reader.readLine();
            return parseFileInput(line);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }
        catch (IOException e)
        {
            System.out.println("Error reading file");
        }
        return null;
    }

    public int[] generateFromRandom(int arraySize,ArrayType arrayType)
    {
        int[] array = new int[arraySize];
        Random random = new Random();
        for (int i = 0; i < arraySize; i++) {
            array[i] = random.nextInt(100);
        }
        switch(arrayType)
        {
            case RANDOM: return  array;
            case SORTED:
                Arrays.sort(array);
                return  array;

            case INVERSELY_SORTED:
                Arrays.sort(array);
                for(int i = 0; i < arraySize/2; i++)
                {
                    int temp = array[i];
                    array[i] = array[arraySize-i-1];
                    array[arraySize-i-1] = temp;
                }
                return  array;

            default:
                return  array;
        }
    }


    private int[] parseFileInput(String line)
    {
        line=line.replaceAll("[\\[\\]]","");
        String[] lines = line.split(", ");
        int[] array = new int[lines.length];
        for(int i=0;i<lines.length;i++)
        {
            array[i] = Integer.parseInt(lines[i]);
        }
        return array;
    }

//    public static void main(String[] args) {
//
//        ArrayGenerator obj = new ArrayGenerator();
//
//        int[] arr1 = obj.generateFromRandom(10,  ArrayType.RANDOM);
//        int[] arr2 = obj.generateFromRandom(10, ArrayType.SORTED);
//        int[] arr3 = obj.generateFromRandom(10,  ArrayType.INVERSELY_SORTED);
//        int[] arr4 = obj.generateFromFile("eyad.txt");
//        System.out.println("FROM FILE:");
//        printArray(arr4);
//
//        System.out.println("RANDOM:");
//        printArray(arr1);
//
//        System.out.println("SORTED:");
//        printArray(arr2);
//
//        System.out.println("INVERSELY SORTED:");
//        printArray(arr3);
//    }
//
//    private static void printArray(int[] arr) {
//        if (arr == null) {
//            System.out.println("Array is null");
//            return;
//        }
//
//        for (int num : arr) {
//            System.out.print(num + " ");
//        }
//        System.out.println();
//    }

}
