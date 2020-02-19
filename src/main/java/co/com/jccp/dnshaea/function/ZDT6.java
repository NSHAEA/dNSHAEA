package co.com.jccp.dnshaea.function;


public class ZDT6 implements ObjectiveFunction<double[]> {

    @Override
    public double[] apply(double[] individual) {

        double f1 = 1 - Math.exp(-4*Math.abs(individual[0]))*Math.pow(Math.sin(6*Math.PI*Math.abs(individual[0])), 6);
        double f2 = 0.0;

        double g = 0.0;

        for (int i = 1; i < 10; i++) {
            g += individual[i];
        }

        g = 1 + (9*Math.pow(g/9.0, 0.25));

        f2 = g * (1 - Math.pow(f1/g, 2));



        return new double[]{f1, f2};
    }

    @Override
    public double[][] optimal(int totalPoints) {
        double[][] optimal = new double[totalPoints][2];
        double pass = 1.0 / (totalPoints - 1.0);
        double[] x0 = new double[totalPoints];
        x0[0] = 0;
        for (int i = 1; i < totalPoints; i++) {
            x0[i] = x0[i - 1] + pass;
        }

        for (int i = 0; i < totalPoints; i++) {
            double[] xi = new double[10];
            xi[0] = x0[i];
            for (int j = 1; j < 10; j++) {
                xi[j] = 0.0;
            }
            optimal[i] = apply(xi);
        }
        return optimal;
    }

    @Override
    public int getNObjectives() {
        return 2;
    }
}
