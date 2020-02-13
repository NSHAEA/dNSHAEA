package co.com.jccp.ealgorithms.function;


public interface ObjectiveFunction<T> {

    double[] apply(T individual);

    double[][] optimal(int totalPoints);

    int getNObjectives();

}
