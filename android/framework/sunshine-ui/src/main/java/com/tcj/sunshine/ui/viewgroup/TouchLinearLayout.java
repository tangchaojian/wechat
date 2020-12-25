package com.tcj.sunshine.ui.viewgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class TouchLinearLayout extends LinearLayout {

	public TouchLinearLayout(Context context) {
		super(context);
	}
	
	public TouchLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TouchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
