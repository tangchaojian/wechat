package com.tcj.sunshine.base.common;

import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.ScreenUtils;

/**
 *  屏幕
 */
public class Screen {

    /**
     * 屏幕宽
     */
    public final static int WIDTH = ScreenUtils.getScreenWidth();

    /**
     * 屏幕高(不是总高度，而是有效高度，可以显示控件的面积)
     */
    public final static int HEIGHT = ScreenUtils.getScreenHeight();

    /**
     * 状态栏高度
     */
    public static int STATUS_BAR_HEIGHT = ScreenUtils.getStatusBarHeight();

    /**
     * 导航栏高度
     */
    public static int NAV_BAR_HEIGHT = ScreenUtils.getStatusBarHeight();

    public static void test(){
        LogUtils.i("sunshine-app", "Device信息:[\n"
                + "屏幕宽:" + WIDTH + "\n"
                + "屏幕高:" + HEIGHT + "\n"
                + "状态栏高度:" + STATUS_BAR_HEIGHT + "\n"
                + "导航栏高度:" + NAV_BAR_HEIGHT + "\n"
                + "]");
    }
}
