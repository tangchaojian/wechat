package com.tcj.sunshine.tools;

/**
 * 计算工具类
 */
public class CalculateUtils {

    private CalculateUtils(){}


    /**
     * 得到按原始比例缩放后的大小
     * @param originalWidth 原始宽
     * @param originalHeight 原始高
     * @param maxWidth 最大宽
     * @param maxHeight 最大高
     * @return
     */
    public static int[] getOriginalRatioSize(int originalWidth, int originalHeight, int maxWidth, int maxHeight) {
        return getOriginalRatioSize(originalWidth,originalHeight, maxWidth, maxHeight, false);
    }


    /**
     * 得到按原始比例缩放后的大小
     * @param originalWidth 原始宽
     * @param originalHeight 原始高
     * @param maxWidth 最大宽
     * @param maxHeight 最大高
     * @param crossBorder 是否允许越界
     * @return
     */
    public static int[] getOriginalRatioSize(int originalWidth, int originalHeight, int maxWidth, int maxHeight, boolean crossBorder) {
        int[] size = new int[2];
        if(originalHeight == 0 || originalHeight == 0 || maxWidth == 0 || maxHeight == 0){
            size[0] = originalWidth;
            size[1] = originalHeight;
            return size;
        }

        if(crossBorder) {
            //允许越界
            float scale = Math.max(maxWidth * 1.0f / originalWidth, maxHeight * 1.0f / originalHeight);
            int finalWidth = (int)(scale * originalWidth);
            int finalHeight = (int)(scale * originalHeight);

            size[0] = finalWidth;
            size[1] = finalHeight;
            return size;

        }else {
            //不允许越界
            int finalWidth;
            int finalHeight;
            float ratio; //输出目标的宽高或高宽比例

            if(maxWidth >= maxHeight) {
                //宽大于高
                ratio = (float)originalHeight / (float)originalWidth;
                finalWidth = maxWidth;
                finalHeight = (int)(finalWidth * ratio);
                if(finalHeight > maxHeight) {
                    ratio = (float)maxHeight / (float) finalHeight;
                    finalWidth = (int)(finalWidth * ratio);
                    finalHeight = maxHeight;
                }
            }else {
                ratio = (float) originalWidth / (float)originalHeight;
                finalHeight = maxHeight;
                finalWidth = (int)(finalHeight * ratio);
                if(finalWidth > maxWidth) {
                    ratio = (float)maxWidth / (float)finalWidth;
                    finalHeight = (int)(finalHeight * ratio);
                    finalWidth = maxWidth;
                }
            }
            size[0] = finalWidth;
            size[1] = finalHeight;
            return size;
        }
    }
}
