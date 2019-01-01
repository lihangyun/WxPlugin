package com.ccy.android.wxplugin.module.main;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ccy.android.wxplugin.R;
import com.ccy.android.wxplugin.base.BaseActivity;
import com.ccy.android.wxplugin.constant.Constant;
import com.ccy.android.wxplugin.data.DataHelper;
import com.ccy.android.wxplugin.module.about.AboutActivity;
import com.ccy.android.wxplugin.module.main.contract.MainContract;
import com.ccy.android.wxplugin.module.main.presenter.MainPresenterImpl;
import com.ccy.android.wxplugin.service.FloatRemoteService;
import com.ccy.android.wxplugin.service.WxAccessibilityService;
import com.tencent.bugly.beta.Beta;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainContract.View {

    /**
     * 悬浮窗权限页面请求code
     */
    private static final int REQUEST_MANAGE_OVERLAY_PERMISSION = 10;

    @BindView(R.id.go_access_ui)
    Switch mAccessibilityStatusCheckBox;
    //
    @BindView(R.id.go_over_window)
    Switch mOverStatusCheckBox;
    @BindView(R.id.message)
    EditText messageEditText;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.main_draw_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_nv)
    NavigationView mNavigationView;

    private MainPresenterImpl mPresenter;

    @OnClick({R.id.go_access_ui, R.id.go_wx_ui, R.id.go_over_window})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_access_ui:
                goAccessUi();
                break;
            case R.id.go_wx_ui:
                String text = messageEditText.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(this, "请输入需要群发的内容", Toast.LENGTH_SHORT).show();
                    return;
                }
                DataHelper.setText(text);


                if (!mOverStatusCheckBox.isChecked() || !mAccessibilityStatusCheckBox.isChecked()) {
                    Toast.makeText(this, "请先开启必须的权限，才能开始群发~", Toast.LENGTH_SHORT).show();
                    return;
                }

                goWxUi();
                break;

            case R.id.go_over_window:
                goOver();
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar();

        mPresenter = new MainPresenterImpl();

        showFloatWindow();


    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer_content_desc, R.string.close_drawer_content_desc);
        toggle.syncState();
        mDrawerLayout.addDrawerListener(toggle);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_about:
                        goActivity(AboutActivity.class);
                        break;
                    case R.id.action_update:
                        Beta.checkUpgrade(true, true);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }


    private void goOver() {
        if (!mOverStatusCheckBox.isChecked()) {
            stopFloatRemoteService();
            return;
        }

        // M版本需去申请权限,才能开启悬浮窗服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 权限已打开
            if (Settings.canDrawOverlays(this)) {
                startFloatRemoteServices();
            } else {
                startManageOverlayPermissionActivity();
            }
        } else {
            startFloatRemoteServices();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void startManageOverlayPermissionActivity() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivityForResult(intent, REQUEST_MANAGE_OVERLAY_PERMISSION);
    }

    /**
     * 显示悬浮窗
     */
    private void showFloatWindow() {
        // M版本需去申请权限,才能开启悬浮窗服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 权限已打开
            if (Settings.canDrawOverlays(this)) {
                startFloatRemoteServices();
            }
        } else {
            startFloatRemoteServices();
        }
    }

    /**
     * 开启悬浮窗服务
     */

    private void startFloatRemoteServices() {
        Intent intent = new Intent(this, FloatRemoteService.class);
        startService(intent);
        mOverStatusCheckBox.setChecked(true);
    }

    /**
     * 停止悬浮窗服务
     */
    private void stopFloatRemoteService() {
        Intent intent = new Intent(this, FloatRemoteService.class);
        stopService(intent);
        mOverStatusCheckBox.setChecked(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  权限申请请求
        if (requestCode == REQUEST_MANAGE_OVERLAY_PERMISSION) {
            showFloatWindow();
        } else if (requestCode == 20) {
            mAccessibilityStatusCheckBox.setChecked(isAccessibilitySettingsOn(this));
        }
    }


    /**
     * 进入辅助服务页面
     */
    private void goAccessUi() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 20);
    }

    private void goWxUi() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(Constant.PACKAGE_NAME, Constant.LauncherUI);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "微信未安装", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + WxAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        } else {
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mOverStatusCheckBox.isChecked()) {
            Intent intent = new Intent(this, FloatRemoteService.class);
            stopService(intent);
            return;
        }
    }
}
