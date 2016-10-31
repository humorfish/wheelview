package com.you.wheelviewlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by you on 2016/7/13.
 */
public class BodyPartView extends View {
    private final String TAG = "BodyPartView";
    private final int MAJOR_MOVE = 2;
    public static final byte BIGWHEELVIEW = 0x01;
    public static final byte LITTLEWHEELVIEW = 0x02;
    private byte WHEELTYPE = 0X00;
    private int screenWidth = 480;
    private int screenHeigth = 800;

    int[] dxdy = new int[2];
    Bitmap centerBitmap;
    Bitmap leftBitmap;
    Bitmap rightBitmap;
    int centerBitmapId = -1;

    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 0;

    private float mRadius;
    private int radiuWidth;
    private int radiuHeight;
    private int centerX, centerY;
    private int backgroundColorId;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MotionEvent.ACTION_DOWN:
                    handleActionDown();
                    break;
                case MotionEvent.ACTION_UP:
                    handleActionUp();
                    break;
                case MotionEvent.ACTION_MOVE:
                    handleActionMove();
                    break;
                default:
                    break;
            }
        }
    };

    private int[] bodyparts = {
            R.drawable.call_bodypart_upperarm,
            R.drawable.call_bodypart_elbow,
            R.drawable.call_bodypart_forearm,
            R.drawable.call_bodypart_hand,
            R.drawable.call_bodypart_shoulders,
            R.drawable.call_bodypart_cervicals,
            R.drawable.call_bodypart_dorsals,
            R.drawable.call_bodypart_lumbar,
            R.drawable.call_bodypart_abdomen,
            R.drawable.call_bodypart_buttocks,
            R.drawable.call_bodypart_thigh,
            R.drawable.call_bodypart_knee,
            R.drawable.call_bodypart_calf,
            R.drawable.call_bodypart_ankle,
            R.drawable.call_bodypart_foot
    };

    private int[] activitys = {
            R.drawable.call_activity_fitness,
            R.drawable.call_activity_care,
            R.drawable.call_activity_relax
    };

    public BodyPartView(Context context) {
        super(context);
    }

    public BodyPartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BodyPartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawLines(canvas);
        drawBodypart(canvas);
    }

    public void init(int screenWidth, int screenHeigth) {
        this.screenWidth = screenWidth;
        this.screenHeigth = screenHeigth;
    }

    public void setBackground(int backgroundColorId) {
        this.backgroundColorId = backgroundColorId;
    }

    public void setWheelType(byte wheelType, int centerBitmapId){
        if (WHEELTYPE != 0x00)
            return;

        WHEELTYPE = wheelType;
        initBitmap(centerBitmapId);
    }

    public void setStartAngle(double mStartAngle){
        this.mStartAngle = mStartAngle;
    }

    private void initBitmap(int centerBitmapId) {
        this.centerBitmapId = centerBitmapId;

        int centerResourceId = 0;
        int leftResourceId =0;
        int rightResourceId =0;
        if (WHEELTYPE == LITTLEWHEELVIEW){
            if (centerBitmapId == 0){
                centerResourceId = activitys[this.centerBitmapId];
                rightResourceId = activitys[this.centerBitmapId+1];
                leftResourceId = activitys[activitys.length -1];
            } else if(centerBitmapId == activitys.length -1){
                centerResourceId = activitys[0];
                rightResourceId = activitys[1];
                leftResourceId = activitys[activitys.length -1];
            } else if(centerBitmapId < 0){
                this.centerBitmapId = activitys.length -1;
                centerResourceId = activitys[this.centerBitmapId];
                rightResourceId = activitys[0];
                leftResourceId = activitys[this.centerBitmapId -1];
            } else if (this.centerBitmapId >= activitys.length){
                this.centerBitmapId = 0;
                centerResourceId = activitys[this.centerBitmapId];
                rightResourceId = activitys[this.centerBitmapId + 1];
                leftResourceId = activitys[activitys.length -1];
            } else {
                centerResourceId = activitys[this.centerBitmapId];
                rightResourceId = activitys[this.centerBitmapId + 1];
                leftResourceId = activitys[this.centerBitmapId -1];
            }

        } else if (WHEELTYPE == BIGWHEELVIEW){
            if (centerBitmapId == 0){
                centerResourceId = bodyparts[this.centerBitmapId];
                rightResourceId = bodyparts[this.centerBitmapId+1];
                leftResourceId = bodyparts[bodyparts.length -1];
            } else if(centerBitmapId >= bodyparts.length){
                this.centerBitmapId = 0;
                centerResourceId = bodyparts[this.centerBitmapId];
                rightResourceId = bodyparts[this.centerBitmapId +1];
                leftResourceId = bodyparts[bodyparts.length -1];
            } else if(centerBitmapId < 0){
                this.centerBitmapId = bodyparts.length -1;
                centerResourceId = bodyparts[this.centerBitmapId];
                rightResourceId = bodyparts[0];
                leftResourceId = bodyparts[this.centerBitmapId -1];
            } else if(this.centerBitmapId == bodyparts.length -1){
                centerResourceId = bodyparts[bodyparts.length -1];
                rightResourceId = bodyparts[0];
                leftResourceId = bodyparts[bodyparts.length -2];
            } else{
                centerResourceId = bodyparts[this.centerBitmapId];
                rightResourceId = bodyparts[this.centerBitmapId+1];
                leftResourceId = bodyparts[this.centerBitmapId -1];
            }
        }

        Bitmap bitmapTmp = BitmapFactory.decodeResource(getResources(), rightResourceId);
        rightBitmap = Bitmap.createScaledBitmap(bitmapTmp, screenWidth/3, screenWidth/3, true);
        bitmapTmp.recycle(); // 释放Bitmap的native像素数组

        bitmapTmp = BitmapFactory.decodeResource(getResources(), leftResourceId);
        leftBitmap = Bitmap.createScaledBitmap(bitmapTmp, screenWidth/3, screenWidth/3, true);
        bitmapTmp.recycle(); // 释放Bitmap的native像素数组

        bitmapTmp = BitmapFactory.decodeResource(getResources(), centerResourceId);
        centerBitmap = Bitmap.createScaledBitmap(bitmapTmp, screenWidth/3, screenWidth/3, true);
        bitmapTmp.recycle(); // 释放Bitmap的native像素数组

        Log.i(TAG, "setBitmap.centerBitmapId:" + centerBitmapId);
    }

    private void drawBackground(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(5);
        mPaint.setColor(backgroundColorId);
        canvas.drawCircle(centerX, getHeight(), mRadius, mPaint);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerX, getHeight(), mRadius, mPaint);
    }

    private void drawBodypart(Canvas canvas) {
        if (centerBitmap == null)
            return;

        int bitmapWidth = centerBitmap.getWidth();
        double mAngle= -mStartAngle % 360;

        // 中间的 图片
        int left = centerX - bitmapWidth / 2 ;//+ (int) deltaX
        int top = centerY - centerX - bitmapWidth / 2 ;//+ (int) deltaY
        //创建操作图片是用的matrix对象
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.preTranslate(left, top);
        //旋转图片动作
        matrix.postRotate((float) mAngle, centerX, centerY);//以坐标50，100 旋转30°
        canvas.drawBitmap(centerBitmap, matrix, null);

        matrix.reset();
        matrix.preTranslate(left, top);
        //左边的图片
        matrix.postRotate((float) mAngle + 90, centerX, centerY);//以坐标50，100 旋转30°
        canvas.drawBitmap(leftBitmap, matrix, null);

        matrix.reset();
        matrix.preTranslate(left, top);
        //右边的图片
        matrix.postRotate((float) mAngle - 90, centerX, centerY);//以坐标50，100 旋转30°
        canvas.drawBitmap(rightBitmap, matrix, null);

        if (WHEELTYPE == LITTLEWHEELVIEW){
            matrix.reset();
            matrix.preTranslate(left, top);
            //右边的图片
            matrix.postRotate((float) mAngle - 90 - 90, centerX, centerY);//以坐标50，100 旋转30°
            canvas.drawBitmap(centerBitmap, matrix, null);
        }
    }

    double firstPositonAngle = 45;
    private void drawLines(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        double quyu = mStartAngle%360;

        dxdy[0] = (int) (centerX + mRadius* Math.cos(Math.toRadians(firstPositonAngle + quyu)));
        dxdy[1] = (int) (getHeight() - mRadius*(Math.sin(Math.toRadians(firstPositonAngle + quyu))));

        canvas.drawLine(centerX, getHeight(), dxdy[0], dxdy[1], mPaint);

        dxdy[0] = (int) (centerX + mRadius* Math.cos(Math.toRadians(firstPositonAngle + 90 +  quyu)));
        dxdy[1] = (int) (getHeight() - mRadius*(Math.sin(Math.toRadians(firstPositonAngle + 90 + quyu))));
        canvas.drawLine(centerX, getHeight(), dxdy[0], dxdy[1], mPaint);

        dxdy[0] = (int) (centerX + mRadius* Math.cos(Math.toRadians(firstPositonAngle - 90 + quyu)));
        dxdy[1] = (int) (getHeight() - mRadius*(Math.sin(Math.toRadians(firstPositonAngle - 90 + quyu))));

        canvas.drawLine(centerX, getHeight(), dxdy[0], dxdy[1], mPaint);

        dxdy[0] = (int) (centerX + mRadius* Math.cos(Math.toRadians(firstPositonAngle + 90 + 90 + quyu)));
        dxdy[1] = (int) (getHeight() - mRadius*(Math.sin(Math.toRadians(firstPositonAngle + 90 + 90 + quyu))));
        canvas.drawLine(centerX, getHeight(), dxdy[0], dxdy[1], mPaint);
    }

    private void handleActionDown() {
        Log.i(TAG, "handleActionDown.angle = " + mStartAngle + "  centerBitmapId:" + this.centerBitmapId);
        initBitmap(this.centerBitmapId);
        invalidate();
    }


    private void handleActionMove() {
        Log.i(TAG, "handleActionMove.angle:" + mStartAngle + "  centerBitmapId:" + centerBitmapId);
        invalidate();
    }

    private void handleActionUp() {
        int currentAngle = (int)mStartAngle%360;

        if (currentAngle%90 > 0 && currentAngle%90 < 45){
            mStartAngle = 0;
        } else if (currentAngle%90 >= 45 && currentAngle%90 < 90){
            mStartAngle = 90;
            this.centerBitmapId -=1;
        } else if(currentAngle%90 < 0 && currentAngle%90 > -45){
            mStartAngle = 0;
        } else if(currentAngle%90 <= -45 && currentAngle%90 >- 90){
            mStartAngle = -90;
            this.centerBitmapId +=1;
        }

        Log.i(TAG, "handleActionUp.angle = " + mStartAngle + "  centerBitmapId:" + centerBitmapId);
        invalidate();
    }


    /**
     * 根据触摸的位置，计算角度
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - (mRadius / 2d);
        double y = yTouch - (mRadius / 2d);
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置计算象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX >= 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    public void refeshView(int action) {
        Message msg = Message.obtain();
        msg.what = action;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            radiuWidth = getWidth() / 2;
            radiuHeight = getHeight() / 2;
            centerX = getWidth() / 2;
            centerY = getHeight();
            mRadius = (float) Math.sqrt(2) * getWidth() / 2;
            Log.i(TAG, "------->onLayout.getHeight" + getHeight() + "  getWidth" + getWidth() + "  mRadius" + mRadius);
        }
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();

//        Log.e(TAG,"wheelType="+ WHEELTYPE +  "  x = " + x + "  , y = " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopFlag = false;
                mLastX = x;
                mLastY = y;
                mStartAngle = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                *//**
                 * 获得开始的角度
                 *//*
                float start = getAngle(mLastX, mLastY);
                *//**
                 * 获得当前的角度
                 *//*
                float end = getAngle(x, y);

                // Log.e("TAG", "start = " + start + " , end =" + end);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                } else // 二、三象限，色角度值是付值
                {
                    mStartAngle += start - end;
                }

                //重新 绘制 界面
                refeshView(MotionEvent.ACTION_MOVE);

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                stopFlag = true;
                //重新 绘制 界面
                refeshView(MotionEvent.ACTION_UP);
                break;
        }
        return super.dispatchTouchEvent(event);
    }*/

}