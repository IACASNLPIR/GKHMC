package edu.main;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by sunder on 2015/12/28.
 * 常量：文件路径等
 */
public class Const {
    public static void main(String[] args) {
        Const.init();
    }
    public static void init(){
        String configFilename = "SmartHistoryConfig.xml";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(configFilename));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        Element rootElement = document.getDocumentElement();
        NodeList pathNodeList = rootElement.getElementsByTagName("path");
        for (int i = 0; i < pathNodeList.getLength(); i++){
            Node node = pathNodeList.item(i);
            Element path = (Element) node;
            DATA_BASE_HOME_PATH = path.getAttribute("value");
            QUESTION_FILENAME = DATA_BASE_HOME_PATH + path.getElementsByTagName("question")
                    .item(0)
                    .getTextContent();
            ANSWER_FILENAME = DATA_BASE_HOME_PATH + path.getElementsByTagName("answer")
                    .item(0)
                    .getTextContent();
            QUESTION_SCORE_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("questionScores")
                    .item(0)
                    .getTextContent();
            ENTITY_FINAL_SCORE = DATA_BASE_HOME_PATH + path.getElementsByTagName("entityfinalScore")
                    .item(0)
                    .getTextContent();
            BAIKE_INFO_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("baikeinfo")
                    .item(0)
                    .getTextContent();
            BAIKE_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("baikefile")
                    .item(0)
                    .getTextContent();
            HISTORY_TIMELINE_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("historytimeline")
                    .item(0)
                    .getTextContent();
            WIKIPEDIA_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("wikipedia")
                    .item(0)
                    .getTextContent();
            HISTORY_TYPE_BAIKE_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("historytypebaike")
                    .item(0)
                    .getTextContent();
            UN_HISTORY_TYPE_BAIKE_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("unhistorytypebaike")
                    .item(0)
                    .getTextContent();
            BAIKE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("baikedocindex")
                    .item(0)
                    .getTextContent();
            BAIKE_PARAGRAPH_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("baikeparaindex")
                    .item(0)
                    .getTextContent();
            BAIKE_SENTENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("baikesentindex")
                    .item(0)
                    .getTextContent();
            BOOK_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("bookindex")
                    .item(0)
                    .getTextContent();
            DOC_CO_OCCURRENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("doccooccurrenceindex")
                    .item(0)
                    .getTextContent();
            PARA_CO_OCCURRENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("paracooccurrenceindex")
                    .item(0)
                    .getTextContent();
            SENT_CO_OCCURRENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("sentcooccurrenceindex")
                    .item(0)
                    .getTextContent();
            LINK_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("linkindex")
                    .item(0)
                    .getTextContent();
            HISTORY_TIMELINE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("historytimelineindex")
                    .item(0)
                    .getTextContent();
            STOP_WORDS_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("stopwords")
                    .item(0)
                    .getTextContent();
            ALL_WORDS_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("allwords")
                    .item(0)
                    .getTextContent();
            BAIKE_CLASSIFY_MODEL_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("baikeclassifymodel")
                    .item(0)
                    .getTextContent();
            TITLE_ID_INDEX_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("titleidindex")
                    .item(0)
                    .getTextContent();
            RESULT_LOG_FILEPATH = DATA_BASE_HOME_PATH + path.getElementsByTagName("log")
                    .item(0)
                    .getTextContent();
        }

