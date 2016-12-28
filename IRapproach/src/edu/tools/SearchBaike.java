package edu.tools;

import edu.main.Const;
import edu.classifier.baikeClassify.BaikeClassifier;
import edu.scores.searchScore.retrivealMethods.Retrieval;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sunder on 2016/3/28.
 * 检索百科，用于查看具体的百科页面，会经常修改
 */
public class SearchBaike {
    public static void main(String[] args) {
        searchByDocId(345595);

    }

    static void searchByDocId(int docID){
        IndexSearcher baikeSearcher = Retrieval.getBaikeDocSearcher();
        try {
            Document document = baikeSearcher.doc(docID);
            System.out.println("===TITLE");
            System.out.println(document.getField(Const.FIELD_TITLE));
            System.out.println("===CONTENT");
            System.out.println(document.getField(Const.FIELD_CONTENT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void searchByContent(){
        List<String> words = buildList("content:缔约国 content:同意 content:欧洲 content:北美 content:一个 content:数个 content:缔约国 content:武装 content:攻击 content:应 content:视为 content:缔约国 content:全体 content:攻击 content:…… content:缔约国 content:应 content:单独 content:会同 " +
                "content:缔约国 content:采取 content:视为 content:必要 content:行动 content:包括 content:武力 " +
                "content:使用 content:段 content:文字 content:出自 content:华沙条约");
//        List<String> words = buildList("content:北大西洋公约");
        BaikeClassifier baikeClassifier = new BaikeClassifier();
        IndexSearcher baikeSearcher = Retrieval.getBaikeDocSearcher();
        BooleanQuery booleanQuery = new BooleanQuery();
        for (int i = 0; i < words.size(); i++) {
            TermQuery termQuery = new TermQuery(new Term(Const.FIELD_CONTENT, words.get(i)));
            if(i == words.size()-1) termQuery.setBoost(2);
            booleanQuery.add(termQuery, BooleanClause.Occur.SHOULD);
        }
        System.out.println(booleanQuery.toString());
        try {
            TopDocs topDocs = baikeSearcher.search(booleanQuery, 50);
            int count = 3;
            for(ScoreDoc scoreDoc : topDocs.scoreDocs){
                int docID = scoreDoc.doc;
                Document document = baikeSearcher.doc(docID);
                if(baikeClassifier.isHistoryType(document.get(Const.FIELD_CONTENT))){
                    System.out.println("===SCORE:\t" + scoreDoc.score);
                    System.out.println("===TITLE");
                    System.out.println(document.getField(Const.FIELD_TITLE));
                    System.out.println("===CONTENT");
                    System.out.println(document.getField(Const.FIELD_CONTENT));
                    System.out.println("");
                    count = count - 1;
                    if(count == 0) break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<String> buildList(String str){
        str = str.replaceAll(" ","");
        String[] tmp = str.split("content:");
        List<String> words = Arrays.asList(tmp);
        System.out.println(words);
        return words;
    }
}
