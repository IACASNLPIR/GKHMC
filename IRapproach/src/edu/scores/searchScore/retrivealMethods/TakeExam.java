package edu.scores.searchScore.retrivealMethods;

import edu.classifier.baikeClassify.GetClassifier;
import edu.dev.entityLinking.QuestionDeal;
import edu.main.Const;
import edu.question.Question;
import edu.question.QuestionAnalyzer;
import edu.classifier.baikeClassify.BaikeClassifier;
import edu.classifier.questionType.bayesianQuestionCandidateType.QuestionTypeNBC;
import edu.classifier.questionType.RuleQuestionCandidateType;
import edu.question.QuestionCandidate;
import edu.tools.OutputQuestion;
import org.apache.lucene.search.IndexSearcher;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sunder on 2016/1/25.
 * 依次读入question并调用其它类解题
 */
public class TakeExam {
    public static void main(String[] args) {
        Const.init();
        String filename;
        filename = "CountryReal(2011-2015)(106)SingleEntity.xml";
        TakeExam myTakeExam = new TakeExam(Const.QUESTIONS_FILEPATH + filename);
        List<Double> singleEntitySearcherRates = new ArrayList<Double>(){
            {
                add(0.950269);
//                add(0.017878);
//                add(0.014177);
//                add(0.017676);
            }};
        List<Double> sentenceSearcherRates = new ArrayList<Double>(){
            {
                add(0.950269);
//                add(0.017878);
//                add(0.014177);
//                add(0.017676);
            }};
        myTakeExam.setSearcherRates(singleEntitySearcherRates, sentenceSearcherRates);
        List<String> myargs = new ArrayList<>();
        myargs.add("baikeDocSearcher");
        myTakeExam.run(myargs); // true 静默，只输出最后正确率， false 输出所有细节
    }


    List<Double> singleEntitySearcherRates;
    List<Double> sentenceSearcherRates;
    List<Double> queryRates;
    QuestionDeal questionDeal = new QuestionDeal(true);

    public void setQueryRates(List<Double> queryRates) {
        this.queryRates = queryRates;
    }
    public void setQueryRates(int size){
        this.queryRates = getRates(size);
    }
    public void setSearcherRates(List<Double> singleEntitySearcherRates, List<Double> sentenceSearcherRates){
        this.singleEntitySearcherRates = singleEntitySearcherRates;
        this.sentenceSearcherRates = sentenceSearcherRates;
    }
    public void setSearcherRates(int size){
        this.singleEntitySearcherRates = getRates(size);
        this.sentenceSearcherRates = getRates(size);
    }

    public List<Double> getRates(int size){
        List<Double> rates = new ArrayList<>();
        Random random = new Random();
        double sum = 0;
        for(int i = 0; i < size - 1; i++){
            double num = (1 - sum) * random.nextDouble();
            sum += num;
            rates.add(num);
        }
        rates.add(1 - sum);
        return rates;
    }

    QuestionAnalyzer analyzer;
    public TakeExam(String questionFilepath) {
        analyzer  = new QuestionAnalyzer();
        analyzer.init(questionFilepath);
    }
    public TakeExam(String questionFilepath, String answerFilepath, boolean isTrue) {
        analyzer  = new QuestionAnalyzer();
        analyzer.init(questionFilepath, answerFilepath, isTrue);
//        analyzer.init(questionFilepath, answerFilepath);
    }

    BaikeClassifier baikeClassifier = null;

