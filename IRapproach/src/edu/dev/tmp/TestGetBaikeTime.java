package edu.dev.tmp;

import edu.others.historyTime.PredictBaikeTime;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sunder on 2016/1/6.
 */
public class TestGetBaikeTime {
    public static void main(String[] args) {
        try {
            TestGetBaikeTime testGetBaikeTime = new TestGetBaikeTime();
            testGetBaikeTime.run("data/original/100BaikeEvent", "data/original/100BaikeEventTime");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void run(String filename, String timeFilename)throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        BufferedReader brTime = new BufferedReader(new InputStreamReader(new FileInputStream(timeFilename)));
        String line;
        boolean isTitle = true;
        while ((line = br.readLine()) != null){
            if(!isTitle){
                int time = PredictBaikeTime.getTime(line);
                String answer = brTime.readLine();
                System.out.println(time + " " + answer);
            }
            isTitle = !isTitle;
        }
    }
}
