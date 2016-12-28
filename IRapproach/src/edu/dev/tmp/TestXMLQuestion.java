package edu.dev.tmp;


import edu.question.Question;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by shawn on 15-12-29.
 */
public class TestXMLQuestion {
    public static void main(String[] args) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        org.w3c.dom.Document doc = null;
        try {
            doc = db.parse(new File("data/questions/practiceQuestion.xml"));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        DOMReader domReader = new DOMReader();
        Document document = domReader.read(doc);

        Element rootElement = document.getRootElement();
        ArrayList<Question> questionList = new ArrayList<>();

        for (Iterator questionIter = rootElement.elementIterator(); questionIter.hasNext();) {
            Element question = (Element) questionIter.next();
            Question newQuestion = new Question();

            for (Iterator elementIter = question.elementIterator(); elementIter.hasNext();) {
                Element element = (Element) elementIter.next();
                if (element.getName() == "entityList") {
                    newQuestion.setNumericalType(true);
                    if (Integer.parseInt(element.attributeValue("id")) >= newQuestion.getNumericalCandidateNum()) {
                        newQuestion.setNumericalCandidateNum(Integer.parseInt(element.attributeValue("id")));
                    }
                    newQuestion.setNumCandidates(Integer.parseInt(element.attributeValue("id")) - 1, element.getTextTrim());
                }
            }

            newQuestion.setQuestion(question.element("description").getTextTrim());
            int candidateID = 0;
            for(Iterator iterCandidate = question.element("candidates").elementIterator(); iterCandidate.hasNext();) {
                Element choice = (Element) iterCandidate.next();
                newQuestion.setCandidate(candidateID, choice.getTextTrim());
                candidateID++;
            }
            if (question.element("entityList") != null) {
                newQuestion.setNumericalType(true);
            }

            questionList.add(newQuestion);
        }

        for (Question question : questionList) {
            System.out.println("#" + questionList.indexOf(question) + ":" +
                                question.getQuestion());

            if (question.getNumericalType() == true) {
                System.out.println("\tNumerical candidates:");
                for (int i = 0; i < question.getNumericalCandidateNum(); i++) {
                    System.out.print("#" + i + ":" + question.getNumCandidates(i) + " ");
                }
            }

            System.out.print("\t");
            for (int i = 0; i < 4; i++) {
                System.out.print(i + question.getCandidates(i) + " ");
            }
            System.out.println();
        }
    }
}