    public void run(List<String> myargs){
        if (myargs.contains("classify")){
            baikeClassifier = GetClassifier.get(TakeExam.class.getName());
        }
        SimpleDateFormat format=new SimpleDateFormat();
        String time=format.format(new Date());
        if (myargs.contains("saveResults")) {
            saveResultToFile("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n当前时间：" + time);
        }
        NumRecorder numRecorder = new NumRecorder();
        RuleQuestionCandidateType ruleQuestionCandidateType = new RuleQuestionCandidateType();
        for (Question question : analyzer.acquisition.questionList) {
            dealWithSequenceQuestion(question);
            try {
                questionDeal.entityLinkInit(question);
            } catch (IOException e) {
                e.printStackTrace();
            }
            numRecorder.questionID = question.getID();
            QuestionInfo questionInfo = new QuestionInfo();
            questionInfo.setQuestionID(numRecorder.questionID);
            questionInfo.setQuestion(question);
            questionInfo.setRightAnswer(question.getAnswer());
            questionInfo.setBaikeClassifier(baikeClassifier);
            questionInfo.setSingleEntitySearcherRates(singleEntitySearcherRates);
            questionInfo.setSentenceSearcherRates(sentenceSearcherRates);
            questionInfo.setQuestionStemType(question.getStemType());
            // 设置题目选项类型
            String candidateType = "";
            if (myargs.contains("useBayes")) candidateType = QuestionTypeNBC.run(question);
            if (myargs.contains("useGSM")) candidateType = ruleQuestionCandidateType.judge(question, true);
            if (myargs.contains("useZXR")) candidateType = ruleQuestionCandidateType.judge(question, false);
//            candidateType = question.questionRealType.get(0);
            questionInfo.setQuestionCandidateType(candidateType);

            // 设置检索元
             // 通过参数设置
            List<IndexSearcher> searcherList = questionInfo.getSearcherList();
            if (myargs.contains("baikeDocSearcher")) {
                searcherList.add(Retrieval.getBaikeDocSearcher());
            }
            if (myargs.contains("bookSearcher")) {
                searcherList.add(Retrieval.getBookSearcher());
            }
            if (myargs.contains("baikeParaSearcher")) {
                searcherList.add(Retrieval.getBaikeParaSearcher());
            }
            if (myargs.contains("baikeSentSearcher")) {
                searcherList.add(Retrieval.getBaikeSentSearcher());
            }
            // 根据题目选项类型自动设置. 如果参数没有指定检索元，那么就按照题目选项类型自动设置
            if (questionInfo.getSearcherList().size() == 0){
                switch (candidateType) {
                    case Const.Q_T_ENTITY:
                        searcherList.add(Retrieval.getBaikeDocSearcher());
                        break;
                    case Const.Q_T_SINGLE_ENTITY:
                        searcherList.add(Retrieval.getBaikeDocSearcher());
                        break;
                    case Const.Q_T_MULTIPLE_ENTITY:
                        searcherList.add(Retrieval.getBaikeSentSearcher());
                        break;
                    case Const.Q_T_SENTENCE:
                        searcherList.add(Retrieval.getBaikeParaSearcher());
                        break;
                }
            }
            questionInfo.setSearcherList(searcherList);
           // 求解答案
            MethodContext methodContext = new MethodContext(questionInfo);
            boolean isNormalizeScore = false;
            if(myargs.contains("normalizeScore"))
                isNormalizeScore = true;
            methodContext.workOutAnswer(isNormalizeScore);
            // 记录信息
            boolean isRight = question.getAnswer().equals(questionInfo.getAnswer());
            List<Double> candidateCertainties = calcCandidateCertainties(questionInfo.getScore());
            question.setCandidateCertainties(candidateCertainties);
            numRecorder.currentQuestionType = questionInfo.getQuestionCandidateType();
            numRecorder.record(isRight);

            // 是否显示当前题信息
            boolean isQuiet = false;
            if (myargs.contains("beQuiet")){
                isQuiet = true;
            }
            if (!isQuiet) {
                System.out.printf("%d\t%s\tQuestion ID:%s\tPredict:%s\tRight:%s\t",
                        numRecorder.questionNum, numRecorder.currentQuestionType,
                        numRecorder.questionID, questionInfo.getAnswer(), question.getAnswer());
                System.out.println(isRight);
                if (myargs.contains("saveResults")) {
                    saveResultToFile(questionInfo.toString());
                }
            }

        }
        // 保存信息
        OutputQuestion.outputCandidateCertainty(analyzer.acquisition.questionList, "zxr_out.txt");
        OutputQuestion.asNNTest(analyzer.acquisition.questionList, "words.txt", "tmp.txt");
        System.out.println(toString(numRecorder));
        if (myargs.contains("saveResults")) {
            saveResultToFile(toString(numRecorder));
        }
    }

    public void showQuestionType(List<String> myargs){
        RuleQuestionCandidateType ruleQuestionCandidateType = new RuleQuestionCandidateType();
        NumRecorder numRecorder = new NumRecorder();
        QuestionTypeNBC questionTypeNBC = new QuestionTypeNBC();
        for (Question question : analyzer.acquisition.questionList) {
            String candidateType = "";
            if (myargs.contains("useGSM")) {
                candidateType = ruleQuestionCandidateType.judge(question, true);
            }
            if (myargs.contains("useZXR")){
                candidateType = ruleQuestionCandidateType.judge(question, false);
            }
            if (myargs.contains("useBayes")) {
//                candidateType = QuestionTypeNBC.run(question);
                candidateType = questionTypeNBC.use(question);
            }
            numRecorder.currentQuestionType = candidateType;
            numRecorder.record(false);

            if (!myargs.contains("beQuiet")) {
                System.out.println(question.getID() + "," + candidateType);
//                System.out.println("\t" + question.getQuestion());
            }
        }
        System.out.println(toString(numRecorder));
    }
    public String toString(NumRecorder numRecorder){
        return "RESULT"
                + "\n    right/total num:\t" + numRecorder.rightNum + ":" + numRecorder.questionNum
                + "\n  Single entity num:\t" + numRecorder.numSingleEntity[0] + ":" + numRecorder.numSingleEntity[1]
                + "\nMultiple entity num:\t" + numRecorder.numMultipleEntity[0] + ":" + numRecorder.numMultipleEntity[1]
                + "\n       Sentence num:\t" + numRecorder.numSentence[0] + ":" + numRecorder.numSentence[1]
                + "\n       Entity num:\t" + numRecorder.numEntity[0] + ":" + numRecorder.numEntity[1]
                + "\n           Time num:\t" + numRecorder.numTime[0] + ":" + numRecorder.numTime[1]
                + "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
    }

