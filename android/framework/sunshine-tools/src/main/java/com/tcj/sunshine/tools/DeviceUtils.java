package com.tcj.sunshine.tools;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

/**
 * Created by Stefan Lau on 2019/11/15.
 */
public class DeviceUtils {

    private static String[] huaweiRongyao = {
            "hwH60",    //荣耀6
            "hwPE",     //荣耀6 plus
            "hwH30",    //3c
            "hwHol",    //3c畅玩版
            "hwG750",   //3x
            "hw7D",      //x1
            "hwChe2",      //x1
            "Y85A",      //x1
            "Y85",      //x1
    };

    private static String[] vivo = {
            "PD1728",    //vivio手机
    };


    /**
     * 是否是手机设备
     */
    public static boolean isPhone() {
        TelephonyManager tm = getTelephonyManager();
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     *
     */
    public static String getAndroidId(){
        String androidId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            androidId = Settings.System.getString(ContextUtils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return androidId;
    }
    /**
     * 获取设备ID(唯一识别码)
     * android 10 将会获取不到设备ID
     */
    @RequiresPermission(READ_PHONE_STATE)
    public static String getDeviceId() {
        TelephonyManager tm = getTelephonyManager();

        String deviceId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.System.getString(ContextUtils.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            if (ActivityCompat.checkSelfPermission(ContextUtils.getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                return tm.getDeviceId();
            }

        }
        return deviceId;
    }

    /**
     * 获取硬件序列号
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getSerial() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? Build.getSerial() : Build.SERIAL;
    }

    /**
     * 获取IMEI（国际移动设备识别码）
     * @return
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMEI() {
        TelephonyManager tm = getTelephonyManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.getImei();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Class clazz = tm.getClass();
                //noinspection unchecked
                Method getImeiMethod = clazz.getDeclaredMethod("getImei");
                getImeiMethod.setAccessible(true);
                String imei = (String) getImeiMethod.invoke(tm);
                if (imei != null) return imei;
            } catch (Exception e) {
                Log.e("PhoneUtils", "getIMEI: ", e);
            }
        }
        String imei = tm.getDeviceId();
        if (imei != null && imei.length() == 15) {
            return imei;
        }
        return "";
    }

    /**
     * 获取IMEI（国际移动设备识别码）
     * @return
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMEI(int slotId) {
        TelephonyManager tm = getTelephonyManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.getImei(slotId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Class clazz = tm.getClass();
                //noinspection unchecked
                Method getImeiMethod = clazz.getDeclaredMethod("getImei", int.class);
                getImeiMethod.setAccessible(true);
                String imei = (String) getImeiMethod.invoke(tm, slotId);
                if (imei != null) return imei;
            } catch (Exception e) {
                Log.e("PhoneUtils", "getIMEI: ", e);
            }
        }
        return getIMEI();
    }

    /**
     * 获取MEID(移动设备识别码)
     * @return
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getMEID() {
        TelephonyManager tm = getTelephonyManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.getMeid();
        }
        return tm.getDeviceId();
    }

    /**
     * 获取MEID(移动设备识别码)
     * @return
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getMEID(final int slotId) {
        TelephonyManager tm = getTelephonyManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.getMeid(slotId);
        }
        return getMEID();
    }

    /**
     * 获取IMSI(国际移动用户识别码)
     * @return
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getIMSI() {
        TelephonyManager tm = getTelephonyManager();
        return tm.getSubscriberId();
    }

    /**
     * 获取手机通信类型
     * PHONE_TYPE_NONE
     * PHONE_TYPE_GSM   全球移动通信系统(时分多址技术)--移动通信技术
     * PHONE_TYPE_CDMA  码分多址是指利用码序列相关性实现的多址通信  --联通通信技术
     * PHONE_TYPE_SIP   会话初始协议,SIP 是一种源于互联网的IP 语音会话控制协议
     */
    public static int getPhoneType() {
        TelephonyManager tm = getTelephonyManager();
        return tm.getPhoneType();
    }

    /**
     * 是否有sim卡
     */
    public static boolean isSimCardReady() {
        TelephonyManager tm = getTelephonyManager();
        return tm.getSimState() == TelephonyManager.SIM_STATE_READY;
    }

    /**
     * 获取注册的网络运营商的名字
     */
    public static String getSimOperatorName() {
        TelephonyManager tm = getTelephonyManager();
        return tm.getSimOperatorName();
    }


    /**
     * 获取运营商
     * @return
     */
    public static String getSimOperatorByMnc() {
        TelephonyManager tm = getTelephonyManager();
        String operator = tm.getSimOperator();
        if (operator == null) return "";
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
            case "46020":
                return "中国移动";
            case "46001":
            case "46006":
            case "46009":
                return "中国联通";
            case "46003":
            case "46005":
            case "46011":
                return "中国电信";
            default:
                return operator;
        }
    }


    /**
     * 获取手机状态信息
     * @return
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    public static String getPhoneStatus() {
        TelephonyManager tm = getTelephonyManager();
        String str = "";
        //noinspection ConstantConditions
        str += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
        str += "Line1Number = " + tm.getLine1Number() + "\n";
        str += "NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n";
        str += "NetworkOperator = " + tm.getNetworkOperator() + "\n";
        str += "NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n";
        str += "NetworkType = " + tm.getNetworkType() + "\n";
        str += "PhoneType = " + tm.getPhoneType() + "\n";
        str += "SimCountryIso = " + tm.getSimCountryIso() + "\n";
        str += "SimOperator = " + tm.getSimOperator() + "\n";
        str += "SimOperatorName = " + tm.getSimOperatorName() + "\n";
        str += "SimSerialNumber = " + tm.getSimSerialNumber() + "\n";
        str += "SimState = " + tm.getSimState() + "\n";
        str += "SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n";
        str += "VoiceMailNumber = " + tm.getVoiceMailNumber();
        return str;
    }


    /**
     * 直接发短信
     * @param phoneNumber
     * @param content
     */
    @RequiresPermission(SEND_SMS)
    public static void sendSmsSilent(final String phoneNumber, final String content) {
        if (TextUtils.isEmpty(content)) return;
        PendingIntent sentIntent = PendingIntent.getBroadcast(ContextUtils.getContext(), 0, new Intent("send"), 0);
        SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }

    private static TelephonyManager getTelephonyManager() {
        return (TelephonyManager) ContextUtils.getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    private static boolean isIntentAvailable(final Intent intent) {
        return ContextUtils.getContext()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    public static boolean isVivoX21() {
        int length = vivo.length;
        for (int i = 0; i < length; i++) {
            if (Build.DEVICE.trim().equalsIgnoreCase(vivo[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHuaWeiRongyao() {
        int length = huaweiRongyao.length;
        for (int i = 0; i < length; i++) {
            if (huaweiRongyao[i].equalsIgnoreCase(Build.DEVICE)) {
                return true;
            }
        }
        return false;
    }
}
