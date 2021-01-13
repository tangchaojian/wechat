package com.tencent.live.entity;

public class LiveGiftEntity {
    private String imgUrl;
    private String name;
    private String count;

    public LiveGiftEntity() {
    }

    public LiveGiftEntity(String imgUrl, String name, String count) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.count = count;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
