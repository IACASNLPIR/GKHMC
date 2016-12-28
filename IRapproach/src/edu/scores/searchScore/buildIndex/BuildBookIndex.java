package edu.scores.searchScore.buildIndex;

import edu.main.Const;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2015/12/28.
 * 给课本建立索引
 */
public class BuildBookIndex extends BuildIndex{
    public BuildBookIndex(String indexFilepath) {
        super(indexFilepath);
    }

    public static void main(String[] args) {
        BuildBookIndex buildBookIndex = new BuildBookIndex(Const.BOOK_INDEX_FILEPATH);
        try {
            buildBookIndex.buildIndex("data/original/BookAll.xml", "utf-8", 10000);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 是否到达一个文档的结尾
    @Override
    boolean isEndOfDocument(String line){ return line.startsWith(Const.TOPIC_END_SYMBOL); }

    // 构建Document
    @Override
    List<Document> buildDocument(String topic){
        List<Document> documents = new ArrayList<>();
        topic = parse(topic, "", "");
        TextField contentField = new TextField(Const.FIELD_CONTENT, separateWordsWithSpace.apply(topic), Field.Store.YES);
        Document document = new Document();
        document.add(contentField);
        documents.add(document);
        return documents;
    }

    @Override
    public String parse(String content, String startSymbol, String endSymbol){
        return content.replaceAll("<.*?>", "");
    }

}

