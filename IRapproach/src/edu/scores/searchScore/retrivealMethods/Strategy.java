package edu.scores.searchScore.retrivealMethods;

import org.apache.lucene.search.IndexSearcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/2/18.
 * 解题策略的公共接口
 */
public abstract class Strategy {
    QuestionInfo questionInfo;
    public Strategy(QuestionInfo questionInfo){
        this.questionInfo = questionInfo;
    }

    abstract int use(boolean isScoreNormalize);


    /**
     * 分别计算每个选项对应的历史类结果的得分之和
     * @return 每个选项对应的得分列表
     */
    List<Double> sumHistoryScores(){
        List<Double> allSum = new ArrayList<>();
        double sum0 = 0.0;
        double sum1 = 0.0;
        double sum2 = 0.0;
        double sum3 = 0.0;
        for(QuestionInfo.QueryInfo queryInfo : questionInfo.getQueryInfoList()){
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
        allSum.add(sum0);
        allSum.add(sum1);
        allSum.add(sum2);
        allSum.add(sum3);
        return allSum;
    }


    /**
     * 一次查询的前N篇历史类文档的得分之和，最后乘以权重
     * @param queryInfo 查询信息
     * @return 加权后的总分
     */
    double sumTopNHistoryScore(QuestionInfo.QueryInfo queryInfo){
        IndexSearcher searcher = queryInfo.searcher;
        int searcherNumber = questionInfo.getSearcherList().indexOf(searcher);
        double searcherRate = questionInfo.getSearcherRates().get(searcherNumber);
        double sum = 0;
        for(int i = 0; i < Integer.min(questionInfo.getTopN(), queryInfo.historyScoreList.size()); i++){
            sum += queryInfo.historyScoreList.get(i);
        }
        return sum*searcherRate;
    }

    int maxIndex(List<Double> list){
        int index = -1;
        double max = -Double.MAX_VALUE;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) > max){
                max = list.get(i);
                index = i;
            }
        }
        return index;
    }

    int minIndex(List<Double> list){
        int index = -1;
        double min = Double.MAX_VALUE;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i) < min){
                min = list.get(i);
                index = i;
            }
        }
        return index;
    }
}
