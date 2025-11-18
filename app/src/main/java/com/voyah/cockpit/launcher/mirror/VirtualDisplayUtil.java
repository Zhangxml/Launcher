package com.voyah.cockpit.launcher.mirror;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;

public class VirtualDisplayUtil {


    public static final int VIRTUAL_DISPLAY_FLAG_OWN_FOCUS = 1 << 14;
    public static final int VIRTUAL_DISPLAY_FLAG_SUPPORTS_TOUCH = 1 << 6;
    public static final int VIRTUAL_DISPLAY_FLAG_TRUSTED = 1 << 10;
    public static final int VIRTUAL_DISPLAY_FLAG_OWN_DISPLAY_GROUP = 1 << 11;

    private VirtualDisplay mVirtualDisplay;

    private VirtualDisplayUtil() {}

    private static class Holder {
        static final VirtualDisplayUtil INSTANCE = new VirtualDisplayUtil();
    }

    public static VirtualDisplayUtil getInstance() {
        return Holder.INSTANCE;
    }

    @SuppressLint("WrongConstant")
    private Surface getSurface(int width, int height){
        android.media.ImageReader imageReader = ImageReader.newInstance(
                width, height, PixelFormat.RGBA_8888, 2);
        Surface surface = imageReader.getSurface();
        return surface;
    }

    public VirtualDisplayUtil createVirtual(Context mContext, int width, int height){
        DisplayManager dm = (DisplayManager) mContext.getSystemService(Context.DISPLAY_SERVICE);
        Surface surface = getSurface(width,height);
        mVirtualDisplay = dm.createVirtualDisplay(
                "SystemVirtualDisplay",
                width, height, getDensityDpiByDisplayId(dm,0),
                surface,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
                        |VIRTUAL_DISPLAY_FLAG_OWN_FOCUS
                        |VIRTUAL_DISPLAY_FLAG_SUPPORTS_TOUCH
                        |VIRTUAL_DISPLAY_FLAG_TRUSTED
                        |VIRTUAL_DISPLAY_FLAG_OWN_DISPLAY_GROUP
        );
        return this;
    }

    public VirtualDisplay getVirtualDisplay() {
        return mVirtualDisplay;
    }

    private int getDensityDpiByDisplayId(DisplayManager displayManager, int displayId) {
        Display display = displayManager.getDisplay(displayId);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return displayMetrics.densityDpi;
    }

    public void startActivity(Context mContext,String pkg,String clazz){
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchDisplayId(mVirtualDisplay.getDisplay().getDisplayId());
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkg,clazz));
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mContext.startActivity(intent,options.toBundle());
    }

}
