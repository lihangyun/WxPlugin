package com.ccy.android.wxplugin.base;

import android.app.Application;

import com.ccy.android.wxplugin.constant.Constant;
import com.tencent.bugly.Bugly;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Bugly.init(getApplicationContext(), Constant.BUGLY_ID, true);
    }

}
