package com.tcj.sunshine.tools;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/**
 * 危险权限
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 0	CALENDAR	        READ_CALENDAR                       允许程序读取用户的日程信息
 *                          WRITE_CALENDAR                      允许程序写入日程，但不可读取
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 1	CAMERA	            CAMERA                              允许程序访问摄像头进行拍照
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 2	CONTACTS	        READ_CONTACTS                       允许程序访问联系人通讯录信息
 *                          WRITE_CONTACTS                      写入联系人,但不可读取
 *                          GET_ACCOUNTS                        允许程序访问账户Gmail列表
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 3	LOCATION	        ACCESS_FINE_LOCATION                允许程序通过GPS芯片接收卫星的定位信息
 *                          ACCESS_COARSE_LOCATION              允许程序通过WiFi或移动基站的方式获取用户错略的经纬度信息
 * ----------------------------------------------------------------------------------------------
 * 4	MICROPHONE	        RECORD_AUDIO                        允许程序录制声音通过手机或耳机的麦克
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 5	PHONE	            READ_PHONE_STATE                    允许程序访问电话状态
 *                          CALL_PHONE                          允许程序从非系统拨号器里拨打电话
 *                          READ_CALL_LOG                       读取通话记录
 *                          WRITE_CALL_LOG                      允许程序写入（但是不能读）用户的联系人数据
 *                          ADD_VOICEMAIL                       允许一个应用程序添加语音邮件系统
 *                          USE_SIP                             允许程序使用SIP视频服务
 *                          PROCESS_OUTGOING_CALLS              允许程序监视，修改或放弃播出电话
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 6	SENSORS         	BODY_SENSORS                        传感器权限
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 7	SMS	                SEND_SMS                            允许程序发送短信
 *                          RECEIVE_SMS                         允许程序接收短信
 *                          READ_SMS                            允许程序读取短信内容
 *                          RECEIVE_WAP_PUSH                    允许程序接收WAP PUSH信息
 *                          RECEIVE_MMS                         允许程序接收彩信
 * -----------------------------------------------------------------------------------------------------------------------------------------
 * 8	STORAGE	            READ_EXTERNAL_STORAGE               程序可以读取设备外部存储空间
 *                          WRITE_EXTERNAL_STORAGE              程序可以写入设备外部存储空间(如果有了该权限，不用申请READ_EXTERNAL_STORAGE)
 * -----------------------------------------------------------------------------------------------------------------------------------------
 */


/**
 * 权限工具类
 */
public class PermissionUtils {

    /**
     * 程序可以写入设备外部存储空间(如果有了该权限，不用申请READ_EXTERNAL_STORAGE)
     */
    public static final String  PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * 允许程序访问电话状态
     */
    public static final String  PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;

    /**
     * 允许程序访问摄像头进行拍照
     */
    public static final String  PERMISSION_CAMERA = Manifest.permission.CAMERA;

    /**
     * 允许程序访问联系人通讯录信息
     */
    public static final String  PERMISSION_READ_CONTACTS = Manifest.permission.READ_CONTACTS;

    /**
     * 允许程序从非系统拨号器里拨打电话
     */
    public static final String  PERMISSION_CALL_PHONE = Manifest.permission.CALL_PHONE;

    /**
     * 允许程序通过GPS芯片接收卫星的定位信息
     */
    public static final String  PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    /**
     * 允许程序通过WiFi或移动基站的方式获取用户错略的经纬度信息
     */
    public static final String  PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    /**
     * 允许程序录制声音通过手机或耳机的麦克
     */
    public static final String  PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;

    /**
     * 传感器权限
     */
    public static final String  PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS;

    private static SparseArray<PermissionsCallback> callbacks = new SparseArray<>();


    /**
     * 检测权限
     * @return
     */
    public static void checkSelfPermission(@NonNull Activity activity,  @NonNull String[] permissions, int requestCode, PermissionsCallback callback) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ArrayList<String> list = new ArrayList<>();
                if(permissions != null && permissions.length > 0) {
                    for (String permission : permissions) {
                        if (ContextCompat.checkSelfPermission(activity , permission) != PackageManager.PERMISSION_GRANTED) {
                            list.add(permission);
                        }
                    }
                }
                if (list.size() > 0) {
                    callbacks.put(requestCode,callback);
                    ActivityCompat.requestPermissions(activity, list.toArray(new String[list.size()]), requestCode);
                }else {
                    callback.callback(requestCode, permissions, new int[permissions.length]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("sunshine-error", "异常:" + e.getMessage());
        }
    }


    /**
     * 判断是否有权限
     * @return
     */
    public static boolean hasPermission(@NonNull String[] permissions) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ArrayList<String> list = new ArrayList<>();
                if(permissions != null && permissions.length > 0) {
                    for (String permission : permissions) {
                        if (ContextCompat.checkSelfPermission(ContextUtils.getContext() , permission) != PackageManager.PERMISSION_GRANTED) {
                            list.add(permission);
                        }
                    }
                }
                if (list.size() > 0) {
                    return false;
                }else {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("sunshine-error", "异常:" + e.getMessage());
        }

        return true;
    }


    /**
     * 权限请求回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsCallback permissionsCallback = callbacks.get(requestCode);
        if(permissionsCallback != null) {
            callbacks.remove(requestCode);
            permissionsCallback.callback(requestCode, permissions, grantResults);
        }
    }

    /**
     * 检测是否全部授权
     * @param grantResults
     * @return
     */
    public static boolean checkGranted(int[] grantResults) {
        boolean allowed = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allowed = false;
                break;
            }
        }
        return allowed;
    }


    /**
     * 回调接口
     */
    public interface PermissionsCallback{

        void callback(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    }

}
