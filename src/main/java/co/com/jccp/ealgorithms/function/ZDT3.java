package co.com.jccp.ealgorithms.function;


public class ZDT3 implements ObjectiveFunction<double[]> {

    @Override
    public double[] apply(double[] individual) {

        double f1 = Math.abs(individual[0]);
        double f2 = 0.0;

        double g = 0.0;

        for (int i = 1; i < 30; i++) {
            g += individual[i];
        }

        g = 1 + (9*(g)/(29));

        double h = (f1/g) * Math.sin(10*Math.PI*f1);

        f2 = g * (1 - Math.sqrt(f1 / g) - h);



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
            double[] xi = new double[30];
            xi[0] = x0[i];
            for (int j = 1; j < 30; j++) {
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
