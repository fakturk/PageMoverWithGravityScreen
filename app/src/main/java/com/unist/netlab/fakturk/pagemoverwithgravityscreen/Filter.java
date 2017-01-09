package com.unist.netlab.fakturk.pagemoverwithgravityscreen;

/**
 * Created by fakturk on 16. 5. 2.
 *
 */
class Filter
{
    Filter() {
    }



    // recursive low pass filter
    // y[n] = a0*x[n] + b1*y[n-1]
    // a0 = 1-alpha, b1 = alpha
    float[] recursivelowPass(float factor, float[] input, float[] output)
    {
        float a0=1-factor;
        float b1  = factor;

        if (output == null) return input;
        for (int i = 0; i < input.length; i++)
        {
            output[i] = a0*input[i]+ b1*output[i];
        }

        return output;
    }

    // recursive high pass filter
    // y[n] = a0*x[n] + a1*x[n-1]  + b1*y[n-1]
    // a0 = (1+alpha)/2, a1 = -(1+alpha)/2, b1 = alpha
    float[] recursivehighPass(float factor, float[] input, float[] inputOld, float[] output)
    {
        float a0=(1+factor)/2;
        float a1 = -(1+factor)/2;
        float b1  = factor;
//        Log.d("katsayi","a0 = "+a0+", a1 = "+a1+" ,b1 = "+b1);
//        Log.d("carpilmis","a0*input[i] :"+a0*input[0]+" ,a1*inputOld[i] : "+a1*inputOld[0]+" , b1*output[i] : "+b1*output[0]);
//        Log.d("total", "output[i] : "+(a0*input[0]+a1*inputOld[0]+ b1*output[0]));

        if (output == null) return input;
        for (int i = 0; i < input.length; i++)
        {
            output[i] = a0*input[i]+a1*inputOld[i]+ b1*output[i];
        }


        return output;
    }

    float[] recursiveMechanic(float[] mean, float[] input)
    {
        float[] output= new float[input.length];
        for (int i = 0; i < input.length; i++)
        {
            if (Math.abs(input[i])>mean[i])
            {
                output[i] = input[i];
            }
            else
            {
                output[i]=0;
            }
        }
        return output;
    }

}
