package edu.scores.searchScore.retrivealMethods;

import edu.question.Question;
import edu.classifier.baikeClassify.BaikeClassifier;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/1/24.
 * 解题方法的结构，将不同方法得到的得分存储在其中
 */
public class QuestionInfo {
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getQuestionCandidateType() {
        return questionCandidateType;
    }

    public void setQuestionCandidateType(String questionCandidateType) {
        this.questionCandidateType = questionCandidateType;
    }

    public int getQuestionStemType() {
        return questionStemType;
    }

    public void setQuestionStemType(int questionStemType) {
        this.questionStemType = questionStemType;
    }

    public BaikeClassifier getBaikeClassifier() {
        return baikeClassifier;
    }

    public void setBaikeClassifier(BaikeClassifier baikeClassifier) {
        this.baikeClassifier = baikeClassifier;
    }

    public List<IndexSearcher> getSearcherList() {
        return searcherList;
    }

    public void setSearcherList(List<IndexSearcher> searcherList) {
        this.searcherList = searcherList;
    }

    public List<Double> getSearcherRates() {
        return searcherRates;
    }

    public void setSearcherRates(List<Double> searcherRates) {
        this.searcherRates = searcherRates;
    }

    public List<Double> getSingleEntitySearcherRates() {
        return singleEntitySearcherRates;
    }

    public void setSingleEntitySearcherRates(List<Double> singleEntitySearcherRates) {
        this.singleEntitySearcherRates = singleEntitySearcherRates;
    }

    public List<Double> getSentenceSearcherRates() {
        return sentenceSearcherRates;
    }

    public void setSentenceSearcherRates(List<Double> sentenceSearcherRates) {
        this.sentenceSearcherRates = sentenceSearcherRates;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public float getCandidateBoostValue() {
        return candidateBoostValue;
    }

    public void setCandidateBoostValue(float candidateBoostValue) {
        this.candidateBoostValue = candidateBoostValue;
    }

    public List<Double> getScore() {
        return score;
    }

    public void setScore(List<Double> score) {
        this.score = score;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Double> getQueryRates() {
        return queryRates;
    }

    public void setQueryRates(List<Double> queryRates) {
        this.queryRates = queryRates;
    }

    public List<QueryInfo> getQueryInfoList() {
        return queryInfoList;
    }

    public void setQueryInfoList(List<QueryInfo> queryInfoList) {
        this.queryInfoList = queryInfoList;
    }

    public List<int[]> getCandidatePeriod() {
        return candidatePeriod;
    }

    public void setCandidatePeriod(List<int[]> candidatePeriod) {
        this.candidatePeriod = candidatePeriod;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }

    private String questionID;
    private Question question; // 问题
    private String questionCandidateType; // 选项类型：单实体，多实体，句子
    private int questionStemType; // 题干类型，4表示题目要求选择错误的答案
    private BaikeClassifier baikeClassifier;    // 百科历史类/非历史类分类器
    private List<IndexSearcher> searcherList = new ArrayList<>();  // 解该题用到的检索元列表
    private List<Double> searcherRates = new ArrayList<>(); // 不同检索元的权重
    private List<Double> singleEntitySearcherRates = new ArrayList<>(); // 单实体题的检索元的权重
    private List<Double> sentenceSearcherRates = new ArrayList<>(); // 句子题的检索元的权重
    private int topN = 3; // 对前topN篇文档的得分求和
    private float candidateBoostValue = 1; // 对选项包含的词进行boost，如果不进行boost，则将值设置为1

    private List<Double> score; // 每个选项的最后总得分
    private String rightAnswer; // 正确答案，
    private String answer = "E"; // 预测的答案。A,B,C,D,E。E表示无法解答

    private List<Double> queryRates = new ArrayList<>(); // 不同查询的权重
    private List<QueryInfo> queryInfoList = new ArrayList<>();
    public class QueryInfo{
        public Query query;
        public IndexSearcher searcher;
        public int relatedCandidate = -1; // 相关的选项的编号。 A～D对应0～3， -1表示查询与选项无关
        public List<Float> scoreList = new ArrayList<>();
        public List<Float> historyScoreList = new ArrayList<>();
        public List<String> historyDocumentTitle = new ArrayList<>();
        public List<Integer> historyDocumentTime = new ArrayList<>(); // 历史类文档抽取出的时间
    }

    List<int[]> candidatePeriod = new ArrayList<>(); // 每个选项代表的时期，如果不需要，则不赋值

    public String toString() {
        String str = "--------------------\n";
        if(question == null) str += "QUESTION: empty\n";
        else str += "QUESTION " +questionID + ": " + question.getQuestion()
                 + "\t" + question.getCandidates(0)
                 + "\t" + question.getCandidates(1)
                 + "\t" + question.getCandidates(2)
                 + "\t" + question.getCandidates(3) + "\n";

        if("".equals(rightAnswer)) str += "rightAnswer: empty\n";
        else str += "RIGHT_ANSWER:\t" + rightAnswer + "\n";
        if("".equals(answer)) str += "predict answer: empty\n";
        else{
            str += "PREDICT_ANSWER:\t" + answer + "\t";
            if(rightAnswer.equals(answer))
                str += "YOU_DO_RIGHT" + "\n";
            else
                str += "YOU_DO_WRONG" + "\n";
        }

        str += "QUESTION_TYPE:\t" + questionCandidateType + "\t" + "QUESTION_STEM_TYPE:\t" + questionStemType + "\n";

        str += "SINGLE_ENTITY_SEARCHER_RATES:\t" + singleEntitySearcherRates + "\n";
        str += "SENTENCE_SEARCHER_RATES:\t" + sentenceSearcherRates + "\n";

        str += "CANDIDATE_BOOST_VALUE:\t" + candidateBoostValue + "\n";

        str += "SCORE_FOR_EVERY_CANDIDATE:\t" + score + "\n";

        str += "SEARCHER:\n";
        if(searcherList == null) str += "empty\n";
        else {
            for (IndexSearcher searcher : searcherList) {
                str += searcher.toString() + "\n";
            }
        }
        str += "CANDIDATE_ENTITY:\n";
        for(int i = 0; i < 4; i++) {
            str += question.getCandidateNER(i) + "\n";
        }

        str += "QUERY:\n";
        if (queryInfoList.size() == 0) str += "empty\n";
        else {
            for (QueryInfo queryInfo : queryInfoList) {
//                str += "searcher: " + queryInfo.searcher;
                str += "query: " + queryInfo.query.toString() + "\n";
                str += "\trelated candidate: " + queryInfo.relatedCandidate + "\n";
                str += "\thistory document score: " + queryInfo.historyScoreList + "\n";
                str += "\thistory document title: " + queryInfo.historyDocumentTitle + "\n";
            }
        }
        return str + "====================";
    }

    // 检查questionInfo是否合法
    public boolean check(){
        boolean flag = true;
        if(searcherList.size() != singleEntitySearcherRates.size() &&
                searcherList.size() != sentenceSearcherRates.size()){
            flag = false;
            System.out.printf("CHECK RESULT: searcher list size: %d, searcher rates size: %d\t%d\n",
                    searcherList.size(), singleEntitySearcherRates.size(), sentenceSearcherRates.size());
        }
        if(queryInfoList.size() == 0){
            flag = false;
            System.out.printf("CHECK RESULT: no query!\n");
        }
        return flag;
    }

}
