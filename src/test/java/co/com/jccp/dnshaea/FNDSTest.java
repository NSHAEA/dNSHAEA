package co.com.jccp.dnshaea;

import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.utils.FNDS;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class FNDSTest {

    @Test
    public void fndsTest()
    {
        Random rnd = new Random();

        List<MOEAIndividual<double[]>> individuals = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {

            MOEAIndividual<double[]> individual = new MOEAIndividual<>();
            individual.setObjectiveValues(new double[]{rnd.nextDouble(), rnd.nextDouble()});
            individuals.add(individual);
        }

        List<List<MOEAIndividual<double[]>>> fronts = FNDS.apply(individuals, true);

        for (List<MOEAIndividual<double[]>> front : fronts) {
            System.out.println("FRONT.....");

            for (MOEAIndividual individual : front) {
                System.out.println(Arrays.toString(individual.getObjectiveValues()) + " : " + individual.getParetoRank());
            }

        }



    }

    @Test
    public void probe()
    {
        double a = Double.MAX_VALUE;

        System.out.println(a + 100000000.0);
    }



}
