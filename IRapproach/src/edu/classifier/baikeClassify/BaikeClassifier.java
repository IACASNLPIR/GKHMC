package edu.classifier.baikeClassify;

import edu.main.Const;
import libsvm.svm_model;

import java.io.IOException;
import java.util.List;

/**
 * Created by sunder on 2016/1/20.
 * 判断百科是否是历史类
 */
public class BaikeClassifier {
    private Vector myVector = new Vector();
    private svm_model model;
    private List<String> words;

    public BaikeClassifier() {
        try {
            this.model = svm_predict.loadModel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.words = myVector.readInWords(Const.ALL_WORDS_FILEPATH);
    }

    public boolean isHistoryType(String document){
        if(!isHistoryTypeByRules(document)) return false;
        try {
            List<Integer> v = myVector.turnDoc2Vector(document, this.words);
            String vector = myVector.turnVector2String(v, "1");
            return svm_predict.predict(vector, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isHistoryTypeByRules(String document){
        // rule1: movie
        if(document.contains("导    演|导演|演   员|演员|配音|字幕")){
            return false;
        }
        // rule2: book
        if(document.contains("出版社")){
            return false;
        }
        // rule3: game
        if(document.contains("游戏")){
            return false;
        }
        // rule4: 品牌
        if(document.contains("品牌|商标|公司")){
            return false;
        }
        // rule5: 软件
        if(document.contains("软件|版本")){
            return false;
        }
        return true;
    }
}
