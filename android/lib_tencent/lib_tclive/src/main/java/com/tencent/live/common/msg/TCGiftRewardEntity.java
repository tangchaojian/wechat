package com.tencent.live.common.msg;


import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Module:   TCGiftRewardEntity
 * <p>
 * Function: 礼物打赏消息载体类。
 */
public class TCGiftRewardEntity {
    private String userid;      //用户id
    private String senderName;    // 发送者的名字
    private String avatar;      //头像url
    private String giftId;      //礼物ID
    private String giftName;    //礼物名称
    private String giftImgUrl;  //礼物图标url
    private String giftPrice;   //礼物价格
    private int giftNum;        //礼物数量
    private int type;           //礼物类型；预留字段


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getGiftImgUrl() {
        return giftImgUrl;
    }

    public void setGiftImgUrl(String giftImgUrl) {
        this.giftImgUrl = giftImgUrl;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(String giftPrice) {
        this.giftPrice = giftPrice;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TCGiftRewardEntity)) return false;

        TCGiftRewardEntity that = (TCGiftRewardEntity) o;

        if (getType() != that.getType()) return false;
        if (!Objects.equals(senderName, that.senderName))
            return false;

        return false;
    }

    @Override
    public int hashCode() {
        int result = senderName != null ? senderName.hashCode() : 0;
        result = 31 * result + (getUserid() != null ? getUserid().hashCode() : 0);
        result = 31 * result + getType();
        return result;
    }

    public String toJson(){
        /**
         *     private String giftId;      //礼物ID
         *     private String giftName;    //礼物名称
         *     private String giftImgUrl;  //礼物图标url
         *     private String giftPrice;   //礼物价格
         *     private int giftNum;        //礼物数量
         *     private int type;           //礼物类型；预留字段
         */
        Map<String, Object> map = new HashMap<>();
        map.put("giftId", giftId);
        map.put("giftName", giftName);
        map.put("giftImgUrl", giftImgUrl);
        map.put("giftPrice", giftPrice);
        map.put("giftNum", giftNum);
        map.put("type", type);
        return new Gson().toJson(map);
    }

    public void fromJson(String json){
        if(TextUtils.isEmpty(json)) return;

        try {
            JSONObject object = new JSONObject(json);
            this.giftId = object.getString("giftId");
            this.giftName = object.getString("giftName");
            this.giftImgUrl = object.getString("giftImgUrl");
            this.giftPrice = object.getString("giftPrice");
            this.giftNum = object.getInt("giftNum");
            this.type = object.getInt("type");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
