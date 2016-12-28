package edu.scores.searchScore.buildIndex;

import edu.main.Const;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunder on 2016/1/18.
 * 构建细粒度的百科索引：句子级别和段落级别
 */
public class BuildBaikeFragmentIndex extends BuildBaikeIndex {
    public static void main(String[] args) {
        buildBaikeParagraph();
        buildBaikeSentence();
    }

    public BuildBaikeFragmentIndex(String indexFilepath) {
        super(indexFilepath);
    }

    public void setFragmentSize(int fragmentSize) {
        this.fragmentSize = fragmentSize;
    }

    int fragmentSize;
    // 构建Document
    @Override
    List<Document> buildDocument(String baikeDocument){
        String title = parse(baikeDocument, Const.TITLE_START_SYMBOL, Const.TITLE_END_SYMBOL);
        String content = parse(baikeDocument, Const.CONTENT_START_SYMBOL, Const.CONTENT_END_SYMBOL);
        return buildFragmentDocument(title, content, fragmentSize);
    }

    List<Document> buildFragmentDocument(String title, String content, int fragmentSize){
        List<Document> documents = new ArrayList<>();
        int fragmentNum = content.length()/fragmentSize;
        for(int i = 0; i < fragmentNum; i++){
            String fragment;
            if(i == fragmentNum - 1) fragment = content.substring(i*fragmentSize);
            else fragment = content.substring(i*fragmentSize, (1+i)*fragmentSize);
            TextField contentField = new TextField(Const.FIELD_CONTENT, separateWordsWithSpace.apply(fragment), Field.Store.YES);
            StringField titleField = new StringField(Const.FIELD_TITLE, title, Field.Store.YES);
            Document document = new Document();
            document.add(titleField);
            document.add(contentField);
            documents.add(document);
        }
        return documents;
    }

    static void buildBaikeSentence(){
        String indexFilepath = Const.BAIKE_SENTENCE_INDEX_FILEPATH;
        BuildBaikeFragmentIndex bkf = new BuildBaikeFragmentIndex(indexFilepath);
        try {
            bkf.setFragmentSize(100);
            bkf.buildIndex(Const.BAIKE_FILEPATH, "gbk", 6058005);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void buildBaikeParagraph(){
        String indexFilepath = Const.BAIKE_PARAGRAPH_INDEX_FILEPATH;
        BuildBaikeFragmentIndex bkf = new BuildBaikeFragmentIndex(indexFilepath);
        try {
            bkf.setFragmentSize(500);
            bkf.buildIndex(Const.BAIKE_FILEPATH, "gbk", 6058005);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
