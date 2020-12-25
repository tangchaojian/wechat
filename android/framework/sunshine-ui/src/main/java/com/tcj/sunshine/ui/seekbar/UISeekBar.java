package com.tcj.sunshine.ui.seekbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.tcj.sunshine.tools.DrawableUtils;
import com.tcj.sunshine.ui.R;

/**
 * 自定义SeekBar
 */
public class UISeekBar extends AppCompatSeekBar {
    public UISeekBar(Context context) {
        super(context);
        this.initUI(context);
    }

    public UISeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public UISeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    private void initUI(Context context){
        /**
         * android:progressDrawable="@drawable/shape_seekbar_progress"
         * android:thumb="@drawable/shape_seekbar_circle_thumb" />
         */
        this.setProgressDrawable(DrawableUtils.getDrawable(R.drawable.shape_seekbar_progress));
        this.setThumb(DrawableUtils.getDrawable(R.drawable.shape_seekbar_circle_thumb));
    }
}
