package com.tcj.sunshine.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * 视频播放器的SeekBar
 */
public class VideoSeekBar extends AppCompatSeekBar {

    private OnSeekBarTouchListener mListener;

    public VideoSeekBar(Context context) {
        super(context);
    }

    public VideoSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(mListener != null) {
            mListener.onTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    public void setOnSeekBarTouchListener(OnSeekBarTouchListener mListener) {
        this.mListener = mListener;
    }

    public interface OnSeekBarTouchListener {

        void onTouchEvent(MotionEvent event);
    }
}
