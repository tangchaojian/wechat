package com.tcj.sunshine.ui.refresh.header;

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
import com.tcj.sunshine.ui.refresh.RefreshLayout;

/**
 * 下拉刷新
 */
public class HeaderView extends BaseHeaderView {

    private TextView mTvTitle;
    private ImageView mIvAnim;
    private Animation mLoadingAnim;

    private static String REFRESH_HEADER_PULLING = "下拉刷新";//"下拉可以刷新";
    private static String REFRESH_HEADER_RELEASE = "松开立即刷新";//"释放立即刷新";
    private static String REFRESH_HEADER_REFRESHING = "正在刷新...";//"正在刷新...";

    public HeaderView(Context context) {
        super(context);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_refresh_header;
    }

    @Override
    public void initUI(View view) {
        this.mTvTitle = view.findViewById(R.id.mTvTitle);
        this.mIvAnim = view.findViewById(R.id.mIvAnim);
    }

    @Override
    public int getViewHeight() {
        return ScreenUtils.dip2px(50);
    }

    @Override
    public void onStateChanged(RefreshLayout mRefreshLayout, float overscrollTop, RefreshLayout.Status oldState, RefreshLayout.Status newState) {
        switch (newState) {
            case STATUS_NONE:
                this.mTvTitle.setText("");
                this.mIvAnim.clearAnimation();
                break;
            case STATUS_PULL_REFRESH:
                this.mTvTitle.setText(REFRESH_HEADER_PULLING);
                this.mIvAnim.clearAnimation();
                break;
            case STATUS_RELEASE_REFRESH:
                this.mTvTitle.setText(REFRESH_HEADER_RELEASE);
                this.mIvAnim.clearAnimation();
                break;
            case STATUS_REFRESHING:
                this.mTvTitle.setText(REFRESH_HEADER_REFRESHING);
                this.mLoadingAnim = new RotateAnimation(0f, 360, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                this.mLoadingAnim.setRepeatCount(Animation.INFINITE);
                this.mLoadingAnim.setRepeatMode(Animation.RESTART);
                this.mLoadingAnim.setDuration(1000);
                this.mLoadingAnim.setInterpolator(new LinearInterpolator());
                this.mIvAnim.startAnimation(mLoadingAnim);
                break;

        }
    }
}
