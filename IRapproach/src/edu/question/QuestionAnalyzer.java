package edu.question;

import com.shawn.BasicIO;
import edu.tools.NER;
import edu.tools.ANSJSEG;
import edu.tools.OutputQuestion;

import edu.tools.RNN.Dict4RNN;
import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;

/**
 * Author:             Shawn Guo
 * E-mail:             air_fighter@163.com
 *
 * Create Time:        2015/11/26 10:37
 * Last Modified Time: 2015/12/30 15:29
 *
 * Class Name:         QuestionAnalyzer
 * Class Function:
 *                     该类在使用Question类的基础上，对题干进行分析，确定题目类型。
 */

public class QuestionAnalyzer {
    private String[] regexes = {                                        //切分标识符
            ".+。”",
            ".+！”",
            ".+？”",
            ".+。"
            };


    public QuestionAcquisition acquisition = new QuestionAcquisition();
    private NER ner = new NER();

    public void init(String questionFile, String answerFile) {
        System.out.print("Getting questions...");
        try {
            acquisition.initFromXMLandAnswerFile(questionFile, answerFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");

        System.out.print("Analyzer: building Question's material...");
        buildQuestionMaterial(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' words, pos, and entities...");
        buildQuestionWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Materials' words, pos, and entities...");
        buildMaterialWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Stems' words, pos, and entities...");
        buildStemWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Questions' stemType...");
        buildQuestionType(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' candidateType...");
        buildCandidateType(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' Candidates word&POS&NERs...");
        buildNumericalCandidatesWordPOSandNER(acquisition.questionList);
        System.out.println("Done!");
    }

    public void init(String questionFile, String answerFile, boolean xf) {
        System.out.print("Getting questions...");
        try {
            acquisition.initFromXFXMLandAnswerFile(questionFile, answerFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");

        System.out.print("Analyzer: building Question's material...");
        buildQuestionMaterial(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' words, pos, and entities...");
        buildQuestionWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Materials' words, pos, and entities...");
        buildMaterialWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Stems' words, pos, and entities...");
        buildStemWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Questions' stemType...");
        buildQuestionType(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' candidateType...");
        buildCandidateType(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' Candidates word&POS&NERs...");
        buildNumericalCandidatesWordPOSandNER(acquisition.questionList);
        System.out.println("Done!");
    }

    public void init(String questionFile) {
        System.out.print("Getting questions...");
        try {
            acquisition.initFromXML(questionFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");

        System.out.print("Analyzer: building Question's material...");
        buildQuestionMaterial(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' words, pos, and entities...");
        buildQuestionWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Materials' words, pos, and entities...");
        buildMaterialWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Stems' words, pos, and entities...");
        buildStemWordsPOSEntities(acquisition.questionList);
        System.out.println("Done!");

        System.out.println("Analyzer: building Questions' stemType...");
        buildQuestionType(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' candidateType...");
        buildCandidateType(acquisition.questionList);
        System.out.println("Done!");

        System.out.print("Analyzer: building Questions' Candidates word&POS&NERs...");
        buildNumericalCandidatesWordPOSandNER(acquisition.questionList);
        System.out.println("Done!");

    }



    public void buildQuestionMaterial(ArrayList<Question> questionList) {
        //int questionNum = 0;  //for debug
        for (Question question : questionList) {
            //questionNum++;
            String questionString = question.getQuestion();
            boolean findInRegex = false;
            for (String regex : regexes) {
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(questionString);
                if (m.find()) {
                    question.setMaterial(m.group());
                    question.setQuestionStem();
                    findInRegex = true;
                    break;
                }
            }
            if (findInRegex && question.getMaterial().length() < (question.getQuestionStem().length() - 7)) {
                Pattern p = Pattern.compile(".+，");
                String questionStemString = question.getQuestionStem();
                Matcher m = p.matcher(questionStemString);
                if (m.find()) {
                    question.setMaterial(m.group());
                    question.setQuestionStem();
                }
            }

            if (!findInRegex) {
                Pattern p = Pattern.compile(".+，");
                Matcher m = p.matcher(questionString);
                if (m.find() && m.group().length() >= 7) {
                    question.setMaterial(m.group());
                }
               question.setQuestionStem();
            }
        }
    }

    /**
    public void buildQuestionWordsPOSEntities(ArrayList<Question> questionList) {
        for (Question question : questionList) {
            List<Term> terms = ANSJSEG.seg(question.getQuestion());
            new NatureRecognition(terms).recognition();
            FilterModifWord.modifResult(terms);
            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> pos = new ArrayList<>();
            ArrayList<String> entities = new ArrayList<>();

            String nerInput = new String();
            for (Term term : terms) {
                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                    words.add(term.toString().split("/")[0]);
                    nerInput += term.toString().split("/")[0];
                    nerInput += " ";
                    pos.add(term.toString().split("/")[1]);
                }
            }

            System.err.println(nerInput);
            String nerOutput = ner.doNER(nerInput);
            for (String word : nerOutput.split(" ")) {
                if (word.length() >= 3 && word.contains("/")) {
                    entities.add(word.split("/")[1]);
                }
            }

            question.setQuestionWords(words);
            question.setQuestionPOS(pos);
            question.setQuestionEntities(entities);
        }
    }**/

    public void buildQuestionWordsPOSEntities(ArrayList<Question> questionList) {
        for (Question question : questionList) {
            List<Term> terms = ANSJSEG.seg(question.getQuestion());
            new NatureRecognition(terms).recognition();
            FilterModifWord.modifResult(terms);

            HashMap<String, String> originalPOSHash = new HashMap<>();
            List<Term> parse = NlpAnalysis.parse(question.getQuestion());
            for (Term term : parse) {
                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                    originalPOSHash.put(term.toString().split("/")[0], term.toString().split("/")[1]);
                }
            }

            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> pos = new ArrayList<>();
            ArrayList<String> entities = new ArrayList<>();
            ArrayList<String> originalPOS = new ArrayList<>();

            String nerInput = new String();
            for (Term term : terms) {
                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                    words.add(term.toString().split("/")[0]);
                    nerInput += term.toString().split("/")[0];
                    nerInput += " ";
                    pos.add(term.toString().split("/")[1]);
                    if (originalPOSHash.containsKey(term.toString().split("/")[0])) {
                        originalPOS.add(originalPOSHash.get(term.toString().split("/")[0]));
                    }
                    else {
                        originalPOS.add(term.toString().split("/")[1]);
                    }
                }
            }

            System.err.println(nerInput);
            String nerOutput = ner.doNER(nerInput);
            for (String word : nerOutput.split(" ")) {
                if (word.length() >= 3 && word.contains("/")) {
                    entities.add(word.split("/")[1]);
                }
            }

            question.setQuestionWords(words);
            question.setQuestionPOS(pos);
            question.setQuestionEntities(entities);
            question.setQuestionOriginalPOS(originalPOS);
        }
    }

    public void buildMaterialWordsPOSEntities(ArrayList<Question> questionList) {
        for (Question question : questionList) {
            List<Term> terms = ANSJSEG.seg(question.getMaterial());
            new NatureRecognition(terms).recognition();
            FilterModifWord.modifResult(terms);

            HashMap<String, String> originalPOSHash = new HashMap<>();
            List<Term> parse = NlpAnalysis.parse(question.getMaterial());
            for (Term term : parse) {
                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                    originalPOSHash.put(term.toString().split("/")[0], term.toString().split("/")[1]);
                }
            }

            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> pos = new ArrayList<>();
            ArrayList<String> entities = new ArrayList<>();
            ArrayList<String> originalPOS = new ArrayList<>();

            String nerInput = new String();
            for (Term term : terms) {
                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                    words.add(term.toString().split("/")[0]);
                    nerInput += term.toString().split("/")[0];
                    nerInput += " ";
                    pos.add(term.toString().split("/")[1]);
                    if (originalPOSHash.containsKey(term.toString().split("/")[0])) {
                        originalPOS.add(originalPOSHash.get(term.toString().split("/")[0]));
                    }
                    else {
                        originalPOS.add(term.toString().split("/")[1]);
                    }
                }
            }

            System.err.println(nerInput);
            String nerOutput = ner.doNER(nerInput);
            for (String word : nerOutput.split(" ")) {
                if (word.length() >= 3 && word.contains("/")) {
                    entities.add(word.split("/")[1]);
                }
            }

            question.setMaterialWords(words);
            question.setMaterialPOS(pos);
            question.setMaterialEntities(entities);
            question.setMaterialOriginalPOS(originalPOS);
        }
    }

    public void buildStemWordsPOSEntities(ArrayList<Question> questionList) {
        for (Question question : questionList) {
            List<Term> terms = ANSJSEG.seg(question.getQuestionStem());
            new NatureRecognition(terms).recognition();
            FilterModifWord.modifResult(terms);

            HashMap<String, String> originalPOSHash = new HashMap<>();
            List<Term> parse = NlpAnalysis.parse(question.getQuestionStem());
            for (Term term : parse) {
                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                    originalPOSHash.put(term.toString().split("/")[0], term.toString().split("/")[1]);
                }
            }

            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> pos = new ArrayList<>();
            ArrayList<String> entities = new ArrayList<>();
            ArrayList<String> originalPOS = new ArrayList<>();

            String nerInput = new String();
            for (Term term : terms) {
                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                    words.add(term.toString().split("/")[0]);
                    nerInput += term.toString().split("/")[0];
                    nerInput += " ";
                    pos.add(term.toString().split("/")[1]);
                    if (originalPOSHash.containsKey(term.toString().split("/")[0])) {
                        originalPOS.add(originalPOSHash.get(term.toString().split("/")[0]));
                    }
                    else {
                        originalPOS.add(term.toString().split("/")[1]);
                    }
                }
            }

            System.err.println(nerInput);
            String nerOutput = ner.doNER(nerInput);
            for (String word : nerOutput.split(" ")) {
                if (word.length() >= 3 && word.contains("/")) {
                    entities.add(word.split("/")[1]);
                }
            }

            question.setStemWords(words);
            question.setStemPOS(pos);
            question.setStemEntities(entities);
            question.setStemOriginalPOS(originalPOS);
        }
    }


    public void buildQuestionType(ArrayList<Question> questionList) {
        QuestionClassifier classifier = new QuestionClassifier();
        classifier.initRegexes();
        for (Question question : questionList) {
            String questionStem = question.getQuestionStem();
            int type = classifier.computeStemType(questionStem);
            question.setStemType(type);
        }
    }

    public void buildRightAnswer(ArrayList<Question> questionList, String fileName) {
        ArrayList<String> answers = new ArrayList<>();
        try {
             answers = BasicIO.readFile2StringArray(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        for (Question question : questionList) {
            question.setAnswer(answers.get(questionList.indexOf(question)));
        }
    }

    public void testQuestionType(ArrayList<Question> questionList, String fileName) {
        int[] readType = new int[questionList.size()];
        int rightNum = 0;

        try {
            readType = BasicIO.readFile2IntArray(questionList.size(), fileName);
            for (int index = 0; index < questionList.size(); index++) {
                if(questionList.get(index).getStemType() != readType[index]) {
                    System.out.println("#" + Integer.sum(index, 1) + "\t" + questionList.get(index).getQuestionStem());
                    System.out.print("\ttype:" + questionList.get(index).getStemType() + "\t");
                    System.out.println("rightType:" + readType[index]);
                }
                else {
                    rightNum++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("共有" + questionList.size() + "道题目，其中" + rightNum +"道分类正确。");
    }


    public void buildCandidateType(ArrayList<Question> questionList) {
        String[] multiConj = {
                "、",
                "与",
                "和",
                "——",
                "，",
                "→"
        };

        for (Question question : questionList) {
            //System.out.println("#" + questionList.indexOf(question));
            int singleEntity = 0;
            int multiEntity = 0;
            int sentence = 0;
            for (int i = 0; i <= 3; i++) {
                try {
                    List<Term> tokenTerms = NlpAnalysis.parse(question.getCandidates(i));
                    ArrayList<String> words = new ArrayList<>();
                    ArrayList<String> pos = new ArrayList<>();
                    ArrayList<String> nerResult = new ArrayList<>();

                    for (Term term : tokenTerms) {
                        if (term.toString().length() >=3 && term.toString().contains("/")) {
                            words.add(term.toString().split("/")[0]);
                            pos.add(term.toString().split("/")[1]);
                        }
                    }

                    String nerInput = new String();
                    for (String word : words) {
                        nerInput += word;
                        nerInput += " ";
                    }
                    String nerOutput = ner.doNER(nerInput);
                    for (String word : nerOutput.split(" ")) {
                        if (word.length() >= 3 && word.contains("/")) {
                            nerResult.add(word.split("/")[1]);
                        }
                    }


                    question.setCandidateWordandPOS(i, words, pos, nerResult);

                    boolean posContainOther = false;
                    for (String po : pos) {
                        if (po.startsWith("v") ||                           //wyz是书名号，所以不能通过!startsWith("n")判断
                            po.startsWith("d") ||
                            po.startsWith("r") ||
                            po.startsWith("a") // ||
                            //po.startsWith("u")
                           ) {
                            posContainOther = true;
                            break;
                        }
                    }

                    boolean wordContainConj = false;
                    for (String conj : multiConj) {
                        if (words.contains(conj)) {
                            wordContainConj = true;
                            break;
                        }
                    }

                    if (posContainOther) {
                        sentence++;
                    }
                    else if (wordContainConj) {
                        multiEntity++;
                    }
                    else {
                        singleEntity++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (max(singleEntity, multiEntity, sentence) == singleEntity
                    &&  singleEntity >= 3) {
                question.setCandidateType(0);
            }
            else if (max(singleEntity, multiEntity, sentence) == multiEntity) {
                question.setCandidateType(1);
            }
            else {
                question.setCandidateType(2);
            }
        }
    }

    public void buildNumericalCandidatesWordPOSandNER(ArrayList<Question> questionList) {
        for (Question question : questionList) {
            if (!question.getNumericalType()) {
                continue;
            }

            for (QuestionCandidate candidate : question.getNumCandidates()) {
                List<Term> terms = ANSJSEG.seg(candidate.getContent());
                new NatureRecognition(terms).recognition();
                FilterModifWord.modifResult(terms);
                HashMap<String, String> originalPOSHash = new HashMap<>();

                List<Term> parse = NlpAnalysis.parse(candidate.getContent());
                for (Term term : parse) {
                    if (term.toString().length() >= 3 && term.toString().contains("/")) {
                        originalPOSHash.put(term.toString().split("/")[0], term.toString().split("/")[1]);
                    }
                }

                ArrayList<String> words = new ArrayList<>();
                ArrayList<String> pos = new ArrayList<>();
                ArrayList<String> entities = new ArrayList<>();
                ArrayList<String> originalPOS = new ArrayList<>();

                String nerInput = "";
                for (Term term : terms) {
                    if (term.toString().length() >= 3 && term.toString().contains("/")) {
                        words.add(term.toString().split("/")[0]);
                        nerInput += term.toString().split("/")[0];
                        nerInput += " ";
                        pos.add(term.toString().split("/")[1]);
                        if (originalPOSHash.containsKey(term.toString().split("/")[0])) {
                            originalPOS.add(originalPOSHash.get(term.toString().split("/")[0]));
                        }
                        else {
                            originalPOS.add(term.toString().split("/")[1]);
                        }
                    }
                }

                System.err.println(nerInput);
                String nerOutput = ner.doNER(nerInput);
                for (String word : nerOutput.split(" ")) {
                    if (word.length() >= 3 && word.contains("/")) {
                        entities.add(word.split("/")[1]);
                    }
                }

                candidate.setWords(words);
                candidate.setPOSes(pos);
                candidate.setNERs(entities);
                candidate.setOriginalPOSes(originalPOS);
            }


        }
    }

    public int max(int a, int b, int c) {
        return Math.max(a, Math.max(b, c));
    }

    public void testCandidateType(ArrayList<Question> questionList, String answerFile) {
        int rightNum = 0;
        int[] type = null;
        try {
            type = BasicIO.readFile2IntArray(questionList.size(),
                    System.getProperty("user.dir") + answerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Question question : questionList) {

            if(type[questionList.indexOf(question)] != question.getCandidateType()) {
                System.out.print("#" + Integer.sum(questionList.indexOf(question),1) + "\t");
                for (int i = 1; i <= 4; i++) {
                    ArrayList<String> words = question.getCandidateWords(i);
                    for (String word : words) {
                        System.out.print(word + " ");
                    }
                    ArrayList<String> pos = question.getCandidatePOS(i);
                    for (String po : pos) {
                        System.out.print(po + " ");
                    }
                    System.out.println();
                }
                System.out.println("type:" + question.getCandidateType() +
                        "\trightType:" + type[questionList.indexOf(question)]);
            }
            else {
                rightNum++;
            }
        }

        System.out.println("共有" + questionList.size() + "道题目，其中" + rightNum +"道分类正确。");
    }


    public static void main(String[] args) throws Exception{
        QuestionAnalyzer self = new QuestionAnalyzer();

        String questionXMLfile = "data/questions/PracticeQuestion(5803).xml";
        // String answerFile =      "data/questions/xf_xml/History-Test_A_2016.7.answer";
        self.init(questionXMLfile);

        //self.init(questionXMLfile);
        //OutputQuestion.normalOutput(self.acquisition.questionList);
        OutputQuestion.asNNTrain_QAS_leadin(self.acquisition.questionList,
                "out/train_words.txt", "out/train_labels.txt");


        Dict4RNN dictOut = new Dict4RNN();
        HashSet<String> wordSet = dictOut.getWordSet("out/words.txt");
        System.out.println(wordSet.size());

        dictOut.outputDict2File(wordSet, "/home/shawn/GitWS/SmartHistory/data/dicts/baike-embeddings.txt",
                "out/dict.txt");
    }
}