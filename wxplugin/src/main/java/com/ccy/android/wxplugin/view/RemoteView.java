package com.ccy.android.wxplugin.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.ccy.android.wxplugin.listener.IUpdatePostListener;

public class RemoteView extends LinearLayout {

    private int x;
    private int y;
    private int mTouchStartX;
    private int mTouchStartY;

    private IUpdatePostListener mPostListener;

    public RemoteView(Context context) {
        super(context);
    }

    public RemoteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RemoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = (int) event.getRawX();
        y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = (int) event.getX();
                mTouchStartY = (int) event.getY();
                break;


            case MotionEvent.ACTION_MOVE:
                if (mPostListener != null) {
                    mPostListener.updatePos(x - mTouchStartX, y - mTouchStartY);
                }
                break;

            default:
                return super.onTouchEvent(event);
        }
        return true;
    }

    public void setUpdatePostListener(IUpdatePostListener mPostListener) {
        this.mPostListener = mPostListener;
    }
}
