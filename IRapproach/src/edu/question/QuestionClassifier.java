package edu.question;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author:             Shawn Guo
 * E-mail:             air_fighter@163.com
 *
 * Create Time:        2015/12/14 08:28
 * Last Modified Time: 2015/12/14
 *
 * Class Name:         QuestionClassifier
 * Class Function:
 *                     该类在使用Question类的基础上，直接通过小众题型的关键词匹配确定题目类型。
 */

public class QuestionClassifier {
    private ArrayList<ArrayList<String>> regexes = new ArrayList<>();

    public void initRegexes() {
        ArrayList<String> regex00 = new ArrayList<String>(){{
            add(".*“.+”处.*是.*");
        }};
        ArrayList<String> regex01 = new ArrayList<String>(){{
            add(".*不同.*");
            add(".*分歧.*");
        }};
        ArrayList<String> regex02 = new ArrayList<String>(){{
            add(".*相同.*");
            add(".*相似的.*");
            //add(".*也.*");
            add(".*都.*");
            add(".*共同.*");
            add(".*不仅.*还.*");
            add(".*下列.*同.*");
        }};
        ArrayList<String> regex03 = new ArrayList<String>(){{
            add(".*排序.*");
            add(".*排列.*");
        }};
        ArrayList<String> regex04 = new ArrayList<String>(){{
            add(".*不.*");
            add(".*无.*");
            add(".*未.*");
            add(".*错误.*");
            add(".*有误.*");
            add(".*违背.*");
        }};
        ArrayList<String> regex05 = new ArrayList<String>(){{
        }};
        regexes.add(regex00);
        regexes.add(regex01);
        regexes.add(regex02);
        regexes.add(regex03);
        regexes.add(regex04);
        regexes.add(regex05);
    }

    public int computeStemType(String input) {
        for (ArrayList<String> regexList : regexes) {
            for (String regex : regexList) {
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(input);
                if (m.find()) {
                    return regexes.indexOf(regexList);
                }
            }
        }
        return 5;
    }


    // by zxr 2016/5/4
    public int judgeQuestionTypeByCandidateType(Question question){
        // single entity 0
        // multiple entity 1
        // sentence 2
        int[] flag = {0, 0, 0};
        for (int i = 0 ; i < 4; i++){
            flag[analyseCandidate(question, i)]++;
        }
        return maxIndex(flag);
    }
    public int analyseCandidate(Question question, int candidateNum){
        String candidate = question.getCandidates(candidateNum);
        // multiple entity?
        String[] r0 = candidate.split("与");
        if (r0.length >= 2) {
            if (r0[1].length() - r0[0].length() < 4 && r0[1].length() - r0[0].length() > -4)
            return 1;
        }

        String[] r1 = candidate.split("\\s+|、|／|——|，");
        if (r1.length >= 2){
            return 1;
        }
        String[] r2 = candidate.split("《");
        if (r2.length > 2){
            return 1;
        }


        // single entity or sentence?
        if (candidate.length() < 6){
            return 0; // single entity
        }else if (candidate.length() < 12){
            List<String> pos = question.getCandidatePOS(candidateNum);
            for (String p : pos) {
                if (p.startsWith("v")) return 2;
            }
            return 0;
        }else{
            return 2; // sentence
        }

    }

    int maxIndex(int[] flag){
        if (flag[0] > flag[1] && flag[0] > flag[2]) {
            return 0;
        }else if(flag[1] > flag[2] && flag[1] > flag[0]) {
            return 1;
        }else{
            return 2;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        QuestionClassifier self = new QuestionClassifier();
        self.initRegexes();

        System.out.println(self.computeStemType(input));
    }
}
