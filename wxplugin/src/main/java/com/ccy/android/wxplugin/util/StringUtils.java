package com.ccy.android.wxplugin.util;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String safeString(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        return str;
    }

}
