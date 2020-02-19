package co.com.jccp.dnshaea.initialization;

import co.com.jccp.dnshaea.individual.MOEAIndividual;

import java.util.List;


public abstract class PopulationInitialization<T> {



    public abstract List<MOEAIndividual<T>> init(int popSize, int dimensions);


}
