package edu.main;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sunder on 2016/5/26.
 * 根据各个打分来估计答案
 */
public class AnswerPredictor {
    public static Logger logger = LogManager.getLogger(AnswerPredictor.class);

    List<Double> entityWeights = new ArrayList<Double>(){
        {
            //entity
//            add(3.78909254  );
//            add(4.06376966  );
//            add(3.02978592  );
//            add(0.95314025  );
//            add(0.89601003  );
//            add(0.86857663  );
//            add(0.28380541  );

            add(0.9);
//            add(0.05);
//            add(0.05);
        }};
    List<Double> sentenceWeights = new ArrayList<Double>(){
        {
            //sentence
//            add(0.45741502  );
//            add(0.39178998  );
//            add(0.31261236  );
//            add(0.72477774  );
//            add(0.80059729  );
//            add(0.94102394  );
//            add(0.63338021  );

            add(1.0);
//            add(0.8);
//            add(0.05);
        }};

    PrintWriter printWriter;
    public static void main(String[] args) {
        AnswerPredictor answerPredictor = new AnswerPredictor();
        // init
        answerPredictor.init();
//        QuestionFilter questionFilter = new QuestionFilter();
//        List<String> allowedQuestionCandidateType = new ArrayList<>();
//        allowedQuestionCandidateType.add(Const.Q_T_ENTITY);
//        questionFilter.setQuestionFilter(allowedQuestionCandidateType);

        System.out.println("获取各题打分");
        String scoreFilename = Const.QUESTION_SCORE_FILEPATH;
        List<QuestionScores> allQuestionScores = answerPredictor.getQuestionScores(scoreFilename);
        System.out.println("预测答案");
        try {
            answerPredictor.printWriter = new PrintWriter(new File(Const.ENTITY_FINAL_SCORE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        answerPredictor.predict(allQuestionScores, false);
        answerPredictor.printWriter.close();
    }

    void init(){
        // Const init
        Const.init();
        ScoreFunctionFilter scoreFunctionFilter = new ScoreFunctionFilter();
        scoreFunctionFilter.setCode(Const.SCORE_FUNC_CODE);
        Const.SCORE_FUNC_NUM = scoreFunctionFilter.getFuncNum();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        logger.printf(Level.getLevel("ZXROUT"), "=============DATE\t%s\tOUTPUT=============", df.format(new Date())); // new Date()为获取当前系统时间
        logger.printf(Level.getLevel("ZXROUT"), Const.QUESTION_FILENAME);
        logger.printf(Level.getLevel("ZXROUT"), Const.ANSWER_FILENAME);
        logger.printf(Level.getLevel("ZXROUT"), Const.QUESTION_SCORE_FILEPATH);
        logger.printf(Level.getLevel("ZXROUT"), "Score function code:%d", Const.SCORE_FUNC_CODE);

        // 判断权重个数和打分函数个数是否相同
        if (entityWeights.size() != Const.SCORE_FUNC_NUM || sentenceWeights.size() != Const.SCORE_FUNC_NUM){
            logger.error("Weights length is not equal score function number. " +
                    "EntityWeights length:%d, SentenceWeights length:%d, Score function number:" +
                    entityWeights.size(), sentenceWeights.size(), Const.SCORE_FUNC_NUM);
            return;
        }

        // 如果score文件不存在，自动调用ScoreCombination计算score
        File file = new File(Const.QUESTION_SCORE_FILEPATH);
        if (!file.exists()){
            String[] args = {Const.QUESTION_FILENAME};
            ScoreCombination.main(args);
        }

    }


    void predict(List<QuestionScores> questionScoresList, boolean useRealType){
        int rightCount = 0;
        for (QuestionScores questionScores : questionScoresList) {
            String realType = questionScores.getCandidateRealType();
            String predictType = questionScores.getCandidatePredictType();
            String type;
            if (useRealType) type = realType;
            else type = predictType;
            String answer = "E";
            boolean isRight = false;
            if (type.equals(Const.Q_T_ENTITY)) {
                answer = predictAnswer(questionScores, entityWeights);
            }
            if (type.equals(Const.Q_T_SENTENCE)) {
                answer = predictAnswer(questionScores, sentenceWeights);
            }
            if (answer.equals(questionScores.getCorrectAnswer())) {
                isRight = true;
                rightCount++;
            }
            logger.printf(Level.getLevel("ZXROUT"), "ID:%s\trealType:%s\tpredictType:%s\tpredict: %s\tcorrect: %s\t%b",
                    questionScores.getId(),
                    questionScores.getCandidateRealType(),
                    questionScores.getCandidatePredictType(),
                    answer,
                    questionScores.getCorrectAnswer(),
                    isRight);
        }
        logger.printf(Level.getLevel("ZXROUT"), "right number:%d, total number:%d, rate:%f",
                rightCount,
                questionScoresList.size(),
                rightCount*1.0/questionScoresList.size());
    }

    List<QuestionScores> getQuestionScores(String scoreFilename){
        int gap = Const.SCORE_FUNC_NUM + 2;
        int count = 0;
        QuestionScores questionScores = null;
        List<QuestionScores> allQuestionScores = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(scoreFilename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNext()){
            count++;
            String line = scanner.nextLine().trim();
            if (count % gap == 1){
                if (questionScores != null) allQuestionScores.add(questionScores);
                questionScores = new QuestionScores();
                String[] seg = line.split("\\s+");
                String id = seg[0].split(":")[1];
                String realType = seg[1].split(":")[1];
                String predictType = seg[2].split(":")[1];
                questionScores.setId(id);
                questionScores.setCandidateRealType(realType);
                questionScores.setCandidatePredictType(predictType);
                continue;
            }

            String[] seg = line.split("\\s+");
            double[] value = new double[4];
            for (int i = 0; i < 4; i++){
                value[i] = Double.valueOf(seg[i]);
            }

            if (count % gap == 0){
                questionScores.setTargetScore(value);
                questionScores.setCorrectAnswer(turnTargetValueToAnswer(value));
                continue;
            }
            questionScores.scores.add(value);
        }
        allQuestionScores.add(questionScores);
        return allQuestionScores;
    }


    String predictAnswer(QuestionScores questionScores, List<Double> weights){
        double[] finalScore = {0,0,0,0};
        for (int i = 0; i < questionScores.scores.size(); i++){
            finalScore = doubleArrayAddDoubleArray(finalScore,
                    doubleArrayTimesDouble(questionScores.scores.get(i), weights.get(i)));
        }
        printWriter.printf("ID:%s\tRealType:%s\t", questionScores.getId(), questionScores.getCandidateRealType());
        printWriter.printf("%f\t%f\t%f\t%f\n", finalScore[0], finalScore[1], finalScore[2], finalScore[3]);
        printWriter.flush();


        double maxScore = 0;
        int index = -1;
        for (int i = 0; i < finalScore.length; i++){
            if (finalScore[i] >= maxScore){
                maxScore = finalScore[i];
                index = i;
            }
        }
        switch (index){
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
        }
        return "E";
    }

    private double[] doubleArrayAddDoubleArray(double[] arr1, double[] arr2){
        if (arr1.length != arr2.length) {
            System.out.println("array size not the same");
            return null;
        }else {
            double[] arrSum = new double[arr1.length];
            for (int i = 0; i < arr1.length; i++){
                arrSum[i] = (arr1[i] + arr2[i]);
            }
            return arrSum;
        }
    }

    private double[] doubleArrayTimesDouble(double[] arr, double value){
        double[] sum = new double[arr.length];
        for (int i = 0; i < arr.length; i++){
            sum[i] = arr[i] * value;
        }
        return sum;
    }

    private String turnTargetValueToAnswer(double[] value){
        if (value[0] == 1) return "A";
        if (value[1] == 1) return "B";
        if (value[2] == 1) return "C";
        if (value[3] == 1) return "D";
        return "E";
    }


}
