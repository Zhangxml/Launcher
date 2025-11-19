package com.voyah.cockpit.launcher.mirror;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.gui.TrustedOverlay;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.View;
import android.view.WindowManagerGlobal;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.voyah.cockpit.launcher.App;
import com.voyah.cockpit.launcher.R;
import com.voyah.cockpit.launcher.VirtualDisplayUtil;

public class MirrorActivity extends AppCompatActivity {
    public final String TAG = "MirrorActivity123";
    private boolean isAttached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mirror);
        registerTaskStackListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume:");
        if(isAttached){
            getWindow().getDecorView().post(()->{
                startMirror();
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause:");
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isAttached){
            isAttached = true;
            Log.e(TAG, "onAttachedToWindow:");
            int i = VirtualDisplayUtil.getInstance()
                    .createVirtual(App.mContext, 400, 696)
                    .startActivity(this, "com.zpd.menu", "com.zpd.menu.MenusActivity");
            if (i == 0){
                startMirror();
            }
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
        Log.e(TAG, "onDetachedFromWindow:");
    }

    // 拿到对应display的SurfaceControl
    private SurfaceControl mirrorDisplay(final int displayId) {
        try {
            SurfaceControl outSurfaceControl = new SurfaceControl();
            WindowManagerGlobal.getWindowManagerService().mirrorDisplay(displayId, outSurfaceControl);
            return outSurfaceControl;
        } catch (Exception e) {
            Log.e(TAG, "Unable to reach window manager", e);
        }
        return null;
    }

    private void startMirror(){
        Log.e(TAG, "startMirror:");
        SurfaceControl parent = getWindow().getDecorView().getViewRootImpl().getSurfaceControl();
        SurfaceControl mMirrorSurface = mirrorDisplay(VirtualDisplayUtil.getInstance().getVirtualDisplayId());
        if (!mMirrorSurface.isValid()) {
            Log.e(TAG, "startMirror:  isValid  = false");
            return;
        }
        SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();
        mTransaction.show(mMirrorSurface)
                .setTrustedOverlay(parent, TrustedOverlay.DISABLED)
                .setWindowCrop(mMirrorSurface, 400, 696)
                .setPosition(mMirrorSurface, 0, 0)
                .reparent(mMirrorSurface, parent).apply();
    }

    private void registerTaskStackListener(){
        try {
            ActivityTaskManager.getService().registerTaskStackListener(new TaskStackListener() {
                @Override
                public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {
                    super.onTaskCreated(taskId, componentName);
                }

                @Override
                public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                    super.onTaskMovedToFront(taskInfo);
                    if (taskInfo.displayId == VirtualDisplayUtil.getInstance().getVirtualDisplayId()){
                        startMirror();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "onTaskMovedToFront: ",e);
        }
    }
}