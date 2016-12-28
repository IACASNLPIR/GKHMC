package edu.scores.searchScore.retrivealMethods;

import edu.main.Const;

/**
 * Created by sunder on 2016/1/24.
 * 策略工厂
 */
public class MethodContext {
    QuestionInfo questionInfo;
    public MethodContext(QuestionInfo questionInfo) {
        this.questionInfo = questionInfo;
    }

    public Strategy selectStrategy(){
        String answer = "E";
        ConcreteStrategy cs = new ConcreteStrategy();
        Strategy strategy = null;
        switch(this.questionInfo.getQuestionCandidateType()){
//            // 时间格式转换题
//            case Const.Parameter.Q_T_TIME_DESCRIBE:
//                strategy = cs.new ConcreteStrategyForTimeDescribe(questionInfo);
//                break;
            // 时间题
            case Const.Q_T_TIME:
                strategy = cs.new ConcreteStrategyForTime(questionInfo);
                break;
            // 实体题
            case Const.Q_T_ENTITY:
                strategy = cs.new ConcreteStrategyForSingleEntity(questionInfo);
                break;
            // 单实体题
            case Const.Q_T_SINGLE_ENTITY:
                strategy = cs.new ConcreteStrategyForSingleEntity(questionInfo);
                break;
            // 多实体题
            case Const.Q_T_MULTIPLE_ENTITY:
                strategy = cs.new ConcreteStrategyForMultipleEntity(questionInfo);
                strategy = cs.new ConcreteStrategyForSingleEntity(questionInfo);
                break;
            // 句子题
            case Const.Q_T_SENTENCE:
                strategy = cs.new ConcreteStrategyForSentence(questionInfo);
                break;
        }
        return strategy;
    }

    public String workOutAnswer(boolean isNormalizeScore){
        Strategy strategy = selectStrategy();
        String answer =  answerNormalize(strategy.use(isNormalizeScore));
        questionInfo.setAnswer(answer);
        return answer;
    }

    public String answerNormalize(int x){
        switch (x){
            case 0: return "A";
            case 1: return "B";
            case 2: return "C";
            case 3: return "D";
            default: return "E";
        }
    }

}
