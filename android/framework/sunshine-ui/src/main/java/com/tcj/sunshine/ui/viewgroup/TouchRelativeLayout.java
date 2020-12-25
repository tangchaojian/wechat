package com.tcj.sunshine.ui.viewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TouchRelativeLayout extends RelativeLayout {

	public TouchRelativeLayout(Context context) {
		super(context);
	}
	
	public TouchRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public TouchRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

}
