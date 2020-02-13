package co.com.jccp.ealgorithms.selection;

import co.com.jccp.ealgorithms.individual.MOEAIndividual;

import java.util.Comparator;
import java.util.List;


public interface Selection<T> {

    List<MOEAIndividual<T>> select(List<MOEAIndividual<T>> individuals, int individualNumber, boolean minimize, Comparator<MOEAIndividual<T>> comparator);

}
