package com.wechat.wechat

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import com.example.umeng_sdk.UmengSdkPlugin
import com.tcj.sunshine.tools.PermissionUtils
import com.umeng.analytics.MobclickAgent
import io.flutter.app.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity: FlutterActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlutterToAndroidPlugins.registerWith(this);
    }

    fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine?) {
        GeneratedPluginRegistrant.registerWith(flutterEngine!!)
        com.example.umeng_sdk.UmengSdkPlugin.setContext(this)
        Log.i("UMLog", "onCreate@MainActivity")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
