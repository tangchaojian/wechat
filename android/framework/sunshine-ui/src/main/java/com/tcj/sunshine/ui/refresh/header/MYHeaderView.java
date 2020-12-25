package com.tcj.sunshine.ui.refresh.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.DrawableUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.R;
import com.tcj.sunshine.ui.refresh.RefreshLayout;

/**
 * 秒音下拉刷新头部
 */
public class MYHeaderView extends BaseHeaderView {

    private ImageView mIvAnim;
    private int mAnimWidth;

    private AnimationDrawable mRefreshingAnim;
    private AnimationDrawable mReleaseAnim;

    Handler mHandler;

    public MYHeaderView(Context context) {
        super(context);
    }

    public MYHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MYHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MYHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_miaoyin_refresh_header;
    }

    @Override
    public void initUI(View view) {
        this.mIvAnim = view.findViewById(R.id.mIvAnim);
        this.mAnimWidth = ScreenUtils.dip2px(100);
        mHandler = new Handler();
    }

    @Override
    public int getViewHeight() {
        return ScreenUtils.dip2px(110);
    }

    @Override
    public void onStateChanged(RefreshLayout mRefreshLayout, float overscrollTop, RefreshLayout.Status oldStatus, RefreshLayout.Status newStatus) {
        switch (newStatus) {
            case STATUS_NONE:
                LogUtils.i("sunshine-ui", "STATUS_NONE");
                this.mIvAnim.clearAnimation();
                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) this.mIvAnim.getLayoutParams();
                params1.width = 0;
                params1.height = 0;
                this.mIvAnim.requestLayout();
                this.mIvAnim.setImageResource(R.mipmap.img_download_11);
                break;
            case STATUS_PULL_REFRESH:
                LogUtils.i("sunshine-ui", "STATUS_PULL_REFRESH");
                this.mIvAnim.clearAnimation();
                this.mIvAnim.setImageResource(R.mipmap.img_download_11);
                int width = (int) (overscrollTop / getViewHeight() * mAnimWidth);
                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) this.mIvAnim.getLayoutParams();
                params2.width = width;
                params2.height = width;
                this.mIvAnim.requestLayout();
                break;
            case STATUS_RELEASE_REFRESH:
                LogUtils.i("sunshine-ui", "STATUS_REFRESHING");
                if (oldStatus == RefreshLayout.Status.STATUS_PULL_REFRESH && oldStatus != newStatus) {

                    FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) this.mIvAnim.getLayoutParams();
                    params3.width = mAnimWidth;
                    params3.height = mAnimWidth;
                    this.mIvAnim.requestLayout();

                    this.mIvAnim.clearAnimation();//停止动画
                    this.mRefreshingAnim = (AnimationDrawable) DrawableUtils.getDrawable(R.drawable.anim_miaoyin_refresh_loading);
                    this.mIvAnim.setImageDrawable(this.mRefreshingAnim);
                    this.mRefreshingAnim.start();//开始动画
                }
                break;
            case STATUS_REFRESHING:
                if (oldStatus == RefreshLayout.Status.STATUS_NONE && oldStatus != newStatus) {
                    // 设置大小显示(避免初次打开APP时无加载动画，原因是宽高不可见)
                    int size = dip2px(this.mIvAnim.getContext(), 100);
                    ViewGroup.LayoutParams layoutParams = this.mIvAnim.getLayoutParams();
                    if (layoutParams.width < size) {
                        layoutParams.width = size;
                        layoutParams.height = size;
                    }
                    // 执行动画设置
                    this.mIvAnim.clearAnimation();//停止动画
                    this.mRefreshingAnim = (AnimationDrawable) DrawableUtils.getDrawable(R.drawable.anim_miaoyin_refresh_loading);
                    this.mIvAnim.setImageDrawable(this.mRefreshingAnim);
                    this.mRefreshingAnim.start();//开始动画
                }
                break;

        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mRefreshingAnim != null) {
            mRefreshingAnim.stop();
        }
    }
}
