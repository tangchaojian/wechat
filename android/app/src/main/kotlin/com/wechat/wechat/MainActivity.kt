package com.wechat.wechat

import android.os.Bundle
import com.tcj.sunshine.tools.PermissionUtils
import io.flutter.app.FlutterActivity


class MainActivity: FlutterActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlutterToAndroidPlugins.registerWith(this);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