        NodeList parameterNodeList = rootElement.getElementsByTagName("parameter");
        for (int i = 0; i < parameterNodeList.getLength(); i++){
            Node node = parameterNodeList.item(i);
            Element parameter = (Element) node;
            ENTITY_ID_SEPARATOR = parameter.getElementsByTagName("entityidseparator")
                    .item(0)
                    .getTextContent();
            SCORE_FUNC_CODE = Integer.valueOf(parameter.getElementsByTagName("scorefunccode")
                    .item(0)
                    .getTextContent());
            IS_XUNFEI_TYPE = parameter.getElementsByTagName("isxunfei")
                    .item(0)
                    .getTextContent()
                    .equals("true");
        }
    }

    public static String HOME_PATH = "";
    public static String DATA_BASE_HOME_PATH =
            "E:/Documents/data_base/project_smart_history/";
    //        "data/";
    public static String BAIKE_INFO_FILEPATH = HOME_PATH + "data/original/Baike-AllInfo.txt";
    public static String BAIKE_FILEPATH = HOME_PATH + "data/original/Baike-AllContent.xml";
    public static String BAIKE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/BaikeIndex";
    public static String BAIKE_SENTENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/BaikeSentenceIndex";
    public static String BAIKE_PARAGRAPH_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/BaikeParagraphIndex";
    public static String STOP_WORDS_FILEPATH = HOME_PATH + "data/dicts/stopwords_cn.txt";
    public static String WIKIPEDIA_FILEPATH = HOME_PATH + "data/original/zhwiki-20160501-pages-articles.xml";
    public static String BOOK_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/BookIndex";

    public static String HISTORY_TIMELINE_FILEPATH = HOME_PATH + "data/original/history_timeline.txt";
    public static String HISTORY_TIMELINE_INDEX_FILEPATH = HOME_PATH + "data/index/HistoryTimelineIndex/";

    public static String QUESTIONS_FILEPATH = HOME_PATH +  "data/questions/";

    public static String ALL_WORDS_FILEPATH = HOME_PATH +  "data/all_words.txt";
    public static String HISTORY_TYPE_BAIKE_FILEPATH = HOME_PATH + "data/original/classify_data/history_type/";
    public static String UN_HISTORY_TYPE_BAIKE_FILEPATH = HOME_PATH + "data/original/classify_data/un_history_type/";
    public static String BAIKE_CLASSIFY_MODEL_FILEPATH = HOME_PATH +  "data/baike_classify.model";

    public static String RESULT_LOG_FILEPATH = HOME_PATH +  "data/log/"; // 程序结果的log

    public static String TOPIC_FOLDER_FILEPATH = HOME_PATH + "data/original/topic/";

    public static String DOC_CO_OCCURRENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/CoOccurrence/DocCoOccur";
    public static String PARA_CO_OCCURRENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/CoOccurrence/ParaCoOccur";
    public static String SENT_CO_OCCURRENCE_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/CoOccurrence/SentCoOccur";
    public static String LINK_INDEX_FILEPATH = DATA_BASE_HOME_PATH + "index/linkIndex";
    public static String TITLE_ID_INDEX_FILEPATH = HOME_PATH + "data/titleIdIndex.txt";

    public static String QUESTION_FILENAME = "";
    public static String ANSWER_FILENAME = "";
    public static String QUESTION_SCORE_FILEPATH = HOME_PATH + "data/allQuestionScores.txt";
    public static String ENTITY_FINAL_SCORE = "";


    public static String USER_DEFINE_SYMBOL = "userDefine";

    public static String FIELD_CONTENT = "content";
    public static String FIELD_TITLE = "title";

    public static String FIELD_TIME = "time";

    public static String LINK_KEY_FIELD = "linkkey";
    public static String LINK_COUNT_FIELD = "linkcount";

    public static String DOCUMENT_START_SYMBOL = "<item>";
    public static String DOCUMENT_END_SYMBOL = "</item>";
    public static String TITLE_START_SYMBOL = "<lemmatitle>";
    public static String TITLE_END_SYMBOL = "</lemmatitle>";
    public static String CONTENT_START_SYMBOL = "<content>";
    public static String CONTENT_END_SYMBOL = "</content>";
    public static String SUMMARY_START_SYMBOL = "<summarycontent>";
    public static String SUMMARY_END_SYMBOL = "</summarycontent>";

    public static String TOPIC_START_SYMBOL = "<subtopic";
    public static String TOPIC_END_SYMBOL = "</subtopic>";

    public static int ERROR_TIME = 9999;

    public static final String Q_T_SINGLE_ENTITY= "单实体题";
    public static final String Q_T_MULTIPLE_ENTITY = "多实体题";
    public static final String Q_T_SENTENCE = "句子题";
    public static final String Q_T_ENTITY = "实体题";

    public static final String Q_T_TIME_DESCRIBE = "时间格式转换题";
    public static final String Q_T_TIME = "时间题";
    public static final String Q_T_FILL = "填空题";
    public static final String Q_T_NEGATIVE = "否定题";
    public static final String Q_T_SORTING = "排序题";
    public static final String Q_T_FIND_SAME = "相同之处题";
    public static final String Q_T_FIND_DIFFERENCES = "不同之处题";
    public static final String Q_T_SIMILARITIES_MEASURE = "相似度计算题";

    public static final String COOCCURANCE_KEY_FIELD = "Key";
    public static final String COOCCURANCE_VALUE_FIELD = "Value";

    public static final String BAIKE_DOC_SEARCHER_NAME = "baikeDocSearcher";
    public static final String BAIKE_PARA_SEARCHER_NAME = "baikeParaSearcher";
    public static final String BAIKE_SENT_SEARCHER_NAME = "baikeSentSearcher";
    public static final String CO_OCCUR_DOC_SEARCHER_NAME = "ooccurDocScore";
    public static final String CO_OCCUR_PARA_SEARCHER_NAME = "cooccurParaScore";
    public static final String CO_OCCUR_SENT_SEARCHER_NAME = "cooccurSentScore";
    public static final String BAIKE_LINK_SEARCHER_NAME = "linkScore";

    public static String ENTITY_ID_SEPARATOR = "E_I_S";
    public static int SCORE_FUNC_NUM = 1;
    public static boolean IS_XUNFEI_TYPE = false;
    public static int SCORE_FUNC_CODE = 1000000;






}
