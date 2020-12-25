package com.tcj.sunshine.lib.video;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 封面图实体类
 */
public class CoverEntity implements Serializable {

    public File file;//封面图文件
    public ArrayList<Bitmap> bitmaps;//封面图Bitmap
    public int width;//视频宽
    public int height;//视频高
    public long size;//封面图文件大小
    public int videoWidth;//视频宽
    public int videoHeight;//视频高
    public long videoDuration;//视频时长
}