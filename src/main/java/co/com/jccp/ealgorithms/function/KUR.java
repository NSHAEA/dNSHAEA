package co.com.jccp.ealgorithms.function;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;


public class KUR implements ObjectiveFunction<double[]> {

    @Override
    public double[] apply(double[] individual) {

        double f1 = 0.0;
        double f2 = 0.0;

        for (int i = 0; i < 2; i++) {
            f1 += -10.0 * Math.exp(-0.2 * Math.sqrt(Math.pow(individual[i], 2) + Math.pow(individual[i + 1], 2)));
        }

        for (int i = 0; i < 3; i++) {
            f2 += Math.pow(Math.abs(individual[i]), 0.8) + 5.0 * Math.sin(Math.pow(individual[i], 3));
        }

        return new double[]{f1, f2};
    }

    @Override
    public double[][] optimal(int totalPoints) {

        List<String> lines ;
        try {
            lines = Files.readAllLines(new File("KUR.txt").toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error leyendo el archivo KUR.txt");
            return new double[0][];
        }

        double[][] optimal = new double[lines.size()][2];
        for (int i = 0; i < lines.size(); i++) {
            String[] ss = lines.get(i).split(" ");
            optimal[i][0] = Double.parseDouble(ss[0]);
            optimal[i][1] = Double.parseDouble(ss[1]);
        }


        return optimal;
    }

    @Override
    public int getNObjectives() {
        return 2;
    }
}
