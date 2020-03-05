package co.com.jccp.dnshaea.distributed.cloud;

import co.com.jccp.dnshaea.function.ObjectiveFunction;
import co.com.jccp.dnshaea.function.ZDT4;
import co.com.jccp.dnshaea.gop.GeneticOperator;
import co.com.jccp.dnshaea.gop.PolynomialMutation;
import co.com.jccp.dnshaea.gop.SimulatedBinaryXover;
import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.selection.Selection;
import co.com.jccp.dnshaea.selection.TournamentSelection;
import co.com.jccp.dnshaea.utils.RandomUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.*;

/**
 * Created by: Juan Camilo Castro Pinto
 **/
public class RealGenerateOffspring implements RequestHandler<CloudIndividual<double[]>, List<MOEAIndividual<double[]>>> {

    private static final ObjectiveFunction<double[]> function = new ZDT4();
    private static final List<GeneticOperator<double[]>> operators = new ArrayList<>();
    private static final Selection<double[]> selection = new TournamentSelection<>(4);
    private static final boolean minimize = true;

    private static final Comparator<MOEAIndividual<double[]>> comp = (o1, o2) -> {
            int result = o1.compareByRank(o2);
            if (result == 0)
                return o2.compareByDiversity(o1);
            return result;
        };

    private static final int DIMENSIONS = 10;

    static
    {
        double[][] limits = new double[DIMENSIONS][2];

        limits[0][0] = 0.0;
        limits[0][1] = 1.0;

        for (int i = 1; i < DIMENSIONS; i++) {
            limits[i][0] = -5.0;
            limits[i][1] = 5.0;
        }
        operators.add(new SimulatedBinaryXover(5.0, limits));
        operators.add(new PolynomialMutation(5.0, limits));
    }

    @Override
    public List<MOEAIndividual<double[]>> handleRequest(CloudIndividual<double[]> input, Context context) {
        MOEAIndividual<double[]> ind = input.getIndividual();
        List<MOEAIndividual<double[]>> pop = input.getPop();
        int selectedOP = RandomUtils.nextIntegerWithDefinedDistribution(ind.getOpProbabilities());
        ind.setSelectedOp(selectedOP);
        GeneticOperator<double[]> operator = operators.get(selectedOP);
        List<MOEAIndividual<double[]>> parents = new ArrayList<>(operator.getnParents());
        parents.add(ind);
        if(operator.getnParents() >= 2)
            parents.addAll(selection.select(pop, operator.getnParents() - 1, minimize, comp));
        List<MOEAIndividual<double[]>> off = operator.apply(parents);
        calculateObjectives(off);
        return off;
    }
    private void calculateObjectives(List<MOEAIndividual<double[]>> pop)
    {
        for (MOEAIndividual<double[]> ind : pop) {
            double[] objectiveValues = function.apply(ind.getData());
            ind.setObjectiveValues(objectiveValues);
        }
    }
}