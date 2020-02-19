package co.com.jccp.dnshaea.function;


public interface ObjectiveFunction<T> {

    double[] apply(T individual);

    double[][] optimal(int totalPoints);

    int getNObjectives();

}
