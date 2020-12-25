package com.tcj.sunshine.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.tcj.sunshine.tools.ActivityUtils;
import com.tcj.sunshine.tools.ColorUtils;
import com.tcj.sunshine.tools.DrawableUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;


/**
 * 自定义标题栏
 */
public class TitleView extends RelativeLayout {

    public static final int RIGHT_SHOW_NONE = 0;
    public static final int RIGHT_SHOW_IMG = 1;
    public static final int RIGHT_SHOW_TEXT = 2;

    private final int RESOURCE_IS_NULL = 0;

    private TextView mTvTitle;
    private TextView mTvRight;
    private FrameLayout mLayoutLeft;
    private FrameLayout mLayoutRight;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private View mTitleBackView;

    public TitleView(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_common_title, this);
        this.mTvTitle = this.findViewById(R.id.mTvTitle);
        this.mTvRight = this.findViewById(R.id.mTvRight);
        this.mLayoutLeft = this.findViewById(R.id.mLayoutLeft);
        this.mLayoutRight = this.findViewById(R.id.mLayoutRight);
        this.mIvLeft = this.findViewById(R.id.mIvLeft);
        this.mIvRight = this.findViewById(R.id.mIvRight);
        this.mTitleBackView = this.findViewById(R.id.mTitleBackView);

        int mode = ScreenUtils.getFullScreenMode(ActivityUtils.getActivityByContext(context));
        int statusBarHeight;
        if(mode == ScreenUtils.MODE_FULL_SCREEN_NONE) {
            statusBarHeight = 0;
        }else {
            statusBarHeight = ScreenUtils.getStatusBarHeight();
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTitleBackView.getLayoutParams();
        params.height = statusBarHeight + ScreenUtils.dip2px(54);
        mTitleBackView.requestLayout();

        String rightText = "";

        if(attrs != null) {

            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleViewStyle);
            String title = ta.getString(R.styleable.TitleViewStyle_title_text);
            int titleBackgroundResId = ta.getResourceId(R.styleable.TitleViewStyle_title_background, RESOURCE_IS_NULL);
            int titleFontColorResId = ta.getResourceId(R.styleable.TitleViewStyle_title_font_color, RESOURCE_IS_NULL);
            int rightMode = ta.getInteger(R.styleable.TitleViewStyle_right_mode, 0);
            int leftImgResId = ta.getResourceId(R.styleable.TitleViewStyle_left_img_src, RESOURCE_IS_NULL);
            int rightImgResId = ta.getResourceId(R.styleable.TitleViewStyle_right_img_src, RESOURCE_IS_NULL);
            int rightTextResId = ta.getResourceId(R.styleable.TitleViewStyle_right_text_src, RESOURCE_IS_NULL);

            setTitle(title);

            if (titleBackgroundResId != RESOURCE_IS_NULL) {
                setTitleBackgroundRes(titleBackgroundResId);
            }else {
                mTitleBackView.setBackground(getBackground());
            }

            if(rightTextResId > 0) {
                rightText = ta.getResources().getString(rightTextResId);
            }else {
                rightText = ta.getString(R.styleable.TitleViewStyle_right_text_src);
            }

            if(titleFontColorResId != RESOURCE_IS_NULL) {
                setTitleFontColorRes(titleFontColorResId);
            }

            if(leftImgResId != RESOURCE_IS_NULL) {
                mIvLeft.setImageResource(leftImgResId);
            }

            if (rightMode == 0) {
                rightMode = RIGHT_SHOW_NONE;
            }
            switch (rightMode) {
                case RIGHT_SHOW_IMG:
                    setRightImg(rightImgResId);
                    break;
                case RIGHT_SHOW_TEXT:
                    setRightText(rightText);
                    break;
                case RIGHT_SHOW_NONE:
                default:
                    mLayoutRight.setVisibility(GONE);
                    break;
            }

            ta.recycle();
        }

        this.mLayoutLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = ActivityUtils.getActivityByView(TitleView.this);
                if(activity != null) {
                    activity.finish();
                }
            }
        });
    }


    /**
     * 显示并设置右边的文本
     * @param text 右边按钮文本
     */
    public void setRightText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mIvRight.setVisibility(GONE);
            mTvRight.setVisibility(VISIBLE);
            mTvRight.setText(text);
        }
        mLayoutRight.setVisibility(VISIBLE);
    }

    public void setRightTextColor(int color){

        mTvRight.setTextColor(color);

    }


    public void setRightButtonEnabled(boolean enable){
        mLayoutRight.setEnabled(enable);
    }

    public void hideLeftButton(){
        mLayoutLeft.setVisibility(View.GONE);
    }

    public void hideRightButton(){
        mLayoutRight.setVisibility(GONE);
    }

    public FrameLayout getLayoutRight(){
        return mLayoutRight;
    }

    public FrameLayout getLayoutLeft() {
        return mLayoutLeft;
    }


    /**
     * 显示并设置左边的图片
     * @param resId DrawableRes
     */
    public void setLeftImg(@DrawableRes int resId) {
        if (resId != RESOURCE_IS_NULL) {
            mIvLeft.setVisibility(VISIBLE);
            mIvLeft.setImageResource(resId);
            mLayoutLeft.setVisibility(VISIBLE);
        } else {
            mLayoutLeft.setVisibility(GONE);
        }
    }

    /**
     * 显示并设置右边的图片
     * @param resId DrawableRes
     */
    public void setRightImg(@DrawableRes int resId) {
        if (resId != RESOURCE_IS_NULL) {
            mIvRight.setVisibility(VISIBLE);
            mTvRight.setVisibility(GONE);
            mIvRight.setImageResource(resId);
            mLayoutRight.setVisibility(VISIBLE);
        } else {
            mLayoutRight.setVisibility(GONE);
        }
    }


    /**
     * 设置标题栏背景色
     * @param resId DrawableRes
     */
    public void setTitleBackgroundRes(@DrawableRes int resId) {
        mTitleBackView.setBackgroundResource(resId);
    }

    /**
     * 设置标题字体颜色
     * @param resId
     */
    public void setTitleFontColorRes(@ColorRes int resId){
        mTvTitle.setTextColor(ContextCompat.getColor(getContext(), resId));
    }

    /**
     * 设置标题的文本
     * @param title 标题
     */
    public void setTitle(String title) {
        if(!TextUtils.isEmpty(title)) {
            mTvTitle.setText(title);
        }else {
            mTvTitle.setText("");
        }
    }

    /**
     * 设置左边的点击事件
     * @param leftOnClickListener 点击事件
     */
    public void setLeftOnClickListener(OnClickListener leftOnClickListener) {
        mLayoutLeft.setOnClickListener(leftOnClickListener);
    }

    /**
     * 设置右边的点击事件
     * @param rightOnClickListener 点击事件
     */
    public void setRightOnClickListener(OnClickListener rightOnClickListener) {
        mLayoutRight.setOnClickListener(rightOnClickListener);
    }
}
