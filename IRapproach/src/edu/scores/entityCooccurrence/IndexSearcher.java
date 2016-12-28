package edu.scores.entityCooccurrence;

import edu.main.Const;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;

/**
 * Created by sunder on 2016/5/24.
 * 对共现索引的检索
 */
public class IndexSearcher {
    org.apache.lucene.search.IndexSearcher docCooccurSearcher;  // searcherCode:0
    org.apache.lucene.search.IndexSearcher paraCooccurSearcher; // searcherCode:1
    org.apache.lucene.search.IndexSearcher sentCooccurSearcher; // searcherCode:2
    org.apache.lucene.search.IndexSearcher searcher;


    public int search(String term, int searcherCode){
        searcher = chooseSearcher(searcherCode);
        Term t = new Term(Const.COOCCURANCE_KEY_FIELD, term);
        Query query = new TermQuery(t);
        try {
            TopDocs docs = searcher.search(query, 1);
            if (docs.totalHits == 0) return 0;
            int num = docs.scoreDocs[0].doc;
            Document doc = searcher.doc(num);
            return Integer.parseInt(doc.get(Const.COOCCURANCE_VALUE_FIELD));
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    private org.apache.lucene.search.IndexSearcher initSearcher(String indexFilepath){
        IndexReader indexReader;
        try {
            System.out.println("Open " + indexFilepath);
            Directory directory = FSDirectory.open(new File(indexFilepath));
            indexReader = DirectoryReader.open(directory);
            return new org.apache.lucene.search.IndexSearcher(indexReader);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private org.apache.lucene.search.IndexSearcher initSearcher(org.apache.lucene.search.IndexSearcher searcher,
                                                        String indexFilepath){
        if (searcher == null){
            searcher = initSearcher(indexFilepath);
        }
        return searcher;
    }

    private org.apache.lucene.search.IndexSearcher chooseSearcher(int searcherCode){
        switch (searcherCode){
            case 0:
                docCooccurSearcher = initSearcher(docCooccurSearcher, Const.DOC_CO_OCCURRENCE_INDEX_FILEPATH);
                return docCooccurSearcher;
            case 1:
                paraCooccurSearcher = initSearcher(paraCooccurSearcher, Const.PARA_CO_OCCURRENCE_INDEX_FILEPATH);
                return paraCooccurSearcher;
            case 2:
                sentCooccurSearcher = initSearcher(sentCooccurSearcher, Const.SENT_CO_OCCURRENCE_INDEX_FILEPATH);
                return sentCooccurSearcher;
        }
        return null;
    }
}
