package com.ccy.android.wxplugin.util;

import android.content.Context;

public class DisplayUtils {

    public static int dp2px(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
