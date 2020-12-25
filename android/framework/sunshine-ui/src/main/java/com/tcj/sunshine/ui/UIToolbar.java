package com.tcj.sunshine.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.tcj.sunshine.tools.ActivityUtils;
import com.tcj.sunshine.tools.ScreenUtils;

/**
 * Created by Stefan Lau on 2019/11/28.
 */
public class UIToolbar extends FrameLayout {

    private Context context;
    public View mStatusView;
    public View mBackView;
    public ImageView mIvBack;
    public TextView mTvTitle;

    private String title;
    private int backIconResId;
    private int titleColorId;
    private int backgroundColorId;

    public UIToolbar(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public UIToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public UIToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UIToolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs){
        this.context = context;
        if(attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UIToolBarStyle);
            this.title = ta.getString(R.styleable.UIToolBarStyle_toolbar_title);
            this.backIconResId = ta.getResourceId(R.styleable.UIToolBarStyle_toolbar_back_icon_src, 0);
            this.titleColorId = ta.getResourceId(R.styleable.UIToolBarStyle_toolbar_title_color, 0);
            this.backgroundColorId = ta.getResourceId(R.styleable.UIToolBarStyle_toolbar_background, 0);

            ta.recycle();
        }

        LayoutInflater.from(context).inflate(R.layout.view_toolbar, this,true);
        this.mStatusView = this.findViewById(R.id.mStatusView);
        this.mBackView = this.findViewById(R.id.mBackView);
        this.mIvBack = this.findViewById(R.id.mIvBack);
        this.mTvTitle = this.findViewById(R.id.mTvTitle);

        int mode = ScreenUtils.getFullScreenMode(ActivityUtils.getActivityByContext(context));
        int statusBarHeight;
        if(mode == ScreenUtils.MODE_FULL_SCREEN_NONE) {
            statusBarHeight = 0;
        }else {
            statusBarHeight = ScreenUtils.getStatusBarHeight();
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mStatusView.getLayoutParams();
        params.height = statusBarHeight;
        mStatusView.requestLayout();

        if(backIconResId != 0) {
            mIvBack.setImageResource(backIconResId);
        }

        if(titleColorId != 0) {
            this.mTvTitle.setTextColor(ContextCompat.getColor(context, titleColorId));
        }
        this.mTvTitle.setText(this.title);

        if(backgroundColorId != 0) {
            //背景颜色
            this.setBackgroundColor(ContextCompat.getColor(context, backgroundColorId));
        }else {
            //默认背景颜色
            this.setBackgroundColor(Color.parseColor("#212121"));
        }

//        ?android:attr/actionBarSize
        TypedValue typedValue = new TypedValue();
        int minHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, this.getResources().getDisplayMetrics());
        this.setMinimumHeight(minHeight);



        this.mBackView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = ActivityUtils.getActivityByView(UIToolbar.this);
                if(activity != null){
                    activity.finish();
                }
            }
        });
    }

    public void setBackIconBitmap(Bitmap bitmap){
        if(bitmap != null) {
            this.mIvBack.setImageBitmap(bitmap);
        }
    }

    public void setBackIconResource(int resId){
        if(resId != 0) {
            this.mIvBack.setImageResource(resId);
        }
    }

    public void setBackIconDrawable(Drawable drawable){
        if(drawable != null) {
            this.mIvBack.setImageDrawable(drawable);
        }
    }

    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)) {
            this.mTvTitle.setText(title);
        }
    }

    public void setTitleColor(int color) {
        this.mTvTitle.setTextColor(ContextCompat.getColor(context, color));
    }

}
