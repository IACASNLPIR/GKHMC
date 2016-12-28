package edu.tools;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import org.ansj.domain.Term;

import java.util.List;

/**
 * Author:             Shawn Guo
 * E-mail:             air_fighter@163.com
 *
 * Create Time:        2015/12/25 11:07
 * Last Modified Time: 2015/12/25
 *
 * Class Name:         NER
 * Class Function:
 *                     该类使用Stanford的NER对输入的字符串中的命名实体进行识别。其中输入为已经使用空格间隔
 *                     好的字符串，如“刘康 是 中国科学院 自动化研究所 帅哥 副研究员 。 ”，返回字符串在每个
 *                     分词后面标有实体类型，如“刘康/PERSON 是/O 中国科学院/ORG 自动化研究所/ORG 帅哥/O
 *                      副研究员/O 。/O”。
 */

public class NER {
    private static AbstractSequenceClassifier<CoreLabel> ner;

    public NER() {
        initNER();
    }

    public void initNER() {
        String serializedClassifier = "data/chinese.misc.distsim.crf.ser.gz";
        if (ner == null) {
            ner = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
        }
    }

    public String doNER(String input) {
        return ner.classifyToString(input);
        //return ner.classifyWithInlineXML(input);
    }

    public String doNER(List<Term> input) {
        String nerInput = "";
        for (Term term : input) {
            nerInput += term.toString().split("/")[0];
            nerInput += " ";
        }
        String ret = ner.classifyToString(nerInput);
        return ret;
    }

    public static void main(String[] args) {
        String str = "刘康 是 中国科学院 自动化研究所 帅哥 副研究员 。 ";
        NER ner = new NER();
        System.out.println(ner.doNER(str));
    }
}
