package com.tcj.sunshine.tools;

import android.text.TextUtils;
import android.util.Log;

import com.tcj.sunshine.lib.BuildConfig;

/**
 * 日志工具
 */
public class LogUtils {
    public static boolean locked = BuildConfig.LOCKED;

    /**
     * info日志
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (locked) {
            //截取打印长度日志打印
            subStrPrint(0, tag, msg);
        }
    }

    /**
     * verbose 日志
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (locked) {
            //截取打印长度日志打印
            subStrPrint(1, tag, msg);
        }
    }

    /**
     * 警告日志
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (locked) {
            //截取打印长度日志打印
            subStrPrint(2, tag, msg);
        }
    }

    /**
     * 调试日志
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (locked) {
            //截取打印长度日志打印
            subStrPrint(3, tag, msg);
        }
    }

    /**
     * 错误日志
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (locked) {
            //截取打印长度日志打印
            subStrPrint(4, tag, msg);
        }
    }

    /**
     * 截取打印日志
     * @param level 0:Log.i(); 1:Log.v(), 2:Log.w(), 3:Log.d();4:Log.e()
     * @param tag
     * @param msg
     */
    private static void subStrPrint(int level, String tag, String msg) {

        if(TextUtils.isEmpty(msg))return;

        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int maxLength = 2001 - tag.length();
        //大于4000时
        while (msg.length() > maxLength) {
            print(level, tag, msg.substring(0, maxLength));
            msg = msg.substring(maxLength);
        }
        //剩余部分
        print(level, tag, msg);
    }

    private static void print(int level, String tag, String msg) {
        switch (level) {
            case 0:
                Log.i(tag, msg);
                break;
            case 1:
                Log.v(tag, msg);
                break;
            case 2:
                Log.w(tag, msg);
                break;
            case 3:
                Log.d(tag, msg);
                break;
            case 4:
                Log.e(tag, msg);
                break;
        }
    }
}
