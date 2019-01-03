package com.ccy.android.wxplugin.module.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ccy.android.wxplugin.R;
import com.ccy.android.wxplugin.listener.IOnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.Holder> {

    private List<String> mData = new ArrayList<>();
    private Context mContext;
    private IOnItemClickListener<String> mItemClickListener;

    public MainAdapter() {
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_rv, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final String str = mData.get(position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(holder, str);
                }
            }
        });

        holder.title.setText(str);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.title.getLayoutParams();
        lp.height = getHeight();
        holder.title.setLayoutParams(lp);

        holder.layout.setBackgroundResource(R.drawable.shape_send_all);

    }

    public void setOnItemClickListener(IOnItemClickListener<String> mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private int getHeight() {
        Random random = new Random();
        // 获得转换后的px值
        int a = random.nextInt(70) + 70;
        Log.e("tag", "dp " + a);
        int pxDimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a, mContext.getResources().getDisplayMetrics());
        Log.e("tag", "px-- " + pxDimension);
        return pxDimension;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void init() {
        mData.add("群发消息");
        mData.add("群发群消息");
        mData.add("群发图片");
        mData.add("检测死粉");
        mData.add("群内家人");
        mData.add("批量加人");
        notifyItemRangeInserted(0, mData.size() - 1);
    }

    static class Holder extends RecyclerView.ViewHolder {

        private LinearLayout layout;
        private TextView title;

        public Holder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_main_layout);
            title = itemView.findViewById(R.id.item_main_tv);
        }
    }

}

