package com.tcj.sunshine.tools;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonUtils {

    /**
     * 解析json字符串
     * @param json
     * @param type
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T fromJson(String json, Type type) throws Exception{

        try {
            return new Gson().fromJson(json, type);
        }catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }


    /**
     * 转换成json字符串
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            return new Gson().toJson(obj);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
