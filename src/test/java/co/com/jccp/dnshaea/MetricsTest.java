package co.com.jccp.dnshaea;

import co.com.jccp.dnshaea.metrics.Convergence;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;


public class MetricsTest {

    @Test
    public void convergenceTest() throws IOException {

        List<String> rr = Files.readAllLines(new File("NSHAEA-ZDT1.txt").toPath());
        List<String> oo = Files.readAllLines(new File("ZDT1_OPT.txt").toPath());

        double[] xopt = new double[oo.size()];
        double[] yopt = new double[oo.size()];
        int i = 0;
        for (String s : oo) {
            String[] aloha = s.split(",");
            xopt[i] = Double.parseDouble(aloha[0]);
            yopt[i] = Double.parseDouble(aloha[1]);
            i++;
        }

        i = 1;
        int cc = 0;
        double[] xres = new double[100];
        double[] yres = new double[100];
        DoubleIndex []converg = new DoubleIndex[100];
        for (String s : rr) {
            String[] aloha = s.split(",");
            xres[i - 1] = Double.parseDouble(aloha[0]);
            yres[i - 1] = Double.parseDouble(aloha[1]);

            if(i%100 == 0)
            {

                converg[cc] = new DoubleIndex(Convergence.calculate(new double[][]{xres, yres}, new double[][]{xopt, yopt}), cc);
                System.out.println(converg[cc]);
                cc++;
                i = 0;
            }
            i++;

        }

        Arrays.sort(converg, Comparator.comparingDouble(DoubleIndex::getD));

        System.out.println();

//
//        double[][] x = new double[][]{{0,1,2},{0,1,2}};
//        double[][] y = new double[][]{{3,2},{1,2}};
//
//        System.out.println(Convergence.calculate(y, x));


    }



}
