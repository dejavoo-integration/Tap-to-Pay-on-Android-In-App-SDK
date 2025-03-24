package com.denovo.app.invokeiposgosample.utility;

import android.content.Context;
import android.content.res.Configuration;
import androidx.annotation.NonNull;

import com.denovo.app.top.utility.MyApplication;


public class ThemeApplication extends MyApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
