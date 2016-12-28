package edu.tools.MemNet;

import com.shawn.BasicIO;
import edu.tools.ANSJSEG;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by shawn on 16-5-19.
 */
public class JieXi2rnntrain {
    public ArrayList<Question4Jiexi> questionList = new ArrayList<>();

    public static ArrayList<Question4Jiexi> getQuestionList(String fileName) throws IOException, ClassNotFoundException {
        ArrayList<String> contentList = BasicIO.readFile2StringArray(fileName);
        ArrayList<Question4Jiexi> retList = new ArrayList<>();

        int iterNum = contentList.size() / 7;
        //System.out.println(contentList.size());
        //System.out.println(iterNum);
        for (int i = 0; i < iterNum; i++) {
            Question4Jiexi newQuestion = new Question4Jiexi();
            newQuestion.question = contentList.get(i * 7);
            newQuestion.candidates[0] = contentList.get(i * 7 + 1);
            newQuestion.candidates[1] = contentList.get(i * 7 + 2);
            newQuestion.candidates[2] = contentList.get(i * 7 + 3);
            newQuestion.candidates[3] = contentList.get(i * 7 + 4);
            newQuestion.explaination = contentList.get(i * 7 + 5).split("tion:")[1].trim();
            if (contentList.get(i * 7 + 6).split(":")[1].trim().toCharArray().length < 1) {
                System.err.println(i);
                System.err.println(newQuestion.question);
                System.exit(-1);
            }
            newQuestion.answer = contentList.get(i * 7 + 6).split(":")[1].trim().toCharArray()[0];
            retList.add(newQuestion);
        }

        return retList;
    }

    public static ArrayList<Question4Jiexi> filterOutPunctuation(ArrayList<Question4Jiexi> questionList) {
        for (Question4Jiexi question: questionList) {
            question.question = question.question.replaceFirst("[\\(（]\\d{4}.*?[）\\)]", "");
            question.question = question.question.replaceAll("[\\(（]\\?+\\s*[）\\)]", "");
            question.question = question.question.replaceAll("[\\(（]\\s*[）\\)]", "");
            question.question = question.question.replaceAll("\\?", "");
            question.question = question.question.replaceAll("；", "");
            for (int i=0; i<4; i++) {
                question.candidates[i] = question.candidates[i].replaceAll("\\?", "");
                question.candidates[i] = question.candidates[i].replaceAll(".*、", "");
            }
        }
        return questionList;
    }

    public static ArrayList<Question4Jiexi> splitNumCandidates(ArrayList<Question4Jiexi> questionList) {
        for (Question4Jiexi question:questionList) {
            if(question.question.contains("④")) {
                question.numCandidates = new String[4];
                int four = (int) "④".toCharArray()[0];
                for (int i = 0; i < 4; i++) {
                    //System.out.println(question.question);
                    question.numCandidates[3 - i] = question.question.split(String.valueOf((char)(four - i)))[1];
                    question.question = question.question.split(String.valueOf((char)(four - i)))[0];
                }
            }
        }
        return questionList;
    }

    public static ArrayList<Question4Jiexi> splitWords(ArrayList<Question4Jiexi> questionList) {
        for (Question4Jiexi question: questionList) {
            question.question = ANSJSEG.segStr2Str(question.question);

            if (question.numCandidates != null) {
                for (int i=0; i<4; i++) {
                    question.numCandidates[i] = ANSJSEG.segStr2Str(question.numCandidates[i]);
                    question.candidates[i] = question.candidates[i].trim();
                }
            }
            else {
                for (int i = 0; i < 4; i++) {
                    question.candidates[i] = ANSJSEG.segStr2Str(question.candidates[i]);
                }
            }
        }
        return questionList;
    }

    public static ArrayList<Question4Jiexi> repalceNumCandidates(ArrayList<Question4Jiexi> questionList) {
        for (Question4Jiexi question: questionList) {
            if (question.numCandidates != null) {
                System.err.println(question.question);
                question.numRealCans = new String[4];
                for (int i=0; i<4; i++) {
                    String tmp = "";
                    int one = (int) "①".toCharArray()[0];
                    for (char numChar : question.candidates[i].trim().toCharArray()) {
                        int index = (int) numChar - one;
                        if (index >=0 && index <= 3){
                            tmp += question.numCandidates[index] + " ";
                        }
                    }
                    question.numRealCans[i] = tmp;
                }
            }
        }
        return questionList;
    }

    public static ArrayList<Question4Jiexi> splitInference(ArrayList<Question4Jiexi> questionList) {
        for (Question4Jiexi question : questionList){
            for (String str:question.explaination.split("，")) {
                question.explainations.add(ANSJSEG.segStr2Str(str));
            }
        }
        return questionList;
    }

    public static void main(String[] args) {

        ArrayList<Question4Jiexi> questionList = new ArrayList<>();
        try {
            questionList = getQuestionList("/home/shawn/GitWS/SmartHistory/data/questions/jiexi.txt");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        questionList = filterOutPunctuation(questionList);
        questionList = splitNumCandidates(questionList);
        questionList = splitWords(questionList);
        questionList = repalceNumCandidates(questionList);
        questionList = splitInference(questionList);

        File wordFile = new File("out/words.txt");
        FileOutputStream wordFileStream = null;
        try {
            wordFileStream = new FileOutputStream(wordFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream wordStream = new PrintStream(wordFileStream);

        for (Question4Jiexi question : questionList) {
            wordStream.println(question.question);
            System.err.println(question.question);
            System.err.println(question.answer);
            int rightIndex = (int) question.answer - 65;

            if (question.numCandidates != null) {
                wordStream.println(question.numRealCans[rightIndex]);
            }
            else {
                wordStream.println(question.candidates[rightIndex]);
            }

            //System.out.println(question.explaination);
            for (String str : question.explainations) {
                if (question.explainations.indexOf(str) != question.explainations.size() - 1){
                    wordStream.print(str + "|||");
                }
                else {
                    wordStream.println(str);
                }
            }
        }


    }
}
