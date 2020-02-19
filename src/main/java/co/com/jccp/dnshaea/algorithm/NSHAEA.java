package co.com.jccp.dnshaea.algorithm;

import co.com.jccp.dnshaea.function.ObjectiveFunction;
import co.com.jccp.dnshaea.gop.GeneticOperator;
import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.initialization.PopulationInitialization;
import co.com.jccp.dnshaea.selection.Selection;
import co.com.jccp.dnshaea.selection.TournamentSelection;
import co.com.jccp.dnshaea.utils.CloneUtils;
import co.com.jccp.dnshaea.utils.CrowdingDistance;
import co.com.jccp.dnshaea.utils.FNDS;
import co.com.jccp.dnshaea.utils.RandomUtils;

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
    private Selection<T> selection;

    public NSHAEA(PopulationInitialization<T> popInit, ObjectiveFunction<T> function, boolean minimize, int dimensions, int popSize, int iterations, List<GeneticOperator<T>> operators, Selection<T> selection) {
        this.popInit = popInit;
        this.function = function;
        this.minimize = minimize;
        this.dimensions = dimensions;
        this.popSize = popSize;
        this.iterations = iterations;
        this.operators = operators;
        this.selection = selection;
    }

    public List<MOEAIndividual<T>> apply()
    {
        List<MOEAIndividual<T>> pop = popInit.init(popSize, dimensions);
        calculateObjectives(pop);
        for (MOEAIndividual<T> individual : pop) {
            individual.setOpProbabilities(RandomUtils.generateRandomProbabilityVector(operators.size()));
        }
        Comparator<MOEAIndividual<T>> totalComparator = (o1, o2) -> {
            int result = o1.compareByRank(o2);
            if (result == 0)
                return o2.compareByDiversity(o1);
            return result;
        };
        FNDS.apply(pop, minimize);
        for (int t = 0; t < iterations; t++) {
            Comparator<MOEAIndividual<T>> comp;
            if(t == 0)
                comp = MOEAIndividual::compareByRank;
            else
                comp = totalComparator;


            List<MOEAIndividual<T>> offspring = new ArrayList<>();
            for (MOEAIndividual<T> ind : pop) {
                int selectedOP = RandomUtils.nextIntegerWithDefinedDistribution(ind.getOpProbabilities());
                ind.setSelectedOp(selectedOP);
                GeneticOperator<T> operator = operators.get(selectedOP);
                List<MOEAIndividual<T>> parents = new ArrayList<>(operator.getnParents());
                parents.add(ind);
                if(operator.getnParents() >= 2)
                    parents.addAll(selection.select(pop, operator.getnParents() - 1, minimize, comp));
                List<MOEAIndividual<T>> off = operator.apply(parents);
                calculateObjectives(off);
                offspring.addAll(off);
                ind.setOffspring(off);
            }

            List<MOEAIndividual<T>> r = new ArrayList<>(pop.size() + offspring.size());
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
            for (List<MOEAIndividual<T>> front : fronts) {
                CrowdingDistance.apply(front, limitsObjective);
            }

            List<MOEAIndividual<T>> newPop = new ArrayList<>(popSize);


            for (MOEAIndividual<T> ind : pop) {

                MOEAIndividual<T> best = ind;
                for (MOEAIndividual<T> off : ind.getOffspring()) {
                    if(totalComparator.compare(off, best) <= 0) {
                        best = off;
                    }
                }

                if(best.equals(ind))
                {
                    modifyProbabilities(-1, ind.getOpProbabilities(), ind.getSelectedOp());
                }
                else
                {
                    if (totalComparator.compare(ind, best) == 0)
                        modifyProbabilities(-1, ind.getOpProbabilities(), ind.getSelectedOp());
                    else
                        modifyProbabilities(1, ind.getOpProbabilities(), ind.getSelectedOp());
                }
                best.setOpProbabilities(ind.getOpProbabilities());
                newPop.add(best);
            }
            pop = newPop;
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
