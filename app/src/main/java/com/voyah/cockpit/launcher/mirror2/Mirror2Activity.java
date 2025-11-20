package com.voyah.cockpit.launcher.mirror2;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.car.view.MirroredSurfaceView;
import android.content.ComponentName;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.voyah.cockpit.launcher.R;
import com.voyah.cockpit.launcher.util.VirtualDisplayUtil;

import java.lang.reflect.Method;

public class Mirror2Activity extends AppCompatActivity {

    MirroredSurfaceView mMirroredSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mirror2);
        mMirroredSurfaceView = findViewById(R.id.surface_view);

        registerTaskStackListener();

        VirtualDisplayUtil.getInstance()
                .createVirtual(this,400,696)
                .startActivity(this,"com.zpd.menu","com.zpd.menu.MenusActivity");

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
                        try {
                            Method token = mMirroredSurfaceView.getClass().getMethod("getTokenForTaskId", int.class);
                            IBinder mIBinder = (IBinder) token.invoke(mMirroredSurfaceView,taskInfo.taskId);
                            mMirroredSurfaceView.mirrorSurface(mIBinder);

                            mMirroredSurfaceView.setObscuredTouchRegion(new Region(0,0,400,696)); // 设置父亲可以响应的区域

                        } catch (Exception e) {
                            Log.e("TAG", "onTaskMovedToFront: ",e);
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "onTaskMovedToFront: ",e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        MotionEvent mMotionEvent = event.copy();
        mMotionEvent.setDisplayId(VirtualDisplayUtil.getInstance().getVirtualDisplayId());
        InputManager mInputManager = InputManager.getInstance();
        boolean touch = mInputManager.injectInputEvent(mMotionEvent,InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        return touch;
    }


}