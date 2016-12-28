package edu.tools.RNN;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by shawn on 16-3-6.
 */
public class Dict4RNN {
    public HashSet<String> getWordSet(String fileName) {
        HashSet<String> retSet = new HashSet<>();
        File inputFile = new File(fileName);
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        String lineString = "";

        try {
            inputStream = new FileInputStream(inputFile);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            while ( (lineString = bufferedReader.readLine()) != null) {
                for (String word : lineString.trim().split(" ")) {
                    retSet.add(word);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件！");
            return null;
        } catch (IOException e) {
            System.out.println("读取文件失败！");
            return null;
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retSet;
    }

    public void outputDict2File(HashSet<String> wordSet, String embeddingFile, String outFileName) {
        File inputFile = new File(embeddingFile);
        FileInputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        String lineString = "";

        File outputFile = new File(outFileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            PrintStream printStream = new PrintStream(outputStream);
            inputStream = new FileInputStream(inputFile);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            while ( (lineString = bufferedReader.readLine()) != null) {
                lineString = lineString.trim();
                if (wordSet.contains(lineString.split(" ")[0])) {
                    printStream.println(lineString);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("找不到指定文件！");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("读取文件失败！");
            System.exit(-1);
        } finally {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Dict4RNN self = new Dict4RNN();
        HashSet<String> wordSet = self.getWordSet("out/all_words.txt");
        System.out.println(wordSet.size());

        self.outputDict2File(wordSet, "/home/shawn/PycharmProjects/RNNTutorial/data/baike-embeddings.txt",
                "out/dict_all.txt");
    }
}
