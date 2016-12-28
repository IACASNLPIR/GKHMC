package edu.dev.emnlp2016;

import edu.main.Const;
import edu.question.Question;
import edu.question.QuestionAnalyzer;
import edu.classifier.questionType.bayesianQuestionCandidateType.QuestionTypeNBC;
import edu.scores.searchScore.retrivealMethods.MethodContext;
import edu.scores.searchScore.retrivealMethods.QuestionInfo;
import edu.scores.searchScore.retrivealMethods.Retrieval;
import org.apache.lucene.search.IndexSearcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016-5-21.
 */
public class AutoClassifyAndSolve {
    QuestionAnalyzer analyzer;
    boolean isNormalizeScore;

    void init(String xmlFilename, boolean isNormalizeScore) {
        analyzer = new QuestionAnalyzer();
        analyzer.init(xmlFilename);
        this.isNormalizeScore = isNormalizeScore;
    }

    QuestionTypeNBC questionTypeNBC = new QuestionTypeNBC();
    List<Integer> iterateQuestion() {
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
            questionInfo.setQuestionStemType(question.getStemType());
            questionInfo.setRightAnswer(question.getAnswer());
            questionInfo.setQuestionCandidateType(questionTypeNBC.use(question));
            questionInfo.setSingleEntitySearcherRates(singleEntitySearcherRates);
            questionInfo.setSentenceSearcherRates(sentenceSearcherRates);
            List<IndexSearcher> searcherList = questionInfo.getSearcherList();
            if (questionInfo.getQuestionCandidateType().equals(Const.Q_T_ENTITY)) {
                searcherList.add(Retrieval.getBaikeDocSearcher());
            }
            if (questionInfo.getQuestionCandidateType().equals(Const.Q_T_SENTENCE)) {
                searcherList.add(Retrieval.getBaikeParaSearcher());
            }
            questionInfo.setSearcherList(searcherList);
            MethodContext methodContext = new MethodContext(questionInfo);
            switch (questionInfo.getQuestionCandidateType()) {
                case Const.Q_T_ENTITY:
                    numEntityQuestion++;
                    methodContext.workOutAnswer(this.isNormalizeScore);
                    if(question.getAnswer().equals(questionInfo.getAnswer())){
                        rightNumEntityQuestion++;
                    }
                    break;
                case Const.Q_T_SENTENCE:
                    numSentenceQuestion++;
                    methodContext.workOutAnswer(this.isNormalizeScore);
                    if(question.getAnswer().equals(questionInfo.getAnswer())){
                        rightNumSentenceQuestion++;
                    }
                    break;
            }
        }
        System.out.println("EQ\t");
        System.out.println(numEntityQuestion + ":" + rightNumEntityQuestion + "\t" + rightNumEntityQuestion*1.0/numEntityQuestion);
        System.out.println("SQ\t");
        System.out.println(numSentenceQuestion + ":" + rightNumSentenceQuestion + "\t" + rightNumSentenceQuestion*1.0/numSentenceQuestion);
        System.out.println("Average");
        System.out.println((rightNumEntityQuestion + rightNumSentenceQuestion)*1.0/(numEntityQuestion + numSentenceQuestion));
        List<Integer> data = new ArrayList<>();
        data.add(numEntityQuestion);
        data.add(rightNumEntityQuestion);
        data.add(numSentenceQuestion);
        data.add(rightNumSentenceQuestion);
        return data;
    }

    public static void main(String[] args) {
        String filename = "gaokao_all(2011-2015)_data/CountryAllRealHistoryMultipleChoiceQuestion(2011-2015)(744)WithType.xml";
        filename = Const.QUESTIONS_FILEPATH + filename;
        AutoClassifyAndSolve autoClassifyAndSolve = new AutoClassifyAndSolve();
        autoClassifyAndSolve.init(filename, false);
        List<Integer> data = autoClassifyAndSolve.iterateQuestion();
    }
}
