package co.com.jccp.dnshaea.function;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;


public class POL implements ObjectiveFunction<double[]> {

    @Override
    public double[] apply(double[] individual) {

        double a1 = 0.5 * Math.sin(1.0) - 2.0 * Math.cos(1.0) + Math.sin(2.0) - 1.5 * Math.cos(2.0);
        double a2 = 1.5 * Math.sin(1.0) - Math.cos(1.0) + 2.0 * Math.sin(2.0) - 0.5 * Math.cos(2.0);
        double b1 = 0.5 * Math.sin(individual[0]) - 2.0 * Math.cos(individual[0]) + Math.sin(individual[1]) - 1.5 * Math.cos(individual[1]);
        double b2 = 1.5 * Math.sin(individual[0]) - Math.cos(individual[0]) + 2.0 * Math.sin(individual[1]) - 0.5 * Math.cos(individual[1]);

        double f1 = 1 + Math.pow(a1 - b1, 2) + Math.pow(a2 - b2, 2);
        double f2 = Math.pow(individual[0] + 3.0, 2) + Math.pow(individual[1] + 1.0, 2);

        return new double[]{f1, f2};
    }

    @Override
    public double[][] optimal(int totalPoints) {
        List<String> lines ;
        try {
            lines = Files.readAllLines(new File("POL.txt").toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo POL.txt");
            return new double[0][];
        }

        double[][] optimal = new double[lines.size()][2];
        for (int i = 0; i < lines.size(); i++) {
            String[] ss = lines.get(i).split(" ");
            optimal[i][1] = Double.parseDouble(ss[1]);
            optimal[i][0] = Double.parseDouble(ss[0]);
        }


        return optimal;
    }

    @Override
    public int getNObjectives() {
        return 2;
    }
}
