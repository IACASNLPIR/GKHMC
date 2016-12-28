package edu.others.historyTime;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2016/1/12.
 * 将各种时期表示法统一表示为【开始时间，结束时间】
 */
public class HistoryTimeNormalize {
    public static int[] normalize(String unNormalizedTime){
        if(isMatch("\\d+世纪\\d+年代", unNormalizedTime)){
            return centuryWithAgeNormalize(unNormalizedTime);
        }else if(isMatch("\\d+世纪", unNormalizedTime)){
            return centuryNormalize(unNormalizedTime);
        }else if(isMatch("\\d+年", unNormalizedTime)){
            return normalizeYear(unNormalizedTime);
        }else{
            return dynastyNormalize(unNormalizedTime);
        }
    }

    static boolean isMatch(String regex, String unNormalizedTime){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unNormalizedTime);
        return matcher.find();
    }
    public static String extractOneTime(String regex, String unNormalizedTime){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unNormalizedTime);
        String time;
        if(matcher.find()) time = matcher.group(1);
        else time = "";
        return time;
    }
    public static boolean isNotFind(String time){return "".equals(time);}
    public static boolean isNotFind(String[] times){return null == times;}
    public static boolean isNotFind(int[] period){return null == period;}

    // xx世纪 -> （xx，yy）
    // 顺序很重要
    public static int[] centuryNormalize(String unNormalizedTime){
        int[] period;
        period = normalizeEarlyBC(unNormalizedTime);
        if(isNotFind(period)){
            period = normalizeMiddleBC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeLaterBC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeBC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeEarlyAC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeMiddleAC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeLaterAC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeAC(unNormalizedTime);
        }
        if(isNotFind(period)) return null;
        else return period;
    }

    public static int[] normalizeEarlyBC(String unNormalizedTime){
        String regex = "前(\\d+)世纪初";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)){
            regex = "前(\\d+)世纪早期";
            time = extractOneTime(regex, unNormalizedTime);
        }
        if(isNotFind(time)) return null;
        int startTime = -100*Integer.valueOf(time);
        int endTime = startTime + 50;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeLaterBC(String unNormalizedTime){
        String regex = "前(\\d+)世纪末";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)){
            regex = "前(\\d+)世纪晚期";
            time = extractOneTime(regex, unNormalizedTime);
        }
        if(isNotFind(time)) return null;
        int startTime = -100*Integer.valueOf(time) + 50;
        int endTime = startTime + 50;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeMiddleBC(String unNormalizedTime){
        String regex = "前(\\d+)世纪中叶";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)) return null;
        int startTime = -100*Integer.valueOf(time)+25;
        int endTime = startTime + 50;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeBC(String unNormalizedTime){
        String regex = "前(\\d+)世纪";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)) return null;
        int startTime = -100*Integer.valueOf(time);
        int endTime = startTime + 100;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeEarlyAC(String unNormalizedTime){
        String regex = "(\\d+)世纪初";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)){
            regex = "(\\d+)世纪早期";
            time = extractOneTime(regex, unNormalizedTime);
        }
        if(isNotFind(time)) return null;
        int startTime = 100*Integer.valueOf(time) - 100;
        int endTime = startTime + 50;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeLaterAC(String unNormalizedTime){
        String regex = "(\\d+)世纪末";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)){
            regex = "(\\d+)世纪晚期";
            time = extractOneTime(regex, unNormalizedTime);
        }
        if(isNotFind(time)) return null;
        int startTime = 100*Integer.valueOf(time) - 50;
        int endTime = startTime + 50;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeMiddleAC(String unNormalizedTime){
        String regex = "(\\d+)世纪中叶";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)) return null;
        int startTime = 100*Integer.valueOf(time) - 75;
        int endTime = startTime + 50;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeAC(String unNormalizedTime){
        String regex = "(\\d+)世纪";
        String time = extractOneTime(regex, unNormalizedTime);
        if(isNotFind(time)) return null;
        int startTime = 100*Integer.valueOf(time) - 100;
        int endTime = startTime + 100;
        return new int[]{startTime, endTime};
    }

    public static String[] extractTwoTime(String regex, String unNormalizedTime){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unNormalizedTime);
        String[] times = new String[2];
        if(matcher.find()){
            times[0] = matcher.group(1);
            times[1] = matcher.group(2);
        }else return null;
        return times;
    }

    public static int[] centuryWithAgeNormalize(String unNormalizedTime){
        int[] period;
        period = normalizeEarlyAgeAC(unNormalizedTime);
        if(isNotFind(period)){
            period = normalizeMiddleAgeAC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeLaterAgeAC(unNormalizedTime);
        }
        if(isNotFind(period)){
            period = normalizeAgeAC(unNormalizedTime);
        }
        if(isNotFind(period)) return null;
        else return period;

    }

    public static int[] normalizeEarlyAgeAC(String unNormalizedTime){
        String regex = "(\\d+)世纪(\\d+)年代初";
        String[] times = extractTwoTime(regex, unNormalizedTime);
        if(isNotFind(times)){
            regex = "(\\d+)世纪(\\d+)年代早期";
            times = extractTwoTime(regex, unNormalizedTime);
        }
        if(isNotFind(times)) return null;
        int century = Integer.valueOf(times[0]);
        int age = Integer.valueOf(times[1]);
        int startTime = 100 * (century - 1) + age;
        int endTime = startTime + 4;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeMiddleAgeAC(String unNormalizedTime){
        String regex = "(\\d+)世纪(\\d+)年代中期";
        String[] times = extractTwoTime(regex, unNormalizedTime);
        if(isNotFind(times)) return null;
        int century = Integer.valueOf(times[0]);
        int age = Integer.valueOf(times[1]);
        int startTime = 100 * (century - 1) + age + 3;
        int endTime = startTime + 5;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeLaterAgeAC(String unNormalizedTime){
        String regex = "(\\d+)世纪(\\d+)年代末";
        String[] times = extractTwoTime(regex, unNormalizedTime);
        if(isNotFind(times)) return null;
        int century = Integer.valueOf(times[0]);
        int age = Integer.valueOf(times[1]);
        int startTime = 100 * (century - 1) + age + 5;
        int endTime = startTime + 4;
        return new int[]{startTime, endTime};
    }

    public static int[] normalizeAgeAC(String unNormalizedTime){
        String regex = "(\\d+)世纪(\\d+)年代";
        String[] times = extractTwoTime(regex, unNormalizedTime);
        if(isNotFind(times)) return null;
        int century = Integer.valueOf(times[0]);
        int age = Integer.valueOf(times[1]);
        int startTime = 100 * (century - 1) + age;
        int endTime = startTime + 9;
        return new int[]{startTime, endTime};
    }

    // xx年 或者 前xx年
    public static int[] normalizeYear(String unNormalizedTime){
        int[] period = normalizeBCYear(unNormalizedTime);
        if(isNotFind(period)){
            period = normalizeACYear(unNormalizedTime);
        }
        return period;
    }

    static int[] normalizeACYear(String unNormalizedTime){
        String regex = "(\\d+)年";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unNormalizedTime);
        if(matcher.find()){
            int time = Integer.valueOf(matcher.group(1));
            return new int[]{time, time};
        }else return null;
    }
    static int[] normalizeBCYear(String unNormalizedTime){
        String regex = "前(\\d+)年";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unNormalizedTime);
        if(matcher.find()){
            int time = Integer.valueOf(matcher.group(1));
            return new int[]{time, time};
        }else return null;
    }

    // xx朝 或者 xx时期
    static int[] dynastyNormalize(String unNormalizedTime){
        String regex = "(.)末(.)初";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unNormalizedTime);
        if(matcher.find()){
            String firstDynasty = matcher.group(1);
            String secondDynasty = matcher.group(2);
            int[] firstPeriod = dynastyNormalize(firstDynasty);
            int[] secondPeriod = dynastyNormalize(secondDynasty);
            return new int[]{firstPeriod[1] - 50, secondPeriod[0] + 50};
        }
        int position;
        if((position = unNormalizedTime.indexOf("朝")) != -1){
            return dynastyTime.get(unNormalizedTime.substring(0, position));
        }else if((position = unNormalizedTime.indexOf("代")) != -1){
            return dynastyTime.get(unNormalizedTime.substring(0, position));
        }if((position = unNormalizedTime.indexOf("时期")) != -1){
            return dynastyTime.get(unNormalizedTime.substring(0, position));
        }else{
            return dynastyTime.getOrDefault(unNormalizedTime, null);
        }
    }

    public static Map<String, int[]> dynastyTime = new HashMap<String, int[]>(){{
        put("夏", new int[]{-2070, -1600});
        put("商", new int[]{-1600, -1046});
        put("周", new int[]{-1046, -256});
        put("西周", new int[]{-1046, -771});
        put("东周", new int[]{-770, -256});
        put("春秋战国", new int[]{-770, -221});
        put("春秋", new int[]{-770, -473});
        put("战国", new int[]{-473, -221});
        put("秦汉", new int[]{-219, 220});
        put("先秦", new int[]{-219, -207});
        put("秦", new int[]{-219, -207});
        put("汉", new int[]{-202, 220});
        put("西汉", new int[]{-202, 8});
        put("玄汉", new int[]{23, 25});
        put("东汉", new int[]{25, 220});
        put("东汉", new int[]{25, 220});
        put("魏晋南北朝", new int[]{220, 589});
        put("魏晋", new int[]{220, 420});
        put("三国", new int[]{220, 280});
        put("魏", new int[]{220, 266});
        put("蜀", new int[]{221, 263});
        put("吴", new int[]{222, 280});
        put("晋", new int[]{266, 420});
        put("西晋", new int[]{266, 316});
        put("东晋", new int[]{317, 420});
        put("十六国", new int[]{304, 439});
        put("南北朝", new int[]{420, 581});
        put("南朝", new int[]{420, 589});
        put("北朝", new int[]{534, 581});
        put("隋唐", new int[]{581, 907});
        put("隋", new int[]{581, 618});
        put("唐", new int[]{618, 907});
        put("五代十国", new int[]{907, 960});
        put("辽宋", new int[]{917, 1276});
        put("辽", new int[]{917, 1125});
        put("宋", new int[]{960, 1276});
        put("北宋", new int[]{960, 1127});
        put("南宋", new int[]{1127, 1279});
        put("西夏", new int[]{1038, 1227});
        put("金", new int[]{1115, 1234});
        put("元明", new int[]{1271, 1644});
        put("元", new int[]{1271, 1368});
        put("明清", new int[]{1368, 1911});
        put("明", new int[]{1368, 1644});
        put("清", new int[]{1636, 1911});
        put("民国", new int[]{1912, 1949});
        put("中华人民共和国", new int[]{1949, 9999});
    }};
}
