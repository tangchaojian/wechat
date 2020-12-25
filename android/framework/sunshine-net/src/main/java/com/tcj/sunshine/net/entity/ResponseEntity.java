package com.tcj.sunshine.net.entity;


import com.google.gson.annotations.SerializedName;

/**
 * 响应实体类
 * 一般都是这种结构：{code":200,"msg":"\u8fd4\u56de\u6210\u529f","data":{}}
 * 注:data的结构是不确定的，也许是对象，也许是数组
 */
public class ResponseEntity<T> {

    protected String code;

    protected String message;

    protected T data;

    protected long timestamp;

    protected int status;

    protected Object bean;

    protected boolean rel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public boolean isRel() {
        return rel;
    }

    public void setRel(boolean rel) {
        this.rel = rel;
    }
}
