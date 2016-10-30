package com.you.wheelview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import com.you.wheelviewlib.WheelView;


/**
 * Created by you on 2016/7/12.
 */
public class WheelViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.wheel_view);


        // 方法1 Android获得屏幕的宽和高
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        WheelView wheelView = (WheelView) findViewById(R.id.wheelview);
        wheelView.init(screenWidth, screenHeight);
        Log.i("WheelViewActivity", "screenWidth=" + screenWidth + "  screenHeight="+ screenHeight);
    }
}
