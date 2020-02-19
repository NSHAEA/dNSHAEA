package co.com.jccp.dnshaea.selection;

import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;


public class TournamentSelection<T> implements Selection<T> {

    private int numberRivals;

    public TournamentSelection(int numberRivals) {
        this.numberRivals = numberRivals;
    }

    @Override
    public List<MOEAIndividual<T>> select(List<MOEAIndividual<T>> individuals, int individualNumber, boolean minimize, Comparator<MOEAIndividual<T>> comparator) {

        int max = individuals.size();
        List<MOEAIndividual<T>> selected = new ArrayList<>();

        for (int i = 0; i < individualNumber; i++) {

            HashSet<Integer> hs = RandomUtils.getDifferentRandomIntegers(max, numberRivals);
            List<Integer>  indexes = new ArrayList<>(hs);
            MOEAIndividual<T> winner = individuals.get(indexes.get(0));

            for (int j = 1; j < numberRivals; j++) {
                if (minimize && comparator.compare(individuals.get(indexes.get(j)), winner) < 0)
                    winner = individuals.get(indexes.get(j));
                else if (!minimize && comparator.compare(individuals.get(indexes.get(j)), winner) > 0)
                    winner = individuals.get(indexes.get(j));
            }
            selected.add(winner);
        }
        return selected;
    }
}
