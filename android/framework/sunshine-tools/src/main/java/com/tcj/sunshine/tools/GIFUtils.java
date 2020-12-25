package com.tcj.sunshine.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefan Lau on 2019/12/27.
 */
public class GIFUtils {

    static {
        System.loadLibrary("gifutils");
    }


    private String saveFilePath;

    private int width;
    private int height;
    private int colorSpace;
    private int quality;
    private long delayed;

    private List<File> sourceFiles;
    private List<Bitmap> sourceBitmaps;

    /**
     * 添加动画帧
     * @param pixels pixels array from bitmap
     * @return 是否成功.
     */
    private native static int addFrame(int[] pixels);

    /**
     * Gifflen init
     *
     * @param saveFilePath    Gif 图片的保存路径
     * @param width   Gif 图片的宽度.
     * @param height  Gif 图片的高度.
     * @param color   Gif 图片的色彩空间(0-255)
     * @param quality 进行色彩量化时的quality参数.0-100
     * @param delayed   相邻的两帧之间的时间间隔.
     * @return 如果返回值不是0, 就代表着执行失败.
     */
    private native static int init(String saveFilePath, int width, int height, int color, int quality, long delayed);

    /**
     * * native层做一些释放资源的操作.
     */
    private native static void close();


    /**
     * 设置gif保存的文件路径
     * @param saveFilePath
     * @return
     */
    public static Builder with(String saveFilePath) {
        Builder builder = new Builder();
        builder.setSaveFile(saveFilePath);
        return builder;
    }


    /**
     * 设置保存gif文件
     * @param saveFile 保存文件
     * @return
     */
    public static Builder with(File saveFile) {
        Builder builder = new Builder();
        builder.setSaveFile(saveFile);
        return builder;
    }


