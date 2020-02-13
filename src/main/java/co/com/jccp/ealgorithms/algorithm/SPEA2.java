package co.com.jccp.ealgorithms.algorithm;

import co.com.jccp.ealgorithms.function.ObjectiveFunction;
import co.com.jccp.ealgorithms.gop.GeneticOperator;
import co.com.jccp.ealgorithms.individual.MOEAIndividual;
import co.com.jccp.ealgorithms.initialization.PopulationInitialization;
import co.com.jccp.ealgorithms.selection.TournamentSelection;
import co.com.jccp.ealgorithms.utils.CloneUtils;
import co.com.jccp.ealgorithms.utils.ParetoUtils;
import co.com.jccp.ealgorithms.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class SPEA2<T> {

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
    private int archiveSize;

    public SPEA2(PopulationInitialization<T> popInit, ObjectiveFunction<T> function, boolean minimize, int dimensions, int popSize, double mutationProbability, double xoverProbability, int iterations, GeneticOperator<T> xover, GeneticOperator<T> mutation, CloneUtils<T> cloneUtils, int archiveSize) {
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
        this.archiveSize = archiveSize;
    }

    public List<MOEAIndividual<T>> apply()
    {
        List<MOEAIndividual<T>> pop = popInit.init(popSize, dimensions);
        List<MOEAIndividual<T>> archive = new ArrayList<>(archiveSize);

        for (int t = 0; t < iterations; t++) {

            List<MOEAIndividual<T>> r = new ArrayList<>(pop);
            r.addAll(archive);
            calculateObjectives(r);
            calculateFitness(r);
            r.sort(Comparator.comparingDouble(MOEAIndividual::getFitness));
            archive = r.subList(0, archiveSize);
            pop = generateOffspring(pop, Comparator.comparingDouble(MOEAIndividual::getFitness));

        }

        return archive;




    }

    public void calculateObjectives(List<MOEAIndividual<T>> pop)
    {
        for (MOEAIndividual<T> ind : pop) {
            double[] objectiveValues = function.apply(ind.getData());
            ind.setObjectiveValues(objectiveValues);
            ind.setParetoRank(0);
            ind.setFitness(0.0);
            ind.setDiversityMeasure(0.0);
        }
    }

    public void calculateFitness(List<MOEAIndividual<T>> r)
    {

        for (int i = 0; i < r.size(); i++) {
            for (int j = i + 1; j < r.size(); j++) {
                if (ParetoUtils.dominates(r.get(i), r.get(j), minimize))
                    r.get(i).setParetoRank(r.get(i).getParetoRank() + 1);
                else if(ParetoUtils.dominates(r.get(j), r.get(i), minimize))
                    r.get(j).setParetoRank(r.get(j).getParetoRank() + 1);
            }
        }

        for (int i = 0; i < r.size(); i++) {
            for (int j = i + 1; j < r.size(); j++) {
                if (ParetoUtils.dominates(r.get(i), r.get(j), minimize))
                    r.get(j).setFitness((double)r.get(i).getParetoRank() + r.get(j).getFitness());
                else if(ParetoUtils.dominates(r.get(j), r.get(i), minimize))
                    r.get(i).setFitness((double)r.get(j).getParetoRank() + r.get(i).getFitness());
            }
        }

        int k = (int)Math.sqrt(r.size());

        List<MOEAIndividual<T>> aux = new ArrayList<>(r);

        for (int i = 0; i < r.size(); i++) {
            for (int j = 0; j < r.size(); j++) {
                if(i != j)
                {
                    double dist = 0.0;
                    for (int l = 0; l < function.getNObjectives(); l++) {
                        dist += (r.get(i).getObjectiveValues()[l] - r.get(j).getObjectiveValues()[l]) * (r.get(i).getObjectiveValues()[l] - r.get(j).getObjectiveValues()[l]);
                    }
                    r.get(j).setDiversityMeasure(Math.sqrt(dist));
                }
            }
            aux.sort(Comparator.comparingDouble(MOEAIndividual::getDiversityMeasure));
            r.get(i).setFitness(r.get(i).getFitness() + 1.0/(aux.get(k).getDiversityMeasure() + 2.0));
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
