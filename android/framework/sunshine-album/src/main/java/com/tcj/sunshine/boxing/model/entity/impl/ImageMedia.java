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


import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.tcj.sunshine.boxing.model.entity.BaseMedia;
import com.tcj.sunshine.boxing.utils.BoxingExecutor;
import com.tcj.sunshine.boxing.utils.BoxingExifHelper;
import com.tcj.sunshine.boxing.utils.BoxingFileHelper;
import com.tcj.sunshine.boxing.utils.CompressTask;
import com.tcj.sunshine.boxing.utils.ImageCompressor;

import java.io.File;
import java.io.FileInputStream;

import androidx.annotation.NonNull;


/**
 * Id and absolute path is necessary.Builder Mode can be used too.
 * compress image through {@link #compress(ImageCompressor)}.
 *
 * @author ChenSL
 */
public class ImageMedia extends BaseMedia implements Parcelable {
    private static final long MAX_GIF_SIZE = 1024 * 1024L;
    private static final long MAX_IMAGE_SIZE = 1024 * 1024L;

    private boolean mIsSelected;
    private String mThumbnailPath;
    private String mCompressPath;
    private int mHeight;
    private int mWidth;
    private IMAGE_TYPE mImageType;
    private String mMimeType;

    public enum IMAGE_TYPE {
        PNG, JPG, GIF
    }

    public ImageMedia(String id, String imagePath) {
        super(id, imagePath);
    }

    public ImageMedia(@NonNull File file) {
        this.mId = String.valueOf(System.currentTimeMillis());
        this.mPath = file.getAbsolutePath();
        this.mSize = String.valueOf(file.length());
        this.mIsSelected = true;
    }

    public ImageMedia(Builder builder) {
        super(builder.mId, builder.mImagePath);
        this.mThumbnailPath = builder.mThumbnailPath;
        this.mSize = builder.mSize;
        this.mHeight = builder.mHeight;
        this.mIsSelected = builder.mIsSelected;
        this.mWidth = builder.mWidth;
        this.mMimeType = builder.mMimeType;
        this.mImageType = getImageTypeByMime(builder.mMimeType);
        this.mDateModified = builder.mDateModified;
    }

