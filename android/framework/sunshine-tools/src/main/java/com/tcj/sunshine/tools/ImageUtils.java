package com.tcj.sunshine.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片压缩工具类
 */
public final class ImageUtils {

    static {
        System.loadLibrary("jpeg");
        System.loadLibrary("turbojpeg");
        System.loadLibrary("image-compress");
    }

    private List<Bitmap> bitmaps;
    private List<File> sourceFiles;
    private int maxWidth;
    private int maxHeight;
    private int quality;
    private String saveDirPath;
    private long maxFileSize;
    private boolean optimize;

    private ImageUtils(){}

    /**
     * 本地方法 JNI处理图片
     *
     * @param bitmap bitmap
     * @param quality 图片质量 100表示不变 越小就压缩越严重
     * @param targetFilePath 压缩之后的文件路径
     * @param optimize 是否采用哈弗曼表数据计算
     * @return "0"失败, "1"成功
     */
    private static native int compressBitmap(Bitmap bitmap, int quality, String targetFilePath, boolean optimize);


    public static Builder with(File sourceFile){
        Builder builder = new Builder();
        builder.setSourceFile(sourceFile);
        return builder;
    }

    public static Builder with(Bitmap bitmap){
        Builder builder = new Builder();
        builder.setSourceBitmap(bitmap);
        return builder;
    }

    public static Builder withFiles(List<File> sourceFiles){
        Builder builder = new Builder();
        builder.setSourceFiles(sourceFiles);
        return builder;
    }

    public static Builder withBitmaps(List<Bitmap> sourceBitmaps){
        Builder builder = new Builder();
        builder.setSourceBitmaps(sourceBitmaps);
        return builder;
    }

    /**
     * 同步压缩
     * @return
     */
    public static int compress(File sourceFile, int maxWidth, int maxHeight, int quality, File targetFile, long maxFileSize, boolean optimize){
        ImageUtils imageUtils = new ImageUtils();
        return imageUtils.startCompress(sourceFile, maxWidth, maxHeight, quality, targetFile, maxFileSize, optimize, null);
    }

    /**
     * 同步压缩
     * @return
     */
    public static int compress(Bitmap bitmap, int maxWidth, int maxHeight, int quality, File targetFile, long maxFileSize, boolean optimize){
        ImageUtils imageUtils = new ImageUtils();
        if(bitmap != null) {
            return imageUtils.startCompress(bitmap, maxWidth, maxHeight, quality, targetFile, maxFileSize, optimize, null);
        }
        return 0;
    }


    /**
     * 异步压缩，有回调
     * @param callback
     */
    public void compress(OnCompressCallback callback){
        CompressRunanble runanble = new CompressRunanble(bitmaps, sourceFiles, maxWidth, maxHeight, quality, saveDirPath, maxFileSize, optimize, callback);
        Thread thread = new Thread(runanble);
        thread.start();//开启线程压缩
    }

    private class CompressRunanble implements Runnable{

        private List<Bitmap> bitmaps = null;
        private List<File> sourceFiles = null;
        private int maxWidth;
        private int maxHeight;
        private int quality;
        private String saveDirPath;
        private long maxFileSize;
        private boolean optimize;
        private OnCompressCallback callback;
        private ImageHandler handler;

        public CompressRunanble(List<Bitmap> bitmaps, List<File> sourceFiles, int maxWidth, int maxHeight, int quality, String saveDirPath, long maxFileSize, boolean optimize, OnCompressCallback callback) {
            this.bitmaps = bitmaps;
            this.sourceFiles = sourceFiles;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.quality = quality;
            this.saveDirPath = saveDirPath;
            this.maxFileSize = maxFileSize;
            this.optimize = optimize;
            this.callback = callback;
            this.handler = new ImageHandler(Looper.getMainLooper(), this.callback);
        }

