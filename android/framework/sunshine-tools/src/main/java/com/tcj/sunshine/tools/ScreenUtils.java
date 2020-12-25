package com.tcj.sunshine.tools;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * 屏幕工具类
 */
public class ScreenUtils {

    public static float density = 0;
    public static float scaledDensity = 0;
    public static int densityDpi = 0;

    public static final int MODE_FULL_SCREEN_NONE = 0;//不全屏，显示状态栏，导航栏
    public static final int MODE_FULL_SCREEN_PART = 1;//部分全屏，隐藏状态栏，显示导航栏
    public static final int MODE_FULL_SCREEN_ALL = 2;//完全全屏，隐藏状态栏，导航栏

    private static final String TAG_STATUS_BAR = "TAG_STATUS_BAR";
    private static final String TAG_OFFSET     = "TAG_OFFSET";
    private static final int    KEY_OFFSET     = -123;

    //===========================屏幕================================

    public static DisplayMetrics getMetrics() {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            // 测试设备 1440*2960 --- SDK VERSION 24 --- 包含虚拟功能键
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                ((WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm); // 包含虚拟功能键高度 dm.heightPixels = 2960
            } else {
                ((WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm); // 不包含虚拟功能键高度 dm.heightPixels = 2768
            }

            if(density != 0 && scaledDensity != 0 && densityDpi != 0) {
                dm.density = density;
                dm.scaledDensity = scaledDensity;
                dm.densityDpi = densityDpi;
            }

            return dm;
        } catch (Exception e) {
            LogUtils.i("sunshine-error", "异常->" + e.getMessage());
        }
        return null;
    }

    /**
     * 屏幕宽
     *
     * @return
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    /**
     * 屏幕高
     *
     * @return
     */
    public static int getScreenHeight() {
        WindowManager wm = (WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.y;
    }

    /**
     * 获取屏幕密度
     *
     * @return
     */
    public static float getDensity() {
        DisplayMetrics metrics = getMetrics();
        if (metrics != null) {
            return metrics.density;
        }
        return 0;
    }

    /**
     * 获取屏幕密度
     *
     * @return
     */
    public static float getScaledDensity() {
        DisplayMetrics metrics = getMetrics();
        if (metrics != null) {
            return metrics.scaledDensity;
        }
        return 0;
    }

    /**
     * dip转px
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        final float scale = getDensity();
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = getScaledDensity();
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 设置全屏
     *
     * @param activity
     */
    public static void setFullScreen(@NonNull Activity activity, int mode) {
        Window window = activity.getWindow();
        ViewGroup mDecorView = (ViewGroup) window.getDecorView();
        WindowManager.LayoutParams attrs;

        switch (mode) {
            case MODE_FULL_SCREEN_NONE://不全屏，显示状态栏，导航栏
                mDecorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

                attrs = window.getAttributes();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    //允许window扩展到刘海区
                    attrs.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                }
                window.setAttributes(attrs);
                break;
            case MODE_FULL_SCREEN_PART://部分全屏，透明状态栏，显示导航栏
                mDecorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                //全屏
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ColorUtils.getColor(R.color.black_40_percent));
                }else {
                    setStatusBarColor(activity, ColorUtils.getColor(R.color.black_40_percent));
                }

