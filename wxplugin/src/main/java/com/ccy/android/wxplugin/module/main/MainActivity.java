package com.ccy.android.wxplugin.module.main;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ccy.android.wxplugin.R;
import com.ccy.android.wxplugin.base.BaseActivity;
import com.ccy.android.wxplugin.listener.IOnItemClickListener;
import com.ccy.android.wxplugin.module.about.AboutActivity;
import com.ccy.android.wxplugin.module.addmessage.AddMessageActivity;
import com.ccy.android.wxplugin.module.main.adapter.MainAdapter;
import com.ccy.android.wxplugin.module.main.contract.MainContract;
import com.ccy.android.wxplugin.module.main.presenter.MainPresenterImpl;
import com.ccy.android.wxplugin.util.DisplayUtils;
import com.tencent.bugly.beta.Beta;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements MainContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.main_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.main_draw_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.main_nv)
    NavigationView mNavigationView;
    int mMarge;
    private MainPresenterImpl mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected Toolbar getToolBar() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar();

        mMarge = DisplayUtils.dp2px(MainActivity.this, 8);

        mPresenter = new MainPresenterImpl();

        MainAdapter adapter = new MainAdapter();
        adapter.setOnItemClickListener(new IOnItemClickListener<String>() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, String data) {
                if (holder.getAdapterPosition() == 0) {
                    goActivity(AddMessageActivity.class);
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();

                if (layoutParams.getSpanIndex() == 0) {
                    outRect.left = mMarge;
                    outRect.right = mMarge;
                } else {
                    outRect.right = mMarge;
                }
                outRect.top = mMarge;
            }
        });
        mRecyclerView.setAdapter(adapter);

        adapter.init();

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
}
