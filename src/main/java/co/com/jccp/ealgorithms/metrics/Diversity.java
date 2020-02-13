package co.com.jccp.ealgorithms.metrics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Diversity {

    public static double calculate(double[][] solutionFound, double[][] optimal)
    {

        int nDataSF = solutionFound[0].length;
        int nObjectives = solutionFound.length;
        int nDataOpt = optimal[0].length;

        List<DoubleIndividual> solutionF = new ArrayList<>(nDataSF);
        List<DoubleIndividual> solutionO = new ArrayList<>(nDataSF);

        for (int i = 0; i < nDataSF; i++) {
            double[] ind = new double[nObjectives];
            for (int j = 0; j < nObjectives; j++) {
                ind[j] = solutionFound[j][i];
            }
            solutionF.add(new DoubleIndividual(ind));
        }

        for (int i = 0; i < nDataOpt; i++) {
            double[] ind = new double[nObjectives];
            for (int j = 0; j < nObjectives; j++) {
                ind[j] = optimal[j][i];
            }
            solutionO.add(new DoubleIndividual(ind));
        }

        solutionF.sort(Comparator.comparingDouble(o -> o.solution[0]));
        solutionO.sort(Comparator.comparingDouble(o -> o.solution[0]));

        double df = 0.0;
        double dl = 0.0;

        for (int i = 0; i < nObjectives; i++) {
            df += (solutionF.get(0).solution[i] - solutionO.get(0).solution[i]) * (solutionF.get(0).solution[i] - solutionO.get(0).solution[i]);
        }
        df = Math.sqrt(df);

        for (int i = 0; i < nObjectives; i++) {
            dl += (solutionF.get(nDataSF - 1).solution[i] - solutionO.get(nDataOpt - 1).solution[i]) * (solutionF.get(nDataSF - 1).solution[i] - solutionO.get(nDataOpt - 1).solution[i]);
        }
        dl = Math.sqrt(dl);

        double mean = 0.0;
        double diversitySum = df + dl;

        for (int i = 0; i < (nDataSF - 1); i++) {
            double sum = 0.0;
            for (int j = 0; j < nObjectives; j++) {
                sum += (solutionF.get(i).solution[j] - solutionF.get(i + 1).solution[j]) * (solutionF.get(i).solution[j] - solutionF.get(i + 1).solution[j]);
            }
            mean += Math.sqrt(sum);
        }

        mean = mean / (double) (nDataSF - 1);

        for (int i = 0; i < (nDataSF - 1); i++) {
            double sum = 0.0;
            for (int j = 0; j < nObjectives; j++) {
                sum += (solutionF.get(i).solution[j] - solutionF.get(i + 1).solution[j]) * (solutionF.get(i).solution[j] - solutionF.get(i + 1).solution[j]);
            }
            diversitySum += Math.abs(Math.sqrt(sum) - mean);
        }
        return diversitySum / (df + dl + (nDataSF - 1) * mean);




    }

    private static class DoubleIndividual
    {
        double[] solution;

        public DoubleIndividual(double[] solution)
        {
            this.solution = solution;
        }

    }


}
