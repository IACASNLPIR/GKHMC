package edu.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/5/26.
 * 保存每道题的得分
 */
public class QuestionScores {
    public List<double[]> scores = new ArrayList<>();
    private String id;
    private String candidateRealType = "unKnow";

    public String getCandidatePredictType() {
        return candidatePredictType;
    }

    public void setCandidatePredictType(String candidatePredictType) {
        this.candidatePredictType = candidatePredictType;
    }

    private String candidatePredictType = "unKnow";
    private String correctAnswer;
    private double[] targetScore = {0, 0, 0, 0};

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCandidateRealType() {
        return candidateRealType;
    }

    public void setCandidateRealType(String candidateRealType) {
        this.candidateRealType = candidateRealType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public double[] getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(double[] targetScore) {
        this.targetScore = targetScore;
    }
}