        @Override
        public void run() {
            ArrayList<File> successList = new ArrayList<>();
            Message msg = new Message();
            Bundle data = new Bundle();
            long startTimeMillis = System.currentTimeMillis();
            msg.setData(data);
            if(sourceFiles != null && !sourceFiles.isEmpty()){
                ArrayList<File> failList = new ArrayList<>();
                //循环压缩
                for (File sourceFile : sourceFiles) {
                    File targetFile = new File(saveDirPath, System.currentTimeMillis() + ".jpg");
                    int result = startCompress(sourceFile, this.maxWidth, this.maxHeight, this.quality, targetFile, this.maxFileSize, this.optimize, this.handler);
                    if(result == 1) {
                        successList.add(targetFile);
                    }else {
                        failList.add(sourceFile);
                    }
                }

                data.putSerializable("failList", failList);
            }else if(bitmaps != null && !bitmaps.isEmpty()) {
                //循环压缩
                ArrayList<Bitmap> failList = new ArrayList<>();
                for (Bitmap bitmap : bitmaps) {
                    File targetFile = new File(saveDirPath, System.currentTimeMillis() + ".jpg");
                    int result = startCompress(bitmap, this.maxWidth, this.maxHeight, this.quality, targetFile, this.maxFileSize, this.optimize, this.handler);
                    if(result == 1) {
                        successList.add(targetFile);
                    }else {
                        failList.add(bitmap);
                    }
                }

                data.putSerializable("failList", failList);
            }else {
                throw new RuntimeException("sourceFile 不能为空");
            }
            msg.what = 2;
            long endTimeMillis = System.currentTimeMillis();
            data.putSerializable("successList", successList);
            data.putLong("millis", endTimeMillis - startTimeMillis);
            handler.sendMessage(msg);
        }
    }

