package edu.dev.entityLinking;

import java.io.File;
import java.io.IOException;
import java.util.*;

import edu.main.Const;
import edu.question.Question;
import edu.question.QuestionAnalyzer;
import edu.scores.searchScore.retrivealMethods.Retrieval;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import edu.dev.entityLinking.ExtractEntity;
import edu.scores.searchScore.retrivealMethods.QuestionInfo;
import edu.classifier.baikeClassify.*;


/**
 * Created by zxy on 2016/7/8.
 * Retrieval the entity that we were extracted;
 */
public class EntityRetrieval {

    private IndexSearcher baikeDocSearcher;
    private BaikeClassifier baikeClassifier = null;

    public EntityRetrieval(boolean isClassify){
        baikeDocSearcher = Retrieval.getBaikeDocSearcher();
        if (isClassify) {
            baikeClassifier = GetClassifier.get(EntityRetrieval.class.getName());
        }else {
            baikeClassifier = null;
        }
    }

    public IndexSearcher buildSearcher(String indexFilepath){
        IndexReader indexReader;
        try {
            Directory directory = FSDirectory.open(new File(indexFilepath));
            indexReader = DirectoryReader.open(directory);
            System.out.println("新建检索元：" + indexFilepath);
            return new IndexSearcher(indexReader);
        } catch (IOException e) {
            System.out.println("新建检索器失败：" + indexFilepath);
            return null;
        }
    }

    /**建立书名号与UserDefine查询*/
    public BooleanQuery buildBookAndUserdefineQuery(String word, String field) throws NullPointerException, IOException{
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(new TermQuery(new Term(field, word)),  BooleanClause.Occur.SHOULD);
        return booleanQuery;
    }

    /**对引号中的内容进行分词*/
    public ArrayList<String> spiltQuotes(String word) throws NullPointerException, IOException{
        ArrayList<String> spiltWord = new ArrayList<>();
        List<org.ansj.domain.Term> parse = NlpAnalysis.parse(word);
        for (org.ansj.domain.Term term : parse) {
            spiltWord.add(term.getName());
        }
        return spiltWord;
    }

    /**已经分词的引号内容建立查询*/
    public BooleanQuery buildQuotesQuery(ArrayList<String> words, String field, BooleanClause.Occur occur){
        BooleanQuery booleanQuery = new BooleanQuery();
        for (String word : words) {
            booleanQuery.add(new TermQuery(new Term(field, word)), occur);
        }
        return booleanQuery;
    }


    /**检索书名号与UserDefine返回匹配文档*/
    public Map<String, ArrayList<Integer>> booksAndUserDefineTitleAndDocID(ArrayList<String> inputList) throws IOException, NullPointerException {
        Map<String, ArrayList<Integer>> titleAndDocIDMap = new HashMap<>();
        if (!inputList.isEmpty()) {
            for (int i = 0; i < inputList.size(); i++) {
                ArrayList<Integer> docIDList = new ArrayList<>();
                TopDocs topDocs = search(baikeDocSearcher, buildBookAndUserdefineQuery(inputList.get(i), "title"));
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    //System.out.println(scoreDoc.score);
                    int docID = scoreDoc.doc;
                    Document document = baikeDocSearcher.doc(docID);
                    if (baikeClassifier == null || baikeClassifier.isHistoryType(document.get(Const.FIELD_CONTENT))) {
                        docIDList.add(docID);
                        titleAndDocIDMap.put(inputList.get(i), docIDList);
                    }
                }
            }
        }
        return titleAndDocIDMap;
    }

    /**检索引号中内容返回匹配文档*/
    public Map<String, ArrayList<Integer>> quotesTitleAndDocID(ArrayList<String> inputList) throws IOException, NullPointerException{
        Map<String, ArrayList<Integer>> titleAndDocIDMap = new HashMap<>();
        if (!inputList.isEmpty()) {
            for (int i = 0; i < inputList.size(); i++) {
                ArrayList<Integer> docIDList = new ArrayList<>();
                ArrayList<String> spiltQuotesString = new ArrayList<>();
                spiltQuotesString = spiltQuotes(inputList.get(i));
                //System.out.println(spiltQuotesString);
                TopDocs topDocs = search(baikeDocSearcher, buildQuotesQuery(spiltQuotesString, Const.FIELD_CONTENT, BooleanClause.Occur.MUST));
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    //System.out.println(scoreDoc.score);
                    int docID = scoreDoc.doc;
                    Document document = baikeDocSearcher.doc(docID);
                    if (baikeClassifier == null || baikeClassifier.isHistoryType(document.get(Const.FIELD_CONTENT))) {
                        docIDList.add(docID);
                        titleAndDocIDMap.put(inputList.get(i), docIDList);
                    }
                }
            }
        }
        return titleAndDocIDMap;
    }

    /**检索前10得分的文档*/
    public TopDocs search(IndexSearcher searcher, Query query) throws IOException {
        return searcher.search(query, 10);
    }


    public static void main(String[] args) throws IOException {
        Const.init();
        EntityRetrieval entityRetrieval = new EntityRetrieval(true);
        ExtractEntity extractEntity = new ExtractEntity();
        BaikeClassifier baikeClassifier = new BaikeClassifier();

        String test1 = "李世民又称“唐太宗”,唐朝有“贞观之治”、“开元盛世”，“还有很多其他的盛世时代”。“测试一下句子能否分词”。";
        String test2 = "中国四大名著有《西游记》、《红楼梦》、《三国演义》、《水浒传》。";
        String testInputWordsString = "史学家 认为 洪仁玕 思想 不够 充分 却 十分 可贵 方向 走 下去 一定 上道 这种 观点 判断 太平天国运动 方向 走 下去 表现";
        String testInputPOSString = "n v userDefine n userDefine ad d d a n v v d userDefine r n v userDefine n v v v";

        Map<String, ArrayList<Integer>> testTitleAndDocIDMap = new HashMap<>();
        ArrayList<String> testInputWords = new ArrayList<>();
        ArrayList<String> testInputPOS = new ArrayList<>();



//        testTitleAndDocIDMap = entityRetrieval.booksAndUserDefineTitleAndDocID(extractEntity.dealWithBooks(test2));
        testTitleAndDocIDMap = entityRetrieval.booksAndUserDefineTitleAndDocID(extractEntity.dealWithUserDefine(extractEntity.dealWithSpace(testInputWordsString, testInputWords), extractEntity.dealWithSpace(testInputPOSString, testInputPOS)));
//          testTitleAndDocIDMap = entityRetrieval.quotesTitleAndDocID(extractEntity.dealWithQuotes(test1));
        for (Map.Entry<String, ArrayList<Integer>> entry : testTitleAndDocIDMap.entrySet()) {
            System.out.println("content= " + entry.getKey() + " Value = " + entry.getValue());
        }
    }
}
