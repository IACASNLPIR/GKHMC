package edu.classifier.questionType.bayesianQuestionCandidateType;

import edu.main.Const;
import edu.question.Question;
import edu.question.QuestionAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunder on 2016/5/13.
 */
public class Statistics {
    public static void main(String[] args) {
        String filename = "gaokao_all(2011-2015)_data/CountryAllRealHistoryMultipleChoiceQuestion(2011-2015)(744)WithType.xml";
        filename = Const.QUESTIONS_FILEPATH + filename;
        Statistics st = new Statistics(filename);
        Map<String, List<QuestionStatistics>> map = st.run();
//        System.out.println(map.get("entity").get(0).vector);
    }
    QuestionAnalyzer analyzer;
    public Statistics(String questionFilepath) {
        analyzer  = new QuestionAnalyzer();
        analyzer.init(questionFilepath);
    }
    public Statistics(String questionFilepath, String answerFilepath) {
        analyzer  = new QuestionAnalyzer();
        analyzer.init(questionFilepath, answerFilepath);
    }

    Map<String, List<QuestionStatistics>> run(){
        List<QuestionStatistics> entityQuestionStatisticsList = new ArrayList<>();
        List<QuestionStatistics> sentenceQuestionStatisticsList = new ArrayList<>();
        for (Question question : analyzer.acquisition.questionList) {
            QuestionStatistics questionStatistics = statQuestion(question);
            if (question.questionRealType.get(0).equals(Const.Q_T_ENTITY)){
                entityQuestionStatisticsList.add(questionStatistics);
            }
            if (question.questionRealType.get(0).equals(Const.Q_T_SENTENCE)){
                sentenceQuestionStatisticsList.add(questionStatistics);
            }
        }
        Map<String, List<QuestionStatistics>> map = new HashMap<>();
        map.put("entity", entityQuestionStatisticsList);
        map.put("sentence", sentenceQuestionStatisticsList);
        return map;
    }

    public static QuestionStatistics statQuestion(Question question){
        int lengthSum = 0;
        int verbNumSum = 0;
        for (int i = 0; i < 4; i++) {
            lengthSum += question.getCandidates(i).length();
            for (String p : question.getCandidatePOS(i)){
                if (p.startsWith("v")){
                    verbNumSum++;
                }
            }
        }
        QuestionStatistics questionStatistics = new QuestionStatistics();
        questionStatistics.candidateLength = lengthSum / 4.0;
        questionStatistics.candidateVerbNum = verbNumSum / 4.0;
        questionStatistics.vector = questionStatistics.turnToVector();
//        System.out.print(questionStatistics.vector + ", ");
//        System.out.println(question.questionRealType.get(0));
        return questionStatistics;
    }

//    void run(){
//        Map<Integer, Integer> candidateLength = new HashMap<>();
//        Map<Integer, Integer> candidateUserDefineCount = new HashMap<>();
//        Map<Integer, Integer> candidateVerbCount = new HashMap<>();
//        Map<Integer, Integer> candidateFeatureCount = new HashMap<>();
//
//        for (Question question : analyzer.acquisition.questionList) {
//            for (int i = 0; i < 4; i++) {
//                candidateLength(question.getCandidates(i), candidateLength);
//                candidatePos(question.getCandidatePOS(i), candidateUserDefineCount, candidateVerbCount);
////                candidateFeature(question.getCandidates(i), candidateFeatureCount);
//            }
//        }
////        System.out.println("length");
////        showMap(candidateLength);
////        System.out.println("userDefine");
////        showMap(candidateUserDefineCount);
////        System.out.println("verb");
////        showMap(candidateVerbCount);
//        System.out.println("feature");
//        showMap(candidateFeatureCount);
//    }

    void candidateLength(String candidate, Map<Integer, Integer> candidateLength){
        int len = candidate.length();
        int count = candidateLength.getOrDefault(len, 0);
        candidateLength.put(len, count + 1);
    }
    void candidatePos(List<String> pos,
                             Map<Integer, Integer> candidateUserDefineCount, Map<Integer, Integer> candidateVerbCount){
        int userDefineCount = 0;
        int verbCount = 0;
        for (String p : pos){
            if (p.equals("userDefine")){
                userDefineCount++;
            }
            if (p.startsWith("v")){
                verbCount++;
            }
        }
        int times = candidateUserDefineCount.getOrDefault(userDefineCount, 0);
        candidateUserDefineCount.put(userDefineCount, times+1);
        times = candidateVerbCount.getOrDefault(verbCount, 0);
        candidateVerbCount.put(verbCount, times+1);
    }

    void candidateFeature(String candidate, Map<Integer, Integer> candidateFeature){
        String[] seg = candidate.trim().split("》");
        int count = candidateFeature.getOrDefault(seg.length, 0);
        candidateFeature.put(seg.length, count+1);
    }

    void showMap(Map<Integer, Integer> map){
        for (Map.Entry<Integer, Integer> entry : map.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}

class QuestionStatistics{
    // 四个选项的平均值
    double candidateLength = -1;
    double candidateVerbNum = -1;
    List<Integer> vector = new ArrayList<>();

    List<Integer> turnToVector(){
        if (candidateLength == -1 || candidateVerbNum == -1){
            System.out.println("false");
            return null;
        }
        if (candidateLength <= 3)
            vector.add(1);
        else
            vector.add(0);
        if (candidateLength >= 4 && candidateLength <= 6)
            vector.add(1);
        else
            vector.add(0);
        if (candidateLength >= 7 && candidateLength <= 9)
            vector.add(1);
        else
            vector.add(0);
        if (candidateLength >= 10 &&candidateLength <= 12)
            vector.add(1);
        else
            vector.add(0);
        if (candidateLength >= 13)
            vector.add(1);
        else
            vector.add(0);
        //
        if (candidateVerbNum == 0)
            vector.add(1);
        else
            vector.add(0);
        if (candidateVerbNum == 1)
            vector.add(1);
        else
            vector.add(0);
        if (candidateVerbNum == 2)
            vector.add(1);
        else
            vector.add(0);
        if (candidateVerbNum >= 3)
            vector.add(1);
        else
            vector.add(0);

        return vector;
    }
}