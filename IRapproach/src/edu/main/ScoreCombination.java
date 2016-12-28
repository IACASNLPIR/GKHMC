package edu.main;

import edu.classifier.questionType.bayesianQuestionCandidateType.QuestionTypeNBC;
import edu.question.Question;
import edu.question.QuestionAnalyzer;
import edu.scores.entityCooccurrence.CooccurScore;
import edu.scores.entityLinks.LinkScore;
import edu.scores.searchScore.SearchScore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/5/25.
 * 调用各个打分函数并计算得分
 */
public class ScoreCombination {
    public static Logger logger = LogManager.getLogger(ScoreCombination.class);
    public static void main(String[] args) {
        Const.init();
        String filename;
        String answerFilename = Const.ANSWER_FILENAME;
        if (args.length == 0) {
            filename = Const.QUESTION_FILENAME;
        }else {
            filename = args[0];
        }

        String outScoreFilename = Const.QUESTION_SCORE_FILEPATH;
        System.out.println(outScoreFilename);
        QuestionFilter questionFilter = new QuestionFilter();
        List<String> allowedQuestionCandidateType = new ArrayList<>();
        allowedQuestionCandidateType.add(Const.Q_T_ENTITY);
        allowedQuestionCandidateType.add(Const.Q_T_SENTENCE);

        questionFilter.setQuestionFilter(allowedQuestionCandidateType);

        ScoreCombination scoreCombination = new ScoreCombination();
        QuestionAnalyzer analyzer;
        if (Const.IS_XUNFEI_TYPE){
            analyzer = scoreCombination.init(filename, answerFilename, questionFilter);
        }else{
            analyzer = scoreCombination.init(filename, questionFilter);
        }
        List<QuestionScores> allQuestionScores = scoreCombination.iterateQuestionGetScores(analyzer,
                Const.SCORE_FUNC_CODE,
                outScoreFilename);
    }

    QuestionFilter questionFilter;
    public QuestionAnalyzer init(String xmlFilename, QuestionFilter questionFilter){
        this.questionFilter = questionFilter;
        QuestionAnalyzer analyzer = new QuestionAnalyzer();
        analyzer.init(xmlFilename);
        return analyzer;
    }
    public QuestionAnalyzer init(String xmlFilename, String answerFilename, QuestionFilter questionFilter){
        this.questionFilter = questionFilter;
        QuestionAnalyzer analyzer = new QuestionAnalyzer();
        analyzer.init(xmlFilename, answerFilename);
        return analyzer;
    }


