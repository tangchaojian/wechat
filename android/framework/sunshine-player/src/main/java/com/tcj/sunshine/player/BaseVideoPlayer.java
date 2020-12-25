package com.tcj.sunshine.player;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.view.View;

import androidx.annotation.DrawableRes;

import com.tcj.sunshine.tools.ActivityUtils;
import com.tcj.sunshine.tools.LogUtils;

import java.io.IOException;
import java.util.Map;

/**
 * 播放器接口
 */
public interface BaseVideoPlayer {

    /**
     * 设置播放地址
     */
    public void setPath(String path);

    /**
     * 设置播放地址
     */
    public void setPath(String path, Map<String, String> header);

    /**
     * 获得播放地址
     * @return
     */
    public String getPath();



    /**
     * 开始加载视频
     */
    public void start();

    /**
     * 继续播放
     */
    public void play();

    /**
     * 重新播放
     */
    public void restart();


    /**
     * 释放播放器资源
     */
    public void release();

    /**
     * 暂停播放
     */
    public void pause();


    /**
     * 停止播放
     */
    public void stop();

    /**
     * 重置
     */
    public void reset();

    /**
     * 是否循环播放
     * @param isLooping
     */
    public void setLooping(boolean isLooping);

    /**
     * 改变播放进度
     * @param progress
     */
    public void seekTo(int progress);


    /**
     * 视频总时长
     * @return
     */
    public long getDuration();

    /**
     * 当前播放时长
     * @return
     */
    public long getCurrentPosition();

    /**
     * 当前进度
     * @return
     */
    public int getProgress();

    /**
     * 最大进度
     * @return
     */
    public int getMaxProgress();


    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying();

    /**
     * 是否自动播放
     * @param isAutoPlay
     */
    public void setAutoPlay(boolean isAutoPlay);

    /**
     * 是否可以准备播放了
     * @return
     */
    public boolean isPrepared();

    /**
     * 设置是否全屏
     * @param isFull
     */
    public void setFullScreen(boolean isFull);


    /**
     * 重新播放icon
     * @param resId
     */
    public void setPlayIcon(@DrawableRes int resId);


    /**
     * 重新播放icon
     * @param resId
     */
    public void setPauseIcon(@DrawableRes int resId);

    /**
     * 重新播放icon
     * @param resId
     */
    public void setRePlayIcon(@DrawableRes int resId);

    /**
     * 设置播放/暂停/重播图片大小
     * @param width
     * @param height
     */
    public void setHandleIconSize(int width, int height);

    /**
     * 是否显示全屏icon
     * @param isVisible
     */
    public void setFullScreenIconVisible(boolean isVisible);

    /**
     * 设置窗口切换监听
     * @param mListener
     */
    public void setOnSwitchWindowListener(VideoPlayer.OnSwitchWindowListener mListener);

    /**
     * 设置窗口切换监听
     * @param mListener
     */
    public void setOnProgressListener(VideoPlayer.OnProgressListener mListener);

}
