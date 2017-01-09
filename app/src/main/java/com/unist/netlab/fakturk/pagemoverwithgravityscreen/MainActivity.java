package com.unist.netlab.fakturk.pagemoverwithgravityscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;

import Jama.Matrix;

import static android.hardware.SensorManager.GRAVITY_EARTH;

public class MainActivity extends AppCompatActivity
{

    WebView netlab;
    ArrowView gravityView, compassView;
    Button buttonStart, buttonReset;
    Switch switchGyr, switchAcc, switchReset, switchSmooth, switchGra, switchCompass, switchWeb;
    SeekBar slider;
    ViewGroup.MarginLayoutParams marginParams;
    TextView sensitivity, textViewLinear;

    Kalman kalman;
    Matrix X;
    RelativeLayout root;
    Filter filter;
    StatisticCalculations statistic;
    DecimalFormat df ;



    float[] acc, gyr, oldAcc, oldGyr,mag, oldMag,initialMag,gravity, sideY, sideX,velocity,distance, oldGravity, rotatedGyr, rotational_vel, rotational_vel_earth, linear_acc, linear_vel, linear_dist, startingEuler, rotMag;
    float[] lPAcc,mfLPAcc,hpVel;
    float[][] rotation, resultOfDynamic;
    boolean start, onlyGyr, accEnable, resetEnable, smoothEnable;

    Gravity g;
    Orientation orientation;
    DynamicAcceleration dynamic;
    int counter;
    float omega_x, omega_y, omega_z, angle;
    float new_x,new_y;
    int sliderValue;


    int top ;
    int bottom ;
    int left;
    int right ;
    int height ;
    int width ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = (RelativeLayout) findViewById(R.id.activity_main);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonReset = (Button) findViewById(R.id.buttonReset);
        switchGyr = (Switch) findViewById(R.id.switch_gyr);
        switchAcc = (Switch) findViewById(R.id.switchAcc);
        switchReset = (Switch) findViewById(R.id.switchReset);
        switchSmooth = (Switch) findViewById(R.id.switchSmoothReset);
        switchGra = (Switch) findViewById(R.id.switchGra);
        switchCompass = (Switch) findViewById(R.id.switchCompass);
        switchWeb = (Switch) findViewById(R.id.switchWeb);
        sensitivity = (TextView) findViewById(R.id.textView);
        textViewLinear = (TextView) findViewById(R.id.textViewLinear);
        slider = (SeekBar) findViewById(R.id.seekBar);
        sliderValue = 60;
        slider.setMax(60);
        slider.setProgress(60);

        kalman = new Kalman();
        top = root.getTop();
        bottom = root.getBottom();
        left = root.getLeft();
        right = root.getRight();
        height = textViewLinear.getHeight();
        width = textViewLinear.getWidth();

         velocity = new float[3];
        distance = new float[3];

        filter = new Filter();
        statistic = new StatisticCalculations();
        df = new DecimalFormat("#.####");






        gravityView = (ArrowView) findViewById(R.id.graView);
        compassView = (ArrowView) findViewById(R.id.compassView);

        netlab = (WebView) findViewById(R.id.webView);
        netlab.setWebViewClient(new MyWebViewClient());
        netlab.getSettings().setJavaScriptEnabled(true);
        netlab.loadUrl("http://netlab.unist.ac.kr/people/changhee-joo/");


        marginParams = new ViewGroup.MarginLayoutParams(netlab.getLayoutParams());

        acc = new float[3];
        gyr = new float[3];
        mag = new float[3];
        initialMag = new float[]{0, 0, 0};
        rotMag = new float[]{0, 0, 0};
        oldAcc = null;
        oldGyr = null;
        oldMag =null;
        gravity = new float[3];
        sideX = new float[3];
        sideY = new float[3];
        rotation = null;
        rotatedGyr = new float[3];
        resultOfDynamic = new float[5][3];
        rotational_vel = new float[]{0,0,0};
        rotational_vel_earth = new float[]{0,0,0};
        linear_acc = new float[]{0,0,0};
        linear_vel = new float[]{0,0,0};
        linear_dist = new float[]{0,0,0};
        startingEuler= new float[]{0,0,0};

        lPAcc= new float[]{0,0,0};
        mfLPAcc= new float[]{0,0,0};
        hpVel= new float[]{0,0,0};

