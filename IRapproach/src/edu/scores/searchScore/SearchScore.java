package edu.scores.searchScore;


import edu.scores.Score;
import edu.tools.zxr.*;
import edu.main.Const;
import edu.question.Question;
import edu.question.QuestionAnalyzer;
import edu.scores.searchScore.retrivealMethods.QueryBuilder;
import edu.scores.searchScore.retrivealMethods.QuestionInfo;
import edu.scores.searchScore.retrivealMethods.Retrieval;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/5/24.
 * 检索文档，前三篇文档的得分之和
 */
public class SearchScore extends Score{
    IndexSearcher searcher;

    String searcherName = Const.BAIKE_DOC_SEARCHER_NAME;
    public void setSearcherName(String searcherName) {
        this.searcherName = searcherName;
    }
    public double[] getScore(Question question){
        return getSearchScore(question, searcherName);
    }

    public double[] getSearchScore(Question question, String searcherName){
        searcher = getSearcher(searcherName);
        List<QuestionInfo.QueryInfo> queryInfoList = setQuery(question);
        try {
            searchFindTopScore(queryInfoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        double[] scores = sumScores(queryInfoList);
        return scores;
    }

    IndexSearcher getSearcher(String searcherName){
        switch (searcherName){
            case Const.BAIKE_DOC_SEARCHER_NAME:
                return Retrieval.getBaikeDocSearcher();
            case Const.BAIKE_PARA_SEARCHER_NAME:
                return Retrieval.getBaikeParaSearcher();
            case Const.BAIKE_SENT_SEARCHER_NAME:
                return Retrieval.getBaikeSentSearcher();
        }
        return null;
    }

    List<QuestionInfo.QueryInfo> setQuery(Question question){
        List<QuestionInfo.QueryInfo> queryInfoList = new ArrayList<>();
        for (int candidateNumber = 0; candidateNumber < 4; candidateNumber++) {
            QuestionInfo.QueryInfo queryInfo = new QuestionInfo().new QueryInfo();
            queryInfo.query = QueryBuilder.buildORQuestionWithCandidateQuery(
                    question,
                    candidateNumber,
                    Const.FIELD_CONTENT,
                    1);
            queryInfo.searcher = searcher;
            queryInfo.relatedCandidate = candidateNumber;
            queryInfoList.add(queryInfo);
        }
        return queryInfoList;
    }

    void searchFindTopScore(List<QuestionInfo.QueryInfo> queryInfoList) throws IOException {
        for(QuestionInfo.QueryInfo queryInfo : queryInfoList){
            TopDocs topDocs = searcher.search(queryInfo.query, 500);
            int count = 0;
            for(ScoreDoc scoreDoc : topDocs.scoreDocs){
                queryInfo.scoreList.add(scoreDoc.score);
                queryInfo.historyScoreList.add(scoreDoc.score);
                count++;
                if(count >= 3) break;
            }
        }
    }

    /**
     * 分别计算每个选项对应的历史类结果的得分之和
     * @return 每个选项对应的得分列表
     */
    double[] sumScores(List<QuestionInfo.QueryInfo> queryInfoList){
        double[] scores = new double[4];
        double sum;
        double sum0 = 0.0;
        double sum1 = 0.0;
        double sum2 = 0.0;
        double sum3 = 0.0;
        for(QuestionInfo.QueryInfo queryInfo : queryInfoList){
            switch(queryInfo.relatedCandidate){
                case 0:
                    sum0 += sumTopNHistoryScore(queryInfo);
                    break;
                case 1:
                    sum1 += sumTopNHistoryScore(queryInfo);
                    break;
                case 2:
                    sum2 += sumTopNHistoryScore(queryInfo);
                    break;
                case 3:
                    sum3 += sumTopNHistoryScore(queryInfo);
                    break;
                default:
            }
        }
        sum = sum0 + sum1 + sum2 + sum3;
        scores[0] = sum0 / sum;
        scores[1] = sum1 / sum;
        scores[2] = sum2 / sum;
        scores[3] = sum3 / sum;
        return scores;
    }

    /**
     * 一次查询的前N篇历史类文档的得分之和
     * @param queryInfo 查询信息
     * @return 总分
     */
    double sumTopNHistoryScore(QuestionInfo.QueryInfo queryInfo){
        return CollectionOperates.sumFloatListTopN(queryInfo.historyScoreList, 3);
    }

    public static void main(String[] args) {
        String filename =
                "gaokao_all(2011-2015)_data/CountryAllRealHistoryMultipleChoiceQuestion(2011-2015)(744)WithType.xml";
        filename = Const.QUESTIONS_FILEPATH + filename;
        QuestionAnalyzer analyzer = new QuestionAnalyzer();
        analyzer.init(filename);
        SearchScore searchScore = new SearchScore();
        for (Question question : analyzer.acquisition.questionList) {
            System.out.println("ID: " + question.getID());
            double[] prob = searchScore.getSearchScore(question, Const.BAIKE_DOC_SEARCHER_NAME);
            for (double p : prob) {
                System.out.print(p + "\t");
            }
            System.out.println(" ");
        }
    }
}
