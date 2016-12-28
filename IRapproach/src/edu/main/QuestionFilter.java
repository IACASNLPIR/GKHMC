package edu.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/5/26.
 * 设置允许、不允许哪些类型的题目
 */
public class QuestionFilter {

    List<String> allowedQuestionCandidateType = new ArrayList<>();
    public void setQuestionFilter(List<String> allowedQuestionCandidateType){
        this.allowedQuestionCandidateType = allowedQuestionCandidateType;
    }

    public boolean filter(String candidateType){
        return (allowedQuestionCandidateType.contains(candidateType));
    }
}
