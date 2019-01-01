package com.ccy.android.wxplugin.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {

    public static void addRun(Runnable runnable) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(runnable);
    }
}
