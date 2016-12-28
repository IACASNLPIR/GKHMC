package edu.scores.searchScore.retrivealMethods;

import edu.main.Const;
import edu.others.historyTime.PredictBaikeTime;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by sunder on 2016/1/21.
 * 用于创建各个索引的检索器
 */
public class Retrieval {
    /**
     * add by zxr 2016/6/30
     * change the number to a Const
     */
    private final static int RETURNED_DOC_NUM = 50; // 检索返回xx篇文档

    private static IndexSearcher baikeDocSearcher;
    private static IndexSearcher baikeParaSearcher;
    private static IndexSearcher baikeSentSearcher;
    private static IndexSearcher bookSearcher;
    private static IndexSearcher timelineSearcher;
    private static IndexSearcher linkSearcher;

    public static IndexSearcher getLinkSearcher() {
        if (linkSearcher == null) {
            linkSearcher = Retrieval.buildSearcher(Const.LINK_INDEX_FILEPATH);
        }
        return linkSearcher;
    }

    public static  IndexSearcher getTimelineSearcher() {
        if(timelineSearcher == null)
            timelineSearcher = buildSearcher(Const.HISTORY_TIMELINE_INDEX_FILEPATH);
        return timelineSearcher;
    }

    public static  IndexSearcher getBaikeParaSearcher() {
        if(baikeParaSearcher == null)
            baikeParaSearcher = buildSearcher(Const.BAIKE_PARAGRAPH_INDEX_FILEPATH);
        return baikeParaSearcher;
    }

    public static  IndexSearcher getBaikeSentSearcher() {
        if(baikeSentSearcher == null)
            baikeSentSearcher = buildSearcher(Const.BAIKE_SENTENCE_INDEX_FILEPATH);
        return baikeSentSearcher;
    }

    public static  IndexSearcher getBookSearcher() {
        if(bookSearcher == null)
            bookSearcher = buildSearcher(Const.BOOK_INDEX_FILEPATH);
        return bookSearcher;
    }

    public static  IndexSearcher getBaikeDocSearcher() {
        if(baikeDocSearcher == null)
            baikeDocSearcher = buildSearcher(Const.BAIKE_INDEX_FILEPATH);
        return baikeDocSearcher;
    }

    static IndexSearcher buildSearcher(String indexFilepath){
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

    public static  TopDocs search(IndexSearcher searcher, Query query) throws IOException {
        /**
         * annotated by zxr 2016/6/30
         * don't use 50 directly but use a Const
        return searcher.search(query, 50);
         **/
        /**
         * add by zxr 2016/6/30
         * use a Const instead
         */
        return searcher.search(query, RETURNED_DOC_NUM);
    }

    /**
     * annotated by zxr 2016/6/30
     *
    // 检索并得到前N篇文档的得分
    public static void searchFindTopScore(QuestionInfo questionInfo) throws IOException{
        for(QuestionInfo.QueryInfo queryInfo : questionInfo.getQueryInfoList()){
            TopDocs topDocs = search(queryInfo.searcher, queryInfo.query);
            int count = 0;
            for(ScoreDoc scoreDoc : topDocs.scoreDocs){
                queryInfo.scoreList.add(scoreDoc.score);
                int docID = scoreDoc.doc;
                Document document = queryInfo.searcher.doc(docID);
                if(questionInfo.getBaikeClassifier() == null ||
                        questionInfo.getBaikeClassifier().isHistoryType(document.get(Const.FIELD_CONTENT))){
                    queryInfo.historyScoreList.add(scoreDoc.score);
                    queryInfo.historyDocumentTitle.add(document.get(Const.FIELD_TITLE));
                    count++;
                    if(count >= questionInfo.getTopN()) break;
                }
            }
        }
    }
     */
    /**
     * add by zxr 2016/6/30
     */
    public static void searchFindTopScore(QuestionInfo questionInfo, boolean isNormalize) throws IOException{
        for(QuestionInfo.QueryInfo queryInfo : questionInfo.getQueryInfoList()){
            TopDocs topDocs = search(queryInfo.searcher, queryInfo.query);
            double sum = sumAllScores(topDocs);
            int count = 0;
            for(ScoreDoc scoreDoc : topDocs.scoreDocs){
                queryInfo.scoreList.add(scoreDoc.score);
                int docID = scoreDoc.doc;
                Document document = queryInfo.searcher.doc(docID);
                if(questionInfo.getBaikeClassifier() == null ||
                        questionInfo.getBaikeClassifier().isHistoryType(document.get(Const.FIELD_CONTENT))){
                    if (isNormalize)
                        queryInfo.historyScoreList.add(normalizeTopScore(scoreDoc.score, sum));
                    else
                        queryInfo.historyScoreList.add(scoreDoc.score);
                    queryInfo.historyDocumentTitle.add(document.get(Const.FIELD_TITLE));
                    count++;
                    if(count >= questionInfo.getTopN()) break;
                }
            }

        }
    }

    /**
     * add by zxr 2016/6/30
     * sum up all returned scores
     */
    private static double sumAllScores(TopDocs topDocs){
        double sum = 0;
        for(ScoreDoc scoreDoc : topDocs.scoreDocs){
            sum += scoreDoc.score;
        }
        return sum;
    }

    /**
     * add by zxr 2016/6/30
     * normalize score
     */
    private static float normalizeTopScore(double score, double sum){
//        return (float) (Math.log(score)-Math.log(sum));
        return (float) (score/sum);
    }


    // 检索并得到实体的百科中的时间
    public static void searchFindTime(QuestionInfo questionInfo)throws Exception{
        List<QuestionInfo.QueryInfo> queryInfoList = questionInfo.getQueryInfoList();
        for(QuestionInfo.QueryInfo queryInfo : queryInfoList){
            IndexSearcher searcher = queryInfo.searcher;
            Query query = queryInfo.query;
            TopDocs topDocs = search(searcher, query);
            int count = 0;
            for(ScoreDoc scoreDoc : topDocs.scoreDocs){
                queryInfo.scoreList.add(scoreDoc.score);
                int docId = scoreDoc.doc;
                Document document = searcher.doc(docId);
                if(questionInfo.getBaikeClassifier().isHistoryType(document.get(Const.FIELD_CONTENT))){
                    queryInfo.historyScoreList.add(scoreDoc.score);
                    int time = PredictBaikeTime.getTime(document.get(Const.FIELD_CONTENT));
                    queryInfo.historyDocumentTime.add(time);
                    count++;
                    if(count == questionInfo.getTopN()) break;
                }
            }
        }
    }
}
