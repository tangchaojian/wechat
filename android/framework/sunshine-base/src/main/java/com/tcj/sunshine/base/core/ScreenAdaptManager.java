package com.tcj.sunshine.base.core;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import com.tcj.sunshine.lib.BuildConfig;
import com.tcj.sunshine.tools.ScreenUtils;

/**
 * 头条屏幕适配方案
 */
public class ScreenAdaptManager {
    // 系统的Density
    private static float sNoncompatDensity;
    // 系统的ScaledDensity
    private static float sNoncompatScaledDensity;

    public static void setCustomDensity(Activity activity, Application application) {
        DisplayMetrics displayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = displayMetrics.density;
            sNoncompatScaledDensity = displayMetrics.scaledDensity;
            // 监听在系统设置中切换字体
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        // 此处以360dp的设计图作为例子
        //在sunshine-lib/build.gradle中配置
        float targetDensity = displayMetrics.widthPixels / BuildConfig.SCREEN_WIDTH_DP;
        float targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        int targetDensityDpi = (int) (160 * targetDensity);
        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = targetDensityDpi;

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;

        ScreenUtils.density = targetDensity;
        ScreenUtils.scaledDensity = targetScaledDensity;
        ScreenUtils.densityDpi = targetDensityDpi;
    }
}
