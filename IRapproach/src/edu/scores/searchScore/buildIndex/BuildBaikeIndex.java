package edu.scores.searchScore.buildIndex;

import edu.main.Const;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2015/12/8.
 * 对百度百科建立索引，建立方法是先对句子调用分词，再用空格将词分开，建立索引时用WhiteSpaceAnalyzer
 */
public class BuildBaikeIndex extends BuildIndex{

    public static void main(String[] args) {
        String indexFilepath = Const.BAIKE_INDEX_FILEPATH;
        BuildBaikeIndex bk = new BuildBaikeIndex(indexFilepath);
        try {
            bk.buildIndex(Const.HOME_PATH, "gbk", 6058005);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BuildBaikeIndex(String indexFilepath) {
        super(indexFilepath);
    }

    // 是否到达一个文档的结尾
    @Override
    boolean isEndOfDocument(String line){
        return Const.DOCUMENT_END_SYMBOL.equals(line);
    }

    // 构建Document
    @Override
    List<Document> buildDocument(String baikeDocument){
        List<Document> documents = new ArrayList<>();
        String title = parse(baikeDocument, Const.TITLE_START_SYMBOL, Const.TITLE_END_SYMBOL);
        String content = parse(baikeDocument, Const.CONTENT_START_SYMBOL, Const.CONTENT_END_SYMBOL);
        StringField titleField = new StringField(Const.FIELD_TITLE, title, Field.Store.YES);
        TextField contentField = new TextField(Const.FIELD_CONTENT, separateWordsWithSpace.apply(content), Field.Store.YES);
        Document document = new Document();
        document.add(titleField);
        document.add(contentField);
        documents.add(document);
        return documents;
    }

    @Override
    protected String parse(String doc, String startSymbol, String endSymbol){
        String pattern = startSymbol + "(.*?)" + endSymbol;
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(doc);
        String result = "";
        if(m.find()){
            result = m.group(1);
        }else{
            System.out.println("No found!");
        }
        // do cleaning
        result = result.replaceAll("\\[sup\\].*?\\[/sup\\]", "");
        result = result.replaceAll("\\[.*?\\]", "");
        return result;
    }
}
