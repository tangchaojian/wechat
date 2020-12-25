package com.tcj.sunshine.view.core;

import com.tcj.sunshine.boxing.BoxingCrop;
import com.tcj.sunshine.boxing.BoxingMediaLoader;
import com.tcj.sunshine.view.common.BlibliIBoxingMediaLoader;
import com.tcj.sunshine.view.common.BoxingCropImpl;


/**
 * Boxing初始化配置类
 */
public class BoxingUtils {

    private BoxingUtils(){}

    public static void init(){
        BoxingMediaLoader.getInstance().init(new BlibliIBoxingMediaLoader());
        BoxingCrop.getInstance().init(new BoxingCropImpl()); // 需要实现 IBoxingCrop
    }
}
