package com.tcj.sunshine.tools;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;

/**
 * Created by Stefan Lau on 2019/11/19.
 */
public class DrawableUtils {

    private DrawableUtils(){}

    public static Drawable getDrawable(@DrawableRes int id){
        return ContextCompat.getDrawable(ContextUtils.getContext(), id);
    }

}
