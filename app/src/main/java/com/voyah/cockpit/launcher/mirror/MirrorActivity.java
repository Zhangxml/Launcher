package com.voyah.cockpit.launcher.mirror;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.gui.TrustedOverlay;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.WindowManagerGlobal;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.voyah.cockpit.launcher.App;
import com.voyah.cockpit.launcher.R;
import com.voyah.cockpit.launcher.log.MLog;
import com.voyah.cockpit.launcher.util.VirtualDisplayUtil;
import com.voyah.cockpit.launcher.view.RoundWindow;

public class MirrorActivity extends AppCompatActivity {
    public final String TAG = "MirrorActivity123";
    private boolean isAttached = false;

    private ActivityManager.RunningTaskInfo mTaskInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mirror);
        RoundWindow.get(this).addWindow();
        registerTaskStackListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.e(TAG, "onResume:");
        if(isAttached){
            if (mTaskInfo != null){
                if (mTaskInfo.displayId == VirtualDisplayUtil.getInstance().getVirtualDisplayId()){
                    getWindow().getDecorView().post(()->{
                        startMirror();
                    });
                }else {
                    VirtualDisplayUtil.getInstance()
                            .createVirtual(App.mContext, 400, 696)
                            .startActivity(this, "com.zpd.menu", "com.zpd.menu.MenusActivity");
                }
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLog.e(TAG, "onPause:");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isAttached){
            isAttached = true;
            MLog.e(TAG, "onAttachedToWindow:");
            VirtualDisplayUtil.getInstance()
                    .createVirtual(App.mContext, 400, 696)
                    .startActivity(this, "com.zpd.menu", "com.zpd.menu.MenusActivity");
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
        MLog.e(TAG, "onDetachedFromWindow:");
    }

    private SurfaceControl mirrorDisplay(final int displayId) {
        try {
            SurfaceControl outSurfaceControl = new SurfaceControl();
            WindowManagerGlobal.getWindowManagerService().mirrorDisplay(displayId, outSurfaceControl); // mirror对应display的SurfaceControl
            return outSurfaceControl;
        } catch (Exception e) {
            Log.e(TAG, "Unable to reach window manager", e);
        }
        return null;
    }

    private void startMirror(){
        MLog.e(TAG, "startMirror:");
        SurfaceControl parent = getWindow().getDecorView().getViewRootImpl().getSurfaceControl();
        SurfaceControl mMirrorSurface = mirrorDisplay(VirtualDisplayUtil.getInstance().getVirtualDisplayId());
        if (!mMirrorSurface.isValid()) {
            Log.e(TAG, "startMirror:  isValid  = false");
            return;
        }
        SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();
        mTransaction.show(mMirrorSurface)
                .setTrustedOverlay(parent, TrustedOverlay.DISABLED)
                .setWindowCrop(mMirrorSurface, 400, 696) // 这个很重要
                .setCornerRadius(mMirrorSurface,10)
                .setPosition(mMirrorSurface, 0, 0)
                .reparent(mMirrorSurface, parent).apply();
    }

    private void registerTaskStackListener(){
        try {
            ActivityTaskManager.getService().registerTaskStackListener(new TaskStackListener() {
                @Override
                public void onTaskDisplayChanged(int taskId, int newDisplayId) throws RemoteException {
                    super.onTaskDisplayChanged(taskId, newDisplayId);
                    if (mTaskInfo != null && mTaskInfo.taskId == taskId && newDisplayId == VirtualDisplayUtil.getInstance().getVirtualDisplayId()){
                        MLog.e(TAG, "onTaskDisplayChanged: newDisplayId = " + newDisplayId);
                        getWindow().getDecorView().postDelayed(()->{
                            startMirror();
                        },2000);
                    }
                }

                @Override
                public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                    super.onTaskMovedToFront(taskInfo);
                    if (taskInfo.displayId == VirtualDisplayUtil.getInstance().getVirtualDisplayId()){
                        mTaskInfo = taskInfo; // 初始化
                        startMirror();
                    }

                    if (mTaskInfo != null && mTaskInfo.taskId == taskInfo.taskId){
                        mTaskInfo = taskInfo;
                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "onTaskMovedToFront: ",e);
        }
    }
}