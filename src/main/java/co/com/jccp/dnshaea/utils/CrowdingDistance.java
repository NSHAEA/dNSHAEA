package co.com.jccp.dnshaea.utils;

import co.com.jccp.dnshaea.individual.MOEAIndividual;

import java.util.List;


public class CrowdingDistance {


    public static<T> void apply(List<MOEAIndividual<T>> individuals, double[][] limitsObjective)
    {
        for (MOEAIndividual<T> individual : individuals) {
            individual.setDiversityMeasure(0.0);
        }

        int l = individuals.size();
        int objectives = individuals.get(0).getObjectiveValues().length;

        for (int i = 0; i < objectives; i++) {

            individuals.sort(new SolutionsComparator<>(i));
            individuals.get(0).setDiversityMeasure(Double.MAX_VALUE);
            individuals.get(l - 1).setDiversityMeasure(Double.MAX_VALUE);


            for (int i1 = 1; i1 < l - 1; i1++) {
                double distance = individuals.get(i1).getDiversityMeasure();
                distance += ( individuals.get(i1 + 1).getObjectiveValues()[i] -  individuals.get(i1 - 1).getObjectiveValues()[i]) / (limitsObjective[i][1] - limitsObjective[i][0]);
                if((limitsObjective[i][1] - limitsObjective[i][0]) == 0.0)
                    individuals.get(i1).setDiversityMeasure(0.0);
                else
                    individuals.get(i1).setDiversityMeasure(distance);
            }
        }
    }


}
