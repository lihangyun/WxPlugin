package com.ccy.android.wxplugin.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ccy.android.wxplugin.util.FloatViewManager;

public class FloatRemoteService extends Service {

    private FloatViewManager mFloatViewManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatViewManager = new FloatViewManager(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFloatViewManager.destroy();
    }

}
