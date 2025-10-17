package com.voyah.cockpit.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PaintView extends View {

    public final String TAG = getClass().getSimpleName();

    Paint mPaint = null;
    private RectF rectF1,rectF2;
    private int measuredWidth;
    private int measuredHeight;
    private int mLeft  = 1408 - 400;
    private float mStartX;
    private int mRound = 20;  // 圆角
    private int mWidth = 0;  // 4周边框

    private int midWidth = 5;
    private Rect mDisplayBounds;
    private int mNaviHeight;


    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(1);
        mPaint.setColor(Color.BLACK);

        WindowManager mWindowManager = getContext().getSystemService(WindowManager.class);
        mDisplayBounds = mWindowManager.getCurrentWindowMetrics().getBounds();
        Log.e(TAG, "init:  displayBounds = " + mDisplayBounds.toString());

        Insets insets = mWindowManager.getCurrentWindowMetrics().getWindowInsets().getInsets(WindowInsets.Type.navigationBars());
        mNaviHeight = insets.bottom;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight() - mNaviHeight;
        Log.e(TAG, "onMeasure: measuredWidth = " + measuredWidth);
    }
    /*
    * mLeft 为0 或者 measuredWidth时，全屏圆角
    * */
    public void setOffset(int left){
        if (mLeft<0) return;
        if (mLeft>measuredWidth)return;
        mLeft = left;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        //
        canvas.save();
        rectF1 = new RectF();
        rectF1.set(mWidth,mWidth,measuredWidth-mWidth-mLeft,measuredHeight-mWidth);
        //  从A中减掉B
        Path mPath1 = new Path();
        mPath1.addRoundRect(rectF1,mRound,mRound, Path.Direction.CCW);
        canvas.clipPath(mPath1, Region.Op.DIFFERENCE);
        rectF1.set(0,0,measuredWidth-mLeft+midWidth,measuredHeight);
        canvas.clipRect(rectF1);// A
        canvas.drawRect(rectF1,mPaint);
        canvas.restore();
        //
        canvas.save();
        rectF2 = new RectF();
        rectF2.set(measuredWidth-mLeft+midWidth,mWidth,measuredWidth-mWidth,measuredHeight-mWidth);
        //  从A中减掉B
        Path mPath2 = new Path();
        mPath2.addRoundRect(rectF2,mRound,mRound, Path.Direction.CCW);
        canvas.clipPath(mPath2, Region.Op.DIFFERENCE);
        rectF2.set(measuredWidth-mLeft-mWidth,0,measuredWidth,measuredHeight);
        canvas.clipRect(rectF2); // A
        canvas.drawRect(rectF2,mPaint);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mStartX = event.getX();
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                float cX = event.getX();
                int left = (int) (mLeft - (cX - mStartX));
                setOffset(left);
                mStartX = cX;
                break;
            }
            case MotionEvent.ACTION_UP:{
                mStartX = 0;
                break;
            }
        }
        return true;
    }
}
