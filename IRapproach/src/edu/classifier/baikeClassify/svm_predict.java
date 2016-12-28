package edu.classifier.baikeClassify;

import edu.main.Const;
import libsvm.*;
import java.io.*;
import java.util.*;

public class svm_predict {
    private static double atof(String s)
    {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(String s)
    {
        return Integer.parseInt(s);
    }

    public static boolean predict(String line, svm_model model) throws IOException
    {
        if(line == null) return false;

        StringTokenizer st = new StringTokenizer(line," \t\n\r\f:");
        double target = atof(st.nextToken());
        int m = st.countTokens()/2;
        svm_node[] x = new svm_node[m];
        for(int j=0;j<m;j++)
        {
            x[j] = new svm_node();
            x[j].index = atoi(st.nextToken());
            x[j].value = atof(st.nextToken());
        }

        double result;
        result = svm.svm_predict(model,x);
//        System.out.println(result);
        return result == target;
    }

    public static svm_model loadModel() throws IOException
    {
        svm_model model = svm.svm_load_model(Const.BAIKE_CLASSIFY_MODEL_FILEPATH);
        if (model == null)
        {
            System.err.print("can't open model file\n");
            System.exit(1);
        }
        return model;
    }

}
