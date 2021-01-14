package com.tencent.live.common.msg;


/**
 * Module:   TCChatEntity
 * <p>
 * Function: 消息载体类。
 */
public class TCUserEnterEntity {
    private String userid;          //用户id
    private String grpSendName;    // 发送者的名字
    private String mobile;          //用户手机号
    private int type;            // 消息类型

    public String getSenderName() {
        return grpSendName != null ? grpSendName : "";
    }

    public void setSenderName(String grpSendName) {
        this.grpSendName = grpSendName;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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
        if (!(o instanceof TCUserEnterEntity)) return false;

        TCUserEnterEntity that = (TCUserEnterEntity) o;

        if (getType() != that.getType()) return false;
        if (grpSendName != null ? !grpSendName.equals(that.grpSendName) : that.grpSendName != null)
            return false;
        return getUserid() != null ? getUserid().equals(that.getUserid()) : that.getUserid() == null;

    }

    @Override
    public int hashCode() {
        int result = grpSendName != null ? grpSendName.hashCode() : 0;
        result = 31 * result + (getUserid() != null ? getUserid().hashCode() : 0);
        result = 31 * result + getType();
        return result;
    }
}
