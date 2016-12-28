package edu.others.historyTime;

import edu.main.Const;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2016/1/12.
 * 从正文中抽取时间，选一个作为该事件的时间
 */
public class PredictBaikeTime {
    public static ArrayList<Integer> findTime(String content){
        ArrayList<Integer> times = new ArrayList<>();
        times = findBCTimes(content, times);
        times = findACTimes(content, times);
        return times;
    }

    public static ArrayList<Integer> findBCTimes(String content,  ArrayList<Integer> times){
        String regex = "前\\s(\\d+)年";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            times.add(-1 * Integer.valueOf(matcher.group(1)));
        }
        return times;
    }

    public static ArrayList<Integer> findACTimes(String content,  ArrayList<Integer> times){
        String regex = "[^前]\\s(\\d+)年";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            times.add(Integer.valueOf(matcher.group(1)));
        }
        return times;
    }

    public static Function<ArrayList<Integer>, Integer> findMiddleNumber = times -> {
        if(times.size() == 0) return Const.ERROR_TIME;
        Collections.sort(times);
        return times.get(times.size()/2);
    };

    public static Integer getTime(String content){
        try{
            return findMiddleNumber.apply(findTime(content));
        }catch (Exception e){
            return Const.ERROR_TIME;
        }
    }
}