    /**
     * 开始压缩
     * sourceFile 和 bitmap只能有一个
     */
    private <T> int startCompress(T source, int maxWidth, int maxHeight,
                              int quality, File targetFile, long maxFileSize, boolean optimize, ImageHandler handler){
        boolean status = false;
        int width = 0;
        int height = 0;
        Bitmap bitmap = null;
        Bitmap compressBitmap = null;
        try {
            int originalWidth = 0;
            int originalHeight = 0;
            byte[] buffer = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            if(source instanceof File) {
                String fileName = ((File)source).getName();
                LogUtils.i("sunshine-ndk", "压缩文件名[" + fileName + "]" );

                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(((File)source).getAbsolutePath(), options);
                originalWidth = options.outWidth;
                originalHeight = options.outHeight;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(((File)source).getAbsolutePath(), options);
            }else {
                bitmap = (Bitmap)source;
                options.inJustDecodeBounds = true;
                buffer = BitmapUtils.bitmapToBytes(bitmap, Bitmap.CompressFormat.JPEG);
                BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
                originalWidth = options.outWidth;
                originalHeight = options.outHeight;
            }

            LogUtils.i("sunshine-ndk", "originalWidth[" + originalWidth + "]");
            LogUtils.i("sunshine-ndk", "originalHeight[" + originalHeight + "]");

            maxWidth = Math.min(originalWidth, maxWidth);//得到最小宽的值,注：最大宽不能大于原图宽
            maxHeight = Math.min(originalHeight, maxHeight);//得到最小高的值,注：最大高不能大于原图高

            //按图片原始比例缩小，得到图片将要压缩到的宽，高值
            int size[] = CalculateUtils.getOriginalRatioSize(originalWidth, originalHeight, maxWidth, maxHeight);
            width = size[0];//压缩到的宽度
            height = size[1];//压缩到的高度

            if(quality == 0) quality = 100;

            if(!targetFile.exists()) {
                targetFile.createNewFile();
            }

            int inSampleSize = (originalWidth / width) > 0 ? (originalWidth / width) : 1;
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;// 读取所有内容
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[32 * 1024];

            if(source instanceof File) {
                compressBitmap = BitmapFactory.decodeFile(((File)source).getAbsolutePath(), options);
            }else {
                compressBitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
            }

            String filePath = targetFile.getAbsolutePath();
            if(maxFileSize > 0) {
                //如果控制了目标压缩图片最大的值，需要反复压缩
                //第一次压缩
                int result = compressBitmap(compressBitmap, quality, filePath, optimize);
                if(result == 1){

                    long len = targetFile.length();
                    int qualityValue = quality - 3;
                    while (len > maxFileSize) {
                        //如果图片大于maxFileSize，循环压缩
//                        LogUtils.i("sunshine-ndk", "循环压缩, qualityValue[" + qualityValue + "]");
                        result = compressBitmap(compressBitmap, qualityValue, filePath, optimize);
                        if(result == 0) {
                            //压缩失败，则停止压缩
                            break;
                        }

                        len = targetFile.length();
//                        LogUtils.i("sunshine-ndk", "图片大小:" + len);
//                        LogUtils.i("sunshine-ndk", "maxFileSize大小:" + maxFileSize);
                        if(len <= maxFileSize) {
                            break;
                        }
                        qualityValue -= 3;
                    }
                    status = true;
                    LogUtils.i("sunshine-ndk", "压缩成功");
                }
            }else {
                int result = compressBitmap(compressBitmap, quality, filePath, optimize);
                if(result == 1) {
                    status = true;
                    LogUtils.i("sunshine-ndk", "压缩成功");
                }
            }
        } catch (Exception e) {

        } finally {
            if(compressBitmap != null) {
                compressBitmap.recycle();
            }

            if(status) {
                if(handler != null) {
                    //压缩成功
                    Bundle data = new Bundle();
                    data.putSerializable("targetFile", targetFile);
                    data.putInt("width", width);
                    data.putInt("height", height);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                return 1;
            }else {
                if(handler != null) {
                    //压缩失败
                    Bundle data = new Bundle();
                    if(source instanceof File) {
                        data.putSerializable("sourceFile", ((File)source));
                    }else {
                        data.putParcelable("bitmap", bitmap);
                    }
                    Message msg = new Message();
                    msg.what = 0;
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
                return 0;
            }
        }

    }


    class ImageHandler extends Handler {

        private OnCompressCallback callback;

        public ImageHandler(@NonNull Looper looper, OnCompressCallback callback) {
            super(looper);
            this.callback = callback;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            switch (msg.what) {
                case 0:
                    //压缩失败
                    if (data != null && callback != null) {
                        File sourceFile = (File) data.getSerializable("sourceFile");
                        Bitmap bitmap = data.getParcelable("bitmap");
                        if(sourceFile != null) {
                            callback.fail(sourceFile);
                        }else {
                            callback.fail(bitmap);
                        }

                    }
                    break;
                case 1:
                    if (data != null && callback != null) {
                        File targetFile = (File) data.getSerializable("targetFile");
                        int width = data.getInt("width");
                        int height = data.getInt("height");
                        callback.success(targetFile, width, height);
                    }
                    break;
                case 2:
                    if (data != null && callback != null) {
                        ArrayList<File> successList = (ArrayList<File>) data.getSerializable("successList");//加载成功列表
                        ArrayList failList = (ArrayList) data.getSerializable("failList");//加载失败列表
                        long millis = data.getLong("millis");//耗时
                        callback.complete(successList, failList, millis);
                    }
                    break;
            }
        }

    }


    public static class Builder {

        private static final int MAX_WIDTH = 800;
        private static final int MAX_HEIGHT = 800;

        private List<Bitmap> bitmaps = new ArrayList<>();
        private List<File> sourceFiles = new ArrayList<>();
        private int maxWidth;
        private int maxHeight;
        private int quality;
        private String saveDirPath;
        private boolean optimize = true;
        private long maxFileSize;//单位bit


        protected Builder setSourceFiles(List<File> sourceFiles){
            if(sourceFiles == null || sourceFiles.isEmpty()) {
                throw new RuntimeException("sourceFile 不能为空");
            }

            for (File sourceFile : sourceFiles) {
                if(sourceFile != null) {
                    String path = sourceFile.getAbsolutePath();
                    String suffix = StringUtils.getSuffix(path);
                    if(FileUtils.isImageFile(suffix)) {
                        //如果是图片文件
                        this.sourceFiles.add(sourceFile);
                    }else {
                        throw new RuntimeException("sourceFile 不是图片文件");
                    }
                }else {
                    throw new RuntimeException("sourceFile 不能为空");
                }
            }

            return this;
        }

        protected Builder setSourceFile(File sourceFile){
            if(sourceFile != null) {
                String path = sourceFile.getAbsolutePath();
                String suffix = StringUtils.getSuffix(path);
                if(FileUtils.isImageFile(suffix)) {
                    //如果是图片文件
                    this.sourceFiles.add(sourceFile);
                }else {
                    throw new RuntimeException("sourceFile 不是图片文件");
                }
            }else {
                throw new RuntimeException("sourceFile 不能为空");
            }

            return this;
        }

        protected Builder setSourceBitmap(Bitmap bitmap) {

            if(bitmap != null) {
                this.bitmaps.add(bitmap);
            }else {
                throw new RuntimeException("sourceFile 不能为空");
            }

            return this;
        }

        protected Builder setSourceBitmaps(List<Bitmap> bitmaps) {

            if(bitmaps == null || bitmaps.isEmpty()) {
                throw new RuntimeException("sourceFile 不能为空");
            }

            for (Bitmap bitmap : bitmaps) {
                if(bitmap != null) {
                    this.bitmaps.add(bitmap);
                }else {
                    throw new RuntimeException("sourceFile 不能为空");
                }
            }

            return this;
        }

        /**
         * 设置压缩图片的最大宽
         * @param maxWidth
         */
        public Builder setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }


        /**
         * 设置压缩图片的最大高
         * @param maxHeight
         */
        public Builder setMaxHeight(int maxHeight) {
            this.maxHeight = maxHeight;
            return this;
        }


        /**
         * 设置压缩质量
         * @param quality
         */
        public Builder setQuality(int quality) {
            this.quality = quality;
            return this;
        }


        /**
         * 设置压缩之后图片最大值，单位bit
         * @param maxFileSize
         */
        public Builder setMaxFileSize(long maxFileSize) {
            this.maxFileSize = maxFileSize;
            return this;
        }

        /**
         * 设置保存图片的路径
         * @param saveDirPath
         */
        public Builder setSaveDir(String saveDirPath) {
            if(StringUtils.isEmpty(saveDirPath)) {
                throw new RuntimeException("saveDirPath 不能为空");
            }

            //内部存储目录
            String InternalDirPath = "/data/data/" + AppUtils.getPackageName();
            //外部存储目录
            String externalDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            if(saveDirPath.startsWith(InternalDirPath)) {
                //内部存储不需要存储权限
                this.saveDirPath = saveDirPath;
            }else if(saveDirPath.startsWith(externalDirPath)) {
                if(PermissionUtils.hasPermission(new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE})){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        throw new RuntimeException("android Q 系统以上不支持这样存储");
                    }else {
                        this.saveDirPath = saveDirPath;
                    }
                }else {
                    throw new RuntimeException("没有存储写入权限[" + PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE + "]");
                }
            }else {
                throw new RuntimeException("saveDirPath[" + saveDirPath + "]为非法路径");
            }
            return this;
        }