    /**
     * 开启线程，生成GIF图文件
     * @param callback 回调接口
     */
    public void createGifFile(OnGifCallback callback) {
        GifRunnable runnable = new GifRunnable(saveFilePath, width, height, colorSpace, quality , delayed, sourceFiles, sourceBitmaps, callback);
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * 开始进行Gif生成
     *
     * @param width  宽度
     * @param height 高度
     * @param saveFilePath   Gif保存的路径
     * @param sourceFiles  传入的每一帧图片的File对象
     * @param sourceBitmaps  传入的每一帧图片的Bitmap对象
     * @return 是否成功
     */
    public static boolean createGifFile(String saveFilePath, int width, int height,
                                  int colorSpace, int quality, long delayed,
                                  List<File> sourceFiles , List<Bitmap> sourceBitmaps) {
        try {

            int state;
            int[] pixels = new int[width * height];
            File saveFile = new File(saveFilePath);
            if(!saveFile.exists()) {
                saveFile.createNewFile();
            }
            state = init(saveFilePath, width, height, colorSpace, quality, delayed);
            if (state != 0) {
                // 失败
                return false;
            }

            List<Bitmap> bitmaps = new ArrayList<>();
            if(sourceFiles != null) {
                for (File file : sourceFiles) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    bitmaps.add(bitmap);
                }
            }else {
                bitmaps.addAll(sourceBitmaps);
            }

            for (Bitmap bitmap : bitmaps) {

                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                bitmap.getPixels(pixels, 0, width, 0, 0, bitmap.getWidth(), bitmap.getHeight());
                addFrame(pixels);
                bitmap.recycle();
            }
            close();
            return true;

        }catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private class GifRunnable implements Runnable {

        private String saveFilePath;

        private int width;
        private int height;
        private int colorSpace;
        private int quality;
        private long delayed;

        private List<File> sourceFiles;
        private List<Bitmap> sourceBitmaps;
        private OnGifCallback callback;
        private Handler handler;
        private boolean result = false;

        public GifRunnable(String saveFilePath, int width, int height, int colorSpace, int quality, long delayed, List<File> sourceFiles, List<Bitmap> sourceBitmaps, OnGifCallback callback) {
            this.saveFilePath = saveFilePath;
            this.width = width;
            this.height = height;
            this.colorSpace = colorSpace;
            this.quality = quality;
            this.delayed = delayed;
            this.sourceFiles = sourceFiles;
            this.sourceBitmaps = sourceBitmaps;
            this.callback = callback;
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void run() {
            try {
                this.result = createGifFile(saveFilePath, width, height, colorSpace, quality, delayed, sourceFiles, sourceBitmaps);
            }catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(callback != null){
                    this.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.callback(result, new File(saveFilePath), width, height);
                        }
                    });
                }

            }
        }
    }


    public static class Builder {
        private String saveFilePath;

        private int width;
        private int height;
        private int colorSpace;
        private int quality;
        private long delayed;

        private List<File> sourceFiles = new ArrayList<>();
        private List<Bitmap> sourceBitmaps = new ArrayList<>();


        /**
         * 设置保存文件路径
         * @param saveFilePath
         * @return
         */
        private Builder setSaveFile(String saveFilePath) {
            if(!TextUtils.isEmpty(saveFilePath)) {
                this.saveFilePath = saveFilePath;
            }else {
                throw new RuntimeException("saveFilePath不能为空");
            }
            return this;
        }


        /**
         * 设置保存文件
         * @param saveFile
         * @return
         */
        private Builder setSaveFile(File saveFile) {
            if(saveFile != null) {
                String path = saveFile.getAbsolutePath();
                String suffix = StringUtils.getSuffix(path);
                if(FileUtils.isImageFile(suffix)) {
                    //如果是图片文件
                    this.saveFilePath = saveFile.getAbsolutePath();
                }else {
                    throw new RuntimeException("sourceFile 不是图片文件");
                }

                this.saveFilePath = saveFile.getAbsolutePath();
            }else {
                throw new RuntimeException("saveFile不能为空");
            }
            return this;
        }


        /**
         * GIF图片大小尺寸
         * @param width
         * @param height
         * @return
         */
        public Builder setSize(int width, int height) {

            if(width <= 0) {
                throw new RuntimeException("width不能小于等于0");
            }

            if(height <= 0) {
                throw new RuntimeException("height不能小于等于0");
            }

            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * 色彩空间
         * @param colorSpace 范围0-100
         * @return
         */
        public Builder setColorSpace(int colorSpace) {
            if(colorSpace >= 0 && colorSpace <= 100) {
                this.colorSpace = colorSpace;
            }else {
                throw new RuntimeException("colorSpace的值不在范围");
            }
            return this;
        }


        /**
         * 进行色彩量化时的quality参数.
         * @param quality 范围0-100
         * @return
         */
        public Builder setQuality(int quality) {
            if(colorSpace > 0 && colorSpace <= 100) {
                this.quality = quality;
            }else {
                throw new RuntimeException("quality的值不在范围");
            }
            return this;

        }


        /**
         * 设置每一帧的延迟
         * @param delayed
         * @return
         */
        public Builder setDelayed(long delayed) {
            if(delayed > 0) {
                this.delayed = delayed;
            }else {
                throw new RuntimeException("delayed的值不在范围");
            }
            return this;
        }

        public Builder setSourceBitmaps(List<Bitmap> bitmaps) {
            if(bitmaps != null && !bitmaps.isEmpty()) {
                this.sourceFiles = null;
                this.sourceBitmaps = bitmaps;
            }else {
                throw new RuntimeException("bitmaps不能为空");
            }

            return this;
        }

        public Builder setSourceFiles(List<File> files) {
            if(files != null && !files.isEmpty()) {
                this.sourceFiles = files;
            }else {
                throw new RuntimeException("files不能为空");
            }

            return this;
        }

        public GIFUtils build(){
            GIFUtils gifUtils = new GIFUtils();
            gifUtils.saveFilePath = this.saveFilePath;
            gifUtils.width = this.width;
            gifUtils.height = this.height;
            gifUtils.colorSpace = this.colorSpace;
            gifUtils.quality = this.quality;
            gifUtils.delayed = this.delayed;
            gifUtils.sourceBitmaps = this.sourceBitmaps;
            gifUtils.sourceFiles = this.sourceFiles;
            return gifUtils;
        }


    }

    public interface OnGifCallback{
        void callback(boolean result, File targetFile, int width, int height);
    }
}
