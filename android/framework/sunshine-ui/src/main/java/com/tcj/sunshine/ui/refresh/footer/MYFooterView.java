package com.tcj.sunshine.ui.refresh.footer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.DrawableUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;
import com.tcj.sunshine.ui.refresh.RefreshRecyclerView;

/**
 * 仿天猫
 */
public class MYFooterView extends BaseFooterView {

    private View mClickView;
    private TextView mTvTitle;
    private ImageView mIvAnim;
    private AnimationDrawable mLoadingAnim;

    protected boolean mNoMoreData = false;

    private static final String REFRESH_FOOTER_LOADING = "";
    private static final String REFRESH_FOOTER_FAILED = "加载失败，点击重新加载";
    private static final String REFRESH_FOOTER_COMPLETE = "";

    public MYFooterView(Context context) {
        super(context);
    }

    public MYFooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MYFooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MYFooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_miaoyin_loading_footer;
    }

    @Override
    public void initUI(View view) {
        this.mClickView = view.findViewById(R.id.mClickView);
        this.mTvTitle = view.findViewById(R.id.mTvTitle);
        this.mIvAnim = view.findViewById(R.id.mIvAnim);
    }

    @Override
    public int getViewHeight() {
        return ScreenUtils.dip2px(80);
    }

    @Override
    public void onStateChanged(RefreshRecyclerView mRecyclerView, RefreshRecyclerView.Status oldStatus, RefreshRecyclerView.Status newStatus) {
        switch (newStatus) {
            case STATUS_NONE:
                this.mClickView.setOnClickListener(null);
                this.mIvAnim.setVisibility(View.GONE);
                this.mTvTitle.setText("");
                break;
            case STATUS_LOADING:
                this.mClickView.setOnClickListener(null);
                this.mTvTitle.setText(REFRESH_FOOTER_LOADING);
                this.mIvAnim.clearAnimation();
                this.mLoadingAnim = (AnimationDrawable) DrawableUtils.getDrawable(R.drawable.anim_miaoyin_load_loading);
                this.mIvAnim.setBackground(this.mLoadingAnim);
                this.mLoadingAnim.start();
                this.mIvAnim.setVisibility(View.VISIBLE);
                break;
            case STATUS_LOAD_COMPLETE:
                this.mClickView.setOnClickListener(null);
                this.mTvTitle.setText(REFRESH_FOOTER_COMPLETE);
                this.mIvAnim.clearAnimation();
                this.mIvAnim.setVisibility(View.GONE);
                break;
            case STATUS_LOAD_FAILD:
                this.mTvTitle.setText(REFRESH_FOOTER_FAILED);
                this.mIvAnim.clearAnimation();
                this.mIvAnim.setVisibility(View.GONE);
                this.mClickView.setOnClickListener((v)-> { mRecyclerView.loadMore(300); });
                break;
        }
    }
}
