package com.tcj.sunshine.ui.refresh;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ListViewCompat;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.ui.refresh.header.BaseHeaderView;
import com.tcj.sunshine.ui.refresh.header.MYHeaderView;

/**
 * 刷新Layout
 */
public class RefreshLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    private BaseHeaderView mHeaderView;
    private View mTarget;

    private OnChildScrollUpCallback mChildScrollUpCallback;

    private long refreshDelayMillis = 500;//刷新延迟时间, 默认300毫秒

    private static final int ANIMATE_TO_START_DURATION = 500;

    private int mTouchSlop;
    private static int WIDTH_HEADER_VIEW;//headerView的宽度
    private static int HEIGHT_HEADER_VIEW;//headerView的高度
    private static int HEIGHT_RELEASE;//松开立即刷新的高度值
    private static int HEIGHT_MAX_VALUE;//下拉最大高度
    private int mTranslationY;//移动的高

    private Status status = Status.STATUS_NONE;//所处状态


    private float mTotalUnconsumed;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = 0.5f;

    private float mInitialMotionY;
    private float mInitialDownY;
    private boolean mIsBeingDragged = false;
    private int mActivePointerId = INVALID_POINTER;

    private boolean mReturningToStart;
    // Whether the client has set a custom starting position;
    boolean mUsingCustomStart;

    private boolean allowIntercept = false;//允许拦截


    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];

    private float startY;
    private float startX;

    // 记录viewPager是否拖拽的标记
    private boolean mIsDragger;

    private static final int[] LAYOUT_ATTRS = new int[]{
            android.R.attr.enabled
    };

    private OnRefreshListener mListener;

    public RefreshLayout(Context context) {
        super(context);
        this.initUI(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initUI(context, attrs);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initUI(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initUI(context, attrs);
    }

    private void initUI(Context context, AttributeSet attrs) {

        try {
            this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.setWillNotDraw(false);

            this.mHeaderView = new MYHeaderView(context);
            this.addView(mHeaderView);
            this.mHeaderView.bringToFront();

            WIDTH_HEADER_VIEW = ScreenUtils.getScreenWidth();
            HEIGHT_HEADER_VIEW = this.mHeaderView.getViewHeight();
            HEIGHT_RELEASE = HEIGHT_HEADER_VIEW;
            HEIGHT_MAX_VALUE = 2 * HEIGHT_RELEASE;

            this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
            this.mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
            this.setNestedScrollingEnabled(true);

            if (attrs != null) {
                final TypedArray ta = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
                this.setEnabled(ta.getBoolean(0, true));
                ta.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 设置headView
     *
     * @param mHeaderView
     */
    public void setHeaderView(BaseHeaderView mHeaderView) {
        if (mHeaderView != null && this.mHeaderView != null) {
            this.removeView(this.mHeaderView);
            this.addView(mHeaderView);
            this.mHeaderView.bringToFront();
        }
        this.mHeaderView = mHeaderView;
        HEIGHT_HEADER_VIEW = this.mHeaderView.getViewHeight();
        HEIGHT_RELEASE = HEIGHT_HEADER_VIEW;
        HEIGHT_MAX_VALUE = 2 * HEIGHT_RELEASE;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        final View child = mTarget;
        final int childLeft = getPaddingLeft();
        final int childTop = getPaddingTop();
        final int childWidth = width - getPaddingLeft() - getPaddingRight();
        final int childHeight = height - getPaddingTop() - getPaddingBottom();
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        this.mHeaderView.layout((width / 2 - WIDTH_HEADER_VIEW / 2), -HEIGHT_HEADER_VIEW, (width / 2 + WIDTH_HEADER_VIEW / 2), 0);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mTarget == null) {
            ensureTarget();
        }
        if (mTarget == null) {
            return;
        }
        mTarget.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        mHeaderView.measure(MeasureSpec.makeMeasureSpec(WIDTH_HEADER_VIEW, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(HEIGHT_HEADER_VIEW, MeasureSpec.EXACTLY));
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            reset();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        ensureTarget();

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (!allowIntercept) {
                return false;
            }
        }

        if (!isGestureIntercept(ev)) {
            return false;
        }

        final int action = ev.getActionMasked();
        int pointerIndex;

        if ((status == Status.STATUS_REFRESHING) && action == MotionEvent.ACTION_DOWN) {
            setStatus(Status.STATUS_NONE, 0, false);
            setTargetOffsetTopAndBottom(0);
            return false;
        }


        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || (status == Status.STATUS_REFRESHING)) {
//            LogUtils.i("sunshine-ui", "1");
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(0);
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
//                    LogUtils.i("sunshine-ui", "2");
                    return false;
                }
                mInitialDownY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
//                    LogUtils.i("sunshine-ui", "3");
                    return false;
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
//                    LogUtils.i("sunshine-ui", "4");
                    return false;
                }
                final float y = ev.getY(pointerIndex);
                startDragging(y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }

//        if(!mIsBeingDragged) {
//            LogUtils.i("sunshine-ui", "5");
//        }else {
//            LogUtils.i("sunshine-ui", "6");
//        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        int pointerIndex = -1;

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false;
        }

        if (!isEnabled() || mReturningToStart || canChildScrollUp()
                || (status == Status.STATUS_REFRESHING)) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = ev.getY(pointerIndex);
                startDragging(y);

                if (mIsBeingDragged) {
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    if (overscrollTop > 0) {
//                        LogUtils.i("sunshine-ui", "onTouchEvent->moveSpinner");
                        moveSpinner(overscrollTop);
                    } else {
                        return false;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                pointerIndex = ev.getActionIndex();
                if (pointerIndex < 0) {
                    return false;
                }
                mActivePointerId = ev.getPointerId(pointerIndex);
                break;
            }

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP: {
                pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                if (mIsBeingDragged) {
                    final float y = ev.getY(pointerIndex);
                    final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                    mIsBeingDragged = false;
                    finishSpinner(overscrollTop);
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return true;
    }


    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {

        if ((android.os.Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }

    //=============================NestedScrollingParent, NestedScrollingChild实现接口 ==================


    // NestedScrollingParent
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !mReturningToStart && !(status == Status.STATUS_REFRESHING)
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mTotalUnconsumed = 0;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - (int) mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                mTotalUnconsumed -= dy;
                consumed[1] = dy;
            }
//            LogUtils.i("sunshine-ui", "onNestedPreScroll->moveSpinner");
            moveSpinner(mTotalUnconsumed);
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            if (mHeaderView != null) mHeaderView.setVisibility(View.GONE);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed);
            mTotalUnconsumed = 0;
        }
        // Dispatch up our nested parent
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {

        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy);
//            LogUtils.i("sunshine-ui", "onNestedScroll->moveSpinner");
            moveSpinner(mTotalUnconsumed);
        }
    }

    // NestedScrollingChild

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {

        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }


    //===============================================================


    private boolean canChildScrollUp() {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback.canChildScrollUp(this, mTarget);
        }
        if (mTarget instanceof ListView) {
            return ListViewCompat.canScrollList((ListView) mTarget, -1);
        }
        return mTarget.canScrollVertically(-1);
    }


    private void setTargetOffsetTopAndBottom(int offset) {
        this.mHeaderView.bringToFront();
        if (mHeaderView != null) mHeaderView.setTranslationY(offset);
        if (mTarget != null) mTarget.setTranslationY(offset);
        mTranslationY = offset;
    }

    /**
     * 开始拖拽
     *
     * @param y
     */
    private void startDragging(float y) {
        final float yDiff = y - mInitialDownY;
        if (yDiff > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop;
            mIsBeingDragged = true;
        }
    }

    /**
     * 下拉刷新动作
     *
     * @param overscrollTop
     */
    private void moveSpinner(float overscrollTop) {
        int offset = (int) overscrollTop;
        if (offset >= HEIGHT_RELEASE) {
            setStatus(Status.STATUS_RELEASE_REFRESH, overscrollTop);
        } else {
            setStatus(Status.STATUS_PULL_REFRESH, overscrollTop);
        }

        if (offset > HEIGHT_MAX_VALUE) {
            offset = HEIGHT_MAX_VALUE;
        }

//        LogUtils.i("sunshine-ui", "offset:" + offset);
        setTargetOffsetTopAndBottom(offset);
    }


    /**
     * 完成下拉动作
     *
     * @param overscrollTop
     */
    private void finishSpinner(float overscrollTop) {

        if (overscrollTop >= HEIGHT_RELEASE) {
            //开始刷新
            LogUtils.d("sunshine-ui", "开始刷新");
            setStatus(Status.STATUS_REFRESHING, overscrollTop);
        } else {
            // 取消刷新,回弹
            LogUtils.d("sunshine-ui", "取消刷新");
            setStatus(Status.STATUS_NONE, overscrollTop);
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void moveAnim(int start, int end) {
        moveAnim(start, end, null);
    }

    private void moveAnim(int start, int end, Animator.AnimatorListener listener) {
        ValueAnimator anim = ValueAnimator.ofInt(start, end);
        anim.setDuration(ANIMATE_TO_START_DURATION);
        anim.addUpdateListener(animation -> {
            int offset = (int) animation.getAnimatedValue();
            setTargetOffsetTopAndBottom(offset);
        });
        if (listener != null) {
            anim.addListener(listener);
        }
        anim.start();
    }

    /**
     * 确定Target,即RecylerView
     */
    private void ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
        if (mTarget == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView)) {
                    mTarget = child;
                    break;
                }
            }
        }
    }

    private boolean isGestureIntercept(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {

            case MotionEvent.ACTION_DOWN:

                // 记录手指按下的位置
                startY = ev.getY();
                startX = ev.getX();
                // 初始化标记
                mIsDragger = false;

                break;
            case MotionEvent.ACTION_MOVE:

                // 如果viewpager正在拖拽中，那么不拦截它的事件，直接return false；
                if (mIsDragger) {
                    return false;
                }

                // 获取当前手指位置
                float endY = ev.getY();
                float endX = ev.getX();
                float distanceX = Math.abs(endX - startX);
                float distanceY = Math.abs(endY - startY);
                // 如果X轴位移大于Y轴位移，那么将事件交给viewPager处理。
                if (distanceX > mTouchSlop && distanceX > distanceY) {
                    mIsDragger = true;
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 初始化标记
                mIsDragger = false;

                break;
        }

        // 如果是Y轴位移大于X轴，事件交给RefreshLayout处理。
        return true;
    }

    // =============================================================================================


    /**
     * 设置是否允许拦截（有些情况需要强行控制不能拦截）
     *
     * @param allowIntercept
     */
    public void setAllowIntercept(boolean allowIntercept) {
        this.allowIntercept = allowIntercept;
    }

    /**
     * 设置状态
     *
     * @param status
     */
    public void setStatus(Status status, float overscrollTop) {
        this.setStatus(status, overscrollTop, true);
    }

    /**
     * @param status
     * @param anim   是否需要显示动画
     */
    public void setStatus(Status status, float overscrollTop, boolean anim) {
        Status oldStatus = this.status;
        this.status = status;
        ensureTarget();
        int start;
        int end;
        switch (this.status) {
            case STATUS_NONE:
                if (anim) {
                    start = this.mTranslationY;
                    end = 0;
                    moveAnim(start, end, new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mHeaderView == null) return;
                            mHeaderView.onStateChanged(RefreshLayout.this, overscrollTop, oldStatus, status);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    return;
                }
                break;
            case STATUS_PULL_REFRESH:
            case STATUS_RELEASE_REFRESH:
                break;
            case STATUS_REFRESHING:
                start = this.mTranslationY;
                end = HEIGHT_RELEASE;
                moveAnim(start, end);
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) mListener.onRefresh();
                    }
                }, refreshDelayMillis);
                break;
        }

        if (mHeaderView == null) return;
        mHeaderView.onStateChanged(this, overscrollTop, oldStatus, status);
    }


    /**
     * 设置刷新延迟时间
     *
     * @param refreshDelayMillis
     */
    public void setRefreshDelayMillis(long refreshDelayMillis) {
        this.refreshDelayMillis = refreshDelayMillis;
    }

    /**
     * 重置下拉刷新
     */
    public void reset() {
        this.setStatus(Status.STATUS_NONE, 0, false);
        this.setTargetOffsetTopAndBottom(0);
    }

    public void refresh() {
        this.setStatus(Status.STATUS_REFRESHING, 0, false);
        int start = 0;
        int end = HEIGHT_RELEASE;
        this.moveAnim(start, end);
    }

    /**
     * 完成刷新
     */
    public void completeRefresh() {
        this.setStatus(Status.STATUS_NONE, 0);
    }


    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }


    public interface OnRefreshListener {
        /**
         * 刷新回调
         */
        void onRefresh();
    }


    public interface OnChildScrollUpCallback {
        /**
         * Callback that will be called when {@link RefreshLayout#canChildScrollUp()} method
         * is called to allow the implementer to override its behavior.
         *
         * @param parent SwipeRefreshLayout that this callback is overriding.
         * @param child  The child view of SwipeRefreshLayout.
         * @return Whether it is possible for the child view of parent layout to scroll up.
         */
        boolean canChildScrollUp(@NonNull RefreshLayout parent, @Nullable View child);
    }

    public enum Status {
        STATUS_NONE,//初始状态
        STATUS_PULL_REFRESH,//开始下拉刷新
        STATUS_RELEASE_REFRESH,//释放刷新
        STATUS_REFRESHING//正在刷新
    }
}
