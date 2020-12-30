package com.readchen.fx.rtmpdemo;

import android.content.Context;

import com.tcj.sunshine.base.core.ActivityLifecycleManager;
import com.tcj.sunshine.base.dagger.component.DaggerRetrofitComponent;
import com.tcj.sunshine.base.dagger.module.RetrofitModule;
import com.tcj.sunshine.net.HttpHelper;
import com.tcj.sunshine.tools.ContextUtils;
import com.tcj.sunshine.tools.PreferenceUtils;
import com.tcj.sunshine.tools.ToastUtils;
import com.tcj.sunshine.view.core.BoxingUtils;
import com.tencent.rtmp.TXLiveBase;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import androidx.multidex.MultiDex;
import io.flutter.app.FlutterApplication;

/**
 * 基础Application，框架级的初始化在这里操作
 */
public class BaseApplication extends FlutterApplication {

    private static final String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/f9597e80d06113364ea645c5bd6972b4/TXUgcSDK.licence";
    private static final String ugcKey = "b9fe87252b53913adfeb43d327ff81fd";

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

        TXLiveBase.setConsoleEnabled(false);
        TXLiveBase.setAppID("1252463788");

        TXLiveBase.getInstance().setLicence(INSTANCE, ugcLicenceUrl, ugcKey);

        UMConfigure.init(this, "5feaf195adb42d582694122d", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
        android.util.Log.i("UMLog", "UMConfigure.init@MainApplication");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    public static BaseApplication getInstance() {
        return INSTANCE;
    }
}