                attrs = window.getAttributes();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    //允许window扩展到刘海区
                    attrs.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
                }
                window.setAttributes(attrs);
                break;
            case MODE_FULL_SCREEN_ALL://完全全屏，隐藏状态栏，导航栏
                mDecorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                //全屏
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                // 隐藏导航栏
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);



                attrs = window.getAttributes();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    //允许window扩展到刘海区
                    attrs.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
                }
                window.setAttributes(attrs);
                break;

        }
    }


    /**
     * 判断是否全屏
     *
     * @param activity
     */
    public static boolean isFullScreen(@NonNull Activity activity) {
        if ((activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN) {
            return true;
        }
        return false;
    }


    /**
     * 得到全屏模式
     * @param activity
     * @return
     */
    public static int getFullScreenMode(@NonNull Activity activity) {
        if ((activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN) {
            return MODE_FULL_SCREEN_ALL;
        }else if((activity.getWindow().getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN){
            return MODE_FULL_SCREEN_PART;
        }
        return MODE_FULL_SCREEN_NONE;
    }

    /**
     * 设置横屏
     *
     * @param activity
     */
    public static void setLandscape(@NonNull final Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 设置竖屏
     *
     * @param activity
     */
    public static void setPortrait(@NonNull final Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 是否横屏
     *
     * @return
     */
    public static boolean isLandscape() {
        return ContextUtils.getContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * 是否竖屏
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPortrait() {
        return ContextUtils.getContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 获取屏幕旋转角度
     *
     * @param activity
     * @return
     */
    public static int getScreenRotation(@NonNull final Activity activity) {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return 0;
        }
    }

    /**
     * 截屏
     *
     * @param activity
     * @return
     */
    public static Bitmap screenShot(@NonNull final Activity activity) {
        return screenShot(activity, false);
    }

    /**
     * 截屏是否去掉状态栏
     *
     * @param activity
     * @param isDeleteStatusBar 是否删除状态栏
     * @return
     */
    public static Bitmap screenShot(@NonNull final Activity activity, boolean isDeleteStatusBar) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.setWillNotCacheDrawing(false);
        Bitmap bmp = decorView.getDrawingCache();
        if (bmp == null) return null;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Bitmap ret;
        if (isDeleteStatusBar) {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = resources.getDimensionPixelSize(resourceId);
            ret = Bitmap.createBitmap(
                    bmp,
                    0,
                    statusBarHeight,
                    dm.widthPixels,
                    dm.heightPixels - statusBarHeight
            );
        } else {
            ret = Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels);
        }
        decorView.destroyDrawingCache();
        return ret;
    }


    //============================状态栏==========================================

    /**
     * 获取状态栏高度(单位px)
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 设置状态栏是否显示
     *
     * @param activity
     * @param isVisible
     */
    public static void setStatusBarVisibility(@NonNull final Activity activity, final boolean isVisible) {
        setStatusBarVisibility(activity.getWindow(), isVisible);
    }

    /**
     * 设置状态栏是否显示
     *
     * @param window
     * @param isVisible
     */
    public static void setStatusBarVisibility(@NonNull final Window window,
                                              final boolean isVisible) {
        if (isVisible) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            showStatusBarView(window);
            addMarginTopEqualStatusBarHeight(window);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideStatusBarView(window);
            subtractMarginTopEqualStatusBarHeight(window);
        }
    }

    /**
     * 状态栏是否显示
     *
     * @param activity
     */
    public static boolean isStatusBarVisible(@NonNull final Activity activity) {
        int flags = activity.getWindow().getAttributes().flags;
        return (flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0;
    }


    /**
     * 设置状态栏颜色
     * @param activity
     * @param color
     * @return
     */
    public static View setStatusBarColor(@NonNull final Activity activity, @ColorInt final int color) {
        return setStatusBarColor(activity, color, false);
    }

    /**
     * 设置状态栏颜色
     * @param activity
     * @param color
     * @param isDecor
     */
    public static View setStatusBarColor(@NonNull final Activity activity,
                                         @ColorInt final int color,
                                         final boolean isDecor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return null;
        transparentStatusBar(activity);
        return applyStatusBarColor(activity, color, isDecor);
    }


    private static void transparentStatusBar(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int vis = window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.getDecorView().setSystemUiVisibility(option | vis);
            } else {
                window.getDecorView().setSystemUiVisibility(option);
            }
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private static View applyStatusBarColor(final Activity activity,
                                            final int color,
                                            boolean isDecor) {
        ViewGroup parent = isDecor ?
                (ViewGroup) activity.getWindow().getDecorView() :
                (ViewGroup) activity.findViewById(android.R.id.content);
        View fakeStatusBarView = parent.findViewWithTag(TAG_STATUS_BAR);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            fakeStatusBarView = createStatusBarView(activity, color);
            parent.addView(fakeStatusBarView);
        }
        return fakeStatusBarView;
    }


    private static void hideStatusBarView(final Window window) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag("TAG_STATUS_BAR");
        if (fakeStatusBarView == null) return;
        fakeStatusBarView.setVisibility(View.GONE);
    }

    private static void showStatusBarView(final Window window) {
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View fakeStatusBarView = decorView.findViewWithTag("TAG_STATUS_BAR");
        if (fakeStatusBarView == null) return;
        fakeStatusBarView.setVisibility(View.VISIBLE);
    }

    private static void addMarginTopEqualStatusBarHeight(final Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        View withTag = window.getDecorView().findViewWithTag(KEY_OFFSET);
        if (withTag == null) return;
        addMarginTopEqualStatusBarHeight(withTag);
    }

    /**
     * Add the top margin size equals status bar's height for view.
     *
     * @param view The view.
     */
    private static void addMarginTopEqualStatusBarHeight(@NonNull View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        view.setTag(KEY_OFFSET);
        Object haveSetOffset = view.getTag(KEY_OFFSET);
        if (haveSetOffset != null && (Boolean) haveSetOffset) return;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin + getStatusBarHeight(),
                layoutParams.rightMargin,
                layoutParams.bottomMargin);
        view.setTag(KEY_OFFSET, true);
    }


    private static void subtractMarginTopEqualStatusBarHeight(final Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        View withTag = window.getDecorView().findViewWithTag(KEY_OFFSET);
        if (withTag == null) return;
        subtractMarginTopEqualStatusBarHeight(withTag);
    }

    private static void subtractMarginTopEqualStatusBarHeight(@NonNull View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        Object haveSetOffset = view.getTag(KEY_OFFSET);
        if (haveSetOffset == null || !(Boolean) haveSetOffset) return;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin,
                layoutParams.topMargin - getStatusBarHeight(),
                layoutParams.rightMargin,
                layoutParams.bottomMargin);
        view.setTag(KEY_OFFSET, false);
    }

    private static View createStatusBarView(final Activity activity,
                                            final int color) {
        View statusBarView = new View(activity);
        statusBarView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight()));
        statusBarView.setBackgroundColor(color);
        statusBarView.setTag(TAG_STATUS_BAR);
        return statusBarView;
    }

    //============================底部导航栏======================================


    public static void setNavBarVisibility(@NonNull final Activity activity, boolean isVisible) {
        setNavBarVisibility(activity.getWindow(), isVisible);
    }

    /**
     * 设置是否显示导航栏
     *
     * @param window
     * @param isVisible
     */
    public static void setNavBarVisibility(@NonNull final Window window, boolean isVisible) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        final ViewGroup decorView = (ViewGroup) window.getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = ContextUtils.getContext().getResources().getResourceEntryName(id);
                if ("navigationBarBackground".equals(resourceEntryName)) {
                    child.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
                }
            }
        }
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (isVisible) {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~uiOptions);
        } else {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | uiOptions);
        }
    }

    /**
     * 导航栏是否显示
     *
     * @param activity
     */
    public static boolean isNavBarVisible(@NonNull final Activity activity) {
        return isNavBarVisible(activity.getWindow());
    }

    /**
     * 导航栏是否显示
     *
     * @param window
     */
    public static boolean isNavBarVisible(@NonNull final Window window) {
        boolean isVisible = false;
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        for (int i = 0, count = decorView.getChildCount(); i < count; i++) {
            final View child = decorView.getChildAt(i);
            final int id = child.getId();
            if (id != View.NO_ID) {
                String resourceEntryName = ContextUtils.getContext().getResources().getResourceEntryName(id);
                if ("navigationBarBackground".equals(resourceEntryName)
                        && child.getVisibility() == View.VISIBLE) {
                    isVisible = true;
                    break;
                }
            }
        }
        if (isVisible) {
            int visibility = decorView.getSystemUiVisibility();
            isVisible = (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }
        return isVisible;
    }

    /**
     * 设置导航栏颜色
     *
     * @param activity
     * @param color
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setNavBarColor(@NonNull final Activity activity, @ColorInt final int color) {
        setNavBarColor(activity.getWindow(), color);
    }

    /**
     * 设置导航栏颜色
     *
     * @param window
     * @param color
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setNavBarColor(@NonNull final Window window, @ColorInt final int color) {
        window.setNavigationBarColor(color);
    }

    /**
     * 是否支持底部导航栏
     */
    public static boolean isSupportNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager wm = (WindowManager) ContextUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
            if (wm == null) return false;
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y || realSize.x != size.x;
        }
        boolean menu = ViewConfiguration.get(ContextUtils.getContext()).hasPermanentMenuKey();
        boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !menu && !back;
    }

    public static int getToolsBarHeight(){
        TypedValue typedValue = new TypedValue();
        if (ContextUtils.getContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, getMetrics());
        }
        return 0;
    }
}
