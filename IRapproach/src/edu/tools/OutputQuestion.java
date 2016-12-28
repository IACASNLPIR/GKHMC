package edu.tools;

import edu.question.Question;
import edu.question.QuestionCandidate;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by shawn on 16-3-12.
 */
public class OutputQuestion {
    public static void outputQuestionsAndRightContents(ArrayList<Question> questionList, String fileName) {
        System.out.println("Now, it's outputting");
        File outputFile = new File(fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            PrintStream printStream = new PrintStream(outputStream);
            for (Question question : questionList) {
                for (String word : question.getQuestionWords()) {
                    printStream.print(word + " ");
                }
                for (QuestionCandidate candidate : question.getRightCandidates()) {
                    for (String word : candidate.getWords()) {
                        printStream.print(word + " ");
                    }
                }
                printStream.println();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void outputQuestionsAsRNNInput(ArrayList<Question> questionList, String fileName) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream printStream = new PrintStream(outputStream);

        for (Question question : questionList) {
            for (String word : question.getQuestionWords()) {
                printStream.print(word + " ");
            }
            if (question.getNumericalType()) {

            }
            else {
                if (question.getAnswer().equals("A")) {
                    for (String word : question.getCandidateWords(1)) {
                        printStream.print(word + " ");
                    }
                }
                else if (question.getAnswer().equals("B")) {
                    for (String word : question.getCandidateWords(2)) {
                        printStream.print(word + " ");
                    }
                }
                else if (question.getAnswer().equals("C")) {
                    for (String word : question.getCandidateWords(3)) {
                        printStream.print(word + " ");
                    }
                }
                else if (question.getAnswer().equals("D")) {
                    for (String word : question.getCandidateWords(4)) {
                        printStream.print(word + " ");
                    }
                }
            }
            printStream.println();
        }
    }

    public static void outputQuestion2XML(ArrayList<Question> questionList, String fileName) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintStream printStream = new PrintStream(outputStream);

        printStream.println("<questionlist>");
        for (Question question : questionList) {
            printStream.println("<question id=\"" + questionList.indexOf(question) + "\">");
            printStream.println("\t<description>" + question.getQuestion() + "</description>");
            if (question.getNumericalType()) {
                for (int i = 0; i < question.getNumericalCandidateNum(); i++) {
                    char one = "①".toCharArray()[0];
                    char id = (char)((int)one + i);
                    printStream.println("\t\t<entity id=\"" + Integer.sum(i, 1) + "\">" + id +question.getNumCandidates(i) + "</entity>");
                }
            }
            printStream.println("\t<candidates>");
            int answer = (int)question.getAnswer().toCharArray()[0] - (int) 'A';
            for(int i = 0; i < 4; i++) {
                printStream.print("\t\t<candidate value=\"");
                if (i == answer) {
                    printStream.print("1");
                }
                else {
                    printStream.print("0");
                }
                char num = (char) ((int)'A' + i);
                printStream.println("\">" + num+ "." + question.getCandidates(i) + "</candidate>");
            }
            printStream.println("\t</candidates>");
            printStream.println("</question>");
        }
        printStream.println("</questionlist>");
    }

    public static void normalOutput(ArrayList<Question> questionList) {
        System.out.println("Now, it's outputting");
        File outputFile = new File("out/output.txt");
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            PrintStream printStream = new PrintStream(outputStream);

            int i = 0;
            for (Question question : questionList) {
                i += 1;
                printStream.println("Question #" + i);

                printStream.println("Question:\t" + question.getQuestion());
                printStream.println("Candidates:\tA." + question.getCandidates(0)
                        + " B." + question.getCandidates(1)
                        + " C." + question.getCandidates(2)
                        + " D." + question.getCandidates(3));
                printStream.println("Answer:\t" + question.getAnswer());
                printStream.println("Type:\t" + question.getStemType());

                printStream.println("Question Segments:\t");
                for (String word : question.getQuestionWords()) {
                    printStream.print(word + " ");
                }
                printStream.println();
                for (String pos : question.getQuestionPOS()) {
                    printStream.print(pos + " ");
                }
                printStream.println();
                for (String pos : question.getQuestionOriginalPOS()) {
                    printStream.print(pos + " ");
                }
                printStream.println();
                for (String entity : question.getQuestionEntities()) {
                    printStream.print(entity + " ");
                }
                printStream.println();

                if(question.getNumericalType()) {
                    printStream.println("Numerical Type:\t" + question.getNumericalType());
                    printStream.println("Numerical Candidates:");
                    ArrayList<QuestionCandidate> candidates = question.getNumCandidates();
                    for (QuestionCandidate candidate : candidates) {
                        printStream.println(candidate.getContent());
                    }
                }
                else {
                    printStream.println("Candidate Type:\t" + question.getCandidateType());
                    printStream.println("Candidates Segment:\t");
                    for (int j = 0; j <= 3; j++) {
                        ArrayList<String> words = question.getCandidateWords(j);
                        ArrayList<String> pos = question.getCandidatePOS(j);
                        ArrayList<String> ners = question.getCandidateNER(j);
                        for (String word : words) {
                            printStream.print(word + " ");
                        }
                        for (String po : pos) {
                            printStream.print(po + " ");
                        }
                        for (String nerStr : ners) {
                            printStream.print(nerStr + " ");
                        }
                        printStream.println();
                    }
                }

                printStream.print("RightAnswer Content:\t");
                for (QuestionCandidate candidate : question.getRightCandidates()) {
                    printStream.print(candidate.getContent() + " ");
                }
                printStream.println();

                printStream.println("Materials:\t" + question.getMaterial());
                printStream.println("QuestionStem:\t" + question.getQuestionStem());

                printStream.print("Original Material:\t");
                for (String originalMaterial : question.getOrignialMaterials()) {
                    printStream.print(originalMaterial + " ");
                }
                printStream.println();
                printStream.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void outputQuestionsAndContents(ArrayList<Question> questionList, String fileName) {
        System.out.println("Now, it's outputting");
        File outputFile = new File(fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            PrintStream printStream = new PrintStream(outputStream);
            for (Question question : questionList) {
                for (String word : question.getQuestionWords()) {
                    printStream.print(word + " ");
                }
                for (QuestionCandidate candidate : question.getRightCandidates()) {
                    for (String word : candidate.getWords()) {
                        printStream.print(word + " ");
                    }
                }
                printStream.println();
                for (String word : question.getQuestionWords()) {
                    printStream.print(word + " ");
                }
                for (QuestionCandidate candidate : question.getWrongCandidates()) {
                    for (String word : candidate.getWords()) {
                        printStream.print(word + " ");
                    }
                }
                printStream.println();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void outputQuestionsContentsAndLables(ArrayList<Question> questionList,
                                                        String strFileName,
                                                        String labelFileName) {
        deletFile(strFileName);
        deletFile(labelFileName);
        /**
         * 输出形如下方：
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 郡县制
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 分封制
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 王位世袭制
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 行省制
         * 0
         * 1
         * 0
         * 0
         */
        System.out.println("Now, it's outputting");
        File wordFile = new File(strFileName);
        File labelFile = new File(labelFileName);
        try {
            FileOutputStream wordFileStream = new FileOutputStream(wordFile);
            FileOutputStream labelFileStream = new FileOutputStream(labelFile);

            PrintStream wordStream = new PrintStream(wordFileStream);
            PrintStream labelStream = new PrintStream(labelFileStream);

            for (Question question : questionList) {
                if (question.getNumericalType()) {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    for (String word : question.getQuestionWords()) {
                        wordStream.print(word + " ");
                    }

                    for (int i = 0; i < 4; i++) {
                        for (char word : question.getCandidates(i).toCharArray()) {
                            char one = "①".toCharArray()[0];
                            if (word - one > 3 || word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }
                        wordStream.println();
                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }
                    }
                }
                else {
                    int rightIndex = question.getAnswer().charAt(0) - 65;
                    for (int i = 0; i < 4; i++) {
                        for (String word : question.getQuestionWords()) {
                            wordStream.print(word + " ");
                        }
                        for (String word : question.getCandidateWords(i)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();
                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void asNNTest(ArrayList<Question> questionList,
                                                        String strFileName,
                                                        String labelFileName) {
        deletFile(strFileName);
        deletFile(labelFileName);
        /**
         * 输出形如下方：
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 郡县制
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 分封制
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 王位世袭制
         * 秦遂 并兼 四海 以为 周制 微弱 终 诸侯 丧 不 立 尺土 封 分 天下 郡县 …… 材料 中的 周制 指 行省制
         * 0
         * 1
         * 0
         * 0
         */
        System.out.println("Now, it's outputting");
        File wordFile = new File(strFileName);
        File labelFile = new File(labelFileName);
        try {
            FileOutputStream wordFileStream = new FileOutputStream(wordFile);
            FileOutputStream labelFileStream = new FileOutputStream(labelFile);

            PrintStream wordStream = new PrintStream(wordFileStream);
            PrintStream labelStream = new PrintStream(labelFileStream);

            for (Question question : questionList) {
                /**
                if (question.getNumericalType()) {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    for (int i = 0; i < 4; i++) {
                        for (char word : question.getCandidates(i).toCharArray()) {

                            for (String qword : question.getQuestionWords()) {
                                wordStream.print(qword + " ");
                            }

                            char one = "①".toCharArray()[0];
                            if (word - one > 3 || word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }
                        wordStream.println();
                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }
                    }
                }
                else {
                 **/
                    int rightIndex = question.getAnswer().charAt(0) - 65;
                    for (int i = 0; i < 4; i++) {
                        for (String word : question.getQuestionWords()) {
                            wordStream.print(word + " ");
                        }
                        for (String word : question.getCandidateWords(i)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();
                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }
                    }
                }
//            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void outputQuestionsWithAllCandidates(ArrayList<Question> questionList,
                                                        String strFileName,
                                                        String labelFileName) {
        System.out.println("Now, it's outputting");
        File wordFile = new File(strFileName);
        File labelFile = new File(labelFileName);
        try {
            FileOutputStream wordFileStream = new FileOutputStream(wordFile);
            FileOutputStream labelFileStream = new FileOutputStream(labelFile);

            PrintStream wordStream = new PrintStream(wordFileStream);
            PrintStream labelStream = new PrintStream(labelFileStream);

            for (Question question : questionList) {
                if (question.getNumericalType()) {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    for (int i = 0; i < 4; i++) {
                        for (String word : question.getQuestionWords()) {
                            wordStream.print(word + " ");
                        }

                        for (char word : question.getCandidates(i).toCharArray()) {
                            char one = "①".toCharArray()[0];
                            if (word - one > 3 || word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }

                        wordStream.println();
                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }
                    }
                }
                else {
                    int rightIndex = question.getAnswer().charAt(0) - 65;
                    for (int i = 0; i < 4; i++) {
                        for (String word : question.getQuestionWords()) {
                            wordStream.print(word + " ");
                        }
                        for (String word : question.getCandidateWords(i)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();
                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void outputQuestionsandAllCansRightFirst(ArrayList<Question> questionList, String fileName) {
        System.out.println("Now, it's outputting");
        File outputFile = new File(fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            PrintStream printStream = new PrintStream(outputStream);
            for (Question question : questionList) {
                for (String word : question.getQuestionWords()) {
                    printStream.print(word + " ");
                }
                for (String word : question.getCandidateWords(question.getAnswer().toCharArray()[0] - 65)) {
                    printStream.print(word + " ");
                }
                printStream.println();
                for (int i = 0; i < 4; i++) {
                    if (i == question.getAnswer().toCharArray()[0] - 65) {
                        continue;
                    }
                    for (String word : question.getQuestionWords()) {
                        printStream.print(word + " ");
                    }
                    for (String word : question.getCandidateWords(i)) {
                        printStream.print(word + " ");
                    }
                    printStream.println();
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void asNNTrain_QAS(ArrayList<Question> questionList,
                                                        String strFileName,
                                                        String labelFileName) {
        /**
         中国 古代 某 时期 朝廷 地方 矛盾 尖锐 某 节度使 派 人 中书省 办事 态度 恶劣 遭 宰相 武元衡 呵斥 不久 武元衡 靖安坊 东门 节度使 派 人 刺杀 此事 发生
         汉长安
         唐长安
         宋汴梁
         唐长安
         元大都
         唐长安
         -1
         1
         -1
         1
         -1
         1
         */

        System.out.println("Now, it's outputting");
        File wordFile = new File(strFileName);
        File labelFile = new File(labelFileName);
        try {
            FileOutputStream wordFileStream = new FileOutputStream(wordFile);
            FileOutputStream labelFileStream = new FileOutputStream(labelFile);

            PrintStream wordStream = new PrintStream(wordFileStream);
            PrintStream labelStream = new PrintStream(labelFileStream);

            for (Question question : questionList) {
                if (question.getNumericalType()) {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    for (String word : question.getQuestionWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (int i = 0; i < 4; i++) {
                        if (i == rightIndex) continue;

                        for (char word : question.getCandidates(i).toCharArray()) {
                            char one = "①".toCharArray()[0];
                            if (word - one > 3 || word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }
                        wordStream.println();
                        labelStream.println("-1");

                        for (char word : question.getCandidates(rightIndex).toCharArray()) {
                            char one = "①".toCharArray()[0];
                            if (word - one > 3 || word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }
                        wordStream.println();
                        labelStream.println("1");

                    }
                }
                else {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    for (String word : question.getQuestionWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (int i = 0; i < 4; i++) {
                        if (i == rightIndex) continue;
                        for (String word : question.getCandidateWords(i)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();
                        labelStream.println("-1");

                        for (String word : question.getCandidateWords(rightIndex)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();
                        labelStream.println("1");
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void asNNTrain_QAS_leadin(ArrayList<Question> questionList,
                                     String strFileName,
                                     String labelFileName) {
        /**
         中国 古代 某 时期 朝廷 地方 矛盾 尖锐 某 节度使 派 人 中书省 办事 态度 恶劣 遭 宰相 武元衡 呵斥 不久 武元衡 靖安坊 东门 节度使 派 人 刺杀
         此事 发生
         汉长安
         唐长安
         宋汴梁
         唐长安
         元大都
         唐长安
         -------------------------------------------------------------------------------------------
         -1
         1
         -1
         1
         -1
         1
         */

        System.out.println("Now, it's outputting");
        File wordFile = new File(strFileName);
        File labelFile = new File(labelFileName);
        try {
            FileOutputStream wordFileStream = new FileOutputStream(wordFile);
            FileOutputStream labelFileStream = new FileOutputStream(labelFile);

            PrintStream wordStream = new PrintStream(wordFileStream);
            PrintStream labelStream = new PrintStream(labelFileStream);

            for (Question question : questionList) {
                if(question.getStemWords().size() == 0) {
                    continue;
                }

                if (question.getNumericalType()) {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    boolean flag = false;
                    char one = "①".toCharArray()[0];
                    for (int i = 0; i < 4; i++) {
                        for(char index : question.getCandidates(i).toCharArray()) {
                            if (index - one >= question.getNumCandidates().size())
                                flag = true;
                        }
                    }
                    if (flag) continue;

                    for (String word : question.getMaterialWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (String word : question.getStemWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (int i = 0; i < 4; i++) {
                        if (i == rightIndex) continue;

                        for (char word : question.getCandidates(i).toCharArray()) {
                            if ( word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }
                        wordStream.println();
                        labelStream.println("-1");

                        for (char word : question.getCandidates(rightIndex).toCharArray()) {
                            if ( word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }
                        wordStream.println();
                        labelStream.println("1");

                    }
                }
                else {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    for (String word : question.getMaterialWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (String word : question.getStemWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (int i = 0; i < 4; i++) {
                        if (i == rightIndex) continue;
                        for (String word : question.getCandidateWords(i)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();
                        labelStream.println("-1");

                        for (String word : question.getCandidateWords(rightIndex)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();
                        labelStream.println("1");
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void asNNTest_QAS_leadin(ArrayList<Question> questionList,
                                            String strFileName,
                                            String labelFileName) {
        /**
         中国 古代 某 时期 朝廷 地方 矛盾 尖锐 某 节度使 派 人 中书省 办事 态度 恶劣 遭 宰相 武元衡 呵斥 不久 武元衡 靖安坊 东门 节度使 派 人 刺杀
         此事 发生
         汉长安
         唐长安
         宋汴梁
         元大都
         -------------------------------------------------------------------------------------------
         0
         1
         0
         0
         */

        System.out.println("Now, it's outputting");
        File wordFile = new File(strFileName);
        File labelFile = new File(labelFileName);
        try {
            FileOutputStream wordFileStream = new FileOutputStream(wordFile);
            FileOutputStream labelFileStream = new FileOutputStream(labelFile);

            PrintStream wordStream = new PrintStream(wordFileStream);
            PrintStream labelStream = new PrintStream(labelFileStream);

            for (Question question : questionList) {
                if(question.getStemWords().size() == 0) {
                    continue;
                }

                if (question.getNumericalType()) {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    boolean flag = false;
                    char one = "①".toCharArray()[0];
                    for (int i = 0; i < 4; i++) {
                        for(char index : question.getCandidates(i).toCharArray() ) {
                            if (index - one >= question.getNumCandidates().size() ||
                                    question.getMaterialWords().size() == 0 ||
                                    question.getStemWords().size() == 0)
                                flag = true;
                        }
                    }
                    if (flag) continue;

                    for (String word : question.getMaterialWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (String word : question.getStemWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (int i = 0; i < 4; i++) {

                        for (char word : question.getCandidates(i).toCharArray()) {
                            if ( word - one < 0) {
                                break;
                            }
                            for (String printWord : question.getNumCandidates().get(word - one).getWords()) {
                                wordStream.print(printWord + " ");
                            }
                        }
                        wordStream.println();

                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }

                    }
                }
                else {
                    int rightIndex = question.getAnswer().charAt(0) - 65;

                    for (String word : question.getMaterialWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (String word : question.getStemWords()) {
                        wordStream.print(word + " ");
                    }
                    wordStream.println();

                    for (int i = 0; i < 4; i++) {

                        for (String word : question.getCandidateWords(i)) {
                            wordStream.print(word + " ");
                        }
                        wordStream.println();

                        if (i == rightIndex) {
                            labelStream.println("1");
                        }
                        else {
                            labelStream.println("0");
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void outputCandidateCertainty(ArrayList<Question> questionList, String filename){
        deletFile(filename);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)));
            for(Question question : questionList){
                String line = "";
//                line += question.getID() + "\t" + question.getCandidateRealType() + "\n";
                if (question.questionRealType.size() != 0) {
                    line += question.getID() + "\t" + question.questionRealType.get(0) + "\n";
                }else {
                    line += question.getID() + "\t" + question.getCandidateType() + "\n";
                }
                for (int i = 0; i < 4; i++) {
                    line += question.getCandidateCertainties().get(i) + "\n";
                }
                bw.write(line);
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static void deletFile(String filename){
        File f = new File(filename);
        if(f.exists()){
            boolean isDeleteSuccess = f.delete();
            if (!isDeleteSuccess) System.out.println("删除失败：" + filename);
        }
    }
}
