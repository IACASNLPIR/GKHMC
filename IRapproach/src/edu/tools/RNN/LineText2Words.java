package edu.tools.RNN;

import com.shawn.BasicIO;
import edu.tools.ANSJSEG;
import org.ansj.domain.Term;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shawn on 16-3-17.
 */
public class LineText2Words {
    public static void textFileProcess(String inputFile, String outputFile) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            lines = BasicIO.readFile2StringArray(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Read " + lines.size() + "lines.");

        File outFile = new File(outputFile);
        try {

            FileOutputStream e = new FileOutputStream(outFile);
            PrintStream printStream = new PrintStream(e);

            for (String line : lines) {
                List<Term> terms = ANSJSEG.seg(line);
                String outLine = "";
                for (Term term : terms) {
                    printStream.print(term.toString().split("/")[0] + " ");
                }
                printStream.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String inputFile = "data/textbook_main.txt";
        String outputFile = "out/textbook_out.txt";

        textFileProcess(inputFile, outputFile);
    }
}
