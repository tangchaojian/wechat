package com.tcj.sunshine.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.PermissionUtils;
import com.tcj.sunshine.tools.ScreenUtils;
import com.tcj.sunshine.tools.StatusBarUtils;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.ButterKnife;

/**
 * 基础Activity
 */
public abstract class BaseActivity extends RxAppCompatActivity {
    protected Context context = BaseActivity.this;
    protected View mContentView;
    protected Resources res;
    protected Handler handler = new Handler();
    //屏幕状态栏，导航栏状态改变，继续还原原来设置的全屏模式
    private ScreenRunnable mScreenRunnable = new ScreenRunnable();

    protected static final int MODE_FULL_SCREEN_NONE = 0;//不全屏，显示状态栏，导航栏
    protected static final int MODE_FULL_SCREEN_PART = 1;//部分全屏，隐藏状态栏，显示导航栏
    protected static final int MODE_FULL_SCREEN_ALL = 2;//完全全屏，隐藏状态栏，导航栏

    protected int mScreenMode = MODE_FULL_SCREEN_PART;//默认不全屏模式
    private Config config = new Config();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null){
            onHandleIntent(getIntent());
        }
        //初始化配置
        this.config(this.config);
        this.mScreenMode = this.config.mScreenMode;
        //设置显示的屏幕模式
        ScreenUtils.setFullScreen(this, mScreenMode);
        Window window = this.getWindow();
        window.getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener());
        if(mScreenMode != MODE_FULL_SCREEN_NONE) {
            StatusBarUtils.setImmersiveStatusBar(this, true);
        }
        this.mContentView = LayoutInflater.from(this).inflate(this.getLayoutResID(), null);
        this.setContentView(mContentView);
        ButterKnife.bind(this);

        this.res = this.getResources();
        this.initUI();
        this.mContentView.getViewTreeObserver().addOnGlobalLayoutListener(onKeyboardLayoutListener);
        LogUtils.i("sunshine-app", "屏幕适配之后：屏幕密度[" + ScreenUtils.getDensity() + "]");
    }

    protected void onHandleIntent(Intent intent) { }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()){
            onRelease();
            if(handler != null) {
                //移除所有Runnable和消息
                handler.removeCallbacksAndMessages(null);
            }
            EventBus.getDefault().unregister(this);
        }
    }

    //Activity销毁时候资源释放
    protected void onRelease() { }

    //返回ContentView的layoutId
    public abstract int getLayoutResID();


    /**
     * 初始化配置，在initUI之前的设置
     * @return
     */
    public void config(Config config){};

    //初始化控件
    public abstract void initUI();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //权限结果回调
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FragmentManager manager = this.getSupportFragmentManager();
        List<Fragment> list = manager.getFragments();
        if(list != null && !list.isEmpty()) {
            for (Fragment fragment : list) {
                if(fragment != null && fragment.isVisible()) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }


    //屏幕状态栏，底部导航栏状态改变监听
    private class OnSystemUiVisibilityChangeListener implements View.OnSystemUiVisibilityChangeListener {

        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                handler.removeCallbacks(mScreenRunnable);
                handler.postDelayed(mScreenRunnable, 1000);
            } else {
                LogUtils.i("sunshine-app", "全屏");
            }
        }
    }

    //屏幕模式还原Runnable
    private class ScreenRunnable implements Runnable {

        @Override
        public void run() {
            ScreenUtils.setFullScreen(BaseActivity.this, mScreenMode);
            if(mScreenMode != MODE_FULL_SCREEN_NONE) {
                StatusBarUtils.setImmersiveStatusBar(BaseActivity.this, true);
            }
        }
    }

    protected class Config{
        public int mScreenMode = MODE_FULL_SCREEN_PART;//默认值
    }

    public void onKeyboardChange(int visible) {

    }

    public boolean isShowSoftKeyboard = false;
    private ViewTreeObserver.OnGlobalLayoutListener onKeyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();

            mContentView.getWindowVisibleDisplayFrame(r);

            int heightDiff = ScreenUtils.getScreenHeight() - r.bottom;
            if (heightDiff > ScreenUtils.getScreenHeight() / 4.0f) {
                isShowSoftKeyboard = true;
                onKeyboardChange(View.VISIBLE);
            } else if(isShowSoftKeyboard){
                isShowSoftKeyboard = false;
                onKeyboardChange(View.GONE);
            }
        }
    };
}