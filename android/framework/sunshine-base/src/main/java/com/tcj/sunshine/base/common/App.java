package com.tcj.sunshine.base.common;

import com.tcj.sunshine.base.BuildConfig;
import com.tcj.sunshine.tools.AppUtils;
import com.tcj.sunshine.tools.DeviceUtils;
import com.tcj.sunshine.tools.NetUtils;

/**
 * App
 */
public class App {

    /**
     * App名称
     */
    public static final String APP_NAME = AppUtils.getAppName();

    /**
     * 版本名称
     */
    public static final String VERSION_NAME = AppUtils.getVersionName();

    /**
     * 版本号
     */
    public static final Long VERSION_CODE = AppUtils.getVersonCode();

    /**
     * 包名
     */
    public static final String PACKAGE_NAME = AppUtils.getPackageName();

    /**
     * 渠道名
     */
    public static final String CHANNEL = AppUtils.getChannel();


}
