package com.ccy.android.wxplugin.module.addmessage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ccy.android.wxplugin.R;
import com.ccy.android.wxplugin.base.BaseActivity;
import com.ccy.android.wxplugin.constant.Constant;
import com.ccy.android.wxplugin.service.FloatRemoteService;
import com.ccy.android.wxplugin.util.AppUtils;
import com.ccy.android.wxplugin.util.StringUtils;

import butterknife.BindView;

public class AddMessageActivity extends BaseActivity {


    /**
     * 辅助功能权限页面请求code
     */
    private static final int REQUEST_ACCESSIBILITY_SERVICE = 10;

    /**
     * 悬浮窗权限页面请求code
     */
    private static final int REQUEST_MANAGE_OVERLAY_PERMISSION = 20;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_message_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.add_message_part_friend)
    LinearLayout mPartFriendLinearLayout;
    @BindView(R.id.add_message_et)
    TextInputEditText mMessageEditText;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_message;
    }

    @Override
    protected Toolbar getToolBar() {
        return mToolbar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("群发消息");

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.add_message_all:
                        mPartFriendLinearLayout.setVisibility(View.GONE);
                        break;
                    case R.id.add_message_part:
                        mPartFriendLinearLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ACCESSIBILITY_SERVICE == requestCode) {
            boolean isOn = AppUtils.isAccessibilitySettingsOn(this);
            if (!isOn) {
                Toast.makeText(this, "服务未开启，请重试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 下一步
            case R.id.action_next:
                performNext();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_message, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFloatRemoteService();
    }

    /**
     * 下一步
     */
    private void performNext() {
        String message = mMessageEditText.getText().toString().trim();

        if (StringUtils.isEmpty(message)) {
            Toast.makeText(this, "请输入需要群发的消息~", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检测辅助服务权限
        if (!AppUtils.isAccessibilitySettingsOn(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("通知");
            builder.setMessage("使用自动化群发服务，需要先开启辅助服务功能哦~!\n\n" +
                    "1.进入后请打开“微信群发辅助服务开关”");
            builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 打开辅助服务界面
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, REQUEST_ACCESSIBILITY_SERVICE);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.setNeutralButton("忽略", null);
            builder.show();
            return;
        }

        // 检测悬浮窗权限
        // M版本需去申请权限,才能开启悬浮窗服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 权限未打开
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("通知");
                builder.setMessage("使用自动化群发服务，还需开启悬浮窗权限哦亲!");
                builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 打开悬浮窗权限界面
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        startActivityForResult(intent, REQUEST_MANAGE_OVERLAY_PERMISSION);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.setNeutralButton("忽略", null);
                builder.show();
                return;
            }
        }

        startFloatRemoteServices();

        // 打开微信
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(Constant.PACKAGE_NAME, Constant.LauncherUI);
        startActivity(intent);
    }

    /**
     * 开启悬浮窗服务
     */

    private void startFloatRemoteServices() {
        Intent intent = new Intent(this, FloatRemoteService.class);
        startService(intent);
    }

    /**
     * 停止悬浮窗服务
     */
    private void stopFloatRemoteService() {
        Intent intent = new Intent(this, FloatRemoteService.class);
        stopService(intent);
    }
}
