package co.com.jccp.dnshaea.gop;

import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.utils.RandomUtils;

import java.util.ArrayList;
import java.util.List;


public class SimulatedBinaryXover extends GeneticOperator<double[]> {

    double distributionIndex;
    private static final double EPS = 1.0e-14;
    double[][] limits;


    public SimulatedBinaryXover(double distributionIndex, double[][] limits)
    {
        this.distributionIndex = distributionIndex;
        this.limits = limits;
        setnParents(2);
    }

    @Override
    public List<MOEAIndividual<double[]>> apply(List<MOEAIndividual<double[]>> parents) {

        MOEAIndividual<double[]> p1 = parents.get(0);
        MOEAIndividual<double[]> p2 = parents.get(1);

        if(p1.getData().length == 1)
            return applySBX(p1, p2, 1.0);
        else
            return applySBX(p1, p2, 0.5);
    }

    private List<MOEAIndividual<double[]>> applySBX(MOEAIndividual<double[]> p1, MOEAIndividual<double[]> p2, double pr)
    {
        List<MOEAIndividual<double[]>> off = new ArrayList<>(2);
        int nData = p1.getData().length;
        double[] newData1 = new double[nData];
        double[] newData2 = new double[nData];
        double y1, y2;


        for (int i = 0; i < p1.getData().length; i++) {

            if(RandomUtils.nextDouble() < pr)
            {
                if(Math.abs(p1.getData()[i] - p2.getData()[i]) > EPS)
                {
                    if (p1.getData()[i] < p2.getData()[i]) {
                        y1 = p1.getData()[i];
                        y2 = p2.getData()[i];
                    } else {
                        y1 = p2.getData()[i];
                        y2 = p1.getData()[i];
                    }

                    double u = RandomUtils.nextDouble();
                    double beta = 1.0 + (2.0 * (y1 - limits[i][0]) / (y2 - y1));
                    double alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));
                    double betaq;

                    if (u <= (1.0 / alpha)) {
                        betaq = Math.pow(u * alpha, (1.0 / (distributionIndex + 1.0)));
                    } else {
                        betaq = Math.pow(1.0 / (2.0 - u * alpha), 1.0 / (distributionIndex + 1.0));
                    }
                    double c1 = 0.5 * (y1 + y2 - betaq * (y2 - y1));

                    beta = 1.0 + (2.0 * (limits[i][1] - y2) / (y2 - y1));
                    alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

                    if (u <= (1.0 / alpha)) {
                        betaq = Math.pow((u * alpha), (1.0 / (distributionIndex + 1.0)));
                    } else {
                        betaq = Math.pow(1.0 / (2.0 - u * alpha), 1.0 / (distributionIndex + 1.0));
                    }
                    double c2 = 0.5 * (y1 + y2 + betaq * (y2 - y1));

                    if(c1 < limits[i][0])
                        c1 = limits[i][0];
                    if(c1 > limits[i][1])
                        c1 = limits[i][1];

                    if(c2 < limits[i][0])
                        c2 = limits[i][0];
                    if(c2 > limits[i][1])
                        c2 = limits[i][1];

                    if(RandomUtils.nextDouble() <= 0.5)
                    {
                        newData1[i] = c2;
                        newData2[i] = c1;
                    }
                    else
                    {
                        newData1[i] = c1;
                        newData2[i] = c2;
                    }
                }
                else
                {
                    newData1[i] = p1.getData()[i];
                    newData2[i] = p2.getData()[i];
                }
            }
            else
            {
                newData1[i] = p2.getData()[i];
                newData2[i] = p1.getData()[i];
            }

        }
        MOEAIndividual<double[]> off1 = new MOEAIndividual<>();
        off1.setData(newData1);
        MOEAIndividual<double[]> off2 = new MOEAIndividual<>();
        off2.setData(newData2);

        off.add(off1);off.add(off2);

        return off;
    }

}
