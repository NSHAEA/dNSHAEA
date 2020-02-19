package co.com.jccp.dnshaea.functions;

import co.com.jccp.dnshaea.function.*;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FunctionsTest {

    @Test
    public void functionTest() throws IOException {
        ZDT1 z1 = new ZDT1();
        ZDT2 z2 = new ZDT2();
        ZDT3 z3 = new ZDT3();
        ZDT4 z4 = new ZDT4();
        ZDT6 z6 = new ZDT6();

        double[][] o = z1.optimal(500);
        FileWriter fw = new FileWriter(new File("ZDT1_OPT.txt"));
        for (int i = 0; i < o.length; i++) {
            fw.append(String.valueOf(o[i][0])).append(",").append(String.valueOf(o[i][1])).append("\n");
        }
        fw.close();

        o = z2.optimal(500);
        fw = new FileWriter(new File("ZDT2_OPT.txt"));

        for (int i = 0; i < o.length; i++) {
            fw.append(String.valueOf(o[i][0])).append(",").append(String.valueOf(o[i][1])).append("\n");
        }
        fw.close();

        o = z3.optimal(500);
        fw = new FileWriter(new File("ZDT3_OPT.txt"));

        for (int i = 0; i < o.length; i++) {
            fw.append(String.valueOf(o[i][0])).append(",").append(String.valueOf(o[i][1])).append("\n");
        }
        fw.close();

        o = z4.optimal(500);
        fw = new FileWriter(new File("ZDT4_OPT.txt"));

        for (int i = 0; i < o.length; i++) {
            fw.append(String.valueOf(o[i][0])).append(",").append(String.valueOf(o[i][1])).append("\n");
        }
        fw.close();

        o = z6.optimal(500);
        fw = new FileWriter(new File("ZDT6_OPT.txt"));

        for (int i = 0; i < o.length; i++) {
            fw.append(String.valueOf(o[i][0])).append(",").append(String.valueOf(o[i][1])).append("\n");
        }
        fw.close();








    }

}
