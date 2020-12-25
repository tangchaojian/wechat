package com.tcj.sunshine.ui.refresh.footer;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;
import com.tcj.sunshine.ui.refresh.RefreshRecyclerView;

/**
 * 底部View
 */
public class FooterView extends BaseFooterView {

    private View mClickView;
    private TextView mTvTitle;
    private ImageView mIvAnim;
    private Animation mLoadingAnim;

    private static final String REFRESH_FOOTER_LOADING = "正在加载...";
    private static final String REFRESH_FOOTER_FAILED = "加载失败，点击重新加载";
    private static final String REFRESH_FOOTER_COMPLETE = "-- 到底了 --";

    public FooterView(Context context) {
        super(context);
    }

    public FooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_loading_footer;
    }

    @Override
    public void initUI(View view) {
        this.mClickView = view.findViewById(R.id.mClickView);
        this.mTvTitle = view.findViewById(R.id.mTvTitle);
        this.mIvAnim = view.findViewById(R.id.mIvAnim);
    }

    @Override
    public int getViewHeight() {
        return ScreenUtils.dip2px(50);
    }

    @Override
    public void onStateChanged(RefreshRecyclerView mRecyclerView, RefreshRecyclerView.Status oldStatus, RefreshRecyclerView.Status newStatus) {
        switch (newStatus) {
            case STATUS_NONE:
                this.mTvTitle.setText("");
                this.mIvAnim.setVisibility(View.VISIBLE);
                this.mIvAnim.clearAnimation();
                break;
            case STATUS_LOADING:
                this.mTvTitle.setText(REFRESH_FOOTER_LOADING);
                this.mIvAnim.setVisibility(View.VISIBLE);
                this.mIvAnim.clearAnimation();
                mLoadingAnim = new RotateAnimation(0f, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                mLoadingAnim.setRepeatCount(Animation.INFINITE);
                mLoadingAnim.setRepeatMode(Animation.RESTART);
                mLoadingAnim.setDuration(1000);
                mLoadingAnim.setInterpolator(new LinearInterpolator());
                this.mIvAnim.startAnimation(mLoadingAnim);
                this.mClickView.setOnClickListener(null);
                break;
            case STATUS_LOAD_FAILD:
                this.mTvTitle.setText(REFRESH_FOOTER_FAILED);
                this.mIvAnim.setVisibility(View.GONE);
                this.mIvAnim.clearAnimation();
                this.mClickView.setOnClickListener((v)-> { mRecyclerView.loadMore(300); });
                break;
            case STATUS_LOAD_COMPLETE:
                this.mTvTitle.setText(REFRESH_FOOTER_COMPLETE);
                this.mIvAnim.setVisibility(View.GONE);
                this.mIvAnim.clearAnimation();
                this.mClickView.setOnClickListener(null);
                break;
        }
    }
}
