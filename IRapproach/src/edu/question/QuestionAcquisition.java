package edu.question;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.main.Const;
import com.shawn.BasicIO;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Author:             Shawn Guo
 * E-mail:             air_fighter@163.com
 *
 * Create Time:        2015/11/26 08:41
 * Last Modified Time: 2015/12/30 14:31
 *
 * Class Name:         QuestionAcquisition
 * Class Function:
 *                     该类的主要功能是获取并且切分问题，得到题干和四个选项。
 */

public class QuestionAcquisition {
    public ArrayList<Question> questionList = new ArrayList<>();

    public void init(String fileName) throws IOException, ClassNotFoundException {
        ArrayList<String> questionCandidateList = BasicIO.readFile2StringArray(fileName);
        int odd = 1;
        Question question;
        for (String indexString : questionCandidateList) {
            if (odd == 1) {
                question = new Question();
                question.setQuestion(indexString);
                questionList.add(question);
                odd = 0;
                continue;
            }
            else {
                for (int i = 3; i >= 0; i--) {
                    questionList.get(questionList.size() - 1).setCandidate(i, indexString.split((char) Integer.sum(65, i) + ".")[1]);
                    indexString = indexString.split((char) Integer.sum(65, i) + ".")[0];
                    //System.out.println(question.getCandidates(i-1));
                }
                odd = 1;
                continue;
            }
        }
    }

    public void initFromXML(String fileName)
            throws SAXException, IOException, ParserConfigurationException {
        //our own xml formation
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();

        org.w3c.dom.Document doc = null;
        doc = db.parse(new File(fileName));

        DOMReader domReader = new DOMReader();
        Document document = domReader.read(doc);

        Element rootElement = document.getRootElement();
        int num = 0;
        for (Iterator questionIter = rootElement.elementIterator(); questionIter.hasNext();) {
            System.out.print(num+" ");
            num++;

            Element question = (Element) questionIter.next();
            Question newQuestion = new Question();
            String id = question.attributeValue("id");
            newQuestion.setID(id);
            for (Iterator elementIter = question.elementIterator(); elementIter.hasNext();) {

                Element element = (Element) elementIter.next();
                if (element.getName() == "entity") {
                    newQuestion.setNumericalType(true);
                    if (Integer.parseInt(element.attributeValue("id")) >= newQuestion.getNumericalCandidateNum()) {
                        newQuestion.setNumericalCandidateNum(Integer.parseInt(element.attributeValue("id")));
                    }
                    newQuestion.addNumCandidates(element.getTextTrim());
                }
            }

            newQuestion.setQuestion(question.element("description").getTextTrim());

            int candidateID = 0;
            for(Iterator iterCandidate = question.element("candidates").elementIterator(); iterCandidate.hasNext();) {
                Element choice = (Element) iterCandidate.next();
                String candidateText = "";
                if (choice.getTextTrim().contains(".") && choice.getTextTrim().length() >= 3) {
                    candidateText = choice.getTextTrim().split("\\.")[1];
                }
                else {
                    candidateText = choice.getTextTrim();
                }
                newQuestion.setCandidate(candidateID, candidateText);
                if ("1".equals(choice.attributeValue("value"))) {
                    //System.err.println((char)(65+candidateID));
                   if (!newQuestion.setAnswer(String.valueOf( (char) (65 + candidateID) ))) {
                       System.err.println("Somethin Wrong when acquisition setting answers!");
                   }
                }
                candidateID++;
            }
            if (question.element("entity") != null) {
                newQuestion.setNumericalType(true);
            }

            // zxr 20160521
            try {
                for (Iterator iterCandidate = question.element("questiontype").elementIterator(); iterCandidate.hasNext(); ) {
                    Element type = (Element) iterCandidate.next();
                    String realType = "";
                    switch (type.getTextTrim()) {
                        case "EntityQuestion":
                            realType = Const.Q_T_ENTITY;
                            break;
                        case "SentenceQuestion":
                            realType = Const.Q_T_SENTENCE;
                            break;
                    }
                    newQuestion.questionRealType.add(realType);
                }
            }catch (Exception e){
                System.out.println("xml file has no questiontype, raise from QuestionAcquisition.java");
            }

            questionList.add(newQuestion);
        }

    }

