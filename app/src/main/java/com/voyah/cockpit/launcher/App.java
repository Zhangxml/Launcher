package com.voyah.cockpit.launcher;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    public static Context mContext;
    public static int mId;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static void saveId(int id){
        mId = id;
    }
}
