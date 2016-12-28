package edu.classifier.baikeClassify;

import edu.tools.zxr.MyReadIn;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sunder on 2016/1/18.
 * 构建文本向量
 */
public class Vector {
    public List<String> readInWords(String filename){
        List<String> words = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename)));
            String line;
            while((line = br.readLine()) != null){
                try {
                    String word = line.split(",")[1].trim();
                    int tf = Integer.valueOf(line.split(",")[0]);
                    if (tf > 10 && !"".equals(word)) words.add(word);
                }catch (Exception e){
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("words size: " + words.size());
        return words;
    }

    public List<Integer> turnDoc2Vector(String content, List<String> words){
       Map<String, Integer> stat = new HashMap<>();
       List<Integer> vector = new ArrayList<>();
       List<Term> terms = NlpAnalysis.parse(content);
       for(Term term : terms) {
           int oldNum = stat.getOrDefault(term.getName(), 0);
           stat.put(term.getName(), oldNum + 1);
       }
       vector.addAll(words.stream().map(word -> stat.getOrDefault(word, 0)).collect(Collectors.toList()));
       return vector;
   }

    public String turnVector2String(List<Integer> vector, String label){
        String v = label + " ";
        for(int position = 0; position < vector.size(); position++){
            int value = vector.get(position);
            if(value == 0) continue;
            v = v + (position+1) + ":" + value + " ";
        }
        return v.trim() + "\n";
    }

    public List<String> turnFolder2Vectors(String folderPath, String label, List<String> words, int sampleNumber){
        List<String> vectors = new ArrayList<>();
        MyReadIn.FolderReadIn folderReadIn = new MyReadIn().new FolderReadIn(folderPath);
        MyReadIn.BaikeReadIn baikeReadIn = new MyReadIn().new BaikeReadIn();
        String document;
        int fileNumber = folderReadIn.getFileNumber();
        List<Integer> index = randomNumbersInRange(sampleNumber, 0, fileNumber);
        for (int i : index){
            document = folderReadIn.nextDocument(i);
            String content = baikeReadIn.nextContent(document);
            List<Integer> vector = turnDoc2Vector(content, words);
            String v = turnVector2String(vector, label);
            vectors.add(v);
        }
        System.out.println(vectors.size());
        return vectors;
    }

    public List<Integer> randomNumbersInRange(int size, int min, int max){
        Random random = new Random();
        List<Integer> result = new ArrayList<>();
        int count = 0;
        while(count < size){
            int num = random.nextInt(max)%(max-min+1) + min;
            if(!result.contains(num)){
                result.add(num);
                count++;
            }
        }
        return result;
    }
}
