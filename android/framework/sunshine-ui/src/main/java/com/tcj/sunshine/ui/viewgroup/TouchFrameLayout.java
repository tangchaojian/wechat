package com.tcj.sunshine.ui.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchFrameLayout extends FrameLayout {

	public TouchFrameLayout(Context context) {
		super(context);
	}
	
	public TouchFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TouchFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		return true;
//	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

}
