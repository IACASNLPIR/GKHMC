package edu.scores.searchScore.retrivealMethods;

import edu.main.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/3/23.
 * 调参，会经常修改
 */
public class Lab {
    public static void main(String[] args) {
        Const.init();
        String filename = Const.QUESTION_FILENAME;

        // 参数列表
        // baikeDocSearcher baikeParaSearcher baikeSentSearcher bookSearcher 如果没设置检索元，那么按照题型来选择检索元
        // beQuiet 设置之后静默运行
        // useGSM 设置之后用郭尚敏的题目选项分类策略
        // useBayes 使用朴素贝叶斯分类题目选项
        // xunfei 采用讯飞格式
        // solve 运行解题 showClassify 运行分类
        // classify 是否对检索返回文档分成历史与非历史类
        // saveResults 保存解题的log文件
        List<String> myargs = new ArrayList<>();
//        myargs.add("beQuiet");
        myargs.add("saveResults");
        myargs.add("solve");
//        myargs.add("showClassify");
//        myargs.add("useGSM");
//        myargs.add("useZXR");
        myargs.add("classify");
        myargs.add("useBayes");
        myargs.add("xunfei");
//        myargs.add("normalizeScore");
        myargs.add("baikeDocSearcher");
//        myargs.add("baikeParaSearcher");
//        myargs.add("baikeSentSearcher");
//        myargs.add("bookSearcher");

        // 命令行参数
        if (args.length >= 1) { // 文件名
            filename = args[0];
        }
        if (args.length >= 2) {
            if (args[1].equals("true")) { //  静默运行
                myargs.add("beQuiet");
            }
        }
        if (args.length >= 3) {
            if (args[2].equals("xunfei")) { // 采用讯飞格式
                myargs.add("xunfei");
            }
        }
        if (args.length == 4) {
            if (args[3].equals("normalize")) { // 采用讯飞格式
                myargs.add("normalizeScore");
            }
        }

        List<String> filenameList;
        if (args.length != 0 || !filename.equals(Const.QUESTIONS_FILEPATH)){
            filenameList = createFilenameList(filename);
        }else {
            filenameList = createFilenameList();
        }
        for(String questionFilename : filenameList) {
            System.out.println(questionFilename);
            TakeExam myTakeExam;
            if (myargs.contains("xunfei")) {
                String answerFilename = Const.ANSWER_FILENAME;
                myTakeExam = new TakeExam(questionFilename, answerFilename, true);
            }else{
                myTakeExam = new TakeExam(questionFilename);
            }
            String logFilename = buildLogFilename(questionFilename);
            myTakeExam.setLogFilename(logFilename); // 设置log的文件名

            int ratesSize = 1;

            try {
                if (myargs.contains("showClassify")) {
                    myTakeExam.showQuestionType(myargs);
                }
                if (myargs.contains("solve")) {
                    doByGivenRates(myTakeExam, ratesSize, myargs);
//        doByRandomRates(myTakeExam, ratesSize, isRunQuiet);
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Lab WRONG");
            }
        }
    }
    static void doByGivenRates(TakeExam myTakeExam, int ratesSize, List<String> myargs){
        List<List<Double>> singleEntityRateList = buildSingleEntityRateList(ratesSize);
        List<List<Double>> sentenceRateList = buildSentenceRateList(ratesSize);
        int loop; // 循环次数
        loop = singleEntityRateList.size() * sentenceRateList.size();
        System.out.println("loop times: " + loop);
        System.out.println(singleEntityRateList);
        System.out.println(sentenceRateList);
        for(List<Double> singleEntityRate : singleEntityRateList){
            for(List<Double> sentenceRate : sentenceRateList){
                myTakeExam.setSearcherRates(singleEntityRate, sentenceRate); // 指定具体的检索元比率
                myTakeExam.run(myargs);
            }
        }
    }

    static void doByRandomRates(TakeExam myTakeExam, int ratesSize, List<String> myargs){
        int loop; // 循环次数
        loop = 100;
        System.out.println("loop times: " + loop);
        while(loop-- > 0){
            myTakeExam.setSearcherRates(ratesSize); // 随机产生四个比率
            myTakeExam.run(myargs);
        }
    }


    static List<List<Double>> buildSingleEntityRateList(int size) {
//        double[] originalData = {0.393502,0.46518,0.124661,0.016657};
        double[] originalData = {1};
        return buildRateList(size, originalData);
    }
    static List<List<Double>> buildSentenceRateList(int size){
//        double[] originalData = {0.10821827544135754,0.5129617390252432,0.11541127620786763,0.263409};
        double[] originalData = {1};
        return buildRateList(size, originalData);
    }
    static List<List<Double>> buildRateList(int size, double[] originalData){
        List<List<Double>> rateList = new ArrayList<>();
        for(int i = 0; i < originalData.length/size; i++){
            List<Double> searcherRates = new ArrayList<Double>();
            for(int j = i*size; j < (i+1)*size; j++){
                searcherRates.add(originalData[j]);
            }
            rateList.add(searcherRates);
        }
        return rateList;
    }

    static List<String> createFilenameList(String filename){
        List<String> filenameList = new ArrayList<>();
        filenameList.add(filename);
        return filenameList;
    }
    static List<String> createFilenameList(){
        List<String> filenameList = new ArrayList<>();
//        for(int i = 1; i < 16; i++){
//            String id;
////            if(i == 5) continue;
//            if(i < 10){
//                id = "0" + i;
//            }else{
//                id = "" + i;
//            }
//            String filename = "xunfei(1-15)/gsm/Test" + id + ".xml";
//            filenameList.add(filename);
//        }
        filenameList.add(Const.QUESTIONS_FILEPATH + "gaokao_all(2011-2015)_data/SingleEntity.xml");
        filenameList.add(Const.QUESTIONS_FILEPATH + "gaokao_all(2011-2015)_data/MultiEntity.xml");
        filenameList.add(Const.QUESTIONS_FILEPATH + "gaokao_all(2011-2015)_data/Sentence.xml");
        return filenameList;
    }

    static String buildLogFilename(String filename){
        return "logs/tmp.log";
//        int position = filename.indexOf(Const.QUESTIONS_FILEPATH);
//        String logFilename = Const.RESULT_LOG_FILEPATH;
//        if (position >= 0){
//            logFilename += filename.substring(position + Const.QUESTIONS_FILEPATH.length()).replace("xml", "log");
//        } else {
//            logFilename += filename.replace("xml", "log");
//        }
//        return logFilename;
    }

}
