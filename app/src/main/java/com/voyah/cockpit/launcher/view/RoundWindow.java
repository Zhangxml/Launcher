package com.voyah.cockpit.launcher.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

public class RoundWindow{

    private static RoundWindow mRoundWindow;
    private static Context mContext;

    public static RoundWindow get(Context context){
        mRoundWindow = new RoundWindow();
        mContext = context;
        return mRoundWindow;
    }

    public RoundWindow addWindow() {
        final WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.type = 2024;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE   // 事件是否支持透传
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        mWindowParams.setTitle("RoundWindow");
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.gravity = Gravity.CENTER;
        mWindowParams.setFitInsetsTypes(0);
        mWindowParams.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
        mWindowManager.addView(new PaintView(mContext), mWindowParams);
        return this;
    }
}
