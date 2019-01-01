package com.ccy.android.wxplugin.event;

public class ChangeEvent {
    private String text;

    public ChangeEvent(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
