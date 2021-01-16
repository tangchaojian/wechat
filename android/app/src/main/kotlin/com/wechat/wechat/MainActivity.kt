package com.wechat.wechat

import android.os.Bundle
import android.util.Log
import androidx.annotation.NonNull
import com.tcj.sunshine.tools.PermissionUtils
import com.tencent.live.login.TCUserMgr
import io.flutter.app.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity: FlutterActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlutterToAndroidPlugins.registerWith(this);

        TCUserMgr.getInstance().login("199096", "一蓑烟雨任平生",object : TCUserMgr.Callback {
            override fun onSuccess() {}
            override fun onFailed(code: Int, msg: String) {}
        })
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
