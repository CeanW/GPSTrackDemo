package com.ceanwu.gpstrackdemo;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by Shengyun Wu on 2/6/2017.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
