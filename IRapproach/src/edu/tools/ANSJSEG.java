package edu.tools;

import com.shawn.BasicIO;
import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shawn on 16-1-6.
 */
public class ANSJSEG {

    private static ArrayList<String>  stopWordSet = new ArrayList<>();
    private static boolean builtStopWordSet = false;

    public static void buildStopWordSet(String fileName) {
        try {
            stopWordSet = BasicIO.readFile2StringArray(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static List<Term> seg(String input) {
        if (!builtStopWordSet) {
            buildStopWordSet("data/dicts/stopwords_cn.txt");
            builtStopWordSet = true;
        }

        List<Term> retSet = new ArrayList<>();

        List<Term> parse = NlpAnalysis.parse(input);
        new NatureRecognition(parse).recognition();
        FilterModifWord.modifResult(parse);

        for (Term term : parse) {
            if (term.toString().contains("/") && term.toString().length() >=3 &&
                    !stopWordSet.contains(term.toString().split("/")[0])) {
                retSet.add(term);
            }
        }

        return retSet;
    }

    public static String segStr2Str(String input) {
        if (!builtStopWordSet) {
            buildStopWordSet("data/dicts/stopwords_cn.txt");
            builtStopWordSet = true;
        }

        List<Term> retSet = new ArrayList<>();

        List<Term> parse = NlpAnalysis.parse(input);
        new NatureRecognition(parse).recognition();
        FilterModifWord.modifResult(parse);

        for (Term term : parse) {
            if (term.toString().contains("/") && term.toString().length() >=3 &&
                    !stopWordSet.contains(term.toString().split("/")[0])) {
                retSet.add(term);
            }
        }

        String retStr = "";
        for (Term term : retSet) {
            retStr += term.toString().split("/")[0];
            if (retSet.indexOf(term) != retSet.size() - 1) {
                retStr += " ";
            }
        }
        return retStr;
    }
}
