package com.ccy.android.wxplugin.data;

public class DataHelper {

    private static String text;

    public static void setText(String text) {
        DataHelper.text = text;
    }

    public static String getText(){
        return text;
    }
}
