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

import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.boxing.model.entity.BaseMedia;

import java.util.Locale;


/**
 * Entity represent a Video.
 *
 * @author ChenSL
 */
public class AudioMedia extends BaseMedia {
    private static final long MB = 1024 * 1024;

    private String mTitle;//歌曲名称
    private String mArtist;//歌手名称
    private String mAlbum;//专辑
    private String mMimeType;
    private String mDuration;//时长
    private boolean checked;//是否选中

    private AudioMedia() {
    }

    @Override
    public TYPE getType() {
        return TYPE.VIDEO;
    }

    public AudioMedia(Builder builder) {
        super(builder.mId, builder.mPath);
        this.mTitle = builder.mTitle;
        this.mDuration = builder.mDuration;
        this.mSize = builder.mSize;
        this.mMimeType = builder.mMimeType;
        this.mArtist = builder.mArtist;
        this.mAlbum = builder.mAlbum;
        this.checked = builder.checked;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getDuration() {
        try {
            long duration = Long.parseLong(mDuration);
            return formatTimeWithMin(duration);
        } catch (NumberFormatException e) {
            return "0:00";
        }
    }

    public String formatTimeWithMin(long duration) {
        if (duration <= 0) {
            return String.format(Locale.US, "%02d:%02d", 0, 0);
        }
        long totalSeconds = duration / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d", hours * 60 + minutes,
                    seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDuration(String duration) {
        mDuration = duration;
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

    public String getMimeType() {
        return mMimeType;
    }

    public static class Builder {
        private String mId;
        private String mTitle;
        private String mPath;
        private String mArtist;//歌手名称
        private String mAlbum;//专辑
        private String mDuration;
        private String mSize;
        private String mMimeType;
        private boolean checked;//是否选中

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

        public Builder setSize(String size) {
            this.mSize = size;
            return this;
        }

        public Builder setMimeType(String type) {
            this.mMimeType = type;
            return this;
        }

        public Builder setArtist(String artist) {
            this.mArtist = artist;
            return this;
        }

        public Builder setAlbum(String album) {
            this.mAlbum = album;
            return this;
        }

        public Builder setCheched(boolean checked) {
            this.checked = checked;
            return this;
        }


        public AudioMedia build() {
            return new AudioMedia(this);
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
        dest.writeString(this.mArtist);
        dest.writeString(this.mAlbum);
        dest.writeString(this.mMimeType);
        dest.writeString(this.mDuration);
        dest.writeByte(this.checked ? (byte) 1 : (byte) 0);
    }

    protected AudioMedia(Parcel in) {
        super(in);
        this.mTitle = in.readString();
        this.mArtist = in.readString();
        this.mAlbum = in.readString();
        this.mMimeType = in.readString();
        this.mDuration = in.readString();
        this.checked = in.readByte() != 0;
    }

    public static final Parcelable.Creator<AudioMedia> CREATOR = new Parcelable.Creator<AudioMedia>() {
        @Override
        public AudioMedia createFromParcel(Parcel source) {
            return new AudioMedia(source);
        }

        @Override
        public AudioMedia[] newArray(int size) {
            return new AudioMedia[size];
        }
    };
}
