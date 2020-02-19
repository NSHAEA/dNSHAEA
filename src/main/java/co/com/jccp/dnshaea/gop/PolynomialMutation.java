package co.com.jccp.dnshaea.gop;

import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;


public class PolynomialMutation extends GeneticOperator<double[]> {

    private double indexParameter;
    private double[][] limits;

    public PolynomialMutation(double indexParameter, double[][] limits)
    {
        this.indexParameter = indexParameter;
        this.limits = limits;
        setnParents(1);
    }

    @Override
    public List<MOEAIndividual<double[]>> apply(List<MOEAIndividual<double[]>> parents) {

        List<MOEAIndividual<double[]>> offspring = new ArrayList<>(parents.size());

        for (MOEAIndividual<double[]> parent : parents) {

            int pos = RandomUtils.nextInt(parent.getData().length);
            double[] newData = new double[parent.getData().length];

            for (int i = 0; i < parent.getData().length; i++) {

                if (i == pos)
                {
                    double u = RandomUtils.nextDouble();
                    double delta1 = (parent.getData()[i] - limits[i][0]) / (limits[i][1] - limits[i][0]);
                    double delta2 = (limits[i][1] - parent.getData()[i]) / (limits[i][1] - limits[i][0]);
                    double mutPow = 1.0 / (indexParameter + 1.0);
                    double deltaq;

                    if(u <= 0.5) {
                        double xy = 1.0 - delta1;
                        double val = 2.0 * u + (1.0 - 2.0 * u) * (Math.pow(xy, indexParameter + 1.0));
                        deltaq = Math.pow(val, mutPow) - 1.0;
                    }
                    else {
                        double xy = 1.0 - delta2;
                        double val = 2.0 * (1.0 - u) + 2.0 * (u - 0.5) * (Math.pow(xy, indexParameter + 1.0));
                        deltaq = 1.0 - Math.pow(val, mutPow);
                    }
                    newData[i] = parent.getData()[i] + deltaq * (limits[i][1] - limits[i][0]);
                }
                else
                    newData[i] = parent.getData()[i];

                if(newData[i] < limits[i][0])
                    newData[i] = limits[i][0];
                if(newData[i] > limits[i][1])
                    newData[i] = limits[i][1];


            }
            MOEAIndividual<double[]> off = new MOEAIndividual<>();
            off.setData(newData);
            offspring.add(off);
        }

        return offspring;
    }
}
