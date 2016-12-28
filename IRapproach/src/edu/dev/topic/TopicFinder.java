package edu.dev.topic;

import edu.question.Question;
import edu.question.QuestionAnalyzer;

import java.io.*;
import java.util.*;

/**
 * Created by sunder on 2016/4/20.
 * 确定问题与哪个主题相关
 */
public class TopicFinder {
    public List<String> readInDict(String dicFilename){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(dicFilename)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        List<String> topicDicts = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null){
                if (!line.startsWith("#")) {
                    String word = line.trim();
                    topicDicts.add(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return topicDicts;
    }

    public List<String> findTopic(String sentence, List<String> topicDicts){
        List<String> topics = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (String word : topicDicts){
            if (sentence.contains(word)){
                map.put(word, sentence.indexOf(word));
            }
        }
        List<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //升序排序
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
            });
        for (Map.Entry<String, Integer> entry : list) {
            topics.add(entry.getKey());
        }
        return topics;
    }

    void testTopicFinder(String questionFilepath, List<String> topicDicts){
        QuestionAnalyzer analyzer;
        analyzer  = new QuestionAnalyzer();
        analyzer.init(questionFilepath);
        int id = 0;
        for (Question question : analyzer.acquisition.questionList) {
            for (int i = 0; i < 4; i++) {
                List<String> topics = findTopic(question.getCandidates(i), topicDicts);
                System.out.println(id + "\t" + topics.toString());
            }
            id++;
        }
    }

    public static void main(String[] args) {
        String dicFilename = "data/zxr_dict";
        String questionFilename = "data/questions/BeijingReal(2005-2014)(87).xml";
        TopicFinder topicFinder = new TopicFinder();
        List<String> topicDicts = topicFinder.readInDict(dicFilename);
        topicFinder.testTopicFinder(questionFilename, topicDicts);
    }

}
