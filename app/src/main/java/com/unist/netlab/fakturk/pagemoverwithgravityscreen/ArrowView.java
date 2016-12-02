package com.unist.netlab.fakturk.pagemoverwithgravityscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fakturk on 24/10/2016.
 */

public class ArrowView extends View
{
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintBlue = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintGreen= new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint thinLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintTextLarge = new Paint(Paint.ANTI_ALIAS_FLAG);

    float lineStartX, lineStartY, lineFinishX, lineFinishY, lineZStartX, lineZStartY, lineZFinishX, lineZFinishY;
    float accXFinish, accYFinish, accZFinish;
    String type;



    private float coefficient;


    public ArrowView(Context context)
    {

        super(context);

        init();

    }
    public ArrowView(Context context, AttributeSet attrs, int defStyle)
    {

        super(context, attrs, defStyle);


        init();

    }
    public ArrowView(Context context, AttributeSet attrs)
    {

        super(context, attrs);

        init();

    }



    @Override
    protected void onDraw(Canvas c)
    {
        super.onDraw(c);
        lineStartX= this.getWidth()/2;
        lineStartY = this.getHeight()/2;
        lineZStartX =this.getWidth()-50;
        lineZStartY = lineStartY;
        lineZFinishX = 0;


        //graph itself
        c.drawLine(lineStartX,20,lineStartX,this.getHeight()-20,thinLine); //yatay vertical
        c.drawLine(20,lineStartY,this.getWidth()-20,lineStartY,thinLine); //dikey horizontal
        c.drawLine(lineZStartX,20,lineZStartX,this.getHeight()-20,thinLine); //dikey Z horizontal

        c.drawLine(lineStartX,20,lineStartX+20,40,thinLine);
        c.drawLine(lineStartX,20,lineStartX-20,40,thinLine);
        c.drawLine(lineStartX,this.getHeight()-20,lineStartX+20,this.getHeight()-40,thinLine);
        c.drawLine(lineStartX,this.getHeight()-20,lineStartX-20,this.getHeight()-40,thinLine);
        c.drawText("y",lineStartX-50,50,paintTextLarge);
        c.drawText("-y",lineStartX-50,this.getHeight()-50,paintTextLarge);


        c.drawLine(20,lineStartY,40,lineStartY+20,thinLine);
        c.drawLine(20,lineStartY,40,lineStartY-20,thinLine);
        c.drawText("-x",20,lineStartY+50,paintTextLarge);

        c.drawLine(lineZStartX,20,lineZStartX+20,40,thinLine);
        c.drawLine(lineZStartX,20,lineZStartX-20,40,thinLine);
        c.drawLine(lineZStartX,this.getHeight()-20,lineZStartX+20,this.getHeight()-40,thinLine);
        c.drawLine(lineZStartX,this.getHeight()-20,lineZStartX-20,this.getHeight()-40,thinLine);
        c.drawText("z",lineZStartX-50,50,paintTextLarge);
        c.drawText("-z",lineZStartX-50,this.getHeight()-50,paintTextLarge);

        c.drawLine(lineStartX,lineStartY,lineStartX+accXFinish,lineStartY+accYFinish,paint);

        //name label
        c.drawText(type,lineStartX-130,lineStartY-40,paintTextLarge);


        for (int i = 0; i <=20; i++)
        {
            c.drawLine(lineStartX-10,lineStartY+20*(i-10)*coefficient,lineStartX+10,lineStartY+20*(i-10)*coefficient,thinLine); //small ticks on y axis
            if (i-10!=0)
            {
                if (coefficient>=1)
                c.drawText(Integer.toString(10-i),lineStartX+30,lineStartY+20*(i-10)*coefficient+5,paintText);
            }


            c.drawLine(lineZStartX-10,lineStartY+20*(i-10)*coefficient,lineZStartX+10,lineStartY+20*(i-10)*coefficient,thinLine);
            if (i-10!=0)
            {
                if (coefficient>=1)
                c.drawText(Integer.toString(10-i),lineZStartX+20,lineStartY+20*(i-10)*coefficient+5,paintText);

            }

            c.drawLine(lineStartX+20*(i-10)*coefficient, lineStartY-10,lineStartX+20*(i-10)*coefficient, lineStartY+10,thinLine);
            if (i-10!=0)
            {
                if (coefficient>=1)
                c.drawText(Integer.toString(i-10),lineStartX+20*(i-10)*coefficient,lineStartY+50,paintText);
            }

        }



//        System.out.println("ondraw gra: "+lineStartX+", "+lineStartY+", "+lineFinishX+", "+lineFinishY);

        if (Math.abs(lineFinishX)>Math.abs(lineFinishY))
        {
            c.drawLine(lineStartX,lineStartY,lineStartX+lineFinishX,lineStartY+lineFinishY,paintGreen);


        }
        else
        {
            c.drawLine(lineStartX,lineStartY,lineStartX+lineFinishX,lineStartY+lineFinishY,paintBlue);
        }
        c.drawLine(lineZStartX,lineZStartY,lineZStartX+lineZFinishX,lineZStartY+lineZFinishY,paintRed);

    }

//

    void setLine(float lineFinishX, float lineFinishY, float lineFinishZ)
    {


        this.lineFinishX = coefficient*lineFinishX;
        this.lineFinishY = coefficient*lineFinishY;
        this.lineZFinishY = coefficient*lineFinishZ;
//        System.out.println("setLine: "+lineStartX+", "+lineStartY+", "+lineFinishX+", "+lineFinishY);
        invalidate();

//        System.out.println("setLine after invalidate: "+lineStartX+", "+lineStartY+", "+lineFinishX+", "+lineFinishY);

    }
    void setAccLine(float accXFinish, float accYFinish, float accZFinish)
    {
        this.accXFinish = accXFinish;
        this.accYFinish = accYFinish;
        this.accZFinish = accZFinish;
    }

    void setCoefficient(float coefficient)
    {
        this.coefficient = coefficient;
    }
    public float getCoefficient()
    {

        return coefficient;
    }
    void setType(String type)
    {
        this.type = type;
    }

    void init()
    {


        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);


        paintText.setColor(Color.BLACK);
        paintText.setStyle(Paint.Style.STROKE);
        paintText.setTextSize(22);

        paintTextLarge.setColor(Color.BLACK);
//        paintText.setStyle(Paint.Style.STROKE);
        paintTextLarge.setTextSize(36);


        thinLine.setColor(Color.BLACK);
        thinLine.setStyle(Paint.Style.STROKE);
        thinLine.setStrokeWidth(1);

        paintGreen.setColor(Color.GREEN);
        paintGreen.setStyle(Paint.Style.STROKE);
        paintGreen.setStrokeWidth(8);

        paintRed.setColor(Color.RED);
        paintRed.setStyle(Paint.Style.STROKE);
        paintRed.setStrokeWidth(8);

        paintBlue.setColor(Color.BLUE);
        paintBlue.setStyle(Paint.Style.STROKE);
        paintBlue.setStrokeWidth(8);

        lineStartX=0;
        lineStartY=0;
        lineFinishX=0;
        lineFinishY=0;
        coefficient = 1;
        type = "Gravity";

    }

}
