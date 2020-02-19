package co.com.jccp.dnshaea.algorithm;

import co.com.jccp.dnshaea.gop.GeneticOperator;
import co.com.jccp.dnshaea.selection.TournamentSelection;
import co.com.jccp.dnshaea.utils.CloneUtils;
import co.com.jccp.dnshaea.utils.CrowdingDistance;
import co.com.jccp.dnshaea.utils.FNDS;
import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.function.ObjectiveFunction;
import co.com.jccp.dnshaea.initialization.PopulationInitialization;
import co.com.jccp.dnshaea.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class NSGAII<T> {

    private PopulationInitialization<T> popInit;
    private ObjectiveFunction<T> function;
    private boolean minimize;
    private int dimensions;
    private int popSize;
    private double mutationProbability;
    private double xoverProbability;
    private int iterations;
    private GeneticOperator<T> xover;
    private GeneticOperator<T> mutation;
    private CloneUtils<T> cloneUtils;

    public NSGAII(PopulationInitialization<T> popInit, ObjectiveFunction<T> function, boolean minimize, int dimensions, int popSize, double mutationProbability, double xoverProbability, int iterations, GeneticOperator<T> xover, GeneticOperator<T> mutation, CloneUtils<T> cloneUtils) {
        this.popInit = popInit;
        this.function = function;
        this.minimize = minimize;
        this.dimensions = dimensions;
        this.popSize = popSize;
        this.mutationProbability = mutationProbability;
        this.xoverProbability = xoverProbability;
        this.iterations = iterations;
        this.xover = xover;
        this.mutation = mutation;
        this.cloneUtils = cloneUtils;
    }

    public List<MOEAIndividual<T>> apply()
    {
        List<MOEAIndividual<T>> pop = popInit.init(popSize, dimensions);

        calculateObjectives(pop);

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
            int i=0;
            while (newPop.size() + fronts.get(i).size() <= popSize) {
                CrowdingDistance.apply(fronts.get(i), limitsObjective);
                newPop.addAll(fronts.get(i));
                i++;
            }

            if(newPop.size() < popSize)
            {
                CrowdingDistance.apply(fronts.get(i), limitsObjective);
                fronts.get(i).sort((o1, o2) -> {
                    int result = o1.compareByRank(o2);
                    if(result == 0)
                        return o2.compareByDiversity(o1);
                    return result;
                });
                int k = 0;
                while(newPop.size() < popSize) {
                    newPop.add(fronts.get(i).get(k));
                    k++;
                }
            }

            pop = newPop;
            offspring = generateOffspring(pop, (o1, o2) -> {
                int result = o1.compareByRank(o2);
                if(result == 0)
                    return o2.compareByDiversity(o1);
                return result;
            });
        }

//        calculateObjectives(offspring);
//
//        List<MOEAIndividual<T>> r = new ArrayList<>();
//        r.addAll(pop);
//        r.addAll(offspring);

        return FNDS.apply(pop, minimize).get(0);
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
        TournamentSelection<T> selection = new TournamentSelection<>(2);
        for (int i = 0; i < popSize; i+=2) {
            List<MOEAIndividual<T>> offi = new ArrayList<>();
            List<MOEAIndividual<T>> parents = selection.select(pop, 2, minimize, comparator);
            MOEAIndividual<T> off1 = cloneUtils.clone(parents.get(0));
            MOEAIndividual<T> off2 = cloneUtils.clone(parents.get(1));
            offi.add(off1);offi.add(off2);

            if(RandomUtils.nextDouble() < xoverProbability)
            {
                offi = xover.apply(offi);
            }
            if(RandomUtils.nextDouble() < mutationProbability)
            {
                offi = mutation.apply(offi);
            }
            offspring.addAll(offi);

        }
        return offspring;
    }


}
