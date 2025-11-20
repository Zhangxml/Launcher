package com.voyah.cockpit.launcher.log;

import android.os.SystemClock;
import android.util.Log;

public final class MLog {

    private static final String TAG = "androidW";
    private static boolean sPrintLog = true;
    /*
     * 统计方法调用时间
     * */
    private static final int mMaxNestedCalls = 10;
    private static String[] mStartNames = new String[mMaxNestedCalls];
    private static long[] mStartTimes = new long[mMaxNestedCalls];
    private static int mCurrentLevel = -1;

    private MLog() {
    }

    public static void w(String msg) {
        printLogW("",msg);
    }

    public static void w(String TAG,String msg) {
        printLogW(TAG,msg);
    }

    public static void e(String msg) {
        printLogE("",msg);
    }

    public static void e(String TAG,String msg) {
        printLogE(TAG,msg);
    }

    static void printLogW(String TAG_,String msg) {
        String tag = android.text.TextUtils.isEmpty(TAG_)?TAG:String.join("_",TAG_,TAG);
        if (sPrintLog) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            if (elements == null || elements.length < 5) {
                Log.w(tag, msg);
            } else {
                Log.w(tag, elements[4].getFileName() // 文件名
                        + "("
                        + elements[4].getLineNumber()// 行号
                        + ")::"
                        + elements[4].getMethodName()// 方法名
                        + "()  "
                        + msg);
            }
        }
    }

    static void printLogE(String TAG_,String msg) {
        String tag = android.text.TextUtils.isEmpty(TAG_)?TAG:String.join("_",TAG_,TAG);
        if (sPrintLog) {
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
            if (elements == null || elements.length < 5) {
                Log.e(tag, msg);
            } else {
                Log.e(tag, elements[4].getFileName() // 文件名
                        + "("
                        + elements[4].getLineNumber()// 行号
                        + ")::"
                        + elements[4].getMethodName()// 方法名
                        + "()  "
                        + msg);
            }
        }
    }

    public static void traceBegin(String name) {
        if (mCurrentLevel + 1 >= mMaxNestedCalls) {
            return;
        }
        mCurrentLevel++;
        mStartNames[mCurrentLevel] = name;
        mStartTimes[mCurrentLevel] = SystemClock.elapsedRealtime();
    }

    public static void traceEnd() {
        if (mCurrentLevel < 0) {
            return;
        }
        final String name = mStartNames[mCurrentLevel];
        final long duration = SystemClock.elapsedRealtime() - mStartTimes[mCurrentLevel];
        mCurrentLevel--;
        if (sPrintLog) {
            Log.i(TAG, " - " + name + " took to complete: " + duration + "ms");
        }
    }
}