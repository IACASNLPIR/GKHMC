package edu.main;

import edu.stanford.nlp.util.ArraySet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sunder on 2016/6/12.
 * 设置采用那些打分函数
 */
public class ScoreFunctionFilter {
    // baikeDocScore, baikeParaScore, baikeSentScore,ooccurDocScore, cooccurParaScore, cooccurSentScore, linkScore
    // code:1111111     length = 7
    // 1 means use the corresponding function, 0 means don't use
    Set<String> allowedFunction = new ArraySet<>();
    public void setCode(int code){
        if (code % 10 == 1){
            // use linkScore
            allowedFunction.add(Const.BAIKE_LINK_SEARCHER_NAME);
            System.out.println(Const.BAIKE_LINK_SEARCHER_NAME);
        }
        if ((code/10) * 10 % 100 == 10){
            allowedFunction.add(Const.CO_OCCUR_DOC_SEARCHER_NAME);
            System.out.println(Const.CO_OCCUR_DOC_SEARCHER_NAME);
        }
        if ((code/100) * 100 % 1000 == 100){
            allowedFunction.add(Const.CO_OCCUR_PARA_SEARCHER_NAME);
            System.out.println(Const.CO_OCCUR_PARA_SEARCHER_NAME);
        }
        if ((code/1000) * 1000 % 10000 == 1000){
            allowedFunction.add(Const.CO_OCCUR_SENT_SEARCHER_NAME);
            System.out.println(Const.CO_OCCUR_SENT_SEARCHER_NAME);
        }
        if ((code/10000) * 10000 % 100000 == 10000){
            allowedFunction.add(Const.BAIKE_SENT_SEARCHER_NAME);
            System.out.println(Const.BAIKE_SENT_SEARCHER_NAME);
        }
        if ((code/100000) * 100000 % 1000000 == 100000){
            allowedFunction.add(Const.BAIKE_PARA_SEARCHER_NAME);
            System.out.println(Const.BAIKE_PARA_SEARCHER_NAME);
        }
        if ((code/1000000) * 1000000 % 10000000 == 1000000){
            allowedFunction.add(Const.BAIKE_DOC_SEARCHER_NAME);
            System.out.println(Const.BAIKE_DOC_SEARCHER_NAME);
        }
    }

    public Set<String> getAllowedFunction() {
        return allowedFunction;
    }

    public int getFuncNum(){
        return allowedFunction.size();
    }

    public static void main(String[] args) {
        ScoreFunctionFilter scoreFunctionFilter = new ScoreFunctionFilter();
        scoreFunctionFilter.setCode(1000000);
    }
}
