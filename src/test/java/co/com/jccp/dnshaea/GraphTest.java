package co.com.jccp.dnshaea;

import co.com.jccp.dnshaea.graphs.MatlabChart;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;


public class GraphTest {

    @Test
    public void graphTest() throws IOException {

        List<String> lines = Files.readAllLines(new File("KUR.txt").toPath(), StandardCharsets.UTF_8);

        double[] x = new double[lines.size()];
        double[] y1 = new double[lines.size()];
        for (int i = 0; i < lines.size(); i++) {

            String[] ss = lines.get(i).split(" ");
            x[i] = Double.parseDouble(ss[0]);
            y1[i] = Double.parseDouble(ss[1]);


        }



//        // Create some sample data
//        double[] x = new double[100]; x[0] = 1;
//        double[] y1 = new double[100]; y1[0] = 200;
//        double[] y2 = new double[100]; y2[0] = 300;
//        for(int i = 1; i < x.length; i++){
//            x[i] = i+1;
//            y1[i] = y1[i-1] + Math.random()*10 - 4;
//            y2[i] = y2[i-1] + Math.random()*10 - 6;
//        }
//
        // JAVA:                             // MATLAB:
        MatlabChart fig = new MatlabChart(); // figure('Position',[100 100 640 480]);
        fig.plot(x, y1, "-r", 2.0f, "AAPL"); // plot(x,y1,'-r','LineWidth',2);
//        fig.plot(x, y2, ":k", 3.0f, "BAC");  // plot(x,y2,':k','LineWidth',3);
        fig.RenderPlot();                    // First render plot before modifying
        fig.title("Stock 1 vs. Stock 2");    // title('Stock 1 vs. Stock 2');
        fig.xlim(-25, 5);                   // xlim([10 100]);
        fig.ylim(-25, 5);                  // ylim([200 300]);
        fig.xlabel("Days");                  // xlabel('Days');
        fig.ylabel("Price");                 // ylabel('Price');
        fig.grid("on","on");                 // grid on;
        fig.legend("northeast");             // legend('AAPL','BAC','Location','northeast')
        fig.font("Helvetica",15);            // .. 'FontName','Helvetica','FontSize',15
        fig.saveas("MyPlot.jpeg",640,480);   // saveas(gcf,'MyPlot','jpeg');
    }


}
