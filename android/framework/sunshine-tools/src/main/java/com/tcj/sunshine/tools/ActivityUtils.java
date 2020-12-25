package com.tcj.sunshine.tools;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import static android.Manifest.permission.CALL_PHONE;

/**
 * Activity工具类
 */
public final class ActivityUtils {

    private ActivityUtils() {}

    /**
     * 通过一个View得到Activity
     * @param view
     * @return
     */
    public static Activity getActivityByView(@NonNull View view) {
        return getActivityByContext(view.getContext());
    }

    /**
     * 通过一个context得到一个Activity
     * @param context
     * @return
     */
    public static Activity getActivityByContext(@NonNull Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * Activity是否存在
     *
     * @param pkg 包名
     * @param cls
     * @return
     */
    public static boolean isActivityExists(@NonNull final String pkg, @NonNull final String cls) {
        Intent intent = new Intent();
        intent.setClassName(pkg, cls);
        return !(ContextUtils.getContext().getPackageManager().resolveActivity(intent, 0) == null ||
                intent.resolveActivity(ContextUtils.getContext().getPackageManager()) == null ||
                ContextUtils.getContext().getPackageManager().queryIntentActivities(intent, 0).size() == 0);
    }

    /**
     * 判断Intent是否可以跳转
     * @param intent
     * @return
     */
    private static boolean isIntentAvailable(final Intent intent) {
        return ContextUtils.getContext()
                .getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    /**
     * 打开一个Activity
     * @param context
     * @param clz
     */
    public static void startActivity(@NonNull Context context, @NonNull Class<?> clz) {
        startActivity(context, null, clz);
    }

    /**
     * 打开一个Activity
     * @param context
     * @param cls 目标Activity的类名
     */
    public static void startActivity(@NonNull Context context, @NonNull String cls) {
        startActivity(context, null, cls);
    }

    public static void startActivity(@NonNull Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String cls = intent.getComponent().getClassName();
        startActivity(context, extras, cls);
    }

    public static void startActivityForResult(@NonNull Activity activity, Intent intent, int requestCode) {
        Bundle extras = intent.getExtras();
        String cls = intent.getComponent().getClassName();
        startActivityForResult(activity, extras, cls, requestCode);
    }


    /**
     * 打开一个Activity
     * @param activity
     * @param clz
     * @param requestCode
     */
    public static void startActivityForResult(@NonNull Activity activity, @NonNull Class<?> clz, int requestCode) {
        startActivityForResult(activity, null, clz, requestCode);
    }

    /**
     * 打开一个Activity
     * @param activity
     * @param cls
     * @param requestCode
     */
    public static void startActivityForResult(@NonNull Activity activity, @NonNull String cls, int requestCode) {
        startActivityForResult(activity, null, cls, requestCode);
    }


    /**
     * 打开一个Activity
     * @param context
     * @param extras
     * @param clz
     */
    public static void startActivity(@NonNull Context context, Bundle extras, @NonNull Class<?> clz) {
        startActivity(context, extras, clz, null);
    }


    /**
     * 打开一个Activity
     * @param context
     * @param extras
     * @param cls
     */
    public static void startActivity(@NonNull Context context, Bundle extras, @NonNull String cls) {
        startActivity(context, extras, cls, null);
    }


    /**
     * 打开一个Activity
     * @param activity
     * @param extras
     * @param clz
     * @param requestCode
     */
    public static void startActivityForResult(@NonNull Activity activity, Bundle extras, @NonNull Class<?> clz, int requestCode) {
        startActivityForResult(activity, extras, clz, requestCode, null);
    }


    /**
     * 打开一个Activity
     * @param activity
     * @param extras
     * @param cls
     * @param requestCode
     */
    public static void startActivityForResult(@NonNull Activity activity, Bundle extras, @NonNull String cls, int requestCode) {
        startActivityForResult(activity, extras, cls, requestCode, null);
    }


    /**
     * 打开一个Activity
     * @param context
     * @param extras
     * @param clz
     * @param options
     */
    public static void startActivity(@NonNull Context context, Bundle extras, @NonNull Class<?> clz, Bundle options) {
        startActivity(context, extras, clz.getName(), options);
    }

    /**
     * 打开一个Activity
     * @param context
     * @param extras
     * @param cls
     * @param options
     */
    public static void startActivity(@NonNull Context context, Bundle extras, @NonNull String cls, Bundle options) {
        startActivity(context, extras, ContextUtils.getContext().getPackageName(), cls, options);
    }

    /**
     * 打开一个Activity
     * @param activity
     * @param extras
     * @param clz
     * @param requestCode
     * @param options
     */
    public static void startActivityForResult(@NonNull Activity activity, Bundle extras, @NonNull Class<?> clz, int requestCode, Bundle options) {
        startActivityForResult(activity, extras, clz.getName(), requestCode, options);
    }

    /**
     * 打开一个Activity
     * @param activity
     * @param extras
     * @param cls
     * @param requestCode
     * @param options
     */
    public static void startActivityForResult(@NonNull Activity activity, Bundle extras, @NonNull String cls, int requestCode, Bundle options) {
        startActivityForResult(activity, extras, ContextUtils.getContext().getPackageName(), cls, requestCode, options);
    }



    /**
     * 跳转拨号页面
     * @param phoneNumber
     */
    public static boolean dial(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        if (isIntentAvailable(intent)) {
            ContextUtils.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }
        return false;
    }

    /**
     * 拨打电话
     * @param phoneNumber 电话号码
     */
    @RequiresPermission(CALL_PHONE)
    public static boolean call(final String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (isIntentAvailable(intent)) {
            ContextUtils.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }
        return false;
    }

    /**
     * 跳转发送短信页面
     * @param phoneNumber 手机号码
     * @param content 短信内容
     */
    public static boolean sendSms(final String phoneNumber, final String content) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        if (isIntentAvailable(intent)) {
            intent.putExtra("sms_body", content);
            ContextUtils.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        }
        return false;
    }


    /**
     * 调用系统照相机
     * @param activity
     * @param mSaveFileURI
     * @param requestCode
     */
    public static void openCamera(Activity activity, Uri mSaveFileURI, int requestCode){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mSaveFileURI);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 调用系统裁剪图片
     * @param activity
     * @param aspectX 裁剪比例y的值
     * @param aspectY 裁剪比例y的值
     * @param outputX 裁剪最大宽的值
     * @param outputY 裁剪最大高的值
     * @param canScale 是否缩放
     * @param fromFileURI 需要裁剪图片的url
     * @param saveFileURI 保存图片url
     * @param requestCode 返回值
     */
    public static void openCropPicture(Activity activity, int aspectX, int aspectY, int outputX, int outputY, boolean canScale, Uri fromFileURI, Uri saveFileURI, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(fromFileURI, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪比例x的值
        intent.putExtra("aspectX", aspectX <= 0 ? 1 : aspectX);
        // 裁剪比例y的值
        intent.putExtra("aspectY", aspectY <= 0 ? 1 : aspectY);
        //裁剪最大宽
        intent.putExtra("outputX", outputX);
        //裁剪最大高
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", canScale);
        // 图片剪裁不足黑边解决
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        // 需要将读取的文件路径和裁剪写入的路径区分，否则会造成文件0byte
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
        // true-->返回数据类型可以设置为Bitmap，但是不能传输太大，截大图用URI，小图用Bitmap或者全部使用URI
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        // 取消人脸识别功能
        intent.putExtra("noFaceDetection", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 请求打开通知栏开关
     *
     */
    public void openNotificationLock(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, ContextUtils.getContext().getPackageName());
            context.startActivity(intent);
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", ContextUtils.getContext().getPackageName());
            intent.putExtra("app_uid", ContextUtils.getContext().getApplicationInfo().uid);
            context.startActivity(intent);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + ContextUtils.getContext().getPackageName()));
            context.startActivity(intent);
        }
    }


    // ===================================私有方法=================================================

    private static void startActivity(Context context, Bundle extras, String pkg, String cls, Bundle options) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (extras != null) intent.putExtras(extras);
        intent.setComponent(new ComponentName(pkg, cls));
        startActivity(context, intent, options);
    }

    private static boolean startActivity(Context context,Intent intent, Bundle options) {
        if (!isIntentAvailable(intent)) {
            LogUtils.e("sunshine-error", "intent是无法获得的");
            return false;
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (options != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            context.startActivity(intent, options);
        } else {
            context.startActivity(intent);
        }
        return true;
    }


    private static boolean startActivityForResult(Activity activity,Bundle extras, String pkg, String cls, int requestCode, Bundle options) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (extras != null) intent.putExtras(extras);
        intent.setComponent(new ComponentName(pkg, cls));
        return startActivityForResult(intent, activity, requestCode, options);
    }

    private static boolean startActivityForResult(Intent intent,Activity activity,int requestCode,Bundle options) {
        if (!isIntentAvailable(intent)) {
            LogUtils.e("sunshine-error", "intent是无法获得的");
            return false;
        }
        if (options != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
        return true;
    }

}

