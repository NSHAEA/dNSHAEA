package co.com.jccp.dnshaea.utils;

/**
 * Created by: Juan Camilo Castro Pinto
 **/
public class NumberUtils {

    public static String[] numberArrayToStringArray(double[] arr)
    {
        String[] res = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = Double.toString(arr[i]);
        }
        return res;
    }

    public static double[] stringArrayToNumberArray(String[] arr)
    {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = Double.parseDouble(arr[i]);
        }
        return res;
    }


}
