package com.tcj.sunshine.ui.refresh.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.DrawableUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;
import com.tcj.sunshine.ui.refresh.RefreshLayout;

/**
 * 仿天猫下拉刷新头部
 */
public class TmallHeaderView extends BaseHeaderView  {

    private TextView mTvTitle;
    private ImageView mIvAnim;
    private AnimationDrawable mPullAnim;
    private AnimationDrawable mLoadingAnim;

    private static String REFRESH_HEADER_PULLING = "下拉刷新";//"下拉可以刷新";
    private static String REFRESH_HEADER_REFRESHING = "正在刷新...";//"正在刷新...";
    private static String REFRESH_HEADER_RELEASE = "松开立即刷新";//"释放立即刷新";


    public TmallHeaderView(Context context) {
        super(context);
    }

    public TmallHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TmallHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TmallHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_tmall_refresh_header;
    }

    @Override
    public void initUI(View view) {
        this.mTvTitle = view.findViewById(R.id.mTvTitle);
        this.mIvAnim = view.findViewById(R.id.mIvAnim);
    }

    @Override
    public int getViewHeight() {
        return ScreenUtils.dip2px(110);
    }

    @Override
    public void onStateChanged(RefreshLayout mRefreshLayout, float overscrollTop, RefreshLayout.Status oldStatus, RefreshLayout.Status newStatus) {
        switch (newStatus) {
            case STATUS_NONE:
                this.mIvAnim.clearAnimation();
                this.mTvTitle.setText("");
                break;
            case STATUS_PULL_REFRESH:
                this.mIvAnim.clearAnimation();//停止动画
                this.mPullAnim = (AnimationDrawable) DrawableUtils.getDrawable(R.drawable.anim_tm_refresh_pull_down);
                this.mIvAnim.setBackground(this.mPullAnim);
                this.mPullAnim.start();//开始动画
                this.mTvTitle.setText(REFRESH_HEADER_PULLING);
                break;
            case STATUS_RELEASE_REFRESH:
                this.mTvTitle.setText(REFRESH_HEADER_RELEASE);
                break;
            case STATUS_REFRESHING:
                this.mIvAnim.clearAnimation();//停止动画
                this.mLoadingAnim = (AnimationDrawable) DrawableUtils.getDrawable(R.drawable.anim_tm_refresh_loading);
                this.mIvAnim.setBackground(this.mLoadingAnim);
                this.mLoadingAnim.start();//开始动画
                this.mTvTitle.setText(REFRESH_HEADER_REFRESHING);
                break;

        }
    }
}
