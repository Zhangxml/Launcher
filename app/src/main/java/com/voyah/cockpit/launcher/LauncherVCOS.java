package com.voyah.cockpit.launcher;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LauncherVCOS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_vcos);
        RoundWindow.get(this).addWindow();
    }

    public void onClick1(View view) {
        lightBar();
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