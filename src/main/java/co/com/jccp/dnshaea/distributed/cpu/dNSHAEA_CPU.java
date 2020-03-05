package co.com.jccp.dnshaea.distributed.cpu;

import co.com.jccp.dnshaea.function.ObjectiveFunction;
import co.com.jccp.dnshaea.gop.GeneticOperator;
import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.initialization.PopulationInitialization;
import co.com.jccp.dnshaea.selection.Selection;
import co.com.jccp.dnshaea.utils.CrowdingDistance;
import co.com.jccp.dnshaea.utils.FNDS;
import co.com.jccp.dnshaea.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class dNSHAEA_CPU<T> {

    private PopulationInitialization<T> popInit;
    private ObjectiveFunction<T> function;
    private boolean minimize;
    private int dimensions;
    private int popSize;
    private int iterations;
    private List<GeneticOperator<T>> operators;
    private Selection<T> selection;

    private static final int MAX_THREADS = 1;

    public dNSHAEA_CPU(PopulationInitialization<T> popInit, ObjectiveFunction<T> function, boolean minimize, int dimensions, int popSize, int iterations, List<GeneticOperator<T>> operators, Selection<T> selection) {
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
        long tt = System.currentTimeMillis();
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

            List<GenerateOffspring<T>> rs = new ArrayList<>(pop.size());
            ExecutorService generateOffspringPool = Executors.newFixedThreadPool(MAX_THREADS);

            List<MOEAIndividual<T>> offspring = new ArrayList<>();
            for (MOEAIndividual<T> ind : pop) {
                GenerateOffspring<T> go = new GenerateOffspring<>(ind, pop, operators, selection, function, minimize, comp);
                rs.add(go);
                generateOffspringPool.execute(go);
            }

            generateOffspringPool.shutdown();

            try {
                generateOffspringPool.awaitTermination(100, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (GenerateOffspring<T> r : rs) {
                offspring.addAll(r.getOff());
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
            ExecutorService replacePool = Executors.newFixedThreadPool(MAX_THREADS);
            List<Replace<T>> replaces = new ArrayList<>(pop.size());

            for (MOEAIndividual<T> ind : pop) {
                Replace<T> re = new Replace<>(pop, ind, totalComparator);
                replaces.add(re);
                replacePool.execute(re);
            }

            replacePool.shutdown();

            try {
                replacePool.awaitTermination(100, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Replace<T> re : replaces) {
                newPop.add(re.getBest());
            }
            pop = newPop;
        }
        System.out.println(System.currentTimeMillis() - tt);
        return pop;
    }

    public void calculateObjectives(List<MOEAIndividual<T>> pop)
    {
        for (MOEAIndividual<T> ind : pop) {
            double[] objectiveValues = function.apply(ind.getData());
            ind.setObjectiveValues(objectiveValues);
        }
    }



}
