package com.tcj.sunshine.base.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * Created by Stefan Lau on 2020/1/1 0001.
 */
public class ActivityLifecycleManager implements Application.ActivityLifecycleCallbacks {

    private static ActivityLifecycleManager instance;
    private LinkedList<Activity> activityList = new LinkedList<>();
    private Application application;
    private int mForegroundCount = 0;
    private int  mConfigCount = 0;
    private boolean isForeground = true;//是否在前台

    private ActivityLifecycleManager(){}

    public static ActivityLifecycleManager getInstance(){

        if(instance == null) {
            instance = new ActivityLifecycleManager();
        }
        return instance;
    }

    public void init(Application application) {
        LogUtils.i("sunshine-app", "屏幕适配之前：屏幕密度[" + ScreenUtils.getDensity() + "]");

        if(application != null) {
            this.application = application;
            this.application.registerActivityLifecycleCallbacks(instance);
        }else {
            throw new RuntimeException("Application 不能为空");
        }
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
//        //屏幕适配
        ScreenAdaptManager.setCustomDensity(activity, application);
        this.setTopActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        ScreenAdaptManager.setCustomDensity(activity, application);
        if (isForeground) {
            setTopActivity(activity);
        }

        if (mConfigCount < 0) {
            ++mConfigCount;
        } else {
            ++mForegroundCount;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (!isForeground) {
            isForeground = true;
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        //isChangingConfigurations()返回值为true，则说明该Activity正在被销毁然后重新创建一个新的
        if (activity.isChangingConfigurations()) {
            --mConfigCount;
        } else {
            --mForegroundCount;
            if (mForegroundCount <= 0) {
                isForeground = false;
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        activityList.remove(activity);
        //解决软键盘内存泄漏问题
//        this.fixInputMethodManagerLeaks(activity);
    }


    private void setTopActivity(Activity activity) {
        if (activityList.contains(activity)) {
            if (!activityList.getLast().equals(activity)) {
                activityList.remove(activity);
                activityList.addLast(activity);
            }
        } else {
            activityList.addLast(activity);
        }
    }



    /**
     * 解决软键盘输入InputMethodManager内存泄漏问题(注:Leaks 泄漏意思)
     * @param activity
     */
    private void fixInputMethodManagerLeaks(final Activity activity) {

        try {
            if (activity == null) return;
            Context context = application.getApplicationContext();
            if(context == null) return;

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) return;

            String[] leakViews = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
            for (String leakView : leakViews) {
                try {
                    Field leakViewField = InputMethodManager.class.getDeclaredField(leakView);
                    if (leakViewField == null) continue;
                    if (!leakViewField.isAccessible()) {
                        leakViewField.setAccessible(true);
                    }
                    Object obj = leakViewField.get(imm);
                    if (!(obj instanceof View)) continue;
                    View view = (View) obj;
                    if (view.getRootView() == activity.getWindow().getDecorView().getRootView()) {
                        leakViewField.set(imm, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取最上面的Activity
     * @return
     */
    public Activity getTopActivity() {
        if (!activityList.isEmpty()) {
            final Activity topActivity = activityList.getLast();
            if (topActivity != null) {
                return topActivity;
            }
        }
        return null;
    }

    public boolean isForeground(){
        return this.isForeground;
    }


    /**
     * 关掉在栈中这个Activity上面所有的Activity
     */
    public void closeAllForAboveThisActivity(String activityName) {
        boolean begun = false;
        for (Activity activity : activityList) {
            String clazzName = activity.getClass().getName();

            if (!begun && clazzName.equals(activityName)) {
                begun = true;//开始关闭之后的所有Activity
                continue;
            }

            if(begun) {
                //关掉Activity
                activity.finish();
            }
        }
    }

    /**
     * 是否存在某个Activity
     */
    public boolean isExistActivity(String activityName){
        for (Activity activity : activityList) {
            if(activity != null ) {
                String clazzName = activity.getClass().getName();
                if (clazzName.equals(activityName)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 退出系统
     */
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    /**
     * 关闭某activity页面
     * @param activityName
     */
    public void closeActivity(String activityName) {
        for (Activity activity : activityList) {
            String clazzName = activity.getClass().getName();
            if (clazzName.equals(activityName)) {
                activity.finish();
            }
        }
    }
}
