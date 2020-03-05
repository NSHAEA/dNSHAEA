package co.com.jccp.dnshaea.distributed.cloud;

import co.com.jccp.dnshaea.individual.MOEAIndividual;

import java.util.List;

/**
 * Created by: Juan Camilo Castro Pinto
 **/
public class CloudIndividual<T> {

    private MOEAIndividual<T> individual;
    private List<MOEAIndividual<T>> pop;

    public MOEAIndividual<T> getIndividual() {
        return individual;
    }

    public void setIndividual(MOEAIndividual<T> individual) {
        this.individual = individual;
    }

    public List<MOEAIndividual<T>> getPop() {
        return pop;
    }

    public void setPop(List<MOEAIndividual<T>> pop) {
        this.pop = pop;
    }
}
