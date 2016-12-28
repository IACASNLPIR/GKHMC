package edu.dev.emnlp2016;

import edu.main.Const;
import edu.question.Question;
import edu.question.QuestionAnalyzer;
import edu.scores.searchScore.retrivealMethods.MethodContext;
import edu.scores.searchScore.retrivealMethods.QuestionInfo;
import edu.scores.searchScore.retrivealMethods.Retrieval;
import org.apache.lucene.search.IndexSearcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016-5-21.
 * emnlp2016 influence of the grain of index experiment
 */
public class InfluenceOfGrainOfIndex {

    QuestionAnalyzer analyzer;
    boolean isNormalizeScore;

    void init(String xmlFilename, boolean isNormalizeScore) {
        analyzer = new QuestionAnalyzer();
        analyzer.init(xmlFilename);
        this.isNormalizeScore = isNormalizeScore;
    }

    List<Integer> iterateQuestion(String searcherName) {
        int numEntityQuestion = 0;
        int rightNumEntityQuestion = 0;
        int numSentenceQuestion = 0;
        int rightNumSentenceQuestion = 0;
        List<Double> singleEntitySearcherRates = new ArrayList<Double>(){{
            add(1.0);}
        };
        List<Double> sentenceSearcherRates = new ArrayList<Double>(){{
            add(1.0);}
        };
        for (Question question : analyzer.acquisition.questionList) {
            QuestionInfo questionInfo = new QuestionInfo();
            questionInfo.setQuestion(question);
            questionInfo.setRightAnswer(question.getAnswer());
            questionInfo.setQuestionStemType(question.getStemType());
            questionInfo.setQuestionCandidateType(question.questionRealType.get(0));
            questionInfo.setSingleEntitySearcherRates(singleEntitySearcherRates);
            questionInfo.setSentenceSearcherRates(sentenceSearcherRates);
            List<IndexSearcher> searcherList = questionInfo.getSearcherList();
            if (searcherName.equals("baikeDocSearcher")) {
                searcherList.add(Retrieval.getBaikeDocSearcher());
            }
            if (searcherName.equals("bookSearcher")) {
                searcherList.add(Retrieval.getBookSearcher());
            }
            if (searcherName.equals("baikeParaSearcher")) {
                searcherList.add(Retrieval.getBaikeParaSearcher());
            }
            if (searcherName.equals("baikeSentSearcher")) {
                searcherList.add(Retrieval.getBaikeSentSearcher());
            }
            questionInfo.setSearcherList(searcherList);
            MethodContext methodContext = new MethodContext(questionInfo);
            switch (questionInfo.getQuestionCandidateType()) {
                case Const.Q_T_ENTITY:
                    numEntityQuestion++;
                    methodContext.workOutAnswer(this.isNormalizeScore);
                    break;
                case Const.Q_T_SENTENCE:
                    numSentenceQuestion++;
                    methodContext.workOutAnswer(this.isNormalizeScore);
                    break;
            }


            boolean isRight = question.getAnswer().equals(questionInfo.getAnswer());
//            System.out.print(question.getID() + "\t");
            if (isRight) {
                switch (questionInfo.getQuestionCandidateType()) {
                    case Const.Q_T_ENTITY:
                        rightNumEntityQuestion++;
//                        System.out.print("EQ\t");
                        break;
                    case Const.Q_T_SENTENCE:
                        rightNumSentenceQuestion++;
//                        System.out.println("SQ\t");
                        break;
                }
//                System.out.println("right");
            }
//            else System.out.println("wrong");
        }
        System.out.println("EQ\t" + searcherName);
        System.out.println(numEntityQuestion + ":" + rightNumEntityQuestion + "\t" + rightNumEntityQuestion*1.0/numEntityQuestion);
        System.out.println("SQ\t" + searcherName);
        System.out.println(numSentenceQuestion + ":" + rightNumSentenceQuestion + "\t" + rightNumSentenceQuestion*1.0/numSentenceQuestion);
        List<Integer> data = new ArrayList<>();
        data.add(numEntityQuestion);
        data.add(rightNumEntityQuestion);
        data.add(numSentenceQuestion);
        data.add(rightNumSentenceQuestion);
        return data;
    }

    public static void main(String[] args) {
        String filename = "gaokao_all(2011-2015)_data/CountryAllRealHistoryMultipleChoiceQuestion(2011-2015)(744)WithType.xml";
//        filename = "one.xml";
        filename = Const.QUESTIONS_FILEPATH + filename;
        InfluenceOfGrainOfIndex influenceOfGrainOfIndex = new InfluenceOfGrainOfIndex();
        influenceOfGrainOfIndex.init(filename, false);
        List<Integer> data1 = influenceOfGrainOfIndex.iterateQuestion("baikeDocSearcher");
        List<Integer> data2 = influenceOfGrainOfIndex.iterateQuestion("baikeParaSearcher");
        List<Integer> data3 = influenceOfGrainOfIndex.iterateQuestion("baikeSentSearcher");
    }
}