package com.tcj.sunshine.ui.refresh.footer;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.refresh.RefreshLayout;
import com.tcj.sunshine.ui.refresh.RefreshRecyclerView;

/**
 *
 */
public abstract class BaseFooterView extends LinearLayout {

    protected Context context;

    public BaseFooterView(Context context) {
        super(context);
        this.init(context);
    }

    public BaseFooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public BaseFooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BaseFooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    protected void init(Context context){
        int width = ScreenUtils.getScreenWidth();
        int height = this.getViewHeight();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        this.setLayoutParams(params);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(this.getLayoutResId(), this, true);
        this.initUI(view);
    }


    /**
     * 返回布局文件layoutId
     * @return
     */
    public abstract @LayoutRes int getLayoutResId();

    /**
     * 初始化控件
     * @param view
     */
    public abstract void initUI(View view);

    /**
     * 返回view的高度
     * @return
     */
    public abstract int getViewHeight();


    /**
     * 状态改变回调方法
     * @param mRecyclerView
     * @param oldStatus 上一状态
     * @param newStatus 当前状态
     */
    public abstract void onStateChanged(RefreshRecyclerView mRecyclerView, RefreshRecyclerView.Status oldStatus, RefreshRecyclerView.Status newStatus);
}
