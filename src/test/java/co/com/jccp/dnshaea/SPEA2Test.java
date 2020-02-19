package co.com.jccp.dnshaea;

import co.com.jccp.dnshaea.algorithm.SPEA2;
import co.com.jccp.dnshaea.function.*;
import co.com.jccp.dnshaea.gop.PolynomialMutation;
import co.com.jccp.dnshaea.gop.SimulatedBinaryXover;
import co.com.jccp.dnshaea.individual.MOEAIndividual;
import co.com.jccp.dnshaea.initialization.RandomRealInitialization;
import co.com.jccp.dnshaea.metrics.Convergence;
import co.com.jccp.dnshaea.metrics.Diversity;
import co.com.jccp.dnshaea.utils.RealCloneUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class SPEA2Test {

    @Test
    public void spea2Test() throws IOException {

//        SCH function = new SCH();
//        int dimensions = 1;
//        double[][] limits = new double[][]{{-1000, 1000}};
//        String fileName = "SPEA2-SCH";
//        double[] xlim = new double[]{0, 4};
//        double[] ylim = new double[]{0, 4};

//        FON function = new FON();
//        int dimensions = 3;
//        double[][] limits = new double[][]{{-4, 4}, {-4, 4}, {-4, 4}};
//        String fileName = "SPEA2-FON";
//        double[] xlim = new double[]{0, 1};
//        double[] ylim = new double[]{0, 1};

        ZDT1 function1 = new ZDT1();
        int dimensions = 30;
        double[][] limits = new double[dimensions][2];

        for (int i = 0; i < dimensions; i++) {
            limits[i][0] = 0.0;
            limits[i][1] = 1.0;
        }
        String fileName = "SPEA2-ZDT1";
        double[] xlim = new double[]{0, 1};
        double[] ylim = new double[]{0, 1};

        FunctionContainer zdt1 = new FunctionContainer(function1, dimensions, limits, fileName, xlim, ylim);

        ZDT2 function2 = new ZDT2();
        dimensions = 30;
        limits = new double[dimensions][2];

        for (int i = 0; i < dimensions; i++) {
            limits[i][0] = 0.0;
            limits[i][1] = 1.0;
        }
        fileName = "SPEA2-ZDT2";
        xlim = new double[]{0, 1};
        ylim = new double[]{0, 1};

        FunctionContainer zdt2 = new FunctionContainer(function2, dimensions, limits, fileName, xlim, ylim);

        ZDT3 function3 = new ZDT3();
        dimensions = 30;
        limits = new double[dimensions][2];

        for (int i = 0; i < dimensions; i++) {
            limits[i][0] = 0.0;
            limits[i][1] = 1.0;
        }
        fileName = "SPEA2-ZDT3";
        xlim = new double[]{0, 1};
        ylim = new double[]{0, 1};
        FunctionContainer zdt3 = new FunctionContainer(function3, dimensions, limits, fileName, xlim, ylim);

        ZDT4 function4 = new ZDT4();
        dimensions = 10;
        limits = new double[dimensions][2];

        limits[0][0] = 0.0;
        limits[0][1] = 1.0;

        for (int i = 1; i < dimensions; i++) {
            limits[i][0] = -5.0;
            limits[i][1] = 5.0;
        }
        fileName = "SPEA2-ZDT4";
        xlim = new double[]{0, 1};
        ylim = new double[]{0, 2};
        FunctionContainer zdt4 = new FunctionContainer(function4, dimensions, limits, fileName, xlim, ylim);

        ZDT6 function6 = new ZDT6();
        dimensions = 10;
        limits = new double[dimensions][2];

        for (int i = 0; i < dimensions; i++) {
            limits[i][0] = 0.0;
            limits[i][1] = 1.0;
        }
        fileName = "SPEA2-ZDT6";
        xlim = new double[]{0, 1};
        ylim = new double[]{0, 2};
        FunctionContainer zdt6 = new FunctionContainer(function6, dimensions, limits, fileName, xlim, ylim);


        int RUNSAMPLES = 100;
        DoubleIndex[] converg = new DoubleIndex[RUNSAMPLES];
        double[] diversity = new double[RUNSAMPLES];

        FunctionContainer[] functions = new FunctionContainer[]{zdt1,zdt2,zdt3,zdt4,zdt6};

        for (FunctionContainer f : functions) {
            List<List<String>> data = new ArrayList<>();
            FileWriter fw;
            try {
                fw = new FileWriter(new File(f.getFileName() + ".txt"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            for (int ii = 0; ii < RUNSAMPLES; ii++) {

                RandomRealInitialization init = new RandomRealInitialization(f.getLimits());

                boolean minimize = true;

                int popSize = 100;

                double mutProb = 1;

                double xoverProb = 0.9;

                int iterations = 250;

                SimulatedBinaryXover sbx = new SimulatedBinaryXover(10.0, f.getLimits());

                PolynomialMutation pm = new PolynomialMutation(10.0, f.getLimits());

                RealCloneUtil rcu = new RealCloneUtil();

                int archiveSize = 100;

                SPEA2<double[]> spea2 = new SPEA2<>(init, f.getFunction(), minimize, f.getDimensions(), popSize, mutProb, xoverProb, iterations, sbx, pm, rcu, archiveSize);

                List<MOEAIndividual<double[]>> answer = spea2.apply();

                double[] x = new double[answer.size()];
                double[] y = new double[answer.size()];

                double[] xopt = new double[500];
                double[] yopt = new double[500];

                String index = ii + "\n";
                try {
                    fw.append(index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<String> tt = new LinkedList<>();
                for (int i = 0; i < answer.size(); i++) {
                    x[i] = answer.get(i).getObjectiveValues()[0];
                    y[i] = answer.get(i).getObjectiveValues()[1];
                    try {
                        String point = x[i] + "," + y[i] + "\n";
                        fw.append(point);
                        tt.add(point);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                data.add(tt);

                double[][] opt = f.getFunction().optimal(500);

                for (int i = 0; i < 500; i++) {
                    xopt[i] = opt[i][0];
                    yopt[i] = opt[i][1];
                }

                converg[ii] = new DoubleIndex(Convergence.calculate(new double[][]{x, y}, new double[][]{xopt, yopt}), ii);
                diversity[ii] = Diversity.calculate(new double[][]{x, y}, new double[][]{xopt, yopt});

//                MatlabChart fig = new MatlabChart(); // figure('Position',[100 100 640 480]);
//                fig.plot(x, y, ".r", 2.0f, "Found"); // plot(x,y1,'-r','LineWidth',2);
//                fig.plot(xopt, yopt, "-b", 1.0f, "Optimal"); // plot(x,y1,'-r','LineWidth',2);
//                fig.RenderPlot();                    // First render plot before modifying
//                fig.title(fileName);    // title('Stock 1 vs. Stock 2');
//                fig.xlim(xlim[0], xlim[1]);                   // xlim([10 100]);
//                fig.ylim(ylim[0], ylim[1]);                  // ylim([200 300]);
//                fig.xlabel("Objective 1");                  // xlabel('Days');
//                fig.ylabel("Objective 2");                 // ylabel('Price');
//                fig.grid("on", "on");                 // grid on;
//                fig.legend("northeast");             // legend('AAPL','BAC','Location','northeast')
//                fig.font("Helvetica", 15);            // .. 'FontName','Helvetica','FontSize',15
//                fig.saveasSVG("SPEA2/" + fileName + "-" + ii + ".svg", 640, 640);   // saveas(gcf,'MyPlot','jpeg');

            }

            fw.close();

            double meanConv = 0.0;
            double meanDivers = 0.0;
            for (int i = 0; i < RUNSAMPLES; i++) {
                meanConv += converg[i].getD();
                meanDivers += diversity[i];
            }
            meanConv /= RUNSAMPLES;
            meanDivers /= RUNSAMPLES;
            double medianConv;
            double medianDivers;
            Arrays.sort(converg, Comparator.comparingDouble(DoubleIndex::getD));
            Arrays.sort(diversity);
            DoubleIndex dMedian = converg[(RUNSAMPLES / 2) + 1];
            if(RUNSAMPLES % 2 == 1) {

                medianConv = dMedian.getD();
                medianDivers = diversity[(RUNSAMPLES / 2) + 1];
            }
            else {
                medianConv = (converg[RUNSAMPLES / 2].getD() + converg[(RUNSAMPLES / 2) + 1].getD()) / 2.0;
                medianDivers = (diversity[RUNSAMPLES / 2] + diversity[(RUNSAMPLES / 2) + 1]) / 2.0;
            }

            double varConv = 0.0;
            double varDivers = 0.0;

            for (int i = 0; i < RUNSAMPLES; i++) {
                varConv += (converg[i].getD() - meanConv) * (converg[i].getD() - meanConv);
                varDivers += (diversity[i] - meanDivers) * (diversity[i] - meanDivers);
            }

            varConv /= (RUNSAMPLES - 1.0);
            varDivers /= (RUNSAMPLES - 1.0);

            FileWriter fw2;
            try {
                fw2 = new FileWriter(new File(f.getFileName() + "-MAINDATA.txt"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            for (String s : data.get(converg[0].getIndex())) {
                try {
                    fw2.append(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (String s : data.get(dMedian.getIndex())) {
                try {
                    fw2.append(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (String s : data.get(converg[converg.length - 1].getIndex())) {
                try {
                    fw2.append(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println("*******************************************************");
            System.out.println(f.getFileName());
            System.out.println();
            System.out.println("MEAN CONVERGENCE: " + meanConv);
            System.out.println("MEDIAN CONVERGENCE: " + medianConv);
            System.out.println("DEVIATION CONVERGENCE: " + varConv);
            System.out.println();
            System.out.println("MEAN DIVERSITY: " + meanDivers);
            System.out.println("MEDIAN DIVERSITY: " + medianDivers);
            System.out.println("DEVIATION DIVERSITY: " + varDivers);
            System.out.println();
            System.out.println("BEST CONVERGENCE: " + converg[0].getIndex() + " : " + converg[0].getD());
            System.out.println("MEDIAN CONVERGENCE: " + dMedian.getIndex() + " : " + dMedian.getD());
            System.out.println("WORST CONVERGENCE: " + converg[converg.length - 1].getIndex() + " : " + converg[converg.length - 1].getD());

            FileWriter ff = new FileWriter(f.getFileName() + "-converg.txt");
            for (DoubleIndex doubleIndex : converg) {
                ff.append(String.valueOf(doubleIndex.getD())).append("\n");
            }
            ff.close();




        }







    }


}
