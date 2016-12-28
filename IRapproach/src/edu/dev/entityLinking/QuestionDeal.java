package edu.dev.entityLinking;

import edu.dev.entityLinking.*;
import edu.main.Const;
import edu.question.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zxy on 2016/7/12.
 * Retrieval the entity about Quotes,Books and Userdefine in questions.
 */
public class QuestionDeal {
    Question question;
    QuestionAcquisition questionAcquisition;
    ExtractEntity extractEntity;
    EntityRetrieval entityRetrieval;

    public QuestionDeal(boolean isClassify){
        question = new Question();
        questionAcquisition = new QuestionAcquisition();
        extractEntity = new ExtractEntity();
        entityRetrieval = new EntityRetrieval(isClassify);
    }


    /**处理问题中书名号中的实体*/
    public void dealQuestionsBooks(Question question) throws IOException {
        Map<String, ArrayList<Integer>> contentAndDocIDMap = entityRetrieval.booksAndUserDefineTitleAndDocID(extractEntity.dealWithBooks(question.getQuestion()));
        if (!contentAndDocIDMap.isEmpty()) {
            for (Map.Entry<String, ArrayList<Integer>> entry : contentAndDocIDMap.entrySet()) {
                LinkInfo linkInfo = new LinkInfo();
                linkInfo.setEntityConten(entry.getKey());
                linkInfo.setEntityRetrivalList(entry.getValue());
                linkInfo.setEntityClass("Book");
                linkInfo.setEntityLocation("Question");
                question.addEntityLinkInf(linkInfo);
            }
        }
    }
    /**处理问题中Userdefine中的实体*/
    public void dealQuestionsUserDefine(Question question) throws IOException {
        Map<String, ArrayList<Integer>> contentAndDocIDMap = entityRetrieval.booksAndUserDefineTitleAndDocID(extractEntity.dealWithUserDefine(question.getQuestionWords(), question.getQuestionPOS()));
        if (!contentAndDocIDMap.isEmpty()) {
            for (Map.Entry<String, ArrayList<Integer>> entry : contentAndDocIDMap.entrySet()) {
                LinkInfo linkInfo = new LinkInfo();
                linkInfo.setEntityConten(entry.getKey());
                linkInfo.setEntityRetrivalList(entry.getValue());
                linkInfo.setEntityClass("UserDefine");
                linkInfo.setEntityLocation("Question");
                question.addEntityLinkInf(linkInfo);
            }
        }

    }
    /**处理问题中引号中的实体*/
    public void dealQuestionsQuotes(Question question)throws IOException{
        Map<String, ArrayList<Integer>> contentAndDocIDMap = entityRetrieval.quotesTitleAndDocID(extractEntity.dealWithQuotes(question.getQuestion()));
        if (!contentAndDocIDMap.isEmpty()) {
            for (Map.Entry<String, ArrayList<Integer>> entry : contentAndDocIDMap.entrySet()) {
                LinkInfo linkInfo = new LinkInfo();
                linkInfo.setEntityConten(entry.getKey());
                linkInfo.setEntityRetrivalList(entry.getValue());
                linkInfo.setEntityClass("Quote");
                linkInfo.setEntityLocation("Question");
                question.addEntityLinkInf(linkInfo);
            }
        }
    }
    /**处理候选项中书名号中实体*/
    public void dealCandidatesBooks(Question question) throws IOException{
        for (int i = 0; i <= 3; i++){
            Map<String, ArrayList<Integer>> contentAndDocIDMap = entityRetrieval.booksAndUserDefineTitleAndDocID(extractEntity.dealWithBooks(question.getCandidates(i)));
            if (!contentAndDocIDMap.isEmpty()){
                for (Map.Entry<String, ArrayList<Integer>> entry : contentAndDocIDMap.entrySet()) {
                    LinkInfo linkInfo = new LinkInfo();
                    linkInfo.setEntityConten(entry.getKey());
                    linkInfo.setEntityRetrivalList(entry.getValue());
                    linkInfo.setEntityClass("Book");
                    if (i == 0) {linkInfo.setEntityLocation("Candidate-A");}
                    if (i == 1) {linkInfo.setEntityLocation("Candidate-B");}
                    if (i == 2) {linkInfo.setEntityLocation("Candidate-C");}
                    if (i == 3) {linkInfo.setEntityLocation("Candidate-D");}
                    question.addEntityLinkInf(linkInfo);
                }
            }
        }
    }
    /**处理候选项中Userdefine实体*/
    public void dealCandidatesUserDefine(Question question) throws IOException{
        for (int i = 0; i <= 3; i++){
            Map<String, ArrayList<Integer>> contentAndDocIDMap = entityRetrieval.booksAndUserDefineTitleAndDocID(extractEntity.dealWithUserDefine(question.getCandidateWords(i), question.getCandidatePOS(i)));
            if (!contentAndDocIDMap.isEmpty()){
                for (Map.Entry<String, ArrayList<Integer>> entry : contentAndDocIDMap.entrySet()) {
                    LinkInfo linkInfo = new LinkInfo();
                    linkInfo.setEntityConten(entry.getKey());
                    linkInfo.setEntityRetrivalList(entry.getValue());
                    linkInfo.setEntityClass("UserDefine");
                    if (i == 0) {linkInfo.setEntityLocation("Candidate-A");}
                    if (i == 1) {linkInfo.setEntityLocation("Candidate-B");}
                    if (i == 2) {linkInfo.setEntityLocation("Candidate-C");}
                    if (i == 3) {linkInfo.setEntityLocation("Candidate-D");}
                    question.addEntityLinkInf(linkInfo);
                }
            }
        }
    }
    /**处理候选项中引号中实体*/
    public void dealCandidatesQuotes(Question question) throws IOException{
        for (int i = 0; i <= 3; i++){
            Map<String, ArrayList<Integer>> contentAndDocIDMap = entityRetrieval.quotesTitleAndDocID(extractEntity.dealWithQuotes(question.getCandidates(i)));
            if (!contentAndDocIDMap.isEmpty()){
                for (Map.Entry<String, ArrayList<Integer>> entry : contentAndDocIDMap.entrySet()) {
                    LinkInfo linkInfo = new LinkInfo();
                    linkInfo.setEntityConten(entry.getKey());
                    linkInfo.setEntityRetrivalList(entry.getValue());
                    linkInfo.setEntityClass("Quote");
                    if (i == 0) {linkInfo.setEntityLocation("Candidate-A");}
                    if (i == 1) {linkInfo.setEntityLocation("Candidate-B");}
                    if (i == 2) {linkInfo.setEntityLocation("Candidate-C");}
                    if (i == 3) {linkInfo.setEntityLocation("Candidate-D");}
                    question.addEntityLinkInf(linkInfo);
                }
            }
        }
    }

