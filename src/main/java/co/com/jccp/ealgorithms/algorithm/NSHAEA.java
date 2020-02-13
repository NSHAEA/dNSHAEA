package co.com.jccp.ealgorithms.algorithm;

import co.com.jccp.ealgorithms.function.ObjectiveFunction;
import co.com.jccp.ealgorithms.gop.GeneticOperator;
import co.com.jccp.ealgorithms.individual.MOEAIndividual;
import co.com.jccp.ealgorithms.initialization.PopulationInitialization;
import co.com.jccp.ealgorithms.selection.TournamentSelection;
import co.com.jccp.ealgorithms.utils.CloneUtils;
import co.com.jccp.ealgorithms.utils.CrowdingDistance;
import co.com.jccp.ealgorithms.utils.FNDS;
import co.com.jccp.ealgorithms.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class NSHAEA<T> {

    private PopulationInitialization<T> popInit;
    private ObjectiveFunction<T> function;
    private boolean minimize;
    private int dimensions;
    private int popSize;
    private int iterations;
    private List<GeneticOperator<T>> operators;
    private CloneUtils<T> cloneUtils;

    public NSHAEA(PopulationInitialization<T> popInit, ObjectiveFunction<T> function, boolean minimize, int dimensions, int popSize, int iterations, List<GeneticOperator<T>> operators, CloneUtils<T> cloneUtils) {
        this.popInit = popInit;
        this.function = function;
        this.minimize = minimize;
        this.dimensions = dimensions;
        this.popSize = popSize;
        this.iterations = iterations;
        this.operators = operators;
        this.cloneUtils = cloneUtils;
    }

    public List<MOEAIndividual<T>> apply()
    {
        List<MOEAIndividual<T>> pop = popInit.init(popSize, dimensions);
        calculateObjectives(pop);
        for (MOEAIndividual<T> individual : pop) {
            individual.setOpProbabilities(RandomUtils.generateRandomProbabilityVector(operators.size()));
        }
        FNDS.apply(pop, minimize);
        List<MOEAIndividual<T>> offspring = generateOffspring(pop, MOEAIndividual::compareByRank);
        for (int t = 0; t < iterations; t++) {
            calculateObjectives(offspring);
            List<MOEAIndividual<T>> r = new ArrayList<>(2*popSize);
            r.addAll(pop);
            r.addAll(offspring);
            double[][] limitsObjective = new double[function.getNObjectives()][2];
            for (int i = 0; i < function.getNObjectives(); i++) {
                limitsObjective[i][0] = Double.MAX_VALUE;
                limitsObjective[i][1] = Double.MIN_VALUE;
            }
            for (MOEAIndividual<T> ind : r) {
                for (int i = 0; i < function.getNObjectives(); i++) {
                    if(ind.getObjectiveValues()[i] < limitsObjective[i][0])
                        limitsObjective[i][0] = ind.getObjectiveValues()[i];
                    if(ind.getObjectiveValues()[i] > limitsObjective[i][1])
                        limitsObjective[i][1] = ind.getObjectiveValues()[i];
                }
            }
            List<List<MOEAIndividual<T>>> fronts = FNDS.apply(r, minimize);
            List<MOEAIndividual<T>> newPop = new ArrayList<>(popSize);
            for (List<MOEAIndividual<T>> front : fronts) {
                CrowdingDistance.apply(front, limitsObjective);
            }
            for (MOEAIndividual<T> parent : pop) {
                List<MOEAIndividual<T>> comp = new ArrayList<>(parent.getOffspring());
                comp.add(parent);
                comp.sort((o1, o2) -> {
                    int result = o1.compareByRank(o2);
                    if(result == 0)
                        return o2.compareByDiversity(o1);
                    return result;
                });
                if(comp.get(0).equals(parent))
                {
                    modifyProbabilities(-1, parent.getOpProbabilities(), parent.getSelectedOp());
                }
                else
                {
                    if (parent.compareByRank(comp.get(0)) == 0 && parent.compareByDiversity(comp.get(0)) == 0)
                        modifyProbabilities(-1, parent.getOpProbabilities(), parent.getSelectedOp());
                    else
                        modifyProbabilities(1, parent.getOpProbabilities(), parent.getSelectedOp());
                }
                comp.get(0).setOpProbabilities(parent.getOpProbabilities());
                newPop.add(comp.get(0));
            }
            pop = newPop;
            offspring = generateOffspring(pop, (o1, o2) -> {
                int result = o1.compareByRank(o2);
                if(result == 0)
                    return o2.compareByDiversity(o1);
                return result;
            });
        }
        return pop;
    }

    public void calculateObjectives(List<MOEAIndividual<T>> pop)
    {
        for (MOEAIndividual<T> ind : pop) {
            double[] objectiveValues = function.apply(ind.getData());
            ind.setObjectiveValues(objectiveValues);
        }
    }

    private List<MOEAIndividual<T>> generateOffspring(List<MOEAIndividual<T>> pop, Comparator<MOEAIndividual<T>> comparator)
    {
        List<MOEAIndividual<T>> offspring = new ArrayList<>(popSize);
        TournamentSelection<T> selection = new TournamentSelection<>(4);
        for (int i = 0; i < popSize; i++) {
            int selectedOP = RandomUtils.nextIntegerWithDefinedDistribution(pop.get(i).getOpProbabilities());
            pop.get(i).setSelectedOp(selectedOP);
            GeneticOperator<T> operator = operators.get(selectedOP);
            List<MOEAIndividual<T>> parents = new ArrayList<>(operator.getnParents());
            parents.add(pop.get(i));
            if(operator.getnParents() >= 2)
            {
                parents.addAll(selection.select(pop, operator.getnParents() - 1, minimize, comparator));
            }
            List<MOEAIndividual<T>> off = operator.apply(parents);
            offspring.addAll(off);
            pop.get(i).setOffspring(off);
        }
        return offspring;
    }

    private void modifyProbabilities(int sign, double[] prob, int selectedOGIndex)
    {
        double num = RandomUtils.nextDouble();
        num *= sign;
        prob[selectedOGIndex] += num;
        prob[selectedOGIndex] = prob[selectedOGIndex] < 0 ? prob[selectedOGIndex]*-1 : prob[selectedOGIndex];
        double sum = 0.0;
        for (double aProb : prob) {
            sum += aProb;
        }
        for (int i = 0; i < prob.length; i++) {
            prob[i] /= sum;
        }
    }


}