    @Override
    public TYPE getType() {
        return TYPE.IMAGE;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public boolean isGifOverSize() {
        return isGif() && getSize() > MAX_GIF_SIZE;
    }

    public boolean isGif() {
        return getImageType() == IMAGE_TYPE.GIF;
    }

    public boolean compress(ImageCompressor imageCompressor) {
        return CompressTask.compress(imageCompressor, this, MAX_IMAGE_SIZE);
    }

    /**
     * @param maxSize the proximate max size for compression
     * @return may be a little bigger than expected for performance.
     */
    public boolean compress(ImageCompressor imageCompressor, long maxSize) {
        return CompressTask.compress(imageCompressor, this, maxSize);
    }

    /**
     * get mime type displayed in database.
     *
     * @return "image/gif" or "image/jpeg".
     */
    public String getMimeType() {
        if (getImageType() == IMAGE_TYPE.GIF) {
            return "image/gif";
        } else if (getImageType() == IMAGE_TYPE.JPG) {
            return "image/jpeg";
        }
        return "image/jpeg";
    }

    public IMAGE_TYPE getImageType() {
        return mImageType;
    }

    private IMAGE_TYPE getImageTypeByMime(String mimeType) {
        if (!TextUtils.isEmpty(mimeType)) {
            if ("image/gif".equals(mimeType)) {
                return IMAGE_TYPE.GIF;
            } else if ("image/png".equals(mimeType)) {
                return IMAGE_TYPE.PNG;
            } else {
                return IMAGE_TYPE.JPG;
            }
        }
        return IMAGE_TYPE.PNG;
    }

    public void setImageType(IMAGE_TYPE imageType) {
        mImageType = imageType;
    }

    public String getId() {
        return mId;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }

    public String getCompressPath() {
        return mCompressPath;
    }

    public void removeExif() {
        BoxingExifHelper.removeExif(getPath());
    }

    /**
     * save image to MediaStore.
     */
    public void saveMediaStore(final ContentResolver cr) {
        BoxingExecutor.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                if (cr != null && !TextUtils.isEmpty(getId())) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, getId());
                    values.put(MediaStore.Images.Media.MIME_TYPE, getMimeType());
                    values.put(MediaStore.Images.Media.DATA, getPath());
                    cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }
            }
        });

    }

    /**
     * save image to MediaStore.
     */
    public void saveVideoStore(final ContentResolver cr, final File file) {
        BoxingExecutor.getInstance().runWorker(new Runnable() {
            @Override
            public void run() {
                if (cr != null && !TextUtils.isEmpty(getId())) {
                    long timestamp = System.currentTimeMillis();

                    ContentValues values = new ContentValues();
                    values.put("title", file.getName());
                    values.put("_display_name", file.getName());
                    values.put("mime_type", "video/mp4");
                    values.put("datetaken", timestamp);
                    values.put("date_modified", timestamp);
                    values.put("date_added", timestamp);
                    values.put("_data", file.getAbsolutePath());
                    values.put("_size", getFileSize(file));
                    cr.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,values);
                }
            }
        });

    }

    public void setCompressPath(String compressPath) {
        mCompressPath = compressPath;
    }

    public void setSize(String size) {
        mSize = size;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    @Override
    public String toString() {
        return "ImageMedia{" +
                ", mThumbnailPath='" + mThumbnailPath + '\'' +
                ", mCompressPath='" + mCompressPath + '\'' +
                ", mSize='" + mSize + '\'' +
                ", mHeight=" + mHeight +
                ", mWidth=" + mWidth;
    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + (mPath != null ? mPath.hashCode() : 0);
        return result;
    }

    @NonNull
    public String getThumbnailPath() {
        if (BoxingFileHelper.isFileValid(mThumbnailPath)) {
            return mThumbnailPath;
        } else if (BoxingFileHelper.isFileValid(mCompressPath)) {
            return mCompressPath;
        }
        return mPath;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageMedia other = (ImageMedia) obj;
        return !(TextUtils.isEmpty(mPath) || TextUtils.isEmpty(other.mPath)) && this.mPath.equals(other.mPath);
    }

    public static class Builder {
        private String mId;
        private String mImagePath;
        private boolean mIsSelected;
        private String mThumbnailPath;
        private String mSize;
        private int mHeight;
        private int mWidth;
        private String mMimeType;
        private String mDateModified;

        public Builder(String id, String path) {
            this.mId = id;
            this.mImagePath = path;
        }

        public Builder setSelected(boolean selected) {
            this.mIsSelected = selected;
            return this;
        }

        public Builder setThumbnailPath(String thumbnailPath) {
            mThumbnailPath = thumbnailPath;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            mMimeType = mimeType;
            return this;
        }

        public Builder setSize(String size) {
            this.mSize = size;
            return this;
        }

        public Builder setDateModified(String mDateModified){
            this.mDateModified = mDateModified;
            return this;
        }

        public ImageMedia build() {
            return new ImageMedia(this);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.mIsSelected ? (byte) 1 : (byte) 0);
        dest.writeString(this.mThumbnailPath);
        dest.writeString(this.mCompressPath);
        dest.writeInt(this.mHeight);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mImageType == null ? -1 : this.mImageType.ordinal());
        dest.writeString(this.mMimeType);
        dest.writeString(this.mDateModified);
    }

    protected ImageMedia(Parcel in) {
        super(in);
        this.mIsSelected = in.readByte() != 0;
        this.mThumbnailPath = in.readString();
        this.mCompressPath = in.readString();
        this.mHeight = in.readInt();
        this.mWidth = in.readInt();
        int tmpMImageType = in.readInt();
        this.mImageType = tmpMImageType == -1 ? null : IMAGE_TYPE.values()[tmpMImageType];
        this.mMimeType = in.readString();
        this.mDateModified = in.readString();
    }

    public static final Creator<ImageMedia> CREATOR = new Creator<ImageMedia>() {
        @Override
        public ImageMedia createFromParcel(Parcel source) {
            return new ImageMedia(source);
        }

        @Override
        public ImageMedia[] newArray(int size) {
            return new ImageMedia[size];
        }
    };


    private long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(file);
            size = fis.available();
        }catch (Exception e) {

        }finally {
            if(fis != null) {
                try {
                    fis.close();
                }catch (Exception e) {

                }
            }
        }

        return size;
    }
}
