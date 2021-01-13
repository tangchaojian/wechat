package com.wechat.wechat

import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.aliyun.svideo.recorder.activity.AlivcSvideoRecordActivity
import com.aliyun.svideo.recorder.bean.AlivcRecordInputParam
import com.aliyun.svideosdk.common.struct.common.VideoQuality
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs
import com.readchen.fx.rtmpdemo.R
import com.tcj.sunshine.boxing.Boxing
import com.tcj.sunshine.boxing.model.config.BoxingConfig
import com.tcj.sunshine.tools.PermissionUtils
import com.tcj.sunshine.tools.PermissionUtils.PermissionsCallback
import com.tcj.sunshine.view.activity.BoxingActivity
import com.tencent.live.anchor.TCAnchorActivity
import com.tencent.live.audience.TCAudienceActivity
import com.tencent.live.common.utils.TCConstants
import com.tencent.live.login.TCUserMgr
import com.tencent.live.main.videolist.TCLiveListActivity
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
            }else if(call.method == "startAliyunRecord") {
                if(PermissionUtils.hasPermission(arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE))) {
                    startAliyunRecord()
                    result.success("ok");
                }else {
                    PermissionUtils.checkSelfPermission(activity, arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE), 101, PermissionsCallback {
                        requestCode, permissions, grantResults ->

                        if(requestCode == 101 && permissions.size == grantResults.size) {
                            startAliyunRecord()
                            result.success("ok");
                        }
                    })
                }

            }else if(call.method == "startTencentLive") {
                if(PermissionUtils.hasPermission(arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE))) {
                    startTencentLive()
                    result.success("ok");
                }else {
                    PermissionUtils.checkSelfPermission(activity, arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE), 102, PermissionsCallback {
                        requestCode, permissions, grantResults ->

                        if(requestCode == 102 && permissions.size == grantResults.size) {
                            startTencentLive()
                            result.success("ok");
                        }
                    })
                }

            }else if(call.method == "startLiveList") {
                if(PermissionUtils.hasPermission(arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE))) {
                    startLiveList()
                    result.success("ok");
                }else {
                    PermissionUtils.checkSelfPermission(activity, arrayOf(PermissionUtils.PERMISSION_CAMERA, PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE), 103, PermissionsCallback {
                        requestCode, permissions, grantResults ->

                        if(requestCode == 103 && permissions.size == grantResults.size) {
                            startLiveList()
                            result.success("ok");
                        }
                    })
                }

            }else if(call.method == "startLookLive") {
                startLookLive()
                result.success("ok");

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

    fun startAliyunRecord(){
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

    fun startTencentLive(){
        var intent = Intent()
        intent.setClass(activity, TCAnchorActivity::class.java)
        intent.putExtra(TCConstants.ROOM_TITLE, "亮健直播测试");
        intent.putExtra(TCConstants.USER_ID, TCUserMgr.getInstance().getUserId())
        intent.putExtra(TCConstants.USER_NICK, TCUserMgr.getInstance().getNickname())
        intent.putExtra(TCConstants.USER_HEADPIC, TCUserMgr.getInstance().getAvatar())
        intent.putExtra(TCConstants.COVER_PIC, TCUserMgr.getInstance().getCoverPic())
        intent.putExtra(TCConstants.USER_LOC,"广州")
        activity.startActivity(intent)
    }

    fun startLiveList(){
        var intent = Intent()
        intent.setClass(activity, TCLiveListActivity::class.java)
        activity.startActivity(intent)
    }

    fun startLookLive(){

        var intent = Intent()
        intent.setClass(activity, TCAudienceActivity::class.java)
        intent.putExtra(TCConstants.PUSHER_ID, "199090")
        intent.putExtra(TCConstants.GROUP_ID, "10010")
        intent.putExtra(TCConstants.PUSHER_NAME, "唐小唐")
        intent.putExtra(TCConstants.PUSHER_AVATAR, "https://huyaimg.msstatic.com/cdnimage/actprop/20114_1__45_1609922333.jpg")
        intent.putExtra(TCConstants.HEART_COUNT, "100")
        intent.putExtra(TCConstants.MEMBER_COUNT, "10")
        intent.putExtra(TCConstants.ROOM_TITLE, "亮健直播测试")
        activity.startActivity(intent)
    }


}