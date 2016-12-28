package edu.question;

import java.util.ArrayList;

/**
 * Created by shawn on 16-2-25.
 */

public class QuestionCandidate {
    private String content = null;
    private ArrayList<String> words = new ArrayList<>();
    private ArrayList<String> ners = new ArrayList<>();
    private ArrayList<String> poses = new ArrayList<>();
    private ArrayList<String> originalPOSes = new ArrayList<>();

    public boolean setContent(String contentStr) {
        content = contentStr;
        if (content == null) {
            return false;
        }
        return true;
    }
    public String getContent() {
        return content;
    }

    public boolean setWords(ArrayList<String> wordSet) {
        words = wordSet;
        if (words == null) {
            return false;
        }
        return true;
    }
    public ArrayList<String> getWords() {
        return words;
    }

    public boolean setNERs(ArrayList<String> nerSet) {
        ners = nerSet;
        if (ners == null) {
            return false;
        }
        return true;
    }
    public ArrayList<String> getNERs() {
        return ners;
    }

    public boolean setPOSes(ArrayList<String> posSet) {
        poses =  posSet;
        if (poses == null) {
            return false;
        }
        return true;
    }
    public ArrayList<String> getPOSes() {
        return poses;
    }

    public boolean setOriginalPOSes(ArrayList<String> originalPOSSet) {
        originalPOSes = originalPOSSet;
        if (originalPOSes == null) {
            return false;
        }
        return true;
    }
    public ArrayList<String> getOriginalPOSes() {
        return originalPOSes;
    }
}
