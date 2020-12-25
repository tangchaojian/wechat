package com.tcj.sunshine.player;

/**
 * 视频播放器模式
 */
public enum VideoMode {

    MODE_CENTER_CROP,//按比例缩放，超出parent截取中间

    MODE_CENTER_INSIDE,//按比例缩放，不超出parent布局,居中显示
}
