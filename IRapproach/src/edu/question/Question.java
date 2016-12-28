package edu.question;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.question.QuestionCandidate;
import edu.dev.entityLinking.*;
/**
 * Author:             Shawn Guo
 * E-mail:             air_fighter@163.com
 *
 * Create Time:        2015/11/26 09:05
 * Last Modified Time: 2015/12/07 17:13
 *
 * Class Name:         Question
 * Class Function:
 *                     该类的主要功能存储一个问题的题干和对应的四个选项，并且可以存储正确选项。
 *                     答案的初始化字符为“ ”。
 */
public class Question {
    public List<String> questionRealType = new ArrayList<>();       // 真实类型

    public List<Double> getCandidateCertainties() {
        return candidateCertainties;
    }

//    add by zuoxinyu 2016/7/13
    private ArrayList<LinkInfo> entityLinkInfo = new ArrayList<>();//ZXY 存储问题实体链接的信息

    public void setCandidateCertainties(List<Double> candidateCertainties) {
        this.candidateCertainties = candidateCertainties;
    }

    private List<Double> candidateCertainties = new ArrayList<>();

    private String id = null;
    private String question = null;                                 //题目原文
    private HashSet<String> wordSet = new HashSet<>();              //题目分词结果

    private ArrayList<String> questionWords = new ArrayList<>();    //问题分词结果
    private ArrayList<String> questionPOS = new ArrayList<>();      //问题词性标注结果
    private ArrayList<String> questionOriginalPOS = new ArrayList<>();//问题原始词性标注结果
    private ArrayList<String> questionEntities = new ArrayList<>(); //问题实体识别结果

    private ArrayList<QuestionCandidate> candidates = new ArrayList<>();//四个候选答案
    private String answer = null;                                   //正确答案标号
    private int stemType = 0;                                       //问题类型
    private int candidateType = 0;                                  //根据材料确定的类型

    private boolean numericalType;                                  //是否是有序号实体的题目
    private int numericalCandidateNum = 0;
    private ArrayList<QuestionCandidate> numCandidates = new ArrayList<>(); //序号候选


    private String material = new String();                         //材料文本
    private ArrayList<String> materialWords = new ArrayList<>();    //材料分词结果
    private ArrayList<String> materialPOS = new ArrayList<>();      //材料词性标注结果
    private ArrayList<String> materialOriginalPOS = new ArrayList<>();//材料原始词性标注结果
    private ArrayList<String> materialEntities = new ArrayList<>(); //材料实体识别结果

    private String questionStem = new String();                     //题干
    private ArrayList<String> stemWords = new ArrayList<>();    //题干分词结果
    private ArrayList<String> stemPOS = new ArrayList<>();      //题干词性标注结果
    private ArrayList<String> stemOriginalPOS = new ArrayList<>();//题干原始词性标注结果
    private ArrayList<String> stemEntities = new ArrayList<>(); //题干实体识别结果

    private String[] originalMaterialRegex = {                      //原始材料正则表达
            "“.+?”"
    };
    private HashSet<String> orignialMaterials = new HashSet<>();    //提取出来的原始材料

    private HashSet<String> punctuationSet = new HashSet<String>() {{         //需要排除的标点符号
        add("；");
        add("。");
        add("、");
        add("，");
        add("？");
        add("：");
        add("…");
        add("(");
        add(")");
        add("（");
        add("）");
        add("《");
        add("》");
        add("”");
        add("“");
        add("：");
    }}; //抛弃不要的标点符号


    // add by zuoxinyu 2016/7/13
    public boolean addEntityLinkInf(LinkInfo linkInfo){
        entityLinkInfo.add(linkInfo);
        if (linkInfo != null) {
            return true;
        }
        return false;
    }
    // add by zuoxinyu 2016/7/13
    public boolean setEntityLinkInfo(ArrayList<LinkInfo> linkInfos) {
        entityLinkInfo = linkInfos;
        if (entityLinkInfo != null) {
            return true;
        }
        return false;
    }
    // add by zuoxinyu 2016/7/13
    public ArrayList<LinkInfo> getEntityLinkInfo() {
        return entityLinkInfo;
    }


    public boolean setID(String input) {
        id = input;
        if (id == null) {
            return false;
        }
        return true;
    }

    public String getID() {
        return id;
    }

    public boolean setQuestion(String input) {
        question = input;
        if (question == null)
            return false;
        return true;
    }
    public String getQuestion() {
        return question;
    }

