package com.tcj.sunshine.ui.refresh;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tcj.sunshine.ui.refresh.footer.TmallFooterView;

/**
 * 仿天猫滚动到底部加载更多效果
 */
public class TmallRefreshRecyclerView extends RefreshRecyclerView{
    public TmallRefreshRecyclerView(@NonNull Context context) {
        super(context);
        this.initUI(context);
    }

    public TmallRefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public TmallRefreshRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    private void initUI(Context context){
        TmallFooterView mFooterView = new TmallFooterView(context);
        this.setFooterView(mFooterView);
    }
}
