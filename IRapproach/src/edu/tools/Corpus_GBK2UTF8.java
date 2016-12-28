package edu.tools;

import java.io.*;

/**
 * Created by shawn on 16-1-12.
 */
public class Corpus_GBK2UTF8 {
    public void GBK_XML2UTF8 (String inputFileName, String outputFileName)
        throws  FileNotFoundException, IOException, ClassNotFoundException {
        File inputFile = new File(inputFileName);
        FileInputStream inputStream = new FileInputStream(inputFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
        BufferedReader reader = new BufferedReader(inputStreamReader);

        File outputFile = new File(outputFileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        PrintStream printer = new PrintStream(outputStream);

        String lineString = "";
        int num = 0;
        int num2 = 0;

        while ( (lineString = reader.readLine()) != null) {
            num++;
            if (num == 1000) {
                num2++;
                if (num2 == 100) {
                    System.out.println();
                    num2 =0;
                }
                System.out.print(".");
                num = 0;
            }
            lineString = lineString.replaceAll("<.*?>http://.*<.*?>", "");
            lineString = lineString.replaceAll("<.*?>https://.*<.*?>", "");
            lineString = lineString.replaceAll("\\[img\\].*?\\[/img\\]", "");
            lineString = lineString.replaceAll("\\[sup\\].*?\\[/sup\\]", "");
            lineString = lineString.replaceAll("\\[.*?\\]", "");
            lineString = lineString.replaceAll("<.*?>", "");
            lineString = lineString.replaceAll("\\s+", "");
            lineString = lineString.replaceAll("　　", "");
            lineString = lineString.trim();
            if (lineString.length() > 0) {
                printer.println(lineString);
            }
        }
    }

    public static void main(String[] args) {
        String inputFile = "data/original/BaiduBaikeMini.xml";
        String outputFile = "out/baike-mini.txt";
        Corpus_GBK2UTF8 self = new Corpus_GBK2UTF8();
        try {
            self.GBK_XML2UTF8(inputFile, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
