package edu.scores.searchScore.buildIndex;

import edu.main.Const;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by sunder on 2016/1/4.
 * 构建索引的父类
 */
abstract public class BuildIndex {
    Directory directory;
    IndexWriter indexWriter;
    ArrayList<String> stopWords;
    public BuildIndex(String indexFilepath){
        NlpAnalysis.parse("");
        try {
            Directory directory = FSDirectory.open(new File(indexFilepath));
            IndexWriter indexWriter = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LATEST, new WhitespaceAnalyzer()));
            this.directory = directory;
            this.indexWriter = indexWriter;
            this.stopWords = getStopWords(Const.STOP_WORDS_FILEPATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void buildIndex(String baikeFilepath, String encoding, int total) throws Exception{
        long time1 = System.currentTimeMillis();
        int count = 0;
        String line;
        String baikeDocument = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(baikeFilepath), encoding));
        while(!((line = br.readLine()) == null)){
            line = line.trim();
            baikeDocument += line;
            if(isEndOfDocument(line)){
                List<Document> documents = buildDocument(baikeDocument);
                for(Document document : documents)  this.indexWriter.addDocument(document);
                count += 1;
                baikeDocument = "";
                showStatus(count, total, time1);
            }
        }
        this.indexWriter.close();
        System.out.printf("完成比率：1.00");
    }
    // 是否到达一个文档的结尾
    abstract boolean isEndOfDocument(String line);

    // 构建Document
    abstract List<Document> buildDocument(String baikeDocument);
    // 解析
    abstract String parse(String doc, String startSymbol, String endSymbol);

    void showStatus(int count, int total, long time1){
        if(count % 10000 == 0){
            long time2 = System.currentTimeMillis();
            System.out.printf("完成比率：%f, 总耗时：%fs\n", 1.0*count/total, (time2-time1)/1000.0);
        }
    }

    ArrayList<String> getStopWords(String filepath)throws IOException{
        ArrayList<String> stopWords = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
        String word;
        while ((word = br.readLine()) != null){
            word = word.trim();
            if("".equals(word)) continue;
            stopWords.add(word);
        }
        return stopWords;
    }

    Function<String, String> separateWordsWithSpace =
            line -> NlpAnalysis.parse(line).stream().
                    map(Term::getName).
                    filter(name -> !stopWords.contains(name)).
                    reduce("", (n1, n2) -> n1 + n2+ " " );
}
