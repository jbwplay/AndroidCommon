package com.androidlib;

import android.app.Application;

import com.androidbase.utils.AndroidUtils;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidUtils.init(this);
    }

}
