package com.tcj.sunshine.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

/**
 * App工具类
 */
public class AppUtils {

    public static String APP_WX = "com.tencent.mm";//微信APP包名
    public static String APP_QQ = "com.tencent.mobileqq";//微信APP包名
    public static String APP_WEIBO = "com.sina.weibo";//微博APP包名
    public static String APP_ALIPAY = "com.eg.android.AlipayGphone";//支付宝APP包名

    private AppUtils() { }


    public static String getAppName(){
        return ContextUtils.getContext().getResources().getString(R.string.app_name);
    }

    /**
     * 取得app版本名称
     * @return
     */
    public static String getVersionName() {
        try {
            PackageManager manager = ContextUtils.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(ContextUtils.getContext().getPackageName(),0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得app版本号
     * @return
     */
    public static long getVersonCode() {
        try {
            PackageManager manager = ContextUtils.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(ContextUtils.getContext().getPackageName(),0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return info.getLongVersionCode();
            }else {
                return info.versionCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取包名
     * @return
     */
    public static String getPackageName(){
        return ContextUtils.getContext().getPackageName();
    }


    /**
     * 获取渠道名
     */
    public static String getChannel() {
        return "";
    }

    /**
     * App 是否处于前台
     * @return
     */
    public static boolean isAppForeground() {
        ActivityManager am = (ActivityManager) ContextUtils.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) return false;
        List<ActivityManager.RunningAppProcessInfo> info = am.getRunningAppProcesses();
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return aInfo.processName.equals(ContextUtils.getContext().getPackageName());
            }
        }
        return false;
    }


    /**
     * 判断是否安卓某个应用
     * @param packageName
     * @return
     */
    public static boolean isIntallApp(String packageName) {
        PackageManager packageManager = ContextUtils.getContext().getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            if (packages.get(i).packageName.equalsIgnoreCase(packageName)){
                return true;
            }
        }
        return false;
    }


    /**
     * 是否安装微信
     * @return
     */
    public static boolean isInstallWX() {
        return isIntallApp(APP_WX);
    }

    /**
     * 是否安装QQ
     * @return
     */
    public static boolean isInstallQQ() {
        return isIntallApp(APP_QQ);
    }

    /**
     * 是否安装微博
     * @return
     */
    public static boolean isInstallWeiBo() {
        return isIntallApp(APP_WEIBO);
    }

    /**
     * 是否安装支付宝
     * @return
     */
    public static boolean isInstallAliPay() {
        return isIntallApp(APP_ALIPAY);
    }

}
