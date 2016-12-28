package edu.dev.QuotationSource;

import edu.classifier.baikeClassify.BaikeClassifier;
import edu.tools.zxr.MyReadIn;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2016/4/28.
 * 找到百科中所有用“”引起来的句子，按标点拆分，对应到该页面的标题
 */
public class BaikeQuotation {
    public static void main(String[] args) {
        BaikeQuotation quotation = new BaikeQuotation();
        quotation.run();
    }

    public void run(){
        String filename = "data/original/topics_all.txt";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)), 'w');
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BaikeClassifier baikeClassifier = new BaikeClassifier();
        MyReadIn myReadIn = new MyReadIn();
        MyReadIn.BaikeReadIn baikeReadIn = myReadIn.new BaikeReadIn();
        int count = 0;
        while (true){
            String document = baikeReadIn.nextDocument();
            if (document == null) break;
//            if (baikeClassifier.isHistoryType(document)) {
                String content = baikeReadIn.nextContent(document);
                String title = baikeReadIn.nextTitle(document);
                List<String> quotationList = findQuotation(content);
                writeOut(bw, title, quotationList);
                count ++;
                if (count % 1000 == 0){
                    System.out.println(count);
                }
//            }
        }
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<String> findQuotation(String content){
        List<String> quotationList = new ArrayList<>();
        String pattern = "“(.*?)”";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        while(m.find()) {
            String result = m.group(1);
            if (result.length() >= 4) {
                List<String> segments = segQuotation(result);
                quotationList.addAll(segments);
            }
        }
        return quotationList;
    }

    List<String> segQuotation(String quotation){
        String[] array = quotation.split("，|。|？|；|：|！| ");
        List<String> segments = new ArrayList<>();
        for (String seg : array){
            if (seg.length() >= 4){
                segments.add(seg);
            }
        }
        return segments;
    }

    void writeOut(BufferedWriter bw, String title, List<String> quotations){
        try {
            bw.write(title);
            bw.newLine();
            for (String quota : quotations){
                bw.write(quota + " ");
            }
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
