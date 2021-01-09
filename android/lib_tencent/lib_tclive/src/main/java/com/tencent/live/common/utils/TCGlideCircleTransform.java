package com.tencent.live.common.utils;

import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

import androidx.annotation.NonNull;

/**
 * Module:   TCGlideCircleTransform
 *
 * Function: Glide图像裁剪
 *
 */
public class TCGlideCircleTransform extends BitmapTransformation {

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return TCUtils.createCircleImage(toTransform, 0);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}
