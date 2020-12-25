package com.tcj.sunshine.boxing.model.entity.impl;

import android.os.Parcel;
import android.os.Parcelable;

import com.tcj.sunshine.boxing.model.entity.BaseMedia;

/**
 * 照相机
 */
public class CameraMedia extends BaseMedia implements Parcelable {


    public CameraMedia() {
        super();
    }


    protected CameraMedia(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CameraMedia> CREATOR = new Creator<CameraMedia>() {
        @Override
        public CameraMedia createFromParcel(Parcel in) {
            return new CameraMedia(in);
        }

        @Override
        public CameraMedia[] newArray(int size) {
            return new CameraMedia[size];
        }
    };

    @Override
    public TYPE getType() {
        return TYPE.IMAGE;
    }

}
