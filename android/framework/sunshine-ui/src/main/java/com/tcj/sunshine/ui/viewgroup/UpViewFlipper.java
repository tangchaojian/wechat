package com.tcj.sunshine.ui.viewgroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Adapter;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ViewUtils;

/**
 * 向上翻滚跑马灯
 */
public class UpViewFlipper extends LinearLayout {

    private Context context;
    private Adapter adapter;
    private int position;//索引
    private int height;
    private int itemHeight;

    private int count;

    private long duration = 1000;

    private ValueAnimator animator;

    private MDataSetObserver mDataSetObserver;
    private SparseArray<View> mViewPools = new SparseArray<>();

    private OnAnimatorUpdateListener onAnimatorUpdateListener = new OnAnimatorUpdateListener();


    public UpViewFlipper(@NonNull Context context) {
        super(context);
        this.initUI(context);
    }

    public UpViewFlipper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public UpViewFlipper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UpViewFlipper(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context){
        this.context = context;
        this.setOrientation(LinearLayout.VERTICAL);
        this.mDataSetObserver = new MDataSetObserver();
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if(this.adapter != null){
            this.adapter.registerDataSetObserver(this.mDataSetObserver);
        }
    }

    /**
     * 停止动画
     */
    public void stop(){
        if(this.animator != null) {
            this.animator.removeAllUpdateListeners();
            this.animator.cancel();
            this.animator = null;
        }
    }

    /**
     * 有数据之后，初始化view
     */
    private void updateUI(){
        position = 0;
        //生成第一个View
        if(this.adapter != null && this.adapter.getCount() > 0) {

            ViewUtils.measureView(this);
            this.height = this.getMeasuredHeight();//总高度
            if (height == 0) return;

            View itemView = this.adapter.getView(0, null, null);
            ViewUtils.measureView(itemView);
            this.itemHeight = itemView.getMeasuredHeight();
            if (this.itemHeight == 0) return;

            int size = (int) Math.ceil(height * 1.0f / itemHeight);
            if (size <= 0) return;

            //添加第一个view
            this.addView(itemView);
            this.mViewPools.put(0, itemView);

            //为了显示效果，无缝衔接，所以最大个数加上2，顶部移除的itemView,又添加的底部去，实现itemView的重用
            this.count = size + 2;
            //添加view,如果需要显示的view比初始化显示的数量少，所以需要用重复的view去填充满一页
            for (int i = 1; i < this.count; i++) {
                this.position = i % adapter.getCount();//获取view的索引，求余数,应为count的值可能大于adapter.getCount()
                View childView = this.adapter.getView(this.position, null, this);
                this.addView(childView);
                this.mViewPools.put(i, childView);
            }

            LogUtils.i("sunshine-ui", "position->" + this.position);
            LogUtils.i("sunshine-ui", "mViewPools数量->" + this.mViewPools.size());

            //动画开始
            startAnim();
        }

    }


    /**
     * 设计时长
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * 开始启动动画
     */
    private void startAnim() {
        this.stop();
        this.animator = ValueAnimator.ofInt(0, -1 * itemHeight);
        this.animator.setDuration(duration);
        this.animator.setInterpolator(new LinearInterpolator());
        this.animator.addUpdateListener(onAnimatorUpdateListener);
        this.animator.start();
    }

    private class OnAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int value = (int) animation.getAnimatedValue();
//            LogUtils.i("sunshine-ui", "value->" + value);
            View itemView = UpViewFlipper.this.getChildAt(0);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)itemView.getLayoutParams();
            params.topMargin = value;
            itemView.requestLayout();

            if(value == -1 * itemHeight) {
                //动画结束
                UpViewFlipper.this.removeView(itemView);
                //修改topMargin的值
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams)itemView.getLayoutParams();
                params2.topMargin = 0;
                itemView.requestLayout();
                UpViewFlipper.this.addView(itemView);//添加到末尾

                //索引自增
                position ++;
                //如果下一条数据大于adapter.getCount()，则又从0开始，实现无线循环
                if(position >= adapter.getCount()) {
                    position = 0;
                }

                //更新数据
                adapter.getView(position, itemView, UpViewFlipper.this);
                //下一个动画开始
                startAnim();
            }
        }
    }


    /**
     * adapter.notifyDataChange()监听
     */
    private class MDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            //刷新view,重新绘制,先移除Runnable
            updateUI();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

}
