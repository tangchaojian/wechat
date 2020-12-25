package com.wechat.wechat

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Bundle
import android.util.Log
import com.aliyun.svideo.recorder.activity.AlivcSvideoRecordActivity
import com.aliyun.svideo.recorder.bean.AlivcRecordInputParam
import com.aliyun.svideosdk.common.struct.common.VideoQuality
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs
import com.tcj.sunshine.boxing.Boxing
import com.tcj.sunshine.boxing.model.config.BoxingConfig
import com.tcj.sunshine.tools.PermissionUtils
import com.tcj.sunshine.tools.PermissionUtils.PermissionsCallback
import com.tcj.sunshine.view.activity.BoxingActivity
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


class FlutterToAndroidPlugins : MethodChannel.MethodCallHandler {

    lateinit var activity: FlutterActivity;

    private constructor(activity: FlutterActivity) {
        this.activity = activity;
    }

    companion object {

        private var instance: FlutterToAndroidPlugins? =null
        private var channel: MethodChannel? = null

        val CHANNELS_FLUTTER_TO_NATIVE = "com.wechant.app.plugins/flutter_to_native";

        fun registerWith(activity: FlutterActivity){
            if(instance == null) {
                Log.i("TAG", "注册插件")
                instance = FlutterToAndroidPlugins(activity);
                channel = MethodChannel(activity.registrarFor(CHANNELS_FLUTTER_TO_NATIVE).messenger(), CHANNELS_FLUTTER_TO_NATIVE);
                channel!!.setMethodCallHandler(instance);
            }
        }
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        try {
            if(call.method == "openAlbum") {
                var count = call.arguments as Int;
                if(PermissionUtils.hasPermission(arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE))) {
                    openAlbum(count)
                    result.success("ok");
                }else {
                    PermissionUtils.checkSelfPermission(activity, arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE), 100, PermissionsCallback {
                        requestCode, permissions, grantResults ->

                        if(requestCode == 100 && permissions.size == grantResults.size) {
                            openAlbum(count)
                            result.success("ok");
                        }
                    })
                }
            }else if(call.method == "startRecord") {
                if(PermissionUtils.hasPermission(arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE))) {
                    startRecord()
                    result.success("ok");
                }else {
                    PermissionUtils.checkSelfPermission(activity, arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE), 101, PermissionsCallback {
                        requestCode, permissions, grantResults ->

                        if(requestCode == 101 && permissions.size == grantResults.size) {
                            startRecord()
                            result.success("ok");
                        }
                    })
                }

            }
        }catch (e : Exception) {
            e.printStackTrace();
            result.success("原生调用失败：${e.message}")
        }

    }

    fun openAlbum(count: Int) {
        var config = BoxingConfig(BoxingConfig.Mode.MULTI_IMG)
        config.needCamera(R.mipmap.icon_album_camera).withMaxCount(count) // 支持gif，相机，设置最大选图数
        Boxing.of(config).withIntent(activity, BoxingActivity::class.java).start(activity, 1000)
    }

    fun startRecord(){
        var recordInputParam: AlivcRecordInputParam = AlivcRecordInputParam.Builder()
                .setResolutionMode(AlivcRecordInputParam.RESOLUTION_720P)
                .setRatioMode(AlivcRecordInputParam.RATIO_MODE_3_4)
                .setMaxDuration(AlivcRecordInputParam.DEFAULT_VALUE_MAX_DURATION)
                .setMinDuration(AlivcRecordInputParam.DEFAULT_VALUE_MIN_DURATION)
                .setVideoQuality(VideoQuality.HD)
                .setGop(30)
                .setVideoCodec(VideoCodecs.H264_HARDWARE)
                .build()

        AlivcSvideoRecordActivity.startRecord(activity, recordInputParam);
    }
}