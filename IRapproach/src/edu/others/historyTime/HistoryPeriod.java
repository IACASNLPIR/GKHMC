package edu.others.historyTime;

import edu.main.Const;
import edu.question.Question;
import edu.scores.searchScore.retrivealMethods.QueryBuilder;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2016/1/16.
 * 跟时期有关的方法
 */
public class HistoryPeriod {


    public static BiFunction<Integer, int[], Boolean> isEarlierThanPeriod = (time, period) -> time < period[0];
    public static BiFunction<Integer, int[], Boolean> isLaterThanPeriod = (time, period) -> time > period[1];

    public static boolean isTimeInAnyPeriod(Integer time, List<int[]> periods){
        for(int[] period : periods){
            if(!isLaterThanPeriod.apply(time, period) && !isEarlierThanPeriod.apply(time, period)) return true;
        }
        return false;
    }

    public static boolean isTimeInPeriod(Integer time, int[] period) {
        return period != null && !isLaterThanPeriod.apply(time, period) && !isEarlierThanPeriod.apply(time, period);
    }

    public static boolean isTimeInAnyPeriod(List<Integer> times, List<int[]> periods){
        for(Integer time : times){
            if(isTimeInAnyPeriod(time, periods)) return true;
        }
        return false;
    }

    public static int predictQuestionTimeWithTimeline(Question question, IndexSearcher searcher){
        // 查询问题可能对应的时间
        BooleanQuery query = QueryBuilder.buildORQuestionQuery(question, Const.FIELD_CONTENT);
        int time;
        try {
            ScoreDoc scoreDoc = searcher.search(query, 1).scoreDocs[0];
            int docID = scoreDoc.doc;
            Document document = searcher.doc(docID);
            String strTime = document.get(Const.FIELD_TIME);
            time = Integer.valueOf(strTime);
        } catch (Exception e) {
            time = Const.ERROR_TIME;
        }
        return time;
    }

    public static List<int[]> extractPeriodFrom(String input){
        List<int[]> periods = new ArrayList<>();
        findPeriod("(前\\d+年)", input, periods);
        findPeriod("([^前]\\d+年)", " " + input, periods);
        findPeriod("(前\\d+世纪\\d+年代)", input, periods);
        findPeriod("(前\\d+世纪)[^\\d+年代]]", input, periods);
        findPeriod("([^前]\\d+世纪\\d+年代)", " " + input, periods);
        findPeriod("([^前]\\d+世纪)[^\\d+年代])", input, periods);
        return periods;
    }
    static void findPeriod(String regex, String input, List<int[]> periods){
        Matcher matcher = Pattern.compile(regex).matcher(input);
        if(matcher.find()) periods.add(HistoryTimeNormalize.normalize(matcher.group(1)));
    }
}
