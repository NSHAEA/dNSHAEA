package co.com.jccp.ealgorithms.utils;

import co.com.jccp.ealgorithms.individual.MOEAIndividual;


public interface CloneUtils<T> {

    MOEAIndividual<T> clone(MOEAIndividual<T> original);

}
