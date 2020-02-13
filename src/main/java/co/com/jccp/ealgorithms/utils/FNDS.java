package co.com.jccp.ealgorithms.utils;

import co.com.jccp.ealgorithms.individual.MOEAIndividual;

import java.util.ArrayList;
import java.util.List;


public class FNDS {
    public static <T> List<List<MOEAIndividual<T>>> apply(List<MOEAIndividual<T>> individuals, boolean minimize)
    {
        int n = individuals.size();
        List<List<MOEAIndividual<T>>> f = new ArrayList<>(n);
        List<MOEAIndividual<T>> f1 = new ArrayList<>();
        for (MOEAIndividual<T> p : individuals) {
            List<MOEAIndividual<T>> sp = new ArrayList<>();
            int np = 0;
            for (MOEAIndividual q : individuals) {
                if (ParetoUtils.dominates(p, q, minimize))
                    sp.add(q);
                else if (ParetoUtils.dominates(q, p, minimize))
                    np++;
            }
            if(np == 0)
            {
                p.setParetoRank(0);
                f1.add(p);
            }
            p.setDominatesMe(np);
            p.setDominatedByMe(sp);
        }
        f.add(f1);
        int k = 0;
        while(!f.get(k).isEmpty())
        {
            List<MOEAIndividual<T>> qq = new ArrayList<>();
            for (MOEAIndividual<T> p : f.get(k)) {
                for (Object qo : p.getDominatedByMe()) {
                    MOEAIndividual q = (MOEAIndividual) qo;
                    q.setDominatesMe(q.getDominatesMe() - 1);
                    if(q.getDominatesMe() == 0)
                    {
                        q.setParetoRank(k + 1);
                        qq.add(q);
                    }
                }
            }
            k++;
            f.add(qq);
        }
        f.remove(f.size() - 1);
        return f;
    }



}
