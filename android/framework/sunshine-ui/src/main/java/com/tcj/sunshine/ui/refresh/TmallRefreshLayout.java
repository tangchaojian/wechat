package com.tcj.sunshine.ui.refresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;

import com.tcj.sunshine.ui.refresh.header.TmallHeaderView;

/**
 * 仿天猫下拉刷新效果
 */
public class TmallRefreshLayout extends RefreshLayout {
    public TmallRefreshLayout(Context context) {
        super(context);
        this.initUI(context);
    }

    public TmallRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public TmallRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TmallRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context){
        //设置自定义下拉刷新头部
        TmallHeaderView mHeaderView = new TmallHeaderView(context);
        this.setHeaderView(mHeaderView);
    }
}
