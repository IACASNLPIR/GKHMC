package edu.scores.entityLinks;

import edu.main.Const;
import edu.question.Question;
import edu.scores.Score;
import edu.scores.searchScore.retrivealMethods.Retrieval;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sunder on 2016/5/28.
 * 检索
 */
public class LinkScore extends Score{
    Map<String, Integer> titleToId;
    IndexSearcher searcher;
    public LinkScore(){
        DataLoader dataLoader = new DataLoader();
        this.titleToId = dataLoader.readIDIndex(Const.LINK_INDEX_FILEPATH);
        this.searcher = Retrieval.getLinkSearcher();
    }

    public double[] getScore(Question question){
        int[] counts = new int[4];
        int maxCount = 0;
        int flag = -1; // 第几个选项
        List<String> questionEntities = findUserDefineEntities(question.getQuestionPOS(), question.getQuestionWords());
        for (int i = 0; i < 4; i++){
            List<String> candidateEntities =
                    findUserDefineEntities(question.getCandidatePOS(i), question.getCandidateWords(i));
            int count = getMaxLinkCount(questionEntities, candidateEntities);
            counts[i] = count;
            if (count > maxCount){
                flag = i;
                maxCount = count;
            }
        }
        return normalize(counts);
    }

    int getMaxLinkCount(List<String> entitiesA, List<String> entitiesB){
        int maxCount = 0;
        for (String entity : entitiesB){
            int count = getMaxLinkCount(entitiesA, entity);
            if (count > maxCount) maxCount = count;
        }
        return maxCount;
    }

    int getMaxLinkCount(List<String> entities, String entityB){
        int maxCount = 0;
        for (String entityA : entities){
            String key = buildKey(entityA, entityB);
            int count = getLinkScore(key);
            if (count > maxCount) maxCount = count;
        }
        return maxCount;
    }

    String buildKey(String entityA, String entityB){
        String entitySeparater = "E_E_S";
        int keyID = titleToId.getOrDefault(entityA, 0);
        int id = titleToId.getOrDefault(entityB, 0);
        return keyID > id ? keyID + entitySeparater + id : id + entitySeparater + keyID;
    }

    int getLinkScore(String key){
        TermQuery termQuery = new TermQuery(new Term(Const.LINK_KEY_FIELD, key));
        try {
            TopDocs topDocs = searcher.search(termQuery, 1);
            int docID = topDocs.scoreDocs[0].doc;
            Document document = searcher.doc(docID);
            String count = document.get(Const.LINK_COUNT_FIELD);
            return Integer.valueOf(count);
        }catch (Exception e){
            return 0;
        }
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
        LinkScore search = new LinkScore();
        System.out.println(search.getLinkScore("1193087E_E_S-1294168901"));
    }
}
