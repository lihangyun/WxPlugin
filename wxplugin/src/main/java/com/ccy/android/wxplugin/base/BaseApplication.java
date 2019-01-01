package com.ccy.android.wxplugin.base;

import android.app.Application;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        Bugly.init(getApplicationContext(), "23c4ac2aa3", true);
    }

}
