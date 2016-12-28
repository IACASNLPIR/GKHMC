package edu.tools.Corpus;

import edu.tools.ANSJSEG;
import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shawn on 16-6-23.
 */
public class CorpusHandler {
    private static ArrayList<String> wordList = new ArrayList<String>(){{
        // add("说明了");
        // add("表明了");
        add("说明");
        add("表明");
    }};

    public static void readXML2OutFile(String xmlName, String outFileName)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();

        org.w3c.dom.Document doc = null;
        doc = db.parse(new File(xmlName));

        DOMReader domReader = new DOMReader();
        Document document = domReader.read(doc);

        Element rootElement = document.getRootElement();
        File outputFile = new File(outFileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        PrintStream printStream = new PrintStream(outputStream);
        int num = 0;

        for (Element itemIter : rootElement.elements()) {
            Element contentEle = itemIter.element("content");
            String content = contentEle.getTextTrim();
            content = content.replaceAll("\\[title.*?\\[/title\\]", "");
            for(String str : content.split("\\[/p\\] .+? \\[p\\]")) {
                printStream.println(str);
            }
        }
    }


    public static List<String> toSentenceList(char[] chars) {

        StringBuilder sb = new StringBuilder();

        List<String> sentences = new ArrayList<String>();

        for (int i = 0; i < chars.length; i++) {
            if (sb.length() == 0 && (Character.isWhitespace(chars[i]) || chars[i] == ' ')) {
                continue;
            }

            sb.append(chars[i]);
            switch (chars[i]) {
                case '.':
                    if (i < chars.length - 1 && chars[i + 1] > 128) {
                        sentences.add(sb.toString().trim());
                        sb = new StringBuilder();
                    }
                    break;
                case ' ':
                case '	':
                case '　':
                //case ' ':
                //case ',':
                case '。':
                //case ';':
                //case '；':
                case '!':
                case '！':
                //case '，':
                case '?':
                case '？':
                case '\n':
                case '\r':
                    sentences.add(sb.toString().trim());
                    sb = new StringBuilder();
            }
        }

        if (sb.length() > 0) {
            sentences.add(sb.toString().trim());
        }

        return sentences;
    }


    public static void handleCorpusLineByLine(String xmlName, String outFileName) throws IOException {
        File inputFile = new File(xmlName);
        FileReader inputFileReader = new FileReader(inputFile);
        BufferedReader inputReader = new BufferedReader(inputFileReader);
        File outputFile = new File(outFileName);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        PrintStream printStream = new PrintStream(outputStream);
        int num = 0;

        String tmpStr = null;
        while((tmpStr = inputReader.readLine()) != null) {
            num++;
            for(String word : wordList) {
                if (tmpStr.contains(word)) {
                    //System.err.println(tmpStr);
                    //tmpStr = tmpStr.replaceAll("\\[url\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[/url\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[p\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[/p\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[b\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[/b\\]", "");
                    tmpStr = tmpStr.replaceAll("\\[img.*?\\[/img\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[sup.*?\\[/sup\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[title.*?\\[/title\\]", "");
                    //tmpStr = tmpStr.replaceAll("\\[title2.*?\\[/title2\\]", "");
                    tmpStr = tmpStr.replaceAll("\\[.*?\\]", "");
                    tmpStr = tmpStr.replaceAll("<index92>.*?</index92>", "");
                    tmpStr = tmpStr.replaceAll("<.*?>", "");
                    tmpStr = tmpStr.replaceAll("\\s?+", "");
                    tmpStr = tmpStr.trim();
                    if (tmpStr.length() == 0 ||
                            tmpStr.trim().split(word).length < 2 ||
                            tmpStr.trim().split(word)[1].length() < 4 ||
                            tmpStr.trim().split(word)[0].length() < 4) continue;

                    List<String> sentences = toSentenceList(tmpStr.toCharArray());

                    String _word = word.substring(0, 2);
                    for (String sentence : sentences){
                        if (sentence.contains(word)) {
                            HashMap<String, String> originalPOSHash = new HashMap<>();
                            List<Term> parse = NlpAnalysis.parse(sentence);
                            for (Term term : parse) {
                                if (term.toString().length() >= 3 && term.toString().contains("/")) {
                                    originalPOSHash.put(term.toString().split("/")[0], term.toString().split("/")[1]);
                                }
                            }
                            //System.err.println(originalPOSHash);
                            if (originalPOSHash.containsKey(_word) && originalPOSHash.get(_word).contains("v"))  printStream.println(sentence.trim());
                        }
                    }
                }
            }
        }
        inputReader.close();
    }

    public static void main(String[] args) {
        try {
            String word = "说明";
            word = word.substring(0, 2);
            System.out.println(word);
            handleCorpusLineByLine("data/baike/BaikeAll.xml", "out/baike_pure.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
