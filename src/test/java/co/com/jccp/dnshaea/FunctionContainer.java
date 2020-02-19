package co.com.jccp.dnshaea;

import co.com.jccp.dnshaea.function.ObjectiveFunction;

public class FunctionContainer {

    private ObjectiveFunction<double[]> function;
    private int dimensions;
    private double[][] limits;
    private String fileName;
    private double[] xlim;
    private double[] ylim;

    public FunctionContainer(ObjectiveFunction<double[]> function, int dimensions, double[][] limits, String fileName, double[] xlim, double[] ylim) {
        this.function = function;
        this.dimensions = dimensions;
        this.limits = limits;
        this.fileName = fileName;
        this.xlim = xlim;
        this.ylim = ylim;
    }

    public ObjectiveFunction<double[]> getFunction() {
        return function;
    }

    public void setFunction(ObjectiveFunction<double[]> function) {
        this.function = function;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public double[][] getLimits() {
        return limits;
    }

    public void setLimits(double[][] limits) {
        this.limits = limits;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double[] getXlim() {
        return xlim;
    }

    public void setXlim(double[] xlim) {
        this.xlim = xlim;
    }

    public double[] getYlim() {
        return ylim;
    }

    public void setYlim(double[] ylim) {
        this.ylim = ylim;
    }
}
