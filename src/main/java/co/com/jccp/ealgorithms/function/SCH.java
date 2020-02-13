package co.com.jccp.ealgorithms.function;


public class SCH implements ObjectiveFunction<double[]> {
    @Override
    public double[] apply(double[] individual) {
        double[] results = new double[2];
        results[0] = individual[0] * individual[0];
        results[1] = (individual[0] - 2.0) * (individual[0] - 2.0);
        return results;
    }

    @Override
    public double[][] optimal(int totalPoints) {
        double[][] optimal = new double[totalPoints][2];
        double pass = 2.0 / (totalPoints - 1.0);
        double[] x = new double[totalPoints];
        x[0] = 0;
        for (int i = 1; i < totalPoints; i++) {
            x[i] = x[i - 1] + pass;
        }

        for (int i = 0; i < totalPoints; i++) {
            optimal[i] = apply(new double[]{x[i]});
        }
        return optimal;
    }

    @Override
    public int getNObjectives() {
        return 2;
    }
}
