package com.tcj.sunshine.tools;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StringUtils {

    /**
     * 判断是否为空
     * @param str
     * @return ture:为空;false:不为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || "null".equals(str.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static boolean isNoEmpty(String str) {
        return !isEmpty(str);
    }

    public static String getParams(Map<String, String> params) {
        if (params != null && params.size() > 0) {
            StringBuilder sbuilder = new StringBuilder();
            Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (!isEmpty(value)) {
                    sbuilder.append("&").append(key).append("=").append(value);
                }
            }
            return sbuilder.toString();
        }
        return "";
    }

    /**
     * 整数长度不足用0补齐
     *
     * @param length
     * @param number
     * @return
     */
    public static String padded(int length, int number) {
        String f = "%0" + length + "d";
        return String.format(f, number);
    }

    /**
     * 获取随机数
     *
     * @param num 位数
     * @return
     */
    public static int getRandom(int num) {
        try {
            if (num > 1) {
                int numcode = (int) ((Math.random() * 9 + 1) * Math.pow(10, num - 1));
                return numcode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 格式化字符串
     * 返回每n个字符一个空格的字符串
     * 如:1108 1002 3333
     * @param num 几个字符一个空格
     * @param text
     * @return
     */
    public static String getRegexGapStr(int num, String text) {
        String regex = "(.{" + num + "})";
        String value = text.replaceAll(regex, "$1 ");
        return value;
    }

    public static SpannableStringBuilder setTextForeColor(String text, String regex, int color) {
        int start = text.indexOf(regex);
        if(start != -1) {
            int end = start + regex.length();
            return setTextForeColor(text, start, end, color);
        }
        return new SpannableStringBuilder(text);
    }

    /**
     * 给TextView设置不同的前景颜色
     *
     * @param start
     * @param end
     * @param color
     */
    public static SpannableStringBuilder setTextForeColor(String text, int start, int end, int color) {
        SpannableStringBuilder sbuilder = new SpannableStringBuilder(text);
        ForegroundColorSpan mForeColor = new ForegroundColorSpan(color);
        sbuilder.setSpan(mForeColor, start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sbuilder;
    }


    /**
     * 设置double/float类型保留小数
     * 例：保留两位小数: "0.00"
     * @return
     */
    public static String formatDouble(double value, String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return decimalFormat.format(value);//format 返回的是字符串
    }

    /**
     * 显示数值，超过了就显示maxNum+
     * @return
     */
    public static String getMaxNumStr(String numberStr, int maxNum) {

        if (!StringUtils.isEmpty(numberStr)) {
            int num = Integer.parseInt(numberStr);
            if (num > maxNum) {
                return maxNum + "+";
            }
        } else {
            return "0";
        }
        return numberStr;
    }

    public static String getNumberStr(long number) {
        if(number > 9999) {
            float value = number * 1.0f / 10000;
            return formatFloat(value, "0.0") + "W";
        }

        return String.valueOf(number);
    }

    public static String getFormatPrice(String priceStr) {
        if(!TextUtils.isEmpty(priceStr)) {
            try {
                double price = Float.parseFloat(priceStr);
                if(price == (long)price){
                    return "" + (long)price;
                }else {
                    return StringUtils.formatDouble(price, "0.00");
                }
            }catch (Exception e) {
                e.printStackTrace();
                return "0";
            }
        }else {
            return "0";
        }
    }

    public static String getFormatNumber(String numberStr) {
        if(!TextUtils.isEmpty(numberStr)) {
            try {
                int number = (int)Float.parseFloat(numberStr);
                return "" + number;
            }catch (Exception e) {
                e.printStackTrace();
                return "0";
            }
        }else {
            return "0";
        }
    }

    public static String getFormatPrice2(String priceStr) {
        if(!TextUtils.isEmpty(priceStr)) {
            try {
                double price = Float.parseFloat(priceStr);
                return StringUtils.formatDouble(price, "0.00");
            }catch (Exception e) {
                e.printStackTrace();
                return "0.00";
            }
        }else {
            return "0.00";
        }
    }

    /**
     * 设置float类型保留小数
     * 例：保留两位小数: "0.00"
     *
     * @return
     */
    public static String formatFloat(float value, String format) {
        DecimalFormat mDecFormat = new DecimalFormat(format);//构造方法的字符格式这里如果小数不足2位,会以0补足.
        return mDecFormat.format(value);//format 返回的是字符串
    }

    /**
     * 获取MD5值
     *
     * @param source
     * @param bit    32为，16位
     * @return
     */
    public static String getMD5(byte[] source, int bit) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            StringBuffer buf = new StringBuffer();
            for (byte b : md.digest())
                buf.append(String.format("%02x", b & 0xff));
            return bit == 32 ? buf.toString() : buf.substring(8, 24);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取进程名
     *
     * @param context
     * @param pid
     * @return
     */
    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }

        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    /**
     * 是否是数字
     *
     * @param ch
     * @return
     */
    public static boolean isNumeric(char ch) {
        return Character.isDigit(ch);
    }


    /**
     * 判断纯字母的
     */
    public static boolean isLetter(char ch) {
        if (((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 转换成Unicode的字符串
     * @param str
     * @return
     */
    public static String toUnicodeStr(String str) {
        str = (str == null ? "" : str);
        String tmp;
        StringBuffer sb = new StringBuffer(1000);
        char c;
        int i, j;
        sb.setLength(0);
        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            sb.append("\\u");
            j = (c >>> 8); // 取出高8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);
            j = (c & 0xFF); // 取出低8位
            tmp = Integer.toHexString(j);
            if (tmp.length() == 1)
                sb.append("0");
            sb.append(tmp);

        }
        return (new String(sb));
    }

    /**
     * 获取后缀
     * @param text
     * @return
     */
    public static String getSuffix(String text) {
        if(!isEmpty(text)) {
            int index = text.lastIndexOf(".");
            if(index == -1)return "";

            return text.substring(index + 1).toLowerCase();
        }
        return "";
    }

    public static boolean copy(String text, boolean isHint){
        try {
            ClipboardManager cm = (ClipboardManager) (ContextUtils.getContext()).getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里
            ClipData clip = ClipData.newPlainText("text", text);
            cm.setPrimaryClip(clip);
            if(isHint) {
                ToastUtils.show("复制到剪切板");
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean copy(String text){
        return copy(text, false);
    }

}
