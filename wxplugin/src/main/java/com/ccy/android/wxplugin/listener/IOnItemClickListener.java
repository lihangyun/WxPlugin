package com.ccy.android.wxplugin.listener;

import android.support.v7.widget.RecyclerView;

public interface IOnItemClickListener<T> {

    public void onItemClick(RecyclerView.ViewHolder holder, T data);
}
