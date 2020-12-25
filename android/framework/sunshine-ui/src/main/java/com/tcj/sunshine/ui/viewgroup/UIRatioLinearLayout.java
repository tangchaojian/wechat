package com.tcj.sunshine.ui.viewgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.ui.R;


/**
 * 比例布局
 */
public class UIRatioLinearLayout extends LinearLayout {

    private int ratio_width;
    private int ratio_height;

    public UIRatioLinearLayout(Context context) {
        super(context);
    }

    public UIRatioLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UIRatioLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("NewApi")
    public UIRatioLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioViewStyle);

        this.ratio_width = a.getInteger(R.styleable.RatioViewStyle_ratio_width, 0);
        this.ratio_height = a.getInteger(R.styleable.RatioViewStyle_ratio_height, 0);

        LogUtils.i("sunshine-ui", "ratio_width->" + ratio_width);
        LogUtils.i("sunshine-ui", "ratio_height->" + ratio_height);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(ratio_width != 0 && ratio_height != 0) {

            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            //根据宽高比ratio和模式创建一个测量值
            float ratio = ratio_height / (ratio_width * 1.0f);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (widthSize * ratio), MeasureSpec.EXACTLY);
        }
        //必须调用下面的两个方法之一完成onMeasure方法的重写，否则会报错
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
