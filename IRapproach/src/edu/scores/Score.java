package edu.scores;

import edu.question.Question;

/**
 * Created by sunder on 2016/6/12.
 * 得分的父类
 */
public abstract class Score {
    public abstract double[] getScore(Question question);
}
