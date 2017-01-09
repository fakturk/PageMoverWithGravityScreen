package com.unist.netlab.fakturk.pagemoverwithgravityscreen;

import Jama.Matrix;

/**
 * Created by fakturk on 2/17/16.
 */
public class Kalman
{


    float dt;
    float[] ACC_DATA, VEL_DATA, POS_DATA;
    Matrix F;       // state matrix 9 X 9
    Matrix H;       // measurement sensitivity matrix 3 X 9
    Matrix P_post;  // a posteriori error covariance 9 X 9
    Matrix P_pre;   // a priori error covariance 9 X 9
    Matrix K;       // Kalman gain 9 X 3
    Matrix Q;       // process noise covariance matrix 9 X 9
    Matrix R;       // measurement noise covariance matrix 3 X 3
    Matrix I;       // identity matrix 9 X 9
    Matrix X;       // calculated position, velocity and accelerometer value
    Matrix X_pre;   // pre state estimation 9 X 1
    Matrix X_post;  // post state estimation 9 X 1
    Matrix Z;       // real measurement 3 X 1
    Matrix G;       // error matrix 9 X 3
    Matrix variance;       // v of error
    Matrix QVariance;

    public Kalman()
    {

        this.dt = 0;
        this.ACC_DATA = new float[3];
        this.VEL_DATA = new float[3];
        this.POS_DATA = new float[3];
        this.F = new Matrix(9,9);
        this.P_post = new Matrix(9,9);
        this.P_pre = new Matrix(9,9);
        this.K = new Matrix(9,3);
        this.Q = new Matrix(9,9);
        //this.R = new Matrix(3,3);
        this.I =  Matrix.identity(9,9);
        this.X_pre = new Matrix(9,1);
        this.X_post = new Matrix(9,1);
        this.X = new Matrix(9,1);
        this.Z = new Matrix(3,1);
        this.G = new Matrix(9,3);

        for (int i = 0; i < 9; i++) {
            P_pre.set(i,i,10);
        }

//        this.variance = new Matrix(q);
        double[][] qVar = {{0.1, 0, 0}, {0, 0.1, 0}, {0, 0, 0.1}};
        this.QVariance = new Matrix(qVar);



        this.R = new Matrix(qVar);
//        for (int i = 0; i <3 ; i++)
//        {
//                Log.d("covariance (R) ", q[i][0]+" "+q[i][1]+" "+q[i][2]);
//
//        }

        double[][] sensitivity = new double[][]{
                {0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1}
        };
        this.H = new Matrix(sensitivity);



    }




    public Matrix filter(float dt, float[] ACC_DATA) // q is v of error
    {
        // events
        this.dt = dt;
        for (int i=0;i<3;i++)
        {
            Z.set(i,0,ACC_DATA[i]);


        }
//        Log.d("ACC:",ACC_DATA[0]+" "+ACC_DATA[1]+" "+ACC_DATA[2]);
        double[][] error = new double[9][3];
        //= new double[]{0.5*dt*dt, 0.5*dt*dt, 0.5*dt*dt, dt, dt, dt, 0, 0, 0};
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (i==j)
                {
                    error[i][j]=0.5*dt*dt;
                }
                else if (i-3==j)
                {
                    error[i][j]=dt;
                }
                else if (i-6==j)
                {
                    error[i][j]=1;
                }
                else
                {
                    error[i][j]=0;
                }

            }
        }
        Matrix Error = new Matrix(error);
        G.setMatrix(0,8,0,2,Error);
        Q = (G.times(QVariance)).times(G.transpose());      // Q = G * v * G^T

        double[][] state = new double[9][9];
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                if (i==j)
                {
                    state[i][j]=1;
                }
                if(i==j-3)
                {
                    state[i][j]=dt;
                }
                if (i==j-6)
                {
                    state[i][j]=0.5*dt*dt;
                }
            }
        }

        Matrix State = new Matrix(state);
        F.setMatrix(0,8,0,8,State);


        //measurement update
        updateKalmanGain();
        updatePostEstimation();
        updatePostErrorCovariance();

        //time update
        updateNextPreEstimation();
        updateNextPreErrorCovariance();

//        Log.d("X_pre",+X_pre.get(0,0)+", "
//                      +X_pre.get(1,0)+", "
//                      +X_pre.get(2,0)+", "
//                      +X_pre.get(3,0)+", "
//                      +X_pre.get(4,0)+", "
//                      +X_pre.get(5,0)+", "
//                      +X_pre.get(6,0)+", "
//                      +X_pre.get(7,0)+", "
//                      +X_pre.get(8,0));



        //Matrix e_pre, e_post;
        //e_pre  = X.minus(X_pre);
        //e_post = X.minus(X_post);


        return X_pre;



    }






    public void updateKalmanGain()
    {
        Matrix nom = P_pre.times(H.transpose());
        Matrix denom = (H.times(P_pre)).times(H.transpose());
        denom.plusEquals(R);
        K = nom.times(denom.inverse());

    }

    public void updatePostEstimation()
    {
        X_post = X_pre.plus(K.times(Z.minus(H.times(X_pre))));
    }

    public void updatePostErrorCovariance()
    {
        P_post = (I.minus(K.times(H))).times(P_pre);
    }

    public void updateNextPreEstimation()
    {
        X_pre = F.times(X_post);
    }
    public void updateNextPreErrorCovariance()
    {
        P_pre = ((F.times(P_post)).times(F.transpose())).plus(Q);
    }
}
