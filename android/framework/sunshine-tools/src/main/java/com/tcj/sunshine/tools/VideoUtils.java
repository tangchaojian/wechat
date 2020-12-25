package com.tcj.sunshine.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.tcj.sunshine.lib.video.CoverEntity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 视频工具类
 */
public class VideoUtils {

    private static final long DEFAULT_TIMEOUT_US = 10000;
    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;

    private static final float TIME_FRAME_INTERVAL_MS = 41.666f;//电影一般1秒24帧，也就是说1帧41.666毫秒(计算公式:1 * 1000 / 24)

    private VideoUtils() { }

    public static VideoInfo getVideoInfo(String url) {
        MediaMetadataRetriever retriever = null;
        try {
            retriever = new android.media.MediaMetadataRetriever();
            if (!TextUtils.isEmpty(url)) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                retriever.setDataSource(url, headers);
            }

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高
            String date = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);//高
            return new VideoInfo(width, height, duration, date);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (retriever != null) retriever.release();
        }

        return null;
    }

    public static class VideoInfo {
        String width;
        String height;
        String duration;
        String date;

        public VideoInfo(String width, String height, String duration, String date) {
            this.width = width;
            this.height = height;
            this.duration = duration;
            this.date = date;
        }
    }


    /**
     * 获取视频第一帧图片
     * @param url               视频url
     * @param callBack          回调
     */
    public static void getFirstFrameImage(String url, OnVideoCallBack callBack){
        VideoFrameRunnable runnable = new VideoFrameRunnable(url, 1, 0, FrameMode.MODE_FRAME_START, callBack);
        Thread thread = new Thread(runnable);
        thread.start();
    }


    /**
     * 生成Gif封面图
     * @param url               视频url
     * @param mMaxCount         取几张图片生成GIF
     * @param delayed           Gif几秒1帧
     * @param mode              取前面mMaxCount帧，还是取全视频mMaxCount帧
     * @param callBack          回调
     */
    public static void getGifCorverImage(String url, int mMaxCount, long delayed, FrameMode mode, OnVideoCallBack callBack) {
        VideoFrameRunnable runnable = new VideoFrameRunnable(url, mMaxCount, delayed, mode, callBack);
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private static class VideoFrameRunnable implements Runnable {

        private String url;
        private int mMaxCount;
        private long delayed;
        private FrameMode mode;
        private OnVideoCallBack callBack;

        public VideoFrameRunnable(String url, int mMaxCount, long delayed, FrameMode mode, OnVideoCallBack callBack) {
            this.url = url;
            this.mMaxCount = mMaxCount;
            this.delayed = delayed;
            this.mode = mode;
            this.callBack = callBack;
        }

        @Override
        public void run() {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                createVideoFrameImageSDK21(url, mMaxCount, delayed, mode, callBack);
            }else {
                createVideoFrameImageSDK19(url, mMaxCount, delayed, mode, callBack);
            }
        }
    }


    /**
     * 生成视频帧图片，SDK >= 21
     * @param url
     * @param mMaxCount
     * @param callBack
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void createVideoFrameImageSDK21(String url, int mMaxCount, long delayed, FrameMode mode,OnVideoCallBack callBack){
        long startTimeMillis1 = System.currentTimeMillis();
        MediaCodec mediaCodec = null;
        MediaExtractor extractor = null;

        CoverEntity cover = new CoverEntity();

        try{
            extractor = new MediaExtractor();
            extractor.setDataSource(url);
            int trakCount = extractor.getTrackCount();
            Log.i("DEMO-VIDEO", "trakCount->" + trakCount);
            int trackIndex = -1;
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = extractor.getTrackFormat(i);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                String frameDuration = mediaFormat.getString(MediaFormat.KEY_I_FRAME_INTERVAL);
                Log.i("DEMO-VIDEO", "frameDuration->" + frameDuration);
                Log.i("DEMO-VIDEO", "mime->" + mime);
                //找到视频轨道
                if(mime.startsWith("video/")) {
                    trackIndex = i;
                    break;
                }
            }

            if (trackIndex >= 0) {
                MediaFormat mediaFormat = extractor.getTrackFormat(trackIndex);
                int width = mediaFormat.getInteger(MediaFormat.KEY_WIDTH);
                int height = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT);
                long time = mediaFormat.getLong(MediaFormat.KEY_DURATION);//单位微妙
                long mTimeCountMs = (long)(time / 1000f);//单位毫秒
                //设置轨道

                Log.i("sunshine-tools", "视频宽高[" + width + "," + height + "] 时长[" + mTimeCountMs + "ms]");

                long mFrameCount = (long) (mTimeCountMs / TIME_FRAME_INTERVAL_MS);//大概会有多少帧(一帧表示一张图,但数量大概相等)
                Log.i("sunshine-tools", "一共有[" + mFrameCount + "]帧");
//
                if(mFrameCount > 0) {
                    extractor.selectTrack(trackIndex);
                    //得到一个视频解码器
                    mediaCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
                    mediaCodec.configure(mediaFormat, null, null, 0);
                    mediaCodec.start();

                    ArrayList<Bitmap> bitmaps = getVideoFrameImageSDK21(mediaCodec, extractor, cover, mMaxCount, mTimeCountMs, mFrameCount, mode);
                    long endTimeMillis1 = System.currentTimeMillis();

                    LogUtils.i("sunshine-tools", "取视频帧耗时:" + (endTimeMillis1 - startTimeMillis1) + "毫秒");

                    if(bitmaps != null) {
                        File targetFile = null;
                        String dirPath = FileUtils.getCacheDir(FileUtils.DIR_NAME_TEMP_CACHE).getAbsolutePath();
                        //生成GIF动图文件
                        if(bitmaps.size() > 1) {
                            String fileName = StringUtils.getMD5(url.getBytes(), 16) + ".gif";
                            targetFile = FileUtils.getCacheFile(dirPath, fileName);
                            LogUtils.i("sunshine-tools", "bitmap的数量:" + bitmaps.size());

                            long startTimeMillis2 = System.currentTimeMillis();
                            GIFUtils.createGifFile(targetFile.getAbsolutePath(),width, height, 64, 100, delayed, null, bitmaps);
                            long endTimeMillis2 = System.currentTimeMillis();
                            LogUtils.i("sunshine-tools", "生成GIF耗时:" + (endTimeMillis2 - startTimeMillis2) + "毫秒");
                        }else {
                            String fileName = StringUtils.getMD5(url.getBytes(), 16) + ".jpg";
                            targetFile = FileUtils.getCacheFile(dirPath, fileName);
                            FileOutputStream fos = new FileOutputStream(targetFile);
                            byte[] buffer = BitmapUtils.bitmapToBytes(bitmaps.get(0), Bitmap.CompressFormat.JPEG);
                            fos.write(buffer);
                            fos.flush();
                            fos.close();
                        }
                        cover.file = targetFile;
                        cover.bitmaps = bitmaps;
                    }

                    cover.videoWidth = width;
                    cover.videoWidth = height;
                    cover.videoDuration = mTimeCountMs;
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(()-> callBack.callback(cover));

            if (mediaCodec != null) {
                mediaCodec.stop();
                mediaCodec.release();
            }
            if (extractor != null) {
                extractor.release();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static ArrayList<Bitmap> getVideoFrameImageSDK21(MediaCodec mediaCodec, MediaExtractor extractor, CoverEntity cover, int mMaxCount, long mCountTimeMs, long mFrameCount, FrameMode mode) {

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        try {
            int sum = 0;//统计数量
            while (true) {
                //请求一块空闲的输入缓冲区ByteBuffers。
                int inputBufferId = mediaCodec.dequeueInputBuffer(DEFAULT_TIMEOUT_US);
                if(inputBufferId >= 0){
                    //获取需要编码数据的输入流队列，返回的是一个ByteBuffer数组
                    ByteBuffer buffer = mediaCodec.getInputBuffer(inputBufferId);

                    //把指定通道中的数据按偏移量读取到ByteBuffer中,sampleSize表示数据大小
                    int sampleSize = extractor.readSampleData(buffer, 0);
                    if (sampleSize < 0) {
                        //获取要处理的数据，并将其填充到输入缓冲区（ByteBuffer），提交给编解码器处理
                        mediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        //返回当前的时间戳
                        long sampleTime = extractor.getSampleTime();
                        //获取要处理的数据，并将其填充到输入缓冲区（ByteBuffer），提交给编解码器处理
                        mediaCodec.queueInputBuffer(inputBufferId, 0, sampleSize, sampleTime, 0);
                        extractor.advance();//读取下一帧数据
                    }
                }

                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int outputBufferId = mediaCodec.dequeueOutputBuffer(info, DEFAULT_TIMEOUT_US);
                if(outputBufferId > 0) {

                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        break;
                    }
                    if(info.size != 0) {

                        if(mCountTimeMs > 6 * 1000){
                            //如果视频长度大于6秒的
                            long frameCount = 0;
                            if(mode == FrameMode.MODE_FRAME_START) {
                                //取前面mMaxCount帧
                                long timeCount = 6 * 1000;//取前6秒
                                frameCount = (long) (timeCount / TIME_FRAME_INTERVAL_MS);
                            }else {
                                //取全部视频mMaxCount帧
                                frameCount = mFrameCount;
                            }

                            int i = bitmaps.size();
                            if(sum == (int)(i * (1.0f / mMaxCount) * frameCount)) {
                                Image image = mediaCodec.getOutputImage(outputBufferId);

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                Rect rect = image.getCropRect();
                                cover.width = rect.width();
                                cover.height = rect.width();

                                byte[] data = getDataFromImage(image, COLOR_FormatNV21);
                                YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, rect.width(), rect.height(), null);
                                yuvImage.compressToJpeg(rect, 100, baos);

                                image.close();
                                mediaCodec.releaseOutputBuffer(outputBufferId, true);

                                byte[] buff = baos.toByteArray();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);
                                baos.close();

                                bitmaps.add(bitmap);
                                if(bitmaps.size() >= mMaxCount){
                                    break;
                                }

                            }else {
                                //取下一帧
                                mediaCodec.releaseOutputBuffer(outputBufferId, true);
                            }

                        }else {
                            //如果视频长度小于3s，不管mMaxCount是多少，只取第一帧
                            Image image = mediaCodec.getOutputImage(outputBufferId);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            Rect rect = image.getCropRect();
                            cover.width = rect.width();
                            cover.height = rect.width();

                            byte[] data = getDataFromImage(image, COLOR_FormatNV21);
                            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, rect.width(), rect.height(), null);
                            yuvImage.compressToJpeg(rect, 100, baos);

                            image.close();
                            mediaCodec.releaseOutputBuffer(outputBufferId, true);

                            byte[] buff = baos.toByteArray();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);
                            baos.close();
                            bitmaps.add(bitmap);
                            break;
                        }
                        sum++;
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            return bitmaps;
        }
    }

    private static byte[] getDataFromImage(Image image, int colorFormat) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
                throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
            }

            Rect crop = image.getCropRect();

            int format = image.getFormat();
            int width = crop.width();
            int height = crop.height();
            Image.Plane[] planes = image.getPlanes();
            byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
            byte[] rowData = new byte[planes[0].getRowStride()];

            int channelOffset = 0;
            int outputStride = 1;
            for (int i = 0; i < planes.length; i++) {
                switch (i) {
                    case 0:
                        channelOffset = 0;
                        outputStride = 1;
                        break;
                    case 1:
                        if (colorFormat == COLOR_FormatI420) {
                            channelOffset = width * height;
                            outputStride = 1;
                        } else if (colorFormat == COLOR_FormatNV21) {
                            channelOffset = width * height + 1;
                            outputStride = 2;
                        }
                        break;
                    case 2:
                        if (colorFormat == COLOR_FormatI420) {
                            channelOffset = (int) (width * height * 1.25);
                            outputStride = 1;
                        } else if (colorFormat == COLOR_FormatNV21) {
                            channelOffset = width * height;
                            outputStride = 2;
                        }
                        break;
                }
                ByteBuffer buffer = planes[i].getBuffer();
                int rowStride = planes[i].getRowStride();
                int pixelStride = planes[i].getPixelStride();
                Log.i("sunshine-tools", "pixelStride " + pixelStride);
                Log.i("sunshine-tools", "rowStride " + rowStride);
                Log.i("sunshine-tools", "width " + width);
                Log.i("sunshine-tools", "height " + height);
                Log.i("sunshine-tools", "buffer size " + buffer.remaining());

                int shift = (i == 0) ? 0 : 1;
                int w = width >> shift;
                int h = height >> shift;
                buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
                for (int row = 0; row < h; row++) {
                    int length;
                    if (pixelStride == 1 && outputStride == 1) {
                        length = w;
                        buffer.get(data, channelOffset, length);
                        channelOffset += length;
                    } else {
                        length = (w - 1) * pixelStride + 1;
                        buffer.get(rowData, 0, length);
                        for (int col = 0; col < w; col++) {
                            data[channelOffset] = rowData[col * pixelStride];
                            channelOffset += outputStride;
                        }
                    }
                    if (row < h - 1) {
                        buffer.position(buffer.position() + rowStride - length);
                    }
                }
            }
            return data;
        }

        return null;
    }

    private static void createVideoFrameImageSDK19(String url, int mMaxCount, long delayed, FrameMode mode,OnVideoCallBack callBack){
        MediaMetadataRetriever retriever = null;
        CoverEntity cover = new CoverEntity();
        try {
            retriever = new android.media.MediaMetadataRetriever();
            if (!TextUtils.isEmpty(url)) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                retriever.setDataSource(url, headers);
            }

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//时长(毫秒)
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);//宽
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);//高

            if(StringUtils.isNoEmpty(duration) && StringUtils.isNoEmpty(width) && StringUtils.isNoEmpty(height)) {
                File targetFile = null;
                String dirPath = FileUtils.getCacheDir(FileUtils.DIR_NAME_TEMP_CACHE).getAbsolutePath();

                long countTimeMs = Long.parseLong(duration);
                ArrayList<Bitmap> bitmaps = new ArrayList<>();
                if(mMaxCount > 1 && countTimeMs > 6 * 1000) {
                    String fileName = System.currentTimeMillis() + ".gif";
                    targetFile = FileUtils.getCacheFile(dirPath, fileName);

                    if(mode == FrameMode.MODE_FRAME_START) {
                        //取前面6秒的mMaxCount帧
                        countTimeMs = 6 * 1000;
                    }

                    for (int i = 0; i < mMaxCount; i++) {
                        long timeMs = (long) (i * 1.0f / mMaxCount * countTimeMs);
                        Bitmap bitmap = retriever.getFrameAtTime(timeMs);
                        bitmaps.add(bitmap);
                    }


                    try {
                        int gifWidth = Integer.parseInt(width);
                        int gifHeight = Integer.parseInt(height);
                        GIFUtils.createGifFile(targetFile.getAbsolutePath(),gifWidth, gifHeight, 64, 100, delayed, null, bitmaps);

                    }catch (Exception e) {

                    }

                }else {
                    String fileName = System.currentTimeMillis() + ".jpg";
                    targetFile = FileUtils.getCacheFile(dirPath, fileName);

                    Bitmap bitmap = retriever.getFrameAtTime();
                    bitmaps.add(bitmap);

                    FileOutputStream fos = new FileOutputStream(targetFile);
                    fos.write(BitmapUtils.bitmapToBytes(bitmap, Bitmap.CompressFormat.JPEG));
                    fos.flush();
                    fos.close();
                }

                if(callBack != null) {

                    cover.file = targetFile;
                    cover.bitmaps = bitmaps;

                    if(targetFile.length() > 0) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        //inJustDecodeBounds为true，不返回bitmap，只返回这个bitmap的尺寸
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(targetFile.getAbsolutePath(), options);
                        cover.width = options.outWidth;
                        cover.height = options.outHeight;
                        cover.size = targetFile.length();
                    }

                    cover.videoWidth = StringUtils.isNoEmpty(width) ? Integer.parseInt(width) : 0;
                    cover.videoHeight = StringUtils.isNoEmpty(height) ? Integer.parseInt(height) : 0;
                    cover.videoDuration = Long.parseLong(duration);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(callBack != null){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(()-> callBack.callback(cover));

            }
            if (retriever != null) retriever.release();
        }

    }

    public static File getFrameDir () {
        File dir = FileUtils.getCacheDir(FileUtils.DIR_NAME_TEMP_CACHE);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static String getCacheCoverImagePath(String url){
        if(!StringUtils.isEmpty(url)) {
            String filePath = getFrameDir().toString() + "/" + StringUtils.getMD5(url.getBytes(), 16) + ".jpg";
            File file = new File(filePath);
            if(file.exists()) {
                return "file://" + filePath;
            }

        }
        return null;
    }

    //取帧的风格
    public enum FrameMode {
        MODE_FRAME_START,//取前面数量帧
        MODE_FRAME_ALL,//取全部中的数量帧
    }

    public interface OnVideoCallBack {

        //视频生成图片回调
        void callback(CoverEntity cover);
    }

}
