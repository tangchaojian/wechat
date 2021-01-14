package com.tencent.live.entity;

public class LiveGiftEntity {
    private String giftId;      //礼物ID
    private String giftName;    //礼物名称
    private String giftImgUrl;  //礼物图标url
    private String giftPrice;   //礼物价格
    private int giftNum;        //礼物数量
    private int type;           //礼物类型；预留字段

    public LiveGiftEntity() { }

    public LiveGiftEntity(String giftId, String giftName, String giftImgUrl, String giftPrice) {
        this.giftId = giftId;
        this.giftName = giftName;
        this.giftImgUrl = giftImgUrl;
        this.giftPrice = giftPrice;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
