package edu.dev.entityLinking;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zxy on 2016/7/7.
 * EntityLinking Regx Match
 */
public class ExtractEntity {
    public static void main(String[] args) {
        ExtractEntity extractEntity = new ExtractEntity();
        String test1 = "李世民又称“唐太宗”,唐朝有“贞观之治”、“开元盛世”，还有很多其他的“盛世时代”。“测试一下，带标点的，‘这个’”。";
        String test2 = "中国四大名著有《西游记》、《红楼梦》、《三国演义》、《水浒传》。";
        ArrayList<String> testInputWords = new ArrayList<>();
        ArrayList<String> testInputPOS = new ArrayList<>();
        String testInputWordsString = "史学家 认为 洪仁玕 思想 不够 充分 却 十分 可贵 方向 走 下去 一定 上道 这种 观点 判断 太平天国运动 方向 走 下去 表现";
        String testInputPOSString = "n v userDefine n userDefine ad d d a n v v d userDefine r n v userDefine n v v v";


        ArrayList<String> Test1 = extractEntity.dealWithQuotes(test1);
        for (int i = 0 ; i < Test1.size() ; i++) {
            System.out.println(Test1.get(i));
        }
        ArrayList<String> Test2 = extractEntity.dealWithBooks(test2);
        for (int i = 0 ; i < Test2.size() ; i++) {
            System.out.println(Test2.get(i));
        }
        ArrayList<String> Test3 = extractEntity.dealWithUserDefine(extractEntity.dealWithSpace(testInputWordsString, testInputWords), extractEntity.dealWithSpace(testInputPOSString, testInputPOS));
        for (int i = 0 ; i < Test3.size() ; i++) {
            System.out.println(Test3.get(i));
        }
    }

    /**Deal the quotes in the questions*/
    public ArrayList<String> dealWithQuotes(String inputString){
        String regex = "“(.+?)”";
        ArrayList<String> quotesList = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputString);
        while(m.find()){
            quotesList.add(m.group(1));
        }
        return quotesList;
    }

    /**Deal the books in the questions*/
    public ArrayList<String> dealWithBooks(String inputString){
        String regex = "《(.+?)》";
        ArrayList<String> booksList = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(inputString);
        while(m.find()){
            booksList.add(m.group(1));
        }
        /*for (int i = 0 ; i <= booksList.size() - 1; i++)
        System.out.println(booksList.get(i));*/
        return booksList;
    }

    /**Deal the userdefine in the questions*/
    public ArrayList<String> dealWithUserDefine(ArrayList<String> inputWords, ArrayList<String> inputPOS){
        ArrayList<String> userDefnine = new ArrayList<>();
        for (int i = 0 ; i <= inputPOS.size() - 1; i++){
            if(inputPOS.get(i).equals("userDefine")){
                userDefnine.add(inputWords.get(i));
            }
        }
        return userDefnine;
    }

    /**Spilt string with space*/
    public ArrayList<String> dealWithSpace(String inputString, ArrayList<String> inputStringList){
        String[] spiltWithSpaceString = inputString.split(" ");
        for (int i = 0; i <= spiltWithSpaceString.length - 1; i++){
            //System.out.println(spiltWithSpace[i]);
            inputStringList.add(spiltWithSpaceString[i]);
        }
        return inputStringList;
    }


}
