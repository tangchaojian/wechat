package com.tcj.sunshine.ui.viewgroup;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

public class TapRelativeLayout extends RelativeLayout {

    private GestureDetector detector;
    private GestureDetectorListener onGestureDetectorListener;

    private OnTapListener onTapListener;

    public TapRelativeLayout(Context context) {
        super(context);
        this.initUI(context);
    }

    public TapRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public TapRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TapRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context) {
        this.onGestureDetectorListener = new GestureDetectorListener();
        this.detector = new GestureDetector(context, onGestureDetectorListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    private class GestureDetectorListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener  {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(onTapListener != null) {
                onTapListener.onTap();
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(onTapListener != null) {
                onTapListener.onDoubleTap();
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return true;
        }
    }

    public interface OnTapListener {

        void onTap();

        void onDoubleTap();
    }
}
