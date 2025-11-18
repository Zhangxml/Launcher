package com.voyah.cockpit.launcher.mirror;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

public class MirrorView extends SurfaceView {

    private TouchableInsetsProvider mTouchableInsetsProvider;

    public MirrorView(Context context) {
        super(context);
    }

    public MirrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MirrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MirrorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTouchableInsetsProvider = new TouchableInsetsProvider(this);
    }

    public void setObscuredTouchRegion(){
        this.post(()->{
            int[] location = new int[2];
            this.getLocationOnScreen(location);
            Log.e("TAG", "onCreate:location  x = " + location[0]
                    + "  y = " + location[1] + "  Width = "+ getWidth() + "  Height = "+ getHeight());
            mTouchableInsetsProvider.setObscuredTouchRegion(new Region(location[0],location[1],getWidth(),getHeight()/2));
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mTouchableInsetsProvider.addToViewTreeObserver();
    }

    @Override
    public void onDetachedFromWindow() {
        mTouchableInsetsProvider.removeFromViewTreeObserver();
        super.onDetachedFromWindow();
    }
}
