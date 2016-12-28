package edu.tools.zxr;

import edu.main.Const;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sunder on 2016/1/18.
 * 读入各种文件的工具类
 */
public class MyReadIn {
    public class BaikeReadIn{
        public int getCount() {
            return count;
        }

        private int count;
        private BufferedReader br;
        public BaikeReadIn(){
            try {
                this.br = new BufferedReader(new InputStreamReader(new FileInputStream(Const.BAIKE_FILEPATH), "gbk"));
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        public String nextDocument(){
            String line;
            String baikeDocument = "";
            try {
                while(!((line = this.br.readLine()) == null)){
                    line = line.trim();
                    baikeDocument += line;
                    if(Const.DOCUMENT_END_SYMBOL.equals(line)){
                        count++;
                        return baikeDocument;
                    }else if(Const.DOCUMENT_START_SYMBOL.equals(line))  baikeDocument = Const.DOCUMENT_START_SYMBOL;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String nextContent(String document){
            String dirtyContent = parse(document, Const.CONTENT_START_SYMBOL, Const.CONTENT_END_SYMBOL);
            return clean(dirtyContent);
        }

        public String nextTitle(String document){
            String dirtyTitle = parse(document, Const.TITLE_START_SYMBOL, Const.TITLE_END_SYMBOL);
            return clean(dirtyTitle);
        }

        public String nextID(String document){
            Matcher matcher = Pattern.compile("<sublemmaid>(.+)</sublemmaid>").matcher(document);
            if(matcher.find()) return matcher.group(1);
            return null;
        }

        String parse(String doc, String startSymbol, String endSymbol){
            String result = "";
            if(doc == null) return result;
            String pattern = startSymbol + "(.*?)" + endSymbol;
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(doc);
            if(m.find()){
                result = m.group(1);
            }else{
                System.out.println("No found!");
            }
            return result;
        }

        public String clean(String content){
            content = content.replaceAll("\\[sup\\].*?\\[/sup\\]", "");
            content = content.replaceAll("\\[img\\].*?\\[/img\\]", "");
            content = content.replaceAll("\\[.*?\\]", "");
            content = content.replaceAll("<.*?>", "");
            return content;
        }
    }

    public class NewBaikeReadIn{
        String[] fileNames;
        int count;
        String folderPath;
        public NewBaikeReadIn(String folderPath){
            File folder = new File(folderPath);
            this.fileNames = folder.list();
            this.folderPath = folderPath;
        }
        public String nextDocument(){
            if(count >= fileNames.length) return null;
            String filename = this.folderPath + this.fileNames[this.count];
            this.count++;
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
                String document = "";
                String line;
                while ((line = br.readLine()) != null){
                    document = document + line;
                }
                if("".equals(document)) return null;
                return document;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String nextContent(String document){
            return parseContent(document);
        }

        public String nextTitle(String document){
            return null;
        }

        String parseTitle(String document){
            return null;
        }

        String parseContent(String document){
            String content = "";
            String regex = "<div class=\"para\" label-module=\"para\">(.*?)</div>";
            Matcher matcher = Pattern.compile(regex).matcher(document);
            while (matcher.find()){
                content += matcher.group(1);
            }
            return clean(content);
        }

        public String clean(String dirty){
            return dirty.replaceAll("<.*?>", "");
        }
    }

    public class FolderReadIn{
        String[] fileNames;

        public int getFileNumber() {
            return fileNumber;
        }

        int fileNumber;
        int count;
        String folderPath;
        public FolderReadIn(String folderPath){
            File folder = new File(folderPath);
            this.fileNames = folder.list();
            this.folderPath = folderPath;
            this.fileNumber = this.fileNames.length;
        }
        public String getContent(String filename){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
                String document = "";
                String line;
                while ((line = br.readLine()) != null){
                    document = document + line;
                }
                if("".equals(document)) return null;
                return document;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String nextDocument(){
            if(count >= fileNames.length) return null;
            String filename = this.folderPath + this.fileNames[this.count];
            this.count++;
            return getContent(filename);
        }

        public String nextDocument(int index){
            if(count >= fileNames.length) return null;
            String filename = this.folderPath + this.fileNames[index];
            this.count = index + 1;
            return getContent(filename);
        }

    }

    public class WikipediaReadIn{
        private int count;
        private BufferedReader br;
        public WikipediaReadIn(){
            try {
                this.br = new BufferedReader(new InputStreamReader(new FileInputStream(Const.WIKIPEDIA_FILEPATH), "utf8"));
            } catch (UnsupportedEncodingException | FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        public String nextDocument(){
            String line;
            String document = "";
            try {
                while(!((line = this.br.readLine()) == null)){
                    if(line.startsWith("=== ")){
                        if (!"".equals(document)) {
                            count++;
                            return document;
                        }
                        document = line;
                    }else{
                        document += line;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void main(String[] args) {
        MyReadIn myReadIn = new MyReadIn();
        WikipediaReadIn wikipediaReadIn = myReadIn.new WikipediaReadIn();
        for(int i = 0; i < 5; i++){
            System.out.println(wikipediaReadIn.nextDocument());
        }
    }
}
