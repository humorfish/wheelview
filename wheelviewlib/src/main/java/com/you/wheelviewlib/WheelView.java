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
import android.widget.ImageButton;
import android.widget.Scroller;

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

                    littleWheel.layout(0, getHeight() - (int) Math.sqrt(2) * getWidth() / 2 - getWidth() / 2, right, getHeight());
                }
            } else if (child.getId() == R.id.id_circle_menu_big_wheel) {
                if (bigWheel == null) {
                    bigWheel = (BodyPartView) child;
                    bigWheel.init(screenWidth, screenHeigth);
                    bigWheel.setBackground(getResources().getColor(R.color.wheelview_big_view));
                    bigWheel.setWheelType(BodyPartView.BIGWHEELVIEW, 0);

                    bigWheel.layout(0, getHeight() - (int) Math.sqrt(2) * getWidth() - getWidth() / 2, right, getHeight() - (int) Math.sqrt(2) * getWidth() / 2);
                }
            } else if (child.getId() == R.id.id_click_view) {
                ImageButton view = (ImageButton)child;
                view.layout(0, getHeight() - getWidth() * 2 / 5 , getWidth(), getHeight() + getWidth() * 3/ 5);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick");
                    }
                });
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getY() <= getHeight() - Math.sqrt(2) * getWidth() / 2) {
            bigWheel.dispatchTouchEvent(event);
            littleWheel.setFocusableInTouchMode(false);
        }else{
            littleWheel.dispatchTouchEvent(event);
            bigWheel.setFocusableInTouchMode(false);
        }
        return true;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        //setVisibility(INVISIBLE);
        //this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //canvas.concat(matrix);
        //this.setLayerType(View.LAYER_TYPE_NONE, null);
        //setVisibility(VISIBLE);
        //drawCircle(canvas);
        //drawLittleView(canvas);
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