    public boolean setMaterialWords(ArrayList<String> words) {
        materialWords = words;
        if (materialWords != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getMaterialWords() {
        return materialWords;
    }

    public boolean setMaterialOriginalPOS(ArrayList<String> pos) {
        materialOriginalPOS = pos;
        if (materialOriginalPOS != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getMaterialOriginalPOS() {
        return materialOriginalPOS;
    }

    public boolean setMaterialPOS(ArrayList<String> pos) {
        materialPOS = pos;
        if (materialPOS != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getMaterialPOS() {
        return materialPOS;
    }

    public boolean setMaterialEntities(ArrayList<String> entities) {
        materialEntities = entities;
        if (materialEntities != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getMaterialEntities() {
        return materialEntities;
    }

    public boolean setStemWords(ArrayList<String> words) {
        stemWords = words;
        if (stemWords != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getStemWords() {
        return stemWords;
    }

    public boolean setStemOriginalPOS(ArrayList<String> pos) {
        stemOriginalPOS = pos;
        if (stemOriginalPOS != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getStemOriginalPOS() {
        return stemOriginalPOS;
    }

    public boolean setStemPOS(ArrayList<String> pos) {
        stemPOS = pos;
        if (stemPOS != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getStemPOS() {
        return stemPOS;
    }

    public boolean setStemEntities(ArrayList<String> entities) {
        stemEntities = entities;
        if (stemEntities != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getStemEntities() {
        return stemEntities;
    }

    public boolean setQuestionWords(ArrayList<String> words) {
        questionWords = words;
        if (questionWords != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getQuestionWords() {
        return questionWords;
    }

    public boolean setQuestionOriginalPOS(ArrayList<String> pos) {
        questionOriginalPOS = pos;
        if (questionOriginalPOS != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getQuestionOriginalPOS() {
        return questionOriginalPOS;
    }

    public boolean setQuestionPOS(ArrayList<String> pos) {
        questionPOS = pos;
        if (questionPOS != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getQuestionPOS() {
        return questionPOS;
    }

    public boolean setQuestionEntities(ArrayList<String> entities) {
        questionEntities = entities;
        if (questionEntities != null) {
            return true;
        }
        return false;
    }
    public ArrayList<String> getQuestionEntities() {
        return questionEntities;
    }

    public boolean setNumericalType(boolean input) {
        numericalType = input;
        return true;
    }
    public boolean getNumericalType() {
        return numericalType;
    }

    public boolean setNumericalCandidateNum (int input) {
        numericalCandidateNum = input;
        if (numericalCandidateNum < 0) {
            return false;
        }
        return true;
    }
    public int getNumericalCandidateNum () {
        return numericalCandidateNum;
    }

    public boolean initNumCandidates(int num) {
        for (int i = 0; i < num; i++) {
            QuestionCandidate candidate = new QuestionCandidate();
            numCandidates.add(candidate);
        }
        numericalCandidateNum = num;
        if (numCandidates.isEmpty()) {
            return false;
        }
        return true;
    }
    public boolean initNumCandidates() {
        for (int i = 0; i < numericalCandidateNum; i++) {
            QuestionCandidate candidate = new QuestionCandidate();
            numCandidates.add(candidate);
        }
        if (numCandidates.isEmpty()) {
            return false;
        }
        return true;
    }
    public boolean addNumCandidates(String input) {
        QuestionCandidate candidate = new QuestionCandidate();
        candidate.setContent(input);
        numCandidates.add(candidate);
        return numCandidates.get(numCandidates.size() - 1).getContent().equals(input.trim());
    }
    public boolean setNumCandidates(int index, String input) {
        numCandidates.get(index).setContent(input.trim());
        return numCandidates.get(index).getContent().equals(input.trim());
    }
    public String getNumCandidates(int index) {
        return numCandidates.get(index).getContent();
    }
    public ArrayList<QuestionCandidate> getNumCandidates() {
        return numCandidates;
    }

    public boolean setNumCandidateWordandPOS(int index,
                                          ArrayList<String> words,
                                          ArrayList<String> pos,
                                          ArrayList<String> ner) {
        numCandidates.get(index).setWords(words);
        numCandidates.get(index).setPOSes(pos);
        numCandidates.get(index).setNERs(ner);
        return false;
    }

    public void initCandidates() {
        for (int i = 0; i < 4; i++) {
            QuestionCandidate candidate = new QuestionCandidate();
            candidates.add(candidate);
        }
    }

    public boolean setCandidate(int index, String input) {
        if (candidates.isEmpty()) {
            initCandidates();
        }
        QuestionCandidate candidate = candidates.get(index);
        candidate.setContent(input.trim());
        return !candidate.getContent().equals("");
    }
    public String getCandidates(int index) {
        return candidates.get(index).getContent();
    }

    public boolean setAnswer(String input) {
        answer = input;
        if (answer == null)
            return false;
        return true;
    }
    public String getAnswer() {
        return answer;
    }

    public boolean setStemType(int stemTypeInt) {
        stemType = stemTypeInt;
        if (stemType == 0)
            return false;
        return true;
    }
    public int getStemType() {
        // 0 填空
        // 1 找不同
        // 2 相同
        // 3 排序
        // 4 否定类
        return stemType;
    }

    public boolean setCandidateType(int typeInt) {
        candidateType = typeInt;
        if (candidateType == 0)
            return false;
        return true;
    }
    public int getCandidateType() {
        // 0 单实体
        // 1 多实体
        // 2 句子
        return candidateType;
    }

    public boolean setMaterial(String input) {
        material = input;
        if (material == null)
            return false;
        return true;
    }
    public String getMaterial() {
        return material;
    }

    private void buildOriginalMaterial() {
        for (String regex : originalMaterialRegex) {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(question);
            while(m.find()) {
                orignialMaterials.add(m.group());
            }
        }
    }
    public HashSet<String> getOrignialMaterials() {
        buildOriginalMaterial();
        return orignialMaterials;
    }


    public boolean setQuestionStem() {
        String questionString = question;
        if (material != null) {
            questionStem = questionString.replace(material, "");
            if (questionStem.startsWith("”")) {
                questionStem = questionStem.replaceFirst("”", "");
            }
        }
        else {
            questionStem = question;
        }
        return true;
    }
    public String getQuestionStem() {
        return questionStem;
    }


    public boolean setCandidateWordandPOS(int index,
                                          ArrayList<String> words,
                                          ArrayList<String> pos,
                                          ArrayList<String> ner) {
        candidates.get(index).setWords(words);
        candidates.get(index).setPOSes(pos);
        candidates.get(index).setNERs(ner);
        return true;
    }

    public ArrayList<String> getCandidateWords(int index) {
        /** annotated by zxr 2016/6/29
        if (!numericalType) {
            return candidates.get(index).getWords();
        }

               char one = "①".toCharArray()[0];
        ArrayList<String> retList = new ArrayList<>();
        for (char numIndex : candidates.get(index).getContent().toCharArray()) {
            if ((numIndex - one) > numericalCandidateNum ||
                    (numIndex - one) < 0) {
                continue;
            }
            retList.addAll(numCandidates.get(numIndex - one).getWords());
        }
        return retList;
         **/
        /**
         * add by zxr 2016/6/29
         */
        return candidates.get(index).getWords();
    }

    public ArrayList<String> getCandidatePOS(int index) {
        return candidates.get(index).getPOSes();
    }

    public ArrayList<String> getCandidateNER(int index) {
        return candidates.get(index).getNERs();
    }

    public ArrayList<QuestionCandidate> getRightCandidates() {
        if (!numericalType) {
            char rightAnswer = answer.charAt(0);
            int answer_index = rightAnswer - 65;
            ArrayList<QuestionCandidate> retList = new ArrayList<>();
            retList.add(candidates.get(answer_index));
            return retList;
        }

        char one = "①".toCharArray()[0];
        char rightAnswer = answer.charAt(0);
        int answer_index = rightAnswer - 65;
        ArrayList<QuestionCandidate> retList = new ArrayList<>();

        for (char index : candidates.get(answer_index).getContent().toCharArray()) {
            if ((index - one) > numericalCandidateNum ||
                    (index - one) < 0) {
                break;
            }
            retList.add(numCandidates.get(index - one));
        }

        return retList;
    }

    public ArrayList<QuestionCandidate> getWrongCandidates() {
        if (!numericalType) {
            char rightAnswer = answer.charAt(0);
            int answer_index = rightAnswer - 65;
            ArrayList<QuestionCandidate> retList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (i != answer_index) {
                    retList.add(candidates.get(i));
                }
            }
            return retList;
        }

        char one = "①".toCharArray()[0];
        char rightAnswer = answer.charAt(0);
        int answer_index = rightAnswer - 65;
        ArrayList<QuestionCandidate> rightList = new ArrayList<>();

        for (char index : candidates.get(answer_index).getContent().toCharArray()) {
            if ((index - one) > numericalCandidateNum ||
                    (index - one) < 0) {
                break;
            }
            rightList.add(numCandidates.get(index - one));
        }

        ArrayList<QuestionCandidate> retList = new ArrayList<>();

        for (QuestionCandidate candidate : numCandidates) {
            if (!rightList.contains(candidate)) {
                retList.add(candidate);
            }
        }

        return retList;
    }

}
