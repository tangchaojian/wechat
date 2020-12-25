package com.tcj.sunshine.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stefan Lau on 2019/11/14.
 */
public class DateUtils {
    //年到秒
    public static String FORMAT_Y_TO_S = "yyyy-MM-dd HH:mm:ss";
    //年到分钟
    public static String FORMAT_Y_TO_M = "yyyy-MM-dd HH:mm";
    //年到日
    public static String FORMAT_Y_TO_D = "yyyy-MM-dd";
    //年到月
    public static String FORMAT_Y_TO_MONTH = "yyyy-MM";
    //月到分钟
    public static String FORMAT_MONTH_TO_M = "MM-dd HH:mm";
    //月到日
    public static String FORMAT_MONTH_TO_D = "MM-dd";

    public static String FORMAT_H_TO_M = "HH:mm";

    /**
     * 得到格式化日期 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getFormatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_Y_TO_S);
        return sdf.format(new Date());
    }

    /**
     * 得到格式化日期 格式 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_Y_TO_S);
        return sdf.format(date);
    }


    /**
     * 得到格式化日期
     *
     * @param format 自定义格式
     * @return
     */
    public static String getFormatDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    /**
     * 得到格式化日期
     *
     * @param format 自定义格式
     * @return
     */
    public static String getFormatDate(String format, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }


    /**
     * 得到格式化日期
     *
     * @param newFormat 新日期格式
     * @param oldFormat 旧日期格式
     * @param strDate   字符串日期
     * @return
     */
    public static String getFormatDate(String newFormat, String oldFormat, String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
            Date date = sdf.parse(strDate);
            SimpleDateFormat mSDNewFormat = new SimpleDateFormat(newFormat);
            return mSDNewFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 得到格式化日期
     *
     * @param format  日期格式
     * @param strDate 字符串日期
     * @return
     */
    public static long getDateTime(String format, String strDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(strDate);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 得到当前日期 格式 yyyy-MM-dd
     */
    public static String getCurrenDate() {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_Y_TO_D);
        Date date = new Date();
        return format.format(date);
    }

    public static long getTimestamp() {
        return new Date().getTime();
    }


    public static long getTimestampForTen() {
        long time = new Date().getTime();
        return time / 1000;
    }

    /**
     * 得到当前日期 格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrenTime() {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_Y_TO_S);
        return format.format(new Date());
    }

    /**
     * 字符串日期转日期
     *
     * @param strDate
     * @param format
     */
    public static Date strToDate(String strDate, String format) {
        try {
            SimpleDateFormat mDateFormat = new SimpleDateFormat(format);
            return mDateFormat.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dateToString(Date date, String format) {
        try {
            SimpleDateFormat mDateFormat = new SimpleDateFormat(format);
            return mDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定天数之后的日期
     * @param datetime 日期的格式：yyyy-MM-dd HH:mm:ss
     * @param day
     * @return
     */
    public static String getAfterDay(String datetime, int day) {
        return getAfterDay(datetime, FORMAT_Y_TO_S, day);
    }


    /**
     * 指定天数之后的日期
     * @param timestamp 时间戳
     * @param format
     * @param day
     * @return
     */
    public static String getAfterDay(long timestamp, String format, int day) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(timestamp));
            calendar.add(Calendar.DAY_OF_MONTH, day);
            return sdf.format(calendar.getTime());
        }catch (Exception e) {
            return "";
        }
    }


    /**
     * 指定天数之后的日期
     * @param datetime 日期
     * @param format 日期格式
     * @param day 增加的天数
     * @return
     */
    public static String getAfterDay(String datetime, String format, int day) {
        if(!StringUtils.isEmpty(datetime)) {
            long timestamp = getDateTime(format, datetime);
            return getAfterDay(timestamp, format, day);
        }
        return "";
    }

    public static String formatTime(long timeMs) {
        try {
            if (timeMs <= 0) {
                return String.format(Locale.CHINA, "%02d:%02d", 0, 0);
            }
            long totalSeconds = timeMs / 1000;

            long seconds = totalSeconds % 60;
            long minutes = (totalSeconds / 60) % 60;
            long hours = totalSeconds / 3600;

            if (hours > 0) {
                return String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.CHINA, "%02d:%02d", minutes, seconds);
            }
        } catch (NumberFormatException e) {
            return "00:00";
        }

    }

    public static String getDateDesc(long timestamp) {
        try {
            long time = timestamp / 1000;
            long now = System.currentTimeMillis() / 1000;
            long ago = now - time;
            if (ago < 60 * 60) {
                if(ago / 60 < 1) {
                    return "刚刚";
                }else {
                    return ago /60 + "分钟前";
                }
            } else if(ago < 24 * 60 * 60){
                return ago / (60 * 60) + "小时前";
            }else {
                return getFormatDate(DateUtils.FORMAT_MONTH_TO_D, timestamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getFormatDate(DateUtils.FORMAT_MONTH_TO_D, timestamp);
        }
    }


}
