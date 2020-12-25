package com.wechat.wechat;

import android.content.Context;

import com.tcj.sunshine.base.core.ActivityLifecycleManager;
import com.tcj.sunshine.base.dagger.component.DaggerRetrofitComponent;
import com.tcj.sunshine.base.dagger.module.RetrofitModule;
import com.tcj.sunshine.net.HttpHelper;
import com.tcj.sunshine.tools.ContextUtils;
import com.tcj.sunshine.tools.PreferenceUtils;
import com.tcj.sunshine.tools.ToastUtils;
import com.tcj.sunshine.view.core.BoxingUtils;

import androidx.multidex.MultiDex;
import io.flutter.app.FlutterApplication;

/**
 * 基础Application，框架级的初始化在这里操作
 */
public class BaseApplication extends FlutterApplication {

    public static BaseApplication INSTANCE;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        //这个必须第一个初始化,因为很多地方的context都是用这里的
        ContextUtils.init(this);
        DaggerRetrofitComponent.builder()
                .retrofitModule(new RetrofitModule())
                .build()
                .inject(HttpHelper.getInstance());

        /**
         * 测试依赖注入
         */
        HttpHelper.getInstance().test();
        //初始化
        ToastUtils.getInstance().init(this);                               //土司初始化
        PreferenceUtils.getInstance().init(this);                          //SharedPreferences存储初始化
        BoxingUtils.init();                                                        //相册初始化
        ActivityLifecycleManager.getInstance().init(this);              //Activity管理初始化         //腾讯x5初始化
    }

    public static BaseApplication getInstance() {
        return INSTANCE;
    }
}