    void show(){
        int questionNum = 0;
        int questionID = -1;
        for (Question question : analyzer.acquisition.questionList) {
            questionID++;
            if(question.getCandidateType() != 0) continue;
            if(question.getNumericalType()) continue;
            questionNum++;
            System.out.println(question.getQuestion());
            System.out.printf("%d Question:%d\n", questionNum, questionID);
        }
    }

    private String logFilename = Const.RESULT_LOG_FILEPATH + "result.log";
    public void setLogFilename(String logFilename){
        this.logFilename = logFilename;
    }

    void saveResultToFile(String info){
        File f = new File(logFilename);
        File parent = f.getParentFile();
        if (!parent.exists()){
            parent.mkdirs();
        }
        if (!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("Create log file failed: " + logFilename);
                e.printStackTrace();
            }
        }
        try{
            FileWriter fileWriter = new FileWriter(logFilename, true);
            fileWriter.write(info + "\n");
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private List<Double> calcCandidateCertainties(List<Double> scores){
        List<Double> certainties = new ArrayList<>();
        double sum = 0;
        for(double score : scores) sum += score;
        for (int i = 0; i < 4; i++){
            certainties.add(scores.get(i)/sum);
        }
        return certainties;
    }

    private void dealWithSequenceQuestion(Question question){
        if(question.getNumericalType()){
            for (int i = 0; i < 4; i++){
                dealWithSequenceQuestion(i, question);
            }
        }
    }
    private void dealWithSequenceQuestion(int candidateIndex, Question question){
        Map<String, Integer> map = new HashMap<>();
        map.put("①", 1);
        map.put("②", 2);
        map.put("③", 3);
        map.put("④", 4);
        map.put("⑤", 5);
        map.put("⑥", 6);
        map.put("⑦", 7);
        map.put("⑧", 8);
        map.put("⑨", 9);
        map.put("⑩", 10);
        String candidate = question.getCandidates(candidateIndex);
        String newCandidate = "";
        ArrayList<String> newCandidateWords = new ArrayList<>();
        ArrayList<String> newCandidateNERs = new ArrayList<>();
        ArrayList<String> newCandidatePOSs = new ArrayList<>();
        for (int i = 0; i < candidate.length(); i++){
            String tmp = candidate.substring(i, i+1);
            int index;
            if (map.containsKey(tmp)) {
                index = map.get(tmp);
            }else return;
            newCandidate += question.getNumCandidates(index-1) + ",";
            newCandidateWords.addAll(question.getNumCandidates().get(index-1).getWords());
            newCandidateNERs.addAll(question.getNumCandidates().get(index-1).getNERs());
            newCandidatePOSs.addAll(question.getNumCandidates().get(index-1).getPOSes());
        }
        question.setCandidate(candidateIndex, newCandidate);
        question.setCandidateWordandPOS(candidateIndex, newCandidateWords, newCandidatePOSs, newCandidateNERs);

    }

    class NumRecorder {
        String currentQuestionType;
        String questionID = "unKnow"; // 当前题的ID
        int questionNum = 0; // 总共做了的题数
        int rightNum = 0; // 做对的题数
        int[] numSentence = {0, 0}; // 句子题，第一个数是做对了的句子题数，第二个数是句子题的总数
        int[] numSingleEntity = {0, 0}; // 单实体题
        int[] numMultipleEntity = {0, 0}; // 多实体题
        int[] numTime = {0, 0}; // 时间题
        int[] numEntity = {0, 0}; // 实体题

        public void record(boolean isRight){
            questionNum++;
            int startIndex;
            if (isRight){
                rightNum++;
                startIndex = 0;
            }else {
                startIndex = 1;
            }
            for (int index = startIndex; index < 2; index++) {
                switch (currentQuestionType) {
                    case Const.Q_T_SINGLE_ENTITY:
                        numSingleEntity[index]++;
                        break;
                    case Const.Q_T_MULTIPLE_ENTITY:
                        numMultipleEntity[index]++;
                        break;
                    case Const.Q_T_SENTENCE:
                        numSentence[index]++;
                        break;
                    case Const.Q_T_TIME:
                        numTime[index]++;
                        break;
                    case Const.Q_T_ENTITY:
                        numEntity[index]++;
                        break;

                }
            }
        }
    }
}
