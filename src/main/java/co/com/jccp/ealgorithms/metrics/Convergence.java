package co.com.jccp.ealgorithms.metrics;

import java.util.Arrays;


public class Convergence {

    public static double calculate(double[][] solutionFound, double[][] optimal)
    {

        int nDataSF = solutionFound[0].length;
        int nObjectives = solutionFound.length;
        int nDataOpt = optimal[0].length;
        double minData[] = new double[nDataSF];

        double result = 0.0;

        for (int i = 0; i < nDataSF; i++) {
            double min = Double.MAX_VALUE;
            for (int j = 0; j < nDataOpt; j++) {
                double sum = 0.0;
                for (int k = 0; k < nObjectives; k++) {
                    sum += (solutionFound[k][i] - optimal[k][j]) * (solutionFound[k][i] - optimal[k][j]);
                }
                sum = Math.sqrt(sum);
                if(sum < min)
                    min = sum;
            }
            result += min;
        }

        return result / nDataSF;
    }

}
