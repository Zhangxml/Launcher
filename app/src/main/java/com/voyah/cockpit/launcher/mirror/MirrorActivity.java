package com.voyah.cockpit.launcher.mirror;

import android.annotation.Nullable;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.content.ComponentName;
import android.graphics.Rect;
import android.graphics.Region;
import android.gui.TrustedOverlay;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManagerGlobal;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.voyah.cockpit.launcher.App;
import com.voyah.cockpit.launcher.R;

public class MirrorActivity extends AppCompatActivity {
    public final String TAG = getClass().getSimpleName();
    private MirrorView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mirror);
        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(new Holder());

        VirtualDisplayUtil.getInstance()
                .createVirtual(this,400,696)
                .startActivity(this,"com.zpd.menu","com.zpd.menu.MenusActivity");

        App.saveId(VirtualDisplayUtil.getInstance().getVirtualDisplay().getDisplay().getDisplayId());
    }

    private SurfaceControl refectSurfaceControl(){
        return new SurfaceControl();
    }

    // 拿到对应display的SurfaceControl
    private SurfaceControl mirrorDisplay(final int displayId) {
        try {
            SurfaceControl outSurfaceControl = refectSurfaceControl();
            WindowManagerGlobal.getWindowManagerService().mirrorDisplay(displayId, outSurfaceControl);
            return outSurfaceControl;
        } catch (Exception e) {
            Log.e(TAG, "Unable to reach window manager", e);
        }
        return null;
    }

    private void startMirror(){
        mSurfaceView.setObscuredTouchRegion();
        SurfaceControl surfaceControl = mSurfaceView.getSurfaceControl(); // parent
        SurfaceControl mMirrorSurface = mirrorDisplay(App.mId);
        if (!mMirrorSurface.isValid()) {
            return;
        }
        SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();
        mTransaction.show(mMirrorSurface)
                .setTrustedOverlay(surfaceControl, TrustedOverlay.DISABLED)
                .reparent(mMirrorSurface, surfaceControl).apply();
    }

    private class Holder implements SurfaceHolder.Callback{

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            new Handler(Looper.myLooper()).postDelayed(()->{
                startMirror();
            },5000);
        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        }
    }



    private void re(){
        try {
            ActivityTaskManager.getService().registerTaskStackListener(new TaskStackListener() {
                @Override
                public void onTaskCreated(int taskId, ComponentName componentName) throws RemoteException {
                    super.onTaskCreated(taskId, componentName);
                }

                @Override
                public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
                    super.onTaskMovedToFront(taskInfo);

                }
            });
        } catch (Exception e) {
        }
    }
}