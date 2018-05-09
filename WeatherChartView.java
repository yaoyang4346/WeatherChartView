package com.app.chenyang.sweather.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.app.chenyang.sweather.R;
import com.app.chenyang.sweather.utils.BaseUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WeatherChartView extends View{
    private static final int DAY_NUM = 6;
    private int[] temMax = new int[DAY_NUM];
    private int[] temMin = new int[DAY_NUM];
    private Bitmap[] weatherIcon = new Bitmap[DAY_NUM];
    private String[] time = new String[DAY_NUM];
    private int mHeight;
    private int mWidth;
    private Paint mTextPaint;
    private static final float TEXT_SIZE  = BaseUtils.dpToPx(12.5);
    private static final int TEXT_COLOR  = Color.WHITE;
    private static final int PART_MARGIN = BaseUtils.dpToPx(25);
    private int maxInMax;
    private int minInMax;
    private int maxInMin;
    private int minInMin;
    private int diffInMax;
    private int diffInMin;
    private int partHeight;
    private int dayWidth;
    private int firstPointX;
    private int diffInMaxHeight;
    private int diffInMinHeight;
    private static final int PART = 3;
    private static final int DIVIDE_MARGIN = BaseUtils.dpToPx(10);
    private Paint mDayDividePaint;
    private static final int DIVIDE_COLOR = Color.parseColor("#42CDFF");
    private static final int DIVIDE_WIDTH = 2;
    private Paint mPointPaint;
    private static final int POINT_COLOR = Color.WHITE;
    private static final int POINT_RADIUS = BaseUtils.dpToPx(2.5);
    private Paint mMaxLinePaint;
    private static final int MAX_LINE_COLOR = Color.WHITE;
    private static final int TEMPERATURE_LINE_WIDTH = 2;
    private Paint mMinLintPaint;
    private static final int MIN_LINE_COLOR = Color.WHITE;
    private static final String DEGREE = "°";
    private static final int TEXT_MARGIN = BaseUtils.dpToPx(6);
    private int animFlag = 0;

    public WeatherChartView(Context context) {
        this(context,null);
    }

    public WeatherChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initPaint();

    }

    private void initPaint(){
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setColor(TEXT_COLOR);

        mDayDividePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDayDividePaint.setStrokeWidth(DIVIDE_WIDTH);
        mDayDividePaint.setColor(DIVIDE_COLOR);

        mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(POINT_COLOR);
        mPointPaint.setStyle(Paint.Style.FILL);

        mMaxLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaxLinePaint.setColor(MAX_LINE_COLOR);
        mMaxLinePaint.setStyle(Paint.Style.STROKE);
        mMaxLinePaint.setStrokeWidth(TEMPERATURE_LINE_WIDTH);

        mMinLintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMinLintPaint.setColor(MIN_LINE_COLOR);
        mMinLintPaint.setStyle(Paint.Style.STROKE);
        mMinLintPaint.setStrokeWidth(TEMPERATURE_LINE_WIDTH);
    }

    private void computer() {
        maxInMax = getMax(temMax);
        minInMax = getMin(temMax);

        maxInMin = getMax(temMin);
        minInMin = getMin(temMin);

        diffInMax = maxInMax - minInMax;
        diffInMin = maxInMin - minInMin;

        diffInMaxHeight = diffInMax == 0 ? 0: (partHeight- PART_MARGIN *2) / diffInMax;
        diffInMinHeight = diffInMin == 0 ? 0: (partHeight- PART_MARGIN *2) / diffInMin;

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < DAY_NUM ; i++){
            if(i==0){
                time[i] = getResources().getString(R.string.today);
                continue;
            }
            calendar.add(Calendar.DAY_OF_WEEK,1);
            time[i] = new SimpleDateFormat("EEEE",getResources().getConfiguration().locale).format(calendar.getTime());
        }
    }

    private int getMin(int[] m) {
        int tmp = m[0];
        for (int i : m) {
            tmp = tmp < i ? tmp : i;
        }
        return tmp;
    }

    private int getMax(int[] m) {
        int tmp = m[0];
        for (int i : m) {
            tmp = tmp > i ? tmp : i;
        }
        return tmp;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;
        partHeight = mHeight / PART;
        dayWidth = mWidth / DAY_NUM;
        firstPointX = dayWidth / 2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.colorTransparent));
        computer();
        drawDayDivide(canvas,animFlag);
        drawPointAndTemperatureLine(canvas,animFlag);
        drawWeatherIconAndText(canvas,animFlag);
    }

    public void startAnimation(){
        animFlag = 0;
        handler.sendEmptyMessage(0);
    }

    public void cleanAnimation(){
        handler.removeMessages(0);
        animFlag = 0;
        postInvalidate();
    }

    private android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (animFlag < DAY_NUM){
                postInvalidate();
                animFlag++;
                sendEmptyMessageDelayed(0,50);
            }
        }
    };

    private void drawWeatherIconAndText(Canvas canvas,int flag) {
        Bitmap icon = null;
        for (int i = 0 ; i < flag ; i++){
            if(weatherIcon[i]==null){
                continue;
            }
            float r = (partHeight - partHeight * 3 / 9 - mTextPaint.descent() + mTextPaint.ascent() ) / weatherIcon[i].getHeight();
            icon = scaleBitmap(weatherIcon[i],r);
            canvas.drawBitmap(icon, dayWidth / 2 - icon.getWidth() / 2 + dayWidth * i, partHeight * 2 , null);
            canvas.drawText(time[i], dayWidth / 2 - mTextPaint.measureText(time[i]) / 2 + dayWidth * i, partHeight * 2 + icon.getHeight() + partHeight * 2 / 9, mTextPaint);

        }
    }

    private void drawPointAndTemperatureLine(Canvas canvas,int flag) {
        int x,y;
        Path maxPath = new Path();
        Path minPath = new Path();
        String temp ;
        for(int i = 0 ; i < flag ; i++){
            x = firstPointX + dayWidth * i;

            if(diffInMax == 0){
                y = partHeight / 2;
            }else{
                y = Math.abs(temMax[i] - maxInMax) * diffInMaxHeight + PART_MARGIN;
            }
            canvas.drawCircle(x , y , POINT_RADIUS , mPointPaint);
            temp = temMax[i]+DEGREE;
            canvas.drawText(temp , x - mTextPaint.measureText(temp)/2 , y - TEXT_MARGIN , mTextPaint);
            if(i == 0){
                maxPath.moveTo(x , y);
            }else{
                maxPath.lineTo(x , y);
            }

            if(diffInMin == 0){
                y = partHeight / 2 + partHeight;
            }else{
                y = Math.abs(temMin[i] - maxInMin) * diffInMinHeight + PART_MARGIN + partHeight;
            }
            canvas.drawCircle(x , y , POINT_RADIUS , mPointPaint);
            temp = temMin[i]+DEGREE;
            canvas.drawText(temp , x - mTextPaint.measureText(temp)/2 , y - TEXT_MARGIN , mTextPaint);
            if(i == 0){
                minPath.moveTo(x , y);
            }else{
                minPath.lineTo(x , y);
            }
        }
        canvas.drawPath(maxPath , mMaxLinePaint);
        canvas.drawPath(minPath , mMinLintPaint);
    }

    private Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        return newBM;
    }

    private void drawDayDivide(Canvas canvas,int flag) {
        for(int i = 1 ; i < flag ; i++){
            canvas.drawLine(i * dayWidth, DIVIDE_MARGIN, i * dayWidth, mHeight - DIVIDE_MARGIN, mDayDividePaint);
        }
    }

    public void setTemperatureAndIcon(ArrayList<Integer> max, ArrayList<Integer> min, ArrayList<Integer> icon){
        for (int i = 0 ; i < DAY_NUM ; i++){
            temMax[i] = max == null ? 0 : max.get(i);
            temMin[i] = min == null ? 0 : min.get(i);
            weatherIcon[i] = BitmapFactory.decodeResource(getResources(),icon ==null ? R.mipmap.s999 : icon.get(i));
        }
        invalidate();
    }

}
