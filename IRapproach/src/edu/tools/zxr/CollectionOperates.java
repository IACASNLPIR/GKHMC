package edu.tools.zxr;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;

/**
 * Created by sunder on 2016/6/2.
 * 自己定义的一些集合（数组、列表、map等）的函数
 */
public class CollectionOperates {
    // list
    public static int sumIntList(List<Integer> list){
        return list.stream()
                .reduce(0, (e1, e2) -> e1 + e2);
    }

    public static float sumFloatList(List<Float> list){
        return list.stream()
                .reduce((float) 0, (e1, e2) -> e1 + e2);
    }

    public static double sumDoubleList(List<Double> list){
        return list.stream()
                .reduce(0.0, (e1, e2) -> e1 + e2);
    }

    public static int sumIntListTopN(List<Integer> list, int N){
        int size = list.size() > N ? N : list.size();
        return list.subList(0, size).stream()
                .reduce(0, (e1, e2) -> e1 + e2);
    }

    public static float sumFloatListTopN(List<Float> list, int N){
        int size = list.size() > N ? N : list.size();
        return list.subList(0, size).stream()
                .reduce((float) 0, (e1, e2) -> e1 + e2);
    }

    public static double sumDoubleListTopN(List<Double> list, int N){
        int size = list.size() > N ? N : list.size();
        return list.subList(0, size).stream()
                .reduce(0.0, (e1, e2) -> e1 + e2);
    }

    // array
    public static int sumArray(int[] array){
        return Arrays.stream(array)
                .reduce(0, (e1, e2) -> e1 + e2);
    }

    public static double sumArray(double[] array){
        return Arrays.stream(array)
                .reduce(0, (e1, e2) -> e1 + e2);
    }

    public static double[] normalizeArray(double[] array){
        double sum = sumArray(array);
        double[] normalizedArray = new double[array.length];
        for (int i = 0; i < array.length; i++){
            normalizedArray[i] = array[i]/sum;
        }
        return normalizedArray;
    }
    public static double[] normalizeArray(int[] array){
        int sum = sumArray(array);
        double[] normalizedArray = new double[array.length];
        for (int i = 0; i < array.length; i++){
            normalizedArray[i] = array[i]/sum;
        }
        return normalizedArray;
    }
}
