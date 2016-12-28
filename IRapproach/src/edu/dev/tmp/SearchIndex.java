package edu.dev.tmp;

import edu.main.Const;
import edu.tools.ANSJSEG;
import edu.tools.NER;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by sunder on 2015/12/28.
 * 检索
 */
public class SearchIndex {
    public static void main(String[] args) {
        SearchIndex searchIndex = new SearchIndex();
        NER ner = new NER();
        System.out.println(ner.doNER(NlpAnalysis.parse("")));
        while (true){
            searchIndex.search();
        }

//        searchIndex.showDetail();
    }

    IndexSearcher searcher;
    public SearchIndex(){
//                String indexFilepath = Const.Path.BAIKE_INDEX_FILEPATH;
//                String indexFilepath = Const.Path.BOOK_INDEX_FILEPATH;
        String indexFilepath = Const.HISTORY_TIMELINE_INDEX_FILEPATH;
        try {
            Directory directory = FSDirectory.open(new File(indexFilepath));
            IndexReader indexReader = DirectoryReader.open(directory);
            searcher = new IndexSearcher(indexReader);
            NlpAnalysis.parse("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void search(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
            System.out.print("input -> ");
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BooleanQuery booleanQuery = new BooleanQuery();
        List<org.ansj.domain.Term> terms = ANSJSEG.seg(line);

        for(org.ansj.domain.Term term: terms){
            TermQuery termQuery = new TermQuery(new Term(Const.FIELD_CONTENT, term.getName()));
            BooleanClause.Occur occur = BooleanClause.Occur.SHOULD;
//            if(Const.Parameter.USER_DEFINE_SYMBOL.equals(term.getNatureStr())) occur = BooleanClause.Occur.MUST;
            booleanQuery.add(termQuery, occur);
        }
        System.out.println(booleanQuery.toString());
        try {
            TopDocs topDocs = searcher.search(booleanQuery, 3);
            System.out.println(topDocs.totalHits);
//            Arrays.asList(topDocs.scoreDocs).stream().map(sd -> sd.score).forEach(System.out :: println);
//            System.out.println(Arrays.asList(topDocs.scoreDocs).stream().map(sd -> sd.score).reduce(0f, Float::sum));

            for (ScoreDoc scoreDoc : topDocs.scoreDocs){
                int num = scoreDoc.doc;
                Document doc = searcher.doc(num);

                System.out.println(doc.get(Const.FIELD_TIME));
                System.out.println(doc.get(Const.FIELD_CONTENT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showDetail(){
        while(true){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            try {
                System.out.print("input -> ");
                line = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int num = Integer.valueOf(line.trim());
            try {
                Document doc = searcher.doc(num);
                System.out.println(doc.get(Const.FIELD_TITLE));
                System.out.println(doc.get(Const.FIELD_CONTENT));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
