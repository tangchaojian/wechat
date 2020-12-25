package com.tcj.sunshine.ui.viewgroup;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ViewUtils;

/**
 * 向上翻滚跑马灯
 */
public class UpMarqueeLayout extends FrameLayout {

    private Context context;
    private FrameLayout mUpAnimView;//用来显示向上动画的view，动画结束后，显示showItemView，mUpAnimView在后面进行位置还原（让用户感觉不到）
    private Adapter adapter;
    private int position;//索引
    private int height;

    private AnimatorSet mUpAnim1;//向上动画
    private AnimatorSet mUpAnim2;//向上动画
    private long ANIMATION_DURATION = 1000;
    private long SHOW_DURATION = 1000;

    private MDataSetObserver mDataSetObserver;
    private LoopRunnable mLoopRunnable = new LoopRunnable();
    private SparseArray<View> mViewPools = new SparseArray<>();


    public UpMarqueeLayout(@NonNull Context context) {
        super(context);
        this.initUI(context);
    }

    public UpMarqueeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context);
    }

    public UpMarqueeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UpMarqueeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context);
    }

    private void initUI(Context context){
        this.mDataSetObserver = new MDataSetObserver();
        //里面添加两个itemView
        this.mUpAnimView = new FrameLayout(context);
        this.addView(this.mUpAnimView);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if(this.adapter != null){
            this.adapter.registerDataSetObserver(this.mDataSetObserver);
        }
    }

    public void stop(){
        if(this.mLoopRunnable != null && getHandler() != null){
            this.getHandler().removeCallbacks(this.mLoopRunnable);
        }
    }

    /**
     * 有数据之后，初始化view
     */
    private void updateUI(){
        position = 0;
        //生成第一个View
        if(this.adapter != null && this.adapter.getCount() > 0) {

            if(this.mUpAnimView != null && mUpAnimView.getChildCount() > 0)this.mUpAnimView.removeAllViews();

            //先生成三个view，可以反复使用
            View itemView1 = null;
            View itemView2 = null;

            itemView1 = this.adapter.getView(position, null, null);

            if(this.adapter.getCount() > 1) {
                itemView2 = this.adapter.getView(position + 1, null, null);
            }

            if(itemView1 != null) this.mViewPools.put(0, itemView1);
            if(itemView2 != null) this.mViewPools.put(1, itemView2);

            ViewUtils.measureView(this);
            this.height = this.getMeasuredHeight();
            LayoutParams params =(LayoutParams)mUpAnimView.getLayoutParams();
            params.height = 3 * height;
            params.topMargin = - height;
            this.mUpAnimView.requestLayout();

            this.mUpAnimView.addView(itemView1);
            if(this.adapter.getCount() > 1) {
                this.mUpAnimView.addView(itemView2);
            }

            itemView1.setTranslationY(1 * height);
            itemView2.setTranslationY(2 * height);

            if(this.adapter.getCount() > 1) {
                postDelayed(mLoopRunnable, SHOW_DURATION);
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
            if(mLoopRunnable != null && getHandler() != null)getHandler().removeCallbacks(mLoopRunnable);
            updateUI();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }


    private class LoopRunnable implements Runnable {

        @Override
        public void run() {
            if(adapter == null)return;
            ObjectAnimator translate1 = ObjectAnimator.ofFloat(mViewPools.get(position % 2), "translationY", 1 * height, 0);
            mUpAnim1 = new AnimatorSet();
            mUpAnim1.play(translate1);
            mUpAnim1.setDuration(ANIMATION_DURATION);
            mUpAnim1.addListener(new OnAnimatorListener(position));
            mUpAnim1.start();

            int next = position + 1;
            ObjectAnimator translate2 = ObjectAnimator.ofFloat(mViewPools.get(next % 2), "translationY", 2 * height, height);
            mUpAnim2 = new AnimatorSet();
            mUpAnim2.play(translate2);
            mUpAnim2.setDuration(ANIMATION_DURATION);
            mUpAnim2.start();

            position++;
            if(position >= adapter.getCount()) {
                position = 0;
            }
        }
    }

    private class OnAnimatorListener implements Animator.AnimatorListener {

        private int index;

        public OnAnimatorListener(int index) {
            this.index = index;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

            //预加载，加载下一条数据
            View itemView = mViewPools.get(index % 2);

            int nextPost = index + 2;
            if(nextPost % 2 == 0 && nextPost >= adapter.getCount()) {
                //索引为偶数
                nextPost = 0;
            }else if(nextPost % 2 == 1 && nextPost >= adapter.getCount()) {
                //索引为奇数
                nextPost = 1;
            }
//            LogUtils.i("sunshine-ui", "nextPost->" + nextPost);
            adapter.getView(nextPost, itemView, null);
            itemView.setTranslationY(2 * height);

            postDelayed(mLoopRunnable, SHOW_DURATION);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
