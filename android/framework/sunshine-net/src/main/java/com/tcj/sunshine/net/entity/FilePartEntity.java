package com.tcj.sunshine.net.entity;

import java.io.File;
import java.io.Serializable;

/**
 * 上传文件实体类
 */
public class FilePartEntity implements Serializable {
    public String name;//表单提交的key
    public File file;//表单提交文件

    public FilePartEntity() { }

    public FilePartEntity(String name, File file) {
        this.name = name;
        this.file = file;
    }


}
