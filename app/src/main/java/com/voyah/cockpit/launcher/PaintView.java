package com.voyah.cockpit.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PaintView extends View {

    public final String TAG = getClass().getSimpleName();

    Paint mPaint = null;
    private RectF rectF1,rectF2;
    private int measuredWidth;
    private int measuredHeight;
    private int mLeft  = 1000;
    private float mStartX;
    private int mRound = 20;
    private int mWidth = 10;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
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
        Log.e(TAG, "setOffset: mLeft = " +mLeft);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        //
        canvas.save();
        rectF1 = new RectF();
        rectF1.set(mWidth,mWidth,measuredWidth-mWidth-mLeft,measuredHeight-mWidth);
        Path mPath1 = new Path();
        mPath1.addRoundRect(rectF1,mRound,mRound, Path.Direction.CCW);
        //  从A中减掉B
        canvas.clipPath(mPath1, Region.Op.DIFFERENCE);
        rectF1.set(0,0,measuredWidth-mLeft,measuredHeight);
        canvas.clipRect(rectF1);// A
        canvas.drawRect(rectF1,mPaint);
        canvas.restore();
        //
        canvas.save();
        rectF2 = new RectF();
        rectF2.set(measuredWidth-mLeft,mWidth,measuredWidth-mWidth,measuredHeight-mWidth);
        Path mPath2 = new Path();
        mPath2.addRoundRect(rectF2,mRound,mRound, Path.Direction.CCW);
//        //  从A中减掉B
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
                mLeft = (int) (mLeft - (cX - mStartX));
                setOffset(mLeft);
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