    /**EntityLinkInit*/
    public void entityLinkInit(Question question)throws IOException{

//            System.out.println("Deal Questions Books " + question.getID());
            dealQuestionsBooks(question);
//            System.out.println("Done!");

//            System.out.println("Deal Questions Quote " + question.getID());
            dealQuestionsQuotes(question);
//            System.out.println("Done!");

//            System.out.print("Deal Questions UserDefine " + question.getID());
            dealQuestionsUserDefine(question);
//            System.out.println("Done!");

//            System.out.println("Deal Candidates Books " + question.getID());
            dealCandidatesBooks(question);
//            System.out.println("Done!");

//            System.out.println("Deal Candidates Quotes " + question.getID());
            dealCandidatesQuotes(question);
//            System.out.println("Done!");

//            System.out.println("Deal Candidates UserDefine " + question.getID());
            dealCandidatesUserDefine(question);
//            System.out.println("Done!");
    }

    public static void main(String[] args) throws IOException {
        Const.init();
        QuestionDeal questionDeal = new QuestionDeal(false);
        QuestionAnalyzer questionAnalyzer = new QuestionAnalyzer();
        String filename = "E:\\Documents\\data_base\\project_smart_history\\questions\\BeijingReal(2005-2014)(87).xml";
        questionAnalyzer.init(filename);

        for (Question question : questionAnalyzer.acquisition.questionList){
            questionDeal.entityLinkInit(question);
//            System.out.print(question.getID());
//            System.out.println(question.getQuestionWords());
//            System.out.println(question.getQuestionPOS());
//            System.out.println(question.getEntityLinkInfo());
            for (int i = 0 ; i <= question.getEntityLinkInfo().size() - 1; i++)
            {
                System.out.println(
                        " " + question.getEntityLinkInfo().get(i).getEntityLocation() +
                        " " + question.getEntityLinkInfo().get(i).getEntityClass() +
                        " " + question.getEntityLinkInfo().get(i).getEntityConten() +
                        " " + question.getEntityLinkInfo().get(i).getEntityRetrivalList());
            }
        }
    }


}
