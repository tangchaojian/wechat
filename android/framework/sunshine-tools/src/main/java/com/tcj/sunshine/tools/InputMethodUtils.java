package com.tcj.sunshine.tools;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by AiMr
 * on 2020/7/6
 */
public class InputMethodUtils {

    /**
     * 显示软键盘
     */
    public static void showSoftKeyboard(Context context, EditText editText) {
        if (editText != null) {
            editText.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    /**
     * 关闭软键盘
     */
    public static void hideSoftKeyboard(Context context, EditText editText) {
        if (editText != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            //如果软键盘已经开启
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    /**
     * 关闭软键盘
     */
    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            //如果软键盘已经开启
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 切换软键盘的状态
     */
    public static void toggleSoftKeyboard(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
