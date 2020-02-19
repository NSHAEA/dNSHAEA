package co.com.jccp.dnshaea.function;


public class FON implements ObjectiveFunction<double[]> {

    @Override
    public double[] apply(double[] individual) {

        double f1 = 0.0;
        double f2 = 0.0;


        for (int i = 0; i < 3; i++) {
            f1 += Math.pow((individual[i] - (1.0/Math.sqrt(3))), 2);
            f2 += Math.pow((individual[i] + (1.0/Math.sqrt(3))), 2);
        }

        f1 = 1 - Math.exp(-f1);
        f2 = 1 - Math.exp(-f2);

        return new double[]{f1, f2};
    }

    @Override
    public double[][] optimal(int totalPoints) {
        double[][] optimal = new double[totalPoints][2];
        double pass = (2.0 / Math.sqrt(3)) / (totalPoints - 1.0);
        double[] x = new double[totalPoints];
        x[0] = -1.0/Math.sqrt(3);
        for (int i = 1; i < totalPoints; i++) {
            x[i] = x[i - 1] + pass;
        }

        for (int i = 0; i < totalPoints; i++) {
            optimal[i] = apply(new double[]{x[i], x[i], x[i]});
        }
        return optimal;
    }

    @Override
    public int getNObjectives() {
        return 2;
    }
}
