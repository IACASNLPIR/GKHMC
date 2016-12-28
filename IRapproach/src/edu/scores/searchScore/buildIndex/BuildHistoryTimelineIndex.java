package edu.scores.searchScore.buildIndex;

import edu.main.Const;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2015/12/31.
 * 对历史事件构建索引
 */
public class BuildHistoryTimelineIndex {
    public static void main(String[] args) {
        BuildHistoryTimelineIndex buildHistoryTimelineIndex = new BuildHistoryTimelineIndex(Const.HISTORY_TIMELINE_INDEX_FILEPATH);
        try {
            buildHistoryTimelineIndex.build(Const.HISTORY_TIMELINE_FILEPATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Directory directory;
    IndexWriter indexWriter;
    ArrayList<String> stopWords;
    public BuildHistoryTimelineIndex(String indexFilepath) {
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

    void build(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
        String line;
        while ((line = br.readLine()) != null){
            Matcher matcher = Pattern.compile("(-?\\d+)年").matcher(line);
            if(!matcher.find()) continue;
            String time = matcher.group(1);
            String[] events = line.split("。");
            for(String event : events){
                addDoc(time, event);
            }
        }
        this.indexWriter.close();
    }

    void addDoc(String time, String event){
        StringField timeField = new StringField(Const.FIELD_TIME, time, Field.Store.YES);
        TextField eventField = new TextField(Const.FIELD_CONTENT, cutLine(event), Field.Store.YES);
        Document document = new Document();
        document.add(timeField);
        document.add(eventField);
        try {
            this.indexWriter.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
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


    String cutLine(String line){
        List<Term> Terms = NlpAnalysis.parse(line);
        String result = "";
        for(Term t : Terms){
            if(stopWords.contains(t.getName())) continue;
            result += t.getName() + " ";
        }
        return result;
    }
}