    public void initFromXMLandAnswerFile(String fileName, String answerFileName)
            throws SAXException, IOException, ParserConfigurationException {
        //the old version of XunFei XML
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();

        org.w3c.dom.Document doc = null;
        doc = db.parse(new File(fileName));

        DOMReader domReader = new DOMReader();
        Document document = domReader.read(doc);

        Element rootElement = document.getRootElement();
        rootElement = rootElement.element("section");
        List<Element> questionsList = rootElement.elements();
        int num = 0;
        for (Element questionsIter : questionsList) {
            System.out.print(num+" ");
            num++;

            for (Element question : questionsIter.elements()) {
                if (!question.getName().equals("question")) {
                    continue;
                }
                Question newQuestion = new Question();

                String id = question.attributeValue("id");
                newQuestion.setID(id);

                Element textElement = question.element("text");
                newQuestion.setQuestion(textElement.getTextTrim());

                if (textElement.elements().size() > 0) {
                    newQuestion.setNumericalType(true);
                    for (Iterator numChoiceIter = textElement.elementIterator(); numChoiceIter.hasNext();) {
                        Element labelIter = (Element) numChoiceIter.next();
                        newQuestion.addNumCandidates(labelIter.getTextTrim());
                    }
                }

                Element selectElement = question.element("select");
                newQuestion.initCandidates();
                int index = 0;
                for (Iterator candidateIter = selectElement.elementIterator(); candidateIter.hasNext();) {
                    Element optionIter = (Element) candidateIter.next();
                    newQuestion.setCandidate(index, optionIter.getTextTrim());
                    index++;
                }

                questionList.add(newQuestion);
            }
        }

        try {
            ArrayList<String> answerList = BasicIO.readFile2StringArray(answerFileName);
            System.out.println(answerList.size());
            for (int i = 0; i < answerList.size(); i++) {
                String answer = answerList.get(i);
                questionList.get(i).setAnswer(answer);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("no answer file exist, set all answer as \"E\"");
            for (Question question : questionList) {
                question.setAnswer("E");
            }
        }



    }

    public void initFromXFXMLandAnswerFile(String fileName, String answerFileName)
            throws SAXException, IOException, ParserConfigurationException {
        //the old version of XunFei XML
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();

        org.w3c.dom.Document doc = null;
        doc = db.parse(new File(fileName));

        DOMReader domReader = new DOMReader();
        Document document = domReader.read(doc);

        Element rootElement = document.getRootElement();
        rootElement = rootElement.element("section");
        List<Element> questionsList = rootElement.elements();
        int num = 0;
        for (Element questionsIter : questionsList) {
            System.out.print(num+" ");
            num++;

            for (Element question : questionsIter.elements()) {
                if (!question.getName().equals("question")) {
                    continue;
                }
                Question newQuestion = new Question();

                String id = question.attributeValue("id");
                newQuestion.setID(id);

                Element textElement = question.element("text");
                if (textElement.getText().contains("**(label,0,")) {
                    newQuestion.setNumericalType(true);
                    String[] strList = textElement.getText().split("\\n");
                    newQuestion.setQuestion(strList[0]);

                    int i = 0;
                    newQuestion.initNumCandidates(strList.length - 2);
                    for (String str : strList) {
                        if (i == 0 || i == strList.length - 1) {
                            i += 1;
                            continue;
                        }
                        String[] tmpStr = strList[i].split("\\*\\*");
                        newQuestion.setNumCandidates(i - 1, tmpStr[2]);
                        i += 1;
                    }
                }
                else {
                    newQuestion.setQuestion(textElement.getTextTrim());
                }


                Element selectElement = question.element("select");
                newQuestion.initCandidates();
                int index = 0;
                for (Iterator candidateIter = selectElement.elementIterator(); candidateIter.hasNext();) {
                    Element optionIter = (Element) candidateIter.next();
                    newQuestion.setCandidate(index, optionIter.getTextTrim());
                    index++;
                }

                questionList.add(newQuestion);
            }
        }

        try {
            ArrayList<String> answerList = BasicIO.readFile2StringArray(answerFileName);
            System.out.println(answerList.size());
            for (int i = 0; i < answerList.size(); i++) {
                String answer = answerList.get(i);
                questionList.get(i).setAnswer(answer);
            }
        } catch (Exception e) {
            System.out.println("no answer file exist, set all answer as \"E\"");
            for (Question question : questionList) {
                question.setAnswer("E");
            }
        }



    }


    public static void main(String[] args) throws Exception {
        QuestionAcquisition self = new QuestionAcquisition();
        self.initFromXFXMLandAnswerFile("data/questions/xf_xml/gaokao/2015Beijing.xml", "data/questions/xf_xml/gaokao/2015Beijing.answer");

        for (int i = 0; i < self.questionList.size(); i++) {
            System.out.print("Question " + (i + 1) +": ");
            System.out.println(self.questionList.get(i).getQuestion());
        }
    }
}
