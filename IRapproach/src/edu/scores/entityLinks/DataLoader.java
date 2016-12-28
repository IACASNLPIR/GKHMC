package edu.scores.entityLinks;

import edu.main.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by sunder on 2016/5/28.
 * 读入跟百科实体链接相关的文件
 */
public class DataLoader {
    String idIndexSeparater = "E_I_S";
    Map<String, Integer> readIDIndex(String filename){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, Integer> titleToId = new HashMap<>();
        while (scanner.hasNext()){
            String line = scanner.nextLine().trim();
            String[] seg = line.split(idIndexSeparater);
            titleToId.put(seg[0], Integer.valueOf(seg[1]));
        }
        return titleToId;
    }

    String entitySeparater = "E_E_S";
    Map<String, Integer> readLinks(String filename, Map<String, Integer> titleToId){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String[]> linksMap = new HashMap<>();
        Map<String, Integer> linksCountMap = new HashMap<>();
        while (scanner.hasNext()){
            String lineA = scanner.nextLine().trim();
            while (scanner.hasNext()){
                String lineB = scanner.nextLine().trim();
                String keyEntity = lineA;
                String[] entities = lineB.split("\\s+");
//                linksMap.put(key, toLinks);
                int keyID = titleToId.getOrDefault(keyEntity, 0);
                for (String entity : entities){
                    int id = titleToId.getOrDefault(entity, 0);
                    String key = keyID > id ? keyID + entitySeparater + id : id + entitySeparater + keyID;
                    int count = linksCountMap.getOrDefault(key, 0);
                    linksCountMap.put(key, count+1);
                }
                break;
            }
        }
        return linksCountMap;
    }

    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        Map<String, Integer> titleToId = dataLoader.readIDIndex(Const.DATA_BASE_HOME_PATH + "original/linkIdIndex.txt");
        dataLoader.readLinks(Const.DATA_BASE_HOME_PATH + "original/linksfromBaidu_washed.txt", titleToId);
    }
}
