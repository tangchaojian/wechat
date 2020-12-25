package com.tcj.sunshine.ui.textview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatTextView;


/**
 * 跑马灯效果<向上方向>的TextView
 * Created by jayuchou on 15/7/9.
 */
public class UpMarqueeTextView extends AppCompatTextView implements Animator.AnimatorListener {

    private static final String TAG = "UpMarqueeTextView";

    private static final int ANIMATION_DURATION = 500;
    private float height;
    private AnimatorSet mAnimatorStartSet;
    private AnimatorSet mAnimatorEndSet;
    private String mText;

    public UpMarqueeTextView(Context context) {
        super(context);
    }

    public UpMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       // LogUtils.w(TAG, "--- onDraw ---");
        height = getHeight()/2;// 确保view的高度
    }

    /**--- 初始化向上脱离屏幕的动画效果 ---*/
    private void initStartAnimation() {
        ObjectAnimator translate = ObjectAnimator.ofFloat(this, "translationY", 0, -height);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.1f);
        mAnimatorStartSet = new AnimatorSet();
        mAnimatorStartSet.play(translate).with(alpha);
        mAnimatorStartSet.setDuration(ANIMATION_DURATION);
        mAnimatorStartSet.addListener(this);
    }

    /**--- 初始化从屏幕下面向上的动画效果 ---*/
    private void initEndAnimation() {
        ObjectAnimator translate = ObjectAnimator.ofFloat(this, "translationY", height, 0);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        mAnimatorEndSet = new AnimatorSet();
        mAnimatorEndSet.play(translate).with(alpha);
        mAnimatorEndSet.setDuration(ANIMATION_DURATION);
    }

    /**--- 设置内容 ---**/
    public void setText(String text) {
        if (TextUtils.isEmpty(text)) {
           // LogUtils.e(TAG, "--- 请确保text不为空 ---");
            return;
        }
        mText = text;
        if (null == mAnimatorStartSet) {
            initStartAnimation();
        }
        mAnimatorStartSet.start();
    }


    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        super.setText(mText);
        if (null == mAnimatorEndSet) {
            initEndAnimation();
        }
        mAnimatorEndSet.start();
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}
