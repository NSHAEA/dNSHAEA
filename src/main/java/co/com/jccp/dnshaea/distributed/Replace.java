package co.com.jccp.dnshaea.distributed;

import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.utils.RandomUtils;

import java.util.Comparator;
import java.util.List;

/**
 * Created by: Juan Camilo Castro Pinto
 **/
public class Replace<T> implements Runnable {

    List<MOEAIndividual<T>> pop;
    MOEAIndividual<T> ind;
    Comparator<MOEAIndividual<T>> comp;
    MOEAIndividual<T> best;

    public Replace(List<MOEAIndividual<T>> pop, MOEAIndividual<T> ind, Comparator<MOEAIndividual<T>> comp)
    {
        this.pop = pop;
        this.ind = ind;
        this.best = ind;
        this.comp = comp;
    }


    @Override
    public void run() {

        for (MOEAIndividual<T> off : ind.getOffspring()) {
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

    public MOEAIndividual<T> getBest() {
        return best;
    }
}
