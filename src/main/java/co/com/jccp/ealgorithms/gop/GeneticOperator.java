package co.com.jccp.ealgorithms.gop;

import co.com.jccp.ealgorithms.individual.MOEAIndividual;

import java.util.List;


public abstract class GeneticOperator<T> {

    int nParents;

    public abstract List<MOEAIndividual<T>> apply(List<MOEAIndividual<T>> parents);

    public int getnParents() {
        return nParents;
    }

    public void setnParents(int nParents) {
        this.nParents = nParents;
    }
}
