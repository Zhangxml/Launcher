package com.voyah.cockpit.launcher.taskview;

import static android.view.WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.car.app.RemoteCarTaskView;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.window.WindowContainerTransaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.voyah.cockpit.launcher.R;
import com.voyah.cockpit.launcher.view.RoundWindow;

public class LauncherVCOS extends AppCompatActivity {

    private final String PKG = "com.android.car.settings";
    private final String CLS = "com.android.car.settings.Settings_Launcher_Homepage";

    private final String PKG1 = "com.android.calendar";
    private final String CLS1 = "com.android.calendar.AllInOneActivity";

    ViewGroup mMapsCard1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_vcos);
        RoundWindow.get(this).addWindow();

        getWindow().addPrivateFlags(PRIVATE_FLAG_TRUSTED_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);


        mMapsCard1 = findViewById(R.id.maps_card_1);
        if (mMapsCard1 != null) {
            setupRemoteCarTaskView2(mMapsCard1);
        }

//        new Handler().postDelayed(()->{
//            ViewGroup mMapsCard = findViewById(R.id.maps_card);
//            if (mMapsCard != null) {
//                setupRemoteCarTaskView(mMapsCard);
//            }
//        },1000);

        new Handler().postDelayed(()->{
            setTaskVisibility(false);
        },2000);
    }

    // **************************************************************************************


    private void setTaskVisibility(boolean visible){
        try {
            if (mMapsCard1 == null) return;
            RemoteCarTaskView mRemoteCarTaskView = (RemoteCarTaskView) mMapsCard1.getChildAt(0);
            ActivityManager.RunningTaskInfo taskInfo = (ActivityManager.RunningTaskInfo) mRemoteCarTaskView.getTag();
            final WindowContainerTransaction wct = new WindowContainerTransaction();
            wct.setHidden(taskInfo.token, !visible /* hidden */);
            if (visible) {
                wct.reorder(taskInfo.token, visible /* onTop */);
                wct.setBounds(taskInfo.token,new Rect(400, 0 ,1408, 792));
            }
            ActivityTaskManager.getService().getWindowOrganizerController().applyTransaction(wct);
        } catch (Exception e) {
            Log.e("wm_", "setTaskVisibility: ",e);
        }
    }


    private void setupRemoteCarTaskView(ViewGroup parent) {
        CarLauncherViewModel mCarLauncherViewModel = new ViewModelProvider(this,
                new CarLauncherViewModel.CarLauncherViewModelFactory(this, getMapsIntent(PKG,CLS)))
                .get(CarLauncherViewModel.class);

        getLifecycle().addObserver(mCarLauncherViewModel);
        addOnNewIntentListener(mCarLauncherViewModel.getNewIntentListener());

        setUpRemoteCarTaskViewObserver(parent,mCarLauncherViewModel);
    }

    private void setUpRemoteCarTaskViewObserver(ViewGroup parent,CarLauncherViewModel mCarLauncherViewModel) {
        mCarLauncherViewModel.getRemoteCarTaskView().observe(this, taskView -> {
            if (taskView == null || taskView.getParent() == parent) {
                // Discard if the parent is still the same because it doesn't signify a config
                // change.
                return;
            }
            if (taskView.getParent() != null) {
                // Discard the previous parent as its invalid now.
                ((ViewGroup) taskView.getParent()).removeView(taskView);
            }
            parent.removeAllViews(); // Just a defense against a dirty parent.
            parent.addView(taskView);
        });
    }

    private void setupRemoteCarTaskView2(ViewGroup parent) {
        CarLauncherViewModel2 mCarLauncherViewModel2 = new ViewModelProvider(this,
                new CarLauncherViewModel2.CarLauncherViewModelFactory(this, getMapsIntent(PKG1,CLS1)))
                .get(CarLauncherViewModel2.class);

        getLifecycle().addObserver(mCarLauncherViewModel2);
        addOnNewIntentListener(mCarLauncherViewModel2.getNewIntentListener());

        setUpRemoteCarTaskViewObserver2(parent,mCarLauncherViewModel2);
    }

    private void setUpRemoteCarTaskViewObserver2(ViewGroup parent,CarLauncherViewModel2 mCarLauncherViewModel) {
        mCarLauncherViewModel.getRemoteCarTaskView().observe(this, taskView -> {
            if (taskView == null || taskView.getParent() == parent) {
                // Discard if the parent is still the same because it doesn't signify a config
                // change.
                return;
            }
            if (taskView.getParent() != null) {
                // Discard the previous parent as its invalid now.
                ((ViewGroup) taskView.getParent()).removeView(taskView);
            }
            parent.removeAllViews(); // Just a defense against a dirty parent.
            parent.addView(taskView);
        });
    }

    private Intent getMapsIntent(String PKG,String CLS) {
        Intent defaultIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        defaultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        defaultIntent.setComponent(new ComponentName(PKG,CLS));
        defaultIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        return defaultIntent;
    }

    public void onClick1(View view) {
        LinearLayout.LayoutParams mLayoutParams = (LinearLayout.LayoutParams) mMapsCard1.getLayoutParams();
        mLayoutParams.leftMargin = 0;
        mMapsCard1.setLayoutParams(mLayoutParams);
        setTaskVisibility(true);

    }

    public void onClick2(View view) {
        darkBar();
    }

    protected void lightBar(){
        getWindow().setStatusBarColor(Color.RED);
        WindowInsetsController windowInsetsController = getWindow().getDecorView().getWindowInsetsController();
        windowInsetsController.setSystemBarsAppearance(0,WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
    }
    protected void darkBar(){
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        WindowInsetsController windowInsetsController = getWindow().getDecorView().getWindowInsetsController();
        windowInsetsController.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS);
    }
}