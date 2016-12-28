package edu.tools;

import java.io.*;
import java.util.List;

import org.ansj.domain.Term;

/**
 * Created by shawn on 16-1-12.
 */
public class SegCorpus {
    public void segCorpus(String inputFileName, String outputFileName)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        File inputFile = new File(inputFileName);
        FileInputStream inputStream = new FileInputStream(inputFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        File outputFile = new File(outputFileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        PrintStream printer = new PrintStream(outputStream);

        String lineString = "";

        int num = 0;
        int num2 = 0;

        while ( (lineString = reader.readLine()) != null) {
            num++;
            if (num == 1000) {
                num2++;
                if (num2 == 100) {
                    System.out.println();
                    num2 =0;
                }
                System.out.print(".");
                num = 0;
            }

            List<Term> segOutput = ANSJSEG.seg(lineString);
            String outString = "";
            for (Term item : segOutput) {
                if (item.toString().contains("/") && !item.toString().equals("/")) {
                    outString += item.toString().split("/")[0] + " ";
                }
            }

            if (outString.length() >= 1) {
                printer.println(outString);
            }
        }
    }

    public static void main(String[] args) {
        SegCorpus self = new SegCorpus();
        try {
            self.segCorpus("out/baike-all.txt", "out/seg-baike-all.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
