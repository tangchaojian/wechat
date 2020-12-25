package com.tcj.sunshine.view.common;

import androidx.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tcj.sunshine.album.R;
import com.tcj.sunshine.boxing.loader.IBoxingCallback;
import com.tcj.sunshine.boxing.loader.IBoxingMediaLoader;
import com.tcj.sunshine.tools.ContextUtils;
import com.tcj.sunshine.tools.GlideUtils;


public class BlibliIBoxingMediaLoader implements IBoxingMediaLoader {

    @Override
    public void displayThumbnail(@NonNull ImageView mImageView, @NonNull String absPath, int width, int height) {
        String path = "file://" + absPath;
        GlideUtils.loadImage(path, width, height, mImageView, R.mipmap.icon_alum_defualt_photo, R.mipmap.icon_alum_defualt_photo, null);
    }

    @Override
    public void displayRoundThumbnail(@NonNull ImageView mImageView, @NonNull String absPath, int round) {
        String path = "file://" + absPath;
        GlideUtils.loadImage(path, mImageView);
    }

    @Override
    public void displayRaw(@NonNull final ImageView mImageView, @NonNull String absPath, int width, int height, final IBoxingCallback callback) {
        String path = "file://" + absPath;
        GlideUtils.loadImage(path, width, height, mImageView, R.mipmap.icon_alum_defualt_photo, R.mipmap.icon_alum_defualt_photo, null);
    }
}
