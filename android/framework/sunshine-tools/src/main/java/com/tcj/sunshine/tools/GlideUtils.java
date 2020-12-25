package com.tcj.sunshine.tools;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.tcj.sunshine.lib.common.LoadCallback;

import java.io.File;
import java.security.MessageDigest;

/**
 * Created by Stefan Lau on 2019/11/22.
 */
public class GlideUtils {

    public static RequestOptions getRequestOptions(boolean isCache) {
        RequestOptions DOWNLOAD_ONLY_OPTIONS =
                RequestOptions.diskCacheStrategyOf(isCache ? DiskCacheStrategy.ALL : DiskCacheStrategy.NONE).priority(Priority.LOW)
                        .skipMemoryCache(false).centerCrop();
        return DOWNLOAD_ONLY_OPTIONS;
    }

    /**
     * 加载本地资源文件
     * @param resId
     * @param mImageView
     */
    public static void loadImage(int resId, ImageView mImageView) {
        loadImage(resId, 0, 0, mImageView, 0, 0, null);
    }

    //==============================前两个参数一样的 ===============================
    public static void loadImage(String url, ImageView mImageView) {
        loadImage(url, mImageView, 0);
    }

    public static void loadImage(String url, ImageView mImageView, @DrawableRes int placeResId) {
        loadImage(url, 0, 0, mImageView, placeResId, 0, null);
    }

    /**
     * 加载本地资源文件
     * @param context
     * @param resId
     * @param imageView
     */
    public static void loadImage(Context context, int resId, ImageView imageView) {
        Glide.with(context).load(resId).apply(getRequestOptions(true)).into(imageView);
    }

    public static void loadImage(Object model,                            //url
                                 int width,                             //缓存图片的宽
                                 int height,                            //缓存图片的高
                                 ImageView mImageView,
                                 @DrawableRes int placeResId,            //加载显示的图片
                                 @DrawableRes int failResId,            //失败显示的图片
                                 LoadCallback callback) {

        RequestManager manager = getRequestManager(mImageView);

        /**
         * 默认的策略是DiskCacheStrategy.AUTOMATIC
         * DiskCacheStrategy有五个常量：
         * DiskCacheStrategy.ALL 使用DATA和RESOURCE缓存远程数据，仅使用RESOURCE来缓存本地数据。
         * DiskCacheStrategy.NONE 不使用磁盘缓存
         * DiskCacheStrategy.DATA 在资源解码前就将原始数据写入磁盘缓存
         * DiskCacheStrategy.RESOURCE 在资源解码后将数据写入磁盘缓存，即经过缩放等转换后的图片资源。
         * DiskCacheStrategy.AUTOMATIC 根据原始图片数据和资源编码策略来自动选择磁盘缓存策略。
         */
        RequestOptions options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL).skipMemoryCache(false);            //不跳过缓存，优先从缓存中取

        if (width > 0 && height > 0) {
            options.override(width, height);
        }

        if (placeResId != 0) {
            options.placeholder(placeResId);
        }

        if (failResId != 0) {
            options.error(failResId);
        }

        RequestBuilder builder = null;

        if(model instanceof String) {
            String url = (String) model;
            String suffix = StringUtils.getSuffix(url);
            if("gif".equals(suffix)) {
                builder = manager.asGif().addListener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        LogUtils.i("sunshine-tools", "onLoadFailed");

                        if (callback != null) {
                            callback.fail(e.getMessage());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {

                        if (callback != null) {
                            callback.success(mImageView, url, resource);
                        }
                        return false;
                    }
                });
            }else {
                builder = manager.asBitmap().addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        LogUtils.i("sunshine-tools", "onLoadFailed");

                        if (callback != null) {
                            callback.fail(e.getMessage());
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {

                        if (callback != null) {
                            callback.success(mImageView, url, resource);
                        }
                        return false;
                    }
                });
            }
        }else if(model instanceof Integer || model instanceof byte[]) {
            builder = manager.asDrawable();
        }else if(model instanceof File) {
            builder = manager.asFile();
        }else {
            builder = manager.asDrawable();
        }

        if(builder != null) {
            builder.load(model).apply(options).into(mImageView);
        }
    }

    private static RequestManager getRequestManager(ImageView mImageView) {
        RequestManager manager = null;
        try {
            if (mImageView != null) {
                Activity activity = ActivityUtils.getActivityByView(mImageView);
                if (activity != null) {
                    manager = Glide.with(activity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (manager != null) {
                return manager;
            } else {
                return Glide.with(ContextUtils.getContext());
            }
        }
    }
}
