package edu.classifier.questionType.bayesianQuestionCandidateType;

/**
 * Created by sunder on 2016/5/13.
 */
public class Probability {
    public static double entityAll = 640.0;
    public static double sentenceAll = 2340.0;
    static EntityType et = new EntityType();
    static SentenceType st = new SentenceType();
    public static double calc(int candidateLength, int verbCount, int userDefineCount, boolean isEntity){
        MyType mt;
        if (isEntity){
            mt = et;
        }
        else{
            mt = st;
        }
        double proPre = mt.probPre();
        double probCandidateLength = mt.probCandidateLength(candidateLength);
        double probVerbCount = mt.proVerbCount(verbCount);
        double probUserDefineCount = mt.proUserDefineCount(userDefineCount);
        return Math.log(proPre) + Math.log(probCandidateLength) + Math.log(probVerbCount) + Math.log(probUserDefineCount);
    }


}

abstract class MyType {
    abstract double probPre();
    abstract double probCandidateLength(int length);
    abstract double proVerbCount(int count);
    abstract double proUserDefineCount(int count);
}
class EntityType extends MyType {
    double all = Probability.entityAll;
    double probPre(){
        return all /(Probability.entityAll + Probability.sentenceAll);
    }
    double probCandidateLength(int length) {
        if (length <= 3) return (4 + 87 + 82)/ all;
        if (length >= 4 && length <= 6) return (115 + 74 + 124) / all;
        if (length >= 7 && length <= 9) return (53 + 33 + 21) / all;
        if (length >= 10 && length <= 12) return (12 + 5 + 6) / all;
        if (length >= 13) return (24)/ all;

//        if (length == 1) return 4/all;
//        if (length == 2) return 87/all;
//        if (length == 3) return 82/all;
//        if (length == 4) return 115/all;
//        if (length == 5) return 74/all;
//        if (length == 6) return 124/all;
//        if (length == 7) return 53/all;
//        if (length == 8) return 33/all;
//        if (length == 9) return 21/all;
//        if (length == 10) return 12/all;
//        if (length == 11) return 5/all;
//        if (length == 12) return 6/all;
//        if (length >=13) return 24/all;
        return 0;
    }

    double proVerbCount(int count){
        if (count == 0) return 615/ all;
        if (count == 1) return 21/ all;
        if (count == 2) return 4/ all;
        return 0;
    }

    double proUserDefineCount(int count){
        if (count == 0) return 319/ all;
        if (count == 1) return 302/ all;
        if (count == 2) return 16/ all;
        if (count == 3) return 1/ all;
        if (count >= 4) return 2/ all;
        return 0;
    }
}

class SentenceType extends MyType {
    double all = Probability.sentenceAll;
    double probPre() {
        return all / (Probability.entityAll + Probability.sentenceAll);
    }
    double probCandidateLength(int length) {
        if (length <= 3) return (0)/ all;
        if (length >= 4 && length <= 6) return (221) / all;
        if (length >= 7 && length <= 9) return (303) / all;
        if (length >= 10 && length <= 12) return (726) / all;
        if (length >= 13) return (1090)/ all;

//        if (length == 1) return 0/all;
//        if (length == 2) return 0/all;
//        if (length == 3) return 0/all;
//        if (length == 4) return 83/all;
//        if (length == 5) return 12/all;
//        if (length == 6) return 126/all;
//        if (length == 7) return 96/all;
//        if (length == 8) return 203/all;
//        if (length == 9) return 4/all;
//        if (length == 10) return 213/all;
//        if (length == 11) return 264/all;
//        if (length == 12) return 249/all;
//        if (length >=13) return 1090/all;
        return 0;
    }
    double proVerbCount(int count){
        if (count == 0) return 303/ all;
        if (count == 1) return 975/ all;
        if (count == 2) return 759/ all;
        if (count == 3) return 221/ all;
        if (count >=4 ) return 82/ all;
        return 0;
    }

    double proUserDefineCount(int count){
        if (count == 0) return 1065/ all;
        if (count == 1) return 999/ all;
        if (count == 2) return 233/ all;
        if (count == 3) return 30/ all;
        if (count >= 4) return 13/ all;
        return 0;
    }
}