        g = new Gravity();
        orientation = new Orientation();
        dynamic = new DynamicAcceleration();
        counter = 0;
        omega_x = 0;
        omega_y = 0;
        omega_z = 0;

        start = false;
        onlyGyr = false;
        accEnable=false;
        resetEnable=false;
        smoothEnable=false;

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

//                marginParams.setMargins(250, 250, 250, 250);

//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                netlab.setLayoutParams(layoutParams);


                acc = (intent.getFloatArrayExtra("ACC_DATA"));
                gyr = intent.getFloatArrayExtra("GYR_DATA");
                mag = intent.getFloatArrayExtra("MAG_DATA");

                if (acc != null && oldAcc == null)
                {
                    oldAcc = new float[3];
                    System.arraycopy(acc, 0, oldAcc, 0, acc.length);

                }
                if (gyr != null && oldGyr == null)
                {
                    oldGyr = new float[3];
                    System.arraycopy(gyr, 0, oldGyr, 0, gyr.length);

                }
                if (mag != null && oldMag == null)
                {
                    oldMag = new float[3];
                    System.arraycopy(mag, 0, oldMag, 0, mag.length);

                }
                if (acc == null && oldAcc != null)
                {
                    acc = new float[3];
                    System.arraycopy(oldAcc, 0, acc, 0, oldAcc.length);

                }
                if (gyr == null && oldGyr != null)
                {
                    gyr = new float[]{0, 0, 0};

                }
                if (mag == null && oldMag != null)
                {
                    mag = new float[3];
                    System.arraycopy(oldMag, 0, mag, 0, oldMag.length);

                }


