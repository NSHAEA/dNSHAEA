package co.com.jccp.ealgorithms.utils;

import co.com.jccp.ealgorithms.individual.MOEAIndividual;


public class ParetoUtils {

    public static boolean dominates(MOEAIndividual i1, MOEAIndividual i2)
    {
        return dominates(i1, i2, true);
    }

    public static boolean dominates(MOEAIndividual i1, MOEAIndividual i2, boolean minimize)
    {
        int n = i1.getObjectiveValues().length;
        for (int i = 0; i < n; i++) {

            try {

                if (minimize && i1.getObjectiveValues()[i] > i2.getObjectiveValues()[i])
                    return false;
                else if (!minimize && i1.getObjectiveValues()[i] < i2.getObjectiveValues()[i])
                    return false;
            } catch(Exception e)
            {
                System.out.println();
            }

        }
        return true;
    }



}
