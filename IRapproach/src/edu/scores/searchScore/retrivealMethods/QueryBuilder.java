package edu.scores.searchScore.retrivealMethods;

import edu.dev.entityLinking.LinkInfo;
import edu.main.Const;
import edu.question.Question;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sunder on 2016/1/21.
 * 构建各种类型的查询
 */
public class QueryBuilder {
    static boolean isShow = true;

    // add by sunder 2016/7/15
    // 问题+某个选项+引号对应的title
    public static BooleanQuery buildORQuestionWithCandidateWithQuotesQuery(
            Question question, int candidateNum, String field, float candidateBoostValue){
        List<String> quotesTitle = question.getEntityLinkInfo().stream()
                .filter(lf -> lf.getEntityClass().equals("Quote"))
                .map(LinkInfo::getEntityConten).collect(Collectors.toList());
        BooleanQuery booleanQuery = buildORQuestionWithCandidateQuery(question, candidateNum, field, candidateBoostValue);
        return buildQuery(booleanQuery, quotesTitle, field, BooleanClause.Occur.SHOULD);

//        return buildORQuestionWithCandidateQuery(question, candidateNum, field, candidateBoostValue);
    }

    // 问题和某个选项构成查询
    public static BooleanQuery buildORQuestionWithCandidateQuery(
            Question question, int candidateNum, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateNum, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidateQuery");
            isShow = false;
        }
        return booleanQuery;
    }
    public static BooleanQuery buildORQuestionWithCandidateQuery(
            Question question, String candidateEntity, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateEntity, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidateQuery");
            isShow = false;
        }
        return booleanQuery;
    }

    // 问题中的名词和某个选项构成查询
    public static BooleanQuery buildORQuestionWithCandidateNounQuery(
            Question question, int candidateNum, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionNounQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateNum, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidateNounQuery");
            isShow = false;
        }
        return booleanQuery;
    }
    public static BooleanQuery buildORQuestionWithCandidateNounQuery(
            Question question, String candidateEntity, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionNounQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateEntity, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidateNounQuery");
            isShow = false;
        }
        return booleanQuery;
    }

    // 问题中的动词和某个选项构成查询
    public static BooleanQuery buildORQuestionWithCandidateVerbQuery(
            Question question, String candidateEntity, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionVerbQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateEntity, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidateVerbQuery");
            isShow = false;
        }
        return booleanQuery;
    }
    public static BooleanQuery buildORQuestionWithCandidateVerbQuery(
            Question question, int candidateNum, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionVerbQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateNum, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidateVerbQuery");
            isShow = false;
        }
        return booleanQuery;
    }

    // 问题中的介词和某个选项构成查询
    public static BooleanQuery buildORQuestionWithCandidatePrepositionQuery(
            Question question, int candidateNum, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionPrepositionQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateNum, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidatePrepositionQuery");
            isShow = false;
        }
        return booleanQuery;
    }
    public static BooleanQuery buildORQuestionWithCandidatePrepositionQuery(
            Question question, String candidateEntity, String field, float candidateBoostValue){
        BooleanQuery booleanQuery = buildORQuestionPrepositionQuery(question, field);
        booleanQuery = buildORCandidateQuery(question, candidateEntity, field, booleanQuery, candidateBoostValue);
        if(isShow){
            System.out.println("buildORQuestionWithCandidatePrepositionQuery");
            isShow = false;
        }
        return booleanQuery;
    }

    public static BooleanQuery buildORQuestionQuery(Question question, String field){
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery = buildQuery(booleanQuery, question.getQuestionWords(), field, BooleanClause.Occur.SHOULD);
        return booleanQuery;
    }

    public static BooleanQuery buildORCandidateQuery(
            Question question, int candidateNum, String field, BooleanQuery booleanQuery, float candidateBoostValue){
        return buildQuery(booleanQuery, question.getCandidateWords(candidateNum), field, BooleanClause.Occur.SHOULD, candidateBoostValue);
    }
    public static BooleanQuery buildORCandidateQuery(
            Question question, String candidateEntity, String field, BooleanQuery booleanQuery, float candidateBoostValue){
        return buildQuery(booleanQuery, candidateEntity, field, BooleanClause.Occur.SHOULD, candidateBoostValue);
    }

    static BooleanQuery buildQuery(BooleanQuery booleanQuery, List<String> words, String field, BooleanClause.Occur occur){
        return buildQuery(booleanQuery, words, field, occur, 1);
    }
    static BooleanQuery buildQuery(List<String> words, String field, BooleanClause.Occur occur){
        BooleanQuery booleanQuery = new BooleanQuery();
        return buildQuery(booleanQuery, words, field, occur, 1);
    }
    static BooleanQuery buildQuery(BooleanQuery booleanQuery, List<String> words, String field, BooleanClause.Occur occur, float boostValue){
        for (String word : words) {
            TermQuery termQuery = new TermQuery(new Term(field, word));
            termQuery.setBoost(boostValue);
            booleanQuery.add(termQuery, occur);
        }
        return booleanQuery;
    }
    static BooleanQuery buildQuery(BooleanQuery booleanQuery, String word, String field, BooleanClause.Occur occur, float boostValue){
        TermQuery termQuery = new TermQuery(new Term(field, word));
        termQuery.setBoost(boostValue);
        booleanQuery.add(termQuery, occur);
        return booleanQuery;
    }


    public static BooleanQuery buildORQuestionNounQuery(Question question, String field){
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery = buildQuery(booleanQuery, Tools.getNounFromQuestion(question), field, BooleanClause.Occur.SHOULD);
        return booleanQuery;
    }

    public static BooleanQuery buildORQuestionVerbQuery(Question question, String field){
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery = buildQuery(booleanQuery, Tools.getVerbFromQuestion(question), field, BooleanClause.Occur.SHOULD);
        return booleanQuery;
    }

    public static BooleanQuery buildORQuestionPrepositionQuery(Question question, String field){
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery = buildQuery(booleanQuery, Tools.getPrepositionFromQuestion(question), field, BooleanClause.Occur.SHOULD);
        return booleanQuery;
    }




    private static class Tools{
        static List<String> getNounFromQuestion(Question question){
            List<String> nounWords = new ArrayList<>();
            for(int i = 0; i < question.getQuestionWords().size(); i++){
                if("n".equals(question.getQuestionOriginalPOS().get(i).substring(0,1)) || // 词性以n开始的（n,nr等）都是名词
                        Const.USER_DEFINE_SYMBOL.equals(question.getQuestionOriginalPOS().get(i))){
                    nounWords.add(question.getQuestionWords().get(i));
                }
            }
            return nounWords;
        }

        static List<String> getVerbFromQuestion(Question question){
            List<String> verbWords = new ArrayList<>();
            for(int i = 0; i < question.getQuestionWords().size(); i++){
                if("v".equals(question.getQuestionOriginalPOS().get(i).substring(0,1)) ||
                        Const.USER_DEFINE_SYMBOL.equals(question.getQuestionOriginalPOS().get(i))){
                    verbWords.add(question.getQuestionWords().get(i));
                }
            }
            return verbWords;
        }

        static List<String> getPrepositionFromQuestion(Question question){
            List<String> prepositionWords = new ArrayList<>();
            for(int i = 0; i < question.getQuestionWords().size(); i++){
                if("p".equals(question.getQuestionOriginalPOS().get(i).substring(0,1)) ||
                        Const.USER_DEFINE_SYMBOL.equals(question.getQuestionOriginalPOS().get(i))){
                    prepositionWords.add(question.getQuestionWords().get(i));
                }
            }
            return prepositionWords;
        }
    }
}
