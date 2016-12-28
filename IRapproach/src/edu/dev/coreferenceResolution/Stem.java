package edu.dev.coreferenceResolution;

import edu.question.Question;
import edu.question.QuestionAnalyzer;

/**
 * Created by sunder on 2016/6/21.
 */
public class Stem {
    public static void main(String[] args) {
        Stem stem = new Stem();
        stem.init("E:\\Documents\\data_base\\project_smart_history\\questions\\gaokao_all(2011-2015)_data\\CountryAllRealHistoryMultipleChoiceQuestion(2011-2015)(744)WithType.xml");
        stem.stat();
    }

    QuestionAnalyzer analyzer;
    private void init(String questionFilepath) {
        analyzer  = new QuestionAnalyzer();
        analyzer.init(questionFilepath);
    }

    private void stat(){
        for (Question question : analyzer.acquisition.questionList) {
            System.out.println(question.getQuestionStem());
        }
    }
}
