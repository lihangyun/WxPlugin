package com.ccy.android.wxplugin.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.ccy.android.wxplugin.constant.Constant;
import com.ccy.android.wxplugin.data.DataHelper;
import com.ccy.android.wxplugin.event.ChangeEvent;
import com.ccy.android.wxplugin.event.MessageEvent;
import com.ccy.android.wxplugin.util.AccessOperation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;

public class WxAccessibilityService extends AccessibilityService {

    /**
     * tag
     */
    private static final String TAG = "WxAccessibilityService";

    /**
     * 开始执行群发操作标记，默认false
     */
    private boolean isStart = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessOperation.getInstance().update(getRootInActiveWindow());

        String className = event.getClassName().toString();
        Log.e(TAG, "className : " + className);
        Log.e(TAG, "event : " + event);

        if (isStart) {
            int eventType = event.getEventType();
            switch (eventType) {
                // 窗口状态有变化时，回调此方法
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    next(className);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 第一步操作
     */
    private void first() {
        AccessOperation.getInstance().clickText("我");
        sleep();

        AccessOperation.getInstance().clickText("设置");
    }

    private void next(String className) {
        switch (className) {
            case Constant.LauncherUI:
                first();
                break;

            case Constant.SETTINGS_UI:
                sleep();
                AccessOperation.getInstance().clickText("通用");
                break;

            case Constant.SETTINGS_ABOUT_SYSTEM_UI:
                sleep();
                AccessOperation.getInstance().clickText("辅助功能");
                break;

            case Constant.SETTINGS_PLUGINS_UI:
                sleep();
                AccessOperation.getInstance().clickText("群发助手");
                break;

            case Constant.CONTACT_INFO_UI:
                sleep();
                AccessOperation.getInstance().clickText("开始群发");
                break;

            case Constant.MASS_SEND_HISTORY_UI:
                sleep();
                AccessOperation.getInstance().clickText("新建群发");
                break;

            case Constant.MASS_SEND_SELECT_CONTACT_UI:
                sleep();
                AccessOperation.getInstance().clickText("全选");

                sleep();
                int count = AccessOperation.getInstance().selectAllContact();
                AccessOperation.getInstance().clickText(String.format(Locale.getDefault(), "下一步(%d)", count));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sleep();
                    AccessOperation.getInstance().setText(Constant.EDIT_TEXT_VIEW_ID, DataHelper.getText());
                } else {
                    // 请求焦点
                    sleep();
                    AccessOperation.getInstance().requestFocus(Constant.EDIT_TEXT_VIEW_ID);

                    // 使用剪切板
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        ClipData clip = ClipData.newPlainText("text", DataHelper.getText());
                        clipboard.setPrimaryClip(clip);
                    }

                    // 粘贴文案
                    sleep();
                    AccessOperation.getInstance().pasteText(Constant.EDIT_TEXT_VIEW_ID);
                }

                sleep();
                AccessOperation.getInstance().clickText("发送");

                break;
            default:
                break;
        }
    }

    /**
     * 开始执行群发操作的回调
     *
     * @param messageEvent event
     */
    @Subscribe
    public void sheClickText(MessageEvent messageEvent) {
        isStart = !isStart;

        if (isStart) {
            boolean clickable = AccessOperation.getInstance().clickText("微信");
            // 返回false，表示未找到该文案，代表未在微信首页
            if (!clickable) {
                Toast.makeText(this, "请回到微信首页", Toast.LENGTH_SHORT).show();
                return;
            }
            EventBus.getDefault().post(new ChangeEvent("停止群发"));
            first();
        } else {
            EventBus.getDefault().post(new ChangeEvent("开始群发"));
        }
    }

    /**
     * 每一个动作完成后休眠200ms，防止低配置手机页面反应不及时
     */
    private void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onInterrupt() {

    }
}
