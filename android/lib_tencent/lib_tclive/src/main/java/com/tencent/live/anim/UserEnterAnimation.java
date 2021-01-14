package com.tencent.live.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.tencent.live.common.msg.TCUserEnterEntity;
import com.tencent.live.widget.LiveGiftAnimView;
import com.tencent.live.widget.LiveUserEnterAnimView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 礼物动画
 * 查看 {@link LiveGiftAnimView}
 */
public class UserEnterAnimation {
    private final int SHOW_HIDE_ANIMATOR_DURATION = 500;
    private final int ANIMATION_STAY_DURATION = 1000;

    private boolean isFree = true;

    private ViewGroup mAnimView;

    private AnimatorSet mAnimatorSet;

    private Queue<TCUserEnterEntity> cache = new LinkedList<>();

    public UserEnterAnimation(ViewGroup mAnimView) {
        this.mAnimView = mAnimView;
        this.mAnimatorSet = buildAnimationSet(mAnimView);
    }

    // 用户进入直播间，等待显示动画
    public void showUserEnterAnimation(final TCUserEnterEntity message) {
        cache.add(message);
        checkAndStart();
    }

    private void checkAndStart() {
        if(!isFree) {
            return;
        }

        if(isFree) {
            startAnimation(mAnimView, mAnimatorSet);
        }
    }

    // 开始礼物动画
    private void startAnimation(ViewGroup target, AnimatorSet set) {
        TCUserEnterEntity message = cache.poll();
        if(message == null) {
            return;
        }

        // 更新状态
        onAnimationStart(target);

        // 更新礼物视图
        updateView(message, target);

        // 执行动画组
        target.setAlpha(1f);
        target.setVisibility(View.VISIBLE);
        set.start();
    }

    private void onAnimationStart(final ViewGroup target) {
        isFree = false;
    }

    private void onAnimationCompleted(final ViewGroup target) {
        isFree = true;

        checkAndStart();
    }

    /**
     * ********************* 属性动画 *********************
     */

    private AnimatorSet buildAnimationSet(final ViewGroup target){
        ObjectAnimator show = buildShowAnimator(target, ANIMATION_STAY_DURATION);
        ObjectAnimator hide = buildHideAnimator(target, SHOW_HIDE_ANIMATOR_DURATION);
        hide.setStartDelay(ANIMATION_STAY_DURATION);

        AnimatorSet set = new AnimatorSet();
        set.setTarget(target);
        set.playSequentially(show, hide);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onAnimationCompleted(target);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return set;
    }

    private ObjectAnimator buildShowAnimator(final View target, long duration) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(target, "translationX", -600.0F, 100.0F).setDuration(duration);
        translationX.setInterpolator(new OvershootInterpolator());

        return translationX;
    }

    private ObjectAnimator buildHideAnimator(final View target, long duration) {
//        return ObjectAnimator.ofFloat(target, View.ALPHA, 1f, 0.0f).setDuration(duration);

        ObjectAnimator translationX = ObjectAnimator.ofFloat(target, "translationX", 100.0F, -600.0F).setDuration(duration);
        translationX.setInterpolator(new OvershootInterpolator());
        return translationX;
    }

    /**
     * ********************* 更新礼物信息 *********************
     */

    private void updateView(final TCUserEnterEntity message, ViewGroup root) {
        LiveUserEnterAnimView mAnimView = (LiveUserEnterAnimView)root;
        mAnimView.updateView(message);
    }
}
