package com.you.wheelviewlib;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;

public class WheelView extends ViewGroup {
    private final String TAG = "WheelView";

    Scroller mScroller = null;
    BodyPartView littleWheel = null;
    BodyPartView bigWheel = null;

    private int mRadius;
    private int radiuWidth;
    private int radiuHeight;


    // 中心的坐标
    private int centerX;
    private int centerY;

    private int screenWidth = 0;
    private int screenHeigth = 0;
    /**
     * 记录上一次的x，y坐标
     */
    private float mLastX;
    private float mLastY;
    /**
     * 布局时的开始角度
     */
    private double mStartAngle = 0;
    /**
     * 菜单项的文本
     */
    private String[] mItemTexts;
    /**
     * 菜单项的图标
     */
    private int[] mItemImgs;
    /**
     * 菜单的个数
     */
    private int mMenuItemCount;
    /**
     * 检测按下到抬起时旋转的角度
     */
    private float mTmpAngle;

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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                default:
                    break;
            }
        }
    };

    public WheelView(Context context) {
        super(context);

        initData(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initData(Context context) {
        mScroller = new Scroller(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setWillNotDraw(false);
        Log.i(TAG, "-------->initData");
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            radiuWidth = getWidth() / 2;
            radiuHeight = getHeight() / 2;
            centerX = getWidth() / 2;
            centerY = getHeight();

            mRadius = Math.min(radiuWidth, radiuHeight);
        }

        final int childCount = getChildCount();
        Log.i(TAG, "  radiuWidth=" + radiuWidth + "  radiuHeight=" + radiuHeight + "   mRadius=" + mRadius + "  childCount=" + childCount + "  right=" + right + "  bottom=" + bottom + " getHeight() - getWidth()=" + (getHeight() - getWidth()));

        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getId() == R.id.id_circle_menu_little_wheel) {
                if (littleWheel == null) {
                    littleWheel = (BodyPartView) child;
                    littleWheel.init(screenWidth, screenHeigth);
                    littleWheel.setBackground(getResources().getColor(R.color.wheelview_little_view));
                    littleWheel.setWheelType(BodyPartView.LITTLEWHEELVIEW, 0);

                    littleWheel.layout(0, getHeight() - (int)(Math.sqrt(2) * (getWidth()/2)), right, getHeight());
                }
            } else if (child.getId() == R.id.id_circle_menu_big_wheel) {
                if (bigWheel == null) {
                    bigWheel = (BodyPartView) child;
                    bigWheel.init(screenWidth, screenHeigth);
                    bigWheel.setBackground(getResources().getColor(R.color.wheelview_big_view));
                    bigWheel.setWheelType(BodyPartView.BIGWHEELVIEW, 0);

                    bigWheel.layout(0, getHeight() - (int)(Math.sqrt(2) * (getWidth()/2)) - getWidth()/2, right, getHeight() - (int) Math.sqrt(2) * getWidth() / 2);
                }
            } else if (child.getId() == R.id.id_click_view) {
                ImageView view = (ImageView)child;
                view.layout(0, getHeight() - getWidth() * 3 / 10, getWidth(), getHeight() + getWidth() * 7/ 10);
                view.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            Toast.makeText(getContext(), "" + motionEvent.getRawY(), Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        return true;
                    }
                });
            }
        }
    }


    private byte touchArea = 0x00;
    private final byte OUT_AREA = 0x01;
    private final byte SMALLWHEEL = 0x02;
    private final byte BIGWHEEL = 0X03;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        Log.e(TAG, "  x = " + x + "  , y = " + y);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getHeight() - (int)(Math.sqrt(2) * (getWidth()/2)) - getWidth()/2 < y && y <  getHeight() - (int)(Math.sqrt(2) * (getWidth()/2)))
                    touchArea = BIGWHEEL;
                else if(getHeight() - (int)(Math.sqrt(2) * (getWidth()/2)) < y && y < getHeight() - getWidth() * 3 / 10)
                    touchArea = SMALLWHEEL;
                else
                    touchArea = OUT_AREA;

                mLastX = x;
                mLastY = y;
                mStartAngle = 0;
                handleScollTouchDown();
                break;
            case MotionEvent.ACTION_MOVE:
                /**
                 * 获得开始的角度
                 */
                float start = getAngle(mLastX, mLastY);
                /**
                 * 获得当前的角度
                 */
                float end = getAngle(x, y);

                // Log.e("TAG", "start = " + start + " , end =" + end);
                // 如果是一、四象限，则直接end-start，角度值都是正值
                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    mStartAngle += end - start;
                } else // 二、三象限，色角度值是付值
                {
                    mStartAngle += start - end;
                }


                handleScollTouchMove();
                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                handleScollTouchUp();
                mLastX =0;
                mLastY =0;
                mStartAngle =0;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    private void handleScollTouchDown(){
        if (touchArea == BIGWHEEL){
            bigWheel.setStartAngle(mStartAngle);
            bigWheel.refeshView(MotionEvent.ACTION_DOWN);
        }else if (touchArea == SMALLWHEEL){
            littleWheel.setStartAngle(mStartAngle *2);
            littleWheel.refeshView(MotionEvent.ACTION_DOWN);
        }
    }

    private void handleScollTouchMove(){
        if (touchArea == BIGWHEEL){
            bigWheel.setStartAngle(mStartAngle);
            bigWheel.refeshView(MotionEvent.ACTION_MOVE);
        }else if (touchArea == SMALLWHEEL){
            littleWheel.setStartAngle(mStartAngle *2);
            littleWheel.refeshView(MotionEvent.ACTION_MOVE);
        }
    }

    private void handleScollTouchUp(){
        if (touchArea == BIGWHEEL){
            bigWheel.setStartAngle(mStartAngle);
            bigWheel.refeshView(MotionEvent.ACTION_UP);
        }else if (touchArea == SMALLWHEEL){
            littleWheel.setStartAngle(mStartAngle *2);
            littleWheel.refeshView(MotionEvent.ACTION_UP);
        }
    }

    private void drawBodypart(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bodyparts[0]);
        int bitmapWidth = bitmap.getWidth();

        int layoutRadius = mRadius / 3;
        mStartAngle %= 360;

        // 计算，中心点到menu item中心的距离
        float tmp = layoutRadius / 2f - bitmapWidth / 2;

        float deltaX = (int) Math.round(tmp
                * Math.cos(Math.toRadians(mStartAngle)));

        float deltaY = (int) Math.round(tmp
                * Math.sin(Math.toRadians(mStartAngle)));


        // tmp cosa 即menu item中心点的横坐标
        int left = getWidth() / 2 - bitmapWidth / 2 + (int) deltaX;

        // tmp sina 即menu item的纵坐标
        int top = getHeight() - getWidth() / 2 - bitmapWidth / 2 + (int) deltaY;

        //创建操作图片是用的matrix对象
        Matrix matrix = new Matrix();

        matrix.preTranslate(left, top);
        //缩放图片动作
        //matrix.postScale(1, 1);
        //平移 图片 动作
        //matrix.postTranslate(deltaX , deltaY);
        //旋转图片动作
        matrix.postRotate((float) mStartAngle, getWidth() / 2, getHeight());//以坐标50，100 旋转30°

        //创建新图片
        //Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapWidth, matrix, true);

        canvas.drawBitmap(bitmap, matrix, null);
    }

    // kkh
    public Bitmap loadBitmapFromView(View v, int size) {
        Bitmap b = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        //v.layout(0, getHeight() - getWidth(), getWidth(), getHeight());
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }


    /**
     * 主要为了action_down时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
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

    public void init(int screenWidth, int screenHeigth) {
        this.screenWidth = screenWidth;
        this.screenHeigth = screenHeigth;
    }

}
