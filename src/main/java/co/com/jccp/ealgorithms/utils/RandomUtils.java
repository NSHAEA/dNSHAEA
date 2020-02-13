package co.com.jccp.ealgorithms.utils;

import java.util.HashSet;
import java.util.Random;


public class RandomUtils {

    public static double[] generateRandomArray(int n, double[][] limit)
    {
        Random rnd = new Random();
        double[] array = new double[n];

        for (int i = 0; i < n; i++) {
            array[i] = (rnd.nextDouble() * (limit[i][1] - limit[i][0])) + limit[i][0];
        }
        return array;
    }

    public static int nextInt(int max)
    {
        Random rnd = new Random();
        return rnd.nextInt(max);
    }

    public static HashSet<Integer> getDifferentRandomIntegers(int max, int size)
    {
        HashSet<Integer> hs =  new HashSet<>();
        while (hs.size() < size)
        {
            hs.add(nextInt(max));
        }
        return hs;
    }

    public static double nextDouble()
    {
        Random rnd = new Random();
        return rnd.nextDouble();
    }

    public static int nextIntegerWithDefinedDistribution(double[] distribution)
    {
        double[] acumm = new double[distribution.length];
        acumm[0] = distribution[0];
        for (int i = 1; i < distribution.length; i++) {
            acumm[i] = acumm[i-1] + distribution[i];
        }
        double num = nextDouble();

        for (int i = 0; i < distribution.length; i++) {
            if(num < acumm[i])
                return i;
        }
        return 0;
    }

    public static double[] generateRandomProbabilityVector(int n)
    {
        Random rnd = new Random();
        double[] array = new double[n];
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            array[i] = rnd.nextDouble();
            sum += array[i];
        }
        for (int i = 0; i < array.length; i++) {
            array[i] /= sum;
        }
        return array;
    }

}