                if (acc != null && gyr != null && mag!=null &&start != true )
                {
                    start = true;
                    Log.d("start","");
                    float accNorm = (float) Math.sqrt(Math.pow(acc[0], 2) + Math.pow(acc[1], 2) + Math.pow(acc[2], 2));
                    for (int j = 0; j < 3; j++)
                    {
                        gravity[j] = acc[j] * (GRAVITY_EARTH / accNorm);
                    }
                    rotation = orientation.rotationFromGravity(gravity);
                    startingEuler = orientation.eulerFromRotation(rotation);
//                    Log.d("initial mag  : ",initialMag[0]+" "+initialMag[1]+" "+initialMag[2]);

                    initialMag = orientation.rotationVectorMultiplication(orientation.rotationTranspose(rotation),mag);


//                    System.arraycopy(mag, 0, initialMag, 0, mag.length);
//                    Log.d("initial mag  : ",initialMag[0]+" "+initialMag[1]+" "+initialMag[2]);




                }
                if (start)
                {


                    float thresholdAcc = 0; //0.15
                    float thresholdGyr = 0.15f; //0.2
                    float accNorm = (float) Math.sqrt(Math.pow(acc[0], 2) + Math.pow(acc[1], 2) + Math.pow(acc[2], 2));
                    //if phone stable gravity = acc
                    if ((Math.abs(gyr[0]) + Math.abs(gyr[1]) + Math.abs(gyr[2])) < thresholdGyr)
                    {

//                        System.out.print("stable, ");
                        if (counter > 10) //if phone is stable over a second
                        {
                            if (resetEnable)
                            {
                                if (smoothEnable)
                                {
                                    smoothReset(2);
                                }
                                else
                                {
                                    reset();
                                }
                            }


//                            reset();
                            for (int j = 0; j < 3; j++)
                            {
                                gravity[j] = acc[j] * (GRAVITY_EARTH / accNorm);


                            }


                            counter = 0;
                        } else
                        {


                            counter++;


                        }


                    } else // not stable
                    {
//                        System.out.print("not stable, ");
                        counter = 0;

                        rotation = orientation.rotationFromGravity(gravity);
                        rotational_vel[0] += gyr[0] * dynamic.getDeltaT();
                        rotational_vel[1] -= gyr[1] * dynamic.getDeltaT();
                        rotational_vel[2] += gyr[2] * dynamic.getDeltaT();

                        rotatedGyr = orientation.rotatedGyr(gyr, rotation);

                        omega_x += rotatedGyr[0] * dynamic.getDeltaT();
                        omega_y += rotatedGyr[1] * dynamic.getDeltaT();
                        omega_z += rotatedGyr[2] * dynamic.getDeltaT();
                        rotation = orientation.updateRotationMatrix(rotation, rotatedGyr, dynamic.getDeltaT());

                        gravity = g.gravityAfterRotation(rotation);
//                        angle = orientation.angleBetweenMag(initialMag,mag);

//                        rotation = orientation.updateRotationAfterOmegaZ(rotation, omega_z);
                        rotMag = orientation.rotationVectorMultiplication(orientation.rotationTranspose(rotation),mag);
                        angle =  (orientation.angleBetweenMag(initialMag,orientation.rotationVectorMultiplication(orientation.rotationTranspose(rotation),mag)));
                        rotation = orientation.updateRotationAfterOmegaZ(rotation, angle);

                        float[] reRotatedGyr = orientation.reRotatedGyr(rotatedGyr,rotation);

//                        rotational_vel_earth[0]+=reRotatedGyr[0]* dynamic.getDeltaT();
//                        rotational_vel_earth[1]+=reRotatedGyr[1]* dynamic.getDeltaT();
//                        rotational_vel_earth[2]+=reRotatedGyr[2]* dynamic.getDeltaT();

//                        rotational_vel_earth = orientation.eulerFromRotation(orientation.rotationFromRotation(orientation.rotationFromEuler(startingEuler), rotation));

                        rotational_vel_earth = orientation.eulerFromRotation(rotation);
                        rotational_vel_earth = orientation.reRotatedGyr(rotational_vel_earth,rotation);

                        sideX = g.sideXAfterRotation(rotation);
                        sideY = g.sideYAfterRotation(rotation);


                    }


                    //store acc values
                    System.arraycopy(acc, 0, oldAcc, 0, acc.length);
                    System.arraycopy(mag, 0, oldMag, 0, acc.length);
                    float[] rotationValues = new float[]{0,0,0};

                    if (onlyGyr)
                    {
                        for (int i = 0; i < 3; i++)
                        {
                            rotationValues[i]=rotational_vel[i];
                        }


                    } else
                    {
                        for (int i = 0; i < 3; i++)
                        {
                            rotationValues[i]=rotational_vel_earth[i] - startingEuler[i];
//                            rotationValues[i]=rotational_vel_earth[i];
                        }
                        rotationValues[0]*=-1;

                    }
                    if (accEnable)
                    {
                        for (int i = 0; i <3 ; i++)
                        {
                            linear_acc[i]=acc[i]-gravity[i];
                            linear_vel[i]+=linear_acc[i]*dynamic.deltaT;
                            linear_dist[i]+=linear_vel[i]*dynamic.deltaT;

                        }

//                        netlab.setTranslationX(linear_dist[0]);
//                        netlab.setTranslationY(linear_dist[1]);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                        {
//                            netlab.setTranslationZ(linear_dist[2]);
//                        }

                    }
                    else
                    {

                    }

                    //set views
                    int lS = 20; // size coefficient of the line
//                    Log.d("magnetometer readings : ",mag[0]+", "+mag[1]+", "+mag[2]);
//                    Log.d("initial magnetometer readings : ",initialMag[0]+", "+initialMag[1]+", "+initialMag[2]);



//                    Log.d("acc readings : ",acc[0]+", "+acc[1]+", "+acc[2]);
//                    Log.d("gyr readings : ",gyr[0]+", "+gyr[1]+", "+gyr[2]);
//                    Log.d("magnetometer  : ",mag[0]+" "+mag[1]+" "+mag[2]);
//                    Log.d("initial mag  : ",initialMag[0]+" "+initialMag[1]+" "+initialMag[2]);
//                    Log.d("rotated mag  : ",rotMag[0]+" "+rotMag[1]+" "+rotMag[2]);
//                    Log.d("gravity readings : ",gravity[0]+", "+gravity[1]+", "+gravity[2]);


//                    Log.d("angle: ",Math.toDegrees(angle) +", "+Math.toDegrees(omega_z));

//                    Log.d("angle : ", String.valueOf(orientation.angleBetweenMag(initialMag,mag)));
                    float magMagnitude = (float) Math.sqrt( Math.pow(rotMag[0],2)+Math.pow(rotMag[1],2)+Math.pow(rotMag[2],2));
//                    float magMagnitude = 300;
                    float mS =lS*10/magMagnitude; // magnetometer line size coefficient
                    gravityView.setLine((-1)*gravity[0]*lS,gravity[1]*lS,gravity[2]*lS);

                    compassView.setLine(rotMag[0]*mS, -1*rotMag[1]*mS, rotMag[2]*mS);
                    compassView.setAccLine(initialMag[0]*mS, -1*initialMag[1]*mS, initialMag[2]*mS);
                    compassView.setType(String.valueOf(Math.toDegrees(angle)));

                    netlab.setRotationX(rotationValues[0] * sliderValue);
                    netlab.setRotationY( rotationValues[1] * sliderValue);
                    netlab.setRotation(rotationValues[2] * sliderValue);
//                    Log.d(rotationValues[2]*sliderValue);

//                    Log.d("ACC:",linear_acc[0]+" "+linear_acc[1]+" "+linear_acc[2]+" "+gravity[0]+" "+gravity[1]+" "+gravity[2]+" "+acc[0]+" "+acc[1]+" "+acc[2]);
//                    X = kalman.filter(dynamic.deltaT,linear_acc);
                    statistic.update(linear_acc);
                    lPAcc = filter.recursivelowPass(0.85f,acc,lPAcc);
                    mfLPAcc = filter.recursiveMechanic(statistic.getMean(),lPAcc);
                    float[] oldVel = new float[mfLPAcc.length];
                    for (int i = 0; i < 3; i++)
                    {
                        oldVel[i] = velocity[i];
                        if (Math.abs( mfLPAcc[i])>0.02)
                        velocity[i]= velocity[i] +dynamic.getDeltaT()*mfLPAcc[i];
                        else
                            velocity[i]=0;

                    }
                    hpVel=filter.recursivehighPass(0.85f,velocity,oldVel,hpVel);
                    for (int i = 0; i < distance.length; i++)
                    {
                        distance[i]= distance[i] +dynamic.getDeltaT()*hpVel[i];
                    }
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    float[] px = new float[3];

                    for (int i = 0; i < 3; i++) {
                        px[i] = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, distance[i]*1000,
                                dm);
                    }

//                    new_x -= px[0];
//                    new_y -= px[1];
//
//                    //oldVelocity = velocity;
//
//                    if(new_x<50)
//                    {
//                        new_x=50;
//                        velocity[0] = 0;
//                        // distanceX = 0;
//                    }
//                    if (new_x>right-width-50)
//                    {
//                        new_x = right-width-50;
//                        velocity[0] = 0;
//                        //distanceX = 0;
//                    }
//                    if(new_y<50)
//                    {
//                        new_y=50;
//                        velocity[0] = 0;
//                        // distanceX = 0;
//                    }
//                    if (new_y>bottom-height-50)
//                    {
//                        new_y = bottom-height-50;
//                        velocity[0] = 0;
//                        //distanceX = 0;
//                    }
//
//                    textViewLinear.setX(new_x);
//                    textViewLinear.setY(new_y);


