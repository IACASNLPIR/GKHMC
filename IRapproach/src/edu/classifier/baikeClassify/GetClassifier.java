package edu.classifier.baikeClassify;

/**
 * Created by sunder on 2016/8/15.
 * 调用百科分类器，多次调用只初始化一次
 */
public class GetClassifier {
    static BaikeClassifier baikeClassifier = null;
    public static BaikeClassifier get(String info){
        if (baikeClassifier == null){
            baikeClassifier = new BaikeClassifier();
            System.out.println("Init BaikeClassify in " + info);
        }
        System.out.println("Use BaikeClassify in " + info);
        return baikeClassifier;
    }
}
