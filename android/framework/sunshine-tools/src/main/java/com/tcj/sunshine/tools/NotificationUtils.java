package com.tcj.sunshine.tools;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tcj.sunshine.tools.AppUtils;

/**
 * 通知栏工具类
 */
public class NotificationUtils {

    private NotificationUtils(){}


    /**
     * 是否可以弹出通知栏
     * @return
     */
    public static boolean isNotificationEnabled(){
        NotificationManagerCompat manager = NotificationManagerCompat.from(ContextUtils.getContext());
        return manager.areNotificationsEnabled();
    }


    /**
     * 请求打开通知栏开关
     */
    public void requestNotificationPermission(Context context) {
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


    /**
     * 显示通知栏通知
     * @param context
     * @param intent
     * @param nofifyId
     * @param icon
     * @param ticherText
     * @param title
     * @param content
     */
    public static void showNotification(Context context, Intent intent, int nofifyId, @DrawableRes int icon, String ticherText, String title, String content){
        try {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelID = "10898";
                String channelName = "miaoyin";
                NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(true);
                // 配置通知渠道的属性
                channel.setDescription(AppUtils.getAppName() + "通知");// 设置通知出现时的闪灯（如果 android 设备支持的话）
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                // 设置通知出现时的震动（如果 android 设备支持的话）
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);
                Notification.Builder builder = new Notification.Builder(context, channel.getId());

                builder.setSmallIcon(icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                        .setTicker(ticherText)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setContentText(content)
                        .setContentTitle(title)
                        .setChannelId(channelID)
                        .setGroupSummary(false)
                        .setGroup("group");

                PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
                manager.notify("miaoyin", nofifyId, builder.build());
            }else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(icon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                        .setTicker(ticherText)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    builder.setGroupSummary(false)
                            .setGroup("group");
                }

                PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify("miaoyin", nofifyId, builder.build());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
