package edu.question;

/**
 * Author:             Shawn Guo
 * E-mail:             air_fighter@163.com
 *
 * Create Time:        2015/11/26 15:19
 * Last Modified Time: 2015/11/26 16:17
 *
 * Class Name:         Perceptron
 * Class Function:
 *                     通用感知器算法，通过构建函数设置参数，然后调用Running，通过接口取结果。
 */
public class Perceptron {
    private double[][] x;
    private int[] y;
    private double[] w;
    private double b = 0.0;
    private double eta = 0.0;
    private int num = 0;
    private int dim = 0;
    private int iterNum = 0;

    public Perceptron(int instanceNum, int instanceDim, double learningRate) {
        eta = learningRate;
        num = instanceNum;
        dim = instanceDim;
        w = new double[dim];
        for (int i = 0; i < dim; i++)
            w[i] = 0.0;
        x = new double[num][dim];
        for (int i = 0; i < num; i++)
            for (int j = 0; j < dim; j++)
                x[i][j] = 0.0;
        y = new int[num];
        for (int i = 0; i < num; i++)
            y[i] = 0;
    }

    public boolean setInstanceAndLabel(double[][] inputX, int[] inputY) {
        for(int i = 0; i < num; i++)
            for(int j = 0; j < dim; j ++)
                x[i][j] = inputX[i][j];

        for(int i = 0; i < num; i++)
            y[i] = inputY[i];

        if(x[num-1][dim-1] != 0 && y[num-1] != 0)
            return true;
        else
            return false;
    }

    public void running() {
        int wrongNum = dim;
        for(int i = 0; i < dim; i++)
            w[i] = 0.0;
        while(wrongNum != 0) {
            iterNum++;
            wrongNum = num;
            for(int i = 0; i < num; i++) {
                if(multipleWandX(i) + b > 0) {
                    wrongNum--;
                    continue;
                }
                else {
                    for(int j = 0; j < dim; j++)
                        w[j] = w[j] + eta*y[i]*x[i][j];
                    b = b + eta*y[i];
                }
            }
            System.out.println("Iter:" + iterNum + "WrongNum:" + wrongNum);

        }
    }

    public double[] getW() {
        return w;
    }

    public double getB() {
        return b;
    }

    public int getIterNum() {
        return iterNum;
    }

    public double multipleWandX(int index) {
        double ret = 0.0;
        for (int i = 0; i < dim; i++)
            ret += w[i] * x[index][i];
        return ret;
    }
    
    public static void main(String[] args) {
        Perceptron self = new Perceptron(3, 2, 0.1);
        double[][] inputX = new double[][]{{3.0, 3.0}, {4.0, 3.0}, {1.0, 1.0}};
        int[] inputY = new int[]{1,1,-1};
        self.setInstanceAndLabel(inputX, inputY);
        self.running();
        double[] outW = self.getW();
        System.out.print("W: ");
        for(int i = 0; i < 2; i++)
            System.out.print(outW[i] + " ");
        double outB = self.getB();
        System.out.println("B: " + outB);
    }

}
