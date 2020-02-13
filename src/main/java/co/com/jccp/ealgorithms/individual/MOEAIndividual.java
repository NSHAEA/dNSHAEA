package co.com.jccp.ealgorithms.individual;

import java.util.List;


public class MOEAIndividual<T> {

    private T data;
    private int selectedOp;
    private int paretoRank;
    private double diversityMeasure;
    private double fitness;
    private double[] objectiveValues;
    private List<MOEAIndividual<T>> dominatedByMe;
    private List<MOEAIndividual<T>> offspring;
    private double[] opProbabilities;
    private int dominatesMe;

    public int getParetoRank() {
        return paretoRank;
    }

    public void setParetoRank(int paretoRank) {
        this.paretoRank = paretoRank;
    }

    public double getDiversityMeasure() {
        return diversityMeasure;
    }

    public void setDiversityMeasure(double diversityMeasure) {
        this.diversityMeasure = diversityMeasure;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double[] getObjectiveValues() {
        return objectiveValues;
    }

    public void setObjectiveValues(double[] objectiveValues) {
        this.objectiveValues = objectiveValues;
    }

    public List<MOEAIndividual<T>> getDominatedByMe() {
        return dominatedByMe;
    }

    public void setDominatedByMe(List<MOEAIndividual<T>> dominatedByMe) {
        this.dominatedByMe = dominatedByMe;
    }

    public int getDominatesMe() {
        return dominatesMe;
    }

    public void setDominatesMe(int dominatesMe) {
        this.dominatesMe = dominatesMe;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int compareByRank(MOEAIndividual<T> o)
    {
        return Integer.compare(this.getParetoRank(), o.getParetoRank());
    }

    public int compareByDiversity(MOEAIndividual<T> o)
    {
        return Double.compare(this.getDiversityMeasure(), o.getDiversityMeasure());
    }

    public int compareByFitness(MOEAIndividual<T> o)
    {
        return Double.compare(this.getFitness(), o.getFitness());
    }

    public List<MOEAIndividual<T>> getOffspring() {
        return offspring;
    }

    public void setOffspring(List<MOEAIndividual<T>> offspring) {
        this.offspring = offspring;
    }

    public double[] getOpProbabilities() {
        return opProbabilities;
    }

    public void setOpProbabilities(double[] opProbabilities) {
        this.opProbabilities = opProbabilities;
    }

    public int getSelectedOp() {
        return selectedOp;
    }

    public void setSelectedOp(int selectedOp) {
        this.selectedOp = selectedOp;
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
