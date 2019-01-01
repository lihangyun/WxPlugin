package com.ccy.android.wxplugin.util;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ccy.android.wxplugin.R;
import com.ccy.android.wxplugin.event.ChangeEvent;
import com.ccy.android.wxplugin.event.MessageEvent;
import com.ccy.android.wxplugin.listener.IUpdatePostListener;
import com.ccy.android.wxplugin.view.RemoteView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FloatViewManager {

    @BindView(R.id.remote_rv)
    RemoteView mRemoteView;
    @BindView(R.id.remote_perform)
    TextView mPerformTextView;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private View mView;

    public FloatViewManager(Context context) {
        EventBus.getDefault().register(this);
        initWindowManager(context);

        mView = LayoutInflater.from(context).inflate(R.layout.remote_view, null);
        ButterKnife.bind(this, mView);
        addView();

        mRemoteView.setUpdatePostListener(new IUpdatePostListener() {
            @Override
            public void updatePos(int x, int y) {
                updateViewLayout(x, y);
            }
        });
    }

    @OnClick({R.id.remote_perform})
    public void onClick(View v) {
        switch (v.getId()) {
            // 开始动作or停止动作
            case R.id.remote_perform:
                EventBus.getDefault().post(new MessageEvent());
                break;
        }
    }

    private void initWindowManager(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE   // 使返回键有效
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;// 使悬浮窗可接受事件
        // 背景显示格式，透明格式，默认不透明
        mLayoutParams.format = PixelFormat.TRANSPARENT;
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }


    private void addView() {
        mWindowManager.addView(mView, mLayoutParams);
    }

    private void updateViewLayout(int x, int y) {
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        mWindowManager.updateViewLayout(mView, mLayoutParams);
    }

    private void removeView() {
        mWindowManager.removeView(mView);
    }

    /**
     * 回收资源
     */
    public void destroy() {
        EventBus.getDefault().unregister(this);
        removeView();
    }

    /**
     * 悬浮窗文案更新
     *
     * @param changeEvent event
     */
    @Subscribe
    public void onChangeText(ChangeEvent changeEvent) {
        mPerformTextView.setText(changeEvent.getText());
    }
}
