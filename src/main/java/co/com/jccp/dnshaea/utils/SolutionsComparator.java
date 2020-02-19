package co.com.jccp.dnshaea.utils;

import co.com.jccp.dnshaea.individual.MOEAIndividual;

import java.util.Comparator;


public class SolutionsComparator<T> implements Comparator<MOEAIndividual<T>> {

    int objective;

    public SolutionsComparator(int objective)
    {
        this.objective = objective;
    }

    @Override
    public int compare(MOEAIndividual<T> o1, MOEAIndividual<T> o2) {
        return Double.compare(o1.getObjectiveValues()[objective], o2.getObjectiveValues()[objective]);
    }
}
