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

package com.tcj.sunshine.boxing.model.entity.impl;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.tcj.sunshine.boxing.model.entity.BaseMedia;

import java.util.Locale;


/**
 * Entity represent a Video.
 *
 * @author ChenSL
 */
public class VideoMedia extends BaseMedia {
    private static final long MB = 1024 * 1024;
    
    private String mTitle;
    private String mDuration;
    private long time;//时长
    private String mDateTaken;
    private String mMimeType;
    private String width;
    private String height;

    private VideoMedia() {

    }

    @Override
    public TYPE getType() {
        return TYPE.VIDEO;
    }

    public VideoMedia(Builder builder) {
        super(builder.mId, builder.mPath);
        this.mTitle = builder.mTitle;
        this.mDuration = builder.mDuration;
        this.time = builder.time;
        this.mSize = builder.mSize;
        this.mDateTaken = builder.mDateTaken;
        this.mMimeType = builder.mMimeType;
        this.width = builder.width;
        this.height = builder.height;
        this.mDateModified = builder.mDateModified;
    }

    public String getDuration() {
        try {
            long duration = Long.parseLong(mDuration);
            return formatTimeWithMin(duration);
        } catch (NumberFormatException e) {
            return "00:00";
        }
    }

    public String formatTimeWithMin(long duration) {
        if (duration <= 0) {
            return String.format(Locale.CHINA, "%02d:%02d", 0, 0);
        }
        long totalSeconds = duration / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.CHINA, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.CHINA, "%02d:%02d", minutes, seconds);
        }
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSizeByUnit() {
        double size = getSize();
        if (size == 0) {
            return "0K";
        }
        if (size >= MB) {
            double sizeInM = size / MB;
            return String.format(Locale.getDefault(), "%.1f", sizeInM) + "M";
        }
        double sizeInK = size / 1024;
        return String.format(Locale.getDefault(), "%.1f", sizeInK) + "K";
    }

    public String getDateTaken() {
        return mDateTaken;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public static class Builder {
        private String mId;
        private String mTitle;
        private String mPath;
        private String mDuration;
        private long time;
        private String mSize;
        private String mDateTaken;
        private String mMimeType;
        private String width;
        private String height;
        private String mDateModified;

        public Builder(String id, String path) {
            this.mId = id;
            this.mPath = path;
        }

        public Builder setTitle(String title) {
            this.mTitle = title;
            return this;
        }

        public Builder setDuration(String duration) {
            this.mDuration = duration;
            return this;
        }

        public Builder setTime(String time) {
            if(!TextUtils.isEmpty(time)) {
                this.time = Long.parseLong(time);
            }else {
                this.time = 0;
            }

            return this;
        }

        public Builder setSize(String size) {
            this.mSize = size;
            return this;
        }

        public Builder setDataTaken(String dateTaken) {
            this.mDateTaken = dateTaken;
            return this;
        }

        public Builder setMimeType(String type) {
            this.mMimeType = type;
            return this;
        }


        public Builder setWidth(String width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(String height) {
            this.height = height;
            return this;
        }

        public Builder setDateModified(String mDateModified) {
            this.mDateModified = mDateModified;
            return this;
        }


        public VideoMedia build() {
            return new VideoMedia(this);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mTitle);
        dest.writeString(this.mDuration);
        dest.writeLong(this.time);
        dest.writeString(this.mDateTaken);
        dest.writeString(this.mMimeType);
        dest.writeString(this.width);
        dest.writeString(this.height);
        dest.writeString(mDateModified);
    }

    protected VideoMedia(Parcel in) {
        super(in);
        this.mTitle = in.readString();
        this.mDuration = in.readString();
        this.time = in.readLong();
        this.mDateTaken = in.readString();
        this.mMimeType = in.readString();
        this.width = in.readString();
        this.height = in.readString();
        this.mDateModified = in.readString();
    }

    public static final Parcelable.Creator<VideoMedia> CREATOR = new Parcelable.Creator<VideoMedia>() {
        @Override
        public VideoMedia createFromParcel(Parcel source) {
            return new VideoMedia(source);
        }

        @Override
        public VideoMedia[] newArray(int size) {
            return new VideoMedia[size];
        }
    };
}
