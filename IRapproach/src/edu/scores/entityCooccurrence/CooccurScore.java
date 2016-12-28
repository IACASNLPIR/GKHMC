package edu.scores.entityCooccurrence;

import edu.main.Const;
import edu.question.Question;
import edu.question.QuestionAnalyzer;
import edu.scores.Score;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunder on 2016/5/24.
 * 百科实体在篇章、段落、句子级别的共线次数
 */
public class CooccurScore extends Score {
    Map<String, Integer> idIndex = null;
    IndexSearcher searcher = null;

    public void setSearcherCode(int searcherCode) {
        this.searcherCode = searcherCode;
    }

    int searcherCode = 0;
    public double[] getScore(Question question){
        if (searcher == null) {
            searcher = new IndexSearcher();
        }
        try {
            if (idIndex == null) {
                idIndex = readInIDIndex(Const.TITLE_ID_INDEX_FILEPATH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getCooccurScore(question, searcherCode);
    }

    public double[] getCooccurScore(Question question, int searcherCode){
        int[] counts = getCooccurCount(question, searcherCode);
        return normalize(counts);
    }

    int[] getCooccurCount(Question question, int searcherCode){
        int[] counts = new int[4];
        int maxCount = 0;
        int flag = -1; // 第几个选项
        List<String> questionEntities = findUserDefineEntities(question.getQuestionPOS(), question.getQuestionWords());
        for (int i = 0; i < 4; i++){
            List<String> candidateEntities =
                    findUserDefineEntities(question.getCandidatePOS(i), question.getCandidateWords(i));
            int count = getMaxCooccurCount(questionEntities, candidateEntities, searcherCode);
            counts[i] = count;
            if (count > maxCount){
                flag = i;
                maxCount = count;
            }
        }
        return counts;
    }

    List<String> findUserDefineEntities(List<String> posList, List<String> wordsList){
        List<String> entities = new ArrayList<>();
        int i = 0;
        if (posList.size() != wordsList.size() || posList.size() == 0 || wordsList.size() == 0) return entities;
        for (String pos : posList){
            if (pos.equals(Const.USER_DEFINE_SYMBOL)){
                entities.add(wordsList.get(i));
            }
            i++;
        }
        return entities;
    }

    int getMaxCooccurCount(List<String> entitiesA, List<String> entitiesB, int searcherCode){
        int maxCount = 0;
        for (String entity : entitiesA){
            int count = getMaxCooccurCount(entitiesB, entity, searcherCode);
            if (count > maxCount) maxCount = count;
        }
        return maxCount;
    }

    int getMaxCooccurCount(List<String> entities,  String entityB, int searcherCode){
        int maxCount = 0;
        for (String entityA : entities){
            String key = buildKey(entityA, entityB);
            int count = getCooccurCount(key, searcherCode);
            if (count > maxCount) maxCount = count;
        }
        return maxCount;
    }

    int getCooccurCount(String key, int searcherCode){
        return searcher.search(key, searcherCode);
    }

    String buildKey(String entityA, String entityB){
        int idA = idIndex.getOrDefault(entityA, -1);
        int idB = idIndex.getOrDefault(entityB, -1);
        return idA > idB ? idA + ":" + idB : idB + ":" + idA;
    }

    private Map<String, Integer> readInIDIndex(String filepath)throws Exception{
        Map<String, Integer> idIndex = new HashMap<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
        String line;
        while((line = br.readLine()) != null){
            line = line.trim();
            if("".equals(line)) continue;
            String[] segs = line.split(Const.ENTITY_ID_SEPARATOR);
            if(segs.length != 2) continue;
            idIndex.put(segs[0], Integer.valueOf(segs[1]));
        }
        return idIndex;
    }

    double[] normalize(int[] counts){
        int sum  = 0;
        for (int count : counts) sum += count;
        if (sum == 0) sum = 1; // 避免分母为0
        double[] prob = new double[counts.length];
        for (int i = 0; i < counts.length; i++){
            prob[i] = 1.0*counts[i]/sum;
        }
        return prob;
    }

    public static void main(String[] args) {
        String filename =
            "gaokao_all(2011-2015)_data/CountryAllRealHistoryMultipleChoiceQuestion(2011-2015)(744)WithType.xml";
        filename = Const.QUESTIONS_FILEPATH + filename;
        CooccurScore cooccurScore = new CooccurScore();
        QuestionAnalyzer analyzer = new QuestionAnalyzer();
        analyzer.init(filename);
        for (Question question : analyzer.acquisition.questionList) {
            System.out.println("ID: " + question.getID());
            double[] prob = cooccurScore.getCooccurScore(question, 2);
            for (double p : prob) {
                System.out.print(p + "\t");
            }
            System.out.println(" ");
        }
    }
}
