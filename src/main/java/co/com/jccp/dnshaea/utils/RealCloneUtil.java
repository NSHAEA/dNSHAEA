package co.com.jccp.dnshaea.utils;

import co.com.jccp.dnshaea.individual.MOEAIndividual;

import java.util.Arrays;


public class RealCloneUtil implements CloneUtils<double[]> {
    @Override
    public MOEAIndividual<double[]> clone(MOEAIndividual<double[]> original) {
        MOEAIndividual<double[]> copy = new MOEAIndividual<>();
        copy.setData(Arrays.copyOf(original.getData(), original.getData().length));
        return copy;
    }
}
