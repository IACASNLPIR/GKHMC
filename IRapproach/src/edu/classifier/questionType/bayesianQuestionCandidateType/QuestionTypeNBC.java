package edu.classifier.questionType.bayesianQuestionCandidateType;

import edu.main.Const;
import edu.question.Question;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by sunder on 2016/5/13.
 */
public class QuestionTypeNBC {
    Classifier cls = null;
    public QuestionTypeNBC(){
        try {
            cls = (Classifier) weka.core.SerializationHelper.read("data/bayesQuestionType.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String run(Question question){
        double entityScoreSum = 0;
        double sentenceScoreSum = 0;
        for (int i = 0; i < 4; i++){
            int candidateLength = question.getCandidates(i).length();
            int verbCount = 0;
            int userDefineCount = 0;
            for (String p : question.getCandidatePOS(i)){
                if (p.startsWith("v")) verbCount++;
                if (p.equals("userDefine")) userDefineCount++;
            }
            double entityScore = Probability.calc(candidateLength, verbCount, userDefineCount, true);
            double sentenceScore = Probability.calc(candidateLength, verbCount, userDefineCount, false);
            entityScoreSum += entityScore;
            sentenceScoreSum += sentenceScore;
        }
//        System.out.println(entityScoreSum);
//        System.out.println(sentenceScoreSum);
        if (entityScoreSum > sentenceScoreSum){
            return Const.Q_T_ENTITY;
        }else {
            return Const.Q_T_SENTENCE;
        }
    }

    public String use(Question question){
        Attribute numeric0 = new Attribute("candidatelengthrange0-3");
        Attribute numeric1 = new Attribute("candidatelengthrange4-6");
        Attribute numeric2 = new Attribute("candidatelengthrange7-9");
        Attribute numeric3 = new Attribute("candidatelengthrange10-12");
        Attribute numeric4 = new Attribute("candidatelengthbigerequal13");
        Attribute numeric5 = new Attribute("candiateverbnum0");
        Attribute numeric6 = new Attribute("candiateverbnum1");
        Attribute numeric7 = new Attribute("candiateverbnum2");
        Attribute numeric8 = new Attribute("candiateverbnumbiggerequal3");
        FastVector labels = new FastVector();
        labels.addElement("entity");
        labels.addElement("sentence");
        Attribute noimal = new Attribute("class", labels);
        FastVector attributes = new FastVector();
        attributes.addElement(numeric0);
        attributes.addElement(numeric1);
        attributes.addElement(numeric2);
        attributes.addElement(numeric3);
        attributes.addElement(numeric4);
        attributes.addElement(numeric5);
        attributes.addElement(numeric6);
        attributes.addElement(numeric7);
        attributes.addElement(numeric8);
        attributes.addElement(noimal);
        QuestionStatistics questionStatistics = Statistics.statQuestion(question);
        Instances dataset = new Instances("Test", attributes, 1);
        double[] values = new double[dataset.numAttributes()];
        for (int i = 0; i < questionStatistics.vector.size(); i++){
            values[i] = questionStatistics.vector.get(i);
        }
        values[dataset.numAttributes()-1] = dataset.attribute(dataset.numAttributes()-1).indexOfValue("entity");
        Instance instance = new Instance(1.0, values);
        dataset.add(instance);
        dataset.setClassIndex(dataset.numAttributes()-1);
        double result = 0;
        try {
            result = cls.classifyInstance(dataset.instance(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result > 0.5) return Const.Q_T_ENTITY;
        else return Const.Q_T_SENTENCE;
    }
}