        /**
         * 设置图片保存的目标文件
         * @param saveDir
         */
        public Builder setSaveDir(File saveDir) {

            if(saveDir == null) {
                throw new RuntimeException("saveDir 不能为空");
            }
            String saveDirPath = saveDir.getAbsolutePath();
            return setSaveDir(saveDirPath);
        }

        /**
         * 是否使用哈弗曼表数据计算
         * @param optimize
         */
        public Builder setOptimize(boolean optimize) {
            this.optimize = optimize;
            return this;
        }


        public ImageUtils build(){

            if(bitmaps.isEmpty() && sourceFiles.isEmpty()) {
                throw new RuntimeException("sourceFile 不能为空");
            }

            ImageUtils imageUtils = new ImageUtils();
            imageUtils.bitmaps = this.bitmaps;
            imageUtils.sourceFiles = this.sourceFiles;
            imageUtils.maxWidth = this.maxWidth != 0 ? this.maxWidth : MAX_WIDTH;
            imageUtils.maxHeight = this.maxHeight != 0 ? this.maxHeight : MAX_HEIGHT;
            imageUtils.quality = this.quality;
            if(StringUtils.isNoEmpty(this.saveDirPath)) {
                File dir = new File(this.saveDirPath);
                if(!dir.exists()) {
                    dir.mkdirs();
                }
                imageUtils.saveDirPath = this.saveDirPath;
            }else {
                //如果缓存目录没有设置，则为默认压缩缓存目录
                imageUtils.saveDirPath = FileUtils.getCacheDir(FileUtils.DIR_NAME_COMPRESS_CACHE).getAbsolutePath();
            }
            imageUtils.maxFileSize = this.maxFileSize;
            imageUtils.optimize = this.optimize;
            return imageUtils;
        }
    }

    public interface OnCompressCallback<T> {

        /**
         * 单个图片压缩成功
         * @param targetFile 保存的目标文件
         * @param width 压缩之后的宽
         * @param height 压缩之后的高
         */
        void success(File targetFile, int width, int height);

        /**
         * 压缩失败，返回源文件，或者源bitmap
         */
        void fail(T source);

        /**
         * 压缩完毕
         */
        void complete(List<File> successList, List<T> failList, long millis);
    }

}
