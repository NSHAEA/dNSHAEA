package co.com.jccp.dnshaea.utils;

import co.com.jccp.dnshaea.individual.MOEAIndividual;


public interface CloneUtils<T> {

    MOEAIndividual<T> clone(MOEAIndividual<T> original);

}
