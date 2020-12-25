package com.tcj.sunshine.tools;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Stefan Lau on 2019/12/4.
 */
public class ViewUtils {
    private ViewUtils(){}

    /**
     * 测量view的高度
     * @param view
     */
    public static void measureView(View view) {

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int height = 0;
        if (params.height > 0) {
            height = View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.EXACTLY);
        } else {
            height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }

        view.measure(width, height);
    }
}
