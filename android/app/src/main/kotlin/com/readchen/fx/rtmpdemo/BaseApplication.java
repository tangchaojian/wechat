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
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.live.TCGlobalConfig;
import com.tencent.live.common.report.TCELKReportMgr;
import com.tencent.live.common.utils.TCConstants;
import com.tencent.live.liveroom.MLVBLiveRoomImpl;
import com.tencent.live.login.TCUserMgr;
import com.tencent.rtmp.TXLiveBase;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import androidx.multidex.MultiDex;
import io.flutter.app.FlutterApplication;

/**
 * 基础Application，框架级的初始化在这里操作
 */
public class BaseApplication extends FlutterApplication {

    /**
     * bugly 组件的 AppId
     *
     * bugly sdk 系腾讯提供用于 APP Crash 收集和分析的组件。
     */
    public static final String BUGLY_APPID = "1400012894";

    private static final String TAG = "TCApplication";

    public static BaseApplication INSTANCE;


    public static BaseApplication getInstance() {
        return INSTANCE;
    }

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

        // 必须：初始化 LiteAVSDK Licence。 用于直播推流鉴权。
        TXLiveBase.getInstance().setLicence(this, TCGlobalConfig.LICENCE_URL, TCGlobalConfig.LICENCE_KEY);

        // 必须：初始化 MLVB 组件
        MLVBLiveRoomImpl.sharedInstance(this);

        // 必须：初始化全局的 用户信息管理类，记录个人信息。
        TCUserMgr.getInstance().initContext(getApplicationContext());

        // 可选：初始化 bugly crash上报系统。
        initBuglyCrashReportSDK();

        // 可选：初始化小直播上报组件
        initXZBAppELKReport();

        UMConfigure.init(this, "5feaf195adb42d582694122d", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        UMConfigure.setLogEnabled(true);
        android.util.Log.i("UMLog", "UMConfigure.init@MainApplication");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
    }

    /**
     * 初始化 bugly crash 组件：用于上报小直播的 crash。
     */
    private void initBuglyCrashReportSDK() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppVersion(TXLiveBase.getSDKVersionStr());
        // 若您需要使用的话，请将 BUGLY_APPID 替换为您的 appid，否则会出现无法上报的问题。
        CrashReport.initCrashReport(getApplicationContext(), BUGLY_APPID, true, strategy);
    }

    /**
     *
     * 初始化 ELK 数据上报：仅仅适用于数据收集上报，您可以不关注；或者将相关代码删除。
     */
    private void initXZBAppELKReport() {
        TCELKReportMgr.getInstance().init(this);
        TCELKReportMgr.getInstance().registerActivityCallback(this);
        TCELKReportMgr.getInstance().reportELK(TCConstants.ELK_ACTION_START_UP, TCUserMgr.getInstance().getUserId(), 0, "启动成功", null);
    }

}
