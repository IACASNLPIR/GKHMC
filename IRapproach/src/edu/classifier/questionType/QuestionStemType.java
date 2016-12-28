package edu.classifier.questionType;

import edu.main.Const;
import edu.question.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2016/5/23.
 * 题干类型
 */
public class QuestionStemType {
    public static String run(Question question){
        String stem = question.getQuestionStem().trim();
        if (isMeasureSimilarity(stem))
            return Const.Q_T_SIMILARITIES_MEASURE;
        return "";
    }
    static Pattern pattern;
    static Matcher matcher;

    // 相似度计算题 类别太多，考虑先判断是否是其它类型，如果都不是则是该类
    static boolean isMeasureSimilarity(String stem){
        List<String> regexes = new ArrayList<String>(){{
            add("*.反映.*");
            add("*.表明.*");
            add("*.推出.*");
            add("*.原因是.*");
            add("*.因为.*是");
            add("*.应该是");
            add("*.作为.*依据.*");
            add("*.主要指");
            add("*.指的是");
            add("*.的是");
        }
        };
        return regexesMatch(stem, regexes);
    }

    static boolean regexMatch(String stem, String regex){
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(stem);
        return matcher.find();
    }

    static boolean regexesMatch(String stem, List<String> regexes){
        boolean isMatch = false;
        for (String regex : regexes){
            if (regexMatch(stem, regex)){
                isMatch = true;
                break;
            }
        }
        return isMatch;
    }
}