                    textViewLinear.setText(
                            " linear acc : "+df.format( mfLPAcc[0])
//                            +",\n acc : "+acc[0]
                            +",\n VelocityX : "+df.format( hpVel[0]*1000)
                            +
                                    ",\n distanceX : "+df.format( distance[0]*100)
//                            + "\n new_x :"+new_x



                    );

                    Log.d(" linear acc : ",df.format( mfLPAcc[0])+" "+df.format( hpVel[0]*1000)+" "+df.format( distance[0]*100));





                }
            }
        }, new IntentFilter(SensorService.ACTION_SENSOR_BROADCAST));

        buttonStart.setOnClickListener(mGlobal_OnClickListener);
        buttonReset.setOnClickListener(mGlobal_OnClickListener);
        switchGyr.setOnClickListener(mGlobal_OnClickListener);
        switchAcc.setOnClickListener(mGlobal_OnClickListener);
        switchReset.setOnClickListener(mGlobal_OnClickListener);
        switchSmooth.setOnClickListener(mGlobal_OnClickListener);
        switchGra.setOnClickListener(mGlobal_OnClickListener);
        switchCompass.setOnClickListener(mGlobal_OnClickListener);
        switchWeb.setOnClickListener(mGlobal_OnClickListener);


        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                sliderValue = i;
                sensitivity.setText("Sensitiviy: "+i);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });




    }

    final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            switch(v.getId()) {
                case R.id.buttonStart:
                    if (buttonStart.getText().equals("Start"))
                    {
                        buttonStart.setText("Stop");
                        startService(new Intent(MainActivity.this, SensorService.class));

                    } else
                    {
                        buttonStart.setText("Start");
                        stopService(new Intent(MainActivity.this, SensorService.class));


                    }
                    break;
                case R.id.buttonReset:
//                  acc= new float[]{0, 0, 0};
//                  gyr = new float[]{0, 0, 0};
                    netlab.setTranslationX(0);
                    netlab.setTranslationY(0);
                    reset();
                    break;
                case R.id.switch_gyr:
                    if (switchGyr.isChecked())
                    {
                        onlyGyr = true;
                        switchReset.setEnabled(false);
                        switchSmooth.setEnabled(false);
                    } else
                    {
                        onlyGyr = false;
                        switchReset.setEnabled(true);
                        switchSmooth.setEnabled(true);
                    }
                    break;
                case R.id.switchAcc:
                    if (switchAcc.isChecked())
                    {
                        accEnable = true;
                    } else
                    {
                        accEnable = false;
                    }
                    break;
                case R.id.switchReset:
                    if (switchReset.isChecked())
                    {
                        resetEnable = true;
                        switchSmooth.setEnabled(true);
                    } else
                    {
                        resetEnable = false;
                        switchSmooth.setEnabled(false);
                        switchSmooth.setChecked(false);
                        smoothEnable=false;
                    }
                    break;
                case R.id.switchSmoothReset:
                    if (switchSmooth.isChecked())
                    {
                        smoothEnable = true;
                    } else
                    {
                        smoothEnable = false;
                    }
                    break;
                case R.id.switchGra:

                    if (switchGra.isChecked())
                    {
                        gravityView.setVisibility(View.VISIBLE);
                    } else
                    {
                        gravityView.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.id.switchCompass:
                    if (switchCompass.isChecked())
                    {
                        compassView.setVisibility(View.VISIBLE);
                    } else
                    {
                        compassView.setVisibility(View.INVISIBLE);
                    }
                    break;
                case R.id.switchWeb:
                    if (switchWeb.isChecked())
                    {
                        netlab.setVisibility(View.VISIBLE);
                        textViewLinear.setVisibility(View.INVISIBLE);

                    } else
                    {
                        netlab.setVisibility(View.INVISIBLE);
                        textViewLinear.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    private void smoothReset(float factor)
    {
//            float factor=10f;

        for (int j = 0; j < 3; j++)
        {
//                acc[j]/=factor;
//                gyr[j]/=factor;

            rotational_vel_earth[j]/=factor;
        }
        omega_x/=factor;
        omega_y /=factor;
        omega_z /=factor;

//        Log.d("rotation before: "+netlab.getRotation());
        netlab.setRotation(netlab.getRotation()/factor);
//        Log.d("rotation after: "+netlab.getRotation());
        netlab.setRotationX(netlab.getRotationX()/factor);
        netlab.setRotationY(netlab.getRotationY()/factor);

//        acc= new float[]{0, 0, 0};
//        gyr = new float[]{0, 0, 0};
//        omega_x =0;
//        omega_y =0;
//        omega_z =0;
//        rotational_vel_earth[0] = 0;
//        rotational_vel_earth[1] = 0;
//        rotational_vel_earth[2] = 0;
//
//        netlab.setRotation(0);
//        netlab.setRotationX(0);
//        netlab.setRotationY(0);
    }

    private void reset()
    {

        Log.d("initial mag  : ",initialMag[0]+" "+initialMag[1]+" "+initialMag[2]);

        netlab.setRotation(0);
        netlab.setRotationX(0);
        netlab.setRotationY(0);
        omega_x =0;
        omega_y =0;
        omega_z =0;

        start = false;
        rotational_vel_earth[0] = 0;
        rotational_vel_earth[1] = 0;
        rotational_vel_earth[2] = 0;
        rotational_vel=new float[]{0, 0, 0};
        for (int i = 0; i < 3; i++)
        {
            velocity[i]=0;
            distance[i]=0;
        }

        angle=0;



    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    netlab.setScrollY(netlab.getScrollY()-100);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    netlab.setScrollY(netlab.getScrollY()+100);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    public class MyWebViewClient extends WebViewClient
    {

        public MyWebViewClient()
        {

            super();
            //start anything you need to
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            //Do something to the urls, views, etc.
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
        stopService(getIntent());


    }


    @Override
    protected void onResume() {

        super.onResume();
//        startService(new Intent(this, SensorService.class));
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();
        stopService(getIntent());
    }


    @Override
    public void onStart() {

        super.onStart();


    }

    @Override
    public void onStop() {

        super.onStop();


    }
}
