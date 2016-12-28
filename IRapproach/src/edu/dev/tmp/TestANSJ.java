package edu.dev.tmp;

import edu.tools.ANSJSEG;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.util.List;

/**
 * Created by shawn on 15-12-25.
 */

public class TestANSJ {
    public static void main(String[] args) {
        List<Term> parse = NlpAnalysis.parse("东汉的班超曾经出使西域，唐还有文成公主入藏。");
        for (Term term : parse) {
            System.out.println(term.toString());
        }

        List<Term> baseParse = ANSJSEG.seg("东汉的班超曾经出使西域，唐还有文成公主入藏。");
        for (Term term : baseParse) {
            System.out.println(term.toString());
        }
    }
}
