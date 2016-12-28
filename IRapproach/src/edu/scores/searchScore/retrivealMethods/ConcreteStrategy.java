package edu.scores.searchScore.retrivealMethods;

import edu.main.Const;
import edu.question.Question;
import edu.others.historyTime.HistoryPeriod;
import edu.others.historyTime.HistoryTimeNormalize;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by sunder on 2016/2/18.
 * 具体的算法
 */
public class ConcreteStrategy{
    public static void main(String[] args) {
        ConcreteStrategy concreteStrategy = new ConcreteStrategy();
        ConcreteStrategyForTime concreteStrategyForTime = concreteStrategy.new ConcreteStrategyForTime(new QuestionInfo());
        concreteStrategyForTime.use(true);
    }

    public class ConcreteStrategyForSingleEntity extends Strategy{

        public ConcreteStrategyForSingleEntity(QuestionInfo questionInfo) {
            super(questionInfo);
        }

        @Override
        public int use(boolean isScoreNormalize) {
            // 构建查询
            setQuery();
            // 解题
            if(!questionInfo.check()) return -1;
            try {
                Retrieval.searchFindTopScore(this.questionInfo, isScoreNormalize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            questionInfo.setSearcherRates(questionInfo.getSingleEntitySearcherRates());
            List<Double> allSum = sumHistoryScores();
            if(allSum.size() != 0) {
                questionInfo.setScore(allSum);
                if(questionInfo.getQuestionStemType() == 4)
                    return minIndex(allSum);
                else
                    return maxIndex(allSum);
            }
            return -1;
        }


        /**
         * 设置查询
         */
        void setQuery(){
            List<QuestionInfo.QueryInfo> queryInfoList = new ArrayList<>();
            for(IndexSearcher searcher : questionInfo.getSearcherList()) {
                for (int candidateNumber = 0; candidateNumber < 4; candidateNumber++) {
                    QuestionInfo.QueryInfo queryInfo = new QuestionInfo().new QueryInfo();
                    queryInfo.query = QueryBuilder.buildORQuestionWithCandidateWithQuotesQuery(questionInfo.getQuestion(),
                            candidateNumber,
                            Const.FIELD_CONTENT,
                            questionInfo.getCandidateBoostValue());
//                    queryInfo.query = QueryBuilder.buildORQuestionWithCandidateQuery(questionInfo.getQuestion(),
//                            candidateNumber,
//                            Const.FIELD_CONTENT,
//                            questionInfo.getCandidateBoostValue());
//                    queryInfo.query = QueryBuilder.buildORQuestionWithCandidateNounQuery(questionInfo.getQuestion(),
//                            candidateNumber,
//                            Const.Parameter.FIELD_CONTENT,
//                            questionInfo.candidateBoostValue);
//                    queryInfo.query = QueryBuilder.buildORQuestionWithCandidateVerbQuery(questionInfo.getQuestion(),
//                            candidateNumber,
//                            Const.Parameter.FIELD_CONTENT,
//                            questionInfo.candidateBoostValue);
//                    queryInfo.query = QueryBuilder.buildORQuestionWithCandidatePrepositionQuery(questionInfo.getQuestion(),
//                            candidateNumber,
//                            Const.Parameter.FIELD_CONTENT,
//                            questionInfo.candidateBoostValue);
                    queryInfo.searcher = searcher;
                    queryInfo.relatedCandidate = candidateNumber;
                    queryInfoList.add(queryInfo);
                }
            }
            questionInfo.setQueryInfoList(queryInfoList);
        }
    }

    public class ConcreteStrategyForSentence extends Strategy{
        public ConcreteStrategyForSentence(QuestionInfo questionInfo){
            super(questionInfo);
        }

        @Override
        public int use(boolean isScoreNormalize) {
            // 构建查询
            setQuery();
            // 解题
            if(!questionInfo.check()) return -1;
            try {
                Retrieval.searchFindTopScore(this.questionInfo, isScoreNormalize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            questionInfo.setSearcherRates(questionInfo.getSentenceSearcherRates());
            List<Double> allSum = sumHistoryScores();
            if(allSum.size() != 0) {
                questionInfo.setScore(allSum);
                if(questionInfo.getQuestionStemType() == 4)
                    return minIndex(allSum);
                else
                    return maxIndex(allSum);
            }
            return -1;
        }
        /**
         * 设置查询
         */
        void setQuery(){
            List<QuestionInfo.QueryInfo> queryInfoList = new ArrayList<>();
            for(IndexSearcher searcher : questionInfo.getSearcherList()) {
                for (int candidateNumber = 0; candidateNumber < 4; candidateNumber++) {
//                    List<String> candidateNER = questionInfo.getQuestion().getCandidateNER(candidateNumber);
//                    for(String ner : candidateNER) {
                    QuestionInfo.QueryInfo queryInfo = new QuestionInfo().new QueryInfo();
                    queryInfo.query = QueryBuilder.buildORQuestionWithCandidateWithQuotesQuery(questionInfo.getQuestion(),
                            candidateNumber,
                            Const.FIELD_CONTENT,
                            questionInfo.getCandidateBoostValue());
//                    queryInfo.query = QueryBuilder.buildORQuestionWithCandidateQuery(questionInfo.getQuestion(),
//                            candidateNumber,
//                            Const.FIELD_CONTENT,
//                            questionInfo.getCandidateBoostValue());
                    queryInfo.searcher = searcher;
                    queryInfo.relatedCandidate = candidateNumber;
                    queryInfoList.add(queryInfo);
//                    }
                }
            }
            questionInfo.setQueryInfoList(queryInfoList);
        }
    }

    public class ConcreteStrategyForMultipleEntity extends Strategy{
        public ConcreteStrategyForMultipleEntity(QuestionInfo questionInfo) {
            super(questionInfo);
        }

        @Override
        public int use(boolean isScoreNormalize) {
            // 构建查询
            setQuery();
            // 解题
            if(!questionInfo.check()) return -1;
            try {
                Retrieval.searchFindTopScore(this.questionInfo, isScoreNormalize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            questionInfo.setSearcherRates(questionInfo.getSingleEntitySearcherRates());
            List<Double> allSum = sumHistoryScores();
            if(allSum.size() != 0) {
                questionInfo.setScore(allSum);
                if(questionInfo.getQuestionStemType() == 4)
                    return minIndex(allSum);
                else
                    return maxIndex(allSum);
            }
            return -1;
        }


        /**
         * 设置查询
         */
        void setQuery(){
            List<QuestionInfo.QueryInfo> queryInfoList = new ArrayList<>();
            for(IndexSearcher searcher : questionInfo.getSearcherList()) {
                for (int candidateNumber = 0; candidateNumber < 4; candidateNumber++) {
                    String[] candidateEntities =
                            questionInfo.getQuestion().getCandidates(candidateNumber).split("\\s+|、|／|——|，|①|②|③|④|与|和");
                    for (String entity : candidateEntities) {
                        QuestionInfo.QueryInfo queryInfo = new QuestionInfo().new QueryInfo();
                        queryInfo.query = QueryBuilder.buildORQuestionWithCandidateQuery(questionInfo.getQuestion(),
                                entity,
                                Const.FIELD_CONTENT,
                                questionInfo.getCandidateBoostValue());
                        queryInfo.searcher = searcher;
                        queryInfo.relatedCandidate = candidateNumber;
                        queryInfoList.add(queryInfo);
                    }
                }
            }
            questionInfo.setQueryInfoList(queryInfoList);
        }
    }

    public class ConcreteStrategyForTimeDescribe extends Strategy{
        public ConcreteStrategyForTimeDescribe(QuestionInfo questionInfo){
            super(questionInfo);
        }

        @Override
        public int use(boolean isScoreNormalize) {
            // 直接解题
            int result = -1;
            Question question = questionInfo.getQuestion();
            List<int[]> periods = HistoryPeriod.extractPeriodFrom(question.getQuestion());
            for(int[] period : periods){
                for(int i = 0; i < 4; i++){
                    int[] candidatePeriod = HistoryTimeNormalize.normalize(question.getCandidates(i));
                    if(HistoryPeriod.isTimeInPeriod(period[0], candidatePeriod) &&
                            HistoryPeriod.isTimeInPeriod(period[1], candidatePeriod)){
                        result = i;
                        return result;
                    }
                }

            }
            return result;
        }
    }

    public class ConcreteStrategyForTime_OLD extends Strategy{
        public ConcreteStrategyForTime_OLD(QuestionInfo questionInfo){
            super(questionInfo);
        }

        @Override
        public int use(boolean isScoreNormalize) {
            // 设置查询
            setQuery();
            // 得到选项的时期
            Question question = questionInfo.getQuestion();
            for (int i = 0; i < 4; i++) {
                String candidate = question.getCandidates(i);
                int[] period = HistoryTimeNormalize.normalize(candidate);
                if (period == null) period = new int[]{Const.ERROR_TIME, Const.ERROR_TIME};
                questionInfo.candidatePeriod.add(period);
            }
            // 解题
            if(!questionInfo.check()) return -1;
            try {
                Retrieval.searchFindTime(this.questionInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int nearestTime = findNearestTime();
            return findNearestCandidate(nearestTime);
        }

        /**
         * 设置查询
         */
        void setQuery(){
            Question question = questionInfo.getQuestion();
            for(IndexSearcher searcher : questionInfo.getSearcherList()){
                for(int i = 0; i < question.getQuestionWords().size(); i++){
                    QuestionInfo.QueryInfo queryInfo = new QuestionInfo().new QueryInfo();
                    if(Const.USER_DEFINE_SYMBOL.equals(question.getQuestionPOS().get(i))){
                        BooleanQuery booleanQuery = new BooleanQuery();
                        booleanQuery.add(new TermQuery(new Term(
                                        Const.FIELD_TITLE,
                                        question.getQuestionWords().get(i))),
                                BooleanClause.Occur.MUST);
                        queryInfo.query = booleanQuery;
                        queryInfo.searcher = searcher;
                    }
                }
            }
        }

        // 找到题目中与选项时间最接近的实体的时间T，然后选择与T最接近的选项
        int findNearestTime(){
            int minDistance = Const.ERROR_TIME;
            int nearestTime = Const.ERROR_TIME;
            for(QuestionInfo.QueryInfo queryInfo : questionInfo.getQueryInfoList()){
                int distance =
                        calcDistanceOfTimeWithPeriods(queryInfo.historyDocumentTime.get(0), questionInfo.candidatePeriod);
                if(distance < minDistance){
                    minDistance = distance;
                    nearestTime = queryInfo.historyDocumentTime.get(0);
                }
            }
            return nearestTime;
        }
        int calcDistanceOfTimeWithPeriod(int time, int[] period){
            if(HistoryPeriod.isEarlierThanPeriod.apply(time, period)) return period[0] - time;
            else if(HistoryPeriod.isLaterThanPeriod.apply(time, period)) return time - period[1];
            else return 0;
        }
        int calcDistanceOfTimeWithPeriods(int time, List<int []> periods) {
            int sum = 0;
            for (int[] period : periods) {
                int distance = calcDistanceOfTimeWithPeriod(time, period);
                sum += distance;
            }
            return sum;
        }

        int findNearestCandidate(int nearestTime){
            int minDistance = Const.ERROR_TIME;
            int index = -1;
            for(int i = 0; i < questionInfo.candidatePeriod.size(); i++){
                int[] period = questionInfo.candidatePeriod.get(i);
                int distance = calcDistanceOfTimeWithPeriod(nearestTime, period);
                if(distance < minDistance){
                    index = i;
                    minDistance = distance;
                }
            }
            return index;
        }
    }

    public class ConcreteStrategyForTime extends Strategy{
        public ConcreteStrategyForTime(QuestionInfo questionInfo) {
            super(questionInfo);
        }

        @Override
        public int use(boolean isScoreNormalize) {
            Map<String, String> topicsKeyWords = readInTopics();
            Question question = questionInfo.getQuestion();
            String topic = gussTopic(question, topicsKeyWords);
            int time = getTopicPeriod(question, topic);
            if (time == Const.ERROR_TIME) return -1;
            int candidateIndex = select(questionInfo, time);
            System.out.println(topic);
            System.out.println(time);
            System.out.println(candidateIndex);
            return candidateIndex;
        }

        String gussTopic(Question question, Map<String, String> topicsKeyWords){
            Map<String, Integer> topicCount = new HashMap<>();
            for (String w : question.getQuestionWords()){
                if (topicsKeyWords.containsKey(w)){
                    String topic = topicsKeyWords.get(w);
                    int count = topicCount.getOrDefault(topic, 0);
                    topicCount.put(topic, count);
                }
            }


            List<Map.Entry<String, Integer>> infoIds =
                    new ArrayList<Map.Entry<String, Integer>>(topicCount.entrySet());
            //排序
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    //return (o2.getValue() - o1.getValue());
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            if (infoIds.size() > 0) {
                return infoIds.get(0).getKey();
            }else return "";
        }

        int getTopicPeriod(Question question, String topic){
            IndexSearcher timelineSearcher = Retrieval.getTimelineSearcher();
            List<String> words = new ArrayList<>();
            BooleanClause.Occur occur;
            if (topic.equals("")){
                List<String> tmp = question.getQuestionWords();
                for (int i = 0; i < tmp.size(); i++){
                    if (question.getQuestionPOS().get(i).equals("userDefine")){
                        words.add(tmp.get(i));
                    }
                }
                occur = BooleanClause.Occur.SHOULD;
            }else {
                List<org.ansj.domain.Term> terms = NlpAnalysis.parse(topic);
                for (org.ansj.domain.Term term : terms) {
                    words.add(term.getName());
                }
                occur = BooleanClause.Occur.MUST;
            }
            BooleanQuery booleanQuery =
                    QueryBuilder.buildQuery(words, Const.FIELD_CONTENT, occur);
            String timeStr = "";
            try {
                TopDocs topDocs = timelineSearcher.search(booleanQuery, 1);
                for(ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    int docId = scoreDoc.doc;
                    Document document = timelineSearcher.doc(docId);
                    timeStr = document.get(Const.FIELD_TIME);
                    System.out.println(document.get(Const.FIELD_CONTENT));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (timeStr.equals("")) return Const.ERROR_TIME;
            else return Integer.valueOf(timeStr);
        }

        int select(QuestionInfo questionInfo, int time){
            double[] distances = {0, 0, 0, 0};
            double minDistance = Integer.MAX_VALUE;
            int index = -1;
            for (int i = 0; i < 4; i++){
                String candidate = questionInfo.getQuestion().getCandidates(i);
                int[] period = HistoryTimeNormalize.normalize(candidate);
                double distance = 0;
                if (time <= period[1] && time >= period[0]){
                    distance = 0;
                }else if(time > period[1]){
                    distance = time - period[1];
                }else if (time < period[0]){
                    distance = period[0] - time;
                }
                if (distance < minDistance){
                    minDistance = distance;
                    index = i;
                }
                if (distance == 0) distances[i] = 1.0;
                else distances[i] = 1.0/distance;
            }
            List<Double> scores = new ArrayList<>();
            if (minDistance == 0.0){
                for (int i = 0; i < 4; i++){
                    if (i == index) scores.add(1.0);
                    else scores.add(0.0);
                }
            }else{
                double sum = 0;
                for (double v : distances) sum += v;
                for (int i = 0; i < 4; i++){
                    scores.add(distances[i]/sum);
                }
            }
            questionInfo.setScore(scores);
            return index;
        }

        Map<String, String> readInTopics(){
            Map<String, String> topicsKeyWords = new HashMap<>();
            String filename = "data/original/topics_keywords.txt";
            Scanner scanner = null;
            try {
                scanner = new Scanner(new File(filename));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (scanner.hasNextLine()){
                String line = scanner.nextLine().trim();
                String[] sp = line.split("：");
                String topic = sp[0];
                String keywordsLine = sp[1];
                String[] keywords = keywordsLine.split("，");
                for (String kw : keywords){
                    topicsKeyWords.put(kw, topic);
                }
            }
            return topicsKeyWords;
        }
    }
}
