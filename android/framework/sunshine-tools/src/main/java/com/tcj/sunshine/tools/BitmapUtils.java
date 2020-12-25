package com.tcj.sunshine.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Bitmap工具类
 */
public class BitmapUtils {

    private BitmapUtils() {
    }

    /**
     * Bitmap转字节数组.
     *
     * @param bitmap
     * @param format
     * @return bytes
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, CompressFormat format) {
        if (bitmap == null) return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(format, 100, baos);
            byte[] buffer = baos.toByteArray();
            baos.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节数组转Bitmap
     *
     * @param bytes 字节数组
     * @return bitmap
     */
    public static Bitmap bytesToBitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Drawable转bitmap.
     *
     * @param drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转drawable
     *
     * @param bitmap The bitmap.
     * @return drawable
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(ContextUtils.getContext().getResources(), bitmap);
    }

    /**
     * Drawable转字节数组
     *
     * @param drawable
     * @param format
     * @return
     */
    public static byte[] drawable2Bytes(Drawable drawable, CompressFormat format) {
        return drawable == null ? null : bitmapToBytes(drawableToBitmap(drawable), format);
    }

    /**
     * 字节数组转Drawable
     *
     * @param bytes
     * @return
     */
    public static Drawable bytesToDrawable(byte[] bytes) {
        return bitmapToDrawable(bytesToBitmap(bytes));
    }


    /**
     * 把View转换成Bitmap
     *
     * @param view
     * @return
     */
    public static Bitmap viewToBitmap(View view) {
        if (view == null) return null;
        Bitmap ret = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(ret);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }
        view.draw(canvas);
        return ret;
    }


    /**
     * 获取Bitmap
     *
     * @param file
     * @return
     */
    public static Bitmap getBitmap(File file) {
        if (file == null) return null;
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * 获取Bitmap,指定最大宽高
     *
     * @param file
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return
     */
    public static Bitmap getBitmap(File file, int maxWidth, int maxHeight) {
        if (file == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * 获取Bitmap
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmap(String filePath) {
        if (isSpace(filePath)) return null;
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * 获取Bitmap
     *
     * @param filePath  文件路径
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return bitmap
     */
    public static Bitmap getBitmap(String filePath, int maxWidth, int maxHeight) {
        if (isSpace(filePath)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 获取Bitmap
     *
     * @param is 输入流
     * @return
     */
    public static Bitmap getBitmap(InputStream is) {
        if (is == null) return null;
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 获取Bitmap
     *
     * @param is        输入流
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return bitmap
     */
    public static Bitmap getBitmap(InputStream is, int maxWidth, int maxHeight) {
        if (is == null) return null;
        byte[] bytes = input2Byte(is);
        return getBitmap(bytes, 0, maxWidth, maxHeight);
    }

    /**
     * 获取Bitmap
     *
     * @param data   The data.
     * @param offset The offset.
     * @return bitmap
     */
    public static Bitmap getBitmap(byte[] data, int offset) {
        if (data.length == 0) return null;
        return BitmapFactory.decodeByteArray(data, offset, data.length);
    }

    /**
     * 获取Bitmap
     *
     * @param data      The data.
     * @param offset    The offset.
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return bitmap
     */
    public static Bitmap getBitmap(byte[] data, int offset, int maxWidth, int maxHeight) {
        if (data.length == 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    /**
     * 获取Bitmap
     *
     * @param resId The resource id.
     * @return bitmap
     */
    public static Bitmap getBitmap(@DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(ContextUtils.getContext(), resId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取Bitmap
     *
     * @param resId
     * @param maxWidth
     * @param maxHeight
     * @return bitmap
     */
    public static Bitmap getBitmap(@DrawableRes int resId, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Resources resources = ContextUtils.getContext().getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 获取Bitmap
     *
     * @param
     * @return bitmap
     */
    public static Bitmap getBitmap(FileDescriptor fd) {
        if (fd == null) return null;
        return BitmapFactory.decodeFileDescriptor(fd);
    }

    /**
     * 获取Bitmap
     *
     * @param fd
     * @param maxWidth
     * @param maxHeight
     * @return bitmap
     */
    public static Bitmap getBitmap(FileDescriptor fd, int maxWidth, int maxHeight) {
        if (fd == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }


    /**
     * 返回一个缩放Bitmap
     *
     * @param src
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @return
     */
    public static Bitmap scale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 返回一个缩放Bitmap
     *
     * @param src
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @param recycle   是否回收
     * @return
     */
    public static Bitmap scale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * Return
     *
     * @param src
     * @param scaleWidth  缩放宽
     * @param scaleHeight 缩放高
     * @return
     */
    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * Return
     *
     * @param src
     * @param scaleWidth  缩放宽
     * @param scaleHeight 缩放高
     * @param recycle     是否回收
     * @return
     */
    public static Bitmap scale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 裁剪Bitmap
     *
     * @param src
     * @param x      裁剪的起始x位置
     * @param y      裁剪的起始y位置
     * @param width  裁剪宽
     * @param height 裁剪高
     * @return
     */
    public static Bitmap clip(Bitmap src, int x, int y, int width, int height) {
        return clip(src, x, y, width, height, false);
    }

    /**
     * 裁剪Bitmap
     *
     * @param src
     * @param x       裁剪的起始x位置
     * @param y       裁剪的起始y位置
     * @param width   裁剪宽
     * @param height  裁剪高
     * @param recycle 是否回收
     * @return
     */
    public static Bitmap clip(Bitmap src, int x, int y, int width, int height, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }


    /**
     * 得到一个旋转Bitmap
     *
     * @param src
     * @param degrees 旋转角度
     * @param px      旋转点x坐标
     * @param py      旋转点y坐标
     * @return
     */
    public static Bitmap rotate(Bitmap src, int degrees, float px, float py) {
        return rotate(src, degrees, px, py, false);
    }

    /**
     * 得到一个旋转Bitmap
     *
     * @param src
     * @param degrees 旋转角度
     * @param px      旋转点x坐标
     * @param py      旋转点y坐标
     * @param recycle 是否回收
     * @return
     */
    public static Bitmap rotate(Bitmap src, int degrees, float px, float py, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        if (degrees == 0) return src;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 得到图片文件旋转角度
     *
     * @param filePath
     * @return
     */
    public static int getRotateDegree(String filePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 得到一个圆角Bitmap
     *
     * @param src
     * @return
     */
    public static Bitmap toRound(Bitmap src) {
        return toRound(src, 0, 0, false);
    }

    /**
     * 给Bitmap加一个边框
     *
     * @param src
     * @param recycle 是否回收
     * @return
     */
    public static Bitmap toRound(Bitmap src, boolean recycle) {
        return toRound(src, 0, 0, recycle);
    }

    /**
     * 给Bitmap加一个边框
     *
     * @param src
     * @param borderSize  边框宽
     * @param borderColor 边框颜色
     * @return
     */
    public static Bitmap toRound(Bitmap src, @IntRange(from = 0) int borderSize, @ColorInt int borderColor) {
        return toRound(src, borderSize, borderColor, false);
    }

    /**
     * 给Bitmap加一个边框
     *
     * @param src
     * @param recycle     是否回收
     * @param borderSize  边框宽
     * @param borderColor 边框颜色
     * @return
     */
    public static Bitmap toRound(Bitmap src, @IntRange(from = 0) int borderSize, @ColorInt int borderColor, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        float center = size / 2f;
        RectF rectF = new RectF(0, 0, width, height);
        rectF.inset((width - size) / 2f, (height - size) / 2f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        if (width != height) {
            matrix.preScale((float) size / width, (float) size / height);
        }
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            float radius = center - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }

    /**
     * 圆角bitmap
     *
     * @param src
     * @param radius
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap src, float radius) {
        return toRoundCorner(src, radius, 0, 0, false);
    }

    /**
     * 圆角bitmap
     *
     * @param src
     * @param radius
     * @param recycle 是否回收
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap src, float radius, boolean recycle) {
        return toRoundCorner(src, radius, 0, 0, recycle);
    }

    /**
     * 圆角bitmap
     *
     * @param src
     * @param radius
     * @param borderSize  边框宽
     * @param borderColor 边框颜色
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap src, float radius, @IntRange(from = 0) int borderSize, @ColorInt int borderColor) {
        return toRoundCorner(src, radius, borderSize, borderColor, false);
    }

    /**
     * 圆角bitmap
     *
     * @param src
     * @param radius
     * @param borderSize  边框宽
     * @param borderColor 边框颜色
     * @param recycle     是否回收
     * @return
     */
    public static Bitmap toRoundCorner(Bitmap src, float radius, @IntRange(from = 0) int borderSize, @ColorInt int borderColor, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0, 0, width, height);
        float halfBorderSize = borderSize / 2f;
        rectF.inset(halfBorderSize, halfBorderSize);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
        if (recycle && !src.isRecycled() && ret != src) src.recycle();
        return ret;
    }


    /**
     * 圆角bitmap
     *
     * @param src
     * @param radius
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap src, float radius) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0, 0, width, height);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setShader(null);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return ret;
    }

    /**
     * 保存Bitmap到文件
     *
     * @param src
     * @param targetPath 目标文件路径
     * @param format     图片格式
     * @return
     */
    public static boolean save(Bitmap src, String targetPath, CompressFormat format) {
        return save(src, getFileByPath(targetPath), format, false);
    }

    /**
     * 保存Bitmap到文件
     *
     * @param src
     * @param targetPath
     * @param format     图片格式
     * @return
     */
    public static boolean save(Bitmap src, File targetPath, CompressFormat format) {
        return save(src, targetPath, format, false);
    }

    /**
     * 保存Bitmap到文件
     *
     * @param src
     * @param targetPath
     * @param format     图片格式
     * @param recycle    是否回收
     * @return
     */
    public static boolean save(Bitmap src, String targetPath, CompressFormat format, boolean recycle) {
        return save(src, getFileByPath(targetPath), format, recycle);
    }

    /**
     * 保存Bitmap到文件
     *
     * @param src
     * @param file    保存文件
     * @param format  The format of the image.
     * @param recycle 是否回收
     * @return
     */
    public static boolean save(Bitmap src, File file, CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src) || !createFileByDeleteOldFile(file)) return false;
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled()) src.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 是否是图片
     *
     * @param file
     * @return
     */
    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return isImage(file.getPath());
    }

    /**
     * 是否是图片
     *
     * @param filePath
     * @return
     */
    public static boolean isImage(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            return options.outWidth != -1 && options.outHeight != -1;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 图片类型
     *
     * @param filePath
     * @return
     */
    public static String getImageType(String filePath) {
        return getImageType(getFileByPath(filePath));
    }

    /**
     * 图片类型
     *
     * @param file
     * @return
     */
    public static String getImageType(File file) {
        if (file == null) return "";
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            String type = getImageType(is);
            if (type != null) {
                return type;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getFileExtension(file.getAbsolutePath()).toUpperCase();
    }

    private static String getFileExtension(String filePath) {
        if (isSpace(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    private static String getImageType(InputStream is) {
        if (is == null) return null;
        try {
            byte[] bytes = new byte[8];
            return is.read(bytes, 0, 8) != -1 ? getImageType(bytes) : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getImageType(byte[] bytes) {
        if (isJPEG(bytes)) return "JPEG";
        if (isGIF(bytes)) return "GIF";
        if (isPNG(bytes)) return "PNG";
        if (isBMP(bytes)) return "BMP";
        return null;
    }

    private static boolean isJPEG(byte[] b) {
        return b.length >= 2 && (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(byte[] b) {
        return b.length >= 6 && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8' && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(byte[] b) {
        return b.length >= 8 && (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78 && b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10 && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(byte[] b) {
        return b.length >= 2 && (b[0] == 0x42) && (b[1] == 0x4d);
    }

    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }


    /**
     * 压缩图片
     *
     * @param src
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @return
     */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 压缩图片
     *
     * @param src
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @param recycle   是否回收
     * @return
     */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }

    /**
     * 压缩图片
     *
     * @param src
     * @param scaleWidth  缩放宽
     * @param scaleHeight 缩放高
     * @return
     */
    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight) {
        return scale(src, scaleWidth, scaleHeight, false);
    }

    /**
     * 压缩图片
     *
     * @param src
     * @param scaleWidth  缩放宽
     * @param scaleHeight 缩放高
     * @param recycle     是否回收
     * @return he compressed bitmap
     */
    public static Bitmap compressByScale(Bitmap src, float scaleWidth, float scaleHeight, boolean recycle) {
        return scale(src, scaleWidth, scaleHeight, recycle);
    }

    /**
     * 质量压缩
     *
     * @param src
     * @param quality
     * @return
     */
    public static Bitmap compressByQuality(Bitmap src, @IntRange(from = 0, to = 100) int quality) {
        return compressByQuality(src, quality, false);
    }

    /**
     * 质量压缩
     *
     * @param src
     * @param quality
     * @param recycle 是否回收
     * @return
     */
    public static Bitmap compressByQuality(Bitmap src, @IntRange(from = 0, to = 100) int quality, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 质量压缩
     *
     * @param src
     * @param maxByteSize 压缩最大值
     * @return
     */
    public static Bitmap compressByQuality(Bitmap src, long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * 质量压缩
     *
     * @param src
     * @param maxByteSize 压缩最大值
     * @param recycle     是否回收
     * @return
     */
    public static Bitmap compressByQuality(Bitmap src, long maxByteSize, boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(CompressFormat.JPEG, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray();
            } else {
                // find the best quality using binary search
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 把Bitmap压缩到一个值
     *
     * @param src
     * @param sampleSize The sample size.
     * @return
     */

    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    /**
     * 把Bitmap压缩到一个值
     *
     * @param src
     * @param sampleSize The sample size.
     * @param recycle    是否回收
     * @return
     */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 把Bitmap压缩到一个值
     *
     * @param src
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return
     */
    public static Bitmap compressBySampleSize(Bitmap src, int maxWidth, int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
    }

    /**
     * 把Bitmap压缩到一个值
     *
     * @param src
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @param recycle   是否回收
     * @return
     */
    public static Bitmap compressBySampleSize(Bitmap src, int maxWidth, int maxHeight, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 返回Bitmap大小
     *
     * @param filePath
     * @return
     */
    public static int[] getSize(String filePath) {
        return getSize(getFileByPath(filePath));
    }

    /**
     * 返回Bitmap大小
     *
     * @param file
     * @return
     */
    public static int[] getSize(File file) {
        if (file == null) return new int[]{0, 0};
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        return new int[]{opts.outWidth, opts.outHeight};
    }

    /**
     * 把图片压缩到固定大小
     *
     * @param options   The options.
     * @param maxWidth  最大宽
     * @param maxHeight 最大高
     * @return the sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (height > maxHeight || width > maxWidth) {
            height >>= 1;
            width >>= 1;
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    /**
     * 通过文件路径得到一个文件对象
     *
     * @param filePath
     * @return
     */
    private static File getFileByPath(String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }


    /**
     * 如果这个文件存在，则删除再创建
     *
     * @param file
     * @return
     */
    private static boolean createFileByDeleteOldFile(File file) {
        if (file == null) return false;
        if (file.exists() && !file.delete()) return false;
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 是否创建目录成功
     *
     * @param file
     * @return
     */
    private static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * input转换成字节数组
     *
     * @param is
     * @return
     */
    private static byte[] input2Byte(InputStream is) {
        if (is == null) return null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b, 0, 1024)) != -1) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取视频文件截图
     *
     * @param path 视频文件的路径
     * @return Bitmap 返回获取的Bitmap
     */
    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return media.getFrameAtTime();
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @param ratio  宽高比（宽/高）
     * @return
     */
    public static Bitmap cropBitmap(Bitmap bitmap, float ratio) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight(); // 得到图片的宽，高
        int cropWidth = w;
        int cropHeight = (int) (w / ratio);
        return Bitmap.createBitmap(bitmap, 0, (h - cropHeight) / 2, cropWidth, cropHeight, null, false);
    }

    /**
     * @param bitmap
     * @param quality
     * @return
     */
    public static File getFile(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        File file = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[4096];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.flush();
            fos.close();
            is.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


//    public static Bitmap toRound(Bitmap bitmap, int roundPx) {
//        try {
//            // 其原理就是：先建立一个与图片大小相同的透明的Bitmap画板
//            // 然后在画板上画出一个想要的形状的区域。
//            // 最后把源图片帖上。
//            final int width = bitmap.getWidth();
//            final int height = bitmap.getHeight();
//
//            Bitmap paintingBoard = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(paintingBoard);
//            canvas.drawARGB(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT);
//
//            final Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setColor(Color.TRANSPARENT);
//
//            //画出4个圆角
//            final RectF rectF = new RectF(0, 0, width, height);
//            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//
//            //把不需要的圆角去掉
//            clipTopLeft(canvas,paint,roundPx,width,height);
//            clipTopRight(canvas, paint, roundPx, width, height);
//            clipBottomLeft(canvas,paint,roundPx,width,height);
//            clipBottomRight(canvas, paint, roundPx, width, height);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//
//            //帖子图
//            final Rect src = new Rect(0, 0, width, height);
//            final Rect dst = src;
//            canvas.drawBitmap(bitmap, src, dst, paint);
//            return paintingBoard;
//        } catch (Exception exp) {
//            return bitmap;
//        }
//    }

    private static void clipTopLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, 0, offset, offset);
        canvas.drawRect(block, paint);
    }

    private static void clipTopRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(width - offset, 0, width, offset);
        canvas.drawRect(block, paint);
    }

    private static void clipBottomLeft(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(0, height - offset, offset, height);
        canvas.drawRect(block, paint);
    }

    private static void clipBottomRight(final Canvas canvas, final Paint paint, int offset, int width, int height) {
        final Rect block = new Rect(width - offset, height - offset, width, height);
        canvas.drawRect(block, paint);
    }

    /**
     * 压缩图片
     *
     * @param path
     * @return
     */
    public static File compressImage(Context context, String path) {
        if (path == null || !new File(path).exists()) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calulateSize(options);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            File parent;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                parent = context.getExternalCacheDir();
            } else {
                parent = context.getCacheDir();
            }
            File dir = new File(parent, Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "temp.jpg");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                baos.close();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 计算出合适的缩放系数
     *
     * @param options
     * @return
     */
    public static int calulateSize(BitmapFactory.Options options) {
        int area = ScreenUtils.getScreenWidth() * ScreenUtils.getScreenHeight() / 4;
        int sampleSize = 1;
        while (options.outWidth * options.outHeight > area * sampleSize * sampleSize) {
            sampleSize *= 2;
        }
        return sampleSize;
    }


}