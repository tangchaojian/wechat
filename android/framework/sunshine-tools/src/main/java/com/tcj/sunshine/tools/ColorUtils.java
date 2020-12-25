package com.tcj.sunshine.tools;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;

/**
 * Created by Stefan Lau on 2019/12/12.
 */
public class ColorUtils {

    private ColorUtils(){}

    public static @ColorInt int getColor(@ColorRes int colorResId) {
        return ContextUtils.getContext().getResources().getColor(colorResId);
    }
}
