package co.com.jccp.dnshaea.distributed.cpu;

import co.com.jccp.dnshaea.function.ObjectiveFunction;
import co.com.jccp.dnshaea.gop.GeneticOperator;
import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.selection.Selection;
import co.com.jccp.dnshaea.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GenerateOffspring<T> implements Runnable {

    List<MOEAIndividual<T>> off;
    MOEAIndividual<T> ind;
    List<MOEAIndividual<T>> pop;
    List<GeneticOperator<T>> operators;
    Selection<T> selection;
    ObjectiveFunction<T> function;
    boolean minimize;
    Comparator<MOEAIndividual<T>> comp;

    public GenerateOffspring(MOEAIndividual<T> ind, List<MOEAIndividual<T>> pop, List<GeneticOperator<T>> operators, Selection<T> selection, ObjectiveFunction<T> function, boolean minimize, Comparator<MOEAIndividual<T>> comp)
    {
        this.ind = ind;
        this.pop = pop;
        this.operators = operators;
        this.selection = selection;
        this.function = function;
        this.minimize = minimize;
        this.comp = comp;
    }

    @Override
    public void run() {
        int selectedOP = RandomUtils.nextIntegerWithDefinedDistribution(ind.getOpProbabilities());
        ind.setSelectedOp(selectedOP);
        GeneticOperator<T> operator = operators.get(selectedOP);
        List<MOEAIndividual<T>> parents = new ArrayList<>(operator.getnParents());
        parents.add(ind);
        if(operator.getnParents() >= 2)
            parents.addAll(selection.select(pop, operator.getnParents() - 1, minimize, comp));
        List<MOEAIndividual<T>> off = operator.apply(parents);
        calculateObjectives(off);
        ind.setOffspring(off);
        this.off = off;
    }

    private void calculateObjectives(List<MOEAIndividual<T>> pop)
    {
        for (MOEAIndividual<T> ind : pop) {
            double[] objectiveValues = function.apply(ind.getData());
            ind.setObjectiveValues(objectiveValues);
        }
    }

    public List<MOEAIndividual<T>> getOff() {
        return off;
    }
}
