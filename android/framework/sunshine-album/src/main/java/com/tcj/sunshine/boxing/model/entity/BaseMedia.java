/*
 *  Copyright (C) 2017 Bilibili
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tcj.sunshine.boxing.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The base entity for media.
 *
 * @author ChenSL
 */
public abstract class BaseMedia implements Parcelable {
    public enum TYPE {
        IMAGE, VIDEO, AUDIO
    }

    protected String mPath;
    protected String mId;
    protected String mSize;
    protected String num;
    protected int isSelected;//0 未选中， 1选中
    protected int viewType;//0:CAMERA_TYPE(照相机view), 1:NORMAL_TYPE，普通view
    protected String mDateModified;
    protected boolean current;//是否是当前这个

    public BaseMedia() {
    }

    public BaseMedia(String id, String path) {
        mId = id;
        mPath = path;
    }

    public abstract TYPE getType();

    public String getId() {
        return mId;
    }

    public long getSize() {
        try {
            long result = Long.parseLong(mSize);
            return result > 0 ? result : 0;
        }catch (NumberFormatException size) {
            return 0;
        }
    }

    public void setId(String id) {
        mId = id;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public String getPath(){
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getDateModified() {
        return mDateModified;
    }

    public void setDateModified(String mDateModified) {
        this.mDateModified = mDateModified;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPath);
        dest.writeString(this.mId);
        dest.writeString(this.mSize);
        dest.writeString(this.num);
        dest.writeInt(this.isSelected);
        dest.writeInt(this.viewType);
    }

    protected BaseMedia(Parcel in) {
        this.mPath = in.readString();
        this.mId = in.readString();
        this.mSize = in.readString();
        this.num = in.readString();
        this.isSelected = in.readInt();
        this.viewType = in.readInt();
    }

}
