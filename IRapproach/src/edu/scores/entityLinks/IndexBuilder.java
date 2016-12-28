package edu.scores.entityLinks;

import edu.main.Const;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by sunder on 2016/5/28.
 * 对链接信息建立索引
 */
public class IndexBuilder {
    void build(String indexFilepath, Map<String, Integer> linksCountMap) {
        IndexWriter indexWriter = null;
        try {
            Directory directory = FSDirectory.open(new File(indexFilepath));
            indexWriter = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LATEST, new WhitespaceAnalyzer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Integer> entry : linksCountMap.entrySet()) {
            String key = entry.getKey();
            String count = "" + entry.getValue();
            addDoc(key, count, indexWriter);
        }
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void addDoc(String key, String count, IndexWriter indexWriter) {
        StringField linkKey = new StringField(Const.LINK_KEY_FIELD, key, Field.Store.YES);
        StringField linkCount = new StringField(Const.LINK_COUNT_FIELD, count, Field.Store.YES);
        Document document = new Document();
        document.add(linkKey);
        document.add(linkCount);
        try {
            indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("读入数据");
        DataLoader dataLoader = new DataLoader();
        Map<String, Integer> titleToId = dataLoader.readIDIndex(Const.DATA_BASE_HOME_PATH + "original/linkIdIndex.txt");
        Map<String, Integer> linksCountMap =
                dataLoader.readLinks(Const.DATA_BASE_HOME_PATH + "original/linksfromBaidu_washed.txt", titleToId);
        IndexBuilder indexBuilder = new IndexBuilder();
        System.out.println("创建索引");
        indexBuilder.build(Const.LINK_INDEX_FILEPATH, linksCountMap);
    }
}
