package co.com.jccp.dnshaea.distributed.cloud;

import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.utils.RandomUtils;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Comparator;
import java.util.List;

/**
 * Created by: Juan Camilo Castro Pinto
 **/
public class RealReplace implements RequestHandler<CloudIndividual<double[]>, MOEAIndividual<double[]>> {

    private static final Comparator<MOEAIndividual<double[]>> comp = (o1, o2) -> {
        int result = o1.compareByRank(o2);
        if (result == 0)
            return o2.compareByDiversity(o1);
        return result;
    };

    @Override
    public MOEAIndividual<double[]> handleRequest(CloudIndividual<double[]> input, Context context) {
        MOEAIndividual<double[]> ind = input.getIndividual();
        MOEAIndividual<double[]> best = input.getIndividual();
        for (MOEAIndividual<double[]> off : input.getPop()) {
            if(comp.compare(off, best) <= 0) {
                best = off;
            }
        }

        if(best.equals(ind))
        {
            modifyProbabilities(-1, ind.getOpProbabilities(), ind.getSelectedOp());
        }
        else
        {
            if (comp.compare(ind, best) == 0)
                modifyProbabilities(-1, ind.getOpProbabilities(), ind.getSelectedOp());
            else
                modifyProbabilities(1, ind.getOpProbabilities(), ind.getSelectedOp());
        }
        best.setOpProbabilities(ind.getOpProbabilities());
        return best;
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
