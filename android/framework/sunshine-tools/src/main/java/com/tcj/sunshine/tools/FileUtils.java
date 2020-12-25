package com.tcj.sunshine.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import static android.os.Environment.DIRECTORY_DCIM;

/**
 * 由于Android Q版本，外部存储目录一般不能被访问
 * 只能访问公共目录，比如相册
 */
public class FileUtils {

    public static File DIR_CAMERA = null;
    public static File DIR_CACHE = null;


    public static final String DIR_CAMERA_NAME = "Camera";
    public static final String DIR_CACHE_NAME = "cache";


    public static final String DIR_NAME_IMAGE_CACHE = "images";//日常图片缓存
    public static final String DIR_NAME_TEMP_CACHE = "temp";//日常临时文件缓存目录
    public static final String DIR_NAME_GIF_CACHE = "gif";//gif图片缓存目录
    public static final String DIR_NAME_COMPRESS_CACHE = "compress";//图片压缩目录
    public static final String DIR_NAME_HTTP_CACHE = "HttpCache";//http缓存目录

    static {
        DIR_CAMERA = getExternalStorageDirectory(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM), DIR_CAMERA_NAME);
        DIR_CACHE = getCacheRootDir();
    }


    /**
     * 获取内缓存根目录
     *
     * @return
     */
    public static File getCacheRootDir() {
        try {
            File dir = new File(ContextUtils.getContext().getCacheDir(), DIR_CACHE_NAME);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static File getExternalStorageRootDir() {
        try {
            boolean result = PermissionUtils.hasPermission(new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE});
            if (result) {
                //有权限
                String path = Environment.getExternalStorageDirectory().toString() + "/" + AppUtils.getPackageName();
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                return dir;
            }
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取内缓存路径
     *
     * @param dirName
     * @return
     */
    public static File getCacheDir(String dirName) {
        try {
            File dir = new File(DIR_CACHE, dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取内部缓存文件
     *
     * @param dirName  目录名
     * @param fileName 文件名
     * @return
     */
    public static File getCacheFile(String dirName, String fileName) {
        try {
            File dir = getCacheDir(dirName);
            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取外存储目录
     *
     * @param dirName
     * @return
     */
    public static File getExternalStorageCacheDir(String dirName) {
        try {
            boolean result = PermissionUtils.hasPermission(new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE});
            if (result) {
                //有权限
                String path = Environment.getExternalStorageDirectory().toString() + "/" + AppUtils.getPackageName() + "/" + dirName;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                return dir;
            }
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 获取外存储目录
     *
     * @param dirName
     * @return
     */
    public static File getExternalStorageDirectory(File parentDir, String dirName) {
        try {
            boolean result = PermissionUtils.hasPermission(new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE});
            if (result) {
                //有权限
                File dir = new File(parentDir, dirName);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                return dir;
            }
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 创建文件
     *
     * @param parentDir 文件上一级目录
     * @param fileName  文件名
     * @return
     */
    public static File getExternalStorageFile(File parentDir, String fileName) {
        try {
            boolean result = PermissionUtils.hasPermission(new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE});
            if (result) {
                //有权限
                File file = new File(parentDir, fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                return file;
            }
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 创建文件
     *
     * @param fileName 文件名
     * @return
     */
    public static File getExternalStorageCameraFile(String fileName) {
        try {
            boolean result = PermissionUtils.hasPermission(new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE});
            if (result) {
                //有权限
                File file = new File(DIR_CAMERA, fileName);
                if (!file.exists()) {
                    file.createNewFile();
                }
                return file;
            }
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 获取外存储目录
     *
     * @param dirPath
     * @return
     */
    public static File getExternalStorageDirectory(String dirPath) {
        try {
            boolean result = PermissionUtils.hasPermission(new String[]{PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE});
            if (result) {
                //有权限
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                return dir;
            }
        } catch (Exception e) {
            LogUtils.e("sunshine-exception", e.getMessage());
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public static File createFile(File dir, String fileName){

        try {
            File file = new File(dir, fileName);
            if(!file.exists()) {
                file.createNewFile();
            }
            return file;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 根据file得到http的Content-type
     *
     * @return
     */
    public static String toContentType(File file) {
        if (file != null && !StringUtils.isEmpty(file.getName()) && file.isFile()) {
            String name = file.getName();
            String suffix = name.substring(name.lastIndexOf(".")).toLowerCase();
            switch (suffix) {
                case ".txt":
                    return "text/plain";
                case ".xml":
                    return "text/xml";
                case ".html":
                case ".htm":
                    return "text/html";
                case ".java":
                    return "java/*";
                case ".jpg":
                case ".jpeg":
                    return "image/jpeg";
                case ".png":
                    return "image/png";
                case ".gif":
                    return "image/gif";
                case ".bmp":
                    return "application/x-bmp";
                case ".mp4":
                    return "video/mpeg4";
                case ".avi":
                    return "video/avi";
                case ".rmvb":
                    return "application/vnd.rn-realmedia-vbr";
                case ".rmp":
                    return "application/vnd.rn-rn_music_package";
                case ".apk":
                    return "application/vnd.android.package-archive";
                case ".mp3":
                    return "audio/mp3";
                case ".xls":
                    return "application/x-xls";
                case ".ppt":
                    return "application/x-ppt";
                case ".doc":
                    return "application/msword";
                case ".exe":
                    return "application/x-msdownload";

            }
        }
        return "";
    }


    /**
     * 复制文件
     *
     * @param context
     * @param filePath
     * @param targetPath
     */
    public static void copyFile(Context context, String filePath, String targetPath) {
        InputStream input = null;
        OutputStream output = null;
        try {
            if (!new File(targetPath).exists()) {
                new File(targetPath).mkdir();
            }
            File file = new File(filePath);
            String ouputPath = targetPath + file.getName();
            input = new FileInputStream(file);
            output = new FileOutputStream(ouputPath);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, bytesRead);
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + ouputPath)));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 是否是图片文件
     * @param suffix
     * @return
     */
    public static boolean isImageFile(String suffix) {
        if(StringUtils.isEmpty(suffix))return false;

        switch (suffix) {
            case "jpeg":
            case "jpg":
            case "png":
            case "gif":
            case "bmp":
                return true;
        }

        return false;
    }


    /**
     * 获取指定文件大小 　　
     */
    public static long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            size = file.length();
        } else {
            Log.i("sunshine-libjpeg", "文件不存在!");
        }
        return size;
    }

    public static String getFileSizeStr(File file) {
        long size = getFileSize(file);
//        Log.i("sunshine-libjpeg", "file.size:" + size);
        if(size > 1024 * 1024 * 1024){
            float value = size * 1.0f / (1024 * 1024 * 1024);
            return StringUtils.formatDouble(value, "0.00") + "G";
        }else if(size > 1024 * 1024) {
            float value = size * 1.0f / (1024 * 1024);
            return StringUtils.formatDouble(value, "0.00") + "M";
        }else if(size > 1024){
            float value = size * 1.0f / 1024;
            return StringUtils.formatDouble(value, "0.00") + "K";
        }else {
            float value = size * 1.0f;
            return StringUtils.formatDouble(value, "0.00") + "B";
        }
    }


    /**
     * 读取文件
     * @param sourceFile
     * @return
     */
    public static byte[] readFile(File sourceFile) {
        try {
            //判断文件是否存在
            if(!sourceFile.exists()) return null;

            FileInputStream fis = new FileInputStream(sourceFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while((len = fis.read(buffer)) != -1){
                baos.write(buffer, 0, len);
            }
            baos.flush();
            baos.close();
            fis.close();
            return baos.toByteArray();

        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 写入文件
     * @param input
     * @param targetFile
     */
    public static void writeFile(byte[] input, File targetFile) {
        try {
            //判断文件是否存在
            if(!targetFile.exists()) {
                targetFile.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(targetFile);
            fos.write(input);
            fos.flush();
            fos.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件
     * @param url
     * @return
     */
    public static boolean deleteFile(String url) {
        boolean result = false;
        File file = new File(url);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    public static String formatSize(long size){
        DecimalFormat format = new DecimalFormat("#.0");
        if (size < 1024){
            return size +"B";
        }else if (size <1024 * 1024){
            double result = size * 1.0D / 1024;
            return format.format(result) +"KB";
        }else if (size  < 1024 * 1024 * 1024){
            double result = size * 1.0D / 1024 * 1024;
            return format.format(result) +"MB";
        } else {
            double result = size * 1.0D / 1024 * 1024 * 1024;
            return format.format(result) +"GB";
        }
    }
}
