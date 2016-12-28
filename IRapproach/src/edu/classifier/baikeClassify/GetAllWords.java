package edu.classifier.baikeClassify;

import edu.main.Const;
import edu.tools.zxr.MyReadIn;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.io.*;
import java.util.*;

/**
 * Created by sunder on 2016/1/17.
 * 得到所有词，构成分类的空间
 */
public class GetAllWords {
    Map<String, Integer> wordFrequency = new HashMap<>();
    public static void main(String[] args) {
        Const.init();
        GetAllWords getAllWords = new GetAllWords();
        System.out.println("统计中");
        try {
            getAllWords.statBaike();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("写入文件中");
        List<Map.Entry<String,Integer>> list = getAllWords.sortMap(getAllWords.wordFrequency);
        getAllWords.save2File(list, Const.ALL_WORDS_FILEPATH);
    }

    void statBaike() throws Exception{
        MyReadIn.BaikeReadIn baikeReadIn = new MyReadIn().new BaikeReadIn();
        String document;
        while((document = baikeReadIn.nextDocument()) != null){
            String content = baikeReadIn.nextContent(document);
            List<Term> terms = NlpAnalysis.parse(content);
            stat(terms);
            if(baikeReadIn.getCount() % 10000 == 0) System.out.println( "finished" + baikeReadIn.getCount() * 1.0 / 6058005);
        }
    }

    void stat(List<Term> terms){
        for(Term term : terms) {
            int oldNum = wordFrequency.getOrDefault(term.getName(), 0);
            wordFrequency.put(term.getName(), oldNum + 1);
        }
    }

    List<Map.Entry<String,Integer>> sortMap(Map<String, Integer> map){
        List<Map.Entry<String,Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue() - o1.getValue()));
        return list;

    }
    void save2File(List<Map.Entry<String,Integer>> list, String filepath){
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath)));
            for(Map.Entry<String,Integer> entry : list){
                String word = entry.getKey();
                int frequency = entry.getValue();
                bw.write(frequency + "," + word);
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
