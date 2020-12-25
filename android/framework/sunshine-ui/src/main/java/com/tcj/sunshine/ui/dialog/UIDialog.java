package com.tcj.sunshine.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

import com.tcj.sunshine.ui.R;


public class UIDialog extends Dialog {

	private UIDialog mDialog = this;
	public View contentView;
	public int gravity = Gravity.CENTER;
	public int width = WindowManager.LayoutParams.WRAP_CONTENT;
	public int height = WindowManager.LayoutParams.WRAP_CONTENT;
	public int locX = 0;
	public int locY = 0;

	public int animStyleId;

	public int what;
	public Object obj1;
	public Object obj2;
	public Bundle data;

	private UIDialog(Context context, int styleId, View view) {
		super(context, styleId);
		this.setContentView(view);
	}

	public void setGravity(int gravity){
		this.gravity = gravity;
	}

	public void setData(Bundle data){
		this.data = data;
	}

	public Bundle getData(){
		return this.data;
	}

	@Override
	public void show() {
		Window window = mDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = width;
		lp.height = height;

		lp.x = locX;
		lp.y = locY;
		window.setGravity(this.gravity);
		window.setAttributes(lp);
		if(this.animStyleId != 0) {
			window.setWindowAnimations(this.animStyleId);
		}
		super.show();
	}

	public void show(int x, int y) {
		this.locX = x;
		this.locY = y;
		this.show();
	}



	public static final class Builder {

		private UIDialog dialog;
		private Context context;
		private View contentView;
		private int styleId = R.style.ThemeStyleUIDailog;
		private int gravity = Gravity.CENTER;

		private int width = WindowManager.LayoutParams.WRAP_CONTENT;
		private int height = WindowManager.LayoutParams.WRAP_CONTENT;
		private int locX;//WindowManager.LayoutParams x的值
		private int locY;//WindowManager.LayoutParams y的值

		private @StyleRes int animStyleId;
		private int what;
		private Object obj1;
		private Object obj2;
		private Bundle data;

		private OnViewClickListener mListener = new OnViewClickListener();
		private OnDialogClickListener listener;

		public Builder(Context context){
			this.context = context;
		}


		/**
		 * 设置内容View
		 * @param contentView
		 * @return
		 */
		public Builder setContentView(View contentView) {
			this.contentView = contentView;
			return this;
		}

		/**
		 * 设置内容view的id
		 * @param id
		 * @return
		 */
		public Builder setContentViewId(@LayoutRes int id) {
			this.contentView = LayoutInflater.from(this.context).inflate(id,null);
			return this;
		}

		/**
		 * 设置窗体风格
		 * @param styleId
		 * @return
		 */
		public Builder setStyleId(@StyleRes int styleId) {
			this.styleId = styleId;
			return this;
		}

		/**
		 * 设置显示位置
		 * @param gravity {@link Gravity}
		 * @return
		 */
		public Builder setGravity(int gravity) {
			this.gravity = gravity;
			return this;
		}

		public Builder setWidth(int width) {
			this.width = width;
			return this;
		}

		public Builder setHeight(int height) {
			this.height = height;
			return this;
		}

		public Builder setWindowLocation(int locX, int locY) {
			this.locX = locX;
			this.locY = locY;
			return this;
		}

		/**
		 * 设置参数(处理点击事件的时候，可能用得上)
		 * @param what
		 * @return
		 */
		public Builder setWhat(int what) {
			this.what = what;
			return this;
		}

		/**
		 * 设置参数(处理点击事件的时候，可能用得上)
		 * @param obj1
		 * @return
		 */
		public Builder setObj1(Object obj1) {
			this.obj1 = obj1;
			return this;
		}

		/**
		 * 设置参数(处理点击事件的时候，可能用得上)
		 * @param obj2
		 * @return
		 */
		public Builder setObj2(Object obj2) {
			this.obj2 = obj2;
			return this;
		}

		/**
		 * 设置参数(处理点击事件的时候，可能用得上)
		 * @param data
		 * @return
		 */
		public Builder setBundle(Bundle data) {
			this.data = data;
			return this;
		}

		public Builder setText(@IdRes int viewId, String text) {
			if(contentView == null){
				throw new UnsupportedOperationException("需要先设置contentView");
			}

			View child = contentView.findViewById(viewId);
			if(child != null && child instanceof TextView) {
				((TextView) child).setText(text);
			}

			return this;
		}


		public Builder setTextColor(@IdRes int viewId, @ColorRes int color){
			if(contentView == null){
				throw new UnsupportedOperationException("需要先设置contentView");
			}

			View child = contentView.findViewById(viewId);
			if(child != null && child instanceof TextView) {
				((TextView) child).setTextColor(ContextCompat.getColor(context, color));
			}

			return this;
		}

		/**
		 * 设置字体颜色,格式：#000000
		 * @param viewId
		 * @param color
		 * @return
		 */
		public Builder setTextColor(@IdRes int viewId, String color){
			if(contentView == null){
				throw new UnsupportedOperationException("需要先设置contentView");
			}

			View child = contentView.findViewById(viewId);
			if(child != null && child instanceof TextView) {
				try {
					((TextView) child).setTextColor(Color.parseColor(color));
				}catch (Exception e) {
					e.printStackTrace();
				}
			}

			return this;
		}

		/**
		 * 设置点击事件的id
		 * @param viewIds
		 * @return
		 */
		public Builder setClickViewIds(@IdRes Integer... viewIds){
			if(contentView == null){
				throw new UnsupportedOperationException("需要先设置contentView");
			}

			if(mListener == null){
				throw new UnsupportedOperationException("需要先设置OnDialogClickListener");
			}

			for (int id : viewIds) {
				View child = contentView.findViewById(id);
				child.setOnClickListener(mListener);
			}

			return this;
		}


		/**
		 * 设置点击事件的id
		 * @param viewIds
		 * @return
		 */
		public Builder setClickViewIds(@IdRes int[] viewIds){
			if(contentView == null){
				throw new UnsupportedOperationException("需要先设置contentView");
			}

			for (int id : viewIds) {
				View child = contentView.findViewById(id);
				child.setOnClickListener(mListener);
			}

			return this;
		}


		/**
		 * 设置View的点击事件
		 * @param listener {@link UIDialog.OnDialogClickListener}
		 * @param listener
		 * @return
		 */
		public Builder setClickListener(OnDialogClickListener listener){
			this.listener = listener;
			return this;
		}

		/**
		 * 设置动画风格id(R.style.xxx)
		 * @param animStyleId
		 * @return
		 */
		public Builder setWindowAnimations(@StyleRes int animStyleId){
			this.animStyleId = animStyleId;
			return this;
		}

		public UIDialog build(){
			this.dialog = new UIDialog(context, styleId, contentView);
			this.dialog.contentView = contentView;
			this.dialog.gravity = this.gravity;
			this.dialog.width = this.width;
			this.dialog.height = this.height;
			this.dialog.locX = this.locX;
			this.dialog.locY = this.locY;
			this.dialog.animStyleId = this.animStyleId;//动画风格id
			this.dialog.what = this.what;
			this.dialog.obj1 = this.obj1;
			this.dialog.obj2 = this.obj2;
			this.dialog.data = this.data;
			return dialog;
		}

		private class OnViewClickListener implements View.OnClickListener {

			@Override
			public void onClick(View v) {
				if(listener != null) listener.onClick(dialog, v);
			}
		}
	}

	public interface OnDialogClickListener {
		void onClick(UIDialog dialog, View view);
	}
}
