package com.tcj.sunshine.crop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.tcj.sunshine.crop.model.AspectRatio;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;


/**
 * 裁剪配置类
 */
public class Crop {

    private Activity activity;
    private Uri source;//需要裁剪的图片的uri
    private Uri target;//裁剪之后的保存图片uri
    private int outputMaxWidth;//裁剪输出最大宽
    private int outputMaxHeight;//裁剪输最大高
    private ArrayList<AspectRatio> aspectRatioList = new ArrayList<>();//支持的裁剪比例
    private int mode = MODE_CROP;//功能模式
    private String format;//输出格式


    private static final int MIN_SIZE = 10;
    public static final int SIZE_ORIGINAL = Integer.MIN_VALUE;

    public static final int MODE_CROP = 0x0000;//裁剪(默认)
    public static final int MODE_CROP_SCALE = 0x0001;//裁剪缩放
    public static final int MODE_CROP_ROTATE = 0x0002;//裁剪旋转
    public static final int MODE_ALL = 0x0003;//所有(裁剪,缩放,旋转）

    private static final String EXTRA_PREFIX = "com.tcj.sunshine.crop";

    //keys
    public static final String CROP_SOURCE_URI = EXTRA_PREFIX + ".SoruceURI";
    public static final String CROP_TARGET_URI = EXTRA_PREFIX + ".TargetUI";
    public static final String CROP_ASPECT_RATIO_ARRAY = EXTRA_PREFIX + ".AspectRatioArray";
    public static final String CROP_OUTPUT_MAX_WIDTH = EXTRA_PREFIX + ".OutputMaxWidth";
    public static final String CROP_OUTPUT_MAX_HEIGHT = EXTRA_PREFIX + ".OutputMaxHeight";
    public static final String CROP_MODE = EXTRA_PREFIX + ".Mode";
    public static final String CROP_OUTPUT_FORMAT = EXTRA_PREFIX + ".OutputFormat";

    /**
     * 裁剪之后回调keys
     */
    public static final String CROP_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".OutputImageWidth";
    public static final String CROP_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".OutputImageHeight";


    /**
     * 所有支持的裁剪比例
     */
    private static final float[][] supportAllAspectRatio = {
            {1, 1},
            {3, 4},
            {4, 3},
            {SIZE_ORIGINAL, SIZE_ORIGINAL},//原始比例
            {9, 16},
            {16, 9},
            {2, 3},
            {3, 2},
    };


    private Crop(Activity activity, @NonNull Uri source, @NonNull Uri target) {
        this.activity = activity;
        this.source = source;
        this.target = target;
    }

    /**
     * 开始裁剪
     */
    public void start(int REQUEST_CROP) {

        /**
         * 跳转裁剪activity
         */
        Intent intent = new Intent(activity, CropActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(CROP_SOURCE_URI, source);
        bundle.putParcelable(CROP_TARGET_URI, target);
        bundle.putParcelableArrayList(CROP_ASPECT_RATIO_ARRAY, aspectRatioList);

        if (this.outputMaxWidth < MIN_SIZE) {
            this.outputMaxWidth = MIN_SIZE;
        }

        if (this.outputMaxHeight < MIN_SIZE) {
            this.outputMaxHeight = MIN_SIZE;
        }

        bundle.putInt(CROP_OUTPUT_MAX_WIDTH, this.outputMaxWidth);
        bundle.putInt(CROP_OUTPUT_MAX_HEIGHT, this.outputMaxHeight);
        bundle.putInt(CROP_MODE, this.mode);
        bundle.putString(CROP_OUTPUT_FORMAT, this.format);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, REQUEST_CROP);
    }

    public static final class Builder {
        private Activity activity;
        private Uri source;//需要裁剪的图片的uri
        private Uri target;//裁剪之后的保存图片uri
        private int outputMaxWidth;//裁剪输出最大宽
        private int outputMaxHeight;//裁剪输出最大高
        private ArrayList<AspectRatio> aspectRatioList = new ArrayList<>();//支持的裁剪比例
        private int mode;//功能模式
        private String format;//输出格式


        public Builder (Activity activity){
            this.activity = activity;
        }


        public Builder setImageURI(Uri source, Uri target) {
            this.source = source;
            this.target = target;
            return this;
        }

        /**
         * 设置需要裁剪的图片uri,使用FileProvider
         * @param source 格式：content://
         * @return
         */
        public Builder setSourceURI(Uri source) {
            this.source = source;
            return this;
        }


        /**
         * 设置裁剪之后的图片uri
         * @param target 格式：file:// + file的绝对路径
         * @return
         */
        public Builder setTargetURI(Uri target) {
            this.target = target;
            return this;
        }


        /**
         * 添加裁剪支持的裁剪比例(最大支持比例个数：8个)
         * @param aspectRatioX
         * @param aspectRatioY
         * @return
         */
        public Builder addAspectRatio(float aspectRatioX, float aspectRatioY) {
            AspectRatio aspectRatio = new AspectRatio(aspectRatioX, aspectRatioY);
            //支持的个数不能超过所有比例的数量
            if(this.aspectRatioList.size() < supportAllAspectRatio.length) {
                this.aspectRatioList.add(aspectRatio);
            }
            return this;
        }

        /**
         * 支持所有裁剪比例
         * @return
         */
        public Builder setSupportAllAspectRatio(){
            this.aspectRatioList.clear();
            for (float[] ratio : supportAllAspectRatio) {
                AspectRatio aspectRatio = new AspectRatio(ratio[0], ratio[1]);
                this.aspectRatioList.add(aspectRatio);
            }
            return this;
        }

        /**
         * 设置功能模式
         * int NONE = 0;//裁剪
         * int SCALE = 1;//裁剪和缩放
         * int ROTATE = 2;//裁剪和旋转
         * int ALL = 3;//所有(裁剪,缩放,旋转)
         * @return
         */
        public Builder setMode(int mode) {
            this.mode = mode;
            return this;
        }


        /**
         * 设置裁剪输出格式
         * @param format
         * @return
         */
        public Builder setOutputFormat(Crop.FORMAT format) {
            this.format = format.name();
            return this;
        }


        /**
         * 设置裁剪的最大的图片宽高
         * @param outputMaxWidth
         * @param outputMaxHeight
         * @return
         */
        public Builder setOutputMaxSize(int outputMaxWidth, int outputMaxHeight) {
            this.outputMaxWidth = outputMaxWidth;
            this.outputMaxHeight = outputMaxHeight;
            return this;
        }

        public Crop build(){
            Crop crop = new Crop(this.activity, this.source, this.target);
            crop.aspectRatioList = this.aspectRatioList;
            crop.outputMaxWidth = this.outputMaxWidth;
            crop.outputMaxHeight = this.outputMaxHeight;
            crop.mode = this.mode;
            crop.format = this.format;
            return crop;
        }


    }


    @IntDef({MODE_CROP, MODE_CROP_SCALE, MODE_CROP_ROTATE, MODE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {

    }

    public enum FORMAT {
        PNG, JPEG
    }

}
