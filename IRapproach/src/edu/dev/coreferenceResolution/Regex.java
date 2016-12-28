package edu.dev.coreferenceResolution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2016/6/21.
 */
public class Regex {
    public static void main(String[] args) {
        Regex re = new Regex();
        String line = "在毛泽东回到延安的欢迎晚会上，张治中发表感想说：“毛先生此次去重庆，造成了普遍的最良好的印象，同时，也获得了很大的成就。”这里“很大的成就”主要指";
        System.out.println(re.isMatchedByAllRegexList(line, re.allRegexExpList));
    }


    List<String> changeRegexExps = new ArrayList<String>(){{
        add("[这此](.*?)变化");
        add("[这此](.*?)转变");
    }};

    List<String> eventRegexExps = new ArrayList<String>(){{
        add("[这此](.*?)运动");
        add("[这此](.*?)事件");
        add("[这此](.*?)现象");
        add("[这此](.*?)情形");
    }};

    List<String> opinionRegexExps = new ArrayList<String>(){{
        add("[这此](.*?)主张");
        add("[这此](.*?)观点");
        add("[这此](.*?)认识");
    }};

    List<String> speechRegexExps = new ArrayList<String>(){{
        add("[这此](.*?)言论");
        add("[这此](.*?)[句段]话");
    }};

    List<String> ruleRegexExps = new ArrayList<String>(){{
        add("[这此](.*?)条约");
        add("[这此](.*?)规定");
        add("[这此](.*?)制度");
    }};

    List<String> informationRegexExps = new ArrayList<String>(){{
        add("[这此](.*?)材料");
        add("[这此][一里]“(.*?)”");
    }};

    List<List<String>> allRegexExpList = new ArrayList<>();

    public Regex(){
        allRegexExpList.add(changeRegexExps);
        allRegexExpList.add(eventRegexExps);
        allRegexExpList.add(opinionRegexExps);
        allRegexExpList.add(speechRegexExps);
        allRegexExpList.add(ruleRegexExps);
        allRegexExpList.add(informationRegexExps);
    }

    public boolean isMatchedByRegexList(String line, List<String> expList){
        Pattern p;
        Matcher matcher;
        for(String exp : expList){
            p = Pattern.compile(exp);
            matcher = p.matcher(line);
            if (matcher.find())
                return true;
        }
        return false;
    }

    public int isMatchedByAllRegexList(String line, List<List<String>> expList){
        for(int i = 0; i < expList.size(); i++){
            if(isMatchedByRegexList(line, expList.get(i))){
                return i;
            }
        }
        return -1;
    }
}


