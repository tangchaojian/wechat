package com.tcj.sunshine.tools;

import android.app.Application;
import android.content.Context;

/**
 * Created by Stefan Lau on 2019/11/11.
 */
public class ContextUtils {

    /**
     * Application 的Context
     */
    private static Context context;

    private ContextUtils(){}

    public static void init(Application application) {
        context = application.getApplicationContext();
        LogUtils.i("sunshine-exception", "context是否为空[" + (context != null) + "]");
    }

    public static Context getContext() {
        return context;
    }
}
