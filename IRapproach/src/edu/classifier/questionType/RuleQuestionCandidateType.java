package edu.classifier.questionType;

import edu.main.Const;
import edu.question.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sunder on 2016/5/6.
 * 问题选项类型
 */
public class RuleQuestionCandidateType {
    public static void main(String[] args) {
        RuleQuestionCandidateType type = new RuleQuestionCandidateType();
        Question question = new Question();
        question.setCandidate(0, "北宋中期");
        question.setCandidate(1, "元末明初");
        question.setCandidate(2, "明末清初");
        question.setCandidate(3, "鸦片战争");
        boolean fla = type.isTime(question);
        System.out.println(fla);
    }


        public String judge(Question question, boolean isGSM){
            if (isTime(question)) return Const.Q_T_TIME;
            String candidateType = null;
            if (isGSM){
                int type = question.getCandidateType();
                switch (type){
                    case 0:
                        candidateType = Const.Q_T_SINGLE_ENTITY;
                        break;
                    case 1:
                        candidateType = Const.Q_T_MULTIPLE_ENTITY;
                        break;
                    case 2:
                        candidateType = Const.Q_T_SENTENCE;
                        break;
                }
            }else {
                if (isMultipleEntity(question)) candidateType = Const.Q_T_MULTIPLE_ENTITY;
                else if (isSingleEntity(question)) candidateType = Const.Q_T_SINGLE_ENTITY;
                else candidateType = Const.Q_T_SENTENCE;

                switch (candidateType){
                    case Const.Q_T_SINGLE_ENTITY:
                        question.setCandidateType(0);
                        break;
                    case Const.Q_T_MULTIPLE_ENTITY:
                        question.setCandidateType(1);
                        break;
                    case Const.Q_T_SENTENCE:
                        question.setCandidateType(2);
                        break;
                }
            }
            return candidateType;
        }

        boolean isSingleEntity(Question question){
            // 选项被符号分隔，则不是单实体
            for (int i = 0; i < 4; i++){
                String candidate =  question.getCandidates(i);
                if (candidate.trim().contains("\\s+|、|／|——|，")) return false;
            }
            // 选项平均长度小于阈值
            int lenSum = 0;
            for (int i = 0; i < 4; i++){
                String candidate =  question.getCandidates(i);
                lenSum += candidate.length();
            }
            if (lenSum / 4 < 6) return true;
            // 选项长度在一定范围内，且不包含动词
            int verbNum = 0;
            if (lenSum / 4 < 12){
                for (int i = 0 ; i < 4; i++){
                    List<String> pos = question.getCandidatePOS(i);
                    for (String p : pos) {
                        if (p.startsWith("v")) verbNum++;
                    }
                }
                return verbNum < 1;
            }
            return false;
        }

        boolean isMultipleEntity(Question question){
            // 选项被符号分隔，且包含同样多个单实体，且每个实体长度小于阈值
            int subCandidatesNum = 0;
            List<String> subCandidates = new ArrayList<>();
            for (int i = 0; i < 4; i++){
                String candidate =  question.getCandidates(i);
                String[] cans = candidate.split("\\s+|、|／|——|，|①|②|③|④|与|和");
                if (subCandidatesNum == 0) {
                    subCandidatesNum = cans.length;
                }
                if (cans.length == 1 || subCandidatesNum != cans.length){
                    return false;
                }
                for (String pos : question.getCandidatePOS(i)){
                    if (pos.startsWith("v")){
                        return false;
                    }
                }
                Collections.addAll(subCandidates, cans);
            }
            for (String can : subCandidates){
                if (can.length() > 10) return false;
            }
            return true;
        }

        boolean isSentence(Question question){
            return !isSingleEntity(question) && !isMultipleEntity(question);
        }

        public  boolean isTime(Question question){
            int sum = 0;
            for (int i = 0; i < 4; i++){
                String candidate = question.getCandidates(i);
                if (candidate.matches("\\d+～\\d+") && candidate.length() < 12){
                    sum++;
                }else if (candidate.matches("\\d+[年]～\\d+[年]") && candidate.length() < 12){
                    sum++;
                }else if (candidate.matches(".*\\d+世纪.*") && candidate.length() < 9){
                    sum++;
                }else if (candidate.matches(".*\\d+年") && candidate.length() < 6){
                    sum++;
                }
            }
            return sum >= 3;
        }



}

