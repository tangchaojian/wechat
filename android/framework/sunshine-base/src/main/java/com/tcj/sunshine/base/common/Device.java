package com.tcj.sunshine.base.common;

import com.tcj.sunshine.tools.DeviceUtils;
import com.tcj.sunshine.tools.LogUtils;
import com.tcj.sunshine.tools.NetUtils;

/**
 * 设备
 */
public class Device {

    /**
     * 设备id
     */
    private static String deviceId = "";

    /**
     * 安卓id（android 8以上才有）
     */
    private static String androidId = "";

    /**
     * 手机通信类型
     */
    public static final int PHONE_TYPE = DeviceUtils.getPhoneType();


    /**
     * 网络运营商的公司全称
     */
    public static final String SIM_OPERATOR_COMPANY_NAME = DeviceUtils.getSimOperatorName();


    /**
     * 网络运营商的名字(如：中国移动，中国联通，中国电信)
     */
    public static final String SIM_OPERATOR_NAME = DeviceUtils.getSimOperatorByMnc();

    /**
     * 是否挂载SIM卡
     */
    public static final boolean IS_MOUNT_SIM = DeviceUtils.isSimCardReady();

    /**
     * IP地址
     */
    public static final String IP = NetUtils.getIpAddress();

    /**
     * MAC地址
     */
    public static final String MAC = NetUtils.getMacAddress();

    /**
     * 网络名称(WIFI，3G，4G，或者5G等等)
     */
    public static final String NET_NAME = NetUtils.getNetworkName();

    public static void test(){
        LogUtils.i("sunshine-app", "Device信息:[\n"
                + "设备id:" + deviceId + "\n"
                + "安卓id:" + androidId + "\n"
                + "手机通信类型:" + PHONE_TYPE + "\n"
                + "网络运营商公司:" + SIM_OPERATOR_COMPANY_NAME + "\n"
                + "网络运营商:" + SIM_OPERATOR_NAME + "\n"
                + "是否挂载SIM卡:" + IS_MOUNT_SIM + "\n"
                + "IP地址:" + IP + "\n"
                + "MAC地址:" + MAC + "\n"
                + "网络名称:" + NET_NAME + "\n"
                + "]");
    }

}
