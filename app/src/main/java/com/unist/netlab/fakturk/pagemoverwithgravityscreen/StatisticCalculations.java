package com.unist.netlab.fakturk.pagemoverwithgravityscreen;

/**
 * Created by fakturk on 09/01/2017.
 */

public class StatisticCalculations
{
    float[] mean;
    float[] oldMean;
    float[] v;

    public float[] getMean()
    {

        return mean;
    }

    public float[] getVariance()
    {

        return variance;
    }

    float[] variance;
    int k=0;

    public StatisticCalculations(float[] x)
    {
        mean = new float[x.length];
        oldMean = new float[x.length];
        v = new float[x.length];
        variance = new float[x.length];
        k=1;

        for (int i = 0; i < x.length; i++)
        {
            mean[i]=x[i];
            oldMean[i]=0;
            v[i]=0;
            variance[i]=0;
        }
    }
    public StatisticCalculations()
    {
        mean = new float[3];
        oldMean = new float[3];
        v = new float[3];
        variance = new float[3];
        k=0;

        for (int i = 0; i < 3; i++)
        {
            mean[i]=0;
            oldMean[i]=0;
            v[i]=0;
            variance[i]=0;
        }
    }

    float[] updateMean(float[] x)
    {

        for (int i = 0; i < mean.length; i++)
        {
            oldMean[i]=mean[i];
            mean[i]=mean[i]+(x[i]-mean[i])/k;
        }

        return mean;
    }

    float[] updateVariance(float[] x, float[] mean, float[] oldMean)
    {

        for (int i = 0; i < v.length; i++)
        {
            v[i]= v[i]+(x[i]-oldMean[i])*(x[i]-mean[i]);
            variance[i]=v[i]/(k-1);
        }
        return variance;

    }

    void update(float[] x)
    {
        k++;
        updateMean(x);
        updateVariance(x,mean,oldMean);

    }

    float[][] covariance()
    {
        float[][] cov = new float[variance.length][variance.length];
        for (int i = 0; i < variance.length; i++)
        {
            for (int j = 0; j < variance.length; j++)
            {
                cov[i][j] = variance[i]*variance[j];
            }
        }
        return cov;
    }

    float[][] varianceMatrix()
    {
        float[][] varMatrix = new float[variance.length][variance.length];
        for (int i = 0; i < variance.length; i++)
        {
            for (int j = 0; j < variance.length; j++)
            {
                if (i==j)
                {
                    varMatrix[i][j]=variance[i]*variance[j];
                }
                else
                {
                    varMatrix[i][j]=0;
                }

            }
        }
        return varMatrix;
    }
}
