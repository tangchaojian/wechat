package com.tcj.sunshine.ui.tablayout;

import java.io.Serializable;

public class TabLayoutEntity implements Serializable {

    private String id;
    private Object icon;//可以是url，也可以是本地资源图标
    private String name;
    private Object obj;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getIcon() {
        return icon;
    }

    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
