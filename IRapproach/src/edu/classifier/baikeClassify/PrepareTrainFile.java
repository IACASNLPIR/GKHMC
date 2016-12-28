package edu.classifier.baikeClassify;

import edu.main.Const;
import edu.tools.zxr.MyReadIn;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by sunder on 2016/1/19.
 * 准备训练文件：指定部分历史类tag，找到所有包含这些tag的百科作为历史类，剩下的为其他类，作为二分类的训练数据
 *
 * modified by zxr on 2016/7/12
 * change the historyTags and add the noise tags
 */
public class PrepareTrainFile {
    public static void main(String[] args) {
        Const.init();
        PrepareTrainFile prepareTrainFile = new PrepareTrainFile();
        System.out.println("读入百科标签");
        Map<String, String[]> baikeTags = prepareTrainFile.getBaikeTag(Const.BAIKE_INFO_FILEPATH);
        System.out.println("挑选历史类");
        prepareTrainFile.chooseHistoryType(prepareTrainFile.historyTags, prepareTrainFile.unHistoryTags, baikeTags);
    }
    List<String> historyTags = new ArrayList<String>(){{
        add("历史");
        add("中国历史");
        add("历史人物");
        add("战争");
        add("革命");
        add("文物");
        add("朝代");
        add("世界历史");
        add("政治");
        add("宗教");
        add("年号");
        add("地名");
        add("佛教");
        add("制度");
        add("文物");
        add("中国");
    }};

    List<String> unHistoryTags = new ArrayList<String>(){{
        add("电影");
        add("电视剧");
        add("歌曲");
        add("影视");
        add("演员");
        add("交通");
        add("词语");
        add("流行");
        add("生活");
        add("词汇");
        add("旅游");
        add("爱情");
        add("明星");
        add("工具");
        add("水果");
    }};

    Random random = new Random();

    Map<String, String[]> getBaikeTag(String filename){
        HashMap<String, String[]> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "gbk"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] segs = line.split("\t");
                String id = segs[0];
                String tagLine = segs[16];
                String[] tags = tagLine.split(",|\\.|\\s");
                map.put(id, tags);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(map.size());
        return map;
    }

    boolean isAtLeastOneTheSame(List<String> historyTags, String[] tags){
        if(tags == null) return false;
        for(String tag : tags){
            if(historyTags.contains(tag)) return true;
        }
        return false;
    }
    void chooseHistoryType(List<String> historyTags, List<String> unHistoryTags, Map<String, String[]> baikeTags){
        MyReadIn.BaikeReadIn baikeReadIn = new MyReadIn().new BaikeReadIn();
        String document;
        while ((document = baikeReadIn.nextDocument()) != null){
            String id = baikeReadIn.nextID(document);
            String title = baikeReadIn.nextTitle(document);
            if(id != null){
               if(isAtLeastOneTheSame(historyTags, baikeTags.getOrDefault(id, null))){
                   saveAsHistoryType(document, title);
               }else if (isAtLeastOneTheSame(unHistoryTags, baikeTags.getOrDefault(id, null)))
                   // 随机保存负例，概率是0.01
                   if (random.nextInt(100) ==  0)
                       saveAsUnHistoryType(document, title);
            }

            if(baikeReadIn.getCount() % 100000 == 0) System.out.println(1.0 * baikeReadIn.getCount()/6058005);
        }
    }

    void saveAsHistoryType(String document, String title){
        String filename = Const.HISTORY_TYPE_BAIKE_FILEPATH + title + ".html";
        saveFile(document, filename);
    }
    void saveAsUnHistoryType(String document, String title){
        String filename = Const.UN_HISTORY_TYPE_BAIKE_FILEPATH + title + ".html";
        saveFile(document, filename);
    }
    public boolean saveFile(String content, String filename){
        try {
            File file = new File(filename);
            if(file.exists()) return true;
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
            bw.write(content);
            bw.close();
            return true;
        }catch (Exception e){
            System.out.println(filename);
            return false;
        }
    }
}