    public List<QuestionScores> iterateQuestionGetScores(QuestionAnalyzer analyzer,
                                                         int ScoreFunctionCode,
                                                         String outScoreFilename){
        PrintWriter outWriter = null;
        try {
            outWriter = new PrintWriter(outScoreFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ScoreFunctionFilter scoreFunctionFilter = new ScoreFunctionFilter();
        scoreFunctionFilter.setCode(ScoreFunctionCode);
        SearchScore searchScore = new SearchScore();
        CooccurScore cooccurScore = new CooccurScore();
        LinkScore linkScore = null;
        List<QuestionScores> questionScoresList = new ArrayList<>();
        int count = 0;
        for (Question question : analyzer.acquisition.questionList) {
            try {
                QuestionScores questionScores = new QuestionScores();
                System.out.println(count + "\tID: " + question.getID()); count++;
                // 打分函数
                useScoreFunctions(searchScore, cooccurScore, linkScore, scoreFunctionFilter, questionScores, question);
                // 固定信息
                double[] targetScore = targetScore(question.getAnswer());
                questionScores.setTargetScore(targetScore);
                questionScores.setId(question.getID());
                questionScores.setCorrectAnswer(question.getAnswer());
                questionScores.setCandidatePredictType(QuestionTypeNBC.run(question));
                questionScoresList.add(questionScores);

                saveScores(questionScores, outWriter);
                outWriter.flush();
            }catch (Exception e){
                logger.error("some error");
                e.printStackTrace();
            }
        }

        outWriter.close();
        return questionScoresList;
    }

    void useScoreFunctions(SearchScore searchScore,
                           CooccurScore cooccurScore,
                           LinkScore linkScore,
                           ScoreFunctionFilter scoreFunctionFilter,
                           QuestionScores questionScores,
                           Question question){
        if (scoreFunctionFilter.getAllowedFunction().contains(Const.BAIKE_DOC_SEARCHER_NAME)) {
            searchScore.setSearcherName(Const.BAIKE_DOC_SEARCHER_NAME);
            double[] prob1 = searchScore.getScore(question);
            questionScores.scores.add(prob1);
        }
        if (scoreFunctionFilter.getAllowedFunction().contains(Const.BAIKE_PARA_SEARCHER_NAME)) {
            searchScore.setSearcherName(Const.BAIKE_PARA_SEARCHER_NAME);
            double[] prob2 = searchScore.getScore(question);
            questionScores.scores.add(prob2);
        }
        if (scoreFunctionFilter.getAllowedFunction().contains(Const.BAIKE_SENT_SEARCHER_NAME)) {
            searchScore.setSearcherName(Const.BAIKE_SENT_SEARCHER_NAME);
            double[] prob3 = searchScore.getScore(question);
            questionScores.scores.add(prob3);
        }
        if (scoreFunctionFilter.getAllowedFunction().contains(Const.CO_OCCUR_DOC_SEARCHER_NAME)) {
            cooccurScore.setSearcherCode(0);
            double[] prob4 = cooccurScore.getScore(question);
            questionScores.scores.add(prob4);
        }
        if (scoreFunctionFilter.getAllowedFunction().contains(Const.CO_OCCUR_PARA_SEARCHER_NAME)) {
            cooccurScore.setSearcherCode(1);
            double[] prob5 = cooccurScore.getScore(question);
            questionScores.scores.add(prob5);
        }
        if (scoreFunctionFilter.getAllowedFunction().contains(Const.CO_OCCUR_SENT_SEARCHER_NAME)) {
            cooccurScore.setSearcherCode(2);
            double[] prob6 = cooccurScore.getScore(question);
            questionScores.scores.add(prob6);
        }
        if (scoreFunctionFilter.getAllowedFunction().contains(Const.BAIKE_LINK_SEARCHER_NAME)) {
            double[] prob7 = linkScore.getScore(question);
            questionScores.scores.add(prob7);
        }
    }

    double[] targetScore(String correctAnswer){
        switch (correctAnswer){
            case "A":
                return new double[]{1, 0, 0, 0};
            case "B":
                return new double[]{0, 1, 0, 0};
            case "C":
                return new double[]{0, 0, 1, 0};
            case "D":
                return new double[]{0, 0, 0, 1};
        }
        return new double[]{0, 0, 0, 0};
    }

    void saveScores(QuestionScores questionScores, PrintWriter output){
        output.printf("#ID:%s\tCandidateType:%s\tPredictType:%s\n",
                questionScores.getId(),
                questionScores.getCandidateRealType(),
                questionScores.getCandidatePredictType());
        for (double[] score : questionScores.scores){
            saveArray(score, output);
        }
        saveArray(questionScores.getTargetScore(), output);
    }
//
//    void saveScores(List<QuestionScores> questionScoresList, String outFilename) throws FileNotFoundException {
//        PrintWriter output = new PrintWriter(outFilename);
//        int count = (int) questionScoresList.stream()
//                .map(questionScores -> {
//                    output.printf("#ID:%s\tCandidateType:%s\n", questionScores.getId(), questionScores.getCandidateRealType());
//                    saveArray(questionScores.getBaikeDocScore(), output);
//                    saveArray(questionScores.getBaikeParaScore(), output);
//                    saveArray(questionScores.getBaikeSentScore(), output);
//                    saveArray(questionScores.getCooccurDocScore(), output);
//                    saveArray(questionScores.getCooccurParaScore(), output);
//                    saveArray(questionScores.getCooccurSentScore(), output);
//                    saveArray(questionScores.getTargetScore(), output);
//                    return null;
//                })
//                .count();
//        output.close();
//        System.out.printf("save %d questions' score to file\n", count);
//    }

    void saveArray(double[] arr, PrintWriter output){
        for (double value : arr){
            output.print(value + "\t");
        }
        output.println();
    }
}
